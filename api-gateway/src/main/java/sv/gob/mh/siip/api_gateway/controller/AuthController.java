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
import sv.gob.mh.siip.api_gateway.dto.LoginRequest;
import sv.gob.mh.siip.api_gateway.dto.TokenResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
		String bodyValue = String.format(
				"client_id=%s&client_secret=%s&username=%s&password=%s&grant_type=password",
				clientId, clientSecret, loginRequest.getUsername(), loginRequest.getPassword());

		return webClient.post().uri(keycloakTokenUri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(bodyValue)
				.retrieve().bodyToMono(TokenResponse.class);
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
