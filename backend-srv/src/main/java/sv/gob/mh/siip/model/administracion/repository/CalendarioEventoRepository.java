package sv.gob.mh.siip.model.administracion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.administracion.domain.CalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;

public interface CalendarioEventoRepository extends JpaRepository<CalendarioEvento, Long> {

    Optional<CalendarioEvento> findByTipoEventoAndAnioAndCuatrimestreIsNull(TipoEventoCalendario tipoEvento,
            Integer anio);

    /** RN-A.a/RN-A.b de CU-PRE-32/33: el bloqueo por calendario es por cuatrimestre, no por año completo. */
    Optional<CalendarioEvento> findByTipoEventoAndAnioAndCuatrimestre(TipoEventoCalendario tipoEvento, Integer anio,
            Integer cuatrimestre);
}
