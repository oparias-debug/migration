package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AccionesDisponiblesElegibilidadDto;

/**
 * Foto de una operación de CU-PRE-25 "Elegibilidad": el actor que la ejecuta,
 * el proyecto al que
 * accede y el estado de la gestión, derivado de la última emisión de
 * {@link Elegibilidad}.
 *
 * <p>
 * La ficha del Anexo A.1 admite cambios en dos momentos:
 * <ul>
 * <li>antes de la primera emisión, con el proyecto en "Proyecto viable"
 * (FB1);</li>
 * <li>después de una emisión, solo si la Opinión Técnica devolvió el proyecto
 * con comentarios (RN07,
 * RN09; FB2).</li>
 * </ul>
 * En cualquier otro caso la ficha queda bloqueada (FB1 paso 6, RN04).
 *
 * @param actor         usuario autenticado que ejecuta la operación
 * @param proyecto      proyecto consultado o gestionado
 * @param ultimaEmision última emisión de Elegibilidad del proyecto, o
 *                      {@code null} si nunca se emitió
 * @param habilitada    si la ficha admite registrar la calificación y emitir la
 *                      Elegibilidad
 */
public record ElegibilidadContexto(Usuario actor, Proyecto proyecto, Elegibilidad ultimaEmision, boolean habilitada) {

    public static final String FICHA_ELEGIBILIDAD_DESHABILITADA = "FICHA_ELEGIBILIDAD_DESHABILITADA";

    /**
     * @return si la Elegibilidad ya se emitió alguna vez, es decir, si una nueva
     *         emisión responde a
     *         comentarios de la OT (FB2, RN15)
     */
    public boolean esReemision() {
        return ultimaEmision != null;
    }

    /**
     * Acciones del Anexo A.1 habilitadas para el actor: solo el Viabilizador
     * registra, guarda y emite
     * (RN01, RN05), y solo con la ficha habilitada (RN09). El backend vuelve a
     * validar cada una al
     * ejecutarla.
     *
     * @return las acciones disponibles
     */
    public AccionesDisponiblesElegibilidadDto acciones() {
        boolean gestiona = actor.getRol() == RolUsuario.VIABILIZADOR && habilitada;
        return new AccionesDisponiblesElegibilidadDto(gestiona, gestiona);
    }

    /** Exige que la ficha admita cambios (RN04, RN07, RN09). */
    public void exigirHabilitada() {
        if (habilitada) {
            return;
        }
        String mensaje = esReemision()
                ? ("La ficha de Elegibilidad está bloqueada; solo se habilita cuando la Opinión Técnica "
                        + "envía comentarios.")
                : "La ficha de Elegibilidad solo se habilita cuando el proyecto está en estado \"Proyecto viable\".";
        throw new ConflictoEstadoException(FICHA_ELEGIBILIDAD_DESHABILITADA, mensaje);
    }
}
