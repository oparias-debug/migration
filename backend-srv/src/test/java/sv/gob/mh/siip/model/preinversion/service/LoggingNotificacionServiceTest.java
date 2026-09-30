package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
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
    void notificarSolicitudViabilidad_incluyeElLinkAlAnexoA1YLosCorreosDeLosViabilizadores() {
        service.notificarSolicitudViabilidad(proyecto(), List.of(usuario("viab@test.com")));

        assertThat(ultimoMensaje()).contains("Proyecto Test", "/preinversion/proyectos/1/viabilidad", "viab@test.com");
    }

    @Test
    void notificarComentariosYEmisionDeViabilidad_incluyenAlTecnicoUrpOIndicanQueNoSeResolvio() {
        service.notificarComentariosViabilidad(proyecto(), usuario("urp@test.com"));
        assertThat(ultimoMensaje()).contains("comentarios", "urp@test.com");

        service.notificarEmisionViabilidad(proyecto(), usuario("urp@test.com"));
        assertThat(ultimoMensaje()).contains("emitió la Viabilidad", "urp@test.com");

        service.notificarEmisionViabilidad(proyecto(), null);
        assertThat(ultimoMensaje()).contains("sin usuario resuelto");
    }

    @Test
    void notificarSolicitudCup_indicaSinDestinatarios_cuandoListaVacia() {
        service.notificarSolicitudCup(proyecto(), List.of());

        assertThat(ultimoMensaje()).contains("sin destinatarios activos con ese rol");
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

    @Test
    void notificarProgramacionEnviadaARevision_incluyeUnidadAnioYCorreosDeLosDestinatarios() {
        service.notificarProgramacionEnviadaARevision(25L, 2027, List.of(usuario("pre1@test.com")));

        assertThat(ultimoMensaje()).contains("25", "2027", "pre1@test.com");
    }

    @Test
    void notificarProgramacionEnviadaARevision_indicaSinDestinatarios_cuandoListaVacia() {
        service.notificarProgramacionEnviadaARevision(25L, 2027, List.of());

        assertThat(ultimoMensaje()).contains("sin destinatarios activos con ese rol");
    }

    @Test
    void notificarObservacionesDgicp_incluyeUnidadAnioYCorreosDeLosDestinatarios() {
        service.notificarObservacionesDgicp(25L, 2027, List.of(usuario("urp1@test.com")));

        assertThat(ultimoMensaje()).contains("25", "2027", "urp1@test.com");
    }

    @Test
    void notificarRespuestaInstitucion_incluyeUnidadAnioYCorreosDeLosDestinatarios() {
        service.notificarRespuestaInstitucion(25L, 2027, List.of(usuario("pre2@test.com")));

    assertThat(ultimoMensaje()).contains("25", "2027", "pre2@test.com");
  }

  @Test
  void notificarEmisionElegibilidad_incluyeProyectoYCorreosDeLosDestinatarios() {
    service.notificarEmisionElegibilidad(proyecto(), List.of(usuario("urp25@test.com"), usuario("pre25@test.com")));

    assertThat(ultimoMensaje()).contains("CU-PRE-25 FB1", "urp25@test.com", "pre25@test.com");
  }

  @Test
  void notificarComentariosOtElegibilidad_incluyeElFormularioYLosViabilizadores() {
    service.notificarComentariosOtElegibilidad(proyecto(), List.of(usuario("viab25@test.com")));

    assertThat(ultimoMensaje()).contains("CU-PRE-25 FB2", "/elegibilidad", "viab25@test.com");
  }

  @Test
  void notificarObservacionesElegibilidadAtendidas_indicaSinDestinatarios_cuandoListaVacia() {
    service.notificarObservacionesElegibilidadAtendidas(proyecto(), List.of());

    assertThat(ultimoMensaje()).contains("CU-PRE-25 FB2", "sin destinatarios activos con ese rol");
  }

    @Test
    void notificacionesDeOpinionTecnica_incluyenElCupYLosDestinatarios() {
        LocalDate fin = LocalDate.of(2026, 10, 5);
        List<Usuario> destinatarios = List.of(usuario("actor@test.com"));

        service.notificarSolicitudOpinionTecnica(proyecto(), destinatarios);
        assertThat(ultimoMensaje()).contains("A2 a", "00123", "actor@test.com");
        service.notificarAsignacionOpinionTecnica(proyecto(), usuario("pre@test.com"));
        assertThat(ultimoMensaje()).contains("A2 b", "pre@test.com");
        service.notificarComentariosOpinionTecnica(proyecto(), destinatarios, fin);
        assertThat(ultimoMensaje()).contains("A2 c", "2026-10-05", "actor@test.com");
        service.notificarAjustesOpinionTecnica(proyecto(), destinatarios);
        assertThat(ultimoMensaje()).contains("A2 d", "actor@test.com");
        service.notificarVistoBuenoOpinionTecnica(proyecto(), null);
        assertThat(ultimoMensaje()).contains("visto bueno", "sin usuario resuelto");
        service.notificarEmisionOpinionTecnica(proyecto(), destinatarios);
        assertThat(ultimoMensaje()).contains("A2 e", "actor@test.com");
        service.notificarAlertaPlazoObservaciones(proyecto(), destinatarios, fin);
        assertThat(ultimoMensaje()).contains("A2 f", "2026-10-05");
        service.notificarVencimientoPlazoObservaciones(proyecto(), List.of());
        assertThat(ultimoMensaje()).contains("A2 g", "sin destinatarios activos con ese rol");
    }

    @Test
    void notificacionesDePriorizacion_incluyenElCupElTramoYLosDestinatarios() {
        List<Usuario> destinatarios = List.of(usuario("coord@test.com"));

        service.notificarPriorizacionPorRevisar(proyecto(), destinatarios, "criterio 5");
        assertThat(ultimoMensaje()).contains("CU-PRE-26.5", "criterio 5", "00123", "coord@test.com");
        service.notificarCriterioCincoPorCalificar(proyecto(), destinatarios);
        assertThat(ultimoMensaje()).contains("debe calificarse el criterio 5", "Proyecto Test");
        service.notificarPriorizacionCompletada(proyecto(), List.of());
        assertThat(ultimoMensaje()).contains("Se ha completado", "sin destinatarios activos con ese rol");
    }

    @Test
    void conElNivelInfoDesactivado_noRegistraNingunaNotificacion() {
        Logger logger = (Logger) LoggerFactory.getLogger(LoggingNotificacionService.class);
        Level anterior = logger.getLevel();
        logger.setLevel(Level.WARN);
        try {
            Proyecto p = proyecto();
            Usuario u = usuario("u@test.com");
            List<Usuario> d = List.of(u);
            LocalDate fin = LocalDate.of(2026, 10, 5);
            service.notificarEmisionCup(p, u);
            service.notificarProgramacionEnviadaARevision(1L, 2026, d);
            service.notificarObservacionesDgicp(1L, 2026, d);
            service.notificarRespuestaInstitucion(1L, 2026, d);
            service.notificarObservacionesAvance(1L, 2026, "C1", d);
            service.notificarRespuestaInstitucionAvance(1L, 2026, "C1", d);
            service.notificarSolicitudViabilidad(p, d);
            service.notificarComentariosViabilidad(p, u);
            service.notificarEmisionViabilidad(p, u);
            service.notificarEmisionElegibilidad(p, d);
            service.notificarComentariosOtElegibilidad(p, d);
            service.notificarObservacionesElegibilidadAtendidas(p, d);
            service.notificarSolicitudOpinionTecnica(p, d);
            service.notificarAsignacionOpinionTecnica(p, u);
            service.notificarComentariosOpinionTecnica(p, d, fin);
            service.notificarAjustesOpinionTecnica(p, d);
            service.notificarVistoBuenoOpinionTecnica(p, u);
            service.notificarEmisionOpinionTecnica(p, d);
            service.notificarAlertaPlazoObservaciones(p, d, fin);
            service.notificarVencimientoPlazoObservaciones(p, d);
            service.notificarPriorizacionPorRevisar(p, d, "criterio 5");
            service.notificarCriterioCincoPorCalificar(p, d);
            service.notificarPriorizacionCompletada(p, d);
        } finally {
            logger.setLevel(anterior);
        }

        assertThat(logAppender.list).isEmpty();
    }
}
