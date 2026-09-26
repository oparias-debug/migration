package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionMetasPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionProgramacionPapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". Análogo de
 * {@link ProgramacionFinancieraPapServiceImpl} (CU-PRE-30): un {@code idEtapaMetaFisica}
 * ({@link sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap}) persiste a través de los años;
 * el porcentaje programado por cuatrimestre de cada año vive en
 * {@link sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica}. A diferencia de
 * CU-PRE-30, solo hay una meta física por etapa (no varias "fuentes"), y los valores son porcentajes
 * (0-100), no montos monetarios (RN-B.a). El ciclo de revisión/aprobación
 * ({@link sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap}) es compartido con
 * CU-PRE-30 (decisión funcional v1.2 de CU-PRE-31.openapi.yaml): se expone en
 * {@link ProgramacionMetasFisicasPapRevisionService}, CU-PRE-30 no tiene endpoints propios para él.
 *
 * <p>La lógica de este servicio vive en {@link ProgramacionMetasFisicasPapListado} (listado y
 * reporte) y {@link ProgramacionMetasFisicasPapEstudio} (consulta y registro de un estudio, y
 * habilitación fuera de plazo); esta clase mantiene el control transaccional.
 */
@Service
@Transactional
public class ProgramacionMetasFisicasPapServiceImpl implements ProgramacionMetasFisicasPapService {

    private final ProgramacionMetasFisicasPapListado listado;
    private final ProgramacionMetasFisicasPapEstudio estudio;

    public ProgramacionMetasFisicasPapServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository,
            HabilitacionModificacionMetasPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository,
            RevisionProgramacionPapRepository revisionRepository, ActorContexto actorContexto) {
        this.listado = new ProgramacionMetasFisicasPapListado(actorContexto, etapaMetaRepository, progRepository,
                revisionRepository);
        this.estudio = new ProgramacionMetasFisicasPapEstudio(actorContexto, proyectoRepository,
                etapaPreinversionRepository, etapaMetaRepository, progRepository, habilitacionRepository,
                calendarioEventoRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramacionMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, Integer pagina,
            Integer tamanio) {
        return listado.listar(idUnidadEjecutora, anio, pagina, tamanio);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudioProgramacionMetasDto obtenerProgramacionMetasEstudio(String cup, Integer anio) {
        return estudio.obtenerProgramacionMetasEstudio(cup, anio);
    }

    @Override
    public EstudioProgramacionMetasDto guardarProgramacionMetasEstudio(String cup, Integer anio,
            GuardarProgramacionMetasEstudioRequestDto request) {
        return estudio.guardarProgramacionMetasEstudio(cup, anio, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource generarReporteMetasFisicas(Long idUnidadEjecutora, Integer anio, String formato) {
        return listado.generarReporteMetasFisicas(idUnidadEjecutora, anio, formato);
    }

    @Override
    public void habilitarModificacionesMetasFueraPlazo(EnviarProgramacionARevisionDgicpRequestDto request) {
        estudio.habilitarModificacionesMetasFueraPlazo(request);
    }
}
