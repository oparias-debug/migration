package sv.gob.mh.infrastructure.config.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Table;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * El enganche de Hibernate que audita los cambios de entidad.
 *
 * <p>Tres cosas que se fijan aquí. Una: **sólo se audita lo marcado con {@code @Auditable}**; sin
 * ese filtro se auditaría cada tabla técnica y el servicio de auditoría acabaría inservible.
 * Dos: los tres eventos tienen que llenar el antes y el después de forma distinta —el alta no
 * tiene antes, la baja no tiene después—, que es justo lo que se consulta luego. Tres: **ninguno
 * de los métodos puede bloquear la operación**, por eso todos devuelven {@code false}: devolver
 * {@code true} en un {@code onPreInsert} de Hibernate <b>cancela el guardado</b>.</p>
 */
class AuditEntityListenerTest {

    @Auditable
    @Table(name = "expediente", schema = "demo")
    static class EntidadAuditada {
        private final String campo = "valor";

        public String getCampo() {
            return campo;
        }
    }

    @Auditable
    @Table(name = "expediente_sin_esquema")
    static class EntidadAuditadaSinEsquema {
        public String getCampo() {
            return "otro campo";
        }
    }

    @Auditable
    static class EntidadAuditadaSinTabla {
        public String getCampo() {
            return "valor";
        }
    }

    static class EntidadNormal {
        public String getCampo() {
            return "valor";
        }
    }

    private AuditService auditService;
    private AuditEntityListener listener;

    @BeforeEach
    void prepararListener() {
        auditService = mock(AuditService.class);
        UserContextService userContext = mock(UserContextService.class);
        when(userContext.getCurrentUserId()).thenReturn("usuario.admin");

        listener = new AuditEntityListener(auditService, userContext,
            new EntitySerializer(new ObjectMapper()));
        ReflectionTestUtils.setField(listener, "serviceName", "demo-authz");
    }

    /** Espera al envío asíncrono y devuelve el evento que se auditó. */
    private AuditEvent eventoAuditado() {
        ArgumentCaptor<AuditEvent> capturado = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditService, timeout(2000)).sendAuditEvent(capturado.capture());
        return capturado.getValue();
    }

    private PreInsertEvent altaDe(Object entidad) {
        PreInsertEvent evento = mock(PreInsertEvent.class);
        when(evento.getEntity()).thenReturn(entidad);
        return evento;
    }

    @Test
    @DisplayName("El alta audita con el después lleno y el antes vacío, y no cancela el guardado")
    void elAltaAuditaElDespues() {
        boolean cancela = listener.onPreInsert(altaDe(new EntidadAuditada()));

        assertFalse(cancela, "devolver true cancelaría el guardado en Hibernate");
        AuditEvent evento = eventoAuditado();
        assertEquals("INSERT", evento.getAction());
        assertEquals("usuario.admin", evento.getUserId());
        assertEquals("demo-authz", evento.getApplication());
        assertEquals("demo.expediente", evento.getResource());
        assertNull(evento.getOldValues());
        assertNotNull(evento.getNewValues());
        assertNotNull(evento.getTimestamp());
    }

    @Test
    @DisplayName("La baja audita con el antes lleno y el después vacío")
    void laBajaAuditaElAntes() {
        PreDeleteEvent evento = mock(PreDeleteEvent.class);
        when(evento.getEntity()).thenReturn(new EntidadAuditada());

        boolean cancela = listener.onPreDelete(evento);

        assertFalse(cancela);
        AuditEvent auditado = eventoAuditado();
        assertEquals("DELETE", auditado.getAction());
        assertNotNull(auditado.getOldValues());
        assertNull(auditado.getNewValues());
    }

    @Test
    @DisplayName("La modificación audita el antes que se guardó al cargar la entidad")
    void laModificacionAuditaElAntesYElDespues() {
        EntidadAuditada original = new EntidadAuditada();
        PostLoadEvent carga = mock(PostLoadEvent.class);
        when(carga.getEntity()).thenReturn(original);
        listener.onPostLoad(carga);

        PreUpdateEvent cambio = mock(PreUpdateEvent.class);
        when(cambio.getEntity()).thenReturn(new EntidadAuditada());

        boolean cancela = listener.onPreUpdate(cambio);

        assertFalse(cancela);
        AuditEvent auditado = eventoAuditado();
        assertEquals("UPDATE", auditado.getAction());
        assertNotNull(auditado.getOldValues());
        assertNotNull(auditado.getNewValues());
    }

    @Test
    @DisplayName("Una entidad sin @Auditable no se audita, en ninguno de los tres eventos")
    void entidadNormalNoSeAudita() {
        PreDeleteEvent baja = mock(PreDeleteEvent.class);
        when(baja.getEntity()).thenReturn(new EntidadNormal());
        PreUpdateEvent cambio = mock(PreUpdateEvent.class);
        when(cambio.getEntity()).thenReturn(new EntidadNormal());
        PostLoadEvent carga = mock(PostLoadEvent.class);
        when(carga.getEntity()).thenReturn(new EntidadNormal());

        listener.onPreInsert(altaDe(new EntidadNormal()));
        listener.onPreDelete(baja);
        listener.onPreUpdate(cambio);
        listener.onPostLoad(carga);

        verify(auditService, never()).sendAuditEvent(any());
    }

    @Test
    @DisplayName("Sin @Table, el recurso auditado es el nombre de la clase")
    void sinTablaElRecursoEsLaClase() {
        listener.onPreInsert(altaDe(new EntidadAuditadaSinTabla()));

        assertEquals("EntidadAuditadaSinTabla", eventoAuditado().getResource());
    }

    @Test
    @DisplayName("Con @Table sin esquema, el recurso auditado queda bajo 'default'")
    void tablaSinEsquemaQuedaBajoDefault() {
        listener.onPreInsert(altaDe(new EntidadAuditadaSinEsquema()));

        assertEquals("default.expediente_sin_esquema", eventoAuditado().getResource());
    }

    @Test
    @DisplayName("Si el servicio de auditoría falla, la operación de negocio no se entera")
    void falloDeAuditoriaNoTumbaLaOperacion() {
        doThrow(new IllegalStateException("auditoría caída"))
            .when(auditService).sendAuditEvent(any());

        boolean cancela = listener.onPreInsert(altaDe(new EntidadAuditada()));

        // El fallo ocurre en el hilo del pool y se queda ahí: ni cancela el guardado ni sube.
        assertFalse(cancela);
        verify(auditService, timeout(2000)).sendAuditEvent(any());
    }
}
