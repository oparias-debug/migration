package sv.gob.mh.siip.api_gateway.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import sv.gob.mh.siip.api_gateway.controller.AuthController;
import sv.gob.mh.siip.api_gateway.dto.LoginRequest;

class GatewayAuditoriaAspectTest {

    private final GatewayAuditoriaAspect aspect = new GatewayAuditoriaAspect();
    private final Logger logger = (Logger) LoggerFactory.getLogger(GatewayAuditoriaAspect.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private JoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        appender.start();
        logger.addAppender(appender);
        Signature firma = mock(Signature.class);
        when(firma.toString()).thenReturn("AuthController.login(..)");
        joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(firma);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
    }

    private static JoinPoint llamadaA(Method metodo, Object... argumentos) {
        MethodSignature firma = mock(MethodSignature.class);
        when(firma.getMethod()).thenReturn(metodo);
        when(firma.toString()).thenReturn(metodo.getName());
        JoinPoint llamada = mock(JoinPoint.class);
        when(llamada.getSignature()).thenReturn(firma);
        when(llamada.getArgs()).thenReturn(argumentos);
        return llamada;
    }

    private String mensajeRegistrado() {
        assertThat(appender.list).hasSize(1);
        return appender.list.get(0).getFormattedMessage();
    }

    @Test
    void auditarEntrada_registraFirmaYArgumentos() {
        when(joinPoint.getArgs()).thenReturn(new Object[] {"usuario"});

        aspect.auditarEntrada(joinPoint);

        assertThat(mensajeRegistrado()).contains("Llamada entrante a: AuthController.login(..)", "usuario");
    }

    @Test
    void auditarEntrada_controladorConArgumentosSensibles_noRegistraElPassword() throws Exception {
        Method login = AuthController.class.getMethod("login", LoginRequest.class);

        aspect.auditarEntrada(llamadaA(login, new LoginRequest("tecnico.urp", "clave-secreta")));

        assertThat(mensajeRegistrado())
                .contains("Llamada entrante a: login", GatewayAuditoriaAspect.ARGUMENTOS_OMITIDOS)
                .doesNotContain("clave-secreta", "tecnico.urp");
    }

    @Test
    void auditarEntrada_controladorConArgumentosSensibles_noRegistraElRefreshToken() throws Exception {
        Method refresh = AuthController.class.getMethod("refreshToken", String.class);

        aspect.auditarEntrada(llamadaA(refresh, "refresh-token-secreto"));

        assertThat(mensajeRegistrado()).doesNotContain("refresh-token-secreto");
    }

    @Test
    void auditarEntrada_metodoSinArgumentosSensibles_registraLosArgumentos() throws Exception {
        Method comun = Object.class.getMethod("equals", Object.class);

        aspect.auditarEntrada(llamadaA(comun, "argumento-visible"));

        assertThat(mensajeRegistrado()).contains("argumento-visible");
    }

    @Test
    void loginRequest_toStringNoIncluyeElPassword() {
        assertThat(new LoginRequest("tecnico.urp", "clave-secreta").toString())
                .contains("tecnico.urp")
                .doesNotContain("clave-secreta");
    }

    @Test
    void auditarSalida_registraFirma() {
        aspect.auditarSalida(joinPoint);

        assertThat(mensajeRegistrado()).isEqualTo("API Gateway - Llamada saliente de: AuthController.login(..)");
    }

    @Test
    void restController_esSoloLaDeclaracionDelPointcut() {
        aspect.restController();

        assertThat(appender.list).isEmpty();
    }
}
