package sv.gob.mh.siip.model.preinversion.enums;

/**
 * Etapas de la Ruta de Preinversión (CU-PRE-03.5). No incluye un valor distinto para
 * "Estudio General": RN07/RN08 tratan a Estudios Generales y Programa como usuarios de las mismas
 * PERFIL/EJECUCION que Proyecto, no como una etapa con nombre propio.
 *
 * <p>El orden de declaración es el orden de progresión de la Ruta (de menos a más avanzada): los
 * consumidores que necesitan "la etapa más avanzada" comparan con {@link #compareTo}, no por nombre.
 */
public enum TipoEtapaPreinversion {
    PERFIL("Perfil"),
    PREFACTIBILIDAD("Prefactibilidad"),
    FACTIBILIDAD("Factibilidad"),
    DISENO("Diseño"),
    EJECUCION("Ejecución");

    private final String etiquetaUi;

    TipoEtapaPreinversion(String etiquetaUi) {
        this.etiquetaUi = etiquetaUi;
    }

    /** Texto tal como aparece en los mockups (p. ej. columna "Etapa" del Anexo A.1 de CU-PRE-29). */
    public String getEtiquetaUi() {
        return etiquetaUi;
    }
}
