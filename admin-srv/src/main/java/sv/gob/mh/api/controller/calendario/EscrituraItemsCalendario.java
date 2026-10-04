package sv.gob.mh.api.controller.calendario;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.calendario.ExcepcionDto;
import sv.gob.mh.api.dto.calendario.PeriodoInputDto;
import sv.gob.mh.api.dto.calendario.PeriodoLaboralDto;
import sv.gob.mh.api.dto.calendario.PeriodoNoLaboralDto;
import sv.gob.mh.api.dto.calendario.RegistrarExcepcionRequestDto;
import sv.gob.mh.api.mapper.CalendarItemApiMapper;
import sv.gob.mh.api.mapper.DefinicionCalendarioApiMapper;
import sv.gob.mh.api.mapper.RecurrenciaApiMapper;
import sv.gob.mh.application.command.calendario.AgregarPeriodoCommand;
import sv.gob.mh.application.command.calendario.RegistrarExcepcionCommand;
import sv.gob.mh.application.handler.calendario.AgregarPeriodoHandler;
import sv.gob.mh.application.handler.calendario.RegistrarExcepcionHandler;
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * Alta de períodos y excepciones de un calendario (CU-ADM-04-02 a 04) para
 * {@link CalendarioGestionController}.
 */
@Component
public class EscrituraItemsCalendario {

    private final AgregarPeriodoHandler agregarPeriodo;
    private final RegistrarExcepcionHandler registrarExcepcion;

    public EscrituraItemsCalendario(AgregarPeriodoHandler agregarPeriodo,
            RegistrarExcepcionHandler registrarExcepcion) {
        this.agregarPeriodo = agregarPeriodo;
        this.registrarExcepcion = registrarExcepcion;
    }

    public ResponseEntity<PeriodoLaboralDto> agregarPeriodoLaboral(String codigoCalendario, PeriodoInputDto request) {
        var resultado = agregarPeriodo.handle(
                aCommand(codigoCalendario, TipoPeriodo.LABORAL, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CalendarItemApiMapper
                .aPeriodoLaboralDto(resultado.periodo(), resultado.calendario().getEstado()));
    }

    public ResponseEntity<PeriodoNoLaboralDto> agregarPeriodoNoLaboral(String codigoCalendario,
            PeriodoInputDto request) {
        var resultado = agregarPeriodo.handle(
                aCommand(codigoCalendario, TipoPeriodo.NO_LABORAL, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CalendarItemApiMapper
                .aPeriodoNoLaboralDto(resultado.periodo(), resultado.calendario().getEstado()));
    }

    public ResponseEntity<ExcepcionDto> registrarExcepcion(String codigoCalendario,
            RegistrarExcepcionRequestDto request) {
        var resultado = registrarExcepcion.handle(new RegistrarExcepcionCommand(
                codigoCalendario, request.getFecha(), DefinicionCalendarioApiMapper.aTipoExcepcion(request.getTipo()),
                request.getDescripcion()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CalendarItemApiMapper
                .aExcepcionDto(resultado.excepcion(), resultado.calendario().getEstado()));
    }

    private static AgregarPeriodoCommand aCommand(String codigoCalendario, TipoPeriodo tipo, PeriodoInputDto request) {
        return new AgregarPeriodoCommand(codigoCalendario, tipo, request.getCodigo(), request.getNombre(),
                RecurrenciaApiMapper.aRecurrencia(request.getRecurrencia()));
    }
}
