package sv.gob.mh.siip.model.preinversion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {

    List<UnidadMedida> findAllByOrderByCategoriaAscNombreAsc();

    Optional<UnidadMedida> findByCategoriaAndNombre(String categoria, String nombre);

    /**
     * Resuelve por nombre sin categoría: CU-PRE-11 referencia la unidad de medida como un código
     * plano (sin desambiguar por categoría, a diferencia del catálogo administrado de CU-ADM-02).
     */
    Optional<UnidadMedida> findFirstByNombre(String nombre);
}
