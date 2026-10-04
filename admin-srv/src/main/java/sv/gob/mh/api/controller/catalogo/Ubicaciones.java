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
                .fromPath(CatalogosAdministracionController.BASE + CatalogosApi.PATH_CONSULTAR_CATALOGO)
                .buildAndExpand(codigo).encode().toUri();
    }

    /** @return la ruta de búsqueda por llave del registro creado */
    static URI registro(String codigo, String llave) {
        return UriComponentsBuilder
                .fromPath(CatalogosAdministracionController.BASE + RegistrosApi.PATH_BUSCAR_REGISTRO_POR_LLAVE)
                .buildAndExpand(codigo, llave).encode().toUri();
    }
}
