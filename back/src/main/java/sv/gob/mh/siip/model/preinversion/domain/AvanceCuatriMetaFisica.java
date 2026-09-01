package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Avance cuatrimestral por metas fisicas del PAP. CU-PRE-33. */
@Entity
@Table(name = "AVANCE_CUATRI_META_FISICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AvanceCuatriMetaFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "avance_meta_fis_cuatri_seq")
    @SequenceGenerator(name = "avance_meta_fis_cuatri_seq", sequenceName = "AVANCE_META_FIS_CUATRI_SEQ", allocationSize = 1)
    @Column(name = "ID_AVANCE_META_FIS_CUATRI")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROG_CUATRI_META_FIS", nullable = false)
    private ProgCuatrimestralMetaFisica programacionMeta;

    @NotNull
    @Column(name = "AVANCE_FISICO", nullable = false, precision = 18, scale = 2)
    private BigDecimal avanceFisico;

    @NotNull
    @Column(name = "PORCENTAJE_AVANCE", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeAvance;

    @NotNull
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;
}
