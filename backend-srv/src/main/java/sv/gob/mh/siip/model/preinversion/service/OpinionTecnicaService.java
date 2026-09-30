package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.preinversion.dto.ComentariosDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ConclusionesOpinionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EmisionOpinionTecnicaFavorableResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnvioComentariosDgicpResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.InformeOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionesInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionTecnicaResponseDto;

/**
 * Revisión de una gestión de CU-PRE-26 "Opinión Técnica" en la pantalla del Anexo A.1: la DGICP revisa
 * y comenta (HU-PRE-26-03, 04), la institución justifica (HU-PRE-26-05) y la DGICP emite la OT favorable
 * con el visto bueno del Coordinador PRE (HU-PRE-26-07, 08). La apertura de las gestiones está en
 * {@link SolicitudOpinionTecnicaService}.
 */
public interface OpinionTecnicaService {

    /**
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @return la pantalla del Anexo A.1
     */
    OpinionTecnicaResponseDto obtener(Long idProyecto, Long idGestion);

    /**
     * Botón "Guardar" de la DGICP (FA02).
     *
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @param request comentarios DGICP
     * @return la pantalla actualizada
     */
    OpinionTecnicaResponseDto guardarComentarios(Long idProyecto, Long idGestion, ComentariosDgicpRequestDto request);

    /**
     * Botón "Enviar comentarios" (FA03, RN14).
     *
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @param request comentarios DGICP
     * @return el estado del proyecto, el plazo y la ruta de retorno
     */
    EnvioComentariosDgicpResponseDto enviarComentarios(Long idProyecto, Long idGestion,
            ComentariosDgicpRequestDto request);

    /**
     * Botón "Guardar" del Técnico URP (FA02, FA03.1).
     *
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @param request justificaciones de la institución
     * @return la pantalla actualizada
     */
    OpinionTecnicaResponseDto guardarJustificaciones(Long idProyecto, Long idGestion,
            JustificacionesInstitucionRequestDto request);

    /**
     * Guarda las "Conclusiones" (FA01 paso 1.1).
     *
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @param request conclusiones
     * @return la pantalla actualizada
     */
    OpinionTecnicaResponseDto guardarConclusiones(Long idProyecto, Long idGestion,
            ConclusionesOpinionTecnicaRequestDto request);

    /**
     * "Visto bueno OT" del Coordinador PRE (FA01 pasos 1.3–1.4).
     *
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @return la pantalla actualizada
     */
    OpinionTecnicaResponseDto darVistoBueno(Long idProyecto, Long idGestion);

    /**
     * Botón "OT favorable" (FA01 pasos 1.5–1.6).
     *
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @param notaOt "Nota de OT" firmada por el Director DGICP
     * @param numeroNotaOt "N° de nota de OT"
     * @return el estado resultante
     */
    EmisionOpinionTecnicaFavorableResponseDto emitirFavorable(Long idProyecto, Long idGestion, MultipartFile notaOt,
            String numeroNotaOt);

    /**
     * "Ver Informe" del Histórico de OT (Anexo A1.5 → Anexo A.6).
     *
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de una gestión de OT emitida
     * @return el informe de la OT
     */
    InformeOpinionTecnicaResponseDto obtenerInforme(Long idProyecto, Long idGestion);
}
