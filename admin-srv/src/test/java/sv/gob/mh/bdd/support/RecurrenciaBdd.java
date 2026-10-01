package sv.gob.mh.bdd.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sv.gob.mh.infrastructure.persistence.entity.calendario.PeriodoCalendarioEntity.TipoRecurrencia;

/**
 * Recurrencia de un período tal como la describen los .feature de CU-ADM-04, p.ej.
 * {@code "UNA_VEZ (2026-01-05 a 2026-06-30)"}, {@code "SEMANAL (2026-01-01 a 2026-12-31, lunes a
 * viernes)"} o {@code "MENSUAL (días 1 a 5, meses enero a diciembre)"}. Se traduce al cuerpo JSON
 * del contrato ({@link #aJson()}) o la usa {@link CalendarioFixtures} para sembrar la base.
 */
public record RecurrenciaBdd(TipoRecurrencia tipo, LocalDate fechaInicio, LocalDate fechaFin,
        Set<DayOfWeek> diasSemana, Set<Integer> diasMes, Set<Month> meses) {

    private static final Map<String, DayOfWeek> DIAS_SEMANA = Map.ofEntries(
            Map.entry("lunes", DayOfWeek.MONDAY),
            Map.entry("martes", DayOfWeek.TUESDAY),
            Map.entry("miércoles", DayOfWeek.WEDNESDAY),
            Map.entry("miercoles", DayOfWeek.WEDNESDAY),
            Map.entry("jueves", DayOfWeek.THURSDAY),
            Map.entry("viernes", DayOfWeek.FRIDAY),
            Map.entry("sábado", DayOfWeek.SATURDAY),
            Map.entry("sabado", DayOfWeek.SATURDAY),
            Map.entry("domingo", DayOfWeek.SUNDAY));

    private static final Map<String, Month> MESES = Map.ofEntries(
            Map.entry("enero", Month.JANUARY),
            Map.entry("febrero", Month.FEBRUARY),
            Map.entry("marzo", Month.MARCH),
            Map.entry("abril", Month.APRIL),
            Map.entry("mayo", Month.MAY),
            Map.entry("junio", Month.JUNE),
            Map.entry("julio", Month.JULY),
            Map.entry("agosto", Month.AUGUST),
            Map.entry("septiembre", Month.SEPTEMBER),
            Map.entry("octubre", Month.OCTOBER),
            Map.entry("noviembre", Month.NOVEMBER),
            Map.entry("diciembre", Month.DECEMBER));

    private static final Pattern PATRON_UNA_VEZ = Pattern
            .compile("^UNA_VEZ \\((\\d{4}-\\d{2}-\\d{2}) a (\\d{4}-\\d{2}-\\d{2})\\)$");
    private static final Pattern PATRON_SEMANAL = Pattern
            .compile("^SEMANAL \\((\\d{4}-\\d{2}-\\d{2}) a (\\d{4}-\\d{2}-\\d{2}), (.+)\\)$");
    private static final Pattern PATRON_MENSUAL = Pattern
            .compile("^MENSUAL \\((?:día|días) ([^,]+), (?:mes|meses) ([^)]+)\\)$");

    public static RecurrenciaBdd unaVez(LocalDate fechaInicio, LocalDate fechaFin) {
        return new RecurrenciaBdd(TipoRecurrencia.UNA_VEZ, fechaInicio, fechaFin, Set.of(), Set.of(), Set.of());
    }

    public static RecurrenciaBdd interpretar(String texto) {
        String valor = texto.trim();

        Matcher unaVez = PATRON_UNA_VEZ.matcher(valor);
        if (unaVez.matches()) {
            return unaVez(LocalDate.parse(unaVez.group(1)), LocalDate.parse(unaVez.group(2)));
        }

        Matcher semanal = PATRON_SEMANAL.matcher(valor);
        if (semanal.matches()) {
            return new RecurrenciaBdd(TipoRecurrencia.SEMANAL, LocalDate.parse(semanal.group(1)),
                    LocalDate.parse(semanal.group(2)), interpretarRango(semanal.group(3), DIAS_SEMANA, DayOfWeek.class),
                    Set.of(), Set.of());
        }

        Matcher mensual = PATRON_MENSUAL.matcher(valor);
        if (mensual.matches()) {
            return new RecurrenciaBdd(TipoRecurrencia.MENSUAL, null, null, Set.of(),
                    interpretarRangoEnteros(mensual.group(1)), interpretarRango(mensual.group(2), MESES, Month.class));
        }

        throw new IllegalArgumentException("No se pudo interpretar la recurrencia: " + texto);
    }

    /** Schema {@code Recurrencia} del contrato (discriminador {@code tipo}). */
    public Map<String, Object> aJson() {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("tipo", tipo.name());
        switch (tipo) {
            case UNA_VEZ -> {
                json.put("fechaInicio", fechaInicio.toString());
                json.put("fechaFin", fechaFin.toString());
            }
            case SEMANAL -> {
                json.put("fechaInicio", fechaInicio.toString());
                json.put("fechaFin", fechaFin.toString());
                json.put("diasSemana", new TreeSet<>(diasSemana).stream().map(Enum::name).toList());
            }
            case MENSUAL -> {
                json.put("diasDelMes", new TreeSet<>(diasMes).stream().toList());
                json.put("meses", new TreeSet<>(meses).stream().map(Enum::name).toList());
            }
        }
        return json;
    }

    /** Interpreta "X a Y" (rango inclusivo) o "X y Z" (enumeración) sobre un vocabulario cerrado. */
    private static <T extends Enum<T>> Set<T> interpretarRango(String texto, Map<String, T> vocabulario,
            Class<T> tipo) {
        String valor = texto.trim();
        Set<T> resultado = EnumSet.noneOf(tipo);
        if (valor.contains(" a ")) {
            String[] partes = valor.split(" a ", 2);
            T desde = exigir(vocabulario, partes[0]);
            T hasta = exigir(vocabulario, partes[1]);
            for (int i = desde.ordinal(); i <= hasta.ordinal(); i++) {
                resultado.add(tipo.getEnumConstants()[i]);
            }
            return resultado;
        }
        for (String parte : valor.split(" y ")) {
            resultado.add(exigir(vocabulario, parte));
        }
        return resultado;
    }

    private static <T> T exigir(Map<String, T> vocabulario, String palabra) {
        T valor = vocabulario.get(palabra.trim().toLowerCase());
        if (valor == null) {
            throw new IllegalArgumentException("Palabra no reconocida en la recurrencia: " + palabra);
        }
        return valor;
    }

    /** Interpreta "1 a 5" (rango inclusivo), "1 y 5" (enumeración) o "25" sobre días del mes. */
    private static Set<Integer> interpretarRangoEnteros(String texto) {
        String valor = texto.trim();
        Set<Integer> resultado = new TreeSet<>();
        if (valor.contains(" a ")) {
            String[] partes = valor.split(" a ", 2);
            for (int i = Integer.parseInt(partes[0].trim()); i <= Integer.parseInt(partes[1].trim()); i++) {
                resultado.add(i);
            }
            return resultado;
        }
        for (String parte : valor.split(" y ")) {
            resultado.add(Integer.parseInt(parte.trim()));
        }
        return resultado;
    }
}
