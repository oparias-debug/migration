package sv.gob.mh.siip.model.preinversion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sv.gob.mh.siip.model.preinversion.domain.Localizacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.CoordenadasDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaLocalizacionRequestDto;

/**
 * Mapper para transformar entre entidades de dominio {@link Localizacion}
 * y los DTOs de filas asociados al caso de uso CU-PRE-12 utilizando MapStruct.
 *
 * @author Luis Medrano
 */
@Mapper(
        componentModel = "spring",
        imports = {
                Proyecto.class,
                java.math.BigDecimal.class
        }
)
public interface LocalizacionMapper {

    @Mapping(target = "departamento", expression = "java(entity.getDepartamento() != null ? entity.getDepartamento().getNombre() : null)")
    @Mapping(target = "distrito", expression = "java(entity.getMunicipio() != null ? entity.getMunicipio().getNombre() : null)")
    @Mapping(target = "direccionEspecifica", source = "direccion")
    @Mapping(target = "coordenadas", expression = "java(mapToCoordenadasDto(entity))")
    @Mapping(target = "requiereAdquisicionTerreno", source = "requiereAdquisicionTerreno")
    @Mapping(target = "propietario", source = "propietario")
    @Mapping(target = "especifique", source = "especifique")
    FilaLocalizacionRequestDto toFilaDto(Localizacion entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proyecto", expression = "java(idProyecto != null ? Proyecto.builder().id(idProyecto).build() : null)")
    @Mapping(target = "departamento", ignore = true) // Lo gestiona el servicio buscando/creando en catálogo
    @Mapping(target = "municipio", ignore = true)    // Lo gestiona el servicio buscando/creando en catálogo
    @Mapping(target = "direccion", source = "filaDto.direccionEspecifica")
    @Mapping(target = "latitud", expression = "java(filaDto.getCoordenadas() != null && filaDto.getCoordenadas().getLatitud() != null ? BigDecimal.valueOf(filaDto.getCoordenadas().getLatitud()) : null)")
    @Mapping(target = "longitud", expression = "java(filaDto.getCoordenadas() != null && filaDto.getCoordenadas().getLongitud() != null ? BigDecimal.valueOf(filaDto.getCoordenadas().getLongitud()) : null)")
    @Mapping(target = "requiereAdquisicionTerreno", source = "filaDto.requiereAdquisicionTerreno")
    @Mapping(target = "propietario", source = "filaDto.propietario")
    @Mapping(target = "especifique", source = "filaDto.especifique")
    Localizacion toEntity(Long idProyecto, FilaLocalizacionRequestDto filaDto);

    default CoordenadasDto mapToCoordenadasDto(Localizacion entity) {
        if (entity.getLatitud() == null || entity.getLongitud() == null) {
            return null;
        }
        CoordenadasDto dto = new CoordenadasDto();
        dto.setLatitud(entity.getLatitud().doubleValue());
        dto.setLongitud(entity.getLongitud().doubleValue());
        return dto;
    }
}