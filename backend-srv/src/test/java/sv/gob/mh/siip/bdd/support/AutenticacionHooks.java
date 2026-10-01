package sv.gob.mh.siip.bdd.support;

import io.cucumber.java.After;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * Los steps autentican con {@link AutenticacionDePrueba#autenticar(String)}, que deja el usuario
 * en el SecurityContext del hilo. Se limpia al terminar cada escenario para que ninguno herede el
 * usuario del anterior.
 */
public class AutenticacionHooks {

    @After
    public void limpiarAutenticacion() {
        AutenticacionDePrueba.limpiar();
    }
}
