package sv.gob.mh.siip.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;

/**
 * La solicitud está bien formada y el recurso está en un estado válido, pero incumple una regla de
 * negocio que el contrato expone con un código propio (422). Ejemplo: solicitar Viabilidad sin el
 * Documento de Preinversión (CU-PRE-24, RN02).
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ReglaNegocioException extends RuntimeException {

  private final String codigo;
  private final transient List<ErrorDetalleDto> detalles;

  /**
   * @param codigo código estable en UPPER_SNAKE_CASE que el cliente usa para distinguir la regla
   *        incumplida; {@code null} para el código genérico "REGLA_NEGOCIO"
   * @param mensaje mensaje legible para el usuario
   */
  public ReglaNegocioException(String codigo, String mensaje) {
    this(codigo, mensaje, null);
  }

  /**
   * @param codigo código estable en UPPER_SNAKE_CASE de la regla incumplida
   * @param mensaje mensaje legible para el usuario
   * @param detalles elementos del request que incumplen la regla (p. ej. los criterios sin
   *        "Especificar" de CU-PRE-25, RN03); {@code null} si no aplica
   */
  public ReglaNegocioException(String codigo, String mensaje, List<ErrorDetalleDto> detalles) {
    super(mensaje);
    this.codigo = codigo;
    this.detalles = detalles;
  }

  public String getCodigo() {
    return codigo;
  }

  public List<ErrorDetalleDto> getDetalles() {
    return detalles;
  }
}
