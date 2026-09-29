package sv.gob.mh.infrastructure.config.authz;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * El interceptor de un permiso suelto (`@Permission`), hermano del de `@PermissionsAllowed`.
 *
 * <p>Lo que hay que asegurar es que <b>deniega antes de ejecutar</b>: si dejara pasar y
 * comprobara después, el efecto ya estaría hecho. Y que el mensaje del error diga quién,
 * qué operación y sobre qué recurso, porque es lo único que aparece en el log cuando alguien
 * pregunta por qué le sale un 403.</p>
 */
class PermissionInterceptorTest {

    private final AuthorizationService authorizationService = mock(AuthorizationService.class);
    private final PermissionInterceptor interceptor = new PermissionInterceptor(authorizationService);

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    /** Anotación de permiso con los valores indicados, sin necesidad de una clase anotada. */
    private Permission permiso(String code, String operation, String path) {
        return new Permission() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Permission.class;
            }

            @Override
            public String code() {
                return code;
            }

            @Override
            public String operation() {
                return operation;
            }

            @Override
            public String path() {
                return path;
            }
        };
    }

    @Test
    @DisplayName("Con el permiso concedido, deja seguir")
    void conPermisoDejaSeguir() throws Throwable {
        ProceedingJoinPoint punto = mock(ProceedingJoinPoint.class);
        when(punto.proceed()).thenReturn("resultado");
        when(authorizationService.hasGranularPermission("VIEW", "expedientes-consulta")).thenReturn(true);

        Object resultado = interceptor.checkGranularPermission(
            punto, permiso("consultar", "VIEW", "expedientes-consulta"));

        assertEquals("resultado", resultado);
        verify(punto).proceed();
    }

    @Test
    @DisplayName("Sin el permiso, deniega ANTES de ejecutar el método")
    void sinPermisoDeniegaAntesDeEjecutar() throws Throwable {
        ProceedingJoinPoint punto = mock(ProceedingJoinPoint.class);
        when(authorizationService.hasGranularPermission("DELETE", "expedientes-registro")).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("usuario.consulta", null, java.util.List.of()));

        AccessDeniedException error = assertThrows(AccessDeniedException.class,
            () -> interceptor.checkGranularPermission(
                punto, permiso("eliminar", "DELETE", "expedientes-registro")));

        verify(punto, never()).proceed();
        assertTrue(error.getMessage().contains("usuario.consulta"));
        assertTrue(error.getMessage().contains("DELETE"));
        assertTrue(error.getMessage().contains("expedientes-registro"));
    }

    @Test
    @DisplayName("Sin sesión, el mensaje dice 'anonymous' en vez de fallar al construirlo")
    void sinSesionElMensajeDiceAnonymous() {
        ProceedingJoinPoint punto = mock(ProceedingJoinPoint.class);
        when(authorizationService.hasGranularPermission("VIEW", "expedientes")).thenReturn(false);

        AccessDeniedException error = assertThrows(AccessDeniedException.class,
            () -> interceptor.checkGranularPermission(punto, permiso("ver", "VIEW", "expedientes")));

        assertTrue(error.getMessage().contains("anonymous"));
    }
}
