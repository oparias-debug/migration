package sv.gob.mh.api.controller.catalogo;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

/** Header {@code Location} de los recursos que crea CU-ADM-01, con las rutas del contrato. */
final class Ubicaciones {

    private Ubicaciones() {
    }

    /** @return la ruta de consulta del catálogo creado */
    static URI catalogo(String codigo) {
        return UriComponentsBuilder
                .fromPath(CatalogosAdministracionController.BASE
                        + AdministracionCatalogosApi.PATH_CONSULTAR_CATALOGO_POR_CODIGO)
                .buildAndExpand(codigo).encode().toUri();
    }

    /** @return la ruta de consulta del registro creado */
    static URI registro(String codigo, String clave) {
        return UriComponentsBuilder
                .fromPath(CatalogosAdministracionController.BASE
                        + AdministracionCatalogosApi.PATH_BUSCAR_REGISTRO_POR_CLAVE)
                .buildAndExpand(codigo, clave).encode().toUri();
    }
}
