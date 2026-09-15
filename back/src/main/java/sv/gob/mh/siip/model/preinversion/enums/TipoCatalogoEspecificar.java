package sv.gob.mh.siip.model.preinversion.enums;

/**
 * A cuál catálogo de "Especificar" pertenece un criterio de elegibilidad con
 * {@code tipoEspecificar = CATALOGO} (CU-PRE-25). {@code EJE_PLAN_GOBIERNO} (catálogo C.2) es el
 * único valor que no se resuelve con {@code GET /catalogos/especificar-elegibilidad}: se resuelve
 * con {@code GET /catalogos/ejes-plan-gobierno} (CU-PRE-03.5).
 */
public enum TipoCatalogoEspecificar {
    ODS,
    EJE_PLAN_GOBIERNO,
    COMPONENTE_MEDIO_AMBIENTE,
    MEDIDA_GRD,
    MEDIDA_ACC,
    GRUPO_POBLACIONAL_VULNERABLE,
    MEJORA_CALIDAD_VIDA
}
