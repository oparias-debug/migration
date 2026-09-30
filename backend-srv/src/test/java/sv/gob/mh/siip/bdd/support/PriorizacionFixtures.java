package sv.gob.mh.siip.bdd.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.EscalaCalificacionSubcriterio;
import sv.gob.mh.siip.model.preinversion.domain.RangoInterpretacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;
import sv.gob.mh.siip.model.preinversion.repository.CriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.EscalaCalificacionSubcriterioRepository;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SubcriterioPriorizacionRepository;

/**
 * Catálogo oficial de la priorización de CU-PRE-26.5 (criterios, subcriterios, escala del Anexo C y
 * rangos de interpretación), leído de los mismos CSV de {@code data/seed} que usa el perfil dev. La
 * siembra es idempotente por código: el esquema de pruebas es compartido entre escenarios.
 */
public final class PriorizacionFixtures {

    public static final String CSV_ESCALA = "escala-calificacion.csv";

    private static final String CARPETA = "data/seed/";
    private static final String CODIGO = "codigo";

    private PriorizacionFixtures() {
    }

    /**
     * Siembra el catálogo si aún no existe; debe llamarse dentro de una transacción.
     *
     * @param criterios repositorio de criterios
     * @param subcriterios repositorio de subcriterios
     * @param escalas repositorio de la escala de calificación
     * @param rangos repositorio de los rangos de interpretación
     */
    public static void sembrarCatalogo(CriterioPriorizacionRepository criterios,
            SubcriterioPriorizacionRepository subcriterios, EscalaCalificacionSubcriterioRepository escalas,
            RangoInterpretacionPriorizacionRepository rangos) {
        for (Map<String, String> fila : leerCsv("rangos-interpretacion.csv")) {
            if (rangos.findByCategoria(fila.get("categoria")).isEmpty()) {
                rangos.save(RangoInterpretacionPriorizacion.builder()
                        .puntajeMinimo(Double.valueOf(fila.get("puntaje_minimo")))
                        .puntajeMaximo(Double.valueOf(fila.get("puntaje_maximo")))
                        .categoria(fila.get("categoria")).implicacion(fila.get("implicacion")).build());
            }
        }
        List<Map<String, String>> filasSubcriterios = leerCsv("subcriterios-priorizacion.csv");
        for (Map<String, String> fila : leerCsv("criterios-priorizacion.csv")) {
            if (criterios.findByCodigo(fila.get(CODIGO)).isEmpty()) {
                CriterioPriorizacion criterio = criterios.save(CriterioPriorizacion.builder().codigo(fila.get(CODIGO))
                        .numeroCriterio(Integer.valueOf(fila.get("numero"))).nombreCriterio(fila.get("nombre"))
                        .ponderacionCriterio(Double.valueOf(fila.get("ponderacion"))).build());
                filasSubcriterios.stream()
                        .filter((Map<String, String> s) -> criterio.getCodigo().equals(s.get("codigo_criterio")))
                        // También en la colección: en la misma sesión el criterio se relee de la caché.
                        .forEach((Map<String, String> s) -> criterio.getSubcriterios().add(subcriterios.save(
                                SubcriterioPriorizacion.builder().criterio(criterio).codigo(s.get(CODIGO))
                                        .numero(s.get("numero")).nombre(s.get("nombre"))
                                        .ponderacionSubcriterio(Double.valueOf(s.get("ponderacion")))
                                        .descripcionRequerimiento(s.get("descripcion_requerimiento")).build())));
            }
        }
        for (Map<String, String> fila : leerCsv(CSV_ESCALA)) {
            ValorCalificacion valor = ValorCalificacion.valueOf(fila.get("valor"));
            if (escalas.findByCodigoSubcriterioAndValor(fila.get("codigo_subcriterio"), valor).isEmpty()) {
                escalas.save(EscalaCalificacionSubcriterio.builder().codigoSubcriterio(fila.get("codigo_subcriterio"))
                        .valor(valor).descripcion(fila.get("descripcion")).build());
            }
        }
    }

    /**
     * @param archivo nombre del CSV en {@code data/seed}
     * @return sus filas de datos como columna → valor (celda vacía → {@code null})
     */
    public static List<Map<String, String>> leerCsv(String archivo) {
        ClassPathResource recurso = new ClassPathResource(CARPETA + archivo);
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(recurso.getInputStream(), StandardCharsets.UTF_8))) {
            List<String> lineas = lector.lines().filter((String l) -> !l.isBlank() && !l.startsWith("#")).toList();
            String[] encabezado = lineas.get(0).split(";", -1);
            List<Map<String, String>> filas = new ArrayList<>();
            for (String linea : lineas.subList(1, lineas.size())) {
                String[] celdas = linea.split(";", -1);
                Map<String, String> fila = new HashMap<>();
                for (int i = 0; i < encabezado.length; i++) {
                    fila.put(encabezado[i], celdas[i].isEmpty() ? null : celdas[i]);
                }
                filas.add(fila);
            }
            return filas;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
