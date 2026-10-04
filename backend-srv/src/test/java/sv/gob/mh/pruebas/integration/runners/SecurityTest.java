package sv.gob.mh.pruebas.integration.runners;

import com.intuit.karate.junit5.Karate;

/**
 * Runner de JUnit5 para ejecutar tests de integración de Security
 * 
 * Endpoints probados:
 * - /api/v1/demo/security/publico (sin autenticación)
 * - /api/v1/demo/security/autenticado (sólo sesión)
 * - /api/v1/demo/security/expedientes (permiso VIEW sobre expedientes-consulta)
 * - DELETE /api/v1/demo/security/expedientes/{id} (permiso DELETE sobre expedientes-registro)
 * 
 * Uso:
 * mvn test -Dtest=SecurityTest
 * mvn test -Dtest=SecurityTest -Dkarate.env=qa
 * mvn test -Dtest=SecurityTest -Djwt.token="your-token-here"
 */
public class SecurityTest {
    
    @Karate.Test
    Karate testSecurity() {
        return Karate.run("classpath:integration/features/security/security.feature")
                .tags("@integration", "@security")
                .relativeTo(getClass());
    }
    
    @Karate.Test
    Karate testPublicEndpoints() {
        return Karate.run("classpath:integration/features/security/security.feature")
                .tags("@public")
                .relativeTo(getClass());
    }
    
    @Karate.Test
    Karate testAuthenticatedEndpoints() {
        return Karate.run("classpath:integration/features/security/security.feature")
                .tags("@authenticated")
                .relativeTo(getClass());
    }
    
    @Karate.Test
    Karate testPermissionBasedEndpoints() {
        return Karate.run("classpath:integration/features/security/security.feature")
                .tags("@permissions")
                .relativeTo(getClass());
    }
}
