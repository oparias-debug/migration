package sv.gob.mh.infrastructure.config.authz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * La regla de permiso tal y como la devuelve el authorization-service.
 *
 * <p>Es un contrato de integración: los nombres de estos campos son los del JSON del servicio.
 * Renombrar uno no rompe la compilación, rompe la deserialización en tiempo de ejecución, y el
 * síntoma es que todo se deniega sin explicación.</p>
 */
class PermissionRuleTest {

    @Test
    @DisplayName("Conserva los ocho campos del contrato del servicio")
    void conservaLosCamposDelContrato() {
        PermissionRule regla = new PermissionRule();

        regla.setId("1");
        regla.setGroupId("EJEMPLO_GRUPO_ADMIN");
        regla.setComponentId("demo-authz");
        regla.setResourcePath("expedientes-registro");
        regla.setResourceName("Registro de expedientes");
        regla.setOperationName("DELETE");
        regla.setEffect("1");
        regla.setConditions("{}");

        assertEquals("1", regla.getId());
        assertEquals("EJEMPLO_GRUPO_ADMIN", regla.getGroupId());
        assertEquals("demo-authz", regla.getComponentId());
        assertEquals("expedientes-registro", regla.getResourcePath());
        assertEquals("Registro de expedientes", regla.getResourceName());
        assertEquals("DELETE", regla.getOperationName());
        assertEquals("1", regla.getEffect());
        assertEquals("{}", regla.getConditions());
    }

    @Test
    @DisplayName("Una regla recién creada no trae nada: todo llega del servicio")
    void reglaNuevaEstaVacia() {
        PermissionRule regla = new PermissionRule();

        assertNull(regla.getId());
        assertNull(regla.getGroupId());
        assertNull(regla.getEffect());
    }
}
