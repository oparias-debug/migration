package sv.gob.mh.pruebas.smoke.runners;

import com.intuit.karate.junit5.Karate;

/**
 * Runner para Smoke Tests - Pruebas rápidas de validación post-despliegue
 *
 * Estos tests deben ejecutarse después de cada despliegue para validar
 * que la aplicación está funcionando correctamente.
 *
 * Características:
 * - Ejecución rápida (< 2 minutos)
 * - Tests críticos de funcionalidad básica
 * - Validación de health checks
 * - Validación de endpoints principales
 * - Validación de dependencias externas
 *
 * Uso:
 * # Ejecutar todos los smoke tests
 * mvn test -Dtest=SmokeTestRunner
 *
 * # Ejecutar en ambiente específico
 * mvn test -Dtest=SmokeTestRunner -Dkarate.env=qa
 *
 * # Ejecutar solo tests críticos
 * mvn test -Dtest=SmokeTestRunner#testSmokeCritical
 *
 * # Ejecutar con configuración personalizada
 * mvn test -Dtest=SmokeTestRunner -Dkarate.env=prod -Dbase.url=https://mi-servicio.gob.sv
 */
public class SmokeTestRunner {

    /**
     * Ejecuta todos los smoke tests
     */
    @Karate.Test
    Karate testSmokeAll() {
        return Karate.run("classpath:smoke/features")
                .tags("@smoke")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo tests críticos (mínimo indispensable)
     */
    @Karate.Test
    Karate testSmokeCritical() {
        return Karate.run("classpath:smoke/features")
                .tags("@critical")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo smoke tests de health checks
     */
    @Karate.Test
    Karate testSmokeHealth() {
        return Karate.run("classpath:smoke/features")
                .tags("@smoke", "@health")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo smoke tests de API endpoints
     */
    @Karate.Test
    Karate testSmokeApi() {
        return Karate.run("classpath:smoke/features")
                .tags("@smoke", "@api")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta solo smoke tests de dependencias
     */
    @Karate.Test
    Karate testSmokeDependencies() {
        return Karate.run("classpath:smoke/features")
                .tags("@smoke", "@dependencies")
                .relativeTo(getClass());
    }

    /**
     * Ejecuta smoke tests de performance
     */
    @Karate.Test
    Karate testSmokePerformance() {
        return Karate.run("classpath:smoke/features")
                .tags("@smoke", "@performance")
                .relativeTo(getClass());
    }
}
