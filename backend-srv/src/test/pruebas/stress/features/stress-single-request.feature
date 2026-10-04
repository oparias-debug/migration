@ignore
Feature: Helper — Petición individual para pruebas de stress

  Scenario: Petición GET al path indicado
    Given url baseUrl
    And path targetPath
    When method GET
