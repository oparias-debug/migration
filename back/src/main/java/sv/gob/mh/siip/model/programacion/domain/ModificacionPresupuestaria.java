package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.OrigenModificacion;
import sv.gob.mh.siip.model.programacion.enums.TipoModificacionPresupuestaria;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Modificacion presupuestaria sobre la programacion del proyecto. CU-PRO-17. */
@Entity
@Table(name = "MODIFICACION_PRESUPUESTARIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ModificacionPresupuestaria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modif_presupuestaria_seq")
    @SequenceGenerator(name = "modif_presupuestaria_seq", sequenceName = "MODIF_PRESUPUESTARIA_SEQ", allocationSize = 1)
    @Column(name = "ID_MODIFICACION_PRESUPUESTARIA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_MODIFICACION", nullable = false, length = 20)
    private TipoModificacionPresupuestaria tipoModificacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGEN", nullable = false, length = 10)
    private OrigenModificacion origen;

    @NotNull
    @Column(name = "MONTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @NotNull
    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;
}
