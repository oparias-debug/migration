package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;

/**
 * [SUPUESTO] Fuente central de aperturas/cierres por tipo de proceso, infiriendo el
 * "CU-ADM-04" referenciado sin documentar en CU-PRE-01, CU-EJE-01/05/10, CU-PRO-07/21/25.
 * NO reemplaza las tablas PeriodoProgramacion* ya creadas en los modulos Programacion/Ejecucion
 * (ver nota de refactor en TRAZABILIDAD-ADMINISTRACION.md).
 */
@Entity
@Table(name = "CALENDARIO_EVENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CalendarioEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendario_evento_seq")
    @SequenceGenerator(name = "calendario_evento_seq", sequenceName = "CALENDARIO_EVENTO_SEQ", allocationSize = 1)
    @Column(name = "ID_CALENDARIO_EVENTO")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO", nullable = false, length = 40)
    private TipoEventoCalendario tipoEvento;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @Min(1) @Max(12)
    @Column(name = "MES")
    private Integer mes;

    @Min(1) @Max(3)
    @Column(name = "CUATRIMESTRE")
    private Integer cuatrimestre;

    @Column(name = "FECHA_APERTURA")
    private LocalDateTime fechaApertura;

    @Column(name = "FECHA_CIERRE")
    private LocalDateTime fechaCierre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoCalendarioEvento estado;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;
}
