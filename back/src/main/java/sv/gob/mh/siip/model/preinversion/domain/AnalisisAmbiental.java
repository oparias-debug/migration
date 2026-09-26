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
    @Column(name = "TIENE_IMPACTOS_AMBIENTALES", nullable = false)
    private Boolean tieneImpactosAmbientales;


    // Relación One-to-Many con los impactos (hijos)
    @Builder.Default
    @OneToMany(mappedBy = "analisisAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImpactosAmbientales> impactosAmbientales = new ArrayList<>();

}
