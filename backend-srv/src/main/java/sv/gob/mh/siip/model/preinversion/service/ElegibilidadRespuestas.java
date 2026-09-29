package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sv.gob.mh.siip.model.preinversion.domain.CalificacionCriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.dto.CriterioElegibilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.DimensionElegibilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.OpcionCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaCriterioElegibilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoEspecificarDto;

/**
 * Conversión a DTO de la ficha de Elegibilidad (CU-PRE-25, Anexo A.1): dimensiones, criterios con
 * sus opciones y la calificación guardada.
 */
final class ElegibilidadRespuestas {

  private ElegibilidadRespuestas() {
  }

  /**
   * Arma la ficha agrupando los criterios vigentes por dimensión, en el orden de la pantalla.
   *
   * @param contexto contexto de la operación
   * @param vigentes criterios vigentes y opciones de sus catálogos
   * @param calificaciones calificación guardada del proyecto
   * @return la ficha
   */
  static FichaElegibilidadResponseDto ficha(ElegibilidadContexto contexto, CriteriosVigentesElegibilidad vigentes,
      List<CalificacionCriterioElegibilidad> calificaciones) {
    Map<Long, CalificacionCriterioElegibilidad> porCriterio = new LinkedHashMap<>();
    calificaciones.forEach(c -> porCriterio.put(c.getCriterio().getId(), c));

    Map<String, DimensionElegibilidadDto> dimensiones = new LinkedHashMap<>();
    for (CriterioElegibilidad criterio : vigentes.criterios()) {
      DimensionElegibilidadDto dimension = dimensiones.computeIfAbsent(criterio.getDimension(),
          nombre -> new DimensionElegibilidadDto(numeroDimension(criterio, dimensiones.size()), nombre,
              new ArrayList<>()));
      dimension.addCriteriosItem(criterio(criterio, vigentes, porCriterio.get(criterio.getId())));
    }
    return new FichaElegibilidadResponseDto(contexto.proyecto().getId(),
        contexto.proyecto().getEstado().getEtiquetaUi(), new ArrayList<>(dimensiones.values()),
        contexto.acciones());
  }

  /**
   * @param calificacion calificación guardada de un criterio
   * @param vigentes criterios vigentes, para resolver el nombre de cada opción
   * @return la respuesta registrada
   */
  static RespuestaCriterioElegibilidadDto respuesta(CalificacionCriterioElegibilidad calificacion,
      CriteriosVigentesElegibilidad vigentes) {
    Map<String, String> opciones = vigentes.opciones(calificacion.getCriterio());
    List<OpcionCatalogoDto> seleccionadas = calificacion.getCodigosOpcion().stream()
        .map(codigo -> new OpcionCatalogoDto(codigo, opciones.getOrDefault(codigo, codigo)))
        .toList();
    RespuestaCriterioElegibilidadDto dto = new RespuestaCriterioElegibilidadDto(calificacion.getCriterio().getId(),
        calificacion.getAplica(), new ArrayList<>(seleccionadas));
    dto.setEspecificarTexto(calificacion.getEspecificarTexto());
    return dto;
  }

  private static CriterioElegibilidadDto criterio(CriterioElegibilidad criterio, CriteriosVigentesElegibilidad vigentes,
      CalificacionCriterioElegibilidad calificacion) {
    boolean deCatalogo = CriteriosVigentesElegibilidad.esDeCatalogo(criterio);
    CriterioElegibilidadDto dto = new CriterioElegibilidadDto(criterio.getId(), criterio.getCriterio(),
        criterio.getPregunta() == null ? criterio.getCriterio() : criterio.getPregunta(),
        deCatalogo ? TipoEspecificarDto.LISTADO_CATALOGO : TipoEspecificarDto.TEXTO_LIBRE,
        deCatalogo && Boolean.TRUE.equals(criterio.getPermiteSeleccionMultiple()));
    if (deCatalogo && criterio.getCatalogoEspecificar() != null) {
      dto.setCodigoCatalogo(criterio.getCatalogoEspecificar().getCodigoAnexo());
    }
    vigentes.opciones(criterio).forEach((codigo, nombre) -> dto.addOpcionesItem(new OpcionCatalogoDto(codigo, nombre)));
    dto.setRespuesta(calificacion == null ? null : respuesta(calificacion, vigentes));
    return dto;
  }

  /** Número configurado de la dimensión; si falta, su posición en la ficha. */
  private static int numeroDimension(CriterioElegibilidad criterio, int dimensionesPrevias) {
    return criterio.getNumeroDimension() != null ? criterio.getNumeroDimension() : (dimensionesPrevias + 1);
  }
}
