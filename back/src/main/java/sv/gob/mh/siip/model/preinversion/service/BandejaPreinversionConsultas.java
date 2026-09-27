package sv.gob.mh.siip.model.preinversion.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.administracion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.ConteoTecnicoPreDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesActivasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesArchivadasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository.ConteoTecnico;

/**
 * Consultas de la Bandeja de Preinversión (CU-PRE-02): tablas de solicitudes activas y archivadas,
 * conteo de casos por Técnico PRE y catálogo de Técnicos PRE asignables.
 */
@Component
public class BandejaPreinversionConsultas {

    private final SolicitudPreinversionRepository solicitudes;
    private final UsuarioRepository usuarios;
    private final ProyectoMapper mapper;
    private final CatalogosAdministracionMapper catalogosMapper;
    private final BandejaSolicitudEnsamblador ensamblador;

    public BandejaPreinversionConsultas(SolicitudPreinversionRepository solicitudes, UsuarioRepository usuarios,
            ProyectoMapper mapper, CatalogosAdministracionMapper catalogosMapper,
            BandejaSolicitudEnsamblador ensamblador) {
        this.solicitudes = solicitudes;
        this.usuarios = usuarios;
        this.mapper = mapper;
        this.catalogosMapper = catalogosMapper;
        this.ensamblador = ensamblador;
    }

    /** Solicitudes activas visibles para el actor: el Técnico PRE solo ve las que tiene asignadas. */
    public SolicitudesActivasResponseDto activas(Usuario actor, TipoSolicitudDto tipo, Integer pagina,
            Integer tamanio) {
        Specification<SolicitudPreinversion> filtro = BandejaPreinversionFiltros.activas()
                .and(BandejaPreinversionFiltros.deTipo(tipo));
        if (actor.getRol() == RolUsuario.TECNICO_PRE) {
            filtro = filtro.and(BandejaPreinversionFiltros.asignadasA(actor.getId()));
        }
        Page<SolicitudPreinversion> resultado = solicitudes.findAll(filtro,
                BandejaPreinversionFiltros.pagina(pagina, tamanio));
        return new SolicitudesActivasResponseDto().contenido(resultado.map(ensamblador::activa).getContent())
                .paginacion(ensamblador.metadata(resultado)).conteoPorTecnico(conteoPorTecnico());
    }

    /** Reporte de solicitudes archivadas. */
    public SolicitudesArchivadasResponseDto archivadas(TipoSolicitudDto tipo, Integer pagina, Integer tamanio) {
        Page<SolicitudPreinversion> resultado = solicitudes.findAll(
                BandejaPreinversionFiltros.archivadas().and(BandejaPreinversionFiltros.deTipo(tipo)),
                BandejaPreinversionFiltros.pagina(pagina, tamanio));
        return new SolicitudesArchivadasResponseDto().contenido(resultado.map(ensamblador::archivada).getContent())
                .paginacion(ensamblador.metadata(resultado));
    }

    /** Técnicos PRE activos, candidatos a recibir una asignación. */
    public List<UsuarioResumenDto> tecnicos() {
        return usuarios.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE).stream()
                .map(catalogosMapper::toResumen)
                .toList();
    }

    /** El contrato exige conteos globales, independientes de filtro y paginación. */
    private List<ConteoTecnicoPreDto> conteoPorTecnico() {
        Map<Long, ConteoTecnico> porId = solicitudes.conteosActivos(
                BandejaPreinversionFiltros.ESTADOS_PROYECTO_ACTIVOS,
                BandejaPreinversionFiltros.ESTADOS_SOLICITUD_EXCLUIDOS).stream()
                .collect(Collectors.toMap(ConteoTecnico::getTecnicoId, Function.identity()));
        return usuarios.findAllById(porId.keySet()).stream()
                .sorted(Comparator.comparing(Usuario::getNombreCompleto))
                .map((Usuario tecnico) -> new ConteoTecnicoPreDto().tecnico(mapper.toResumen(tecnico))
                        .cantidadCup(Math.toIntExact(porId.get(tecnico.getId()).getCantidadCup()))
                        .cantidadOpinionTecnica(
                                Math.toIntExact(porId.get(tecnico.getId()).getCantidadOpinionTecnica())))
                .toList();
    }
}
