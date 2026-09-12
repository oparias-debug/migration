package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository.Specs;
import sv.gob.mh.siip.security.ActorContexto;

// DTOs generados por el plugin de OpenAPI
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
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

    /** Etapas aceptadas de la Ruta de Preinversión (CU-PRE-3.5), para resolver {@code etapaActual}. */
    private final EtapaPreinversionRepository etapaPreinversionRepository;

    /** Resuelve el actor autenticado, para aplicar RN01/RN02 (alcance de visibilidad por rol). */
    private final ActorContexto actorContexto;

    /**
     * Inyecta las dependencias del repositorio de captura, de etapas de preinversión y del
     * contexto del actor autenticado.
     *
     * @param proyectoCapturaRepository instancia de {@link ProyectoCapturaRepository}.
     * @param etapaPreinversionRepository instancia de {@link EtapaPreinversionRepository}.
     * @param actorContexto instancia de {@link ActorContexto}.
     */
    public ProyectoCapturaServiceImpl(ProyectoCapturaRepository proyectoCapturaRepository,
            EtapaPreinversionRepository etapaPreinversionRepository, ActorContexto actorContexto) {
        this.proyectoCapturaRepository = proyectoCapturaRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.actorContexto = actorContexto;
    }

    /**
     * Ejecuta la consulta de proyectos delegando las especificaciones a {@link Specs} y gestionando el objeto {@link Pageable}.
     * RN01/RN02: el Técnico URP solo ve los proyectos de su propia Unidad Ejecutora; el resto de
     * los roles (Viabilizador, Usuarios Internos/Externos, Técnico PRE, Coordinador PRE) ve todos
     * los proyectos sin restricción de Unidad Ejecutora.
     *
     * @see ProyectoCapturaService#listarProyectosCaptura(ProyectoCapturaFiltro, Integer, Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public ProyectosCapturaResponseDto listarProyectosCaptura(
            ProyectoCapturaFiltro filtro,
            Integer pagina,
            Integer tamanio) {

        Usuario actor = actorContexto.exigir();
        IniciativaInversionDto iniciativaInversion = filtro.iniciativaInversion();
        EstadoProyectoDto estado = filtro.estado();

        Specification<Proyecto> spec = Specification.allOf(
                Specs.fetchUnidadEjecutora(),
                Specs.esValidoParaCaptura(),
                Specs.byBusquedaGeneral(filtro.busqueda()),
                Specs.byCup(filtro.cup()),
                Specs.byNombre(filtro.nombreProyecto()),
                Specs.byUnidadEjecutora(filtro.idUnidadEjecutoraFiltro()),
                Specs.byIniciativa(iniciativaInversion != null ? iniciativaInversion.getValue() : null),
                Specs.byEstado(estado != null ? estado.getValue() : null),
                Specs.byUnidadEjecutora(actor.getRol() == RolUsuario.TECNICO_URP
                        ? actor.getUnidadEjecutora().getId() : null)
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
        Map<Long, NombreEtapaDto> etapaActualPorProyecto = etapaActualPorProyecto(paginaEntidades.getContent());

        List<ProyectoCapturaItemDto> contenido = paginaEntidades.getContent().stream()
                .map(entidad -> toItemDto(entidad, etapaActualPorProyecto.get(entidad.getId())))
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
    private ProyectoCapturaItemDto toItemDto(Proyecto entidad, NombreEtapaDto etapaActual) {
        ProyectoCapturaItemDto dto = new ProyectoCapturaItemDto();
        dto.setIdProyecto(entidad.getId());
        dto.setCup(entidad.getCup());
        dto.setNombreProyecto(entidad.getNombre());
        dto.setEtapaActual(etapaActual);

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

    /**
     * Resuelve, para cada proyecto de la página, la etapa aceptada más avanzada de su Ruta de
     * Preinversión (CU-PRE-3.5) — {@code null} si el proyecto todavía no tiene ninguna etapa
     * aceptada. Una sola consulta para toda la página (ver contrato-CU-PRE-03.md, v1.1.0).
     */
    private Map<Long, NombreEtapaDto> etapaActualPorProyecto(List<Proyecto> proyectos) {
        List<Long> idsProyecto = proyectos.stream().map(Proyecto::getId).toList();
        return etapaPreinversionRepository.findByProyectoIdIn(idsProyecto).stream()
                .collect(Collectors.toMap(
                        e -> e.getProyecto().getId(),
                        EtapaPreinversion::getTipoEtapa,
                        (etapaActual, otraEtapa) -> otraEtapa.compareTo(etapaActual) > 0 ? otraEtapa : etapaActual))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> NombreEtapaDto.valueOf(entry.getValue().name())));
    }
}