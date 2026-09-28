package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * Steps de CU-ADM-04 (gestion de calendarios) cuyo texto es literalmente identico en varios de los
 * 15 .feature del caso de uso (Cucumber exige una unica definicion por texto en el classpath de
 * test, sin importar la clase o el tipo de keyword usado): autenticacion del actor por rol, el
 * rechazo por falta de permisos, el Antecedentes comun "existe un calendario ACTIVO con código...,
 * fecha de inicio... y fecha de fin...", y los rechazos de RN08/RN10/RN15/RN17 compartidos entre
 * CU-ADM-04-02 (período LABORAL) y CU-ADM-04-03 (período NO_LABORAL).
 */
public class Adm04ComunCalendario {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    public Adm04ComunCalendario(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            PeriodoRepository periodoRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que el actor tiene el rol \"([^\"]*)\"$")
    public void que_el_actor_tiene_el_rol(String rol) {
        String nombreUsuario = "actor.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        usuarioRepository.save(CalendarioFixtures.nuevoUsuarioConRol(nombreUsuario, RolUsuario.valueOf(rol)));
        CalendarioFixtures.autenticarComo(nombreUsuario);
    }

    @Dado("^que el actor no tiene el rol \"ADMINISTRADOR\" ni \"ADMINISTRADOR_CALENDARIO\"$")
    public void que_el_actor_no_tiene_el_rol_adecuado() {
        String nombreUsuario = "usuario.sin.rol.bdd." + CalendarioFixtures.nuevoSufijo();
        usuarioRepository.save(CalendarioFixtures.nuevoUsuarioSinRolAdecuado(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
    }

    @Entonces("^el sistema rechaza la operación por falta de permisos$")
    public void el_sistema_rechaza_la_operacion_por_falta_de_permisos() {
        assertThat(contextoValidacion.getUltimaExcepcion())
                .isInstanceOfAny(NoAutenticadoException.class, AccesoDenegadoException.class);
    }

    @Entonces("^el sistema rechaza la operación indicando que la fecha de inicio no puede ser posterior a la fecha de fin$")
    public void el_sistema_rechaza_la_operacion_por_fecha_de_inicio_posterior_a_fecha_de_fin() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }

    @Entonces("^el sistema rechaza la operación indicando que el período no está enmarcado dentro del rango del calendario$")
    public void el_sistema_rechaza_la_operacion_por_periodo_fuera_de_rango() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }

    @Entonces("^el sistema rechaza la operación indicando que el código de período ya existe en ese calendario$")
    public void el_sistema_rechaza_la_operacion_por_codigo_de_periodo_duplicado() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(ConflictoEstadoException.class);
    }

    @Entonces("^el sistema retorna error indicando que el calendario no existe$")
    public void el_sistema_retorna_error_indicando_que_el_calendario_no_existe() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Entonces("^el sistema retorna error indicando que el período no existe$")
    public void el_sistema_retorna_error_indicando_que_el_periodo_no_existe() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\", fecha de inicio \"([^\"]*)\" y fecha de fin \"([^\"]*)\"$")
    public void que_existe_un_calendario_activo_con_codigo_fecha_inicio_y_fecha_fin(String codigo,
            String fechaInicio, String fechaFin) {
        // El código literal del .feature (p.ej. "CAL-2026") se reutiliza en muchos escenarios; como
        // los escenarios de Cucumber NO revierten los cambios entre si (no hay rollback transaccional
        // por escenario en este runner), se le agrega un sufijo unico para no violar la unicidad de
        // Calendario.codigo. Los demas steps de esta clase leen el codigo real desde
        // ContextoCalendarioBdd.getCalendarioActual() en lugar de volver a usar el literal capturado.
        String codigoReal = codigo + "-" + CalendarioFixtures.nuevoSufijo();
        Calendario calendario = calendarioRepository.save(CalendarioFixtures.nuevoCalendario(codigoReal,
                LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin), autenticarNuevoAdministrador()));
        contextoCalendario.setCalendarioActual(calendario);
        contextoCalendario.setCodigoCalendarioActualLiteral(codigo);
    }

    @Dado("^que el calendario \"([^\"]*)\" ya tiene un período con código \"([^\"]*)\"$")
    public void que_el_calendario_ya_tiene_un_periodo_con_codigo(String codigoCalendarioLiteral,
            String codigoPeriodo) {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        // Se persiste el periodo directamente (no via calendario.getPeriodos().add(...)): calendario
        // fue obtenido de un repositorio en un paso anterior, por lo que su coleccion "periodos" es
        // una coleccion perezosa de Hibernate; agregarle un elemento la inicializaria innecesariamente.
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, codigoPeriodo, TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(calendario.getFechaInicio(), calendario.getFechaInicio().plusDays(10))));
    }

    @Cuando("^el actor intenta agregar otro período con código \"([^\"]*)\" al calendario \"([^\"]*)\"$")
    public void el_actor_intenta_agregar_otro_periodo_con_codigo_duplicado(String codigoPeriodo,
            String codigoCalendarioLiteral) {
        String codigoCalendario = contextoCalendario.getCalendarioActual().getCodigo();
        Calendario calendario = calendarioRepository.findByCodigo(codigoCalendario).orElseThrow();
        RecurrenciaUnaVezDto recurrencia = new RecurrenciaUnaVezDto().tipo("UNA_VEZ")
                .fechaInicio(calendario.getFechaInicio()).fechaFin(calendario.getFechaInicio().plusDays(1));
        PeriodoInputDto request = new PeriodoInputDto().codigo(codigoPeriodo)
                .nombre("Período de prueba BDD").recurrencia(recurrencia);
        try {
            calendarioService.agregarPeriodoLaboral(codigoCalendario, request);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el período se agrega correctamente al calendario \"([^\"]*)\"$")
    public void el_periodo_se_agrega_correctamente_al_calendario(String codigoCalendarioLiteral) {
        assertThat(contextoCalendario.getPeriodoActual()).isNotNull();
        // Se verifica via PeriodoRepository (no navegando calendario.getPeriodos(), coleccion
        // perezosa de Hibernate) para no arriesgar una sesion ya cerrada.
        String codigoCalendario = contextoCalendario.getCalendarioActual().getCodigo();
        String codigoPeriodo = contextoCalendario.getPeriodoActual().getCodigo();
        assertThat(periodoRepository.existsByCalendario_CodigoAndCodigo(codigoCalendario, codigoPeriodo)).isTrue();
    }

    @Entonces("^el período hereda el estado \"([^\"]*)\" del calendario$")
    public void el_periodo_hereda_el_estado_del_calendario(String estadoEsperado) {
        assertThat(contextoCalendario.getPeriodoActual()).isNotNull();
        Calendario calendario = calendarioRepository
                .findByCodigo(contextoCalendario.getCalendarioActual().getCodigo()).orElseThrow();
        assertThat(calendario.getEstado()).isEqualTo(EstadoCalendario.valueOf(estadoEsperado));
    }

    /** Crea, persiste y autentica un nuevo Usuario ADMINISTRADOR_CALENDARIO; retorna la entidad persistida. */
    private Usuario autenticarNuevoAdministrador() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        Usuario administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        return administrador;
    }
}
