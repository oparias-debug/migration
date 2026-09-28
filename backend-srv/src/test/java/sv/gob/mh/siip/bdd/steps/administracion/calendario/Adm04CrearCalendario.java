package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;

/** CU-ADM-04-01-crear-calendario.feature. */
public class Adm04CrearCalendario {

    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoValidacionBdd contextoValidacion;

    private CalendarioDto ultimoResultado;

    // El código literal del .feature (p.ej. "CAL-2026") se repite entre pasos de un mismo escenario;
    // como los escenarios no revierten sus cambios entre si, cada Dado que "prepara" un código genera
    // uno real unico y lo guarda aqui, para que el Cuando siguiente lo reutilice en vez de volver a
    // usar el literal capturado (ver mismo criterio en Adm04ComunCalendario).
    private String codigoPreparado;

    public Adm04CrearCalendario(CalendarioRepository calendarioRepository, CalendarioService calendarioService,
            ContextoValidacionBdd contextoValidacion) {
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^no existe ningún calendario con código \"([^\"]*)\"$")
    public void no_existe_ningun_calendario_con_codigo(String codigo) {
        codigoPreparado = codigo + "-" + CalendarioFixtures.nuevoSufijo();
        assertThat(calendarioRepository.existsByCodigo(codigoPreparado)).isFalse();
    }

    @Cuando("^el actor crea un calendario con código \"([^\"]*)\", nombre \"([^\"]*)\", fecha de inicio \"([^\"]*)\", fecha de fin \"([^\"]*)\" y estado \"([^\"]*)\"$")
    public void el_actor_crea_un_calendario_con_datos_validos(String codigoLiteral, String nombre,
            String fechaInicio, String fechaFin, String estado) {
        CrearCalendarioRequestDto request = new CrearCalendarioRequestDto()
                .codigo(codigoPreparado)
                .nombre(nombre)
                .fechaInicio(LocalDate.parse(fechaInicio))
                .fechaFin(LocalDate.parse(fechaFin))
                .estado(EstadoCalendarioDto.valueOf(estado));
        ultimoResultado = calendarioService.crear(request);
    }

    @Entonces("^el calendario se crea correctamente con estado \"([^\"]*)\"$")
    public void el_calendario_se_crea_correctamente_con_estado(String estadoEsperado) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getEstado()).isEqualTo(EstadoCalendarioDto.valueOf(estadoEsperado));
        assertThat(calendarioRepository.existsByCodigo(ultimoResultado.getCodigo())).isTrue();
    }

    @Dado("^ya existe un calendario con código \"([^\"]*)\"$")
    public void ya_existe_un_calendario_con_codigo(String codigoLiteral) {
        codigoPreparado = codigoLiteral + "-" + CalendarioFixtures.nuevoSufijo();
        CrearCalendarioRequestDto request = new CrearCalendarioRequestDto()
                .codigo(codigoPreparado)
                .nombre("Calendario existente BDD")
                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO)
                .fechaFin(CalendarioFixtures.FIN_CALENDARIO)
                .estado(EstadoCalendarioDto.ACTIVO);
        calendarioService.crear(request);
    }

    @Cuando("^el actor intenta crear otro calendario con código \"([^\"]*)\"$")
    public void el_actor_intenta_crear_otro_calendario_con_el_mismo_codigo(String codigoLiteral) {
        CrearCalendarioRequestDto request = new CrearCalendarioRequestDto()
                .codigo(codigoPreparado)
                .nombre("Otro calendario de prueba BDD")
                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO)
                .fechaFin(CalendarioFixtures.FIN_CALENDARIO)
                .estado(EstadoCalendarioDto.ACTIVO);
        intentarCrear(request);
    }

    @Entonces("^el sistema rechaza la operación indicando que el código ya existe$")
    public void el_sistema_rechaza_la_operacion_indicando_que_el_codigo_ya_existe() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(ConflictoEstadoException.class);
    }

    @Cuando("^el actor intenta crear un calendario con fecha de inicio \"([^\"]*)\" y fecha de fin \"([^\"]*)\"$")
    public void el_actor_intenta_crear_un_calendario_con_fechas_invertidas(String fechaInicio, String fechaFin) {
        CrearCalendarioRequestDto request = new CrearCalendarioRequestDto()
                .codigo("CAL-" + CalendarioFixtures.nuevoSufijo())
                .nombre("Calendario de prueba BDD")
                .fechaInicio(LocalDate.parse(fechaInicio))
                .fechaFin(LocalDate.parse(fechaFin))
                .estado(EstadoCalendarioDto.ACTIVO);
        intentarCrear(request);
    }

    @Cuando("^el actor intenta crear un calendario$")
    public void el_actor_intenta_crear_un_calendario() {
        CrearCalendarioRequestDto request = new CrearCalendarioRequestDto()
                .codigo("CAL-" + CalendarioFixtures.nuevoSufijo())
                .nombre("Calendario de prueba BDD")
                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO)
                .fechaFin(CalendarioFixtures.FIN_CALENDARIO)
                .estado(EstadoCalendarioDto.ACTIVO);
        intentarCrear(request);
    }

    private void intentarCrear(CrearCalendarioRequestDto request) {
        try {
            calendarioService.crear(request);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
