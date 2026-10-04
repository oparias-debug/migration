package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.Priorizacion;
import sv.gob.mh.siip.model.preinversion.domain.PriorizacionProyecto;
import sv.gob.mh.siip.model.preinversion.domain.TramoCalificacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoTramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionRepository;

/**
 * Revisión de la calificación por los Coordinadores (CU-PRE-26.5, FB1 pasos 7–8) y habilitación de
 * ajustes (RN10, RN11).
 *
 * <p>Revisados los dos tramos, la priorización se completa (RN07): se guarda la "Prioridad del proyecto"
 * con su categoría y se registra en {@link Priorizacion}, que el Banco de Proyectos (CU-PRE-29) muestra.
 */
@Component
@Transactional
public class PriorizacionRevision {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");
    private static final int MESES_POR_CUATRIMESTRE = 4;

    private final PriorizacionProyectoRepository priorizaciones;
    private final PriorizacionRepository banco;
    private final MatrizPriorizacion matriz;
    private final InterpretacionPriorizacion interpretacion;
    private final UsuarioRepository usuarios;
    private final NotificacionService notificaciones;

    public PriorizacionRevision(PriorizacionProyectoRepository priorizaciones,
            PriorizacionRepository banco,
            MatrizPriorizacion matriz,
            InterpretacionPriorizacion interpretacion,
            UsuarioRepository usuarios,
            NotificacionService notificaciones) {
        this.priorizaciones = priorizaciones;
        this.banco = banco;
        this.matriz = matriz;
        this.interpretacion = interpretacion;
        this.usuarios = usuarios;
        this.notificaciones = notificaciones;
    }

    /**
     * "Priorización revisada Coordinador PRE/SYMP" (FB1 pasos 7–8): la pantalla queda deshabilitada para el
     * Técnico del tramo. Tras los criterios 1 a 4 se avisa al Técnico SYMP; tras el criterio 5 la
     * priorización se completa y se avisa al Técnico PRE.
     *
     * @param contexto contexto de la operación del Coordinador del tramo
     * @param tramo tramo que se revisa
     * @return la calificación del proyecto
     */
    public PriorizacionProyecto revisar(PriorizacionContexto contexto, TramoPriorizacion tramo) {
        contexto.exigirEnviada(tramo);
        PriorizacionProyecto priorizacion = contexto.priorizacion();
        var ahora = LocalDateTime.now(ZONA_EL_SALVADOR);
        TramoCalificacionPriorizacion estado = priorizacion.tramo(tramo);
        estado.setEstado(EstadoTramoPriorizacion.REVISADA);
        estado.setCoordinador(contexto.actor());
        estado.setFechaRevision(ahora);
        priorizaciones.save(priorizacion);

        boolean completa = priorizacion.tramo(TramoPriorizacion.PRE).getEstado() == EstadoTramoPriorizacion.REVISADA
                && priorizacion.tramo(TramoPriorizacion.SYMP).getEstado() == EstadoTramoPriorizacion.REVISADA;
        if (completa) {
            completar(priorizacion, ahora);
        } else if (tramo == TramoPriorizacion.PRE) {
            notificaciones.notificarCriterioCincoPorCalificar(contexto.proyecto(),
                    unir(usuarios.findByRolAndActivoTrue(RolUsuario.TECNICO_SYMP),
                            usuarios.findByRolAndActivoTrue(RolUsuario.COORDINADOR_SYMP)));
        } else {
            // Un ajuste del criterio 5 revisado antes de revisar otra vez los criterios 1 a 4: se completa
            // cuando el Coordinador PRE revise.
            notificaciones.notificarPriorizacionPorRevisar(contexto.proyecto(),
                    usuarios.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE),
                    TramoPriorizacion.PRE.getDescripcion());
        }
        return priorizacion;
    }

    /**
     * "Habilitar Calificación de Prioridad" (RN10, RN11): vuelve a habilitar al Técnico del tramo los
     * campos de calificación. Si el tramo todavía no se envió, ya está habilitado y no cambia nada.
     *
     * @param contexto contexto de la operación del Coordinador del tramo
     * @param tramo tramo cuyos ajustes se habilitan
     */
    public void habilitarAjustes(PriorizacionContexto contexto, TramoPriorizacion tramo) {
        PriorizacionProyecto priorizacion = contexto.priorizacion();
        if (priorizacion != null && priorizacion.tramo(tramo).getEstado() != EstadoTramoPriorizacion.PENDIENTE) {
            priorizacion.tramo(tramo).setAjustesHabilitados(true);
            priorizaciones.save(priorizacion);
        }
    }

    /** RN07: guarda la "Prioridad del proyecto" y la registra para el Banco de Proyectos. */
    private void completar(PriorizacionProyecto priorizacion, LocalDateTime ahora) {
        BigDecimal prioridad = matriz.cargar(priorizacion).calcular().prioridad();
        var rango = interpretacion.interpretar(prioridad);
        priorizacion.setPrioridad(prioridad);
        priorizacion.setCategoria(rango == null ? null : rango.nombre());
        priorizacion.setFechaCompletada(ahora);
        priorizaciones.save(priorizacion);
        banco.save(Priorizacion.builder()
                .proyecto(priorizacion.getProyecto())
                .anio(ahora.getYear())
                .cuatrimestre((ahora.getMonthValue() - 1) / MESES_POR_CUATRIMESTRE + 1)
                .puntaje(prioridad)
                .fechaPriorizacion(ahora)
                .build());
        Usuario tecnicoPre = priorizacion.tramo(TramoPriorizacion.PRE).getTecnico();
        List<Usuario> tecnicos = tecnicoPre == null ? usuarios.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE)
                : List.of(tecnicoPre);
        notificaciones.notificarPriorizacionCompletada(priorizacion.getProyecto(),
                unir(tecnicos, usuarios.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE)));
    }

    private static List<Usuario> unir(List<Usuario> primeros, List<Usuario> segundos) {
        return DestinatariosOpinionTecnica.unir(primeros, segundos);
    }
}
