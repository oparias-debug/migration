package sv.gob.mh.siip.model.preinversion.enums;

/**
 * A cuál catálogo de "Especificar" pertenece un criterio de elegibilidad con
 * {@code tipoEspecificar = CATALOGO} (CU-PRE-25). {@code EJE_PLAN_GOBIERNO} (catálogo C.2) es el
 * único valor que no se resuelve con {@code GET /catalogos/especificar-elegibilidad}: se resuelve
 * con {@code GET /catalogos/ejes-plan-gobierno} (CU-PRE-03.5).
 */
public enum TipoCatalogoEspecificar {
  ODS("C.1"),
  EJE_PLAN_GOBIERNO("C.2"),
  COMPONENTE_MEDIO_AMBIENTE("C.3"),
  MEDIDA_GRD("C.4"),
  MEDIDA_ACC("C.5"),
  GRUPO_POBLACIONAL_VULNERABLE(null),
  MEJORA_CALIDAD_VIDA(null);

  private final String codigoAnexo;

  TipoCatalogoEspecificar(String codigoAnexo) {
    this.codigoAnexo = codigoAnexo;
  }

  /**
   * Código del catálogo en el Anexo C de CU-PRE-25.
   *
   * @return el código (p. ej. "C.1"), o {@code null} para los catálogos de "Aspectos Sociales", que el
   *         documento no numera
   */
  public String getCodigoAnexo() {
    return codigoAnexo;
  }
}
