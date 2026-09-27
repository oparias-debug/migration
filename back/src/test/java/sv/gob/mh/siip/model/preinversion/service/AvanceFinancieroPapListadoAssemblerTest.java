package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

class AvanceFinancieroPapListadoAssemblerTest {

    private static final int ANIO = 2028;
    private static final Long ID_UNIDAD = 5L;

    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository =
            mock(FuenteFinanciamientoEtapaPapRepository.class);
    private final ProgCuatrimestralFinancieraRepository progRepository =
            mock(ProgCuatrimestralFinancieraRepository.class);
    private final AvanceFinancieroCuatrimestralRepository avanceRepository =
            mock(AvanceFinancieroCuatrimestralRepository.class);

    private final AvanceFinancieroPapListadoAssembler assembler = new AvanceFinancieroPapListadoAssembler(
            new AvanceFinancieroPapCalculos(fuenteRepository, progRepository, avanceRepository));

    @Test
    void filasActivasEnAnio_omiteEtapasFinalizadasYDejaVaciosLosPorcentajesSinProgramacion() {
        Proyecto proyecto = Proyecto.builder().id(1L).cup("08040").nombre("Proyecto A").build();
        EtapaPreinversion etapaFinalizada = EtapaPreinversion.builder().id(11L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).costo(100D).build();
        EtapaPreinversion etapaSinCosto = EtapaPreinversion.builder().id(12L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PREFACTIBILIDAD).build();
        FuenteFinanciamientoEtapaPap fuenteFinalizada = FuenteFinanciamientoEtapaPap.builder().id(21L)
                .etapaPreinversion(etapaFinalizada).build();
        FuenteFinanciamientoEtapaPap fuenteSinProgramacion = FuenteFinanciamientoEtapaPap.builder().id(22L)
                .etapaPreinversion(etapaSinCosto).build();
        when(fuenteRepository.buscarActivasEnAnio(eq(ID_UNIDAD), eq(ANIO), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(fuenteFinalizada, fuenteSinProgramacion)));
        when(fuenteRepository.findByEtapaPreinversionId(11L)).thenReturn(List.of(fuenteFinalizada));
        when(avanceRepository.findByProgramacion_Fuente_Id(21L)).thenReturn(List.of(
                AvanceFinancieroPapCalculosTest.avance(ANIO - 1, Cuatrimestre.CUATRIMESTRE_III, "100")));

        List<EstudioFilaAvancePAPDto> filas = assembler.filasActivasEnAnio(ID_UNIDAD, ANIO,
                Cuatrimestre.CUATRIMESTRE_II);

        assertThat(filas).hasSize(1);
        EstudioFilaAvancePAPDto fila = filas.getFirst();
        assertThat(fila.getEtapa()).isEqualTo(NombreEtapaDto.PREFACTIBILIDAD);
        assertThat(fila.getAvanceAnualProgramado()).isZero();
        assertThat(fila.getAvanceAnualEjecutadoPorcentaje()).isNull();
        assertThat(fila.getAvanceAlCuatrimestreEjecutadoPorcentaje()).isNull();
        assertThat(fila.getAvanceDelCuatrimestrePorcentaje()).isNull();
        assertThat(fila.getAlertaExcesoProgramado()).isNull();
        assertThat(fila.getFuenteFinanciamiento()).isNull();
    }
}
