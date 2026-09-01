package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.time.LocalDateTime;

/** Observaciones DGI sobre la programacion de un proyecto en el PRIPME. CU-PRO-02. */
@Entity
@Table(name = "OBSERVACION_PRIPME")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ObservacionPripme {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "observacion_pripme_seq")
    @SequenceGenerator(name = "observacion_pripme_seq", sequenceName = "OBSERVACION_PRIPME_SEQ", allocationSize = 1)
    @Column(name = "ID_OBSERVACION_PRIPME")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PRIPME", nullable = false)
    private Pripme pripme;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_AUTOR", nullable = false)
    private Usuario autor;

    @NotBlank
    @Column(name = "TEXTO", nullable = false, length = 2000)
    private String texto;

    @NotNull
    @Column(name = "FECHA_OBSERVACION", nullable = false)
    private LocalDateTime fechaObservacion;
}
