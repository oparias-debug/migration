Feature: Single Health Check Helper
  Helper para hacer un health check simple

  Scenario: Single health check request
    Given url baseUrl
    And path endpoints.health
    When method GET
    Then status 200
