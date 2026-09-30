package sv.gob.mh.api.controller.catalogo;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.catalogo.CatalogChildResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogExistenceResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordFieldValuesResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogSummaryResponseDto;
import sv.gob.mh.api.mapper.CatalogoApiMapper;
import sv.gob.mh.api.mapper.RegistroApiMapper;
import sv.gob.mh.application.query.catalogo.BuscarRegistroPorClaveQuery;
import sv.gob.mh.application.query.catalogo.BuscarRegistrosQuery;
import sv.gob.mh.application.query.catalogo.ConsultarCatalogoQuery;
import sv.gob.mh.application.query.catalogo.ConsultarCatalogosHijosQuery;
import sv.gob.mh.application.query.catalogo.ConsultarRegistrosHijosQuery;
import sv.gob.mh.application.query.catalogo.ListarCatalogosQuery;
import sv.gob.mh.application.query.catalogo.VerificarExistenciaCatalogoQuery;

/**
 * Consultas de CU-ADM-01 sobre catálogos y registros (HU-ADM-01-02 a 05, 10, 11 y 15) para
 * {@link CatalogosAdministracionController}.
 */
@Component
public class LecturaCatalogos {

    private final ListarCatalogosQuery listarCatalogos;
    private final VerificarExistenciaCatalogoQuery verificarExistencia;
    private final ConsultarCatalogoQuery consultarCatalogo;
    private final ConsultarCatalogosHijosQuery consultarCatalogosHijos;
    private final BuscarRegistrosQuery buscarRegistros;
    private final BuscarRegistroPorClaveQuery buscarRegistroPorClave;
    private final ConsultarRegistrosHijosQuery consultarRegistrosHijos;

    public LecturaCatalogos(ListarCatalogosQuery listarCatalogos,
            VerificarExistenciaCatalogoQuery verificarExistencia,
            ConsultarCatalogoQuery consultarCatalogo,
            ConsultarCatalogosHijosQuery consultarCatalogosHijos,
            BuscarRegistrosQuery buscarRegistros,
            BuscarRegistroPorClaveQuery buscarRegistroPorClave,
            ConsultarRegistrosHijosQuery consultarRegistrosHijos) {
        this.listarCatalogos = listarCatalogos;
        this.verificarExistencia = verificarExistencia;
        this.consultarCatalogo = consultarCatalogo;
        this.consultarCatalogosHijos = consultarCatalogosHijos;
        this.buscarRegistros = buscarRegistros;
        this.buscarRegistroPorClave = buscarRegistroPorClave;
        this.consultarRegistrosHijos = consultarRegistrosHijos;
    }

    public ResponseEntity<List<CatalogSummaryResponseDto>> listarCatalogos() {
        return ResponseEntity.ok(listarCatalogos.ejecutar().stream().map(CatalogoApiMapper::aCatalogSummary).toList());
    }

    public ResponseEntity<CatalogExistenceResponseDto> verificarExistencia(String name) {
        return ResponseEntity.ok(new CatalogExistenceResponseDto(name, verificarExistencia.ejecutar(name)));
    }

    public ResponseEntity<CatalogResponseDto> consultarCatalogo(String code) {
        return ResponseEntity.ok(CatalogoApiMapper.aCatalogResponse(consultarCatalogo.ejecutar(code)));
    }

    public ResponseEntity<List<CatalogChildResponseDto>> consultarCatalogosHijos(String code) {
        return ResponseEntity.ok(
                consultarCatalogosHijos.ejecutar(code).stream().map(CatalogoApiMapper::aCatalogChild).toList());
    }

    public ResponseEntity<List<CatalogRecordFieldValuesResponseDto>> buscarRegistros(String code,
            List<String> fields) {
        return ResponseEntity.ok(
                buscarRegistros.ejecutar(code, fields).stream().map(RegistroApiMapper::aFieldValues).toList());
    }

    public ResponseEntity<CatalogRecordFieldValuesResponseDto> buscarRegistroPorClave(String code, String keyValue,
            List<String> fields) {
        return ResponseEntity.ok(RegistroApiMapper.aFieldValues(buscarRegistroPorClave.ejecutar(code, keyValue,
                fields)));
    }

    public ResponseEntity<List<CatalogRecordResponseDto>> consultarRegistrosHijos(String code, String keyValue) {
        return ResponseEntity.ok(consultarRegistrosHijos.ejecutar(code, keyValue).stream()
                .map(RegistroApiMapper::aCatalogRecordResponse)
                .toList());
    }
}
