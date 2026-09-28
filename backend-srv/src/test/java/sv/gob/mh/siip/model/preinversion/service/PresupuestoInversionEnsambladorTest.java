package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoPresupuestoDto;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;

class PresupuestoInversionEnsambladorTest {

    private PresupuestoInversionMacroactividades macroactividades;
    private ComponenteRepository componentes;
    private PresupuestoInversionEnsamblador ensamblador;
    private Proyecto proyecto;
    private PresupuestoProyecto presupuesto;

    @BeforeEach
    void setup() {
        macroactividades = mock(PresupuestoInversionMacroactividades.class);
        componentes = mock(ComponenteRepository.class);
        ensamblador = new PresupuestoInversionEnsamblador(macroactividades, componentes);
        proyecto = Proyecto.builder().id(1L).build();
        presupuesto = PresupuestoProyecto.builder().id(2L).proyecto(proyecto).periodosEstimados(4).build();
    }

    @Test
    void contarProductosCuentaLosComponentesDelProyecto() {
        when(componentes.findByProyectoIdOrderByIdAsc(1L)).thenReturn(List.of(componente("P1"), componente("P2")));

        assertThat(ensamblador.contarProductos(1L)).isEqualTo(2);
    }

    @Test
    void dtoCalculaTotalesPorProductoYDelProyecto() {
        MacroactividadDto m1 = new MacroactividadDto(1L, "1.1", "A", List.of(), List.of(1.0, 2.0));
        MacroactividadDto m2 = new MacroactividadDto(2L, "1.2", "B", List.of(), List.of(3.0));
        MacroactividadDto m3 = new MacroactividadDto(3L, "3.1", "C", List.of(), List.of(0.5, 0.5, 4.0));
        when(macroactividades.porProducto(presupuesto)).thenReturn(Map.of(1, List.of(m1, m2), 3, List.of(m3)));
        when(componentes.findByProyectoIdOrderByIdAsc(1L))
                .thenReturn(List.of(componente("P1"), componente("P2"), componente("P3")));

        PresupuestoDto dto = ensamblador.dto(proyecto, presupuesto);

        assertThat(dto.getIdProyecto()).isEqualTo(1L);
        assertThat(dto.getPeriodosEstimados()).isEqualTo(4);
        assertThat(dto.getResumenPorComponente()).isEmpty();
        assertThat(dto.getProductos()).hasSize(3);
        ProductoPresupuestoDto primero = dto.getProductos().get(0);
        assertThat(primero.getNumero()).isEqualTo(1);
        assertThat(primero.getProducto().getCodigoProducto()).isEqualTo("P1");
        assertThat(primero.getMacroactividades()).containsExactly(m1, m2);
        assertThat(primero.getCostoProductoPorPeriodo()).containsExactly(4.0, 2.0);
        assertThat(primero.getCostoProductoTotal()).isEqualTo(6.0);
        ProductoPresupuestoDto segundo = dto.getProductos().get(1);
        assertThat(segundo.getMacroactividades()).isEmpty();
        assertThat(segundo.getCostoProductoPorPeriodo()).isEmpty();
        assertThat(segundo.getCostoProductoTotal()).isZero();
        assertThat(dto.getInversionEstimadaPreciosMercado().getPorPeriodo()).containsExactly(4.5, 2.5, 4.0);
        assertThat(dto.getInversionEstimadaPreciosMercado().getTotal()).isEqualTo(11.0);
        assertThat(dto.getTotalResumenPorComponente()).isEqualTo(11.0);
    }

    @Test
    void dtoSinProductosDevuelveTotalesEnCero() {
        when(macroactividades.porProducto(presupuesto)).thenReturn(Map.of());
        when(componentes.findByProyectoIdOrderByIdAsc(1L)).thenReturn(List.of());

        PresupuestoDto dto = ensamblador.dto(proyecto, presupuesto);

        assertThat(dto.getProductos()).isEmpty();
        assertThat(dto.getInversionEstimadaPreciosMercado().getPorPeriodo()).isEmpty();
        assertThat(dto.getInversionEstimadaPreciosMercado().getTotal()).isZero();
    }

    private Componente componente(String codigo) {
        return Componente.builder().proyecto(proyecto).nombre("TC-" + codigo).codigoProducto(codigo).build();
    }
}
