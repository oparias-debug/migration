package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorEvaluacion;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorEvaluacionDto;
import sv.gob.mh.siip.model.preinversion.dto.MontoPorPeriodoDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoPresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.enums.TipoIndicador;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.DescripcionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorEvaluacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;

/** Pruebas unitarias de {@link FichaViabilidadEnsamblador} (CU-PRE-24, Anexo B.1). */
class FichaViabilidadEnsambladorTest {

    private static final Long ID = 8L;

    private IdentificacionRepository identificaciones;
    private DescripcionTecnicaRepository descripciones;
    private ComponenteRepository componentes;
    private ProductoIndicadorCatalogoRepository catalogo;
    private AnalisisPoblacionRepository poblaciones;
    private PresupuestoInversionService presupuestoInversion;
    private PresupuestoProyectoRepository presupuestos;
    private PresupuestoOmService presupuestoOm;
    private IndicadorEvaluacionRepository indicadores;
    private FichaViabilidadEnsamblador ensamblador;

    @BeforeEach
    void setUp() {
        identificaciones = mock(IdentificacionRepository.class);
        descripciones = mock(DescripcionTecnicaRepository.class);
        componentes = mock(ComponenteRepository.class);
        catalogo = mock(ProductoIndicadorCatalogoRepository.class);
        poblaciones = mock(AnalisisPoblacionRepository.class);
        presupuestoInversion = mock(PresupuestoInversionService.class);
        presupuestos = mock(PresupuestoProyectoRepository.class);
        presupuestoOm = mock(PresupuestoOmService.class);
        indicadores = mock(IndicadorEvaluacionRepository.class);
        ensamblador = new FichaViabilidadEnsamblador(identificaciones, descripciones, componentes, catalogo, poblaciones,
                presupuestoInversion, presupuestos, presupuestoOm, indicadores);

        when(presupuestoOm.costosPorTipo(ID, PresupuestoOmService.TIPO_COSTO_OPERACION))
                .thenReturn(new PresupuestoOmService.CostosPorTipo(List.of(), List.of(), List.of()));
        when(presupuestoOm.costosPorTipo(ID, PresupuestoOmService.TIPO_COSTO_MANTENIMIENTO))
                .thenReturn(new PresupuestoOmService.CostosPorTipo(List.of(), List.of(), List.of()));
    }

    private static FichaViabilidadResponseDto fichaVacia() {
        return new FichaViabilidadResponseDto();
    }

    @Test
    void sinInformacionDeOrigenLosCamposQuedanVaciosYSeMuestranLasEtiquetasDeIndicadores() {
        FichaViabilidadResponseDto ficha = fichaVacia();

        ensamblador.completarCamposDeConsulta(ficha, ID);

        assertThat(ficha.getObjetivoGeneral()).isNull();
        assertThat(ficha.getDescripcion()).isNull();
        assertThat(ficha.getProductos()).isEmpty();
        assertThat(ficha.getPoblacionObjetivo()).isNull();
        assertThat(ficha.getInversionEstimada()).isNull();
        assertThat(ficha.getResumenPresupuesto()).isEmpty();
        assertThat(ficha.getCostoOperacion()).isNull();
        assertThat(ficha.getCostoMantenimiento()).isNull();
        assertThat(ficha.getFuenteFinanciamiento()).isEmpty();
        assertThat(ficha.getIndicadoresEvaluacion()).extracting(IndicadorEvaluacionDto::getNombre)
                .containsExactly("VAN", "TIR", "R B/C");
        assertThat(ficha.getIndicadoresEvaluacion()).extracting(IndicadorEvaluacionDto::getValor).containsOnlyNulls();
        verify(catalogo, never()).findByCodigoProductoIn(anyList());
    }

    @Test
    void tomaCadaCampoDeSuCasoDeUsoDeOrigen() {
        when(identificaciones.findByProyectoId(ID))
                .thenReturn(Optional.of(Identificacion.builder().objetivoGeneral("Objetivo").build()));
        when(descripciones.findByProyectoId(ID))
                .thenReturn(Optional.of(DescripcionTecnica.builder().descripcion("Descripción").build()));
        when(componentes.findByProyectoIdOrderByIdAsc(ID)).thenReturn(List.of(
                Componente.builder().nombre("Componente A").codigoProducto("P-1").build(),
                Componente.builder().nombre("Componente B").codigoProducto("P-2").build(),
                Componente.builder().nombre("Componente C").build(),
                Componente.builder().build()));
        when(catalogo.findByCodigoProductoIn(List.of("P-1", "P-2"))).thenReturn(List.of(
                ProductoIndicadorCatalogo.builder().codigoProducto("P-1").producto("Carretera").build(),
                ProductoIndicadorCatalogo.builder().codigoProducto("P-1").producto("Carretera (indicador 2)").build()));
        AnalisisPoblacion poblacion = AnalisisPoblacion.builder().ubicacionesObjetivo(new ArrayList<>(List.of(
                new CeldaUbicacionPoblacion("A", 100), new CeldaUbicacionPoblacion("B", null),
                new CeldaUbicacionPoblacion("C", 50)))).build();
        when(poblaciones.findByProyectoId(ID)).thenReturn(Optional.of(poblacion));
        ProductoPresupuestoDto producto = new ProductoPresupuestoDto(1, new ProductoSeleccionadoDto().codigoProducto("P-1"),
                List.of(), List.of(2500.5)).costoProductoTotal(2500.5);
        ProductoPresupuestoDto sinProducto = new ProductoPresupuestoDto(2, null, List.of(), List.of()).costoProductoTotal(0D);
        when(presupuestoInversion.consultarSoloLectura(ID)).thenReturn(Optional.of(new PresupuestoDto(ID,
                List.of(producto, sinProducto), new MontoPorPeriodoDto(List.of(2500.5), 2500.5), List.of(), 2500.5)));
        when(presupuestoOm.costosPorTipo(ID, PresupuestoOmService.TIPO_COSTO_OPERACION))
                .thenReturn(new PresupuestoOmService.CostosPorTipo(List.of(), List.of(800D, 800D), List.of()));
        PresupuestoProyecto presupuesto = PresupuestoProyecto.builder().fuenteRecursos("Fondo General")
                .fuentesFinanciamiento(new ArrayList<>(List.of(FuenteFinanciamiento.FONDO_GENERAL))).build();
        when(presupuestos.findByProyectoId(ID)).thenReturn(Optional.of(presupuesto));
        LocalDateTime hoy = LocalDateTime.now();
        when(indicadores.findByProyectoId(ID)).thenReturn(List.of(
                IndicadorEvaluacion.builder().tipoIndicador(TipoIndicador.VAN).valor(BigDecimal.ONE).fechaCalculo(hoy.minusDays(1)).build(),
                IndicadorEvaluacion.builder().tipoIndicador(TipoIndicador.VAN).valor(BigDecimal.TEN).fechaCalculo(hoy).build(),
                IndicadorEvaluacion.builder().tipoIndicador(TipoIndicador.VAN).valor(BigDecimal.ZERO).fechaCalculo(null).build(),
                IndicadorEvaluacion.builder().tipoIndicador(TipoIndicador.TIR_SOCIAL).valor(BigDecimal.valueOf(12)).fechaCalculo(hoy).build(),
                IndicadorEvaluacion.builder().valor(BigDecimal.ONE).build()));
        FichaViabilidadResponseDto ficha = fichaVacia();

        ensamblador.completarCamposDeConsulta(ficha, ID);

        assertThat(ficha.getObjetivoGeneral()).isEqualTo("Objetivo");
        assertThat(ficha.getDescripcion()).isEqualTo("Descripción");
        assertThat(ficha.getProductos()).containsExactly("Carretera", "Componente B", "Componente C");
        assertThat(ficha.getPoblacionObjetivo()).isEqualTo(150L);
        assertThat(ficha.getInversionEstimada()).isEqualByComparingTo("2500.5");
        assertThat(ficha.getResumenPresupuesto()).containsEntry("total", BigDecimal.valueOf(2500.5));
        assertThat(ficha.getResumenPresupuesto().get("productos")).asInstanceOf(LIST).hasSize(2);
        assertThat(ficha.getCostoOperacion()).isEqualByComparingTo("800");
        assertThat(ficha.getCostoMantenimiento()).isNull();
        assertThat(ficha.getFuenteFinanciamiento()).isEqualTo(Map.of(
                "fuentesFinanciamiento", List.of("FONDO_GENERAL"), "fuenteRecursos", "Fondo General"));
        assertThat(ficha.getIndicadoresEvaluacion()).extracting(IndicadorEvaluacionDto::getNombre)
                .containsExactly("VAN", "TIR", "R B/C", "TIR_SOCIAL");
        assertThat(ficha.getIndicadoresEvaluacion().get(0).getValor()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void unPresupuestoSinMontoTotalNoMuestraInversion() {
        PresupuestoDto sinMonto = new PresupuestoDto(ID, List.of(), null, List.of(), 0D);
        when(presupuestoInversion.consultarSoloLectura(ID)).thenReturn(Optional.of(sinMonto));
        FichaViabilidadResponseDto ficha = fichaVacia();

        ensamblador.completarCamposDeConsulta(ficha, ID);

        assertThat(ficha.getInversionEstimada()).isNull();
        assertThat(ficha.getResumenPresupuesto()).containsEntry("productos", List.of());
    }
}
