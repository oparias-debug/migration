package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DETALLE_ANALISIS_LEGAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AnalsisGestionesLegalesRequeridas {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detalle_analisis_legal_seq")
    @SequenceGenerator(
            name = "detalle_analisis_legal_seq",
            sequenceName = "DETALLE_ANALISIS_LEGAL_SEQ",
            allocationSize = 1)
    @Column(name = "ID_DETALLE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ANALISIS_LEGAL", nullable = false)
    private AnalisisLegal analisisLegal;

    @NotNull
    @Column(name = "ANALISIS_GESTION_LEGAL_REQUERIDA", columnDefinition = "TEXT", nullable = false)
    private String analisisGestionLegalRequerida;

    @NotNull
    @Column(name = "ENTREGABLE", columnDefinition = "TEXT", nullable = false)
    private String entregable;

    @Column(name = "COSTO_ENTREGABLE")
    private Double costoEntregable;

    @Column(name = "MARCO_LEGAL_APLICABLE", nullable = true, length = 2000)
    private String marcoLegalAplicable;

    @Column(name = "OBSERVACIONES", nullable = true, length = 2000)
    private String observaciones;
}
