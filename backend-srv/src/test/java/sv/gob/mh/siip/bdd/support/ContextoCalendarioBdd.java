package sv.gob.mh.siip.bdd.support;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Periodo;

/**
 * Estado compartido entre step classes de CU-ADM-04 (gestion de calendarios) dentro de un mismo
 * escenario BDD: el calendario que un Dado de una clase deja preparado y que otra clase necesita
 * leer (p.ej. el Background comun y la clase especifica de cada .feature), y el ultimo Periodo
 * agregado, consumido por los pasos "Entonces" compartidos de CU-ADM-04-02/03 (agregar periodo
 * LABORAL/NO_LABORAL). Bean nuevo por escenario (ScenarioScope de cucumber-spring).
 */
@Component
@ScenarioScope
public class ContextoCalendarioBdd {

    private Calendario calendarioActual;
    private String codigoCalendarioActualLiteral;
    private Periodo periodoActual;

    public Calendario getCalendarioActual() {
        return calendarioActual;
    }

    public void setCalendarioActual(Calendario calendarioActual) {
        this.calendarioActual = calendarioActual;
    }

    /**
     * Codigo literal del .feature (p.ej. "CAL-2026") con el que se creo {@link #calendarioActual},
     * antes de sufijarlo para evitar colisiones entre escenarios (ver Adm04ComunCalendario). Permite
     * a un step distinguir si un codigo capturado se refiere al calendario del Background o a un
     * codigo deliberadamente distinto (p.ej. "CAL-INEXISTENTE" en un escenario negativo).
     */
    public String getCodigoCalendarioActualLiteral() {
        return codigoCalendarioActualLiteral;
    }

    public void setCodigoCalendarioActualLiteral(String codigoCalendarioActualLiteral) {
        this.codigoCalendarioActualLiteral = codigoCalendarioActualLiteral;
    }

    public Periodo getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(Periodo periodoActual) {
        this.periodoActual = periodoActual;
    }
}
