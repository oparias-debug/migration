package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitudOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/**
 * Lo que CU-PRE-24 "Viabilidad" y CU-PRE-25 "Elegibilidad" necesitan saber de los filtros que les
 * siguen (RN03 de CU-PRE-24): la Elegibilidad y la Opinión Técnica de CU-PRE-26. Solo consulta; no
 * modifica datos de esos CU.
 *
 * <p>RN14 de CU-PRE-26 define a qué filtro vuelve el proyecto cuando la OT lo devuelve: los comentarios
 * a los campos del proyecto reabren la Viabilidad y los comentarios a los criterios de elegibilidad
 * reabren la Elegibilidad.
 */
@Component
@Transactional(readOnly = true)
public class FiltrosPosterioresViabilidad {

    private final ElegibilidadRepository elegibilidades;
    private final OpinionTecnicaRepository opinionesTecnicas;
    private final ComentarioOpinionTecnicaRepository comentariosOt;

    public FiltrosPosterioresViabilidad(ElegibilidadRepository elegibilidades,
            OpinionTecnicaRepository opinionesTecnicas,
            ComentarioOpinionTecnicaRepository comentariosOt) {
        this.elegibilidades = elegibilidades;
        this.opinionesTecnicas = opinionesTecnicas;
        this.comentariosOt = comentariosOt;
    }

    /**
     * RN03: el proceso de Elegibilidad solo se requiere una vez.
     *
     * @param idProyecto identificador del proyecto
     * @return {@code true} si el proyecto ya pasó por CU-PRE-25 "Elegibilidad"
     */
    public boolean yaPasoPorElegibilidad(Long idProyecto) {
        return elegibilidades.existsByProyectoId(idProyecto);
    }

    /**
     * RN03/RN11 de CU-PRE-24: tras emitir la Viabilidad, el proyecto solo vuelve a ella si la OT lo
     * devolvió con comentarios a sus campos después de esa emisión (RN14 de CU-PRE-26), o si el Técnico
     * URP inició después una Actualización de OT, que continúa por la Viabilidad (FA04 paso 4.5).
     *
     * @param idProyecto identificador del proyecto
     * @param fecha fecha de la última emisión de Viabilidad; {@code null} acepta cualquier devolución
     * @return {@code true} si la Viabilidad vuelve a habilitarse
     */
    public boolean otReabrioViabilidadDespuesDe(Long idProyecto, LocalDateTime fecha) {
        boolean devuelta = observadaDespuesDe(idProyecto, fecha)
                .filter(ot -> comentariosOt.tieneComentariosProyecto(ot.getId()))
                .isPresent();
        return devuelta || actualizacionSolicitadaDespuesDe(idProyecto, fecha);
    }

    /**
     * RN07/RN09 de CU-PRE-25: tras emitir la Elegibilidad, la ficha solo vuelve a habilitarse si la OT
     * envió comentarios a los criterios de elegibilidad después de esa emisión (RN14 de CU-PRE-26).
     *
     * @param idProyecto identificador del proyecto
     * @param fecha fecha de la última emisión de Elegibilidad; {@code null} acepta cualquier devolución
     * @return {@code true} si la Elegibilidad vuelve a habilitarse
     */
    public boolean otReabrioElegibilidadDespuesDe(Long idProyecto, LocalDateTime fecha) {
        return observadaDespuesDe(idProyecto, fecha)
                .filter(ot -> comentariosOt.tieneComentariosElegibilidad(ot.getId()))
                .isPresent();
    }

    /**
     * RN14 de CU-PRE-26: si la OT comentó los campos del proyecto y también los criterios de
     * elegibilidad, la Viabilidad reemitida debe pasar otra vez por la Elegibilidad antes de volver a
     * la OT.
     *
     * @param idProyecto identificador del proyecto
     * @return {@code true} si hay comentarios de la OT a la Elegibilidad sin reemitirla todavía
     */
    public boolean otPideElegibilidad(Long idProyecto) {
        LocalDateTime ultimaEmision = elegibilidades.findFirstByProyectoIdOrderByFechaEvaluacionDescIdDesc(idProyecto)
                .map(Elegibilidad::getFechaEvaluacion)
                .orElse(null);
        return otReabrioElegibilidadDespuesDe(idProyecto, ultimaEmision);
    }

    /**
     * RN11 de CU-PRE-24: si la última Opinión Técnica del proyecto quedó "Observado", la nueva solicitud
     * de Viabilidad responde a esa observación y exige que cada comentario a los campos o documentos del
     * proyecto esté respondido en "Justificación Institución". Los comentarios a la Elegibilidad no
     * cuentan: los responde el Viabilizador al reemitirla. No aplica a una gestión archivada por
     * vencimiento del plazo (RN09 de CU-PRE-26): esa solicitud se tramita de nuevo.
     *
     * @param idProyecto identificador del proyecto
     * @return {@code true} si hay comentarios de una OT observada al proyecto sin responder
     */
    public boolean tieneComentariosProyectoSinResponder(Long idProyecto) {
        return observadaVigente(idProyecto)
                .map(ot -> comentariosOt.contarProyectoSinResponder(ot.getId()) > 0)
                .orElse(false);
    }

    /**
     * RN15 de CU-PRE-25: la reemisión de la Elegibilidad exige que los comentarios de la OT a la
     * Elegibilidad estén respondidos. Los comentarios a los campos del proyecto no cuentan.
     *
     * @param idProyecto identificador del proyecto
     * @return {@code true} si hay comentarios de una OT observada a la Elegibilidad sin responder
     */
    public boolean tieneComentariosElegibilidadSinResponder(Long idProyecto) {
        return observadaVigente(idProyecto)
                .map(ot -> comentariosOt.contarElegibilidadSinResponder(ot.getId()) > 0)
                .orElse(false);
    }

    /** Última gestión cerrada de la OT, si quedó "Observado" y no se archivó. */
    private Optional<OpinionTecnica> observadaVigente(Long idProyecto) {
        return opinionesTecnicas.findFirstByProyectoIdOrderByFechaEmisionDesc(idProyecto)
                .filter(OpinionTecnica::estaObservada);
    }

    /** Última gestión cerrada de la OT, si quedó "Observado" después de {@code fecha}. */
    private Optional<OpinionTecnica> observadaDespuesDe(Long idProyecto, LocalDateTime fecha) {
        return opinionesTecnicas.findFirstByProyectoIdOrderByFechaEmisionDesc(idProyecto)
                .filter(ot -> ot.getResultado() == ResultadoOpinionTecnica.OBSERVADO)
                .filter(ot -> fecha == null || (ot.getFechaEmision() != null && ot.getFechaEmision().isAfter(fecha)));
    }

    private boolean actualizacionSolicitadaDespuesDe(Long idProyecto, LocalDateTime fecha) {
        if (fecha == null) {
            return opinionesTecnicas.existsByProyectoIdAndTipoSolicitudAndResultadoIsNullAndFechaArchivoIsNull(
                    idProyecto, TipoSolicitudOpinionTecnica.ACTUALIZACION_OT);
        }
        return opinionesTecnicas
                .existsByProyectoIdAndTipoSolicitudAndResultadoIsNullAndFechaArchivoIsNullAndFechaSolicitudAfter(
                        idProyecto, TipoSolicitudOpinionTecnica.ACTUALIZACION_OT, fecha);
    }
}
