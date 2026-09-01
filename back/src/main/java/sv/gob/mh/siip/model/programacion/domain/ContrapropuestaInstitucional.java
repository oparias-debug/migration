package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EstadoContrapropuesta;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Institucion;

import java.time.LocalDateTime;

/** Contrapropuesta institucional sobre la propuesta PAIP autorizada. CU-PRO-11. */
@Entity
@Table(name = "CONTRAPROPUESTA_INSTITUCIONAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ContrapropuestaInstitucional {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contrapropuesta_seq")
    @SequenceGenerator(name = "contrapropuesta_seq", sequenceName = "CONTRAPROPUESTA_SEQ", allocationSize = 1)
    @Column(name = "ID_CONTRAPROPUESTA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESCENARIO", nullable = false)
    private EscenarioCortoPlazo escenario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION", nullable = false)
    private Institucion institucion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 30)
    private EstadoContrapropuesta estado;

    @NotNull
    @Column(name = "FECHA_ELABORACION", nullable = false)
    private LocalDateTime fechaElaboracion;

    @Column(name = "FECHA_APROBACION")
    private LocalDateTime fechaAprobacion;
}
