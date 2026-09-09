package sv.gob.mh.siip.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Aspect
@Component
public class AuditoriaAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaAspect.class.getName());

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    @Around("restController()")
    public Object logFullRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Headers
        String headers = Collections.list(request.getHeaderNames()).stream()
                .map(h -> h + "=" + request.getHeader(h))
                .collect(Collectors.joining(", "));

        // Query params
        String queryParams = request.getQueryString() != null ? request.getQueryString() : "";

        // Body (solo si hay un argumento que no es HttpServletRequest o
        // HttpServletResponse)
        Object requestBody = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg != null && !(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse))
                .findFirst()
                .orElse(null);

        String bodyJson = requestBody != null ? requestBody.toString() : "N/A";

        logger.info("📥 [{}] {}?{} \nHeaders: {} \nBody: {}", method, uri, queryParams, headers, bodyJson);

        Object result;
        try {
            result = joinPoint.proceed();
            logger.info("📤 Respuesta: {}", result);
        } catch (Exception e) { // NOSONAR java:S2139 -- ver justificacion abajo
            // Este aspecto es el unico punto que deja rastro de auditoria de la peticion
            // que fallo (metodo, URI, headers y body ya logueados arriba); ManejadorErroresGlobal
            // solo traduce la excepcion a respuesta HTTP y no vuelve a loguearla, asi que no hay
            // log duplicado. El relanzamiento es obligatorio para que el status HTTP
            // (401/403/404/...) se resuelva correctamente en vez de responder 200 vacio.
            logger.error("❌ Error al ejecutar [{}] {}: {}", method, uri, e.getMessage(), e);
            throw e;
        }

        return result;
    }
}
