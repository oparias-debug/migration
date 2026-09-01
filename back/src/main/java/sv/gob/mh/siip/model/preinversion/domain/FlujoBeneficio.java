package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Flujo de beneficios del proyecto por anio. CU-PRE-20. */
@Entity
@Table(name = "FLUJO_BENEFICIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FlujoBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flujo_beneficio_seq")
    @SequenceGenerator(name = "flujo_beneficio_seq", sequenceName = "FLUJO_BENEFICIO_SEQ", allocationSize = 1)
    @Column(name = "ID_FLUJO_BENEFICIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotBlank
    @Column(name = "TIPO_BENEFICIO", nullable = false, length = 100)
    private String tipoBeneficio;

    @NotNull
    @Column(name = "MONTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;
}
