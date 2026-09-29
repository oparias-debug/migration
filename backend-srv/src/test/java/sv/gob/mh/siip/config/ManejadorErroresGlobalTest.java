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
import sv.gob.mh.siip.exception.FormatoArchivoNoSoportadoException;
import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.OperacionNoPermitidaException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
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
    @DisplayName("Debería respetar el código propio de RecursoNoEncontradoException")
    void testManejarNoEncontradoConCodigo() {
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarNoEncontrado(new RecursoNoEncontradoException("PROYECTO_NO_ENCONTRADO", "No existe"));

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("PROYECTO_NO_ENCONTRADO", body.getCodigo());
    }

    @Test
    @DisplayName("Debería manejar ReglaNegocioException y retornar status 422 con su código")
    void testManejarReglaNegocio() {
        ResponseEntity<ErrorDto> conCodigo = manejadorErroresGlobal
                .manejarReglaNegocio(
                        new ReglaNegocioException("DOCUMENTO_PREINVERSION_REQUERIDO", "Falta el documento"));
        ResponseEntity<ErrorDto> sinCodigo = manejadorErroresGlobal
                .manejarReglaNegocio(new ReglaNegocioException(null, "Regla incumplida"));

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, conCodigo.getStatusCode());
    ErrorDto body = conCodigo.getBody();
    assertNotNull(body);
    assertEquals("DOCUMENTO_PREINVERSION_REQUERIDO", body.getCodigo());
    assertEquals("Falta el documento", body.getMensaje());
    ErrorDto generico = sinCodigo.getBody();
    assertNotNull(generico);
    assertEquals("REGLA_NEGOCIO", generico.getCodigo());
  }

  @Test
  @DisplayName("Debería incluir los detalles de ReglaNegocioException en la respuesta 422")
  void testManejarReglaNegocioConDetalles() {
    ErrorDetalleDto detalle = new ErrorDetalleDto().campo("respuestas[criterioId=2]").mensaje("Sin especificar");

    ResponseEntity<ErrorDto> respuesta = manejadorErroresGlobal
        .manejarReglaNegocio(new ReglaNegocioException("ESPECIFICAR_INCOMPLETO", "Falta especificar",
            List.of(detalle)));

    ErrorDto body = respuesta.getBody();
    assertNotNull(body);
    assertEquals("ESPECIFICAR_INCOMPLETO", body.getCodigo());
    assertEquals(1, body.getDetalles().size());
    assertEquals("respuestas[criterioId=2]", body.getDetalles().get(0).getCampo());
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
    @DisplayName("Debería manejar ConflictoEstadoException sin código y retornar el código genérico")
    void testManejarConflictoEstado() {
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarConflictoEstado(new ConflictoEstadoException("Estado invalido"));

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("CONFLICTO_ESTADO", body.getCodigo());
    }

    @Test
    @DisplayName("Debería manejar ConflictoEstadoException con código propio y respetarlo en la respuesta")
    void testManejarConflictoEstadoConCodigoPropio() {
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarConflictoEstado(new ConflictoEstadoException("PERIODO_CERRADO", "Periodo cerrado"));

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("PERIODO_CERRADO", body.getCodigo());
        assertEquals("Periodo cerrado", body.getMensaje());
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

    @Test
    @DisplayName("Debería manejar FormatoArchivoNoSoportadoException y retornar status 415")
    void testManejarFormatoArchivoNoSoportado() {
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarFormatoArchivoNoSoportado(new FormatoArchivoNoSoportadoException("El archivo no es PDF/A"));

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("FORMATO_ARCHIVO_NO_SOPORTADO", body.getCodigo());
        assertEquals("El archivo no es PDF/A", body.getMensaje());
    }

    @Test
    @DisplayName("Debería manejar InconsistenciaFechaException con y sin código propio y retornar status 422")
    void testManejarInconsistenciaFecha() {
        ResponseEntity<ErrorDto> conCodigo = manejadorErroresGlobal
                .manejarInconsistenciaFecha(new InconsistenciaFechaException("RANGO_INVERTIDO", "Rango invertido"));
        ResponseEntity<ErrorDto> sinCodigo = manejadorErroresGlobal
                .manejarInconsistenciaFecha(new InconsistenciaFechaException("Fuera del periodo"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, conCodigo.getStatusCode());
        ErrorDto body = conCodigo.getBody();
        assertNotNull(body);
        assertEquals("RANGO_INVERTIDO", body.getCodigo());
        assertEquals("Rango invertido", body.getMensaje());
        ErrorDto generico = sinCodigo.getBody();
        assertNotNull(generico);
        assertEquals("INCONSISTENCIA_FECHA", generico.getCodigo());
        assertEquals("Fuera del periodo", generico.getMensaje());
    }

    @Test
    @DisplayName("Debería manejar OperacionNoPermitidaException y retornar status 405")
    void testManejarOperacionNoPermitida() {
        ResponseEntity<ErrorDto> responseEntity = manejadorErroresGlobal
                .manejarOperacionNoPermitida(new OperacionNoPermitidaException("No se puede eliminar"));

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        ErrorDto body = responseEntity.getBody();
        assertNotNull(body);
        assertEquals("OPERACION_NO_PERMITIDA", body.getCodigo());
        assertEquals("No se puede eliminar", body.getMensaje());
    }
}
