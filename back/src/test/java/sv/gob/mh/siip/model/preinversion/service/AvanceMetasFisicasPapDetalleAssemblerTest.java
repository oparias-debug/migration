package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class AvanceMetasFisicasPapDetalleAssemblerTest {

    private static final int ANIO = 2028;

    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final EtapaMetaFisicaPapRepository etapaMetaRepository = mock(EtapaMetaFisicaPapRepository.class);
    private final AvanceCuatriMetaFisicaRepository avanceRepository = mock(AvanceCuatriMetaFisicaRepository.class);

    private final AvanceMetasFisicasPapDetalleAssembler assembler = new AvanceMetasFisicasPapDetalleAssembler(
            new AvancePapConsultas(mock(ProyectoRepository.class), etapaPreinversionRepository,
                    mock(CalendarioEventoRepository.class)),
            etapaMetaRepository,
            new AvanceMetasFisicasPapCalculos(mock(ProgCuatrimestralMetaFisicaRepository.class), avanceRepository));

    private static AvanceCuatriMetaFisica avance(int anio, Cuatrimestre cuatrimestre, String porcentaje) {
        return AvanceCuatriMetaFisica.builder()
                .programacionMeta(ProgCuatrimestralMetaFisica.builder().anio(anio).build())
                .cuatrimestre(cuatrimestre)
                .avanceCuatrimestre(new BigDecimal(porcentaje))
                .build();
    }

    @Test
    void construirEstudioDto_etapaSinMetaFisica_quedaEnCeroYSinAvancesAnteriores() {
        Proyecto proyecto = Proyecto.builder().id(1L).cup("08040").nombre("Proyecto A").build();
        EtapaPreinversion perfil = EtapaPreinversion.builder().id(10L).tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        EtapaPreinversion prefactibilidad = EtapaPreinversion.builder().id(11L)
                .tipoEtapa(TipoEtapaPreinversion.PREFACTIBILIDAD).build();
        when(etapaPreinversionRepository.findByProyectoId(1L)).thenReturn(List.of(prefactibilidad, perfil));
        when(etapaMetaRepository.findByEtapaPreinversionId(11L)).thenReturn(Optional.of(EtapaMetaFisicaPap.builder()
                .id(41L).entregable(Entregable.ESTUDIO_DE_PREFACTIBILIDAD).build()));
        when(avanceRepository.findByProgramacionMeta_EtapaMetaFisica_Id(41L)).thenReturn(List.of(
                avance(ANIO, Cuatrimestre.CUATRIMESTRE_III, "30"),
                avance(ANIO, Cuatrimestre.CUATRIMESTRE_I, "20")));

        AvanceMetasEstudioDto estudio = assembler.construirEstudioDto(proyecto, ANIO, Cuatrimestre.CUATRIMESTRE_II);

        assertThat(estudio.getEtapas()).hasSize(2);
        EtapaAvanceMetasDto sinMeta = estudio.getEtapas().getFirst();
        assertThat(sinMeta.getEntregable()).isNull();
        assertThat(sinMeta.getEstado()).isNull();
        assertThat(sinMeta.getTotalMetaEjecutada()).isZero();
        assertThat(sinMeta.getAvancesCuatrimestresAnteriores()).isEmpty();

        EtapaAvanceMetasDto conMeta = estudio.getEtapas().get(1);
        assertThat(conMeta.getEntregable()).isEqualTo("ESTUDIO_DE_PREFACTIBILIDAD");
        // El avance del Cuatrimestre III es posterior al consultado: no suma al ejecutado del año.
        assertThat(conMeta.getEjecutadoEnElAnio()).isEqualTo(20D);
        assertThat(conMeta.getAvancesCuatrimestresAnteriores()).singleElement()
                .satisfies(anterior -> assertThat(anterior.getPeriodo()).isEqualTo(CuatrimestreDto.CUATRIMESTRE_I));
    }
}
