package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.CalificacionCriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.CriterioElegibilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.DimensionElegibilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoEspecificarDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;

class ElegibilidadRespuestasTest {

  private static final Proyecto PROYECTO = Proyecto.builder().id(1L).estado(EstadoProyecto.VIABLE).build();

  @Test
  void ficha_agrupaPorDimensionYCompletaLosValoresQueFaltanEnElCatalogo() {
    CriterioElegibilidad sinNumero = CriterioElegibilidad.builder().id(1L).codigo("A").dimension("Sin número")
        .criterio("Criterio A").tipoEspecificar(TipoEspecificar.TEXTO_LIBRE).build();
    CriterioElegibilidad social = CriterioElegibilidad.builder().id(2L).codigo("B").dimension("Aspectos Sociales")
        .criterio("Criterio B").pregunta("¿A qué grupos beneficia?").tipoEspecificar(TipoEspecificar.CATALOGO)
        .catalogoEspecificar(TipoCatalogoEspecificar.GRUPO_POBLACIONAL_VULNERABLE).build();
    Map<TipoCatalogoEspecificar, Map<String, String>> opciones = new EnumMap<>(TipoCatalogoEspecificar.class);
    opciones.put(TipoCatalogoEspecificar.GRUPO_POBLACIONAL_VULNERABLE, Map.of("GPV-01", "Niñez"));
    CriteriosVigentesElegibilidad vigentes = new CriteriosVigentesElegibilidad(List.of(sinNumero, social), opciones);
    CalificacionCriterioElegibilidad calificacion = CalificacionCriterioElegibilidad.builder().criterio(social)
        .aplica(true).codigosOpcion(List.of("GPV-01", "RETIRADA")).build();

    FichaElegibilidadResponseDto ficha = ElegibilidadRespuestas.ficha(contexto(), vigentes, List.of(calificacion));

    assertThat(ficha.getEstadoProyecto()).isEqualTo("Proyecto viable");
    assertThat(ficha.getDimensiones()).extracting(DimensionElegibilidadDto::getNumero).containsExactly(1, 2);
    CriterioElegibilidadDto textoLibre = ficha.getDimensiones().get(0).getCriterios().get(0);
    assertThat(textoLibre.getPregunta()).isEqualTo("Criterio A");
    assertThat(textoLibre.getTipoEspecificar()).isEqualTo(TipoEspecificarDto.TEXTO_LIBRE);
    assertThat(textoLibre.getRespuesta()).isNull();
    CriterioElegibilidadDto catalogo = ficha.getDimensiones().get(1).getCriterios().get(0);
    assertThat(catalogo.getCodigoCatalogo()).isNull();
    assertThat(catalogo.getSeleccionMultiple()).isFalse();
    // Una opción que ya no está en el catálogo se muestra con su código.
    assertThat(catalogo.getRespuesta().getEspecificarOpciones())
        .extracting(o -> o.getNombre()).containsExactly("Niñez", "RETIRADA");
  }

  @Test
  void opciones_vaciasParaCriteriosSinCatalogoOConCatalogoSinEntradas() {
    CriterioElegibilidad sinCatalogo = CriterioElegibilidad.builder().tipoEspecificar(TipoEspecificar.TEXTO_LIBRE)
        .build();
    CriterioElegibilidad sinEntradas = CriterioElegibilidad.builder().tipoEspecificar(TipoEspecificar.CATALOGO)
        .catalogoEspecificar(TipoCatalogoEspecificar.MEDIDA_GRD).build();
    CriteriosVigentesElegibilidad vigentes = new CriteriosVigentesElegibilidad(List.of(), Map.of());

    assertThat(vigentes.opciones(sinCatalogo)).isEmpty();
    assertThat(vigentes.opciones(sinEntradas)).isEmpty();
    assertThat(CriteriosVigentesElegibilidad.seMuestraEnFicha(
        CriterioElegibilidad.builder().tipoEspecificar(TipoEspecificar.SI_NO).build())).isFalse();
  }

  private static ElegibilidadContexto contexto() {
    return new ElegibilidadContexto(Usuario.builder().rol(RolUsuario.VIABILIZADOR).build(), PROYECTO, null, true);
  }
}
