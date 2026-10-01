package sv.gob.mh.api.mapper;

import sv.gob.mh.api.dto.calendario.EstadoCalendarioDto;
import sv.gob.mh.api.dto.calendario.TipoExcepcionDto;
import sv.gob.mh.shared.enums.EstadoCalendario;
import sv.gob.mh.shared.enums.TipoExcepcion;

/** Enumerados del contrato CU-ADM-04 ↔ dominio, compartidos por los mappers de calendarios. */
final class EnumeradosCalendarioApi {

    private EnumeradosCalendarioApi() {
    }

    static EstadoCalendario aEstado(EstadoCalendarioDto estado) {
        return EstadoCalendario.valueOf(estado.name());
    }

    static EstadoCalendarioDto aEstadoDto(EstadoCalendario estado) {
        return EstadoCalendarioDto.valueOf(estado.name());
    }

    static TipoExcepcion aTipoExcepcion(TipoExcepcionDto tipo) {
        return TipoExcepcion.valueOf(tipo.name());
    }

    static TipoExcepcionDto aTipoExcepcionDto(TipoExcepcion tipo) {
        return TipoExcepcionDto.valueOf(tipo.name());
    }
}
