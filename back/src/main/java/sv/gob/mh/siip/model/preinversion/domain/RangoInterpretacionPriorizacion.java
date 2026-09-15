package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Rango de interpretación del puntaje de priorización (CU-ADM-02/CU-PRE-26.5, Anexo A.2, RN09).
 * El contrato documenta este catálogo como de contenido confirmado (4 filas fijas), a diferencia
 * de los criterios/subcriterios de priorización (bloqueados por ambigüedad de versión).
 */
@Entity
@Table(name = "RANGO_INTERPRETACION_PRIORIZACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RangoInterpretacionPriorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rango_interpretacion_priorizacion_seq")
    @SequenceGenerator(name = "rango_interpretacion_priorizacion_seq",
            sequenceName = "RANGO_INTERPRETACION_PRIORIZACION_SEQ", allocationSize = 1)
    @Column(name = "ID_RANGO_INTERPRETACION")
    private Long id;

    @NotNull
    @Column(name = "PUNTAJE_MINIMO", nullable = false)
    private Double puntajeMinimo;

    @NotNull
    @Column(name = "PUNTAJE_MAXIMO", nullable = false)
    private Double puntajeMaximo;

    @NotBlank
    @Column(name = "CATEGORIA", nullable = false, length = 100, unique = true)
    private String categoria;

    @NotBlank
    @Column(name = "IMPLICACION", nullable = false, length = 500)
    private String implicacion;
}
