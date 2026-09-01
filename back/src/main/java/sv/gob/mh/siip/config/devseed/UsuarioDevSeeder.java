package sv.gob.mh.siip.config.devseed;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** Institución, unidad ejecutora y usuarios de prueba (uno por rol) para desarrollo local. */
@Component
@Profile("dev")
@Order(10)
public class UsuarioDevSeeder implements DevSeeder {

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioDevSeeder(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void seed() {
        Institucion institucion = institucionRepository.findByCodigo("MH-DGICP")
                .orElseGet(() -> institucionRepository.save(Institucion.builder()
                        .codigo("MH-DGICP")
                        .nombre("Dirección General de Inversión y Crédito Público")
                        .activo(true)
                        .build()));

        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.findByCodigo("URP-01")
                .orElseGet(() -> unidadEjecutoraRepository.save(UnidadEjecutora.builder()
                        .institucion(institucion)
                        .codigo("URP-01")
                        .nombre("Unidad Responsable de Proyecto (prueba)")
                        .activo(true)
                        .build()));

        crearUsuarioSiNoExiste("tecnico.urp", "Técnico URP (prueba)", "tecnico.urp@example.com",
                RolUsuario.TECNICO_URP, unidadEjecutora, institucion);
        crearUsuarioSiNoExiste("tecnico.pre", "Técnico PRE (prueba)", "tecnico.pre@example.com",
                RolUsuario.TECNICO_PRE, null, null);
        crearUsuarioSiNoExiste("admin", "Administrador del Sistema (prueba)", "admin@example.com",
                RolUsuario.ADMINISTRADOR, null, null);
    }

    private void crearUsuarioSiNoExiste(String nombreUsuario, String nombreCompleto, String correo,
            RolUsuario rol, UnidadEjecutora unidadEjecutora, Institucion institucion) {
        if (usuarioRepository.findByNombreUsuario(nombreUsuario).isPresent()) {
            return;
        }
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto(nombreCompleto)
                .correo(correo)
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());
    }
}
