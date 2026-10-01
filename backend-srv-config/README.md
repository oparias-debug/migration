# backend-srv-config

Configuración de despliegue de **backend-srv** (`dgicp-siip2-backend-srv`) para los ambientes
de la entidad: chart Helm que ArgoCD aplica en cada cluster de OpenShift.

El código vive en el repo `dgicp-siip2/backend-srv`. Aquí vive **todo lo que distingue un
ambiente de otro**: URL de base de datos, esquemas, niveles de log, réplicas, recursos, imagen.
La imagen es la misma en todos los ambientes; lo que cambia es este repo.

## Estructura

```
backend-srv/
├── Chart.yaml                 # chart dgicp-siip2-backend-srv
├── .argocd-source.yaml        # fija el release name de Helm a "backend-srv"
├── values.yaml                # valores base = ambiente DEV
├── envs/
│   ├── values-gcp-op-desa.yaml     # dev  (gcp-op-desa)
│   ├── values-ocp-dev-dga.yaml     # dev  (ocp-dev-dga)
│   ├── values-ocp-test-3t.yaml     # tst  (ocp-test-3t)
│   ├── values-ocp-test-dga.yaml    # tst  (ocp-test-dga)
│   └── values-gcp-op-preprod.yaml  # ppd  (gcp-op-preprod)
└── templates/                 # Deployment, Service, Route, ConfigMap, Secret, HPA...
```

## Cómo se arma la configuración de un ambiente

1. **La rama es el ambiente.** Cada rama de este repo corresponde a un ambiente (`dev`, y las
   que se creen para pruebas/preproducción/producción). La promoción entre ramas
   (`merge-*-image`) solo sincroniza `image.tag` e `image.repository`: el resto de los valores
   de cada rama **no se pisa**.
2. **Overlay por cluster.** El ApplicationSet (`05-cd-multicluster`) renderiza
   `helm -f values.yaml -f envs/values-<cluster>.yaml`. El overlay sobreescribe solo lo
   acoplado al cluster (registry, dominios, base de datos, esquemas, URL del gateway).
3. **ConfigMap `backend-srv-cmp`** (`configMap.data`): variables **no sensibles**. Se inyectan
   al contenedor con `envFrom`.
4. **Secret `backend-srv-secret`**: variables **sensibles**. El chart lo crea **vacío** a
   propósito (no hay secretos en git); el owner del proyecto carga las claves directamente en
   el cluster (`oc edit secret backend-srv-secret` o consola). ArgoCD ignora su `/data`, así
   que no revierte esas ediciones.

## Variables que lee backend-srv

Es el contrato con `src/main/resources/application.yml` del repo `dgicp-siip2/backend-srv`
(detalle en su página de TechDocs "Configuración y ambientes"). Si la app empieza a leer
una variable nueva, se agrega aquí (en `values.yaml` y, si depende del cluster, en cada overlay).

| Variable | Dónde | Valor en la entidad | Para qué |
|---|---|---|---|
| `DB_USER` | Secret | usuario `*_POOL` | Usuario de conexión (no el dueño del esquema). |
| `DB_PASSWORD` | Secret | — | Contraseña de `DB_USER`. |
| `DB_URL` | ConfigMap / overlay | `jdbc:oracle:thin:@<host>:<puerto>/<servicio>` | JDBC de la base de negocio. |
| `DB_DRIVER_CLASS_NAME` | ConfigMap | `oracle.jdbc.OracleDriver` | Driver JDBC. |
| `DB_SCHEMA` | ConfigMap / overlay | esquema dueño de las tablas | `hibernate.default_schema`. |
| `JPA_DDL_AUTO` | ConfigMap | `validate` | La app no crea ni borra tablas. |
| `JPA_SHOW_SQL` | ConfigMap | `false` | Log de SQL. |
| `FLOWABLE_DB_SCHEMA` | ConfigMap / overlay | esquema de Flowable | Esquema propio del motor de procesos. |
| `FLOWABLE_DB_SCHEMA_UPDATE` | ConfigMap | `false` | Flowable no crea ni actualiza sus tablas. |
| `GATEWAY_URL` | ConfigMap / overlay | URL pública de api-gateway | Solo para los `servers` del OpenAPI (Swagger UI). |
| `HTTP_PORT` | ConfigMap | `8080` | Puerto HTTP; debe coincidir con `containerPort`. |
| `SPRING_PROFILES_ACTIVE` | ConfigMap | `prod` | **Nunca `dev`**: activa los datos de prueba y recrea el esquema. |
| `LOG_LEVEL` | ConfigMap | `WARN` | Nivel raíz de log (`TRACE`/`DEBUG`/`INFO`/`WARN`/`ERROR`). |

Las demás claves de `configMap.data` (`CONFIG_SERVICE_URL`, `AUTHZ_SERVICE_URL`,
`AUDIT_SERVICE_URL`, `LOG_SERVICE_URL`, `SECURITY_*`, `CORS_*`, `REMOTE_LOGGER_ENABLED`) vienen
de la plantilla del marco DINAFI. **backend-srv no las lee hoy**: la autenticación la hace
api-gateway, la auditoría es local y no expone CORS. Se conservan sin efecto para que los
overlays generados por el scaffolder sigan aplicando.

`verificacion.*` no es configuración de la aplicación: la lee el ciclo (task `karate-verify`).

## Crear o ajustar un ambiente

1. Completar los valores marcados `<...>` del overlay del cluster (`DB_URL`, `DB_SCHEMA`,
   `FLOWABLE_DB_SCHEMA`, `GATEWAY_URL`). Mientras quede un `<...>`, el pod no arranca o el
   Swagger apunta mal: es intencional, para que el faltante sea evidente.
2. Pedir al DBA los esquemas de negocio y de Flowable creados con el usuario dueño, y los
   grants hacia el usuario `*_POOL` (ver "Pendientes").
3. Cargar `DB_USER` y `DB_PASSWORD` en el Secret `backend-srv-secret` del namespace.
4. Revisar el arranque: `oc logs deploy/backend-srv` y `/actuator/health/readiness`.

Para probar el render localmente:

```bash
helm template backend-srv ./backend-srv -f backend-srv/values.yaml -f backend-srv/envs/values-ocp-test-3t.yaml
```

## Contribuir

Igual que el código: por revisión en Gerrit sobre la rama del ambiente
(`git push origin HEAD:refs/for/dev`).

## Pendientes / por confirmar

- **Esquema de base de datos.** backend-srv no tiene migraciones (Flyway/Liquibase). Con
  `JPA_DDL_AUTO=validate` y `FLOWABLE_DB_SCHEMA_UPDATE=false`, alguien tiene que crear las
  tablas antes del primer despliegue, con el usuario dueño del esquema. Falta definir quién y
  cómo (scripts DDL entregados al DBA o una herramienta de migraciones).
- **Flowable en Oracle.** Su esquema separado debe provisionarlo el DBA como otro
  usuario/esquema, con grants cruzados hacia el `*_POOL`.
- **Sin Route.** `route.enabled: false` por decisión de seguridad: backend-srv no se publica
  fuera del cluster y solo lo invoca api-gateway, por el Service (`ClusterIP`). Además valida el
  JWT por su cuenta (`SECURITY_URL_KEYCLOAK` y `SECURITY_REALM` de este ConfigMap).
  `verificacion.baseUrl` queda vacía, así que karate-verify no verifica nada (lo dice en el log)
  hasta que api-gateway esté desplegado en la entidad y la verificación pase por él.
- **Solo api-gateway, también dentro del cluster.** Sin Route nadie llega desde afuera, pero
  otro pod del cluster sí puede llamar al Service. Para que el único cliente sea api-gateway
  falta una `NetworkPolicy` que solo admita su tráfico; el chart no trae plantilla y depende de
  dónde se despliegue api-gateway (namespace y labels).
- **Recursos.** `limits.memory: 512Mi` y la `startupProbe` (~65 s) son los de la plantilla.
  Spring Boot + Hibernate + Flowable puede superar ambos; validar en el primer despliegue.
- **URLs `<...>`** de los overlays: confirmar con infra.
