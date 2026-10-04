package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.CalificacionSubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.PriorizacionProyecto;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.TramoCalificacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionPriorizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionSubcriterioRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoTramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionSubcriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionProyectoRepository;

/**
 * Calificación de un tramo de la matriz multicriterio por su Técnico (CU-PRE-26.5, FB1 pasos 4–6): el
 * botón "Guardar" (FA01) y el de calificar la priorización, que la envía a revisión del Coordinador.
 */
@Component
@Transactional
public class PriorizacionCalificacion {

    public static final String SUBCRITERIO_FUERA_DE_ALCANCE =
            ValidacionCalificacionPriorizacion.SUBCRITERIO_FUERA_DE_ALCANCE;
    public static final String CALIFICACION_INCOMPLETA = ValidacionCalificacionPriorizacion.CALIFICACION_INCOMPLETA;

    /** Mensaje literal de RN04. */
    public static final String MENSAJE_CALIFICACION_INCOMPLETA =
            ValidacionCalificacionPriorizacion.MENSAJE_CALIFICACION_INCOMPLETA;

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final PriorizacionProyectoRepository priorizaciones;
    private final CalificacionSubcriterioPriorizacionRepository calificaciones;
    private final MatrizPriorizacion matriz;
    private final UsuarioRepository usuarios;
    private final NotificacionService notificaciones;

    public PriorizacionCalificacion(PriorizacionProyectoRepository priorizaciones,
            CalificacionSubcriterioPriorizacionRepository calificaciones,
            MatrizPriorizacion matriz,
            UsuarioRepository usuarios,
            NotificacionService notificaciones) {
        this.priorizaciones = priorizaciones;
        this.calificaciones = calificaciones;
        this.matriz = matriz;
        this.usuarios = usuarios;
        this.notificaciones = notificaciones;
    }

    /**
     * Botón "Guardar" (FA01): registra las calificaciones que llegan, aunque falten subcriterios.
     *
     * @param contexto contexto de la operación del Técnico del tramo
     * @param tramo tramo que se califica
     * @param request calificaciones por subcriterio
     * @return la calificación del proyecto
     */
    public PriorizacionProyecto guardar(PriorizacionContexto contexto, TramoPriorizacion tramo,
            CalificacionPriorizacionRequestDto request) {
        return registrar(contexto, tramo, request);
    }

    /**
     * Calificar la priorización (FB1 pasos 5–6): registra las calificaciones, exige un puntaje para cada
     * subcriterio del tramo (RN04), bloquea la pantalla para el Técnico (RN05) y notifica al Coordinador.
     *
     * @param contexto contexto de la operación del Técnico del tramo
     * @param tramo tramo que se califica
     * @param request calificaciones por subcriterio
     * @return la calificación del proyecto
     */
    public PriorizacionProyecto calificar(PriorizacionContexto contexto, TramoPriorizacion tramo,
            CalificacionPriorizacionRequestDto request) {
        PriorizacionProyecto priorizacion = registrar(contexto, tramo, request);
        ValidacionCalificacionPriorizacion.exigirCompleta(tramo, matriz.cargar(priorizacion));
        TramoCalificacionPriorizacion estado = priorizacion.tramo(tramo);
        estado.setEstado(EstadoTramoPriorizacion.ENVIADA_A_REVISION);
        estado.setAjustesHabilitados(false);
        estado.setTecnico(contexto.actor());
        estado.setFechaEnvio(LocalDateTime.now(ZONA_EL_SALVADOR));
        priorizaciones.save(priorizacion);
        notificaciones.notificarPriorizacionPorRevisar(contexto.proyecto(),
                usuarios.findByRolAndActivoTrue(tramo.getCoordinador()), tramo.getDescripcion());
        return priorizacion;
    }

    /** Valida y guarda las calificaciones del tramo, creando la calificación del proyecto si hace falta. */
    private PriorizacionProyecto registrar(PriorizacionContexto contexto, TramoPriorizacion tramo,
            CalificacionPriorizacionRequestDto request) {
        contexto.exigirCalificable(tramo);
        PriorizacionProyecto priorizacion = contexto.priorizacion() != null ? contexto.priorizacion()
                : priorizaciones.save(PriorizacionProyecto.builder().proyecto(contexto.proyecto())
                        .opinionTecnica(contexto.opinionTecnica()).build());
        var actual = matriz.cargar(priorizacion);
        List<CalificacionSubcriterioRequestDto> recibidas = request.getCalificaciones();
        ValidacionCalificacionPriorizacion.exigirDelTramo(tramo, actual, recibidas);
        List<SubcriterioPriorizacion> delTramo = actual.subcriterios(tramo);
        for (CalificacionSubcriterioRequestDto recibida : recibidas) {
            SubcriterioPriorizacion subcriterio = delTramo.stream()
                    .filter((SubcriterioPriorizacion s) -> s.getNumero().equals(recibida.getSubcriterioNumero()))
                    .findFirst()
                    .orElseThrow();
            CalificacionSubcriterioPriorizacion calificacion = actual.calificaciones().get(subcriterio.getId());
            if (calificacion == null) {
                calificacion = CalificacionSubcriterioPriorizacion.builder().priorizacion(priorizacion)
                        .subcriterio(subcriterio).build();
            }
            calificacion.setValor(ValorCalificacion.valueOf(recibida.getCalificacion().name()));
            calificaciones.save(calificacion);
        }
        return priorizacion;
    }
}
