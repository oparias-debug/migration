package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ComentariosDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Revisión de la DGICP sobre una gestión de Opinión Técnica (CU-PRE-26): guardar los comentarios
 * DGICP (FA02, HU-PRE-26-03), enviarlos a la institución (FA03, HU-PRE-26-04) y guardar las
 * conclusiones que habilitan el visto bueno (FA01 pasos 1.1–1.2, HU-PRE-26-07).
 */
@Component
@Transactional
public class OpinionTecnicaRevision {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final OpinionTecnicaRepository opinionesTecnicas;
    private final ProyectoRepository proyectos;
    private final ComentariosDgicpOpinionTecnica comentarios;
    private final BandejaOpinionTecnica bandeja;
    private final DestinatariosOpinionTecnica destinatarios;
    private final NotificacionService notificaciones;

    public OpinionTecnicaRevision(OpinionTecnicaRepository opinionesTecnicas,
            ProyectoRepository proyectos,
            ComentariosDgicpOpinionTecnica comentarios,
            BandejaOpinionTecnica bandeja,
            DestinatariosOpinionTecnica destinatarios,
            NotificacionService notificaciones) {
        this.opinionesTecnicas = opinionesTecnicas;
        this.proyectos = proyectos;
        this.comentarios = comentarios;
        this.bandeja = bandeja;
        this.destinatarios = destinatarios;
        this.notificaciones = notificaciones;
    }

    /**
     * Botón "Guardar" de la DGICP (FA02). El formulario se guarda completo: los comentarios de la
     * solicitud reemplazan a los guardados y un comentario en blanco se elimina.
     *
     * @param contexto contexto de la operación del Técnico PRE o Coordinador PRE
     * @param request comentarios DGICP
     */
    public void guardarComentarios(OpinionTecnicaContexto contexto, ComentariosDgicpRequestDto request) {
        contexto.exigirRevisable();
        comentarios.registrar(contexto.gestion(), contexto.esEmergencia(), request);
    }

    /**
     * Botón "Enviar comentarios" (FA03): guarda los comentarios, deja la gestión observada, cambia el
     * estado del proyecto a "Observado" (lo que habilita la formulación al Técnico URP y la Viabilidad o
     * la Elegibilidad según RN14), inicia el plazo de 5 días hábiles (RN08, RN09) y notifica a la
     * institución y al Viabilizador (RN07 c).
     *
     * @param contexto contexto de la operación del Técnico PRE o Coordinador PRE
     * @param request comentarios DGICP
     * @return a qué se dirigieron los comentarios y las fechas del plazo
     */
    public EnvioComentariosDgicp enviarComentarios(OpinionTecnicaContexto contexto,
            ComentariosDgicpRequestDto request) {
        contexto.exigirRevisable();
        ComentariosDgicpOpinionTecnica.Registrados registrados = comentarios.registrar(contexto.gestion(),
                contexto.esEmergencia(), request);
        registrados.exigirAlguno();

        LocalDateTime ahora = LocalDateTime.now(ZONA_EL_SALVADOR);
        LocalDate finPlazo = DiasHabilesOpinionTecnica.sumar(ahora.toLocalDate(), DiasHabilesOpinionTecnica.PLAZO);
        OpinionTecnica gestion = contexto.gestion();
        gestion.setResultado(ResultadoOpinionTecnica.OBSERVADO);
        gestion.setFechaEmision(ahora);
        gestion.getPlazoComentarios().iniciar(finPlazo);
        contexto.asumirResponsable();
        opinionesTecnicas.save(gestion);
        Proyecto proyecto = contexto.proyecto();
        proyecto.setEstado(EstadoProyecto.OBSERVADO);
        proyectos.save(proyecto);
        bandeja.observar(gestion);

        if (registrados.elegibilidad()) {
            // Anexo A2 h: CU-PRE-25 avisa al Viabilizador que puede ajustar la Elegibilidad (FB2 paso 2); el
            // correo c le llegaría repetido, así que ese va solo a la institución.
            notificaciones.notificarComentariosOpinionTecnica(proyecto, destinatarios.tecnicosUrp(proyecto), finPlazo);
            notificaciones.notificarComentariosOtElegibilidad(proyecto, destinatarios.viabilizadores(proyecto));
        } else {
            notificaciones.notificarComentariosOpinionTecnica(proyecto,
                    destinatarios.institucionYViabilizadores(proyecto), finPlazo);
        }
        return new EnvioComentariosDgicp(registrados.proyecto(), registrados.elegibilidad(), ahora.toLocalDate(),
                finPlazo);
    }

    /**
     * Guarda las "Conclusiones" (FA01 paso 1.1). Si cambian, el "Visto bueno OT" que ya tuvieran deja
     * de valer: el Coordinador PRE aprobó otro texto.
     *
     * @param contexto contexto de la operación del Técnico PRE o Coordinador PRE
     * @param conclusiones texto de las conclusiones
     */
    public void guardarConclusiones(OpinionTecnicaContexto contexto, String conclusiones) {
        contexto.exigirRevisable();
        if (conclusiones == null || conclusiones.isBlank()) {
            throw ComentariosDgicpOpinionTecnica.invalido("conclusiones",
                    "Debe registrar las Conclusiones de la Opinión Técnica.");
        }
        String texto = conclusiones.strip();
        ComentariosDgicpOpinionTecnica.validarLongitud("conclusiones", texto);
        OpinionTecnica gestion = contexto.gestion();
        if (gestion.getRevisionConclusiones().registrar(texto)) {
            opinionesTecnicas.save(gestion);
        }
    }
}
