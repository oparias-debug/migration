# Ambiente local

## Con el docker-compose del monorepo

El servicio `api-gateway` de `docker-compose.yml` construye `Dockerfile.local`, que copia
`target/app.jar` y espera a que Keycloak esté listo (`wait-for-keycloak.sh`). Compilar antes:

```bash
./mvnw clean package -DskipTests
docker compose up -d --build api-gateway
```

Publica el puerto `8080` del host. Las variables vienen de `.env` (`KEYCLOAK_*`); las URLs de los
servicios toman sus valores por defecto (`http://backend-srv:8081`, `http://admin-srv:8080`,
`http://siipsafi-srv:8080`), que son los nombres de la red `microred`.

Comprobar que está arriba:

```bash
curl http://localhost:8080/actuator/health/readiness
```

## Ejecución directa

```bash
KEYCLOAK_INTERNAL_URL=http://localhost:8085 KEYCLOAK_REALM=siip-api \
KEYCLOAK_CLIENT_ID=api-gateway KEYCLOAK_CLIENT_SECRET=<secreto> \
BACKEND_SRV_URL=http://localhost:8081 ./mvnw spring-boot:run
```
