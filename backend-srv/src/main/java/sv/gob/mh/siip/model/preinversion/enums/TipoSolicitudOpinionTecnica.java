package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Opciones del desplegable "Tipo de solicitud" del menú GESTIÓN › OT (CU-PRE-26, Anexo A). Coincide
 * con la columna "Tipo de gestión" del Histórico de OT (Anexo A1.5).
 */
public enum TipoSolicitudOpinionTecnica {
    /** "1. Opinión Técnica": OT para una etapa presentada por primera vez. */
    OPINION_TECNICA,
    /** "2. Actualización de OT": actualización de una OT ya emitida de un proyecto sin ejecución iniciada. */
    ACTUALIZACION_OT
}
