package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.OrigenModificacion;
import sv.gob.mh.siip.model.programacion.enums.TipoModificacionPresupuestaria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
