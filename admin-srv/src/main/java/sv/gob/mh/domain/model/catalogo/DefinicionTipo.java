package sv.gob.mh.domain.model.catalogo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import sv.gob.mh.shared.enums.TipoCampo;

/**
 * Tipo de un campo con su restricción (producción {@code type} de la gramática, RN-04):
 * {@code NUMERIC {mínimo : máximo}}, {@code STRING {longitud}}, {@code FECHA {desde : hasta}} o
 * {@code ENUM {s1, s2, ...}}. Solo se informan los parámetros del tipo declarado.
 *
 * <p>Los campos de catálogos cargados antes de que el contrato exigiera la restricción pueden no
 * tenerla: {@link #admite} entonces solo verifica el formato del valor.</p>
 *
 * @param valoresEnum en el orden recibido y con repetidos, para poder rechazarlos (E-05)
 */
public record DefinicionTipo(TipoCampo tipo, BigDecimal minimo, BigDecimal maximo, Integer longitudMaxima,
        LocalDate fechaMinima, LocalDate fechaMaxima, List<String> valoresEnum) {

    /** Longitud máxima del campo KEY: su valor se guarda como clave del registro (modelo de dominio v4.0). */
    public static final int LONGITUD_MAXIMA_KEY = 255;
    /** Longitud máxima de un valor STRING (RN-11: los valores se guardan como texto). */
    public static final int LONGITUD_MAXIMA_STRING = 4000;
    /** RN-04, S-09: longitud del STRING que recibe un campo definido sin tipo. */
    public static final int LONGITUD_POR_DEFECTO = 80;

    public DefinicionTipo {
        valoresEnum = valoresEnum == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(valoresEnum));
    }

    /** RN-04, S-09: un campo definido sin tipo es {@code STRING{80}}; se ignoran sus demás parámetros de tipo. */
    public static DefinicionTipo porDefecto() {
        return texto(LONGITUD_POR_DEFECTO);
    }

    public static DefinicionTipo numerico(BigDecimal minimo, BigDecimal maximo) {
        return new DefinicionTipo(TipoCampo.NUMBER, minimo, maximo, null, null, null, null);
    }

    public static DefinicionTipo texto(Integer longitudMaxima) {
        return new DefinicionTipo(TipoCampo.STRING, null, null, longitudMaxima, null, null, null);
    }

    public static DefinicionTipo fecha(LocalDate fechaMinima, LocalDate fechaMaxima) {
        return new DefinicionTipo(TipoCampo.DATE, null, null, null, fechaMinima, fechaMaxima, null);
    }

    public static DefinicionTipo enumerado(List<String> valores) {
        return new DefinicionTipo(TipoCampo.ENUM, null, null, null, null, null, valores);
    }

    /**
     * E-05: la restricción es la de su tipo y es consistente (mínimo ≤ máximo, longitud positiva,
     * lista ENUM no vacía y sin repetidos). Un campo KEY STRING admite a lo sumo
     * {@value #LONGITUD_MAXIMA_KEY} caracteres.
     */
    public boolean esValida(boolean esKey) {
        if (tipo == null || !soloParametrosDelTipo()) {
            return false;
        }
        return switch (tipo) {
            case NUMBER -> minimo != null && maximo != null && minimo.compareTo(maximo) <= 0;
            case STRING -> longitudMaxima != null && longitudMaxima > 0
                    && longitudMaxima <= (esKey ? LONGITUD_MAXIMA_KEY : LONGITUD_MAXIMA_STRING);
            case DATE -> fechaMinima != null && fechaMaxima != null && !fechaMinima.isAfter(fechaMaxima);
            case ENUM -> !valoresEnum.isEmpty() && valoresEnum.stream().noneMatch(v -> v == null || v.isBlank())
                    && new HashSet<>(valoresEnum).size() == valoresEnum.size();
        };
    }

    private boolean soloParametrosDelTipo() {
        boolean numericos = minimo != null || maximo != null;
        boolean longitud = longitudMaxima != null;
        boolean fechas = fechaMinima != null || fechaMaxima != null;
        boolean enumerados = !valoresEnum.isEmpty();
        return (!numericos || tipo == TipoCampo.NUMBER) && (!longitud || tipo == TipoCampo.STRING)
                && (!fechas || tipo == TipoCampo.DATE) && (!enumerados || tipo == TipoCampo.ENUM);
    }

    /** RN-01, RN-11: el valor (en versión STRING) cumple el tipo y su restricción. Nulo es un valor faltante. */
    public boolean admite(String valor) {
        if (valor == null) {
            return false;
        }
        return switch (tipo) {
            case NUMBER -> admiteNumero(valor);
            case STRING -> longitudMaxima == null || valor.length() <= longitudMaxima;
            case DATE -> admiteFecha(valor);
            case ENUM -> valoresEnum.isEmpty() || valoresEnum.contains(valor);
        };
    }

    private boolean admiteNumero(String valor) {
        BigDecimal numero;
        try {
            numero = new BigDecimal(valor.trim());
        } catch (NumberFormatException ex) {
            return false;
        }
        return (minimo == null || numero.compareTo(minimo) >= 0) && (maximo == null || numero.compareTo(maximo) <= 0);
    }

    /** Fechas en ISO-8601 (AAAA-MM-DD), como las declara el contrato. */
    private boolean admiteFecha(String valor) {
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(valor.trim());
        } catch (DateTimeParseException ex) {
            return false;
        }
        return (fechaMinima == null || !fecha.isBefore(fechaMinima))
                && (fechaMaxima == null || !fecha.isAfter(fechaMaxima));
    }

    /** El tipo en la notación de la gramática, p.ej. {@code NUMERIC{0:2000000000}} o {@code ENUM{AMERICA,EUROPA}}. */
    public String notacion() {
        String restriccion = switch (tipo) {
            case NUMBER -> minimo == null || maximo == null ? null : texto(minimo) + ":" + texto(maximo);
            case STRING -> longitudMaxima == null ? null : String.valueOf(longitudMaxima);
            case DATE -> fechaMinima == null || fechaMaxima == null ? null : fechaMinima + ":" + fechaMaxima;
            case ENUM -> valoresEnum.isEmpty() ? null : String.join(",", valoresEnum);
        };
        return tipo.getNotacion() + (restriccion == null ? "" : "{" + restriccion + "}");
    }

    private static String texto(BigDecimal numero) {
        return numero.stripTrailingZeros().toPlainString();
    }
}
