package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Programacion cuatrimestral de metas fisicas de la preinversion (PAP). CU-PRE-31. */
@Entity
@Table(name = "PROG_CUATRIMESTRAL_META_FISICA",
       uniqueConstraints = @UniqueConstraint(name = "UK_PROG_CUATRI_META_FIS", columnNames = {"ID_PROYECTO", "ANIO", "CUATRIMESTRE"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProgCuatrimestralMetaFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_cuatri_meta_fis_seq")
    @SequenceGenerator(name = "prog_cuatri_meta_fis_seq", sequenceName = "PROG_CUATRI_META_FIS_SEQ", allocationSize = 1)
    @Column(name = "ID_PROG_CUATRI_META_FIS")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(3)
    @Column(name = "CUATRIMESTRE", nullable = false)
    private Integer cuatrimestre;

    @NotNull
    @Column(name = "META_FISICA_PROGRAMADA", nullable = false, precision = 18, scale = 2)
    private BigDecimal metaFisicaProgramada;

    @NotBlank
    @Column(name = "UNIDAD_MEDIDA", nullable = false, length = 50)
    private String unidadMedida;

    @OneToMany(mappedBy = "programacionMeta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<AvanceCuatriMetaFisica> avances = new java.util.ArrayList<>();
}
