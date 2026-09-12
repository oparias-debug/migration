package sv.gob.mh.siip.model.administracion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.administracion.domain.Registro;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    Optional<Registro> findByCatalogo_CodigoAndClave(String codigoCatalogo, String clave);

    List<Registro> findByCatalogo_Codigo(String codigoCatalogo);

    boolean existsByCatalogo_Codigo(String codigoCatalogo);

    boolean existsByCatalogo_CodigoAndClave(String codigoCatalogo, String clave);
}
