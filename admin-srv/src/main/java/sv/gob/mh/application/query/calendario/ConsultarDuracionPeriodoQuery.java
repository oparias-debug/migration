package sv.gob.mh.application.query.calendario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.calendario.CalculosCalendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/** CU-ADM-04-07 (RN04, RN09, RN11). Errores: CALENDARIO_INEXISTENTE y PERIODO_INEXISTENTE. */
@Service
public class ConsultarDuracionPeriodoQuery {

    private final CalendarioRepository calendarioRepository;

    public ConsultarDuracionPeriodoQuery(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public int ejecutar(String codigoCalendario, String codigoPeriodo) {
        return CalculosCalendario.duracionDias(calendarioRepository.obtenerPorCodigo(codigoCalendario), codigoPeriodo);
    }
}
