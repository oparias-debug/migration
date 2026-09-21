package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;

/** CU-ADM-04-03-agregar-periodo-no-laboral.feature. */
public class Adm04AgregarPeriodoNoLaboral {

    private final PeriodoRepository periodoRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    public Adm04AgregarPeriodoNoLaboral(PeriodoRepository periodoRepository, CalendarioService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Cuando("^el actor agrega al calendario \"([^\"]*)\" un período NO_LABORAL con código \"([^\"]*)\", nombre \"([^\"]*)\" y recurrencia \"([^\"]*)\"$")
    public void el_actor_agrega_un_periodo_no_laboral(String codigoCalendarioLiteral, String codigo, String nombre,
            String recurrencia) {
        String codigoCalendario = contextoCalendario.getCalendarioActual().getCodigo();
        PeriodoInputDto request = new PeriodoInputDto().codigo(codigo).nombre(nombre)
                .recurrencia(CalendarioFixtures.parseRecurrencia(recurrencia));
        PeriodoNoLaboralDto resultado = calendarioService.agregarPeriodoNoLaboral(codigoCalendario, request);
        contextoCalendario.setPeriodoActual(periodoRepository
                .findByCalendario_CodigoAndCodigo(codigoCalendario, resultado.getCodigo()).orElseThrow());
    }

    @Cuando("^el actor intenta agregar un período NO_LABORAL con fecha de inicio \"([^\"]*)\" y fecha de fin \"([^\"]*)\"$")
    public void el_actor_intenta_agregar_un_periodo_no_laboral_con_fechas_invertidas(String fechaInicio,
            String fechaFin) {
        intentarAgregar(contextoCalendario.getCalendarioActual().getCodigo(), fechaInicio, fechaFin);
    }

    @Cuando("^el actor intenta agregar un período NO_LABORAL con fecha de inicio \"([^\"]*)\" y fecha de fin \"([^\"]*)\" al calendario \"([^\"]*)\"$")
    public void el_actor_intenta_agregar_un_periodo_no_laboral_fuera_de_rango(String fechaInicio, String fechaFin,
            String codigoCalendarioLiteral) {
        intentarAgregar(contextoCalendario.getCalendarioActual().getCodigo(), fechaInicio, fechaFin);
    }

    @Cuando("^el actor intenta agregar un período NO_LABORAL al calendario \"([^\"]*)\"$")
    public void el_actor_intenta_agregar_un_periodo_no_laboral_sin_permisos(String codigoCalendarioLiteral) {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        intentarAgregar(calendario.getCodigo(), calendario.getFechaInicio().toString(),
                calendario.getFechaInicio().plusDays(1).toString());
    }

    private void intentarAgregar(String codigoCalendario, String fechaInicio, String fechaFin) {
        RecurrenciaUnaVezDto recurrencia = new RecurrenciaUnaVezDto().tipo("UNA_VEZ")
                .fechaInicio(LocalDate.parse(fechaInicio)).fechaFin(LocalDate.parse(fechaFin));
        PeriodoInputDto request = new PeriodoInputDto().codigo("NOLAB-" + CalendarioFixtures.nuevoSufijo())
                .nombre("Período de prueba BDD").recurrencia(recurrencia);
        try {
            calendarioService.agregarPeriodoNoLaboral(codigoCalendario, request);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
