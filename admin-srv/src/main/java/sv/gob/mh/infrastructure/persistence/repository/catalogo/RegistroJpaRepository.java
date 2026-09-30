package sv.gob.mh.infrastructure.persistence.repository.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sv.gob.mh.infrastructure.persistence.entity.catalogo.RegistroEntity;

@Repository
public interface RegistroJpaRepository extends JpaRepository<RegistroEntity, Long> {

    Optional<RegistroEntity> findByCatalogo_CodigoAndClave(String codigoCatalogo, String clave);

    List<RegistroEntity> findByCatalogo_CodigoOrderByIdAsc(String codigoCatalogo);

    List<RegistroEntity> findByRegistroPadre_IdOrderByCatalogo_CodigoAscIdAsc(Long idRegistroPadre);

    long countByCatalogo_Codigo(String codigoCatalogo);

    boolean existsByCatalogo_Codigo(String codigoCatalogo);

    boolean existsByCatalogo_CodigoAndClave(String codigoCatalogo, String clave);
}
