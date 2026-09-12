package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActualizarCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CampoDefinicionDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoVigenciaDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCampoDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * CU-ADM-01-actualizar-catalogo.feature.
 *
 * Steps genericos definidos aqui y reutilizados sin redefinir por el resto de features de
 * CU-ADM-01 (Cucumber exige una unica definicion por texto): autenticacion/autorizacion del
 * Administrador de Catalogos, el actor no autorizado, "selecciona un catálogo existente" (tambien
 * usado por CU-ADM-01-crear-registro.feature via {@link ContextoCatalogoBdd}) y el rechazo
 * generico por falta de autorizacion (via {@link ContextoValidacionBdd}).
 */
public class AdmActualizarCatalogo {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final CatalogoService catalogoService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    private ActualizarCatalogoRequestDto ultimaSolicitud;
    private CatalogoResponseDto ultimoResultado;
    private String codigoOriginal;

    public AdmActualizarCatalogo(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, CatalogoService catalogoService,
            ContextoCatalogoBdd contextoCatalogo, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.catalogoService = catalogoService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos$")
    public void que_el_administrador_de_catalogos_esta_autenticado_y_autorizado() {
        String nombreUsuario = "admin.catalogos.bdd." + CatalogoFixtures.nuevoSufijo();
        usuarioRepository.save(CatalogoFixtures.nuevoAdministradorCatalogos(nombreUsuario));
        autenticarComo(nombreUsuario);
    }

    @Dado("^selecciona un catálogo existente$")
    public void selecciona_un_catalogo_existente() {
        contextoCatalogo.setCatalogoActual(nuevoCatalogoPersistido());
    }

    @Cuando("^modifica nombre, catálogo padre, estado activo/inactivo y/o vigencia, sin modificar el código$")
    public void modifica_nombre_catalogo_padre_estado_y_o_vigencia() {
        Catalogo padre = nuevoCatalogoPersistido();
        ultimaSolicitud = new ActualizarCatalogoRequestDto()
                .nombre("Nombre actualizado (BDD)")
                .catalogoPadreCodigo(padre.getCodigo())
                .estado(EstadoVigenciaDto.INACTIVE);
        ultimoResultado = catalogoService.actualizar(contextoCatalogo.getCatalogoActual().getCodigo(), ultimaSolicitud);
    }

    @Entonces("^el sistema aplica los cambios permitidos$")
    public void el_sistema_aplica_los_cambios_permitidos() {
        assertThat(ultimoResultado).isNotNull();
        if (ultimaSolicitud.getNombre() != null) {
            assertThat(ultimoResultado.getNombre()).isEqualTo(ultimaSolicitud.getNombre());
        }
        if (ultimaSolicitud.getCatalogoPadreCodigo() != null) {
            assertThat(ultimoResultado.getCatalogoPadreCodigo()).isEqualTo(ultimaSolicitud.getCatalogoPadreCodigo());
        }
        if (ultimaSolicitud.getEstado() != null) {
            assertThat(ultimoResultado.getEstado()).isEqualTo(ultimaSolicitud.getEstado());
        }
        if (ultimaSolicitud.getCampos() != null && !ultimaSolicitud.getCampos().isEmpty()) {
            assertThat(ultimoResultado.getCampos()).extracting(CampoDefinicionDto::getNombre)
                    .containsExactlyElementsOf(ultimaSolicitud.getCampos().stream()
                            .map(CampoDefinicionDto::getNombre).toList());
        }
    }

    @Dado("^selecciona un catálogo existente que no contiene registros$")
    public void selecciona_un_catalogo_existente_que_no_contiene_registros() {
        contextoCatalogo.setCatalogoActual(nuevoCatalogoPersistido());
    }

    @Cuando("^modifica los campos \\(fields\\) del catálogo$")
    public void modifica_los_campos_del_catalogo() {
        ultimaSolicitud = new ActualizarCatalogoRequestDto();
        ultimaSolicitud.addCamposItem(
                new CampoDefinicionDto().nombre("campoActualizado").tipo(TipoCampoDto.STRING).esKey(true));
        ultimoResultado = catalogoService.actualizar(contextoCatalogo.getCatalogoActual().getCodigo(), ultimaSolicitud);
    }

    @Cuando("^intenta modificar el código del catálogo$")
    public void intenta_modificar_el_codigo_del_catalogo() {
        codigoOriginal = contextoCatalogo.getCatalogoActual().getCodigo();
        ultimaSolicitud = new ActualizarCatalogoRequestDto().nombre("Intento con otro código (BDD)");
        ultimoResultado = catalogoService.actualizar(codigoOriginal, ultimaSolicitud);
    }

    @Entonces("^el sistema rechaza la modificación del código$")
    public void el_sistema_rechaza_la_modificacion_del_codigo() {
        // ActualizarCatalogoRequestDto no tiene campo "codigo" (regla 16): el codigo del catalogo
        // no puede cambiar sin importar que otros campos se actualicen en la misma solicitud.
        assertThat(ultimoResultado.getCodigo()).isEqualTo(codigoOriginal);
    }

    @Dado("^selecciona un catálogo existente que ya contiene registros$")
    public void selecciona_un_catalogo_existente_que_ya_contiene_registros() {
        Catalogo catalogo = nuevoCatalogoPersistido();
        Registro registro = Registro.builder()
                .catalogo(catalogo)
                .clave("K1")
                .estado(EstadoVigencia.ACTIVE)
                .build();
        registro.getValores().put(catalogo.getCampos().get(0).getNombre(), "K1");
        registroRepository.save(registro);
        contextoCatalogo.setCatalogoActual(catalogo);
    }

    @Cuando("^intenta modificar los campos \\(fields\\) del catálogo$")
    public void intenta_modificar_los_campos_del_catalogo() {
        ActualizarCatalogoRequestDto solicitud = new ActualizarCatalogoRequestDto();
        solicitud.addCamposItem(new CampoDefinicionDto().nombre("otroCampo").tipo(TipoCampoDto.STRING).esKey(true));
        String codigo = contextoCatalogo.getCatalogoActual().getCodigo();
        try {
            catalogoService.actualizar(codigo, solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema rechaza la modificación de los campos$")
    public void el_sistema_rechaza_la_modificacion_de_los_campos() {
        assertThat(contextoValidacion.getUltimaExcepcion())
                .isInstanceOf(sv.gob.mh.siip.exception.ConflictoEstadoException.class);
    }

    @Dado("^que el actor no está autenticado o no está autorizado para administrar catálogos$")
    public void que_el_actor_no_esta_autenticado_o_no_esta_autorizado() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("^intenta actualizar un catálogo existente$")
    public void intenta_actualizar_un_catalogo_existente() {
        Catalogo catalogo = nuevoCatalogoPersistido();
        ActualizarCatalogoRequestDto solicitud = new ActualizarCatalogoRequestDto().nombre("Intento no autorizado (BDD)");
        try {
            catalogoService.actualizar(catalogo.getCodigo(), solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema rechaza la operación por falta de autorización$")
    public void el_sistema_rechaza_la_operacion_por_falta_de_autorizacion() {
        assertThat(contextoValidacion.getUltimaExcepcion())
                .isInstanceOfAny(NoAutenticadoException.class, AccesoDenegadoException.class);
    }

    private Catalogo nuevoCatalogoPersistido() {
        return catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
