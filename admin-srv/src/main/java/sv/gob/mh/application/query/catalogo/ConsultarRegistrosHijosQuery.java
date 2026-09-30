package sv.gob.mh.application.query.catalogo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * HU-ADM-01-14: registros de los catálogos hijos enlazados a este registro (Reglas 23, 24).
 * Errores: CATALOGO_INEXISTENTE, REGISTRO_INEXISTENTE y CATALOGO_SIN_CATALOGO_HIJO.
 */
@Service
public class ConsultarRegistrosHijosQuery {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public ConsultarRegistrosHijosQuery(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional(readOnly = true)
    public List<Registro> ejecutar(String codigoCatalogo, String clave) {
        catalogoRepository.obtenerPorCodigo(codigoCatalogo);
        Long idRegistro = registroRepository.obtenerPorClave(codigoCatalogo, clave).getId();
        exigirCatalogoHijo(codigoCatalogo);
        return registroRepository.listarHijos(idRegistro);
    }

    private void exigirCatalogoHijo(String codigoCatalogo) {
        if (catalogoRepository.listarHijos(codigoCatalogo).isEmpty()) {
            throw ErrorCatalogoException.reglaNegocio("CATALOGO_SIN_CATALOGO_HIJO",
                    "El catálogo de este registro no tiene catálogo hijo definido.");
        }
    }
}
