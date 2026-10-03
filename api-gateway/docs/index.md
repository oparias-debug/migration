# dgicp-siip2-api-gateway

API Gateway de SIIP: el único punto de entrada público. Recibe las llamadas del front, valida el
JWT emitido por Keycloak y las enruta a backend-srv, admin-srv y siipsafi-srv, relevando el mismo
token para que cada servicio haga su propia autorización.

| Componente | Repositorio Gerrit | Configuración |
|---|---|---|
| api-gateway | `dgicp-siip2/api-gateway` (por crear, P-05) | `dgicp-siip2/api-gateway-config` |

## Contenido

- [Arquitectura](architecture.md)
- [Configuración de variables](helm-configuration.md)
- [Ambiente local](local-development.md)
- [Despliegue](deployment.md)
