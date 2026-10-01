package sv.gob.mh.infrastructure.config.security;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 401 de las rutas de CU-ADM-01 y CU-ADM-04 con el schema {@code Error} de su contrato ({@code codigo:
 * NO_AUTENTICADO}). El token ausente, inválido o expirado se detecta en el filtro de seguridad,
 * antes del controller, así que el manejador de errores de catálogos no llega a verlo.
 */
public class CatalogosAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final ObjectMapper objectMapper;

    public CatalogosAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("codigo", "NO_AUTENTICADO");
        cuerpo.put("mensaje", "Token ausente, inválido o expirado.");
        cuerpo.put("timestamp", OffsetDateTime.now(ZONA_EL_SALVADOR));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), cuerpo);
    }
}
