package sv.gob.mh.siip.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Mensaje, código y status HTTP declarados por las excepciones de negocio simples. */
class ExcepcionesHttpTest {

    @Test
    void notFoundException_conYSinMensaje_respondeNotFound() {
        assertThat(new NotFoundException().getMessage()).isNull();
        assertThat(new NotFoundException("no existe")).hasMessage("no existe");
        assertThat(statusDe(NotFoundException.class)).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void referencedException_tomaElMensajeDelWarning_yRespondeConflict() {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setKey("proyecto.referenciado");
        warning.addParam(7);

        assertThat(new ReferencedException(warning)).hasMessage("proyecto.referenciado,7");
        assertThat(new ReferencedException().getMessage()).isNull();
        assertThat(statusDe(ReferencedException.class)).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void formatoArchivoNoSoportadoException_respondeUnsupportedMediaType() {
        assertThat(new FormatoArchivoNoSoportadoException("no es PDF/A")).hasMessage("no es PDF/A");
        assertThat(statusDe(FormatoArchivoNoSoportadoException.class))
                .isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void operacionNoPermitidaException_respondeMethodNotAllowed() {
        assertThat(new OperacionNoPermitidaException("no se elimina")).hasMessage("no se elimina");
        assertThat(statusDe(OperacionNoPermitidaException.class)).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    private static HttpStatus statusDe(Class<? extends RuntimeException> tipo) {
        return tipo.getAnnotation(ResponseStatus.class).value();
    }
}
