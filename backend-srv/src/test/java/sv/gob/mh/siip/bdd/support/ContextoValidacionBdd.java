package sv.gob.mh.siip.bdd.support;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;

/**
 * Estado compartido entre step classes para el paso "el sistema muestra el mensaje {string}"
 * (definido una unica vez en Pre01RegistrarNuevoProyecto, ya que Cucumber exige una unica
 * definicion por texto): guarda la ultima excepcion de negocio capturada por otra clase de steps
 * (p.ej. Pre35RegistrarFichaEmergencia) para que ese paso compartido pueda verificarla contra el
 * mensaje esperado sin acoplarse a los detalles de cada caso de uso. Bean nuevo por escenario
 * (ScenarioScope de cucumber-spring), asi que no hace falta limpiarlo entre escenarios.
 */
@Component
@ScenarioScope
public class ContextoValidacionBdd {

    private RuntimeException ultimaExcepcion;

    public RuntimeException getUltimaExcepcion() {
        return ultimaExcepcion;
    }

    public void setUltimaExcepcion(RuntimeException ultimaExcepcion) {
        this.ultimaExcepcion = ultimaExcepcion;
    }
}
