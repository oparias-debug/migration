package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class AvanceFinancieroPapDetalleAssemblerTest {

    private static final int ANIO = 2028;

    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository =
            mock(FuenteFinanciamientoEtapaPapRepository.class);
    private final AvanceFinancieroCuatrimestralRepository avanceRepository =
            mock(AvanceFinancieroCuatrimestralRepository.class);

    private final AvanceFinancieroPapDetalleAssembler assembler = new AvanceFinancieroPapDetalleAssembler(
            new AvancePapConsultas(mock(ProyectoRepository.class), etapaPreinversionRepository,
                    mock(CalendarioEventoRepository.class)),
            new AvanceFinancieroPapCalculos(fuenteRepository, mock(ProgCuatrimestralFinancieraRepository.class),
                    avanceRepository));

    @Test
    void construirEstudioDto_omiteEtapasFinalizadasYSinCostoNoCalculaLaAlertaDeExceso() {
        Proyecto proyecto = Proyecto.builder().id(1L).cup("08040").nombre("Proyecto A").build();
        EtapaPreinversion sinCosto = EtapaPreinversion.builder().id(12L)
                .tipoEtapa(TipoEtapaPreinversion.PREFACTIBILIDAD).build();
        EtapaPreinversion finalizada = EtapaPreinversion.builder().id(11L)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).costo(100D).build();
        when(etapaPreinversionRepository.findByProyectoId(1L)).thenReturn(List.of(sinCosto, finalizada));
        when(fuenteRepository.findByEtapaPreinversionId(11L))
                .thenReturn(List.of(FuenteFinanciamientoEtapaPap.builder().id(21L).build()));
        when(avanceRepository.findByProgramacion_Fuente_Id(21L)).thenReturn(List.of(
                AvanceFinancieroPapCalculosTest.avance(ANIO - 1, Cuatrimestre.CUATRIMESTRE_III, "100")));
        when(fuenteRepository.findByEtapaPreinversionId(12L))
                .thenReturn(List.of(FuenteFinanciamientoEtapaPap.builder().id(22L).build()));

        AvanceEstudioDto estudio = assembler.construirEstudioDto(proyecto, ANIO, Cuatrimestre.CUATRIMESTRE_I);

        assertThat(estudio.getEtapas()).hasSize(1);
        EtapaAvanceDto etapa = estudio.getEtapas().getFirst();
        assertThat(etapa.getEtapa()).isEqualTo(NombreEtapaDto.PREFACTIBILIDAD);
        assertThat(etapa.getCostoEtapa()).isNull();
        assertThat(etapa.getEjecutadoAniosAnteriores()).isNull();
        FilaAvanceFuenteDto fila = etapa.getFuentes().getFirst();
        assertThat(fila.getIdFuente()).isEqualTo(22L);
        assertThat(fila.getAlertaExcesoProgramado()).isNull();
        assertThat(fila.getAvancesCuatrimestresAnteriores()).isEmpty();
    }
}
