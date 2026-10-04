package sv.gob.mh.application.query.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * HU-ADM-01-13 (SF-11, RN-08): todos los registros del catálogo, activos e inactivos, con los
 * campos del Field Set. Errores: E-10 y E-21.
 */
@Service
public class BuscarRegistrosQuery {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public BuscarRegistrosQuery(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional(readOnly = true)
    public ConjuntoResultado ejecutar(String codigoCatalogo, List<String> nombresCampos) {
        var catalogo = catalogoRepository.obtenerPorCodigo(codigoCatalogo);
        List<CampoDefinicion> campos = catalogo.conjuntoDeCampos(nombresCampos);
        return ConjuntoResultado.de(campos, registroRepository.listarPorCatalogo(codigoCatalogo));
    }
}
