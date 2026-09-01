package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Usuario;

import java.time.LocalDateTime;

/** Opinion tecnica emitida sobre el proyecto. CU-PRE-26. */
@Entity
@Table(name = "OPINION_TECNICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OpinionTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "opinion_tecnica_seq")
    @SequenceGenerator(name = "opinion_tecnica_seq", sequenceName = "OPINION_TECNICA_SEQ", allocationSize = 1)
    @Column(name = "ID_OPINION_TECNICA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "RESULTADO", nullable = false, length = 20)
    private ResultadoOpinionTecnica resultado;

    @NotNull
    @Column(name = "FECHA_EMISION", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TECNICO_RESPONSABLE", nullable = false)
    private Usuario tecnicoResponsable;
}
