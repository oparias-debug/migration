package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.EstadoVigenciaDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroValoresResponseDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.RegistroService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-ADM-01-buscar-registro-por-clave.feature. Sin Dado de autenticacion propio en ninguno de sus
 * escenarios: cada Dado de datos se autentica a si mismo. "el sistema reporta un error" se
 * reutiliza desde AdmActualizarRegistro sin redefinir (Cucumber exige una unica definicion por
 * texto).
 */
public class AdmBuscarRegistroPorClave {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final RegistroService registroService;
    private final ContextoValidacionBdd contextoValidacion;

    private Catalogo catalogoActual;
    private Registro registroActual;
    private List<String> camposSolicitados;
    private RegistroValoresResponseDto ultimoResultado;
    private String claveConsultada;

    public AdmBuscarRegistroPorClave(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, RegistroService registroService,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.registroService = registroService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que existe un registro con una clave \\(KEY\\) determinada en un catálogo$")
    public void que_existe_un_registro_con_una_clave_key_determinada() {
        autenticarComoNuevoAdministrador();
        catalogoActual = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        registroActual = guardarRegistro(catalogoActual, "K1", "Descripción (BDD)");
    }

    @Cuando("^se provee el código del catálogo y el valor de la clave, sin especificar lista de campos$")
    public void se_provee_el_codigo_del_catalogo_y_el_valor_de_la_clave_sin_lista_de_campos() {
        ultimoResultado = registroService.buscarPorClave(catalogoActual.getCodigo(), registroActual.getClave(), null);
    }

    @Entonces("^el sistema retorna el valor del primer campo no-KEY del registro$")
    public void el_sistema_retorna_el_valor_del_primer_campo_no_key_del_registro() {
        assertThat(ultimoResultado.getValores()).containsOnlyKeys(nombreCampoNoKey(catalogoActual));
    }

    @Dado("^se especifica una lista de nombres de campos que existen en el catálogo$")
    public void se_especifica_una_lista_de_nombres_de_campos_que_existen_en_el_catalogo() {
        camposSolicitados = List.of(nombreCampoNoKey(catalogoActual));
    }

    @Cuando("^se provee el código del catálogo, el valor de la clave y la lista de campos$")
    public void se_provee_el_codigo_del_catalogo_el_valor_de_la_clave_y_la_lista_de_campos() {
        try {
            ultimoResultado = registroService.buscarPorClave(catalogoActual.getCodigo(), registroActual.getClave(),
                    camposSolicitados);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema retorna el conjunto de valores solicitados$")
    public void el_sistema_retorna_el_conjunto_de_valores_solicitados() {
        assertThat(ultimoResultado.getValores()).containsOnlyKeys(camposSolicitados.toArray(String[]::new));
    }

    @Dado("^que no existe ningún registro con la clave \\(KEY\\) provista en el catálogo$")
    public void que_no_existe_ningun_registro_con_la_clave_key_provista() {
        autenticarComoNuevoAdministrador();
        catalogoActual = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        claveConsultada = "NOEXISTE-" + CatalogoFixtures.nuevoSufijo();
    }

    @Cuando("^se realiza la búsqueda por esa clave$")
    public void se_realiza_la_busqueda_por_esa_clave() {
        try {
            ultimoResultado = registroService.buscarPorClave(catalogoActual.getCodigo(), claveConsultada, null);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^se especifica una lista de campos donde al menos uno no existe en el catálogo$")
    public void se_especifica_una_lista_de_campos_donde_al_menos_uno_no_existe() {
        camposSolicitados = List.of(nombreCampoNoKey(catalogoActual), "campoInexistente-BDD");
    }

    @Dado("^que el catálogo está marcado como INACTIVE$")
    public void que_el_catalogo_esta_marcado_como_inactive() {
        autenticarComoNuevoAdministrador();
        catalogoActual = CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD");
        catalogoActual.setEstado(EstadoVigencia.INACTIVE);
        catalogoActual = catalogoRepository.save(catalogoActual);
        registroActual = guardarRegistro(catalogoActual, "K1", "Descripción (BDD)");
    }

    @Cuando("^se busca cualquier registro de ese catálogo por su clave$")
    public void se_busca_cualquier_registro_de_ese_catalogo_por_su_clave() {
        ultimoResultado = registroService.buscarPorClave(catalogoActual.getCodigo(), registroActual.getClave(), null);
    }

    @Entonces("^el sistema retorna INACTIVO para el registro consultado$")
    public void el_sistema_retorna_inactivo_para_el_registro_consultado() {
        assertThat(ultimoResultado.getEstado()).isEqualTo(EstadoVigenciaDto.INACTIVE);
    }

    private Registro guardarRegistro(Catalogo catalogo, String clave, String descripcion) {
        Registro registro = Registro.builder().catalogo(catalogo).clave(clave).estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put(nombreCampoKey(catalogo), clave);
        registro.getValores().put(nombreCampoNoKey(catalogo), descripcion);
        return registroRepository.save(registro);
    }

    private static String nombreCampoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(CampoDefinicion::isEsKey).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }

    private static String nombreCampoNoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(campo -> !campo.isEsKey()).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }

    private void autenticarComoNuevoAdministrador() {
        String nombreUsuario = "admin.catalogos.bdd." + CatalogoFixtures.nuevoSufijo();
        usuarioRepository.save(CatalogoFixtures.nuevoAdministradorCatalogos(nombreUsuario));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
