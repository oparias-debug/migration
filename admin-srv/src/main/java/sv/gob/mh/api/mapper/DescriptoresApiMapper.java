package sv.gob.mh.api.mapper;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import sv.gob.mh.api.dto.catalogo.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.DescriptoresCatalogoInformados;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores.Descriptor;

/**
 * Traducción de la actualización de descriptores de un catálogo (HU-ADM-01-06): distingue las
 * propiedades informadas de las omitidas (Regla 22).
 */
public final class DescriptoresApiMapper {

    private static final Map<String, Descriptor> DESCRIPTORES = Map.of(
            DescriptoresCatalogoInformados.PROPIEDAD_NAME, Descriptor.NOMBRE,
            DescriptoresCatalogoInformados.PROPIEDAD_PARENT, Descriptor.PADRE,
            DescriptoresCatalogoInformados.PROPIEDAD_ACTIVE, Descriptor.ESTADO,
            DescriptoresCatalogoInformados.PROPIEDAD_FROM_DATE, Descriptor.FECHA_DESDE,
            DescriptoresCatalogoInformados.PROPIEDAD_TO_DATE, Descriptor.FECHA_HASTA);

    private DescriptoresApiMapper() {
    }

    public static CambioDescriptores aCambioDescriptores(CatalogDescriptorsUpdateRequestDto request) {
        Set<Descriptor> informados = EnumSet.noneOf(Descriptor.class);
        DescriptoresCatalogoInformados.informadas(request)
                .forEach((String nombre) -> informados.add(DESCRIPTORES.get(nombre)));
        return new CambioDescriptores(informados, request.getName(), request.getParent(),
                EnumeradosApi.aEstado(request.getActive()), request.getFromDate(), request.getToDate());
    }
}
