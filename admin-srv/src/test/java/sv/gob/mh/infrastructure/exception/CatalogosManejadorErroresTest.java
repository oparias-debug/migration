package sv.gob.mh.infrastructure.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import sv.gob.mh.api.dto.catalogo.CatalogoCreacionDto;
import sv.gob.mh.api.dto.catalogo.ErrorDto;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/** Traducción de los errores de CU-ADM-01 al schema {@code Error} del contrato. */
class CatalogosManejadorErroresTest {

    private final CatalogosManejadorErrores manejador = new CatalogosManejadorErrores();

    static Stream<Arguments> erroresDeCatalogo() {
        return Stream.of(
            Arguments.of(ErroresCatalogo.registroInexistente("SV"), HttpStatus.NOT_FOUND),
            Arguments.of(ErroresCatalogo.catalogoConRegistros(), HttpStatus.CONFLICT),
            Arguments.of(ErroresCatalogo.jerarquiaCiclica(), HttpStatus.CONFLICT),
            Arguments.of(ErroresCatalogo.fechaVigenciaVencida(), HttpStatus.CONFLICT),
            Arguments.of(ErroresCatalogo.codigoInmutable(), HttpStatus.UNPROCESSABLE_ENTITY),
            Arguments.of(ErroresCatalogo.eliminacionNoPermitida(), HttpStatus.METHOD_NOT_ALLOWED));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("erroresDeCatalogo")
    @DisplayName("Cada error del CU tiene su estado HTTP del contrato y su código E-xx / S-0x")
    void cadaTipoTieneSuEstado(ErrorCatalogoException error, HttpStatus esperado) {
        ResponseEntity<ErrorDto> respuesta = manejador.manejarErrorCatalogo(error,
                new MockHttpServletRequest("DELETE", "/api/v1/catalogos/PAIS"));

        assertThat(respuesta.getStatusCode()).isEqualTo(esperado);
        assertThat(respuesta.getBody().getCodigo()).isEqualTo(error.getCodigo()).matches("^(E-\\d{2}|S-0[45])$");
        assertThat(respuesta.getBody().getMensaje()).isEqualTo(error.getMessage());
        assertThat(respuesta.getHeaders().containsKey(HttpHeaders.ALLOW))
            .isEqualTo(esperado == HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("E-24: el header Allow ofrece las operaciones que sí admite el recurso")
    void eliminacionOfreceLasOperacionesPermitidas() {
        ResponseEntity<ErrorDto> catalogo = manejador.manejarErrorCatalogo(ErroresCatalogo.eliminacionNoPermitida(),
                new MockHttpServletRequest("DELETE", "/api/v1/catalogos/PAIS"));
        ResponseEntity<ErrorDto> registro = manejador.manejarErrorCatalogo(ErroresCatalogo.eliminacionNoPermitida(),
                new MockHttpServletRequest("DELETE", "/api/v1/catalogos/PAIS/registros/COL"));

        assertThat(catalogo.getBody().getCodigo()).isEqualTo("E-24");
        assertThat(catalogo.getBody().getMensaje()).isEqualTo("Operación no permitida: solo se admite la inactivación.");
        assertThat(catalogo.getHeaders().getFirst(HttpHeaders.ALLOW)).isEqualTo("GET, PUT");
        assertThat(registro.getHeaders().getFirst(HttpHeaders.ALLOW)).isEqualTo("GET, PATCH");
    }

    @Test
    @DisplayName("E-25: sin el rol de mantenimiento, 403")
    void sinPermisos() {
        ResponseEntity<ErrorDto> respuesta = manejador.manejarSinPermisos(new AccessDeniedException("no"));

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(respuesta.getBody().getCodigo()).isEqualTo("E-25");
        assertThat(respuesta.getBody().getMensaje()).isEqualTo("No tiene permisos para modificar catálogos o registros.");
    }

    @Test
    @DisplayName("Un cuerpo que no cumple el schema es 400, salvo la posición de un campo (S-05, 422)")
    void cuerpoInvalido() {
        BeanPropertyBindingResult nombre = new BeanPropertyBindingResult(new CatalogoCreacionDto(), "request");
        nombre.addError(new FieldError("request", "nombre", "no debe ser nulo"));
        BeanPropertyBindingResult posicion = new BeanPropertyBindingResult(new CatalogoCreacionDto(), "request");
        posicion.addError(new FieldError("request", "campos[1].posicion", null, false, null, null, "no debe ser nulo"));

        ResponseEntity<ErrorDto> porNombre = manejador.manejarCuerpoInvalido(new BindException(nombre));
        ResponseEntity<ErrorDto> porPosicion = manejador.manejarCuerpoInvalido(new BindException(posicion));

        assertThat(porNombre.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(porNombre.getBody().getCodigo()).isEqualTo(CatalogosManejadorErrores.SOLICITUD_INVALIDA);
        assertThat(porPosicion.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(porPosicion.getBody().getCodigo()).isEqualTo("S-05");
    }

    @Test
    @DisplayName("JSON ilegible, parámetros inválidos y errores no controlados")
    void erroresFueraDelCu() {
        assertThat(manejador.manejarCuerpoIlegible(new IllegalStateException("json roto")).getBody().getCodigo())
            .isEqualTo(CatalogosManejadorErrores.SOLICITUD_INVALIDA);
        assertThat(manejador.manejarParametroInvalido(new IllegalArgumentException()).getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        ResponseEntity<ErrorDto> interno = manejador.manejarErrorInterno(new IllegalStateException("fallo"));
        assertThat(interno.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(interno.getBody().getCodigo()).isEqualTo("ERROR_INTERNO");
    }
}
