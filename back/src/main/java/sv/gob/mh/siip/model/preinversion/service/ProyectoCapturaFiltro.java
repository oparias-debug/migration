package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;

/**
 * Agrupa los criterios de filtrado de {@link ProyectoCapturaService#listarProyectosCaptura} para
 * mantener el método dentro del límite de parámetros permitido (máx. 7).
 *
 * @param busqueda término libre de búsqueda.
 * @param cup valor de filtro para el código de proyecto.
 * @param nombreProyecto valor de filtro para la denominación del proyecto.
 * @param iniciativaInversion tipo de iniciativa asociada.
 * @param estado estado del proyecto.
 * @param idUnidadEjecutoraFiltro id de unidad ejecutora solicitado en la petición.
 */
public record ProyectoCapturaFiltro(
        String busqueda,
        String cup,
        String nombreProyecto,
        IniciativaInversionDto iniciativaInversion,
        EstadoProyectoDto estado,
        Long idUnidadEjecutoraFiltro) {
}
