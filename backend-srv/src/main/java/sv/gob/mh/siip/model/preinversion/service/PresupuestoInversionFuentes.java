package sv.gob.mh.siip.model.preinversion.service;

import java.util.Objects;

import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.FuentesFinanciamientoRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;

/**
 * Fuentes de financiamiento del Presupuesto de Inversión (CU-PRE-20):
 * validación de la solicitud, aplicación sobre el presupuesto y conversión a su
 * DTO de respuesta.
 */
final class PresupuestoInversionFuentes {
    
    private PresupuestoInversionFuentes() {
    
    }

    /**
     * Exige al menos una fuente de financiamiento y una fuente de recursos no
     * vacía, en ese orden.
     *
     * @param req solicitud de fuentes de financiamiento
     */
    static void validar(FuentesFinanciamientoRequestDto req) {
        if (req.getFuentesFinanciamiento() == null || req.getFuentesFinanciamiento().isEmpty()) {
            throw PresupuestoInversionValidaciones.invalido("fuentesFinanciamiento");
        }
        String fuenteRecursos = req.getFuenteRecursos();
        if (fuenteRecursos == null || fuenteRecursos.isBlank()) {
            throw PresupuestoInversionValidaciones.invalido("fuenteRecursos");
        }
    }

    /**
     * Copia sobre el presupuesto las fuentes de financiamiento y la fuente de
     * recursos (recortada) de una solicitud ya validada con
     * {@link #validar(FuentesFinanciamientoRequestDto)}.
     *
     * @param p presupuesto del proyecto
     * @param req solicitud validada
     */
    static void aplicar(PresupuestoProyecto p, FuentesFinanciamientoRequestDto req) {
        p.setFuentesFinanciamiento(
                req.getFuentesFinanciamiento().stream().map(x -> FuenteFinanciamiento.valueOf(x.name())).toList());
        // Ya validada: validar() exige una fuente de recursos no vacía.
        p.setFuenteRecursos(Objects.requireNonNull(req.getFuenteRecursos()).trim());
    }

    /**
     * Fuentes de financiamiento registradas en el presupuesto.
     *
     * @param p presupuesto del proyecto
     * @return el DTO de fuentes de financiamiento
     */
    static FuentesFinanciamientoRequestDto dto(PresupuestoProyecto p) {
        return new FuentesFinanciamientoRequestDto().fuenteRecursos(p.getFuenteRecursos()).fuentesFinanciamiento(
                p.getFuentesFinanciamiento().stream().map(x -> FuenteFinanciamientoDto.valueOf(x.name())).toList());
    }
}
