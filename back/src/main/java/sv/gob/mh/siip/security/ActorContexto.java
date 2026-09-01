package sv.gob.mh.siip.security;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * Resuelve el actor autenticado a partir del header X-Usuario, que el api-gateway agrega con el
 * "preferred_username" del JWT de Keycloak ya validado. back no valida JWT por su cuenta: confia
 * en el gateway (ver nota en docker-compose.yml sobre back "sin seguridad propia").
 */
@Component
public class ActorContexto {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final UsuarioRepository usuarioRepository;

    public ActorContexto(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Vacio si no hay request HTTP activa, no llega el header, o el usuario no existe/esta inactivo. */
    public Optional<Usuario> actual() {
        return nombreUsuarioActual()
                .flatMap(usuarioRepository::findByNombreUsuario)
                .filter(usuario -> Boolean.TRUE.equals(usuario.getActivo()));
    }

    /**
     * Solo el nombre de usuario del header, sin ir a base de datos. Es lo unico que necesita
     * {@link AuditorAwareImpl}: consultar aqui el Usuario via repositorio, dentro de un callback
     * de auditoria de Hibernate, dispara un auto-flush que re-entra en el mismo callback
     * (StackOverflowError).
     */
    public Optional<String> nombreUsuarioActual() {
        String nombreUsuario = headerUsuarioActual();
        return (nombreUsuario == null || nombreUsuario.isBlank()) ? Optional.empty() : Optional.of(nombreUsuario);
    }

    /** Como {@link #actual()}, pero exige que exista un actor resuelto (401 si no). */
    public Usuario exigir() {
        return actual().orElseThrow(() -> new NoAutenticadoException(
                "No se pudo identificar al usuario autenticado (header " + HEADER_USUARIO + ")."));
    }

    /** Exige un actor autenticado con alguno de los roles permitidos (403 si su rol no califica). */
    public Usuario exigirRol(RolUsuario... rolesPermitidos) {
        Usuario usuario = exigir();
        for (RolUsuario rol : rolesPermitidos) {
            if (rol == usuario.getRol()) {
                return usuario;
            }
        }
        throw new AccesoDenegadoException(
                "El rol " + usuario.getRol() + " no tiene permiso para realizar esta accion.");
    }

    private String headerUsuarioActual() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            return request.getHeader(HEADER_USUARIO);
        } catch (IllegalStateException _) {
            return null;
        }
    }
}
