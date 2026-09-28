package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.RegistroService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-ADM-01-buscar-lista-registros.feature. "realizo la búsqueda" (escenarios 2 y 3) se reutiliza
 * sin redefinir desde {@link AdmComun} (tambien lo usa buscar-registro-por-clave.feature),
 * dispatchando aqui a {@code RegistroService.buscarLista} porque ninguno de los Dado de este
 * .feature fija una {@link ContextoCatalogoBdd#getClaveConsultada()}.
 */
public class AdmBuscarListaRegistros {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;

    public AdmBuscarListaRegistros(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, RegistroService registroService, ContextoCatalogoBdd contextoCatalogo) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
    }

    @Dado("^un catálogo con registros y una lista de nombres de campos$")
    public void un_catalogo_con_registros_y_lista_de_campos() {
        Catalogo catalogo = prepararCatalogoConRegistros();
        contextoCatalogo.setCamposSolicitados(List.of(CatalogoFixtures.nombreCampoNoKey(catalogo)));
    }

    @Cuando("^realizo la búsqueda sin indicar KEY$")
    public void realizo_la_busqueda_sin_indicar_key() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        contextoCatalogo.setResultadoLista(registroService.buscarLista(catalogo.getCodigo(),
                contextoCatalogo.getCamposSolicitados(), PageRequest.of(0, 20)));
    }

    @Entonces("^el sistema retorna los valores de cada campo solicitado para cada registro conforme a la Regla 5$")
    public void el_sistema_retorna_los_valores_solicitados_para_cada_registro() {
        String nombreCampoNoKey = CatalogoFixtures.nombreCampoNoKey(contextoCatalogo.getCatalogoActual());
        assertThat(contextoCatalogo.getResultadoLista().getContent()).hasSize(2)
                .allSatisfy(registro -> assertThat(registro.getValues()).containsOnlyKeys(nombreCampoNoKey));
    }

    @Dado("^un catálogo con registros sin lista de campos$")
    public void un_catalogo_con_registros_sin_lista_de_campos() {
        // A diferencia de prepararCatalogoConRegistros() (un solo campo no-KEY, indistinguible de
        // "el campo solicitado"), este catálogo define DOS campos no-KEY para poder verificar que,
        // sin lista de campos, el sistema retorna solo el primero por posición (Regla 5) y no todos.
        prepararCatalogoConDosCamposNoKey();
    }

    @Entonces("^el sistema retorna el valor del primer campo no KEY de todos los registros conforme a la Regla 5$")
    public void el_sistema_retorna_el_primer_campo_no_key_de_todos_los_registros() {
        assertThat(contextoCatalogo.getResultadoLista().getContent()).hasSize(2)
                .allSatisfy(registro -> assertThat(registro.getValues()).containsOnlyKeys("descripcion"));
    }

    @Dado("^que el catálogo está INACTIVE$")
    public void que_el_catalogo_esta_inactive() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo inactivo (BDD)");
        catalogo.setEstado(EstadoVigencia.INACTIVE);
        catalogo = catalogoRepository.save(catalogo);
        guardarRegistro(catalogo, "K1", "Descripción 1 (BDD)");
        contextoCatalogo.setCatalogoActual(catalogo);
    }

    @Entonces("^el sistema retorna INACTIVE para todos los registros conforme a la Regla 12$")
    public void el_sistema_retorna_inactive_para_todos_los_registros() {
        assertThat(contextoCatalogo.getResultadoLista().getContent()).isNotEmpty()
                .allSatisfy(registro -> assertThat(registro.getActive()).isEqualTo(ActiveStatusDto.INACTIVE));
    }

    private Catalogo prepararCatalogoConRegistros() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        guardarRegistro(catalogo, "K1", "Descripción 1 (BDD)");
        guardarRegistro(catalogo, "K2", "Descripción 2 (BDD)");
        contextoCatalogo.setCatalogoActual(catalogo);
        return catalogo;
    }

    private void guardarRegistro(Catalogo catalogo, String clave, String descripcion) {
        Registro registro = Registro.builder().catalogo(catalogo).clave(clave).estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put(CatalogoFixtures.nombreCampoKey(catalogo), clave);
        registro.getValores().put(CatalogoFixtures.nombreCampoNoKey(catalogo), descripcion);
        registroRepository.save(registro);
    }

    /** Catálogo con campos "codigoInterno" (KEY), "descripcion" (no-KEY, posición 1) y "notas" (no-KEY, posición 2). */
    private Catalogo prepararCatalogoConDosCamposNoKey() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = Catalogo.builder().codigo("CAT-" + CatalogoFixtures.nuevoSufijo())
                .nombre("Catálogo de prueba BDD").estado(EstadoVigencia.ACTIVE).build();
        Catalogo catalogoAsignado = catalogo;
        List<CampoDefinicion> campos = List.of(CatalogoFixtures.campoKey("codigoInterno", 0),
                CatalogoFixtures.campoTexto("descripcion", 1), CatalogoFixtures.campoTexto("notas", 2));
        campos.forEach(campo -> campo.setCatalogo(catalogoAsignado));
        catalogo.getCampos().addAll(campos);
        catalogo = catalogoRepository.save(catalogo);

        guardarRegistroConDosCamposNoKey(catalogo, "K1", "Descripción 1 (BDD)", "Nota 1 (BDD)");
        guardarRegistroConDosCamposNoKey(catalogo, "K2", "Descripción 2 (BDD)", "Nota 2 (BDD)");
        contextoCatalogo.setCatalogoActual(catalogo);
        return catalogo;
    }

    private void guardarRegistroConDosCamposNoKey(Catalogo catalogo, String clave, String descripcion, String nota) {
        Registro registro = Registro.builder().catalogo(catalogo).clave(clave).estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put("codigoInterno", clave);
        registro.getValores().put("descripcion", descripcion);
        registro.getValores().put("notas", nota);
        registroRepository.save(registro);
    }
}
