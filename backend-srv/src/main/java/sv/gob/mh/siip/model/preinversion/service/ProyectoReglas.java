package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;

/**
 * Reglas de negocio del registro de proyectos (CU-PRE-01) que no dependen de datos externos:
 * estado inicial del registro, estados editables, regla de emergencia, habilitación de la
 * respuesta a observaciones y alcance por Unidad Ejecutora.
 */
final class ProyectoReglas {

    /** Estados desde los que el registro sigue siendo editable / puede volver a solicitarse el CUP. */
    private static final List<EstadoProyecto> ESTADOS_EDITABLES = List.of(EstadoProyecto.EN_REGISTRO,
            EstadoProyecto.OBSERVADO_DGICP_REGISTRO);

    private static final String CAMPO_OBLIGATORIO = "*Campo obligatorio";
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private ProyectoReglas() {
    }

    /** Fecha y hora actuales en la zona horaria de El Salvador. */
    static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA_EL_SALVADOR);
    }

    /**
     * Proyecto nuevo en estado En Registro, activo y adscrito a la Unidad Ejecutora e Institución
     * del Técnico URP que lo registra.
     */
    static Proyecto nuevoEnRegistro(Usuario actor) {
        var entidad = new Proyecto();
        entidad.setUnidadEjecutora(actor.getUnidadEjecutora());
        entidad.setInstitucion(actor.getInstitucion());
        entidad.setEstado(EstadoProyecto.EN_REGISTRO);
        entidad.setFechaIngreso(ahora());
        entidad.setActivo(true);
        return entidad;
    }

    /** @throws ConflictoEstadoException si el proyecto no está en un estado editable. */
    static void exigirEstadoEditable(Proyecto entidad) {
        if (!ESTADOS_EDITABLES.contains(entidad.getEstado())) {
            throw new ConflictoEstadoException(
                    "El proyecto no se encuentra en un estado que permita esta accion (estado actual: "
                            + entidad.getEstado() + ").");
        }
    }

    /**
     * Si el proyecto es de emergencia, el tipo de evento y el N. de Decreto Legislativo son
     * obligatorios.
     *
     * @throws ValidacionNegocioException con un detalle por cada campo faltante.
     */
    static void validarReglaEmergencia(Proyecto entidad) {
        if (!Boolean.TRUE.equals(entidad.getEsProyectoEmergencia())) {
            return;
        }
        List<ErrorDetalleDto> detalles = new ArrayList<>();
        if (entidad.getTipoEvento() == null || entidad.getTipoEvento().isBlank()) {
            detalles.add(new ErrorDetalleDto().campo("tipoEvento").mensaje(CAMPO_OBLIGATORIO));
        }
        if (entidad.getNumeroDecretoLegislativo() == null || entidad.getNumeroDecretoLegislativo().isBlank()) {
            detalles.add(new ErrorDetalleDto().campo("numeroDecretoLegislativo").mensaje(CAMPO_OBLIGATORIO));
        }
        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException(
                    "Si el proyecto es de emergencia, el tipo de evento y el N. de DL son obligatorios.", detalles);
        }
    }

    /**
     * El campo Respuesta solo está habilitado con el proyecto Observado DGICP (Registro) y es
     * obligatorio.
     */
    static void exigirRespuestaObservacionHabilitada(Proyecto entidad, RespuestaObservacionRequestDto request) {
        if (entidad.getEstado() != EstadoProyecto.OBSERVADO_DGICP_REGISTRO) {
            throw new ConflictoEstadoException(
                    "El proyecto no esta en estado Observado DGICP (Registro); el campo Respuesta no esta habilitado.");
        }
        if (request.getRespuesta().isBlank()) {
            throw new ValidacionNegocioException("El campo Respuesta es obligatorio.",
                    List.of(new ErrorDetalleDto().campo("respuesta").mensaje(CAMPO_OBLIGATORIO)));
        }
    }

    /**
     * Solo el Técnico URP está adscrito (y acotado) a su propia Unidad Ejecutora; el resto de roles
     * no tienen una y ven cualquier proyecto.
     */
    static void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto entidad) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(entidad.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }
}
