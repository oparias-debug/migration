package sv.gob.mh.siip.model.preinversion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;

@Mapper(componentModel = "spring")
public interface DescripcionTecnicaMapper {

    @Mapping(target = "descripcionProyecto", source = "proyecto.descripcionProyecto")
    @Mapping(target = "idProyecto", source = "proyecto.id")
    // Las filas salen de los Componentes del proyecto; las arma DescripcionTecnicaServiceImpl
    @Mapping(target = "filas", ignore = true)
    DescripcionTecnicaDto toDto(DescripcionTecnica entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proyecto", ignore = true)
    @Mapping(target = "descripcion", source = "descripcionProyecto")
    // El contrato (DescripcionTecnicaRequest) no expone estas columnas; hoy nadie las asigna
    @Mapping(target = "especificaciones", ignore = true)
    @Mapping(target = "vidaUtilAnios", ignore = true)
    DescripcionTecnica toEntity(DescripcionTecnicaRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proyecto", ignore = true)
    @Mapping(target = "descripcion", source = "descripcionProyecto")
    // Ignorarlas conserva el valor que ya tenga la entidad
    @Mapping(target = "especificaciones", ignore = true)
    @Mapping(target = "vidaUtilAnios", ignore = true)
    void updateEntityFromDto(DescripcionTecnicaRequestDto requestDto, @MappingTarget DescripcionTecnica entity);

    // Mapeo explicito delegando la transformación de TipoCostoResumenDto a mapComponenteToTipoCosto.
    // "producto" y "unidadMedida" se ignoran aquí (requieren resolver contra catálogos externos,
    // via repositorio, en tipos DTO que además viven en el paquete preinversion.dto y no
    // administracion.dto pese a compartir forma — ver DescripcionTecnicaServiceImpl); se resuelven
    // en el servicio tras invocar este mapeo.
    @Mapping(target = "componente", expression = "java(mapComponenteToTipoCosto(componente))")
    @Mapping(target = "descripcionProducto", source = "descripcion")
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "unidadMedida", ignore = true)
    FilaDescripcionTecnicaDto toFilaDto(Componente componente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", ignore = true)
    @Mapping(target = "descripcion", source = "descripcionProducto")
    @Mapping(target = "codigoProducto", source = "producto.codigoProducto")
    @Mapping(target = "proyecto", ignore = true)
    Componente toComponenteEntity(FilaDescripcionTecnicaRequestDto filaRequestDto);

    /**
     * Mapeador auxiliar explícito para transformar de entidad Componente a TipoCostoResumenDto.
     */
    default TipoCostoResumenDto mapComponenteToTipoCosto(Componente componente) {
        if (componente == null) {
            return null;
        }
        var dto = new TipoCostoResumenDto();
        dto.setCodigo((componente.getId() != null) ? String.valueOf(componente.getId()) : null);
        dto.setNombre(componente.getNombre());
        return dto;
    }
}
