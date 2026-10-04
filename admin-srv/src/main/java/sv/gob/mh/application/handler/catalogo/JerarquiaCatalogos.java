package sv.gob.mh.application.handler.catalogo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.RegistroPadre;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;

/**
 * Reglas de la jerarquía padre-hijo entre catálogos (RN-05, SF-01, SF-04, SF-15), que necesitan
 * consultar otros catálogos del catalogMaster. Se invoca dentro de la transacción del handler.
 */
@Service
public class JerarquiaCatalogos {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;

    public JerarquiaCatalogos(CatalogoRepository catalogoRepository, RegistroRepository registroRepository) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
    }

    /**
     * El catálogo {@code codigoPadre} puede ser padre de {@code codigoHijo}: existe (E-06), no
     * genera un ciclo (E-08), no tiene ya otro hijo (E-07) y está activo (E-15).
     */
    public void validarPadre(String codigoHijo, String codigoPadre) {
        Catalogo padre = catalogoRepository.buscarPorCodigo(codigoPadre)
                .orElseThrow(() -> ErroresCatalogo.padreInexistente(codigoPadre));
        if (generaCiclo(codigoHijo, padre)) {
            throw ErroresCatalogo.jerarquiaCiclica();
        }
        Optional<Catalogo> hijoActual = catalogoRepository.buscarHijo(codigoPadre);
        if (hijoActual.isPresent() && !hijoActual.get().getCodigo().equals(codigoHijo)) {
            throw ErroresCatalogo.padreConHijo(codigoPadre, hijoActual.get().getCodigo());
        }
        if (!padre.estaActivo()) {
            throw ErroresCatalogo.padreInactivo(codigoPadre);
        }
    }

    /** Ciclo: el padre es el propio catálogo o uno de sus descendientes (el catálogo es ancestro del padre). */
    private boolean generaCiclo(String codigoHijo, Catalogo padre) {
        Set<String> visitados = new HashSet<>();
        Catalogo actual = padre;
        while (actual != null && visitados.add(actual.getCodigo())) {
            if (actual.getCodigo().equals(codigoHijo)) {
                return true;
            }
            String siguiente = actual.getCatalogoPadreCodigo();
            actual = siguiente == null ? null : catalogoRepository.buscarPorCodigo(siguiente).orElse(null);
        }
        return false;
    }

    /**
     * SF-04/SF-15: asigna {@code nuevoPadre} ({@code null} deja el catálogo plano). Si el catálogo
     * tiene registros se exige confirmación (S-04). Confirmada, al quitar el padre sus registros
     * quedan sin registro padre; con un padre nuevo, cada registro se enlaza al que indica
     * {@code registrosPadre} (valor KEY del registro → valor KEY del registro padre), que debe
     * existir y estar activo en el nuevo catálogo padre (modelo de dominio v4.0, E-18).
     */
    public void cambiarPadre(Catalogo catalogo, String nuevoPadre, boolean confirmado,
            Map<String, String> registrosPadre) {
        if (nuevoPadre != null) {
            validarPadre(catalogo.getCodigo(), nuevoPadre);
        }
        List<Registro> registros = registroRepository.listarPorCatalogo(catalogo.getCodigo());
        if (!registros.isEmpty()) {
            if (!confirmado) {
                throw ErroresCatalogo.confirmacionCambioPadreRequerida(catalogo.getCodigo());
            }
            if (nuevoPadre == null) {
                registroRepository.quitarRegistrosPadre(catalogo);
            } else {
                for (Registro registro : registros) {
                    registro.asignarRegistroPadre(registroPadre(nuevoPadre, registrosPadre.get(registro.getClave())));
                    registroRepository.guardar(registro);
                }
            }
        }
        catalogo.asignarPadre(nuevoPadre);
    }

    /** E-18 si {@code clavePadre} falta o no es un registro activo del catálogo {@code codigoPadre}. */
    private RegistroPadre registroPadre(String codigoPadre, String clavePadre) {
        Optional<Registro> padre = clavePadre == null || clavePadre.isBlank() ? Optional.empty()
                : registroRepository.buscarPorClave(codigoPadre, clavePadre);
        return padre.filter(Registro::estaActivo)
                .map(Registro::comoPadre)
                .orElseThrow(() -> ErroresCatalogo.registroPadreInvalido(clavePadre, codigoPadre));
    }

    /** SF-06: un catálogo con padre solo se reactiva si su padre está activo (E-15). */
    public void exigirPadreActivo(Catalogo catalogo) {
        String codigoPadre = catalogo.getCatalogoPadreCodigo();
        if (codigoPadre != null && !catalogoRepository.buscarPorCodigo(codigoPadre).map(Catalogo::estaActivo)
                .orElse(false)) {
            throw ErroresCatalogo.padreInactivo(codigoPadre);
        }
    }
}
