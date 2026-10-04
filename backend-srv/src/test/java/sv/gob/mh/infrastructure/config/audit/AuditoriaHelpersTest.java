package sv.gob.mh.infrastructure.config.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Las tres piezas que rodean a la auditoría: quién hizo el cambio, cómo se serializa y qué pasa
 * si el servicio de auditoría no responde.
 *
 * <p>La regla que se fija aquí y que no es obvia: <b>un fallo de auditoría no puede tumbar la
 * operación de negocio</b>. Si lo hiciera, el servicio de auditoría caído dejaría fuera de
 * servicio a lo que audita.</p>
 */
class AuditoriaHelpersTest {

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private void conAutenticacion(Authentication auth) {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ---------------------------------------------------------------- UserContextService

    @Test
    @DisplayName("Sin sesión, el usuario auditado es 'system', no nulo")
    void sinSesionElUsuarioEsSystem() {
        assertEquals("system", new UserContextService().getCurrentUserId());
    }

    @Test
    @DisplayName("Con un JWT usa preferred_username, que es el nombre que reconoce la gente")
    void conJwtUsaPreferredUsername() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("usuario.admin");
        conAutenticacion(new UsernamePasswordAuthenticationToken(jwt, null, java.util.List.of()));

        assertEquals("usuario.admin", new UserContextService().getCurrentUserId());
    }

    @Test
    @DisplayName("Si el JWT no trae preferred_username, cae al subject")
    void sinPreferredUsernameCaeAlSubject() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(null);
        when(jwt.getSubject()).thenReturn("sub-123");
        conAutenticacion(new UsernamePasswordAuthenticationToken(jwt, null, java.util.List.of()));

        assertEquals("sub-123", new UserContextService().getCurrentUserId());
    }

    @Test
    @DisplayName("Si preferred_username viene vacío, tampoco vale: se usa el subject")
    void preferredUsernameVacioCaeAlSubject() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("");
        when(jwt.getSubject()).thenReturn("sub-456");
        conAutenticacion(new UsernamePasswordAuthenticationToken(jwt, null, java.util.List.of()));

        assertEquals("sub-456", new UserContextService().getCurrentUserId());
    }

    @Test
    @DisplayName("Con una autenticación que no es JWT, usa su nombre")
    void autenticacionQueNoEsJwtUsaElNombre() {
        conAutenticacion(new UsernamePasswordAuthenticationToken("ana.perez", null, java.util.List.of()));

        assertEquals("ana.perez", new UserContextService().getCurrentUserId());
    }

    // ------------------------------------------------------------------- EntitySerializer

    @Test
    @DisplayName("Serializa la entidad a JSON")
    void serializaLaEntidad() {
        EntitySerializer serializer = new EntitySerializer(new ObjectMapper());

        String json = serializer.serialize(new AuditEvent("u", "UPDATE", "R", "app", null, null, null));

        assertTrue(json.contains("\"action\":\"UPDATE\""));
    }

    @Test
    @DisplayName("Una entidad nula se serializa como nulo, no como el texto \"null\"")
    void entidadNulaEsNulo() {
        assertNull(new EntitySerializer(new ObjectMapper()).serialize(null));
    }

    @Test
    @DisplayName("Si la serialización falla, devuelve el motivo en vez de tumbar la operación")
    void siFallaDevuelveElMotivo() throws Exception {
        ObjectMapper roto = mock(ObjectMapper.class);
        when(roto.writeValueAsString(any())).thenThrow(new IllegalStateException("no serializable"));

        String resultado = new EntitySerializer(roto).serialize(new Object());

        assertTrue(resultado.startsWith("Serialization error:"));
    }

    // ------------------------------------------------------------------------ AuditService

    @Test
    @DisplayName("Manda el evento al cliente REST")
    void mandaElEventoAlCliente() {
        AuditRestClient cliente = mock(AuditRestClient.class);
        AuditEvent evento = new AuditEvent();

        new AuditService(cliente).sendAuditEvent(evento);

        verify(cliente).sendAuditEvent(evento);
    }

    @Test
    @DisplayName("Si la auditoría falla, NO tumba la operación de negocio")
    void siLaAuditoriaFallaNoTumbaElNegocio() {
        AuditRestClient cliente = mock(AuditRestClient.class);
        doThrow(new IllegalStateException("auditoria caída")).when(cliente).sendAuditEvent(any());

        // No se espera excepción: auditar es un efecto secundario, no la operación.
        new AuditService(cliente).sendAuditEvent(new AuditEvent());

        verify(cliente).sendAuditEvent(any());
    }

    @Test
    @DisplayName("Una sesión no autenticada se audita como 'system'")
    void sesionNoAutenticadaEsSystem() {
        UsernamePasswordAuthenticationToken sinAutenticar =
            new UsernamePasswordAuthenticationToken("ana.perez", null);
        conAutenticacion(sinAutenticar);

        assertEquals("system", new UserContextService().getCurrentUserId());
    }

    @Test
    @DisplayName("Sin principal tampoco se inventa un usuario")
    void sinPrincipalEsSystem() {
        conAutenticacion(new UsernamePasswordAuthenticationToken(null, null, java.util.List.of()));

        assertEquals("system", new UserContextService().getCurrentUserId());
    }
}
