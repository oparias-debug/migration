package sv.gob.mh.siip.api_gateway.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GatewayAuditoriaAspect {

    private static final Logger logger = LoggerFactory.getLogger(GatewayAuditoriaAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    @Before("restController()")
    public void auditarEntrada(JoinPoint joinPoint) {

        logger.info("API Gateway - Llamada entrante a: {} con estos argumentos {} ", joinPoint.getSignature(),
                joinPoint.getArgs());
    }

    @After("restController()")
    public void auditarSalida(JoinPoint joinPoint) {
        logger.info("API Gateway - Llamada saliente de: {}", joinPoint.getSignature());
    }
}
