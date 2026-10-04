package sv.gob.mh.api.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sv.gob.mh.api.dto.catalogo.CatalogRecordsResultDto;
import sv.gob.mh.api.dto.catalogo.ChildRecordsResultDto;
import sv.gob.mh.api.dto.catalogo.KeySearchResultDto;
import sv.gob.mh.api.dto.catalogo.RegistroCreacionDto;
import sv.gob.mh.api.dto.catalogo.RegistroDto;
import sv.gob.mh.api.dto.catalogo.ResultRowDto;
import sv.gob.mh.application.command.catalogo.CrearRegistroCommand;
import sv.gob.mh.application.query.catalogo.ConjuntoResultado;
import sv.gob.mh.application.query.catalogo.ConsultarRegistrosHijosQuery.RegistrosHijos;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.ValorCampo;

/**
 * Traducción de los registros de catálogo del contrato CU-ADM-01 ↔ modelo de dominio, y de los
 * resultados de búsqueda SF-10, SF-11 y SF-13 con la estructura literal del CU.
 */
public final class RegistroApiMapper {

    private RegistroApiMapper() {
    }

    public static CrearRegistroCommand aCommand(String codigoCatalogo, RegistroCreacionDto request) {
        return new CrearRegistroCommand(codigoCatalogo, aValores(request.getValores()), request.getRegistroPadre(),
                CatalogoApiMapper.desde(request.getVigencia()), CatalogoApiMapper.hasta(request.getVigencia()));
    }

    /** Nombre de campo → valor (STRING), como lo envía el contrato. */
    public static List<ValorCampo> aValores(Map<String, String> valores) {
        return valores == null ? List.of()
                : valores.entrySet().stream().map(valor -> new ValorCampo(valor.getKey(), valor.getValue())).toList();
    }

    /** Registro completo: todos sus valores en orden de posición, con su estado efectivo (RN-06). */
    public static RegistroDto aRegistro(Registro registro) {
        Map<String, String> valores = new LinkedHashMap<>();
        registro.getCatalogo().camposOrdenados().forEach(campo -> valores.put(campo.getNombre(), registro.valor(campo)));
        return new RegistroDto()
                .codigoCatalogo(registro.getCatalogo().getCodigo())
                .llave(registro.getClave())
                .registroPadre(registro.getRegistroPadre() != null ? registro.getRegistroPadre().clave() : null)
                .valores(valores)
                .estado(EnumeradosApi.aEstadoDto(registro.estadoEfectivo()))
                .vigencia(CatalogoApiMapper.aVigencia(registro.getFechaDesde(), registro.getFechaHasta()));
    }

    /** SF-10. */
    public static KeySearchResultDto aKeySearchResult(String argumento, String codigoCatalogo,
            ConjuntoResultado conjunto) {
        return new KeySearchResultDto(argumento, codigoCatalogo, conjunto.campos(), filas(conjunto));
    }

    /** SF-11. */
    public static CatalogRecordsResultDto aCatalogRecordsResult(String codigoCatalogo, ConjuntoResultado conjunto) {
        return new CatalogRecordsResultDto(codigoCatalogo, conjunto.campos(), filas(conjunto));
    }

    /** SF-13. */
    public static ChildRecordsResultDto aChildRecordsResult(String argumento, String codigoCatalogoPadre,
            RegistrosHijos hijos) {
        return new ChildRecordsResultDto(argumento, codigoCatalogoPadre, hijos.codigoCatalogoHijo(),
                hijos.conjunto().campos(), filas(hijos.conjunto()));
    }

    private static List<ResultRowDto> filas(ConjuntoResultado conjunto) {
        return conjunto.filas().stream()
                .map(fila -> new ResultRowDto(new ArrayList<>(fila.valores()), EnumeradosApi.aEstadoDto(fila.estado())))
                .toList();
    }
}
