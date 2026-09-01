package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Poblacion de referencia, demandante y objetivo. CU-PRE-07. */
@Entity
@Table(name = "POBLACION_OBJETIVO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PoblacionObjetivo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "poblacion_objetivo_seq")
    @SequenceGenerator(name = "poblacion_objetivo_seq", sequenceName = "POBLACION_OBJETIVO_SEQ", allocationSize = 1)
    @Column(name = "ID_POBLACION_OBJETIVO")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @Column(name = "POBLACION_REFERENCIA")
    private Long poblacionReferencia;

    @Column(name = "POBLACION_DEMANDANTE")
    private Long poblacionDemandante;

    @Column(name = "POBLACION_OBJETIVO")
    private Long poblacionObjetivo;

    @Column(name = "CRITERIOS_FOCALIZACION", length = 2000)
    private String criteriosFocalizacion;
}
