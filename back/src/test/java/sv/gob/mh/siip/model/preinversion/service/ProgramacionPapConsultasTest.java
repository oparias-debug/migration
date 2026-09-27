package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class ProgramacionPapConsultasTest {

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);

    private final ProgramacionPapConsultas consultas =
            new ProgramacionPapConsultas(proyectoRepository, etapaPreinversionRepository);

    @Test
    void buscarEstudio_proyectoSinEtapasDePreinversion_noEsUnEstudio() {
        when(proyectoRepository.findByCup("08040")).thenReturn(Optional.of(Proyecto.builder().id(1L).build()));
        when(etapaPreinversionRepository.findByProyectoId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> consultas.buscarEstudio("08040"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El estudio (CUP + año) no existe.");
    }

    @Test
    void buscarEstudio_proyectoConEtapas_devuelveElProyecto() {
        Proyecto proyecto = Proyecto.builder().id(1L).build();
        when(proyectoRepository.findByCup("08040")).thenReturn(Optional.of(proyecto));
        when(etapaPreinversionRepository.findByProyectoId(1L))
                .thenReturn(List.of(EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build()));

        assertThat(consultas.buscarEstudio("08040")).isSameAs(proyecto);
    }

    @Test
    void buscarProyecto_cupInexistente_lanzaRecursoNoEncontrado() {
        when(proyectoRepository.findByCup("99999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultas.buscarProyecto("99999"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El CUP indicado no existe.");
    }
}
