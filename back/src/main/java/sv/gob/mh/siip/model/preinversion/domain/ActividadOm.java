package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CU-PRE-18: fila de la tabla "Costos de Operación"/"Costos de Mantenimiento" (Anexo A.1),
 * originada en la pantalla "Detalle de Actividad" (Anexo A.2). El costo y el factor de
 * corrección del Período 1 ya no son escalares propios: se derivan sumando {@link #insumos}
 * (RN07: cada insumo trae su propio factor de corrección del catálogo {@link InsumoTipo}).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadOm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Proyecto proyecto;
    private String tipoCostoTabla;
    private String nombreActividad;
    @ElementCollection
    @CollectionTable(name = "ACTIVIDAD_OM_INSUMO", joinColumns = @JoinColumn(name = "ID_ACTIVIDAD_OM"))
    @Builder.Default
    private List<InsumoActividad> insumos = new ArrayList<>();

    /** Total (Precios de Mercado) de "Detalle de Actividad": suma de los insumos, Período 1. */
    public double getCostoPeriodo1PrecioMercado() {
        return insumos.stream()
                .mapToDouble(i -> (i.getCostoPeriodo1PrecioMercado() == null)
                        ? 0D : i.getCostoPeriodo1PrecioMercado())
                .sum();
    }

    /** Total (Precios Ajustados) de "Detalle de Actividad": Periodo_1_ajustado = insumo × FC (RN07), sumado. */
    public double getCostoPeriodo1PrecioAjustado() {
        return insumos.stream().mapToDouble((InsumoActividad i) -> {
            double costo = (i.getCostoPeriodo1PrecioMercado() == null) ? 0D : i.getCostoPeriodo1PrecioMercado();
            double factor = (i.getFactorCorreccion() == null) ? 1D : i.getFactorCorreccion();
            return costo * factor;
        }).sum();
    }
}
