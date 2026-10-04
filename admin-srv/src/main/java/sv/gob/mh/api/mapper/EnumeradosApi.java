package sv.gob.mh.api.mapper;

import sv.gob.mh.api.dto.catalogo.CalificadorDto;
import sv.gob.mh.api.dto.catalogo.EstadoDto;
import sv.gob.mh.api.dto.catalogo.TipoCampoDto;
import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.enums.TipoCampo;

/** Enumerados del contrato CU-ADM-01 ↔ dominio, compartidos por los mappers de la capa API. */
final class EnumeradosApi {

    private EnumeradosApi() {
    }

    static EstadoVigencia aEstado(EstadoDto estado) {
        return estado == null ? null : EstadoVigencia.valueOf(estado.name());
    }

    static EstadoDto aEstadoDto(EstadoVigencia estado) {
        return EstadoDto.valueOf(estado.name());
    }

    static CalificadorDto calificador(CampoDefinicion campo) {
        return campo.isEsKey() ? CalificadorDto.KEY : CalificadorDto.FIELD;
    }

    /** El contrato usa la notación de la gramática del CU (NUMERIC, FECHA), el dominio NUMBER y DATE. */
    static TipoCampo aTipoCampo(TipoCampoDto tipo) {
        return switch (tipo) {
            case NUMERIC -> TipoCampo.NUMBER;
            case STRING -> TipoCampo.STRING;
            case FECHA -> TipoCampo.DATE;
            case ENUM -> TipoCampo.ENUM;
        };
    }

    static TipoCampoDto aTipoCampoDto(TipoCampo tipo) {
        return TipoCampoDto.fromValue(tipo.getNotacion());
    }
}
