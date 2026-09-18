package sv.gob.mh.siip.bdd.steps.administracion;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PertenenciaPeriodoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-06-consultar-pertenencia-periodo.feature. Las consultas no requieren rol (RN18). */
public class Adm04ConsultarPertenenciaPeriodo {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private String codigoCalendarioConsultado;
    private String codigoPeriodoConsultado;
    private LocalDate rangoInicio;
    private LocalDate rangoFin;

    public Adm04ConsultarPertenenciaPeriodo(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un período \"([^\"]*)\" definido en un calendario$")
    public void un_periodo_definido_en_un_calendario(String tipoPeriodo) {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        rangoInicio = calendario.getFechaInicio().plusDays(10);
        rangoFin = calendario.getFechaInicio().plusDays(20);
        codigoPeriodoConsultado = "PER-" + CalendarioFixtures.nuevoSufijo();
        RecurrenciaUnaVezDto recurrencia = new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                .fechaInicio(rangoInicio).fechaFin(rangoFin);
        if ("LABORAL".equals(tipoPeriodo)) {
            calendarioService.agregarPeriodoLaboral(calendario.getCodigo(), new PeriodoLaboralRequestDto()
                    .codigo(codigoPeriodoConsultado).nombre("Período LABORAL (BDD)").recurrencia(recurrencia));
        } else {
            calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(), new PeriodoNoLaboralRequestDto()
                    .codigo(codigoPeriodoConsultado).nombre("Período NO_LABORAL (BDD)").recurrencia(recurrencia));
        }
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Cuando("^cualquier usuario consulta si una fecha \"([^\"]*)\" a ese período$")
    public void cualquier_usuario_consulta_si_una_fecha_pertenece_a_ese_periodo(String resultadoEsperado) {
        LocalDate fecha = "pertenece".equals(resultadoEsperado) ? rangoInicio.plusDays(2) : rangoFin.plusDays(5);
        PertenenciaPeriodoResponseDto resultado = calendarioService
                .consultarPertenenciaPeriodo(codigoCalendarioConsultado, codigoPeriodoConsultado, fecha);
        contextoCalendario
                .setUltimaRespuestaTexto(Boolean.TRUE.equals(resultado.getPertenece()) ? "pertenece" : "no pertenece");
    }

    @Cuando("^cualquier usuario consulta la pertenencia de una fecha a un período sobre ese código de calendario$")
    public void cualquier_usuario_consulta_la_pertenencia_sobre_ese_codigo_de_calendario() {
        try {
            calendarioService.consultarPertenenciaPeriodo(contextoCalendario.getCodigoCalendarioInexistente(),
                    "PER-INEXISTENTE", LocalDate.now());
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^un calendario existente sin ningún período registrado con el código indicado$")
    public void un_calendario_existente_sin_ningun_periodo_registrado_con_el_codigo_indicado() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorPersistido()));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
        codigoPeriodoConsultado = "PER-INEXISTENTE-" + CalendarioFixtures.nuevoSufijo();
    }

    @Cuando("^cualquier usuario consulta la pertenencia de una fecha a ese código de período$")
    public void cualquier_usuario_consulta_la_pertenencia_a_ese_codigo_de_periodo() {
        try {
            calendarioService.consultarPertenenciaPeriodo(codigoCalendarioConsultado, codigoPeriodoConsultado,
                    LocalDate.now());
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
