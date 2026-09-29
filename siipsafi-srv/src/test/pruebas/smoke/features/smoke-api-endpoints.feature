@smoke @api
Feature: Smoke Tests de Endpoints API
  Validar que todos los endpoints principales responden correctamente

  Background:
    * url baseUrl
    * configure headers = headers
    * configure connectTimeout = 10000
    * configure readTimeout = 10000

  @smoke @api @public
  Scenario: GET /api/v1/demo/security/hello responde
    Given path endpoints.securityHello
    When method GET
    Then status 200
    And match response == { message: '#string' }
    And assert responseTime < 3000

  @smoke @api @authenticated
  Scenario: GET /api/v1/demo/security/hello/auth responde (con token)
    * def hasToken = authToken != null && authToken != ''
    * if (!hasToken) karate.abort()

    Given path endpoints.securityAuth
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And match response == '#object'

  @smoke @api @authenticated
  Scenario: GET /api/v1/demo/security/hello/auth sin token retorna 401
    Given path endpoints.securityAuth
    And headers { Authorization: null }
    When method GET
    Then status 401

  @smoke @api @permissions
  Scenario: GET /api/v1/demo/security/hello/auth/roles responde
    * def hasToken = authToken != null && authToken != ''
    * if (!hasToken) karate.abort()

    Given path endpoints.securityRoles
    And header Authorization = 'Bearer ' + authToken
    When method GET
    # 200 si tiene permisos, 403 si no, ambos son válidos en smoke test
    Then assert responseStatus == 200 || responseStatus == 403

  @smoke @api @validation
  Scenario: API valida Content-Type correctamente
    Given path endpoints.securityHello
    And header Content-Type = 'application/json'
    When method GET
    Then status 200
    And match header Content-Type contains 'application/json'

  @smoke @api @errors
  Scenario: API maneja errores 404 correctamente
    Given path '/api/v1/endpoint-que-no-existe'
    When method GET
    Then status 404

  @smoke @api @methods
  Scenario: API valida métodos HTTP correctamente
    Given path endpoints.securityHello
    When method POST
    Then status 405

  @smoke @api @performance
  Scenario: Endpoints públicos responden rápidamente
    Given path endpoints.securityHello
    When method GET
    Then status 200
    And assert responseTime < 2000

  @smoke @api @concurrent
  Scenario: API maneja requests concurrentes
    * def results = karate.repeat(5, function(){ return karate.call('classpath:smoke/features/smoke-single-request.feature') })
    * def allSuccess = results.every(r => r.responseStatus == 200)
    * assert allSuccess

  @smoke @api @headers
  Scenario: API incluye headers de respuesta correctos
    Given path endpoints.securityHello
    When method GET
    Then status 200
    And match header Content-Type == '#present'
    And match header Date == '#present'
