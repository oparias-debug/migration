package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AccionesOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.CamposEditablesOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoAccionDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;

/**
 * Foto de una operación sobre una gestión de Opinión Técnica (CU-PRE-26): el actor, el proyecto, la
 * gestión y lo que cada rol puede hacer en la pantalla del Anexo A.1.
 *
 * <p>La DGICP (Técnico PRE y Coordinador PRE) revisa mientras la gestión está en curso y el proyecto es
 * "Proyecto viable" o "Proyecto elegible" (RN02, RN05). Al enviar comentarios la gestión queda
 * observada y el Técnico URP registra la "Justificación Institución" con el proyecto "Observado" (RN03,
 * RN06). Emitida la OT favorable, nada vuelve a habilitarse (RN10).
 *
 * @param actor usuario autenticado que ejecuta la operación
 * @param proyecto proyecto de la gestión
 * @param gestion gestión de OT consultada o gestionada
 * @param puedeSolicitarOt si el proyecto admite una nueva solicitud de OT (RN04)
 */
public record OpinionTecnicaContexto(Usuario actor, Proyecto proyecto, OpinionTecnica gestion,
        boolean puedeSolicitarOt) {

    public static final String OPINION_TECNICA_YA_EMITIDA = "OPINION_TECNICA_YA_EMITIDA";
    public static final String SOLICITUD_OT_ARCHIVADA = "SOLICITUD_OT_ARCHIVADA";
    public static final String COMENTARIOS_DGICP_YA_ENVIADOS = "COMENTARIOS_DGICP_YA_ENVIADOS";
    public static final String ESTADO_PROYECTO_NO_PERMITE_ENVIO_COMENTARIOS =
            "ESTADO_PROYECTO_NO_PERMITE_ENVIO_COMENTARIOS";
    public static final String ESTADO_PROYECTO_NO_OBSERVADO = "ESTADO_PROYECTO_NO_OBSERVADO";

    /** @return si el actor es de la DGICP: registra comentarios y conclusiones (RN02) */
    public boolean esDgicp() {
        return actor.getRol() == RolUsuario.TECNICO_PRE || actor.getRol() == RolUsuario.COORDINADOR_PRE;
    }

    /** @return si el proyecto usa el formulario de emergencia en lugar del Anexo A.1 (RN13) */
    public boolean esEmergencia() {
        return Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia());
    }

    /** Si la gestión todavía no tiene Técnico PRE asignado y el actor lo es, queda como responsable. */
    public void asumirResponsable() {
        if (gestion.getTecnicoResponsable() == null && actor.getRol() == RolUsuario.TECNICO_PRE) {
            gestion.setTecnicoResponsable(actor);
        }
    }

    /** @return si el proyecto está en un estado que la DGICP revisa (RN05) */
    public boolean estadoRevisable() {
        return proyecto.getEstado() == EstadoProyecto.VIABLE || proyecto.getEstado() == EstadoProyecto.ELEGIBLE;
    }

    /**
     * RN07 b: una vez asignada la gestión, solo el Técnico PRE asignado la revisa y emite; el Coordinador
     * PRE conserva el acceso. Sin asignación, el primer Técnico PRE que actúa queda como responsable.
     *
     * @return si el actor es el responsable de la gestión o puede asumirla
     */
    public boolean esResponsable() {
        Usuario responsable = gestion.getTecnicoResponsable();
        return actor.getRol() != RolUsuario.TECNICO_PRE || responsable == null
                || responsable.getId().equals(actor.getId());
    }

    /** @return si el actor puede registrar comentarios DGICP y conclusiones en este momento */
    public boolean revisa() {
        return esDgicp() && esResponsable() && gestion.estaEnCurso() && estadoRevisable();
    }

    /** @return si el actor puede registrar alguna "Justificación Institución" en este momento */
    public boolean justifica() {
        return justificaProyecto() || justificaElegibilidad();
    }

    /**
     * RN03: el Técnico URP responde los comentarios a los apartados y a los documentos anexos, con la
     * gestión observada y el proyecto "Observado".
     *
     * @return si el actor puede justificar los comentarios al proyecto
     */
    public boolean justificaProyecto() {
        return actor.getRol() == RolUsuario.TECNICO_URP && gestion.estaObservada()
                && proyecto.getEstado() == EstadoProyecto.OBSERVADO;
    }

    /**
     * Anexo B.1 "Respuesta Institución": el Viabilizador responde los comentarios a la Elegibilidad
     * (solo en la primera gestión, RN 12) mientras no la haya reemitido (RN15 de CU-PRE-25). Decisión
     * sobre la contradicción RN03 vs. Anexo B.1 (Observaciones ítem 8).
     *
     * @return si el actor puede justificar los comentarios a la Elegibilidad
     */
    public boolean justificaElegibilidad() {
        EstadoProyecto estado = proyecto.getEstado();
        return actor.getRol() == RolUsuario.VIABILIZADOR && gestion.estaObservada()
                && Boolean.TRUE.equals(gestion.getPrimeraGestion())
                && (estado == EstadoProyecto.OBSERVADO || estado == EstadoProyecto.EN_VIABILIDAD
                        || estado == EstadoProyecto.VIABLE);
    }

    /**
     * Botones de la pantalla del Anexo A.1 para el actor: {@code visible} según su rol y
     * {@code habilitada} según el estado (RN04, RN05, RN06, RN10; FA01). El backend vuelve a validar
     * cada acción al ejecutarla.
     *
     * @return las acciones disponibles
     */
    public AccionesOpinionTecnicaDto acciones() {
        RolUsuario rol = actor.getRol();
        boolean urp = rol == RolUsuario.TECNICO_URP;
        boolean coordinador = rol == RolUsuario.COORDINADOR_PRE;
        boolean tecnicoPre = rol == RolUsuario.TECNICO_PRE;
        boolean solicitante = urp || rol == RolUsuario.VIABILIZADOR;
        boolean conConclusiones = gestion.getRevisionConclusiones().estanRegistradas();
        boolean vistoBueno = gestion.getRevisionConclusiones().tieneVistoBueno();
        return new AccionesOpinionTecnicaDto(
                accion(solicitante, puedeSolicitarOt),
                accion(esDgicp() || solicitante, revisa() || justifica()),
                accion(esDgicp(), revisa()),
                accion(urp, justifica()),
                accion(coordinador, revisa() && conConclusiones && !vistoBueno),
                accion(tecnicoPre, revisa() && vistoBueno));
    }

    /**
     * Campos habilitados para registro según rol y estado (RN02, RN03, RN 12).
     *
     * @return los campos editables
     */
    public CamposEditablesOpinionTecnicaDto camposEditables() {
        boolean revisa = revisa();
        return new CamposEditablesOpinionTecnicaDto(revisa, revisa,
                revisa && Boolean.TRUE.equals(gestion.getPrimeraGestion()), revisa, justificaProyecto(),
                justificaElegibilidad());
    }

    /**
     * Exige que la DGICP pueda revisar la gestión: en curso, con el proyecto viable o elegible y, para un
     * Técnico PRE, asignada a él (RN07 b).
     */
    public void exigirRevisable() {
        if (!esResponsable()) {
            throw new AccesoDenegadoException(
                    "La Opinión Técnica está asignada a otro Técnico PRE; solo el asignado puede revisarla.");
        }
        exigirNoCerrada();
        if (gestion.estaObservada()) {
            throw new ConflictoEstadoException(COMENTARIOS_DGICP_YA_ENVIADOS,
                    "Los comentarios DGICP ya fueron enviados; la gestión espera los ajustes de la institución.");
        }
        if (!estadoRevisable()) {
            throw new ConflictoEstadoException(ESTADO_PROYECTO_NO_PERMITE_ENVIO_COMENTARIOS,
                    "El proyecto debe estar en estado Proyecto viable o Proyecto Elegible para revisarlo.");
        }
    }

    /**
     * Exige que el actor pueda justificar: el Técnico URP con la gestión observada y el proyecto
     * "Observado" (RN03); el Viabilizador, los comentarios a la Elegibilidad antes de reemitirla.
     */
    public void exigirJustificable() {
        exigirNoCerrada();
        if (!justifica()) {
            throw new ConflictoEstadoException(ESTADO_PROYECTO_NO_OBSERVADO,
                    "Solo se puede registrar la Justificación Institución con la gestión observada y los "
                            + "comentarios pendientes de ajuste.");
        }
    }

    /** RN10 y RN09: ni la OT emitida ni una gestión archivada admiten cambios. */
    private void exigirNoCerrada() {
        if (gestion.esFavorable()) {
            throw new ConflictoEstadoException(OPINION_TECNICA_YA_EMITIDA,
                    "La Opinión Técnica ya fue emitida; la acción está inhabilitada.");
        }
        if (gestion.estaArchivada()) {
            throw new ConflictoEstadoException(SOLICITUD_OT_ARCHIVADA,
                    "El plazo de atención de comentarios caducó; debe gestionar nuevamente la Opinión Técnica.");
        }
    }

    private static EstadoAccionDto accion(boolean visible, boolean habilitada) {
        return new EstadoAccionDto(visible, visible && habilitada);
    }

    /** @return si el texto es nulo o solo tiene espacios */
    static boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
