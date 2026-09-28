package sv.gob.mh.siip.model.administracion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.administracion.domain.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
}
