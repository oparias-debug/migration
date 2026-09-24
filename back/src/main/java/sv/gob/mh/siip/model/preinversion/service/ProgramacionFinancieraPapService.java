package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.HabilitarModificacionesFueraPlazoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;

/** Programación Financiera Cuatrimestral del PAP (CU-PRE-30). */
public interface ProgramacionFinancieraPapService {

    ProgramacionFinancieraPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, String busqueda, Integer pagina,
            Integer tamanio);

    EstudioProgramacionPAPDto agregarEstudio(AgregarEstudioRequestDto request);

    EstudioProgramacionPAPDto obtenerProgramacionEstudio(String cup, Integer anio);

    EstudioProgramacionPAPDto guardarProgramacionEstudio(String cup, Integer anio,
            GuardarProgramacionEstudioRequestDto request);

    void desactivarEstudio(String cup, Integer anio);

    void eliminarEtapaProgramacion(String cup, NombreEtapaDto etapa, Integer anio);

    void eliminarFuenteFinanciamiento(String cup, NombreEtapaDto etapa, Long idFuente, Integer anio);

    void habilitarModificacionesFueraPlazo(HabilitarModificacionesFueraPlazoRequestDto request);

    Resource generarReporte(Long idUnidadEjecutora, Integer anio, String formato);
}
