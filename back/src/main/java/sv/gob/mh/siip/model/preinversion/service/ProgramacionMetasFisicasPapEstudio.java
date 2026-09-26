package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionMetasPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": consultar y guardar
 * la programación física de un estudio (Anexo A.4), y habilitar modificaciones fuera de plazo
 * (SF-8/SF-9), con el control de roles y del período de ingreso de información.
 */
final class ProgramacionMetasFisicasPapEstudio {

    private final ActorContexto actorContexto;
    private final ProgramacionPapConsultas consultas;
    private final ProgramacionMetasFisicasPapPlazo plazo;
    private final ProgramacionMetasFisicasPapEstudioAssembler estudioAssembler;
    private final ProgramacionMetasFisicasPapRegistro registro;

    ProgramacionMetasFisicasPapEstudio(ActorContexto actorContexto, ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository, EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository,
            HabilitacionModificacionMetasPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository) {
        this.actorContexto = actorContexto;
        this.consultas = new ProgramacionPapConsultas(proyectoRepository, etapaPreinversionRepository);
        this.plazo = new ProgramacionMetasFisicasPapPlazo(habilitacionRepository, calendarioEventoRepository);
        this.estudioAssembler = new ProgramacionMetasFisicasPapEstudioAssembler(consultas, etapaMetaRepository,
                progRepository);
        this.registro = new ProgramacionMetasFisicasPapRegistro(consultas, etapaMetaRepository, progRepository);
    }

    EstudioProgramacionMetasDto obtenerProgramacionMetasEstudio(String cup, Integer anio) {
        ProgramacionPapSoporte.exigirRolConsultaMetas(actorContexto);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        return estudioAssembler.construirEstudioDto(proyecto, anio);
    }

    EstudioProgramacionMetasDto guardarProgramacionMetasEstudio(String cup, Integer anio,
            GuardarProgramacionMetasEstudioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        plazo.verificarPeriodoAbierto(proyecto.getUnidadEjecutora().getId(), anio);

        registro.guardar(proyecto.getId(), anio, request.getEtapas());

        return estudioAssembler.construirEstudioDto(proyecto, anio);
    }

    void habilitarModificacionesMetasFueraPlazo(EnviarProgramacionARevisionDgicpRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        plazo.habilitarModificacionesFueraPlazo(request.getIdUnidadEjecutora(), request.getAnio());
    }
}
