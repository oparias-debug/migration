package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.MontoPorPeriodoDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoPresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;

/** Pruebas unitarias de {@link FichaViabilidadPresupuesto} (CU-PRE-24, Anexo B.1). */
class FichaViabilidadPresupuestoTest {

    private static final Long ID = 8L;

    private PresupuestoInversionService presupuestoInversion;
    private PresupuestoProyectoRepository presupuestos;
    private PresupuestoOmService presupuestoOm;
    private FichaViabilidadPresupuesto presupuesto;

    @BeforeEach
    void setUp() {
        presupuestoInversion = mock(PresupuestoInversionService.class);
        presupuestos = mock(PresupuestoProyectoRepository.class);
        presupuestoOm = mock(PresupuestoOmService.class);
        presupuesto = new FichaViabilidadPresupuesto(presupuestoInversion, presupuestos, presupuestoOm);

        when(presupuestoOm.costosPorTipo(ID, PresupuestoOmService.TIPO_COSTO_OPERACION))
                .thenReturn(new PresupuestoOmService.CostosPorTipo(List.of(), List.of(), List.of()));
        when(presupuestoOm.costosPorTipo(ID, PresupuestoOmService.TIPO_COSTO_MANTENIMIENTO))
                .thenReturn(new PresupuestoOmService.CostosPorTipo(List.of(), List.of(), List.of()));
    }

    @Test
    void sinPresupuestosRegistradosLosCamposQuedanVacios() {
        FichaViabilidadResponseDto ficha = new FichaViabilidadResponseDto();

        presupuesto.completar(ficha, ID);

        assertThat(ficha.getInversionEstimada()).isNull();
        assertThat(presupuesto.inversionEstimada(ID)).isNull();
        assertThat(ficha.getResumenPresupuesto()).isEmpty();
        assertThat(ficha.getCostoOperacion()).isNull();
        assertThat(ficha.getCostoMantenimiento()).isNull();
        assertThat(ficha.getFuenteFinanciamiento()).isEmpty();
    }

    @Test
    void tomaLaInversionLosCostosDelAnio1YLaFuenteDeFinanciamiento() {
        ProductoPresupuestoDto producto = new ProductoPresupuestoDto(1,
                new ProductoSeleccionadoDto().codigoProducto("P-1"), List.of(), List.of(2500.5))
                .costoProductoTotal(2500.5);
        ProductoPresupuestoDto sinMacroactividades = new ProductoPresupuestoDto(2,
                new ProductoSeleccionadoDto().codigoProducto("P-2"), List.of(), List.of())
                .costoProductoTotal(0D);
        when(presupuestoInversion.consultarSoloLectura(ID)).thenReturn(Optional.of(new PresupuestoDto(ID,
                List.of(producto, sinMacroactividades), new MontoPorPeriodoDto(List.of(2500.5), 2500.5), List.of(), 2500.5)));
        when(presupuestoOm.costosPorTipo(ID, PresupuestoOmService.TIPO_COSTO_OPERACION))
                .thenReturn(new PresupuestoOmService.CostosPorTipo(List.of(), List.of(800D, 800D), List.of()));
        PresupuestoProyecto registrado = PresupuestoProyecto.builder().fuenteRecursos("Fondo General")
                .fuentesFinanciamiento(new ArrayList<>(List.of(FuenteFinanciamiento.FONDO_GENERAL))).build();
        when(presupuestos.findByProyectoId(ID)).thenReturn(Optional.of(registrado));
        FichaViabilidadResponseDto ficha = new FichaViabilidadResponseDto();

        presupuesto.completar(ficha, ID);

        assertThat(ficha.getInversionEstimada()).isEqualByComparingTo("2500.5");
        assertThat(ficha.getResumenPresupuesto()).containsEntry("total", BigDecimal.valueOf(2500.5));
        assertThat(ficha.getResumenPresupuesto().get("productos")).asInstanceOf(LIST).hasSize(2);
        assertThat(ficha.getCostoOperacion()).isEqualByComparingTo("800");
        assertThat(ficha.getCostoMantenimiento()).isNull();
        assertThat(ficha.getFuenteFinanciamiento()).isEqualTo(Map.of(
                "fuentesFinanciamiento", List.of("FONDO_GENERAL"), "fuenteRecursos", "Fondo General"));
    }

    @Test
    void unPresupuestoSinMacroactividadesMuestraInversionCero() {
        PresupuestoDto sinMacroactividades = new PresupuestoDto(ID, List.of(),
                new MontoPorPeriodoDto(List.of(), 0D), List.of(), 0D);
        when(presupuestoInversion.consultarSoloLectura(ID)).thenReturn(Optional.of(sinMacroactividades));
        FichaViabilidadResponseDto ficha = new FichaViabilidadResponseDto();

        presupuesto.completar(ficha, ID);

        assertThat(ficha.getInversionEstimada()).isZero();
        assertThat(presupuesto.inversionEstimada(ID)).isZero();
        assertThat(ficha.getResumenPresupuesto()).containsEntry("productos", List.of());
    }
}
