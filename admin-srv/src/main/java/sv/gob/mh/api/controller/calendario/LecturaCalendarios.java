package sv.gob.mh.api.controller.calendario;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.calendario.CalendarioDto;
import sv.gob.mh.api.dto.calendario.CalendarioResumenDto;
import sv.gob.mh.api.dto.calendario.RangoFechasCalendarioResponseDto;
import sv.gob.mh.api.mapper.CalendarioApiMapper;
import sv.gob.mh.application.query.calendario.ConsultarCalendarioQuery;
import sv.gob.mh.application.query.calendario.ListarCalendariosQuery;
import sv.gob.mh.domain.model.calendario.Calendario;

/**
 * Consultas de CU-ADM-04 sobre la definición de los calendarios (CU-ADM-04-11 a 13) para
 * {@link CalendarioConsultasController}.
 */
@Component
public class LecturaCalendarios {

    private final ListarCalendariosQuery listarCalendarios;
    private final ConsultarCalendarioQuery consultarCalendario;

    public LecturaCalendarios(ListarCalendariosQuery listarCalendarios, ConsultarCalendarioQuery consultarCalendario) {
        this.listarCalendarios = listarCalendarios;
        this.consultarCalendario = consultarCalendario;
    }

    public ResponseEntity<List<CalendarioResumenDto>> listar() {
        return ResponseEntity.ok(listarCalendarios.ejecutar().stream().map(CalendarioApiMapper::aResumen).toList());
    }

    public ResponseEntity<CalendarioDto> recuperarDefinicion(String codigoCalendario) {
        return ResponseEntity.ok(CalendarioApiMapper.aCalendarioDto(consultarCalendario.ejecutar(codigoCalendario)));
    }

    public ResponseEntity<RangoFechasCalendarioResponseDto> consultarRangoFechas(String codigoCalendario) {
        Calendario calendario = consultarCalendario.ejecutar(codigoCalendario);
        return ResponseEntity.ok(new RangoFechasCalendarioResponseDto()
                .fechaDesde(calendario.getRango().desde())
                .fechaHasta(calendario.getRango().hasta()));
    }
}
