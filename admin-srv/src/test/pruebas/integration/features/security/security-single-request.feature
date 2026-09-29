Feature: Single Request Helper
  Helper para hacer un request simple - usado en pruebas de concurrencia

  Scenario: Single request to public endpoint
    Given url baseUrl
    And path endpoints.securityPublico
    When method GET
