package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.HabilitacionModificacionMetasPap;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionMetasPapRepository;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": período de ingreso de
 * información (RN-A.b) y habilitación de modificaciones fuera de plazo (SF-8/SF-9) por unidad
 * ejecutora y año.
 */
final class ProgramacionMetasFisicasPapPlazo {

    private final HabilitacionModificacionMetasPapRepository habilitacionRepository;
    private final ProgramacionPapCalendario calendario;

    ProgramacionMetasFisicasPapPlazo(HabilitacionModificacionMetasPapRepository habilitacionRepository,
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
        HabilitacionModificacionMetasPap habilitacion = habilitacionRepository
                .findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio)
                .orElseGet(() -> HabilitacionModificacionMetasPap.builder()
                        .idUnidadEjecutora(idUnidadEjecutora)
                        .anio(anio)
                        .build());
        habilitacion.setFechaHabilitacion(ProgramacionPapSoporte.ahora());
        habilitacionRepository.save(habilitacion);
    }
}
