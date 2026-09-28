package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordDto;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.RegistroService;

/**
 * CU-ADM-01-crear-registro.feature. "un catálogo existente" se reutiliza sin redefinir desde
 * {@link AdmComun} (tambien lo usan actualizar-descriptores-catalogo.feature e
 * inactivar-catalogo.feature).
 */
public class AdmCrearRegistro {

    private final RegistroRepository registroRepository;
    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    private CatalogRecordDto ultimoResultado;

    public AdmCrearRegistro(RegistroRepository registroRepository, RegistroService registroService,
            ContextoCatalogoBdd contextoCatalogo, ContextoValidacionBdd contextoValidacion) {
        this.registroRepository = registroRepository;
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Cuando("^proveo el valor de cada uno de sus campos, incluido el campo KEY$")
    public void proveo_el_valor_de_cada_uno_de_sus_campos() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        ultimoResultado = registroService.crear(catalogo.getCodigo(), new CatalogRecordCreateRequestDto(valoresCompletos(catalogo)));
    }

    @Entonces("^el registro se crea correctamente conforme a las Reglas 1 y 8$")
    public void el_registro_se_crea_correctamente() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(registroRepository.existsByCatalogo_CodigoAndClave(contextoCatalogo.getCatalogoActual().getCodigo(),
                ultimoResultado.getKey())).isTrue();
    }

    @Cuando("^intento crear un registro sin proveer el valor de alguno de los campos definidos$")
    public void intento_crear_un_registro_sin_proveer_algun_valor() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        Map<String, String> valores = new HashMap<>();
        valores.put(CatalogoFixtures.nombreCampoKey(catalogo), "K-" + CatalogoFixtures.nuevoSufijo());
        try {
            registroService.crear(catalogo.getCodigo(), new CatalogRecordCreateRequestDto(valores));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^creo un registro sin indicar fechas de vigencia$")
    public void creo_un_registro_sin_indicar_fechas_de_vigencia() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        ultimoResultado = registroService.crear(catalogo.getCodigo(), new CatalogRecordCreateRequestDto(valoresCompletos(catalogo)));
        contextoCatalogo.setActiveResultado(ultimoResultado.getActive());
    }

    private static Map<String, String> valoresCompletos(Catalogo catalogo) {
        Map<String, String> valores = new HashMap<>();
        for (CampoDefinicion campo : catalogo.getCampos()) {
            valores.put(campo.getNombre(), campo.isEsKey() ? "K-" + CatalogoFixtures.nuevoSufijo() : "Valor (BDD)");
        }
        return valores;
    }
}
