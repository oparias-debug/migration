package sv.gob.mh.application.handler.calendario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.calendario.AgregarPeriodoCommand;
import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.domain.model.calendario.Periodo;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/**
 * CU-ADM-04-02/03. Errores: CALENDARIO_INEXISTENTE, CODIGO_PERIODO_DUPLICADO (RN15),
 * PERIODO_RANGO_INVALIDO (RN08) y PERIODO_FUERA_DE_RANGO (RN10).
 */
@Service
public class AgregarPeriodoHandler {

    private final CalendarioRepository calendarioRepository;

    public AgregarPeriodoHandler(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    /** El período guardado, con su id, junto con el calendario al que pertenece (del que hereda el estado). */
    public record Resultado(Calendario calendario, Periodo periodo) {
    }

    @Transactional
    public Resultado handle(AgregarPeriodoCommand command) {
        Calendario calendario = calendarioRepository.obtenerPorCodigo(command.codigoCalendario());
        calendario.agregarPeriodo(command.codigo(), command.nombre(), command.tipo(), command.recurrencia());
        Calendario guardado = calendarioRepository.guardar(calendario);
        return new Resultado(guardado, guardado.exigirPeriodo(command.codigo()));
    }
}
