package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-01-crear-calendario.feature. */
public class Adm04CrearCalendario {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private String codigoPreparado;
    private CrearCalendarioRequestDto ultimaSolicitud;
    private CalendarioDto ultimoResultado;

    public Adm04CrearCalendario(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            CalendarioService calendarioService, ContextoCalendarioBdd contextoCalendario,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que el actor autenticado tiene el rol ADMINISTRADOR o ADMINISTRADOR_CALENDARIO$")
    public void que_el_actor_autenticado_tiene_el_rol_adecuado() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
    }

    @Dado("^no existe ningún calendario con el código indicado$")
    public void no_existe_ningun_calendario_con_el_codigo_indicado() {
        codigoPreparado = "CAL-" + CalendarioFixtures.nuevoSufijo();
        assertThat(calendarioRepository.existsByCodigo(codigoPreparado)).isFalse();
    }

    @Cuando("^el actor crea un calendario con código, nombre, descripción, fecha de inicio, fecha de fin y estado válidos$")
    public void el_actor_crea_un_calendario_con_datos_validos() {
        ultimaSolicitud = new CrearCalendarioRequestDto()
                .codigo(codigoPreparado)
                .nombre("Calendario de prueba BDD")
                .descripcion("Creado por un escenario BDD")
                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO)
                .fechaFin(CalendarioFixtures.FIN_CALENDARIO)
                .estado(EstadoCalendarioDto.ACTIVO);
        ultimoResultado = calendarioService.crear(ultimaSolicitud);
    }

    @Entonces("^el calendario queda creado con los datos indicados$")
    public void el_calendario_queda_creado_con_los_datos_indicados() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getCodigo()).isEqualTo(ultimaSolicitud.getCodigo());
        assertThat(ultimoResultado.getNombre()).isEqualTo(ultimaSolicitud.getNombre());
        assertThat(ultimoResultado.getDescripcion()).isEqualTo(ultimaSolicitud.getDescripcion());
        assertThat(ultimoResultado.getFechaInicio()).isEqualTo(ultimaSolicitud.getFechaInicio());
        assertThat(ultimoResultado.getFechaFin()).isEqualTo(ultimaSolicitud.getFechaFin());
        assertThat(ultimoResultado.getEstado()).isEqualTo(ultimaSolicitud.getEstado());
    }

    @Entonces("^el calendario queda disponible para las consultas de solo lectura$")
    public void el_calendario_queda_disponible_para_las_consultas_de_solo_lectura() {
        assertThat(calendarioRepository.existsByCodigo(ultimaSolicitud.getCodigo())).isTrue();
        assertThat(calendarioService.consultarDiasLaboralesEntreFechas(ultimaSolicitud.getCodigo(),
                ultimaSolicitud.getFechaInicio(), ultimaSolicitud.getFechaInicio())).isNotNull();
    }

    @Dado("^que ya existe un calendario registrado con un código determinado$")
    public void que_ya_existe_un_calendario_registrado_con_un_codigo_determinado() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        Usuario administrador = usuarioRepository.findByNombreUsuario(nombreUsuario).orElseThrow();
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), administrador));
        codigoPreparado = calendario.getCodigo();
        contextoCalendario.setCalendarioActual(calendario);
    }

    @Cuando("^el actor intenta crear un nuevo calendario con ese mismo código$")
    public void el_actor_intenta_crear_un_nuevo_calendario_con_ese_mismo_codigo() {
        CrearCalendarioRequestDto solicitud = new CrearCalendarioRequestDto()
                .codigo(codigoPreparado)
                .nombre("Otro calendario de prueba BDD")
                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO)
                .fechaFin(CalendarioFixtures.FIN_CALENDARIO)
                .estado(EstadoCalendarioDto.ACTIVO);
        try {
            calendarioService.crear(solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema informa que el código de calendario ya existe$")
    public void el_sistema_informa_que_el_codigo_de_calendario_ya_existe() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(ConflictoEstadoException.class);
    }

    @Dado("^que el actor prepara los datos de un nuevo calendario$")
    public void que_el_actor_prepara_los_datos_de_un_nuevo_calendario() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        codigoPreparado = "CAL-" + CalendarioFixtures.nuevoSufijo();
    }

    @Cuando("^el actor indica una fecha de inicio posterior a la fecha de fin$")
    public void el_actor_indica_una_fecha_de_inicio_posterior_a_la_fecha_de_fin() {
        LocalDate fechaInicio = LocalDate.of(2026, 6, 10);
        LocalDate fechaFin = LocalDate.of(2026, 6, 1);
        CrearCalendarioRequestDto solicitud = new CrearCalendarioRequestDto()
                .codigo(codigoPreparado)
                .nombre("Calendario de prueba BDD")
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estado(EstadoCalendarioDto.ACTIVO);
        try {
            calendarioService.crear(solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^el actor intenta crear un calendario$")
    public void el_actor_intenta_crear_un_calendario() {
        CrearCalendarioRequestDto solicitud = new CrearCalendarioRequestDto()
                .codigo("CAL-" + CalendarioFixtures.nuevoSufijo())
                .nombre("Calendario de prueba BDD")
                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO)
                .fechaFin(CalendarioFixtures.FIN_CALENDARIO)
                .estado(EstadoCalendarioDto.ACTIVO);
        try {
            calendarioService.crear(solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
