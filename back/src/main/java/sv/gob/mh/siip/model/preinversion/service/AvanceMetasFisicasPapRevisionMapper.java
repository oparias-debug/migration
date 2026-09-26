package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.dto.EstadoRevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": traducción de la revisión única del
 * avance del PAP ({@link RevisionAvancePap}) a su DTO de respuesta según el actor que consulta.
 */
final class AvanceMetasFisicasPapRevisionMapper {

    private AvanceMetasFisicasPapRevisionMapper() {
    }

    /** RN-A.b: los comentarios al reporte DGICP no son visibles para el Técnico URP. */
    static RevisionAvancePAPDto construirRevisionDto(RevisionAvancePap revision, Usuario actor) {
        RevisionAvancePAPDto dto = new RevisionAvancePAPDto(revision.getIdUnidadEjecutora(), revision.getAnio(),
                AvancePapSoporte.dtoDe(revision.getPeriodo()),
                EstadoRevisionAvancePAPDto.valueOf(revision.getEstado().name()))
                .observacionesDgicp(revision.getObservacionesDgicp())
                .fechaObservaciones(aOffsetDateTime(revision.getFechaObservaciones()))
                .respuestaInstitucion(revision.getRespuestaInstitucion())
                .fechaRespuesta(aOffsetDateTime(revision.getFechaRespuesta()));
        if (AvancePapSoporte.esActorInternoDgicp(actor)) {
            dto.comentarioReporteFinancieroDgicp(revision.getComentarioReporteFinancieroDgicp());
            dto.comentarioReporteMetasFisicasDgicp(revision.getComentarioReporteMetasFisicasDgicp());
        }
        return dto;
    }

    private static OffsetDateTime aOffsetDateTime(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(AvancePapSoporte.ZONA_EL_SALVADOR).toOffsetDateTime();
    }
}
