# 4. Configuración de variables

De dónde sale cada valor que el servicio lee al arrancar, dónde se cambia y cómo se
comprueba. El servicio no fija nada en la imagen: todo llega por entorno o por el
config-server.

---

## 1. Las cuatro fuentes, de menor a mayor prioridad

| Fuente | Qué trae | Dónde se edita | Quién lo aplica |
|---|---|---|---|
| `application.yml` | El **nombre** de cada variable (`${DB_URL}`) y los valores con default (URL del marco de dev, CORS, logger remoto) | Repositorio del código | El build |
| ConfigMap `admin-srv-cmp` | Las variables **no sensibles**: URL del marco, Keycloak, `DB_URL`, CORS | `values.yaml` → `configMap.data` del repositorio `-config`, más el overlay `envs/values-<clúster>.yaml` | ArgoCD, al sincronizar |
| Secret `admin-srv-secret` | Las **credenciales**: `DB_USER`, `DB_PASSWORD` | Directamente en el clúster (`oc create secret …`); nunca en git | Tú, una vez por ambiente |
| Config-server (`inventario-service`) | Configuración **de negocio** del servicio: propiedades que quieras cambiar sin redesplegar | Inventario del marco → componente `dgicp-siip-admin-srv` | `ExternalConfigSource`, al arrancar |

El config-server tiene la prioridad más alta: `ExternalConfigSource` es un
`EnvironmentPostProcessor` que añade lo recibido como la primera fuente de propiedades de
Spring. Las variables de entorno del ConfigMap y el Secret llegan al pod por `envFrom`, así
que cualquier clave nueva del ConfigMap queda disponible como `${MI_VARIABLE}` sin tocar el
chart.

En la máquina del desarrollador no hay ConfigMap ni Secret: el perfil `dev` del
`application.yml` trae la base de desarrollo y las credenciales salen de `DB_USER` y
`DB_PASSWORD` del entorno (ver [Ambiente local](local-development.md)).

---

## 2. Las variables del servicio

Tabla tomada del `application.yml`. "CM" = ConfigMap, "S" = Secret.

| Variable | Origen | Obligatoria | Qué es |
|---|---|---|---|
| `HTTP_PORT` | CM | No (`8080`) | Puerto de escucha |
| `CONFIG_SERVICE_URL` | CM | No (default: dev) | URL **base** del config-server, con el path `/api/v1/config/service`. `ExternalConfigSource` le concatena `/` + `service.name` |
| `AUTHZ_SERVICE_URL` | CM | No (default: dev) | `authorization-service`, con `/api/v1/authz` |
| `AUDIT_SERVICE_URL` | CM | No (default: dev) | `auditoria-service`, con `/api/v1/audit` |
| `LOG_SERVICE_URL` | CM | No (default: dev) | `auditoria-service`, con `/api/v1/logs` |
| `SECURITY_URL_KEYCLOAK` | CM | No (default: dev) | **Host** del Keycloak, sin `https://` ni path. Con `SECURITY_REALM` forma el `issuer-uri` y el `jwk-set-uri` |
| `SECURITY_REALM` | CM | No (`MHINTERNO`) | Realm: `MHINTERNO` |
| `DB_URL` | CM | Sí | Cadena JDBC de Oracle |
| `DB_USER`, `DB_PASSWORD` | S | Sí | Credenciales; usar el usuario de pool |
| `CORS_ALLOWED_ORIGINS` | CM | No (`*`) | Orígenes permitidos. El chart lo fija a la Route del servicio |
| `CORS_ALLOWED_METHODS`, `CORS_ALLOWED_HEADERS` | CM | No | Métodos y cabeceras permitidos |

Las tres claves obligatorias no tienen valor por defecto fuera del perfil `dev`: si faltan,
el pod falla al arrancar con `Could not resolve placeholder 'DB_URL'` (o el nombre que
falte).

El chart trae además `LOG_LEVEL` y `REMOTE_LOGGER_ENABLED`, pero el `application.yml` fija
`remote.logger.level: WARNING` y `remote.logger.enabled: true` sin leerlas. Para gobernar el
logger remoto por ambiente, cambiar esos dos valores a `${LOG_LEVEL:WARNING}` y
`${REMOTE_LOGGER_ENABLED:true}` en el `application.yml`.

---

## 3. Cambiar un valor no sensible

1. En el repositorio `admin-srv-config`, rama del ambiente, editar
   `admin-srv/values.yaml` → `configMap.data`.
2. Si el valor depende del clúster (URL del marco, registro de imágenes, `DB_URL`, origen de
   CORS), editarlo además en `envs/values-<clúster>.yaml`: **el overlay gana** al `values.yaml`.
3. Hacer commit y push a la rama. ArgoCD regenera el ConfigMap y reinicia el pod.

```yaml
# values.yaml
configMap:
  data:
    DB_URL: "jdbc:oracle:thin:@<host>:<puerto>/<servicio>"
    MI_VARIABLE_NUEVA: "valor"          # queda disponible como ${MI_VARIABLE_NUEVA}
```

```yaml
# application.yml: declarar el nombre, con default si aplica
mi:
  propiedad: ${MI_VARIABLE_NUEVA:valor-por-defecto}
```

Qué overlay se aplica: el ApplicationSet del ambiente despliega en los clústeres que le
asigna la plataforma y a cada uno le pasa `values.yaml` + `envs/values-<ese clúster>.yaml`.
El nombre del clúster se ve en la Application de ArgoCD (`destination`).

---

## 4. Cambiar una credencial

```bash
oc -n mh-<ambiente>-dgicp-siip create secret generic admin-srv-secret \
  --from-literal=DB_USER='<usuario_pool>' \
  --from-literal=DB_PASSWORD='<password>' \
  --dry-run=client -o yaml | oc apply -f - \
&& oc -n mh-<ambiente>-dgicp-siip rollout restart deploy/admin-srv
```

El reinicio hace falta: `envFrom` se lee al crear el pod. ArgoCD deja el Secret en paz
(`ignoreDifferences` sobre `data`), así que el valor sobrevive a las sincronizaciones.

---

## 5. Configuración de negocio en el config-server

Para propiedades que cambian por ambiente y que no son infraestructura (umbrales, banderas,
URL de sistemas externos), el sitio es el inventario del marco, no el ConfigMap:

1. En `inventario-ui` (o por API en `inventario-service`), abrir el componente
   `dgicp-siip-admin-srv` del ambiente.
2. Añadir la propiedad con el **mismo nombre** que usa el código (`mi.propiedad`).
3. Reiniciar el pod: `ExternalConfigSource` lee el config-server **una vez, al arrancar**.

En el código se consume como cualquier propiedad de Spring:

```java
@Value("${mi.propiedad:valor}")
private String miPropiedad;
```

---

## 6. Cómo comprobar

```bash
NS=mh-<ambiente>-dgicp-siip
oc -n $NS get cm admin-srv-cmp -o yaml | grep -E "SERVICE_URL|SECURITY_|DB_URL|CORS"
oc -n $NS get secret admin-srv-secret -o jsonpath='{.data}' | tr ',' '\n' | cut -d: -f1
oc -n $NS logs deploy/admin-srv | grep -i "config"
```

Lo esperado: las URL del marco con el segmento del **mismo ambiente** (`mh-dev-`, `mh-qa-`,
`mh-test-`, `mh-ppd-`), `DB_URL` del ambiente, las dos claves en el Secret, y en el log la
lectura del config-server sin error.

Prueba rápida de que `CONFIG_SERVICE_URL` está bien armada (404 con JSON, no HTML):

```bash
curl -s -w " [%{http_code}]" "$CONFIG_SERVICE_URL/PRUEBA"
# {"error":"Componente no encontrado"} [404]
```

---

## 7. Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| El config-server responde `404` en el log | El componente no existe en el inventario de ese ambiente con ese `service.name`, o la URL no lleva el path | Registrarlo o promoverlo; añadir `/api/v1/config/service` |
| `401` con token válido | `SECURITY_URL_KEYCLOAK` lleva `https://`, o el realm no coincide con el emisor del token | Poner sólo el host; comparar `iss` |
| El ConfigMap tiene el valor nuevo pero el pod no | El pod no se reinició | `oc rollout restart deploy/…` |
| El valor del `values.yaml` no llega | El overlay `envs/values-<clúster>.yaml` lo pisa | Editar el overlay |
| `Could not resolve placeholder` | Falta una variable obligatoria en CM o Secret | El nombre viene en el mensaje |
