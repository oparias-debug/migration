package sv.gob.mh.domain.repository.calendario;

import java.util.List;
import java.util.Optional;

import sv.gob.mh.domain.model.calendario.Calendario;
import sv.gob.mh.shared.exception.ErrorCalendarioException;

/** Contrato de persistencia de los calendarios de CU-ADM-04, cada uno con sus períodos y excepciones. */
public interface CalendarioRepository {

    Optional<Calendario> buscarPorCodigo(String codigo);

    boolean existeCodigo(String codigo);

    /** Todos, incluidos los INACTIVO (RN22), por código. */
    List<Calendario> listarPorCodigo();

    /** Persiste el calendario completo; el resultado trae los ids asignados a sus CalendarItems nuevos. */
    Calendario guardar(Calendario calendario);

    /** El calendario con ese código, o CALENDARIO_INEXISTENTE (RN17, RN21). */
    default Calendario obtenerPorCodigo(String codigo) {
        return buscarPorCodigo(codigo).orElseThrow(ErrorCalendarioException::calendarioInexistente);
    }
}
