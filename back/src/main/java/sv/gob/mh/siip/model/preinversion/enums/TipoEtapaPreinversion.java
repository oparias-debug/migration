package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Etapas de la Ruta de Preinversión (CU-PRE-3.5). No incluye un valor distinto para
 * "Estudio General": RN07/RN08 tratan a Estudios Generales y Programa como usuarios de las mismas
 * PERFIL/EJECUCION que Proyecto, no como una etapa con nombre propio.
 */
public enum TipoEtapaPreinversion {
    PERFIL,
    PREFACTIBILIDAD,
    FACTIBILIDAD,
    DISENO,
    EJECUCION
}
