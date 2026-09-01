package sv.gob.mh.siip.model.ejecucion.domain;

import sv.gob.mh.siip.model.ejecucion.enums.EstadoEjecucionFinanciera;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/** Avance financiero por ubicacion geografica del proyecto. CU-EJE-03. */
@Entity
@Table(name = "EJECUCION_FINANCIERA_UBICACION",
       uniqueConstraints = @UniqueConstraint(name = "UK_EJEC_FIN_UBICACION", columnNames = {"ID_PROYECTO", "ID_MUNICIPIO", "ANIO", "MES"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EjecucionFinancieraUbicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ejec_fin_ubicacion_seq")
    @SequenceGenerator(name = "ejec_fin_ubicacion_seq", sequenceName = "EJEC_FIN_UBICACION_SEQ", allocationSize = 1)
    @Column(name = "ID_EJEC_FIN_UBICACION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_DEPARTAMENTO", nullable = false)
    private Departamento departamento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_MUNICIPIO", nullable = false)
    private Municipio municipio;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(12)
    @Column(name = "MES", nullable = false)
    private Integer mes;

    @NotNull
    @Column(name = "MONTO_EJECUTADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoEjecutado;

    @NotNull
    @Column(name = "MONTO_PROVISION_EJECUTADA", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoProvisionEjecutada;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoEjecucionFinanciera estado;

    @Column(name = "OBSERVACION", length = 2000)
    private String observacion;
}
