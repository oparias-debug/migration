package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

class ProgramacionFinancieraPapCalculosTest {

    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository =
            mock(FuenteFinanciamientoEtapaPapRepository.class);
    private final ProgCuatrimestralFinancieraRepository progRepository =
            mock(ProgCuatrimestralFinancieraRepository.class);

    private final ProgramacionFinancieraPapCalculos calculos =
            new ProgramacionFinancieraPapCalculos(fuenteRepository, progRepository);

    private static ProgCuatrimestralFinanciera programacion(int anio, String monto) {
        return ProgCuatrimestralFinanciera.builder().anio(anio).montoCuatrimestre1(new BigDecimal(monto)).build();
    }

    @Test
    void estaFinalizada_sinCostoOCostoCero_noConsultaLasFuentes() {
        assertThat(calculos.estaFinalizada(EtapaPreinversion.builder().id(1L).build(), 2028)).isFalse();
        assertThat(calculos.estaFinalizada(EtapaPreinversion.builder().id(1L).costo(0D).build(), 2028)).isFalse();

        verifyNoInteractions(fuenteRepository);
    }

    @Test
    void estaFinalizada_soloCuentaLaProgramacionDeAniosAnteriores() {
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(1L).costo(100D).build();
        when(fuenteRepository.findByEtapaPreinversionId(1L))
                .thenReturn(List.of(FuenteFinanciamientoEtapaPap.builder().id(10L).build()));
        when(progRepository.findByFuenteId(10L)).thenReturn(List.of(programacion(2026, "60"),
                programacion(2027, "40"), programacion(2028, "500")));

        assertThat(calculos.estaFinalizada(etapa, 2028)).isTrue();
        assertThat(calculos.estaFinalizada(etapa, 2027)).isFalse();
    }

    @Test
    void tieneHistoricoPositivo_fuentesSinMontosPositivos_esFalso() {
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(1L).build();
        when(fuenteRepository.findByEtapaPreinversionId(1L)).thenReturn(List.of(
                FuenteFinanciamientoEtapaPap.builder().id(10L).build(),
                FuenteFinanciamientoEtapaPap.builder().id(11L).build()));
        when(progRepository.findByFuenteId(10L)).thenReturn(List.of(programacion(2027, "0")));
        when(progRepository.findByFuenteId(11L)).thenReturn(List.of());

        assertThat(calculos.tieneHistoricoPositivo(etapa)).isFalse();
    }

    @Test
    void tieneHistoricoPositivo_algunaProgramacionPositiva_esVerdadero() {
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(1L).build();
        when(fuenteRepository.findByEtapaPreinversionId(1L))
                .thenReturn(List.of(FuenteFinanciamientoEtapaPap.builder().id(10L).build()));
        when(progRepository.findByFuenteId(10L)).thenReturn(List.of(programacion(2027, "0"),
                programacion(2028, "15")));

        assertThat(calculos.tieneHistoricoPositivo(etapa)).isTrue();
    }
}
