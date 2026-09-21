package sv.gob.mh.siip.bdd.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Excepcion;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.domain.Recurrencia;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaMensual;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaSemanal;
import sv.gob.mh.siip.model.administracion.domain.RecurrenciaUnaVez;
import sv.gob.mh.siip.model.administracion.dto.DayOfWeekDto;
import sv.gob.mh.siip.model.administracion.dto.MonthDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaMensualDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaSemanalDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;

/**
 * Builders reutilizados por los steps BDD de CU-ADM-04 (gestion de calendarios), para no repetir
 * la construccion de Usuario administrador en cada clase de steps. No es una step definition:
 * helper de test plano (mismo criterio que ProyectoFixtures/CatalogoFixtures).
 */
public final class CalendarioFixtures {

    public static final LocalDate INICIO_CALENDARIO = LocalDate.of(2026, 1, 1);
    public static final LocalDate FIN_CALENDARIO = LocalDate.of(2026, 12, 31);

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

    private CalendarioFixtures() {
    }

    public static String nuevoSufijo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static Usuario nuevoAdministradorCalendario(String nombreUsuario) {
        return nuevoUsuarioConRol(nombreUsuario, RolUsuario.ADMINISTRADOR_CALENDARIO);
    }

    public static Usuario nuevoUsuarioConRol(String nombreUsuario, RolUsuario rol) {
        return Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de calendarios (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .activo(true)
                .build();
    }

    public static Usuario nuevoUsuarioSinRolAdecuado(String nombreUsuario) {
        return Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Usuario sin rol de calendarios (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .activo(true)
                .build();
    }

    /** Autentica al actor de la peticion HTTP simulada actual mediante el header X-Usuario (ver ActorContexto). */
    public static void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Usuario", nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    /** Calendario ACTIVO con el rango por defecto (2026), listo para persistir. */
    public static Calendario nuevoCalendario(String codigo, Usuario administrador) {
        return nuevoCalendario(codigo, INICIO_CALENDARIO, FIN_CALENDARIO, administrador);
    }

    public static Calendario nuevoCalendario(String codigo, LocalDate fechaInicio, LocalDate fechaFin,
            Usuario administrador) {
        return nuevoCalendario(codigo, fechaInicio, fechaFin, EstadoCalendario.ACTIVO, administrador);
    }

    public static Calendario nuevoCalendario(String codigo, LocalDate fechaInicio, LocalDate fechaFin,
            EstadoCalendario estado, Usuario administrador) {
        return Calendario.builder()
                .codigo(codigo)
                .nombre("Calendario de prueba BDD")
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estado(estado)
                .administrador(administrador)
                .build();
    }

    public static RecurrenciaUnaVez recurrenciaUnaVez(LocalDate fechaInicio, LocalDate fechaFin) {
        return RecurrenciaUnaVez.builder().fechaInicio(fechaInicio).fechaFin(fechaFin).build();
    }

    public static RecurrenciaSemanal recurrenciaSemanal(LocalDate fechaInicio, LocalDate fechaFin,
            DayOfWeek... dias) {
        Set<DayOfWeek> diasDeLaSemana = EnumSet.noneOf(DayOfWeek.class);
        diasDeLaSemana.addAll(Arrays.asList(dias));
        return RecurrenciaSemanal.builder().fechaInicio(fechaInicio).fechaFin(fechaFin)
                .diasDeLaSemana(diasDeLaSemana).build();
    }

    public static RecurrenciaMensual recurrenciaMensual(Set<Integer> diasDelMes, Month... meses) {
        Set<Month> mesesSet = EnumSet.noneOf(Month.class);
        mesesSet.addAll(Arrays.asList(meses));
        return RecurrenciaMensual.builder().diasDelMes(new LinkedHashSet<>(diasDelMes)).meses(mesesSet).build();
    }

    /**
     * Agrega un periodo al calendario indicado (relacion bidireccional) y lo retorna, sin persistir.
     * Solo apto para un {@code calendario} aun no persistido (recien construido con
     * {@link #nuevoCalendario}): agrega a su coleccion {@code periodos}, que en ese caso es un
     * ArrayList en memoria (no una coleccion Hibernate perezosa) y no requiere sesion activa.
     */
    public static Periodo agregarPeriodo(Calendario calendario, String codigo, TipoPeriodo tipo,
            Recurrencia recurrencia) {
        Periodo periodo = nuevoPeriodo(calendario, codigo, tipo, recurrencia);
        calendario.getPeriodos().add(periodo);
        return periodo;
    }

    /**
     * Agrega una excepcion al calendario indicado (relacion bidireccional) y la retorna, sin
     * persistir. Solo apto para un {@code calendario} aun no persistido: ver nota de
     * {@link #agregarPeriodo}.
     */
    public static Excepcion agregarExcepcion(Calendario calendario, LocalDate fecha, TipoExcepcion tipo) {
        Excepcion excepcion = nuevaExcepcion(calendario, fecha, tipo);
        calendario.getExcepciones().add(excepcion);
        return excepcion;
    }

    /**
     * Construye un periodo con el {@code calendario} indicado como padre, sin tocar la coleccion
     * {@code calendario.getPeriodos()}. Para agregarlo a un calendario YA persistido (fetched de un
     * repositorio, con colecciones perezosas de Hibernate), guardar el resultado directamente via
     * {@code PeriodoRepository.save(...)} en lugar de {@code calendarioRepository.save(calendario)}:
     * el FK en {@code Periodo.calendario} basta para persistir la relacion sin necesitar inicializar
     * la coleccion inversa (evita LazyInitializationException fuera de sesion).
     */
    public static Periodo nuevoPeriodo(Calendario calendario, String codigo, TipoPeriodo tipo,
            Recurrencia recurrencia) {
        return Periodo.builder()
                .codigo(codigo)
                .nombre("Período de prueba BDD")
                .tipo(tipo)
                .calendario(calendario)
                .recurrencia(recurrencia)
                .build();
    }

    /** Como {@link #nuevoPeriodo}, pero para una excepcion: ver la misma nota sobre persistencia. */
    public static Excepcion nuevaExcepcion(Calendario calendario, LocalDate fecha, TipoExcepcion tipo) {
        return Excepcion.builder()
                .calendario(calendario)
                .fecha(fecha)
                .tipo(tipo)
                .descripcion("Excepción de prueba BDD")
                .build();
    }

    /**
     * Interpreta la notacion usada en los .feature de CU-ADM-04 para describir una recurrencia,
     * p.ej. {@code "UNA_VEZ (2026-01-05 a 2026-06-30)"}, {@code "SEMANAL (2026-01-01 a 2026-12-31,
     * lunes a viernes)"} o {@code "MENSUAL (días 1 a 5, meses enero a diciembre)"}.
     */
    public static RecurrenciaDto parseRecurrencia(String texto) {
        String valor = texto.trim();

        Matcher unaVez = PATRON_UNA_VEZ.matcher(valor);
        if (unaVez.matches()) {
            return new RecurrenciaUnaVezDto().tipo("UNA_VEZ")
                    .fechaInicio(LocalDate.parse(unaVez.group(1)))
                    .fechaFin(LocalDate.parse(unaVez.group(2)));
        }

        Matcher semanal = PATRON_SEMANAL.matcher(valor);
        if (semanal.matches()) {
            RecurrenciaSemanalDto dto = new RecurrenciaSemanalDto().tipo("SEMANAL")
                    .fechaInicio(LocalDate.parse(semanal.group(1)))
                    .fechaFin(LocalDate.parse(semanal.group(2)));
            parseRango(semanal.group(3), DIAS_SEMANA, DayOfWeek.class).stream().sorted()
                    .forEach(dia -> dto.addDiasSemanaItem(DayOfWeekDto.valueOf(dia.name())));
            return dto;
        }

        Matcher mensual = PATRON_MENSUAL.matcher(valor);
        if (mensual.matches()) {
            RecurrenciaMensualDto dto = new RecurrenciaMensualDto().tipo("MENSUAL");
            parseRangoEnteros(mensual.group(1)).forEach(dto::addDiasDelMesItem);
            parseRango(mensual.group(2), MESES, Month.class).stream().sorted()
                    .forEach(mes -> dto.addMesesItem(MonthDto.valueOf(mes.name())));
            return dto;
        }

        throw new IllegalArgumentException("No se pudo interpretar la recurrencia: " + texto);
    }

    /**
     * Como {@link #parseRecurrencia(String)}, pero construye directamente la entidad de dominio
     * {@link Recurrencia} en lugar del DTO, para los Dado que preparan datos sin pasar por
     * {@code CalendarioService} (evitando exigir un actor autenticado solo para el fixture).
     */
    public static Recurrencia parseRecurrenciaDominio(String texto) {
        String valor = texto.trim();

        Matcher unaVez = PATRON_UNA_VEZ.matcher(valor);
        if (unaVez.matches()) {
            return recurrenciaUnaVez(LocalDate.parse(unaVez.group(1)), LocalDate.parse(unaVez.group(2)));
        }

        Matcher semanal = PATRON_SEMANAL.matcher(valor);
        if (semanal.matches()) {
            Set<DayOfWeek> dias = parseRango(semanal.group(3), DIAS_SEMANA, DayOfWeek.class);
            return RecurrenciaSemanal.builder()
                    .fechaInicio(LocalDate.parse(semanal.group(1)))
                    .fechaFin(LocalDate.parse(semanal.group(2)))
                    .diasDeLaSemana(dias)
                    .build();
        }

        Matcher mensual = PATRON_MENSUAL.matcher(valor);
        if (mensual.matches()) {
            Set<Integer> diasDelMes = parseRangoEnteros(mensual.group(1));
            Set<Month> meses = parseRango(mensual.group(2), MESES, Month.class);
            return recurrenciaMensual(diasDelMes, meses.toArray(new Month[0]));
        }

        throw new IllegalArgumentException("No se pudo interpretar la recurrencia: " + texto);
    }

    /** Interpreta "X a Y" (rango inclusivo) o "X y Z" (enumeracion) sobre un vocabulario cerrado. */
    private static <T extends Enum<T>> Set<T> parseRango(String texto, Map<String, T> vocabulario, Class<T> tipo) {
        String valor = texto.trim();
        if (valor.contains(" a ")) {
            String[] partes = valor.split(" a ", 2);
            T desde = vocabulario.get(partes[0].trim().toLowerCase());
            T hasta = vocabulario.get(partes[1].trim().toLowerCase());
            Set<T> resultado = EnumSet.noneOf(tipo);
            for (int i = desde.ordinal(); i <= hasta.ordinal(); i++) {
                resultado.add(tipo.getEnumConstants()[i]);
            }
            return resultado;
        }
        Set<T> resultado = EnumSet.noneOf(tipo);
        for (String parte : valor.split(" y ")) {
            resultado.add(vocabulario.get(parte.trim().toLowerCase()));
        }
        return resultado;
    }

    /** Interpreta "1 a 5" (rango inclusivo) o "1 y 5" (enumeracion) sobre dias del mes. */
    private static Set<Integer> parseRangoEnteros(String texto) {
        String valor = texto.trim();
        if (valor.contains(" a ")) {
            String[] partes = valor.split(" a ", 2);
            int desde = Integer.parseInt(partes[0].trim());
            int hasta = Integer.parseInt(partes[1].trim());
            Set<Integer> resultado = new LinkedHashSet<>();
            for (int i = desde; i <= hasta; i++) {
                resultado.add(i);
            }
            return resultado;
        }
        Set<Integer> resultado = new LinkedHashSet<>();
        for (String parte : valor.split(" y ")) {
            resultado.add(Integer.parseInt(parte.trim()));
        }
        return resultado;
    }
}
