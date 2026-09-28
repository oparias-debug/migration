package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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
 * CU-ADM-01-buscar-registro-por-clave.feature. "realizo la búsqueda" (escenarios 1, 2, 3 y 5) se
 * reutiliza sin redefinir desde {@link AdmComun} (tambien lo usa
 * buscar-lista-registros.feature), dispatchando aqui a {@code RegistroService.buscarPorClave}
 * porque los Dado de este .feature siempre fijan una
 * {@link ContextoCatalogoBdd#getClaveConsultada()}. "el sistema reporta un error conforme a la
 * Regla 4" (escenarios 3 y 4) se reutiliza desde {@link AdmComun}.
 */
public class AdmBuscarRegistroPorClave {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    public AdmBuscarRegistroPorClave(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, RegistroService registroService, ContextoCatalogoBdd contextoCatalogo,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un valor de KEY existente y una lista de nombres de campos$")
    public void un_valor_de_key_existente_y_una_lista_de_campos() {
        Registro registro = prepararCatalogoYRegistro();
        contextoCatalogo.setClaveConsultada(registro.getClave());
        contextoCatalogo.setCamposSolicitados(List.of(CatalogoFixtures.nombreCampoNoKey(contextoCatalogo.getCatalogoActual())));
    }

    @Entonces("^el sistema retorna el valor de cada campo solicitado conforme a la Regla 4$")
    public void el_sistema_retorna_el_valor_de_cada_campo_solicitado() {
        assertThat(contextoCatalogo.getRegistroResultado().getValues())
                .containsOnlyKeys(contextoCatalogo.getCamposSolicitados().toArray(String[]::new));
    }

    @Dado("^un valor de KEY existente sin lista de campos$")
    public void un_valor_de_key_existente_sin_lista_de_campos() {
        Registro registro = prepararCatalogoYRegistro();
        contextoCatalogo.setClaveConsultada(registro.getClave());
    }

    @Entonces("^el sistema retorna el valor del primer campo no KEY del registro conforme a la Regla 4$")
    public void el_sistema_retorna_el_primer_campo_no_key_del_registro() {
        assertThat(contextoCatalogo.getRegistroResultado().getValues())
                .containsOnlyKeys(CatalogoFixtures.nombreCampoNoKey(contextoCatalogo.getCatalogoActual()));
    }

    @Dado("^un valor de KEY que no corresponde a ningún registro$")
    public void un_valor_de_key_que_no_corresponde_a_ningun_registro() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        contextoCatalogo.setCatalogoActual(catalogo);
        contextoCatalogo.setClaveConsultada("NOEXISTE-" + CatalogoFixtures.nuevoSufijo());
    }

    @Dado("^un nombre de campo que no existe en el catálogo$")
    public void un_nombre_de_campo_que_no_existe_en_el_catalogo() {
        Registro registro = prepararCatalogoYRegistro();
        contextoCatalogo.setClaveConsultada(registro.getClave());
        contextoCatalogo.setCamposSolicitados(List.of("campoInexistente-BDD"));
    }

    @Cuando("^realizo la búsqueda solicitando ese campo$")
    public void realizo_la_busqueda_solicitando_ese_campo() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        try {
            contextoCatalogo.setRegistroResultado(registroService.buscarPorClave(catalogo.getCodigo(),
                    contextoCatalogo.getClaveConsultada(), contextoCatalogo.getCamposSolicitados()));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^que el catálogo o el registro está INACTIVE$")
    public void que_el_catalogo_o_el_registro_esta_inactive() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo inactivo (BDD)");
        catalogo.setEstado(EstadoVigencia.INACTIVE);
        catalogo = catalogoRepository.save(catalogo);
        Registro registro = guardarRegistro(catalogo, "K1", "Descripción (BDD)");
        contextoCatalogo.setCatalogoActual(catalogo);
        contextoCatalogo.setClaveConsultada(registro.getClave());
    }

    @Entonces("^el sistema retorna INACTIVE conforme a la Regla 12$")
    public void el_sistema_retorna_inactive() {
        assertThat(contextoCatalogo.getRegistroResultado().getActive()).isEqualTo(ActiveStatusDto.INACTIVE);
    }

    private Registro prepararCatalogoYRegistro() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        contextoCatalogo.setCatalogoActual(catalogo);
        return guardarRegistro(catalogo, "K1", "Descripción (BDD)");
    }

    private Registro guardarRegistro(Catalogo catalogo, String clave, String descripcion) {
        Registro registro = Registro.builder().catalogo(catalogo).clave(clave).estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put(CatalogoFixtures.nombreCampoKey(catalogo), clave);
        registro.getValores().put(CatalogoFixtures.nombreCampoNoKey(catalogo), descripcion);
        return registroRepository.save(registro);
    }
}
