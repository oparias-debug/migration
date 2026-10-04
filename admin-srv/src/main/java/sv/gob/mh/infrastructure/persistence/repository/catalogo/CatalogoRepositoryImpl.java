package sv.gob.mh.infrastructure.persistence.repository.catalogo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    public List<Catalogo> listarPorCodigo() {
        return jpa.findAllByOrderByCodigoAsc().stream().map(CatalogoPersistenceMapper::aModelo).toList();
    }

    @Override
    public List<Catalogo> buscar(String codigo, String nombre) {
        Stream<CatalogoEntity> candidatos = codigo != null ? jpa.findByCodigo(codigo).stream()
                : jpa.findByNombreIgnoreCaseOrderByCodigoAsc(nombre).stream();
        return candidatos
                .filter(catalogo -> nombre == null || catalogo.getNombre().equalsIgnoreCase(nombre))
                .map(CatalogoPersistenceMapper::aModelo)
                .toList();
    }

    @Override
    public Optional<Catalogo> buscarHijo(String codigoPadre) {
        return jpa.findFirstByCatalogoPadre_CodigoOrderByCodigoAsc(codigoPadre).map(CatalogoPersistenceMapper::aModelo);
    }

    /** El padre llega por código (su existencia ya la verificó el caso de uso) y se enlaza por id. */
    @Override
    public Catalogo guardar(Catalogo catalogo) {
        CatalogoEntity entidad = catalogo.getId() == null ? new CatalogoEntity()
                : jpa.findById(catalogo.getId()).orElseThrow();
        String codigoPadre = catalogo.getCatalogoPadreCodigo();
        CatalogoEntity padre = codigoPadre == null ? null : jpa.findByCodigo(codigoPadre).orElseThrow();
        CatalogoPersistenceMapper.copiar(catalogo, entidad, padre);
        return CatalogoPersistenceMapper.aModelo(jpa.saveAndFlush(entidad));
    }
}
