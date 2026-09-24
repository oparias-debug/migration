package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.HabilitacionModificacionMetasPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaMetasFisicasDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoPap;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionMetasPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionProgramacionPapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". Análogo de
 * {@link ProgramacionFinancieraPapServiceImpl} (CU-PRE-30): un {@code idEtapaMetaFisica}
 * ({@link EtapaMetaFisicaPap}) persiste a través de los años; el porcentaje programado por
 * cuatrimestre de cada año vive en {@link ProgCuatrimestralMetaFisica}. A diferencia de CU-PRE-30,
 * solo hay una meta física por etapa (no varias "fuentes"), y los valores son porcentajes (0-100),
 * no montos monetarios (RN-B.a). El ciclo de revisión/aprobación ({@link RevisionProgramacionPap})
 * es compartido con CU-PRE-30 (decisión funcional v1.2 de CU-PRE-31.openapi.yaml): vive
 * enteramente aquí, CU-PRE-30 no tiene endpoints propios para él.
 */
@Service
@Transactional
public class ProgramacionMetasFisicasPapServiceImpl implements ProgramacionMetasFisicasPapService {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private static final RolUsuario[] ROLES_CONSULTA = {
            RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE,
            RolUsuario.COORDINADOR_PROGRAMACION, RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI,
            RolUsuario.SUBJEFE_DGI, RolUsuario.TECNICO_SYMP, RolUsuario.COORDINADOR_SYMP
    };

    /** RN-C: actores internos de la DGICP, únicos que pueden ver "Comentarios al reporte DGICP". */
    private static final RolUsuario[] ROLES_DGICP_INTERNOS = {
            RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE, RolUsuario.COORDINADOR_PROGRAMACION,
            RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI,
            RolUsuario.TECNICO_SYMP, RolUsuario.COORDINADOR_SYMP
    };

    /** "Meta"/"Meta Total": siempre 1.00, tanto para estudios de arrastre como nuevos (Anexo A.4). */
    private static final double META_TOTAL = 1.0d;
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final String CAMPO_OBLIGATORIO = "*Campo obligatorio";

    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final HabilitacionModificacionMetasPapRepository habilitacionRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final RevisionProgramacionPapRepository revisionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final ActorContexto actorContexto;

    public ProgramacionMetasFisicasPapServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository,
            HabilitacionModificacionMetasPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository,
            RevisionProgramacionPapRepository revisionRepository, UsuarioRepository usuarioRepository,
            NotificacionService notificacionService, ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
        this.habilitacionRepository = habilitacionRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.revisionRepository = revisionRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramacionMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, Integer pagina,
            Integer tamanio) {
        Usuario actor = actorContexto.exigirRol(ROLES_CONSULTA);
        Long unidadFiltro = actor.getRol() == RolUsuario.TECNICO_URP
                ? actor.getUnidadEjecutora().getId()
                : idUnidadEjecutora;
        int anioEfectivo = anio != null ? anio : Year.now(ZONA_EL_SALVADOR).getValue();

        Pageable pageable = PageRequest.of(
                pagina != null && pagina >= 0 ? pagina : 0,
                tamanio != null && tamanio > 0 ? tamanio : 20);
        Page<EtapaMetaFisicaPap> resultado = etapaMetaRepository.buscar(unidadFiltro, pageable);

        String comentarios = esActorInternoDgicp(actor) ? comentariosReporteMetasFisicas(unidadFiltro, anioEfectivo) : null;
        List<EstudioFilaMetasFisicasDto> contenido = resultado.getContent().stream()
                .map(etapaMeta -> construirFilaListaDto(etapaMeta, anioEfectivo, comentarios))
                .toList();

        PaginacionMetadataDto paginacion = new PaginacionMetadataDto()
                .pagina(resultado.getNumber())
                .tamanio(resultado.getSize())
                .totalElementos(resultado.getTotalElements())
                .totalPaginas(resultado.getTotalPages());

        return new ProgramacionMetasFisicasPAPResponseDto(unidadFiltro, anioEfectivo, contenido, paginacion);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudioProgramacionMetasDto obtenerProgramacionMetasEstudio(String cup, Integer anio) {
        actorContexto.exigirRol(ROLES_CONSULTA);
        Proyecto proyecto = buscarEstudio(cup);
        return construirEstudioDto(proyecto, anio);
    }

    @Override
    public EstudioProgramacionMetasDto guardarProgramacionMetasEstudio(String cup, Integer anio,
            GuardarProgramacionMetasEstudioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarEstudio(cup);
        verificarPeriodoAbierto(proyecto.getUnidadEjecutora().getId(), anio);

        List<EtapaPreinversion> etapas = etapasOrdenadas(proyecto.getId());
        Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa = construirMapaPorEtapa(request.getEtapas());

        validarCamposAnexoA4(etapas, porEtapa, anio);
        validarPorcentajesContraCien(etapas, porEtapa, anio);
        persistirProgramacion(etapas, porEtapa, anio);

        return construirEstudioDto(proyecto, anio);
    }

    /**
     * Anexo B.1 (sección "Registro de la programación física por etapa de preinversión", Anexo
     * A.4) — validaciones de campo previas a RN-B.a, reportadas todas juntas como
     * {@code VALIDACION_NEGOCIO} con un {@code ErrorDetalle} por campo (CU-ADM-03):
     * <ul>
     * <li>"Entregable Unidad de Medida": campo obligatorio. En una etapa de arrastre el campo está
     * deshabilitado y se toma el ya registrado en ejercicios anteriores (SF-1 paso 2), por lo que
     * se valida el valor efectivo, no el enviado.</li>
     * <li>I/II/III Cuatrimestre: "valores entre 0.00% y 100%", cada uno por separado.</li>
     * <li>"Debe registrar en al menos un cuatrimestre": como el contrato define 0 por defecto en
     * los tres campos, se interpreta como al menos un cuatrimestre con porcentaje mayor que 0.</li>
     * </ul>
     */
    private void validarCamposAnexoA4(List<EtapaPreinversion> etapas,
            Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa, Integer anio) {
        List<ErrorDetalleDto> detalles = new ArrayList<>();
        for (EtapaPreinversion etapa : etapas) {
            EtapaMetaFisicaRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            String prefijo = etapa.getTipoEtapa().name() + ".";
            if (entregableEfectivo(etapa, item, anio) == null) {
                detalles.add(new ErrorDetalleDto().campo(prefijo + "entregable").mensaje(CAMPO_OBLIGATORIO));
            }
            validarRangoCuatrimestre(prefijo + "montoCuatrimestre1", item.getMontoCuatrimestre1(), detalles);
            validarRangoCuatrimestre(prefijo + "montoCuatrimestre2", item.getMontoCuatrimestre2(), detalles);
            validarRangoCuatrimestre(prefijo + "montoCuatrimestre3", item.getMontoCuatrimestre3(), detalles);
            boolean algunCuatrimestreRegistrado = bd(item.getMontoCuatrimestre1()).signum() > 0
                    || bd(item.getMontoCuatrimestre2()).signum() > 0
                    || bd(item.getMontoCuatrimestre3()).signum() > 0;
            if (!algunCuatrimestreRegistrado) {
                detalles.add(new ErrorDetalleDto().campo(prefijo + "programacionCuatrimestral")
                        .mensaje("Debe registrar en al menos un cuatrimestre."));
            }
        }
        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException(
                    "Existen campos obligatorios o con valores inválidos en la programación física por etapa.",
                    detalles);
        }
    }

    private static void validarRangoCuatrimestre(String campo, Double valor, List<ErrorDetalleDto> detalles) {
        BigDecimal porcentaje = bd(valor);
        if (porcentaje.signum() < 0 || porcentaje.compareTo(CIEN) > 0) {
            detalles.add(new ErrorDetalleDto().campo(campo).mensaje("El porcentaje debe estar entre 0.00% y 100%."));
        }
    }

    /** SF-1 paso 2: en una etapa de arrastre "Entregable" está deshabilitado y se conserva el ya registrado. */
    private Entregable entregableEfectivo(EtapaPreinversion etapa, EtapaMetaFisicaRequestDto item, Integer anio) {
        Optional<EtapaMetaFisicaPap> etapaMetaOpt = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId());
        if (etapaMetaOpt.isPresent() && esArrastreEtapa(etapaMetaOpt.get(), anio)) {
            return etapaMetaOpt.get().getEntregable();
        }
        EntregableDto entregable = item.getEntregable();
        return entregable != null ? Entregable.valueOf(entregable.name()) : null;
    }

    private Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> construirMapaPorEtapa(
            List<EtapaMetaFisicaRequestDto> etapas) {
        Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa = new EnumMap<>(TipoEtapaPreinversion.class);
        for (EtapaMetaFisicaRequestDto item : nullSafe(etapas)) {
            porEtapa.put(TipoEtapaPreinversion.valueOf(item.getEtapa().name()), item);
        }
        return porEtapa;
    }

    private void validarPorcentajesContraCien(List<EtapaPreinversion> etapas,
            Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa, Integer anio) {
        for (EtapaPreinversion etapa : etapas) {
            EtapaMetaFisicaRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            validarPorcentajeEtapa(etapa, item, anio);
        }
    }

    /**
     * RN-B.a: el literal aplicable (a.1 arrastre / a.2 nuevo) se decide POR ETAPA, no por
     * proyecto — un mismo proyecto puede combinar una etapa de arrastre con una etapa nueva
     * (p.ej. Perfil ya programado en años anteriores y Prefactibilidad agregada este año, caso
     * mostrado en el mockup del Anexo A.1), y cada una debe recibir su propio mensaje.
     */
    private void validarPorcentajeEtapa(EtapaPreinversion etapa, EtapaMetaFisicaRequestDto item, Integer anio) {
        BigDecimal total = suma(item);
        Optional<EtapaMetaFisicaPap> etapaMetaOpt = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId());
        BigDecimal ejecutadoAnterior = etapaMetaOpt
                .map(etapaMeta -> ejecutadoAniosAnteriores(etapaMeta.getId(), anio))
                .orElse(BigDecimal.ZERO);
        BigDecimal limite = CIEN.subtract(ejecutadoAnterior);
        if (total.compareTo(limite) <= 0) {
            return;
        }
        boolean esArrastreEtapa = etapaMetaOpt.map(etapaMeta -> esArrastreEtapa(etapaMeta, anio)).orElse(false);
        if (esArrastreEtapa) {
            throw new ValidacionNegocioException("PORCENTAJE_SUPERA_100_ARRASTRE",
                    "Porcentaje Programado supera el 100% de la etapa.", null);
        }
        throw new ValidacionNegocioException("MONTO_SUPERA_100_NUEVO", "Monto Programado supera el 100%.", null);
    }

    private void persistirProgramacion(List<EtapaPreinversion> etapas,
            Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa, Integer anio) {
        for (EtapaPreinversion etapa : etapas) {
            EtapaMetaFisicaRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            guardarFila(etapa, item, anio);
        }
    }

    private void guardarFila(EtapaPreinversion etapa, EtapaMetaFisicaRequestDto item, Integer anio) {
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId())
                .orElseGet(() -> EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).build());
        // SF-1 paso 2: en una etapa de arrastre "Entregable" está deshabilitado; se ignora el enviado.
        etapaMeta.setEntregable(entregableEfectivo(etapa, item, anio));
        // SF-4/SF-5: si la meta había sido desactivada desde CU-PRE-30, volver a registrar su
        // programación (SF-8/SF-9) la reactiva, reutilizando el mismo registro y su histórico.
        etapaMeta.setActivo(Boolean.TRUE);
        EtapaMetaFisicaPap etapaMetaGuardada = etapaMetaRepository.save(etapaMeta);

        ProgCuatrimestralMetaFisica prog = progRepository
                .findByEtapaMetaFisicaIdAndAnio(etapaMetaGuardada.getId(), anio)
                .orElseGet(() -> ProgCuatrimestralMetaFisica.builder().etapaMetaFisica(etapaMetaGuardada).anio(anio).build());
        prog.setMontoCuatrimestre1(bd(item.getMontoCuatrimestre1()));
        prog.setMontoCuatrimestre2(bd(item.getMontoCuatrimestre2()));
        prog.setMontoCuatrimestre3(bd(item.getMontoCuatrimestre3()));
        progRepository.save(prog);
    }

    @Override
    public RevisionProgramacionPAPDto enviarProgramacionARevisionDgicp(EnviarProgramacionARevisionDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());

        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setEstadoPap(EstadoPap.ENVIADO_A_REVISION_DGICP);
        RevisionProgramacionPap guardada = revisionRepository.save(revision);

        List<Usuario> tecnicosPre = usuarioRepository.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE);
        notificacionService.notificarProgramacionEnviadaARevision(request.getIdUnidadEjecutora(), request.getAnio(),
                tecnicosPre);
        return construirRevisionDto(guardada);
    }

    @Override
    public RevisionProgramacionPAPDto registrarObservacionesDgicp(RegistrarObservacionesDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setObservacionesDgicp(request.getObservacionesDgicp());
        return construirRevisionDto(revisionRepository.save(revision));
    }

    @Override
    public RevisionProgramacionPAPDto enviarObservacionesDgicp(EnviarProgramacionARevisionDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setFechaObservaciones(LocalDateTime.now(ZONA_EL_SALVADOR));
        revision.setEstadoPap(EstadoPap.OBSERVADO);
        RevisionProgramacionPap guardada = revisionRepository.save(revision);

        List<Usuario> tecnicosUrp = usuarioRepository.findByRolAndUnidadEjecutora_IdAndActivoTrue(RolUsuario.TECNICO_URP,
                request.getIdUnidadEjecutora());
        notificacionService.notificarObservacionesDgicp(request.getIdUnidadEjecutora(), request.getAnio(), tecnicosUrp);
        return construirRevisionDto(guardada);
    }

    @Override
    public RevisionProgramacionPAPDto registrarRespuestaInstitucion(RegistrarRespuestaInstitucionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        // RN-A.b: fuera del Calendario de Eventos del PAP el Técnico URP no puede ingresar ni
        // ajustar datos y "todas las acciones" quedan deshabilitadas — incluye "Respuesta
        // Institución" (SF-3 paso 3), salvo habilitación de modificaciones fuera de plazo (SF-8/SF-9).
        verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setRespuestaInstitucion(request.getRespuestaInstitucion());
        return construirRevisionDto(revisionRepository.save(revision));
    }

    @Override
    public RevisionProgramacionPAPDto enviarRespuestaInstitucion(EnviarProgramacionARevisionDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        // RN-A.b: mismo bloqueo por calendario que registrarRespuestaInstitucion (SF-3 pasos 3-4).
        verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setFechaRespuesta(LocalDateTime.now(ZONA_EL_SALVADOR));
        revision.setEstadoPap(EstadoPap.ENVIADO_A_REVISION_DGICP);
        RevisionProgramacionPap guardada = revisionRepository.save(revision);

        List<Usuario> tecnicosPre = usuarioRepository.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE);
        notificacionService.notificarRespuestaInstitucion(request.getIdUnidadEjecutora(), request.getAnio(), tecnicosPre);
        return construirRevisionDto(guardada);
    }

    @Override
    public RevisionProgramacionPAPDto finalizarRevision(FinalizarRevisionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        // RN-E: "REVISIÓN FINALIZADA" solo está habilitado "durante el período de ingreso de
        // información o cuando se presenten modificaciones al PAP" (habilitación SF-8/SF-9).
        verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        if (request.getComentariosReporteFinancieroDgicp() != null) {
            revision.setComentariosReporteFinancieroDgicp(request.getComentariosReporteFinancieroDgicp());
        }
        if (request.getComentariosReporteMetasFisicasDgicp() != null) {
            revision.setComentariosReporteMetasFisicasDgicp(request.getComentariosReporteMetasFisicasDgicp());
        }
        // RN-D: actualiza el estado a "PAP Revisado" en el Monitoreo PAP de CU-PRO-25 (efecto
        // secundario documentado, sin endpoint propio implementado todavía en ese CU).
        revision.setEstadoPap(EstadoPap.PAP_REVISADO);
        return construirRevisionDto(revisionRepository.save(revision));
    }

    @Override
    @Transactional(readOnly = true)
    public Resource generarReporteMetasFisicas(Long idUnidadEjecutora, Integer anio, String formato) {
        Usuario actor = actorContexto.exigirRol(ROLES_CONSULTA);
        List<EtapaMetaFisicaPap> etapasMeta = etapaMetaRepository
                .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(
                        idUnidadEjecutora);
        String comentarios = esActorInternoDgicp(actor) ? comentariosReporteMetasFisicas(idUnidadEjecutora, anio) : null;
        List<EstudioFilaMetasFisicasDto> filas = etapasMeta.stream()
                .map(etapaMeta -> construirFilaListaDto(etapaMeta, anio, comentarios))
                .toList();

        byte[] contenido = "PDF".equalsIgnoreCase(formato)
                ? ReporteProgramacionMetasFisicasPapGenerator.generarPdf(idUnidadEjecutora, anio, filas)
                : ReporteProgramacionMetasFisicasPapGenerator.generarExcel(idUnidadEjecutora, anio, filas);
        return new ByteArrayResource(contenido);
    }

    @Override
    public void habilitarModificacionesMetasFueraPlazo(EnviarProgramacionARevisionDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        HabilitacionModificacionMetasPap habilitacion = habilitacionRepository
                .findByIdUnidadEjecutoraAndAnio(request.getIdUnidadEjecutora(), request.getAnio())
                .orElseGet(() -> HabilitacionModificacionMetasPap.builder()
                        .idUnidadEjecutora(request.getIdUnidadEjecutora())
                        .anio(request.getAnio())
                        .build());
        habilitacion.setFechaHabilitacion(LocalDateTime.now(ZONA_EL_SALVADOR));
        habilitacionRepository.save(habilitacion);
    }

    // -----------------------------------------------------------------------------------------

    private static boolean esActorInternoDgicp(Usuario actor) {
        for (RolUsuario rol : ROLES_DGICP_INTERNOS) {
            if (rol == actor.getRol()) {
                return true;
            }
        }
        return false;
    }

    private String comentariosReporteMetasFisicas(Long idUnidadEjecutora, Integer anio) {
        return revisionRepository.findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio)
                .map(RevisionProgramacionPap::getComentariosReporteMetasFisicasDgicp)
                .orElse(null);
    }

    private RevisionProgramacionPap obtenerORevision(Long idUnidadEjecutora, Integer anio) {
        return revisionRepository.findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio)
                .orElseGet(() -> RevisionProgramacionPap.builder()
                        .idUnidadEjecutora(idUnidadEjecutora)
                        .anio(anio)
                        .build());
    }

    private RevisionProgramacionPAPDto construirRevisionDto(RevisionProgramacionPap revision) {
        return new RevisionProgramacionPAPDto(revision.getIdUnidadEjecutora(), revision.getAnio(),
                EstadoPAPDto.valueOf(revision.getEstadoPap().name()))
                .observacionesDgicp(revision.getObservacionesDgicp())
                .fechaObservaciones(aOffsetDateTime(revision.getFechaObservaciones()))
                .respuestaInstitucion(revision.getRespuestaInstitucion())
                .fechaRespuesta(aOffsetDateTime(revision.getFechaRespuesta()))
                .comentariosReporteFinancieroDgicp(revision.getComentariosReporteFinancieroDgicp())
                .comentariosReporteMetasFisicasDgicp(revision.getComentariosReporteMetasFisicasDgicp());
    }

    private static OffsetDateTime aOffsetDateTime(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(ZONA_EL_SALVADOR).toOffsetDateTime();
    }

    private Proyecto buscarEstudio(String cup) {
        Proyecto proyecto = proyectoRepository.findByCup(cup)
                .orElseThrow(() -> new RecursoNoEncontradoException("El estudio (CUP + año) no existe."));
        if (etapaPreinversionRepository.findByProyectoId(proyecto.getId()).isEmpty()) {
            throw new RecursoNoEncontradoException("El estudio (CUP + año) no existe.");
        }
        return proyecto;
    }

    private List<EtapaPreinversion> etapasOrdenadas(Long idProyecto) {
        return etapaPreinversionRepository.findByProyectoId(idProyecto).stream()
                .sorted((a, b) -> Integer.compare(a.getTipoEtapa().ordinal(), b.getTipoEtapa().ordinal()))
                .toList();
    }

    /**
     * Resumen a nivel de estudio para {@code EstudioProgramacionMetas.esArrastre}: {@code true} si
     * al menos una de sus etapas es de arrastre. La validación de RN-B.a NO usa este valor, sino
     * {@link #esArrastreEtapa} (un proyecto puede mezclar etapas de arrastre y nuevas).
     */
    private boolean esArrastre(Long idProyecto, Integer anio) {
        List<EtapaMetaFisicaPap> etapasMeta = etapaMetaRepository.findByEtapaPreinversionProyectoId(idProyecto);
        if (etapasMeta.isEmpty()) {
            return false;
        }
        List<Long> idsEtapaMetaFisica = etapasMeta.stream().map(EtapaMetaFisicaPap::getId).toList();
        return progRepository.findByEtapaMetaFisicaIdIn(idsEtapaMetaFisica).stream()
                .anyMatch(prog -> prog.getAnio() < anio);
    }

    /** RN-B.a: una etapa es "de arrastre" si su meta física ya tuvo programación en un año anterior al consultado. */
    private boolean esArrastreEtapa(EtapaMetaFisicaPap etapaMeta, Integer anio) {
        if (etapaMeta.getId() == null) {
            return false;
        }
        return progRepository.findByEtapaMetaFisicaId(etapaMeta.getId()).stream()
                .anyMatch(prog -> prog.getAnio() < anio);
    }

    /** RN-B.a: "Ejecutado años anteriores" = suma histórica de todos los años previos al consultado. */
    private BigDecimal ejecutadoAniosAnteriores(Long idEtapaMetaFisica, Integer anio) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgCuatrimestralMetaFisica prog : progRepository.findByEtapaMetaFisicaId(idEtapaMetaFisica)) {
            if (prog.getAnio() < anio) {
                total = total.add(prog.totalProgramadoAnio());
            }
        }
        return total;
    }

    private EstudioProgramacionMetasDto construirEstudioDto(Proyecto proyecto, Integer anio) {
        boolean esArrastreEstudio = esArrastre(proyecto.getId(), anio);
        List<EtapaMetaFisicaDto> etapas = etapasOrdenadas(proyecto.getId()).stream()
                .map(etapa -> construirEtapaDto(etapa, anio))
                .toList();
        return new EstudioProgramacionMetasDto(proyecto.getCup(), proyecto.getNombre(), esArrastreEstudio, etapas);
    }

    private EtapaMetaFisicaDto construirEtapaDto(EtapaPreinversion etapa, Integer anio) {
        Optional<EtapaMetaFisicaPap> etapaMetaOpt = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId());
        BigDecimal ejecutadoAnterior = etapaMetaOpt
                .map(etapaMeta -> ejecutadoAniosAnteriores(etapaMeta.getId(), anio))
                .orElse(BigDecimal.ZERO);
        ProgCuatrimestralMetaFisica actual = etapaMetaOpt
                .flatMap(etapaMeta -> progRepository.findByEtapaMetaFisicaIdAndAnio(etapaMeta.getId(), anio))
                .orElse(null);
        BigDecimal m1 = actual != null ? actual.getMontoCuatrimestre1() : BigDecimal.ZERO;
        BigDecimal m2 = actual != null ? actual.getMontoCuatrimestre2() : BigDecimal.ZERO;
        BigDecimal m3 = actual != null ? actual.getMontoCuatrimestre3() : BigDecimal.ZERO;
        BigDecimal total = m1.add(m2).add(m3);
        BigDecimal aniosPosteriores = CIEN.subtract(ejecutadoAnterior).subtract(total);
        Entregable entregable = etapaMetaOpt.map(EtapaMetaFisicaPap::getEntregable).orElse(null);
        boolean esArrastreEtapa = etapaMetaOpt.map(etapaMeta -> esArrastreEtapa(etapaMeta, anio)).orElse(false);

        return new EtapaMetaFisicaDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()),
                m1.doubleValue(), m2.doubleValue(), m3.doubleValue(), total.doubleValue())
                .esArrastre(esArrastreEtapa)
                .meta(META_TOTAL)
                .entregable(entregable != null ? EntregableDto.valueOf(entregable.name()) : null)
                .ejecutadoAniosAnteriores(
                        ejecutadoAnterior.compareTo(BigDecimal.ZERO) > 0 ? ejecutadoAnterior.doubleValue() : null)
                .aniosPosteriores(aniosPosteriores.compareTo(BigDecimal.ZERO) > 0 ? aniosPosteriores.doubleValue() : null);
    }

    private EstudioFilaMetasFisicasDto construirFilaListaDto(EtapaMetaFisicaPap etapaMeta, Integer anio,
            String comentariosReporteDgicp) {
        EtapaPreinversion etapa = etapaMeta.getEtapaPreinversion();
        Proyecto proyecto = etapa.getProyecto();
        BigDecimal ejecutadoAnterior = ejecutadoAniosAnteriores(etapaMeta.getId(), anio);
        ProgCuatrimestralMetaFisica actual = progRepository.findByEtapaMetaFisicaIdAndAnio(etapaMeta.getId(), anio)
                .orElse(null);
        BigDecimal total = actual != null ? actual.totalProgramadoAnio() : BigDecimal.ZERO;
        BigDecimal aniosPosteriores = CIEN.subtract(ejecutadoAnterior).subtract(total);
        Entregable entregable = etapaMeta.getEntregable();

        return new EstudioFilaMetasFisicasDto(proyecto.getCup(), proyecto.getNombre(),
                NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()))
                .metaTotal(META_TOTAL)
                .entregable(entregable != null ? EntregableDto.valueOf(entregable.name()) : null)
                .ejecutadoAniosAnteriores(
                        ejecutadoAnterior.compareTo(BigDecimal.ZERO) > 0 ? ejecutadoAnterior.doubleValue() : null)
                .totalAnio(total.doubleValue())
                .aniosPosteriores(aniosPosteriores.compareTo(BigDecimal.ZERO) > 0 ? aniosPosteriores.doubleValue() : null)
                .comentariosReporteDgicp(comentariosReporteDgicp);
    }

    private static BigDecimal suma(EtapaMetaFisicaRequestDto item) {
        return bd(item.getMontoCuatrimestre1()).add(bd(item.getMontoCuatrimestre2())).add(bd(item.getMontoCuatrimestre3()));
    }

    private static BigDecimal bd(Double valor) {
        return valor != null ? BigDecimal.valueOf(valor) : BigDecimal.ZERO;
    }

    private static <T> List<T> nullSafe(List<T> lista) {
        return lista != null ? lista : List.of();
    }

    private void verificarPeriodoAbierto(Long idUnidadEjecutora, Integer anio) {
        boolean habilitado = habilitacionRepository.findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio).isPresent();
        if (habilitado) {
            return;
        }
        boolean abierto = calendarioEventoRepository
                .findByTipoEventoAndAnioAndCuatrimestreIsNull(TipoEventoCalendario.PROGRAMACION_PAP, anio)
                .map(evento -> evento.getEstado() == EstadoCalendarioEvento.ABIERTO)
                .orElse(true);
        if (!abierto) {
            throw new ConflictoEstadoException("PERIODO_CERRADO", "Periodo de ingreso de información ha finalizado.");
        }
    }
}
