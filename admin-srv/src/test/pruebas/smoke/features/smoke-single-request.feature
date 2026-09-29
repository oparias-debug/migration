Feature: Single Request Helper for Smoke Tests
  Helper para hacer un request simple en smoke tests

  Scenario: Single smoke test request
    Given url baseUrl
    And path endpoints.securityHello
    When method GET
