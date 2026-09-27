package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.CalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class AvancePapConsultasTest {

    private static final int ANIO = 2028;

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final CalendarioEventoRepository calendarioEventoRepository = mock(CalendarioEventoRepository.class);

    private final AvancePapConsultas consultas = new AvancePapConsultas(proyectoRepository,
            etapaPreinversionRepository, calendarioEventoRepository);

    private void eventoEjecucionPap(EstadoCalendarioEvento estado) {
        when(calendarioEventoRepository.findByTipoEventoAndAnioAndCuatrimestre(TipoEventoCalendario.EJECUCION_PAP,
                ANIO, Cuatrimestre.CUATRIMESTRE_II.numero()))
                .thenReturn(Optional.of(CalendarioEvento.builder().estado(estado).build()));
    }

    @Test
    void buscarEstudio_proyectoSinEtapasDePreinversion_noEsUnEstudio() {
        when(proyectoRepository.findByCup("08040")).thenReturn(Optional.of(Proyecto.builder().id(1L).build()));
        when(etapaPreinversionRepository.findByProyectoId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> consultas.buscarEstudio("08040"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void etapasOrdenadas_ordenaSegunLaRutaDePreinversion() {
        EtapaPreinversion factibilidad = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.FACTIBILIDAD)
                .build();
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        when(etapaPreinversionRepository.findByProyectoId(1L)).thenReturn(List.of(factibilidad, perfil));

        assertThat(consultas.etapasOrdenadas(1L)).containsExactly(perfil, factibilidad);
    }

    @Test
    void verificarPeriodoAbierto_eventoCerrado_rechazaElIngreso() {
        eventoEjecucionPap(EstadoCalendarioEvento.CERRADO);

        assertThatThrownBy(() -> consultas.verificarPeriodoAbierto(ANIO, Cuatrimestre.CUATRIMESTRE_II))
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting("codigo").isEqualTo("PERIODO_CERRADO");
    }

    @Test
    void verificarPeriodoAbierto_eventoAbierto_permiteElIngreso() {
        eventoEjecucionPap(EstadoCalendarioEvento.ABIERTO);

        assertThatCode(() -> consultas.verificarPeriodoAbierto(ANIO, Cuatrimestre.CUATRIMESTRE_II))
                .doesNotThrowAnyException();
    }
}
