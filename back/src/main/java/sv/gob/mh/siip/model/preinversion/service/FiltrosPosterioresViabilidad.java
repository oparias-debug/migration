package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/**
 * Lo que CU-PRE-24 "Viabilidad" necesita saber de los filtros que le siguen (RN03): CU-PRE-25
 * "Elegibilidad" y CU-PRE-26 "Opinión Técnica". Solo consulta; no modifica datos de esos CU.
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
     * RN03/RN11: tras emitir la Viabilidad, el proyecto solo vuelve a ella si la OT lo devolvió para
     * ajustes después de esa emisión.
     *
     * @param idProyecto identificador del proyecto
     * @param fecha fecha de la última emisión de Viabilidad; {@code null} acepta cualquier OT observada
     * @return {@code true} si la última Opinión Técnica quedó "Observado" después de {@code fecha}
     */
    public boolean otDevolvioDespuesDe(Long idProyecto, LocalDateTime fecha) {
        return opinionesTecnicas.findFirstByProyectoIdOrderByFechaEmisionDesc(idProyecto)
                .filter(ot -> ot.getResultado() == ResultadoOpinionTecnica.OBSERVADO)
                .filter(ot -> fecha == null || (ot.getFechaEmision() != null && ot.getFechaEmision().isAfter(fecha)))
                .isPresent();
    }

    /**
     * RN11: si la última Opinión Técnica del proyecto quedó "Observado", la nueva solicitud de
     * Viabilidad responde a esa observación y exige que cada uno de sus comentarios esté respondido en
     * "Justificación Institución".
     *
     * @param idProyecto identificador del proyecto
     * @return {@code true} si hay comentarios de una OT observada sin responder
     */
    public boolean tieneComentariosOtSinResponder(Long idProyecto) {
        return opinionesTecnicas.findFirstByProyectoIdOrderByFechaEmisionDesc(idProyecto)
                .filter(ot -> ot.getResultado() == ResultadoOpinionTecnica.OBSERVADO)
                .map(ot -> comentariosOt.contarSinResponder(ot.getId()) > 0)
                .orElse(false);
    }
}
