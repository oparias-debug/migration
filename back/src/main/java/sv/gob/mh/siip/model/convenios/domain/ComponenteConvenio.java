package sv.gob.mh.siip.model.convenios.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Distribucion inicial del monto del convenio por componente. CU-MPD-01. RN22: suma <= monto convenio. */
@Entity
@Table(name = "COMPONENTE_CONVENIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ComponenteConvenio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "componente_convenio_seq")
    @SequenceGenerator(name = "componente_convenio_seq", sequenceName = "COMPONENTE_CONVENIO_SEQ", allocationSize = 1)
    @Column(name = "ID_COMPONENTE_CONVENIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONVENIO", nullable = false)
    private Convenio convenio;

    @NotBlank
    @Column(name = "NOMBRE_COMPONENTE", nullable = false, length = 250)
    private String nombreComponente;

    @NotNull
    @Column(name = "MONTO_ASIGNADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoAsignado;

    @Column(name = "PORCENTAJE", precision = 5, scale = 2)
    private BigDecimal porcentaje;
}
