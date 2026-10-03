package sv.gob.mh.siip.bdd.steps.preinversion;

import io.cucumber.java.Scenario;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

/**
 * Pasos con el mismo texto en varias features "de pantalla" (CU-PRE-20, 24, 29, 30, 31, 32 y 33).
 * Los recibe y los delega en la {@link PantallaFront} de la feature que está corriendo.
 *
 * <p>Cada clase de esas features se activa en su propio {@code @Before}:
 * <pre>{@code
 * @Before
 * public void activar(Scenario scenario) {
 *     comun.activarSi(scenario, "CU-PRE-30-programacion-financiera-pap.feature", this);
 * }
 * }</pre>
 */
public class PantallasFrontComun {

    private PantallaFront actual;

    /** Activa {@code pantalla} si el escenario pertenece a {@code archivoFeature}; devuelve si lo activó. */
    public boolean activarSi(Scenario scenario, String archivoFeature, PantallaFront pantalla) {
        if (!esDeLaFeature(scenario, archivoFeature)) {
            return false;
        }
        actual = pantalla;
        return true;
    }

    /** Si el escenario pertenece al archivo {@code .feature} indicado (solo el nombre, sin carpeta). */
    public static boolean esDeLaFeature(Scenario scenario, String archivoFeature) {
        return scenario.getUri().toString().endsWith("/" + archivoFeature);
    }

    /** Si alguna feature de pantalla se activó para el escenario en curso. */
    public boolean hayActiva() {
        return actual != null;
    }

    private PantallaFront actual() {
        if (actual == null) {
            throw new IllegalStateException("Ninguna feature de pantalla se activó para este escenario "
                    + "(falta su @Before con PantallasFrontComun#activarSi).");
        }
        return actual;
    }

    @Dado("que el Técnico URP se encuentra en la pantalla {string} \\(Anexo A.1)")
    public void tecnicoUrpEnPantalla(String pantalla) {
        actual().tecnicoUrpEnPantalla(pantalla);
    }

    /**
     * "hace clic en {string}": el texto lo define {@code Pre11AvanzarLocalizacion} (con una regex), que
     * delega aquí cuando hay una feature de pantalla activa.
     */
    public void haceClic(String boton) {
        actual().haceClic(boton);
    }

    /**
     * "el Técnico URP hace clic en {string}": el texto lo define {@code Pre01RegistrarNuevoProyecto}
     * (no-op para las features viejas), que delega aquí cuando hay una feature de pantalla activa.
     */
    public void tecnicoUrpHaceClic(String boton) {
        actual().tecnicoUrpHaceClic(boton);
    }

    /**
     * "muestra el botón {string}": el texto lo define {@code Pre265Priorizacion}, que delega aquí
     * cuando hay una feature de pantalla activa.
     */
    public void muestraBoton(String boton) {
        actual().muestraBoton(boton);
    }

    @Cuando("el actor hace clic en {string}")
    public void actorHaceClic(String boton) {
        actual().actorHaceClic(boton);
    }

    @Cuando("confirma la acción")
    public void confirmaAccion() {
        actual().confirmaAccion();
    }

    @Dado("que la consulta falla")
    public void consultaFalla() {
        actual().consultaFalla();
    }

    @Cuando("el Técnico URP hace clic en el CUP de un estudio")
    public void clicCupDeEstudio() {
        actual().clicCupDeEstudio();
    }

    @Entonces("el sistema guarda la programación")
    public void guardaProgramacion() {
        actual().guardaProgramacion();
    }

    @Entonces("el sistema guarda el avance")
    public void guardaAvance() {
        actual().guardaAvance();
    }

    @Entonces("el sistema muestra una fila por etapa y fuente de financiamiento")
    public void filaPorEtapaYFuente() {
        actual().filaPorEtapaYFuente();
    }

    @Entonces("el sistema muestra una fila por estudio y etapa")
    public void filaPorEstudioYEtapa() {
        actual().filaPorEstudioYEtapa();
    }

    @Entonces("el sistema genera el reporte del año y el cuatrimestre en pantalla")
    public void reporteDelAnioYCuatrimestre() {
        actual().reporteDelAnioYCuatrimestre();
    }

    @Cuando("escribe sus observaciones y hace clic en {string}")
    public void escribeObservacionesYClic(String boton) {
        actual().escribeObservacionesYClic(boton);
    }

    @Dado("que quien entra es Técnico PRE o Coordinador PRE")
    public void entraTecnicoPreOCoordinadorPre() {
        actual().entraTecnicoPreOCoordinadorPre();
    }
}
