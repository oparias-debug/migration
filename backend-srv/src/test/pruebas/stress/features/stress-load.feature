@stress @load
Feature: Pruebas de Carga — Simulación de usuarios concurrentes
  Valida que los endpoints soporten carga sostenida con tiempos de respuesta aceptables.
  Se ejecuta con múltiples hilos en paralelo vía Karate Runner.

  Background:
    * url baseUrl
    * configure headers = headers
    * configure connectTimeout = 10000
    * configure readTimeout = 10000

  @stress @health @critical
  Scenario: Carga - Endpoint de liveness responde bajo carga
    Given path '/actuator/health/liveness'
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 2000

  @stress @health
  Scenario: Carga - Endpoint de readiness responde bajo carga
    Given path '/actuator/health/readiness'
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 2000

  @stress @public @critical
  Scenario: Carga - Endpoint público responde bajo carga
    Given path '/api/v1/demo/security/hello'
    When method GET
    Then status 200
    And match response.message == 'Hello World, pagina sin autenticación!'
    And assert responseTime < 1000

  @stress @authenticated
  Scenario: Carga - Endpoint autenticado responde bajo carga
    * def hasToken = typeof authToken !== 'undefined' && authToken != null && authToken != ''
    * if (!hasToken) karate.abort()
    Given path '/api/v1/demo/security/hello/auth'
    And header Authorization = 'Bearer ' + authToken
    When method GET
    Then status 200
    And assert responseTime < 2000

  @stress @public
  Scenario: Carga - Health general responde bajo carga
    Given path '/actuator/health'
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 2000
