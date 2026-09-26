package sv.gob.mh.siip.model.preinversion.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porcentaje programado por cuatrimestre para una {@link EtapaMetaFisicaPap} en un año determinado
 * (Anexo A.4, sección "Programación Cuatrimestral"). CU-PRE-31. Una fila por combinación
 * (etapaMetaFisica, año); los campos de lectura (totalAnio, ejecutadoAniosAnteriores,
 * aniosPosteriores) se calculan a partir de estos porcentajes y del histórico de años anteriores de
 * la misma etapa (RN-B.a). A diferencia de {@link ProgCuatrimestralFinanciera}, estos valores son
 * porcentajes (0-100), no montos monetarios.
 */
@Entity
@Table(name = "PROG_CUATRIMESTRAL_META_FISICA",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_PROG_CUATRI_META_FIS",
               columnNames = {"ID_ETAPA_META_FISICA", "ANIO"}))
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
    @JoinColumn(name = "ID_ETAPA_META_FISICA", nullable = false)
    private EtapaMetaFisicaPap etapaMetaFisica;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Builder.Default
    @Column(name = "MONTO_CUATRIMESTRE_1", nullable = false, precision = 5, scale = 2)
    private BigDecimal montoCuatrimestre1 = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "MONTO_CUATRIMESTRE_2", nullable = false, precision = 5, scale = 2)
    private BigDecimal montoCuatrimestre2 = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    @Column(name = "MONTO_CUATRIMESTRE_3", nullable = false, precision = 5, scale = 2)
    private BigDecimal montoCuatrimestre3 = BigDecimal.ZERO;

    @OneToMany(mappedBy = "programacionMeta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AvanceCuatriMetaFisica> avances = new ArrayList<>();

    public BigDecimal totalProgramadoAnio() {
        return montoCuatrimestre1.add(montoCuatrimestre2).add(montoCuatrimestre3);
    }
}
