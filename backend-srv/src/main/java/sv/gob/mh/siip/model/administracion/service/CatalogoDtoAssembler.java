package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;

/**
 * CU-ADM-01 (Gestion de Catalogos): mapeo a DTO de un catalogo, de sus CatalogFields (en el orden de
 * {@link CatalogoReglas#POR_POSICION}) y de su resumen como catalogo hijo.
 */
final class CatalogoDtoAssembler {

    private CatalogoDtoAssembler() {
    }

    static CatalogDto aCatalogDto(Catalogo catalogo) {
        CatalogDto dto = new CatalogDto()
                .code(catalogo.getCodigo())
                .name(catalogo.getNombre())
                .parent(catalogo.getCatalogoPadreCodigo())
                .active(ActiveStatusDto.fromValue(catalogo.getEstado().name()))
                .fromDate(catalogo.getFechaDesde())
                .toDate(catalogo.getFechaHasta());
        catalogo.getCampos().stream().sorted(CatalogoReglas.POR_POSICION)
                .forEach(campo -> dto.addFieldsItem(aCatalogFieldDto(campo)));
        return dto;
    }

    static CatalogSummaryDto aCatalogSummaryDto(Catalogo catalogo) {
        return new CatalogSummaryDto().code(catalogo.getCodigo()).name(catalogo.getNombre());
    }

    private static CatalogFieldDto aCatalogFieldDto(CampoDefinicion campo) {
        return new CatalogFieldDto()
                .name(campo.getNombre())
                .qualifier(campo.isEsKey() ? FieldQualifierDto.KEY : FieldQualifierDto.FIELD)
                .position(campo.getPosicion());
    }
}
