package sv.gob.mh.application.query.calendario;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.calendario.CalculosCalendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/** CU-ADM-04-06. Errores: CALENDARIO_INEXISTENTE y PERIODO_INEXISTENTE. */
@Service
public class ConsultarPertenenciaPeriodoQuery {

    private final CalendarioRepository calendarioRepository;

    public ConsultarPertenenciaPeriodoQuery(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public boolean ejecutar(String codigoCalendario, String codigoPeriodo, LocalDate fecha) {
        return CalculosCalendario.pertenece(calendarioRepository.obtenerPorCodigo(codigoCalendario),
                codigoPeriodo, fecha);
    }
}
