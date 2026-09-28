package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaSpecs;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Implementación de la interfaz de servicios {@link ProyectoCapturaService}.
 *
 * @author Luis Medrano
 * @see ProyectoCapturaService
 * @see ProyectoCapturaRepository
 * @see ProyectoCapturaEnsamblador
 */
@Service
@Transactional
public class ProyectoCapturaServiceImpl implements ProyectoCapturaService {

    /** Tamaño de página usado cuando la petición no indica uno válido. */
    private static final int TAMANIO_PAGINA_POR_DEFECTO = 20;

    /** Repositorio de acceso a datos para proyectos en captura. */
    private final ProyectoCapturaRepository proyectoCapturaRepository;

    /** Ensambla la respuesta paginada, incluida la etapa actual de cada proyecto (CU-PRE-03.5). */
    private final ProyectoCapturaEnsamblador ensamblador;

    /** Resuelve el actor autenticado, para aplicar RN01/RN02 (alcance de visibilidad por rol). */
    private final ActorContexto actorContexto;

    /**
     * Inyecta las dependencias del repositorio de captura, del ensamblador de la respuesta y del
     * contexto del actor autenticado.
     *
     * @param proyectoCapturaRepository instancia de {@link ProyectoCapturaRepository}.
     * @param ensamblador instancia de {@link ProyectoCapturaEnsamblador}.
     * @param actorContexto instancia de {@link ActorContexto}.
     */
    public ProyectoCapturaServiceImpl(ProyectoCapturaRepository proyectoCapturaRepository,
            ProyectoCapturaEnsamblador ensamblador, ActorContexto actorContexto) {
        this.proyectoCapturaRepository = proyectoCapturaRepository;
        this.ensamblador = ensamblador;
        this.actorContexto = actorContexto;
    }

    /**
     * Ejecuta la consulta de proyectos delegando las especificaciones a {@link Specs} y gestionando
     * la paginación ({@code PageRequest}).
     * RN01/RN02: el Técnico URP solo ve los proyectos de su propia Unidad Ejecutora; el resto de
     * los roles (Viabilizador, Técnico PRE, Coordinador PRE) ve todos
     * los proyectos sin restricción de Unidad Ejecutora.
     *
     * @see ProyectoCapturaService#listarProyectosCaptura(ProyectoCapturaFiltro, Integer, Integer)
     */
    @Override
    @Transactional(readOnly = true)
    public ProyectosCapturaResponseDto listarProyectosCaptura(
            ProyectoCapturaFiltro filtro,
            Integer pagina,
            Integer tamanio) {

        Usuario actor = actorContexto.exigir();
        IniciativaInversionDto iniciativaInversion = filtro.iniciativaInversion();
        EstadoProyectoDto estado = filtro.estado();

        Specification<Proyecto> spec = Specification.allOf(
                ProyectoCapturaSpecs.fetchUnidadEjecutora(),
                ProyectoCapturaSpecs.esValidoParaCaptura(),
                ProyectoCapturaSpecs.byBusquedaGeneral(filtro.busqueda()),
                ProyectoCapturaSpecs.byCup(filtro.cup()),
                ProyectoCapturaSpecs.byNombre(filtro.nombreProyecto()),
                ProyectoCapturaSpecs.byUnidadEjecutora(filtro.idUnidadEjecutoraFiltro()),
                ProyectoCapturaSpecs.byIniciativa(iniciativaInversion != null ? iniciativaInversion.getValue() : null),
                ProyectoCapturaSpecs.byEstado(estado != null ? estado.getValue() : null),
                ProyectoCapturaSpecs.byUnidadEjecutora(actor.getRol() == RolUsuario.TECNICO_URP
                        ? actor.getUnidadEjecutora().getId() : null)
        );

        Page<Proyecto> paginaEntidades = proyectoCapturaRepository.findAll(spec, PageRequest.of(
                (pagina != null && pagina >= 0) ? pagina : 0,
                (tamanio != null && tamanio > 0) ? tamanio : TAMANIO_PAGINA_POR_DEFECTO
        ));

        return ensamblador.construirRespuesta(paginaEntidades);
    }
}
