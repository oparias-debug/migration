package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

class LoggingNotificacionServiceTest {

    private final LoggingNotificacionService service = new LoggingNotificacionService();
    private final ListAppender<ILoggingEvent> logAppender = new ListAppender<>();

    @BeforeEach
    void attachAppender() {
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(LoggingNotificacionService.class)).addAppender(logAppender);
    }

    @AfterEach
    void detachAppender() {
        ((Logger) LoggerFactory.getLogger(LoggingNotificacionService.class)).detachAppender(logAppender);
        logAppender.stop();
    }

    private String ultimoMensaje() {
        return logAppender.list.get(logAppender.list.size() - 1).getFormattedMessage();
    }

    private Proyecto proyecto() {
        return Proyecto.builder().id(1L).nombre("Proyecto Test").cup("00123").build();
    }

    private Usuario usuario(String correo) {
        return Usuario.builder().id(1L).correo(correo).build();
    }

    @Test
    void notificarSolicitudCup_incluyeLosCorreosDeLosDestinatarios() {
        service.notificarSolicitudCup(proyecto(), List.of(usuario("coord1@test.com"), usuario("coord2@test.com")));

        assertThat(ultimoMensaje()).contains("Proyecto Test", "coord1@test.com", "coord2@test.com");
    }

    @Test
    void notificarSolicitudCup_indicaSinDestinatarios_cuandoListaVacia() {
        service.notificarSolicitudCup(proyecto(), List.of());

        assertThat(ultimoMensaje()).contains("sin destinatarios con rol COORDINADOR_PRE");
    }

    @Test
    void notificarRespuestaObservacion_incluyeElCorreoDelTecnico() {
        service.notificarRespuestaObservacion(proyecto(), usuario("tecnico@test.com"));

        assertThat(ultimoMensaje()).contains("tecnico@test.com");
    }

    @Test
    void notificarRespuestaObservacion_indicaSinTecnicoAsignado_cuandoDestinatarioEsNulo() {
        service.notificarRespuestaObservacion(proyecto(), null);

        assertThat(ultimoMensaje()).contains("sin tecnico asignado");
    }

    @Test
    void notificarAlertaEliminacion_incluyeElCorreoDelDestinatario() {
        service.notificarAlertaEliminacion(proyecto(), usuario("urp@test.com"));

        assertThat(ultimoMensaje()).contains("urp@test.com");
    }

    @Test
    void notificarAlertaEliminacion_indicaSinUsuarioResuelto_cuandoDestinatarioEsNulo() {
        service.notificarAlertaEliminacion(proyecto(), null);

        assertThat(ultimoMensaje()).contains("sin usuario resuelto");
    }

    @Test
    void notificarDevolucionSolicitud_incluyeElCorreoDelDestinatario() {
        service.notificarDevolucionSolicitud(proyecto(), usuario("urp@test.com"));

        assertThat(ultimoMensaje()).contains("urp@test.com");
    }

    @Test
    void notificarEmisionCup_incluyeElCupYElCorreoDelDestinatario() {
        service.notificarEmisionCup(proyecto(), usuario("urp@test.com"));

        assertThat(ultimoMensaje()).contains("00123", "urp@test.com");
    }
}
