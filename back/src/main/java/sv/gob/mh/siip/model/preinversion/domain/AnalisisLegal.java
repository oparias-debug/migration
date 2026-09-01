package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @Column(name = "MARCO_LEGAL_APLICABLE", length = 2000)
    private String marcoLegalAplicable;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;
}
