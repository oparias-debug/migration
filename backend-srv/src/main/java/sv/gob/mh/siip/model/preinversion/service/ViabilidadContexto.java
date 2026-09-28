package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.AccionesDisponiblesViabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;

/**
 * Foto de una operación de CU-PRE-24 "Viabilidad": el actor que la ejecuta, el proyecto al que
 * accede y el estado de la gestión, derivado de la última {@link RevisionViabilidad} del proyecto.
 *
 * @param actor usuario autenticado que ejecuta la operación
 * @param proyecto proyecto consultado o gestionado
 * @param ultima última revisión del proyecto, o {@code null} si nunca solicitó Viabilidad
 * @param deshabilitada si la ficha está deshabilitada por una emisión vigente (FA02 paso 2.5)
 * @param documentoPreinversionCargado si ya se cargó el Documento de Preinversión (RN02)
 */
public record ViabilidadContexto(Usuario actor, Proyecto proyecto, RevisionViabilidad ultima,
        boolean deshabilitada, boolean documentoPreinversionCargado) {

    public static final String SOLICITUD_VIABILIDAD_EN_CURSO = "SOLICITUD_VIABILIDAD_EN_CURSO";
    public static final String SOLICITUD_VIABILIDAD_NO_VIGENTE = "SOLICITUD_VIABILIDAD_NO_VIGENTE";
    public static final String FICHA_VIABILIDAD_DESHABILITADA = "FICHA_VIABILIDAD_DESHABILITADA";

    /** @return si la última revisión sigue en curso (el Viabilizador la está revisando) */
    public boolean enCurso() {
        return ultima != null && ultima.getEstado() == EstadoRevisionViabilidad.EN_CURSO;
    }

    /** @return las observaciones generales de la última revisión, o {@code null} si no hay revisión */
    public String observacionesGenerales() {
        return ultima == null ? null : ultima.getObservacionesGenerales();
    }

    /** @return si la última revisión habilitó el paso a Elegibilidad (RN03) */
    public boolean habilitoElegibilidad() {
        return ultima != null && Boolean.TRUE.equals(ultima.getHabilitaElegibilidad());
    }

    /**
     * Acciones del Anexo A.1 habilitadas para el actor (RN02, RN04, RN06, RN07; Anexo B.1). El
     * backend vuelve a validar cada una al ejecutarla.
     *
     * @return las acciones disponibles
     */
    public AccionesDisponiblesViabilidadDto acciones() {
        boolean esTecnicoUrp = actor.getRol() == RolUsuario.TECNICO_URP;
        boolean esViabilizador = actor.getRol() == RolUsuario.VIABILIZADOR;
        boolean revisa = esViabilizador && enCurso();
        boolean solicita = esTecnicoUrp && documentoPreinversionCargado && !enCurso() && !deshabilitada;
        return new AccionesDisponiblesViabilidadDto(
                solicita,
                revisa,
                revisa,
                revisa && !esVacio(observacionesGenerales()),
                esViabilizador && deshabilitada && habilitoElegibilidad());
    }

    /**
     * Exige que la ficha admita una nueva solicitud (o cambios en sus documentos): ni emitida ni con
     * una revisión en curso (RN04).
     */
    public void exigirHabilitadaParaSolicitud() {
        if (deshabilitada) {
            throw fichaDeshabilitada();
        }
        if (enCurso()) {
            throw new ConflictoEstadoException(SOLICITUD_VIABILIDAD_EN_CURSO,
                    "El proyecto ya cuenta con una solicitud de Viabilidad en curso.");
        }
    }

    /**
     * Exige una revisión en curso sobre la que actúe el Viabilizador.
     *
     * @return la revisión en curso
     */
    public RevisionViabilidad exigirRevisionEnCurso() {
        if (deshabilitada) {
            throw fichaDeshabilitada();
        }
        if (!enCurso()) {
            throw new ConflictoEstadoException(SOLICITUD_VIABILIDAD_NO_VIGENTE,
                    "El proyecto no cuenta con una solicitud de Viabilidad vigente.");
        }
        return ultima;
    }

    /** @return si el texto es nulo o solo tiene espacios */
    static boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private static ConflictoEstadoException fichaDeshabilitada() {
        return new ConflictoEstadoException(FICHA_VIABILIDAD_DESHABILITADA,
                "La Viabilidad del proyecto ya fue emitida; la ficha no admite cambios.");
    }
}
