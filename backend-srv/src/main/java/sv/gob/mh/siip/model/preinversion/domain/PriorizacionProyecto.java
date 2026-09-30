package sv.gob.mh.siip.model.preinversion.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;

/**
 * Calificación de la matriz multicriterio de un proyecto (CU-PRE-26.5): una por cada Opinión Técnica
 * favorable, porque la priorización se hace "cada vez que se emita Opinión Técnica al proyecto"
 * (Descripción). Guarda el estado de los dos tramos y, al completarse, la "Prioridad del proyecto".
 */
@Entity
@Table(name = "PRIORIZACION_PROYECTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PriorizacionProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priorizacion_proyecto_seq")
    @SequenceGenerator(name = "priorizacion_proyecto_seq", sequenceName = "PRIORIZACION_PROYECTO_SEQ",
            allocationSize = 1)
    @Column(name = "ID_PRIORIZACION_PROYECTO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    /** OT favorable que habilitó esta priorización (filtro habilitante). */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_OPINION_TECNICA", nullable = false)
    private OpinionTecnica opinionTecnica;

    @Builder.Default
    @Embedded
    @Getter(AccessLevel.NONE)
    @AttributeOverride(name = "estado", column = @Column(name = "ESTADO_PRE", length = 20))
    @AttributeOverride(name = "ajustesHabilitados", column = @Column(name = "AJUSTES_PRE"))
    @AttributeOverride(name = "fechaEnvio", column = @Column(name = "FECHA_ENVIO_PRE"))
    @AttributeOverride(name = "fechaRevision", column = @Column(name = "FECHA_REVISION_PRE"))
    @AssociationOverride(name = "tecnico", joinColumns = @JoinColumn(name = "ID_TECNICO_PRE"))
    @AssociationOverride(name = "coordinador", joinColumns = @JoinColumn(name = "ID_COORDINADOR_PRE"))
    private TramoCalificacionPriorizacion tramoPre = new TramoCalificacionPriorizacion();

    @Builder.Default
    @Embedded
    @Getter(AccessLevel.NONE)
    @AttributeOverride(name = "estado", column = @Column(name = "ESTADO_SYMP", length = 20))
    @AttributeOverride(name = "ajustesHabilitados", column = @Column(name = "AJUSTES_SYMP"))
    @AttributeOverride(name = "fechaEnvio", column = @Column(name = "FECHA_ENVIO_SYMP"))
    @AttributeOverride(name = "fechaRevision", column = @Column(name = "FECHA_REVISION_SYMP"))
    @AssociationOverride(name = "tecnico", joinColumns = @JoinColumn(name = "ID_TECNICO_SYMP"))
    @AssociationOverride(name = "coordinador", joinColumns = @JoinColumn(name = "ID_COORDINADOR_SYMP"))
    private TramoCalificacionPriorizacion tramoSymp = new TramoCalificacionPriorizacion();

    /** "Prioridad del proyecto" al completarse la calificación (RN07, RN12). */
    @Column(name = "PRIORIDAD", precision = 8, scale = 2)
    private BigDecimal prioridad;

    /** Nombre de la categoría del rango de interpretación (RN09). */
    @Column(name = "CATEGORIA", length = 100)
    private String categoria;

    @Column(name = "FECHA_COMPLETADA")
    private LocalDateTime fechaCompletada;

    /**
     * @param tramo tramo de la calificación
     * @return su estado; Hibernate deja el embebido en {@code null} si todas sus columnas lo están
     */
    public TramoCalificacionPriorizacion tramo(TramoPriorizacion tramo) {
        if (tramo == TramoPriorizacion.PRE) {
            if (tramoPre == null) {
                tramoPre = new TramoCalificacionPriorizacion();
            }
            return tramoPre;
        }
        if (tramoSymp == null) {
            tramoSymp = new TramoCalificacionPriorizacion();
        }
        return tramoSymp;
    }

    /** @return si el Coordinador SYMP revisó el criterio 5 y la priorización está completa (RN07) */
    public boolean estaCompleta() {
        return fechaCompletada != null;
    }
}
