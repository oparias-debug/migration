package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.FechaResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.dto.TipoPeriodoDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-10-calcular-fecha-resultante.feature. Las consultas no requieren rol (RN18). */
public class Adm04CalcularFechaResultante {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private String codigoCalendarioConsultado;
    private LocalDate fechaInicial;
    private int diasHabiles;
    private LocalDate fechaResultanteEsperada;
    private LocalDate noLaboralInicio;
    private LocalDate noLaboralFin;
    private FechaResultanteResponseDto ultimoResultado;

    public Adm04CalcularFechaResultante(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^una fecha inicial y un número de días hábiles a adicionar$")
    public void una_fecha_inicial_y_un_numero_de_dias_habiles_a_adicionar() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        fechaInicial = calendario.getFechaInicio().plusDays(30);
        noLaboralInicio = fechaInicial.plusDays(1);
        noLaboralFin = fechaInicial.plusDays(3);
        diasHabiles = 5;
        // Días contados: +4,+5,+6,+7,+8 (los días +1..+3 son NO_LABORAL y no cuentan) => resultante = +8.
        fechaResultanteEsperada = fechaInicial.plusDays(8);
        calendarioService.agregarPeriodoLaboral(calendario.getCodigo(),
                new PeriodoLaboralRequestDto().codigo("PER-LAB-" + CalendarioFixtures.nuevoSufijo())
                        .nombre("Período LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ").fechaInicio(fechaInicial)
                                .fechaFin(fechaInicial.plusDays(20))));
        calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(),
                new PeriodoNoLaboralRequestDto().codigo("PER-NOLAB-" + CalendarioFixtures.nuevoSufijo())
                        .nombre("Período NO_LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ").fechaInicio(noLaboralInicio)
                                .fechaFin(noLaboralFin)));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Dado("^los días que caen en períodos NO_LABORAL dentro de ese rango no deben contarse$")
    public void los_dias_que_caen_en_periodos_no_laboral_no_deben_contarse() {
        for (LocalDate fecha = noLaboralInicio; !fecha.isAfter(noLaboralFin); fecha = fecha.plusDays(1)) {
            assertThat(calendarioService.consultarTipoDia(codigoCalendarioConsultado, fecha).getTipoDia())
                    .isEqualTo(TipoPeriodoDto.NO_LABORAL);
        }
    }

    @Cuando("^cualquier usuario solicita la fecha resultante de sumar esos días hábiles$")
    public void cualquier_usuario_solicita_la_fecha_resultante_de_sumar_esos_dias_habiles() {
        ultimoResultado = calendarioService.calcularFechaResultante(codigoCalendarioConsultado, fechaInicial,
                diasHabiles);
    }

    @Entonces("^el sistema responde la fecha LABORAL resultante, sin contar los días NO_LABORAL$")
    public void el_sistema_responde_la_fecha_laboral_resultante() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getFechaResultante()).isEqualTo(fechaResultanteEsperada);
        assertThat(ultimoResultado.getDiasHabiles()).isEqualTo(diasHabiles);
    }

    @Dado("^que la fecha resultante del cálculo no cae en ningún período LABORAL del calendario$")
    public void que_la_fecha_resultante_no_cae_en_ningun_periodo_laboral() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        fechaInicial = calendario.getFechaInicio().plusDays(30);
        // diasHabiles = 0: el cálculo no avanza y clasifica fechaInicial tal cual (cae en NO_LABORAL).
        diasHabiles = 0;
        calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(),
                new PeriodoNoLaboralRequestDto().codigo("PER-NOLAB-" + CalendarioFixtures.nuevoSufijo())
                        .nombre("Período NO_LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                                .fechaInicio(fechaInicial.minusDays(2)).fechaFin(fechaInicial.plusDays(2))));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Dado("^que la fecha resultante del cálculo no cae en ningún período definido del calendario$")
    public void que_la_fecha_resultante_no_cae_en_ningun_periodo_definido() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        fechaInicial = calendario.getFechaInicio().plusDays(30);
        // diasHabiles = 0: el cálculo no avanza y clasifica fechaInicial tal cual (sin ningún período).
        diasHabiles = 0;
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Cuando("^cualquier usuario solicita la fecha resultante de sumar días hábiles$")
    public void cualquier_usuario_solicita_la_fecha_resultante_de_sumar_dias_habiles() {
        try {
            ultimoResultado = calendarioService.calcularFechaResultante(codigoCalendarioConsultado, fechaInicial,
                    diasHabiles);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^cualquier usuario solicita la fecha resultante de sumar días hábiles sobre ese código de calendario$")
    public void cualquier_usuario_solicita_la_fecha_resultante_sobre_ese_codigo_de_calendario() {
        try {
            ultimoResultado = calendarioService
                    .calcularFechaResultante(contextoCalendario.getCodigoCalendarioInexistente(), LocalDate.now(), 1);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    private Usuario nuevoAdministradorPersistido() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        Usuario administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        return administrador;
    }
}
