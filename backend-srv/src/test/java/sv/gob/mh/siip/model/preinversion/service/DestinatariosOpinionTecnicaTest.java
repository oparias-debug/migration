package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;

/** Pruebas unitarias de {@link DestinatariosOpinionTecnica} (CU-PRE-26, RN07). */
class DestinatariosOpinionTecnicaTest {

    private final Usuario urp = Usuario.builder().id(1L).build();
    private final Usuario viabilizador = Usuario.builder().id(2L).build();
    private final Usuario tecnicoPre = Usuario.builder().id(3L).build();
    private final Usuario coordinador = Usuario.builder().id(4L).build();

    private UsuarioRepository usuarios;
    private RevisionViabilidadRepository revisiones;
    private DestinatariosOpinionTecnica destinatarios;
    private Proyecto proyecto;

    @BeforeEach
    void setUp() {
        usuarios = mock(UsuarioRepository.class);
        revisiones = mock(RevisionViabilidadRepository.class);
        destinatarios = new DestinatariosOpinionTecnica(usuarios, revisiones);
        proyecto = Proyecto.builder().id(9L).unidadEjecutora(UnidadEjecutora.builder().id(5L).build()).build();
        when(usuarios.findByRolAndUnidadEjecutora_IdAndActivoTrue(RolUsuario.TECNICO_URP, 5L)).thenReturn(List.of(urp));
        when(usuarios.findByRolAndActivoTrue(RolUsuario.VIABILIZADOR)).thenReturn(List.of(viabilizador));
        when(usuarios.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE)).thenReturn(List.of(tecnicoPre));
        when(usuarios.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE)).thenReturn(List.of(coordinador));
    }

    @Test
    void todosLosActoresSinRepetir() {
        OpinionTecnica gestion = OpinionTecnica.builder().tecnicoResponsable(tecnicoPre).build();

        assertThat(destinatarios.todos(proyecto, gestion)).containsExactly(urp, viabilizador, tecnicoPre, coordinador);
    }

    @Test
    void sinTecnicoAsignadoSeAvisaATodosLosTecnicosPre() {
        assertThat(destinatarios.tecnicosPre(OpinionTecnica.builder().build())).containsExactly(tecnicoPre);
        assertThat(destinatarios.dgicp(OpinionTecnica.builder().build())).containsExactly(tecnicoPre, coordinador);
    }

    @Test
    void seAvisaAlViabilizadorQueRevisoElProyecto() {
        Usuario delProyecto = Usuario.builder().id(6L).rol(RolUsuario.VIABILIZADOR).activo(true).build();
        when(revisiones.findByProyectoIdOrderByNumeroAsc(9L)).thenReturn(List.of(
                RevisionViabilidad.builder().numero(1).viabilizador(delProyecto).build(),
                RevisionViabilidad.builder().numero(2).build()));

        assertThat(destinatarios.viabilizadores(proyecto)).containsExactly(delProyecto);
        assertThat(destinatarios.institucionYViabilizadores(proyecto)).containsExactly(urp, delProyecto);
    }

    @Test
    void siElViabilizadorDelProyectoNoEstaActivoSeAvisaATodos() {
        Usuario inactivo = Usuario.builder().id(6L).rol(RolUsuario.VIABILIZADOR).activo(false).build();
        when(revisiones.findByProyectoIdOrderByNumeroAsc(9L)).thenReturn(List.of(
                RevisionViabilidad.builder().numero(1).viabilizador(inactivo).build()));

        assertThat(destinatarios.viabilizadores(proyecto)).containsExactly(viabilizador);
    }

    @Test
    void unirConservaElPrimeroDeCadaUsuarioYElOrden() {
        Usuario repetido = Usuario.builder().id(1L).correo("otro@test").build();

        assertThat(DestinatariosOpinionTecnica.unir(List.of(urp, viabilizador), List.of(repetido, tecnicoPre)))
                .containsExactly(urp, viabilizador, tecnicoPre)
                .first().isSameAs(urp);
    }

    @Test
    void unProyectoSinUnidadEjecutoraNoTieneTecnicosUrp() {
        assertThat(destinatarios.tecnicosUrp(Proyecto.builder().id(9L).build())).isEmpty();
    }
}
