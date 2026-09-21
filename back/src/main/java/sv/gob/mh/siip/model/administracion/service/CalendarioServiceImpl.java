package sv.gob.mh.siip.model.administracion.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Excepcion;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.domain.Recurrencia;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaMensual;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaSemanal;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaUnaVez;
import sv.gob.mh.siip.model.administracion.dto.CalendarItemDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarItemInputDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioResumenDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.DayOfWeekDto;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionInputDto;
import sv.gob.mh.siip.model.administracion.dto.FechaLaboralResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.MonthDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralInputDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RangoFechasCalendarioResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaMensualDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaSemanalDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.dto.RegistrarExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoExcepcionDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class CalendarioServiceImpl implements CalendarioService {

    /** Limite de dias explorados al buscar una fecha LABORAL resultante, para no ciclar indefinidamente (RN07). */
    private static final int LIMITE_DIAS_EXPLORADOS = 100_000;

    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final ActorContexto actorContexto;

    public CalendarioServiceImpl(CalendarioRepository calendarioRepository, PeriodoRepository periodoRepository,
            ActorContexto actorContexto) {
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    public CalendarioDto crear(CrearCalendarioRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);

        if (calendarioRepository.existsByCodigo(request.getCodigo())) {
            throw new ConflictoEstadoException("Ya existe un calendario con el código indicado.");
        }
        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new InconsistenciaFechaException("CALENDARIO_RANGO_INVALIDO",
                    "La fecha de inicio del calendario es posterior a la fecha de fin.");
        }

        Calendario calendario = Calendario.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .estado(EstadoCalendario.valueOf(request.getEstado().name()))
                .administrador(actor)
                .build();

        return aCalendarioDto(calendarioRepository.save(calendario));
    }

    @Override
    public PeriodoLaboralDto agregarPeriodoLaboral(String codigoCalendario, PeriodoInputDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Periodo periodo = agregarPeriodo(codigoCalendario, request, TipoPeriodo.LABORAL);
        return aPeriodoLaboralDto(periodo, obtenerCalendario(codigoCalendario).getEstado());
    }

    @Override
    public PeriodoNoLaboralDto agregarPeriodoNoLaboral(String codigoCalendario, PeriodoInputDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Periodo periodo = agregarPeriodo(codigoCalendario, request, TipoPeriodo.NO_LABORAL);
        return aPeriodoNoLaboralDto(periodo, obtenerCalendario(codigoCalendario).getEstado());
    }

    private Periodo agregarPeriodo(String codigoCalendario, PeriodoInputDto request, TipoPeriodo tipo) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        if (periodoRepository.existsByCalendario_CodigoAndCodigo(codigoCalendario, request.getCodigo())) {
            throw new ConflictoEstadoException("Ya existe un período con ese código dentro del calendario.");
        }

        Recurrencia recurrencia = aRecurrencia(request.getRecurrencia(), calendario);
        Periodo periodo = Periodo.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .tipo(tipo)
                .calendario(calendario)
                .recurrencia(recurrencia)
                .build();
        calendario.getPeriodos().add(periodo);
        // Igual que en registrarExcepcion: save() cascada un merge, que crea una copia gestionada
        // nueva para el periodo recien agregado (id se asigna a esa copia, no a esta variable
        // local); se recupera del resultado de save() por codigo, unico dentro del calendario
        // (ver existsByCalendario_CodigoAndCodigo arriba).
        Calendario calendarioGuardado = calendarioRepository.save(calendario);
        return calendarioGuardado.getPeriodos().stream()
                .filter(p -> p.getCodigo().equals(request.getCodigo()))
                .findFirst()
                .orElseThrow();
    }

    private Recurrencia aRecurrencia(RecurrenciaDto dto, Calendario calendario) {
        if (dto instanceof RecurrenciaUnaVezDto unaVez) {
            LocalDate fechaInicio = unaVez.getFechaInicio();
            LocalDate fechaFin = unaVez.getFechaFin();
            exigirRangoValido(fechaInicio, fechaFin);
            exigirEnmarcadoEnCalendario(fechaInicio, fechaFin, calendario);
            return RecurrenciaUnaVez.builder().fechaInicio(fechaInicio).fechaFin(fechaFin).build();
        }
        if (dto instanceof RecurrenciaSemanalDto semanal) {
            LocalDate fechaInicio = semanal.getFechaInicio();
            LocalDate fechaFin = semanal.getFechaFin();
            exigirRangoValido(fechaInicio, fechaFin);
            exigirEnmarcadoEnCalendario(fechaInicio, fechaFin, calendario);
            Set<DayOfWeek> dias = semanal.getDiasSemana().stream()
                    .map(dia -> DayOfWeek.valueOf(dia.name()))
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));
            return RecurrenciaSemanal.builder().fechaInicio(fechaInicio).fechaFin(fechaFin).diasDeLaSemana(dias)
                    .build();
        }
        if (dto instanceof RecurrenciaMensualDto mensual) {
            Set<Integer> diasDelMes = new LinkedHashSet<>(mensual.getDiasDelMes());
            Set<Month> meses = mensual.getMeses().stream()
                    .map(mes -> Month.valueOf(mes.name()))
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(Month.class)));
            return RecurrenciaMensual.builder().diasDelMes(diasDelMes).meses(meses).build();
        }
        throw new IllegalArgumentException("Tipo de recurrencia no soportado: " + dto.getClass());
    }

    /** RN08: la fecha de inicio de un período no puede ser posterior a la fecha de fin. */
    private void exigirRangoValido(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new InconsistenciaFechaException("PERIODO_RANGO_INVALIDO",
                    "La fecha de inicio del período es posterior a la fecha de fin.");
        }
    }

    /** RN10: todos los períodos deben quedar enmarcados en el período del calendario. */
    private void exigirEnmarcadoEnCalendario(LocalDate fechaInicio, LocalDate fechaFin, Calendario calendario) {
        if (fechaInicio.isBefore(calendario.getFechaInicio()) || fechaFin.isAfter(calendario.getFechaFin())) {
            throw new InconsistenciaFechaException("PERIODO_FUERA_DE_RANGO",
                    "El período no está enmarcado dentro del rango del calendario.");
        }
    }

    /** RN10: una excepción debe caer dentro del rango del calendario. */
    private void exigirFechaEnmarcadaEnCalendario(LocalDate fecha, Calendario calendario) {
        if (fecha.isBefore(calendario.getFechaInicio()) || fecha.isAfter(calendario.getFechaFin())) {
            throw new InconsistenciaFechaException("EXCEPCION_FUERA_DE_RANGO",
                    "La fecha de la excepción no está enmarcada dentro del rango del calendario.");
        }
    }

    @Override
    public ExcepcionDto registrarExcepcion(String codigoCalendario, RegistrarExcepcionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = obtenerCalendario(codigoCalendario);
        exigirFechaEnmarcadaEnCalendario(request.getFecha(), calendario);

        Excepcion excepcion = Excepcion.builder()
                .calendario(calendario)
                .fecha(request.getFecha())
                .tipo(TipoExcepcion.valueOf(request.getTipo().name()))
                .descripcion(request.getDescripcion())
                .build();
        calendario.getExcepciones().add(excepcion);
        // save() invoca EntityManager.merge() (calendario ya tiene id): al cascadear, merge crea
        // una copia gestionada nueva para cada hijo recien agregado y es esa copia -no la
        // variable local 'excepcion'- la que recibe el id generado. Se recupera del resultado de
        // save() por fecha (UK_EXCEPCION_CALENDARIO_FECHA garantiza que es unica dentro del
        // calendario).
        Calendario calendarioGuardado = calendarioRepository.save(calendario);
        Excepcion excepcionGuardada = calendarioGuardado.getExcepciones().stream()
                .filter(e -> e.getFecha().equals(request.getFecha()))
                .findFirst()
                .orElseThrow();
        return aExcepcionDto(excepcionGuardada, calendarioGuardado.getEstado());
    }

    @Override
    public CalendarioDto cambiarEstado(String codigoCalendario, CambiarEstadoCalendarioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = obtenerCalendario(codigoCalendario);
        calendario.setEstado(EstadoCalendario.valueOf(request.getEstado().name()));
        return aCalendarioDto(calendarioRepository.save(calendario));
    }

    @Override
    public CalendarioDto editarDefinicion(String codigoCalendario, EditarDefinicionCalendarioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = obtenerCalendario(codigoCalendario);

        Set<Long> periodosConservados = new HashSet<>();
        Set<Long> excepcionesConservadas = new HashSet<>();
        for (CalendarItemInputDto item : request.getItems()) {
            if (item instanceof ExcepcionInputDto excepcionInput) {
                excepcionesConservadas.add(aplicarExcepcionInput(calendario, excepcionInput).getId());
            } else {
                periodosConservados.add(aplicarPeriodoInput(calendario, item).getId());
            }
        }
        calendario.getPeriodos().removeIf(periodo -> !periodosConservados.contains(periodo.getId()));
        calendario.getExcepciones().removeIf(excepcion -> !excepcionesConservadas.contains(excepcion.getId()));

        return aCalendarioDto(calendarioRepository.save(calendario));
    }

    /** RN23: `id` presente edita ese CalendarItem; `id` ausente da de alta uno nuevo (RN08, RN10, RN15). */
    private Periodo aplicarPeriodoInput(Calendario calendario, CalendarItemInputDto itemInput) {
        DatosPeriodo datos = switch (itemInput) {
            case PeriodoLaboralInputDto laboral -> new DatosPeriodo(laboral.getId(), laboral.getCodigo(),
                    laboral.getNombre(), laboral.getRecurrencia(), TipoPeriodo.LABORAL);
            case PeriodoNoLaboralInputDto noLaboral -> new DatosPeriodo(noLaboral.getId(), noLaboral.getCodigo(),
                    noLaboral.getNombre(), noLaboral.getRecurrencia(), TipoPeriodo.NO_LABORAL);
            default -> throw new IllegalArgumentException(
                    "Tipo de CalendarItem no soportado: " + itemInput.getClass());
        };
        Long id = datos.id();
        String codigo = datos.codigo();
        String nombre = datos.nombre();
        RecurrenciaDto recurrenciaDto = datos.recurrencia();
        TipoPeriodo tipo = datos.tipo();

        Periodo periodo;
        if (id != null) {
            periodo = calendario.getPeriodos().stream().filter(p -> id.equals(p.getId())).findFirst()
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe ningún período con el id indicado dentro de ese calendario."));
        } else {
            periodo = Periodo.builder().calendario(calendario).build();
            calendario.getPeriodos().add(periodo);
        }

        Periodo periodoEditado = periodo;
        boolean codigoDuplicado = calendario.getPeriodos().stream()
                .anyMatch(otro -> otro != periodoEditado && otro.getCodigo().equals(codigo));
        if (codigoDuplicado) {
            throw new ConflictoEstadoException("Ya existe un período con ese código dentro del calendario.");
        }

        Recurrencia recurrencia = aRecurrencia(recurrenciaDto, calendario);
        periodo.setCodigo(codigo);
        periodo.setNombre(nombre);
        periodo.setTipo(tipo);
        periodo.setRecurrencia(recurrencia);
        return periodo;
    }

    /** Datos comunes de un {@link CalendarItemInputDto} de tipo período, sea laboral o no laboral. */
    private record DatosPeriodo(Long id, String codigo, String nombre, RecurrenciaDto recurrencia,
            TipoPeriodo tipo) {
    }

    /** RN23: `id` presente edita esa excepción; `id` ausente da de alta una nueva (RN10). */
    private Excepcion aplicarExcepcionInput(Calendario calendario, ExcepcionInputDto input) {
        Excepcion excepcion;
        Long id = input.getId();
        if (id != null) {
            excepcion = calendario.getExcepciones().stream().filter(e -> id.equals(e.getId())).findFirst()
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe ninguna excepción con el id indicado dentro de ese calendario."));
        } else {
            excepcion = Excepcion.builder().calendario(calendario).build();
            calendario.getExcepciones().add(excepcion);
        }

        exigirFechaEnmarcadaEnCalendario(input.getFecha(), calendario);
        excepcion.setFecha(input.getFecha());
        excepcion.setTipo(TipoExcepcion.valueOf(input.getTipo().name()));
        excepcion.setDescripcion(input.getDescripcion());
        return excepcion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarioResumenDto> listar() {
        return calendarioRepository.findAll().stream()
                .map(calendario -> new CalendarioResumenDto()
                        .codigo(calendario.getCodigo())
                        .nombre(calendario.getNombre())
                        .estado(EstadoCalendarioDto.valueOf(calendario.getEstado().name())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarioDto recuperarDefinicion(String codigoCalendario) {
        return aCalendarioDto(obtenerCalendario(codigoCalendario));
    }

    @Override
    @Transactional(readOnly = true)
    public RangoFechasCalendarioResponseDto consultarRangoFechasCalendario(String codigoCalendario) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        return new RangoFechasCalendarioResponseDto().fechaDesde(calendario.getFechaInicio())
                .fechaHasta(calendario.getFechaFin());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDiaResponseDto consultarTipoDia(String codigoCalendario, LocalDate fecha) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        TipoPeriodo tipo = clasificarFecha(calendario, fecha)
                .orElseThrow(() -> new InconsistenciaFechaException("FECHA_SIN_PERIODO",
                        "La fecha dada no cae en ningún período ni excepción definidos del calendario."));
        return new TipoDiaResponseDto().fecha(fecha).tipo(TipoDiaDto.valueOf(tipo.name()));
    }

    @Override
    @Transactional(readOnly = true)
    public PertenenciaPeriodoResponseDto consultarPertenenciaPeriodo(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha) {
        Periodo periodo = obtenerPeriodo(codigoCalendario, codigoPeriodo);
        boolean pertenece = perteneceARecurrencia(periodo.getRecurrencia(), fecha);
        return new PertenenciaPeriodoResponseDto().pertenece(pertenece);
    }

    @Override
    @Transactional(readOnly = true)
    public DuracionPeriodoResponseDto consultarDuracionPeriodo(String codigoCalendario, String codigoPeriodo) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        Periodo periodo = obtenerPeriodo(codigoCalendario, codigoPeriodo);
        int duracion = calcularDuracionDias(periodo, calendario);
        return new DuracionPeriodoResponseDto().duracionDias(duracion);
    }

    @Override
    @Transactional(readOnly = true)
    public DiasRestantesResponseDto consultarDiasRestantesPeriodoLaboral(String codigoCalendario,
            String codigoPeriodo, LocalDate fecha) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        Periodo periodo = obtenerPeriodoLaboral(codigoCalendario, codigoPeriodo);
        if (!perteneceARecurrencia(periodo.getRecurrencia(), fecha)) {
            throw new InconsistenciaFechaException("FECHA_FUERA_DE_PERIODO",
                    "La fecha dada no está dentro del período LABORAL indicado.");
        }
        LocalDate finDelPeriodo = finDelPeriodo(periodo, calendario);
        int diasRestantes = (int) ChronoUnit.DAYS.between(fecha, finDelPeriodo);
        return new DiasRestantesResponseDto().diasRestantes(diasRestantes);
    }

    @Override
    @Transactional(readOnly = true)
    public DiasLaboralesEntreFechasResponseDto consultarDiasLaboralesEntreFechas(String codigoCalendario,
            LocalDate fechaInicio, LocalDate fechaFin) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        if (fechaInicio.isAfter(fechaFin)) {
            throw new InconsistenciaFechaException("FECHAS_INCONSISTENTES",
                    "La fecha inicial es posterior a la fecha final.");
        }
        if (!dentroDelCalendario(calendario, fechaInicio) || !dentroDelCalendario(calendario, fechaFin)) {
            throw new InconsistenciaFechaException("FECHAS_FUERA_DE_RANGO",
                    "La fecha inicial o la fecha final no están dentro del rango del calendario.");
        }

        int diasLaborales = 0;
        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
            if (esDiaLaboral(calendario, fecha)) {
                diasLaborales++;
            }
        }
        // RN06 (seccion 14): la convencion de conteo excluye uno de los extremos del rango.
        return new DiasLaboralesEntreFechasResponseDto().diasLaborales(Math.max(diasLaborales - 1, 0));
    }

    @Override
    @Transactional(readOnly = true)
    public FechaLaboralResultanteResponseDto calcularFechaLaboralResultante(String codigoCalendario, LocalDate fecha,
            Integer diasHabiles) {
        Calendario calendario = obtenerCalendario(codigoCalendario);

        LocalDate fechaResultante = fecha;
        int diasContados = 0;
        int diasExplorados = 0;
        while (diasContados < diasHabiles) {
            fechaResultante = fechaResultante.plusDays(1);
            diasExplorados++;
            if (diasExplorados > LIMITE_DIAS_EXPLORADOS) {
                throw new InconsistenciaFechaException("LIMITE_EXPLORACION_EXCEDIDO",
                        "No fue posible determinar la fecha LABORAL resultante dentro de un rango razonable.");
            }
            if (esDiaLaboral(calendario, fechaResultante)) {
                diasContados++;
            }
        }

        TipoPeriodo tipoResultante = clasificarFecha(calendario, fechaResultante)
                .orElseThrow(() -> new InconsistenciaFechaException("FECHA_RESULTANTE_SIN_PERIODO",
                        "La fecha resultante no cae en ningún período definido del calendario."));
        if (tipoResultante != TipoPeriodo.LABORAL) {
            throw new InconsistenciaFechaException("FECHA_RESULTANTE_NO_LABORAL",
                    "La fecha resultante no cae en un período LABORAL.");
        }

        return new FechaLaboralResultanteResponseDto().fecha(fechaResultante);
    }

    // ==========================================================================================
    // Reglas de clasificacion de fechas (RN01, RN02, RN16, RN19) compartidas por las consultas
    // ==========================================================================================

    /**
     * Clasifica una fecha segun las excepciones y periodos del calendario. Una excepcion sobre la
     * fecha tiene prioridad (RN01/seccion 7.1); en su ausencia, la interseccion LABORAL+NO_LABORAL
     * se resuelve como NO_LABORAL (RN02). Vacio si la fecha no cae en ninguna excepcion ni periodo
     * (RN16).
     */
    private Optional<TipoPeriodo> clasificarFecha(Calendario calendario, LocalDate fecha) {
        Optional<Excepcion> excepcion = excepcionEnFecha(calendario, fecha);
        if (excepcion.isPresent()) {
            return Optional.of(
                    excepcion.get().getTipo() == TipoExcepcion.DIA_LABORAL ? TipoPeriodo.LABORAL : TipoPeriodo.NO_LABORAL);
        }
        boolean enLaboral = calendario.getPeriodos().stream()
                .filter(periodo -> periodo.getTipo() == TipoPeriodo.LABORAL)
                .anyMatch(periodo -> perteneceARecurrencia(periodo.getRecurrencia(), fecha));
        boolean enNoLaboral = calendario.getPeriodos().stream()
                .filter(periodo -> periodo.getTipo() == TipoPeriodo.NO_LABORAL)
                .anyMatch(periodo -> perteneceARecurrencia(periodo.getRecurrencia(), fecha));
        if (!enLaboral && !enNoLaboral) {
            return Optional.empty();
        }
        return Optional.of(enNoLaboral ? TipoPeriodo.NO_LABORAL : TipoPeriodo.LABORAL);
    }

    /** RN19: para efectos de conteo, una fecha sin periodo definido tampoco cuenta como LABORAL. */
    private boolean esDiaLaboral(Calendario calendario, LocalDate fecha) {
        return clasificarFecha(calendario, fecha).map(tipo -> tipo == TipoPeriodo.LABORAL).orElse(false);
    }

    private Optional<Excepcion> excepcionEnFecha(Calendario calendario, LocalDate fecha) {
        return calendario.getExcepciones().stream().filter(excepcion -> excepcion.getFecha().equals(fecha))
                .findFirst();
    }

    private boolean dentroDelCalendario(Calendario calendario, LocalDate fecha) {
        return !fecha.isBefore(calendario.getFechaInicio()) && !fecha.isAfter(calendario.getFechaFin());
    }

    private boolean perteneceARecurrencia(Recurrencia recurrencia, LocalDate fecha) {
        if (recurrencia instanceof RecurrenciaUnaVez unaVez) {
            return !fecha.isBefore(unaVez.getFechaInicio()) && !fecha.isAfter(unaVez.getFechaFin());
        }
        if (recurrencia instanceof RecurrenciaSemanal semanal) {
            return !fecha.isBefore(semanal.getFechaInicio()) && !fecha.isAfter(semanal.getFechaFin())
                    && semanal.getDiasDeLaSemana().contains(fecha.getDayOfWeek());
        }
        if (recurrencia instanceof RecurrenciaMensual mensual) {
            return mensual.getMeses().contains(fecha.getMonth()) && mensual.getDiasDelMes().contains(fecha.getDayOfMonth());
        }
        return false;
    }

    /**
     * RN04/RN09/RN11: duracion en dias de un periodo, contando dias calendario reales (RN09) para
     * que MENSUAL resuelva la duracion real de cada mes involucrado (RN11, p.ej. febrero en año
     * bisiesto) sin extrapolar a meses no declarados. Si el periodo es LABORAL, excluye los dias
     * que tambien caen en algun periodo NO_LABORAL del mismo calendario (RN04); MENSUAL no declara
     * un rango propio, por lo que se acota al rango del calendario.
     */
    private int calcularDuracionDias(Periodo periodo, Calendario calendario) {
        Recurrencia recurrencia = periodo.getRecurrencia();
        RangoFechas rango = switch (recurrencia) {
            case RecurrenciaUnaVez unaVez -> new RangoFechas(unaVez.getFechaInicio(), unaVez.getFechaFin());
            case RecurrenciaSemanal semanal -> new RangoFechas(semanal.getFechaInicio(), semanal.getFechaFin());
            default -> new RangoFechas(calendario.getFechaInicio(), calendario.getFechaFin());
        };
        LocalDate desde = rango.desde();
        LocalDate hasta = rango.hasta();

        int total = 0;
        for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            boolean excluidaPorNoLaboral = periodo.getTipo() == TipoPeriodo.LABORAL
                    && enAlgunPeriodoDeTipo(calendario, TipoPeriodo.NO_LABORAL, fecha);
            if (perteneceARecurrencia(recurrencia, fecha) && !excluidaPorNoLaboral) {
                total++;
            }
        }
        return total;
    }

    /** Rango de fechas sobre el que se recorre una recurrencia al calcular su duración. */
    private record RangoFechas(LocalDate desde, LocalDate hasta) {
    }

    private boolean enAlgunPeriodoDeTipo(Calendario calendario, TipoPeriodo tipo, LocalDate fecha) {
        return calendario.getPeriodos().stream().filter(otro -> otro.getTipo() == tipo)
                .anyMatch(otro -> perteneceARecurrencia(otro.getRecurrencia(), fecha));
    }

    /** RN05: fin del periodo LABORAL contra el que se calculan los dias restantes. */
    private LocalDate finDelPeriodo(Periodo periodo, Calendario calendario) {
        Recurrencia recurrencia = periodo.getRecurrencia();
        if (recurrencia instanceof RecurrenciaUnaVez unaVez) {
            return unaVez.getFechaFin();
        }
        if (recurrencia instanceof RecurrenciaSemanal semanal) {
            return semanal.getFechaFin();
        }
        // MENSUAL no declara un rango de fechas propio: se usa el fin del calendario como limite.
        return calendario.getFechaFin();
    }

    // ==========================================================================================
    // Busqueda de entidades (RN17)
    // ==========================================================================================

    private Calendario obtenerCalendario(String codigoCalendario) {
        return calendarioRepository.findByCodigo(codigoCalendario)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún calendario con el código indicado."));
    }

    private Periodo obtenerPeriodo(String codigoCalendario, String codigoPeriodo) {
        return periodoRepository.findByCalendario_CodigoAndCodigo(codigoCalendario, codigoPeriodo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe ningún período con el código indicado dentro de ese calendario."));
    }

    private Periodo obtenerPeriodoLaboral(String codigoCalendario, String codigoPeriodo) {
        Periodo periodo = obtenerPeriodo(codigoCalendario, codigoPeriodo);
        if (periodo.getTipo() != TipoPeriodo.LABORAL) {
            throw new RecursoNoEncontradoException(
                    "No existe ningún período LABORAL con el código indicado dentro de ese calendario.");
        }
        return periodo;
    }

    // ==========================================================================================
    // Mapeo a DTO
    // ==========================================================================================

    private CalendarioDto aCalendarioDto(Calendario calendario) {
        CalendarioDto dto = new CalendarioDto()
                .id(calendario.getId())
                .codigo(calendario.getCodigo())
                .nombre(calendario.getNombre())
                .descripcion(calendario.getDescripcion())
                .fechaInicio(calendario.getFechaInicio())
                .fechaFin(calendario.getFechaFin())
                .estado(EstadoCalendarioDto.valueOf(calendario.getEstado().name()));
        EstadoCalendario estado = calendario.getEstado();
        calendario.getPeriodos().forEach(periodo -> dto.addItemsItem(aPeriodoItemDto(periodo, estado)));
        calendario.getExcepciones().forEach(excepcion -> dto.addItemsItem(aExcepcionDto(excepcion, estado)));
        return dto;
    }

    private CalendarItemDto aPeriodoItemDto(Periodo periodo, EstadoCalendario estado) {
        return periodo.getTipo() == TipoPeriodo.LABORAL ? aPeriodoLaboralDto(periodo, estado)
                : aPeriodoNoLaboralDto(periodo, estado);
    }

    private PeriodoLaboralDto aPeriodoLaboralDto(Periodo periodo, EstadoCalendario estado) {
        return new PeriodoLaboralDto()
                .id(periodo.getId())
                .tipoItem("LABORAL")
                .codigo(periodo.getCodigo())
                .nombre(periodo.getNombre())
                .recurrencia(aRecurrenciaDto(periodo.getRecurrencia()))
                .estado(EstadoCalendarioDto.valueOf(estado.name()));
    }

    private PeriodoNoLaboralDto aPeriodoNoLaboralDto(Periodo periodo, EstadoCalendario estado) {
        return new PeriodoNoLaboralDto()
                .id(periodo.getId())
                .tipoItem("NO_LABORAL")
                .codigo(periodo.getCodigo())
                .nombre(periodo.getNombre())
                .recurrencia(aRecurrenciaDto(periodo.getRecurrencia()))
                .estado(EstadoCalendarioDto.valueOf(estado.name()));
    }

    private ExcepcionDto aExcepcionDto(Excepcion excepcion, EstadoCalendario estado) {
        return new ExcepcionDto()
                .id(excepcion.getId())
                .tipoItem("EXCEPCION")
                .fecha(excepcion.getFecha())
                .tipo(TipoExcepcionDto.valueOf(excepcion.getTipo().name()))
                .descripcion(excepcion.getDescripcion())
                .estado(EstadoCalendarioDto.valueOf(estado.name()));
    }

    private RecurrenciaDto aRecurrenciaDto(Recurrencia recurrencia) {
        if (recurrencia instanceof RecurrenciaUnaVez unaVez) {
            return new RecurrenciaUnaVezDto()
                    .tipo("UNA_VEZ")
                    .fechaInicio(unaVez.getFechaInicio())
                    .fechaFin(unaVez.getFechaFin());
        }
        if (recurrencia instanceof RecurrenciaSemanal semanal) {
            RecurrenciaSemanalDto dto = new RecurrenciaSemanalDto()
                    .tipo("SEMANAL")
                    .fechaInicio(semanal.getFechaInicio())
                    .fechaFin(semanal.getFechaFin());
            semanal.getDiasDeLaSemana().stream().sorted()
                    .forEach(dia -> dto.addDiasSemanaItem(DayOfWeekDto.valueOf(dia.name())));
            return dto;
        }
        if (recurrencia instanceof RecurrenciaMensual mensual) {
            RecurrenciaMensualDto dto = new RecurrenciaMensualDto().tipo("MENSUAL");
            mensual.getDiasDelMes().stream().sorted().forEach(dto::addDiasDelMesItem);
            mensual.getMeses().stream().sorted().forEach(mes -> dto.addMesesItem(MonthDto.valueOf(mes.name())));
            return dto;
        }
        throw new IllegalArgumentException("Tipo de recurrencia no soportado: " + recurrencia.getClass());
    }
}
