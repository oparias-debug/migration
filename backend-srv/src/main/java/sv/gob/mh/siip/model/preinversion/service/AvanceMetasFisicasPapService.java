package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP". El ciclo de revisión (RevisionAvancePap)
 * vive en {@link AvanceMetasFisicasPapRevisionService}.
 */
public interface AvanceMetasFisicasPapService {

    AvanceMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio);

    AvanceMetasEstudioDto obtenerAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodo);

    AvanceMetasEstudioDto guardarAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodo,
            GuardarAvanceMetasEstudioRequestDto request);

    Resource generarReporteAvanceMetas(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo, String formato);
}
