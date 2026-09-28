package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-01-consultar-catalogo.feature. */
public class AdmConsultarCatalogo {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;
    private final ContextoValidacionBdd contextoValidacion;

    private String codigoConsultado;
    private CatalogDto ultimoResultado;

    public AdmConsultarCatalogo(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            CatalogoService catalogoService, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un código de catálogo existente en el catalogMaster$")
    public void un_codigo_de_catalogo_existente() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        codigoConsultado = catalogo.getCodigo();
    }

    @Dado("^un código de catálogo que no existe en el catalogMaster$")
    public void un_codigo_de_catalogo_que_no_existe() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        codigoConsultado = "NOEXISTE-" + CatalogoFixtures.nuevoSufijo();
    }

    @Cuando("^consulto el catálogo por ese código$")
    public void consulto_el_catalogo_por_ese_codigo() {
        try {
            ultimoResultado = catalogoService.consultar(codigoConsultado);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema retorna su definición completa, incluyendo nombre, padre, estado, vigencia y campos$")
    public void el_sistema_retorna_su_definicion_completa() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getCode()).isEqualTo(codigoConsultado);
        assertThat(ultimoResultado.getName()).isNotNull();
        assertThat(ultimoResultado.getActive()).isNotNull();
        assertThat(ultimoResultado.getFields()).isNotEmpty();
    }
}
