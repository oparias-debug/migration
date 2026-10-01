package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionDetalle;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.NombreEtapaDto;

class ProgramacionFinancieraPreinversionAssemblerTest {

    @Test
    void armaFilasPorEtapaConTotalesEIgnoraDetallesFueraDelHorizonte() {
        List<ProgramacionFinPreinversionDetalle> detalles = List.of(
                detalle(TipoEtapaPreinversion.PERFIL, 1, "10.00"),
                detalle(TipoEtapaPreinversion.PERFIL, 2, "5.50"),
                detalle(TipoEtapaPreinversion.PERFIL, 3, "999.00"),
                detalle(TipoEtapaPreinversion.DISENO, 2, "4.50"));
        List<EtapaPreinversion> etapas = List.of(
                EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build(),
                EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.DISENO).build(),
                EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.FACTIBILIDAD).build());

        var respuesta = ProgramacionFinancieraPreinversionAssembler.armar(8L, 2, detalles, etapas);

        assertThat(respuesta.getIdProyecto()).isEqualTo(8L);
        assertThat(respuesta.getPeriodosAProgramar()).isEqualTo(2);
        assertThat(respuesta.getFilas()).extracting(FilaProgramacionEtapaDto::getEtapa)
                .containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.DISENO, NombreEtapaDto.FACTIBILIDAD);
        assertThat(respuesta.getFilas()).extracting(FilaProgramacionEtapaDto::getProgramacionPorPeriodo)
                .containsExactly(List.of(10D, 5.5D), List.of(0D, 4.5D), List.of(0D, 0D));
        assertThat(respuesta.getFilas()).extracting(FilaProgramacionEtapaDto::getCostoEtapa)
                .containsExactly(15.5D, 4.5D, 0D);
        assertThat(respuesta.getTotalGeneralPorPeriodo()).containsExactly(10D, 10D);
        assertThat(respuesta.getTotalGeneral()).isEqualTo(20D);
    }

    @Test
    void sinPeriodosConfiguradosDevuelveFilasVaciasYTotalCero() {
        List<EtapaPreinversion> etapas = List.of(
                EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build());
        List<ProgramacionFinPreinversionDetalle> detalles = List.of(
                detalle(TipoEtapaPreinversion.PERFIL, 1, "10.00"));

        var respuesta = ProgramacionFinancieraPreinversionAssembler.armar(8L, 0, detalles, etapas);

        assertThat(respuesta.getFilas()).singleElement()
                .extracting(FilaProgramacionEtapaDto::getProgramacionPorPeriodo)
                .isEqualTo(List.of());
        assertThat(respuesta.getTotalGeneralPorPeriodo()).isEmpty();
        assertThat(respuesta.getTotalGeneral()).isZero();
    }

    private static ProgramacionFinPreinversionDetalle detalle(TipoEtapaPreinversion etapa, int periodo,
            String monto) {
        return ProgramacionFinPreinversionDetalle.builder().etapa(etapa).periodo(periodo)
                .monto(new BigDecimal(monto)).build();
    }
}
