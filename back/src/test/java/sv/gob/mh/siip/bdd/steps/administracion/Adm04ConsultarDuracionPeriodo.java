package sv.gob.mh.siip.bdd.steps.administracion;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.DuracionPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.MesDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaMensualDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-07-consultar-duracion-periodo.feature. Las consultas no requieren rol (RN18). */
public class Adm04ConsultarDuracionPeriodo {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private String codigoCalendarioConsultado;
    private String codigoPeriodoConsultado;
    private int duracionEsperada;
    private DuracionPeriodoResponseDto ultimoResultado;

    public Adm04ConsultarDuracionPeriodo(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un período con recurrencia UNA_VEZ o SEMANAL definido sobre un rango de fechas$")
    public void un_periodo_con_recurrencia_una_vez_o_semanal_definido_sobre_un_rango_de_fechas() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        LocalDate inicio = calendario.getFechaInicio().plusDays(30);
        LocalDate fin = inicio.plusDays(10);
        duracionEsperada = (int) ChronoUnit.DAYS.between(inicio, fin) + 1;
        codigoPeriodoConsultado = "PER-" + CalendarioFixtures.nuevoSufijo();
        calendarioService.agregarPeriodoLaboral(calendario.getCodigo(),
                new PeriodoLaboralRequestDto().codigo(codigoPeriodoConsultado).nombre("Período LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ").fechaInicio(inicio)
                                .fechaFin(fin)));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Entonces("^el sistema responde la duración en días, considerando la duración tradicional de los meses involucrados$")
    public void el_sistema_responde_la_duracion_en_dias() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getDuracionDias()).isEqualTo(duracionEsperada);
    }

    @Dado("^un período con recurrencia MENSUAL que declara ciertos días del mes sobre un conjunto de meses$")
    public void un_periodo_con_recurrencia_mensual_que_declara_dias_sobre_meses() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        // FEBRERO 2026 tiene 28 días (no bisiesto): solo el día 15 aplica (30 y 31 se descartan, RN11).
        // ABRIL tiene 30 días: 15 y 30 aplican, 31 se descarta.
        RecurrenciaMensualDto recurrencia = new RecurrenciaMensualDto().tipoRecurrencia("MENSUAL");
        Set.of(15, 30, 31).forEach(recurrencia::addDiasDelMesItem);
        recurrencia.addMesesItem(MesDto.valueOf(Month.FEBRUARY.name()));
        recurrencia.addMesesItem(MesDto.valueOf(Month.APRIL.name()));
        codigoPeriodoConsultado = "PER-" + CalendarioFixtures.nuevoSufijo();
        calendarioService.agregarPeriodoLaboral(calendario.getCodigo(), new PeriodoLaboralRequestDto()
                .codigo(codigoPeriodoConsultado).nombre("Período MENSUAL (BDD)").recurrencia(recurrencia));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
        duracionEsperada = 1 + 2; // 1 día válido en febrero + 2 días válidos en abril.
    }

    @Entonces("^el sistema responde el agregado de los días declarados en cada uno de esos meses$")
    public void elSistemaRespondeElAgregadoDeLosDiasDeclarados() {
        el_sistema_responde_la_duracion_en_dias();
    }

    @Entonces("^el sistema no suma días que no pertenezcan a la declaración de esos meses$")
    public void el_sistema_no_suma_dias_que_no_pertenezcan_a_la_declaracion_de_esos_meses() {
        // Suma ingenua (3 días x 2 meses = 6) vs la real (3): confirma que 30/31 de febrero no se cuentan.
        assertThat(ultimoResultado.getDuracionDias()).isLessThan(6).isEqualTo(3);
    }

    @Dado("^que el código de calendario o el código de período indicado no existe$")
    public void que_el_codigo_de_calendario_o_de_periodo_indicado_no_existe() {
        codigoCalendarioConsultado = "NOEXISTE-" + CalendarioFixtures.nuevoSufijo();
        codigoPeriodoConsultado = "PER-INEXISTENTE-" + CalendarioFixtures.nuevoSufijo();
    }

    @Cuando("^cualquier usuario consulta la duración de ese período$")
    public void cualquier_usuario_consulta_la_duracion_de_ese_periodo() {
        try {
            ultimoResultado = calendarioService.consultarDuracionPeriodo(codigoCalendarioConsultado,
                    codigoPeriodoConsultado);
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
