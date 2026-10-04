package sv.gob.mh.application.query.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * HU-ADM-01-12 (SF-10, RN-07): el registro con ese valor KEY, con los campos del Field Set. Si no
 * existe, el Result Set queda vacío (no es error). Errores: E-10 y E-21.
 */
@Service
public class BuscarRegistroPorClaveQuery {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public BuscarRegistroPorClaveQuery(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional(readOnly = true)
    public ConjuntoResultado ejecutar(String codigoCatalogo, String clave, List<String> nombresCampos) {
        var catalogo = catalogoRepository.obtenerPorCodigo(codigoCatalogo);
        List<CampoDefinicion> campos = catalogo.conjuntoDeCampos(nombresCampos);
        return ConjuntoResultado.de(campos, registroRepository.buscarPorClave(codigoCatalogo, clave).stream().toList());
    }
}
