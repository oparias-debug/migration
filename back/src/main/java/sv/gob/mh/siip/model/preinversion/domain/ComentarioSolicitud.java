package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Usuario;

import java.time.LocalDateTime;

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
    @Lob
    @Column(name = "TEXTO", nullable = false)
    private String texto;

    @NotNull
    @Column(name = "FECHA_COMENTARIO", nullable = false)
    private LocalDateTime fechaComentario;
}
