package sv.gob.mh.application.query.calendario;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.calendario.CalculosCalendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/** CU-ADM-04-09 (RN06, RN19). Errores: CALENDARIO_INEXISTENTE, FECHAS_INCONSISTENTES y FECHAS_FUERA_DE_RANGO. */
@Service
public class ContarDiasLaboralesQuery {

    private final CalendarioRepository calendarioRepository;

    public ContarDiasLaboralesQuery(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public int ejecutar(String codigoCalendario, LocalDate fechaInicio, LocalDate fechaFin) {
        return CalculosCalendario.diasLaboralesEntre(calendarioRepository.obtenerPorCodigo(codigoCalendario),
                fechaInicio, fechaFin);
    }
}
