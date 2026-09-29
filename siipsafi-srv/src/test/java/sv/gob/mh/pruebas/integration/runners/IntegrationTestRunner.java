package sv.gob.mh.pruebas.integration.runners;

import com.intuit.karate.junit5.Karate;

/**
 * Runner completo para ejecutar todos los tests de integración
 *
 * Este runner ejecuta todas las features de integración en el proyecto.
 *
 * Uso:
 * # Ejecutar todos los tests de integración
 * mvn test -Dtest=IntegrationTestRunner
 *
 * # Ejecutar en ambiente específico
 * mvn test -Dtest=IntegrationTestRunner -Dkarate.env=qa
 *
 * # Ejecutar con token JWT
 * mvn test -Dtest=IntegrationTestRunner -Djwt.token="your-token-here"
 *
 * # Ejecutar solo tests con tag específico
 * mvn test -Dtest=IntegrationTestRunner -Dkarate.options="--tags @security"
 *
 * # Ejecutar con configuración personalizada (variables del ConfigMap)
 * mvn test -Dtest=IntegrationTestRunner -Dkarate.env=qa -Dbase.url=https://mi-servicio.apps.gcp-op-desa.cloud.mh.gob.sv
 */
public class IntegrationTestRunner {

    /**
     * Ejecuta todos los tests de integración
     */
    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:integration/features")
                .tags("@integration")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo tests de Security
     */
    @Karate.Test
    Karate testSecurityOnly() {
        return Karate.run("classpath:integration/features")
                .tags("@security")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo tests de Health
     */
    @Karate.Test
    Karate testHealthOnly() {
        return Karate.run("classpath:integration/features")
                .tags("@health")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo tests públicos (sin autenticación)
     */
    @Karate.Test
    Karate testPublicOnly() {
        return Karate.run("classpath:integration/features")
                .tags("@public")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo tests autenticados
     */
    @Karate.Test
    Karate testAuthenticatedOnly() {
        return Karate.run("classpath:integration/features")
                .tags("@authenticated")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo tests críticos
     */
    @Karate.Test
    Karate testCriticalOnly() {
        return Karate.run("classpath:integration/features")
                .tags("@critical")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta tests apropiados para smoke testing
     */
    @Karate.Test
    Karate testSmokeTests() {
        return Karate.run("classpath:integration/features")
                .tags("@smoke")
                .relativeTo(getClass());
    }
}
