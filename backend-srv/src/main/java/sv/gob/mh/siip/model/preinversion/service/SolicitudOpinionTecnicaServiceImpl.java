package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.dto.ActualizacionOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.AsignacionOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.AsignarOpinionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionesTecnicasProyectoResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDisponibleDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.TiposSolicitudOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.ApartadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaAcceso.AccesoProyecto;

/**
 * Implementación de {@link SolicitudOpinionTecnicaService}: {@link OpinionTecnicaAcceso} abre cada
 * operación, {@link OpinionTecnicaSolicitud} abre y asigna las gestiones y
 * {@link OpinionTecnicaEnsamblador} arma el listado.
 */
@Service
@Transactional
public class SolicitudOpinionTecnicaServiceImpl implements SolicitudOpinionTecnicaService {

    private final OpinionTecnicaAcceso acceso;
    private final OpinionTecnicaRepository opinionesTecnicas;
    private final OpinionTecnicaSolicitud solicitud;
    private final OpinionTecnicaEnsamblador ensamblador;

    public SolicitudOpinionTecnicaServiceImpl(OpinionTecnicaAcceso acceso,
            OpinionTecnicaRepository opinionesTecnicas,
            OpinionTecnicaSolicitud solicitud,
            OpinionTecnicaEnsamblador ensamblador) {
        this.acceso = acceso;
        this.opinionesTecnicas = opinionesTecnicas;
        this.solicitud = solicitud;
        this.ensamblador = ensamblador;
    }

    @Override
    @Transactional(readOnly = true)
    public TiposSolicitudOpinionTecnicaResponseDto consultarTiposSolicitud(Long idProyecto) {
        var proyecto = acceso.proyectoParaSolicitud(idProyecto).proyecto();
        return new TiposSolicitudOpinionTecnicaResponseDto(List.of(
                new TipoSolicitudDisponibleDto(TipoSolicitudOpinionTecnicaDto.OPINION_TECNICA,
                        solicitud.opinionTecnicaHabilitada(proyecto)),
                new TipoSolicitudDisponibleDto(TipoSolicitudOpinionTecnicaDto.ACTUALIZACION_OT,
                        solicitud.actualizacionHabilitada(proyecto))));
    }

    @Override
    @Transactional(readOnly = true)
    public OpinionesTecnicasProyectoResponseDto listar(Long idProyecto) {
        acceso.proyectoParaConsulta(idProyecto);
        return new OpinionesTecnicasProyectoResponseDto(opinionesTecnicas.findByProyectoIdOrderByIdDesc(idProyecto)
                .stream()
                .map(ensamblador::resumen)
                .toList());
    }

    @Override
    public SolicitudOpinionTecnicaResponseDto solicitar(Long idProyecto, MultipartFile notaSolicitudOt) {
        AccesoProyecto proyecto = acceso.proyectoParaSolicitud(idProyecto);
        OpinionTecnica gestion = solicitud.solicitar(proyecto.actor(), proyecto.proyecto(), notaSolicitudOt);
        return new SolicitudOpinionTecnicaResponseDto(gestion.getId(), idProyecto,
                TipoSolicitudOpinionTecnicaDto.OPINION_TECNICA, gestion.getFechaSolicitud().toLocalDate());
    }

    @Override
    public ActualizacionOpinionTecnicaResponseDto solicitarActualizacion(Long idProyecto) {
        AccesoProyecto proyecto = acceso.paraProyecto(idProyecto, RolUsuario.TECNICO_URP);
        OpinionTecnica gestion = solicitud.solicitarActualizacion(proyecto.actor(), proyecto.proyecto());
        boolean emergencia = Boolean.TRUE.equals(proyecto.proyecto().getEsProyectoEmergencia());
        return new ActualizacionOpinionTecnicaResponseDto(gestion.getId(),
                TipoSolicitudOpinionTecnicaDto.ACTUALIZACION_OT, ApartadoOpinionTecnica.casosDeUso(emergencia));
    }

    @Override
    public AsignacionOpinionTecnicaResponseDto asignar(Long idProyecto, Long idGestion,
            AsignarOpinionTecnicaRequestDto request) {
        OpinionTecnicaContexto contexto = acceso.paraGestion(idProyecto, idGestion, RolUsuario.COORDINADOR_PRE);
        OpinionTecnica gestion = solicitud.asignar(contexto, request.getTecnicoPreId());
        return new AsignacionOpinionTecnicaResponseDto(gestion.getId(), gestion.getTecnicoResponsable().getId(),
                gestion.getFechaAsignacion().toLocalDate());
    }
}
