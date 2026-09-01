package sv.gob.mh.siip.bdd.support;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/**
 * Estado compartido entre step classes de preinversion dentro de un mismo escenario BDD, para los
 * pocos casos donde un Dado/Cuando de una clase crea un Proyecto que otra clase necesita leer
 * (p.ej. "un proyecto en estado {string}" vive en Pre01ResponderObservaciones porque tambien lo
 * usa CU-PRE-01-solicitar-cup.feature). Bean nuevo por escenario (ScenarioScope de cucumber-spring),
 * asi que no hace falta limpiarlo entre escenarios.
 */
@Component
@ScenarioScope
public class ContextoProyectoBdd {

    private Proyecto proyectoActual;

    public Proyecto getProyectoActual() {
        return proyectoActual;
    }

    public void setProyectoActual(Proyecto proyectoActual) {
        this.proyectoActual = proyectoActual;
    }
}
