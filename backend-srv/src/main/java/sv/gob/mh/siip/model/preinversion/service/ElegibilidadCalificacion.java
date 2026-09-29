package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.CalificacionCriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaCriterioElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionCriterioElegibilidadRepository;

/**
 * Guardado de la calificación de criterios por el Viabilizador (CU-PRE-25, HU-PRE-25-01; FB1 pasos
 * 4–5, FA01): valida la solicitud contra los criterios vigentes y reemplaza la calificación del
 * proyecto. Si algo no es válido no se guarda nada.
 */
@Component
@Transactional
public class ElegibilidadCalificacion {

  public static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";
  public static final String ESPECIFICAR_INCOMPLETO = "ESPECIFICAR_INCOMPLETO";

  /** Mensaje literal de RN03. */
  public static final String MENSAJE_ESPECIFICAR_INCOMPLETO =
      "Se debe completar la información de la columna 'Especificar' para los criterios seleccionados";

  /** Longitud máxima del "Especificar" de texto libre (columna de 2000). */
  public static final int LONGITUD_MAXIMA_TEXTO = 2000;

  private final CalificacionCriterioElegibilidadRepository calificaciones;
  private final CriteriosFichaElegibilidad criterios;

  public ElegibilidadCalificacion(CalificacionCriterioElegibilidadRepository calificaciones,
      CriteriosFichaElegibilidad criterios) {
    this.calificaciones = calificaciones;
    this.criterios = criterios;
  }

  /**
   * Valida y guarda la calificación enviada, que reemplaza a la vigente (RN01, RN03, RN08).
   *
   * @param contexto contexto de la operación del Viabilizador
   * @param request respuestas de "¿Aplica?" y "Especificar"
   * @return la calificación guardada, en el orden de la ficha
   */
  public List<CalificacionCriterioElegibilidad> guardar(ElegibilidadContexto contexto,
      GuardarCalificacionElegibilidadRequestDto request) {
    contexto.exigirHabilitada();
    CriteriosVigentesElegibilidad vigentes = criterios.cargar();
    Map<Long, CriterioElegibilidad> porId = vigentes.porId();
    List<RespuestaCriterioElegibilidadRequestDto> respuestas =
        request.getRespuestas() == null ? List.of() : request.getRespuestas();

    validarEstructura(respuestas, porId, vigentes);
    validarEspecificar(respuestas, porId);

    Long idProyecto = contexto.proyecto().getId();
    Map<Long, CalificacionCriterioElegibilidad> anteriores = new HashMap<>();
    calificaciones.findByProyectoId(idProyecto).forEach(c -> anteriores.put(c.getCriterio().getId(), c));

    List<CalificacionCriterioElegibilidad> guardadas = new ArrayList<>();
    for (RespuestaCriterioElegibilidadRequestDto respuesta : respuestas) {
      CriterioElegibilidad criterio = porId.get(respuesta.getCriterioId());
      CalificacionCriterioElegibilidad calificacion = anteriores.remove(criterio.getId());
      if (calificacion == null) {
        calificacion = CalificacionCriterioElegibilidad.builder()
            .proyecto(contexto.proyecto())
            .criterio(criterio)
            .build();
      }
      aplicar(calificacion, criterio, respuesta);
      guardadas.add(calificaciones.save(calificacion));
    }
    // Los criterios que ya no vienen en la solicitud quedan sin calificar.
    calificaciones.deleteAll(anteriores.values());

    return guardadas.stream()
        .sorted((a, b) -> CriteriosVigentesElegibilidad.ORDEN_FICHA.compare(a.getCriterio(), b.getCriterio()))
        .toList();
  }

  /** Copia la respuesta a la calificación; "Especificar" solo se conserva si el criterio aplica. */
  private static void aplicar(CalificacionCriterioElegibilidad calificacion, CriterioElegibilidad criterio,
      RespuestaCriterioElegibilidadRequestDto respuesta) {
    boolean aplica = Boolean.TRUE.equals(respuesta.getAplica());
    calificacion.setAplica(aplica);
    boolean deCatalogo = CriteriosVigentesElegibilidad.esDeCatalogo(criterio);
    String texto = respuesta.getEspecificarTexto();
    calificacion.setEspecificarTexto(aplica && !deCatalogo && texto != null ? texto.strip() : null);
    calificacion.getCodigosOpcion().clear();
    if (aplica && deCatalogo) {
      calificacion.getCodigosOpcion().addAll(codigosSeleccionados(respuesta));
    }
  }

  /**
   * 400 {@code SOLICITUD_INVALIDA}: criterios que no están en la ficha o repetidos, opciones que no
   * pertenecen al catálogo del criterio, más de una opción cuando el criterio no lo admite (RN08) o
   * texto demasiado largo.
   */
  private static void validarEstructura(List<RespuestaCriterioElegibilidadRequestDto> respuestas,
      Map<Long, CriterioElegibilidad> porId, CriteriosVigentesElegibilidad vigentes) {
    List<ErrorDetalleDto> detalles = new ArrayList<>();
    Set<Long> vistos = new HashSet<>();
    for (RespuestaCriterioElegibilidadRequestDto respuesta : respuestas) {
      CriterioElegibilidad criterio = porId.get(respuesta.getCriterioId());
      if (criterio == null) {
        detalles.add(detalle(respuesta, "El criterio no forma parte de la ficha de Elegibilidad."));
      } else if (!vistos.add(criterio.getId())) {
        detalles.add(detalle(respuesta, "El criterio se repite en la solicitud."));
      } else {
        validarEspecificarRecibido(respuesta, criterio, vigentes, detalles);
      }
    }
    if (!detalles.isEmpty()) {
      throw new ValidacionNegocioException(SOLICITUD_INVALIDA,
          "La calificación enviada contiene criterios u opciones que no son válidos.", detalles);
    }
  }

  private static void validarEspecificarRecibido(RespuestaCriterioElegibilidadRequestDto respuesta,
      CriterioElegibilidad criterio, CriteriosVigentesElegibilidad vigentes, List<ErrorDetalleDto> detalles) {
    // "Especificar" solo se conserva cuando el criterio aplica; si no, lo recibido se descarta.
    if (!Boolean.TRUE.equals(respuesta.getAplica())) {
      return;
    }
    if (!CriteriosVigentesElegibilidad.esDeCatalogo(criterio)) {
      String texto = respuesta.getEspecificarTexto();
      if (texto != null && texto.strip().length() > LONGITUD_MAXIMA_TEXTO) {
        detalles.add(detalle(respuesta,
            "\"Especificar\" supera la longitud máxima de " + LONGITUD_MAXIMA_TEXTO + " caracteres."));
      }
      return;
    }
    List<String> codigos = codigosSeleccionados(respuesta);
    Map<String, String> opciones = vigentes.opciones(criterio);
    codigos.stream()
        .filter(codigo -> !opciones.containsKey(codigo))
        .forEach(codigo -> detalles.add(detalle(respuesta,
            "La opción '" + codigo + "' no pertenece al catálogo del criterio.")));
    if (codigos.size() > 1 && !Boolean.TRUE.equals(criterio.getPermiteSeleccionMultiple())) {
      detalles.add(detalle(respuesta, "El criterio admite una sola opción en \"Especificar\"."));
    }
  }

  /** 422 {@code ESPECIFICAR_INCOMPLETO}: criterios seleccionados sin "Especificar" (RN03). */
  private static void validarEspecificar(List<RespuestaCriterioElegibilidadRequestDto> respuestas,
      Map<Long, CriterioElegibilidad> porId) {
    List<ErrorDetalleDto> detalles = respuestas.stream()
        .filter(r -> Boolean.TRUE.equals(r.getAplica()))
        .filter(r -> !especificarCompleto(r, porId.get(r.getCriterioId())))
        .map(r -> detalle(r, "Criterio seleccionado sin información en 'Especificar'."))
        .toList();
    if (!detalles.isEmpty()) {
      throw new ReglaNegocioException(ESPECIFICAR_INCOMPLETO, MENSAJE_ESPECIFICAR_INCOMPLETO, detalles);
    }
  }

  private static boolean especificarCompleto(RespuestaCriterioElegibilidadRequestDto respuesta,
      CriterioElegibilidad criterio) {
    if (CriteriosVigentesElegibilidad.esDeCatalogo(criterio)) {
      return !codigosSeleccionados(respuesta).isEmpty();
    }
    return !ViabilidadContexto.esVacio(respuesta.getEspecificarTexto());
  }

  /** Códigos no vacíos y sin repetir, en el orden recibido. */
  private static List<String> codigosSeleccionados(RespuestaCriterioElegibilidadRequestDto respuesta) {
    if (respuesta.getEspecificarCodigosOpcion() == null) {
      return List.of();
    }
    return respuesta.getEspecificarCodigosOpcion().stream()
        .filter(codigo -> !ViabilidadContexto.esVacio(codigo))
        .map(String::strip)
        .distinct()
        .toList();
  }

  private static ErrorDetalleDto detalle(RespuestaCriterioElegibilidadRequestDto respuesta, String mensaje) {
    return new ErrorDetalleDto().campo("respuestas[criterioId=" + respuesta.getCriterioId() + "]")
        .mensaje(mensaje);
  }
}
