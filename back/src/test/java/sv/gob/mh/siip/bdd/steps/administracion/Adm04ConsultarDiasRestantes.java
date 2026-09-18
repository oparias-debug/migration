package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.DiasRestantesResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-08-consultar-dias-restantes.feature. Las consultas no requieren rol (RN18). */
public class Adm04ConsultarDiasRestantes {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private String codigoCalendarioConsultado;
    private String codigoPeriodoConsultado;
    private LocalDate fechaConsultada;
    private int diasRestantesEsperados;
    private DiasRestantesResponseDto ultimoResultado;

    public Adm04ConsultarDiasRestantes(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^una fecha que está dentro de un período LABORAL identificado por su código$")
    public void una_fecha_que_esta_dentro_de_un_periodo_laboral_identificado_por_su_codigo() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        LocalDate inicio = calendario.getFechaInicio().plusDays(30);
        LocalDate fin = inicio.plusDays(10);
        fechaConsultada = inicio.plusDays(3);
        diasRestantesEsperados = (int) ChronoUnit.DAYS.between(fechaConsultada, fin);
        codigoPeriodoConsultado = "PER-" + CalendarioFixtures.nuevoSufijo();
        calendarioService.agregarPeriodoLaboral(calendario.getCodigo(),
                new PeriodoLaboralRequestDto().codigo(codigoPeriodoConsultado).nombre("Período LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ").fechaInicio(inicio)
                                .fechaFin(fin)));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Entonces("^el sistema responde el número de días que faltan hasta el fin del período$")
    public void el_sistema_responde_el_numero_de_dias_que_faltan_hasta_el_fin_del_periodo() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getDiasRestantes()).isEqualTo(diasRestantesEsperados);
    }

    @Dado("^una fecha que no está dentro del período LABORAL identificado por su código$")
    public void una_fecha_que_no_esta_dentro_del_periodo_laboral_identificado_por_su_codigo() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        LocalDate inicio = calendario.getFechaInicio().plusDays(30);
        LocalDate fin = inicio.plusDays(10);
        fechaConsultada = fin.plusDays(5);
        codigoPeriodoConsultado = "PER-" + CalendarioFixtures.nuevoSufijo();
        calendarioService.agregarPeriodoLaboral(calendario.getCodigo(),
                new PeriodoLaboralRequestDto().codigo(codigoPeriodoConsultado).nombre("Período LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ").fechaInicio(inicio)
                                .fechaFin(fin)));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Cuando("^cualquier usuario consulta los días restantes hasta el fin de ese período desde esa fecha$")
    public void cualquier_usuario_consulta_los_dias_restantes_desde_esa_fecha() {
        try {
            ultimoResultado = calendarioService.consultarDiasRestantesPeriodo(codigoCalendarioConsultado,
                    codigoPeriodoConsultado, fechaConsultada);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^que el código de calendario o el código de período LABORAL indicado no existe$")
    public void que_el_codigo_de_calendario_o_de_periodo_laboral_indicado_no_existe() {
        codigoCalendarioConsultado = "NOEXISTE-" + CalendarioFixtures.nuevoSufijo();
        codigoPeriodoConsultado = "PER-INEXISTENTE-" + CalendarioFixtures.nuevoSufijo();
        fechaConsultada = LocalDate.now();
    }

    @Cuando("^cualquier usuario consulta los días restantes hasta el fin de ese período$")
    public void cualquier_usuario_consulta_los_dias_restantes_hasta_el_fin_de_ese_periodo() {
        try {
            ultimoResultado = calendarioService.consultarDiasRestantesPeriodo(codigoCalendarioConsultado,
                    codigoPeriodoConsultado, fechaConsultada);
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
