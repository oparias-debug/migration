package sv.gob.mh.siip.model.convenios.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Distribucion ajustada (inicial vs ajustada) de un componente del convenio. CU-MPD-03. RN07: suma = monto convenio. */
@Entity
@Table(name = "COMPONENTE_CONVENIO_AJUSTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ComponenteConvenioAjuste {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comp_conv_ajuste_seq")
    @SequenceGenerator(name = "comp_conv_ajuste_seq", sequenceName = "COMP_CONV_AJUSTE_SEQ", allocationSize = 1)
    @Column(name = "ID_COMPONENTE_AJUSTE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_AJUSTE_CONVENIO", nullable = false)
    private AjusteConvenio ajusteConvenio;

    /** Componente original del que proviene este ajuste, si aplica. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPONENTE_CONVENIO")
    private ComponenteConvenio componenteOrigen;

    @NotBlank
    @Column(name = "NOMBRE_COMPONENTE", nullable = false, length = 250)
    private String nombreComponente;

    @NotNull
    @Column(name = "MONTO_ASIGNADO_INICIAL", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoAsignadoInicial;

    @NotNull
    @Column(name = "MONTO_ASIGNADO_AJUSTADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoAsignadoAjustado;

    @Column(name = "PORCENTAJE", precision = 5, scale = 2)
    private BigDecimal porcentaje;
}
