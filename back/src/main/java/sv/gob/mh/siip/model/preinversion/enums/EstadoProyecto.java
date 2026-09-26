package sv.gob.mh.siip.model.preinversion.enums;

/** Estados del ciclo de vida del proyecto a traves de los modulos de Preinversion. */
public enum EstadoProyecto {
    EN_REGISTRO("En Elaboración"),
    ENVIADO_DGICP_REGISTRO("Enviado a DGICP (Registro)"),
    OBSERVADO_DGICP_REGISTRO("Observado DGICP (Registro)"),
    CUP_ASIGNADO("CUP asignado"),
    EN_FORMULACION("En Formulación"),
    PROYECTO_FORMULADO("Proyecto formulado"),
    OBSERVADO("Observado"),

    EN_VIABILIDAD("En viabilidad"),
    VIABLE("Proyecto viable"),
    EN_ELEGIBILIDAD("En elegibilidad"),
    ELEGIBLE("Proyecto elegible"),
    PRIORIZADO("Priorizado"),
    EN_EJECUCION("En Ejecucion"),
    FINALIZADO("Finalizado"),
    ARCHIVADO("Archivado"),
    EN_OT("En Opinión Técnica"),
    PROYECTO_CON_OT("Proyecto con Opinión Técnica");

    private final String etiquetaUi;

    EstadoProyecto(String etiquetaUi) {
        this.etiquetaUi = etiquetaUi;
    }

    // Texto tal como lo define RN04 (docs/casos-de-uso/.../UC-PRE-03-Captura_de_Proyectos.md,
    // seccion Reglas de Negocio) para los 13 estados que cubre ese catalogo oficial: se preservan
    // a proposito sus inconsistencias de mayuscula/redaccion frente al nombre de la constante
    // (p.ej. "En Formulación", "Proyecto con Opinión Técnica"). PRIORIZADO/EN_EJECUCION/FINALIZADO/ARCHIVADO quedan
    // fuera de RN04 (ciclo de vida posterior a CU-PRE-03) y su etiqueta no esta confirmada.
    public String getEtiquetaUi() {
        return (etiquetaUi != null) ? etiquetaUi : name();
    }

    /**
     * Indica si en este estado la información de formulación (pantallas de CU-PRE-04
     * "Identificación" a CU-PRE-23 "Indicadores del Proyecto") queda bloqueada para edición.
     *
     * <p>Hoy solo lo bloquea la solicitud de Viabilidad en curso (CU-PRE-24, RN04); al devolver el
     * proyecto pasa a {@link #OBSERVADO} y vuelve a ser editable (RN05). Si otro CU (p.ej. CU-PRE-25
     * o CU-PRE-26) necesita bloquear la formulación en sus estados, basta con agregarlos aquí: los
     * servicios de CU-PRE-04 a CU-PRE-23 consultan este método y no conocen los estados.
     *
     * @return {@code true} si la formulación no admite cambios en este estado
     */
    public boolean bloqueaFormulacion() {
        return this == EN_VIABILIDAD;
    }

    public static EstadoProyecto fromEtiquetaUi(String etiquetaUi) {
        for (EstadoProyecto estado : values()) {
            if (estado.getEtiquetaUi().equals(etiquetaUi)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado no reconocido: " + etiquetaUi);
    }
}
