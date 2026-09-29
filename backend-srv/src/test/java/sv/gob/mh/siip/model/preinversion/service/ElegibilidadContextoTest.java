package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AccionesDisponiblesElegibilidadDto;

class ElegibilidadContextoTest {

  private static final Proyecto PROYECTO = Proyecto.builder().id(1L).build();
  private static final Elegibilidad EMISION = Elegibilidad.builder().id(5L).fechaEvaluacion(LocalDateTime.now())
      .build();

  @Test
  void acciones_soloParaElViabilizadorConLaFichaHabilitada() {
    AccionesDisponiblesElegibilidadDto viabilizador = contexto(RolUsuario.VIABILIZADOR, null, true).acciones();
    AccionesDisponiblesElegibilidadDto tecnicoPre = contexto(RolUsuario.TECNICO_PRE, null, true).acciones();
    AccionesDisponiblesElegibilidadDto bloqueada = contexto(RolUsuario.VIABILIZADOR, EMISION, false).acciones();

    assertThat(viabilizador.getGuardarCalificacion()).isTrue();
    assertThat(viabilizador.getEmitirElegibilidad()).isTrue();
    assertThat(tecnicoPre.getGuardarCalificacion()).isFalse();
    assertThat(bloqueada.getEmitirElegibilidad()).isFalse();
  }

  @Test
  void esReemision_cuandoYaHayUnaEmision() {
    assertThat(contexto(RolUsuario.VIABILIZADOR, null, true).esReemision()).isFalse();
    assertThat(contexto(RolUsuario.VIABILIZADOR, EMISION, true).esReemision()).isTrue();
  }

  @Test
  void exigirHabilitada_distingueElMotivoDelBloqueo() {
    ElegibilidadContexto habilitada = contexto(RolUsuario.VIABILIZADOR, null, true);
    ElegibilidadContexto noViable = contexto(RolUsuario.VIABILIZADOR, null, false);
    ElegibilidadContexto yaEmitida = contexto(RolUsuario.VIABILIZADOR, EMISION, false);

    assertThatCode(habilitada::exigirHabilitada).doesNotThrowAnyException();
    assertThatThrownBy(noViable::exigirHabilitada)
        .isInstanceOf(ConflictoEstadoException.class)
        .hasMessageContaining("Proyecto viable")
        .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
        .isEqualTo(ElegibilidadContexto.FICHA_ELEGIBILIDAD_DESHABILITADA);
    assertThatThrownBy(yaEmitida::exigirHabilitada)
        .isInstanceOf(ConflictoEstadoException.class)
        .hasMessageContaining("Opinión Técnica envía comentarios");
  }

  private static ElegibilidadContexto contexto(RolUsuario rol, Elegibilidad ultima, boolean habilitada) {
    return new ElegibilidadContexto(Usuario.builder().id(9L).rol(rol).build(), PROYECTO, ultima, habilitada);
  }
}
