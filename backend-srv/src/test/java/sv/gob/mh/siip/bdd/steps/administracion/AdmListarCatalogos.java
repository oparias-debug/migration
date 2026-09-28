package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-01-listar-catalogos.feature. */
public class AdmListarCatalogos {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;

    private Catalogo catalogoActivo;
    private Catalogo catalogoInactivo;
    private Page<CatalogDto> ultimoResultado;

    public AdmListarCatalogos(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            CatalogoService catalogoService) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
    }

    @Dado("^que existen catálogos activos e inactivos en el catalogMaster$")
    public void que_existen_catalogos_activos_e_inactivos() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        catalogoActivo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-ACTIVO-" + CatalogoFixtures.nuevoSufijo(), "Catálogo activo (BDD)"));
        Catalogo inactivo = CatalogoFixtures.nuevoCatalogo("CAT-INACTIVO-" + CatalogoFixtures.nuevoSufijo(),
                "Catálogo inactivo (BDD)");
        inactivo.setEstado(EstadoVigencia.INACTIVE);
        catalogoInactivo = catalogoRepository.save(inactivo);
    }

    @Cuando("^solicito el listado de catálogos$")
    public void solicito_el_listado_de_catalogos() {
        ultimoResultado = catalogoService.listar(PageRequest.of(0, 100));
    }

    @Entonces("^el sistema retorna todos los catálogos del catalogMaster, incluyendo los inactivos$")
    public void el_sistema_retorna_todos_los_catalogos() {
        assertThat(ultimoResultado.getContent()).extracting(CatalogDto::getCode)
                .contains(catalogoActivo.getCodigo(), catalogoInactivo.getCodigo());
    }
}
