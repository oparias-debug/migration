package sv.gob.mh.siip.model.preinversion.service;

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
     * @param filtro criterios de filtrado de la consulta.
     * @param pagina número de página a recuperar.
     * @param tamanio cantidad de elementos por página.
     * @return DTO encapsulador {@link ProyectosCapturaResponseDto} con los resultados de la consulta.
     */
    ProyectosCapturaResponseDto listarProyectosCaptura(
            ProyectoCapturaFiltro filtro,
            Integer pagina,
            Integer tamanio
    );
}