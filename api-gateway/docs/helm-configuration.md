# Configuración de variables

El gateway no tiene valores por defecto para Keycloak: si falta una variable, falla al arrancar
en vez de intentar con un valor inválido. En la entidad las pone el chart `api-gateway-config`
(ConfigMap `api-gateway-cmp` y Secret `api-gateway-secret`); en local, el `docker-compose.yml`
del monorepo a partir de `.env`.

| Variable | Origen en la entidad | Uso |
|---|---|---|
| `HTTP_PORT` | ConfigMap (`8080`) | Puerto HTTP. |
| `BACKEND_SRV_URL` | ConfigMap / overlay | Destino de `/back/**`. |
| `ADMIN_SRV_URL` | ConfigMap | Destino de `/admin/**`. |
| `SIIPSAFI_SRV_URL` | ConfigMap / overlay | Destino de `/siipsafi/**`. |
| `KEYCLOAK_INTERNAL_URL` | ConfigMap / overlay | Base de Keycloak (con esquema): emisor del JWT y endpoint de token. |
| `KEYCLOAK_REALM` | ConfigMap (`MHINTERNO`) | Realm. |
| `KEYCLOAK_CLIENT_ID` | ConfigMap | Cliente confidencial del gateway. |
| `KEYCLOAK_CLIENT_SECRET` | Secret | Secreto de ese cliente. |

El detalle por ambiente y los valores por confirmar están en el README de `api-gateway-config`.
