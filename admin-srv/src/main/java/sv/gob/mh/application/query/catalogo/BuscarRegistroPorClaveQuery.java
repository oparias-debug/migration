package sv.gob.mh.application.query.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * HU-ADM-01-10: como {@link BuscarRegistrosQuery} pero para el registro con ese KEY (Regla 4).
 * Errores: CATALOGO_INEXISTENTE, REGISTRO_INEXISTENTE (E2) y CAMPO_INEXISTENTE (E3).
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
    public RegistroProyectado ejecutar(String codigoCatalogo, String clave, List<String> nombresCampos) {
        Catalogo catalogo = catalogoRepository.obtenerPorCodigo(codigoCatalogo);
        Registro registro = registroRepository.obtenerPorClave(codigoCatalogo, clave);
        return new RegistroProyectado(registro, catalogo.camposProyectados(nombresCampos));
    }
}
