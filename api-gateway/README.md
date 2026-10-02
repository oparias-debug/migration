# dgicp-siip2-api-gateway

Punto de entrada HTTP de SIIP. Spring Cloud Gateway (WebFlux, Spring Boot 3.5, Java 21): valida
el JWT del cliente, lo releva a los servicios (`TokenRelay`) y enruta por prefijo:

| Prefijo público | Servicio | Variable |
|---|---|---|
| `/back/**` | backend-srv | `BACKEND_SRV_URL` |
| `/admin/**` | admin-srv | `ADMIN_SRV_URL` |
| `/siipsafi/**` | siipsafi-srv | `SIIPSAFI_SRV_URL` |

Además expone `/auth/login` y `/auth/refresh` (token por usuario y contraseña contra Keycloak),
Swagger UI agregado (`/swagger-ui.html`) y `/actuator/health/**` para los probes.

Sigue la plataforma de la plantilla DINAFI (Backstage, Dockerfile, chart, Sonar, techdocs). Las
piezas del marco pensadas para servicios Spring MVC (config server, logger remoto, auditoría y
autorización por permisos) no aplican al gateway: la autorización fina la hace cada servicio.

## Documentación

La documentación técnica está en [`docs/`](docs/index.md) y se publica en Backstage (techdocs).

- [Arquitectura](docs/architecture.md): rutas, seguridad y filtros.
- [Configuración de variables](docs/helm-configuration.md): qué lee el gateway y de dónde.
- [Ambiente local](docs/local-development.md): docker-compose del monorepo y ejecución directa.
- [Despliegue](docs/deployment.md): imagen, chart `api-gateway-config` y pendientes.

## Comandos

```bash
./mvnw test                      # pruebas
./mvnw clean package -DskipTests # target/app.jar, el que copian los Dockerfile
```

`Dockerfile` es el de la entidad (imagen base del registry interno del MH); `Dockerfile.local` es
el que usa el `docker-compose.yml` del monorepo.
