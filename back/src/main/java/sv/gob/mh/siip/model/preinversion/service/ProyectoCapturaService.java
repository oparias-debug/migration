package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;


/**
 * Contrato funcional del servicio para la gestión y filtrado de la captura de preinversión.
 *
 * @author Luis Medrano
 * @see ProyectoCapturaServiceImpl
 * @see ProyectosCapturaResponseDto
 */
public interface ProyectoCapturaService {

    /**
     * Consulta paginada y filtrada de los proyectos en fase de captura.
     *
     * @param busqueda término libre de búsqueda.
     * @param cup valor de filtro para el código de proyecto.
     * @param nombreProyecto valor de filtro para la denominación del proyecto.
     * @param iniciativaInversion tipo de iniciativa asociada.
     * @param estado estado del proyecto.
     * @param idUnidadEjecutoraFiltro id de unidad ejecutora solicitado en la petición.
     * @param pagina número de página a recuperar.
     * @param tamanio cantidad de elementos por página.
     * @return DTO encapsulador {@link ProyectosCapturaResponseDto} con los resultados de la consulta.
     */
    ProyectosCapturaResponseDto listarProyectosCaptura(
            String busqueda,
            String cup,
            String nombreProyecto,
            IniciativaInversionDto iniciativaInversion,
            EstadoProyectoDto estado,
            Long idUnidadEjecutoraFiltro,
            Integer pagina,
            Integer tamanio
    );
}