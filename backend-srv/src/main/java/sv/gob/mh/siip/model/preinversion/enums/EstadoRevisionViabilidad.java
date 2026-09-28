package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Estado de una revisión de Viabilidad (CU-PRE-24): cada solicitud del Técnico URP abre una
 * revisión que el Viabilizador cierra devolviendo el proyecto o emitiendo la Viabilidad.
 */
public enum EstadoRevisionViabilidad {

    /** Solicitud enviada; el Viabilizador puede guardar comentarios, devolver o emitir (FB1, FB2). */
    EN_CURSO,

    /** El Viabilizador envió comentarios y el proyecto quedó "Observado" (FA01; RN05, RN10). */
    DEVUELTA,

    /** El Viabilizador emitió la Viabilidad; la ficha queda deshabilitada (FA02 paso 2.5). */
    EMITIDA
}
