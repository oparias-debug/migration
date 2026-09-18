package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.OperacionNoPermitidaException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.dto.TipoExcepcionDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-11-inactivar-calendario.feature. */
public class Adm04InactivarCalendario {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private String codigoPeriodoAgregado;
    private CalendarioDto ultimoResultado;

    public Adm04InactivarCalendario(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            PeriodoRepository periodoRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un calendario existente en estado \"([^\"]*)\" con períodos y excepciones asociados$")
    public void un_calendario_existente_en_estado_con_periodos_y_excepciones_asociados(String estadoActual) {
        autenticarNuevoAdministrador();
        String codigo = "CAL-" + CalendarioFixtures.nuevoSufijo();
        calendarioService.crear(new CrearCalendarioRequestDto().codigo(codigo).nombre("Calendario de prueba BDD")
                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO).fechaFin(CalendarioFixtures.FIN_CALENDARIO)
                .estado(EstadoCalendarioDto.valueOf(estadoActual)));
        codigoPeriodoAgregado = "PER-" + CalendarioFixtures.nuevoSufijo();
        calendarioService.agregarPeriodoLaboral(codigo,
                new PeriodoLaboralRequestDto().codigo(codigoPeriodoAgregado).nombre("Período LABORAL (BDD)")
                        .recurrencia(new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO)
                                .fechaFin(CalendarioFixtures.INICIO_CALENDARIO.plusDays(10))));
        calendarioService.registrarExcepcion(codigo,
                new ExcepcionRequestDto().fecha(CalendarioFixtures.INICIO_CALENDARIO.plusDays(20))
                        .tipo(TipoExcepcionDto.DIA_NO_LABORAL).descripcion("Excepción de prueba BDD"));
        Calendario calendario = calendarioRepository.findByCodigo(codigo).orElseThrow();
        contextoCalendario.setCalendarioActual(calendario);
    }

    @Cuando("^el actor cambia el estado del calendario a \"([^\"]*)\"$")
    public void el_actor_cambia_el_estado_del_calendario_a(String estadoNuevo) {
        ultimoResultado = calendarioService.cambiarEstado(contextoCalendario.getCalendarioActual().getCodigo(),
                new CambiarEstadoCalendarioRequestDto().estado(EstadoCalendarioDto.valueOf(estadoNuevo)));
    }

    @Entonces("^el calendario queda en estado \"([^\"]*)\"$")
    public void el_calendario_queda_en_estado(String estadoNuevo) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getEstado()).isEqualTo(EstadoCalendarioDto.valueOf(estadoNuevo));
    }

    @Entonces("^todos los períodos y excepciones del calendario heredan el estado \"([^\"]*)\"$")
    public void todos_los_periodos_y_excepciones_heredan_el_estado(String estadoNuevo) {
        // Ni Periodo ni Excepcion tienen un campo "estado" propio: su estado efectivo es SIEMPRE
        // periodo.getCalendario().getEstado() (ver Periodo/Excepcion.java), asi que basta confirmar
        // que (a) el periodo agregado en el Dado sigue existiendo bajo este calendario, y (b) el
        // calendario en si quedo con el nuevo estado, para probar la herencia por construccion del
        // modelo (evita navegar las colecciones LAZY periodos/excepciones fuera de una consulta directa).
        String codigoCalendario = contextoCalendario.getCalendarioActual().getCodigo();
        EstadoCalendario esperado = EstadoCalendario.valueOf(estadoNuevo);
        Calendario calendario = calendarioRepository.findByCodigo(codigoCalendario).orElseThrow();
        assertThat(calendario.getEstado()).isEqualTo(esperado);
        assertThat(periodoRepository.existsByCalendario_CodigoAndCodigo(codigoCalendario, codigoPeriodoAgregado))
                .isTrue();
    }

    @Dado("^un calendario existente en el sistema$")
    public void un_calendario_existente_en_el_sistema() {
        Calendario calendario = calendarioRepository.save(
                CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(),
                        autenticarNuevoAdministrador()));
        contextoCalendario.setCalendarioActual(calendario);
    }

    @Cuando("^el actor intenta eliminar el calendario$")
    public void el_actor_intenta_eliminar_el_calendario() {
        try {
            calendarioService.eliminar(contextoCalendario.getCalendarioActual().getCodigo());
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema indica que un calendario no puede eliminarse, solo inactivarse$")
    public void el_sistema_indica_que_un_calendario_no_puede_eliminarse() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(OperacionNoPermitidaException.class);
    }

    @Cuando("^el actor intenta cambiar el estado de un calendario$")
    public void el_actor_intenta_cambiar_el_estado_de_un_calendario() {
        Usuario administrador = usuarioRepository
                .save(CalendarioFixtures.nuevoAdministradorCalendario("admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo()));
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), administrador));
        try {
            calendarioService.cambiarEstado(calendario.getCodigo(),
                    new CambiarEstadoCalendarioRequestDto().estado(EstadoCalendarioDto.INACTIVO));
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
