package sv.gob.mh.siip.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.preinversion.api.PreinversinIdentificacinApi;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.ArchivoDescargado;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionService;

/** CU-PRE-04 (Identificación): delega 1:1 en {@link IdentificacionService}. */
@RestController
public class IdentificacionController implements PreinversinIdentificacinApi {

    private final IdentificacionService identificacionService;

    public IdentificacionController(IdentificacionService identificacionService) {
        this.identificacionService = identificacionService;
    }

    @Override
    public ResponseEntity<IdentificacionDto> obtenerIdentificacion(Long idProyecto) {
        return ResponseEntity.ok(identificacionService.obtener(idProyecto));
    }

    @Override
    public ResponseEntity<IdentificacionDto> guardarIdentificacion(Long idProyecto,
            IdentificacionRequestDto identificacionRequestDto) {
        return ResponseEntity.ok(identificacionService.guardar(idProyecto, identificacionRequestDto));
    }

    @Override
    public ResponseEntity<ArchivoAdjuntoResumenDto> cargarArbolProblemas(Long idProyecto, MultipartFile archivo) {
        return ResponseEntity.ok(identificacionService.cargarArbolProblemas(idProyecto, archivo));
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> descargarArbolProblemas(Long idProyecto) {
        return descargar(identificacionService.descargarArbolProblemas(idProyecto));
    }

    @Override
    public ResponseEntity<Void> eliminarArbolProblemas(Long idProyecto) {
        identificacionService.eliminarArbolProblemas(idProyecto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ArchivoAdjuntoResumenDto> cargarArbolObjetivos(Long idProyecto, MultipartFile archivo) {
        return ResponseEntity.ok(identificacionService.cargarArbolObjetivos(idProyecto, archivo));
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> descargarArbolObjetivos(Long idProyecto) {
        return descargar(identificacionService.descargarArbolObjetivos(idProyecto));
    }

    @Override
    public ResponseEntity<Void> eliminarArbolObjetivos(Long idProyecto) {
        identificacionService.eliminarArbolObjetivos(idProyecto);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<org.springframework.core.io.Resource> descargar(ArchivoDescargado archivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(archivo.nombreArchivo()).build().toString())
                .body(archivo.recurso());
    }
}
