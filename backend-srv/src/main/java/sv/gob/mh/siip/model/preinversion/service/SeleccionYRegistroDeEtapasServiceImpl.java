package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Punto de entrada de CU-PRE-03.5: exige el rol de cada operación y delega en la pantalla
 * correspondiente (ruta, registro de etapas, ficha general, ficha de emergencia), dentro de una
 * única transacción.
 */
@Service
@Transactional
public class SeleccionYRegistroDeEtapasServiceImpl implements SeleccionYRegistroDeEtapasService {

    private final ActorContexto actorContexto;
    private final SeleccionEtapasRuta ruta;
    private final SeleccionEtapasRegistro registro;
    private final SeleccionEtapasFichaGeneral fichaGeneral;
    private final SeleccionEtapasFichaEmergencia fichaEmergencia;

    public SeleccionYRegistroDeEtapasServiceImpl(ActorContexto actorContexto, SeleccionEtapasRuta ruta,
            SeleccionEtapasRegistro registro, SeleccionEtapasFichaGeneral fichaGeneral,
            SeleccionEtapasFichaEmergencia fichaEmergencia) {
        this.actorContexto = actorContexto;
        this.ruta = ruta;
        this.registro = registro;
        this.fichaGeneral = fichaGeneral;
        this.fichaEmergencia = fichaEmergencia;
    }

    @Override
    @Transactional(readOnly = true)
    public RutaPreinversionDto obtenerRutaPreinversion(Long idProyecto) {
        actorContexto.exigir();
        return ruta.obtener(idProyecto);
    }

    @Override
    @Transactional(readOnly = true)
    public RutaPreinversionSugeridaDto generarRutaPreinversion(Long idProyecto, CriteriosCalificacionDto criterios) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return ruta.generar(idProyecto, criterios);
    }

    @Override
    public RutaPreinversionDto aceptarRutaPreinversion(Long idProyecto, CriteriosCalificacionDto criterios) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return ruta.aceptar(idProyecto, criterios);
    }

    @Override
    public RutaPreinversionDto modificarRutaPreinversion(Long idProyecto, ModificarRutaPreinversionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return ruta.modificar(idProyecto, request);
    }

    @Override
    public List<EtapaDto> listarEtapas(Long idProyecto) {
        actorContexto.exigir();
        return registro.listar(idProyecto);
    }

    @Override
    public List<EtapaDto> actualizarEtapas(Long idProyecto, ActualizarEtapasRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return registro.actualizar(idProyecto, request);
    }

    @Override
    @Transactional(readOnly = true)
    public FichaInformacionGeneralDto obtenerFichaInformacionGeneral(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.COORDINADOR_SYMP);
        return fichaGeneral.obtener(idProyecto);
    }

    @Override
    public FichaInformacionGeneralDto seleccionarCoEjecutor(Long idProyecto, SeleccionCoEjecutorRequestDto request) {
        actorContexto.exigirRol(RolUsuario.COORDINADOR_SYMP);
        return fichaGeneral.seleccionarCoEjecutor(idProyecto, request);
    }

    @Override
    @Transactional(readOnly = true)
    public FichaEmergenciaDto obtenerFichaEmergencia(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return fichaEmergencia.obtener(idProyecto);
    }

    @Override
    public FichaEmergenciaDto registrarFichaEmergencia(Long idProyecto, FichaEmergenciaRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return fichaEmergencia.registrar(idProyecto, request);
    }
}
