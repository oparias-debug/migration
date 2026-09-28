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
import sv.gob.mh.siip.model.administracion.dto.FechaLaboralResultanteResponseDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioConsultaService;

/** CU-ADM-04-10-calcular-fecha-laboral-resultante.feature. */
public class Adm04CalcularFechaLaboralResultante {

    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioConsultaService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private LocalDate fechaParaSumaSinAvance = LocalDate.of(2026, 1, 1);
    private FechaLaboralResultanteResponseDto ultimoResultado;

    public Adm04CalcularFechaLaboralResultante(CalendarioRepository calendarioRepository,
            PeriodoRepository periodoRepository, CalendarioConsultaService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que entre la fecha \"([^\"]*)\" y la fecha resultante existen días definidos como NO_LABORAL$")
    public void que_entre_la_fecha_y_la_fecha_resultante_existen_dias_no_laboral(String fechaInicial) {
        // calendario ya fue persistido por el Background: se agregan los periodos directamente via
        // PeriodoRepository (no via calendario.getPeriodos().add(...), coleccion perezosa de Hibernate).
        Calendario calendario = calendarioRepository
                .findByCodigo(contextoCalendario.getCalendarioActual().getCodigo()).orElseThrow();
        LocalDate desde = LocalDate.parse(fechaInicial);
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, "LAB-BASE", TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(desde, desde.plusDays(30))));
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, "NOLAB-INTERMEDIO", TipoPeriodo.NO_LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(desde.plusDays(1), desde.plusDays(2))));
    }

    @Dado("^que la fecha resultante de la suma cae en un período NO_LABORAL$")
    public void que_la_fecha_resultante_de_la_suma_cae_en_no_laboral() {
        Calendario calendario = calendarioRepository
                .findByCodigo(contextoCalendario.getCalendarioActual().getCodigo()).orElseThrow();
        fechaParaSumaSinAvance = LocalDate.of(2026, 2, 1);
        periodoRepository.save(CalendarioFixtures.nuevoPeriodo(calendario, "NOLAB-DESTINO", TipoPeriodo.NO_LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(fechaParaSumaSinAvance, fechaParaSumaSinAvance)));
    }

    @Dado("^que la fecha resultante de la suma no cae en ningún período definido del calendario$")
    public void que_la_fecha_resultante_de_la_suma_no_cae_en_ningun_periodo() {
        fechaParaSumaSinAvance = LocalDate.of(2026, 8, 1);
    }

    @Cuando("^cualquier usuario suma \"([^\"]*)\" días hábiles a la fecha \"([^\"]*)\" en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_suma_dias_habiles_a_la_fecha(String diasHabiles, String fecha,
            String codigoCalendarioLiteral) {
        calcular(contextoCalendario.getCalendarioActual().getCodigo(), LocalDate.parse(fecha),
                Integer.parseInt(diasHabiles));
    }

    @Cuando("^cualquier usuario suma días hábiles a una fecha en el calendario \"([^\"]*)\"$")
    public void cualquier_usuario_suma_dias_habiles_sin_avance(String codigoCalendarioLiteral) {
        // Las tres primeras escenarios de este .feature reutilizan el calendario del Background bajo
        // el mismo literal; solo el escenario de "calendario inexistente" usa un literal distinto
        // (ver nota sobre codigos literales en Adm04ComunCalendario).
        boolean esElCalendarioDelBackground = codigoCalendarioLiteral
                .equals(contextoCalendario.getCodigoCalendarioActualLiteral());
        String codigoCalendario = esElCalendarioDelBackground ? contextoCalendario.getCalendarioActual().getCodigo()
                : codigoCalendarioLiteral;
        calcular(codigoCalendario, fechaParaSumaSinAvance, 0);
    }

    private void calcular(String codigoCalendario, LocalDate fecha, int diasHabiles) {
        try {
            ultimoResultado = calendarioService.calcularFechaLaboralResultante(codigoCalendario, fecha, diasHabiles);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema devuelve la fecha LABORAL resultante, sin contar los días NO_LABORAL$")
    public void el_sistema_devuelve_la_fecha_laboral_resultante() {
        assertThat(ultimoResultado).isNotNull();
        // Desde 2026-01-01: 01-02 y 01-03 son NO_LABORAL (no cuentan); LABORAL vuelve a contar desde
        // 01-04, por lo que el 5.o día hábil cae en 2026-01-08.
        assertThat(ultimoResultado.getFecha()).isEqualTo(LocalDate.of(2026, 1, 8));
    }

    @Entonces("^el sistema retorna error indicando que la fecha resultante no cae en un período LABORAL$")
    public void el_sistema_retorna_error_indicando_que_la_fecha_resultante_no_es_laboral() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }

    @Entonces("^el sistema retorna error indicando que la fecha resultante no está en ningún período definido$")
    public void el_sistema_retorna_error_indicando_que_la_fecha_resultante_no_tiene_periodo() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }
}
