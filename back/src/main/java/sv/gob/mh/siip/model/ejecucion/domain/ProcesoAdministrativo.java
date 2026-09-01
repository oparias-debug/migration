package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;

/** Hito de un proceso de adquisicion asociado a un proyecto. CU-EJE-04. */
@Entity
@Table(name = "PROCESO_ADMINISTRATIVO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProcesoAdministrativo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proceso_admin_seq")
    @SequenceGenerator(name = "proceso_admin_seq", sequenceName = "PROCESO_ADMIN_SEQ", allocationSize = 1)
    @Column(name = "ID_PROCESO_ADMINISTRATIVO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @Column(name = "ID_PROCESO_EXTERNO", length = 50)
    private String idProcesoExterno;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    @Column(name = "METODO_CONTRATACION", length = 100)
    private String metodoContratacion;

    @Column(name = "CATEGORIA", length = 100)
    private String categoria;

    @NotBlank
    @Column(name = "ESTADO", nullable = false, length = 30)
    private String estado;

    @Column(name = "MONTO_PROGRAMADO", precision = 18, scale = 2)
    private BigDecimal montoProgramado;

    @Column(name = "MONTO_COMPROMETIDO", precision = 18, scale = 2)
    private BigDecimal montoComprometido;

    @Column(name = "MONTO_PENDIENTE", precision = 18, scale = 2)
    private BigDecimal montoPendiente;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(12)
    @Column(name = "MES", nullable = false)
    private Integer mes;

    /** Obligatorio mientras el proceso no cuente con ID de contrato. */
    @Column(name = "JUSTIFICACION_SIN_CONTRATO", length = 2000)
    private String justificacionSinContrato;

    @OneToMany(mappedBy = "procesoAdministrativo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ContratoProcesoAdministrativo> contratos = new java.util.ArrayList<>();
}
