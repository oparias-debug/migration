package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/**
 * Regla compartida por los servicios de formulación (CU-PRE-04 "Identificación" a CU-PRE-23
 * "Indicadores del Proyecto"): mientras el proyecto está en un estado que bloquea la formulación
 * (ver {@code EstadoProyecto#bloqueaFormulacion()}), ninguna de esas pantallas admite cambios.
 *
 * <p>Origen: CU-PRE-24 "Viabilidad", RN04 (al solicitar Viabilidad se bloquean los campos) y RN05
 * (al enviar comentarios se habilitan de nuevo). Solo la invocan las operaciones que modifican
 * datos; las consultas siguen disponibles.
 */
public final class EdicionFormulacion {

    /** Código de error (409) que recibe el cliente al intentar editar con la formulación bloqueada. */
    public static final String CODIGO_FORMULACION_BLOQUEADA = "FORMULACION_BLOQUEADA";

    private EdicionFormulacion() {
    }

    /**
     * Rechaza la operación si el estado del proyecto bloquea la formulación.
     *
     * @param proyecto proyecto que se va a modificar
     * @throws ConflictoEstadoException con código {@value #CODIGO_FORMULACION_BLOQUEADA} si la
     *         formulación está bloqueada
     */
    public static void exigirEditable(Proyecto proyecto) {
        if (proyecto.getEstado() != null && proyecto.getEstado().bloqueaFormulacion()) {
            throw new ConflictoEstadoException(CODIGO_FORMULACION_BLOQUEADA,
                    "La información del proyecto no se puede modificar mientras está en proceso de "
                            + "Viabilidad (estado actual: " + proyecto.getEstado().getEtiquetaUi() + ").");
        }
    }
}
