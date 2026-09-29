Feature: OAuth2 Token Helper
  Helper feature para obtener tokens JWT desde servidor OAuth2.
  Este feature se llama desde karate-config.js

  # DOS FLUJOS, elegidos por lo que llegue. El cliente del marco es PUBLICO, asi que
  # Keycloak rechaza client_credentials con "Public client not allowed to retrieve
  # service account". Con usuario y contrasena si emite token -comprobado contra el
  # authentication-service de dev-. Se conserva el flujo de cliente confidencial para
  # el dia que exista uno, sin tener que volver a tocar este fichero.

  Scenario: Obtener token JWT
    * def porContrasena = username != null && username != ''
    * def cuerpo = porContrasena ? { grant_type: 'password', client_id: clientId, username: username, password: password } : { grant_type: 'client_credentials', client_id: clientId, client_secret: clientSecret }
    Given url tokenUrl
    And form fields cuerpo
    When method post
    Then status 200
    And match response.access_token == '#present'
    And match response.token_type == 'Bearer'
