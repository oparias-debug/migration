package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.ArchivoDescargado;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionService;

class IdentificacionControllerTest {

    private static final Long ID_PROYECTO = 7L;

    private IdentificacionService identificacionService;
    private IdentificacionController controller;

    @BeforeEach
    void setUp() {
        identificacionService = mock(IdentificacionService.class);
        controller = new IdentificacionController(identificacionService);
    }

    @Test
    void obtenerIdentificacion_delegaYDevuelve200() {
        IdentificacionDto esperado = new IdentificacionDto();
        when(identificacionService.obtener(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<IdentificacionDto> respuesta = controller.obtenerIdentificacion(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarIdentificacion_delegaYDevuelve200() {
        IdentificacionRequestDto request = new IdentificacionRequestDto();
        IdentificacionDto esperado = new IdentificacionDto();
        when(identificacionService.guardar(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<IdentificacionDto> respuesta = controller.guardarIdentificacion(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void cargarArbolProblemas_delegaYDevuelve200() {
        MultipartFile archivo = pdf("problemas.pdf");
        ArchivoAdjuntoResumenDto esperado = new ArchivoAdjuntoResumenDto();
        when(identificacionService.cargarArbolProblemas(ID_PROYECTO, archivo)).thenReturn(esperado);

        ResponseEntity<ArchivoAdjuntoResumenDto> respuesta = controller.cargarArbolProblemas(ID_PROYECTO, archivo);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void descargarArbolProblemas_devuelvePdfComoAdjuntoConElNombreOriginal() {
        Resource recurso = new ByteArrayResource(new byte[] { 1, 2 });
        when(identificacionService.descargarArbolProblemas(ID_PROYECTO))
                .thenReturn(new ArchivoDescargado(recurso, "problemas.pdf"));

        ResponseEntity<Resource> respuesta = controller.descargarArbolProblemas(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(respuesta.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"problemas.pdf\"");
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }

    @Test
    void eliminarArbolProblemas_delegaYDevuelve204() {
        ResponseEntity<Void> respuesta = controller.eliminarArbolProblemas(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(identificacionService).eliminarArbolProblemas(ID_PROYECTO);
    }

    @Test
    void cargarArbolObjetivos_delegaYDevuelve200() {
        MultipartFile archivo = pdf("objetivos.pdf");
        ArchivoAdjuntoResumenDto esperado = new ArchivoAdjuntoResumenDto();
        when(identificacionService.cargarArbolObjetivos(ID_PROYECTO, archivo)).thenReturn(esperado);

        ResponseEntity<ArchivoAdjuntoResumenDto> respuesta = controller.cargarArbolObjetivos(ID_PROYECTO, archivo);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void descargarArbolObjetivos_devuelvePdfComoAdjuntoConElNombreOriginal() {
        Resource recurso = new ByteArrayResource(new byte[] { 3 });
        when(identificacionService.descargarArbolObjetivos(ID_PROYECTO))
                .thenReturn(new ArchivoDescargado(recurso, "objetivos.pdf"));

        ResponseEntity<Resource> respuesta = controller.descargarArbolObjetivos(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(respuesta.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"objetivos.pdf\"");
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }

    @Test
    void eliminarArbolObjetivos_delegaYDevuelve204() {
        ResponseEntity<Void> respuesta = controller.eliminarArbolObjetivos(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(identificacionService).eliminarArbolObjetivos(ID_PROYECTO);
    }

    private static MultipartFile pdf(String nombre) {
        return new MockMultipartFile("archivo", nombre, MediaType.APPLICATION_PDF_VALUE, new byte[] { 1 });
    }
}
