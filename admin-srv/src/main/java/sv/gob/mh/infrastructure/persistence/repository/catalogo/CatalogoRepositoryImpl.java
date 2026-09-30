package sv.gob.mh.infrastructure.persistence.repository.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CatalogoEntity;

/** Adaptador JPA de {@link CatalogoRepository}. */
@Repository
public class CatalogoRepositoryImpl implements CatalogoRepository {

    private final CatalogoJpaRepository jpa;

    public CatalogoRepositoryImpl(CatalogoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Catalogo> buscarPorCodigo(String codigo) {
        return jpa.findByCodigo(codigo).map(CatalogoPersistenceMapper::aModelo);
    }

    @Override
    public boolean existeCodigo(String codigo) {
        return jpa.existsByCodigo(codigo);
    }

    @Override
    public boolean existeNombre(String nombreIgnorandoMayusculas) {
        return jpa.existsByNombreIgnoreCase(nombreIgnorandoMayusculas);
    }

    @Override
    public List<Catalogo> listarPorCodigo() {
        return jpa.findAllByOrderByCodigoAsc().stream().map(CatalogoPersistenceMapper::aModelo).toList();
    }

    @Override
    public List<Catalogo> listarHijos(String codigoPadre) {
        return jpa.findByCatalogoPadreCodigoOrderByCodigoAsc(codigoPadre).stream()
                .map(CatalogoPersistenceMapper::aModelo)
                .toList();
    }

    @Override
    public Catalogo guardar(Catalogo catalogo) {
        CatalogoEntity entidad = catalogo.getId() == null ? new CatalogoEntity()
                : jpa.findById(catalogo.getId()).orElseThrow();
        CatalogoPersistenceMapper.copiar(catalogo, entidad);
        return CatalogoPersistenceMapper.aModelo(jpa.saveAndFlush(entidad));
    }
}
