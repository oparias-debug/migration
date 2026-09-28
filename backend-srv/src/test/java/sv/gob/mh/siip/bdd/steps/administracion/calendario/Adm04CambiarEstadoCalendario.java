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
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-15-cambiar-estado-calendario.feature. */
public class Adm04CambiarEstadoCalendario {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoValidacionBdd contextoValidacion;

    private CalendarioDto ultimoResultado;

    // Real codigo del calendario creado por el Dado (distinto del literal del .feature): ver nota en
    // Adm04ComunCalendario sobre por que los escenarios no pueden compartir codigos literales.
    private String codigoCalendarioReal;

    public Adm04CambiarEstadoCalendario(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioService calendarioService,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que existe un calendario con código \"([^\"]*)\" con períodos LABORAL, NO_LABORAL y una excepción$")
    public void que_existe_un_calendario_con_periodos_y_excepcion(String codigoCalendarioLiteral) {
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

    @Dado("^el calendario \"([^\"]*)\" está en estado \"([^\"]*)\"$")
    public void que_el_calendario_esta_en_estado(String codigoCalendarioLiteral, String estado) {
        Calendario calendario = calendarioRepository.findByCodigo(codigoCalendarioReal).orElseThrow();
        calendario.setEstado(EstadoCalendario.valueOf(estado));
        calendarioRepository.save(calendario);
    }

    @Cuando("^el actor cambia el estado del calendario \"([^\"]*)\" a \"([^\"]*)\"$")
    public void el_actor_cambia_el_estado_del_calendario(String codigoCalendarioLiteral, String estadoNuevo) {
        CambiarEstadoCalendarioRequestDto request = new CambiarEstadoCalendarioRequestDto()
                .estado(EstadoCalendarioDto.valueOf(estadoNuevo));
        ultimoResultado = calendarioService.cambiarEstado(codigoCalendarioReal, request);
    }

    @Cuando("^el actor intenta cambiar el estado del calendario \"([^\"]*)\"$")
    public void el_actor_intenta_cambiar_el_estado_sin_permisos(String codigoCalendarioLiteral) {
        CambiarEstadoCalendarioRequestDto request = new CambiarEstadoCalendarioRequestDto()
                .estado(EstadoCalendarioDto.INACTIVO);
        try {
            calendarioService.cambiarEstado(codigoCalendarioReal, request);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el calendario queda en estado \"([^\"]*)\"$")
    public void el_calendario_queda_en_estado(String estadoEsperado) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getEstado()).isEqualTo(EstadoCalendarioDto.valueOf(estadoEsperado));
    }

    @Entonces("^todos los CalendarItems del calendario \"([^\"]*)\" heredan el estado \"([^\"]*)\"$")
    public void todos_los_calendaritems_heredan_el_estado(String codigoCalendario, String estadoEsperado) {
        assertThat(ultimoResultado).isNotNull();
        EstadoCalendarioDto esperado = EstadoCalendarioDto.valueOf(estadoEsperado);
        assertThat(ultimoResultado.getItems()).isNotEmpty();
        ultimoResultado.getItems().forEach(item -> {
            if (item instanceof sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto p) {
                assertThat(p.getEstado()).isEqualTo(esperado);
            } else if (item instanceof sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto p) {
                assertThat(p.getEstado()).isEqualTo(esperado);
            } else if (item instanceof sv.gob.mh.siip.model.administracion.dto.ExcepcionDto e) {
                assertThat(e.getEstado()).isEqualTo(esperado);
            }
        });
    }
}
