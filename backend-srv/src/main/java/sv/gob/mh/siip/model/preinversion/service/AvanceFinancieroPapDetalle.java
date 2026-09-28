package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP": consulta y registro del avance de un
 * estudio (por CUP, año y período), con el control de roles y del período abierto (RN-A.b).
 */
final class AvanceFinancieroPapDetalle {

    private final ActorContexto actorContexto;
    private final AvancePapConsultas consultas;
    private final AvanceFinancieroPapDetalleAssembler detalleAssembler;
    private final AvanceFinancieroPapRegistro registro;

    AvanceFinancieroPapDetalle(ActorContexto actorContexto, ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            CalendarioEventoRepository calendarioEventoRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository) {
        this.actorContexto = actorContexto;
        this.consultas = new AvancePapConsultas(proyectoRepository, etapaPreinversionRepository,
                calendarioEventoRepository);
        AvanceFinancieroPapCalculos calculos = new AvanceFinancieroPapCalculos(fuenteRepository, progRepository,
                avanceRepository);
        this.detalleAssembler = new AvanceFinancieroPapDetalleAssembler(consultas, calculos);
        this.registro = new AvanceFinancieroPapRegistro(consultas, calculos, fuenteRepository, progRepository,
                avanceRepository);
    }

    AvanceEstudioDto obtenerAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodo) {
        AvancePapSoporte.exigirRolConsulta(actorContexto);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        return detalleAssembler.construirEstudioDto(proyecto, anio, Cuatrimestre.valueOf(periodo.name()));
    }

    AvanceEstudioDto guardarAvanceEstudio(String cup, Integer anio, CuatrimestreDto periodoDto,
            GuardarAvanceEstudioRequestDto request) {
        AvancePapSoporte.exigirTecnicoUrp(actorContexto);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        Cuatrimestre periodo = Cuatrimestre.valueOf(periodoDto.name());
        consultas.verificarPeriodoAbierto(anio, periodo);
        registro.guardar(proyecto.getId(), request.getEtapas(), anio, periodo);
        return detalleAssembler.construirEstudioDto(proyecto, anio, periodo);
    }
}
