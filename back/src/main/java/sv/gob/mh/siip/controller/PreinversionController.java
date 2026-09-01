package sv.gob.mh.siip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinRegistroYSolicitudDeCupApi;
import sv.gob.mh.siip.model.preinversion.dto.CambioUnidadEjecutoraRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoListResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;

/** CU-PRE-01 (Registro y Solicitud de CUP): delega 1:1 en {@link ProyectoService}. */
@RestController
public class PreinversionController implements PreinversinRegistroYSolicitudDeCupApi {

    private final ProyectoService proyectoService;

    public PreinversionController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @Override
    public ResponseEntity<ProyectoDto> registrarProyecto(ProyectoRequestDto proyectoRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.registrar(proyectoRequestDto));
    }

    @Override
    public ResponseEntity<ProyectoListResponseDto> listarProyectos(Integer pagina, Integer tamanio,
            EstadoProyectoDto estado) {
        return ResponseEntity.ok(proyectoService.listar(pagina, tamanio, estado));
    }

    @Override
    public ResponseEntity<ProyectoDto> obtenerProyecto(Long idProyecto) {
        return ResponseEntity.ok(proyectoService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<ProyectoDto> actualizarProyecto(Long idProyecto, ProyectoRequestDto proyectoRequestDto) {
        return ResponseEntity.ok(proyectoService.actualizar(idProyecto, proyectoRequestDto));
    }

    @Override
    public ResponseEntity<ProyectoDto> solicitarCup(Long idProyecto) {
        return ResponseEntity.ok(proyectoService.solicitarCup(idProyecto));
    }

    @Override
    public ResponseEntity<ProyectoDto> responderObservacionCup(Long idProyecto,
            RespuestaObservacionRequestDto respuestaObservacionRequestDto) {
        return ResponseEntity.ok(proyectoService.responderObservacionCup(idProyecto, respuestaObservacionRequestDto));
    }

    @Override
    public ResponseEntity<ProyectoDto> cambiarUnidadEjecutoraProyecto(Long idProyecto,
            CambioUnidadEjecutoraRequestDto cambioUnidadEjecutoraRequestDto) {
        return ResponseEntity.ok(proyectoService.cambiarUnidadEjecutora(idProyecto, cambioUnidadEjecutoraRequestDto));
    }

    @Override
    public ResponseEntity<Void> eliminarProyecto(Long idProyecto) {
        proyectoService.eliminar(idProyecto);
        return ResponseEntity.noContent().build();
    }

}
