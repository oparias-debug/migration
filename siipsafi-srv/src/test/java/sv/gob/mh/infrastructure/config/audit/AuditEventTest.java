package sv.gob.mh.infrastructure.config.audit;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * El evento de auditoría que se manda al servicio del marco.
 *
 * <p>Los dos campos que justifican que exista la auditoría son {@code oldValues} y
 * {@code newValues}: sin ellos queda constancia de que alguien tocó algo, pero no de qué
 * cambió, que es justo lo que se pregunta después.</p>
 */
class AuditEventTest {

    @Test
    @DisplayName("El constructor completo conserva el antes y el después del cambio")
    void constructorConservaElAntesYElDespues() {
        LocalDateTime momento = LocalDateTime.of(2026, 2, 1, 10, 0);

        AuditEvent evento = new AuditEvent("usuario.admin", "UPDATE", "Expediente",
            "demo-authz", "{\"estado\":\"ABIERTO\"}", "{\"estado\":\"CERRADO\"}", momento);

        assertEquals("usuario.admin", evento.getUserId());
        assertEquals("UPDATE", evento.getAction());
        assertEquals("Expediente", evento.getResource());
        assertEquals("demo-authz", evento.getApplication());
        assertEquals("{\"estado\":\"ABIERTO\"}", evento.getOldValues());
        assertEquals("{\"estado\":\"CERRADO\"}", evento.getNewValues());
        assertEquals(momento, evento.getTimestamp());
    }

    @Test
    @DisplayName("El constructor vacío no inventa datos y los setters los completan")
    void constructorVacioYSetters() {
        AuditEvent evento = new AuditEvent();
        assertNull(evento.getUserId());

        LocalDateTime momento = LocalDateTime.of(2026, 2, 2, 8, 30);
        evento.setUserId("usuario.consulta");
        evento.setAction("DELETE");
        evento.setResource("Expediente");
        evento.setApplication("demo-authz");
        evento.setOldValues("{\"id\":1}");
        evento.setNewValues(null);
        evento.setTimestamp(momento);

        assertEquals("usuario.consulta", evento.getUserId());
        assertEquals("DELETE", evento.getAction());
        assertEquals("Expediente", evento.getResource());
        assertEquals("demo-authz", evento.getApplication());
        assertEquals("{\"id\":1}", evento.getOldValues());
        assertNull(evento.getNewValues());
        assertEquals(momento, evento.getTimestamp());
    }
}
