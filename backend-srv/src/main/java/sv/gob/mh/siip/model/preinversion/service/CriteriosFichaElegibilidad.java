package sv.gob.mh.siip.model.preinversion.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EntradaCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.repository.CriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EntradaCatalogoEspecificarRepository;

/**
 * Lee la configuración vigente de la ficha de Elegibilidad (CU-PRE-25, Anexo A.1): los criterios
 * del catálogo de CU-ADM-02 y las opciones de los catálogos del Anexo C que alimentan la columna
 * "Especificar". El contrato no fija cuántas dimensiones o criterios existen: los define este
 * catálogo.
 */
@Component
@Transactional(readOnly = true)
public class CriteriosFichaElegibilidad {

  private final CriterioElegibilidadRepository criterios;
  private final EntradaCatalogoEspecificarRepository entradas;
  private final EjePlanGobiernoRepository ejesPlanGobierno;

  public CriteriosFichaElegibilidad(CriterioElegibilidadRepository criterios,
      EntradaCatalogoEspecificarRepository entradas,
      EjePlanGobiernoRepository ejesPlanGobierno) {
    this.criterios = criterios;
    this.entradas = entradas;
    this.ejesPlanGobierno = ejesPlanGobierno;
  }

  /**
   * @return los criterios que se califican en la ficha, en el orden de la pantalla, con las opciones
   *         de los catálogos que usan
   */
  public CriteriosVigentesElegibilidad cargar() {
    List<CriterioElegibilidad> vigentes = criterios.findAll().stream()
        .filter(CriteriosVigentesElegibilidad::seMuestraEnFicha)
        .sorted(CriteriosVigentesElegibilidad.ORDEN_FICHA)
        .toList();
    Map<TipoCatalogoEspecificar, Map<String, String>> opciones = new EnumMap<>(TipoCatalogoEspecificar.class);
    vigentes.stream()
        .map(CriterioElegibilidad::getCatalogoEspecificar)
        .filter(Objects::nonNull)
        .distinct()
        .forEach(tipo -> opciones.put(tipo, opcionesDe(tipo)));
    return new CriteriosVigentesElegibilidad(vigentes, opciones);
  }

  /** C.2 sale del catálogo de Ejes del Plan de Gobierno (CU-PRE-03.5); el resto, de "Especificar". */
  private Map<String, String> opcionesDe(TipoCatalogoEspecificar tipo) {
    if (tipo == TipoCatalogoEspecificar.EJE_PLAN_GOBIERNO) {
      return CriteriosVigentesElegibilidad.indexar(ejesPlanGobierno.findByActivoTrueOrderByNombre(),
          EjePlanGobierno::getCodigo, EjePlanGobierno::getNombre);
    }
    return CriteriosVigentesElegibilidad.indexar(entradas.findByTipoOrderByCodigoAsc(tipo),
        EntradaCatalogoEspecificar::getCodigo, EntradaCatalogoEspecificar::getNombre);
  }
}
