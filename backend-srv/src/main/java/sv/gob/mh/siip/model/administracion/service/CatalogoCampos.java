package sv.gob.mh.siip.model.administracion.service;

import java.util.List;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;
import sv.gob.mh.siip.model.administracion.enums.TipoCampo;

/**
 * CU-ADM-01 (Gestion de Catalogos): validacion (Reglas 2 y 3) y reemplazo de la definicion de campos de
 * un catalogo, compartidos por el alta y la actualizacion de campos.
 */
final class CatalogoCampos {

    private CatalogoCampos() {
    }

    /** Reglas 2 (al menos un KEY) y 3 (nombres unicos), compartidas por crear y actualizarCampos. */
    static void validarCampoKeyYNombresUnicos(List<CatalogFieldDto> campos) {
        boolean tieneKey = campos.stream().anyMatch(campo -> campo.getQualifier() == FieldQualifierDto.KEY);
        if (!tieneKey) {
            throw new ValidacionNegocioException("CAMPO_KEY_REQUERIDO",
                    "Debe existir al menos un campo con calificador KEY.", null);
        }
        long nombresUnicos = campos.stream().map(CatalogFieldDto::getName).distinct().count();
        if (nombresUnicos < campos.size()) {
            throw new ValidacionNegocioException("NOMBRES_CAMPO_REPETIDOS",
                    "Los nombres de los campos no deben repetirse.", null);
        }
    }

    static void reemplazarCampos(Catalogo catalogo, List<CatalogFieldDto> camposSolicitados) {
        catalogo.getCampos().clear();
        for (int posicion = 0; posicion < camposSolicitados.size(); posicion++) {
            catalogo.getCampos().add(aCampoDefinicion(camposSolicitados.get(posicion), catalogo, posicion));
        }
    }

    private static CampoDefinicion aCampoDefinicion(CatalogFieldDto dto, Catalogo catalogo, int posicionPorDefecto) {
        return CampoDefinicion.builder()
                .catalogo(catalogo)
                .nombre(dto.getName())
                .tipo(TipoCampo.STRING)
                .esKey(dto.getQualifier() == FieldQualifierDto.KEY)
                .posicion(dto.getPosition() != null ? dto.getPosition() : posicionPorDefecto)
                .build();
    }
}
