package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": consulta y registro del avance de
 * metas físicas de un estudio (por CUP, año y período), con el control de roles y del período
 * abierto.
 */
final class AvanceMetasFisicasPapDetalle {

    private final ActorContexto actorContexto;
    private final AvancePapConsultas consultas;
    private final AvanceMetasFisicasPapDetalleAssembler detalleAssembler;
    private final AvanceMetasFisicasPapRegistro registro;

    AvanceMetasFisicasPapDetalle(ActorContexto actorContexto, ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            CalendarioEventoRepository calendarioEventoRepository, EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository, AvanceCuatriMetaFisicaRepository avanceRepository) {
        this.actorContexto = actorContexto;
        this.consultas = new AvancePapConsultas(proyectoRepository, etapaPreinversionRepository,
                calendarioEventoRepository);
        AvanceMetasFisicasPapCalculos calculos = new AvanceMetasFisicasPapCalculos(progRepository, avanceRepository);
        this.detalleAssembler = new AvanceMetasFisicasPapDetalleAssembler(consultas, etapaMetaRepository, calculos);
        this.registro = new AvanceMetasFisicasPapRegistro(consultas, calculos, etapaMetaRepository, progRepository,
                avanceRepository);
    }

    AvanceMetasEstudioDto obtenerAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodo) {
        AvancePapSoporte.exigirRolConsulta(actorContexto);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        return detalleAssembler.construirEstudioDto(proyecto, anio, Cuatrimestre.valueOf(periodo.name()));
    }

    AvanceMetasEstudioDto guardarAvanceMetasEstudio(String cup, Integer anio, CuatrimestreDto periodoDto,
            GuardarAvanceMetasEstudioRequestDto request) {
        AvancePapSoporte.exigirTecnicoUrp(actorContexto);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        Cuatrimestre periodo = Cuatrimestre.valueOf(periodoDto.name());
        consultas.verificarPeriodoAbierto(anio, periodo);
        registro.guardar(proyecto.getId(), request.getEtapas(), anio, periodo);
        return detalleAssembler.construirEstudioDto(proyecto, anio, periodo);
    }
}
