package sv.gob.mh.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica de repositorio para operaciones CRUD básicas.
 * <p>
 * Las implementaciones concretas en la capa de infraestructura
 * traducen estas operaciones al mecanismo de persistencia (JPA, JDBC, etc.).
 *
 * @param <E> tipo de la entidad
 * @param <I> tipo del identificador
 */
public interface RepositoryInterface<E, I> {
    E create(E entity);
    Optional<E> findById(I id);
    List<E> findAll();
    E update(E entity);
    boolean deleteById(I id);
    long count();
    boolean existsById(I id);
}
