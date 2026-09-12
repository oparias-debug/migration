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
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogoHijoResponseDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-01-buscar-catalogos-hijos.feature. Sin Dado de autenticacion propio: se autentica aqui mismo. */
public class AdmBuscarCatalogosHijos {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;

    private String codigoPadre;
    private List<CatalogoHijoResponseDto> hijosEncontrados;

    public AdmBuscarCatalogosHijos(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            CatalogoService catalogoService) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
    }

    @Dado("^que existen catálogos que referencian a un catálogo padre determinado como PARENT$")
    public void que_existen_catalogos_que_referencian_a_un_catalogo_padre_como_parent() {
        String nombreUsuario = "admin.catalogos.bdd." + CatalogoFixtures.nuevoSufijo();
        usuarioRepository.save(CatalogoFixtures.nuevoAdministradorCatalogos(nombreUsuario));
        autenticarComo(nombreUsuario);

        Catalogo padre = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-PADRE-" + CatalogoFixtures.nuevoSufijo(), "Catálogo padre (BDD)"));
        codigoPadre = padre.getCodigo();

        Catalogo hijo = CatalogoFixtures.nuevoCatalogo("CAT-HIJO-" + CatalogoFixtures.nuevoSufijo(), "Catálogo hijo (BDD)");
        hijo.setCatalogoPadreCodigo(codigoPadre);
        catalogoRepository.save(hijo);
    }

    @Cuando("^se provee el código de ese catálogo padre$")
    public void se_provee_el_codigo_de_ese_catalogo_padre() {
        hijosEncontrados = catalogoService.buscarHijos(codigoPadre);
    }

    @Entonces("^el sistema retorna la lista de \\{código, nombre\\} de todos los catálogos que lo referencian como PARENT$")
    public void el_sistema_retorna_la_lista_de_codigo_nombre_de_los_catalogos_hijos() {
        assertThat(hijosEncontrados).hasSize(1);
        assertThat(hijosEncontrados.get(0).getCodigo()).startsWith("CAT-HIJO-");
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
