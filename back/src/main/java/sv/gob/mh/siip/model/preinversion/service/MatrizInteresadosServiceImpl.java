package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Interesado;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.InteresadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NivelInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.NivelInteresDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoInteresadoDto;
import sv.gob.mh.siip.model.preinversion.enums.NivelInfluencia;
import sv.gob.mh.siip.model.preinversion.enums.NivelInteres;
import sv.gob.mh.siip.model.preinversion.enums.TipoInteresado;
import sv.gob.mh.siip.model.preinversion.repository.InteresadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class MatrizInteresadosServiceImpl implements MatrizInteresadosService {

    private final ProyectoRepository proyectoRepository;
    private final InteresadoRepository interesadoRepository;
    private final ActorContexto actorContexto;

    public MatrizInteresadosServiceImpl(ProyectoRepository proyectoRepository,
            InteresadoRepository interesadoRepository,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.interesadoRepository = interesadoRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public MatrizInteresadosDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        List<Interesado> interesados = interesadoRepository.findByProyectoIdOrderByOrdenAsc(idProyecto);
        return construirDto(proyecto, interesados);
    }

    @Override
    public MatrizInteresadosDto guardar(Long idProyecto, MatrizInteresadosRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        List<Interesado> nuevos = reemplazarInteresados(proyecto, request.getInteresados());
        return construirDto(proyecto, nuevos);
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    /** Igual que en AlternativaSolucionServiceImpl/IdentificacionServiceImpl: RN01/RN02. */
    private void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private List<Interesado> reemplazarInteresados(Proyecto proyecto, List<InteresadoRequestDto> filas) {
        interesadoRepository.deleteAll(interesadoRepository.findByProyectoIdOrderByOrdenAsc(proyecto.getId()));

        List<InteresadoRequestDto> valores = filas == null ? List.of() : filas;
        List<Interesado> nuevos = new ArrayList<>();
        int orden = 0;
        for (InteresadoRequestDto fila : valores) {
            nuevos.add(Interesado.builder()
                    .proyecto(proyecto)
                    .nombreInteresado(fila.getNombreInteresado())
                    .tipo(mapear(fila.getTipo()))
                    .nivelInfluencia(mapear(fila.getNivelInfluencia()))
                    .nivelInteres(mapear(fila.getNivelInteres()))
                    .estrategiaGestion(fila.getEstrategiaGestion())
                    .orden(orden++)
                    .build());
        }
        return interesadoRepository.saveAll(nuevos);
    }

    private MatrizInteresadosDto construirDto(Proyecto proyecto, List<Interesado> interesados) {
        return new MatrizInteresadosDto()
                .idProyecto(proyecto.getId())
                .interesados(interesados.stream().map(this::toRequestDto).toList());
    }

    private InteresadoRequestDto toRequestDto(Interesado entidad) {
        return new InteresadoRequestDto()
                .nombreInteresado(entidad.getNombreInteresado())
                .tipo(mapear(entidad.getTipo()))
                .nivelInfluencia(mapear(entidad.getNivelInfluencia()))
                .nivelInteres(mapear(entidad.getNivelInteres()))
                .estrategiaGestion(entidad.getEstrategiaGestion());
    }

    private TipoInteresado mapear(TipoInteresadoDto tipo) {
        return tipo == null ? null : TipoInteresado.valueOf(tipo.name());
    }

    private TipoInteresadoDto mapear(TipoInteresado tipo) {
        return tipo == null ? null : TipoInteresadoDto.valueOf(tipo.name());
    }

    private NivelInfluencia mapear(NivelInfluenciaDto nivel) {
        return nivel == null ? null : NivelInfluencia.valueOf(nivel.name());
    }

    private NivelInfluenciaDto mapear(NivelInfluencia nivel) {
        return nivel == null ? null : NivelInfluenciaDto.valueOf(nivel.name());
    }

    private NivelInteres mapear(NivelInteresDto nivel) {
        return nivel == null ? null : NivelInteres.valueOf(nivel.name());
    }

    private NivelInteresDto mapear(NivelInteres nivel) {
        return nivel == null ? null : NivelInteresDto.valueOf(nivel.name());
    }
}
