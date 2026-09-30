package sv.gob.mh.infrastructure.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import sv.gob.mh.api.dto.catalogo.CatalogCreateRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.infrastructure.exception.CatalogosManejadorErrores.RespuestaError;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/** Traducción de los errores de CU-ADM-01 al schema {@code Error} del contrato. */
class CatalogosManejadorErroresTest {

    private static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";

    private final CatalogosManejadorErrores manejador = new CatalogosManejadorErrores();

    static Stream<Arguments> erroresDeCatalogo() {
        return Stream.of(
            Arguments.of(ErrorCatalogoException.solicitudInvalida("Mal"), HttpStatus.BAD_REQUEST),
            Arguments.of(ErrorCatalogoException.registroInexistente("SV"), HttpStatus.NOT_FOUND),
            Arguments.of(ErrorCatalogoException.conflicto("CATALOGO_CON_REGISTROS", "Tiene registros"),
                HttpStatus.CONFLICT),
            Arguments.of(ErrorCatalogoException.reglaNegocio("REGLA", "Regla", "code", "X"),
                HttpStatus.UNPROCESSABLE_ENTITY),
            Arguments.of(ErrorCatalogoException.eliminacionNoPermitida("No se elimina", "inactivarCatalogo"),
                HttpStatus.METHOD_NOT_ALLOWED));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("erroresDeCatalogo")
    @DisplayName("Cada tipo de error de catálogo tiene su estado HTTP del contrato")
    void cadaTipoTieneSuEstado(ErrorCatalogoException error, HttpStatus esperado) {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarErrorCatalogo(error);

        assertThat(respuesta.getStatusCode()).isEqualTo(esperado);
        assertThat(respuesta.getBody().codigo()).isEqualTo(error.getCodigo());
        assertThat(respuesta.getHeaders().containsKey(HttpHeaders.ALLOW))
            .isEqualTo(esperado == HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("Un cuerpo que no cumple las validaciones reporta cada campo")
    void cuerpoInvalidoReportaLosCampos() {
        BeanPropertyBindingResult resultado = new BeanPropertyBindingResult(new CatalogCreateRequestDto(), "request");
        resultado.addError(new FieldError("request", "name", "no debe ser nulo"));

        RespuestaError cuerpo = manejador.manejarCuerpoInvalido(new BindException(resultado)).getBody();

        assertThat(cuerpo.codigo()).isEqualTo(SOLICITUD_INVALIDA);
        assertThat(cuerpo.detalles()).singleElement()
            .satisfies(detalle -> assertThat(detalle.getCampo()).isEqualTo("name"));
    }

    @Test
    @DisplayName("Una propiedad no declarada es 400; el code al actualizar descriptores es 422")
    void propiedadesNoDeclaradas() {
        Throwable otra = new IllegalStateException(new UnrecognizedPropertyException(null, "no declarada", null,
            CatalogCreateRequestDto.class, "otra", List.of()));
        Throwable codigo = new IllegalStateException(new UnrecognizedPropertyException(null, "no declarada", null,
            CatalogDescriptorsUpdateRequestDto.class, "code", List.of()));

        Throwable codigoAlCrear = new IllegalStateException(new UnrecognizedPropertyException(null, "no declarada",
            null, CatalogCreateRequestDto.class, "code", List.of()));

        assertThat(manejador.manejarCuerpoIlegible(codigoAlCrear).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ResponseEntity<RespuestaError> desconocida = manejador.manejarCuerpoIlegible(otra);
        ResponseEntity<RespuestaError> inmutable = manejador.manejarCuerpoIlegible(codigo);

        assertThat(desconocida.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(desconocida.getBody().detalles()).singleElement()
            .satisfies(detalle -> assertThat(detalle.getCampo()).isEqualTo("otra"));
        assertThat(inmutable.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(inmutable.getBody().codigo()).isEqualTo("CODIGO_CATALOGO_INMUTABLE");
    }

    @Test
    @DisplayName("JSON ilegible, parámetros inválidos y errores no controlados")
    void erroresSinDetalle() {
        assertThat(manejador.manejarCuerpoIlegible(new IllegalStateException("json roto")).getBody().codigo())
            .isEqualTo(SOLICITUD_INVALIDA);
        assertThat(manejador.manejarParametroInvalido(new IllegalArgumentException()).getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        ResponseEntity<RespuestaError> interno = manejador.manejarErrorInterno(new IllegalStateException("fallo"));
        assertThat(interno.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(interno.getBody().codigo()).isEqualTo("ERROR_INTERNO");
    }
}
