package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Usuario;

/** Historial de comentarios de revision sobre una solicitud. CU-PRE-01, CU-PRE-01.5. */
@Entity
@Table(name = "COMENTARIO_SOLICITUD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ComentarioSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comentario_solicitud_seq")
    @SequenceGenerator(name = "comentario_solicitud_seq", sequenceName = "COMENTARIO_SOLICITUD_SEQ", allocationSize = 1)
    @Column(name = "ID_COMENTARIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_SOLICITUD", nullable = false)
    private SolicitudPreinversion solicitud;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_AUTOR", nullable = false)
    private Usuario autor;

    @NotNull
    
    @Column(name = "TEXTO", nullable = false)
    private String texto;

    @NotNull
    @Column(name = "FECHA_COMENTARIO", nullable = false)
    private LocalDateTime fechaComentario;
}
