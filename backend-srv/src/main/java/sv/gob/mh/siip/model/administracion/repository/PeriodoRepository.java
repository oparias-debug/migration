package sv.gob.mh.siip.model.administracion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.administracion.domain.Periodo;

public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

    Optional<Periodo> findByCalendario_CodigoAndCodigo(String codigoCalendario, String codigo);

    boolean existsByCalendario_CodigoAndCodigo(String codigoCalendario, String codigo);
}
