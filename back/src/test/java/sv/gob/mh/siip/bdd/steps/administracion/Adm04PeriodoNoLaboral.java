package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.dto.PeriodoDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.dto.TipoPeriodoDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-03-periodo-no-laboral.feature. */
public class Adm04PeriodoNoLaboral {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private PeriodoDto ultimoResultado;

    public Adm04PeriodoNoLaboral(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            PeriodoRepository periodoRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Cuando("^el actor agrega un período NO_LABORAL con recurrencia \"([^\"]*)\" y los datos correspondientes$")
    public void el_actor_agrega_un_periodo_no_laboral_con_recurrencia(String recurrencia) {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        PeriodoNoLaboralRequestDto request = new PeriodoNoLaboralRequestDto()
                .codigo(contextoCalendario.getCodigoPeriodoPreparado())
                .nombre("Período NO_LABORAL de prueba BDD")
                .recurrencia(Adm04PeriodoLaboral.construirRecurrencia(recurrencia, calendario));
        ultimoResultado = calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(), request);
        Periodo persistido = periodoRepository
                .findByCalendario_CodigoAndCodigo(calendario.getCodigo(), request.getCodigo()).orElseThrow();
        contextoCalendario.setPeriodoActual(persistido);
    }

    @Entonces("^el período NO_LABORAL queda registrado en el calendario$")
    public void el_periodo_no_laboral_queda_registrado_en_el_calendario() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getTipo()).isEqualTo(TipoPeriodoDto.NO_LABORAL);
        assertThat(periodoRepository.existsByCalendario_CodigoAndCodigo(
                contextoCalendario.getCalendarioActual().getCodigo(), ultimoResultado.getCodigo())).isTrue();
    }

    @Cuando("^el actor intenta agregar un nuevo período NO_LABORAL con ese mismo código en el mismo calendario$")
    public void el_actor_intenta_agregar_un_nuevo_periodo_no_laboral_con_ese_mismo_codigo() {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        PeriodoNoLaboralRequestDto request = new PeriodoNoLaboralRequestDto()
                .codigo(contextoCalendario.getCodigoPeriodoPreparado())
                .nombre("Período NO_LABORAL duplicado (BDD)")
                .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                        .fechaInicio(calendario.getFechaInicio())
                        .fechaFin(calendario.getFechaInicio().plusDays(5)));
        try {
            calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(), request);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^que el actor prepara los datos de un nuevo período NO_LABORAL con recurrencia UNA_VEZ o SEMANAL$")
    public void que_el_actor_prepara_los_datos_de_un_nuevo_periodo_no_laboral() {
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(),
                        autenticarNuevoAdministrador()));
        contextoCalendario.setCalendarioActual(calendario);
        contextoCalendario.setTipoPeriodoPreparado(TipoPeriodo.NO_LABORAL);
    }

    @Cuando("^el actor agrega un período NO_LABORAL cuyo rango de fechas excede el rango del calendario$")
    public void el_actor_agrega_un_periodo_no_laboral_cuyo_rango_excede_el_rango_del_calendario() {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        PeriodoNoLaboralRequestDto request = new PeriodoNoLaboralRequestDto()
                .codigo("PER-" + CalendarioFixtures.nuevoSufijo())
                .nombre("Período NO_LABORAL fuera de rango (BDD)")
                .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                        .fechaInicio(calendario.getFechaInicio())
                        .fechaFin(calendario.getFechaFin().plusDays(5)));
        try {
            calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(), request);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^un calendario con un período NO_LABORAL ya definido$")
    public void un_calendario_con_un_periodo_no_laboral_ya_definido() {
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(),
                        autenticarNuevoAdministrador()));
        calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(),
                new PeriodoNoLaboralRequestDto().codigo("PER-" + CalendarioFixtures.nuevoSufijo())
                        .nombre("Primer período NO_LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                                .fechaInicio(calendario.getFechaInicio().plusMonths(2))
                                .fechaFin(calendario.getFechaInicio().plusMonths(3))));
        contextoCalendario.setCalendarioActual(calendario);
    }

    @Cuando("^el actor agrega un segundo período NO_LABORAL cuyas fechas se intersectan con el primero$")
    public void el_actor_agrega_un_segundo_periodo_no_laboral_que_se_intersecta_con_el_primero() {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        ultimoResultado = calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(),
                new PeriodoNoLaboralRequestDto().codigo("PER-" + CalendarioFixtures.nuevoSufijo())
                        .nombre("Segundo período NO_LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                                .fechaInicio(calendario.getFechaInicio().plusMonths(2).plusDays(15))
                                .fechaFin(calendario.getFechaInicio().plusMonths(4))));
    }

    @Entonces("^el sistema registra el segundo período NO_LABORAL sin reportar ningún conflicto$")
    public void el_sistema_registra_el_segundo_periodo_no_laboral_sin_conflicto() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(periodoRepository.existsByCalendario_CodigoAndCodigo(
                contextoCalendario.getCalendarioActual().getCodigo(), ultimoResultado.getCodigo())).isTrue();
    }

    @Cuando("^el actor intenta agregar un período NO_LABORAL a un calendario$")
    public void el_actor_intenta_agregar_un_periodo_no_laboral_a_un_calendario() {
        Usuario administrador = usuarioRepository
                .save(CalendarioFixtures.nuevoAdministradorCalendario("admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo()));
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), administrador));
        try {
            calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(),
                    new PeriodoNoLaboralRequestDto().codigo("PER-" + CalendarioFixtures.nuevoSufijo())
                            .nombre("Período NO_LABORAL no autorizado (BDD)")
                            .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                                    .fechaInicio(calendario.getFechaInicio())
                                    .fechaFin(calendario.getFechaInicio().plusDays(5))));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    private Usuario autenticarNuevoAdministrador() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        Usuario administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        return administrador;
    }
}
