package sv.gob.mh.siip.model.common.enums;

/** Roles funcionales identificados en los casos de uso (Anexo C). */
public enum RolUsuario {
    TECNICO_URP,
    TECNICO_PRE,
    COORDINADOR_PRE,
    VIABILIZADOR,
    TECNICO_ASYMP,
    TECNICO_PROG,
    TECNICO_SEG,
    TECNICO_LEGAL,
    TECNICO_OPE,
    TECNICO_SIAF,
    INGENIERO_DINAFI,
    COORDINADOR_PROGRAMACION,
    ADMINISTRADOR,
    /** Coordinador SYMP: unica accion, seleccionar Co-ejecutor (CU-PRE-3.5, RN16). */
    COORDINADOR_SYMP,
    /** Administra catalogos maestros y sus registros (CU-ADM-01). Actor aun no incorporado a catalogo-actores.md. */
    ADMINISTRADOR_DE_CATALOGOS,
    /** Administra los calendarios bajo su responsabilidad (CU-ADM-04, RN12). */
    ADMINISTRADOR_CALENDARIO,
    /** Priorizacion de proyectos (CU-PRE-26.5). Actores aun no incorporados a catalogo-actores.md;
     *  ya existen como rol de Keycloak (realm-export.json) con estos mismos nombres. */
    TECNICO_SYMP,
    SUBJEFE_DGI,
    JEFE_DGI
}
