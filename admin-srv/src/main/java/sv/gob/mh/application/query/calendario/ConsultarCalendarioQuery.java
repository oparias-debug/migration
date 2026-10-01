package sv.gob.mh.application.query.calendario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/**
 * CU-ADM-04-11 y 12: el calendario completo, del que se toma su definición (RN21) o su rango de
 * fechas. Error: CALENDARIO_INEXISTENTE.
 */
@Service
public class ConsultarCalendarioQuery {

    private final CalendarioRepository calendarioRepository;

    public ConsultarCalendarioQuery(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public Calendario ejecutar(String codigoCalendario) {
        return calendarioRepository.obtenerPorCodigo(codigoCalendario);
    }
}
