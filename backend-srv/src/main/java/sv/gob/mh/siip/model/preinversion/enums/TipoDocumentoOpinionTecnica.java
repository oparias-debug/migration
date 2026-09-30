package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Documentos que se cargan en la pantalla de Opinión Técnica (CU-PRE-26, Anexo A.1). El "Documento de
 * Preinversión" y los "Otros documentos anexos" que la pantalla también muestra son los de CU-PRE-24
 * ({@link TipoDocumentoViabilidad}).
 */
public enum TipoDocumentoOpinionTecnica {
    /** "Nota de solicitud de OT": requerida para solicitar la OT (RN04). */
    NOTA_SOLICITUD_OT,
    /** "Nota de OT" firmada por el Director DGICP, cargada al emitir la OT favorable (FA01 paso 1.5). */
    NOTA_OT
}
