package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoListResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Consultas de proyectos del registro (CU-PRE-01): búsqueda por id, con y sin alcance por Unidad
 * Ejecutora, y listado paginado de proyectos activos.
 */
@Component
public class ProyectoConsultas {

    private final ProyectoRepository proyectoRepository;
    private final ProyectoMapper mapper;

    public ProyectoConsultas(ProyectoRepository proyectoRepository, ProyectoMapper mapper) {
        this.proyectoRepository = proyectoRepository;
        this.mapper = mapper;
    }

    /** @throws RecursoNoEncontradoException si el proyecto no existe. */
    public Proyecto buscarPorId(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    /**
     * Proyecto dentro del alcance del actor: el Técnico URP solo ve los de su propia Unidad
     * Ejecutora.
     *
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws sv.gob.mh.siip.exception.AccesoDenegadoException si el proyecto es de otra Unidad
     *         Ejecutora.
     */
    public Proyecto buscarVisible(Usuario actor, Long idProyecto) {
        Proyecto entidad = buscarPorId(idProyecto);
        ProyectoReglas.exigirAlcanceUnidadEjecutora(actor, entidad);
        return entidad;
    }

    /** Página de proyectos activos visibles para el actor, opcionalmente filtrada por estado. */
    public ProyectoListResponseDto listar(Usuario actor, Integer pagina, Integer tamanio,
            EstadoProyectoDto estadoFiltro) {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        EstadoProyecto estado = estadoFiltro == null ? null : EstadoProyecto.valueOf(estadoFiltro.name());

        // Solo el Técnico URP está adscrito a una Unidad Ejecutora (RN 1); el resto de roles
        // permitidos en esta bandeja (Técnico PRE, Administrador del Sistema, etc., ver x-roles
        // en el OpenAPI) no tienen una y por lo tanto ven el listado sin acotar por UE.
        Page<Proyecto> paginaResultado;
        if (actor.getUnidadEjecutora() == null) {
            paginaResultado = estado == null ? proyectoRepository.findByActivoTrue(pageable)
                    : proyectoRepository.findByActivoTrueAndEstado(estado, pageable);
        } else {
            Long idUnidadEjecutora = actor.getUnidadEjecutora().getId();
            paginaResultado = estado == null
                    ? proyectoRepository.findByActivoTrueAndUnidadEjecutoraId(idUnidadEjecutora, pageable)
                    : proyectoRepository.findByActivoTrueAndUnidadEjecutoraIdAndEstado(idUnidadEjecutora, estado,
                            pageable);
        }

        return new ProyectoListResponseDto()
                .contenido(paginaResultado.getContent().stream().map(mapper::toListItem).toList())
                .paginacion(new PaginacionMetadataDto()
                        .pagina(paginaResultado.getNumber())
                        .tamanio(paginaResultado.getSize())
                        .totalElementos(paginaResultado.getTotalElements())
                        .totalPaginas(paginaResultado.getTotalPages()));
    }
}
