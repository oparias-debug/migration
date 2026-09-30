package sv.gob.mh.application.handler.catalogo;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.CrearRegistroCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.RegistroPadre;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * HU-ADM-01-09. Errores: CATALOGO_INEXISTENTE, CAMPO_INEXISTENTE, VALOR_CAMPO_FALTANTE,
 * REGISTRO_PADRE_REQUERIDO, REGISTRO_PADRE_INEXISTENTE y CLAVE_REGISTRO_DUPLICADA.
 */
@Service
public class CrearRegistroHandler {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public CrearRegistroHandler(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional
    public Registro handle(CrearRegistroCommand command) {
        Catalogo catalogo = catalogoRepository.obtenerPorCodigo(command.codigoCatalogo());
        Map<String, String> valores = catalogo.valoresCompletos(command.valores());
        RegistroPadre registroPadre = catalogo.catalogoDelRegistroPadre(command.clavePadre())
                .map(codigoPadre -> registroRepository.buscarPorClave(codigoPadre, command.clavePadre())
                        .map(Registro::comoPadre)
                        .orElseThrow(() -> Catalogo.registroPadreInexistente(command.clavePadre())))
                .orElse(null);
        String clave = Registro.clave(catalogo, valores);
        if (registroRepository.existeClave(command.codigoCatalogo(), clave)) {
            throw ErrorCatalogoException.reglaNegocio("CLAVE_REGISTRO_DUPLICADA",
                    "Ya existe un registro con el valor KEY indicado en el catálogo.", "values", clave);
        }
        return registroRepository.guardar(
                Registro.nuevo(catalogo, valores, registroPadre, command.fechaDesde(), command.fechaHasta()));
    }
}
