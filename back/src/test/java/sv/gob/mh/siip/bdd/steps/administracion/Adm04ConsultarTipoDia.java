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
import sv.gob.mh.siip.model.administracion.dto.ExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.TipoPeriodoDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-ADM-04-05-consultar-tipo-dia.feature. Las consultas no requieren rol/autenticacion (RN18),
 * pero los periodos/excepciones de prueba se registran vía {@link CalendarioService} (autenticando
 * un administrador temporal) en lugar de construirlos directamente vía JPA: el campo
 * {@code Periodo.recurrencia} es una asociación LAZY sobre una jerarquía de herencia (Recurrencia),
 * y un Periodo recién releído desde el repositorio expone esa asociación como un proxy Hibernate
 * cuyos chequeos {@code instanceof RecurrenciaUnaVez/...} en {@code CalendarioServiceImpl} fallan
 * silenciosamente si no se fuerza antes su inicialización; pasar por el propio servicio evita ese
 * problema porque conserva en memoria el objeto real recién construido.
 */
public class Adm04ConsultarTipoDia {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private LocalDate fechaConsultada;
    private String codigoCalendarioConsultado;
    private TipoExcepcionDto tipoExcepcionEsperado;
    private TipoDiaResponseDto ultimoResultado;

    public Adm04ConsultarTipoDia(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            CalendarioService calendarioService, ContextoCalendarioBdd contextoCalendario,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un calendario con un período \"([^\"]*)\" que cubre la fecha consultada$")
    public void un_calendario_con_un_periodo_que_cubre_la_fecha_consultada(String tipoPeriodo) {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorAutenticado()));
        fechaConsultada = calendario.getFechaInicio().plusDays(10);
        RecurrenciaUnaVezDto recurrencia = new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                .fechaInicio(fechaConsultada.minusDays(2)).fechaFin(fechaConsultada.plusDays(2));
        agregarPeriodo(calendario.getCodigo(), tipoPeriodo, recurrencia);
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Dado("^un calendario donde un período LABORAL y un período NO_LABORAL se intersectan en una fecha determinada$")
    public void un_calendario_donde_periodo_laboral_y_no_laboral_se_intersectan() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorAutenticado()));
        fechaConsultada = calendario.getFechaInicio().plusDays(10);
        agregarPeriodo(calendario.getCodigo(), "LABORAL", new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                .fechaInicio(fechaConsultada.minusDays(5)).fechaFin(fechaConsultada.plusDays(5)));
        agregarPeriodo(calendario.getCodigo(), "NO_LABORAL", new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                .fechaInicio(fechaConsultada.minusDays(1)).fechaFin(fechaConsultada.plusDays(1)));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Entonces("^el sistema responde NO_LABORAL$")
    public void el_sistema_responde_no_laboral() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getTipoDia()).isEqualTo(TipoPeriodoDto.NO_LABORAL);
    }

    @Dado("^un calendario donde dos períodos LABORAL se intersectan en una fecha determinada$")
    public void un_calendario_donde_dos_periodos_laboral_se_intersectan() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorAutenticado()));
        fechaConsultada = calendario.getFechaInicio().plusDays(10);
        agregarPeriodo(calendario.getCodigo(), "LABORAL", new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                .fechaInicio(fechaConsultada.minusDays(5)).fechaFin(fechaConsultada.plusDays(5)));
        agregarPeriodo(calendario.getCodigo(), "LABORAL", new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                .fechaInicio(fechaConsultada.minusDays(1)).fechaFin(fechaConsultada.plusDays(1)));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Entonces("^el sistema responde LABORAL sin reportar ningún conflicto$")
    public void el_sistema_responde_laboral_sin_conflicto() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getTipoDia()).isEqualTo(TipoPeriodoDto.LABORAL);
    }

    @Dado("^un calendario con una excepción registrada sobre una fecha determinada$")
    public void un_calendario_con_una_excepcion_registrada_sobre_una_fecha_determinada() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorAutenticado()));
        fechaConsultada = calendario.getFechaInicio().plusDays(10);
        tipoExcepcionEsperado = TipoExcepcionDto.DIA_NO_LABORAL;
        calendarioService.registrarExcepcion(calendario.getCodigo(), new ExcepcionRequestDto().fecha(fechaConsultada)
                .tipo(tipoExcepcionEsperado).descripcion("Excepción de prueba BDD"));
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Entonces("^el sistema responde el tipo indicado en la excepción$")
    public void el_sistema_responde_el_tipo_indicado_en_la_excepcion() {
        assertThat(ultimoResultado).isNotNull();
        TipoPeriodoDto tipoEsperado = tipoExcepcionEsperado == TipoExcepcionDto.DIA_LABORAL ? TipoPeriodoDto.LABORAL
                : TipoPeriodoDto.NO_LABORAL;
        assertThat(ultimoResultado.getTipoDia()).isEqualTo(tipoEsperado);
    }

    @Dado("^un calendario en el que la fecha consultada no está cubierta por ningún período ni excepción$")
    public void un_calendario_en_el_que_la_fecha_consultada_no_esta_cubierta() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), nuevoAdministradorAutenticado()));
        fechaConsultada = calendario.getFechaInicio().plusDays(10);
        contextoCalendario.setCalendarioActual(calendario);
        codigoCalendarioConsultado = calendario.getCodigo();
    }

    @Cuando("^cualquier usuario consulta el tipo de esa fecha$")
    public void cualquier_usuario_consulta_el_tipo_de_esa_fecha() {
        try {
            ultimoResultado = calendarioService.consultarTipoDia(codigoCalendarioConsultado, fechaConsultada);
            contextoCalendario.setUltimaRespuestaTexto(ultimoResultado.getTipoDia().name());
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^cualquier usuario consulta el tipo de una fecha sobre ese código de calendario$")
    public void cualquier_usuario_consulta_el_tipo_de_una_fecha_sobre_ese_codigo_de_calendario() {
        try {
            ultimoResultado = calendarioService.consultarTipoDia(
                    contextoCalendario.getCodigoCalendarioInexistente(), LocalDate.now());
            contextoCalendario.setUltimaRespuestaTexto(ultimoResultado.getTipoDia().name());
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    private void agregarPeriodo(String codigoCalendario, String tipoPeriodo, RecurrenciaUnaVezDto recurrencia) {
        String codigo = "PER-" + CalendarioFixtures.nuevoSufijo();
        if ("LABORAL".equals(tipoPeriodo)) {
            calendarioService.agregarPeriodoLaboral(codigoCalendario,
                    new PeriodoLaboralRequestDto().codigo(codigo).nombre("Período LABORAL (BDD)")
                            .recurrencia(recurrencia));
        } else {
            calendarioService.agregarPeriodoNoLaboral(codigoCalendario,
                    new PeriodoNoLaboralRequestDto().codigo(codigo).nombre("Período NO_LABORAL (BDD)")
                            .recurrencia(recurrencia));
        }
    }

    private Usuario nuevoAdministradorAutenticado() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        Usuario administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        return administrador;
    }
}
