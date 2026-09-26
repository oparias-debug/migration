package sv.gob.mh.siip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.preinversion.api.TecnicoUrpApi;
import sv.gob.mh.siip.model.preinversion.api.ViabilizadorApi;
import sv.gob.mh.siip.model.preinversion.dto.CargarDocumentoViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadService;

/**
 * CU-PRE-24 (Viabilidad): implementa las operaciones del Técnico URP ({@link TecnicoUrpApi}) y del
 * Viabilizador ({@link ViabilizadorApi}) delegando 1:1 en {@link ViabilidadService}.
 */
@RestController
public class ViabilidadController implements TecnicoUrpApi, ViabilizadorApi {

    private final ViabilidadService viabilidadService;

    public ViabilidadController(ViabilidadService viabilidadService) {
        this.viabilidadService = viabilidadService;
    }

    @Override
    public ResponseEntity<FichaViabilidadResponseDto> consultarFichaViabilidad(Long proyectoId) {
        return ResponseEntity.ok(viabilidadService.consultarFicha(proyectoId));
    }

    @Override
    public ResponseEntity<CargarDocumentoViabilidadResponseDto> cargarDocumentoViabilidad(Long proyectoId,
            TipoDocumentoViabilidadDto tipoDocumento, MultipartFile archivo) {
        TipoDocumentoViabilidad tipo = tipoDocumento == null ? null : TipoDocumentoViabilidad.valueOf(tipoDocumento.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(viabilidadService.cargarDocumento(proyectoId, tipo, archivo));
    }

    @Override
    public ResponseEntity<GuardarComentariosViabilidadResponseDto> guardarComentariosViabilidad(Long proyectoId,
            GuardarComentariosViabilidadRequestDto guardarComentariosViabilidadRequestDto) {
        return ResponseEntity.ok(viabilidadService.guardarComentarios(proyectoId, guardarComentariosViabilidadRequestDto));
    }

    @Override
    public ResponseEntity<Void> solicitarViabilidad(Long proyectoId) {
        viabilidadService.solicitarViabilidad(proyectoId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EnviarComentariosViabilidadResponseDto> enviarComentariosViabilidad(Long proyectoId) {
        return ResponseEntity.ok(viabilidadService.enviarComentarios(proyectoId));
    }

    @Override
    public ResponseEntity<EmitirViabilidadResponseDto> emitirViabilidad(Long proyectoId) {
        return ResponseEntity.ok(viabilidadService.emitirViabilidad(proyectoId));
    }
}
