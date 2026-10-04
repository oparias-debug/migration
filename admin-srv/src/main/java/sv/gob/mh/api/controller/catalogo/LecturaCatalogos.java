package sv.gob.mh.api.controller.catalogo;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.catalogo.CatalogRecordsResultDto;
import sv.gob.mh.api.dto.catalogo.CatalogoDto;
import sv.gob.mh.api.dto.catalogo.CatalogoResumenDto;
import sv.gob.mh.api.dto.catalogo.ChildCatalogResultDto;
import sv.gob.mh.api.dto.catalogo.ChildRecordsResultDto;
import sv.gob.mh.api.dto.catalogo.KeySearchResultDto;
import sv.gob.mh.api.mapper.CatalogoApiMapper;
import sv.gob.mh.api.mapper.RegistroApiMapper;
import sv.gob.mh.application.query.catalogo.BuscarRegistroPorClaveQuery;
import sv.gob.mh.application.query.catalogo.BuscarRegistrosQuery;
import sv.gob.mh.application.query.catalogo.ConsultarCatalogoHijoQuery;
import sv.gob.mh.application.query.catalogo.ConsultarCatalogoQuery;
import sv.gob.mh.application.query.catalogo.ConsultarRegistrosHijosQuery;
import sv.gob.mh.application.query.catalogo.ListarCatalogosQuery;

/**
 * Consultas de CU-ADM-01 sobre catálogos y registros (HU-ADM-01-03, 04 y 12 a 15) para
 * {@link CatalogosAdministracionController}.
 */
@Component
public class LecturaCatalogos {

    private final ListarCatalogosQuery listarCatalogos;
    private final ConsultarCatalogoQuery consultarCatalogo;
    private final ConsultarCatalogoHijoQuery consultarCatalogoHijo;
    private final BuscarRegistrosQuery buscarRegistros;
    private final BuscarRegistroPorClaveQuery buscarRegistroPorClave;
    private final ConsultarRegistrosHijosQuery consultarRegistrosHijos;

    public LecturaCatalogos(ListarCatalogosQuery listarCatalogos,
            ConsultarCatalogoQuery consultarCatalogo,
            ConsultarCatalogoHijoQuery consultarCatalogoHijo,
            BuscarRegistrosQuery buscarRegistros,
            BuscarRegistroPorClaveQuery buscarRegistroPorClave,
            ConsultarRegistrosHijosQuery consultarRegistrosHijos) {
        this.listarCatalogos = listarCatalogos;
        this.consultarCatalogo = consultarCatalogo;
        this.consultarCatalogoHijo = consultarCatalogoHijo;
        this.buscarRegistros = buscarRegistros;
        this.buscarRegistroPorClave = buscarRegistroPorClave;
        this.consultarRegistrosHijos = consultarRegistrosHijos;
    }

    public ResponseEntity<List<CatalogoResumenDto>> buscarListarCatalogos(String codigo, String nombre) {
        return ResponseEntity.ok(
                listarCatalogos.ejecutar(codigo, nombre).stream().map(CatalogoApiMapper::aCatalogoResumen).toList());
    }

    public ResponseEntity<CatalogoDto> consultarCatalogo(String codigo) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogo(consultarCatalogo.ejecutar(codigo)));
    }

    /** SF-12: sin catálogo hijo el resultado es nulo (200 con cuerpo {@code null}, ver {@link CatalogoHijoNulo}). */
    public ResponseEntity<ChildCatalogResultDto> consultarCatalogoHijo(String codigo) {
        return ResponseEntity.ok(consultarCatalogoHijo.ejecutar(codigo)
                .map(CatalogoApiMapper::aChildCatalogResult)
                .orElse(null));
    }

    public ResponseEntity<CatalogRecordsResultDto> listarRegistros(String codigo, List<String> campos) {
        return ResponseEntity.ok(RegistroApiMapper.aCatalogRecordsResult(codigo, buscarRegistros.ejecutar(codigo, campos)));
    }

    public ResponseEntity<KeySearchResultDto> buscarRegistroPorLlave(String codigo, String llave, List<String> campos) {
        return ResponseEntity.ok(RegistroApiMapper.aKeySearchResult(llave, codigo,
                buscarRegistroPorClave.ejecutar(codigo, llave, campos)));
    }

    public ResponseEntity<ChildRecordsResultDto> buscarRegistrosHijos(String codigo, String llave,
            List<String> campos) {
        return ResponseEntity.ok(RegistroApiMapper.aChildRecordsResult(llave, codigo,
                consultarRegistrosHijos.ejecutar(codigo, llave, campos)));
    }
}
