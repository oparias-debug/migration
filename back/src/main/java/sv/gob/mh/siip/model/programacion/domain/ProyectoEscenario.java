package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.ClasificacionProyectoEscenario;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;

/** Proyecto incluido en un escenario de corto plazo. CU-PRO-08, CU-PRO-09. */
@Entity
@Table(name = "PROYECTO_ESCENARIO", uniqueConstraints = @UniqueConstraint(name = "UK_PROYECTO_ESCENARIO", columnNames = {"ID_ESCENARIO", "ID_PROYECTO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProyectoEscenario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_escenario_seq")
    @SequenceGenerator(name = "proyecto_escenario_seq", sequenceName = "PROYECTO_ESCENARIO_SEQ", allocationSize = 1)
    @Column(name = "ID_PROYECTO_ESCENARIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESCENARIO", nullable = false)
    private EscenarioCortoPlazo escenario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "MONTO_PROPUESTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoPropuesto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "CLASIFICACION", nullable = false, length = 20)
    private ClasificacionProyectoEscenario clasificacion;

    @Column(name = "PUNTAJE_TOTAL", precision = 8, scale = 2)
    private BigDecimal puntajeTotal;

    @Column(name = "ORDEN")
    private Integer orden;
}
