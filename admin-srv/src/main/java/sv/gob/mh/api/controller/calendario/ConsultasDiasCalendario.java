package sv.gob.mh.api.controller.calendario;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import sv.gob.mh.api.dto.calendario.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.api.dto.calendario.DiasRestantesResponseDto;
import sv.gob.mh.api.dto.calendario.DuracionPeriodoResponseDto;
import sv.gob.mh.api.dto.calendario.FechaLaboralResultanteResponseDto;
import sv.gob.mh.api.dto.calendario.PertenenciaPeriodoResponseDto;
import sv.gob.mh.api.dto.calendario.TipoDiaDto;
import sv.gob.mh.api.dto.calendario.TipoDiaResponseDto;
import sv.gob.mh.application.query.calendario.CalcularFechaLaboralResultanteQuery;
import sv.gob.mh.application.query.calendario.ConsultarDiasRestantesQuery;
import sv.gob.mh.application.query.calendario.ConsultarDuracionPeriodoQuery;
import sv.gob.mh.application.query.calendario.ConsultarPertenenciaPeriodoQuery;
import sv.gob.mh.application.query.calendario.ConsultarTipoDiaQuery;
import sv.gob.mh.application.query.calendario.ContarDiasLaboralesQuery;

/**
 * Consultas de días de CU-ADM-04 (CU-ADM-04-05 a 10) para {@link CalendarioConsultasController}:
 * una query por operación del contrato.
 */
@Component
public class ConsultasDiasCalendario {

    private final ConsultarTipoDiaQuery consultarTipoDia;
    private final ConsultarPertenenciaPeriodoQuery consultarPertenencia;
    private final ConsultarDuracionPeriodoQuery consultarDuracion;
    private final ConsultarDiasRestantesQuery consultarDiasRestantes;
    private final ContarDiasLaboralesQuery contarDiasLaborales;
    private final CalcularFechaLaboralResultanteQuery calcularFechaLaboral;

    public ConsultasDiasCalendario(ConsultarTipoDiaQuery consultarTipoDia,
            ConsultarPertenenciaPeriodoQuery consultarPertenencia,
            ConsultarDuracionPeriodoQuery consultarDuracion,
            ConsultarDiasRestantesQuery consultarDiasRestantes,
            ContarDiasLaboralesQuery contarDiasLaborales,
            CalcularFechaLaboralResultanteQuery calcularFechaLaboral) {
        this.consultarTipoDia = consultarTipoDia;
        this.consultarPertenencia = consultarPertenencia;
        this.consultarDuracion = consultarDuracion;
        this.consultarDiasRestantes = consultarDiasRestantes;
        this.contarDiasLaborales = contarDiasLaborales;
        this.calcularFechaLaboral = calcularFechaLaboral;
    }

    public ResponseEntity<TipoDiaResponseDto> tipoDia(String codigoCalendario, LocalDate fecha) {
        return ResponseEntity.ok(new TipoDiaResponseDto()
                .fecha(fecha)
                .tipo(TipoDiaDto.valueOf(consultarTipoDia.ejecutar(codigoCalendario, fecha).name())));
    }

    public ResponseEntity<PertenenciaPeriodoResponseDto> pertenencia(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha) {
        return ResponseEntity.ok(new PertenenciaPeriodoResponseDto()
                .pertenece(consultarPertenencia.ejecutar(codigoCalendario, codigoPeriodo, fecha)));
    }

    public ResponseEntity<DuracionPeriodoResponseDto> duracion(String codigoCalendario, String codigoPeriodo) {
        return ResponseEntity.ok(new DuracionPeriodoResponseDto()
                .duracionDias(consultarDuracion.ejecutar(codigoCalendario, codigoPeriodo)));
    }

    public ResponseEntity<DiasRestantesResponseDto> diasRestantes(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha) {
        return ResponseEntity.ok(new DiasRestantesResponseDto()
                .diasRestantes(consultarDiasRestantes.ejecutar(codigoCalendario, codigoPeriodo, fecha)));
    }

    public ResponseEntity<DiasLaboralesEntreFechasResponseDto> diasLaboralesEntre(String codigoCalendario,
            LocalDate fechaInicio, LocalDate fechaFin) {
        return ResponseEntity.ok(new DiasLaboralesEntreFechasResponseDto()
                .diasLaborales(contarDiasLaborales.ejecutar(codigoCalendario, fechaInicio, fechaFin)));
    }

    public ResponseEntity<FechaLaboralResultanteResponseDto> fechaLaboralResultante(String codigoCalendario,
            LocalDate fecha, Integer diasHabiles) {
        return ResponseEntity.ok(new FechaLaboralResultanteResponseDto()
                .fecha(calcularFechaLaboral.ejecutar(codigoCalendario, fecha, diasHabiles)));
    }
}
