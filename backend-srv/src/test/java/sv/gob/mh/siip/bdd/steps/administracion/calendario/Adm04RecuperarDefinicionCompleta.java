package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioConsultaService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-12-recuperar-definicion-completa.feature. */
public class Adm04RecuperarDefinicionCompleta {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioConsultaService calendarioService;
    private final ContextoValidacionBdd contextoValidacion;

    private CalendarioDto ultimoResultado;

    // Real codigo del calendario creado por el Dado y su literal de origen (distinto del literal del
    // .feature): ver nota en Adm04ComunCalendario sobre por que los escenarios no pueden compartir
    // codigos literales.
    private String codigoCalendarioLiteralCreado;
    private String codigoCalendarioReal;

    public Adm04RecuperarDefinicionCompleta(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioConsultaService calendarioService,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\" con períodos LABORAL, NO_LABORAL y una excepción registrada$")
    public void que_existe_un_calendario_con_periodos_y_excepcion(String codigoCalendarioLiteral) {
        codigoCalendarioLiteralCreado = codigoCalendarioLiteral;
        codigoCalendarioReal = codigoCalendarioLiteral + "-" + CalendarioFixtures.nuevoSufijo();
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        var administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        Calendario calendario = CalendarioFixtures.nuevoCalendario(codigoCalendarioReal, administrador);
        CalendarioFixtures.agregarPeriodo(calendario, "LAB-01", TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30)));
        CalendarioFixtures.agregarPeriodo(calendario, "NOLAB-01", TipoPeriodo.NO_LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31)));
        CalendarioFixtures.agregarExcepcion(calendario, LocalDate.of(2026, 5, 1), TipoExcepcion.DIA_NO_LABORAL);
        calendarioRepository.save(calendario);
    }

    @Cuando("^cualquier usuario recupera la definición del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_recupera_la_definicion_del_calendario(String codigoCalendarioLiteral) {
        String codigoCalendario = codigoCalendarioLiteral.equals(codigoCalendarioLiteralCreado) ? codigoCalendarioReal
                : codigoCalendarioLiteral;
        try {
            ultimoResultado = calendarioService.recuperarDefinicion(codigoCalendario);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema devuelve el código, nombre, descripción, fecha_desde, fecha_hasta, estado y todos los CalendarItems \\(períodos LABORAL, NO_LABORAL y excepciones\\) del calendario$")
    public void el_sistema_devuelve_la_definicion_completa() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getCodigo()).isNotBlank();
        assertThat(ultimoResultado.getNombre()).isNotBlank();
        assertThat(ultimoResultado.getFechaInicio()).isNotNull();
        assertThat(ultimoResultado.getFechaFin()).isNotNull();
        assertThat(ultimoResultado.getEstado()).isNotNull();
        assertThat(ultimoResultado.getItems()).anyMatch(PeriodoLaboralDto.class::isInstance);
        assertThat(ultimoResultado.getItems()).anyMatch(PeriodoNoLaboralDto.class::isInstance);
        assertThat(ultimoResultado.getItems()).anyMatch(ExcepcionDto.class::isInstance);
    }
}
