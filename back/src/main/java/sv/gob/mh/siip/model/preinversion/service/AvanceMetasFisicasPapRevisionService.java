package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": ciclo de revisión DGICP del avance
 * del PAP (RevisionAvancePap), compartido con CU-PRE-32 (que no tiene endpoints propios de revisión).
 */
public interface AvanceMetasFisicasPapRevisionService {

    RevisionAvancePAPDto registrarObservacionesAvanceDgicp(RegistrarObservacionesAvanceDgicpRequestDto request);

    RevisionAvancePAPDto enviarObservacionesAvanceDgicp(EnviarObservacionesAvanceDgicpRequestDto request);

    RevisionAvancePAPDto registrarRespuestaInstitucionAvance(RegistrarRespuestaInstitucionAvanceRequestDto request);

    RevisionAvancePAPDto enviarRespuestaInstitucionAvance(EnviarObservacionesAvanceDgicpRequestDto request);

    RevisionAvancePAPDto finalizarRevisionAvance(FinalizarRevisionAvanceRequestDto request);
}
