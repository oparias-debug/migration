package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.dto.CambioUnidadEjecutoraRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoListResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;

/**
 * Reglas de negocio de CU-PRE-01 (Registro y Solicitud de CUP) y CU-PRE-01.5 (Revision y Emision
 * de CUP). Un metodo por operacion del contrato.
 */
public interface ProyectoService {

    /**
     * Registra un proyecto nuevo en estado {@code EN_REGISTRO} para la Unidad Ejecutora/Institución
     * del actor autenticado.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws ValidacionNegocioException si falta un catálogo obligatorio (sector/eje temático) o,
     *         siendo de emergencia, falta el tipo de evento o el N. de Decreto Legislativo.
     */
    ProyectoDto registrar(ProyectoRequestDto request);

    /**
     * Lista proyectos activos de forma paginada, filtrando por estado si se indica. El Administrador
     * ve todos los proyectos; cualquier otro rol solo los de su propia Unidad Ejecutora.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    ProyectoListResponseDto listar(Integer pagina, Integer tamanio, EstadoProyectoDto estado);

    /**
     * Obtiene un proyecto por id, dentro del alcance de Unidad Ejecutora del actor (el Administrador
     * no tiene esa restricción).
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     * @throws AccesoDenegadoException si el proyecto no pertenece a la Unidad Ejecutora del actor.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    ProyectoDto obtener(Long idProyecto);

    /**
     * Edita un registro de proyecto (SF-2). Solo permitido para el Técnico URP y solo mientras el
     * proyecto está en un estado editable ({@code EN_REGISTRO} u {@code OBSERVADO_DGICP_REGISTRO}).
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ConflictoEstadoException si el proyecto no está en un estado editable.
     * @throws ValidacionNegocioException si falta un catálogo obligatorio o, siendo de emergencia,
     *         falta el tipo de evento o el N. de Decreto Legislativo.
     */
    ProyectoDto actualizar(Long idProyecto, ProyectoRequestDto request);

    /**
     * Envía el proyecto a DGICP para revisión: lo pasa a {@code ENVIADO_DGICP_REGISTRO}, registra (o
     * reactiva) la solicitud de CUP vigente y notifica a los Coordinadores PRE (Anexo A.3.1). Solo
     * permitido para el Técnico URP y solo mientras el proyecto está en un estado editable.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ConflictoEstadoException si el proyecto no está en un estado editable.
     * @throws ValidacionNegocioException si, siendo de emergencia, falta el tipo de evento o el N. de
     *         Decreto Legislativo.
     */
    ProyectoDto solicitarCup(Long idProyecto);

    /**
     * Responde una observación de DGICP sobre la solicitud de CUP vigente (Anexo A.3.3): agrega el
     * comentario del Técnico URP y reenvía el proyecto a {@code ENVIADO_DGICP_REGISTRO}. Solo
     * permitido cuando el proyecto está en estado {@code OBSERVADO_DGICP_REGISTRO}.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ConflictoEstadoException si el proyecto no está observado por DGICP o no tiene una
     *         solicitud de CUP vigente.
     * @throws ValidacionNegocioException si la respuesta viene vacía.
     */
    ProyectoDto responderObservacionCup(Long idProyecto, RespuestaObservacionRequestDto request);

    /**
     * Devuelve la solicitud de CUP vigente al Técnico URP con una observación (CU-PRE-01.5, RN 2.7,
     * Anexo A.3.2): agrega el comentario del Técnico PRE (si lo hay) y regresa el proyecto a
     * {@code OBSERVADO_DGICP_REGISTRO}, habilitando el campo "Respuesta" en CU-PRE-01. Solo
     * permitido para el Técnico PRE al que se le asignó la solicitud en CU-PRE-02.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico PRE, o siéndolo, la solicitud no le
     *         fue asignada.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ConflictoEstadoException si el proyecto no está en estado {@code ENVIADO_DGICP_REGISTRO}
     *         o no tiene una solicitud de CUP vigente.
     */
    ProyectoDto devolverSolicitudCup(Long idProyecto, DevolucionSolicitudRequestDto request);

    /**
     * Emite el Código Único de Proyecto de la solicitud vigente (CU-PRE-01.5, RN 2.8.c, Anexo
     * A.3.4): asigna un CUP consecutivo numérico de 5 dígitos desde 10000 en adelante y cambia el
     * estado a {@code CUP_ASIGNADO}. El salto de numeración cada 53 códigos (RN 2.8.c) no se modela
     * por no estar completamente especificado. Solo permitido para el Técnico PRE al que se le
     * asignó la solicitud en CU-PRE-02.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico PRE, o siéndolo, la solicitud no le
     *         fue asignada.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ConflictoEstadoException si el proyecto no está en estado {@code ENVIADO_DGICP_REGISTRO}
     *         o no tiene una solicitud de CUP vigente.
     */
    ProyectoDto emitirCup(Long idProyecto);

    /** Reasigna el proyecto (y su Institución) a otra Unidad Ejecutora. Solo permitido al Administrador.
     *
     * @throws AccesoDenegadoException si el actor no es Administrador.
     * @throws RecursoNoEncontradoException si el proyecto o la Unidad Ejecutora destino no existen.
     */
    ProyectoDto cambiarUnidadEjecutora(Long idProyecto, CambioUnidadEjecutoraRequestDto request);

    /**
     * Elimina lógicamente el proyecto ({@code activo = false}). Solo permitido para el Técnico URP y
     * solo si nunca se solicitó un CUP para el proyecto (RN 4).
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ConflictoEstadoException si el proyecto ya tiene una solicitud de CUP registrada.
     */
    void eliminar(Long idProyecto);
}
