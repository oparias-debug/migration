package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP". Incluye el ciclo de revisión
 * (RevisionAvancePap), compartido con CU-PRE-32 (que no tiene endpoints propios de revisión).
 */
public interface AvanceMetasFisicasPapService {

    AvanceMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio);

    AvanceMetasEstudioDto obtenerAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodo);

    AvanceMetasEstudioDto guardarAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodo,
            GuardarAvanceMetasEstudioRequestDto request);

    RevisionAvancePAPDto registrarObservacionesAvanceDgicp(RegistrarObservacionesAvanceDgicpRequestDto request);

    RevisionAvancePAPDto enviarObservacionesAvanceDgicp(EnviarObservacionesAvanceDgicpRequestDto request);

    RevisionAvancePAPDto registrarRespuestaInstitucionAvance(RegistrarRespuestaInstitucionAvanceRequestDto request);

    RevisionAvancePAPDto enviarRespuestaInstitucionAvance(EnviarObservacionesAvanceDgicpRequestDto request);

    RevisionAvancePAPDto finalizarRevisionAvance(FinalizarRevisionAvanceRequestDto request);

    Resource generarReporteAvanceMetas(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo, String formato);
}
