package sv.gob.mh.api.mapper;

import sv.gob.mh.api.dto.catalogo.ActiveStatusDto;
import sv.gob.mh.api.dto.catalogo.FieldQualifierDto;
import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.shared.enums.EstadoVigencia;

/** Enumerados del contrato CU-ADM-01 ↔ dominio, compartidos por los mappers de la capa API. */
final class EnumeradosApi {

    private EnumeradosApi() {
    }

    static EstadoVigencia aEstado(ActiveStatusDto activo) {
        return activo == null ? null : EstadoVigencia.valueOf(activo.name());
    }

    static ActiveStatusDto aActiveStatus(EstadoVigencia estado) {
        return ActiveStatusDto.valueOf(estado.name());
    }

    static FieldQualifierDto calificador(CampoDefinicion campo) {
        return campo.isEsKey() ? FieldQualifierDto.KEY : FieldQualifierDto.FIELD;
    }
}
