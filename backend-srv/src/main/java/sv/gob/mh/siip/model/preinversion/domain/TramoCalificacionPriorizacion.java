package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.enums.EstadoTramoPriorizacion;

/**
 * Estado de un tramo de la calificación de la priorización (CU-PRE-26.5): el del Técnico PRE (criterios
 * 1 a 4) o el del Técnico SYMP (criterio 5). {@link PriorizacionProyecto} lo embebe dos veces.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TramoCalificacionPriorizacion {

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", length = 20)
    private EstadoTramoPriorizacion estado = EstadoTramoPriorizacion.PENDIENTE;

    /** El Coordinador habilitó ajustes a una calificación ya enviada (RN10, RN11). */
    @Builder.Default
    @Column(name = "AJUSTES_HABILITADOS")
    private Boolean ajustesHabilitados = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TECNICO")
    private Usuario tecnico;

    @Column(name = "FECHA_ENVIO")
    private LocalDateTime fechaEnvio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COORDINADOR")
    private Usuario coordinador;

    @Column(name = "FECHA_REVISION")
    private LocalDateTime fechaRevision;

    /** @return si el Técnico del tramo puede calificar: aún sin enviar o con ajustes habilitados (RN05) */
    public boolean edicionHabilitada() {
        return estado == EstadoTramoPriorizacion.PENDIENTE || Boolean.TRUE.equals(ajustesHabilitados);
    }

    /** @return si el Coordinador ya revisó el tramo alguna vez (el criterio 5 requiere los 1 a 4 revisados) */
    public boolean fueRevisado() {
        return fechaRevision != null;
    }
}
