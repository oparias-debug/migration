package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Priorizacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.BancoProyectosResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoBancoItemDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class BancoProyectosServiceImpl implements BancoProyectosService {

    private static final int TAMANIO_PAGINA_POR_DEFECTO = 20;

    private final ProyectoCapturaRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaRepository;
    private final PriorizacionRepository priorizacionRepository;
    private final ActorContexto actorContexto;

    public BancoProyectosServiceImpl(ProyectoCapturaRepository proyectoRepository,
            EtapaPreinversionRepository etapaRepository, PriorizacionRepository priorizacionRepository,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.etapaRepository = etapaRepository;
        this.priorizacionRepository = priorizacionRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public BancoProyectosResponseDto listar(Long idUnidadEjecutora, String busqueda, Integer pagina, Integer tamanio) {
        // RN01 / x-roles: Técnico URP, Técnico PRE, Coordinador PRE, Viabilizador y "Usuarios
        // Internos". Estos últimos no son un rol propio sino cualquier usuario del Ministerio de
        // Hacienda, sea cual sea su rol (RQ-C-03, ver UC-PRE-17), con acceso de solo consulta; como
        // el único rol externo al MH es TECNICO_URP (ya incluido), quedan habilitados todos los roles.
        Usuario actor = actorContexto.exigirRol(RolUsuario.values());

        Long unidadEjecutoraFiltro = actor.getRol() == RolUsuario.TECNICO_URP
                ? actor.getUnidadEjecutora().getId()
                : idUnidadEjecutora;

        Pageable pageable = PageRequest.of(
                (pagina != null && pagina >= 0) ? pagina : 0,
                (tamanio != null && tamanio > 0) ? tamanio : TAMANIO_PAGINA_POR_DEFECTO);
        Page<Proyecto> resultado = proyectoRepository.findAll(
                BancoProyectosSpecs.listado(unidadEjecutoraFiltro, busqueda), pageable);
        Map<Long, TipoEtapaPreinversion> etapas = etapasPorProyecto(resultado.getContent());
        Map<Long, BigDecimal> prioridades = prioridadesPorProyecto(resultado.getContent());

        List<ProyectoBancoItemDto> contenido = resultado.getContent().stream()
                .map(proyecto -> toItem(proyecto, etapas.get(proyecto.getId()), prioridades.get(proyecto.getId())))
                .toList();

        PaginacionMetadataDto paginacion = new PaginacionMetadataDto()
                .pagina(resultado.getNumber())
                .tamanio(resultado.getSize())
                .totalElementos(resultado.getTotalElements())
                .totalPaginas(resultado.getTotalPages());
        return new BancoProyectosResponseDto(contenido, paginacion);
    }

    /** Etapa más avanzada de la Ruta de Preinversión de cada proyecto (orden del enum, no alfabético). */
    private Map<Long, TipoEtapaPreinversion> etapasPorProyecto(List<Proyecto> proyectos) {
        if (proyectos.isEmpty()) {
            return Map.of();
        }
        return etapaRepository.findByProyectoIdIn(proyectos.stream().map(Proyecto::getId).toList()).stream()
                .collect(Collectors.toMap(
                        etapa -> etapa.getProyecto().getId(),
                        EtapaPreinversion::getTipoEtapa,
                        (primera, segunda) -> segunda.compareTo(primera) > 0 ? segunda : primera));
    }

    /** Columna "Prioridad" (desde CU-PRE-25): puntaje de la priorización más reciente de cada proyecto. */
    private Map<Long, BigDecimal> prioridadesPorProyecto(List<Proyecto> proyectos) {
        if (proyectos.isEmpty()) {
            return Map.of();
        }
        return priorizacionRepository
                .findByProyectoIdInOrderByAnioDescCuatrimestreDescFechaPriorizacionDesc(
                        proyectos.stream().map(Proyecto::getId).toList())
                .stream()
                .collect(Collectors.toMap(
                        priorizacion -> priorizacion.getProyecto().getId(),
                        Priorizacion::getPuntaje,
                        (masReciente, anterior) -> masReciente));
    }

    private static ProyectoBancoItemDto toItem(Proyecto proyecto, TipoEtapaPreinversion etapa, BigDecimal prioridad) {
        return new ProyectoBancoItemDto(
                proyecto.getId(),
                proyecto.getCup(),
                proyecto.getNombre(),
                etapa != null ? etapa.getEtiquetaUi() : null,
                proyecto.getEstado().getEtiquetaUi())
                .inversionEstimada(proyecto.getMontoEstimadoInversion())
                .prioridad(prioridad != null ? prioridad.doubleValue() : null);
    }
}
