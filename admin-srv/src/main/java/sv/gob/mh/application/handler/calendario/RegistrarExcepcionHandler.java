package sv.gob.mh.application.handler.calendario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.calendario.RegistrarExcepcionCommand;
import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.model.calendario.Excepcion;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/** CU-ADM-04-04. Errores: CALENDARIO_INEXISTENTE, EXCEPCION_FUERA_DE_RANGO (RN10) y EXCEPCION_DUPLICADA. */
@Service
public class RegistrarExcepcionHandler {

    private final CalendarioRepository calendarioRepository;

    public RegistrarExcepcionHandler(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    /** La excepción guardada, con su id, junto con el calendario al que pertenece (del que hereda el estado). */
    public record Resultado(Calendario calendario, Excepcion excepcion) {
    }

    @Transactional
    public Resultado handle(RegistrarExcepcionCommand command) {
        var calendario = calendarioRepository.obtenerPorCodigo(command.codigoCalendario());
        calendario.registrarExcepcion(command.fecha(), command.tipo(), command.descripcion());
        var guardado = calendarioRepository.guardar(calendario);
        var excepcion = guardado.getExcepciones().stream()
                .filter(e -> e.getFecha().equals(command.fecha()))
                .findFirst()
                .orElseThrow();
        return new Resultado(guardado, excepcion);
    }
}
