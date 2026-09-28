package sv.gob.mh.siip.model.preinversion.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoCapturaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.UnidadEjecutoraResumenDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;

/**
 * Ensambla la respuesta paginada de proyectos en captura (CU-PRE-03): mapea cada proyecto a su
 * ítem y resuelve su etapa actual en la Ruta de Preinversión (CU-PRE-03.5).
 *
 * @see ProyectoCapturaServiceImpl
 */
@Component
public class ProyectoCapturaEnsamblador {

    /** Etapas aceptadas de la Ruta de Preinversión (CU-PRE-03.5), para resolver {@code etapaActual}. */
    private final EtapaPreinversionRepository etapaPreinversionRepository;

    /**
     * Inyecta el repositorio de etapas de preinversión.
     *
     * @param etapaPreinversionRepository instancia de {@link EtapaPreinversionRepository}.
     */
    public ProyectoCapturaEnsamblador(EtapaPreinversionRepository etapaPreinversionRepository) {
        this.etapaPreinversionRepository = etapaPreinversionRepository;
    }

    /**
     * Construye la respuesta estructurada DTO a partir de la página de entidades.
     *
     * @param paginaEntidades datos de las entidades paginadas por Spring Data.
     * @return DTO encapsulador {@link ProyectosCapturaResponseDto}.
     */
    public ProyectosCapturaResponseDto construirRespuesta(Page<Proyecto> paginaEntidades) {
        Map<Long, NombreEtapaDto> etapaActualPorProyecto = etapaActualPorProyecto(paginaEntidades.getContent());

        List<ProyectoCapturaItemDto> contenido = paginaEntidades.getContent().stream()
                .map((Proyecto entidad) -> toItemDto(entidad, etapaActualPorProyecto.get(entidad.getId())))
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
    private static ProyectoCapturaItemDto toItemDto(Proyecto entidad, NombreEtapaDto etapaActual) {
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
     * Preinversión (CU-PRE-03.5) — {@code null} si el proyecto todavía no tiene ninguna etapa
     * aceptada. Una sola consulta para toda la página (ver contrato-CU-PRE-03.md, v1.1.0).
     */
    private Map<Long, NombreEtapaDto> etapaActualPorProyecto(List<Proyecto> proyectos) {
        List<Long> idsProyecto = proyectos.stream().map(Proyecto::getId).toList();
        return etapaPreinversionRepository.findByProyectoIdIn(idsProyecto).stream()
                .collect(Collectors.toMap(
                        (EtapaPreinversion etapa) -> etapa.getProyecto().getId(),
                        (EtapaPreinversion etapa) -> NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()),
                        BinaryOperator.maxBy(Comparator.comparing(ProyectoCapturaEnsamblador::ordenEnRuta))));
    }

    /** Posición de la etapa en la ruta PERFIL/PREFACTIBILIDAD/FACTIBILIDAD/DISENO/EJECUCION. */
    private static int ordenEnRuta(NombreEtapaDto etapa) {
        return TipoEtapaPreinversion.valueOf(etapa.name()).ordinal();
    }
}
