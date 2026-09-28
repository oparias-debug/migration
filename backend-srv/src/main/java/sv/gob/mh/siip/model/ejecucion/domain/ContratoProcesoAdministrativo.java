package sv.gob.mh.siip.model.ejecucion.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.ejecucion.enums.EstadoContratoProceso;

/** Contrato derivado de un proceso administrativo. CU-EJE-04. */
@Entity
@Table(name = "CONTRATO_PROCESO_ADMINISTRATIVO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ContratoProcesoAdministrativo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contrato_proc_admin_seq")
    @SequenceGenerator(name = "contrato_proc_admin_seq", sequenceName = "CONTRATO_PROC_ADMIN_SEQ", allocationSize = 1)
    @Column(name = "ID_CONTRATO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROCESO_ADMINISTRATIVO", nullable = false)
    private ProcesoAdministrativo procesoAdministrativo;

    @NotBlank
    @Column(name = "ID_CONTRATO_EXTERNO", nullable = false, length = 50)
    private String idContratoExterno;

    @NotNull
    @Column(name = "MONTO", nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_TERMINO")
    private LocalDate fechaTermino;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 30)
    private EstadoContratoProceso estado;
}
