package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fila de "Resumen de costos" (Componente/Costo) de la Ficha de proyectos de emergencia
 * (Anexo A.4). {@code tipoCosto} es el código tomado de {@code GET /catalogos/tipos-costo}.
 * CU-PRE-3.5.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ComponenteCostoEmergencia {

    @Column(name = "TIPO_COSTO", nullable = false, length = 20)
    private String tipoCosto;

    @Column(name = "COSTO", nullable = false)
    private Double costo;
}
