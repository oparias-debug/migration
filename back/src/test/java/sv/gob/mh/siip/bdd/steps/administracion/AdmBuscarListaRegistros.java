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
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ListaRegistrosResponseDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.RegistroService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-01-buscar-lista-registros.feature. Sin Dado de autenticacion propio: se autentica aqui mismo. */
public class AdmBuscarListaRegistros {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final RegistroService registroService;

    private Catalogo catalogoActual;
    private ListaRegistrosResponseDto ultimoResultado;

    public AdmBuscarListaRegistros(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, RegistroService registroService) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.registroService = registroService;
    }

    @Dado("^que un catálogo tiene registros almacenados$")
    public void que_un_catalogo_tiene_registros_almacenados() {
        String nombreUsuario = "admin.catalogos.bdd." + CatalogoFixtures.nuevoSufijo();
        usuarioRepository.save(CatalogoFixtures.nuevoAdministradorCatalogos(nombreUsuario));
        autenticarComo(nombreUsuario);

        catalogoActual = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        guardarRegistro(catalogoActual, "K1", "Descripción 1 (BDD)");
        guardarRegistro(catalogoActual, "K2", "Descripción 2 (BDD)");
    }

    @Cuando("^se solicita la lista de registros del catálogo sin especificar clave ni lista de campos$")
    public void se_solicita_la_lista_de_registros_sin_especificar_clave_ni_lista_de_campos() {
        ultimoResultado = registroService.buscarLista(catalogoActual.getCodigo(), null);
    }

    @Entonces("^el sistema retorna el valor del primer campo no-KEY de todos los registros$")
    public void el_sistema_retorna_el_valor_del_primer_campo_no_key_de_todos_los_registros() {
        String nombreCampoNoKey = nombreCampoNoKey(catalogoActual);
        assertThat(ultimoResultado.getRegistros()).hasSize(2)
                .allSatisfy(registro -> assertThat(registro.getValores()).containsOnlyKeys(nombreCampoNoKey));
    }

    @Cuando("^se solicita la lista de registros del catálogo indicando una lista de nombres de campos$")
    public void se_solicita_la_lista_de_registros_indicando_una_lista_de_nombres_de_campos() {
        String nombreCampoKey = nombreCampoKey(catalogoActual);
        ultimoResultado = registroService.buscarLista(catalogoActual.getCodigo(), List.of(nombreCampoKey));
    }

    @Entonces("^el sistema retorna la lista de valores de los campos solicitados para todos los registros encontrados$")
    public void el_sistema_retorna_la_lista_de_valores_de_los_campos_solicitados() {
        String nombreCampoKey = nombreCampoKey(catalogoActual);
        assertThat(ultimoResultado.getRegistros()).hasSize(2)
                .allSatisfy(registro -> assertThat(registro.getValores()).containsOnlyKeys(nombreCampoKey));
    }

    private void guardarRegistro(Catalogo catalogo, String clave, String descripcion) {
        Registro registro = Registro.builder().catalogo(catalogo).clave(clave).estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put(nombreCampoKey(catalogo), clave);
        registro.getValores().put(nombreCampoNoKey(catalogo), descripcion);
        registroRepository.save(registro);
    }

    private static String nombreCampoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(CampoDefinicion::isEsKey).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }

    private static String nombreCampoNoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(campo -> !campo.isEsKey()).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
