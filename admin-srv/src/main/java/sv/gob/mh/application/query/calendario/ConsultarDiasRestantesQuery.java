package sv.gob.mh.application.query.calendario;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.calendario.CalculosCalendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/**
 * CU-ADM-04-08 (RN05). Errores: CALENDARIO_INEXISTENTE, PERIODO_INEXISTENTE (o no LABORAL) y
 * FECHA_FUERA_DE_PERIODO.
 */
@Service
public class ConsultarDiasRestantesQuery {

    private final CalendarioRepository calendarioRepository;

    public ConsultarDiasRestantesQuery(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public int ejecutar(String codigoCalendario, String codigoPeriodo, LocalDate fecha) {
        return CalculosCalendario.diasRestantes(calendarioRepository.obtenerPorCodigo(codigoCalendario),
                codigoPeriodo, fecha);
    }
}
