package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". El ciclo de revisión
 * DGICP se expone en {@link ProgramacionMetasFisicasPapRevisionService}.
 */
public interface ProgramacionMetasFisicasPapService {

    ProgramacionMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, Integer pagina,
            Integer tamanio);

    EstudioProgramacionMetasDto obtenerProgramacionMetasEstudio(String cup, Integer anio);

    EstudioProgramacionMetasDto guardarProgramacionMetasEstudio(String cup, Integer anio,
            GuardarProgramacionMetasEstudioRequestDto request);

    Resource generarReporteMetasFisicas(Long idUnidadEjecutora, Integer anio, String formato);

    void habilitarModificacionesMetasFueraPlazo(EnviarProgramacionARevisionDgicpRequestDto request);
}
