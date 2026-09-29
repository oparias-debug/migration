@integration @security
Feature: Seguridad — los cuatro niveles del ejemplo

  Autenticación y autorización contra el servicio desplegado.

  Las pruebas unitarias sustituyen el authorization-service por un doble; éstas NO. Aquí se
  llama al servicio real, así que un escenario de permisos sólo tiene sentido si el padrón de
  `authz/ejemplo-authz.json` está cargado para este componente y el token pertenece a un
  usuario del grupo correspondiente. Cuando falta el dato, el escenario se salta en vez de
  fallar: un rojo por falta de configuración no informa de nada.

  Background:
    * url baseUrl
    * configure headers = headers

  # ==========================================================================
  # NIVEL 1 — recurso público
  # ==========================================================================

  @public @smoke
  Scenario: El recurso público responde sin autenticación
    Given path endpoints.securityPublico
    When method GET
    Then status 200
    And match response.mensaje contains 'no exige autenticación'
    And match responseType == 'json'
    And assert responseTime < 2000

  @public
  Scenario: El recurso público rechaza los métodos que no expone
    Given path endpoints.securityPublico
    When method POST
    Then status 405

  # ==========================================================================
  # NIVEL 2 — sólo autenticación
  # ==========================================================================

  @authenticated @jwt
  Scenario: Sin token, el recurso autenticado responde 401
    Given path endpoints.securityAutenticado
    And headers { Authorization: null }
    When method GET
    Then status 401

  @authenticated @jwt
  Scenario: Un token inválido responde 401
    Given path endpoints.securityAutenticado
    And header Authorization = 'Bearer no-es-un-token'
    When method GET
    Then status 401

  @authenticated @jwt
  Scenario: Con token válido devuelve los claims
    * if (!authToken) karate.abort()
    Given path endpoints.securityAutenticado
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response.identity == '#present'
    And assert responseTime < 3000

  # ==========================================================================
  # NIVEL 3 y 4 — autorización por permiso
  #
  # Es la parte que no se puede simular: la decide el authorization-service con
  # el padrón cargado. Ver docs/autorizacion.md.
  # ==========================================================================

  @authorization @jwt
  Scenario: Sin token, los endpoints con permiso responden 401 y no 403
    # El 401 dice "no sé quién eres" y el 403 "sé quién eres y no puedes".
    # Confundirlos es el diagnóstico más caro de este diseño.
    Given path endpoints.securityExpedientes
    And headers { Authorization: null }
    When method GET
    Then status 401

  @authorization @jwt
  Scenario: Un usuario CON el permiso de consulta recibe 200
    * if (!authToken) karate.abort()
    Given path endpoints.securityExpedientes
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response.identity == '#present'
    And match response.expedientes == '#[_ > 0]'

  @authorization @jwt @sin-permiso
  Scenario: Un usuario SIN el permiso recibe 403, no 200
    # Requiere un segundo token, de un usuario que NO esté en el grupo del padrón.
    # Se pasa con -Djwt.token.sin.permiso=<token>.
    * def tokenSinPermiso = karate.properties['jwt.token.sin.permiso']
    * if (!tokenSinPermiso) karate.abort()

    Given path endpoints.securityExpedientes
    And header Authorization = 'Bearer ' + tokenSinPermiso
    When method GET
    Then status 403

  @authorization @jwt @denegacion
  Scenario: El mismo usuario consulta pero no borra
    # La demostración completa: misma sesión, mismo servicio, distinta operación.
    # Requiere el token de un usuario del grupo de consulta, que tiene VIEW heredado
    # y DELETE denegado con effect = 0.
    * def tokenConsulta = karate.properties['jwt.token.consulta']
    * if (!tokenConsulta) karate.abort()

    Given path endpoints.securityExpedientes
    And header Authorization = 'Bearer ' + tokenConsulta
    When method GET
    Then status 200

    Given path endpoints.securityExpedientes, 1
    And header Authorization = 'Bearer ' + tokenConsulta
    When method DELETE
    Then status 403

  @authorization @jwt
  Scenario: Un usuario CON el permiso de borrado recibe 200
    * def tokenAdmin = karate.properties['jwt.token.admin'] || authToken
    * if (!tokenAdmin) karate.abort()

    Given path endpoints.securityExpedientes, 1
    And header Authorization = 'Bearer ' + tokenAdmin
    When method DELETE
    Then status 200
    And match response.eliminado == 1
