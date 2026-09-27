package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class SeleccionEtapasProyectosTest {

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final SeleccionEtapasProyectos proyectos = new SeleccionEtapasProyectos(proyectoRepository);

    @Test
    void buscar_proyectoExistente_loDevuelve() {
        Proyecto proyecto = Proyecto.builder().id(1L).build();
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        assertThat(proyectos.buscar(1L)).isSameAs(proyecto);
    }

    @Test
    void buscar_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proyectos.buscar(1L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El proyecto 1 no existe.");
    }

    @Test
    void buscarDeEmergencia_proyectoDeEmergencia_loDevuelve() {
        Proyecto proyecto = Proyecto.builder().id(2L).esProyectoEmergencia(true).build();
        when(proyectoRepository.findById(2L)).thenReturn(Optional.of(proyecto));

        assertThat(proyectos.buscarDeEmergencia(2L)).isSameAs(proyecto);
    }

    @Test
    void buscarDeEmergencia_proyectoNoDeEmergencia_lanzaRecursoNoEncontrado() {
        when(proyectoRepository.findById(3L)).thenReturn(Optional.of(Proyecto.builder().id(3L).build()));

        assertThatThrownBy(() -> proyectos.buscarDeEmergencia(3L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("no está categorizado como de emergencia");
    }

    @Test
    void guardar_persisteElProyecto() {
        Proyecto proyecto = Proyecto.builder().id(4L).build();

        proyectos.guardar(proyecto);

        verify(proyectoRepository).save(proyecto);
    }
}
