package sv.gob.mh.siip.security;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * Resuelve el actor autenticado a partir del JWT de Keycloak que valida {@link SecurityConfig}:
 * su "preferred_username" (o el subject, si el token no lo trae) es el NOMBRE_USUARIO de USUARIO.
 * El rol de negocio sale de USUARIO.ROL, no de los roles de Keycloak.
 */
@Component
public class ActorContexto {

    private static final String CLAIM_USUARIO = "preferred_username";

    private final UsuarioRepository usuarioRepository;

    public ActorContexto(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Vacio si no hay un JWT autenticado, o el usuario no existe/esta inactivo. */
    public Optional<Usuario> actual() {
        return nombreUsuarioActual()
                .flatMap(usuarioRepository::findByNombreUsuario)
                .filter(usuario -> Boolean.TRUE.equals(usuario.getActivo()));
    }

    /**
     * Solo el nombre de usuario del token, sin ir a base de datos. Es lo unico que necesita
     * {@link AuditorAwareImpl}: consultar aqui el Usuario via repositorio, dentro de un callback
     * de auditoria de Hibernate, dispara un auto-flush que re-entra en el mismo callback
     * (StackOverflowError).
     */
    public Optional<String> nombreUsuarioActual() {
        String nombreUsuario = nombreUsuarioDelToken();
        return (nombreUsuario == null || nombreUsuario.isBlank()) ? Optional.empty() : Optional.of(nombreUsuario);
    }

    /** Como {@link #actual()}, pero exige que exista un actor resuelto (401 si no). */
    public Usuario exigir() {
        return actual().orElseThrow(() -> new NoAutenticadoException(
                "No se pudo identificar al usuario autenticado (token sin " + CLAIM_USUARIO + ")."));
    }

    /** Exige un actor autenticado con el rol indicado (403 si su rol no califica). */
    public Usuario exigirRol(RolUsuario rolPermitido) {
        return exigirRol(EnumSet.of(rolPermitido));
    }

    /** Exige un actor autenticado con alguno de los dos roles indicados (403 si su rol no califica). */
    public Usuario exigirRol(RolUsuario rolPermitido, RolUsuario otroRolPermitido) {
        return exigirRol(EnumSet.of(rolPermitido, otroRolPermitido));
    }

    /** Exige un actor autenticado con alguno de los roles permitidos (403 si su rol no califica). */
    public Usuario exigirRol(Set<RolUsuario> rolesPermitidos) {
        var usuario = exigir();
        if (rolesPermitidos.contains(usuario.getRol())) {
            return usuario;
        }
        throw new AccesoDenegadoException(
                "El rol " + usuario.getRol() + " no tiene permiso para realizar esta accion.");
    }

    /**
     * "Usuarios Internos" / "usuarios centrales" (RN09 de CU-PRE-17/18/20/21, RQ-C-03): decisión de
     * negocio, cualquier usuario cuyo rol no sea Técnico URP. Controla la visibilidad de precios
     * ajustados, FC y valor de rescate ajustado.
     */
    public static boolean esUsuarioInterno(Usuario usuario) {
        return usuario.getRol() != RolUsuario.TECNICO_URP;
    }

    private static String nombreUsuarioDelToken() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        var preferredUsername = jwt.getClaimAsString(CLAIM_USUARIO);
        return preferredUsername != null && !preferredUsername.isBlank() ? preferredUsername : jwt.getSubject();
    }

}
