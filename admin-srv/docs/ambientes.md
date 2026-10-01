# 9. Creación de un ambiente

Cómo nace `qa`, `test`, `preprod` o `master` para este servicio, y lo que hay que hacer una
vez en cada uno. Se hace desde el portal, con la misma plantilla que después sirve para
[llevar versiones nuevas](promocion.md).

---

## 1. El modelo

| Ambiente | Rama | Namespace | Lo anterior en la cadena |
|---|---|---|---|
| `dev` | `dev` | `mh-dev-dgicp-siip` | Lo crea la plantilla de creación |
| `qa` | `qa` | `mh-qa-dgicp-siip` | `dev` |
| `test` | `test` | `mh-test-dgicp-siip` | `qa` |
| `preprod` | `preprod` | `mh-preprod-dgicp-siip` | `test` |
| `master` (producción) | `master` | `mh-master-dgicp-siip` | `preprod` |

Reglas que aplica la plataforma:

- **La cadena es fija**: `dev → qa → test → preprod → master`. Un salto que no sea al
  siguiente eslabón se rechaza en el primer paso de la plantilla. Excepción: el hotfix
  `master → preprod`.
- **Rama = ambiente** en los dos repositorios (código y `-config`). El chart de cada
  ambiente vive en su rama.
- **Keycloak por grupo**: `dev` y `qa` usan el mismo Keycloak; `test` y `preprod` otro;
  producción el suyo. El cliente OIDC se crea en el que corresponde.
- **Clústeres por ambiente**: cada ambiente despliega en los clústeres que la plataforma le
  asigna; la Application de ArgoCD muestra en cuáles.

---

## 2. Crear el ambiente desde el portal

Developer Hub → **Create** → **Quarkus CI Template** (plantilla de promoción).

| Campo | Qué poner |
|---|---|
| Dirección | `dgicp` |
| Rama de origen | El ambiente anterior en la cadena (`dev` para crear `qa`) |
| Rama de destino | El ambiente que quieres crear |
| Proyecto en Tuleap y repositorio de Gerrit | Los mismos de la creación (ver [creacion-aplicacion.md](creacion-aplicacion.md)) |
| Realm de Keycloak | `MHINTERNO` |
| Repositorio de Nexus | `DGICP-release` |

Lo que hace la plantilla, en orden:

1. Valida que el salto respete la cadena.
2. Lee la configuración del ambiente destino (Keycloak, inventario, dominio).
3. Clona los dos repositorios y **fusiona la rama de origen en la de destino**; si la rama
   de destino no existe, la crea. Regenera los overlays `envs/` del chart para los clústeres
   del destino.
4. Crea el ApplicationSet `<destino>-dgicp-siip-admin-srv` en ArgoCD y lo versiona.
5. Crea el cliente OIDC `dgicp-siip` en el Keycloak del grupo del
   destino, con la Route del destino como `redirect_uri`.
6. Copia la configuración del componente en el inventario del origen al inventario del
   destino.

ArgoCD despliega en cuanto existe el ApplicationSet: el chart de la rama nueva trae el
`image.tag` que tenía el origen en ese momento, así que **lo que aparece en el ambiente
nuevo es exactamente la versión que estaba en el anterior**.

---

## 3. Lo que hay que hacer una vez en el ambiente nuevo

### 3.1 Llenar el Secret

Igual que en dev, el chart crea `admin-srv-secret` vacío en el
namespace nuevo. Las credenciales son las **de la base de datos del ambiente** (qa, test, preprod). Este servicio valida los JWT contra Keycloak sin secreto de cliente.

```bash
oc -n mh-<destino>-dgicp-siip create secret generic admin-srv-secret \
  --from-literal=DB_USER='<usuario_pool>' \
  --from-literal=DB_PASSWORD='<password>' \
  --dry-run=client -o yaml | oc apply -f -
```

Si el ambiente despliega en más de un clúster, el Secret va en cada uno.

### 3.2 Revisar el ConfigMap resultante

La promoción escribe los overlays por clúster, pero los valores que dependen del ambiente
hay que **comprobarlos en el clúster destino** antes de dar por bueno el ambiente:

```bash
oc -n mh-<destino>-dgicp-siip get cm admin-srv-cmp -o yaml \
  | grep -E "SERVICE_URL|SECURITY_|DB_URL|CORS_ALLOWED"
```

| Clave | Debe apuntar a | Señal de que está mal |
|---|---|---|
| `CONFIG_SERVICE_URL`, `AUTHZ_SERVICE_URL`, `AUDIT_SERVICE_URL`, `LOG_SERVICE_URL` | El marco del ambiente (`mh-qa-…`, `mh-test-…`, `mh-ppd-…`) | Un segmento `mh-dev-` |
| `SECURITY_URL_KEYCLOAK`, `SECURITY_REALM` | El host del Keycloak del grupo del destino y el realm `MHINTERNO` | El Keycloak de dev en `test`/`preprod` |
| `DB_URL` | La base de datos del ambiente | `desadevhpdb` o un `<host-bd-…>` literal |
| `CORS_ALLOWED_ORIGINS` | La Route de este ambiente | Un `${{` sin sustituir |

Lo que no coincida se corrige en el repositorio `-config`, **rama del destino**, archivo
`admin-srv/envs/values-<clúster>.yaml` (el overlay gana al
`values.yaml`), y ArgoCD lo aplica. Una promoción posterior regenera los overlays: si el
valor corregido vuelve a salir mal, reportarlo al equipo de plataforma con el par
clave/valor.

### 3.3 Revisar la configuración del inventario

La promoción copia el componente del inventario tal cual. Abrir en `inventario-ui` del
destino el componente `dgicp-siip-admin-srv` y
ajustar las propiedades que cambian por ambiente (URLs de sistemas externos, umbrales).

### 3.4 Sembrar los permisos

Los permisos que exige `@PermissionsAllowed` viven en el `authorization-service` **de cada
ambiente**. Registrarlos en `autorizacion-ui` del destino, o llevarlos con la promoción de
configuración del autorizador ([security.md](security.md) §2).

---

## 4. Cómo comprobar que el ambiente quedó bien

| Qué | Dónde | Lo esperado |
|---|---|---|
| Rama | Gerrit, ambos repositorios | Existe la rama `<destino>` con el contenido del origen |
| ArgoCD | Hub, `<destino>-dgicp-siip-admin-srv` | `Synced` y `Healthy` en cada clúster del ambiente |
| Pod | Namespace del destino | `Running`, imagen con el mismo tag que el origen |
| Route | `oc get route` | Ninguna: admin-srv no se publica (`route.enabled: false`) |
| Health | `oc port-forward svc/admin-srv 8080:80` y `/actuator/health` | `UP` |
| Login | Endpoint autenticado con un token del Keycloak del destino | `200` |
| Config-server | Log del pod | `Configuración cargada exitosamente desde config server` |
| Pruebas | Rama `pruebas-funcional-<destino>` ([testing.md](testing.md) §6) | Veredicto `OK` |

---

## 5. Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| La plantilla se detiene en el primer paso | Salto fuera de la cadena | Promover al siguiente eslabón |
| La plantilla termina pero no hay rama de destino | El merge no pudo subir (conflicto entre origen y destino) | Resolver el conflicto en el repositorio y relanzar |
| ArgoCD `Synced` pero el pod no alcanza el marco | `*_SERVICE_URL` con `mh-dev-` | §3.2 |
| `ORA-01017` o `ORA-12514` | Secret con credenciales de otro ambiente, o `DB_URL` de dev | §3.1 y §3.2 |
| Login `401` con usuario válido | `SECURITY_URL_KEYCLOAK`/`SECURITY_REALM` de otro grupo de Keycloak | §3.2 |
| Route `HostAlreadyClaimed` | El overlay trae el host de otro ambiente | Corregir `route.host` (vacío) en el overlay |
| `403` en endpoints con permisos | Permisos no sembrados en el autorizador del destino | §3.4 |
| Relanzar la plantilla no corrige el ApplicationSet o el cliente OIDC | Existen y la plantilla no los actualiza | Borrar el ApplicationSet (y el AppProject) en el hub y relanzar |
