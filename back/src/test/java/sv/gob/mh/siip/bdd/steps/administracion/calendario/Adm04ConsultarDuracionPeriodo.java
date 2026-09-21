package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-07-consultar-duracion-de-periodo.feature. */
public class Adm04ConsultarDuracionPeriodo {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioService calendarioService;
    private final ContextoValidacionBdd contextoValidacion;

    private DuracionPeriodoResponseDto ultimoResultado;

    // Real codigo del calendario creado por el Dado (distinto del literal del .feature): ver nota en
    // Adm04ComunCalendario sobre por que los escenarios no pueden compartir codigos literales.
    private String codigoCalendarioReal;

    public Adm04ConsultarDuracionPeriodo(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, PeriodoRepository periodoRepository,
            CalendarioService calendarioService, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\"$")
    public void que_existe_un_calendario_activo_con_codigo(String codigoCalendarioLiteral) {
        codigoCalendarioReal = codigoCalendarioLiteral + "-" + CalendarioFixtures.nuevoSufijo();
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        var administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        calendarioRepository.save(CalendarioFixtures.nuevoCalendario(codigoCalendarioReal, administrador));
    }

    @Dado("^que el período LABORAL \"([^\"]*)\" del calendario \"([^\"]*)\" se intersecta parcialmente con el período NO_LABORAL \"([^\"]*)\"$")
    public void que_el_periodo_laboral_se_intersecta_parcialmente_con_no_laboral(String codigoLaboral,
            String codigoCalendarioLiteral, String codigoNoLaboral) {
        // calendario ya fue persistido por el Background: se agregan los periodos directamente via
        // PeriodoRepository (no via calendario.getPeriodos().add(...), coleccion perezosa de Hibernate).
        Calendario calendario = calendarioRepository.findByCodigo(codigoCalendarioReal).orElseThrow();
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, codigoLaboral, TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 20))));
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, codigoNoLaboral, TipoPeriodo.NO_LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 31))));
    }

    @Dado("^que el período NO_LABORAL \"([^\"]*)\" del calendario \"([^\"]*)\" se intersecta parcialmente con el período LABORAL \"([^\"]*)\"$")
    public void que_el_periodo_no_laboral_se_intersecta_parcialmente_con_laboral(String codigoNoLaboral,
            String codigoCalendarioLiteral, String codigoLaboral) {
        que_el_periodo_laboral_se_intersecta_parcialmente_con_no_laboral(codigoLaboral, codigoCalendarioLiteral,
                codigoNoLaboral);
    }

    @Dado("^que el período \"([^\"]*)\" del calendario \"([^\"]*)\" tiene recurrencia \"([^\"]*)\"$")
    public void que_el_periodo_tiene_recurrencia(String codigoPeriodo, String codigoCalendarioLiteral,
            String recurrencia) {
        Calendario calendario = calendarioRepository.findByCodigo(codigoCalendarioReal).orElseThrow();
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, codigoPeriodo, TipoPeriodo.LABORAL,
                CalendarioFixtures.parseRecurrenciaDominio(recurrencia)));
    }

    @Cuando("^cualquier usuario consulta la duración del período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_la_duracion_del_periodo(String codigoPeriodo,
            String codigoCalendarioLiteral) {
        try {
            ultimoResultado = calendarioService.consultarDuracionPeriodo(codigoCalendarioReal, codigoPeriodo);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^cualquier usuario consulta la duración de un período del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_la_duracion_en_calendario_inexistente(String codigoCalendarioLiteral) {
        try {
            ultimoResultado = calendarioService.consultarDuracionPeriodo(codigoCalendarioLiteral, "LAB-01");
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema devuelve la duración en días excluyendo los días de intersección con \"([^\"]*)\"$")
    public void el_sistema_devuelve_la_duracion_excluyendo_la_interseccion(String codigoNoLaboral) {
        assertThat(ultimoResultado).isNotNull();
        // LAB-01: 2026-01-01 a 2026-01-20 (20 días); interseccion con NOLAB (2026-01-10 a 2026-01-31): 11 días.
        assertThat(ultimoResultado.getDuracionDias()).isEqualTo(9);
    }

    @Entonces("^el sistema devuelve la duración en días incluyendo todos los días del período, sin excluir ninguno$")
    public void el_sistema_devuelve_la_duracion_incluyendo_todos_los_dias() {
        assertThat(ultimoResultado).isNotNull();
        // NOLAB-01: 2026-01-10 a 2026-01-31 (22 días), sin exclusiones.
        assertThat(ultimoResultado.getDuracionDias()).isEqualTo(22);
    }

    @Entonces("^el sistema calcula la duración \"([^\"]*)\"$")
    public void el_sistema_calcula_la_duracion(String criterio) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getDuracionDias()).isGreaterThan(0);
    }
}
