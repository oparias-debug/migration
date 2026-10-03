# Configuración y ambientes

`backend-srv` no guarda en el código nada que cambie entre ambientes. Todo sale de variables
de entorno que se leen en `src/main/resources/application.yml`:

| Dónde corre | Quién pone las variables |
|---|---|
| Local (`./mvnw spring-boot:run` o Docker Compose del equipo) | Variables del shell o un archivo `.env` (sección "Arranque rápido" del `README.md` del repositorio) |
| Ambientes de la entidad (dev, pruebas, preproducción, producción) | Chart Helm del repo `dgicp-siip2/backend-srv-config`: ConfigMap `backend-srv-cmp` + Secret `backend-srv-secret` |

La imagen es la misma en todos los ambientes. Para cambiar un valor en un ambiente se edita
`backend-srv-config`, no este repo.

## Variables

| Variable | Obligatoria | Por defecto | Local (`.env`) | Entidad | Sensible |
|---|---|---|---|---|---|
| `DB_URL` | Sí | — | `jdbc:postgresql://postgres:5432/preinversiondb` | `jdbc:oracle:thin:@<host>:<puerto>/<servicio>` | No |
| `DB_DRIVER_CLASS_NAME` | Sí | — | `org.postgresql.Driver` | `oracle.jdbc.OracleDriver` | No |
| `DB_USER` | Sí | — | usuario local | usuario `*_POOL` | **Secret** |
| `DB_PASSWORD` | Sí | — | contraseña local | — | **Secret** |
| `DB_SCHEMA` | No | `public` | `public` | esquema dueño de las tablas | No |
| `JPA_DDL_AUTO` | No | `validate` (`create-drop` en perfil `dev`) | — | `validate` | No |
| `JPA_SHOW_SQL` | No | `false` (`true` en perfil `dev`) | — | `false` | No |
| `GATEWAY_URL` | No | `http://localhost:8080` | `http://localhost:8080` | URL pública de api-gateway | No |
| `SECURITY_URL_KEYCLOAK` | No | `keycloak-mh-dev.apps.gcp-op-desa.cloud.mh.gob.sv` | — (ver nota) | host del Keycloak del ambiente | No |
| `SECURITY_REALM` | No | `MHINTERNO` | — (ver nota) | realm de SIIP | No |
| `HTTP_PORT` | No | `8081` | — | `8080` | No |
| `SPRING_PROFILES_ACTIVE` | No | `prod` | `dev` | `prod` | No |
| `LOG_LEVEL` | No | `INFO` | — | `WARN` | No |

Notas:

- `GATEWAY_URL` solo se usa para los `servers` del OpenAPI (el Swagger UI arma las URLs
  como `<GATEWAY_URL>/back/...`). backend-srv no llama a api-gateway.
- `SECURITY_URL_KEYCLOAK` y `SECURITY_REALM` arman el emisor del JWT que valida backend-srv
  (`https://<host>/realms/<realm>`); son las mismas del ConfigMap del chart y de admin-srv, y
  tienen que apuntar al realm contra el que valida api-gateway. En local, como el Keycloak va
  por `http://`, docker-compose fija directamente
  `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` y `..._JWK_SET_URI`.
- `HTTP_PORT` tiene que coincidir con el `containerPort` del chart (8080). En local se queda
  en 8081, que es a donde apunta api-gateway en `docker-compose.yml`.
- Las probes del chart usan `/actuator/health/liveness` y `/actuator/health/readiness`,
  habilitadas con `management.endpoint.health.probes.enabled`.
- `LOG_LEVEL` acepta los niveles de Logback: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `OFF`
  (`WARNING` no es válido).

## Perfiles

| Perfil | Uso | Esquema | Datos de prueba |
|---|---|---|---|
| `prod` (por defecto) | Ambientes de la entidad | Solo valida (`validate`) | No |
| `dev` | Local y paquete del tester | Lo recrea en cada arranque (`create-drop`) | Sí (`DevSeeder`) |
| `test` | `mvn test` (H2 en memoria) | Lo recrea | No |

**Nunca activar `dev` en la entidad**: borra los datos en cada reinicio y siembra datos
ficticios.

Los valores por defecto de `application.yml` son los seguros a propósito: si a un ambiente
compartido le falta una variable, la app no toca el esquema. Si el esquema no existe o no
coincide con las entidades, el pod no arranca y el error queda en el log.

## Qué no lee backend-srv

El `configMap.data` del chart trae de la plantilla del marco DINAFI `CONFIG_SERVICE_URL`,
`AUTHZ_SERVICE_URL`, `AUDIT_SERVICE_URL`, `LOG_SERVICE_URL`, `SECURITY_URL_KEYCLOAK`,
`SECURITY_REALM`, `CORS_*` y `REMOTE_LOGGER_ENABLED`. backend-srv usa solo `SECURITY_URL_KEYCLOAK`
y `SECURITY_REALM` (para validar el JWT); el resto no:

- la autorización es por rol de negocio (`USUARIO.ROL`, en `ActorContexto`), no por el
  `authorization-service`;
- la auditoría es local (`AuditoriaAspect` + `LogAuditoria`, ver [Arquitectura](architecture.md));
- no expone CORS porque solo lo invoca api-gateway.

## Agregar una propiedad que cambia por ambiente

1. Leerla en `application.yml` desde una variable de entorno, con un valor por defecto seguro:
   `mi.propiedad: ${MI_VARIABLE:valor-seguro}`.
2. Si hace falta en local, agregarla a la sección "Arranque rápido" del `README.md` del
   repositorio y al `docker-compose.yml` del equipo.
3. En `backend-srv-config`, agregarla a `configMap.data` de `values.yaml` y, si depende del
   cluster, a cada `envs/values-<cluster>.yaml`. Si es sensible, no va al chart: se documenta
   en el comentario del Secret y el owner la carga en el cluster.
4. Actualizar la tabla de esta página y la del `README.md` de `dgicp-siip2/backend-srv-config`.

## Pendientes

- **DDL del esquema en la entidad.** No hay migraciones (Flyway/Liquibase). Con `validate`,
  las tablas de negocio tienen que existir antes de desplegar, creadas con
  el usuario dueño del esquema. Falta definir cómo se entrega ese DDL al DBA.
- **Sin Route.** El chart tiene `route.enabled: false`: backend-srv no se publica fuera del
  cluster y solo lo invoca api-gateway. Falta una `NetworkPolicy` para que tampoco lo llame otro
  pod del cluster, y que karate-verify pase por api-gateway. Ver el `README.md` de
  `dgicp-siip2/backend-srv-config`.
