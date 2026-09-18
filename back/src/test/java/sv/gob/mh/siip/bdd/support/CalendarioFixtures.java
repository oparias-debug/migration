package sv.gob.mh.siip.bdd.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

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

    private CalendarioFixtures() {
    }

    public static String nuevoSufijo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static Usuario nuevoAdministradorCalendario(String nombreUsuario) {
        return Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Administrador de Calendarios (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.ADMINISTRADOR_CALENDARIO)
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
        return Calendario.builder()
                .codigo(codigo)
                .nombre("Calendario de prueba BDD")
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estado(EstadoCalendario.ACTIVO)
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

    /** Agrega un periodo al calendario indicado (relacion bidireccional) y lo retorna, sin persistir. */
    public static Periodo agregarPeriodo(Calendario calendario, String codigo, TipoPeriodo tipo,
            Recurrencia recurrencia) {
        Periodo periodo = Periodo.builder()
                .codigo(codigo)
                .nombre("Período de prueba BDD")
                .tipo(tipo)
                .calendario(calendario)
                .recurrencia(recurrencia)
                .build();
        calendario.getPeriodos().add(periodo);
        return periodo;
    }

    /** Agrega una excepcion al calendario indicado (relacion bidireccional) y la retorna, sin persistir. */
    public static Excepcion agregarExcepcion(Calendario calendario, LocalDate fecha, TipoExcepcion tipo) {
        Excepcion excepcion = Excepcion.builder()
                .calendario(calendario)
                .fecha(fecha)
                .tipo(tipo)
                .descripcion("Excepción de prueba BDD")
                .build();
        calendario.getExcepciones().add(excepcion);
        return excepcion;
    }
}
