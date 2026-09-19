package sv.gob.mh.siip.bdd.support;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.enums.TipoCampo;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/**
 * Builders reutilizados por los steps BDD de CU-ADM-01 (administracion de catalogos), para no
 * repetir la construccion de Catalogo/CampoDefinicion/Usuario ni la autenticacion simulada en cada
 * clase de steps. No son step definitions: es un helper de test plano (mismo criterio que
 * CalendarioFixtures/ProyectoFixtures).
 */
public final class CatalogoFixtures {

    private static final String HEADER_USUARIO = "X-Usuario";

    private CatalogoFixtures() {
    }

    public static String nuevoSufijo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /** Rol exigido por CU-ADM-01 (x-roles del contrato OpenAPI), distinto del ADMINISTRADOR generico. */
    public static Usuario nuevoAdministradorCatalogos(String nombreUsuario) {
        return Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Administrador de Catálogos (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS)
                .activo(true)
                .build();
    }

    /** Autentica al actor de la peticion HTTP simulada actual mediante el header X-Usuario (ver ActorContexto). */
    public static void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    /** Crea, persiste y autentica un nuevo Usuario ADMINISTRADOR_DE_CATALOGOS; retorna la entidad persistida. */
    public static Usuario autenticarNuevoAdministrador(UsuarioRepository usuarioRepository) {
        String nombreUsuario = "admin.catalogos.bdd." + nuevoSufijo();
        Usuario administrador = usuarioRepository.save(nuevoAdministradorCatalogos(nombreUsuario));
        autenticarComo(nombreUsuario);
        return administrador;
    }

    public static CampoDefinicion campoKey(String nombre, int posicion) {
        return CampoDefinicion.builder().nombre(nombre).tipo(TipoCampo.STRING).esKey(true).posicion(posicion).build();
    }

    public static CampoDefinicion campoTexto(String nombre, int posicion) {
        return CampoDefinicion.builder().nombre(nombre).tipo(TipoCampo.STRING).esKey(false).posicion(posicion).build();
    }

    /** Catalogo ACTIVE con un campo KEY ("codigoInterno") y uno no-KEY ("descripcion"), listo para persistir. */
    public static Catalogo nuevoCatalogo(String codigo, String nombre) {
        Catalogo catalogo = Catalogo.builder()
                .codigo(codigo)
                .nombre(nombre)
                .estado(EstadoVigencia.ACTIVE)
                .build();
        catalogo.getCampos().add(asignar(campoKey("codigoInterno", 0), catalogo));
        catalogo.getCampos().add(asignar(campoTexto("descripcion", 1), catalogo));
        return catalogo;
    }

    public static String nombreCampoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(CampoDefinicion::isEsKey).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }

    public static String nombreCampoNoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(campo -> !campo.isEsKey()).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }

    private static CampoDefinicion asignar(CampoDefinicion campo, Catalogo catalogo) {
        campo.setCatalogo(catalogo);
        return campo;
    }
}
