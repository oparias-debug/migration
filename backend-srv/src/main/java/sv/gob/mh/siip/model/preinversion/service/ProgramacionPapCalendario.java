package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;

/**
 * Verificación del período de ingreso de información del Calendario de Eventos del PAP (evento
 * PROGRAMACION_PAP), compartida por CU-PRE-30 (financiera) y CU-PRE-31 (metas físicas).
 */
final class ProgramacionPapCalendario {

    private final CalendarioEventoRepository calendarioEventoRepository;

    ProgramacionPapCalendario(CalendarioEventoRepository calendarioEventoRepository) {
        this.calendarioEventoRepository = calendarioEventoRepository;
    }

    /**
     * RN-A.b: fuera de la fecha del Calendario de Eventos del PAP no se permite ingresar ni ajustar
     * datos, salvo habilitación de modificaciones fuera de plazo ("habilitadoFueraPlazo"). [SUPUESTO] La
     * especificación no define qué ocurre si NO hay evento PROGRAMACION_PAP configurado para el año: se
     * asume período abierto (mismo criterio que CU-PRE-31/32/33) para no bloquear la elaboración del PAP
     * mientras el Administrador aún no registra el calendario. Documentado por el escenario "Sin evento
     * de calendario configurado para el año" de CU-PRE-30-bloqueo-fuera-calendario.feature.
     */
    void verificarPeriodoAbierto(boolean habilitadoFueraPlazo, Integer anio) {
        if (habilitadoFueraPlazo) {
            return;
        }
        boolean abierto = calendarioEventoRepository
                .findByTipoEventoAndAnioAndCuatrimestreIsNull(TipoEventoCalendario.PROGRAMACION_PAP, anio)
                .map(evento -> evento.getEstado() == EstadoCalendarioEvento.ABIERTO)
                .orElse(true);
        if (!abierto) {
            throw new ConflictoEstadoException("PERIODO_CERRADO", "Periodo de ingreso de información ha finalizado.");
        }
    }
}
