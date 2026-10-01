package sv.gob.mh.api.mapper;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Set;
import java.util.stream.Collectors;

import sv.gob.mh.api.dto.calendario.DayOfWeekDto;
import sv.gob.mh.api.dto.calendario.MonthDto;
import sv.gob.mh.api.dto.calendario.RecurrenciaDto;
import sv.gob.mh.api.dto.calendario.RecurrenciaMensualDto;
import sv.gob.mh.api.dto.calendario.RecurrenciaSemanalDto;
import sv.gob.mh.api.dto.calendario.RecurrenciaUnaVezDto;
import sv.gob.mh.domain.model.calendario.Recurrencia;
import sv.gob.mh.domain.model.calendario.RecurrenciaMensual;
import sv.gob.mh.domain.model.calendario.RecurrenciaSemanal;
import sv.gob.mh.domain.model.calendario.RecurrenciaUnaVez;

/** Traducción de las recurrencias de los períodos del contrato CU-ADM-04 ↔ modelo de dominio. */
public final class RecurrenciaApiMapper {

    private RecurrenciaApiMapper() {
    }

    public static Recurrencia aRecurrencia(RecurrenciaDto dto) {
        return switch (dto) {
            case RecurrenciaUnaVezDto unaVez -> new RecurrenciaUnaVez(unaVez.getFechaInicio(), unaVez.getFechaFin());
            case RecurrenciaSemanalDto semanal -> new RecurrenciaSemanal(semanal.getFechaInicio(),
                    semanal.getFechaFin(), semanal.getDiasSemana().stream()
                            .map((DayOfWeekDto dia) -> DayOfWeek.valueOf(dia.name()))
                            .collect(Collectors.toSet()));
            case RecurrenciaMensualDto mensual -> new RecurrenciaMensual(Set.copyOf(mensual.getDiasDelMes()),
                    mensual.getMeses().stream()
                            .map((MonthDto mes) -> Month.valueOf(mes.name()))
                            .collect(Collectors.toSet()));
            default -> throw new IllegalArgumentException("Tipo de recurrencia no soportado: " + dto.getClass());
        };
    }

    /** Días de la semana, días del mes y meses salen en orden natural. */
    public static RecurrenciaDto aRecurrenciaDto(Recurrencia recurrencia) {
        return switch (recurrencia) {
            case RecurrenciaUnaVez(var fechaInicio, var fechaFin) -> new RecurrenciaUnaVezDto()
                    .tipo("UNA_VEZ")
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin);
            case RecurrenciaSemanal(var fechaInicio, var fechaFin, var diasDeLaSemana) -> new RecurrenciaSemanalDto()
                    .tipo("SEMANAL")
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin)
                    .diasSemana(diasDeLaSemana.stream().sorted()
                            .map((DayOfWeek dia) -> DayOfWeekDto.valueOf(dia.name()))
                            .toList());
            case RecurrenciaMensual(var diasDelMes, var meses) -> new RecurrenciaMensualDto()
                    .tipo("MENSUAL")
                    .diasDelMes(diasDelMes.stream().sorted().toList())
                    .meses(meses.stream().sorted().map((Month mes) -> MonthDto.valueOf(mes.name())).toList());
        };
    }
}
