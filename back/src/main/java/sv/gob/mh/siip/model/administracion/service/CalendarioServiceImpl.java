package sv.gob.mh.siip.model.administracion.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.exception.OperacionNoPermitidaException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Excepcion;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.domain.Recurrencia;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaMensual;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaSemanal;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaUnaVez;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.DiaSemanaDto;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.EditarCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.FechaResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.MesDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaMensualDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaSemanalDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.TipoPeriodoDto;
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
    public PeriodoDto agregarPeriodoLaboral(String codigoCalendario, PeriodoLaboralRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        return aPeriodoDto(agregarPeriodo(codigoCalendario, request.getCodigo(), request.getNombre(),
                TipoPeriodo.LABORAL, request.getRecurrencia()));
    }

    @Override
    public PeriodoDto agregarPeriodoNoLaboral(String codigoCalendario, PeriodoNoLaboralRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        return aPeriodoDto(agregarPeriodo(codigoCalendario, request.getCodigo(), request.getNombre(),
                TipoPeriodo.NO_LABORAL, request.getRecurrencia()));
    }

    private Periodo agregarPeriodo(String codigoCalendario, String codigo, String nombre, TipoPeriodo tipo,
            RecurrenciaDto recurrenciaDto) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        if (periodoRepository.existsByCalendario_CodigoAndCodigo(codigoCalendario, codigo)) {
            throw new ConflictoEstadoException("Ya existe un período con ese código dentro del calendario.");
        }

        Recurrencia recurrencia = aRecurrencia(recurrenciaDto, calendario);
        Periodo periodo = Periodo.builder()
                .codigo(codigo)
                .nombre(nombre)
                .tipo(tipo)
                .calendario(calendario)
                .recurrencia(recurrencia)
                .build();
        calendario.getPeriodos().add(periodo);
        calendarioRepository.save(calendario);
        return periodo;
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
            Set<DayOfWeek> dias = semanal.getDiasDeLaSemana().stream()
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

    @Override
    public ExcepcionDto registrarExcepcion(String codigoCalendario, ExcepcionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = obtenerCalendario(codigoCalendario);

        Excepcion excepcion = Excepcion.builder()
                .calendario(calendario)
                .fecha(request.getFecha())
                .tipo(TipoExcepcion.valueOf(request.getTipo().name()))
                .descripcion(request.getDescripcion())
                .build();
        calendario.getExcepciones().add(excepcion);
        calendarioRepository.save(calendario);
        return aExcepcionDto(excepcion);
    }

    @Override
    public CalendarioDto cambiarEstado(String codigoCalendario, CambiarEstadoCalendarioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = obtenerCalendario(codigoCalendario);
        calendario.setEstado(EstadoCalendario.valueOf(request.getEstado().name()));
        return aCalendarioDto(calendarioRepository.save(calendario));
    }

    @Override
    public CalendarioDto editar(String codigoCalendario, EditarCalendarioRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = obtenerCalendario(codigoCalendario);
        exigirResponsableOAdministrador(actor, calendario);

        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new InconsistenciaFechaException("CALENDARIO_RANGO_INVALIDO",
                    "La nueva fecha de inicio del calendario es posterior a la nueva fecha de fin.");
        }

        calendario.setNombre(request.getNombre());
        calendario.setDescripcion(request.getDescripcion());
        calendario.setFechaInicio(request.getFechaInicio());
        calendario.setFechaFin(request.getFechaFin());
        return aCalendarioDto(calendarioRepository.save(calendario));
    }

    /** RN12: un ADMINISTRADOR_CALENDARIO solo puede operar sobre calendarios de los que es responsable. */
    private void exigirResponsableOAdministrador(Usuario actor, Calendario calendario) {
        if (actor.getRol() == RolUsuario.ADMINISTRADOR_CALENDARIO
                && !calendario.getAdministrador().getId().equals(actor.getId())) {
            throw new AccesoDenegadoException(
                    "El actor no es el administrador responsable de este calendario.");
        }
    }

    @Override
    public void eliminar(String codigoCalendario) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        throw new OperacionNoPermitidaException(
                "Un calendario no puede eliminarse; use la transición a INACTIVO en su lugar (RN20).");
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDiaResponseDto consultarTipoDia(String codigoCalendario, LocalDate fecha) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        TipoPeriodo tipo = clasificarFecha(calendario, fecha)
                .orElseThrow(() -> new InconsistenciaFechaException("FECHA_SIN_PERIODO",
                        "La fecha dada no cae en ningún período ni excepción definidos del calendario."));
        return new TipoDiaResponseDto().fecha(fecha).tipoDia(TipoPeriodoDto.valueOf(tipo.name()));
    }

    @Override
    @Transactional(readOnly = true)
    public PertenenciaPeriodoResponseDto consultarPertenenciaPeriodo(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha) {
        Periodo periodo = obtenerPeriodo(codigoCalendario, codigoPeriodo);
        boolean pertenece = perteneceARecurrencia(periodo.getRecurrencia(), fecha);
        return new PertenenciaPeriodoResponseDto().fecha(fecha).codigoPeriodo(codigoPeriodo).pertenece(pertenece);
    }

    @Override
    @Transactional(readOnly = true)
    public DuracionPeriodoResponseDto consultarDuracionPeriodo(String codigoCalendario, String codigoPeriodo) {
        Periodo periodo = obtenerPeriodo(codigoCalendario, codigoPeriodo);
        int duracion = calcularDuracionDias(periodo.getRecurrencia(), periodo.getCalendario());
        return new DuracionPeriodoResponseDto().codigoPeriodo(codigoPeriodo).duracionDias(duracion);
    }

    @Override
    @Transactional(readOnly = true)
    public DiasRestantesResponseDto consultarDiasRestantesPeriodo(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha) {
        Periodo periodo = obtenerPeriodoLaboral(codigoCalendario, codigoPeriodo);
        if (!perteneceARecurrencia(periodo.getRecurrencia(), fecha)) {
            throw new InconsistenciaFechaException("FECHA_FUERA_DE_PERIODO",
                    "La fecha dada no está dentro del período LABORAL indicado.");
        }
        LocalDate finDelPeriodo = finDelPeriodo(periodo);
        int diasRestantes = (int) ChronoUnit.DAYS.between(fecha, finDelPeriodo);
        return new DiasRestantesResponseDto().codigoPeriodo(codigoPeriodo).fecha(fecha).diasRestantes(diasRestantes);
    }

    @Override
    @Transactional(readOnly = true)
    public DiasLaboralesEntreFechasResponseDto consultarDiasLaboralesEntreFechas(String codigoCalendario,
            LocalDate fechaInicial, LocalDate fechaFinal) {
        Calendario calendario = obtenerCalendario(codigoCalendario);
        if (fechaInicial.isAfter(fechaFinal)) {
            throw new InconsistenciaFechaException("FECHAS_INCONSISTENTES",
                    "La fecha inicial es posterior a la fecha final.");
        }
        if (!dentroDelCalendario(calendario, fechaInicial) || !dentroDelCalendario(calendario, fechaFinal)) {
            throw new InconsistenciaFechaException("FECHAS_FUERA_DE_RANGO",
                    "La fecha inicial o la fecha final no están dentro del rango del calendario.");
        }

        int diasLaborales = 0;
        for (LocalDate fecha = fechaInicial; !fecha.isAfter(fechaFinal); fecha = fecha.plusDays(1)) {
            if (esDiaLaboral(calendario, fecha)) {
                diasLaborales++;
            }
        }
        return new DiasLaboralesEntreFechasResponseDto().fechaInicial(fechaInicial).fechaFinal(fechaFinal)
                .diasLaborales(diasLaborales);
    }

    @Override
    @Transactional(readOnly = true)
    public FechaResultanteResponseDto calcularFechaResultante(String codigoCalendario, LocalDate fechaInicial,
            Integer diasHabiles) {
        Calendario calendario = obtenerCalendario(codigoCalendario);

        LocalDate fechaResultante = fechaInicial;
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

        return new FechaResultanteResponseDto().fechaInicial(fechaInicial).diasHabiles(diasHabiles)
                .fechaResultante(fechaResultante);
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
     * RN04/RN09/RN11: duracion en dias de un periodo. UNA_VEZ/SEMANAL cuentan dias calendario
     * reales (RN09); MENSUAL agrega, por cada mes declarado, los dias declarados que existen
     * realmente en ese mes (RN11), usando el año de inicio del calendario como referencia para
     * resolver la duracion real de cada mes (p.ej. febrero en año bisiesto).
     */
    private int calcularDuracionDias(Recurrencia recurrencia, Calendario calendario) {
        if (recurrencia instanceof RecurrenciaUnaVez unaVez) {
            return (int) ChronoUnit.DAYS.between(unaVez.getFechaInicio(), unaVez.getFechaFin()) + 1;
        }
        if (recurrencia instanceof RecurrenciaSemanal semanal) {
            int total = 0;
            for (LocalDate fecha = semanal.getFechaInicio(); !fecha.isAfter(semanal.getFechaFin()); fecha = fecha
                    .plusDays(1)) {
                if (semanal.getDiasDeLaSemana().contains(fecha.getDayOfWeek())) {
                    total++;
                }
            }
            return total;
        }
        if (recurrencia instanceof RecurrenciaMensual mensual) {
            int anioReferencia = calendario.getFechaInicio().getYear();
            int total = 0;
            for (Month mes : mensual.getMeses()) {
                int longitudMes = YearMonth.of(anioReferencia, mes).lengthOfMonth();
                total += (int) mensual.getDiasDelMes().stream().filter(dia -> dia <= longitudMes).count();
            }
            return total;
        }
        return 0;
    }

    /** RN05: fin del periodo LABORAL contra el que se calculan los dias restantes. */
    private LocalDate finDelPeriodo(Periodo periodo) {
        Recurrencia recurrencia = periodo.getRecurrencia();
        if (recurrencia instanceof RecurrenciaUnaVez unaVez) {
            return unaVez.getFechaFin();
        }
        if (recurrencia instanceof RecurrenciaSemanal semanal) {
            return semanal.getFechaFin();
        }
        // MENSUAL no declara un rango de fechas propio: se usa el fin del calendario como limite.
        return periodo.getCalendario().getFechaFin();
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
        return new CalendarioDto()
                .codigo(calendario.getCodigo())
                .nombre(calendario.getNombre())
                .descripcion(calendario.getDescripcion())
                .fechaInicio(calendario.getFechaInicio())
                .fechaFin(calendario.getFechaFin())
                .estado(EstadoCalendarioDto.valueOf(calendario.getEstado().name()));
    }

    private PeriodoDto aPeriodoDto(Periodo periodo) {
        return new PeriodoDto()
                .codigo(periodo.getCodigo())
                .nombre(periodo.getNombre())
                .tipo(TipoPeriodoDto.valueOf(periodo.getTipo().name()))
                .recurrencia(aRecurrenciaDto(periodo.getRecurrencia()));
    }

    private ExcepcionDto aExcepcionDto(Excepcion excepcion) {
        return new ExcepcionDto()
                .fecha(excepcion.getFecha())
                .tipo(TipoExcepcionDto.valueOf(excepcion.getTipo().name()))
                .descripcion(excepcion.getDescripcion());
    }

    private RecurrenciaDto aRecurrenciaDto(Recurrencia recurrencia) {
        if (recurrencia instanceof RecurrenciaUnaVez unaVez) {
            return new RecurrenciaUnaVezDto()
                    .tipoRecurrencia("UNA_VEZ")
                    .fechaInicio(unaVez.getFechaInicio())
                    .fechaFin(unaVez.getFechaFin());
        }
        if (recurrencia instanceof RecurrenciaSemanal semanal) {
            RecurrenciaSemanalDto dto = new RecurrenciaSemanalDto()
                    .tipoRecurrencia("SEMANAL")
                    .fechaInicio(semanal.getFechaInicio())
                    .fechaFin(semanal.getFechaFin());
            semanal.getDiasDeLaSemana().stream().sorted().forEach(dia -> dto.addDiasDeLaSemanaItem(DiaSemanaDto.valueOf(dia.name())));
            return dto;
        }
        if (recurrencia instanceof RecurrenciaMensual mensual) {
            RecurrenciaMensualDto dto = new RecurrenciaMensualDto()
                    .tipoRecurrencia("MENSUAL");
            mensual.getDiasDelMes().stream().sorted().forEach(dto::addDiasDelMesItem);
            mensual.getMeses().stream().sorted().forEach(mes -> dto.addMesesItem(MesDto.valueOf(mes.name())));
            return dto;
        }
        throw new IllegalArgumentException("Tipo de recurrencia no soportado: " + recurrencia.getClass());
    }
}
