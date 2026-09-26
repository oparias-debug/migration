package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.dto.RangoFechasCalendarioResponseDto;
import sv.gob.mh.siip.model.administracion.service.CalendarioConsultaService;

/** CU-ADM-04-11-consultar-fecha-desde-fecha-hasta.feature. */
public class Adm04ConsultarRangoFechasCalendario {

    private final CalendarioConsultaService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private RangoFechasCalendarioResponseDto ultimoResultado;

    public Adm04ConsultarRangoFechasCalendario(CalendarioConsultaService calendarioService,
            ContextoCalendarioBdd contextoCalendario, ContextoValidacionBdd contextoValidacion) {
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Cuando("^cualquier usuario consulta la fecha_desde y fecha_hasta del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_la_fecha_desde_y_fecha_hasta(String codigoCalendarioLiteral) {
        boolean esElCalendarioDelBackground = codigoCalendarioLiteral
                .equals(contextoCalendario.getCodigoCalendarioActualLiteral());
        String codigoCalendario = esElCalendarioDelBackground ? contextoCalendario.getCalendarioActual().getCodigo()
                : codigoCalendarioLiteral;
        try {
            ultimoResultado = calendarioService.consultarRangoFechasCalendario(codigoCalendario);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema devuelve fecha_desde \"([^\"]*)\" y fecha_hasta \"([^\"]*)\"$")
    public void el_sistema_devuelve_fecha_desde_y_fecha_hasta(String fechaDesde, String fechaHasta) {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getFechaDesde()).isEqualTo(LocalDate.parse(fechaDesde));
        assertThat(ultimoResultado.getFechaHasta()).isEqualTo(LocalDate.parse(fechaHasta));
    }
}
