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
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.dto.TipoPeriodoDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-09-consultar-dias-laboral-entre-fechas.feature. Las consultas no requieren rol (RN18). */
public class Adm04ConsultarDiasLaboralesEntreFechas {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private String codigoCalendarioConsultado;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private LocalDate interseccionInicio;
    private LocalDate interseccionFin;
    private int diasLaboralesEsperados;
    private DiasLaboralesEntreFechasResponseDto ultimoResultado;

    public Adm04ConsultarDiasLaboralesEntreFechas(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un calendario con múltiples períodos LABORAL y NO_LABORAL entre dos fechas dadas$")
    public void un_calendario_con_multiples_periodos_laboral_y_no_laboral_entre_dos_fechas_dadas() {
        prepararCalendarioConInterseccion();
        // Rango consultado [fechaInicial, fechaFinal] = 10 días; NO_LABORAL cubre 3 de ellos (RN02): 10 - 3 = 7.
        diasLaboralesEsperados = 7;
    }

    @Dado("^ambas fechas están dentro del rango del calendario y son consistentes entre sí$")
    public void ambas_fechas_estan_dentro_del_rango_del_calendario_y_son_consistentes() {
        assertThat(fechaInicial).isBeforeOrEqualTo(fechaFinal);
        Calendario calendario = contextoCalendario.getCalendarioActual();
        assertThat(fechaInicial).isBetween(calendario.getFechaInicio(), calendario.getFechaFin());
        assertThat(fechaFinal).isBetween(calendario.getFechaInicio(), calendario.getFechaFin());
    }

    @Cuando("^cualquier usuario consulta los días LABORAL entre esas dos fechas$")
    public void cualquier_usuario_consulta_los_dias_laboral_entre_esas_dos_fechas() {
        ultimoResultado = calendarioService.consultarDiasLaboralesEntreFechas(codigoCalendarioConsultado,
                fechaInicial, fechaFinal);
    }

    @Entonces("^el sistema responde el número total de días LABORAL considerando todos los períodos del rango$")
    public void el_sistema_responde_el_numero_total_de_dias_laboral() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getDiasLaborales()).isEqualTo(diasLaboralesEsperados);
    }

    @Dado("^un rango de fechas donde un período LABORAL se intersecta con un período NO_LABORAL$")
    public void un_rango_de_fechas_donde_un_periodo_laboral_se_intersecta_con_un_periodo_no_laboral() {
        prepararCalendarioConInterseccion();
    }

    @Cuando("^cualquier usuario consulta los días LABORAL en ese rango$")
    public void cualquier_usuario_consulta_los_dias_laboral_en_ese_rango() {
        ultimoResultado = calendarioService.consultarDiasLaboralesEntreFechas(codigoCalendarioConsultado,
                fechaInicial, fechaFinal);
    }

    @Entonces("^los días de la intersección no se cuentan como LABORAL$")
    public void los_dias_de_la_interseccion_no_se_cuentan_como_laboral() {
        assertThat(ultimoResultado.getDiasLaborales()).isEqualTo(7);
        for (LocalDate fecha = interseccionInicio; !fecha.isAfter(interseccionFin); fecha = fecha.plusDays(1)) {
            assertThat(calendarioService.consultarTipoDia(codigoCalendarioConsultado, fecha).getTipoDia())
                    .isEqualTo(TipoPeriodoDto.NO_LABORAL);
        }
    }

    @Dado("^que la fecha inicial o la fecha final indicadas no están dentro del rango de fechas del calendario$")
    public void que_la_fecha_inicial_o_final_no_estan_dentro_del_rango_del_calendario() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
        fechaInicial = calendario.getFechaInicio().minusDays(5);
        fechaFinal = calendario.getFechaInicio().plusDays(10);
    }

    @Dado("^que la fecha inicial indicada es posterior a la fecha final indicada$")
    public void que_la_fecha_inicial_es_posterior_a_la_fecha_final() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
        fechaInicial = calendario.getFechaInicio().plusDays(10);
        fechaFinal = calendario.getFechaInicio().plusDays(5);
    }

    @Cuando("^cualquier usuario consulta los días LABORAL entre esas fechas$")
    public void cualquier_usuario_consulta_los_dias_laboral_entre_esas_fechas() {
        try {
            ultimoResultado = calendarioService.consultarDiasLaboralesEntreFechas(codigoCalendarioConsultado,
                    fechaInicial, fechaFinal);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^cualquier usuario consulta los días LABORAL entre dos fechas sobre ese código de calendario$")
    public void cualquier_usuario_consulta_los_dias_laboral_sobre_ese_codigo_de_calendario() {
        try {
            ultimoResultado = calendarioService.consultarDiasLaboralesEntreFechas(
                    contextoCalendario.getCodigoCalendarioInexistente(), LocalDate.now(), LocalDate.now());
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    /** Calendario con un período LABORAL de 10 días y uno NO_LABORAL de 3 días intersectados (dias 5-7). */
    private Calendario prepararCalendarioConInterseccion() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        fechaInicial = calendario.getFechaInicio().plusDays(30);
        fechaFinal = fechaInicial.plusDays(9);
        interseccionInicio = fechaInicial.plusDays(4);
        interseccionFin = fechaInicial.plusDays(6);
        calendarioService.agregarPeriodoLaboral(calendario.getCodigo(),
                new PeriodoLaboralRequestDto().codigo("PER-LAB-" + CalendarioFixtures.nuevoSufijo())
                        .nombre("Período LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ").fechaInicio(fechaInicial)
                                .fechaFin(fechaFinal)));
        calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(),
                new PeriodoNoLaboralRequestDto().codigo("PER-NOLAB-" + CalendarioFixtures.nuevoSufijo())
                        .nombre("Período NO_LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                                .fechaInicio(interseccionInicio).fechaFin(interseccionFin)));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
        return calendario;
    }

    private Usuario nuevoAdministradorPersistido() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        Usuario administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        return administrador;
    }
}
