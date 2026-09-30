package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.siip.model.preinversion.dto.ActorRetornoDto;
import sv.gob.mh.siip.model.preinversion.dto.EnvioComentariosDgicpResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.FiltroAprobacionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaRetornoAprobacionDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;

/**
 * Resultado del botón "Enviar comentarios" (CU-PRE-26, FA03): a qué se dirigieron los comentarios, que
 * define la ruta de retorno (RN14), y las fechas del plazo de atención (RN08, RN09).
 *
 * @param proyecto si hay comentarios a los campos o a los documentos del proyecto
 * @param elegibilidad si hay comentarios a los criterios de elegibilidad
 * @param fechaEnvio fecha de envío de los comentarios
 * @param fechaFinPlazo fin del plazo de 5 días hábiles para atenderlos
 */
public record EnvioComentariosDgicp(boolean proyecto, boolean elegibilidad, LocalDate fechaEnvio,
        LocalDate fechaFinPlazo) {

    /**
     * @param idGestion identificador de la gestión de OT
     * @param estado estado resultante del proyecto
     * @return la respuesta del contrato
     */
    public EnvioComentariosDgicpResponseDto respuesta(Long idGestion, EstadoProyecto estado) {
        return new EnvioComentariosDgicpResponseDto(idGestion, EstadoProyectoDto.valueOf(estado.name()), fechaEnvio,
                fechaFinPlazo, rutaRetorno());
    }

    /** RN14: a quién vuelve el proyecto y qué filtros recorre según el destino de los comentarios. */
    RutaRetornoAprobacionDto rutaRetorno() {
        List<FiltroAprobacionDto> filtros = new ArrayList<>();
        if (proyecto) {
            filtros.add(FiltroAprobacionDto.VIABILIDAD);
        }
        if (elegibilidad) {
            filtros.add(FiltroAprobacionDto.ELEGIBILIDAD);
        }
        filtros.add(FiltroAprobacionDto.OPINION_TECNICA);
        return new RutaRetornoAprobacionDto(proyecto ? ActorRetornoDto.TECNICO_URP : ActorRetornoDto.VIABILIZADOR,
                filtros);
    }
}
