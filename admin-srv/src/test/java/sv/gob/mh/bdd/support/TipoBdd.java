package sv.gob.mh.bdd.support;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import sv.gob.mh.infrastructure.persistence.entity.catalogo.CampoDefinicionEntity;
import sv.gob.mh.shared.enums.TipoCampo;

/**
 * Tipo de un campo en la notación de los .feature de CU-ADM-01 ({@code STRING{3}},
 * {@code NUMERIC{0:2000000000}}, {@code FECHA{1000-01-01:2100-12-31}}, {@code ENUM{"A","B"}}), y
 * su traducción al JSON del contrato ({@code CampoDefinicion}), a la entidad y desde ellos.
 * Los valores ENUM se conservan en orden y con repetidos, para poder pedir definiciones inválidas.
 * Un campo sin tipo (celda vacía) es {@code STRING{80}} (RN-04, S-09).
 */
public record TipoBdd(String tipo, BigDecimal minimo, BigDecimal maximo, Integer longitud, LocalDate desde,
        LocalDate hasta, List<String> valores) {

    private static final Pattern NOTACION = Pattern.compile("^(NUMERIC|STRING|FECHA|ENUM)\\{(.*)}$");

    public TipoBdd {
        minimo = minimo == null ? null : minimo.stripTrailingZeros();
        maximo = maximo == null ? null : maximo.stripTrailingZeros();
        valores = valores == null ? List.of() : List.copyOf(valores);
    }

    /** {@code true} si la celda "tipo" está vacía: un campo definido sin tipo. */
    public static boolean sinTipo(String notacion) {
        return notacion == null || notacion.isBlank();
    }

    public static TipoBdd de(String notacion) {
        if (sinTipo(notacion)) {
            return new TipoBdd("STRING", null, null, 80, null, null, null);
        }
        Matcher partes = NOTACION.matcher(notacion.trim());
        if (!partes.matches()) {
            throw new IllegalArgumentException("Tipo no reconocido: " + notacion);
        }
        String tipo = partes.group(1);
        String restriccion = partes.group(2);
        String[] rango = restriccion.split(":");
        return switch (tipo) {
            case "NUMERIC" -> new TipoBdd(tipo, new BigDecimal(rango[0]), new BigDecimal(rango[1]), null, null, null,
                    null);
            case "STRING" -> new TipoBdd(tipo, null, null, Integer.valueOf(restriccion), null, null, null);
            case "FECHA" -> new TipoBdd(tipo, null, null, null, LocalDate.parse(rango[0]), LocalDate.parse(rango[1]),
                    null);
            default -> new TipoBdd(tipo, null, null, null, null, null, restriccion.isBlank() ? List.of()
                    : Arrays.stream(restriccion.split(",")).map(valor -> valor.trim().replace("\"", "")).toList());
        };
    }

    public static TipoBdd de(CampoDefinicionEntity campo) {
        return new TipoBdd(campo.getTipo().getNotacion(), campo.getValorMinimo(), campo.getValorMaximo(),
                campo.getLongitudMaxima(), campo.getFechaMinima(), campo.getFechaMaxima(), campo.getValoresEnum());
    }

    /** Desde un {@code Campo} de la API. */
    public static TipoBdd de(JsonNode campo) {
        List<String> valores = new ArrayList<>();
        campo.path("valores").forEach(valor -> valores.add(valor.asText()));
        return new TipoBdd(campo.path("tipo").asText(), decimal(campo, "minimo"), decimal(campo, "maximo"),
                campo.hasNonNull("longitudMaxima") ? campo.get("longitudMaxima").asInt() : null,
                fecha(campo, "fechaDesde"), fecha(campo, "fechaHasta"), valores);
    }

    private static BigDecimal decimal(JsonNode nodo, String propiedad) {
        return nodo.hasNonNull(propiedad) ? nodo.get(propiedad).decimalValue() : null;
    }

    private static LocalDate fecha(JsonNode nodo, String propiedad) {
        return nodo.hasNonNull(propiedad) ? LocalDate.parse(nodo.get(propiedad).asText()) : null;
    }

    /**
     * {@code CampoDefinicion} del contrato; con {@code notacion} vacía, sin tipo (el servicio lo
     * crea como STRING{80}).
     */
    public static Map<String, Object> solicitud(String nombre, String calificador, Object posicion,
            String notacion) {
        Map<String, Object> campo = new LinkedHashMap<>();
        campo.put("nombre", nombre);
        campo.put("calificador", calificador);
        campo.put("posicion", posicion);
        if (!sinTipo(notacion)) {
            de(notacion).parametros(campo);
        }
        return campo;
    }

    private void parametros(Map<String, Object> campo) {
        campo.put("tipo", tipo);
        switch (tipo) {
            case "NUMERIC" -> {
                campo.put("minimo", minimo);
                campo.put("maximo", maximo);
            }
            case "STRING" -> campo.put("longitudMaxima", longitud);
            case "FECHA" -> {
                campo.put("fechaDesde", desde.toString());
                campo.put("fechaHasta", hasta.toString());
            }
            default -> campo.put("valores", valores);
        }
    }

    public void aplicar(CampoDefinicionEntity campo) {
        campo.setTipo(switch (tipo) {
            case "NUMERIC" -> TipoCampo.NUMBER;
            case "FECHA" -> TipoCampo.DATE;
            default -> TipoCampo.valueOf(tipo);
        });
        campo.setValorMinimo(minimo);
        campo.setValorMaximo(maximo);
        campo.setLongitudMaxima(longitud);
        campo.setFechaMinima(desde);
        campo.setFechaMaxima(hasta);
        campo.reemplazarValoresEnum(valores);
    }

    /** Un valor que el tipo admite; para el campo KEY, {@code clave}. */
    public String valorValido(String clave, boolean esKey) {
        if (esKey) {
            return clave;
        }
        return switch (tipo) {
            case "NUMERIC" -> minimo.toPlainString();
            case "FECHA" -> desde.toString();
            case "ENUM" -> valores.get(0);
            default -> {
                String texto = "Valor " + clave;
                yield texto.length() > longitud ? texto.substring(0, longitud) : texto;
            }
        };
    }

    /** Notación de los .feature, con los valores ENUM entre comillas. */
    public String notacion() {
        String restriccion = switch (tipo) {
            case "NUMERIC" -> minimo.toPlainString() + ":" + maximo.toPlainString();
            case "STRING" -> String.valueOf(longitud);
            case "FECHA" -> desde + ":" + hasta;
            default -> valores.stream().map(valor -> "\"" + valor + "\"").collect(Collectors.joining(","));
        };
        return tipo + "{" + restriccion + "}";
    }
}
