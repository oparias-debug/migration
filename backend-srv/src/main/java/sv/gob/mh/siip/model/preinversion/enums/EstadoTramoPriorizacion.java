package sv.gob.mh.siip.model.preinversion.enums;

/** Estado de un tramo de la calificación de la priorización (CU-PRE-26.5, FB1 pasos 4–8). */
public enum EstadoTramoPriorizacion {
    /** El Técnico todavía no dio clic en "Calificar Priorización". */
    PENDIENTE,
    /** Enviada a revisión del Coordinador; la pantalla queda bloqueada para el Técnico (RN05). */
    ENVIADA_A_REVISION,
    /** El Coordinador registró "Priorización revisada". */
    REVISADA
}
