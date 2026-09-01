package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Avance financiero cuatrimestral del PAP. CU-PRE-32. */
@Entity
@Table(name = "AVANCE_FINANCIERO_CUATRIMESTRAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AvanceFinancieroCuatrimestral {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "avance_fin_cuatri_seq")
    @SequenceGenerator(name = "avance_fin_cuatri_seq", sequenceName = "AVANCE_FIN_CUATRI_SEQ", allocationSize = 1)
    @Column(name = "ID_AVANCE_FIN_CUATRI")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROG_CUATRI_FIN", nullable = false)
    private ProgCuatrimestralFinanciera programacion;

    @NotNull
    @Column(name = "MONTO_EJECUTADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoEjecutado;

    @NotNull
    @Column(name = "PORCENTAJE_AVANCE", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeAvance;

    @NotNull
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;
}
