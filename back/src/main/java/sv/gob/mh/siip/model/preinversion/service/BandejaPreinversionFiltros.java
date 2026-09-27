package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;

/**
 * Filtros y paginación de las tablas de la Bandeja de Preinversión (CU-PRE-02): una solicitud
 * activa es la de un proyecto activo, enviado u observado en DGICP, que no está archivada ni
 * aprobada.
 */
final class BandejaPreinversionFiltros {

    /** Estados del proyecto con que una solicitud aparece en "Solicitudes Activas". */
    public static final List<EstadoProyecto> ESTADOS_PROYECTO_ACTIVOS = List.of(
            EstadoProyecto.ENVIADO_DGICP_REGISTRO, EstadoProyecto.OBSERVADO_DGICP_REGISTRO);

    /** Estados de la solicitud que la sacan de "Solicitudes Activas". */
    public static final List<EstadoSolicitud> ESTADOS_SOLICITUD_EXCLUIDOS = List.of(EstadoSolicitud.ARCHIVADA,
            EstadoSolicitud.APROBADA);

    private static final String ESTADO = "estado";
    private static final String PROYECTO = "proyecto";
    private static final int TAMANIO_PAGINA_POR_DEFECTO = 20;
    private static final int TAMANIO_PAGINA_MAXIMO = 200;

    private BandejaPreinversionFiltros() {
    }

    static Specification<SolicitudPreinversion> activas() {
        return (Root<SolicitudPreinversion> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.and(
                cb.isTrue(root.get(PROYECTO).get("activo")),
                root.get(ESTADO).in(EstadoSolicitud.ARCHIVADA, EstadoSolicitud.APROBADA).not(),
                root.get(PROYECTO).get(ESTADO).in(
                        EstadoProyecto.ENVIADO_DGICP_REGISTRO, EstadoProyecto.OBSERVADO_DGICP_REGISTRO));
    }

    static Specification<SolicitudPreinversion> archivadas() {
        return (Root<SolicitudPreinversion> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.equal(root.get(ESTADO), EstadoSolicitud.ARCHIVADA);
    }

    static Specification<SolicitudPreinversion> asignadasA(Long idTecnico) {
        return (Root<SolicitudPreinversion> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.equal(root.get("tecnicoAsignado").get("id"), idTecnico);
    }

    /** Sin tipo, no filtra. */
    static Specification<SolicitudPreinversion> deTipo(TipoSolicitudDto tipo) {
        return (Root<SolicitudPreinversion> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> tipo == null
                ? cb.conjunction()
                : cb.equal(root.get("tipoSolicitud"), TipoSolicitud.valueOf(tipo.name()));
    }

    /**
     * Página pedida (por defecto la primera, de 20 filas), de la solicitud más reciente a la más
     * antigua.
     *
     * @throws ValidacionNegocioException si la página es negativa o el tamaño está fuera de 1..200.
     */
    static PageRequest pagina(Integer pagina, Integer tamanio) {
        int p = pagina == null ? 0 : pagina;
        int t = tamanio == null ? TAMANIO_PAGINA_POR_DEFECTO : tamanio;
        if (p < 0 || t < 1 || t > TAMANIO_PAGINA_MAXIMO) {
            throw new ValidacionNegocioException("Paginación inválida.", List.of());
        }
        return PageRequest.of(p, t, Sort.by(Sort.Direction.DESC, "fechaSolicitud", "id"));
    }
}
