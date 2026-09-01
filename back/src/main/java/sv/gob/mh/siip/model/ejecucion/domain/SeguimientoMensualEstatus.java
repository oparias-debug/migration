package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.time.LocalDateTime;

/**
 * Estatus textual mensual del proyecto, registrado por el Tecnico SYMP. CU-EJE-11.
 * Avance Financiero (CU-EJE-01) y Avance Fisico (CU-EJE-02) se cargan automaticamente
 * y se consultan desde sus tablas de origen; no se duplican aqui. [SUPUESTO]
 */
@Entity
@Table(name = "SEGUIMIENTO_MENSUAL_ESTATUS",
       uniqueConstraints = @UniqueConstraint(name = "UK_SEGUIMIENTO_ESTATUS", columnNames = {"ID_PROYECTO", "ANIO", "MES"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SeguimientoMensualEstatus {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seguimiento_estatus_seq")
    @SequenceGenerator(name = "seguimiento_estatus_seq", sequenceName = "SEGUIMIENTO_ESTATUS_SEQ", allocationSize = 1)
    @Column(name = "ID_SEGUIMIENTO_ESTATUS")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Min(1) @Max(12)
    @Column(name = "MES", nullable = false)
    private Integer mes;

    @NotBlank
    @Column(name = "ESTATUS", nullable = false, length = 2000)
    private String estatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_AUTOR", nullable = false)
    private Usuario autor;

    @NotNull
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;
}
