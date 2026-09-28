package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.service.RegistroService;

/**
 * CU-ADM-01-actualizar-registro.feature. "un registro existente" se reutiliza sin redefinir desde
 * {@link AdmComun} (tambien lo usa inactivar-registro.feature).
 */
public class AdmActualizarRegistro {

    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    private CatalogRecordDto ultimoResultado;

    public AdmActualizarRegistro(RegistroService registroService, ContextoCatalogoBdd contextoCatalogo,
            ContextoValidacionBdd contextoValidacion) {
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Cuando("^actualizo el valor de uno o más de sus campos no KEY$")
    public void actualizo_el_valor_de_sus_campos_no_key() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        String nombreCampoNoKey = CatalogoFixtures.nombreCampoNoKey(catalogo);
        CatalogRecordUpdateRequestDto solicitud = new CatalogRecordUpdateRequestDto(
                Map.of(nombreCampoNoKey, "Descripción actualizada (BDD)"));
        ultimoResultado = registroService.actualizar(catalogo.getCodigo(), contextoCatalogo.getRegistroActual().getClave(),
                solicitud);
    }

    @Entonces("^los cambios se guardan correctamente conforme a la Regla 16$")
    public void los_cambios_se_guardan_correctamente() {
        String nombreCampoNoKey = CatalogoFixtures.nombreCampoNoKey(contextoCatalogo.getCatalogoActual());
        assertThat(ultimoResultado.getValues()).containsEntry(nombreCampoNoKey, "Descripción actualizada (BDD)");
    }

    @Cuando("^intento modificar el valor de su campo KEY$")
    public void intento_modificar_el_valor_de_su_campo_key() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        String nombreCampoKey = CatalogoFixtures.nombreCampoKey(catalogo);
        CatalogRecordUpdateRequestDto solicitud = new CatalogRecordUpdateRequestDto(Map.of(nombreCampoKey, "K1-modificado"));
        try {
            registroService.actualizar(catalogo.getCodigo(), contextoCatalogo.getRegistroActual().getClave(), solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
