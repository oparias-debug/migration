package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Viabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ViabilidadRepository;

/**
 * Cierre de la revisión en curso por el Viabilizador (CU-PRE-24): devolución con comentarios
 * (HU-PRE-24-02) o emisión de la Viabilidad (HU-PRE-24-03). Cada cierre queda registrado como un
 * {@link Viabilidad} y se notifica al solicitante.
 */
@Component
@Transactional
public class ViabilidadCierre {

    public static final String JUSTIFICACION_VIABILIDAD_REQUERIDA = "JUSTIFICACION_VIABILIDAD_REQUERIDA";

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final ProyectoRepository proyectoRepository;
    private final RevisionViabilidadRepository revisionRepository;
    private final ViabilidadRepository viabilidadRepository;
    private final NotificacionService notificacionService;
    private final FiltrosPosterioresViabilidad filtros;

    public ViabilidadCierre(ProyectoRepository proyectoRepository,
            RevisionViabilidadRepository revisionRepository,
            ViabilidadRepository viabilidadRepository,
            NotificacionService notificacionService,
            FiltrosPosterioresViabilidad filtros) {
        this.proyectoRepository = proyectoRepository;
        this.revisionRepository = revisionRepository;
        this.viabilidadRepository = viabilidadRepository;
        this.notificacionService = notificacionService;
        this.filtros = filtros;
    }

    /**
     * Devuelve el proyecto con los comentarios de la revisión en curso (RN05, RN10).
     *
     * @param contexto contexto de la operación del Viabilizador
     */
    public void devolver(ViabilidadContexto contexto) {
        RevisionViabilidad revision = contexto.exigirRevisionEnCurso();
        Proyecto proyecto = contexto.proyecto();

        // RN10: la revisión devuelta conserva sus comentarios; la próxima solicitud abre otra.
        cerrarRevision(revision, EstadoRevisionViabilidad.DEVUELTA, contexto.actor());
        registrarResultado(proyecto, ResultadoViabilidad.OBSERVADO, revision, contexto.actor());
        // RN05: "Observado" vuelve a habilitar la formulación y "Solicitar Viabilidad".
        proyecto.setEstado(EstadoProyecto.OBSERVADO);
        proyectoRepository.save(proyecto);

        notificacionService.notificarComentariosViabilidad(proyecto, revision.getSolicitante());
    }

    /**
     * Emite la Viabilidad del proyecto sobre la revisión en curso (RN03).
     *
     * @param contexto contexto de la operación del Viabilizador
     * @return {@code true} si corresponde gestionar Elegibilidad: la primera vez, o cuando la OT también
     *         comentó los criterios de elegibilidad (RN14 de CU-PRE-26)
     */
    public boolean emitir(ViabilidadContexto contexto) {
        RevisionViabilidad revision = contexto.exigirRevisionEnCurso();
        if (ViabilidadContexto.esVacio(revision.getObservacionesGenerales())) {
            throw new ReglaNegocioException(JUSTIFICACION_VIABILIDAD_REQUERIDA,
                    "Debe registrar y guardar las Observaciones Generales/Justificación de la Viabilidad "
                            + "antes de emitirla.");
        }
        Proyecto proyecto = contexto.proyecto();

        // RN03: Elegibilidad solo se gestiona la primera vez; después se salta a la OT, salvo que la OT
        // haya comentado también los criterios de elegibilidad (RN14 de CU-PRE-26).
        boolean pasaPorElegibilidad = !filtros.yaPasoPorElegibilidad(proyecto.getId())
                || filtros.otPideElegibilidad(proyecto.getId());
        revision.setHabilitaElegibilidad(pasaPorElegibilidad);
        cerrarRevision(revision, EstadoRevisionViabilidad.EMITIDA, contexto.actor());
        registrarResultado(proyecto, ResultadoViabilidad.VIABLE, revision, contexto.actor());
        proyecto.setEstado(EstadoProyecto.VIABLE);
        proyectoRepository.save(proyecto);

        notificacionService.notificarEmisionViabilidad(proyecto, revision.getSolicitante());
        return pasaPorElegibilidad;
    }

    private void cerrarRevision(RevisionViabilidad revision, EstadoRevisionViabilidad estado, Usuario viabilizador) {
        revision.setEstado(estado);
        revision.setViabilizador(viabilizador);
        revision.setFechaCierre(LocalDateTime.now(ZONA_EL_SALVADOR));
        revisionRepository.save(revision);
    }

    private void registrarResultado(Proyecto proyecto, ResultadoViabilidad resultado, RevisionViabilidad revision,
            Usuario viabilizador) {
        viabilidadRepository.save(Viabilidad.builder()
                .proyecto(proyecto)
                .resultado(resultado)
                .fechaEvaluacion(revision.getFechaCierre())
                .observaciones(revision.getObservacionesGenerales())
                .evaluador(viabilizador)
                .build());
    }
}
