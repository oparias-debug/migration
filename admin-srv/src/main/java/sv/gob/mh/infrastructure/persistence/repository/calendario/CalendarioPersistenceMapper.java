package sv.gob.mh.infrastructure.persistence.repository.calendario;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.model.calendario.Excepcion;
import sv.gob.mh.domain.model.calendario.IdentificacionCalendario;
import sv.gob.mh.domain.model.calendario.Periodo;
import sv.gob.mh.domain.model.calendario.RangoFechas;
import sv.gob.mh.domain.model.calendario.Recurrencia;
import sv.gob.mh.domain.model.calendario.RecurrenciaMensual;
import sv.gob.mh.domain.model.calendario.RecurrenciaSemanal;
import sv.gob.mh.domain.model.calendario.RecurrenciaUnaVez;
import sv.gob.mh.infrastructure.persistence.entity.calendario.CalendarioEntity;
import sv.gob.mh.infrastructure.persistence.entity.calendario.ExcepcionCalendarioEntity;
import sv.gob.mh.infrastructure.persistence.entity.calendario.PeriodoCalendarioEntity;
import sv.gob.mh.infrastructure.persistence.entity.calendario.PeriodoCalendarioEntity.TipoRecurrencia;

/**
 * Traducción entidad JPA ↔ modelo de dominio de CU-ADM-04. Se invoca dentro de la transacción del
 * caso de uso: el modelo sale completo (períodos, recurrencias y excepciones) y no arrastra proxies
 * lazy fuera de ella.
 */
final class CalendarioPersistenceMapper {

    private CalendarioPersistenceMapper() {
    }

    static Calendario aModelo(CalendarioEntity entidad) {
        List<Periodo> periodos = entidad.getPeriodos().stream()
                .map(periodo -> new Periodo(periodo.getId(), periodo.getCodigo(), periodo.getNombre(),
                        periodo.getTipo(), aRecurrencia(periodo)))
                .toList();
        List<Excepcion> excepciones = entidad.getExcepciones().stream()
                .map(excepcion -> new Excepcion(excepcion.getId(), excepcion.getFecha(), excepcion.getTipo(),
                        excepcion.getDescripcion()))
                .toList();
        return new Calendario(entidad.getId(),
                new IdentificacionCalendario(entidad.getCodigo(), entidad.getNombre(), entidad.getDescripcion()),
                new RangoFechas(entidad.getFechaInicio(), entidad.getFechaFin()), entidad.getEstado(),
                entidad.getAdministrador(), periodos, excepciones);
    }

    private static Recurrencia aRecurrencia(PeriodoCalendarioEntity periodo) {
        return switch (periodo.getTipoRecurrencia()) {
            case UNA_VEZ -> new RecurrenciaUnaVez(periodo.getFechaInicio(), periodo.getFechaFin());
            case SEMANAL -> new RecurrenciaSemanal(periodo.getFechaInicio(), periodo.getFechaFin(),
                    periodo.getDiasSemana());
            case MENSUAL -> new RecurrenciaMensual(periodo.getDiasMes(), periodo.getMeses());
        };
    }

    /**
     * Copia el modelo sobre la entidad. Los CalendarItems se sincronizan por id: los que conservan
     * su id se actualizan, los nuevos se agregan y el resto se elimina (orphanRemoval).
     */
    static void copiar(Calendario modelo, CalendarioEntity entidad) {
        entidad.setCodigo(modelo.getCodigo());
        entidad.setNombre(modelo.getNombre());
        entidad.setDescripcion(modelo.getDescripcion());
        entidad.setFechaInicio(modelo.getRango().desde());
        entidad.setFechaFin(modelo.getRango().hasta());
        entidad.setEstado(modelo.getEstado());
        entidad.setAdministrador(modelo.getAdministrador());

        Map<Long, PeriodoCalendarioEntity> periodosExistentes = new HashMap<>();
        entidad.getPeriodos().forEach(periodo -> periodosExistentes.put(periodo.getId(), periodo));
        List<PeriodoCalendarioEntity> periodos = new ArrayList<>();
        for (Periodo periodo : modelo.getPeriodos()) {
            PeriodoCalendarioEntity destino = existenteONuevo(periodosExistentes, periodo.getId(),
                    PeriodoCalendarioEntity::new);
            destino.setCalendario(entidad);
            destino.setCodigo(periodo.getCodigo());
            destino.setNombre(periodo.getNombre());
            destino.setTipo(periodo.getTipo());
            copiarRecurrencia(periodo.getRecurrencia(), destino);
            periodos.add(destino);
        }
        entidad.reemplazarPeriodos(periodos);

        Map<Long, ExcepcionCalendarioEntity> excepcionesExistentes = new HashMap<>();
        entidad.getExcepciones().forEach(excepcion -> excepcionesExistentes.put(excepcion.getId(), excepcion));
        List<ExcepcionCalendarioEntity> excepciones = new ArrayList<>();
        for (Excepcion excepcion : modelo.getExcepciones()) {
            ExcepcionCalendarioEntity destino = existenteONuevo(excepcionesExistentes, excepcion.getId(),
                    ExcepcionCalendarioEntity::new);
            destino.setCalendario(entidad);
            destino.setFecha(excepcion.getFecha());
            destino.setTipo(excepcion.getTipo());
            destino.setDescripcion(excepcion.getDescripcion());
            excepciones.add(destino);
        }
        entidad.reemplazarExcepciones(excepciones);
    }

    private static <T> T existenteONuevo(Map<Long, T> existentes, Long id, Supplier<T> nuevo) {
        return id != null && existentes.containsKey(id) ? existentes.get(id) : nuevo.get();
    }

    private static void copiarRecurrencia(Recurrencia recurrencia, PeriodoCalendarioEntity destino) {
        switch (recurrencia) {
            case RecurrenciaUnaVez(var fechaInicio, var fechaFin) -> {
                destino.setTipoRecurrencia(TipoRecurrencia.UNA_VEZ);
                copiarConjuntos(destino, Set.of(), Set.of(), Set.of());
                destino.setFechaInicio(fechaInicio);
                destino.setFechaFin(fechaFin);
            }
            case RecurrenciaSemanal(var fechaInicio, var fechaFin, var diasDeLaSemana) -> {
                destino.setTipoRecurrencia(TipoRecurrencia.SEMANAL);
                copiarConjuntos(destino, diasDeLaSemana, Set.of(), Set.of());
                destino.setFechaInicio(fechaInicio);
                destino.setFechaFin(fechaFin);
            }
            case RecurrenciaMensual(var diasDelMes, var meses) -> {
                destino.setTipoRecurrencia(TipoRecurrencia.MENSUAL);
                copiarConjuntos(destino, Set.of(), diasDelMes, meses);
                destino.setFechaInicio(null);
                destino.setFechaFin(null);
            }
        }
    }

    private static void copiarConjuntos(PeriodoCalendarioEntity destino, Set<DayOfWeek> diasSemana,
            Set<Integer> diasMes, Set<Month> meses) {
        destino.reemplazarDiasSemana(diasSemana);
        destino.reemplazarDiasMes(diasMes);
        destino.reemplazarMeses(meses);
    }
}
