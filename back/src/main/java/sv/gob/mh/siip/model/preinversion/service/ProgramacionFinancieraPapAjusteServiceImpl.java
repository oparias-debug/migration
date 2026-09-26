package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.dto.HabilitarModificacionesFueraPlazoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": ajustes sobre la programación ya
 * registrada. Las bajas (desactivar un estudio y eliminar una etapa o fuente, RN-D) viven en
 * {@link ProgramacionFinancieraPapEliminacion}; la habilitación de modificaciones fuera de plazo (RN-A.b)
 * delega en {@link ProgramacionFinancieraPapPlazo}. Esta clase mantiene el control transaccional; el
 * listado, el reporte y el registro de estudios están en {@link ProgramacionFinancieraPapServiceImpl}.
 */
@Service
@Transactional
public class ProgramacionFinancieraPapAjusteServiceImpl implements ProgramacionFinancieraPapAjusteService {

    private final ActorContexto actorContexto;
    private final ProgramacionFinancieraPapPlazo plazo;
    private final ProgramacionFinancieraPapEliminacion eliminacion;

    public ProgramacionFinancieraPapAjusteServiceImpl(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            HabilitacionModificacionPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository,
            EtapaMetaFisicaPapRepository etapaMetaFisicaPapRepository, ActorContexto actorContexto) {
        this.actorContexto = actorContexto;
        this.plazo = new ProgramacionFinancieraPapPlazo(habilitacionRepository, calendarioEventoRepository);
        this.eliminacion = new ProgramacionFinancieraPapEliminacion(actorContexto,
                new ProgramacionPapConsultas(proyectoRepository, etapaPreinversionRepository), plazo,
                fuenteRepository, progRepository, etapaMetaFisicaPapRepository);
    }

    @Override
    public void desactivarEstudio(String cup, Integer anio) {
        eliminacion.desactivarEstudio(cup, anio);
    }

    @Override
    public void eliminarEtapaProgramacion(String cup, NombreEtapaDto etapaDto, Integer anio) {
        eliminacion.eliminarEtapaProgramacion(cup, etapaDto, anio);
    }

    @Override
    public void eliminarFuenteFinanciamiento(String cup, NombreEtapaDto etapaDto, Long idFuente, Integer anio) {
        eliminacion.eliminarFuenteFinanciamiento(cup, etapaDto, idFuente, anio);
    }

    @Override
    public void habilitarModificacionesFueraPlazo(HabilitarModificacionesFueraPlazoRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        plazo.habilitarModificacionesFueraPlazo(request.getIdUnidadEjecutora(), request.getAnio());
    }
}
