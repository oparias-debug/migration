package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Columna agrupada "Ubicación"/"N° de Personas" (RN09) dentro de una fila de "Análisis de la
 * Población" (Anexo A.1). CU-PRE-07.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CeldaUbicacionPoblacion {

    @Column(name = "UBICACION", length = 200)
    private String ubicacion;

    @Column(name = "NUMERO_PERSONAS")
    private Integer numeroPersonas;
}
