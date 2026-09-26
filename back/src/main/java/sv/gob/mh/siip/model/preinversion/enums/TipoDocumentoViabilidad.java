package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Documentos que el Técnico URP carga en la sección "Viabilidad" (CU-PRE-24, Anexo B.1; FB1 paso 1;
 * RN02).
 */
public enum TipoDocumentoViabilidad {

    /** Documento de Preinversión: obligatorio para solicitar Viabilidad y único por proyecto (RN02). */
    DOCUMENTO_PREINVERSION,

    /** Otros documentos anexos del proyecto: opcionales y acumulables (RN02, "si aplica"). */
    OTRO_DOCUMENTO
}
