package sv.gob.mh.siip.model.preinversion.enums;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Categorías de los rangos de interpretación de la priorización (CU-PRE-26.5, RN09, Anexo A.2), de la
 * más alta a la más baja. El catálogo de rangos (CU-ADM-02) guarda el nombre; aquí se reconoce.
 */
public enum CategoriaPriorizacion {
    PRIORIZADO_PARA_PROGRAMACION,
    PRIORIZADO_CONDICIONAL,
    ELEGIBLE_PARA_FORTALECIMIENTO,
    NO_PRIORIZABLE_EN_ESTADO_ACTUAL;

    private static final Pattern MARCAS = Pattern.compile("\\p{M}");
    private static final Pattern ESPACIOS = Pattern.compile("\\s+");

    /**
     * @param nombre nombre de la categoría en el catálogo, p. ej. "Priorizado para programación"
     * @return la categoría con ese nombre, sin distinguir mayúsculas ni tildes
     */
    public static Optional<CategoriaPriorizacion> porNombre(String nombre) {
        String sinTildes = MARCAS.matcher(Normalizer.normalize(nombre, Normalizer.Form.NFD)).replaceAll("");
        String clave = ESPACIOS.matcher(sinTildes.trim()).replaceAll("_").toUpperCase(Locale.ROOT);
        return Arrays.stream(values()).filter((CategoriaPriorizacion c) -> c.name().equals(clave)).findFirst();
    }
}
