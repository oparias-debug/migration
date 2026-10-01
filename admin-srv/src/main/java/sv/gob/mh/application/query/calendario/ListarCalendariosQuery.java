package sv.gob.mh.application.query.calendario;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/** CU-ADM-04-13: todos los calendarios, incluidos los INACTIVO (RN22). */
@Service
public class ListarCalendariosQuery {

    private final CalendarioRepository calendarioRepository;

    public ListarCalendariosQuery(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Calendario> ejecutar() {
        return calendarioRepository.listarPorCodigo();
    }
}
