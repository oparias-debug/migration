package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioConsultaService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-06-consultar-pertenencia-a-periodo.feature. */
public class Adm04ConsultarPertenenciaPeriodo {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioConsultaService calendarioService;
    private final ContextoValidacionBdd contextoValidacion;

    private PertenenciaPeriodoResponseDto ultimoResultado;

    // Real codigo del calendario creado por el Dado (distinto del literal del .feature): ver nota en
    // Adm04ComunCalendario sobre por que los escenarios no pueden compartir codigos literales.
    private String codigoCalendarioReal;

    public Adm04ConsultarPertenenciaPeriodo(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioConsultaService calendarioService,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\" con un período \"([^\"]*)\"$")
    public void que_existe_un_calendario_activo_con_un_periodo(String codigoCalendarioLiteral,
            String codigoPeriodo) {
        codigoCalendarioReal = codigoCalendarioLiteral + "-" + CalendarioFixtures.nuevoSufijo();
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        var administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        Calendario calendario = CalendarioFixtures.nuevoCalendario(codigoCalendarioReal, administrador);
        CalendarioFixtures.agregarPeriodo(calendario, codigoPeriodo, TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30)));
        calendarioRepository.save(calendario);
    }

    @Cuando("^cualquier usuario consulta si la fecha \"([^\"]*)\" pertenece al período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_si_la_fecha_pertenece_al_periodo(String fecha, String codigoPeriodo,
            String codigoCalendarioLiteral) {
        consultar(codigoCalendarioReal, codigoPeriodo, fecha);
    }

    @Cuando("^cualquier usuario consulta si una fecha pertenece al período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_si_una_fecha_pertenece_a_periodo_inexistente(String codigoPeriodo,
            String codigoCalendarioLiteral) {
        consultar(codigoCalendarioReal, codigoPeriodo, "2026-03-10");
    }

    @Cuando("^cualquier usuario consulta la pertenencia de una fecha a un período del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_pertenencia_en_calendario_inexistente(String codigoCalendarioLiteral) {
        consultar(codigoCalendarioLiteral, "LAB-01", "2026-03-10");
    }

    private void consultar(String codigoCalendario, String codigoPeriodo, String fecha) {
        try {
            ultimoResultado = calendarioService.consultarPertenenciaPeriodo(codigoCalendario, codigoPeriodo,
                    LocalDate.parse(fecha));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema responde \"([^\"]*)\"$")
    public void el_sistema_responde(String perteneceEsperado) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getPertenece()).isEqualTo(Boolean.parseBoolean(perteneceEsperado));
    }
}
