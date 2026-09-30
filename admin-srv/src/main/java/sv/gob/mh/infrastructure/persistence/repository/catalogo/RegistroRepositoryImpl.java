package sv.gob.mh.infrastructure.persistence.repository.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.RegistroEntity;

/** Adaptador JPA de {@link RegistroRepository}. */
@Repository
public class RegistroRepositoryImpl implements RegistroRepository {

    private final RegistroJpaRepository jpa;
    private final CatalogoJpaRepository catalogoJpa;

    public RegistroRepositoryImpl(RegistroJpaRepository jpa, CatalogoJpaRepository catalogoJpa) {
        this.jpa = jpa;
        this.catalogoJpa = catalogoJpa;
    }

    @Override
    public Optional<Registro> buscarPorClave(String codigoCatalogo, String clave) {
        return jpa.findByCatalogo_CodigoAndClave(codigoCatalogo, clave).map(CatalogoPersistenceMapper::aModelo);
    }

    @Override
    public List<Registro> listarPorCatalogo(String codigoCatalogo) {
        return CatalogoPersistenceMapper.aModelos(jpa.findByCatalogo_CodigoOrderByIdAsc(codigoCatalogo));
    }

    @Override
    public List<Registro> listarHijos(Long idRegistroPadre) {
        return CatalogoPersistenceMapper.aModelos(
                jpa.findByRegistroPadre_IdOrderByCatalogo_CodigoAscIdAsc(idRegistroPadre));
    }

    @Override
    public boolean existeEnCatalogo(String codigoCatalogo) {
        return jpa.existsByCatalogo_Codigo(codigoCatalogo);
    }

    @Override
    public boolean existeClave(String codigoCatalogo, String clave) {
        return jpa.existsByCatalogo_CodigoAndClave(codigoCatalogo, clave);
    }

    @Override
    public Registro guardar(Registro registro) {
        RegistroEntity entidad = registro.getId() == null ? new RegistroEntity()
                : jpa.findById(registro.getId()).orElseThrow();
        entidad.setCatalogo(catalogoJpa.getReferenceById(registro.getCatalogo().getId()));
        entidad.setClave(registro.getClave());
        entidad.setRegistroPadre(registro.getRegistroPadre() == null ? null
                : jpa.getReferenceById(registro.getRegistroPadre().id()));
        entidad.setEstado(registro.getEstado());
        entidad.setFechaDesde(registro.getFechaDesde());
        entidad.setFechaHasta(registro.getFechaHasta());
        entidad.reemplazarValores(registro.getValores());
        return CatalogoPersistenceMapper.aModelo(jpa.saveAndFlush(entidad));
    }
}
