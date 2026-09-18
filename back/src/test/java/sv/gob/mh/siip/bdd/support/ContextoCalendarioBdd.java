package sv.gob.mh.siip.bdd.support;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;

/**
 * Estado compartido entre step classes de CU-ADM-04 (gestion de calendarios) dentro de un mismo
 * escenario BDD: el calendario/periodo que un Dado de una clase crea y que otra clase necesita
 * leer. Bean nuevo por escenario (ScenarioScope de cucumber-spring).
 */
@Component
@ScenarioScope
public class ContextoCalendarioBdd {

    private Calendario calendarioActual;
    private Periodo periodoActual;

    /** Codigo de un calendario que deliberadamente no existe (pasos "que no existe ningún calendario..."). */
    private String codigoCalendarioInexistente;

    /** Codigo de periodo preparado por un Dado compartido para que el Cuando type-specific lo use. */
    private String codigoPeriodoPreparado;

    /** Tipo de periodo (LABORAL/NO_LABORAL) que un Dado type-specific deja preparado para un Cuando compartido. */
    private TipoPeriodo tipoPeriodoPreparado;

    /**
     * Texto de la ultima respuesta de una consulta, para el paso compartido
     * "el sistema responde \"...\"" (CU-ADM-04-05 y CU-ADM-04-06 resuelven al mismo texto de step,
     * aunque provengan de esquemas con placeholders distintos: Cucumber exige una unica definicion).
     */
    private String ultimaRespuestaTexto;

    public Calendario getCalendarioActual() {
        return calendarioActual;
    }

    public void setCalendarioActual(Calendario calendarioActual) {
        this.calendarioActual = calendarioActual;
    }

    public Periodo getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(Periodo periodoActual) {
        this.periodoActual = periodoActual;
    }

    public String getCodigoCalendarioInexistente() {
        return codigoCalendarioInexistente;
    }

    public void setCodigoCalendarioInexistente(String codigoCalendarioInexistente) {
        this.codigoCalendarioInexistente = codigoCalendarioInexistente;
    }

    public String getCodigoPeriodoPreparado() {
        return codigoPeriodoPreparado;
    }

    public void setCodigoPeriodoPreparado(String codigoPeriodoPreparado) {
        this.codigoPeriodoPreparado = codigoPeriodoPreparado;
    }

    public TipoPeriodo getTipoPeriodoPreparado() {
        return tipoPeriodoPreparado;
    }

    public void setTipoPeriodoPreparado(TipoPeriodo tipoPeriodoPreparado) {
        this.tipoPeriodoPreparado = tipoPeriodoPreparado;
    }

    public String getUltimaRespuestaTexto() {
        return ultimaRespuestaTexto;
    }

    public void setUltimaRespuestaTexto(String ultimaRespuestaTexto) {
        this.ultimaRespuestaTexto = ultimaRespuestaTexto;
    }
}
