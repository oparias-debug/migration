package sv.gob.mh.siip.model.preinversion.domain;

import java.math.BigDecimal;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;

/** Monto programado por etapa y período para CU-PRE-22.1. */
@Entity
@Table(name = "PROGRAMACION_FIN_PREINV_DETALLE", uniqueConstraints = @UniqueConstraint(
        name = "UK_PROG_FIN_PREINV_PROYECTO_ETAPA_PERIODO",
        columnNames = { "ID_PROYECTO", "ETAPA", "PERIODO" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramacionFinPreinversionDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_fin_preinv_detalle_seq")
    @SequenceGenerator(name = "prog_fin_preinv_detalle_seq", sequenceName = "PROG_FIN_PREINV_DETALLE_SEQ",
            allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;
    @Enumerated(EnumType.STRING) @Column(name = "ETAPA", nullable = false)
    private TipoEtapaPreinversion etapa;
    @Column(name = "PERIODO", nullable = false)
    private Integer periodo;
    @Column(name = "MONTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;
}
