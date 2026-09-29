@integration @health
Feature: Health Checks de la Aplicación
  Verificar que los endpoints de salud funcionan correctamente

  Background:
    * url baseUrl
    * configure headers = headers

  @smoke @critical
  Scenario: Health check general - /actuator/health
    Given path endpoints.health
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 3000

  @smoke @critical
  Scenario: Liveness probe - /actuator/health/liveness
    Given path endpoints.healthLive
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 2000

  @smoke @critical
  Scenario: Readiness probe - /actuator/health/readiness
    Given path endpoints.healthReady
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 2000

  @health
  Scenario: Health check contiene información de checks individuales
    Given path endpoints.health
    When method GET
    Then status 200
    And match response.components == '#object'

  @health
  Scenario: Validar estructura de respuesta de health check
    Given path endpoints.health
    When method GET
    Then status 200
    And match response contains { status: 'UP' }

  @health @timing
  Scenario: Health checks deben responder rápidamente
    Given path endpoints.healthLive
    When method GET
    Then status 200
    And assert responseTime < 1000

    Given path endpoints.healthReady
    When method GET
    Then status 200
    And assert responseTime < 1000

  @health @headers
  Scenario: Health check acepta diferentes Accept headers
    Given path endpoints.health
    And header Accept = 'application/json'
    When method GET
    Then status 200
    And match header Content-Type contains 'application/json'

  @health @concurrent
  Scenario: Múltiples health checks concurrentes
    * def results = karate.repeat(5, function(){ return karate.call('classpath:integration/features/health/health-single-check.feature') })
    * match each results[*].response.status == 'UP'
