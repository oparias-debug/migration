package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Excepcion;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.domain.Recurrencia;
import sv.gob.mh.siip.model.administracion.dto.CalendarItemInputDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralInputDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaDto;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;

/**
 * CU-ADM-04 (Gestion de Calendarios): aplica sobre el calendario cada CalendarItem de la edicion de su
 * definicion (RN23): con {@code id} edita el existente y sin {@code id} da de alta uno nuevo (RN08, RN10,
 * RN15).
 */
final class CalendarioEdicionItems {

    private CalendarioEdicionItems() {
    }

    /** RN23: `id` presente edita ese CalendarItem; `id` ausente da de alta uno nuevo (RN08, RN10, RN15). */
    static Periodo aplicarPeriodoInput(Calendario calendario, CalendarItemInputDto itemInput) {
        DatosPeriodo datos = switch (itemInput) {
            case PeriodoLaboralInputDto laboral -> new DatosPeriodo(laboral.getId(), laboral.getCodigo(),
                    laboral.getNombre(), laboral.getRecurrencia(), TipoPeriodo.LABORAL);
            case PeriodoNoLaboralInputDto noLaboral -> new DatosPeriodo(noLaboral.getId(), noLaboral.getCodigo(),
                    noLaboral.getNombre(), noLaboral.getRecurrencia(), TipoPeriodo.NO_LABORAL);
            default -> throw new IllegalArgumentException(
                    "Tipo de CalendarItem no soportado: " + itemInput.getClass());
        };
        Long id = datos.id();

        Periodo periodo;
        if (id != null) {
            periodo = calendario.getPeriodos().stream().filter(p -> id.equals(p.getId())).findFirst()
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe ningún período con el id indicado dentro de ese calendario."));
        } else {
            periodo = Periodo.builder().calendario(calendario).build();
        }

        // Se compara contra los demas periodos del calendario: el editado (mismo id) se excluye, y
        // uno nuevo aun no se ha agregado a la lista, por lo que no hay nada que excluir.
        String codigo = datos.codigo();
        boolean codigoDuplicado = calendario.getPeriodos().stream()
                .anyMatch(otro -> (id == null || !id.equals(otro.getId())) && otro.getCodigo().equals(codigo));
        if (codigoDuplicado) {
            throw new ConflictoEstadoException("Ya existe un período con ese código dentro del calendario.");
        }
        if (id == null) {
            calendario.getPeriodos().add(periodo);
        }

        Recurrencia recurrencia = CalendarioRecurrenciaFactory.aRecurrencia(datos.recurrencia(), calendario);
        periodo.setCodigo(codigo);
        periodo.setNombre(datos.nombre());
        periodo.setTipo(datos.tipo());
        periodo.setRecurrencia(recurrencia);
        return periodo;
    }

    /** RN23: `id` presente edita esa excepción; `id` ausente da de alta una nueva (RN10). */
    static Excepcion aplicarExcepcionInput(Calendario calendario, ExcepcionInputDto input) {
        Long id = input.getId();
        Excepcion excepcion;
        if (id != null) {
            excepcion = calendario.getExcepciones().stream().filter(e -> id.equals(e.getId())).findFirst()
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe ninguna excepción con el id indicado dentro de ese calendario."));
        } else {
            excepcion = Excepcion.builder().calendario(calendario).build();
            calendario.getExcepciones().add(excepcion);
        }

        CalendarioRecurrenciaFactory.exigirFechaEnmarcadaEnCalendario(input.getFecha(), calendario);
        excepcion.setFecha(input.getFecha());
        excepcion.setTipo(TipoExcepcion.valueOf(input.getTipo().name()));
        excepcion.setDescripcion(input.getDescripcion());
        return excepcion;
    }

    /** Datos comunes de un {@link CalendarItemInputDto} de tipo período, sea laboral o no laboral. */
    private record DatosPeriodo(Long id, String codigo, String nombre, RecurrenciaDto recurrencia,
            TipoPeriodo tipo) {
    }
}
