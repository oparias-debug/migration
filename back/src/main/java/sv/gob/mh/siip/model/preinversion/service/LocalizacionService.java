package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.LocalizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionRequestDto;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public interface LocalizacionService {

    /**
     * Obtiene la localización y estatus del terreno asociados a un proyecto.
     *
     * @param idProyecto Identificador único del proyecto.
     * @return DTO con la estructura de localización y sus filas.
     */
    LocalizacionDto obtenerLocalizacion(Long idProyecto);

    /**
     * Guarda o actualiza la localización y estatus del terreno de un proyecto.
     * Aplica estrategia de reemplazo completo de filas.
     *
     * @param idProyecto Identificador único del proyecto.
     * @param localizacionRequestDto DTO con la información a persistir.
     * @return DTO con la localización guardada.
     */
    LocalizacionDto guardarLocalizacion(Long idProyecto, LocalizacionRequestDto localizacionRequestDto);

    /**
     * Autocompleta las filas de localización a partir de los distritos registrados
     * en la Población Objetivo del proyecto (CU-PRE-07 / RN03).
     *
     * @param idProyecto Identificador único del proyecto.
     * @return DTO con las filas de localización autocompletadas.
     */
    LocalizacionDto autocompletarLocalizacionDesdeAreaInfluencia(Long idProyecto);
}
