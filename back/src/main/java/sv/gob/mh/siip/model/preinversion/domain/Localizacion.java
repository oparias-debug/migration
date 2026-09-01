package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;

import java.math.BigDecimal;

/** Localizacion geografica exacta del proyecto (1:N). CU-PRE-12. */
@Entity
@Table(name = "LOCALIZACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Localizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localizacion_seq")
    @SequenceGenerator(name = "localizacion_seq", sequenceName = "LOCALIZACION_SEQ", allocationSize = 1)
    @Column(name = "ID_LOCALIZACION")
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

    @Column(name = "DIRECCION", length = 500)
    private String direccion;

    @Column(name = "LATITUD", precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(name = "LONGITUD", precision = 10, scale = 7)
    private BigDecimal longitud;
}
