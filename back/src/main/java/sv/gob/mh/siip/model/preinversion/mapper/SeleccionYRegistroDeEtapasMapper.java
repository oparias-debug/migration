package sv.gob.mh.siip.model.preinversion.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.TipoCosto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoIndicadorDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;

/** Traduce las entidades de dominio de CU-PRE-3.5 a los DTOs generados desde CU-PRE-03.5.openapi.yaml. */
@Mapper(componentModel = "spring")
public interface SeleccionYRegistroDeEtapasMapper {

    @Mapping(target = "nombreEtapa", source = "tipoEtapa")
    EtapaDto toDto(EtapaPreinversion etapa);

    List<EtapaDto> toDtoList(List<EtapaPreinversion> etapas);

    TipoCostoResumenDto toDto(TipoCosto tipoCosto);

    ProductoIndicadorDto toDto(ProductoIndicadorCatalogo producto);

    List<ProductoIndicadorDto> toProductoIndicadorDtoList(List<ProductoIndicadorCatalogo> productos);
}
