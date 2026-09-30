package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;

/** Puntaje seleccionado en la columna "Calificación" para un subcriterio (CU-PRE-26.5, RN08). */
@Entity
@Table(name = "CALIFICACION_SUBCRITERIO_PRIORIZ",
        uniqueConstraints = @UniqueConstraint(name = "UK_CALIF_SUBCRIT_PRIORIZ",
                columnNames = { "ID_PRIORIZACION_PROYECTO", "ID_SUBCRITERIO_PRIORIZACION" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CalificacionSubcriterioPriorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calificacion_subcrit_prioriz_seq")
    @SequenceGenerator(name = "calificacion_subcrit_prioriz_seq", sequenceName = "CALIF_SUBCRIT_PRIORIZ_SEQ",
            allocationSize = 1)
    @Column(name = "ID_CALIFICACION_SUBCRITERIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PRIORIZACION_PROYECTO", nullable = false)
    private PriorizacionProyecto priorizacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_SUBCRITERIO_PRIORIZACION", nullable = false)
    private SubcriterioPriorizacion subcriterio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "VALOR", nullable = false, length = 10)
    private ValorCalificacion valor;
}
