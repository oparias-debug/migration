package sv.gob.mh.siip.model.preinversion.enums;

/** Estados del ciclo de vida del proyecto a traves de los modulos de Preinversion. */
public enum EstadoProyecto {
    EN_REGISTRO,
    ENVIADO_DGICP_REGISTRO,
    OBSERVADO_DGICP_REGISTRO,
    CUP_ASIGNADO,
    EN_FORMULACION,
    PROYECTO_FORMULADO,
    OBSERVADO,

    EN_VIABILIDAD,
    VIABLE,
    EN_ELEGIBILIDAD,
    ELEGIBLE,
    PRIORIZADO,
    EN_BANCO_PROYECTOS,
    EN_EJECUCION,
    FINALIZADO,
    ARCHIVADO,
    EN_OT,
    PROYECTO_CON_OT,
}
