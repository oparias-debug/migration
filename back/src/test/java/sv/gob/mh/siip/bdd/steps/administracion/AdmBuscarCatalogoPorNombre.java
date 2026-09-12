package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd.ModoCondicion;
import sv.gob.mh.siip.model.administracion.dto.CampoDefinicionDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCampoDto;
import sv.gob.mh.siip.model.administracion.dto.VigenciaDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.administracion.service.RegistroService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-ADM-01-buscar-catalogo-por-nombre.feature.
 *
 * El paso "{string}" (una condicion del esquema del escenario entre comillas, sin texto fijo
 * alrededor) se define aqui y lo reutilizan sin redefinir CU-ADM-01-crear-catalogo.feature y
 * CU-ADM-01-crear-registro.feature para su propio "<condicion_vigencia>" (Cucumber exige una
 * unica definicion por texto): el significado de la condicion depende del
 * {@link ModoCondicion} que el Dado propio de cada .feature deja en {@link ContextoCatalogoBdd}.
 */
public class AdmBuscarCatalogoPorNombre {

    private static final String HEADER_USUARIO = "X-Usuario";
    // CatalogoServiceImpl.calcularEstadoVigencia compara contra LocalDate.now(ZONA_EL_SALVADOR),
    // no contra el huso horario por defecto de la JVM: usar el mismo huso aquí evita que la fecha
    // "de ayer" que arma este test deje de ser "antes de hoy" según el reloj de producción.
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;
    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;

    public AdmBuscarCatalogoPorNombre(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            CatalogoService catalogoService, RegistroService registroService, ContextoCatalogoBdd contextoCatalogo) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
    }

    @Dado("^que se provee un nombre de catálogo$")
    public void que_se_provee_un_nombre_de_catalogo() {
        String nombreUsuario = "admin.catalogos.bdd." + CatalogoFixtures.nuevoSufijo();
        usuarioRepository.save(CatalogoFixtures.nuevoAdministradorCatalogos(nombreUsuario));
        autenticarComo(nombreUsuario);

        contextoCatalogo.setModoCondicion(ModoCondicion.EXISTENCIA_POR_NOMBRE);
        contextoCatalogo.setNombreConsultado("Catálogo BDD " + CatalogoFixtures.nuevoSufijo());
    }

    // Alternativa explicita (en vez de un "{string}" generico) para no colisionar con pasos ya
    // definidos en otras features que tambien consisten en dos {string} entre texto fijo (p.ej.
    // "{string} consulta la pestaña {string}"): esas producen igualmente un texto que empieza y
    // termina en comillas, y un "^\"(.*)\"$" ambiguaria contra ellas.
    @Cuando("^\"(el nombre corresponde a un catálogo ya definido"
            + "|el nombre no corresponde a ningún catálogo definido"
            + "|no se indican fechas de vigencia"
            + "|la fecha \"hasta\" \\(TO DATE\\) es anterior a la fecha actual)\"$")
    public void condicion_del_esquema_del_escenario(String condicion) {
        switch (contextoCatalogo.getModoCondicion()) {
            case EXISTENCIA_POR_NOMBRE -> ejecutarExistenciaPorNombre(condicion);
            case VIGENCIA_CATALOGO -> ejecutarVigenciaCatalogo(condicion);
            case VIGENCIA_REGISTRO -> ejecutarVigenciaRegistro(condicion);
        }
    }

    @Entonces("^el sistema indica \"([^\"]*)\"$")
    public void el_sistema_indica(String resultadoEsperado) {
        boolean existeEsperado = resultadoEsperado.equals("que el catálogo existe");
        assertThat(contextoCatalogo.getExistenciaResultado().getExiste()).isEqualTo(existeEsperado);
    }

    private void ejecutarExistenciaPorNombre(String condicion) {
        String nombre = contextoCatalogo.getNombreConsultado();
        if (condicion.equals("el nombre corresponde a un catálogo ya definido")) {
            catalogoRepository.save(CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), nombre));
        }
        contextoCatalogo.setExistenciaResultado(catalogoService.buscarPorNombre(nombre));
    }

    private void ejecutarVigenciaCatalogo(String condicion) {
        LocalDate fechaHasta = fechaHastaSegunCondicion(condicion);
        CrearCatalogoRequestDto request = new CrearCatalogoRequestDto(
                "CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de vigencia (BDD)",
                List.of(new CampoDefinicionDto("codigoInterno", TipoCampoDto.STRING, true)));
        request.setVigencia(new VigenciaDto().fechaHasta(fechaHasta));
        contextoCatalogo.setCatalogoResultado(catalogoService.crear(request));
    }

    private void ejecutarVigenciaRegistro(String condicion) {
        LocalDate fechaHasta = fechaHastaSegunCondicion(condicion);
        java.util.Map<String, Object> valores = new java.util.HashMap<>();
        contextoCatalogo.getCatalogoActual().getCampos().forEach(campo -> valores.put(campo.getNombre(),
                campo.isEsKey() ? "K-" + CatalogoFixtures.nuevoSufijo() : "Valor (BDD)"));
        CrearRegistroRequestDto request = new CrearRegistroRequestDto(valores);
        request.setVigencia(new VigenciaDto().fechaHasta(fechaHasta));
        contextoCatalogo.setRegistroResultado(
                registroService.crear(contextoCatalogo.getCatalogoActual().getCodigo(), request));
    }

    private static LocalDate fechaHastaSegunCondicion(String condicion) {
        return condicion.equals("no se indican fechas de vigencia") ? null
                : LocalDate.now(ZONA_EL_SALVADOR).minusDays(1);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
