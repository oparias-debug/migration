# Pruebas - Spring Boot Application

Pruebas de **integración (Karate)**, **smoke tests (Karate)** y **stress (Karate)**.

## Estructura

```
src/test/pruebas/
├── run-tests.sh               # Script principal de ejecución
├── integration/                # Pruebas de integración (Karate)
│   ├── features/
│   │   ├── security/          # Tests de seguridad
│   │   └── health/            # Tests de health
│   ├── runners/               # Runners JUnit5
│   ├── karate-config.js       # Configuración Karate (multi-ambiente)
│   └── oauth2-token-helper.feature
├── smoke/                     # Smoke tests (Karate)
│   ├── features/
│   ├── runners/
│   └── scripts/
│       └── run-smoke-tests.sh
└── stress/                    # Pruebas de stress (Karate)
    ├── features/
    ├── runners/
    └── README.md
```

## Configuración

Las variables de configuración se manejan de dos formas:

- **DEV (local):** `karate-config.js` tiene valores por defecto para `localhost`. No requiere configuración adicional.
- **Otros ambientes (qa, test, preprod, prod):** Las variables son **obligatorias** y se pasan como `-D` system properties de Maven. En CI/CD (Tekton) se inyectan desde el ConfigMap y Secrets del namespace de OpenShift.

### Variables requeridas

| Variable | System Property | Requerida |
|----------|-----------------|-----------|
| `BASE_URL` | `-Dbase.url` | Solo DEV tiene default |
| `OAUTH2_TOKEN_URL` | `-Doauth2.token.url` | Solo DEV tiene default |
| `OAUTH2_CLIENT_ID` | `-Doauth2.client.id` | Solo DEV tiene default |
| `OAUTH2_CLIENT_SECRET` | `-Doauth2.client.secret` | Desde Secret |
| `JWT_TOKEN` | `-Djwt.token` | Opcional (se obtiene auto) |
| `STRESS_THREADS` | `-Dstress.threads` | Opcional (default: 10) |

## Ejecucion

### Script principal (recomendado)

```bash
export KARATE_ENV=dev
export BASE_URL=http://localhost:8080

./src/test/pruebas/run-tests.sh integration
./src/test/pruebas/run-tests.sh smoke
./src/test/pruebas/run-tests.sh stress
./src/test/pruebas/run-tests.sh all
```

### Maven directo

```bash
# DEV — sin configuracion adicional
mvn test -Pintegration-tests -Dkarate.env=dev
mvn test -Psmoke-tests -Dkarate.env=dev
mvn test -Pstress-tests -Dkarate.env=dev

# Otros ambientes — variables obligatorias
mvn test -Pintegration-tests \
  -Dkarate.env=test \
  -Dbase.url=https://mi-servicio.apps.gcp-op-desa.cloud.mh.gob.sv \
  -Doauth2.token.url=https://keycloak.apps.gcp-op-desa.cloud.mh.gob.sv/realms/MHINTERNO/protocol/openid-connect/token \
  -Doauth2.client.id=test-client \
  -Doauth2.client.secret=mi-secreto
```

## Troubleshooting

### Error: 401 Unauthorized
```bash
curl -X POST "$OAUTH2_TOKEN_URL" \
  -d "client_id=$OAUTH2_CLIENT_ID" \
  -d "client_secret=$OAUTH2_CLIENT_SECRET" \
  -d "grant_type=client_credentials"
```

### Error: Connection Timeout
```bash
curl http://localhost:8080/actuator/health
mvn test -Pintegration-tests -Dkarate.env=dev -Dtimeout=60000
```

### Karate Tests no encuentran features
```bash
mvn clean compile test-compile
```

## Referencias

- [Karate Framework](https://github.com/karatelabs/karate)
- [Spring Boot Testing Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
