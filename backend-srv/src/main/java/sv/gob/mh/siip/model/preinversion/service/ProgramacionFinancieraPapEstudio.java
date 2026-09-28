package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": agregar, consultar y guardar
 * la programación de un estudio (Anexo A.2), con el control de roles y del período de ingreso de
 * información.
 */
final class ProgramacionFinancieraPapEstudio {

    private final ActorContexto actorContexto;
    private final ProgramacionPapConsultas consultas;
    private final ProgramacionFinancieraPapPlazo plazo;
    private final ProgramacionFinancieraPapEstudioAssembler estudioAssembler;
    private final ProgramacionFinancieraPapRegistro registro;

    ProgramacionFinancieraPapEstudio(ActorContexto actorContexto, ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            HabilitacionModificacionPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository) {
        this.actorContexto = actorContexto;
        this.consultas = new ProgramacionPapConsultas(proyectoRepository, etapaPreinversionRepository);
        this.plazo = new ProgramacionFinancieraPapPlazo(habilitacionRepository, calendarioEventoRepository);
        this.estudioAssembler = new ProgramacionFinancieraPapEstudioAssembler(consultas, fuenteRepository,
                progRepository);
        this.registro = new ProgramacionFinancieraPapRegistro(consultas, etapaPreinversionRepository,
                fuenteRepository, progRepository);
    }

    EstudioProgramacionPAPDto agregarEstudio(AgregarEstudioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        plazo.verificarPeriodoAbierto(request.getIdUnidadEjecutora(), request.getAnio());
        Proyecto proyecto = consultas.buscarProyecto(request.getCup());
        return estudioAssembler.construirEstudioDto(proyecto, request.getAnio());
    }

    EstudioProgramacionPAPDto obtenerProgramacionEstudio(String cup, Integer anio) {
        ProgramacionPapSoporte.exigirRolConsultaFinanciera(actorContexto);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        return estudioAssembler.construirEstudioDto(proyecto, anio);
    }

    EstudioProgramacionPAPDto guardarProgramacionEstudio(String cup, Integer anio,
            GuardarProgramacionEstudioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        plazo.verificarPeriodoAbierto(proyecto.getUnidadEjecutora().getId(), anio);
        registro.guardar(proyecto.getId(), anio, request.getEtapas());
        return estudioAssembler.construirEstudioDto(proyecto, anio);
    }
}
