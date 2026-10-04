package sv.gob.mh.application.handler.calendario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.calendario.EditarDefinicionCalendarioCommand;
import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/**
 * CU-ADM-04-14 (RN23). Errores: CALENDARIO_INEXISTENTE, PERIODO_INEXISTENTE o EXCEPCION_INEXISTENTE
 * (id que no pertenece al calendario), CODIGO_PERIODO_DUPLICADO (RN15), EXCEPCION_DUPLICADA,
 * PERIODO_RANGO_INVALIDO (RN08), PERIODO_FUERA_DE_RANGO y EXCEPCION_FUERA_DE_RANGO (RN10).
 */
@Service
public class EditarDefinicionCalendarioHandler {

    private final CalendarioRepository calendarioRepository;

    public EditarDefinicionCalendarioHandler(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional
    public Calendario handle(EditarDefinicionCalendarioCommand command) {
        var calendario = calendarioRepository.obtenerPorCodigo(command.codigoCalendario());
        calendario.editarDefinicion(command.items());
        return calendarioRepository.guardar(calendario);
    }
}
