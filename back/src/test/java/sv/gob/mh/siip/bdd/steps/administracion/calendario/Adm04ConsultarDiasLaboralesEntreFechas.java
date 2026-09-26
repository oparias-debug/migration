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
import sv.gob.mh.siip.model.administracion.dto.DiasLaboralesEntreFechasResponseDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioConsultaService;

/** CU-ADM-04-09-consultar-dias-laboral-entre-fechas.feature. */
public class Adm04ConsultarDiasLaboralesEntreFechas {

    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioConsultaService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private DiasLaboralesEntreFechasResponseDto ultimoResultado;

    public Adm04ConsultarDiasLaboralesEntreFechas(CalendarioRepository calendarioRepository,
            PeriodoRepository periodoRepository, CalendarioConsultaService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que entre las fechas \"([^\"]*)\" y \"([^\"]*)\" existen varios períodos LABORAL y NO_LABORAL, incluyendo intersecciones LABORAL\\+NO_LABORAL$")
    public void que_entre_las_fechas_existen_periodos_laboral_y_no_laboral_con_interseccion(String fechaInicio,
            String fechaFin) {
        // calendario ya fue persistido por el Background: se agregan los periodos directamente via
        // PeriodoRepository (no via calendario.getPeriodos().add(...), coleccion perezosa de Hibernate).
        Calendario calendario = calendarioRepository
                .findByCodigo(contextoCalendario.getCalendarioActual().getCodigo()).orElseThrow();
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, "LAB-BASE", TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(LocalDate.parse(fechaInicio), LocalDate.parse(fechaFin))));
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, "NOLAB-INTERSECCION",
                TipoPeriodo.NO_LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 4))));
    }

    @Cuando("^cualquier usuario consulta los días LABORAL entre \"([^\"]*)\" y \"([^\"]*)\" en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_laborales_entre_fechas(String fechaInicio, String fechaFin,
            String codigoCalendarioLiteral) {
        try {
            ultimoResultado = calendarioService.consultarDiasLaboralesEntreFechas(
                    contextoCalendario.getCalendarioActual().getCodigo(), LocalDate.parse(fechaInicio),
                    LocalDate.parse(fechaFin));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^cualquier usuario consulta los días LABORAL entre dos fechas en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_los_dias_laborales_en_calendario_inexistente(
            String codigoCalendarioLiteral) {
        try {
            ultimoResultado = calendarioService.consultarDiasLaboralesEntreFechas(codigoCalendarioLiteral,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema devuelve el total de días LABORAL, excluyendo los días en intersección con NO_LABORAL y restando 1 según la convención de conteo$")
    public void el_sistema_devuelve_el_total_de_dias_laborales() {
        assertThat(ultimoResultado).isNotNull();
        // [2026-01-01, 2026-01-10]: 10 días, de los cuales 2 (03 y 04) se excluyen por interseccion
        // con NO_LABORAL: 8 días LABORAL crudos; la convención de conteo resta 1 => 7.
        assertThat(ultimoResultado.getDiasLaborales()).isEqualTo(7);
    }

    @Entonces("^el sistema retorna error indicando que una de las fechas está fuera del rango del calendario$")
    public void el_sistema_retorna_error_indicando_que_una_fecha_esta_fuera_de_rango() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }

    @Entonces("^el sistema retorna error indicando que las fechas son inconsistentes$")
    public void el_sistema_retorna_error_indicando_que_las_fechas_son_inconsistentes() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }
}
