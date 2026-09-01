package sv.gob.mh.siip.config.devseed;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

class UsuarioDevSeederTest {

    private InstitucionRepository institucionRepository;
    private UnidadEjecutoraRepository unidadEjecutoraRepository;
    private UsuarioRepository usuarioRepository;
    private UsuarioDevSeeder seeder;

    @BeforeEach
    void setUp() {
        institucionRepository = mock(InstitucionRepository.class);
        unidadEjecutoraRepository = mock(UnidadEjecutoraRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        seeder = new UsuarioDevSeeder(institucionRepository, unidadEjecutoraRepository, usuarioRepository);

        when(institucionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(unidadEjecutoraRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void seed_creaInstitucionUnidadEjecutoraYLosTresUsuarios_cuandoNoExistenAun() {
        when(institucionRepository.findByCodigo("MH-DGICP")).thenReturn(Optional.empty());
        when(unidadEjecutoraRepository.findByCodigo("URP-01")).thenReturn(Optional.empty());
        when(usuarioRepository.findByNombreUsuario(anyString())).thenReturn(Optional.empty());

        seeder.seed();

        verify(institucionRepository).save(argThat((Institucion i) -> "MH-DGICP".equals(i.getCodigo())));
        verify(unidadEjecutoraRepository).save(argThat((UnidadEjecutora u) -> "URP-01".equals(u.getCodigo())));
        verify(usuarioRepository).save(argThat((Usuario u) -> "tecnico.urp".equals(u.getNombreUsuario())
                && u.getRol() == RolUsuario.TECNICO_URP && u.getUnidadEjecutora() != null));
        verify(usuarioRepository).save(argThat((Usuario u) -> "tecnico.pre".equals(u.getNombreUsuario())
                && u.getRol() == RolUsuario.TECNICO_PRE && u.getUnidadEjecutora() == null));
        verify(usuarioRepository).save(argThat((Usuario u) -> "admin".equals(u.getNombreUsuario())
                && u.getRol() == RolUsuario.ADMINISTRADOR && u.getInstitucion() == null));
    }

    @Test
    void seed_esIdempotente_cuandoInstitucionUnidadEjecutoraYUsuariosYaExisten() {
        Institucion institucion = Institucion.builder().id(1L).codigo("MH-DGICP").nombre("Existente").activo(true)
                .build();
        UnidadEjecutora unidadEjecutora = UnidadEjecutora.builder().id(2L).institucion(institucion).codigo("URP-01")
                .nombre("Existente").activo(true).build();
        when(institucionRepository.findByCodigo("MH-DGICP")).thenReturn(Optional.of(institucion));
        when(unidadEjecutoraRepository.findByCodigo("URP-01")).thenReturn(Optional.of(unidadEjecutora));
        when(usuarioRepository.findByNombreUsuario(anyString())).thenReturn(Optional.of(mock(Usuario.class)));

        seeder.seed();

        verify(institucionRepository, never()).save(any());
        verify(unidadEjecutoraRepository, never()).save(any());
        verify(usuarioRepository, never()).save(any());
    }
}
