package sv.gob.mh.application.query.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * HU-ADM-01-11: los campos pedidos de todos los registros, o el primer campo no KEY si no se pide
 * ninguno (Regla 5). Errores: CATALOGO_INEXISTENTE y CAMPO_INEXISTENTE.
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
    public List<RegistroProyectado> ejecutar(String codigoCatalogo, List<String> nombresCampos) {
        Catalogo catalogo = catalogoRepository.obtenerPorCodigo(codigoCatalogo);
        List<CampoDefinicion> campos = catalogo.camposProyectados(nombresCampos);
        return registroRepository.listarPorCatalogo(codigoCatalogo).stream()
                .map(registro -> new RegistroProyectado(registro, campos))
                .toList();
    }
}
