package sv.gob.mh.infrastructure.config.security;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** Rutas de CU-ADM-01 y CU-ADM-04, cuyo 401 lleva el schema {@code Error} de su contrato. */
    private static final String RUTAS_CATALOGOS = "/api/v1/catalogos/**";
    private static final String RUTAS_CALENDARIOS = "/api/v1/calendarios/**";

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:Content-Type,Authorization}")
    private String allowedHeaders;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        AuthenticationEntryPoint entryPoint = authenticationEntryPoint(objectMapper);
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Sin CSRF a propósito: la API es stateless (sin sesión ni cookies de autenticación) y solo
            // acepta el JWT del header Authorization, que un sitio ajeno no puede adjuntar a la petición.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                // Everything else requires JWT authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(entryPoint))
            .oauth2ResourceServer(oauth2 -> oauth2
                .authenticationEntryPoint(entryPoint)
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())))
            .build();
    }

    /** El 401 estándar de Bearer, salvo en las rutas de catálogos y calendarios, que responden con su contrato. */
    private static AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> porRuta = new LinkedHashMap<>();
        porRuta.put(PathPatternRequestMatcher.withDefaults().matcher(RUTAS_CATALOGOS),
                new CatalogosAuthenticationEntryPoint(objectMapper));
        porRuta.put(PathPatternRequestMatcher.withDefaults().matcher(RUTAS_CALENDARIOS),
                new CatalogosAuthenticationEntryPoint(objectMapper));
        DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(porRuta);
        entryPoint.setDefaultEntryPoint(new BearerTokenAuthenticationEntryPoint());
        return entryPoint;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        config.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** Roles de realm de Keycloak como {@code ROLE_<rol>} (ver {@link RolesRealmConverter}). */
    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        var jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new RolesRealmConverter());
        return jwtConverter;
    }
}
