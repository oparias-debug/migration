package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;

/** Distribucion financiera del proyecto por ubicacion geografica. CU-PRO-19. */
@Entity
@Table(name = "DISTRIBUCION_FIN_UBICACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DistribucionFinUbicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "distrib_fin_ubic_seq")
    @SequenceGenerator(name = "distrib_fin_ubic_seq", sequenceName = "DISTRIB_FIN_UBIC_SEQ", allocationSize = 1)
    @Column(name = "ID_DISTRIB_FIN_UBIC")
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

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Column(name = "MONTO_PROGRAMADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoProgramado;

    @Column(name = "MONTO_PROVISION", precision = 18, scale = 2)
    private BigDecimal montoProvision;
}
