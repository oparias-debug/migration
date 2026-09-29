package sv.gob.mh.infrastructure.config.authz;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * El cliente del authorization-service: quien de verdad decide si se puede o no.
 *
 * <p>Se prueba sustituyendo el {@code HttpClient}, no llamando al servicio: lo que hay que
 * asegurar es <b>cómo se construye la pregunta y cómo se interpreta la respuesta</b>.</p>
 *
 * <p>Dos comportamientos que no son obvios y que aquí quedan fijados. Uno: en {@code groupIds}
 * viajan <b>sólo los grupos del token</b>; meter también el nombre de usuario hacía que el
 * servicio respondiera 403 diciendo que ese grupo no pertenece al usuario autenticado. Dos:
 * ante cualquier duda <b>se deniega</b> —sin token, sin sesión, con error de red o con una
 * respuesta que no es 200—, porque un fallo de la comprobación no puede convertirse en un
 * permiso concedido.</p>
 */
class AuthorizationServiceTest {

    private AuthorizationService service;
    private HttpClient httpClient;

    @BeforeEach
    void prepararServicio() {
        httpClient = mock(HttpClient.class);
        service = new AuthorizationService();
        ReflectionTestUtils.setField(service, "httpClient", httpClient);
        ReflectionTestUtils.setField(service, "componentId", "demo-authz");
        ReflectionTestUtils.setField(service, "urlAuthz", "https://authz/api/v1/authz");
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    /** Deja en el contexto un token real con los grupos indicados. */
    private void conTokenYGrupos(String... grupos) {
        Jwt jwt = new Jwt("el-token-en-crudo", Instant.now(), Instant.now().plusSeconds(300),
            Map.of("alg", "none"), Map.of("sub", "usuario.admin"));
        List<SimpleGrantedAuthority> autoridades = List.of(grupos).stream()
            .map(SimpleGrantedAuthority::new).toList();
        SecurityContextHolder.getContext().setAuthentication(
            new JwtAuthenticationToken(jwt, autoridades, "usuario.admin"));
    }

    /** Programa la respuesta del servicio de autorización. */
    private void respondeCon(int codigo, String cuerpo) throws Exception {
        @SuppressWarnings("unchecked")
        HttpResponse<String> respuesta = mock(HttpResponse.class);
        when(respuesta.statusCode()).thenReturn(codigo);
        when(respuesta.body()).thenReturn(cuerpo);
        doReturn(respuesta).when(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("Sin sesión se deniega y no se llega a preguntar")
    void sinSesionSeDeniega() throws Exception {
        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
        verify(httpClient, never()).send(any(), any());
    }

    @Test
    @DisplayName("Autenticado pero sin token en crudo se deniega: no se puede preguntar")
    void sinTokenSeDeniega() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("usuario.admin", null,
                List.of(new SimpleGrantedAuthority("EJEMPLO_GRUPO_ADMIN"))));

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
        verify(httpClient, never()).send(any(), any());
    }

    @Test
    @DisplayName("Pregunta con el token firmado y sólo con los grupos, sin el usuario")
    void preguntaConElTokenYSoloLosGrupos() throws Exception {
        conTokenYGrupos("/EJEMPLO_GRUPO_ADMIN", "EJEMPLO_GRUPO_CONSULTA");
        respondeCon(200, "{\"hasPermission\":true}");

        assertTrue(service.hasGranularPermission("DELETE", "expedientes-registro"));

        ArgumentCaptor<HttpRequest> peticion = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(peticion.capture(), any());
        HttpRequest enviada = peticion.getValue();
        String url = enviada.uri().toString();

        assertEquals("Bearer el-token-en-crudo",
            enviada.headers().firstValue("Authorization").orElse(""));
        assertTrue(url.contains("groupIds=EJEMPLO_GRUPO_ADMIN%2CEJEMPLO_GRUPO_CONSULTA"));
        assertFalse(url.contains("usuario.admin"));
        assertTrue(url.contains("componentId=demo-authz"));
        assertTrue(url.contains("resourcePath=expedientes-registro"));
        assertTrue(url.contains("operationName=DELETE"));
    }

    @Test
    @DisplayName("Un 200 con hasPermission false deniega")
    void doscientosConFalseDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_CONSULTA");
        respondeCon(200, "{\"hasPermission\":false}");

        assertFalse(service.hasGranularPermission("DELETE", "expedientes-registro"));
    }

    @Test
    @DisplayName("Una respuesta que no es 200 deniega, no concede")
    void respuestaNo200Deniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(403, "{}");

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Si el servicio no responde, se deniega: falla cerrado")
    void siNoRespondeFallaCerrado() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        doThrow(new java.io.IOException("authz caído")).when(httpClient).send(any(HttpRequest.class), any());

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una interrupción deja el hilo marcado y deniega")
    void interrupcionDejaElHiloMarcado() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        doThrow(new InterruptedException("interrumpido")).when(httpClient).send(any(HttpRequest.class), any());

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
        assertTrue(Thread.interrupted(), "el hilo debe quedar marcado como interrumpido");
    }

    @Test
    @DisplayName("Un cuerpo que no es JSON válido deniega en vez de reventar")
    void cuerpoIlegibleDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "esto no es json");

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    // ------------------------------------------------------- condiciones adicionales

    @Test
    @DisplayName("Con condiciones vacías, manda el veredicto del servicio")
    void condicionesVaciasNoCambianNada() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,\"conditions\":\"\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una lista de usuarios permitidos que incluye al actual concede")
    void usuarioEnLaListaConcede() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"allowed_users\\\":\\\"otra.persona, usuario.admin\\\"}\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Si el usuario no está en la lista, se deniega aunque el permiso exista")
    void usuarioFueraDeLaListaSeDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"allowed_users\\\":\\\"otra.persona\\\"}\"}");

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Los roles permitidos se comparan contra los grupos del token")
    void rolesPermitidos() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"allowed_roles\\\":\\\"EJEMPLO_GRUPO_ADMIN\\\"}\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Un rol que no está entre los del token deniega")
    void rolNoPermitido() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_CONSULTA");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"allowed_roles\\\":\\\"OTRO_GRUPO\\\"}\"}");

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una franja horaria de todo el día concede")
    void franjaHorariaDeTodoElDia() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"time_restriction\\\":\\\"00:00-23:59\\\"}\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una franja con formato inválido deniega, no concede por defecto")
    void franjaConFormatoInvalidoDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"time_restriction\\\":\\\"no-es-una-hora\\\"}\"}");

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una franja que cruza medianoche se evalúa sin invertirse")
    void franjaQueCruzaMedianoche() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        // Cruza medianoche (fin anterior al inicio): 00:01-00:00 cubre el día entero por el
        // otro camino del cálculo, sea cual sea la hora a la que corra la prueba.
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"time_restriction\\\":\\\"00:01-00:00\\\"}\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una condición desconocida no bloquea: manda el veredicto del servicio")
    void condicionDesconocidaNoBloquea() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"otra_cosa\\\":\\\"valor\\\"}\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Unas condiciones ilegibles deniegan")
    void condicionesIlegiblesDeniegan() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,\"conditions\":\"{no es json\"}");

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Con hasPermission false, las condiciones ni se miran")
    void conPermisoDenegadoLasCondicionesNoSeMiran() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":false,"
            + "\"conditions\":\"{\\\"allowed_users\\\":\\\"usuario.admin\\\"}\"}");

        // Las condiciones sólo afinan un permiso concedido; nunca lo conceden.
        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Unas condiciones con el texto null se tratan como si no hubiera")
    void condicionesConTextoNullSeIgnoran() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,\"conditions\":\"null\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una franja horaria vacía no restringe nada")
    void franjaVaciaNoRestringe() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"time_restriction\\\":\\\"  \\\"}\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una franja con horas imposibles deniega en vez de reventar")
    void franjaConHorasImposiblesDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"time_restriction\\\":\\\"99:99-11:11\\\"}\"}");

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("El usuario permitido puede venir el primero de la lista")
    void usuarioPrimeroEnLaLista() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"allowed_users\\\":\\\"usuario.admin,otra.persona\\\"}\"}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Un token sin grupos pregunta igual, con groupIds vacío")
    void tokenSinGruposPreguntaIgual() throws Exception {
        conTokenYGrupos();
        respondeCon(200, "{\"hasPermission\":false}");

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("Una autoridad vacía no ensucia la lista de grupos")
    void autoridadVaciaNoEnsuciaLaLista() throws Exception {
        conTokenYGrupos("/", "EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true}");

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));

        ArgumentCaptor<HttpRequest> peticion = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(peticion.capture(), any());
        assertTrue(peticion.getValue().uri().toString().contains("groupIds=EJEMPLO_GRUPO_ADMIN"));
    }

    @Test
    @DisplayName("Un token en blanco cuenta como no tener token: se deniega")
    void tokenEnBlancoSeDeniega() throws Exception {
        // Jwt no admite un valor vacio al construirlo, asi que un token en blanco solo puede
        // venir de un emisor que lo devuelva asi; se simula para fijar que tambien se deniega.
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("   ");
        SecurityContextHolder.getContext().setAuthentication(
            new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("G")), "usuario.admin"));

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
        verify(httpClient, never()).send(any(), any());
    }

    @Test
    @DisplayName("Una sesión no autenticada se deniega sin preguntar")
    void sesionNoAutenticadaSeDeniega() throws Exception {
        UsernamePasswordAuthenticationToken sinAutenticar =
            new UsernamePasswordAuthenticationToken("usuario.admin", null);
        SecurityContextHolder.getContext().setAuthentication(sinAutenticar);

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
        verify(httpClient, never()).send(any(), any());
    }

    @Test
    @DisplayName("Una franja horaria nula llega como el texto 'null' y se deniega: falla cerrado")
    void franjaNulaSeDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"time_restriction\\\":null}\"}");

        // El JSON nulo se convierte en la cadena "null", que no tiene forma "HH:mm-HH:mm".
        // Una condición que no se entiende no puede interpretarse como permiso concedido.
        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    /**
     * Responde con una franja horaria calculada a partir de la hora actual, para que la prueba
     * valga igual a cualquier hora del día. Se dan las dos horas ya formateadas.
     */
    private void respondeConFranja(LocalTime inicio, LocalTime fin) throws Exception {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
        respondeCon(200, "{\"hasPermission\":true,"
            + "\"conditions\":\"{\\\"time_restriction\\\":\\\""
            + inicio.format(formato) + "-" + fin.format(formato) + "\\\"}\"}");
    }

    @Test
    @DisplayName("Sin valor de token en el JWT, se deniega en vez de preguntar sin cabecera")
    void jwtSinValorDeTokenSeDeniega() throws Exception {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(
            new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("G")), "usuario.admin"));

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
        verify(httpClient, never()).send(any(), any());
    }

    @Test
    @DisplayName("Una operación o un recurso nulos no rompen la URL: viajan vacíos")
    void operacionYRecursoNulosNoRompenLaUrl() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        respondeCon(200, "{\"hasPermission\":false}");

        assertFalse(service.hasGranularPermission(null, null));

        ArgumentCaptor<HttpRequest> peticion = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(peticion.capture(), any());
        assertTrue(peticion.getValue().uri().toString().contains("resourcePath=&operationName="));
    }

    @Test
    @DisplayName("Una franja que aún no ha empezado deniega")
    void franjaQueAunNoEmpiezaDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        LocalTime ahora = LocalTime.now();
        respondeConFranja(ahora.plusHours(2), ahora.plusHours(3));

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una franja que ya terminó deniega")
    void franjaQueYaTerminoDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        LocalTime ahora = LocalTime.now();
        respondeConFranja(ahora.minusHours(3), ahora.minusHours(2));

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una franja que cruza medianoche y cubre la hora actual concede")
    void franjaQueCruzaMedianocheYCubreAhoraConcede() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        LocalTime ahora = LocalTime.now();
        // Fin anterior al inicio: la franja va del inicio hasta el fin del día siguiente, y
        // la hora actual cae dentro por el tramo que llega hasta el fin.
        respondeConFranja(ahora.plusHours(1), ahora.plusMinutes(5));

        assertTrue(service.hasGranularPermission("VIEW", "expedientes"));
    }

    @Test
    @DisplayName("Una franja que cruza medianoche y deja fuera la hora actual deniega")
    void franjaQueCruzaMedianocheYDejaFueraAhoraDeniega() throws Exception {
        conTokenYGrupos("EJEMPLO_GRUPO_ADMIN");
        LocalTime ahora = LocalTime.now();
        respondeConFranja(ahora.plusHours(1), ahora.minusHours(1));

        assertFalse(service.hasGranularPermission("VIEW", "expedientes"));
    }
}
