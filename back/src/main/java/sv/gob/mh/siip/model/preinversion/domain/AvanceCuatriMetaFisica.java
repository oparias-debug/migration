package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

/**
 * Avance de metas físicas de un cuatrimestre para una {@link ProgCuatrimestralMetaFisica} (etapa +
 * año) (Anexo A.4, "Avance del Cuatrimestre"). CU-PRE-33. Una fila por combinación (programacionMeta,
 * cuatrimestre); análogo de {@link AvanceFinancieroCuatrimestral} pero con valores porcentuales
 * (0-100), no montos monetarios. El resto de campos de lectura de {@code EtapaAvanceMetas}
 * (acumulados, estado, totalMetaEjecutada) se calculan a partir de estas filas y del histórico de la
 * misma etapa (RN-F, RN-G).
 */
@Entity
@Table(name = "AVANCE_CUATRI_META_FISICA",
       uniqueConstraints = @UniqueConstraint(name = "UK_AVANCE_META_FIS_CUATRI", columnNames = {"ID_PROG_CUATRI_META_FIS", "CUATRIMESTRE"}))
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
    @Enumerated(EnumType.STRING)
    @Column(name = "CUATRIMESTRE", nullable = false, length = 20)
    private Cuatrimestre cuatrimestre;

    @NotNull
    @Column(name = "AVANCE_CUATRIMESTRE", nullable = false, precision = 5, scale = 2)
    private BigDecimal avanceCuatrimestre;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;

    @NotNull
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;
}
