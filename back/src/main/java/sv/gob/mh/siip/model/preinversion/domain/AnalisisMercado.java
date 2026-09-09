package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    
    @Column(name = "DEMANDA")
    private String demanda;

    
    @Column(name = "OFERTA")
    private String oferta;

    
    @Column(name = "BRECHA")
    private String brecha;

    @Column(name = "METODOLOGIA", length = 2000)
    private String metodologia;
}
