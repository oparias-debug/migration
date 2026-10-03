package sv.gob.mh.siip.bdd.steps.preinversion;

/**
 * Feature "de pantalla" (las que agregó el front, con pasos como "hace clic en ...") que comparte
 * textos de paso con otras features del mismo tipo. Cucumber admite una sola definición por texto,
 * así que {@link PantallasFrontComun} recibe esos pasos y los delega en la implementación de la
 * feature que está corriendo, que se activa con {@link PantallasFrontComun#activarSi}.
 *
 * <p>Cada método corresponde a un texto de paso compartido. Una feature implementa solo los que
 * usa; los demás fallan con un mensaje que dice qué paso falta.
 */
public interface PantallaFront {

    /** "que el Técnico URP se encuentra en la pantalla {string} (Anexo A.1)". */
    default void tecnicoUrpEnPantalla(String pantalla) {
        throw noAplica("que el Técnico URP se encuentra en la pantalla \"" + pantalla + "\" (Anexo A.1)");
    }

    /** "hace clic en {string}" (el texto lo define {@code Pre11AvanzarLocalizacion}, que delega aquí). */
    default void haceClic(String boton) {
        throw noAplica("hace clic en \"" + boton + "\"");
    }

    /** "el Técnico URP hace clic en {string}". */
    default void tecnicoUrpHaceClic(String boton) {
        throw noAplica("el Técnico URP hace clic en \"" + boton + "\"");
    }

    /** "el actor hace clic en {string}". */
    default void actorHaceClic(String boton) {
        throw noAplica("el actor hace clic en \"" + boton + "\"");
    }

    /** "confirma la acción". */
    default void confirmaAccion() {
        throw noAplica("confirma la acción");
    }

    /** "que la consulta falla". */
    default void consultaFalla() {
        throw noAplica("que la consulta falla");
    }

    /** "el Técnico URP hace clic en el CUP de un estudio". */
    default void clicCupDeEstudio() {
        throw noAplica("el Técnico URP hace clic en el CUP de un estudio");
    }

    /** "el sistema guarda la programación". */
    default void guardaProgramacion() {
        throw noAplica("el sistema guarda la programación");
    }

    /** "el sistema guarda el avance". */
    default void guardaAvance() {
        throw noAplica("el sistema guarda el avance");
    }

    /** "el sistema muestra una fila por etapa y fuente de financiamiento". */
    default void filaPorEtapaYFuente() {
        throw noAplica("el sistema muestra una fila por etapa y fuente de financiamiento");
    }

    /** "el sistema muestra una fila por estudio y etapa". */
    default void filaPorEstudioYEtapa() {
        throw noAplica("el sistema muestra una fila por estudio y etapa");
    }

    /** "el sistema genera el reporte del año y el cuatrimestre en pantalla". */
    default void reporteDelAnioYCuatrimestre() {
        throw noAplica("el sistema genera el reporte del año y el cuatrimestre en pantalla");
    }

    /** "escribe sus observaciones y hace clic en {string}". */
    default void escribeObservacionesYClic(String boton) {
        throw noAplica("escribe sus observaciones y hace clic en \"" + boton + "\"");
    }

    /** "muestra el botón {string}" (el texto lo define {@code Pre265Priorizacion}, que delega aquí). */
    default void muestraBoton(String boton) {
        throw noAplica("muestra el botón \"" + boton + "\"");
    }

    /** "que quien entra es Técnico PRE o Coordinador PRE". */
    default void entraTecnicoPreOCoordinadorPre() {
        throw noAplica("que quien entra es Técnico PRE o Coordinador PRE");
    }

    private UnsupportedOperationException noAplica(String paso) {
        return new UnsupportedOperationException(
                getClass().getSimpleName() + " no implementa el paso compartido: " + paso);
    }
}
