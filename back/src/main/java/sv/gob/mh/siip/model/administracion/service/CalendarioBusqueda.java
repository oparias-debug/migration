package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;

/**
 * CU-ADM-04 (Gestion de Calendarios): busqueda de calendarios y periodos por codigo (RN17), compartida por
 * la gestion y las consultas.
 */
final class CalendarioBusqueda {

    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;

    CalendarioBusqueda(CalendarioRepository calendarioRepository, PeriodoRepository periodoRepository) {
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
    }

    Calendario obtenerCalendario(String codigoCalendario) {
        return calendarioRepository.findByCodigo(codigoCalendario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe ningún calendario con el código indicado."));
    }

    Periodo obtenerPeriodo(String codigoCalendario, String codigoPeriodo) {
        return periodoRepository.findByCalendario_CodigoAndCodigo(codigoCalendario, codigoPeriodo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe ningún período con el código indicado dentro de ese calendario."));
    }

    Periodo obtenerPeriodoLaboral(String codigoCalendario, String codigoPeriodo) {
        Periodo periodo = obtenerPeriodo(codigoCalendario, codigoPeriodo);
        if (periodo.getTipo() != TipoPeriodo.LABORAL) {
            throw new RecursoNoEncontradoException(
                    "No existe ningún período LABORAL con el código indicado dentro de ese calendario.");
        }
        return periodo;
    }
}
