package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

class ProgramacionFinancieraPapValidacionesTest {

    private static final int ANIO = 2028;

    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository =
            mock(FuenteFinanciamientoEtapaPapRepository.class);
    private final ProgCuatrimestralFinancieraRepository progRepository =
            mock(ProgCuatrimestralFinancieraRepository.class);

    private final ProgramacionFinancieraPapValidaciones validaciones = new ProgramacionFinancieraPapValidaciones(
            fuenteRepository, new ProgramacionFinancieraPapCalculos(fuenteRepository, progRepository));

    private static EtapaPreinversion etapa(Long id, TipoEtapaPreinversion tipo, Double costo) {
        return EtapaPreinversion.builder().id(id).tipoEtapa(tipo).costo(costo).build();
    }

    @Test
    void validar_etapaSinSolicitudPeroConHistorico_cuentaComoProgramadaYNoSeValidaSuMonto() {
        EtapaPreinversion perfil = etapa(1L, TipoEtapaPreinversion.PERFIL, 100D);
        EtapaPreinversion prefactibilidad = etapa(2L, TipoEtapaPreinversion.PREFACTIBILIDAD, null);
        when(fuenteRepository.findByEtapaPreinversionId(1L))
                .thenReturn(List.of(FuenteFinanciamientoEtapaPap.builder().id(10L).build()));
        when(progRepository.findByFuenteId(10L)).thenReturn(List.of(ProgCuatrimestralFinanciera.builder()
                .anio(ANIO - 1).montoCuatrimestre1(new BigDecimal("40")).build()));
        // La fila enviada para Prefactibilidad no tiene montos: no cuenta como programación de la etapa.
        EtapaProgramacionRequestDto sinMontos = new EtapaProgramacionRequestDto(NombreEtapaDto.PREFACTIBILIDAD)
                .fuentes(List.of(new FilaFuenteProgramacionRequestDto().montoCuatrimestre1(0D)));
        Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa =
                new EnumMap<>(TipoEtapaPreinversion.class);
        porEtapa.put(TipoEtapaPreinversion.PREFACTIBILIDAD, sinMontos);
        List<EtapaPreinversion> etapas = List.of(perfil, prefactibilidad);

        assertThatCode(() -> validaciones.validar(etapas, porEtapa, ANIO)).doesNotThrowAnyException();
    }

    @Test
    void validar_sinCostoEnLaSolicitud_comparaContraElCostoRegistradoDeLaEtapa() {
        EtapaPreinversion perfil = etapa(1L, TipoEtapaPreinversion.PERFIL, 100D);
        EtapaProgramacionRequestDto excedido = new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                .fuentes(List.of(new FilaFuenteProgramacionRequestDto().montoCuatrimestre1(150D)));
        Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa =
                new EnumMap<>(TipoEtapaPreinversion.class);
        porEtapa.put(TipoEtapaPreinversion.PERFIL, excedido);
        List<EtapaPreinversion> etapas = List.of(perfil);

        assertThatThrownBy(() -> validaciones.validar(etapas, porEtapa, ANIO))
                .isInstanceOf(ValidacionNegocioException.class)
                .extracting("codigo").isEqualTo("MONTO_SUPERA_COSTO_ETAPA");
    }

    @Test
    void validar_filasSinMontosYSinHistorico_exigeAlMenosUnaEtapaProgramada() {
        EtapaPreinversion perfil = etapa(1L, TipoEtapaPreinversion.PERFIL, 100D);
        EtapaProgramacionRequestDto sinMontos = new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                .fuentes(List.of(new FilaFuenteProgramacionRequestDto()));
        Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa =
                new EnumMap<>(TipoEtapaPreinversion.class);
        porEtapa.put(TipoEtapaPreinversion.PERFIL, sinMontos);
        List<EtapaPreinversion> etapas = List.of(perfil);

        assertThatThrownBy(() -> validaciones.validar(etapas, porEtapa, ANIO))
                .isInstanceOf(ValidacionNegocioException.class)
                .extracting("codigo").isEqualTo("SIN_NINGUNA_ETAPA_PROGRAMADA");
    }
}
