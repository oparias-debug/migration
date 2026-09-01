package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Usuario;

import java.time.LocalDateTime;

/**
 * Solicitud de CUP u Opinion Tecnica registrada por el Tecnico URP.
 * CU-PRE-01, CU-PRE-01.5, CU-PRE-02.
 */
@Entity
@Table(name = "SOLICITUD_PREINVERSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SolicitudPreinversion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_preinv_seq")
    @SequenceGenerator(name = "solicitud_preinv_seq", sequenceName = "SOLICITUD_PREINV_SEQ", allocationSize = 1)
    @Column(name = "ID_SOLICITUD")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_SOLICITUD", nullable = false, length = 20)
    private TipoSolicitud tipoSolicitud;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 30)
    private EstadoSolicitud estado;

    @NotNull
    @Column(name = "FECHA_SOLICITUD", nullable = false)
    private LocalDateTime fechaSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TECNICO_ASIGNADO")
    private Usuario tecnicoAsignado;

    @Column(name = "FECHA_ASIGNACION")
    private LocalDateTime fechaAsignacion;

    /** RN-4 CU-PRE-01: archivo automatico tras 3 meses + 5 dias habiles de inactividad. */
    @Column(name = "FECHA_ARCHIVO")
    private LocalDateTime fechaArchivo;

    /** RN-4 CU-PRE-01: momento en que se envio la alerta de posible eliminacion (3 meses sin CUP). */
    @Column(name = "FECHA_ALERTA_ELIMINACION")
    private LocalDateTime fechaAlertaEliminacion;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;

    @Column(name = "USUARIO_CREACION", length = 100)
    private String usuarioCreacion;
}
