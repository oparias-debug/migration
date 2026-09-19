package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-01-consultar-catalogos-hijos.feature. */
public class AdmConsultarCatalogosHijos {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;

    private String codigoPadre;
    private List<CatalogSummaryDto> hijosEncontrados;

    public AdmConsultarCatalogosHijos(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            CatalogoService catalogoService) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
    }

    @Dado("^un catálogo que tiene catálogos hijos$")
    public void un_catalogo_que_tiene_catalogos_hijos() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo padre = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-PADRE-" + CatalogoFixtures.nuevoSufijo(), "Catálogo padre (BDD)"));
        codigoPadre = padre.getCodigo();

        Catalogo hijo = CatalogoFixtures.nuevoCatalogo("CAT-HIJO-" + CatalogoFixtures.nuevoSufijo(), "Catálogo hijo (BDD)");
        hijo.setCatalogoPadreCodigo(codigoPadre);
        catalogoRepository.save(hijo);
    }

    @Dado("^un catálogo sin catálogos hijos$")
    public void un_catalogo_sin_catalogos_hijos() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo sin hijos (BDD)"));
        codigoPadre = catalogo.getCodigo();
    }

    @Cuando("^lo consulto como padre$")
    public void lo_consulto_como_padre() {
        hijosEncontrados = catalogoService.consultarHijos(codigoPadre);
    }

    @Entonces("^el sistema retorna la lista de código y nombre de cada catálogo hijo conforme a la Regla 15$")
    public void el_sistema_retorna_la_lista_de_hijos() {
        assertThat(hijosEncontrados).hasSize(1);
        assertThat(hijosEncontrados.get(0).getCode()).startsWith("CAT-HIJO-");
    }

    @Entonces("^el sistema retorna una lista vacía$")
    public void el_sistema_retorna_una_lista_vacia() {
        assertThat(hijosEncontrados).isEmpty();
    }
}
