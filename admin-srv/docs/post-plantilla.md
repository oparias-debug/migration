# 3. Después de la plantilla

El scaffolder deja el servicio registrado, versionado y con su despliegue definido. Lo que
**no** puede hacer por ti es poner credenciales. Esta página cubre lo que hay que hacer una
sola vez, antes del primer cambio de código, para ver el pod arriba.

---

## 1. Lo que ya está hecho

| Qué | Dónde | Estado al terminar la plantilla |
|---|---|---|
| Código del servicio | Gerrit `dgicp-siip/admin-srv`, rama `dev` | Compila, arranca y trae tres endpoints de ejemplo |
| Chart de despliegue | Gerrit `dgicp-siip/admin-srv-config`, rama `dev`, carpeta `admin-srv/` | `values.yaml` con las URL del marco de dev, más un overlay por clúster en `envs/` |
| Despliegue | ArgoCD, ApplicationSet `dev-dgicp-siip-admin-srv` | Sincroniza el chart al namespace `mh-dev-dgicp-siip` |
| ConfigMap `admin-srv-cmp` | Namespace de dev | Lo genera el chart con todas las variables no sensibles |
| Secret `admin-srv-secret` | Namespace de dev | Lo genera el chart **vacío**; ArgoCD no lo sobrescribe |
| Cliente OIDC | Keycloak, realm `MHINTERNO`, cliente `dgicp-siip` | Creado con la Route de dev como `redirect_uri`. Este servicio sólo valida tokens: no usa el secreto del cliente |
| Pull de la imagen | Secret `quay-credentials` en el namespace | Lo pone la plataforma por política; no hay que crearlo |
| CI | `EventListener` del proyecto en el ciclo de la dirección; webhook en Gerrit | Escucha `patchset-created` y `change-merged` en `dev` |

Hasta que el CI construya una imagen, el chart apunta al tag `latest`, que no existe.
**El primer despliegue real ocurre al fusionar el primer cambio**, no al terminar la
plantilla. Es normal ver el pod en `ImagePullBackOff` mientras tanto.

---

## 2. Lo que hay que hacer una vez

### 2.1 Llenar el Secret del servicio

El chart crea `admin-srv-secret` sin datos. El servicio no arranca sin
estas dos claves (el `application.yml` no les da valor por defecto fuera del perfil `dev`):

| Clave | Valor | De dónde sale |
|---|---|---|
| `DB_USER` | Usuario de base de datos. Usar el usuario de **pool** (`*_POOL`), no el dueño del esquema | Lo entrega el DBA |
| `DB_PASSWORD` | Su contraseña | Lo entrega el DBA |

```bash
oc -n mh-dev-dgicp-siip create secret generic admin-srv-secret \
  --from-literal=DB_USER='<usuario_pool>' \
  --from-literal=DB_PASSWORD='<password>' \
  --dry-run=client -o yaml | oc apply -f -
```

El Secret vive **solo en el clúster**. No se versiona en el repositorio `-config`; el chart
declara `ignoreDifferences` sobre sus datos para que ArgoCD no lo vacíe al sincronizar.

### 2.2 Verificar la conexión a base de datos

`DB_URL` viene en el ConfigMap con la base de desarrollo. Si el servicio usa otra instancia
o esquema, se cambia en `values.yaml` → `configMap.data.DB_URL` del repositorio `-config`
(ver [Configuración de variables](helm-configuration.md)).

### 2.3 Enviar el primer cambio

Cualquier cambio fusionado en `dev` construye la imagen y la despliega. El más sencillo es
completar la sección "Responsabilidades" de `docs/index.md`:

```bash
git checkout dev
# editar docs/index.md
git commit -am "Descripción inicial del servicio"
git push origin HEAD:refs/for/dev
```

Aprobar y fusionar en Gerrit. Unos minutos después el ciclo de CI publica la imagen, actualiza
el tag en el repositorio `-config` y ArgoCD despliega.

---

## 3. Cómo comprobar que quedó bien

| Qué mirar | Dónde | Lo esperado |
|---|---|---|
| PipelineRun `ci-…` | Ficha del componente → CI/CD | `Succeeded` |
| Tag de imagen | Commit del bot en `admin-srv-config`, rama `dev` | `image.tag` con forma `AA.MM.DD-I<6 caracteres del Change-Id>` |
| ArgoCD | Ficha → ArgoCD | `Synced` y `Healthy` |
| Pod | Ficha → Kubernetes | `Running`, 1/1 |
| Health | `https://admin-srv-mh-dev-dgicp-siip.apps.<dominio>/actuator/health` | `"status": "UP"` |
| Endpoint público | `…/api/v1/demo/security/publico` | `200` con JSON |
| Endpoint autenticado | `…/api/v1/demo/security/autenticado` con `Authorization: Bearer <JWT>` | `200` con los claims; sin token `401` |

---

## 4. Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| Pod en `ImagePullBackOff` y ningún PipelineRun | Todavía no se fusionó ningún cambio | Enviar el primer cambio (§2.3) |
| Pod en `CrashLoopBackOff` con `Could not resolve placeholder 'DB_USER'` | El Secret está vacío | §2.1 |
| Pod arriba pero `ORA-01017` en los logs | Credenciales incorrectas | Revisar `DB_USER`/`DB_PASSWORD`; usar el usuario de pool |
| El PipelineRun falla en `build-artifact` | Error de compilación o pruebas | Ver el log del step; reproducir con `./mvnw verify` en local |
| `401` en el endpoint autenticado con un token válido | El token es de otro realm, o `SECURITY_URL_KEYCLOAK`/`SECURITY_REALM` no coinciden con el emisor | Comparar el `iss` del token con el ConfigMap |
| Health `DOWN` | Base de datos inalcanzable (el check de datasource forma parte del health) | Revisar `DB_URL` y las credenciales |

---

## 5. A quién pedir cada cosa

| Necesito… | Lo entrega | Lo hago yo |
|---|---|---|
| Usuario de pool y contraseña de base de datos del ambiente | DBA | Cargarlos en el Secret del namespace |
| Usuario de pruebas con los grupos de negocio | Administración de identidad, a solicitud del líder técnico | Probar el login y los permisos |
| Recursos, permisos y menú en `autorizacion-ui` | Líder técnico de la dirección | Entregarle la lista de `path` y operaciones que exige el código |
| Credencial de las pruebas automáticas (`karate-auth`) | Plataforma, a solicitud del líder técnico | Nada |
| Acceso al namespace (`oc`), al Developer Hub, a Gerrit o a Tuleap | Plataforma, a solicitud del líder técnico | Comprobarlo |
| Corrección durable de un overlay que la promoción vuelve a pisar | Plataforma | Reportar el par clave/valor |

La matriz completa por ambiente y rol está en la guía del desarrollador del marco (Documento 15).
