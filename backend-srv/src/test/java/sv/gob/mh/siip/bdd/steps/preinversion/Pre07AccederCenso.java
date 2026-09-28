package sv.gob.mh.siip.bdd.steps.preinversion;

import org.springframework.web.context.request.RequestContextHolder;

import io.cucumber.java.es.Entonces;

/**
 * CU-PRE-07-acceder-censo.feature. El paso de las Antecedentes ("que el actor se encuentra en la
 * pantalla {string}") es texto identico al de CU-PRE-01-ver-registro.feature (Cucumber exige una
 * unica definicion por texto, mismo criterio que Pre02Bandeja): se reutiliza tal cual desde
 * {@link Pre01VerRegistro}, ya que su implementacion no depende del valor de {@code pantalla} (solo
 * autentica un actor generico). El clic en "Censo" reutiliza el paso generico "hace clic en el
 * botón {string}" (no-op).
 */
public class Pre07AccederCenso {

    @Entonces("el sistema muestra la página del Geoportal del Banco Central de Reserva con el {string}")
    public void el_sistema_muestra_la_pagina_del_geoportal_bcr(String censo) {
        // RN03: navegacion externa de UI pura hacia el Geoportal del BCR, sin estado de backend
        // que verificar.
        RequestContextHolder.resetRequestAttributes();
    }
}
