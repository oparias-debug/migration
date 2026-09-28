package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
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
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;

/**
 * Una vuelta del proceso de Viabilidad (CU-PRE-24): se abre cuando el Técnico URP da clic en
 * "Solicitar Viabilidad" (FB1) y se cierra cuando el Viabilizador devuelve el proyecto (FA01) o
 * emite la Viabilidad (FA02).
 *
 * <p>Cada devolución queda como una revisión cerrada con sus comentarios, de modo que el
 * historial de observaciones y el número de devoluciones se pueden consultar (RN10).
 */
@Entity
@Table(name = "REVISION_VIABILIDAD", uniqueConstraints = @UniqueConstraint(name = "UK_REV_VIABILIDAD_NUMERO",
        columnNames = {"ID_PROYECTO", "NUMERO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RevisionViabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_viabilidad_seq")
    @SequenceGenerator(name = "revision_viabilidad_seq", sequenceName = "REVISION_VIABILIDAD_SEQ", allocationSize = 1)
    @Column(name = "ID_REVISION_VIABILIDAD")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    /** Consecutivo de la revisión dentro del proyecto, empezando en 1. */
    @NotNull
    @Column(name = "NUMERO", nullable = false)
    private Integer numero;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoRevisionViabilidad estado;

    /** Técnico URP que solicitó la Viabilidad; es el destinatario de las notificaciones de cierre. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_SOLICITANTE", nullable = false)
    private Usuario solicitante;

    @NotNull
    @Column(name = "FECHA_SOLICITUD", nullable = false)
    private LocalDateTime fechaSolicitud;

    /** Viabilizador que cerró la revisión (devolución o emisión); nulo mientras está en curso. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VIABILIZADOR")
    private Usuario viabilizador;

    @Column(name = "FECHA_CIERRE")
    private LocalDateTime fechaCierre;

    /**
     * "Observaciones Generales/Justificación de la Viabilidad" (Anexo B.1). Misma longitud que
     * {@code Viabilidad.observaciones}, donde se copia al cerrar la revisión.
     */
    @Column(name = "OBSERVACIONES_GENERALES", length = 2000)
    private String observacionesGenerales;

    /**
     * Si al emitir la Viabilidad se habilitó CU-PRE-25 "Elegibilidad", es decir, si era la primera
     * gestión del proyecto (FA02 paso 2.6; RN03). Nulo mientras la revisión no se emita.
     */
    @Column(name = "HABILITA_ELEGIBILIDAD")
    private Boolean habilitaElegibilidad;

    @ElementCollection
    @CollectionTable(name = "COMENTARIO_REVISION_VIABILIDAD",
            joinColumns = @JoinColumn(name = "ID_REVISION_VIABILIDAD"))
    @OrderColumn(name = "ORDEN")
    @Builder.Default
    private List<ComentarioCampoViabilidad> comentarios = new ArrayList<>();
}
