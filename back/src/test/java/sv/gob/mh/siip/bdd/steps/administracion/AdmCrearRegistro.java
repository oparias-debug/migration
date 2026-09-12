package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd.ModoCondicion;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.CrearRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoVigenciaDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroResponseDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.RegistroService;

/**
 * CU-ADM-01-crear-registro.feature. La autenticacion/autorizacion del Administrador de Catalogos,
 * "selecciona un catálogo existente", el actor no autorizado y el rechazo generico por falta de
 * autorizacion se reutilizan desde AdmActualizarCatalogo; el paso "{string}" del esquema de
 * vigencia por defecto se reutiliza desde AdmBuscarCatalogoPorNombre (Cucumber exige una unica
 * definicion por texto).
 */
public class AdmCrearRegistro {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    private CrearRegistroRequestDto ultimaSolicitud;
    private RegistroResponseDto ultimoResultado;

    public AdmCrearRegistro(CatalogoRepository catalogoRepository, RegistroRepository registroRepository,
            RegistroService registroService, ContextoCatalogoBdd contextoCatalogo,
            ContextoValidacionBdd contextoValidacion) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^provee un valor para cada campo definido en el catálogo$")
    public void provee_un_valor_para_cada_campo_definido_en_el_catalogo() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        Map<String, Object> valores = new HashMap<>();
        for (CampoDefinicion campo : catalogo.getCampos()) {
            valores.put(campo.getNombre(), campo.isEsKey() ? "K-" + CatalogoFixtures.nuevoSufijo() : "Valor (BDD)");
        }
        ultimaSolicitud = new CrearRegistroRequestDto(valores);
    }

    @Dado("^el valor del campo KEY no existe aún en el catálogo$")
    public void el_valor_del_campo_key_no_existe_aun_en_el_catalogo() {
        // El catalogo recien creado en "selecciona un catálogo existente" no tiene registros
        // previos: el valor de KEY generado en el paso anterior ya cumple esta condicion.
    }

    @Cuando("^solicita crear el registro$")
    public void solicita_crear_el_registro() {
        try {
            ultimoResultado = registroService.crear(contextoCatalogo.getCatalogoActual().getCodigo(), ultimaSolicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema almacena el nuevo registro$")
    public void el_sistema_almacena_el_nuevo_registro() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(registroRepository.existsByCatalogo_CodigoAndClave(contextoCatalogo.getCatalogoActual().getCodigo(),
                ultimoResultado.getKey())).isTrue();
    }

    @Dado("^provee un valor de campo KEY que ya existe en el catálogo$")
    public void provee_un_valor_de_campo_key_que_ya_existe_en_el_catalogo() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        String nombreCampoKey = nombreCampoKey(catalogo);
        String claveExistente = "K-DUPLICADO";

        Registro registro = Registro.builder().catalogo(catalogo).clave(claveExistente).estado(EstadoVigencia.ACTIVE)
                .build();
        registro.getValores().put(nombreCampoKey, claveExistente);
        registroRepository.save(registro);

        Map<String, Object> valores = new HashMap<>();
        for (CampoDefinicion campo : catalogo.getCampos()) {
            valores.put(campo.getNombre(), campo.isEsKey() ? claveExistente : "Valor (BDD)");
        }
        ultimaSolicitud = new CrearRegistroRequestDto(valores);
    }

    @Entonces("^el sistema rechaza la creación del registro$")
    public void el_sistema_rechaza_la_creacion_del_registro() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(ConflictoEstadoException.class);
    }

    @Dado("^crea un registro válido con un valor de KEY no existente en el catálogo$")
    public void crea_un_registro_valido_con_un_valor_de_key_no_existente() {
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        contextoCatalogo.setCatalogoActual(catalogo);
        contextoCatalogo.setModoCondicion(ModoCondicion.VIGENCIA_REGISTRO);
    }

    @Entonces("^el registro queda en estado \"([^\"]*)\"$")
    public void el_registro_queda_en_estado(String estadoEsperado) {
        assertThat(contextoCatalogo.getRegistroResultado().getEstado())
                .isEqualTo(EstadoVigenciaDto.fromValue(estadoEsperado));
    }

    @Cuando("^intenta crear un registro en un catálogo$")
    public void intenta_crear_un_registro_en_un_catalogo() {
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        Map<String, Object> valores = new HashMap<>();
        for (CampoDefinicion campo : catalogo.getCampos()) {
            valores.put(campo.getNombre(), campo.isEsKey() ? "K-" + CatalogoFixtures.nuevoSufijo() : "Valor (BDD)");
        }
        try {
            registroService.crear(catalogo.getCodigo(), new CrearRegistroRequestDto(valores));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    private static String nombreCampoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(CampoDefinicion::isEsKey).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }
}
