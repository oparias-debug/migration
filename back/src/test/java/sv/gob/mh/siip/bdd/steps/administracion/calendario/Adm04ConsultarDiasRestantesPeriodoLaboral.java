package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioConsultaService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-08-consultar-dias-restantes-periodo-laboral.feature. */
public class Adm04ConsultarDiasRestantesPeriodoLaboral {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioConsultaService calendarioService;
    private final ContextoValidacionBdd contextoValidacion;

    private DiasRestantesResponseDto ultimoResultado;

    // Real codigo del calendario creado por el Dado (distinto del literal del .feature): ver nota en
    // Adm04ComunCalendario sobre por que los escenarios no pueden compartir codigos literales.
    private String codigoCalendarioReal;

    public Adm04ConsultarDiasRestantesPeriodoLaboral(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioConsultaService calendarioService,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\" con un período LABORAL \"([^\"]*)\" que finaliza el \"([^\"]*)\"$")
    public void que_existe_un_calendario_con_periodo_laboral_que_finaliza_el(String codigoCalendarioLiteral,
            String codigoPeriodo, String fechaFin) {
        codigoCalendarioReal = codigoCalendarioLiteral + "-" + CalendarioFixtures.nuevoSufijo();
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        var administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        Calendario calendario = CalendarioFixtures.nuevoCalendario(codigoCalendarioReal, administrador);
        CalendarioFixtures.agregarPeriodo(calendario, codigoPeriodo, TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(calendario.getFechaInicio(), LocalDate.parse(fechaFin)));
        calendarioRepository.save(calendario);
    }

    @Cuando("^cualquier usuario consulta los días restantes desde la fecha \"([^\"]*)\" hasta el fin del período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_restantes(String fecha, String codigoPeriodo,
            String codigoCalendarioLiteral) {
        consultar(codigoCalendarioReal, codigoPeriodo, fecha);
    }

    @Cuando("^cualquier usuario consulta los días restantes hasta el fin del período \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_restantes_de_periodo_inexistente(String codigoPeriodo,
            String codigoCalendarioLiteral) {
        consultar(codigoCalendarioReal, codigoPeriodo, "2026-01-01");
    }

    @Cuando("^cualquier usuario consulta los días restantes de un período del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_restantes_en_calendario_inexistente(
            String codigoCalendarioLiteral) {
        consultar(codigoCalendarioLiteral, "LAB-01", "2026-01-01");
    }

    private void consultar(String codigoCalendario, String codigoPeriodo, String fecha) {
        try {
            ultimoResultado = calendarioService.consultarDiasRestantesPeriodoLaboral(codigoCalendario, codigoPeriodo,
                    LocalDate.parse(fecha));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema devuelve \"([^\"]*)\" días restantes$")
    public void el_sistema_devuelve_dias_restantes(String diasEsperados) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getDiasRestantes()).isEqualTo(Integer.parseInt(diasEsperados));
    }

    @Entonces("^el sistema retorna error indicando que la fecha no está dentro del período$")
    public void el_sistema_retorna_error_indicando_que_la_fecha_no_esta_dentro_del_periodo() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }
}
