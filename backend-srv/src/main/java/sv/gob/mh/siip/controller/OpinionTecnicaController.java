package sv.gob.mh.siip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import sv.gob.mh.siip.model.preinversion.api.OpinionTecnicaApi;
import sv.gob.mh.siip.model.preinversion.dto.ActualizacionOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.AsignacionOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.AsignarOpinionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentariosDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ConclusionesOpinionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EmisionOpinionTecnicaFavorableResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnvioComentariosDgicpResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.InformeOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionesInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionesTecnicasProyectoResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TiposSolicitudOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaService;
import sv.gob.mh.siip.model.preinversion.service.SolicitudOpinionTecnicaService;

/**
 * CU-PRE-26 (Opinión Técnica): implementa {@link OpinionTecnicaApi} delegando 1:1 en
 * {@link SolicitudOpinionTecnicaService} (apertura de gestiones) y {@link OpinionTecnicaService} (pantalla).
 */
@RestController
public class OpinionTecnicaController implements OpinionTecnicaApi {

    private final SolicitudOpinionTecnicaService solicitudService;
    private final OpinionTecnicaService opinionTecnicaService;

    public OpinionTecnicaController(SolicitudOpinionTecnicaService solicitudService,
            OpinionTecnicaService opinionTecnicaService) {
        this.solicitudService = solicitudService;
        this.opinionTecnicaService = opinionTecnicaService;
    }

    @Override
    public ResponseEntity<TiposSolicitudOpinionTecnicaResponseDto> consultarTiposSolicitudOpinionTecnica(
            Long proyectoId) {
        return ResponseEntity.ok(solicitudService.consultarTiposSolicitud(proyectoId));
    }

    @Override
    public ResponseEntity<OpinionesTecnicasProyectoResponseDto> listarOpinionesTecnicas(Long proyectoId) {
        return ResponseEntity.ok(solicitudService.listar(proyectoId));
    }

    @Override
    public ResponseEntity<SolicitudOpinionTecnicaResponseDto> solicitarOpinionTecnica(Long proyectoId,
            MultipartFile notaSolicitudOt) {
        SolicitudOpinionTecnicaResponseDto respuesta = solicitudService.solicitar(proyectoId, notaSolicitudOt);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{opinionTecnicaId}")
                .buildAndExpand(respuesta.getOpinionTecnicaId())
                .toUri()).body(respuesta);
    }

    @Override
    public ResponseEntity<ActualizacionOpinionTecnicaResponseDto> solicitarActualizacionOpinionTecnica(
            Long proyectoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.solicitarActualizacion(proyectoId));
    }

    @Override
    public ResponseEntity<OpinionTecnicaResponseDto> obtenerOpinionTecnica(Long proyectoId, Long opinionTecnicaId) {
        return ResponseEntity.ok(opinionTecnicaService.obtener(proyectoId, opinionTecnicaId));
    }

    @Override
    public ResponseEntity<AsignacionOpinionTecnicaResponseDto> asignarOpinionTecnica(Long proyectoId,
            Long opinionTecnicaId, AsignarOpinionTecnicaRequestDto asignarOpinionTecnicaRequestDto) {
        return ResponseEntity.ok(
                solicitudService.asignar(proyectoId, opinionTecnicaId, asignarOpinionTecnicaRequestDto));
    }

    @Override
    public ResponseEntity<OpinionTecnicaResponseDto> guardarComentariosDgicp(Long proyectoId, Long opinionTecnicaId,
            ComentariosDgicpRequestDto comentariosDgicpRequestDto) {
        return ResponseEntity.ok(
                opinionTecnicaService.guardarComentarios(proyectoId, opinionTecnicaId, comentariosDgicpRequestDto));
    }

    @Override
    public ResponseEntity<EnvioComentariosDgicpResponseDto> enviarComentariosDgicp(Long proyectoId,
            Long opinionTecnicaId, ComentariosDgicpRequestDto comentariosDgicpRequestDto) {
        return ResponseEntity.ok(
                opinionTecnicaService.enviarComentarios(proyectoId, opinionTecnicaId, comentariosDgicpRequestDto));
    }

    @Override
    public ResponseEntity<OpinionTecnicaResponseDto> guardarJustificacionesInstitucion(Long proyectoId,
            Long opinionTecnicaId, JustificacionesInstitucionRequestDto justificacionesInstitucionRequestDto) {
        return ResponseEntity.ok(opinionTecnicaService.guardarJustificaciones(proyectoId, opinionTecnicaId,
                justificacionesInstitucionRequestDto));
    }

    @Override
    public ResponseEntity<OpinionTecnicaResponseDto> guardarConclusionesOpinionTecnica(Long proyectoId,
            Long opinionTecnicaId, ConclusionesOpinionTecnicaRequestDto conclusionesOpinionTecnicaRequestDto) {
        return ResponseEntity.ok(opinionTecnicaService.guardarConclusiones(proyectoId, opinionTecnicaId,
                conclusionesOpinionTecnicaRequestDto));
    }

    @Override
    public ResponseEntity<OpinionTecnicaResponseDto> darVistoBuenoOpinionTecnica(Long proyectoId,
            Long opinionTecnicaId) {
        return ResponseEntity.ok(opinionTecnicaService.darVistoBueno(proyectoId, opinionTecnicaId));
    }

    @Override
    public ResponseEntity<EmisionOpinionTecnicaFavorableResponseDto> emitirOpinionTecnicaFavorable(Long proyectoId,
            Long opinionTecnicaId, MultipartFile notaOt, String numeroNotaOt) {
        return ResponseEntity.ok(opinionTecnicaService.emitirFavorable(proyectoId, opinionTecnicaId, notaOt,
                numeroNotaOt));
    }

    @Override
    public ResponseEntity<InformeOpinionTecnicaResponseDto> obtenerInformeOpinionTecnica(Long proyectoId,
            Long opinionTecnicaId) {
        return ResponseEntity.ok(opinionTecnicaService.obtenerInforme(proyectoId, opinionTecnicaId));
    }
}
