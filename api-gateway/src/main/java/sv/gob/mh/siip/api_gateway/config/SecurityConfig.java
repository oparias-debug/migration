package sv.gob.mh.siip.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        // /back/** ya no se autoriza aquí por rol Keycloak (ROLE_ADMIN/ROLE_USER):
        // los casos de uso de back (p.ej. CU-PRE-01) tienen su propio modelo de
        // roles de negocio (RolUsuario: TECNICO_URP/TECNICO_PRE/ADMINISTRADOR,
        // resuelto desde USUARIO.ROL vía el header X-Usuario) y hacen su propia
        // autorización fina por endpoint en ActorContexto — mantener también un
        // gate por rol Keycloak aquí duplicaría el modelo de roles en dos sistemas
        // que habría que sincronizar a mano. El gateway solo exige un JWT válido.
        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                                .authorizeExchange(
                                                auth -> auth.pathMatchers("/swagger-ui.html", "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/webjars/**", "/back/swagger-ui.html",
                                                                "/back/swagger-ui/**",
                                                                "/back/v3/api-docs/**").permitAll()
                                                                .pathMatchers("/error/**", "/auth/**").permitAll()
                                                                .anyExchange().authenticated())
                                .oauth2ResourceServer(
                                                oauth2 -> oauth2.jwt(
                                                                jwtSpec -> jwtSpec.jwtAuthenticationConverter(
                                                                                new ReactiveJwtAuthConverter())));
                return http.build();
        }
}
