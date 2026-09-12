package sv.gob.mh.siip.bdd.support;

import java.util.UUID;

import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.enums.TipoCampo;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;

/**
 * Builders reutilizados por los steps BDD de CU-ADM-01 (administracion de catalogos), para no
 * repetir la construccion de Catalogo/CampoDefinicion/Usuario en cada clase de steps. No son step
 * definitions: es un helper de test plano (mismo criterio que ProyectoFixtures).
 */
public final class CatalogoFixtures {

    private CatalogoFixtures() {
    }

    public static String nuevoSufijo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static Usuario nuevoAdministradorCatalogos(String nombreUsuario) {
        return Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Administrador de Catálogos (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS)
                .activo(true)
                .build();
    }

    public static CampoDefinicion campoKey(String nombre) {
        return CampoDefinicion.builder().nombre(nombre).tipo(TipoCampo.STRING).esKey(true).build();
    }

    public static CampoDefinicion campoTexto(String nombre) {
        return CampoDefinicion.builder().nombre(nombre).tipo(TipoCampo.STRING).esKey(false).build();
    }

    /** Catalogo ACTIVE con un campo KEY ("codigoInterno") y uno no-KEY ("descripcion"), listo para persistir. */
    public static Catalogo nuevoCatalogo(String codigo, String nombre) {
        Catalogo catalogo = Catalogo.builder()
                .codigo(codigo)
                .nombre(nombre)
                .estado(EstadoVigencia.ACTIVE)
                .build();
        catalogo.getCampos().add(asignar(campoKey("codigoInterno"), catalogo));
        catalogo.getCampos().add(asignar(campoTexto("descripcion"), catalogo));
        return catalogo;
    }

    private static CampoDefinicion asignar(CampoDefinicion campo, Catalogo catalogo) {
        campo.setCatalogo(catalogo);
        return campo;
    }
}
