package sv.gob.mh.siip.model.common.enums;

/** Roles funcionales identificados en los casos de uso (Anexo C). */
public enum RolUsuario {
    /** Administrador del Sistema. Gestion de solicitudes de CUP y programacion cuatrimestral de Preinversion. */
    ADMINISTRADOR,
    /** Administra los calendarios bajo su responsabilidad (CU-ADM-04, RN12). */
    ADMINISTRADOR_CALENDARIO,
    /** Administra catalogos maestros y sus registros (CU-ADM-01). Actor aun no incorporado a catalogo-actores.md. */
    ADMINISTRADOR_DE_CATALOGOS,
    /** Coordinador de Preinversion. Revision/emision de CUP, priorizacion, opinion tecnica, banco
     *  de proyectos y programacion/avance cuatrimestral de Preinversion. */
    COORDINADOR_PRE,
    /** Coordinador de Programacion y Analisis. Monitoreo de programacion del PAIP/PAP y
     *  presupuesto de inversion. */
    COORDINADOR_PROGRAMACION,
    /** Coordinador SYMP: unica accion, seleccionar Co-ejecutor (CU-PRE-03.5, RN16). */
    COORDINADOR_SYMP,
    /** [SUPUESTO] Rol identificado en Anexo C, aun sin casos de uso implementados; no incorporado
     *  formalmente a catalogo-actores.md. */
    INGENIERO_DINAFI,
    /** Jefe DGI. Priorizacion y programacion/avance cuatrimestral financiero y de metas fisicas de
     *  Preinversion. */
    JEFE_DGI,
    /** Subjefe DGI. Mismo alcance que {@link #JEFE_DGI}. */
    SUBJEFE_DGI,
    /** Tecnico de la Unidad de Asesoria Legal. Registro, modificacion y ficha de convenios. */
    TECNICO_LEGAL,
    /** [SUPUESTO] Tecnico Operativo. Rol identificado en Anexo C, aun sin casos de uso
     *  implementados; no incorporado formalmente a catalogo-actores.md. */
    TECNICO_OPE,
    /** Tecnico de Preinversion. Registro y analisis del ciclo de proyectos en fase de
     *  Preinversion: area de influencia, mercado, tecnica, ambiental, riesgo, legal, presupuesto,
     *  indicadores y elegibilidad. */
    TECNICO_PRE,
    /** Tecnico de Programacion y Analisis. Monitoreo de programacion del PAIP/PAP y presupuesto de
     *  inversion. */
    TECNICO_PROG,
    /** [SUPUESTO] Tecnico de Seguimiento. Rol identificado en Anexo C, aun sin casos de uso
     *  implementados; no incorporado formalmente a catalogo-actores.md. */
    TECNICO_SEG,
    /** [SUPUESTO] Tecnico responsable de la interfaz con el Sistema de Administracion Financiera
     *  Integrado (SIAF, ver SistemaExterno/CU-ITF-01); aun sin casos de uso implementados. */
    TECNICO_SIAF,
    /** Priorizacion de proyectos (CU-PRE-26.5). Actores aun no incorporados a catalogo-actores.md;
     *  ya existen como rol de Keycloak (realm-export.json) con estos mismos nombres. */
    TECNICO_SYMP,
    /** Tecnico de la Unidad Responsable de Proyecto. Identificacion, analisis y
     *  programacion/avance financiero y fisico del proyecto a lo largo de todo el ciclo. */
    TECNICO_URP,
    /** Evaluacion de viabilidad, elegibilidad y opinion tecnica de proyectos. */
    VIABILIZADOR
}
