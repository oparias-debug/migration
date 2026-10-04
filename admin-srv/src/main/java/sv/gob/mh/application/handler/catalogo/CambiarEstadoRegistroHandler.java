package sv.gob.mh.application.handler.catalogo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.application.command.catalogo.CambiarEstadoRegistroCommand;
import sv.gob.mh.domain.model.catalogo.ErroresCatalogo;
import sv.gob.mh.domain.model.catalogo.Registro;
import sv.gob.mh.domain.model.catalogo.RegistroPadre;
import sv.gob.mh.domain.model.catalogo.Vigencia;
import sv.gob.mh.domain.repository.catalogo.CatalogoRepository;
import sv.gob.mh.domain.repository.catalogo.RegistroRepository;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * HU-ADM-01-11 (SF-09): estado y TO DATE de un registro.
 * <ul>
 * <li>INACTIVE, o una TO DATE hoy o pasada, inactivan el registro y, en cascada, sus registros
 * hijos (RN-12, RN-14).</li>
 * <li>ACTIVE reactiva un registro inactivo: exige catálogo y registro padre activos (E-20) y TO
 * DATE vacía o futura (E-16); sus registros hijos conservan su estado (S-03).</li>
 * <li>Una TO DATE futura no cambia el estado.</li>
 * </ul>
 * Errores: E-10, E-22, E-09, E-16 y E-20.
 */
@Service
public class CambiarEstadoRegistroHandler {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final InactivacionEnCascada cascada;

    public CambiarEstadoRegistroHandler(CatalogoRepository catalogoRepository, RegistroRepository registroRepository,
            InactivacionEnCascada cascada) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.cascada = cascada;
    }

    @Transactional
    public Registro handle(CambiarEstadoRegistroCommand command) {
        catalogoRepository.obtenerPorCodigo(command.codigoCatalogo());
        var registro = registroRepository.obtenerPorClave(command.codigoCatalogo(), command.clave());
        EstadoVigencia estado = command.estado();
        if (estado == EstadoVigencia.INACTIVE && !registro.estaActivo()) {
            return registro;
        }
        Vigencia.validarRango(registro.getFechaDesde(), command.fechaHasta());
        if (estado == EstadoVigencia.ACTIVE && !registro.estaActivo()) {
            if (!registro.getCatalogo().estaActivo() || !registroPadreActivo(registro.getRegistroPadre())) {
                throw ErroresCatalogo.padreOCatalogoInactivo();
            }
            registro.reactivar(command.fechaHasta());
            return registroRepository.guardar(registro);
        }
        if ((estado == EstadoVigencia.INACTIVE || Vigencia.vencido(command.fechaHasta())) && registro.estaActivo()) {
            registro.inactivar(command.fechaHasta());
            return cascada.guardarRegistroInactivo(registro);
        }
        registro.cambiarFechaHasta(command.fechaHasta());
        return registroRepository.guardar(registro);
    }

    private boolean registroPadreActivo(RegistroPadre padre) {
        return padre == null || registroRepository.buscarPorClave(padre.codigoCatalogo(), padre.clave())
                .map(Registro::estaActivo)
                .orElse(false);
    }
}
