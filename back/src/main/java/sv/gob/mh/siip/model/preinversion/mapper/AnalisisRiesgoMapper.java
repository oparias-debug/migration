package sv.gob.mh.siip.model.preinversion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisRiesgo;
import sv.gob.mh.siip.model.preinversion.domain.RiesgosDesastresInminentes;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaRiesgoDto;

/**
 * Mapper basado en MapStruct para la conversión entre las entidades JPA y los DTOs
 * del módulo de Análisis de Riesgo (CU-PRE-15).
 *
 * @author Luis Medrano
 * @version 1.0
 * @since 2026-09-20
 */
@Mapper(componentModel = "spring")
public interface AnalisisRiesgoMapper {

    /**
     * Mapea la entidad cabecera {@link AnalisisRiesgo} hacia el {@link AnalisisRiesgoDto} de la API.
     * Mapea automáticamente la lista de filas de detalle y extrae el ID del proyecto desde la relación.
     *
     * @param entidad Entidad JPA cabecera.
     * @return DTO con la estructura completa para la respuesta HTTP.
     * @author Luis Medrano
     */
    @Mapping(source = "filas", target = "filas")
    @Mapping(target = "idProyecto", source = "proyecto.id")
    AnalisisRiesgoDto toDto(AnalisisRiesgo entidad);

    /**
     * Mapea la entidad hija {@link RiesgosDesastresInminentes} hacia el {@link FilaRiesgoDto} de la grilla.
     *
     * @param fila Entidad JPA de detalle.
     * @return DTO de fila individual para el contrato OpenAPI.
     * @author Luis Medrano
     */
    FilaRiesgoDto toFilaDto(RiesgosDesastresInminentes fila);
}