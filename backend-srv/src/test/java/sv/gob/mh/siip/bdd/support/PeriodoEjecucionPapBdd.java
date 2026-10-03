package sv.gob.mh.siip.bdd.support;

import java.util.Optional;

import sv.gob.mh.siip.model.administracion.domain.CalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;

/**
 * Evento EJECUCION_PAP del Calendario de Eventos (CU-ADM-04) para un año y cuatrimestre, que
 * decide si el avance del PAP (CU-PRE-32/33) admite registros (RN-A.b). Las features de pantalla
 * trabajan sobre el año y cuatrimestre vigentes, que otros escenarios también pueden usar, y lo
 * guardado en un escenario no siempre se revierte: por eso el evento se deja como estaba al final
 * ({@link #restaurar()} en el {@code @After}).
 */
public final class PeriodoEjecucionPapBdd {

    private final CalendarioEventoRepository repository;
    private final int anio;
    private final int cuatrimestre;

    private CalendarioEvento evento;
    private EstadoCalendarioEvento estadoOriginal;
    private boolean creado;

    public PeriodoEjecucionPapBdd(CalendarioEventoRepository repository, int anio, int cuatrimestre) {
        this.repository = repository;
        this.anio = anio;
        this.cuatrimestre = cuatrimestre;
    }

    /** Si ya hay un evento para el período y no está abierto, lo abre mientras dura el escenario. */
    public void asegurarAbierto() {
        Optional<CalendarioEvento> existente = buscar();
        if (existente.isPresent() && existente.get().getEstado() != EstadoCalendarioEvento.ABIERTO) {
            fijarEstado(EstadoCalendarioEvento.ABIERTO);
        }
    }

    /** Deja el período cerrado (crea el evento si no existe). */
    public void cerrar() {
        fijarEstado(EstadoCalendarioEvento.CERRADO);
    }

    /** Devuelve el evento a su estado original, o lo elimina si lo creó este escenario. */
    public void restaurar() {
        if (evento == null) {
            return;
        }
        if (creado) {
            repository.delete(evento);
        } else {
            evento.setEstado(estadoOriginal);
            repository.save(evento);
        }
        evento = null;
    }

    private void fijarEstado(EstadoCalendarioEvento estado) {
        if (evento == null) {
            Optional<CalendarioEvento> existente = buscar();
            creado = existente.isEmpty();
            evento = existente.orElseGet(() -> CalendarioEvento.builder()
                    .tipoEvento(TipoEventoCalendario.EJECUCION_PAP)
                    .anio(anio)
                    .cuatrimestre(cuatrimestre)
                    .estado(estado)
                    .build());
            estadoOriginal = evento.getEstado();
        }
        evento.setEstado(estado);
        evento = repository.save(evento);
    }

    private Optional<CalendarioEvento> buscar() {
        return repository.findByTipoEventoAndAnioAndCuatrimestre(TipoEventoCalendario.EJECUCION_PAP, anio,
                cuatrimestre);
    }
}
