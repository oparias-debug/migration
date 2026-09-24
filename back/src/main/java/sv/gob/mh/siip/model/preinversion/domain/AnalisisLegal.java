package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
