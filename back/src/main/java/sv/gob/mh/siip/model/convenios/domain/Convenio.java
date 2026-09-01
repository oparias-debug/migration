package sv.gob.mh.siip.model.convenios.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.convenios.enums.EstadoConvenio;
import sv.gob.mh.siip.model.convenios.enums.TipoFinanciamientoConvenio;

/**
 * Convenio de prestamo externo o donacion. CU-MPD-01.
 * RN01: Nombre + No. Convenio deben ser unicos en conjunto.
 */
@Entity
@Table(name = "CONVENIO", uniqueConstraints = @UniqueConstraint(name = "UK_CONVENIO_NUM_NOMBRE", columnNames = {"NUMERO_CONVENIO", "NOMBRE_CONVENIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Convenio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convenio_seq")
    @SequenceGenerator(name = "convenio_seq", sequenceName = "CONVENIO_SEQ", allocationSize = 1)
    @Column(name = "ID_CONVENIO")
    private Long id;

    @NotBlank
    @Column(name = "NUMERO_CONVENIO", nullable = false, length = 50)
    private String numeroConvenio;

    @NotBlank
    @Column(name = "NOMBRE_CONVENIO", nullable = false, length = 300)
    private String nombreConvenio;

    @NotNull
    @Column(name = "MONTO_CONVENIO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoConvenio;

    @NotBlank
    @Column(name = "MONEDA", nullable = false, length = 10)
    private String moneda;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_FINANCIAMIENTO", nullable = false, length = 20)
    private TipoFinanciamientoConvenio tipoFinanciamiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ORGANISMO_ACREEDOR", nullable = false)
    private OrganismoAcreedor organismoAcreedor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION_EJECUTORA", nullable = false)
    private Institucion institucionEjecutora;

    @Column(name = "OBJETIVO_CONVENIO", length = 2000)
    private String objetivoConvenio;

    @Column(name = "MONTO_CONTRAPARTIDA", precision = 18, scale = 2)
    private BigDecimal montoContrapartida;

    @Column(name = "VALOR_TASA", precision = 6, scale = 3)
    private BigDecimal valorTasa;

    @Column(name = "COMISION_COMPROMISO", precision = 6, scale = 3)
    private BigDecimal comisionCompromiso;

    @Column(name = "FECHA_APROBACION_DECRETO")
    private LocalDate fechaAprobacionDecreto;

    @Column(name = "FECHA_ULTIMO_DESEMBOLSO")
    private LocalDate fechaUltimoDesembolso;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoConvenio estado;

    @Column(name = "FECHA_ELIMINACION")
    private LocalDateTime fechaEliminacion;

    @Column(name = "JUSTIFICACION_ELIMINACION", length = 2000)
    private String justificacionEliminacion;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;
    @Column(name = "USUARIO_CREACION", length = 100)
    private String usuarioCreacion;
    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;
    @Column(name = "USUARIO_MODIFICACION", length = 100)
    private String usuarioModificacion;
}
