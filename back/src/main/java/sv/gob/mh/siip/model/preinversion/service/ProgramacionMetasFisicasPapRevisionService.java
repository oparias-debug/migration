package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": ciclo de revisión
 * DGICP de la programación del PAP (RevisionProgramacionPap), compartido con CU-PRE-30 (que no tiene
 * endpoints propios de revisión).
 */
public interface ProgramacionMetasFisicasPapRevisionService {

    RevisionProgramacionPAPDto enviarProgramacionARevisionDgicp(EnviarProgramacionARevisionDgicpRequestDto request);

    RevisionProgramacionPAPDto registrarObservacionesDgicp(RegistrarObservacionesDgicpRequestDto request);

    RevisionProgramacionPAPDto enviarObservacionesDgicp(EnviarProgramacionARevisionDgicpRequestDto request);

    RevisionProgramacionPAPDto registrarRespuestaInstitucion(RegistrarRespuestaInstitucionRequestDto request);

    RevisionProgramacionPAPDto enviarRespuestaInstitucion(EnviarProgramacionARevisionDgicpRequestDto request);

    RevisionProgramacionPAPDto finalizarRevision(FinalizarRevisionRequestDto request);
}
