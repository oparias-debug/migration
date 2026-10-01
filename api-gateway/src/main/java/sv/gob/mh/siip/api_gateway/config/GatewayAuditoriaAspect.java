package sv.gob.mh.siip.api_gateway.config;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GatewayAuditoriaAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayAuditoriaAspect.class);
    public static final String ARGUMENTOS_OMITIDOS = "[omitidos: @ArgumentosSensibles]";

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    @Before("restController()")
    public void auditarEntrada(JoinPoint joinPoint) {
        Object argumentos = tieneArgumentosSensibles(joinPoint) ? ARGUMENTOS_OMITIDOS : joinPoint.getArgs();
        LOGGER.info("API Gateway - Llamada entrante a: {} con estos argumentos {} ", joinPoint.getSignature(),
                argumentos);
    }

    @After("restController()")
    public void auditarSalida(JoinPoint joinPoint) {
        LOGGER.info("API Gateway - Llamada saliente de: {}", joinPoint.getSignature());
    }

    private static boolean tieneArgumentosSensibles(JoinPoint joinPoint) {
        if (!(joinPoint.getSignature() instanceof MethodSignature firma)) {
            return false;
        }
        Method metodo = firma.getMethod();
        return metodo.isAnnotationPresent(ArgumentosSensibles.class)
                || metodo.getDeclaringClass().isAnnotationPresent(ArgumentosSensibles.class);
    }
}
