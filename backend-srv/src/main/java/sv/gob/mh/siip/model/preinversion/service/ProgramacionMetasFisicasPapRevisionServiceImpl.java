package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoPap;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionMetasPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionProgramacionPapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": ciclo de revisión
 * DGICP de la programación del PAP ({@link RevisionProgramacionPap}), compartido con CU-PRE-30
 * (decisión funcional v1.2 de CU-PRE-31.openapi.yaml): envío a revisión, observaciones DGICP,
 * respuesta de la institución y finalización de la revisión.
 */
@Service
@Transactional
public class ProgramacionMetasFisicasPapRevisionServiceImpl implements ProgramacionMetasFisicasPapRevisionService {

    private final RevisionProgramacionPapRepository revisionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final ActorContexto actorContexto;
    private final ProgramacionMetasFisicasPapPlazo plazo;

    public ProgramacionMetasFisicasPapRevisionServiceImpl(RevisionProgramacionPapRepository revisionRepository,
            UsuarioRepository usuarioRepository, NotificacionService notificacionService,
            HabilitacionModificacionMetasPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository, ActorContexto actorContexto) {
        this.revisionRepository = revisionRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.actorContexto = actorContexto;
        this.plazo = new ProgramacionMetasFisicasPapPlazo(habilitacionRepository, calendarioEventoRepository);
    }

    @Override
    public RevisionProgramacionPAPDto enviarProgramacionARevisionDgicp(
            EnviarProgramacionARevisionDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        plazo.verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());

        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setEstadoPap(EstadoPap.ENVIADO_A_REVISION_DGICP);
        RevisionProgramacionPap guardada = revisionRepository.save(revision);

        notificacionService.notificarProgramacionEnviadaARevision(request.getIdUnidadEjecutora(), request.getAnio(),
                usuarioRepository.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE));
        return ProgramacionMetasFisicasPapRevisionMapper.construirRevisionDto(guardada);
    }

    @Override
    public RevisionProgramacionPAPDto registrarObservacionesDgicp(RegistrarObservacionesDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setObservacionesDgicp(request.getObservacionesDgicp());
        return ProgramacionMetasFisicasPapRevisionMapper.construirRevisionDto(revisionRepository.save(revision));
    }

    @Override
    public RevisionProgramacionPAPDto enviarObservacionesDgicp(EnviarProgramacionARevisionDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setFechaObservaciones(ProgramacionPapSoporte.ahora());
        revision.setEstadoPap(EstadoPap.OBSERVADO);
        RevisionProgramacionPap guardada = revisionRepository.save(revision);

        notificacionService.notificarObservacionesDgicp(request.getIdUnidadEjecutora(), request.getAnio(),
                usuarioRepository.findByRolAndUnidadEjecutora_IdAndActivoTrue(RolUsuario.TECNICO_URP,
                        request.getIdUnidadEjecutora()));
        return ProgramacionMetasFisicasPapRevisionMapper.construirRevisionDto(guardada);
    }

    @Override
    public RevisionProgramacionPAPDto registrarRespuestaInstitucion(RegistrarRespuestaInstitucionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        // RN-A.b: fuera del Calendario de Eventos del PAP el Técnico URP no puede ingresar ni
        // ajustar datos y "todas las acciones" quedan deshabilitadas — incluye "Respuesta
        // Institución" (SF-3 paso 3), salvo habilitación de modificaciones fuera de plazo (SF-8/SF-9).
        plazo.verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setRespuestaInstitucion(request.getRespuestaInstitucion());
        return ProgramacionMetasFisicasPapRevisionMapper.construirRevisionDto(revisionRepository.save(revision));
    }

    @Override
    public RevisionProgramacionPAPDto enviarRespuestaInstitucion(EnviarProgramacionARevisionDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        // RN-A.b: mismo bloqueo por calendario que registrarRespuestaInstitucion (SF-3 pasos 3-4).
        plazo.verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());
        RevisionProgramacionPap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio());
        revision.setFechaRespuesta(ProgramacionPapSoporte.ahora());
        revision.setEstadoPap(EstadoPap.ENVIADO_A_REVISION_DGICP);
        RevisionProgramacionPap guardada = revisionRepository.save(revision);

        notificacionService.notificarRespuestaInstitucion(request.getIdUnidadEjecutora(), request.getAnio(),
                usuarioRepository.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE));
        return ProgramacionMetasFisicasPapRevisionMapper.construirRevisionDto(guardada);
    }

    @Override
    public RevisionProgramacionPAPDto finalizarRevision(FinalizarRevisionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        // RN-E: "REVISIÓN FINALIZADA" solo está habilitado "durante el período de ingreso de
        // información o cuando se presenten modificaciones al PAP" (habilitación SF-8/SF-9).
        plazo.verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());
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
        return ProgramacionMetasFisicasPapRevisionMapper.construirRevisionDto(revisionRepository.save(revision));
    }

    private RevisionProgramacionPap obtenerORevision(Long idUnidadEjecutora, Integer anio) {
        return revisionRepository.findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio)
                .orElseGet(() -> RevisionProgramacionPap.builder()
                        .idUnidadEjecutora(idUnidadEjecutora)
                        .anio(anio)
                        .build());
    }
}
