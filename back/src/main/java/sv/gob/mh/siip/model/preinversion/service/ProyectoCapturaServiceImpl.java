package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository.Specs;

// DTOs generados por el plugin de OpenAPI
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoCapturaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.UnidadEjecutoraResumenDto;

/**
 * Implementación de la interfaz de servicios {@link ProyectoCapturaService}.
 *
 * @author Luis Medrano
 * @see ProyectoCapturaService
 * @see ProyectoCapturaRepository
 */
@Service
@Transactional
public class ProyectoCapturaServiceImpl implements ProyectoCapturaService {

    /** Repositorio de acceso a datos para proyectos en captura. */
    private final ProyectoCapturaRepository proyectoCapturaRepository;

    /**
     * Inyecta la dependencia del repositorio de captura.
     *
     * @param proyectoCapturaRepository instancia de {@link ProyectoCapturaRepository}.
     */
    public ProyectoCapturaServiceImpl(ProyectoCapturaRepository proyectoCapturaRepository) {
        this.proyectoCapturaRepository = proyectoCapturaRepository;
    }

    /**
     * Ejecuta la consulta de proyectos delegando las especificaciones a {@link Specs} y gestionando el objeto {@link Pageable}.
     *
     * @see ProyectoCapturaService#listarProyectosCaptura(String, String, String, IniciativaInversionDto, EstadoProyectoDto, Long, Integer, Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public ProyectosCapturaResponseDto listarProyectosCaptura(
            String busqueda,
            String cup,
            String nombreProyecto,
            IniciativaInversionDto iniciativaInversion,
            EstadoProyectoDto estado,
            Long idUnidadEjecutoraFiltro,
            Integer pagina,
            Integer tamanio) {



        Specification<Proyecto> spec = Specification.allOf(
                Specs.fetchUnidadEjecutora(),
                Specs.esValidoParaCaptura(),
                Specs.byBusquedaGeneral(busqueda),
                Specs.byCup(cup),
                Specs.byNombre(nombreProyecto),
                Specs.byIniciativa(iniciativaInversion != null ? iniciativaInversion.getValue() : null),
                Specs.byEstado(estado != null ? estado.getValue() : null)
        );

        Pageable pageable = PageRequest.of(
                (pagina != null && pagina >= 0) ? pagina : 0,
                (tamanio != null && tamanio > 0) ? tamanio : 20
        );

        Page<Proyecto> paginaEntidades = proyectoCapturaRepository.findAll(spec, pageable);

        return construirRespuesta(paginaEntidades);
    }

    /**
     * Construye la respuesta estructurada DTO a partir de la página de entidades.
     *
     * @param paginaEntidades datos de las entidades paginadas por Spring Data.
     * @return DTO encapsulador {@link ProyectosCapturaResponseDto}.
     */
    private ProyectosCapturaResponseDto construirRespuesta(Page<Proyecto> paginaEntidades) {
        List<ProyectoCapturaItemDto> contenido = paginaEntidades.getContent().stream()
                .map(this::toItemDto)
                .toList();

        PaginacionMetadataDto paginacion = new PaginacionMetadataDto();
        paginacion.setPagina(paginaEntidades.getNumber());
        paginacion.setTamanio(paginaEntidades.getSize());
        paginacion.setTotalElementos(paginaEntidades.getTotalElements());
        paginacion.setTotalPaginas(paginaEntidades.getTotalPages());

        ProyectosCapturaResponseDto response = new ProyectosCapturaResponseDto();
        response.setContenido(contenido);
        response.setPaginacion(paginacion);

        return response;
    }

    /**
     * Transforma una entidad {@link Proyecto} a su DTO proyectado {@link ProyectoCapturaItemDto}.
     *
     * @param entidad instancia de la entidad persistida.
     * @return DTO transformado.
     */
    private ProyectoCapturaItemDto toItemDto(Proyecto entidad) {
        ProyectoCapturaItemDto dto = new ProyectoCapturaItemDto();
        dto.setIdProyecto(entidad.getId());
        dto.setCup(entidad.getCup());
        dto.setNombreProyecto(entidad.getNombre());

        if (entidad.getUnidadEjecutora() != null) {
            UnidadEjecutoraResumenDto ueDto = new UnidadEjecutoraResumenDto();
            ueDto.setIdUnidadEjecutora(entidad.getUnidadEjecutora().getId());
            ueDto.setNombre(entidad.getUnidadEjecutora().getNombre());
            dto.setUnidadEjecutora(ueDto);
        }

        if (entidad.getIniciativaInversion() != null) {
            dto.setIniciativaInversion(
                    IniciativaInversionDto.fromValue(entidad.getIniciativaInversion().name())
            );
        }

        if (entidad.getEstado() != null) {
            dto.setEstado(
                    EstadoProyectoDto.fromValue(entidad.getEstado().name())
            );
        }

        return dto;
    }
}