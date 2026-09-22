package sv.gob.mh.siip.bdd.steps.preinversion;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre12AvanzarTamano {


    @Dado("que el Técnico URP se encuentra en la pantalla {string} localizacion")
    public void queElTécnicoURPSeEncuentraEnLaPantallaLocalizacion(String pantalla) {
        assertThat(pantalla).isEqualTo("Localización");

    }

    @Cuando("hace clic en el botón {string} localizacion")
    public void haceClicEnElBotónLocalizacion(String boton) {
        System.out.println("[Front] usuario hace clic en el boton " + boton);
        assertThat(boton).isEqualTo("Siguiente");
    }

    @Entonces("el sistema avanza a la sección {string} localizacion")
    public void elSistemaAvanzaALaSecciónLocalizacion(String seccion) {
        System.out.println("[Front] usuario avanza a la seccion " + seccion);
        assertThat(seccion).isEqualTo("Tamaño");
    }
}
