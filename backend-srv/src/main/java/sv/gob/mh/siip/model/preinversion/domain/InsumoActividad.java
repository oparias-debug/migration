package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Insumo de una {@link ActividadOm} (CU-PRE-18, Anexo A.2 "Detalle de Actividad"). Código,
 * nombre y factor de corrección se copian del catálogo {@link InsumoTipo} al momento del
 * registro (RN07): ese catálogo "estará sujeto a actualización por parte de la DGICP", así que
 * una actividad ya registrada no debe cambiar de valor si el catálogo cambia después.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsumoActividad {

    private String insumoTipoCodigo;
    private String insumoTipoNombre;
    private Double factorCorreccion;
    private Double costoPeriodo1PrecioMercado;
}
