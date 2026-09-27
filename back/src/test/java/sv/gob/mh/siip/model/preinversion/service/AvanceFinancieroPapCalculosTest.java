package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

class AvanceFinancieroPapCalculosTest {

    private static final int ANIO = 2028;

    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository =
            mock(FuenteFinanciamientoEtapaPapRepository.class);
    private final ProgCuatrimestralFinancieraRepository progRepository =
            mock(ProgCuatrimestralFinancieraRepository.class);
    private final AvanceFinancieroCuatrimestralRepository avanceRepository =
            mock(AvanceFinancieroCuatrimestralRepository.class);

    private final AvanceFinancieroPapCalculos calculos =
            new AvanceFinancieroPapCalculos(fuenteRepository, progRepository, avanceRepository);

    static AvanceFinancieroCuatrimestral avance(int anio, Cuatrimestre cuatrimestre, String monto) {
        return AvanceFinancieroCuatrimestral.builder()
                .programacion(ProgCuatrimestralFinanciera.builder().anio(anio).build())
                .cuatrimestre(cuatrimestre)
                .montoEjecutado(new BigDecimal(monto))
                .build();
    }

    @Test
    void etapaFinalizada_sinCostoOCostoCero_noConsultaLasFuentes() {
        assertThat(calculos.etapaFinalizada(EtapaPreinversion.builder().id(1L).build(), ANIO)).isFalse();
        assertThat(calculos.etapaFinalizada(EtapaPreinversion.builder().id(1L).costo(0D).build(), ANIO)).isFalse();

        verifyNoInteractions(fuenteRepository);
    }

    @Test
    void etapaFinalizada_comparaLoEjecutadoEnAniosAnterioresContraElCosto() {
        when(fuenteRepository.findByEtapaPreinversionId(1L))
                .thenReturn(List.of(FuenteFinanciamientoEtapaPap.builder().id(10L).build()));
        when(avanceRepository.findByProgramacion_Fuente_Id(10L)).thenReturn(List.of(
                avance(ANIO - 1, Cuatrimestre.CUATRIMESTRE_III, "100"),
                avance(ANIO, Cuatrimestre.CUATRIMESTRE_I, "900")));

        assertThat(calculos.etapaFinalizada(EtapaPreinversion.builder().id(1L).costo(100D).build(), ANIO))
                .isTrue();
        assertThat(calculos.etapaFinalizada(EtapaPreinversion.builder().id(1L).costo(150D).build(), ANIO))
                .isFalse();
    }

    @Test
    void ejecutadoEnAnioHastaPeriodo_excluyeCuatrimestresPosterioresYOtrosAnios() {
        when(avanceRepository.findByProgramacion_Fuente_Id(10L)).thenReturn(List.of(
                avance(ANIO, Cuatrimestre.CUATRIMESTRE_I, "10"),
                avance(ANIO, Cuatrimestre.CUATRIMESTRE_II, "20"),
                avance(ANIO, Cuatrimestre.CUATRIMESTRE_III, "40"),
                avance(ANIO - 1, Cuatrimestre.CUATRIMESTRE_I, "80")));

        assertThat(calculos.ejecutadoEnAnioHastaPeriodo(10L, ANIO, Cuatrimestre.CUATRIMESTRE_II))
                .isEqualByComparingTo("30");
    }

    @Test
    void fuenteFinanciamientoDto_sinFuenteRegistrada_esNulo() {
        assertThat(AvanceFinancieroPapCalculos.fuenteFinanciamientoDto(FuenteFinanciamientoEtapaPap.builder().build()))
                .isNull();
        assertThat(AvanceFinancieroPapCalculos.fuenteFinanciamientoDto(FuenteFinanciamientoEtapaPap.builder()
                .fuenteFinanciamiento(FuenteFinanciamiento.FONDO_GENERAL).build()))
                .isEqualTo(FuenteFinanciamientoDto.FONDO_GENERAL);
    }
}
