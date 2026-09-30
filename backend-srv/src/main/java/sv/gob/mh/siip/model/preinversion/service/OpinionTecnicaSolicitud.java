package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitudOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/**
 * Apertura de las gestiones de Opinión Técnica (CU-PRE-26): la solicitud de OT (RN04, HU-PRE-26-01),
 * la Actualización de OT (FA04, HU-PRE-26-10) y la asignación del caso a un Técnico PRE (RN07 b,
 * HU-PRE-26-02).
 *
 * <p>Cada gestión abre además una solicitud de tipo {@code OPINION_TECNICA} en la Bandeja de
 * Preinversión (CU-PRE-02), donde el Coordinador PRE la ve para asignarla (Anexo A2 a).
 */
@Component
@Transactional
public class OpinionTecnicaSolicitud {

    public static final String NOTA_SOLICITUD_OT_REQUERIDA = DocumentosOpinionTecnica.NOTA_SOLICITUD_OT_REQUERIDA;
    public static final String ACTUALIZACION_OT_NO_DISPONIBLE = "ACTUALIZACION_OT_NO_DISPONIBLE";

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final OpinionTecnicaRepository opinionesTecnicas;
    private final BandejaOpinionTecnica bandeja;
    private final OpinionTecnicaAcceso acceso;
    private final EtapasOpinionTecnica etapas;
    private final DocumentosOpinionTecnica documentos;
    private final DestinatariosOpinionTecnica destinatarios;
    private final NotificacionService notificaciones;

    public OpinionTecnicaSolicitud(OpinionTecnicaRepository opinionesTecnicas,
            BandejaOpinionTecnica bandeja,
            OpinionTecnicaAcceso acceso,
            EtapasOpinionTecnica etapas,
            DocumentosOpinionTecnica documentos,
            DestinatariosOpinionTecnica destinatarios,
            NotificacionService notificaciones) {
        this.opinionesTecnicas = opinionesTecnicas;
        this.bandeja = bandeja;
        this.acceso = acceso;
        this.etapas = etapas;
        this.documentos = documentos;
        this.destinatarios = destinatarios;
        this.notificaciones = notificaciones;
    }

    /**
     * Opción "1. Opinión Técnica": desactivada mientras se solicita una Actualización de OT (Anexo A – RN2).
     *
     * @param proyecto proyecto
     * @return si la opción está habilitada
     */
    public boolean opinionTecnicaHabilitada(Proyecto proyecto) {
        return !acceso.actualizacionEnCurso(proyecto.getId());
    }

    /**
     * Opción "2. Actualización de OT": desactivada si la OT se solicita por primera vez (Anexo A – RN1),
     * si hay otra gestión abierta o si la ejecución del proyecto ya inició (Anexo A, opción 2).
     *
     * @param proyecto proyecto
     * @return si la opción está habilitada
     */
    public boolean actualizacionHabilitada(Proyecto proyecto) {
        return impedimentoActualizacion(proyecto) == null;
    }

    /** @return por qué no se puede gestionar la Actualización de OT, o {@code null} si se puede */
    private String impedimentoActualizacion(Proyecto proyecto) {
        Long idProyecto = proyecto.getId();
        String impedimento;
        if (!opinionesTecnicas.existsByProyectoIdAndResultado(idProyecto, ResultadoOpinionTecnica.FAVORABLE)) {
            impedimento = "El proyecto no tiene una Opinión Técnica favorable previa que actualizar.";
        } else if (proyecto.getEstado() == EstadoProyecto.EN_EJECUCION
                || proyecto.getEstado() == EstadoProyecto.FINALIZADO) {
            impedimento = "La ejecución del proyecto ya inició; no se puede actualizar la Opinión Técnica.";
        } else if (acceso.gestionEnCurso(idProyecto)) {
            impedimento = "Hay otra gestión de Opinión Técnica en curso para el proyecto.";
        } else if (etapas.paraActualizacion(idProyecto).isEmpty()) {
            impedimento = "No existe una Opinión Técnica previa para la etapa que se está gestionando.";
        } else {
            impedimento = null;
        }
        return impedimento;
    }

    /**
     * Botón "Solicitar OT" (RN04): registra la "Fecha de solicitud", guarda la "Nota de solicitud de OT"
     * y notifica al Coordinador PRE (RN07 a, Anexo A2 a).
     *
     * @param actor Técnico URP o Viabilizador
     * @param proyecto proyecto
     * @param notaSolicitud archivo "Nota de solicitud de OT"
     * @return la gestión abierta
     */
    public OpinionTecnica solicitar(Usuario actor, Proyecto proyecto, MultipartFile notaSolicitud) {
        DocumentosOpinionTecnica.exigirNotaSolicitud(notaSolicitud);
        acceso.impedimentoSolicitud(proyecto).ifPresent((ConflictoEstadoException conflicto) -> {
            throw conflicto;
        });
        EtapasOpinionTecnica.Etapas gestionadas = etapas.paraOpinionTecnica(proyecto.getId());
        OpinionTecnica gestion = abrir(actor, proyecto, TipoSolicitudOpinionTecnica.OPINION_TECNICA, gestionadas);
        documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_SOLICITUD_OT, notaSolicitud, actor);
        notificaciones.notificarSolicitudOpinionTecnica(proyecto, destinatarios.coordinadoresPre());
        return gestion;
    }

    /**
     * Actualización de OT (FA04 pasos 4.3–4.4), tras confirmar el mensaje del Anexo A.3. Valida que
     * exista una OT previa para la etapa que se gestiona y abre la gestión; el Técnico URP continúa
     * con la Viabilidad (paso 4.5), que se vuelve a habilitar (ver {@link FiltrosPosterioresViabilidad}).
     *
     * @param actor Técnico URP
     * @param proyecto proyecto
     * @return la gestión abierta
     */
    public OpinionTecnica solicitarActualizacion(Usuario actor, Proyecto proyecto) {
        String impedimento = impedimentoActualizacion(proyecto);
        if (impedimento != null) {
            throw new ConflictoEstadoException(ACTUALIZACION_OT_NO_DISPONIBLE, impedimento);
        }
        EtapasOpinionTecnica.Etapas gestionadas = etapas.paraActualizacion(proyecto.getId()).orElseThrow();
        OpinionTecnica gestion = abrir(actor, proyecto, TipoSolicitudOpinionTecnica.ACTUALIZACION_OT, gestionadas);
        notificaciones.notificarSolicitudOpinionTecnica(proyecto, destinatarios.coordinadoresPre());
        return gestion;
    }

    /**
     * El Coordinador PRE asigna la revisión a un Técnico PRE (RN07 b, Anexo A2 b).
     *
     * @param contexto contexto de la operación del Coordinador PRE
     * @param idTecnicoPre usuario con rol Técnico PRE
     * @return la gestión asignada
     */
    public OpinionTecnica asignar(OpinionTecnicaContexto contexto, Long idTecnicoPre) {
        OpinionTecnica gestion = contexto.gestion();
        if (!gestion.estaEnCurso()) {
            throw new ConflictoEstadoException(OpinionTecnicaContexto.COMENTARIOS_DGICP_YA_ENVIADOS,
                    "La gestión de Opinión Técnica ya no está en revisión.");
        }
        if (idTecnicoPre == null || idTecnicoPre <= 0) {
            throw ComentariosDgicpOpinionTecnica.invalido("tecnicoPreId", "Seleccione un Técnico PRE válido.");
        }
        Usuario tecnico = destinatarios.tecnicoPreActivo(idTecnicoPre);
        LocalDateTime ahora = LocalDateTime.now(ZONA_EL_SALVADOR);
        gestion.setTecnicoResponsable(tecnico);
        gestion.setFechaAsignacion(ahora);
        opinionesTecnicas.save(gestion);
        bandeja.asignar(gestion);
        notificaciones.notificarAsignacionOpinionTecnica(contexto.proyecto(), tecnico);
        return gestion;
    }

    private OpinionTecnica abrir(Usuario actor, Proyecto proyecto, TipoSolicitudOpinionTecnica tipo,
            EtapasOpinionTecnica.Etapas gestionadas) {
        LocalDateTime ahora = LocalDateTime.now(ZONA_EL_SALVADOR);
        // RN 12: una gestión archivada por vencimiento del plazo (RN09) no cuenta; la siguiente vuelve a
        // mostrar "Comentarios Elegibilidad".
        boolean primera = !opinionesTecnicas.existsByProyectoIdAndFechaArchivoIsNull(proyecto.getId());
        SolicitudPreinversion solicitud = bandeja.abrir(proyecto, actor, ahora);
        return opinionesTecnicas.save(OpinionTecnica.builder()
                .proyecto(proyecto)
                .tipoSolicitud(tipo)
                .primeraGestion(primera)
                .solicitud(solicitud)
                .solicitante(actor)
                .fechaSolicitud(ahora)
                .etapaActual(gestionadas.actual())
                .etapaFutura(gestionadas.futura())
                .build());
    }
}
