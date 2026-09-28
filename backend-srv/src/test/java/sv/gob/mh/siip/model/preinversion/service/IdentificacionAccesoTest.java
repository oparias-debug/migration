package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link IdentificacionAcceso} (CU-PRE-04, RNA-1 a RNA-3). */
class IdentificacionAccesoTest {

    private static final Long ID_PROYECTO = 1L;
    private static final Long ID_UNIDAD = 5L;

    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final IdentificacionAcceso acceso = new IdentificacionAcceso(actorContexto, proyectoRepository);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(ID_UNIDAD).build()).build();

    private static Usuario actor(RolUsuario rol, Long idUnidad) {
        UnidadEjecutora unidad = idUnidad == null ? null : UnidadEjecutora.builder().id(idUnidad).build();
        return Usuario.builder().rol(rol).unidadEjecutora(unidad).build();
    }

    @Test
    void exigirActor_devuelveElActorAutenticado() {
        Usuario tecnico = actor(RolUsuario.TECNICO_PRE, null);
        when(actorContexto.exigir()).thenReturn(tecnico);

        assertThat(acceso.exigirActor()).isSameAs(tecnico);
    }

    @Test
    void proyectoVisible_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());
        Usuario tecnico = actor(RolUsuario.TECNICO_URP, null);

        assertThatThrownBy(() -> acceso.proyectoVisible(tecnico, ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El proyecto 1 no existe.");
    }

    @Test
    void proyectoVisible_actorSinUnidad_devuelveProyecto() {
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThat(acceso.proyectoVisible(actor(RolUsuario.TECNICO_PRE, null), ID_PROYECTO)).isSameAs(proyecto);
    }

    @Test
    void proyectoVisible_actorDeLaMismaUnidad_devuelveProyecto() {
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThat(acceso.proyectoVisible(actor(RolUsuario.TECNICO_URP, ID_UNIDAD), ID_PROYECTO))
                .isSameAs(proyecto);
    }

    @Test
    void proyectoVisible_actorDeOtraUnidad_lanzaAccesoDenegado() {
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        Usuario tecnico = actor(RolUsuario.TECNICO_URP, 99L);

        assertThatThrownBy(() -> acceso.proyectoVisible(tecnico, ID_PROYECTO))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void proyectoConsultable_usaElActorAutenticado() {
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_PRE, 99L));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> acceso.proyectoConsultable(ID_PROYECTO))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void proyectoEditable_tecnicoUrpConFormulacionHabilitada_devuelveProyecto() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actor(RolUsuario.TECNICO_URP, ID_UNIDAD));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThat(acceso.proyectoEditable(ID_PROYECTO)).isSameAs(proyecto);
    }

    @Test
    void proyectoEditable_proyectoEnViabilidad_lanzaFormulacionBloqueada() {
        proyecto.setEstado(EstadoProyecto.EN_VIABILIDAD);
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actor(RolUsuario.TECNICO_URP, null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> acceso.proyectoEditable(ID_PROYECTO))
                .isInstanceOfSatisfying(ConflictoEstadoException.class, ex -> assertThat(ex.getCodigo())
                        .isEqualTo(EdicionFormulacion.CODIGO_FORMULACION_BLOQUEADA));
    }
}
