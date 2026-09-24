package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.dto.AvanceCuatrimestreAnteriorDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP". Análogo de
 * {@link ProgramacionFinancieraPapServiceImpl} (CU-PRE-30) pero sobre lo EJECUTADO por cuatrimestre
 * ({@link AvanceFinancieroCuatrimestral}) contra lo programado por esa misma fuente (RN-E). Sin
 * flujo de aprobación propio: "Seguimiento de Metas" (SF-2) es pura navegación a CU-PRE-33, donde
 * vive la única aprobación unificada del avance del PAP.
 */
@Service
@Transactional
public class AvanceFinancieroPapServiceImpl implements AvanceFinancieroPapService {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private static final RolUsuario[] ROLES_CONSULTA = {
            RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE,
            RolUsuario.COORDINADOR_PROGRAMACION, RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI
    };

    /**
     * Anexo A.6: actores internos de la DGICP, únicos que ven "Comentarios al reporte financiero
     * DGICP" (mismo criterio que CU-PRE-30 Anexo A.8 y CU-PRE-33 RN-A.b; el Técnico URP no los ve).
     */
    private static final RolUsuario[] ROLES_DGICP_INTERNOS = {
            RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE, RolUsuario.COORDINADOR_PROGRAMACION,
            RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI
    };

    private static final String MENSAJE_LIMITE_CUATRIMESTRE =
            "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado";

    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final AvanceFinancieroCuatrimestralRepository avanceRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final RevisionAvancePapRepository revisionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final ActorContexto actorContexto;

    public AvanceFinancieroPapServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository,
            CalendarioEventoRepository calendarioEventoRepository, RevisionAvancePapRepository revisionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.revisionRepository = revisionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public AvanceFinancieroPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio) {
        Usuario actor = actorContexto.exigirRol(ROLES_CONSULTA);
        Long unidadFiltro = actor.getRol() == RolUsuario.TECNICO_URP
                ? actor.getUnidadEjecutora().getId()
                : idUnidadEjecutora;
        int anioEfectivo = anio != null ? anio : Year.now(ZONA_EL_SALVADOR).getValue();
        Cuatrimestre periodoEfectivo = periodo != null ? Cuatrimestre.valueOf(periodo.name()) : cuatrimestreVigente();

        Pageable pageable = PageRequest.of(
                pagina != null && pagina >= 0 ? pagina : 0,
                tamanio != null && tamanio > 0 ? tamanio : 20);
        // RN-A.a/RN-B.a: "Buscar" filtra por Unidad Ejecutora y Año a nivel de datos (solo estudios
        // activos en la Programación Financiera de ese ejercicio, CU-PRE-30). El Período no excluye
        // filas: determina qué cuatrimestre se muestra/acumula en cada columna (RN-E).
        Page<FuenteFinanciamientoEtapaPap> resultado = fuenteRepository.buscarActivasEnAnio(unidadFiltro, anioEfectivo,
                pageable);

        List<EstudioFilaAvancePAPDto> contenido = resultado.getContent().stream()
                .filter(fuente -> !etapaFinalizada(fuente.getEtapaPreinversion(), anioEfectivo))
                .map(fuente -> construirFilaListaDto(fuente, anioEfectivo, periodoEfectivo))
                .toList();

        PaginacionMetadataDto paginacion = new PaginacionMetadataDto()
                .pagina(resultado.getNumber())
                .tamanio(resultado.getSize())
                .totalElementos(resultado.getTotalElements())
                .totalPaginas(resultado.getTotalPages());

        return new AvanceFinancieroPAPResponseDto(unidadFiltro, anioEfectivo, periodoEfectivo(periodoEfectivo),
                contenido, paginacion);
    }

    @Override
    @Transactional(readOnly = true)
    public AvanceEstudioDto obtenerAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodo) {
        actorContexto.exigirRol(ROLES_CONSULTA);
        Proyecto proyecto = buscarEstudio(cup);
        return construirEstudioDto(proyecto, anio, Cuatrimestre.valueOf(periodo.name()));
    }

    @Override
    public AvanceEstudioDto guardarAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodoDto,
            GuardarAvanceEstudioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarEstudio(cup);
        Cuatrimestre periodo = Cuatrimestre.valueOf(periodoDto.name());
        verificarPeriodoAbierto(anio, periodo);

        for (EtapaAvanceRequestDto etapaRequest : nullSafe(request.getEtapas())) {
            EtapaPreinversion etapa = etapaPreinversionRepository
                    .findByProyectoIdAndTipoEtapa(proyecto.getId(),
                            TipoEtapaPreinversion.valueOf(etapaRequest.getEtapa().name()))
                    .orElseThrow(() -> new RecursoNoEncontradoException("El estudio, la etapa o la fuente no existen."));
            for (FilaAvanceFuenteRequestDto fila : nullSafe(etapaRequest.getFuentes())) {
                guardarFila(etapa, fila, anio, periodo);
            }
        }

        return construirEstudioDto(proyecto, anio, periodo);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource generarReporte(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo, String formato) {
        Usuario actor = actorContexto.exigirRol(ROLES_CONSULTA);
        Cuatrimestre periodoEfectivo = Cuatrimestre.valueOf(periodo.name());
        // Anexo A.6: "la misma tabla de datos del Anexo A.1" -> mismo filtro por año (RN-B.a).
        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository
                .buscarActivasEnAnio(idUnidadEjecutora, anio, Pageable.unpaged()).getContent();
        List<EstudioFilaAvancePAPDto> filas = fuentes.stream()
                .filter(fuente -> !etapaFinalizada(fuente.getEtapaPreinversion(), anio))
                .map(fuente -> construirFilaListaDto(fuente, anio, periodoEfectivo))
                .toList();

        ReporteAvanceFinancieroPapGenerator.Encabezado encabezado = new ReporteAvanceFinancieroPapGenerator.Encabezado(
                nombreInstitucionEjecutora(idUnidadEjecutora), anio, periodoEfectivo,
                esActorInternoDgicp(actor),
                esActorInternoDgicp(actor) ? comentarioReporteFinancieroDgicp(idUnidadEjecutora, anio, periodoEfectivo)
                        : null);
        byte[] contenido = "PDF".equalsIgnoreCase(formato)
                ? ReporteAvanceFinancieroPapGenerator.generarPdf(encabezado, filas)
                : ReporteAvanceFinancieroPapGenerator.generarExcel(encabezado, filas);
        return new ByteArrayResource(contenido);
    }

    /** Anexo A.6 "Institución Ejecutora": nombre de la Institución a la que pertenece la Unidad Ejecutora. */
    private String nombreInstitucionEjecutora(Long idUnidadEjecutora) {
        if (idUnidadEjecutora == null) {
            return "";
        }
        return unidadEjecutoraRepository.findById(idUnidadEjecutora)
                .map(UnidadEjecutora::getInstitucion)
                .map(Institucion::getNombre)
                .orElse("");
    }

    /**
     * Anexo A.6 "Comentarios al reporte financiero DGICP": se registran en la revisión única del
     * avance del PAP (CU-PRE-33 SF-3, {@link RevisionAvancePap}) para la Unidad Ejecutora/Año/Período.
     */
    private String comentarioReporteFinancieroDgicp(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo) {
        return revisionRepository.findByIdUnidadEjecutoraAndAnioAndPeriodo(idUnidadEjecutora, anio, periodo)
                .map(RevisionAvancePap::getComentarioReporteFinancieroDgicp)
                .orElse(null);
    }

    private static boolean esActorInternoDgicp(Usuario actor) {
        for (RolUsuario rol : ROLES_DGICP_INTERNOS) {
            if (rol == actor.getRol()) {
                return true;
            }
        }
        return false;
    }

    // -----------------------------------------------------------------------------------------

    /** RN-A.a: "el cuatrimestre vigente" según el mes actual (I: ene-abr, II: may-ago, III: sep-dic). */
    private Cuatrimestre cuatrimestreVigente() {
        Month mes = LocalDateTime.now(ZONA_EL_SALVADOR).getMonth();
        if (mes.getValue() <= 4) {
            return Cuatrimestre.CUATRIMESTRE_I;
        }
        return mes.getValue() <= 8 ? Cuatrimestre.CUATRIMESTRE_II : Cuatrimestre.CUATRIMESTRE_III;
    }

    private static CuatrimestreDto periodoEfectivo(Cuatrimestre periodo) {
        return CuatrimestreDto.valueOf(periodo.name());
    }

    private Proyecto buscarEstudio(String cup) {
        Proyecto proyecto = proyectoRepository.findByCup(cup)
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudio (CUP + año + período) no existe."));
        if (etapaPreinversionRepository.findByProyectoId(proyecto.getId()).isEmpty()) {
            throw new RecursoNoEncontradoException("El estudio (CUP + año + período) no existe.");
        }
        return proyecto;
    }

    private List<EtapaPreinversion> etapasOrdenadas(Long idProyecto) {
        return etapaPreinversionRepository.findByProyectoId(idProyecto).stream()
                .sorted((a, b) -> Integer.compare(a.getTipoEtapa().ordinal(), b.getTipoEtapa().ordinal()))
                .toList();
    }

    /** RN-B.a: una etapa ya financiada al 100% de su costo en años anteriores al consultado deja de listarse. */
    private boolean etapaFinalizada(EtapaPreinversion etapa, Integer anio) {
        if (etapa.getCosto() == null || etapa.getCosto() <= 0) {
            return false;
        }
        BigDecimal ejecutadoHistorico = BigDecimal.ZERO;
        for (FuenteFinanciamientoEtapaPap fuente : fuenteRepository.findByEtapaPreinversionId(etapa.getId())) {
            ejecutadoHistorico = ejecutadoHistorico.add(ejecutadoAniosAnteriores(fuente.getId(), anio));
        }
        return ejecutadoHistorico.compareTo(BigDecimal.valueOf(etapa.getCosto())) >= 0;
    }

    /** RN-E: "Ejecutado años anteriores" = suma de lo ejecutado en años previos al consultado. */
    private BigDecimal ejecutadoAniosAnteriores(Long idFuente, Integer anio) {
        BigDecimal total = BigDecimal.ZERO;
        for (AvanceFinancieroCuatrimestral avance : avanceRepository.findByProgramacion_Fuente_Id(idFuente)) {
            if (avance.getProgramacion().getAnio() < anio) {
                total = total.add(avance.getMontoEjecutado());
            }
        }
        return total;
    }

    /** RN-E: acumulado ejecutado del año "anio", desde Cuatrimestre I hasta "hastaPeriodo" (inclusive). */
    private BigDecimal ejecutadoEnAnioHastaPeriodo(Long idFuente, Integer anio, Cuatrimestre hastaPeriodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (AvanceFinancieroCuatrimestral avance : avanceRepository.findByProgramacion_Fuente_Id(idFuente)) {
            if (avance.getProgramacion().getAnio().equals(anio) && avance.getCuatrimestre().ordinal() <= hastaPeriodo.ordinal()) {
                total = total.add(avance.getMontoEjecutado());
            }
        }
        return total;
    }

    private BigDecimal montoProgramadoCuatrimestre(ProgCuatrimestralFinanciera prog, Cuatrimestre periodo) {
        if (prog == null) {
            return BigDecimal.ZERO;
        }
        return switch (periodo) {
            case CUATRIMESTRE_I -> prog.getMontoCuatrimestre1();
            case CUATRIMESTRE_II -> prog.getMontoCuatrimestre2();
            case CUATRIMESTRE_III -> prog.getMontoCuatrimestre3();
        };
    }

    private BigDecimal montoProgramadoAlPeriodo(ProgCuatrimestralFinanciera prog, Cuatrimestre hastaPeriodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (Cuatrimestre c : Cuatrimestre.values()) {
            if (c.ordinal() <= hastaPeriodo.ordinal()) {
                total = total.add(montoProgramadoCuatrimestre(prog, c));
            }
        }
        return total;
    }

    private void guardarFila(EtapaPreinversion etapa, FilaAvanceFuenteRequestDto fila, Integer anio,
            Cuatrimestre periodo) {
        FuenteFinanciamientoEtapaPap fuente = fuenteRepository.findById(fila.getIdFuente())
                .filter(f -> f.getEtapaPreinversion().getId().equals(etapa.getId()))
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudio, la etapa o la fuente no existen."));
        ProgCuatrimestralFinanciera programacion = progRepository.findByFuenteIdAndAnio(fuente.getId(), anio)
                .orElseGet(() -> progRepository.save(
                        ProgCuatrimestralFinanciera.builder().fuente(fuente).anio(anio).build()));

        BigDecimal montoEjecutado = BigDecimal.valueOf(fila.getMontoEjecutadoCuatrimestre());
        BigDecimal ejecutadoAnualProgramado = programacion.totalProgramadoAnio();
        BigDecimal ejecutadoPrevioDelAnio = periodo == Cuatrimestre.CUATRIMESTRE_I
                ? BigDecimal.ZERO
                : ejecutadoEnAnioHastaPeriodo(fuente.getId(), anio, Cuatrimestre.deNumero(periodo.numero() - 1));
        if (ejecutadoPrevioDelAnio.add(montoEjecutado).compareTo(ejecutadoAnualProgramado) > 0) {
            throw new ValidacionNegocioException("MONTO_SUPERA_PROGRAMADO_ANUAL", MENSAJE_LIMITE_CUATRIMESTRE, null);
        }

        AvanceFinancieroCuatrimestral avance = avanceRepository
                .findByProgramacionIdAndCuatrimestre(programacion.getId(), periodo)
                .orElseGet(() -> AvanceFinancieroCuatrimestral.builder()
                        .programacion(programacion)
                        .cuatrimestre(periodo)
                        .build());
        avance.setMontoEjecutado(montoEjecutado);
        avance.setObservaciones(fila.getObservacionesCuatrimestre());
        avance.setFechaRegistro(LocalDateTime.now(ZONA_EL_SALVADOR));
        avanceRepository.save(avance);
    }

    private AvanceEstudioDto construirEstudioDto(Proyecto proyecto, Integer anio, Cuatrimestre periodo) {
        List<EtapaAvanceDto> etapas = etapasOrdenadas(proyecto.getId()).stream()
                .filter(etapa -> !etapaFinalizada(etapa, anio))
                .map(etapa -> construirEtapaDto(etapa, anio, periodo))
                .toList();
        return new AvanceEstudioDto(proyecto.getCup(), proyecto.getNombre(), etapas);
    }

    /**
     * RN-B.b: el acumulado ejecutado (años anteriores + Avance Anual/al Cuatrimestre del año hasta el
     * período) no puede superar el "Costo de la Etapa" programado en CU-PRE-30. El costo es único por
     * etapa, así que se compara la SUMA de todas sus fuentes de financiamiento (mismo criterio que
     * CU-PRE-30 RN-B.c); la alerta se replica en cada fila de fuente de esa etapa.
     */
    private boolean alertaExcesoEtapa(EtapaPreinversion etapa, Integer anio, Cuatrimestre periodo) {
        BigDecimal ejecutadoEtapa = CostoEtapaPapSupport.sumarPorEtapa(
                fuenteRepository.findByEtapaPreinversionId(etapa.getId()),
                fuente -> ejecutadoAniosAnteriores(fuente.getId(), anio)
                        .add(ejecutadoEnAnioHastaPeriodo(fuente.getId(), anio, periodo)));
        return CostoEtapaPapSupport.superaCostoEtapa(etapa.getCosto(), ejecutadoEtapa);
    }

    private EtapaAvanceDto construirEtapaDto(EtapaPreinversion etapa, Integer anio, Cuatrimestre periodo) {
        List<FuenteFinanciamientoEtapaPap> fuentesEtapa = fuenteRepository.findByEtapaPreinversionId(etapa.getId());
        Boolean alertaExceso = etapa.getCosto() != null ? alertaExcesoEtapa(etapa, anio, periodo) : null;
        List<FilaAvanceFuenteDto> fuentes = fuentesEtapa.stream()
                .map(fuente -> construirFilaDto(fuente, alertaExceso, anio, periodo))
                .toList();
        BigDecimal ejecutadoAnterior = CostoEtapaPapSupport.sumarPorEtapa(fuentesEtapa,
                fuente -> ejecutadoAniosAnteriores(fuente.getId(), anio));
        return new EtapaAvanceDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()), fuentes)
                .costoEtapa(etapa.getCosto())
                .ejecutadoAniosAnteriores(
                        ejecutadoAnterior.compareTo(BigDecimal.ZERO) > 0 ? ejecutadoAnterior.doubleValue() : null);
    }

    private FilaAvanceFuenteDto construirFilaDto(FuenteFinanciamientoEtapaPap fuente, Boolean alertaExcesoEtapa,
            Integer anio, Cuatrimestre periodo) {
        ProgCuatrimestralFinanciera prog = progRepository.findByFuenteIdAndAnio(fuente.getId(), anio).orElse(null);
        BigDecimal programadoDelPeriodo = montoProgramadoCuatrimestre(prog, periodo);
        BigDecimal programadoAnual = prog != null ? prog.totalProgramadoAnio() : BigDecimal.ZERO;
        BigDecimal programadoAlPeriodo = montoProgramadoAlPeriodo(prog, periodo);

        BigDecimal ejecutadoAlPeriodo = ejecutadoEnAnioHastaPeriodo(fuente.getId(), anio, periodo);
        AvanceFinancieroCuatrimestral avanceDelPeriodo = prog != null
                ? avanceRepository.findByProgramacionIdAndCuatrimestre(prog.getId(), periodo).orElse(null)
                : null;
        BigDecimal ejecutadoDelPeriodo = avanceDelPeriodo != null ? avanceDelPeriodo.getMontoEjecutado() : BigDecimal.ZERO;

        FilaAvanceFuenteDto dto = new FilaAvanceFuenteDto(fuente.getId(), ejecutadoDelPeriodo.doubleValue())
                .fuenteFinanciamiento(fuente.getFuenteFinanciamiento() != null
                        ? FuenteFinanciamientoDto.valueOf(fuente.getFuenteFinanciamiento().name())
                        : null)
                .montoProgramadoCuatrimestre(programadoDelPeriodo.doubleValue())
                .observacionesCuatrimestre(avanceDelPeriodo != null ? avanceDelPeriodo.getObservaciones() : null)
                .avanceAnualProgramado(programadoAnual.doubleValue())
                .avanceAnualEjecutadoMonto(ejecutadoAlPeriodo.doubleValue())
                .avanceAlCuatrimestreProgramado(programadoAlPeriodo.doubleValue())
                .avanceAlCuatrimestreEjecutadoMonto(ejecutadoAlPeriodo.doubleValue())
                .avancesCuatrimestresAnteriores(avancesCuatrimestresAnteriores(fuente.getId(), anio, periodo));

        if (programadoAnual.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceAnualEjecutadoPorcentaje(porcentaje(ejecutadoAlPeriodo, programadoAnual));
        }
        if (programadoAlPeriodo.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceAlCuatrimestreEjecutadoPorcentaje(porcentaje(ejecutadoAlPeriodo, programadoAlPeriodo));
        }
        if (programadoDelPeriodo.compareTo(BigDecimal.ZERO) > 0) {
            dto.porcentajeEjecutadoCuatrimestre(porcentaje(ejecutadoDelPeriodo, programadoDelPeriodo));
        }
        if (alertaExcesoEtapa != null) {
            dto.alertaExcesoProgramado(alertaExcesoEtapa);
        }
        return dto;
    }

    private EstudioFilaAvancePAPDto construirFilaListaDto(FuenteFinanciamientoEtapaPap fuente, Integer anio,
            Cuatrimestre periodo) {
        EtapaPreinversion etapa = fuente.getEtapaPreinversion();
        Proyecto proyecto = etapa.getProyecto();
        ProgCuatrimestralFinanciera prog = progRepository.findByFuenteIdAndAnio(fuente.getId(), anio).orElse(null);
        BigDecimal programadoDelPeriodo = montoProgramadoCuatrimestre(prog, periodo);
        BigDecimal programadoAnual = prog != null ? prog.totalProgramadoAnio() : BigDecimal.ZERO;
        BigDecimal programadoAlPeriodo = montoProgramadoAlPeriodo(prog, periodo);

        BigDecimal ejecutadoAnterior = ejecutadoAniosAnteriores(fuente.getId(), anio);
        BigDecimal ejecutadoAlPeriodo = ejecutadoEnAnioHastaPeriodo(fuente.getId(), anio, periodo);
        AvanceFinancieroCuatrimestral avanceDelPeriodo = prog != null
                ? avanceRepository.findByProgramacionIdAndCuatrimestre(prog.getId(), periodo).orElse(null)
                : null;
        BigDecimal ejecutadoDelPeriodo = avanceDelPeriodo != null ? avanceDelPeriodo.getMontoEjecutado() : BigDecimal.ZERO;

        EstudioFilaAvancePAPDto dto = new EstudioFilaAvancePAPDto(proyecto.getCup(), proyecto.getNombre(),
                NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()))
                .costoEtapa(etapa.getCosto())
                .fuenteFinanciamiento(fuente.getFuenteFinanciamiento() != null
                        ? FuenteFinanciamientoDto.valueOf(fuente.getFuenteFinanciamiento().name())
                        : null)
                .ejecutadoAniosAnteriores(
                        ejecutadoAnterior.compareTo(BigDecimal.ZERO) > 0 ? ejecutadoAnterior.doubleValue() : null)
                .avanceAnualProgramado(programadoAnual.doubleValue())
                .avanceAnualEjecutadoMonto(ejecutadoAlPeriodo.doubleValue())
                .avanceAlCuatrimestreProgramado(programadoAlPeriodo.doubleValue())
                .avanceAlCuatrimestreEjecutadoMonto(ejecutadoAlPeriodo.doubleValue())
                .avanceDelCuatrimestreProgramado(programadoDelPeriodo.doubleValue())
                .avanceDelCuatrimestreEjecutadoMonto(ejecutadoDelPeriodo.doubleValue())
                .observaciones(avanceDelPeriodo != null ? avanceDelPeriodo.getObservaciones() : null);

        if (programadoAnual.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceAnualEjecutadoPorcentaje(porcentaje(ejecutadoAlPeriodo, programadoAnual));
        }
        if (programadoAlPeriodo.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceAlCuatrimestreEjecutadoPorcentaje(porcentaje(ejecutadoAlPeriodo, programadoAlPeriodo));
        }
        if (programadoDelPeriodo.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceDelCuatrimestrePorcentaje(porcentaje(ejecutadoDelPeriodo, programadoDelPeriodo));
        }
        if (etapa.getCosto() != null) {
            dto.alertaExcesoProgramado(alertaExcesoEtapa(etapa, anio, periodo));
        }
        return dto;
    }

    /** RN-F: vacío en Cuatrimestre I; lo ejecutado en los cuatrimestres previos al consultado en los demás casos. */
    private List<AvanceCuatrimestreAnteriorDto> avancesCuatrimestresAnteriores(Long idFuente, Integer anio,
            Cuatrimestre periodo) {
        List<AvanceCuatrimestreAnteriorDto> resultado = new ArrayList<>();
        for (AvanceFinancieroCuatrimestral avance : avanceRepository.findByProgramacion_Fuente_Id(idFuente)) {
            if (avance.getProgramacion().getAnio().equals(anio) && avance.getCuatrimestre().ordinal() < periodo.ordinal()) {
                resultado.add(new AvanceCuatrimestreAnteriorDto(CuatrimestreDto.valueOf(avance.getCuatrimestre().name()),
                        avance.getMontoEjecutado().doubleValue()));
            }
        }
        resultado.sort((a, b) -> a.getPeriodo().ordinal() - b.getPeriodo().ordinal());
        return resultado;
    }

    /**
     * RN-A.b. [SUPUESTO] Sin evento EJECUCION_PAP configurado para el año/cuatrimestre se asume el
     * período abierto (la especificación no define ese caso; mismo criterio que CU-PRE-30).
     */
    private void verificarPeriodoAbierto(Integer anio, Cuatrimestre periodo) {
        boolean abierto = calendarioEventoRepository
                .findByTipoEventoAndAnioAndCuatrimestre(TipoEventoCalendario.EJECUCION_PAP, anio, periodo.numero())
                .map(evento -> evento.getEstado() == EstadoCalendarioEvento.ABIERTO)
                .orElse(true);
        if (!abierto) {
            throw new ConflictoEstadoException("PERIODO_CERRADO", "Periodo de ingreso de información ha finalizado.");
        }
    }

    private static double porcentaje(BigDecimal monto, BigDecimal total) {
        return monto.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP).doubleValue();
    }

    private static <T> List<T> nullSafe(List<T> lista) {
        return lista != null ? lista : List.of();
    }
}
