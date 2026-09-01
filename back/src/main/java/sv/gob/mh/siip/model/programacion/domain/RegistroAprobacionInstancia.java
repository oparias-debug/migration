package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.InstanciaAprobacion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Resultado de aprobacion del PAIP por instancia externa. CU-PRO-11. */
@Entity
@Table(name = "REGISTRO_APROBACION_INSTANCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RegistroAprobacionInstancia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registro_aprob_inst_seq")
    @SequenceGenerator(name = "registro_aprob_inst_seq", sequenceName = "REGISTRO_APROB_INST_SEQ", allocationSize = 1)
    @Column(name = "ID_REGISTRO_APROBACION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONTRAPROPUESTA", nullable = false)
    private ContrapropuestaInstitucional contrapropuesta;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "INSTANCIA", nullable = false, length = 30)
    private InstanciaAprobacion instancia;

    @NotBlank
    @Column(name = "RESULTADO", nullable = false, length = 30)
    private String resultado;

    @Column(name = "MONTO_APROBADO", precision = 18, scale = 2)
    private BigDecimal montoAprobado;

    @NotNull
    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;
}
