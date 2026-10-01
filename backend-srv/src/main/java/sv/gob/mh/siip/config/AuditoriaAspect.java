package sv.gob.mh.siip.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
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
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaAspect.class.getName());

    /** Headers con credenciales: se registran enmascarados para que el token no quede en el log. */
    private static final Set<String> HEADERS_SENSIBLES = Set.of("authorization", "cookie");
    private static final String VALOR_ENMASCARADO = "***";

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
                .map(h -> h + "=" + (HEADERS_SENSIBLES.contains(h.toLowerCase(Locale.ROOT))
                        ? VALOR_ENMASCARADO
                        : request.getHeader(h)))
                .collect(Collectors.joining(", "));

        // Query params
        String queryParams = request.getQueryString() != null ? request.getQueryString() : "";

        // Body (solo si hay un argumento que no es HttpServletRequest o
        // HttpServletResponse)
        Object requestBody = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg != null
                        && !(arg instanceof HttpServletRequest)
                        && !(arg instanceof HttpServletResponse))
                .findFirst()
                .orElse(null);

        String bodyJson = requestBody != null ? requestBody.toString() : "N/A";

        LOGGER.info("📥 [{}] {}?{} \nHeaders: {} \nBody: {}", method, uri, queryParams, headers, bodyJson);

        Object result = joinPoint.proceed();
        LOGGER.info("📤 Respuesta: {}", result);
        return result;
    }

    /**
     * Unico rastro de auditoria de la peticion que fallo (metodo, URI, headers y body ya se registraron
     * al entrar): ManejadorErroresGlobal solo traduce la excepcion a respuesta HTTP y no vuelve a
     * registrarla. Se registra sin capturarla, asi que la excepcion sigue intacta y el status HTTP
     * (401/403/404/...) se resuelve igual.
     *
     * @param error excepcion que lanzo el controller
     */
    @AfterThrowing(pointcut = "restController()", throwing = "error")
    public void logError(Throwable error) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        LOGGER.error("❌ Error al ejecutar [{}] {}: {}", request.getMethod(), request.getRequestURI(),
                error.getMessage(), error);
    }
}
