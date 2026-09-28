package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": ciclo de revisión DGICP del avance
 * del PAP ({@link RevisionAvancePap}), la única aprobación unificada del avance financiero y de
 * metas físicas: observaciones DGICP, respuesta de la institución y finalización de la revisión.
 */
@Service
@Transactional
public class AvanceMetasFisicasPapRevisionServiceImpl implements AvanceMetasFisicasPapRevisionService {

    private final RevisionAvancePapRepository revisionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final ActorContexto actorContexto;

    public AvanceMetasFisicasPapRevisionServiceImpl(RevisionAvancePapRepository revisionRepository,
            UsuarioRepository usuarioRepository, NotificacionService notificacionService, ActorContexto actorContexto) {
        this.revisionRepository = revisionRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.actorContexto = actorContexto;
    }

    @Override
    public RevisionAvancePAPDto registrarObservacionesAvanceDgicp(RegistrarObservacionesAvanceDgicpRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        revision.setObservacionesDgicp(request.getObservacionesDgicp());
        return AvanceMetasFisicasPapRevisionMapper.construirRevisionDto(revisionRepository.save(revision), actor);
    }

    @Override
    public RevisionAvancePAPDto enviarObservacionesAvanceDgicp(EnviarObservacionesAvanceDgicpRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        revision.setFechaObservaciones(AvancePapSoporte.ahora());
        revision.setEstado(EstadoRevisionAvancePap.OBSERVADO);
        RevisionAvancePap guardada = revisionRepository.save(revision);

        notificacionService.notificarObservacionesAvance(request.getIdUnidadEjecutora(), request.getAnio(),
                periodo.name(), usuarioRepository.findByRolAndUnidadEjecutora_IdAndActivoTrue(
                        RolUsuario.TECNICO_URP,
                        request.getIdUnidadEjecutora()));
        return AvanceMetasFisicasPapRevisionMapper.construirRevisionDto(guardada, actor);
    }

    @Override
    public RevisionAvancePAPDto registrarRespuestaInstitucionAvance(
            RegistrarRespuestaInstitucionAvanceRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        revision.setRespuestaInstitucion(request.getRespuestaInstitucion());
        return AvanceMetasFisicasPapRevisionMapper.construirRevisionDto(revisionRepository.save(revision), actor);
    }

    @Override
    public RevisionAvancePAPDto enviarRespuestaInstitucionAvance(EnviarObservacionesAvanceDgicpRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Cuatrimestre periodo = Cuatrimestre.valueOf(request.getPeriodo().name());
        RevisionAvancePap revision = obtenerORevision(request.getIdUnidadEjecutora(), request.getAnio(), periodo);
        revision.setFechaRespuesta(AvancePapSoporte.ahora());
        RevisionAvancePap guardada = revisionRepository.save(revision);

        notificacionService.notificarRespuestaInstitucionAvance(request.getIdUnidadEjecutora(), request.getAnio(),
                periodo.name(), usuarioRepository.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE));
        return AvanceMetasFisicasPapRevisionMapper.construirRevisionDto(guardada, actor);
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
        return AvanceMetasFisicasPapRevisionMapper.construirRevisionDto(revisionRepository.save(revision), actor);
    }

    private RevisionAvancePap obtenerORevision(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo) {
        return revisionRepository.findByIdUnidadEjecutoraAndAnioAndPeriodo(idUnidadEjecutora, anio, periodo)
                .orElseGet(() -> RevisionAvancePap.builder()
                        .idUnidadEjecutora(idUnidadEjecutora)
                        .anio(anio)
                        .periodo(periodo)
                        .build());
    }
}
