package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Campos comunes del ciclo de revisión/aprobación DGICP-Institución, por Unidad Ejecutora y año.
 * Ver {@link RevisionProgramacionPap} (CU-PRE-30/31, programación anual) y
 * {@link RevisionAvancePap} (CU-PRE-33, avance/ejecución cuatrimestral) — recursos distintos que
 * comparten el mismo flujo de observaciones/respuesta.
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class RevisionPapBase {

    @NotNull
    @Column(name = "ID_UNIDAD_EJECUTORA", nullable = false)
    private Long idUnidadEjecutora;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @Column(name = "OBSERVACIONES_DGICP", length = 2000)
    private String observacionesDgicp;

    @Column(name = "FECHA_OBSERVACIONES")
    private LocalDateTime fechaObservaciones;

    @Column(name = "RESPUESTA_INSTITUCION", length = 2000)
    private String respuestaInstitucion;

    @Column(name = "FECHA_RESPUESTA")
    private LocalDateTime fechaRespuesta;
}
