package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;

/**
 * Programación Financiera Cuatrimestral del PAP (CU-PRE-30): listado, reporte y registro de estudios. Las
 * bajas y la habilitación fuera de plazo están en {@link ProgramacionFinancieraPapAjusteService}.
 */
public interface ProgramacionFinancieraPapService {

    ProgramacionFinancieraPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, String busqueda, Integer pagina,
            Integer tamanio);

    EstudioProgramacionPAPDto agregarEstudio(AgregarEstudioRequestDto request);

    EstudioProgramacionPAPDto obtenerProgramacionEstudio(String cup, Integer anio);

    EstudioProgramacionPAPDto guardarProgramacionEstudio(String cup, Integer anio,
            GuardarProgramacionEstudioRequestDto request);

    Resource generarReporte(Long idUnidadEjecutora, Integer anio, String formato);
}
