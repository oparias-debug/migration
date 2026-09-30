package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.CalificacionCriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoElegibilidad;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionCriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Emisión de la Elegibilidad por el Viabilizador (CU-PRE-25, HU-PRE-25-02 y HU-PRE-25-03). Cada
 * emisión queda registrada como una {@link Elegibilidad} con los criterios que aplican, lo que
 * bloquea la ficha hasta que la OT vuelva a enviar comentarios (RN04, RN09).
 */
@Component
@Transactional
public class ElegibilidadEmision {

    public static final String COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER = ViabilidadSolicitud.COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER;

    /** Mensaje literal de RN15. */
    public static final String MENSAJE_COMENTARIOS_OT_SIN_RESPONDER = "Es necesario responder los comentarios de la Opinión Técnica previo a la emisión de elegibilidad";

    /** Longitud de la columna ELEGIBILIDAD.CRITERIOS_CUMPLIDOS. */
    public static final int LONGITUD_CRITERIOS_CUMPLIDOS = 2000;

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final ProyectoRepository proyectoRepository;
    private final ElegibilidadRepository elegibilidadRepository;
    private final CalificacionCriterioElegibilidadRepository calificaciones;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final FiltrosPosterioresViabilidad filtros;

    public ElegibilidadEmision(ProyectoRepository proyectoRepository, ElegibilidadRepository elegibilidadRepository,
            CalificacionCriterioElegibilidadRepository calificaciones, UsuarioRepository usuarioRepository,
            NotificacionService notificacionService, FiltrosPosterioresViabilidad filtros) {
        this.proyectoRepository = proyectoRepository;
        this.elegibilidadRepository = elegibilidadRepository;
        this.calificaciones = calificaciones;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.filtros = filtros;
    }

    /**
     * Emite la Elegibilidad y cambia el estado a "Proyecto elegible". La primera vez notifica al
     * Técnico URP y al Técnico PRE (FB1 paso 6). Las siguientes responden a comentarios de la OT: exigen
     * que estén todos respondidos (RN15) y notifican al Técnico PRE y al Técnico SYMP (FB2 paso 8). El
     * estado tras la reemisión no lo define este CU sino CU-PRE-26 (FA03.1 paso 3.1.3: el proyecto vuelve
     * a la OT como "Proyecto Elegible").
     *
     * @param contexto contexto de la operación del Viabilizador
     */
    public void emitir(ElegibilidadContexto contexto) {
        contexto.exigirHabilitada();
        Proyecto proyecto = contexto.proyecto();
        boolean reemision = contexto.esReemision();
        if (reemision && filtros.tieneComentariosElegibilidadSinResponder(proyecto.getId())) {
            throw new ReglaNegocioException(COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER,
                    MENSAJE_COMENTARIOS_OT_SIN_RESPONDER);
        }

        elegibilidadRepository.save(Elegibilidad.builder().proyecto(proyecto).resultado(ResultadoElegibilidad.ELEGIBLE)
                .criteriosCumplidos(criteriosCumplidos(proyecto.getId()))
                .fechaEvaluacion(LocalDateTime.now(ZONA_EL_SALVADOR)).build());
        proyecto.setEstado(EstadoProyecto.ELEGIBLE);
        proyectoRepository.save(proyecto);

        if (reemision) {
            notificacionService.notificarObservacionesElegibilidadAtendidas(proyecto,
                    destinatarios(RolUsuario.TECNICO_PRE, RolUsuario.TECNICO_SYMP));
            return;
        }
        List<Usuario> destinatarios = new ArrayList<>(usuarioRepository.findByRolAndUnidadEjecutora_IdAndActivoTrue(
                RolUsuario.TECNICO_URP, proyecto.getUnidadEjecutora().getId()));
        destinatarios.addAll(destinatarios(RolUsuario.TECNICO_PRE));
        notificacionService.notificarEmisionElegibilidad(proyecto, destinatarios);
    }

    /** Códigos de los criterios marcados en "¿Aplica?", separados por coma. */
    private String criteriosCumplidos(Long idProyecto) {
        String codigos = calificaciones.findByProyectoId(idProyecto).stream()
                .filter(c -> Boolean.TRUE.equals(c.getAplica())).map(CalificacionCriterioElegibilidad::getCriterio)
                .sorted(CriteriosVigentesElegibilidad.ORDEN_FICHA).map(CriterioElegibilidad::getCodigo)
                .collect(Collectors.joining(","));
        if (codigos.isEmpty()) {
            return null;
        }
        return codigos.length() > LONGITUD_CRITERIOS_CUMPLIDOS ? codigos.substring(0, LONGITUD_CRITERIOS_CUMPLIDOS)
                : codigos;
    }

    private List<Usuario> destinatarios(RolUsuario... roles) {
        List<Usuario> destinatarios = new ArrayList<>();
        for (RolUsuario rol : roles) {
            destinatarios.addAll(usuarioRepository.findByRolAndActivoTrue(rol));
        }
        return destinatarios;
    }
}
