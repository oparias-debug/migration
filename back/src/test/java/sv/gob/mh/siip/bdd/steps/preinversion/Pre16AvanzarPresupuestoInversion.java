package sv.gob.mh.siip.bdd.steps.preinversion;


import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

/**
 * @author Luis Medrano
 * @version 1.0
 */
public class Pre16AvanzarPresupuestoInversion {
    @Dado("que el Técnico URP se encuentra en la pantalla {string} avanzar-analisis-legal")
    public void queElTécnicoURPSeEncuentraEnLaPantallaAvanzarAnalisisLegal(String arg0) {
        // front-end, nada que hacer en back-end

    }

    @Cuando("hace clic en el botón {string} avanzar-analisis-legal")
    public void haceClicEnElBotónAvanzarAnalisisLegal(String arg0) {
        // front-end, nada que hacer en back-end
    }

    @Entonces("el sistema avanza a la sección {string} \\(CU-PRE{int}) avanzar-analisis-legal")
    public void elSistemaAvanzaALaSecciónCUPREAvanzarAnalisisLegal(String arg0, int arg1) {
        // front-end, nada que hacer en back-end
    }
}
