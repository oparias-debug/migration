package sv.gob.mh.bdd.steps.catalogo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.application.handler.catalogo.EvaluarVigenciaHandler;
import sv.gob.mh.bdd.support.CatalogoFixtures;
import sv.gob.mh.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.domain.model.catalogo.Vigencia;
import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Steps de la evaluación diaria de vigencia (HU-ADM-01-16, SF-14). El proceso programado no tiene
 * endpoint: los escenarios invocan directamente el caso de uso que dispara el planificador.
 */
public class AdmVigencia {

    private static final String CATALOGO = "catalogo:";
    private static final String REGISTRO = "registro:";

    private final CatalogoFixtures fixtures;
    private final ContextoCatalogoBdd contexto;
    private final EvaluarVigenciaHandler evaluarVigencia;

    public AdmVigencia(CatalogoFixtures fixtures, ContextoCatalogoBdd contexto,
            EvaluarVigenciaHandler evaluarVigencia) {
        this.fixtures = fixtures;
        this.contexto = contexto;
        this.evaluarVigencia = evaluarVigencia;
    }

    private void catalogo(String codigo, EstadoVigencia estado, LocalDate hasta) {
        fixtures.asegurarCatalogo(codigo);
        fixtures.fijarEstadoCatalogo(codigo, estado, hasta);
        contexto.setElemento(CATALOGO + codigo);
    }

    private void registro(String clave, LocalDate hasta) {
        fixtures.asegurarCatalogo("MONEDA");
        fixtures.crearRegistroActivo("MONEDA", clave);
        fixtures.fijarEstadoRegistro("MONEDA", clave, EstadoVigencia.ACTIVE, hasta);
        contexto.setElemento(REGISTRO + "MONEDA:" + clave);
    }

    @Dado("^un (catálogo|registro) activo sin fechas de vigencia$")
    public void un_elemento_activo_sin_fechas(String elemento) {
        if ("catálogo".equals(elemento)) {
            catalogo("MONEDA", EstadoVigencia.ACTIVE, null);
        } else {
            registro("USD", null);
        }
    }

    @Dado("^un registro activo con TO DATE igual a mañana$")
    public void un_registro_que_vence_manana() {
        registro("USD", Vigencia.hoy().plusDays(1));
    }

    @Dado("^un catálogo inactivo cuya TO DATE fue cambiada a una fecha futura$")
    public void un_catalogo_inactivo_con_to_date_futura() {
        catalogo("MONEDA", EstadoVigencia.INACTIVE, Vigencia.hoy().plusDays(30));
    }

    @Dado("^que el registro \"([^\"]*)\" de \"([^\"]*)\" tiene TO DATE igual a ayer y estado \"(ACTIVE|INACTIVE)\"$")
    public void que_el_registro_vencio_ayer(String clave, String codigo, String estado) {
        fixtures.crearRegistroActivo(codigo, clave);
        fixtures.fijarEstadoRegistro(codigo, clave, EstadoVigencia.valueOf(estado), Vigencia.hoy().minusDays(1));
    }

    @Dado("^que los registros \"([^\"]*)\" y \"([^\"]*)\" de \"([^\"]*)\" tienen como registro padre \"([^\"]*)\"$")
    public void que_los_registros_tienen_registro_padre(String clave1, String clave2, String codigo,
            String clavePadre) {
        fixtures.crearRegistro(codigo, clave1, clavePadre);
        fixtures.crearRegistro(codigo, clave2, clavePadre);
    }

    @Dado("^el catálogo \"([^\"]*)\" con TO DATE vencida, padre de \"([^\"]*)\"$")
    public void el_catalogo_vencido_padre_de(String padre, String hijo) {
        fixtures.fijarPadre(hijo, padre);
        fixtures.fijarEstadoCatalogo(padre, EstadoVigencia.ACTIVE, Vigencia.hoy().minusDays(1));
    }

    @Cuando("^se ejecuta la evaluación diaria$")
    public void se_ejecuta_la_evaluacion_diaria() {
        evaluarVigencia.ejecutar();
    }

    @Entonces("^su estado (?:es|sigue siendo) \"(ACTIVE|INACTIVE)\"$")
    public void su_estado_es(String estado) {
        String[] elemento = contexto.getElemento().split(":");
        EstadoVigencia guardado = CATALOGO.equals(elemento[0] + ":") ? fixtures.estadoCatalogo(elemento[1])
                : fixtures.estadoRegistro(elemento[1], elemento[2]);
        assertThat(guardado).isEqualTo(EstadoVigencia.valueOf(estado));
    }

    @Entonces("^el registro \"([^\"]*)\" pasa a \"(ACTIVE|INACTIVE)\"$")
    public void el_registro_pasa_a(String clave, String estado) {
        assertThat(fixtures.estadoRegistro(fixtures.catalogoDelRegistro(clave), clave))
                .isEqualTo(EstadoVigencia.valueOf(estado));
    }

    @Entonces("^sus registros hijos \"([^\"]*)\" y \"([^\"]*)\" pasan a \"(ACTIVE|INACTIVE)\"$")
    public void sus_registros_hijos_pasan_a(String clave1, String clave2, String estado) {
        el_registro_pasa_a(clave1, estado);
        el_registro_pasa_a(clave2, estado);
    }

    @Entonces("^\"([^\"]*)\", \"([^\"]*)\" y todos sus registros pasan a \"(ACTIVE|INACTIVE)\"$")
    public void los_catalogos_y_sus_registros_pasan_a(String padre, String hijo, String estado) {
        EstadoVigencia esperado = EstadoVigencia.valueOf(estado);
        for (String codigo : new String[] { padre, hijo }) {
            assertThat(fixtures.estadoCatalogo(codigo)).as(codigo).isEqualTo(esperado);
            assertThat(fixtures.claves(codigo)).as("registros de %s", codigo).isNotEmpty()
                    .allSatisfy(clave -> assertThat(fixtures.estadoRegistro(codigo, clave)).isEqualTo(esperado));
        }
    }
}
