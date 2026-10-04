package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioApartadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentariosDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentariosElegibilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.enums.ApartadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;

/**
 * Registro de los comentarios DGICP de una gestión de Opinión Técnica (CU-PRE-26, FA02 y FA03): valida
 * la solicitud contra los apartados de la pantalla (Anexo A.1, RN13) y la sección de Elegibilidad (RN 12)
 * y reemplaza los comentarios guardados.
 */
@Component
@Transactional
public class ComentariosDgicpOpinionTecnica {

    public static final String SECCION_ELEGIBILIDAD_NO_DISPONIBLE = "SECCION_ELEGIBILIDAD_NO_DISPONIBLE";
    public static final String COMENTARIOS_DGICP_REQUERIDOS = "COMENTARIOS_DGICP_REQUERIDOS";

    /** Longitud de las columnas de texto de la gestión y de sus comentarios. */
    public static final int LONGITUD_TEXTO = 2000;

    /**
     * A qué se dirigen los comentarios registrados, lo que define la ruta de retorno (RN14).
     *
     * @param proyecto si hay comentarios a los campos o a los documentos del proyecto
     * @param elegibilidad si hay comentarios a los criterios de elegibilidad
     */
    public record Registrados(boolean proyecto, boolean elegibilidad) {

        /** "Comentarios DGICP: campos obligatorios" (Anexo B.1): no se envía una gestión sin comentarios. */
        public void exigirAlguno() {
            if (!proyecto && !elegibilidad) {
                throw new ReglaNegocioException(COMENTARIOS_DGICP_REQUERIDOS,
                        "Debe registrar al menos un comentario DGICP para enviarlo a la institución.");
            }
        }
    }

    private final ComentarioOpinionTecnicaRepository comentarios;

    public ComentariosDgicpOpinionTecnica(ComentarioOpinionTecnicaRepository comentarios) {
        this.comentarios = comentarios;
    }

    /**
     * Reemplaza los comentarios DGICP de la gestión: el formulario se guarda completo y un comentario en
     * blanco se elimina.
     *
     * @param gestion gestión de OT
     * @param emergencia si el proyecto usa el formulario de emergencia (RN13)
     * @param request comentarios DGICP
     * @return a qué se dirigen los comentarios que quedaron registrados
     */
    public Registrados registrar(OpinionTecnica gestion, boolean emergencia, ComentariosDgicpRequestDto request) {
        Map<String, String> nuevos = comentariosDe(gestion, emergencia, request);
        Map<String, ComentarioOpinionTecnica> guardados = porApartado(gestion);
        guardados.forEach((String apartado, ComentarioOpinionTecnica comentario) -> {
            if (!nuevos.containsKey(apartado)) {
                comentarios.delete(comentario);
            }
        });
        nuevos.forEach((String apartado, String texto) -> {
            ComentarioOpinionTecnica comentario = guardados.computeIfAbsent(apartado,
                    codigo -> ComentarioOpinionTecnica.builder().opinionTecnica(gestion).apartado(codigo).build());
            comentario.setComentario(texto);
            comentarios.save(comentario);
        });
        boolean elegibilidad = nuevos.containsKey(ComentarioOpinionTecnica.ELEGIBILIDAD);
        return new Registrados(nuevos.size() > (elegibilidad ? 1 : 0), elegibilidad);
    }

    /**
     * @param gestion gestión de OT
     * @return sus comentarios por apartado, en el orden en que se registraron
     */
    public Map<String, ComentarioOpinionTecnica> porApartado(OpinionTecnica gestion) {
        return comentarios.findByOpinionTecnicaIdOrderByIdAsc(gestion.getId()).stream()
                .collect(Collectors.toMap((ComentarioOpinionTecnica c) -> String.valueOf(c.getApartado()),
                        (ComentarioOpinionTecnica c) -> c, (a, b) -> a, LinkedHashMap::new));
    }

    /**
     * Registra la "Justificación Institución" de un comentario; un texto en blanco la borra.
     *
     * @param comentario comentario DGICP
     * @param texto justificación de la institución
     */
    public void justificar(ComentarioOpinionTecnica comentario, String texto) {
        String limpio = OpinionTecnicaContexto.esVacio(texto) ? null : texto.strip();
        validarLongitud(comentario.getApartado(), limpio);
        comentario.setJustificacionInstitucion(limpio);
        comentarios.save(comentario);
    }

    /**
     * @param comentario comentario DGICP a la Elegibilidad; {@code null} si no hay
     * @return la sección "Comentarios Elegibilidad" de la primera gestión (RN 12)
     */
    static ComentariosElegibilidadDto seccionElegibilidad(ComentarioOpinionTecnica comentario) {
        var dto = new ComentariosElegibilidadDto();
        if (comentario != null) {
            dto.setComentarioDgicpElegibilidad(comentario.getComentario());
            dto.setJustificacionInstitucionElegibilidad(comentario.getJustificacionInstitucion());
        }
        return dto;
    }

    /** Valida que el texto quepa en su columna. */
    static void validarLongitud(String campo, String texto) {
        if (texto != null && texto.length() > LONGITUD_TEXTO) {
            throw invalido(campo, "El texto supera los " + LONGITUD_TEXTO + " caracteres permitidos.");
        }
    }

    static ValidacionNegocioException invalido(String campo, String mensaje) {
        return new ValidacionNegocioException(DocumentosViabilidad.SOLICITUD_INVALIDA, mensaje,
                List.of(detalle(campo, mensaje)));
    }

    /** Valida la solicitud y la reduce a los comentarios con texto, por apartado. */
    private static Map<String, String> comentariosDe(OpinionTecnica gestion, boolean emergencia,
            ComentariosDgicpRequestDto request) {
        Map<String, String> nuevos = new LinkedHashMap<>();
        if (request == null) {
            return nuevos;
        }
        agregarPorApartado(emergencia, request.getComentariosApartados(), nuevos);
        nuevos.put(ComentarioOpinionTecnica.DOCUMENTOS_ANEXOS, request.getComentarioDgicpDocumentosAnexos());
        String elegibilidad = request.getComentarioDgicpElegibilidad();
        if (!OpinionTecnicaContexto.esVacio(elegibilidad) && !Boolean.TRUE.equals(gestion.getPrimeraGestion())) {
            throw new ReglaNegocioException(SECCION_ELEGIBILIDAD_NO_DISPONIBLE,
                    "La sección Comentarios Elegibilidad solo está disponible en la primera gestión de OT.",
                    List.of(detalle("comentarioDgicpElegibilidad", "No permitido en esta gestión.")));
        }
        nuevos.put(ComentarioOpinionTecnica.ELEGIBILIDAD, elegibilidad);
        nuevos.values().removeIf(OpinionTecnicaContexto::esVacio);
        nuevos.replaceAll((String apartado, String texto) -> texto.strip());
        nuevos.forEach(ComentariosDgicpOpinionTecnica::validarLongitud);
        return nuevos;
    }

    /** Agrega los comentarios por apartado, que deben ser de la pantalla y no repetirse. */
    private static void agregarPorApartado(boolean emergencia, List<ComentarioApartadoRequestDto> porApartado,
            Map<String, String> nuevos) {
        if (porApartado == null) {
            return;
        }
        Set<String> validos = ApartadoOpinionTecnica.delFormulario(emergencia).stream()
                .map(ApartadoOpinionTecnica::getCodigo)
                .collect(Collectors.toSet());
        List<ErrorDetalleDto> errores = new ArrayList<>();
        for (ComentarioApartadoRequestDto comentario : porApartado) {
            String codigo = comentario.getApartadoCodigo();
            if (!validos.contains(codigo)) {
                errores.add(detalle("comentariosApartados", "Apartado no reconocido en este formulario: " + codigo));
            } else if (nuevos.containsKey(codigo)) {
                errores.add(detalle("comentariosApartados", "Apartado repetido: " + codigo));
            } else {
                nuevos.put(codigo, comentario.getComentarioDgicp());
            }
        }
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(DocumentosViabilidad.SOLICITUD_INVALIDA,
                    "Los comentarios DGICP no corresponden a los apartados de la pantalla.", errores);
        }
    }

    private static ErrorDetalleDto detalle(String campo, String mensaje) {
        return new ErrorDetalleDto().campo(campo).mensaje(mensaje);
    }
}
