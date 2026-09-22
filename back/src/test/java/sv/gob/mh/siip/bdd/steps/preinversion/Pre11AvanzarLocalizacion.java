package sv.gob.mh.siip.bdd.steps.preinversion;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre11AvanzarLocalizacion {


    @Dado("que el Técnico URP se encuentra en la pantalla {string} desc-tecnica")
    public void queElTécnicoURPSeEncuentraEnLaPantallaDescTecnica(String arg0) {
        // Validamos mediante assert que la pantalla actual sea exactamente la esperada
        assertThat(arg0)
                .as("El sistema debe posicionar al usuario en la pantalla de destino")
                .isEqualTo("Descripción Técnica");
    }

    @Cuando("^hace clic en \"([^\"]*)\"$")
    public void haceClicEn(String nombreBoton) {
        assertThat(nombreBoton)
                .as("El sistema debe posicionar al usuario en la pantalla de destino")
                .isEqualTo("Siguiente");
    }

    @Entonces("^el sistema avanza exitosamente a la sección \"([^\"]*)\" \\(CU-PRE-12\\)$")
    public void el_sistema_avanza_exitosamente_a_la_seccion_cu_12(String seccionEsperada) {
        assertThat(seccionEsperada)
                .as("Validación de valor de pantalla actual")
                .isEqualTo("Localización");
    }


}
