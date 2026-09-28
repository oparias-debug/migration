package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.preinversion.dto.CargarDocumentoViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;

/**
 * Casos de uso de CU-PRE-24 "Viabilidad": el Técnico URP solicita Viabilidad (FB1) y el Viabilizador
 * revisa la ficha, devuelve el proyecto con comentarios (FA01) o emite la Viabilidad (FA02).
 */
public interface ViabilidadService {

    /**
     * Ficha de Viabilidad del proyecto (Anexo A.1) con las acciones disponibles para el usuario
     * autenticado (FB2 paso 5).
     *
     * @param idProyecto identificador del proyecto
     * @return ficha del proyecto
     */
    FichaViabilidadResponseDto consultarFicha(Long idProyecto);

    /**
     * Carga el Documento de Preinversión (reemplaza al vigente) u otro documento anexo (se agrega)
     * (FB1 paso 1; RN02).
     *
     * @param idProyecto identificador del proyecto
     * @param tipoDocumento tipo de documento cargado
     * @param archivo archivo recibido
     * @return el documento cargado y las acciones disponibles
     */
    CargarDocumentoViabilidadResponseDto cargarDocumento(Long idProyecto, TipoDocumentoViabilidad tipoDocumento,
            MultipartFile archivo);

    /**
     * Guarda los comentarios del Viabilizador y las Observaciones Generales/Justificación de la
     * revisión en curso (FA01 pasos 1.1–1.2; FA02 paso 2.1).
     *
     * @param idProyecto identificador del proyecto
     * @param request comentarios por campo y observaciones generales
     * @return lo guardado y las acciones disponibles
     */
    GuardarComentariosViabilidadResponseDto guardarComentarios(Long idProyecto,
            GuardarComentariosViabilidadRequestDto request);

    /**
     * Envía el proyecto al proceso de Viabilidad (FB1 pasos 3–6; RN02, RN04, RN06, RN11).
     *
     * @param idProyecto identificador del proyecto
     */
    void solicitarViabilidad(Long idProyecto);

    /**
     * Devuelve el proyecto al Técnico URP con los comentarios guardados (FA01 pasos 1.3–1.5; RN05,
     * RN10).
     *
     * @param idProyecto identificador del proyecto
     * @return el nuevo estado del proyecto
     */
    EnviarComentariosViabilidadResponseDto enviarComentarios(Long idProyecto);

    /**
     * Emite la Viabilidad del proyecto (FA02 pasos 2.2–2.6; RN02, RN03).
     *
     * @param idProyecto identificador del proyecto
     * @return el nuevo estado y si se habilitó Elegibilidad
     */
    EmitirViabilidadResponseDto emitirViabilidad(Long idProyecto);
}
