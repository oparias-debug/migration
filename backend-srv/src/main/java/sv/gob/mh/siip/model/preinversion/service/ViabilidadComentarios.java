package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioCampoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioCampoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.CampoFichaViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;

/**
 * Borrador de comentarios del Viabilizador sobre la revisión en curso (CU-PRE-24): valida los
 * comentarios por campo y las Observaciones Generales/Justificación y los guarda en la revisión.
 */
@Component
@Transactional
public class ViabilidadComentarios {

    /** Longitud máxima de cada comentario y de las observaciones generales (columnas de 2000). */
    public static final int LONGITUD_MAXIMA_TEXTO = 2000;

    private static final String CAMPO_COMENTARIOS = "comentariosViabilizador";

    private final RevisionViabilidadRepository revisionRepository;

    public ViabilidadComentarios(RevisionViabilidadRepository revisionRepository) {
        this.revisionRepository = revisionRepository;
    }

    /**
     * Reemplaza los comentarios y las observaciones de la revisión en curso.
     *
     * @param contexto contexto de la operación del Viabilizador
     * @param request comentarios y observaciones recibidos
     * @return los comentarios guardados y las acciones disponibles
     */
    public GuardarComentariosViabilidadResponseDto guardar(ViabilidadContexto contexto,
            GuardarComentariosViabilidadRequestDto request) {
        RevisionViabilidad revision = contexto.exigirRevisionEnCurso();

        List<ComentarioCampoViabilidad> comentarios = validarComentarios(request.getComentariosViabilizador());
        String observaciones = validarObservaciones(request.getObservacionesGeneralesJustificacion());
        revision.getComentarios().clear();
        revision.getComentarios().addAll(comentarios);
        revision.setObservacionesGenerales(observaciones);
        revisionRepository.save(revision);

        var respuesta = new GuardarComentariosViabilidadResponseDto(
                ViabilidadRespuestas.comentarios(revision), contexto.acciones());
        respuesta.setObservacionesGeneralesJustificacion(revision.getObservacionesGenerales());
        return respuesta;
    }

    /**
     * Convierte los comentarios recibidos: descarta las celdas vacías y rechaza campos repetidos o
     * textos demasiado largos.
     */
    private static List<ComentarioCampoViabilidad> validarComentarios(List<ComentarioCampoViabilidadDto> recibidos) {
        List<ComentarioCampoViabilidad> resultado = new ArrayList<>();
        if (recibidos == null) {
            return resultado;
        }
        Set<CampoFichaViabilidad> vistos = EnumSet.noneOf(CampoFichaViabilidad.class);
        for (ComentarioCampoViabilidadDto recibido : recibidos) {
            CampoFichaViabilidad campo = campoDe(recibido);
            if (!vistos.add(campo)) {
                throw invalido(CAMPO_COMENTARIOS, "El campo " + campo + " tiene más de un comentario.");
            }
            String texto = recibido.getComentario() == null ? "" : recibido.getComentario().trim();
            if (texto.length() > LONGITUD_MAXIMA_TEXTO) {
                throw invalido(CAMPO_COMENTARIOS,
                        "El comentario del campo " + campo + " supera los " + LONGITUD_MAXIMA_TEXTO + " caracteres.");
            }
            if (!texto.isEmpty()) {
                resultado.add(new ComentarioCampoViabilidad(campo, texto));
            }
        }
        return resultado;
    }

    private static CampoFichaViabilidad campoDe(ComentarioCampoViabilidadDto recibido) {
        if (recibido == null) {
            throw invalido(CAMPO_COMENTARIOS, "Cada comentario debe indicar el campo de la ficha.");
        }
        return CampoFichaViabilidad.valueOf(recibido.getCampo().name());
    }

    private static String validarObservaciones(String observaciones) {
        if (ViabilidadContexto.esVacio(observaciones)) {
            return null;
        }
        String texto = observaciones.trim();
        if (texto.length() > LONGITUD_MAXIMA_TEXTO) {
            throw invalido("observacionesGeneralesJustificacion",
                    "Las observaciones generales superan los " + LONGITUD_MAXIMA_TEXTO + " caracteres.");
        }
        return texto;
    }

    private static ValidacionNegocioException invalido(String campo, String mensaje) {
        return new ValidacionNegocioException(DocumentosViabilidad.SOLICITUD_INVALIDA, mensaje,
                List.of(new ErrorDetalleDto().campo(campo).mensaje(mensaje)));
    }
}
