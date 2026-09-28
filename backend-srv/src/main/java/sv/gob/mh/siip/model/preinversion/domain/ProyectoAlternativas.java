package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Estado de "Registro de Alternativas" (CU-PRE-05) guardado en la tabla PROYECTO. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ProyectoAlternativas {

    /** Justificación de la alternativa seleccionada. */
    @Column(name = "JUSTIFICACION_ALTERNATIVAS", length = 1000)
    private String justificacionAlternativasSolucion;

    /** Nulo si "Registro de Alternativas" nunca se ha guardado. Ver RN1-2/RN1-3 en CU-PRE-05.openapi.yaml. */
    @Column(name = "FECHA_ULT_GUARDADO_ALTERNATIVAS")
    private LocalDateTime fechaUltimoGuardadoAlternativasSolucion;
}
