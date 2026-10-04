package sv.gob.mh.infrastructure.config.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Table;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
 *
 * <p>Y una cuarta, propia de SIIP (ver el javadoc de {@link AuditEntityListener}): el antes y el
 * después salen del estado del evento, <b>sin tocar la entidad ni sus relaciones</b>; recorrerlas
 * desde otro hilo corrompía la sesión de la transacción de negocio.</p>
 */
class AuditEntityListenerTest {

    @Auditable
    @Table(name = "expediente", schema = "demo")
    static class EntidadAuditada {
    }

    @Auditable
    @Table(name = "expediente_sin_esquema")
    static class EntidadAuditadaSinEsquema {
    }

    @Auditable
    static class EntidadAuditadaSinTabla {
    }

    static class EntidadNormal {
    }

    /** Lo que habría en una relación lazy: si alguien lo toca, la prueba lo detecta. */
    static class ProxyQueNoSeDebeTocar {
        public String getNombre() {
            throw new AssertionError("la auditoría inicializó una relación lazy");
        }
    }

    private static final ObjectMapper JSON = new ObjectMapper();

    private AuditService auditService;
    private AuditEntityListener listener;
    private EntityPersister persister;
    private RuntimeException falloAlLeerId;
    private final ProxyQueNoSeDebeTocar unidadEjecutora = new ProxyQueNoSeDebeTocar();

    @BeforeEach
    void prepararListener() {
        auditService = mock(AuditService.class);
        UserContextService userContext = mock(UserContextService.class);
        when(userContext.getCurrentUserId()).thenReturn("usuario.admin");

        listener = new AuditEntityListener(auditService, userContext,
            new EntitySerializer(new ObjectMapper())) {
            @Override
            protected Object idDeRelacion(Object relacionada, SharedSessionContractImplementor session) {
                if (falloAlLeerId != null) {
                    throw falloAlLeerId;
                }
                return relacionada == unidadEjecutora ? 7L : null;
            }
        };
        ReflectionTestUtils.setField(listener, "serviceName", "demo-authz");

        // Propiedades: nombre (columna), unidadEjecutora (relación), componentes (colección), archivo.
        Type columna = mock(Type.class);
        Type relacion = mock(Type.class);
        when(relacion.isEntityType()).thenReturn(true);
        Type coleccion = mock(Type.class);
        when(coleccion.isCollectionType()).thenReturn(true);

        persister = mock(EntityPersister.class);
        when(persister.getIdentifierPropertyName()).thenReturn("id");
        when(persister.getPropertyNames())
            .thenReturn(new String[] {"nombre", "unidadEjecutora", "componentes", "archivo"});
        when(persister.getPropertyTypes()).thenReturn(new Type[] {columna, relacion, coleccion, columna});

    }

    private Object[] estado(String nombre) {
        return new Object[] {nombre, unidadEjecutora, new Object(), new byte[] {1, 2, 3}};
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
        when(evento.getPersister()).thenReturn(persister);
        when(evento.getId()).thenReturn(1L);
        when(evento.getState()).thenReturn(estado("Escuela"));
        return evento;
    }

    @Test
    @DisplayName("El alta audita con el después lleno y el antes vacío, y no cancela el guardado")
    void elAltaAuditaElDespues() throws Exception {
        boolean cancela = listener.onPreInsert(altaDe(new EntidadAuditada()));

        assertFalse(cancela, "devolver true cancelaría el guardado en Hibernate");
        AuditEvent evento = eventoAuditado();
        assertEquals("INSERT", evento.getAction());
        assertEquals("usuario.admin", evento.getUserId());
        assertEquals("demo-authz", evento.getApplication());
        assertEquals("demo.expediente", evento.getResource());
        assertNull(evento.getOldValues());
        assertEquals("Escuela", JSON.readTree(evento.getNewValues()).get("nombre").asText());
        assertNotNull(evento.getTimestamp());
    }

    @Test
    @DisplayName("Las relaciones van como id sin inicializarlas, las colecciones se omiten y los binarios como tamaño")
    void elEstadoNoRecorreRelaciones() throws Exception {
        listener.onPreInsert(altaDe(new EntidadAuditada()));

        JsonNode despues = JSON.readTree(eventoAuditado().getNewValues());
        assertEquals(1L, despues.get("id").asLong());
        assertEquals(7L, despues.get("unidadEjecutora").asLong());
        assertFalse(despues.has("componentes"));
        assertEquals("<binario 3 bytes>", despues.get("archivo").asText());
    }

    @Test
    @DisplayName("La baja audita con el antes lleno y el después vacío")
    void laBajaAuditaElAntes() throws Exception {
        PreDeleteEvent evento = mock(PreDeleteEvent.class);
        when(evento.getEntity()).thenReturn(new EntidadAuditada());
        when(evento.getPersister()).thenReturn(persister);
        when(evento.getId()).thenReturn(1L);
        when(evento.getDeletedState()).thenReturn(estado("Escuela"));

        boolean cancela = listener.onPreDelete(evento);

        assertFalse(cancela);
        AuditEvent auditado = eventoAuditado();
        assertEquals("DELETE", auditado.getAction());
        assertEquals("Escuela", JSON.readTree(auditado.getOldValues()).get("nombre").asText());
        assertNull(auditado.getNewValues());
    }

    @Test
    @DisplayName("La modificación audita el antes y el después reales, que difieren")
    void laModificacionAuditaElAntesYElDespues() throws Exception {
        PreUpdateEvent cambio = mock(PreUpdateEvent.class);
        when(cambio.getEntity()).thenReturn(new EntidadAuditada());
        when(cambio.getPersister()).thenReturn(persister);
        when(cambio.getId()).thenReturn(1L);
        when(cambio.getOldState()).thenReturn(estado("Escuela"));
        when(cambio.getState()).thenReturn(estado("Escuela rural"));

        boolean cancela = listener.onPreUpdate(cambio);

        assertFalse(cancela);
        AuditEvent auditado = eventoAuditado();
        assertEquals("UPDATE", auditado.getAction());
        assertEquals("Escuela", JSON.readTree(auditado.getOldValues()).get("nombre").asText());
        assertEquals("Escuela rural", JSON.readTree(auditado.getNewValues()).get("nombre").asText());
    }

    @Test
    @DisplayName("Una modificación sin estado previo (entidad no cargada en la sesión) audita el antes vacío")
    void modificacionSinEstadoPrevio() {
        PreUpdateEvent cambio = mock(PreUpdateEvent.class);
        when(cambio.getEntity()).thenReturn(new EntidadAuditada());
        when(cambio.getPersister()).thenReturn(persister);
        when(cambio.getState()).thenReturn(estado("Escuela"));

        listener.onPreUpdate(cambio);

        AuditEvent auditado = eventoAuditado();
        assertNull(auditado.getOldValues());
        assertNotNull(auditado.getNewValues());
    }

    @Test
    @DisplayName("Una entidad sin @Auditable no se audita, en ninguno de los tres eventos")
    void entidadNormalNoSeAudita() {
        PreDeleteEvent baja = mock(PreDeleteEvent.class);
        when(baja.getEntity()).thenReturn(new EntidadNormal());
        PreUpdateEvent cambio = mock(PreUpdateEvent.class);
        when(cambio.getEntity()).thenReturn(new EntidadNormal());

        listener.onPreInsert(altaDe(new EntidadNormal()));
        listener.onPreDelete(baja);
        listener.onPreUpdate(cambio);

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

    @Test
    @DisplayName("Si leer el estado falla (en el hilo de Hibernate), no cancela el guardado y el evento lleva el motivo")
    void falloAlLeerElEstadoNoTumbaLaOperacion() {
        falloAlLeerId = new IllegalStateException("sesión rota");

        boolean cancela = listener.onPreInsert(altaDe(new EntidadAuditada()));

        assertFalse(cancela);
        assertTrue(eventoAuditado().getNewValues().startsWith("Serialization error: sesión rota"));
    }
}
