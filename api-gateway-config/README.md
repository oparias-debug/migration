# api-gateway-config

Chart Helm de `api-gateway` (`dgicp-siip2-api-gateway`) para los clusters de la entidad. Es el
repositorio `-config` de la plantilla DINAFI: ArgoCD lo despliega y la rama es el ambiente.
Se derivó de `admin-srv-config` (mismos templates, `admin-srv` → `api-gateway`); lo propio del
gateway está en `values.yaml` y en `envs/`.

> Todavía no existe el repositorio Gerrit `dgicp-siip2/api-gateway-config` (P-06). Mientras
> tanto este directorio vive solo en el monorepo `siip`.

## Estructura

```
api-gateway/
├── Chart.yaml              dgicp-siip2-api-gateway
├── .argocd-source.yaml     releaseName fijo: api-gateway
├── values.yaml             valores base (ambiente dev)
├── envs/values-<cluster>.yaml   overlay por cluster: imagen, Keycloak y URLs de los servicios
└── templates/              los de admin-srv-config, sin cambios salvo el nombre
```

## En qué se diferencia de los -config de los servicios

- **Route habilitada.** El gateway es el único punto de entrada público de SIIP. backend-srv,
  admin-srv y siipsafi-srv no tienen Route: solo los invoca el gateway por su Service.
- **Sin base de datos ni servicios del marco.** No lleva `DB_*`, `CONFIG_SERVICE_URL`,
  `AUTHZ_SERVICE_URL`, `AUDIT_SERVICE_URL` ni `LOG_SERVICE_URL`: el gateway no los usa.
- **Una URL entre namespaces.** El gateway está en el proyecto `dgicp-siip2`, como backend-srv,
  siipsafi-srv y frontend-ui: a esos dos los alcanza por el nombre corto del Service. admin-srv
  vive en `mh-<env>-dgicp-siip`, por eso su URL lleva `<service>.<namespace>.svc`.

## Variables que lee api-gateway

| Variable | Dónde | Valor | Uso |
|---|---|---|---|
| `HTTP_PORT` | ConfigMap | `8080` | Puerto HTTP; debe coincidir con `containerPort`. |
| `BACKEND_SRV_URL` | ConfigMap | `http://backend-srv` | Destino de `/back/**`. |
| `ADMIN_SRV_URL` | ConfigMap / overlay | `http://admin-srv.mh-<env>-dgicp-siip.svc` | Destino de `/admin/**`. |
| `SIIPSAFI_SRV_URL` | ConfigMap | `http://siipsafi-srv` | Destino de `/siipsafi/**`. |
| `KEYCLOAK_INTERNAL_URL` | ConfigMap / overlay | `https://keycloak-mh-<dev\|test>...` | Emisor de los JWT y endpoint de token del login. |
| `KEYCLOAK_REALM` | ConfigMap | `MHINTERNO` | Realm; el mismo que validan los servicios. |
| `KEYCLOAK_CLIENT_ID` | ConfigMap | `<cliente-api-gateway>` | Cliente confidencial del gateway en el realm. |
| `KEYCLOAK_CLIENT_SECRET` | Secret | — | Secreto de `KEYCLOAK_CLIENT_ID`. |

`verificacion.*` no es configuración de la aplicación: la lee el ciclo (task `karate-verify`).

Los probes del Deployment usan `/actuator/health/liveness` y `/actuator/health/readiness`; el
gateway los expone sin token (solo `health`, sin detalles).

## Crear o ajustar un ambiente

1. Completar los valores marcados `<...>` (`KEYCLOAK_CLIENT_ID`). Mientras quede un `<...>`, el
   login falla: es intencional, para que el faltante sea evidente.
2. Cargar `KEYCLOAK_CLIENT_SECRET` en el Secret `api-gateway-secret` del namespace.
3. Revisar el arranque: `oc logs deploy/api-gateway` y `/actuator/health/readiness` por la Route.
4. Con la Route confirmada, completar `verificacion.baseUrl` y el `GATEWAY_URL` de
   `backend-srv-config` (hoy `<url-publica-api-gateway-dev>`).

## Pendientes / por confirmar

- **Proyecto del gateway (P-06).** `dgicp-siip2`, según la decisión de separar backend, front y
  api-gateway en repos Gerrit de ese proyecto. Si infra lo ubica en otro, cambian las tres URLs.
- **Cliente del gateway en MHINTERNO (P-07).** En local es `api-gateway`
  (`keycloak/realm-export.json`). El gateway también registra el cliente `swagger-ui` para el
  login de Swagger UI: hay que pedir ambos o decidir cuál se usa.
- **Tráfico entre namespaces (P-04).** La NetworkPolicy de admin-srv debe admitir los pods de
  este gateway, que está en otro namespace (`dgicp-siip2`).
- **Dominio de los clusters `ocp-*`.** Los overlays toman el Keycloak que usa admin-srv en cada
  cluster; los de admin-srv también dicen "CONFIRMAR con infra".
