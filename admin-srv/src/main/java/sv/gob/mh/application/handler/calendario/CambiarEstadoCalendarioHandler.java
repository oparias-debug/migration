package sv.gob.mh.application.handler.calendario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.calendario.CambiarEstadoCalendarioCommand;
import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/** CU-ADM-04-15 (RN20). Error: CALENDARIO_INEXISTENTE. */
@Service
public class CambiarEstadoCalendarioHandler {

    private final CalendarioRepository calendarioRepository;

    public CambiarEstadoCalendarioHandler(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional
    public Calendario handle(CambiarEstadoCalendarioCommand command) {
        var calendario = calendarioRepository.obtenerPorCodigo(command.codigoCalendario());
        calendario.cambiarEstado(command.estado());
        return calendarioRepository.guardar(calendario);
    }
}
