package sv.gob.mh.siip.api_gateway.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FallbackControllerTest {

  @Test
  void fallbackBack_devuelveMensajeDeServicioNoDisponible() {
    String mensaje = new FallbackController().fallbackBack();

    assertThat(mensaje).isEqualTo("""
                Servicio Back no disponible en este momento.
                Por favor intente más tarde.""");
  }
}
