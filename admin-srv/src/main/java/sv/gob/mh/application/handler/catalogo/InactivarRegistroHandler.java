package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.InactivarRegistroCommand;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/** HU-ADM-01-13. Errores: CATALOGO_INEXISTENTE, REGISTRO_INEXISTENTE y FECHA_INACTIVACION_FUTURA (R9b). */
@Service
public class InactivarRegistroHandler {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public InactivarRegistroHandler(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional
    public Registro handle(InactivarRegistroCommand command) {
        catalogoRepository.obtenerPorCodigo(command.codigoCatalogo());
        Registro registro = registroRepository.obtenerPorClave(command.codigoCatalogo(), command.clave());
        registro.inactivar(command.fechaHasta());
        return registroRepository.guardar(registro);
    }
}
