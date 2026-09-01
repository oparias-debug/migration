package sv.gob.mh.siip.model.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.common.domain.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
}
