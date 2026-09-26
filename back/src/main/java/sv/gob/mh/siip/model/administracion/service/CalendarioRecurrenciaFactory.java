package sv.gob.mh.siip.model.administracion.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.LinkedHashSet;

import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Recurrencia;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaMensual;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaSemanal;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaUnaVez;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaMensualDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaSemanalDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;

/**
 * CU-ADM-04 (Gestion de Calendarios): construccion de la recurrencia de un periodo a partir de su DTO, con
 * las validaciones de fechas de los CalendarItems contra el calendario (RN08, RN10).
 */
final class CalendarioRecurrenciaFactory {

    private CalendarioRecurrenciaFactory() {
    }

    static Recurrencia aRecurrencia(RecurrenciaDto dto, Calendario calendario) {
        if (dto instanceof RecurrenciaUnaVezDto unaVez) {
            LocalDate fechaInicio = unaVez.getFechaInicio();
            LocalDate fechaFin = unaVez.getFechaFin();
            exigirRangoValido(fechaInicio, fechaFin);
            exigirEnmarcadoEnCalendario(fechaInicio, fechaFin, calendario);
            return RecurrenciaUnaVez.builder().fechaInicio(fechaInicio).fechaFin(fechaFin).build();
        }
        if (dto instanceof RecurrenciaSemanalDto semanal) {
            LocalDate fechaInicio = semanal.getFechaInicio();
            LocalDate fechaFin = semanal.getFechaFin();
            exigirRangoValido(fechaInicio, fechaFin);
            exigirEnmarcadoEnCalendario(fechaInicio, fechaFin, calendario);
            EnumSet<DayOfWeek> dias = EnumSet.noneOf(DayOfWeek.class);
            semanal.getDiasSemana().forEach(dia -> dias.add(DayOfWeek.valueOf(dia.name())));
            return RecurrenciaSemanal.builder().fechaInicio(fechaInicio).fechaFin(fechaFin).diasDeLaSemana(dias)
                    .build();
        }
        if (dto instanceof RecurrenciaMensualDto mensual) {
            LinkedHashSet<Integer> diasDelMes = new LinkedHashSet<>(mensual.getDiasDelMes());
            EnumSet<Month> meses = EnumSet.noneOf(Month.class);
            mensual.getMeses().forEach(mes -> meses.add(Month.valueOf(mes.name())));
            return RecurrenciaMensual.builder().diasDelMes(diasDelMes).meses(meses).build();
        }
        throw new IllegalArgumentException("Tipo de recurrencia no soportado: " + dto.getClass());
    }

    /** RN10: una excepción debe caer dentro del rango del calendario. */
    static void exigirFechaEnmarcadaEnCalendario(LocalDate fecha, Calendario calendario) {
        if (fecha.isBefore(calendario.getFechaInicio()) || fecha.isAfter(calendario.getFechaFin())) {
            throw new InconsistenciaFechaException("EXCEPCION_FUERA_DE_RANGO",
                    "La fecha de la excepción no está enmarcada dentro del rango del calendario.");
        }
    }

    /** RN08: la fecha de inicio de un período no puede ser posterior a la fecha de fin. */
    private static void exigirRangoValido(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new InconsistenciaFechaException("PERIODO_RANGO_INVALIDO",
                    "La fecha de inicio del período es posterior a la fecha de fin.");
        }
    }

    /** RN10: todos los períodos deben quedar enmarcados en el período del calendario. */
    private static void exigirEnmarcadoEnCalendario(LocalDate fechaInicio, LocalDate fechaFin,
            Calendario calendario) {
        if (fechaInicio.isBefore(calendario.getFechaInicio()) || fechaFin.isAfter(calendario.getFechaFin())) {
            throw new InconsistenciaFechaException("PERIODO_FUERA_DE_RANGO",
                    "El período no está enmarcado dentro del rango del calendario.");
        }
    }
}
