package sv.gob.mh.application.handler.calendario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.calendario.CrearCalendarioCommand;
import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/** CU-ADM-04-01. Errores: CODIGO_CALENDARIO_DUPLICADO (RN14) y CALENDARIO_RANGO_INVALIDO (RN08). */
@Service
public class CrearCalendarioHandler {

    private final CalendarioRepository calendarioRepository;

    public CrearCalendarioHandler(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional
    public Calendario handle(CrearCalendarioCommand command) {
        if (calendarioRepository.existeCodigo(command.codigo())) {
            throw Calendario.codigoDuplicado();
        }
        return calendarioRepository.guardar(Calendario.nuevo(command.codigo(), command.nombre(),
                command.descripcion(), command.rango(), command.estado(), command.administrador()));
    }
}
