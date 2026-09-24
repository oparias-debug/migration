package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
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
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.dto.AvanceCuatrimestreMetaAnteriorDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoRevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP". Análogo de
 * {@link AvanceFinancieroPapServiceImpl} (CU-PRE-32) pero con valores porcentuales (0-100), y con el
 * ciclo de revisión ({@link RevisionAvancePap}) que aquí sí vive: es la única aprobación unificada
 * del avance del PAP (financiero y de metas físicas, decisión funcional v1.2). El paso "Enviar a
 * revisión DGICP" que sí existe en CU-PRE-31 no está documentado aquí (ningún Flujo Básico/Subflujo
 * lo narra) — no se modela, tal como señala el propio CU-PRE-33.openapi.yaml.
 */
@Service
@Transactional
public class AvanceMetasFisicasPapServiceImpl implements AvanceMetasFisicasPapService {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private static final RolUsuario[] ROLES_CONSULTA = {
            RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE,
            RolUsuario.COORDINADOR_PROGRAMACION, RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI
    };

    /** RN-D.a: actores internos de la DGICP, únicos que ven los "Comentarios al reporte DGICP". */
    private static final RolUsuario[] ROLES_DGICP_INTERNOS = {
            RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE, RolUsuario.COORDINADOR_PROGRAMACION,
            RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI
    };

    private static final double META_TOTAL = 1.0d;
    /** RN-B.d: la meta del estudio es el 100% (Ejecutado años anteriores + Ejecutado en el año). */
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final String MENSAJE_LIMITE_100 = "El porcentaje total registrado supera el 100%";

    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final AvanceCuatriMetaFisicaRepository avanceRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final RevisionAvancePapRepository revisionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final ActorContexto actorContexto;

    public AvanceMetasFisicasPapServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository,
            AvanceCuatriMetaFisicaRepository avanceRepository,
            CalendarioEventoRepository calendarioEventoRepository, RevisionAvancePapRepository revisionRepository,
            UsuarioRepository usuarioRepository, NotificacionService notificacionService,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.revisionRepository = revisionRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public AvanceMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio) {
        Usuario actor = actorContexto.exigirRol(ROLES_CONSULTA);
        Long unidadFiltro = unidadEjecutoraEfectiva(actor, idUnidadEjecutora);
        int anioEfectivo = anio != null ? anio : Year.now(ZONA_EL_SALVADOR).getValue();
        Cuatrimestre periodoEfectivo = periodo != null ? Cuatrimestre.valueOf(periodo.name()) : cuatrimestreVigente();

        Pageable pageable = PageRequest.of(
                pagina != null && pagina >= 0 ? pagina : 0,
                tamanio != null && tamanio > 0 ? tamanio : 20);
        Page<EtapaMetaFisicaPap> resultado = etapaMetaRepository.buscar(unidadFiltro, pageable);

        List<EstudioFilaAvanceMetasDto> contenido = resultado.getContent().stream()
                .map(etapaMeta -> construirFilaListaDto(etapaMeta, anioEfectivo, periodoEfectivo))
                .toList();

        PaginacionMetadataDto paginacion = new PaginacionMetadataDto()
                .pagina(resultado.getNumber())
                .tamanio(resultado.getSize())
                .totalElementos(resultado.getTotalElements())
                .totalPaginas(resultado.getTotalPages());

        return new AvanceMetasFisicasPAPResponseDto(unidadFiltro, anioEfectivo, dtoDe(periodoEfectivo), contenido,
                paginacion);
    }

    @Override
    @Transactional(readOnly = true)
    public AvanceMetasEstudioDto obtenerAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodo) {
        actorContexto.exigirRol(ROLES_CONSULTA);
        Proyecto proyecto = buscarEstudio(cup);
        return construirEstudioDto(proyecto, anio, Cuatrimestre.valueOf(periodo.name()));
    }

    @Override
    public AvanceMetasEstudioDto guardarAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodoDto,
            GuardarAvanceMetasEstudioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarEstudio(cup);
        Cuatrimestre periodo = Cuatrimestre.valueOf(periodoDto.name());
        verificarPeriodoAbierto(anio, periodo);

        for (EtapaAvanceMetasRequestDto etapaRequest : nullSafe(request.getEtapas())) {
            EtapaPreinversion etapa = etapaPreinversionRepository
                    .findByProyectoIdAndTipoEtapa(proyecto.getId(),
                            TipoEtapaPreinversion.valueOf(etapaRequest.getEtapa().name()))
                    .orElseThrow(() -> new RecursoNoEncontradoException("El estudio o la etapa no existen."));
            guardarFila(etapa, etapaRequest, anio, periodo);
        }

        return construirEstudioDto(proyecto, anio, periodo);
    }

    @Override
    public RevisionAvancePAPDto registrarObservacionesAvanceDgicp(RegistrarObservacionesAvanceDgicpRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        revision.setObservacionesDgicp(request.getObservacionesDgicp());
        return construirRevisionDto(revisionRepository.save(revision), actor);
    }

    @Override
    public RevisionAvancePAPDto enviarObservacionesAvanceDgicp(EnviarObservacionesAvanceDgicpRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        revision.setFechaObservaciones(LocalDateTime.now(ZONA_EL_SALVADOR));
        revision.setEstado(EstadoRevisionAvancePap.OBSERVADO);
        RevisionAvancePap guardada = revisionRepository.save(revision);

        List<Usuario> tecnicosUrp = usuarioRepository.findByRolAndUnidadEjecutora_IdAndActivoTrue(RolUsuario.TECNICO_URP,
                request.getIdUnidadEjecutora());
        notificacionService.notificarObservacionesAvance(request.getIdUnidadEjecutora(), request.getAnio(),
                periodo.name(), tecnicosUrp);
        return construirRevisionDto(guardada, actor);
    }

    @Override
    public RevisionAvancePAPDto registrarRespuestaInstitucionAvance(RegistrarRespuestaInstitucionAvanceRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        revision.setRespuestaInstitucion(request.getRespuestaInstitucion());
        return construirRevisionDto(revisionRepository.save(revision), actor);
    }

    @Override
    public RevisionAvancePAPDto enviarRespuestaInstitucionAvance(EnviarObservacionesAvanceDgicpRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        revision.setFechaRespuesta(LocalDateTime.now(ZONA_EL_SALVADOR));
        RevisionAvancePap guardada = revisionRepository.save(revision);

        List<Usuario> tecnicosPre = usuarioRepository.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE);
        notificacionService.notificarRespuestaInstitucionAvance(request.getIdUnidadEjecutora(), request.getAnio(),
                periodo.name(), tecnicosPre);
        return construirRevisionDto(guardada, actor);
    }

    @Override
    public RevisionAvancePAPDto finalizarRevisionAvance(FinalizarRevisionAvanceRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        if (request.getComentarioReporteFinancieroDgicp() != null) {
            revision.setComentarioReporteFinancieroDgicp(request.getComentarioReporteFinancieroDgicp());
        }
        if (request.getComentarioReporteMetasFisicasDgicp() != null) {
            revision.setComentarioReporteMetasFisicasDgicp(request.getComentarioReporteMetasFisicasDgicp());
        }
        // RN-E: actualiza el estado a "Revisado" en el Monitoreo PAP de CU-EJE-10 (efecto secundario
        // documentado, sin endpoint propio implementado todavía en ese CU).
        revision.setEstado(EstadoRevisionAvancePap.REVISADO);
        return construirRevisionDto(revisionRepository.save(revision), actor);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource generarReporteAvanceMetas(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            String formato) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        // Mismo criterio que listar(): el Técnico URP solo puede generar el reporte de su propia unidad
        // ejecutora; el parámetro recibido solo se respeta para el Técnico PRE.
        Long unidadFiltro = unidadEjecutoraEfectiva(actor, idUnidadEjecutora);
        Cuatrimestre periodoEfectivo = Cuatrimestre.valueOf(periodo.name());
        List<EtapaMetaFisicaPap> etapasMeta = etapaMetaRepository
                .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(
                        unidadFiltro);
        List<EstudioFilaAvanceMetasDto> filas = etapasMeta.stream()
                .map(etapaMeta -> construirFilaListaDto(etapaMeta, anio, periodoEfectivo))
                .toList();

        byte[] contenido = "PDF".equalsIgnoreCase(formato)
                ? ReporteAvanceMetasFisicasPapGenerator.generarPdf(unidadFiltro, anio, periodoEfectivo, filas)
                : ReporteAvanceMetasFisicasPapGenerator.generarExcel(unidadFiltro, anio, periodoEfectivo, filas);
        return new ByteArrayResource(contenido);
    }

    // -----------------------------------------------------------------------------------------

    private Cuatrimestre cuatrimestreVigente() {
        Month mes = LocalDateTime.now(ZONA_EL_SALVADOR).getMonth();
        if (mes.getValue() <= 4) {
            return Cuatrimestre.CUATRIMESTRE_I;
        }
        return mes.getValue() <= 8 ? Cuatrimestre.CUATRIMESTRE_II : Cuatrimestre.CUATRIMESTRE_III;
    }

    /** El Técnico URP queda restringido a su propia unidad ejecutora; los demás actores usan el parámetro. */
    private static Long unidadEjecutoraEfectiva(Usuario actor, Long idUnidadEjecutora) {
        return actor.getRol() == RolUsuario.TECNICO_URP
                ? actor.getUnidadEjecutora().getId()
                : idUnidadEjecutora;
    }

    private static CuatrimestreDto dtoDe(Cuatrimestre periodo) {
        return CuatrimestreDto.valueOf(periodo.name());
    }

    private static boolean esActorInternoDgicp(Usuario actor) {
        for (RolUsuario rol : ROLES_DGICP_INTERNOS) {
            if (rol == actor.getRol()) {
                return true;
            }
        }
        return false;
    }

    private RevisionAvancePap obtenerORevision(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo) {
        return revisionRepository.findByIdUnidadEjecutoraAndAnioAndPeriodo(idUnidadEjecutora, anio, periodo)
                .orElseGet(() -> RevisionAvancePap.builder()
                        .idUnidadEjecutora(idUnidadEjecutora)
                        .anio(anio)
                        .periodo(periodo)
                        .build());
    }

    /** RN-A.b: los comentarios al reporte DGICP no son visibles para el Técnico URP. */
    private RevisionAvancePAPDto construirRevisionDto(RevisionAvancePap revision, Usuario actor) {
        RevisionAvancePAPDto dto = new RevisionAvancePAPDto(revision.getIdUnidadEjecutora(), revision.getAnio(),
                dtoDe(revision.getPeriodo()), EstadoRevisionAvancePAPDto.valueOf(revision.getEstado().name()))
                .observacionesDgicp(revision.getObservacionesDgicp())
                .fechaObservaciones(aOffsetDateTime(revision.getFechaObservaciones()))
                .respuestaInstitucion(revision.getRespuestaInstitucion())
                .fechaRespuesta(aOffsetDateTime(revision.getFechaRespuesta()));
        if (esActorInternoDgicp(actor)) {
            dto.comentarioReporteFinancieroDgicp(revision.getComentarioReporteFinancieroDgicp());
            dto.comentarioReporteMetasFisicasDgicp(revision.getComentarioReporteMetasFisicasDgicp());
        }
        return dto;
    }

    private static OffsetDateTime aOffsetDateTime(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(ZONA_EL_SALVADOR).toOffsetDateTime();
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

    /** RN-F: "Ejecutado años anteriores" = suma de lo ejecutado en años previos al consultado. */
    private BigDecimal ejecutadoAniosAnteriores(Long idEtapaMetaFisica, Integer anio) {
        BigDecimal total = BigDecimal.ZERO;
        for (AvanceCuatriMetaFisica avance : avanceRepository.findByProgramacionMeta_EtapaMetaFisica_Id(idEtapaMetaFisica)) {
            if (avance.getProgramacionMeta().getAnio() < anio) {
                total = total.add(avance.getAvanceCuatrimestre());
            }
        }
        return total;
    }

    /** RN-F: acumulado ejecutado del año "anio", desde Cuatrimestre I hasta "hastaPeriodo" (inclusive). */
    private BigDecimal ejecutadoEnAnioHastaPeriodo(Long idEtapaMetaFisica, Integer anio, Cuatrimestre hastaPeriodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (AvanceCuatriMetaFisica avance : avanceRepository.findByProgramacionMeta_EtapaMetaFisica_Id(idEtapaMetaFisica)) {
            if (avance.getProgramacionMeta().getAnio().equals(anio)
                    && avance.getCuatrimestre().ordinal() <= hastaPeriodo.ordinal()) {
                total = total.add(avance.getAvanceCuatrimestre());
            }
        }
        return total;
    }

    private BigDecimal programadoCuatrimestre(ProgCuatrimestralMetaFisica prog, Cuatrimestre periodo) {
        if (prog == null) {
            return BigDecimal.ZERO;
        }
        return switch (periodo) {
            case CUATRIMESTRE_I -> prog.getMontoCuatrimestre1();
            case CUATRIMESTRE_II -> prog.getMontoCuatrimestre2();
            case CUATRIMESTRE_III -> prog.getMontoCuatrimestre3();
        };
    }

    private BigDecimal programadoAlPeriodo(ProgCuatrimestralMetaFisica prog, Cuatrimestre hastaPeriodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (Cuatrimestre c : Cuatrimestre.values()) {
            if (c.ordinal() <= hastaPeriodo.ordinal()) {
                total = total.add(programadoCuatrimestre(prog, c));
            }
        }
        return total;
    }

    private void guardarFila(EtapaPreinversion etapa, EtapaAvanceMetasRequestDto etapaRequest, Integer anio,
            Cuatrimestre periodo) {
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudio o la etapa no existen."));
        ProgCuatrimestralMetaFisica programacion = progRepository
                .findByEtapaMetaFisicaIdAndAnio(etapaMeta.getId(), anio)
                .orElseGet(() -> progRepository.save(
                        ProgCuatrimestralMetaFisica.builder().etapaMetaFisica(etapaMeta).anio(anio).build()));

        BigDecimal avanceCuatrimestre = BigDecimal.valueOf(etapaRequest.getAvanceCuatrimestre());
        BigDecimal ejecutadoAnterior = ejecutadoAniosAnteriores(etapaMeta.getId(), anio);
        BigDecimal ejecutadoPrevioDelAnio = periodo == Cuatrimestre.CUATRIMESTRE_I
                ? BigDecimal.ZERO
                : ejecutadoEnAnioHastaPeriodo(etapaMeta.getId(), anio, Cuatrimestre.deNumero(periodo.numero() - 1));
        BigDecimal ejecutadoEnElAnio = ejecutadoPrevioDelAnio.add(avanceCuatrimestre);

        // RN-C.b: el avance del cuatrimestre no debe superar el "Programado en el Año" de la
        // programación de metas físicas (CU-PRE-31; AAP de RN-F). Se valida el acumulado del año
        // (cuatrimestres previos + el que se registra), igual que CU-PRE-32 con el monto anual
        // programado. RN-B.b: los estudios con "programado del cuatrimestre" = 0 siempre pueden
        // reportar su avance, por lo que quedan fuera de este tope (sigue aplicando RN-B.d).
        boolean exentoRnBb = programadoCuatrimestre(programacion, periodo).signum() == 0;
        if (!exentoRnBb && ejecutadoEnElAnio.compareTo(programacion.totalProgramadoAnio()) > 0) {
            throw new ValidacionNegocioException("PORCENTAJE_SUPERA_PROGRAMADO_ANUAL", MENSAJE_LIMITE_100, null);
        }
        // RN-B.d: el acumulado (Ejecutado años anteriores + Ejecutado en el año) no puede superar el 100%.
        if (ejecutadoAnterior.add(ejecutadoEnElAnio).compareTo(CIEN) > 0) {
            throw new ValidacionNegocioException("PORCENTAJE_SUPERA_100", MENSAJE_LIMITE_100, null);
        }

        AvanceCuatriMetaFisica avance = avanceRepository
                .findByProgramacionMetaIdAndCuatrimestre(programacion.getId(), periodo)
                .orElseGet(() -> AvanceCuatriMetaFisica.builder()
                        .programacionMeta(programacion)
                        .cuatrimestre(periodo)
                        .build());
        avance.setAvanceCuatrimestre(avanceCuatrimestre);
        avance.setObservaciones(etapaRequest.getObservacionesCuatrimestre());
        avance.setFechaRegistro(LocalDateTime.now(ZONA_EL_SALVADOR));
        avanceRepository.save(avance);
    }

    private AvanceMetasEstudioDto construirEstudioDto(Proyecto proyecto, Integer anio, Cuatrimestre periodo) {
        List<EtapaAvanceMetasDto> etapas = etapasOrdenadas(proyecto.getId()).stream()
                .map(etapa -> construirEtapaDto(etapa, anio, periodo))
                .toList();
        return new AvanceMetasEstudioDto(proyecto.getCup(), proyecto.getNombre(), etapas);
    }

    private EtapaAvanceMetasDto construirEtapaDto(EtapaPreinversion etapa, Integer anio, Cuatrimestre periodo) {
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId()).orElse(null);
        Datos datos = calcularDatos(etapaMeta, anio, periodo);

        return new EtapaAvanceMetasDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()),
                datos.ejecutadoDelPeriodo().doubleValue())
                .meta(META_TOTAL)
                .entregable(etapaMeta != null && etapaMeta.getEntregable() != null ? etapaMeta.getEntregable().name() : null)
                .ejecutadoAniosAnteriores(
                        datos.ejecutadoAnterior().compareTo(BigDecimal.ZERO) > 0 ? datos.ejecutadoAnterior().doubleValue() : null)
                .programadoEnElAnio(datos.programadoAnual().doubleValue())
                .ejecutadoEnElAnio(datos.ejecutadoAlPeriodo().doubleValue())
                .programadoAlCuatrimestre(datos.programadoAlPeriodo().doubleValue())
                .ejecutadoAlCuatrimestre(datos.ejecutadoAlPeriodo().doubleValue())
                .programadoDelCuatrimestre(datos.programadoDelPeriodo().doubleValue())
                .totalMetaEjecutada(datos.totalMetaEjecutada().doubleValue())
                .estado(datos.estado() != null ? EstadoAvanceMetasDto.valueOf(datos.estado().name()) : null)
                .avancesCuatrimestresAnteriores(
                        etapaMeta != null ? avancesCuatrimestresAnteriores(etapaMeta.getId(), anio, periodo) : List.of());
    }

    private EstudioFilaAvanceMetasDto construirFilaListaDto(EtapaMetaFisicaPap etapaMeta, Integer anio,
            Cuatrimestre periodo) {
        EtapaPreinversion etapa = etapaMeta.getEtapaPreinversion();
        Proyecto proyecto = etapa.getProyecto();
        Datos datos = calcularDatos(etapaMeta, anio, periodo);
        ProgCuatrimestralMetaFisica prog = progRepository.findByEtapaMetaFisicaIdAndAnio(etapaMeta.getId(), anio)
                .orElse(null);
        AvanceCuatriMetaFisica avanceDelPeriodo = prog != null
                ? avanceRepository.findByProgramacionMetaIdAndCuatrimestre(prog.getId(), periodo).orElse(null)
                : null;

        return new EstudioFilaAvanceMetasDto(proyecto.getCup(), proyecto.getNombre(),
                NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()))
                .meta(META_TOTAL)
                .entregable(etapaMeta.getEntregable() != null ? etapaMeta.getEntregable().name() : null)
                .ejecutadoAniosAnteriores(
                        datos.ejecutadoAnterior().compareTo(BigDecimal.ZERO) > 0 ? datos.ejecutadoAnterior().doubleValue() : null)
                .programadoEnElAnio(datos.programadoAnual().doubleValue())
                .ejecutadoEnElAnio(datos.ejecutadoAlPeriodo().doubleValue())
                .programadoAlCuatrimestre(datos.programadoAlPeriodo().doubleValue())
                .ejecutadoAlCuatrimestre(datos.ejecutadoAlPeriodo().doubleValue())
                .programadoDelCuatrimestre(datos.programadoDelPeriodo().doubleValue())
                .ejecutadoDelCuatrimestre(datos.ejecutadoDelPeriodo().doubleValue())
                .totalMetaEjecutada(datos.totalMetaEjecutada().doubleValue())
                .observaciones(avanceDelPeriodo != null ? avanceDelPeriodo.getObservaciones() : null)
                .estado(datos.estado() != null ? EstadoAvanceMetasDto.valueOf(datos.estado().name()) : null);
    }

    private Datos calcularDatos(EtapaMetaFisicaPap etapaMeta, Integer anio, Cuatrimestre periodo) {
        if (etapaMeta == null) {
            return new Datos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, null);
        }
        ProgCuatrimestralMetaFisica prog = progRepository.findByEtapaMetaFisicaIdAndAnio(etapaMeta.getId(), anio)
                .orElse(null);
        BigDecimal programadoDelPeriodo = programadoCuatrimestre(prog, periodo);
        BigDecimal programadoAnual = prog != null ? prog.totalProgramadoAnio() : BigDecimal.ZERO;
        BigDecimal programadoAlPeriodo = programadoAlPeriodo(prog, periodo);

        BigDecimal ejecutadoAnterior = ejecutadoAniosAnteriores(etapaMeta.getId(), anio);
        BigDecimal ejecutadoAlPeriodo = ejecutadoEnAnioHastaPeriodo(etapaMeta.getId(), anio, periodo);
        AvanceCuatriMetaFisica avanceDelPeriodo = prog != null
                ? avanceRepository.findByProgramacionMetaIdAndCuatrimestre(prog.getId(), periodo).orElse(null)
                : null;
        BigDecimal ejecutadoDelPeriodo = avanceDelPeriodo != null ? avanceDelPeriodo.getAvanceCuatrimestre() : BigDecimal.ZERO;
        BigDecimal totalMetaEjecutada = ejecutadoAnterior.add(ejecutadoAlPeriodo);

        EstadoAvanceMetas estado = calcularEstado(totalMetaEjecutada, ejecutadoAlPeriodo, programadoAlPeriodo,
                programadoAnual);

        return new Datos(programadoDelPeriodo, programadoAnual, programadoAlPeriodo, ejecutadoAnterior,
                ejecutadoAlPeriodo, ejecutadoDelPeriodo, totalMetaEjecutada, estado);
    }

    /**
     * RN-B.c, calculado al cuatrimestre:
     * <ul>
     * <li>Finalizado: estudio concluido conforme a la ejecución de metas físicas, es decir, el "Total
     * meta ejecutada" alcanzó la meta del estudio (100%, RN-B.d). No se liga al "Programado en el Año":
     * en estudios plurianuales cumplir lo programado del año no concluye el estudio, y en el
     * Cuatrimestre III lo programado al cuatrimestre coincide con el del año, por lo que ese caso es
     * "A tiempo".</li>
     * <li>A tiempo: ejecutado al cuatrimestre igual a lo programado al cuatrimestre.</li>
     * <li>Atrasado: ejecutado al cuatrimestre menor a lo programado al cuatrimestre.</li>
     * <li>Adelantado: ejecutado al cuatrimestre mayor a lo programado al cuatrimestre, sin sobrepasar
     * el "Programado en el Año".</li>
     * </ul>
     * Si lo ejecutado en el año sobrepasa el "Programado en el Año" (solo posible por la excepción de
     * RN-B.b o por una reprogramación posterior en CU-PRE-31), el estudio no encaja en ningún estado
     * del catálogo y se devuelve {@code null} (el campo "estado" es nullable en el contrato).
     */
    private static EstadoAvanceMetas calcularEstado(BigDecimal totalMetaEjecutada, BigDecimal ejecutadoAlPeriodo,
            BigDecimal programadoAlPeriodo, BigDecimal programadoAnual) {
        if (totalMetaEjecutada.compareTo(CIEN) >= 0) {
            return EstadoAvanceMetas.FINALIZADO;
        }
        int comparacion = ejecutadoAlPeriodo.compareTo(programadoAlPeriodo);
        if (comparacion == 0) {
            return EstadoAvanceMetas.A_TIEMPO;
        }
        if (comparacion < 0) {
            return EstadoAvanceMetas.ATRASADO;
        }
        return ejecutadoAlPeriodo.compareTo(programadoAnual) <= 0 ? EstadoAvanceMetas.ADELANTADO : null;
    }

    /** RN-G: vacío en Cuatrimestre I; lo ejecutado en los cuatrimestres previos al consultado en los demás casos. */
    private List<AvanceCuatrimestreMetaAnteriorDto> avancesCuatrimestresAnteriores(Long idEtapaMetaFisica,
            Integer anio, Cuatrimestre periodo) {
        List<AvanceCuatrimestreMetaAnteriorDto> resultado = new ArrayList<>();
        for (AvanceCuatriMetaFisica avance : avanceRepository.findByProgramacionMeta_EtapaMetaFisica_Id(idEtapaMetaFisica)) {
            if (avance.getProgramacionMeta().getAnio().equals(anio) && avance.getCuatrimestre().ordinal() < periodo.ordinal()) {
                resultado.add(new AvanceCuatrimestreMetaAnteriorDto(dtoDe(avance.getCuatrimestre()),
                        avance.getAvanceCuatrimestre().doubleValue()));
            }
        }
        resultado.sort((a, b) -> a.getPeriodo().ordinal() - b.getPeriodo().ordinal());
        return resultado;
    }

    private void verificarPeriodoAbierto(Integer anio, Cuatrimestre periodo) {
        boolean abierto = calendarioEventoRepository
                .findByTipoEventoAndAnioAndCuatrimestre(TipoEventoCalendario.EJECUCION_PAP, anio, periodo.numero())
                .map(evento -> evento.getEstado() == EstadoCalendarioEvento.ABIERTO)
                .orElse(true);
        if (!abierto) {
            throw new ConflictoEstadoException("PERIODO_CERRADO", "Periodo de ingreso de información ha finalizado.");
        }
    }

    private static <T> List<T> nullSafe(List<T> lista) {
        return lista != null ? lista : List.of();
    }

    /** Catálogo de Estados del estudio (RN-B.c), interno — se traduce a {@link EstadoAvanceMetasDto} al construir el DTO. */
    private enum EstadoAvanceMetas {
        A_TIEMPO, ATRASADO, ADELANTADO, FINALIZADO
    }

    private record Datos(BigDecimal programadoDelPeriodo, BigDecimal programadoAnual, BigDecimal programadoAlPeriodo,
            BigDecimal ejecutadoAnterior, BigDecimal ejecutadoAlPeriodo, BigDecimal ejecutadoDelPeriodo,
            BigDecimal totalMetaEjecutada, EstadoAvanceMetas estado) {
    }
}
