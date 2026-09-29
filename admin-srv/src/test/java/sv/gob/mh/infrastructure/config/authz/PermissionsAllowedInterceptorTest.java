package sv.gob.mh.infrastructure.config.authz;

import java.lang.annotation.Annotation;

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
 * El interceptor de {@code @PermissionsAllowed}, que es el que usa el ejemplo de la plantilla.
 *
 * <p>Lo que se fija aquí es la semántica de {@code requireAll}, que es donde se equivoca uno:
 * por omisión basta con <b>uno</b> de los permisos declarados, y con {@code requireAll = true}
 * hacen falta <b>todos</b>. Confundirlos abre o cierra el endpoint de par en par sin que nada
 * falle a la vista.</p>
 *
 * <p>Y, como en todo el marco, la denegación ocurre <b>antes</b> de ejecutar el método: si se
 * comprobara después, el efecto ya estaría hecho.</p>
 */
class PermissionsAllowedInterceptorTest {

    private final AuthorizationService authorizationService = mock(AuthorizationService.class);
    private final PermissionsAllowedInterceptor interceptor =
        new PermissionsAllowedInterceptor(authorizationService);

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private Permission permiso(String operation, String path) {
        return new Permission() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Permission.class;
            }

            @Override
            public String code() {
                return "codigo";
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

    private PermissionsAllowed anotacion(boolean requireAll, Permission... permisos) {
        return new PermissionsAllowed() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return PermissionsAllowed.class;
            }

            @Override
            public Permission[] value() {
                return permisos;
            }

            @Override
            public boolean requireAll() {
                return requireAll;
            }
        };
    }

    private ProceedingJoinPoint puntoQueDevuelve(String resultado) throws Throwable {
        ProceedingJoinPoint punto = mock(ProceedingJoinPoint.class);
        when(punto.proceed()).thenReturn(resultado);
        return punto;
    }

    @Test
    @DisplayName("Sin permisos declarados, no hay nada que comprobar: se ejecuta")
    void sinPermisosDeclaradosSeEjecuta() throws Throwable {
        ProceedingJoinPoint punto = puntoQueDevuelve("resultado");

        assertEquals("resultado", interceptor.checkMultiplePermissions(punto, anotacion(false)));
        verify(punto).proceed();
    }

    @Test
    @DisplayName("Por omisión basta con uno: el primero que conceda deja pasar")
    void bastaConUno() throws Throwable {
        ProceedingJoinPoint punto = puntoQueDevuelve("resultado");
        when(authorizationService.hasGranularPermission("VIEW", "expedientes-consulta")).thenReturn(false);
        when(authorizationService.hasGranularPermission("EXPORT", "expedientes-reporte")).thenReturn(true);

        Object resultado = interceptor.checkMultiplePermissions(punto,
            anotacion(false, permiso("VIEW", "expedientes-consulta"),
                permiso("EXPORT", "expedientes-reporte")));

        assertEquals("resultado", resultado);
    }

    @Test
    @DisplayName("Si ninguno concede, se deniega antes de ejecutar")
    void ningunoConcedeDeniega() throws Throwable {
        ProceedingJoinPoint punto = mock(ProceedingJoinPoint.class);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("usuario.consulta", null, java.util.List.of()));

        AccessDeniedException error = assertThrows(AccessDeniedException.class,
            () -> interceptor.checkMultiplePermissions(punto,
                anotacion(false, permiso("DELETE", "expedientes-registro"))));

        verify(punto, never()).proceed();
        assertTrue(error.getMessage().contains("usuario.consulta"));
    }

    @Test
    @DisplayName("Con requireAll, hacen falta TODOS: uno que falte deniega")
    void conRequireAllUnoQueFalteDeniega() throws Throwable {
        ProceedingJoinPoint punto = mock(ProceedingJoinPoint.class);
        when(authorizationService.hasGranularPermission("VIEW", "expedientes-consulta")).thenReturn(true);
        when(authorizationService.hasGranularPermission("DELETE", "expedientes-registro")).thenReturn(false);

        assertThrows(AccessDeniedException.class,
            () -> interceptor.checkMultiplePermissions(punto,
                anotacion(true, permiso("VIEW", "expedientes-consulta"),
                    permiso("DELETE", "expedientes-registro"))));

        verify(punto, never()).proceed();
    }

    @Test
    @DisplayName("Con requireAll y todos concedidos, se ejecuta")
    void conRequireAllYTodosConcedidos() throws Throwable {
        ProceedingJoinPoint punto = puntoQueDevuelve("resultado");
        when(authorizationService.hasGranularPermission("VIEW", "expedientes-consulta")).thenReturn(true);
        when(authorizationService.hasGranularPermission("DELETE", "expedientes-registro")).thenReturn(true);

        Object resultado = interceptor.checkMultiplePermissions(punto,
            anotacion(true, permiso("VIEW", "expedientes-consulta"),
                permiso("DELETE", "expedientes-registro")));

        assertEquals("resultado", resultado);
    }

    @Test
    @DisplayName("Sin sesión, el mensaje dice 'anonymous' en vez de fallar al construirlo")
    void sinSesionDiceAnonymous() {
        ProceedingJoinPoint punto = mock(ProceedingJoinPoint.class);

        AccessDeniedException error = assertThrows(AccessDeniedException.class,
            () -> interceptor.checkMultiplePermissions(punto,
                anotacion(false, permiso("VIEW", "expedientes"))));

        assertTrue(error.getMessage().contains("anonymous"));
    }
}
