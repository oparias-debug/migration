package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EncabezadoProyectoOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoGestionOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ResumenOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoFormularioOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/**
 * Conversión a DTO de la pantalla de Opinión Técnica (CU-PRE-26, Anexo A.1) y del listado de gestiones.
 * La tabla de apartados la arma {@link ApartadosOpinionTecnica} y la sección de documentos
 * {@link DocumentosAnexosOpinionTecnica}.
 */
@Component
@Transactional(readOnly = true)
public class OpinionTecnicaEnsamblador {

    private final ComentariosDgicpOpinionTecnica comentarios;
    private final DocumentosOpinionTecnica notas;
    private final ApartadosOpinionTecnica apartados;
    private final DocumentosAnexosOpinionTecnica documentosAnexos;
    private final OpinionTecnicaRepository opinionesTecnicas;

    public OpinionTecnicaEnsamblador(ComentariosDgicpOpinionTecnica comentarios,
            DocumentosOpinionTecnica notas,
            ApartadosOpinionTecnica apartados,
            DocumentosAnexosOpinionTecnica documentosAnexos,
            OpinionTecnicaRepository opinionesTecnicas) {
        this.comentarios = comentarios;
        this.notas = notas;
        this.apartados = apartados;
        this.documentosAnexos = documentosAnexos;
        this.opinionesTecnicas = opinionesTecnicas;
    }

    /**
     * @param contexto contexto de la operación
     * @return la pantalla del Anexo A.1 para el actor del contexto
     */
    public OpinionTecnicaResponseDto pantalla(OpinionTecnicaContexto contexto) {
        var proyecto = contexto.proyecto();
        OpinionTecnica gestion = contexto.gestion();
        boolean emergencia = contexto.esEmergencia();
        Map<String, ComentarioOpinionTecnica> porApartado = comentarios.porApartado(gestion);
        List<DocumentoOpinionTecnica> notasGestion = notas.listar(gestion.getId());

        var dto = new OpinionTecnicaResponseDto(gestion.getId(), proyecto.getId(),
                TipoSolicitudOpinionTecnicaDto.valueOf(gestion.getTipoSolicitud().name()),
                estadoGestion(gestion),
                EstadoProyectoDto.valueOf(proyecto.getEstado().name()),
                emergencia ? TipoFormularioOpinionTecnicaDto.EMERGENCIA : TipoFormularioOpinionTecnicaDto.ESTANDAR,
                Boolean.TRUE.equals(gestion.getPrimeraGestion()),
                encabezado(proyecto, gestion),
                apartados.filas(proyecto.getId(), emergencia, porApartado),
                documentosAnexos.seccion(proyecto.getId(), notasGestion,
                        porApartado.get(ComentarioOpinionTecnica.DOCUMENTOS_ANEXOS)),
                // RN15: cada devolución de la OT deja una gestión "Observado", también las que vencieron.
                Math.toIntExact(opinionesTecnicas.contarDevoluciones(proyecto.getId())),
                gestion.getRevisionConclusiones().tieneVistoBueno(),
                contexto.acciones(),
                contexto.camposEditables());
        if (Boolean.TRUE.equals(gestion.getPrimeraGestion())) {
            dto.setComentariosElegibilidad(ComentariosDgicpOpinionTecnica.seccionElegibilidad(
                    porApartado.get(ComentarioOpinionTecnica.ELEGIBILIDAD)));
        }
        DocumentosAnexosOpinionTecnica.notaOt(notasGestion).ifPresent(dto::setNotaOt);
        dto.setFechaSolicitud(fecha(gestion.getFechaSolicitud()));
        dto.setFechaAjustes(fecha(gestion.getFechaAjustes()));
        dto.setFechaEnvioComentarios(fechaEnvioComentarios(gestion));
        dto.setFechaFinPlazoObservaciones(gestion.getPlazoComentarios().getFechaFin());
        dto.setFechaEmisionOt(gestion.esFavorable() ? fecha(gestion.getFechaEmision()) : null);
        dto.setNumeroNotaOt(gestion.getNumeroNotaOt());
        dto.setConclusiones(gestion.getRevisionConclusiones().getTexto());
        return dto;
    }

    /**
     * @param gestion gestión de OT
     * @return la fila del listado de gestiones del proyecto
     */
    public ResumenOpinionTecnicaDto resumen(OpinionTecnica gestion) {
        var dto = new ResumenOpinionTecnicaDto(gestion.getId(),
                TipoSolicitudOpinionTecnicaDto.valueOf(gestion.getTipoSolicitud().name()), estadoGestion(gestion));
        dto.setFechaSolicitud(fecha(gestion.getFechaSolicitud()));
        dto.setEtapaActual(etiqueta(gestion.getEtapaActual()));
        dto.setEtapaFutura(etiqueta(gestion.getEtapaFutura()));
        dto.setNumeroNotaOt(gestion.getNumeroNotaOt());
        dto.setFechaEmisionOt(gestion.esFavorable() ? fecha(gestion.getFechaEmision()) : null);
        dto.setInversionEstimada(gestion.getInversionEstimada());
        return dto;
    }

    /**
     * @param gestion gestión de OT
     * @return su estado para el cliente
     */
    static EstadoGestionOpinionTecnicaDto estadoGestion(OpinionTecnica gestion) {
        if (gestion.estaArchivada()) {
            return EstadoGestionOpinionTecnicaDto.ARCHIVADA;
        }
        if (gestion.esFavorable()) {
            return EstadoGestionOpinionTecnicaDto.FAVORABLE;
        }
        return gestion.estaObservada() ? EstadoGestionOpinionTecnicaDto.OBSERVADA
                : EstadoGestionOpinionTecnicaDto.EN_CURSO;
    }

    /** @return la fecha de envío de los comentarios DGICP, que inicia el plazo (RN08, RN09) */
    static LocalDate fechaEnvioComentarios(OpinionTecnica gestion) {
        return gestion.getPlazoComentarios().estaIniciado() ? fecha(gestion.getFechaEmision()) : null;
    }

    /** Encabezado de la pantalla (Anexo A.1) y del informe (Anexo A.6). */
    static EncabezadoProyectoOpinionTecnicaDto encabezado(Proyecto proyecto, OpinionTecnica gestion) {
        String unidadEjecutora = proyecto.getUnidadEjecutora() == null ? null
                : proyecto.getUnidadEjecutora().getNombre();
        var dto = new EncabezadoProyectoOpinionTecnicaDto(proyecto.getCup(),
                proyecto.getNombre(), unidadEjecutora);
        dto.setEtapaActual(etiqueta(gestion.getEtapaActual()));
        dto.setEtapaFutura(etiqueta(gestion.getEtapaFutura()));
        return dto;
    }

    private static String etiqueta(TipoEtapaPreinversion etapa) {
        return etapa == null ? null : etapa.getEtiquetaUi();
    }

    static LocalDate fecha(LocalDateTime fecha) {
        return fecha == null ? null : fecha.toLocalDate();
    }
}
