package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.CategoriaAmbiental;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Analisis ambiental y permisos requeridos. CU-PRE-14. */
@Entity
@Table(name = "ANALISIS_AMBIENTAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AnalisisAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analisis_ambiental_seq")
    @SequenceGenerator(name = "analisis_ambiental_seq", sequenceName = "ANALISIS_AMBIENTAL_SEQ", allocationSize = 1)
    @Column(name = "ID_ANALISIS_AMBIENTAL")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORIA_AMBIENTAL", nullable = false, length = 20)
    private CategoriaAmbiental categoriaAmbiental;

    @Column(name = "PERMISOS_REQUERIDOS", length = 2000)
    private String permisosRequeridos;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;
}
