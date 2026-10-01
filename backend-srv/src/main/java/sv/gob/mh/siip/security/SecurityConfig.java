package sv.gob.mh.siip.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * backend-srv valida por su cuenta el JWT de Keycloak que api-gateway le reenvía (TokenRelay),
 * igual que admin-srv y siipsafi-srv. Así una petición que llegue sin pasar por el gateway
 * (la Route pública del chart, otro pod del cluster) también necesita un token válido, y la
 * identidad sale del token firmado y no de un header que cualquiera puede escribir.
 *
 * Aquí solo se exige el token. La autorización por rol de negocio (USUARIO.ROL) sigue en
 * {@link ActorContexto}, por endpoint.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Sin CSRF a propósito: la API es stateless (sin sesión ni cookies de autenticación) y solo
            // acepta el JWT del header Authorization, que un sitio ajeno no puede adjuntar a la petición.
            .csrf(csrf -> csrf.disable()) // NOSONAR java:S4502 -- ver comentario anterior
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
