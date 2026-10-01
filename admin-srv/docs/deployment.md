# 8. Despliegue — operación en OpenShift

Qué hay desplegado por ambiente, cómo se ve y cómo se opera en el día a día. El ciclo que lo
produce está en [ci-cd-pipeline.md](ci-cd-pipeline.md); las variables, en
[helm-configuration.md](helm-configuration.md).

---

## 1. Lo que despliega el chart

| Recurso | Nombre | Notas |
|---|---|---|
| Deployment | `admin-srv` | 1 réplica; imagen `mh-dgicp-siip/admin-srv:<tag>` del Quay institucional |
| Service | `admin-srv` | Puerto 80 → contenedor 8080 |
| Route | — | **No se crea** (`route.enabled: false`): admin-srv no se publica fuera del clúster; solo lo invoca api-gateway por el Service |
| ConfigMap | `admin-srv-cmp` | Variables no sensibles; lo regenera ArgoCD |
| Secret | `admin-srv-secret` | Credenciales; lo llenas tú |
| ServiceAccount | `admin-srv` | |
| HPA | `admin-srv` | Activo, mín. 1 y máx. 1 por defecto (CPU 80 %) |

Recursos del contenedor: `requests` 50m CPU / 256Mi, `limits` 500m / 512Mi. Probes de
Actuator: `readiness` en `/actuator/health/readiness` y `liveness` en `/actuator/health/liveness`. El contenedor corre sin privilegios, sin escalada y sin capacidades.

Para cambiar réplicas, recursos o el HPA, editar `values.yaml` en la rama del ambiente del
repositorio `-config`; ArgoCD aplica el cambio.

---

## 2. Dónde ver el servicio

| Qué | Dónde |
|---|---|
| Todo en una vista | Developer Hub → catálogo → `dgicp-siip-admin-srv` (pestañas CI/CD, ArgoCD, Kubernetes, Docs, API) |
| Pods, logs, eventos | Consola de OpenShift del clúster → proyecto `mh-<ambiente>-dgicp-siip` |
| Estado del despliegue | ArgoCD del hub → `<ambiente>-dgicp-siip-admin-srv` |
| Imágenes y tags | Quay institucional → `mh-dgicp-siip/admin-srv` |
| Calidad | SonarQube → `dgicp-siip-admin-srv` |
| Auditoría y logs remotos | `auditoria-ui` del ambiente, filtrando por el `service.name` |
| Contrato | Developer Hub → pestaña API (`api/openapi.yaml`); Swagger UI a través de api-gateway (`/admin/v3/api-docs`) |

---

## 3. Operar con `oc`

```bash
NS=mh-<ambiente>-dgicp-siip
APP=admin-srv

oc -n $NS get pods -l app.kubernetes.io/name=$APP          # estado
oc -n $NS logs -f deploy/$APP                               # logs
oc -n $NS logs deploy/$APP --previous                       # logs del pod anterior (CrashLoop)
oc -n $NS rollout restart deploy/$APP                       # reiniciar (tras cambiar el Secret)
oc -n $NS describe pod -l app.kubernetes.io/name=$APP       # eventos, probes, imagen
```

Health (sin Route, desde dentro del clúster):

```bash
oc -n $NS port-forward svc/$APP 8080:80 &
curl -s http://localhost:8080/actuator/health | jq .
```

No editar Deployment, ConfigMap ni Service a mano: ArgoCD los revierte en la siguiente
sincronización. Lo único que se edita en el clúster es el Secret.

---

## 4. Ambientes

| Ambiente | Rama | Namespace | Marco al que apunta |
|---|---|---|---|
| Desarrollo | `dev` | `mh-dev-dgicp-siip` | `mh-dev-dinafi-usi-frmk` |
| QA | `qa` | `mh-qa-dgicp-siip` | `mh-qa-dinafi-usi-devhub` |
| Test | `test` | `mh-test-dgicp-siip` | `mh-test-dinafi-usi-devhub` |
| Preproducción | `preprod` | `mh-preprod-dgicp-siip` | `mh-ppd-dinafi-usi-devhub` |
| Producción | `master` | `mh-master-dgicp-siip` | `mh-prod-dinafi-usi-devhub` |

Cada ambiente se crea desde el portal ([ambientes.md](ambientes.md)) y recibe versiones por
promoción ([promocion.md](promocion.md)). Keycloak es por grupo: `dev` y `qa` comparten
uno; `test` y `preprod` otro; producción el suyo.

---

## 5. Si algo falla

| Síntoma | Dónde mirar | Causa habitual |
|---|---|---|
| `ImagePullBackOff` | `describe pod` | Tag inexistente (aún no hay CI) o `quay-credentials` ausente en el namespace |
| `CrashLoopBackOff` | `logs --previous` | Variable obligatoria sin valor; Secret vacío; base de datos inalcanzable |
| Pod `Running` pero api-gateway responde `503` en `/admin/**` | `readiness` | El servicio no llega a `ready`: revisar `/actuator/health/readiness` |
| Arranque lento y reinicios | `describe pod` (startup probe) | Spring tarda más en arrancar que Quarkus; si la liveness lo mata antes de estar listo, subir `livenessProbe.initialDelaySeconds` en `values.yaml` |
| Aparece una Route `admin-srv` | `oc get route` | Alguien habilitó `route.enabled` en un overlay: debe quedar en `false` |
| El cambio de `values.yaml` no se aplica | ArgoCD | `OutOfSync` con error de render, o el overlay del clúster pisa el valor |
