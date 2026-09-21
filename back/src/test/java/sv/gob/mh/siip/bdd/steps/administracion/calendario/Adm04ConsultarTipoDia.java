package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.TipoDiaResponseDto;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.ExcepcionRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;

/** CU-ADM-04-05-consultar-tipo-de-dia.feature. */
public class Adm04ConsultarTipoDia {

    private final PeriodoRepository periodoRepository;
    private final ExcepcionRepository excepcionRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private TipoDiaResponseDto ultimoResultado;

    public Adm04ConsultarTipoDia(PeriodoRepository periodoRepository, ExcepcionRepository excepcionRepository,
            CalendarioService calendarioService, ContextoCalendarioBdd contextoCalendario,
            ContextoValidacionBdd contextoValidacion) {
        this.periodoRepository = periodoRepository;
        this.excepcionRepository = excepcionRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que el calendario \"([^\"]*)\" tiene definida la siguiente configuración: \"([^\"]*)\"$")
    public void que_el_calendario_tiene_definida_la_siguiente_configuracion(String codigoCalendarioLiteral,
            String configuracion) {
        // calendario viene de ContextoCalendarioBdd (fetched de un repositorio en un paso anterior),
        // por lo que su coleccion "periodos"/"excepciones" es perezosa: cada Periodo/Excepcion se
        // persiste directamente via su propio repositorio en lugar de mutar esa coleccion.
        Calendario calendario = contextoCalendario.getCalendarioActual();
        LocalDate fecha = LocalDate.of(2026, 3, 10);
        LocalDate desde = fecha.minusDays(5);
        LocalDate hasta = fecha.plusDays(5);

        switch (configuracion) {
            case "período LABORAL que cubre la fecha" -> guardarPeriodo(calendario, "LAB-A", TipoPeriodo.LABORAL,
                    desde, hasta);
            case "período NO_LABORAL que cubre la fecha" -> guardarPeriodo(calendario, "NOLAB-A",
                    TipoPeriodo.NO_LABORAL, desde, hasta);
            case "excepción DIA_LABORAL registrada sobre la fecha, dentro de un NO_LABORAL" -> {
                guardarPeriodo(calendario, "NOLAB-A", TipoPeriodo.NO_LABORAL, desde, hasta);
                excepcionRepository.save(CalendarioFixtures.nuevaExcepcion(calendario, fecha, TipoExcepcion.DIA_LABORAL));
            }
            case "excepción DIA_NO_LABORAL registrada sobre la fecha, dentro de un LABORAL" -> {
                guardarPeriodo(calendario, "LAB-A", TipoPeriodo.LABORAL, desde, hasta);
                excepcionRepository
                        .save(CalendarioFixtures.nuevaExcepcion(calendario, fecha, TipoExcepcion.DIA_NO_LABORAL));
            }
            case "período LABORAL y período NO_LABORAL que se intersectan en la fecha" -> {
                guardarPeriodo(calendario, "LAB-A", TipoPeriodo.LABORAL, desde, hasta);
                guardarPeriodo(calendario, "NOLAB-A", TipoPeriodo.NO_LABORAL, desde, hasta);
            }
            case "dos períodos LABORAL que se intersectan en la fecha" -> {
                guardarPeriodo(calendario, "LAB-A", TipoPeriodo.LABORAL, desde, hasta);
                guardarPeriodo(calendario, "LAB-B", TipoPeriodo.LABORAL, desde, hasta);
            }
            case "dos períodos NO_LABORAL que se intersectan en la fecha" -> {
                guardarPeriodo(calendario, "NOLAB-A", TipoPeriodo.NO_LABORAL, desde, hasta);
                guardarPeriodo(calendario, "NOLAB-B", TipoPeriodo.NO_LABORAL, desde, hasta);
            }
            default -> throw new IllegalArgumentException("Configuración no soportada: " + configuracion);
        }
    }

    private void guardarPeriodo(Calendario calendario, String codigo, TipoPeriodo tipo, LocalDate desde,
            LocalDate hasta) {
        periodoRepository.save(
                CalendarioFixtures.nuevoPeriodo(calendario, codigo, tipo, CalendarioFixtures.recurrenciaUnaVez(desde, hasta)));
    }

    @Dado("^que el calendario \"([^\"]*)\" no tiene ningún período ni excepción que cubra la fecha \"([^\"]*)\"$")
    public void que_el_calendario_no_tiene_ningun_periodo_ni_excepcion_que_cubra_la_fecha(
            String codigoCalendarioLiteral, String fecha) {
        // No-op deliberado: el Background recien crea el calendario sin periodos ni excepciones, asi
        // que la precondicion ya se cumple; se evita releer calendario.getPeriodos()/getExcepciones()
        // (colecciones perezosas de Hibernate) para no arriesgar una sesion ya cerrada.
    }

    @Cuando("^cualquier usuario consulta el tipo de la fecha \"([^\"]*)\" en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_el_tipo_de_la_fecha(String fecha, String codigoCalendarioLiteral) {
        try {
            ultimoResultado = calendarioService.consultarTipoDia(contextoCalendario.getCalendarioActual().getCodigo(),
                    LocalDate.parse(fecha));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^cualquier usuario consulta el tipo de una fecha en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_el_tipo_de_una_fecha_en_calendario_inexistente(String codigoCalendario) {
        try {
            ultimoResultado = calendarioService.consultarTipoDia(codigoCalendario, LocalDate.of(2026, 1, 1));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema responde que la fecha es \"([^\"]*)\"$")
    public void el_sistema_responde_que_la_fecha_es(String resultadoEsperado) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getTipo().name()).isEqualTo(resultadoEsperado);
    }

    @Entonces("^el sistema retorna error indicando que la fecha no está en ningún período definido$")
    public void el_sistema_retorna_error_indicando_que_la_fecha_no_esta_en_ningun_periodo_definido() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }
}
