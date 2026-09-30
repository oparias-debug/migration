package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionApartadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionesInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/**
 * Atención de los comentarios DGICP por la institución (CU-PRE-26, FA02 y FA03.1; HU-PRE-26-05):
 * la "Justificación Institución" del Técnico URP y el envío de los ajustes.
 *
 * <p>El CU no tiene un botón propio para enviar los ajustes: el Técnico URP los envía al dar clic en
 * "Solicitar Viabilidad" de CU-PRE-24 (FA03.1 paso 3.1.1), que invoca {@link #registrarEnvio(Proyecto)}.
 */
@Component
@Transactional
public class OpinionTecnicaAjustes {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final OpinionTecnicaRepository opinionesTecnicas;
    private final ComentariosDgicpOpinionTecnica comentariosDgicp;
    private final DestinatariosOpinionTecnica destinatarios;
    private final NotificacionService notificaciones;

    public OpinionTecnicaAjustes(OpinionTecnicaRepository opinionesTecnicas,
            ComentariosDgicpOpinionTecnica comentariosDgicp,
            DestinatariosOpinionTecnica destinatarios,
            NotificacionService notificaciones) {
        this.opinionesTecnicas = opinionesTecnicas;
        this.comentariosDgicp = comentariosDgicp;
        this.destinatarios = destinatarios;
        this.notificaciones = notificaciones;
    }

    /**
     * Botón "Guardar" del Técnico URP o el Viabilizador (FA02 paso 2.2): registra la "Justificación
     * Institución" de los comentarios DGICP. El Técnico URP responde los apartados y los documentos
     * anexos (RN03); el Viabilizador, la Elegibilidad (Anexo B.1 "Respuesta Institución"). Solo cambia
     * lo que llega en la solicitud; un texto en blanco borra la justificación.
     *
     * @param contexto contexto de la operación del Técnico URP o el Viabilizador
     * @param request justificaciones por apartado, de los documentos anexos y de la Elegibilidad
     */
    public void guardarJustificaciones(OpinionTecnicaContexto contexto, JustificacionesInstitucionRequestDto request) {
        contexto.exigirJustificable();
        Map<String, ComentarioOpinionTecnica> porApartado = comentariosDgicp.porApartado(contexto.gestion());
        Map<String, String> justificaciones = justificacionesDe(request);
        exigirSeccionesDelActor(contexto, justificaciones.keySet());
        List<ErrorDetalleDto> errores = new ArrayList<>();
        for (String apartado : justificaciones.keySet()) {
            if (!porApartado.containsKey(apartado)) {
                errores.add(new ErrorDetalleDto().campo("justificacionesApartados")
                        .mensaje("No hay comentario DGICP que justificar en: " + apartado));
            }
        }
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(DocumentosViabilidad.SOLICITUD_INVALIDA,
                    "Solo se justifican los apartados con comentarios DGICP.", errores);
        }
        for (Map.Entry<String, String> justificacion : justificaciones.entrySet()) {
            comentariosDgicp.justificar(porApartado.get(justificacion.getKey()), justificacion.getValue());
        }
    }

    /** Cada actor justifica solo su sección: el Técnico URP el proyecto y el Viabilizador la Elegibilidad. */
    private static void exigirSeccionesDelActor(OpinionTecnicaContexto contexto, Set<String> apartados) {
        boolean ajenas = apartados.stream().anyMatch((String apartado) -> ComentarioOpinionTecnica.ELEGIBILIDAD
                .equals(apartado) ? !contexto.justificaElegibilidad() : !contexto.justificaProyecto());
        if (ajenas) {
            throw new AccesoDenegadoException("El Técnico URP responde los comentarios a los apartados y documentos "
                    + "del proyecto; el Viabilizador, los comentarios a la Elegibilidad.");
        }
    }

    /** Justificaciones de la solicitud por apartado; las que no llegan no cambian. */
    private static Map<String, String> justificacionesDe(JustificacionesInstitucionRequestDto request) {
        Map<String, String> justificaciones = new LinkedHashMap<>();
        if (request == null) {
            return justificaciones;
        }
        if (request.getJustificacionesApartados() != null) {
            for (JustificacionApartadoRequestDto j : request.getJustificacionesApartados()) {
                justificaciones.put(j.getApartadoCodigo(), j.getJustificacionInstitucion());
            }
        }
        if (request.getJustificacionInstitucionDocumentosAnexos() != null) {
            justificaciones.put(ComentarioOpinionTecnica.DOCUMENTOS_ANEXOS,
                    request.getJustificacionInstitucionDocumentosAnexos());
        }
        if (request.getJustificacionInstitucionElegibilidad() != null) {
            justificaciones.put(ComentarioOpinionTecnica.ELEGIBILIDAD,
                    request.getJustificacionInstitucionElegibilidad());
        }
        return justificaciones;
    }

    /**
     * Envío de los ajustes (FA03.1 paso 3.1.3): si la última gestión de OT del proyecto espera ajustes,
     * registra la "Fecha de ajustes", detiene el control del plazo (RN08) y notifica al Técnico PRE y
     * al Coordinador PRE (RN07 d, Anexo A2 d). Si no hay una OT observada, no hace nada.
     *
     * @param proyecto proyecto cuya Viabilidad se vuelve a solicitar
     */
    public void registrarEnvio(Proyecto proyecto) {
        opinionesTecnicas.findFirstByProyectoIdOrderByFechaEmisionDesc(proyecto.getId())
                .filter(OpinionTecnica::estaObservada)
                .ifPresent((OpinionTecnica gestion) -> {
                    gestion.setFechaAjustes(LocalDateTime.now(ZONA_EL_SALVADOR));
                    opinionesTecnicas.save(gestion);
                    notificaciones.notificarAjustesOpinionTecnica(proyecto, destinatarios.dgicp(gestion));
                });
    }
}
