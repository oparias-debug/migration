package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.AreaInfluencia;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.AreaInfluenciaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class AreaInfluenciaServiceImpl implements AreaInfluenciaService {

    private final ProyectoRepository proyectoRepository;
    private final AreaInfluenciaRepository areaInfluenciaRepository;
    private final AnalisisPoblacionRepository analisisPoblacionRepository;
    private final MunicipioRepository municipioRepository;
    private final ActorContexto actorContexto;

    public AreaInfluenciaServiceImpl(ProyectoRepository proyectoRepository,
            AreaInfluenciaRepository areaInfluenciaRepository,
            AnalisisPoblacionRepository analisisPoblacionRepository,
            MunicipioRepository municipioRepository,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.areaInfluenciaRepository = areaInfluenciaRepository;
        this.analisisPoblacionRepository = analisisPoblacionRepository;
        this.municipioRepository = municipioRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public AreaInfluenciaDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        return construirDto(proyecto, areaInfluenciaRepository.findByProyectoIdOrderByIdAsc(idProyecto));
    }

    @Override
    public AreaInfluenciaDto guardar(Long idProyecto, AreaInfluenciaRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        areaInfluenciaRepository.deleteAll(areaInfluenciaRepository.findByProyectoIdOrderByIdAsc(idProyecto));
        List<AreaInfluenciaFilaRequestDto> filas = request == null || request.getFilas() == null
                ? List.of() : request.getFilas();
        for (AreaInfluenciaFilaRequestDto fila : filas) {
            Municipio municipio = buscarMunicipio(fila.getDistrito());
            areaInfluenciaRepository.save(AreaInfluencia.builder()
                    .proyecto(proyecto)
                    .departamento(municipio.getDepartamento())
                    .municipio(municipio)
                    .descripcion(fila.getUbicacionEspecifica())
                    .build());
        }
        return construirDto(proyecto, areaInfluenciaRepository.findByProyectoIdOrderByIdAsc(idProyecto));
    }

    @Override
    public AreaInfluenciaDto autocompletarDesdePoblacionObjetivo(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        AnalisisPoblacion analisis = analisisPoblacionRepository.findByProyectoId(idProyecto).orElse(null);
        if (analisis == null) {
            return construirDto(proyecto, List.of());
        }
        List<AreaInfluencia> filas = analisis.getUbicacionesObjetivo().stream()
                .filter(ubicacion -> ubicacion.getUbicacion() != null && !ubicacion.getUbicacion().isBlank())
            .map(ubicacion -> construirFilaAutocompletada(proyecto, ubicacion))
                .toList();
        return construirDto(proyecto, filas);
    }

        private AreaInfluencia construirFilaAutocompletada(Proyecto proyecto, CeldaUbicacionPoblacion ubicacion) {
        Municipio municipio = buscarMunicipio(ubicacion.getUbicacion());
        return AreaInfluencia.builder()
            .proyecto(proyecto)
            .departamento(municipio.getDepartamento())
            .municipio(municipio)
            .descripcion(ubicacion.getUbicacion())
            .build();
        }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    private Municipio buscarMunicipio(String distrito) {
        return municipioRepository.findAllByOrderByNombreAsc().stream()
                .filter(municipio -> municipio.getCodigo().equalsIgnoreCase(distrito)
                        || municipio.getNombre().equalsIgnoreCase(distrito))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El distrito " + distrito + " no existe en el catalogo geografico."));
    }

    private void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private AreaInfluenciaDto construirDto(Proyecto proyecto, List<AreaInfluencia> filas) {
        List<AreaInfluenciaFilaDto> dtoFilas = filas.stream()
                .map(fila -> new AreaInfluenciaFilaDto()
                        .region(fila.getDepartamento().getRegion())
                        .departamento(fila.getDepartamento().getNombre())
                        .distrito(fila.getMunicipio().getNombre())
                        .ubicacionEspecifica(fila.getDescripcion()))
                .toList();
        return new AreaInfluenciaDto().idProyecto(proyecto.getId()).filas(dtoFilas);
    }
}