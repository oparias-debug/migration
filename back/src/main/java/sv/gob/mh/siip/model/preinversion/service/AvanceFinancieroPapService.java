package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;

/** CU-PRE-32 "Avance Financiero Cuatrimestral del PAP". */
public interface AvanceFinancieroPapService {

    AvanceFinancieroPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio);

    AvanceEstudioDto obtenerAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodo);

    AvanceEstudioDto guardarAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodo,
            GuardarAvanceEstudioRequestDto request);

    Resource generarReporte(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo, String formato);
}
