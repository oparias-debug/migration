package sv.gob.mh.siip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinRevisinYEmisinDeCupApi;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;

/** CU-PRE-01.5 (Revisión y Emisión de CUP): delega 1:1 en {@link ProyectoService}. */
@RestController
public class RevisionYEmisionCupController implements PreinversinRevisinYEmisinDeCupApi {

    private final ProyectoService proyectoService;

    public RevisionYEmisionCupController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @Override
    public ResponseEntity<ProyectoDto> devolverSolicitudCup(Long idProyecto,
            DevolucionSolicitudRequestDto devolucionSolicitudRequestDto) {
        return ResponseEntity.ok(proyectoService.devolverSolicitudCup(idProyecto, devolucionSolicitudRequestDto));
    }

    @Override
    public ResponseEntity<ProyectoDto> emitirCup(Long idProyecto) {
        return ResponseEntity.ok(proyectoService.emitirCup(idProyecto));
    }
}
