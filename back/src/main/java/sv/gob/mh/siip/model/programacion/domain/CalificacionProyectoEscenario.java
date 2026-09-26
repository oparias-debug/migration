package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.math.BigDecimal;

/** Puntaje por dimension de priorizacion para un proyecto del escenario. CU-PRO-08. */
@Entity
@Table(name = "CALIFICACION_PROYECTO_ESCENARIO",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_CALIF_PROY_ESC_DIM",
               columnNames = {"ID_PROYECTO_ESCENARIO", "ID_DIMENSION_PRIORIZACION"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CalificacionProyectoEscenario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calif_proy_escenario_seq")
    @SequenceGenerator(name = "calif_proy_escenario_seq", sequenceName = "CALIF_PROY_ESCENARIO_SEQ", allocationSize = 1)
    @Column(name = "ID_CALIFICACION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO_ESCENARIO", nullable = false)
    private ProyectoEscenario proyectoEscenario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_DIMENSION_PRIORIZACION", nullable = false)
    private DimensionPriorizacion dimensionPriorizacion;

    @NotNull
    @Column(name = "PUNTAJE", nullable = false, precision = 8, scale = 2)
    private BigDecimal puntaje;
}
