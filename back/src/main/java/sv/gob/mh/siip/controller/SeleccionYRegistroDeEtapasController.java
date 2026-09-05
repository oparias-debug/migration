package sv.gob.mh.siip.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.model.preinversion.api.PreinversinSeleccinYRegistroDeEtapasApi;
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;

/** CU-PRE-3.5 (Selección y Registro de Etapas): delega 1:1 en {@link SeleccionYRegistroDeEtapasService}. */
@RestController
public class SeleccionYRegistroDeEtapasController implements PreinversinSeleccinYRegistroDeEtapasApi {

    private final SeleccionYRegistroDeEtapasService service;

    public SeleccionYRegistroDeEtapasController(SeleccionYRegistroDeEtapasService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<RutaPreinversionDto> obtenerRutaPreinversion(Long idProyecto) {
        return ResponseEntity.ok(service.obtenerRutaPreinversion(idProyecto));
    }

    @Override
    public ResponseEntity<RutaPreinversionSugeridaDto> generarRutaPreinversion(Long idProyecto,
            CriteriosCalificacionDto criteriosCalificacionDto) {
        return ResponseEntity.ok(service.generarRutaPreinversion(idProyecto, criteriosCalificacionDto));
    }

    @Override
    public ResponseEntity<RutaPreinversionDto> aceptarRutaPreinversion(Long idProyecto,
            CriteriosCalificacionDto criteriosCalificacionDto) {
        return ResponseEntity.ok(service.aceptarRutaPreinversion(idProyecto, criteriosCalificacionDto));
    }

    @Override
    public ResponseEntity<RutaPreinversionDto> modificarRutaPreinversion(Long idProyecto,
            ModificarRutaPreinversionRequestDto modificarRutaPreinversionRequestDto) {
        return ResponseEntity.ok(service.modificarRutaPreinversion(idProyecto, modificarRutaPreinversionRequestDto));
    }

    @Override
    public ResponseEntity<List<EtapaDto>> listarEtapas(Long idProyecto) {
        return ResponseEntity.ok(service.listarEtapas(idProyecto));
    }

    @Override
    public ResponseEntity<List<EtapaDto>> actualizarEtapas(Long idProyecto,
            ActualizarEtapasRequestDto actualizarEtapasRequestDto) {
        return ResponseEntity.ok(service.actualizarEtapas(idProyecto, actualizarEtapasRequestDto));
    }

    @Override
    public ResponseEntity<FichaInformacionGeneralDto> obtenerFichaInformacionGeneral(Long idProyecto) {
        return ResponseEntity.ok(service.obtenerFichaInformacionGeneral(idProyecto));
    }

    @Override
    public ResponseEntity<FichaInformacionGeneralDto> seleccionarCoEjecutor(Long idProyecto,
            SeleccionCoEjecutorRequestDto seleccionCoEjecutorRequestDto) {
        return ResponseEntity.ok(service.seleccionarCoEjecutor(idProyecto, seleccionCoEjecutorRequestDto));
    }

    @Override
    public ResponseEntity<FichaEmergenciaDto> obtenerFichaEmergencia(Long idProyecto) {
        return ResponseEntity.ok(service.obtenerFichaEmergencia(idProyecto));
    }

    @Override
    public ResponseEntity<FichaEmergenciaDto> registrarFichaEmergencia(Long idProyecto,
            FichaEmergenciaRequestDto fichaEmergenciaRequestDto) {
        return ResponseEntity.ok(service.registrarFichaEmergencia(idProyecto, fichaEmergenciaRequestDto));
    }
}
