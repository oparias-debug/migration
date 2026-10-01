package sv.gob.mh.siip.api_gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import sv.gob.mh.siip.api_gateway.config.ArgumentosSensibles;
import sv.gob.mh.siip.api_gateway.dto.LoginRequest;
import sv.gob.mh.siip.api_gateway.dto.TokenResponse;

// login recibe el password y refresh el refresh token: la auditoría no registra sus argumentos.
@ArgumentosSensibles
@RestController
@RequestMapping("/auth")
public class AuthController {

  static final String CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos";

  private final WebClient webClient;
  private final String keycloakTokenUri;
  private final String clientId;
  private final String clientSecret;

  public AuthController(WebClient.Builder webClientBuilder,
      @Value("${keycloak.token-uri}") String keycloakTokenUri,
      @Value("${keycloak.client-id}") String clientId,
      @Value("${keycloak.client-secret}") String clientSecret) {
    this.webClient = webClientBuilder.build();
    this.keycloakTokenUri = keycloakTokenUri;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @PostMapping("/login")
  public Mono<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
    if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "El campo Username y password no deben venir vacios.");
    }
    // fromFormData codifica cada valor: un password con &, + o % llega tal cual a Keycloak y un
    // username no puede agregar campos al formulario (antes se concatenaba sin codificar).
    return webClient.post().uri(keycloakTokenUri)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData(formularioLogin(loginRequest)))
        .retrieve().bodyToMono(TokenResponse.class)
        // Un 4xx de Keycloak es un rechazo de las credenciales (401 invalid_grant; 400 si la cuenta
        // está deshabilitada o incompleta): al front le llega 401, y como es 4xx no deja traza en el
        // log. Un 5xx o Keycloak caído sigue saliendo como error del servidor, con su log.
        .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().is4xxClientError()
            ? Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS, ex))
            : Mono.error(ex));
  }

  MultiValueMap<String, String> formularioLogin(LoginRequest loginRequest) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "password");
    formData.add("username", loginRequest.getUsername());
    formData.add("password", loginRequest.getPassword());
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);
    return formData;
  }

  @PostMapping("/refresh")
  public Mono<TokenResponse> refreshToken(@RequestBody String refreshToken) {
    
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "refresh_token");
    formData.add("refresh_token", refreshToken);
    formData.add("client_id", clientId);
    formData.add("client_secret", clientSecret);

    return webClient
      .post()
      .uri(keycloakTokenUri)
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .body(BodyInserters.fromFormData(formData))
      .retrieve()
      .bodyToMono(TokenResponse.class)
      .onErrorResume(WebClientResponseException.class, ex ->
        Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se pudo refrescar el token", ex))
      );
  }
}
