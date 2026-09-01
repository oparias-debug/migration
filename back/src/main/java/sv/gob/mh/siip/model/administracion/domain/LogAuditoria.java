package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.AccionAuditoria;
import sv.gob.mh.siip.model.common.domain.Usuario;

/**
 * [SUPUESTO] Bitacora generica CRUD, complementaria a auditorias puntuales ya existentes
 * (ej. HistorialCierre del modulo Ejecucion). Infiere una necesidad transversal no documentada.
 */
@Entity
@Table(name = "LOG_AUDITORIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_auditoria_seq")
    @SequenceGenerator(name = "log_auditoria_seq", sequenceName = "LOG_AUDITORIA_SEQ", allocationSize = 1)
    @Column(name = "ID_LOG_AUDITORIA")
    private Long id;

    @NotBlank
    @Column(name = "ENTIDAD", nullable = false, length = 100)
    private String entidad;

    @NotNull
    @Column(name = "ID_ENTIDAD", nullable = false)
    private Long idEntidad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ACCION", nullable = false, length = 20)
    private AccionAuditoria accion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @NotNull
    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;

    @Lob
    @Column(name = "DETALLE")
    private String detalle;
}
