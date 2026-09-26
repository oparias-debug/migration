package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Locale;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository.Specs;

/** Filtro del listado del Banco de Proyectos (CU-PRE-29) para {@link BancoProyectosServiceImpl}. */
final class BancoProyectosSpecs {

    /**
     * FB paso 1 (CU-PRE-29): "viabilizados, priorizados, con Opinión Técnica para Ejecución, en
     * ejecución y finalizados". Se incluyen además OBSERVADO y EN_OT porque el mockup del Anexo A.1
     * muestra filas "Observado" y "En proceso de OT".
     */
    private static final List<EstadoProyecto> ESTADOS_BANCO = List.of(
            EstadoProyecto.OBSERVADO,
            EstadoProyecto.VIABLE,
            EstadoProyecto.ELEGIBLE,
            EstadoProyecto.PRIORIZADO,
            EstadoProyecto.EN_OT,
            EstadoProyecto.PROYECTO_CON_OT,
            EstadoProyecto.EN_EJECUCION,
            EstadoProyecto.FINALIZADO);

    private BancoProyectosSpecs() {
    }

    /**
     * Proyectos válidos para captura en alguno de los estados del Banco, de la Unidad Ejecutora
     * indicada (todas si es {@code null}) y cuyo CUP o nombre contenga {@code busqueda}.
     */
    static Specification<Proyecto> listado(Long idUnidadEjecutora, String busqueda) {
        return Specs.fetchUnidadEjecutora()
                .and(Specs.esValidoParaCaptura())
                .and((Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                        root.get("estado").in(ESTADOS_BANCO))
                .and(Specs.byUnidadEjecutora(idUnidadEjecutora))
                .and(porCodigoONombre(busqueda));
    }

    private static Specification<Proyecto> porCodigoONombre(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return null;
        }
        String termino = "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";
        return (Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("cup")), termino),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), termino));
    }
}
