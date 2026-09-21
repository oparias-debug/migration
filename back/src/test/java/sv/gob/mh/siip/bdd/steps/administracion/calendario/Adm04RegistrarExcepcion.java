package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.RegistrarExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.TipoExcepcionDto;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;

/** CU-ADM-04-04-registrar-excepcion.feature. */
public class Adm04RegistrarExcepcion {

    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private ExcepcionDto ultimoResultado;

    public Adm04RegistrarExcepcion(CalendarioService calendarioService, ContextoCalendarioBdd contextoCalendario,
            ContextoValidacionBdd contextoValidacion) {
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Cuando("^el actor registra en el calendario \"([^\"]*)\" una excepción con fecha \"([^\"]*)\", tipo \"([^\"]*)\" y descripción \"([^\"]*)\"$")
    public void el_actor_registra_una_excepcion(String codigoCalendarioLiteral, String fecha, String tipo,
            String descripcion) {
        RegistrarExcepcionRequestDto request = new RegistrarExcepcionRequestDto()
                .fecha(LocalDate.parse(fecha))
                .tipo(TipoExcepcionDto.valueOf(tipo))
                .descripcion(descripcion);
        ultimoResultado = calendarioService.registrarExcepcion(contextoCalendario.getCalendarioActual().getCodigo(),
                request);
    }

    @Entonces("^la excepción se registra correctamente$")
    public void la_excepcion_se_registra_correctamente() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getId()).isNotNull();
    }

    @Entonces("^la excepción hereda el estado \"([^\"]*)\" del calendario$")
    public void la_excepcion_hereda_el_estado_del_calendario(String estadoEsperado) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getEstado()).isEqualTo(EstadoCalendarioDto.valueOf(estadoEsperado));
    }

    @Cuando("^el actor intenta registrar en el calendario \"([^\"]*)\" una excepción con fecha \"([^\"]*)\"$")
    public void el_actor_intenta_registrar_una_excepcion_fuera_de_rango(String codigoCalendarioLiteral,
            String fecha) {
        intentarRegistrar(contextoCalendario.getCalendarioActual().getCodigo(), fecha);
    }

    @Entonces("^el sistema rechaza la operación indicando que la fecha no está enmarcada dentro del rango del calendario$")
    public void el_sistema_rechaza_la_operacion_por_fecha_fuera_de_rango() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(InconsistenciaFechaException.class);
    }

    @Cuando("^el actor intenta registrar una excepción en el calendario \"([^\"]*)\"$")
    public void el_actor_intenta_registrar_una_excepcion_sin_permisos(String codigoCalendarioLiteral) {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        intentarRegistrar(calendario.getCodigo(), calendario.getFechaInicio().toString());
    }

    private void intentarRegistrar(String codigoCalendario, String fecha) {
        RegistrarExcepcionRequestDto request = new RegistrarExcepcionRequestDto()
                .fecha(LocalDate.parse(fecha))
                .tipo(TipoExcepcionDto.DIA_NO_LABORAL)
                .descripcion("Excepción de prueba BDD");
        try {
            calendarioService.registrarExcepcion(codigoCalendario, request);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
