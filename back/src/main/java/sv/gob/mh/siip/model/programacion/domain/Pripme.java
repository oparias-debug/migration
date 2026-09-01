package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EstadoRevisionPripme;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Institucion;

import java.time.LocalDateTime;

/** Cabecera del PRIPME institucional para un periodo. CU-PRO-01, CU-PRO-02. */
@Entity
@Table(name = "PRIPME", uniqueConstraints = @UniqueConstraint(name = "UK_PRIPME_INST_PERIODO", columnNames = {"ID_INSTITUCION", "ID_PERIODO_PRIPME"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Pripme {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pripme_seq")
    @SequenceGenerator(name = "pripme_seq", sequenceName = "PRIPME_SEQ", allocationSize = 1)
    @Column(name = "ID_PRIPME")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION", nullable = false)
    private Institucion institucion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERIODO_PRIPME", nullable = false)
    private PeriodoProgramacionPripme periodo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REVISION_PREINVERSION", nullable = false, length = 20)
    private EstadoRevisionPripme estadoRevisionPreinversion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REVISION_INVERSION", nullable = false, length = 20)
    private EstadoRevisionPripme estadoRevisionInversion;

    @Column(name = "FECHA_ENVIO_MONITOREO")
    private LocalDateTime fechaEnvioMonitoreo;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;
    @Column(name = "USUARIO_CREACION", length = 100)
    private String usuarioCreacion;
    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;
    @Column(name = "USUARIO_MODIFICACION", length = 100)
    private String usuarioModificacion;
}
