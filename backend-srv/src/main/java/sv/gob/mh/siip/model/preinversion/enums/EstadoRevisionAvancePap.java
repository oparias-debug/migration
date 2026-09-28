package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Estado del ciclo de revisión/aprobación del avance cuatrimestral del PAP (CU-PRE-33), definido en
 * CU-PRE-33.openapi.yaml. Distinto de {@link EstadoPap} (CU-PRE-30/31): son ciclos de aprobación
 * independientes, uno por programación (anual) y otro por avance (cuatrimestral).
 */
public enum EstadoRevisionAvancePap {
    EN_ELABORACION,
    OBSERVADO,
    REVISADO
}
