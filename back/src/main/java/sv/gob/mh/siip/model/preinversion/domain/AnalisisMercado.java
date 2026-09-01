package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Analisis de oferta, demanda y brecha. CU-PRE-09. */
@Entity
@Table(name = "ANALISIS_MERCADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AnalisisMercado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analisis_mercado_seq")
    @SequenceGenerator(name = "analisis_mercado_seq", sequenceName = "ANALISIS_MERCADO_SEQ", allocationSize = 1)
    @Column(name = "ID_ANALISIS_MERCADO")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @Lob
    @Column(name = "DEMANDA")
    private String demanda;

    @Lob
    @Column(name = "OFERTA")
    private String oferta;

    @Lob
    @Column(name = "BRECHA")
    private String brecha;

    @Column(name = "METODOLOGIA", length = 2000)
    private String metodologia;
}
