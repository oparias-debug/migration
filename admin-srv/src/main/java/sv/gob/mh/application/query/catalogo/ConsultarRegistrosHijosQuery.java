package sv.gob.mh.application.query.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * HU-ADM-01-15 (SF-13, RN-24): registros del catálogo hijo cuyo registro padre es el indicado,
 * con el Field Set armado sobre el catálogo hijo. Sin catálogo hijo, el resultado no tiene
 * catálogo hijo y su Field Set y Result Set son vacíos (no es error). Errores: E-10, E-22 y E-21.
 */
@Service
public class ConsultarRegistrosHijosQuery {

    /** Resultado de SF-13 sin el argumento ni el catálogo padre, que ya conoce quien consulta. */
    public record RegistrosHijos(String codigoCatalogoHijo, ConjuntoResultado conjunto) {
    }

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public ConsultarRegistrosHijosQuery(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional(readOnly = true)
    public RegistrosHijos ejecutar(String codigoCatalogo, String clave, List<String> nombresCampos) {
        catalogoRepository.obtenerPorCodigo(codigoCatalogo);
        var padre = registroRepository.obtenerPorClave(codigoCatalogo, clave);
        Optional<Catalogo> hijo = catalogoRepository.buscarHijo(codigoCatalogo);
        if (hijo.isEmpty()) {
            return new RegistrosHijos(null, ConjuntoResultado.VACIO);
        }
        List<CampoDefinicion> campos = hijo.get().conjuntoDeCampos(nombresCampos);
        return new RegistrosHijos(hijo.get().getCodigo(),
                ConjuntoResultado.de(campos, registroRepository.listarHijos(padre.getId())));
    }
}
