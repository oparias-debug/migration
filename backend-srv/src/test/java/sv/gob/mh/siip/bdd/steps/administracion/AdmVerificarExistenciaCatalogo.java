package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.model.administracion.dto.CatalogExistenceResponseDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-01-verificar-existencia-catalogo.feature. */
public class AdmVerificarExistenciaCatalogo {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;

    private String nombreConsultado;
    private CatalogExistenceResponseDto ultimoResultado;

    public AdmVerificarExistenciaCatalogo(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            CatalogoService catalogoService) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
    }

    @Dado("^un nombre de catálogo que ya existe en el catalogMaster$")
    public void un_nombre_de_catalogo_que_ya_existe() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        nombreConsultado = "Catálogo BDD " + CatalogoFixtures.nuevoSufijo();
        catalogoRepository.save(CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), nombreConsultado));
    }

    @Dado("^un nombre de catálogo que no existe en el catalogMaster$")
    public void un_nombre_de_catalogo_que_no_existe() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        nombreConsultado = "NoExiste " + CatalogoFixtures.nuevoSufijo();
    }

    @Cuando("^realizo la búsqueda por ese nombre$")
    public void realizo_la_busqueda_por_ese_nombre() {
        ultimoResultado = catalogoService.verificarExistencia(nombreConsultado);
    }

    @Entonces("^el sistema confirma su existencia$")
    public void el_sistema_confirma_su_existencia() {
        assertThat(ultimoResultado.getExists()).isTrue();
    }

    @Entonces("^el sistema indica que no está definido$")
    public void el_sistema_indica_que_no_esta_definido() {
        assertThat(ultimoResultado.getExists()).isFalse();
    }
}
