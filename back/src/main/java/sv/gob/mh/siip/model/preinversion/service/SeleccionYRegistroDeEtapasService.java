package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;

/**
 * Reglas de negocio de CU-PRE-3.5 (Selección y Registro de Etapas). Un método por operación del
 * contrato (CU-PRE-03.5.openapi.yaml).
 */
public interface SeleccionYRegistroDeEtapasService {

    /**
     * Consulta el estado actual de la Ruta de Preinversión (Anexo A.2).
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    RutaPreinversionDto obtenerRutaPreinversion(Long idProyecto);

    /**
     * Calcula (sin persistir) las etapas sugeridas según los 3 criterios calificados (RN10, Anexo
     * B.2). Solo la combinación documentada en el CU tiene una regla explícita; para cualquier
     * otra combinación se devuelve la ruta completa (las 5 etapas) por defecto — ver
     * {@code SeleccionYRegistroDeEtapasServiceImpl}.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ValidacionNegocioException si falta calificar alguno de los 3 criterios (RN01).
     * @throws ConflictoEstadoException si el proyecto no es de iniciativa PROYECTO (RN07/RN08).
     */
    RutaPreinversionSugeridaDto generarRutaPreinversion(Long idProyecto, CriteriosCalificacionDto criterios);

    /**
     * Persiste la ruta calculada a partir de los 3 criterios (mismo cálculo de
     * {@link #generarRutaPreinversion}), trasladando las etapas a Registro de Etapas.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ValidacionNegocioException si falta calificar alguno de los 3 criterios (RN01).
     */
    RutaPreinversionDto aceptarRutaPreinversion(Long idProyecto, CriteriosCalificacionDto criterios);

    /**
     * Modifica manualmente la Ruta de Preinversión ya establecida (RN03: justificación
     * obligatoria). RN13: si la nueva selección deja fuera una etapa que ya tenía Opinión Técnica,
     * esa etapa se marca {@code bloqueadaPorModificacion = true} sin perder su información, en vez
     * de rechazar la operación.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ValidacionNegocioException si falta la justificación (RN03).
     */
    RutaPreinversionDto modificarRutaPreinversion(Long idProyecto, ModificarRutaPreinversionRequestDto request);

    /** Consulta la tabla "Registro de Etapas" (Anexo A.1).
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    List<EtapaDto> listarEtapas(Long idProyecto);

    /**
     * Registra costo y fechas de las etapas (botón único "Guardar"). RN04: costo/fechas
     * obligatorios. RN05/RN11: el costo enviado para EJECUCION se ignora (lo fija el Sistema).
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ValidacionNegocioException si falta costo/fecha de inicio/fecha de fin (RN04).
     */
    List<EtapaDto> actualizarEtapas(Long idProyecto, ActualizarEtapasRequestDto request);

    /**
     * Consulta la Ficha de información general (Anexo A.3, RN14: no editable salvo Co-ejecutor).
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP ni Coordinador SYMP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    FichaInformacionGeneralDto obtenerFichaInformacionGeneral(Long idProyecto);

    /**
     * Asigna la Unidad Ejecutora Co-ejecutora (RN16), única acción del Coordinador SYMP.
     *
     * @throws AccesoDenegadoException si el actor no es Coordinador SYMP.
     * @throws RecursoNoEncontradoException si el proyecto o la Unidad Ejecutora indicada no existen.
     */
    FichaInformacionGeneralDto seleccionarCoEjecutor(Long idProyecto, SeleccionCoEjecutorRequestDto request);

    /**
     * Consulta la Ficha de proyectos de emergencia (Anexo A.4).
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe o no está categorizado como de emergencia.
     */
    FichaEmergenciaDto obtenerFichaEmergencia(Long idProyecto);

    /**
     * Registra la Ficha de proyectos de emergencia; si todos los campos obligatorios están
     * completos, remite el proyecto a Viabilidad (transición a {@code EstadoProyecto.EN_VIABILIDAD}).
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe o no está categorizado como de emergencia.
     * @throws ValidacionNegocioException si faltan campos obligatorios ("Existen campos sin diligenciar").
     */
    FichaEmergenciaDto registrarFichaEmergencia(Long idProyecto, FichaEmergenciaRequestDto request);
}
