package sv.gob.mh.api.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sv.gob.mh.api.dto.catalogo.CatalogRecordFieldValuesResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordResponseDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordValueRequestDto;
import sv.gob.mh.api.dto.catalogo.CatalogRecordValueResponseDto;
import sv.gob.mh.application.query.catalogo.RegistroProyectado;
import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.ValorCampo;

/** Traducción de los registros de catálogo del contrato CU-ADM-01 ↔ modelo de dominio. */
public final class RegistroApiMapper {

    private RegistroApiMapper() {
    }

    public static List<ValorCampo> aValores(Collection<CatalogRecordValueRequestDto> valores) {
        return valores.stream()
                .map((CatalogRecordValueRequestDto valor) -> new ValorCampo(valor.getField(), valor.getValor()))
                .toList();
    }

    /** Registro completo: todos sus valores en orden de posición, con su estado efectivo (Regla 12). */
    public static CatalogRecordResponseDto aCatalogRecordResponse(Registro registro) {
        Catalogo catalogo = registro.getCatalogo();
        CatalogRecordResponseDto dto = new CatalogRecordResponseDto()
                .catalog(catalogo.getCodigo())
                .parentRecord(clavePadre(registro))
                .active(EnumeradosApi.aActiveStatus(registro.estadoEfectivo()))
                .fromDate(registro.getFechaDesde())
                .toDate(registro.getFechaHasta())
                .values(new ArrayList<>());
        catalogo.camposOrdenados().forEach((CampoDefinicion campo) -> dto.addValuesItem(aValor(campo, registro)));
        return dto;
    }

    /** Registro reducido a los campos pedidos (Reglas 4 y 5). */
    public static CatalogRecordFieldValuesResponseDto aFieldValues(RegistroProyectado proyectado) {
        Registro registro = proyectado.registro();
        return new CatalogRecordFieldValuesResponseDto()
                .keyValue(registro.getClave())
                .parentRecord(clavePadre(registro))
                .active(EnumeradosApi.aActiveStatus(registro.estadoEfectivo()))
                .values(proyectado.campos().stream().map((CampoDefinicion campo) -> aValor(campo, registro)).toList());
    }

    private static CatalogRecordValueResponseDto aValor(CampoDefinicion campo, Registro registro) {
        return new CatalogRecordValueResponseDto()
                .field(campo.getNombre())
                .qualifier(EnumeradosApi.calificador(campo))
                .valor(registro.valor(campo));
    }

    /** Regla 23: el KEY del registro padre, o {@code null} si el catálogo no tiene padre. */
    private static String clavePadre(Registro registro) {
        return registro.getRegistroPadre() != null ? registro.getRegistroPadre().clave() : null;
    }
}
