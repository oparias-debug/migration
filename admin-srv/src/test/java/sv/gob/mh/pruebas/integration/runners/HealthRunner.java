package sv.gob.mh.pruebas.integration.runners;

import com.intuit.karate.junit5.Karate;

/**
 * Runner de JUnit5 para ejecutar tests de integración de Health Checks
 *
 * Endpoints probados:
 * - /actuator/health
 * - /actuator/health/liveness
 * - /actuator/health/readiness
 *
 * Uso:
 * mvn test -Dtest=HealthRunner
 * mvn test -Dtest=HealthRunner -Dkarate.env=qa
 */
public class HealthRunner {

    @Karate.Test
    Karate testHealth() {
        return Karate.run("classpath:integration/features/health/health.feature")
                .tags("@integration", "@health")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testHealthCritical() {
        return Karate.run("classpath:integration/features/health/health.feature")
                .tags("@critical")
                .relativeTo(getClass());
    }
}
