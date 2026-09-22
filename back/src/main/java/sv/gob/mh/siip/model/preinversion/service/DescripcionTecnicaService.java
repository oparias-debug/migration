package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaRequestDto;

/**
 * Interfaz de servicio de negocio que define las operaciones permitidas para la
 * administración de la Descripción Técnica del proyecto (CU-PRE-11).
 *
 * @author Luis Medrano
 * @version 1.0
 */
public interface DescripcionTecnicaService {

    /**
     * Consulta la descripción técnica asociada a un proyecto.
     * Si no se ha guardado previamente, inicializa implícitamente la respuesta
     * aplicando el autocompletado del proyecto y la última O.T. emitida (RN03)
     * y permitiendo la sincronización de filas de productos desde el Análisis de Mercado (RN04).
     *
     * @param idProyecto Identificador único del proyecto a consultar.
     * @return DTO {@link DescripcionTecnicaDto} con la información registrada o inicializada.
     * @throws java.util.NoSuchElementException Si el ID del proyecto no existe en el sistema.
     * @throws sv.gob.mh.siip.exception.NoAutenticadoException Si no hay un actor autenticado.
     * @throws sv.gob.mh.siip.exception.AccesoDenegadoException Si el actor no es Técnico URP ni Técnico PRE.
     */
    DescripcionTecnicaDto obtenerDescripcionTecnica(Long idProyecto);

    /**
     * Registra o actualiza la descripción técnica de un proyecto y sustituye por completo
     * las filas de componentes configuradas por el usuario (FA-01).
     *
     * @param idProyecto Identificador único del proyecto sobre el cual se guarda.
     * @param requestDto DTO {@link DescripcionTecnicaRequestDto} con los datos a guardar.
     * @return DTO {@link DescripcionTecnicaDto} actualizado tras la persistencia.
     * @throws java.util.NoSuchElementException Si el ID del proyecto no existe.
     * @throws sv.gob.mh.siip.exception.NoAutenticadoException Si no hay un actor autenticado.
     * @throws sv.gob.mh.siip.exception.AccesoDenegadoException Si el actor no es Técnico URP.
     */
    DescripcionTecnicaDto guardarDescripcionTecnica(Long idProyecto, DescripcionTecnicaRequestDto requestDto);
}