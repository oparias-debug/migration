package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Descripcion tecnica del proyecto. CU-PRE-11. */
@Entity
@Table(name = "DESCRIPCION_TECNICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DescripcionTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "descripcion_tecnica_seq")
    @SequenceGenerator(name = "descripcion_tecnica_seq", sequenceName = "DESCRIPCION_TECNICA_SEQ", allocationSize = 1)
    @Column(name = "ID_DESCRIPCION_TECNICA")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @NotNull
    @Lob
    @Column(name = "DESCRIPCION", nullable = false)
    private String descripcion;

    @Lob
    @Column(name = "ESPECIFICACIONES")
    private String especificaciones;

    @Column(name = "VIDA_UTIL_ANIOS")
    private Integer vidaUtilAnios;
}
