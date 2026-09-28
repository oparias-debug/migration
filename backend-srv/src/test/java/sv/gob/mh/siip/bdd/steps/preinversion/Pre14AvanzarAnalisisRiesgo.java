package sv.gob.mh.siip.bdd.steps.preinversion;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre14AvanzarAnalisisRiesgo {
    @Dado("que el Técnico URP se encuentra en la pantalla {string} avanzar-análisis-ambiental")
    public void queElTécnicoURPSeEncuentraEnLaPantallaAvanzarAnálisisAmbiental(String arg0) {
        //parte del front-end, nada que hacer en back-end
    }

    @Cuando("hace clic en el botón {string} avanzar-análisis-ambiental")
    public void haceClicEnElBotónAvanzarAnálisisAmbiental(String arg0) {
        //parte del front-end, nada que hacer en back-end
    }

    @Entonces("el sistema avanza a la sección {string} \\(CU-PRE{int}) avanzar-análisis-ambiental")
    public void elSistemaAvanzaALaSecciónCUPREAvanzarAnálisisAmbiental(String arg0, int arg1) {
        //parte del front-end, nada que hacer en back-end
    }
}
