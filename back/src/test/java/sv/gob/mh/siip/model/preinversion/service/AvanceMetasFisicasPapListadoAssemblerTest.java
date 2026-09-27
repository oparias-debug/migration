package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;

class AvanceMetasFisicasPapListadoAssemblerTest {

    private static final Long ID_UNIDAD = 5L;

    private final EtapaMetaFisicaPapRepository etapaMetaRepository = mock(EtapaMetaFisicaPapRepository.class);

    private final AvanceMetasFisicasPapListadoAssembler assembler = new AvanceMetasFisicasPapListadoAssembler(
            etapaMetaRepository, new AvanceMetasFisicasPapCalculos(mock(ProgCuatrimestralMetaFisicaRepository.class),
                    mock(AvanceCuatriMetaFisicaRepository.class)));

    @Test
    void filasDeUnidad_metaSinEntregableNiAvance_dejaVaciosEntregableYObservaciones() {
        Proyecto proyecto = Proyecto.builder().id(1L).cup("08040").nombre("Proyecto A").build();
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(10L).proyecto(proyecto)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        when(etapaMetaRepository
            .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(
                        ID_UNIDAD))
                .thenReturn(List.of(EtapaMetaFisicaPap.builder().id(40L).etapaPreinversion(etapa).build()));

        List<EstudioFilaAvanceMetasDto> filas = assembler.filasDeUnidad(ID_UNIDAD, 2028, Cuatrimestre.CUATRIMESTRE_I);

        assertThat(filas).singleElement().satisfies(fila -> {
            assertThat(fila.getCup()).isEqualTo("08040");
            assertThat(fila.getEntregable()).isNull();
            assertThat(fila.getObservaciones()).isNull();
            assertThat(fila.getProgramadoEnElAnio()).isZero();
        });
    }
}
