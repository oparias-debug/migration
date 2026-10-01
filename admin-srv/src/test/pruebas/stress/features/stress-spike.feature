@stress @spike
Feature: Pruebas de Pico (Spike) — Ráfagas de peticiones
  Valida el comportamiento del servicio ante picos repentinos de tráfico.
  Cada escenario ejecuta múltiples peticiones secuenciales para simular ráfagas.

  Background:
    * url baseUrl
    * configure headers = headers
    * configure connectTimeout = 5000
    * configure readTimeout = 5000

  @stress @spike @health
  Scenario: Spike - 10 peticiones rápidas al health check
    * def doRequest =
    """
    function() {
      var response = karate.call('classpath:stress/features/stress-single-request.feature', { targetPath: '/actuator/health/liveness' });
      return { status: response.responseStatus };
    }
    """
    * def results = karate.repeat(10, doRequest)
    * def failures = karate.filter(results, function(x){ return x.status != 200 })
    * assert failures.length == 0

  @stress @spike
  Scenario: Spike - Todas las peticiones completan en menos de 5 segundos
    * def start = java.lang.System.currentTimeMillis()
    * def doRequest =
    """
    function() {
      karate.call('classpath:stress/features/stress-single-request.feature', { targetPath: '/actuator/health/liveness' });
      return true;
    }
    """
    * karate.repeat(10, doRequest)
    * def elapsed = java.lang.System.currentTimeMillis() - start
    * assert elapsed < 5000
