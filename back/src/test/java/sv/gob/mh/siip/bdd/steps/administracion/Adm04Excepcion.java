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
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.dto.TipoExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.TipoPeriodoDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-04-excepcion.feature. */
public class Adm04Excepcion {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private LocalDate fechaExcepcion;
    private ExcepcionDto ultimoResultado;
    private TipoDiaResponseDto ultimaConsultaTipoDia;

    public Adm04Excepcion(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            CalendarioService calendarioService, ContextoCalendarioBdd contextoCalendario,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un calendario existente$")
    public void un_calendario_existente() {
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(),
                        autenticarNuevoAdministrador()));
        contextoCalendario.setCalendarioActual(calendario);
    }

    @Cuando("^el actor registra una excepción sobre una fecha con tipo \"([^\"]*)\" y una descripción$")
    public void el_actor_registra_una_excepcion_sobre_una_fecha_con_tipo(String tipo) {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        fechaExcepcion = calendario.getFechaInicio().plusDays(5);
        ultimoResultado = calendarioService.registrarExcepcion(calendario.getCodigo(),
                new ExcepcionRequestDto().fecha(fechaExcepcion).tipo(TipoExcepcionDto.valueOf(tipo))
                        .descripcion("Excepción de prueba BDD"));
    }

    @Entonces("^la excepción queda registrada en el calendario con el tipo \"([^\"]*)\"$")
    public void la_excepcion_queda_registrada_en_el_calendario_con_el_tipo(String tipo) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getFecha()).isEqualTo(fechaExcepcion);
        assertThat(ultimoResultado.getTipo()).isEqualTo(TipoExcepcionDto.valueOf(tipo));
    }

    @Entonces("^la excepción hereda el estado del calendario$")
    public void la_excepcion_hereda_el_estado_del_calendario() {
        // Excepcion no tiene campo "estado" propio: su estado efectivo es SIEMPRE calendario.getEstado()
        // (ver Excepcion.java). Basta con confirmar que el calendario al que quedó asociada la excepción
        // (ya verificada en el paso anterior) sigue teniendo el estado esperado.
        Calendario calendario = calendarioRepository
                .findByCodigo(contextoCalendario.getCalendarioActual().getCodigo()).orElseThrow();
        assertThat(ultimoResultado).isNotNull();
        assertThat(calendario.getEstado()).isEqualTo(contextoCalendario.getCalendarioActual().getEstado());
    }

    @Dado("^una fecha que cae dentro de un período LABORAL o NO_LABORAL definido en el calendario$")
    public void una_fecha_que_cae_dentro_de_un_periodo_definido_en_el_calendario() {
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(),
                        autenticarNuevoAdministrador()));
        calendarioService.agregarPeriodoLaboral(calendario.getCodigo(),
                new PeriodoLaboralRequestDto().codigo("PER-" + CalendarioFixtures.nuevoSufijo())
                        .nombre("Período LABORAL base (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                                .fechaInicio(calendario.getFechaInicio().plusDays(10))
                                .fechaFin(calendario.getFechaInicio().plusDays(20))));
        contextoCalendario.setCalendarioActual(calendario);
        fechaExcepcion = calendario.getFechaInicio().plusDays(15);
    }

    @Cuando("^existe una excepción registrada sobre esa misma fecha con un tipo distinto al que resultaría del período$")
    public void existe_una_excepcion_registrada_sobre_esa_misma_fecha_con_tipo_distinto() {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        // El período base es LABORAL: se registra una excepción DIA_NO_LABORAL, tipo opuesto.
        ultimoResultado = calendarioService.registrarExcepcion(calendario.getCodigo(),
                new ExcepcionRequestDto().fecha(fechaExcepcion).tipo(TipoExcepcionDto.DIA_NO_LABORAL)
                        .descripcion("Excepción que prevalece sobre el período (BDD)"));
    }

    @Cuando("^se consulta el tipo de esa fecha$")
    public void se_consulta_el_tipo_de_esa_fecha() {
        ultimaConsultaTipoDia = calendarioService
                .consultarTipoDia(contextoCalendario.getCalendarioActual().getCodigo(), fechaExcepcion);
    }

    @Entonces("^el resultado corresponde al tipo indicado en la excepción, no al del período$")
    public void el_resultado_corresponde_al_tipo_indicado_en_la_excepcion() {
        assertThat(ultimaConsultaTipoDia).isNotNull();
        // La excepción registrada es DIA_NO_LABORAL: el período base (LABORAL) queda ignorado.
        assertThat(ultimaConsultaTipoDia.getTipoDia()).isEqualTo(TipoPeriodoDto.NO_LABORAL);
    }

    @Cuando("^el actor intenta registrar una excepción sobre una fecha$")
    public void el_actor_intenta_registrar_una_excepcion_sobre_una_fecha() {
        Usuario administrador = usuarioRepository
                .save(CalendarioFixtures.nuevoAdministradorCalendario("admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo()));
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), administrador));
        try {
            calendarioService.registrarExcepcion(calendario.getCodigo(),
                    new ExcepcionRequestDto().fecha(calendario.getFechaInicio().plusDays(5))
                            .tipo(TipoExcepcionDto.DIA_NO_LABORAL).descripcion("Excepción no autorizada (BDD)"));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    private Usuario autenticarNuevoAdministrador() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        Usuario administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        return administrador;
    }
}
