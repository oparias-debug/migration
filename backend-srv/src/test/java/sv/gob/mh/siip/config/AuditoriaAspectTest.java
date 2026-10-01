package sv.gob.mh.siip.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class AuditoriaAspectTest {

    private final AuditoriaAspect aspect = new AuditoriaAspect();
    private final Logger logger = (Logger) LoggerFactory.getLogger(AuditoriaAspect.class.getName());
    private final ListAppender<ILoggingEvent> logAppender = new ListAppender<>();
    private MockHttpServletRequest request;
    private ProceedingJoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        logAppender.start();
        logger.addAppender(logAppender);
        request = new MockHttpServletRequest("POST", "/proyectos");
        request.addHeader("X-Trace-Id", "abc-123");
        request.addHeader("Authorization", "Bearer token-secreto");
        request.addHeader("Cookie", "SESSION=cookie-secreta");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        joinPoint = mock(ProceedingJoinPoint.class);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
        logger.detachAppender(logAppender);
        logAppender.stop();
    }

    @Test
    void logFullRequestAndResponse_registraPeticionYRespuesta_ignorandoArgumentosNoSerializables()
            throws Throwable {
        request.setQueryString("pagina=1");
        when(joinPoint.getArgs())
                .thenReturn(new Object[] {null, request, new MockHttpServletResponse(), "cuerpo-json"});
        when(joinPoint.proceed()).thenReturn("resultado");

        Object resultado = aspect.logFullRequestAndResponse(joinPoint);

        assertThat(resultado).isEqualTo("resultado");
        assertThat(logAppender.list).hasSize(2);
        String entrada = logAppender.list.get(0).getFormattedMessage();
        // Las credenciales no quedan en el log.
        assertThat(entrada).contains("[POST] /proyectos?pagina=1", "X-Trace-Id=abc-123", "Body: cuerpo-json")
                .contains("Authorization=***", "Cookie=***")
                .doesNotContain("token-secreto", "cookie-secreta");
        assertThat(logAppender.list.get(1).getFormattedMessage()).contains("Respuesta: resultado");
    }

    @Test
    void logFullRequestAndResponse_sinQueryNiCuerpo_registraValoresPorDefecto() throws Throwable {
        when(joinPoint.getArgs()).thenReturn(new Object[] {request});
        when(joinPoint.proceed()).thenReturn(null);

        Object resultado = aspect.logFullRequestAndResponse(joinPoint);

        assertThat(resultado).isNull();
        String entrada = logAppender.list.get(0).getFormattedMessage();
        assertThat(entrada).contains("[POST] /proyectos? ", "Body: N/A");
    }

    @Test
    void logFullRequestAndResponse_cuandoElControladorFalla_registraErrorYRelanza() throws Throwable {
        IllegalStateException falla = new IllegalStateException("fallo de negocio");
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.proceed()).thenThrow(falla);

        assertThatThrownBy(() -> aspect.logFullRequestAndResponse(joinPoint)).isSameAs(falla);
        // Spring invoca el @AfterThrowing con la misma excepción, que sigue su curso.
        aspect.logError(falla);

        ILoggingEvent error = logAppender.list.get(logAppender.list.size() - 1);
        assertThat(error.getLevel()).isEqualTo(Level.ERROR);
        assertThat(error.getFormattedMessage()).contains("[POST] /proyectos", "fallo de negocio");
    }

    @Test
    void restController_esSoloLaDeclaracionDelPointcut() {
        aspect.restController();

        assertThat(logAppender.list).isEmpty();
    }
}
