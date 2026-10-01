package sv.gob.mh.siip.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Simula en las pruebas el JWT de Keycloak que valida {@link SecurityConfig}, con el nombre de
 * usuario en "preferred_username", que es de donde lo lee {@link ActorContexto}.
 */
public final class AutenticacionDePrueba {

    private AutenticacionDePrueba() {
    }

    /** Para peticiones MockMvc: {@code mockMvc.perform(get(...).with(AutenticacionDePrueba.como(usuario)))}. */
    public static RequestPostProcessor como(String nombreUsuario) {
        return jwt().jwt(token -> token.subject(nombreUsuario).claim("preferred_username", nombreUsuario));
    }

    /**
     * Para pruebas que llaman a servicios o controladores sin MockMvc: deja autenticado al usuario
     * en el hilo actual, como lo haría el filtro de Spring Security.
     */
    public static void autenticar(String nombreUsuario) {
        Jwt token = Jwt.withTokenValue("token-de-prueba")
                .header("alg", "none")
                .subject(nombreUsuario)
                .claim("preferred_username", nombreUsuario)
                .build();
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(new JwtAuthenticationToken(token));
        SecurityContextHolder.setContext(contexto);
    }

    /** Deja el hilo sin usuario autenticado. */
    public static void limpiar() {
        SecurityContextHolder.clearContext();
    }
}
