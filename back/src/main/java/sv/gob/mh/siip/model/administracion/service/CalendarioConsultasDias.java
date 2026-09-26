package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.FechaLaboralResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;

/**
 * CU-ADM-04 (Gestion de Calendarios): consultas de dias sobre un calendario (tipo de dia de una fecha, dias
 * restantes de un periodo LABORAL, dias laborales entre dos fechas y fecha LABORAL resultante), abiertas a
 * cualquier usuario autenticado (RN18).
 */
final class CalendarioConsultasDias {

    /** Limite de dias explorados al buscar una fecha LABORAL resultante, para no ciclar indefinidamente (RN07). */
    private static final int LIMITE_DIAS_EXPLORADOS = 100_000;

    private final CalendarioBusqueda busqueda;

    CalendarioConsultasDias(CalendarioRepository calendarioRepository, PeriodoRepository periodoRepository) {
        this.busqueda = new CalendarioBusqueda(calendarioRepository, periodoRepository);
    }

    TipoDiaResponseDto consultarTipoDia(String codigoCalendario, LocalDate fecha) {
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);
        TipoPeriodo tipo = CalendarioClasificacion.clasificarFecha(calendario, fecha)
                .orElseThrow(() -> new InconsistenciaFechaException("FECHA_SIN_PERIODO",
                        "La fecha dada no cae en ningún período ni excepción definidos del calendario."));
        return new TipoDiaResponseDto().fecha(fecha).tipo(TipoDiaDto.valueOf(tipo.name()));
    }

    DiasRestantesResponseDto consultarDiasRestantesPeriodoLaboral(String codigoCalendario, String codigoPeriodo,
            LocalDate fecha) {
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);
        Periodo periodo = busqueda.obtenerPeriodoLaboral(codigoCalendario, codigoPeriodo);
        CalendarioClasificacion.exigirFechaEnPeriodo(periodo, fecha);
        LocalDate finDelPeriodo = CalendarioClasificacion.finDelPeriodo(periodo, calendario);
        int diasRestantes = (int) ChronoUnit.DAYS.between(fecha, finDelPeriodo);
        return new DiasRestantesResponseDto().diasRestantes(diasRestantes);
    }

    DiasLaboralesEntreFechasResponseDto consultarDiasLaboralesEntreFechas(String codigoCalendario,
            LocalDate fechaInicio, LocalDate fechaFin) {
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);
        if (fechaInicio.isAfter(fechaFin)) {
            throw new InconsistenciaFechaException("FECHAS_INCONSISTENTES",
                    "La fecha inicial es posterior a la fecha final.");
        }
        if (!CalendarioClasificacion.dentroDelCalendario(calendario, fechaInicio)
                || !CalendarioClasificacion.dentroDelCalendario(calendario, fechaFin)) {
            throw new InconsistenciaFechaException("FECHAS_FUERA_DE_RANGO",
                    "La fecha inicial o la fecha final no están dentro del rango del calendario.");
        }

        int diasLaborales = 0;
        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
            if (CalendarioClasificacion.esDiaLaboral(calendario, fecha)) {
                diasLaborales++;
            }
        }
        // RN06 (seccion 14): la convencion de conteo excluye uno de los extremos del rango.
        return new DiasLaboralesEntreFechasResponseDto().diasLaborales(Math.max(diasLaborales - 1, 0));
    }

    FechaLaboralResultanteResponseDto calcularFechaLaboralResultante(String codigoCalendario, LocalDate fecha,
            Integer diasHabiles) {
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);

        LocalDate fechaResultante = fecha;
        int diasContados = 0;
        int diasExplorados = 0;
        while (diasContados < diasHabiles) {
            fechaResultante = fechaResultante.plusDays(1);
            diasExplorados++;
            if (diasExplorados > LIMITE_DIAS_EXPLORADOS) {
                throw new InconsistenciaFechaException("LIMITE_EXPLORACION_EXCEDIDO",
                        "No fue posible determinar la fecha LABORAL resultante dentro de un rango razonable.");
            }
            if (CalendarioClasificacion.esDiaLaboral(calendario, fechaResultante)) {
                diasContados++;
            }
        }

        TipoPeriodo tipoResultante = CalendarioClasificacion.clasificarFecha(calendario, fechaResultante)
                .orElseThrow(() -> new InconsistenciaFechaException("FECHA_RESULTANTE_SIN_PERIODO",
                        "La fecha resultante no cae en ningún período definido del calendario."));
        if (tipoResultante != TipoPeriodo.LABORAL) {
            throw new InconsistenciaFechaException("FECHA_RESULTANTE_NO_LABORAL",
                    "La fecha resultante no cae en un período LABORAL.");
        }

        return new FechaLaboralResultanteResponseDto().fecha(fechaResultante);
    }
}
