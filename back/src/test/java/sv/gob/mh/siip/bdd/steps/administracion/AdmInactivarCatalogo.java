package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.administracion.service.RegistroService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-ADM-01-inactivar-catalogo.feature. "un catálogo existente" y las 2 variantes de "fijo su
 * ACTIVE/TO DATE ..." se reutilizan sin redefinir desde {@link AdmComun} (tambien las usa
 * inactivar-registro.feature).
 */
public class AdmInactivarCatalogo {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final CatalogoService catalogoService;
    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    private Catalogo catalogoActual;
    private Page<CatalogRecordDto> resultadoRegistros;

    public AdmInactivarCatalogo(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, CatalogoService catalogoService, RegistroService registroService,
            ContextoCatalogoBdd contextoCatalogo, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.catalogoService = catalogoService;
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un catálogo activo$")
    public void un_catalogo_activo() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        contextoCatalogo.setCatalogoActual(catalogo);
    }

    @Entonces("^el sistema fija automáticamente la TO DATE en la fecha actual conforme a la Regla 9a$")
    public void el_sistema_fija_automaticamente_la_to_date() {
        assertThat(contextoCatalogo.getCatalogoResultado().getToDate()).isEqualTo(LocalDate.now(ZONA_EL_SALVADOR));
    }

    @Entonces("^el catálogo queda INACTIVE conforme a las Reglas 9b y 14$")
    public void el_catalogo_queda_inactive() {
        assertThat(contextoCatalogo.getCatalogoResultado().getActive()).isEqualTo(ActiveStatusDto.INACTIVE);
    }

    @Dado("^un catálogo inactivo$")
    public void un_catalogo_inactivo() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo inactivo (BDD)");
        catalogo.setEstado(EstadoVigencia.INACTIVE);
        catalogoActual = catalogoRepository.save(catalogo);

        Registro registro = Registro.builder().catalogo(catalogoActual).clave("K1").estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put(CatalogoFixtures.nombreCampoKey(catalogoActual), "K1");
        registroRepository.save(registro);
    }

    @Cuando("^se consultan sus registros$")
    public void se_consultan_sus_registros() {
        resultadoRegistros = registroService.buscarLista(catalogoActual.getCodigo(), null, PageRequest.of(0, 20));
    }

    @Entonces("^toda búsqueda sobre ese catálogo retorna INACTIVE conforme a la Regla 12$")
    public void toda_busqueda_retorna_inactive() {
        assertThat(resultadoRegistros.getContent()).isNotEmpty()
                .allSatisfy(registro -> assertThat(registro.getActive()).isEqualTo(ActiveStatusDto.INACTIVE));
    }

    @Cuando("^intento eliminar \\(borrar\\) el catálogo$")
    public void intento_eliminar_el_catalogo() {
        try {
            catalogoService.eliminar(contextoCatalogo.getCatalogoActual().getCodigo());
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
