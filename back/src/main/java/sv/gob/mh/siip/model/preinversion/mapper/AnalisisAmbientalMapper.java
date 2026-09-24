package sv.gob.mh.siip.model.preinversion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisAmbiental;
import sv.gob.mh.siip.model.preinversion.domain.ImpactosAmbientales;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaImpactoAmbientalRequestDto;

@Mapper(componentModel = "spring")
public interface AnalisisAmbientalMapper {

    // Mapeo del Padre: Del Entity al DTO (la entidad usa 'impactosAmbientales', el DTO usa 'filas')
    @Mapping(target = "totalCostoMedidasGestion", ignore = true) // Se calcula al vuelo en el servicio
    @Mapping(target = "filas", source = "impactosAmbientales")
    AnalisisAmbientalDto toDto(AnalisisAmbiental entity);

    // Del DTO al Entity (el DTO trae 'filas', la entidad guarda 'impactosAmbientales')
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proyecto", ignore = true) // Se inyecta manualmente en el service
    @Mapping(target = "impactosAmbientales", source = "filas")
    AnalisisAmbiental toEntity(AnalisisAmbientalDto dto);

    // Mapeo de filas individuales (si necesitas mapear cada ítem)
    FilaImpactoAmbientalRequestDto toFilaDto(ImpactosAmbientales entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "analisisAmbiental", ignore = true) // Se inyecta en el stream del service
    ImpactosAmbientales toFilaEntity(FilaImpactoAmbientalRequestDto dto);
}