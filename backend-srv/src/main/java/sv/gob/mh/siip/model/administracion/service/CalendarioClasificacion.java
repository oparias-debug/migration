package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.util.Optional;

import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Excepcion;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.domain.Recurrencia;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaMensual;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaSemanal;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaUnaVez;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;

/**
 * CU-ADM-04 (Gestion de Calendarios): reglas de clasificacion de fechas (RN01, RN02, RN16, RN19) y de
 * duracion y fin de periodos (RN04, RN05, RN09, RN11) compartidas por las consultas del calendario.
 */
final class CalendarioClasificacion {

    private CalendarioClasificacion() {
    }

    /**
     * Clasifica una fecha segun las excepciones y periodos del calendario. Una excepcion sobre la
     * fecha tiene prioridad (RN01/seccion 7.1); en su ausencia, la interseccion LABORAL+NO_LABORAL
     * se resuelve como NO_LABORAL (RN02). Vacio si la fecha no cae en ninguna excepcion ni periodo
     * (RN16).
     */
    static Optional<TipoPeriodo> clasificarFecha(Calendario calendario, LocalDate fecha) {
        Optional<Excepcion> excepcion = excepcionEnFecha(calendario, fecha);
        if (excepcion.isPresent()) {
            return Optional.of(
                    excepcion.get().getTipo() == TipoExcepcion.DIA_LABORAL
                            ? TipoPeriodo.LABORAL
                            : TipoPeriodo.NO_LABORAL);
        }
        boolean enLaboral = calendario.getPeriodos().stream()
                .filter(periodo -> periodo.getTipo() == TipoPeriodo.LABORAL)
                .anyMatch(periodo -> perteneceARecurrencia(periodo.getRecurrencia(), fecha));
        boolean enNoLaboral = calendario.getPeriodos().stream()
                .filter(periodo -> periodo.getTipo() == TipoPeriodo.NO_LABORAL)
                .anyMatch(periodo -> perteneceARecurrencia(periodo.getRecurrencia(), fecha));
        if (!enLaboral && !enNoLaboral) {
            return Optional.empty();
        }
        return Optional.of(enNoLaboral ? TipoPeriodo.NO_LABORAL : TipoPeriodo.LABORAL);
    }

    /** RN19: para efectos de conteo, una fecha sin periodo definido tampoco cuenta como LABORAL. */
    static boolean esDiaLaboral(Calendario calendario, LocalDate fecha) {
        return clasificarFecha(calendario, fecha).map(tipo -> tipo == TipoPeriodo.LABORAL).orElse(false);
    }

    static boolean dentroDelCalendario(Calendario calendario, LocalDate fecha) {
        return !fecha.isBefore(calendario.getFechaInicio()) && !fecha.isAfter(calendario.getFechaFin());
    }

    static void exigirFechaEnPeriodo(Periodo periodo, LocalDate fecha) {
        if (!perteneceARecurrencia(periodo.getRecurrencia(), fecha)) {
            throw new InconsistenciaFechaException("FECHA_FUERA_DE_PERIODO",
                    "La fecha dada no está dentro del período LABORAL indicado.");
        }
    }

    static boolean perteneceARecurrencia(Recurrencia recurrencia, LocalDate fecha) {
        return switch (recurrencia) {
            case RecurrenciaUnaVez unaVez ->
                    !fecha.isBefore(unaVez.getFechaInicio()) && !fecha.isAfter(unaVez.getFechaFin());
            case RecurrenciaSemanal semanal ->
                    !fecha.isBefore(semanal.getFechaInicio()) && !fecha.isAfter(semanal.getFechaFin())
                            && semanal.getDiasDeLaSemana().contains(fecha.getDayOfWeek());
            case RecurrenciaMensual mensual ->
                    mensual.getMeses().contains(fecha.getMonth())
                            && mensual.getDiasDelMes().contains(fecha.getDayOfMonth());
            case null, default -> false;
        };
    }

    /**
     * RN04/RN09/RN11: duracion en dias de un periodo, contando dias calendario reales (RN09) para
     * que MENSUAL resuelva la duracion real de cada mes involucrado (RN11, p.ej. febrero en año
     * bisiesto) sin extrapolar a meses no declarados. Si el periodo es LABORAL, excluye los dias
     * que tambien caen en algun periodo NO_LABORAL del mismo calendario (RN04); MENSUAL no declara
     * un rango propio, por lo que se acota al rango del calendario.
     */
    static int calcularDuracionDias(Periodo periodo, Calendario calendario) {
        Recurrencia recurrencia = periodo.getRecurrencia();
        RangoFechas rango = switch (recurrencia) {
            case RecurrenciaUnaVez unaVez -> new RangoFechas(unaVez.getFechaInicio(), unaVez.getFechaFin());
            case RecurrenciaSemanal semanal -> new RangoFechas(semanal.getFechaInicio(), semanal.getFechaFin());
            default -> new RangoFechas(calendario.getFechaInicio(), calendario.getFechaFin());
        };
        LocalDate desde = rango.desde();
        LocalDate hasta = rango.hasta();

        int total = 0;
        for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            boolean excluidaPorNoLaboral = periodo.getTipo() == TipoPeriodo.LABORAL
                    && enAlgunPeriodoDeTipo(calendario, TipoPeriodo.NO_LABORAL, fecha);
            if (perteneceARecurrencia(recurrencia, fecha) && !excluidaPorNoLaboral) {
                total++;
            }
        }
        return total;
    }

    /** RN05: fin del periodo LABORAL contra el que se calculan los dias restantes. */
    static LocalDate finDelPeriodo(Periodo periodo, Calendario calendario) {
        Recurrencia recurrencia = periodo.getRecurrencia();
        if (recurrencia instanceof RecurrenciaUnaVez unaVez) {
            return unaVez.getFechaFin();
        }
        if (recurrencia instanceof RecurrenciaSemanal semanal) {
            return semanal.getFechaFin();
        }
        // MENSUAL no declara un rango de fechas propio: se usa el fin del calendario como limite.
        return calendario.getFechaFin();
    }

    private static Optional<Excepcion> excepcionEnFecha(Calendario calendario, LocalDate fecha) {
        return calendario.getExcepciones().stream().filter(excepcion -> excepcion.getFecha().equals(fecha))
                .findFirst();
    }

    private static boolean enAlgunPeriodoDeTipo(Calendario calendario, TipoPeriodo tipo, LocalDate fecha) {
        return calendario.getPeriodos().stream().filter(otro -> otro.getTipo() == tipo)
                .anyMatch(otro -> perteneceARecurrencia(otro.getRecurrencia(), fecha));
    }

    /** Rango de fechas sobre el que se recorre una recurrencia al calcular su duración. */
    private record RangoFechas(LocalDate desde, LocalDate hasta) {
    }
}
