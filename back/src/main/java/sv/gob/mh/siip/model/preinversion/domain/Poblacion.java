package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Poblacion de referencia, demandante y objetivo. CU-PRE-07. */
@Entity
@Table(name = "POBLACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Poblacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "poblacion_seq")
    @SequenceGenerator(name = "poblacion_seq", sequenceName = "POBLACION_SEQ", allocationSize = 1)
    @Column(name = "ID_POBLACION")
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
