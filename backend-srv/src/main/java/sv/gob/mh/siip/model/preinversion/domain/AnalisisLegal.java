package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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

import java.util.ArrayList;
import java.util.List;

/** Analisis legal del proyecto. CU-PRE-16. */
@Entity
@Table(name = "ANALISIS_LEGAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AnalisisLegal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analisis_legal_seq")
    @SequenceGenerator(name = "analisis_legal_seq", sequenceName = "ANALISIS_LEGAL_SEQ", allocationSize = 1)
    @Column(name = "ID_ANALISIS_LEGAL")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @Column(name = "REQUIERE_ANALISIS_LEGAL")
    private Boolean requiereAnalisisLegal;

    @Builder.Default
    @OneToMany(mappedBy = "analisisLegal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AnalsisGestionesLegalesRequeridas> filas = new ArrayList<>();

}
