@smoke @critical
Feature: Smoke Tests Básicos
  Pruebas críticas que deben pasar antes de cualquier despliegue
  Validan la funcionalidad básica de la aplicación

  Background:
    * url baseUrl
    * configure headers = headers
    * configure connectTimeout = 10000
    * configure readTimeout = 10000

  @smoke @health @critical
  Scenario: Aplicación está viva - Liveness Probe
    Given path endpoints.healthLive
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 3000

  @smoke @health @critical
  Scenario: Aplicación está lista - Readiness Probe
    Given path endpoints.healthReady
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 3000

  @smoke @health @critical
  Scenario: Health check general funciona
    Given path endpoints.health
    When method GET
    Then status 200
    And match response.status == 'UP'
    And assert responseTime < 5000

  @smoke @performance @critical
  Scenario: Tiempo de respuesta de health check es aceptable
    Given path endpoints.healthLive
    When method GET
    Then status 200
    And assert responseTime < 2000

  @smoke @api
  Scenario: API rechaza requests sin autenticación en endpoints protegidos
    Given path endpoints.catalogos
    And headers { Authorization: null }
    When method GET
    Then status 401

  @smoke @health
  Scenario: Health check general está UP
    Given path endpoints.health
    When method GET
    Then status 200
    And match response.status == 'UP'

  @smoke @connectivity
  Scenario: Aplicación es accesible desde internet/red
    Given path endpoints.health
    When method GET
    Then status 200
