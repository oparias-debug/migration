package sv.gob.mh.infrastructure.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import sv.gob.mh.api.dto.calendario.CrearCalendarioRequestDto;
import sv.gob.mh.infrastructure.exception.CalendariosManejadorErrores.RespuestaError;
import sv.gob.mh.shared.exception.ErrorCalendarioException;

/** Traducción de los errores de CU-ADM-04 al schema {@code Error} del contrato. */
class CalendariosManejadorErroresTest {

    private static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";

    private final CalendariosManejadorErrores manejador = new CalendariosManejadorErrores();

    static Stream<Arguments> erroresDeCalendario() {
        return Stream.of(
                Arguments.of(ErrorCalendarioException.noEncontrado("CALENDARIO_INEXISTENTE", "No existe"),
                        HttpStatus.NOT_FOUND),
                Arguments.of(ErrorCalendarioException.conflicto("EXCEPCION_DUPLICADA", "Duplicada"),
                        HttpStatus.CONFLICT),
                Arguments.of(ErrorCalendarioException.inconsistenciaFecha("FECHAS_INCONSISTENTES", "Fechas"),
                        HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("erroresDeCalendario")
    @DisplayName("Cada tipo de error de calendario tiene su estado HTTP del contrato")
    void cadaTipoTieneSuEstado(ErrorCalendarioException error, HttpStatus esperado) {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarErrorCalendario(error);

        assertThat(respuesta.getStatusCode()).isEqualTo(esperado);
        assertThat(respuesta.getBody().codigo()).isEqualTo(error.getCodigo());
        assertThat(respuesta.getBody().detalles()).isEmpty();
    }

    @Test
    @DisplayName("Un cuerpo que no cumple las validaciones reporta cada campo una sola vez")
    void cuerpoInvalidoReportaLosCampos() {
        BeanPropertyBindingResult resultado = new BeanPropertyBindingResult(new CrearCalendarioRequestDto(),
                "request");
        resultado.addError(new FieldError("request", "codigo", "no debe ser nulo"));
        resultado.addError(new FieldError("request", "codigo", "otro mensaje"));
        resultado.addError(new FieldError("request", "nombre", null));

        ResponseEntity<RespuestaError> respuesta = manejador.manejarCuerpoInvalido(new BindException(resultado));

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody().codigo()).isEqualTo(SOLICITUD_INVALIDA);
        assertThat(respuesta.getBody().detalles())
                .containsEntry("codigo", "no debe ser nulo")
                .containsEntry("nombre", "null")
                .hasSize(2);
    }

    @Test
    @DisplayName("Una solicitud mal formada es 400 SOLICITUD_INVALIDA, sin detalles")
    void solicitudMalFormada() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarSolicitudInvalida(
                new HttpMessageNotReadableException("JSON inválido", null, null));

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody().codigo()).isEqualTo(SOLICITUD_INVALIDA);
        assertThat(respuesta.getBody().detalles()).isEmpty();
    }

    @Test
    @DisplayName("RN12: sin rol de administración de calendarios es 403 SIN_PERMISOS")
    void sinPermisos() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarSinPermisos(new AccessDeniedException("No"));

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(respuesta.getBody().codigo()).isEqualTo("SIN_PERMISOS");
    }

    @Test
    @DisplayName("Un error no controlado es 500 ERROR_INTERNO, sin exponer el detalle")
    void errorInterno() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarErrorInterno(new IllegalStateException("Fallo"));

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(respuesta.getBody().codigo()).isEqualTo("ERROR_INTERNO");
        assertThat(respuesta.getBody().mensaje()).doesNotContain("Fallo");
        assertThat(respuesta.getBody().timestamp()).isNotNull();
    }
}
