package sv.gob.mh.siip.config;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDto;

class ManejadorErroresGlobalTest {

    private ManejadorErroresGlobal manejadorErroresGlobal;

    @BeforeEach
    void setUp() {
        manejadorErroresGlobal = new ManejadorErroresGlobal();
    }

    @Test
    @DisplayName("Debería manejar RecursoNoEncontradoException y retornar status 404")
    void testManejarNoEncontrado() {
        String mensajeEsperado = "El recurso solicitado no fue encontrado.";
        RecursoNoEncontradoException exception = new RecursoNoEncontradoException(mensajeEsperado);

        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal.manejarNoEncontrado(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("RECURSO_NO_ENCONTRADO", body.getCodigo());
        assertEquals(mensajeEsperado, body.getMensaje());
        assertNotNull(body.getTimestamp());
    }

    @Test
    @DisplayName("Debería manejar NoAutenticadoException y retornar status 401")
    void testManejarNoAutenticado() {
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarNoAutenticado(new NoAutenticadoException("No autenticado"));

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("NO_AUTENTICADO", body.getCodigo());
    }

    @Test
    @DisplayName("Debería manejar AccesoDenegadoException y retornar status 403")
    void testManejarAccesoDenegado() {
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarAccesoDenegado(new AccesoDenegadoException("Sin permiso"));

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("ACCESO_DENEGADO", body.getCodigo());
    }

    @Test
    @DisplayName("Debería manejar ConflictoEstadoException y retornar status 409")
    void testManejarConflictoEstado() {
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarConflictoEstado(new ConflictoEstadoException("Estado invalido"));

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("CONFLICTO_ESTADO", body.getCodigo());
    }

    @Test
    @DisplayName("Debería manejar ValidacionNegocioException y retornar status 400 con detalles")
    void testManejarValidacionNegocio() {
        List<ErrorDetalleDto> detalles = List
                .of(new ErrorDetalleDto().campo("tipoEvento").mensaje("*Campo obligatorio"));
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarValidacionNegocio(new ValidacionNegocioException("Inconsistencias de negocio", detalles));

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("VALIDACION_NEGOCIO", body.getCodigo());
        assertEquals(1, body.getDetalles().size());
        assertEquals("tipoEvento", body.getDetalles().get(0).getCampo());
    }

    @Test
    @DisplayName("Debería manejar MethodArgumentNotValidException y retornar status 400 con detalles de error")
    void testManejarErroresValidacion() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("objeto", "campo1", "Mensaje de error 1");
        FieldError error2 = new FieldError("objeto", "campo2", "Mensaje de error 2");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        @SuppressWarnings("null")
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal.manejarErroresValidacion(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("VALIDACION_NEGOCIO", body.getCodigo());
        assertNotNull(body.getTimestamp());

        List<ErrorDetalleDto> detalles = body.getDetalles();
        assertNotNull(detalles);
        assertEquals(2, detalles.size());
        assertTrue(detalles.stream()
                .anyMatch(d -> "campo1".equals(d.getCampo()) && "Mensaje de error 1".equals(d.getMensaje())));
        assertTrue(detalles.stream()
                .anyMatch(d -> "campo2".equals(d.getCampo()) && "Mensaje de error 2".equals(d.getMensaje())));
    }

    @Test
    @DisplayName("Debería manejar MethodArgumentNotValidException sin errores de campo y retornar status 400")
    void testManejarErroresValidacionSinErroresDeCampo() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        @SuppressWarnings("null")
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal.manejarErroresValidacion(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertTrue(body.getDetalles().isEmpty());
    }
}
