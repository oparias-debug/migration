package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AlternativaSolucion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AlternativaSolucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AlternativaSolucionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class AlternativaSolucionServiceImpl implements AlternativaSolucionService {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");
    private static final String CODIGO_SIN_ALTERNATIVAS = "SIN_ALTERNATIVAS";
    private static final String CODIGO_JUSTIFICACION_REQUERIDA = "JUSTIFICACION_REQUERIDA";

    private final ProyectoRepository proyectoRepository;
    private final AlternativaSolucionRepository alternativaSolucionRepository;
    private final ActorContexto actorContexto;

    public AlternativaSolucionServiceImpl(ProyectoRepository proyectoRepository,
            AlternativaSolucionRepository alternativaSolucionRepository,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.alternativaSolucionRepository = alternativaSolucionRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroAlternativasDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        if (proyecto.getFechaUltimoGuardadoAlternativasSolucion() == null
                && actor.getRol() != RolUsuario.TECNICO_URP) {
            // RN1-2/RN1-3: para cualquier otro actor, sin al menos un guardado previo el recurso no existe.
            throw new RecursoNoEncontradoException(
                    "El registro de alternativas del proyecto " + idProyecto + " todavia no se ha guardado.");
        }
        List<AlternativaSolucion> alternativas = alternativaSolucionRepository
                .findByProyectoIdOrderByOrdenAsc(idProyecto);
        return construirDto(proyecto, alternativas);
    }

    @Override
    public RegistroAlternativasDto guardar(Long idProyecto, RegistroAlternativasRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        List<AlternativaSolucion> nuevas = reemplazarAlternativas(proyecto, request.getAlternativas());

        proyecto.setJustificacionAlternativasSolucion(request.getJustificacion());
        proyecto.setFechaUltimoGuardadoAlternativasSolucion(LocalDateTime.now(ZONA_EL_SALVADOR));
        proyecto = proyectoRepository.save(proyecto);

        return construirDto(proyecto, nuevas);
    }

    @Override
    public RegistroAlternativasDto avanzarAAnalisisInteresados(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        List<AlternativaSolucion> alternativas = alternativaSolucionRepository
                .findByProyectoIdOrderByOrdenAsc(idProyecto);
        if (alternativas.isEmpty()) {
            throw new ValidacionNegocioException(CODIGO_SIN_ALTERNATIVAS,
                    "Debe ingresar al menos una alternativa", null);
        }
        if (alternativas.size() == 1) {
            String justificacion = proyecto.getJustificacionAlternativasSolucion();
            if (justificacion == null || justificacion.isBlank()) {
                throw new ValidacionNegocioException(CODIGO_JUSTIFICACION_REQUERIDA,
                        "Debe completarse el campo Justificación", null);
            }
        }
        return construirDto(proyecto, alternativas);
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    /** Igual que en IdentificacionServiceImpl/ProyectoServiceImpl: RN1-1/RN1-2/RN1-3. */
    private void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private List<AlternativaSolucion> reemplazarAlternativas(Proyecto proyecto,
            List<AlternativaSolucionRequestDto> filas) {
        alternativaSolucionRepository.deleteAll(
                alternativaSolucionRepository.findByProyectoIdOrderByOrdenAsc(proyecto.getId()));

        List<AlternativaSolucionRequestDto> valores = filas == null ? List.of() : filas;
        List<AlternativaSolucion> nuevas = new ArrayList<>();
        int orden = 0;
        for (AlternativaSolucionRequestDto fila : valores) {
            nuevas.add(AlternativaSolucion.builder()
                    .proyecto(proyecto)
                    .nombreAlternativa(fila.getNombreAlternativa())
                    .montoAlternativa(fila.getMontoAlternativa())
                    .descripcionAlternativa(fila.getDescripcionAlternativa())
                    .seleccionada(fila.getSeleccionada())
                    .orden(orden++)
                    .build());
        }
        return alternativaSolucionRepository.saveAll(nuevas);
    }

    private RegistroAlternativasDto construirDto(Proyecto proyecto, List<AlternativaSolucion> alternativas) {
        return new RegistroAlternativasDto()
                .idProyecto(proyecto.getId())
                .alternativas(alternativas.stream().map(this::toRequestDto).toList())
                .justificacion(proyecto.getJustificacionAlternativasSolucion())
                .fechaUltimoGuardado(map(proyecto.getFechaUltimoGuardadoAlternativasSolucion()));
    }

    private AlternativaSolucionRequestDto toRequestDto(AlternativaSolucion entidad) {
        return new AlternativaSolucionRequestDto()
                .nombreAlternativa(entidad.getNombreAlternativa())
                .montoAlternativa(entidad.getMontoAlternativa())
                .descripcionAlternativa(entidad.getDescripcionAlternativa())
                .seleccionada(entidad.getSeleccionada());
    }

    private OffsetDateTime map(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(ZONA_EL_SALVADOR).toOffsetDateTime();
    }
}
