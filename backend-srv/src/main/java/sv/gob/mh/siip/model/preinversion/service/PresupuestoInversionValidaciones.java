package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;

/**
 * Error de validación común del Presupuesto de Inversión (CU-PRE-20): todos los
 * campos obligatorios se rechazan con el mismo título y mensaje, variando solo
 * el campo reportado.
 */
final class PresupuestoInversionValidaciones {

    /**
     * Título de la validación de negocio del presupuesto.
     */
    public static final String TITULO = "Validación de presupuesto";

    /**
     * Mensaje asociado a cada campo obligatorio faltante.
     */
    public static final String CAMPO_OBLIGATORIO = "Campo obligatorio";

    private PresupuestoInversionValidaciones() {
    }

    /**
     * Construye la excepción de validación para un campo obligatorio.
     *
     * @param campo nombre del campo que falta o es inválido
     * @return la excepción lista para lanzarse
     */
    static ValidacionNegocioException invalido(String campo) {
        return new ValidacionNegocioException(TITULO,
                List.of(new ErrorDetalleDto().campo(campo).mensaje(CAMPO_OBLIGATORIO)));
    }
}
