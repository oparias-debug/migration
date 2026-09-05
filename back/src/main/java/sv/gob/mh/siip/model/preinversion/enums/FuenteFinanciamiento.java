package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Catálogo cerrado "Fuentes de Financiamiento" (7 valores, RQ-T-02), definido en
 * CU-PRE-17.openapi.yaml y reutilizado por la Ficha de proyectos de emergencia de CU-PRE-3.5. No
 * relacionado con la entidad JPA {@code sv.gob.mh.siip.model.common.domain.FuenteFinanciamiento}
 * (catálogo administrado usado por Presupuesto de inversión, CU-PRE-17) — mismo nombre en el
 * propio contrato OpenAPI, conceptos distintos.
 */
public enum FuenteFinanciamiento {
    SIN_FINANCIAMIENTO,
    FONDO_GENERAL,
    RECURSOS_PROPIOS,
    PRESTAMOS_EXTERNOS,
    PRESTAMOS_INTERNOS,
    DONACIONES,
    OTROS
}
