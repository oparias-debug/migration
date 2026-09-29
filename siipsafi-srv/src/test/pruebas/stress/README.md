# Pruebas de Stress — Karate

Pruebas de carga y pico (spike) basadas en Karate, ejecutadas como tests de JUnit5 con paralelismo configurable.
No requiere herramientas externas — se ejecuta con Maven como cualquier otro test.

## Ejecución

### Vía script (recomendado)

```bash
export KARATE_ENV=dev
export BASE_URL=http://localhost:8080
./src/test/pruebas/run-tests.sh stress
```

### Directamente con Maven

```bash
mvn test -Pstress-tests -Dkarate.env=dev
mvn test -Pstress-tests \
  -Dkarate.env=test \
  -Dbase.url=https://mi-servicio.apps.gcp-op-desa.cloud.mh.gob.sv \
  -Doauth2.token.url=https://keycloak.apps.gcp-op-desa.cloud.mh.gob.sv/... \
  -Doauth2.client.id=test-client \
  -Doauth2.client.secret=my-secret \
  -Dstress.threads=50

mvn test -Pstress-tests -Dtest=StressTestRunner#testCriticalLoad
mvn test -Pstress-tests -Dtest=StressTestRunner#testSpike
```

## Estructura

```
stress/
├── features/
│   ├── stress-load.feature            # Carga sostenida contra todos los endpoints
│   ├── stress-spike.feature           # Ráfagas de peticiones (spike)
│   └── stress-single-request.feature  # Helper para peticiones individuales
├── runners/
│   └── StressTestRunner.java          # Runner JUnit5 con paralelismo
└── README.md
```

## Tags disponibles

| Tag | Descripción |
|-----|-------------|
| `@load` | Pruebas de carga sostenida |
| `@spike` | Pruebas de pico/ráfaga |
| `@critical` | Solo endpoints críticos |
| `@health` | Endpoints de health check |
| `@public` | Endpoints públicos |
| `@authenticated` | Endpoints con JWT |

## Umbrales de Rendimiento

| Tipo | Fallos permitidos | Tiempo máximo |
|------|-------------------|---------------|
| Carga (load) | 0% | 2s por request |
| Endpoint público | 0% | 1s por request |
| Spike | < 5% | 5s total ráfaga |

## Configuración por Ambiente

| Ambiente | `STRESS_THREADS` | Uso |
|----------|------------------|-----|
| dev | 5 | Validación funcional local |
| qa | 20 | Pruebas de integración |
| test | 50 | Simular producción |
| preprod | 100 | Validación de capacidad |
| prod | **No ejecutar stress** | Solo smoke tests |
