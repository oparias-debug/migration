package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.RegistroService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-ADM-01-inactivar-registro.feature. "un registro existente" y las 2 variantes de "fijo su
 * ACTIVE/TO DATE ..." se reutilizan sin redefinir desde {@link AdmComun} (tambien las usa
 * inactivar-catalogo.feature).
 */
public class AdmInactivarRegistro {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    public AdmInactivarRegistro(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, RegistroService registroService,
            ContextoCatalogoBdd contextoCatalogo, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un registro activo$")
    public void un_registro_activo() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        Registro registro = Registro.builder().catalogo(catalogo).clave("K1").estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put(CatalogoFixtures.nombreCampoKey(catalogo), "K1");
        registro.getValores().put(CatalogoFixtures.nombreCampoNoKey(catalogo), "Descripción (BDD)");
        contextoCatalogo.setCatalogoActual(catalogo);
        contextoCatalogo.setRegistroActual(registroRepository.save(registro));
    }

    @Entonces("^el sistema fija automáticamente su TO DATE en la fecha actual conforme a la Regla 9a$")
    public void el_sistema_fija_automaticamente_su_to_date() {
        assertThat(contextoCatalogo.getRegistroResultado().getToDate()).isEqualTo(LocalDate.now(ZONA_EL_SALVADOR));
    }

    @Entonces("^el registro queda INACTIVE conforme a las Reglas 9b y 14$")
    public void el_registro_queda_inactive() {
        assertThat(contextoCatalogo.getRegistroResultado().getActive()).isEqualTo(ActiveStatusDto.INACTIVE);
    }

    @Cuando("^intento eliminar \\(borrar\\) el registro$")
    public void intento_eliminar_el_registro() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        try {
            registroService.eliminar(catalogo.getCodigo(), contextoCatalogo.getRegistroActual().getClave());
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
