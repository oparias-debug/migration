package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.model.administracion.domain.Recurrencia;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaMensual;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaSemanal;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaUnaVez;
import sv.gob.mh.siip.model.administracion.dto.DayOfWeekDto;
import sv.gob.mh.siip.model.administracion.dto.MonthDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaMensualDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaSemanalDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;

/** CU-ADM-04 (Gestion de Calendarios): mapeo a DTO de la recurrencia de un periodo (UNA_VEZ, SEMANAL, MENSUAL). */
final class CalendarioRecurrenciaDtoMapper {

    private CalendarioRecurrenciaDtoMapper() {
    }

    static RecurrenciaDto aRecurrenciaDto(Recurrencia recurrencia) {
        if (recurrencia instanceof RecurrenciaUnaVez unaVez) {
            return new RecurrenciaUnaVezDto()
                    .tipo("UNA_VEZ")
                    .fechaInicio(unaVez.getFechaInicio())
                    .fechaFin(unaVez.getFechaFin());
        }
        if (recurrencia instanceof RecurrenciaSemanal semanal) {
            RecurrenciaSemanalDto dto = new RecurrenciaSemanalDto()
                    .tipo("SEMANAL")
                    .fechaInicio(semanal.getFechaInicio())
                    .fechaFin(semanal.getFechaFin());
            semanal.getDiasDeLaSemana().stream().sorted()
                    .forEach(dia -> dto.addDiasSemanaItem(DayOfWeekDto.valueOf(dia.name())));
            return dto;
        }
        if (recurrencia instanceof RecurrenciaMensual mensual) {
            RecurrenciaMensualDto dto = new RecurrenciaMensualDto().tipo("MENSUAL");
            mensual.getDiasDelMes().stream().sorted().forEach(dto::addDiasDelMesItem);
            mensual.getMeses().stream().sorted().forEach(mes -> dto.addMesesItem(MonthDto.valueOf(mes.name())));
            return dto;
        }
        throw new IllegalArgumentException("Tipo de recurrencia no soportado: " + recurrencia.getClass());
    }
}
