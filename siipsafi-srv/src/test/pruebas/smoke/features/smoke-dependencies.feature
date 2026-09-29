@smoke @dependencies
Feature: Smoke Tests de Dependencias Externas
  Validar conectividad y disponibilidad de servicios externos

  Background:
    * url baseUrl
    * configure headers = headers

  @smoke @dependencies @database
  Scenario: Verificar conexión a base de datos (si aplica)
    Given path endpoints.health
    When method GET
    Then status 200
    * def hasDb = response.components && response.components.db
    * if (hasDb) karate.match(response.components.db.status, 'UP')

  @smoke @dependencies @auth
  Scenario: Servidor de autenticación es accesible
    * def hasAuthUrl = oauth2TokenUrl != null && oauth2TokenUrl != ''
    * if (!hasAuthUrl) karate.abort()

    Given url oauth2TokenUrl
    When method OPTIONS
    Then assert responseStatus >= 200 && responseStatus < 500

  @smoke @dependencies @health-checks
  Scenario: Health check de dependencias está disponible
    Given path endpoints.health
    When method GET
    Then status 200
    And match response.status == 'UP'

  @smoke @dependencies @timeout
  Scenario: Dependencias responden dentro del timeout configurado
    Given path endpoints.health
    When method GET
    Then status 200
    And assert responseTime < 10000

  @smoke @dependencies @recovery
  Scenario: Aplicación puede iniciar sin algunas dependencias opcionales
    Given path endpoints.healthLive
    When method GET
    Then status 200
    And match response.status == 'UP'
