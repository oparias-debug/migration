package sv.gob.mh.bdd.steps.calendario;

import static org.assertj.core.api.Assertions.assertThat;
import static sv.gob.mh.bdd.steps.calendario.Adm04ComunCalendario.assertExito;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.bdd.support.ContextoCalendarioBdd;

/** CU-ADM-04-11-consultar-fecha-desde-fecha-hasta.feature. */
public class Adm04ConsultarRangoFechasCalendario {

    private final ContextoCalendarioBdd contexto;

    public Adm04ConsultarRangoFechasCalendario(ContextoCalendarioBdd contexto) {
        this.contexto = contexto;
    }

    @Cuando("^cualquier usuario consulta la fecha_desde y fecha_hasta del calendario \"([^\"]*)\"$")
    public void cualquier_usuario_consulta_la_fecha_desde_y_fecha_hasta(String codigoCalendario) {
        contexto.comoCualquierUsuario();
        contexto.get("/calendarios/{codigo}/rango-fechas", codigoCalendario);
    }

    @Entonces("^el sistema devuelve fecha_desde \"([^\"]*)\" y fecha_hasta \"([^\"]*)\"$")
    public void el_sistema_devuelve_fecha_desde_y_fecha_hasta(String fechaDesde, String fechaHasta) {
        assertExito(contexto, 200);
        assertThat(contexto.getUltimoCuerpo().path("fechaDesde").asText()).isEqualTo(fechaDesde);
        assertThat(contexto.getUltimoCuerpo().path("fechaHasta").asText()).isEqualTo(fechaHasta);
    }
}
