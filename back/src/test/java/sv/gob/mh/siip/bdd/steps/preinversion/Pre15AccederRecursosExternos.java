package sv.gob.mh.siip.bdd.steps.preinversion;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre15AccederRecursosExternos {

    String botonSeleccionado;

    /*
     *Esquema del escenario: Acceder a un recurso externo
     */
    @Dado("que el Técnico URP se encuentra en la pantalla {string} analisis-riesgo-recursos")
    public void queElTécnicoURPSeEncuentraEnLaPantallaAnalisisRiesgoRecursos(String pantalla) {
        assertThat(pantalla).isEqualTo("Análisis de Riesgos");

    }

    @Cuando("hace clic en el botón {string} analisis-riesgo-recursos")
    public void haceClicEnElBotónAnalisisRiesgoRecursos(String valorBoton) {
        this.botonSeleccionado = valorBoton;

    }

    @Entonces("el sistema dirige al enlace externo {string} analisis-riesgo-recursos")
    public void elSistemaDirigeAlEnlaceExternoAnalisisRiesgoRecursos(String rul) {
        switch (this.botonSeleccionado) {
            case "VIGEA" ->
                    assertThat(rul).isEqualTo("https://mapas.marn.gob.sv/VIGEA/entry.aspx");

            case "MAPA DE RIESGO DEPARTAMENTAL" ->
                    assertThat(rul).isEqualTo("https://portafolio.snet.gob.sv/digitalizacion/pdf/spa/doc00073/doc00073.htm");

            case "NOMBRE_TERCER_BOTON" -> // Reemplaza con el texto exacto que manda tu feature
                    assertThat(rul).isEqualTo("https://url-del-tercer-boton.com");

            default ->
                    throw new IllegalArgumentException("Botón no reconocido en la prueba: " + this.botonSeleccionado);
        }
    }
}
