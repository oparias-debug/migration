package sv.gob.mh.application.query.calendario;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.calendario.CalculosCalendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;
import sv.gob.mh.shared.enums.TipoPeriodo;

/** CU-ADM-04-05 (RN01, RN02, RN16). Errores: CALENDARIO_INEXISTENTE y FECHA_SIN_PERIODO. */
@Service
public class ConsultarTipoDiaQuery {

    private final CalendarioRepository calendarioRepository;

    public ConsultarTipoDiaQuery(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public TipoPeriodo ejecutar(String codigoCalendario, LocalDate fecha) {
        return CalculosCalendario.tipoDia(calendarioRepository.obtenerPorCodigo(codigoCalendario), fecha);
    }
}
