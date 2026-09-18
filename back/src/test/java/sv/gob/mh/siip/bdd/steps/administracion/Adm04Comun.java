package sv.gob.mh.siip.bdd.steps.administracion;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * Steps de CU-ADM-04 (gestion de calendarios) cuyo texto es literalmente identico en varios de los
 * 11 .feature del caso de uso (Cucumber exige una unica definicion por texto en el classpath
 * de test, sin importar la clase o el tipo de keyword usado): el rechazo por falta de permisos, el
 * rechazo generico de una operacion de gestion, el error generico de una consulta, "que no existe
 * ningún calendario con el código indicado" (reutilizado por varias consultas), y la preparacion de
 * un calendario/periodo compartida entre CU-ADM-04-02 (período LABORAL) y CU-ADM-04-03 (período
 * NO_LABORAL).
 */
public class Adm04Comun {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    public Adm04Comun(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            PeriodoRepository periodoRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que el actor autenticado no tiene el rol ADMINISTRADOR ni ADMINISTRADOR_CALENDARIO$")
    public void que_el_actor_autenticado_no_tiene_el_rol_adecuado() {
        String nombreUsuario = "usuario.sin.rol.bdd." + CalendarioFixtures.nuevoSufijo();
        usuarioRepository.save(CalendarioFixtures.nuevoUsuarioSinRolAdecuado(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
    }

    @Entonces("^el sistema rechaza la operación por falta de permisos$")
    public void el_sistema_rechaza_la_operacion_por_falta_de_permisos() {
        assertThat(contextoValidacion.getUltimaExcepcion())
                .isInstanceOfAny(NoAutenticadoException.class, AccesoDenegadoException.class);
    }

    @Entonces("^el sistema rechaza la operación$")
    public void el_sistema_rechaza_la_operacion() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isNotNull();
    }

    @Entonces("^el sistema retorna un error$")
    public void el_sistema_retorna_un_error() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isNotNull();
    }

    /**
     * Compartido por CU-ADM-04-05 (esquema con placeholder tipo_periodo) y CU-ADM-04-06 (esquema
     * con placeholder resultado_esperado): ambos resuelven al mismo texto de step una vez
     * sustituidos los placeholders, asi que cada clase de consulta guarda su resultado como texto
     * plano en {@link ContextoCalendarioBdd#setUltimaRespuestaTexto(String)} para esta unica
     * definicion compartida.
     */
    @Entonces("^el sistema responde \"([^\"]*)\"$")
    public void el_sistema_responde(String valorEsperado) {
        assertThat(contextoCalendario.getUltimaRespuestaTexto()).isEqualTo(valorEsperado);
    }

    @Dado("^que no existe ningún calendario con el código indicado$")
    public void que_no_existe_ningun_calendario_con_el_codigo_indicado() {
        String codigo = "NOEXISTE-" + CalendarioFixtures.nuevoSufijo();
        assertThat(calendarioRepository.existsByCodigo(codigo)).isFalse();
        contextoCalendario.setCodigoCalendarioInexistente(codigo);
    }

    @Entonces("^el período hereda el estado del calendario$")
    public void el_periodo_hereda_el_estado_del_calendario() {
        // Periodo no tiene un campo "estado" propio: su estado efectivo es SIEMPRE
        // periodo.getCalendario().getEstado() (ver Periodo.java). Se evita navegar
        // periodoActual.getCalendario() (asociacion LAZY) y en su lugar se relee el calendario
        // directamente por su codigo, evitando cualquier proxy Hibernate sin sesion asociada.
        assertThat(contextoCalendario.getPeriodoActual()).isNotNull();
        Calendario calendario = calendarioRepository
                .findByCodigo(contextoCalendario.getCalendarioActual().getCodigo()).orElseThrow();
        assertThat(calendario.getEstado()).isEqualTo(contextoCalendario.getCalendarioActual().getEstado());
    }

    @Dado("^un calendario existente con rango de fechas (\\d{4}-\\d{2}-\\d{2}) a (\\d{4}-\\d{2}-\\d{2})$")
    public void un_calendario_existente_con_rango_de_fechas(String fechaInicioCalendario, String fechaFinCalendario) {
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(),
                        LocalDate.parse(fechaInicioCalendario), LocalDate.parse(fechaFinCalendario),
                        autenticarNuevoAdministrador()));
        contextoCalendario.setCalendarioActual(calendario);
    }

    @Dado("^no existe dentro de ese calendario ningún período con el código indicado$")
    public void no_existe_dentro_de_ese_calendario_ningun_periodo_con_el_codigo_indicado() {
        String codigo = "PER-" + CalendarioFixtures.nuevoSufijo();
        assertThat(periodoRepository
                .existsByCalendario_CodigoAndCodigo(contextoCalendario.getCalendarioActual().getCodigo(), codigo))
                .isFalse();
        contextoCalendario.setCodigoPeriodoPreparado(codigo);
    }

    @Dado("^que ya existe un período con un código determinado dentro de un calendario$")
    public void que_ya_existe_un_periodo_con_un_codigo_determinado_dentro_de_un_calendario() {
        Calendario calendario = CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(),
                autenticarNuevoAdministrador());
        String codigoPeriodo = "PER-" + CalendarioFixtures.nuevoSufijo();
        CalendarioFixtures.agregarPeriodo(calendario, codigoPeriodo, TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(calendario.getFechaInicio(),
                        calendario.getFechaInicio().plusDays(10)));
        calendario = calendarioRepository.save(calendario);
        contextoCalendario.setCalendarioActual(calendario);
        contextoCalendario.setCodigoPeriodoPreparado(codigoPeriodo);
    }

    @Dado("^un calendario con un rango de fechas determinado$")
    public void un_calendario_con_un_rango_de_fechas_determinado() {
        Calendario calendario = calendarioRepository.save(CalendarioFixtures
                .nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), autenticarNuevoAdministrador()));
        contextoCalendario.setCalendarioActual(calendario);
    }

    @Cuando("^el actor indica una fecha de inicio posterior a la fecha de fin del período$")
    public void el_actor_indica_una_fecha_de_inicio_posterior_a_la_fecha_de_fin_del_periodo() {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        LocalDate fechaInicio = calendario.getFechaInicio().plusDays(10);
        LocalDate fechaFin = calendario.getFechaInicio().plusDays(1);
        RecurrenciaUnaVezDto recurrencia = new RecurrenciaUnaVezDto().tipoRecurrencia("UNA_VEZ")
                .fechaInicio(fechaInicio).fechaFin(fechaFin);
        String codigo = "PER-" + CalendarioFixtures.nuevoSufijo();
        try {
            if (contextoCalendario.getTipoPeriodoPreparado() == TipoPeriodo.NO_LABORAL) {
                calendarioService.agregarPeriodoNoLaboral(calendario.getCodigo(), new PeriodoNoLaboralRequestDto()
                        .codigo(codigo).nombre("Período de prueba BDD").recurrencia(recurrencia));
            } else {
                calendarioService.agregarPeriodoLaboral(calendario.getCodigo(), new PeriodoLaboralRequestDto()
                        .codigo(codigo).nombre("Período de prueba BDD").recurrencia(recurrencia));
            }
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    /** Crea, persiste y autentica un nuevo Usuario ADMINISTRADOR_CALENDARIO; retorna la entidad persistida. */
    private Usuario autenticarNuevoAdministrador() {
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        Usuario administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        return administrador;
    }
}
