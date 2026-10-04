package sv.gob.mh.application.handler.catalogo;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.CrearRegistroCommand;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.RegistroPadre;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * HU-ADM-01-09 (SF-07). Errores: E-10, E-13 (RN-14), E-21, E-14, E-09, E-18 (RN-05) y E-17 (S-06).
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
        var catalogo = catalogoRepository.obtenerPorCodigo(command.codigoCatalogo());
        if (!catalogo.estaActivo()) {
            throw ErroresCatalogo.catalogoInactivo();
        }
        Map<String, String> valores = catalogo.valoresParaRegistroNuevo(command.valores());
        var registro = Registro.nuevo(catalogo, valores, registroPadre(catalogo, command.clavePadre()),
                command.fechaDesde(), command.fechaHasta());
        if (registroRepository.existeClave(catalogo.getCodigo(), registro.getClave())) {
            throw ErroresCatalogo.claveDuplicada(registro.getClave());
        }
        return registroRepository.guardar(registro);
    }

    /** RN-05: si el catálogo tiene padre, el registro padre debe existir y estar activo en el catálogo padre. */
    private RegistroPadre registroPadre(Catalogo catalogo, String clavePadre) {
        return catalogo.catalogoDelRegistroPadre(clavePadre)
                .map(codigoPadre -> registroRepository.buscarPorClave(codigoPadre, clavePadre)
                        .filter(Registro::estaActivo)
                        .map(Registro::comoPadre)
                        .orElseThrow(() -> ErroresCatalogo.registroPadreInvalido(clavePadre, codigoPadre)))
                .orElse(null);
    }
}
