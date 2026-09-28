package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.HabilitacionModificacionPap;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionPapRepository;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": período de ingreso de
 * información (RN-A.b) y habilitación de modificaciones fuera de plazo por unidad ejecutora y año.
 */
final class ProgramacionFinancieraPapPlazo {

    private final HabilitacionModificacionPapRepository habilitacionRepository;
    private final ProgramacionPapCalendario calendario;

    ProgramacionFinancieraPapPlazo(HabilitacionModificacionPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository) {
        this.habilitacionRepository = habilitacionRepository;
        this.calendario = new ProgramacionPapCalendario(calendarioEventoRepository);
    }

    /** RN-A.b: ver {@link ProgramacionPapCalendario#verificarPeriodoAbierto}. */
    void verificarPeriodoAbierto(Long idUnidadEjecutora, Integer anio) {
        boolean habilitado = habilitacionRepository.findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio).isPresent();
        calendario.verificarPeriodoAbierto(habilitado, anio);
    }

    void habilitarModificacionesFueraPlazo(Long idUnidadEjecutora, Integer anio) {
        HabilitacionModificacionPap habilitacion = habilitacionRepository
                .findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio)
                .orElseGet(() -> HabilitacionModificacionPap.builder()
                        .idUnidadEjecutora(idUnidadEjecutora)
                        .anio(anio)
                        .build());
        habilitacion.setFechaHabilitacion(ProgramacionPapSoporte.ahora());
        habilitacionRepository.save(habilitacion);
    }
}
