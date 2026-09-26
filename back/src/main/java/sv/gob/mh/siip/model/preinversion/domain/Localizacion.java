package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;

import java.math.BigDecimal;

/** Localizacion geografica exacta del proyecto (1:N). CU-PRE-12. */
@Entity
@Table(name = "LOCALIZACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Localizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localizacion_seq")
    @SequenceGenerator(name = "localizacion_seq", sequenceName = "LOCALIZACION_SEQ", allocationSize = 1)
    @Column(name = "ID_LOCALIZACION")
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

    @Column(name = "DIRECCION", length = 500)
    private String direccion;

    @Column(name = "LATITUD", precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(name = "LONGITUD", precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(name = "REQUIERE_ADQUISICION_TERRENO")
    private Boolean requiereAdquisicionTerreno;

    @Column(name = "PROPIETARIO", length = 150)
    private String propietario;

    @Column(name = "ESPECIFIQUE", length = 500)
    private String especifique;
}

