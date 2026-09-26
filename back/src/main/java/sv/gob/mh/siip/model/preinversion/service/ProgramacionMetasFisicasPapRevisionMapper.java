package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": armado del DTO de la
 * revisión DGICP de la programación del PAP ({@link RevisionProgramacionPap}), compartida con
 * CU-PRE-30.
 */
final class ProgramacionMetasFisicasPapRevisionMapper {

    private ProgramacionMetasFisicasPapRevisionMapper() {
    }

    static RevisionProgramacionPAPDto construirRevisionDto(RevisionProgramacionPap revision) {
        return new RevisionProgramacionPAPDto(revision.getIdUnidadEjecutora(), revision.getAnio(),
                EstadoPAPDto.valueOf(revision.getEstadoPap().name()))
                .observacionesDgicp(revision.getObservacionesDgicp())
                .fechaObservaciones(aOffsetDateTime(revision.getFechaObservaciones()))
                .respuestaInstitucion(revision.getRespuestaInstitucion())
                .fechaRespuesta(aOffsetDateTime(revision.getFechaRespuesta()))
                .comentariosReporteFinancieroDgicp(revision.getComentariosReporteFinancieroDgicp())
                .comentariosReporteMetasFisicasDgicp(revision.getComentariosReporteMetasFisicasDgicp());
    }

    private static OffsetDateTime aOffsetDateTime(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(ProgramacionPapSoporte.ZONA_EL_SALVADOR).toOffsetDateTime();
    }
}
