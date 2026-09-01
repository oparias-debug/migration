package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.*;
import lombok.*;

/** Una de las 6 dimensiones de puntuacion para priorizar proyectos en el escenario. CU-PRO-08. */
@Entity
@Table(name = "DIMENSION_PRIORIZACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DimensionPriorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dimension_prioriz_seq")
    @SequenceGenerator(name = "dimension_prioriz_seq", sequenceName = "DIMENSION_PRIORIZ_SEQ", allocationSize = 1)
    @Column(name = "ID_DIMENSION_PRIORIZACION")
    private Long id;

    @Column(name = "NUMERO", nullable = false, unique = true)
    private Integer numero;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;
}
