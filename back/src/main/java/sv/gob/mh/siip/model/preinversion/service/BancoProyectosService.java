package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.BancoProyectosResponseDto;

/** Consulta de solo lectura del Banco de Proyectos (CU-PRE-29). */
public interface BancoProyectosService {

    BancoProyectosResponseDto listar(Long idUnidadEjecutora, String busqueda, Integer pagina, Integer tamanio);
}
