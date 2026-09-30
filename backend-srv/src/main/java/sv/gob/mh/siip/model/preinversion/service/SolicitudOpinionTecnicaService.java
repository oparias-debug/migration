package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.preinversion.dto.ActualizacionOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.AsignacionOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.AsignarOpinionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionesTecnicasProyectoResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TiposSolicitudOpinionTecnicaResponseDto;

/**
 * Apertura de las gestiones de CU-PRE-26 "Opinión Técnica": la institución solicita la OT
 * (HU-PRE-26-01) o su actualización (HU-PRE-26-10) y el Coordinador PRE asigna el caso (HU-PRE-26-02).
 * La revisión de cada gestión está en {@link OpinionTecnicaService}.
 */
public interface SolicitudOpinionTecnicaService {

    /**
     * @param idProyecto identificador del proyecto
     * @return las opciones del menú GESTIÓN › OT con su habilitación (Anexo A – RN1, RN2)
     */
    TiposSolicitudOpinionTecnicaResponseDto consultarTiposSolicitud(Long idProyecto);

    /**
     * @param idProyecto identificador del proyecto
     * @return las gestiones de OT del proyecto, de la más reciente a la más antigua
     */
    OpinionesTecnicasProyectoResponseDto listar(Long idProyecto);

    /**
     * Botón "Solicitar OT" (RN04, RN07 a).
     *
     * @param idProyecto identificador del proyecto
     * @param notaSolicitudOt archivo "Nota de solicitud de OT"
     * @return la gestión abierta
     */
    SolicitudOpinionTecnicaResponseDto solicitar(Long idProyecto, MultipartFile notaSolicitudOt);

    /**
     * Actualización de OT (FA04).
     *
     * @param idProyecto identificador del proyecto
     * @return la gestión abierta y los campos habilitados
     */
    ActualizacionOpinionTecnicaResponseDto solicitarActualizacion(Long idProyecto);

    /**
     * El Coordinador PRE asigna el caso (RN07 b).
     *
     * @param idProyecto identificador del proyecto
     * @param idGestion identificador de la gestión de OT
     * @param request Técnico PRE asignado
     * @return la asignación registrada
     */
    AsignacionOpinionTecnicaResponseDto asignar(Long idProyecto, Long idGestion,
            AsignarOpinionTecnicaRequestDto request);
}
