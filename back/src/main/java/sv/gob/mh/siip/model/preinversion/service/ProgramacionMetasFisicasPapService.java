package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;

/** CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". */
public interface ProgramacionMetasFisicasPapService {

    ProgramacionMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, Integer pagina,
            Integer tamanio);

    EstudioProgramacionMetasDto obtenerProgramacionMetasEstudio(String cup, Integer anio);

    EstudioProgramacionMetasDto guardarProgramacionMetasEstudio(String cup, Integer anio,
            GuardarProgramacionMetasEstudioRequestDto request);

    RevisionProgramacionPAPDto enviarProgramacionARevisionDgicp(EnviarProgramacionARevisionDgicpRequestDto request);

    RevisionProgramacionPAPDto registrarObservacionesDgicp(RegistrarObservacionesDgicpRequestDto request);

    RevisionProgramacionPAPDto enviarObservacionesDgicp(EnviarProgramacionARevisionDgicpRequestDto request);

    RevisionProgramacionPAPDto registrarRespuestaInstitucion(RegistrarRespuestaInstitucionRequestDto request);

    RevisionProgramacionPAPDto enviarRespuestaInstitucion(EnviarProgramacionARevisionDgicpRequestDto request);

    RevisionProgramacionPAPDto finalizarRevision(FinalizarRevisionRequestDto request);

    Resource generarReporteMetasFisicas(Long idUnidadEjecutora, Integer anio, String formato);

    void habilitarModificacionesMetasFueraPlazo(EnviarProgramacionARevisionDgicpRequestDto request);
}
