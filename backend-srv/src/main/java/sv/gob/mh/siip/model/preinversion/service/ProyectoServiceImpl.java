package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.CambioUnidadEjecutoraRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoListResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaObservacionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Registro de proyectos y trámite de su CUP (CU-PRE-01). Valida el rol del actor y delega en
 * {@link ProyectoDatosRegistro} (datos y catálogos), {@link ProyectoConsultas} (búsquedas y
 * listado), {@link ProyectoTramiteCup} (solicitud, observaciones, devolución y emisión del CUP),
 * {@link ProyectoFlujoProceso} (proceso Flowable) y {@link ProyectoEnsamblador} (DTO de detalle).
 */
@Service
@Transactional
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final ActorContexto actorContexto;
    private final ProyectoDatosRegistro datosRegistro;
    private final ProyectoConsultas consultas;
    private final ProyectoTramiteCup tramiteCup;
    private final ProyectoFlujoProceso flujoProceso;
    private final ProyectoEnsamblador ensamblador;

    public ProyectoServiceImpl(ProyectoRepository proyectoRepository,
            ActorContexto actorContexto,
            ProyectoDatosRegistro datosRegistro,
            ProyectoConsultas consultas,
            ProyectoTramiteCup tramiteCup,
            ProyectoFlujoProceso flujoProceso,
            ProyectoEnsamblador ensamblador) {
        this.proyectoRepository = proyectoRepository;
        this.actorContexto = actorContexto;
        this.datosRegistro = datosRegistro;
        this.consultas = consultas;
        this.tramiteCup = tramiteCup;
        this.flujoProceso = flujoProceso;
        this.ensamblador = ensamblador;
    }

    @Override
    public ProyectoDto registrar(ProyectoRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto entidad = proyectoRepository.save(datosRegistro.nuevo(actor, request));
        flujoProceso.iniciar(entidad.getId());
        return ensamblador.toDto(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoListResponseDto listar(Integer pagina, Integer tamanio, EstadoProyectoDto estadoFiltro) {
        Usuario actor = actorContexto.exigir();
        return consultas.listar(actor, pagina, tamanio, estadoFiltro);
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        return ensamblador.toDto(consultas.buscarVisible(actor, idProyecto));
    }

    @Override
    public ProyectoDto actualizar(Long idProyecto, ProyectoRequestDto request) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto entidad = consultas.buscarPorId(idProyecto);
        datosRegistro.actualizar(entidad, request);
        return ensamblador.toDto(proyectoRepository.save(entidad));
    }

    @Override
    public ProyectoDto solicitarCup(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto entidad = consultas.buscarPorId(idProyecto);
        return ensamblador.toDto(tramiteCup.solicitar(entidad));
    }

    @Override
    public ProyectoDto responderObservacionCup(Long idProyecto, RespuestaObservacionRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto entidad = consultas.buscarPorId(idProyecto);
        return ensamblador.toDto(tramiteCup.responderObservacion(entidad, actor, request));
    }

    @Override
    public ProyectoDto devolverSolicitudCup(Long idProyecto, DevolucionSolicitudRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE);
        Proyecto entidad = consultas.buscarPorId(idProyecto);
        return ensamblador.toDto(tramiteCup.devolver(entidad, actor, request));
    }

    @Override
    public ProyectoDto emitirCup(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_PRE);
        Proyecto entidad = consultas.buscarPorId(idProyecto);
        return ensamblador.toDto(tramiteCup.emitir(entidad, actor));
    }

    @Override
    public ProyectoDto cambiarUnidadEjecutora(Long idProyecto, CambioUnidadEjecutoraRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR);
        Proyecto entidad = consultas.buscarPorId(idProyecto);
        datosRegistro.asignarUnidadEjecutora(entidad, request.getIdUnidadEjecutora());
        return ensamblador.toDto(proyectoRepository.save(entidad));
    }

    @Override
    public void eliminar(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        tramiteCup.exigirSinSolicitudCup(idProyecto);

        Proyecto entidad = consultas.buscarPorId(idProyecto);
        entidad.setActivo(false);
        proyectoRepository.save(entidad);
        flujoProceso.cancelar(idProyecto, "Proyecto eliminado antes de la primera solicitud de CUP (RN 4).");
    }
}
