package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/** Provision financiera autorizada, con documento de respaldo. CU-PRO-17. */
@Entity
@Table(name = "PROVISION_FINANCIERA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProvisionFinanciera {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "provision_financiera_seq")
    @SequenceGenerator(name = "provision_financiera_seq", sequenceName = "PROVISION_FINANCIERA_SEQ", allocationSize = 1)
    @Column(name = "ID_PROVISION_FINANCIERA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROG_FIN_PAIP", nullable = false)
    private ProgramacionFinancieraPaip programacionFinancieraPaip;

    @NotNull
    @Column(name = "MONTO_PROVISION", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoProvision;

    @Column(name = "DOCUMENTO_RESPALDO", length = 500)
    private String documentoRespaldo;

    @Column(name = "REVISADO_POR_SYMP", nullable = false)
    private Boolean revisadoPorSymp;
}
