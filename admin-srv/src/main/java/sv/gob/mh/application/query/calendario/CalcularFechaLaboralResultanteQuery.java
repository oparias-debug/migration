package sv.gob.mh.application.query.calendario;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.calendario.CalculosCalendario;
import sv.gob.mh.domain.repository.calendario.CalendarioRepository;

/**
 * CU-ADM-04-10 (RN07). Errores: CALENDARIO_INEXISTENTE, FECHA_RESULTANTE_SIN_PERIODO,
 * FECHA_RESULTANTE_NO_LABORAL y LIMITE_EXPLORACION_EXCEDIDO.
 */
@Service
public class CalcularFechaLaboralResultanteQuery {

    private final CalendarioRepository calendarioRepository;

    public CalcularFechaLaboralResultanteQuery(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public LocalDate ejecutar(String codigoCalendario, LocalDate fecha, int diasHabiles) {
        return CalculosCalendario.fechaLaboralResultante(calendarioRepository.obtenerPorCodigo(codigoCalendario),
                fecha, diasHabiles);
    }
}
