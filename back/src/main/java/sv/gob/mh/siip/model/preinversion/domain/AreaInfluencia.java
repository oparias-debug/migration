package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;

/** Area geografica de influencia del proyecto (1:N municipios). CU-PRE-08. */
@Entity
@Table(name = "AREA_INFLUENCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AreaInfluencia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "area_influencia_seq")
    @SequenceGenerator(name = "area_influencia_seq", sequenceName = "AREA_INFLUENCIA_SEQ", allocationSize = 1)
    @Column(name = "ID_AREA_INFLUENCIA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_DEPARTAMENTO", nullable = false)
    private Departamento departamento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_MUNICIPIO", nullable = false)
    private Municipio municipio;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;
}
