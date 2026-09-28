package sv.gob.mh.siip.api_gateway.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import sv.gob.mh.siip.api_gateway.dto.LoginRequest;
import sv.gob.mh.siip.api_gateway.dto.TokenResponse;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.Builder webClientBuilder;

    private AuthController authController;
    private WebTestClient webTestClient;

    private static final String KEYCLOAK_TOKEN_URI =
            "http://localhost:8080/auth/realms/test/protocol/openid-connect/token";
    private static final String FORMATO_BODY_LOGIN =
            "client_id=%s&client_secret=%s&username=%s&password=%s&grant_type=password";
    private final String clientId = "test-client";
    private final String clientSecret = "test-secret";

    @BeforeEach
    void setUp() {
        // Configurar el mock del WebClient.Builder
        when(webClientBuilder.build()).thenReturn(webClient);

        // Crear instancia del controller
        authController = new AuthController(webClientBuilder, KEYCLOAK_TOKEN_URI, clientId, clientSecret);

        // Configurar WebTestClient para pruebas de integración
        webTestClient = WebTestClient.bindToController(authController)
                .configureClient()
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    private void setupWebClientMocks() {
        // Configurar la cadena de mocks para WebClient
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void login_ConLoginRequestNulo_DeberiaLanzarBadRequest() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authController.login(null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El campo Username y password no deben venir vacios.", exception.getReason());

        // Verificar que no se llamó al WebClient
        verify(webClient, never()).post();
    }

    @Test
    void login_ConUsernameNulo_DeberiaLanzarBadRequest() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword("testpass");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.login(loginRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El campo Username y password no deben venir vacios.", exception.getReason());

        // Verificar que no se llamó al WebClient
        verify(webClient, never()).post();
    }

    @Test
    void login_ConPasswordNulo_DeberiaLanzarBadRequest() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.login(loginRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El campo Username y password no deben venir vacios.", exception.getReason());

        // Verificar que no se llamó al WebClient
        verify(webClient, never()).post();
    }

    @Test
    void login_ConUsernameVacio_DeberiaFuncionar() {
        // Arrange
        setupWebClientMocks();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("testpass");

        TokenResponse expectedResponse = new TokenResponse();
        expectedResponse.setAccessToken("mock-access-token");

        when(responseSpec.bodyToMono(TokenResponse.class))
                .thenReturn(Mono.just(expectedResponse));

        // Act & Assert - Como el código actual no valida strings vacíos, esto debería
        // funcionar
        StepVerifier.create(authController.login(loginRequest))
                .expectNext(expectedResponse)
                .verifyComplete();

        // Verificar que se llamó al WebClient
        verify(webClient).post();
        verify(requestBodySpec).bodyValue(
                String.format(FORMATO_BODY_LOGIN,
                        clientId, clientSecret, "", "testpass"));
    }

    @Test
    void login_ConPasswordVacio_DeberiaFuncionar() {
        // Arrange
        setupWebClientMocks();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("");

        TokenResponse expectedResponse = new TokenResponse();
        expectedResponse.setAccessToken("mock-access-token");

        when(responseSpec.bodyToMono(TokenResponse.class))
                .thenReturn(Mono.just(expectedResponse));

        // Act & Assert - Como el código actual no valida strings vacíos, esto debería
        // funcionar
        StepVerifier.create(authController.login(loginRequest))
                .expectNext(expectedResponse)
                .verifyComplete();

        // Verificar que se llamó al WebClient
        verify(webClient).post();
        verify(requestBodySpec).bodyValue(
                String.format(FORMATO_BODY_LOGIN,
                        clientId, clientSecret, "testuser", ""));
    }

    @Test
    void login_CuandoKeycloakFalla_DeberiaTransmitirError() {
        // Arrange
        setupWebClientMocks();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");

        RuntimeException keycloakError = new RuntimeException("Keycloak error");
        when(responseSpec.bodyToMono(TokenResponse.class))
                .thenReturn(Mono.error(keycloakError));

        // Act & Assert
        StepVerifier.create(authController.login(loginRequest))
                .expectError(RuntimeException.class)
                .verify();
    }

    // Pruebas de integración usando WebTestClient
    @Test
    void loginEndpoint_ConCredencialesValidas_DeberiaRetornar200() {
        // Arrange
        setupWebClientMocks();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");

        TokenResponse mockResponse = new TokenResponse();
        mockResponse.setAccessToken("mock-token");

        when(responseSpec.bodyToMono(TokenResponse.class))
                .thenReturn(Mono.just(mockResponse));

        // Act & Assert
        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponse.class)
                .isEqualTo(mockResponse);
    }

    @Test
    void loginEndpoint_ConRequestVacio_DeberiaRetornar400() {
        // Act & Assert
        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void loginEndpoint_SinBody_DeberiaRetornar400() {
        // Act & Assert
        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private void setupWebClientMocksRefresh() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).body(any(BodyInserter.class));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void refreshToken_ConTokenValido_DeberiaRetornarNuevoToken() {
        setupWebClientMocksRefresh();
        TokenResponse expectedResponse = new TokenResponse();
        expectedResponse.setAccessToken("nuevo-access-token");
        when(responseSpec.bodyToMono(TokenResponse.class)).thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(authController.refreshToken("refresh-123"))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(requestBodyUriSpec).uri(KEYCLOAK_TOKEN_URI);
        verify(requestBodySpec).contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    void refreshToken_CuandoKeycloakRechaza_DeberiaRetornarUnauthorized() {
        setupWebClientMocksRefresh();
        WebClientResponseException rechazo = WebClientResponseException.create(
                HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null);
        when(responseSpec.bodyToMono(TokenResponse.class)).thenReturn(Mono.error(rechazo));

        StepVerifier.create(authController.refreshToken("refresh-vencido"))
                .expectErrorSatisfies(error -> {
                    ResponseStatusException ex = (ResponseStatusException) error;
                    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
                    assertEquals("No se pudo refrescar el token", ex.getReason());
                    assertEquals(rechazo, ex.getCause());
                })
                .verify();
    }

    @Test
    void refreshToken_CuandoFallaPorOtroMotivo_DeberiaPropagarError() {
        setupWebClientMocksRefresh();
        IllegalStateException falla = new IllegalStateException("sin conexión");
        when(responseSpec.bodyToMono(TokenResponse.class)).thenReturn(Mono.error(falla));

        StepVerifier.create(authController.refreshToken("refresh-123"))
                .expectErrorMatches(falla::equals)
                .verify();
    }
}