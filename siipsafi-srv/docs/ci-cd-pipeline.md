# 8. Despliegue — el ciclo de CI/CD

Qué pasa entre el `push` de un cambio y el pod corriendo, tarea por tarea, y dónde mirar
cuando algo se detiene. El ciclo es de la plataforma; no hay que configurar nada en el
proyecto.

---

## 1. La imagen completa

```text
Gerrit ──webhook──▶ EventListener de la dirección ──▶ PipelineRun (Tekton)
                                                          │
   revisión (refs/for/dev)  ─▶  CR: compila, prueba, Sonar, secretos, CVE  ─▶ vota en Gerrit
   fusión (change-merged)   ─▶  CI: artefacto, imagen, ACS, firma, tag     ─▶ push al repo -config
                                                                                      │
                                                            ArgoCD (ApplicationSet) ◀─┘
                                                                     │
                                                              pod en mh-dev-…
```

Dos repositorios, dos responsabilidades: el de código dispara el ciclo; el `-config` es lo
que ArgoCD despliega. El CI nunca toca el clúster directamente: escribe el tag de la imagen
en el `-config` y ArgoCD hace el resto.

---

## 2. Disparadores

| Evento en Gerrit | Rama | Pipeline | Qué produce |
|---|---|---|---|
| `patchset-created` | `dev` | `cr-java-pipeline` | Votos `Verified` y `Sonar` + comentarios en la revisión |
| `change-merged` | `dev` | `ci-java-pipeline` | Imagen en Quay, tag en el `-config`, despliegue en dev, verificación Karate |
| `patchset-created` | `qa`, `test`, `preprod`, `master` | `cr-branches-java-pipeline` | Voto `Verified` (que la rama esté al día) |
| `change-merged` | `qa`, `test`, `preprod`, `master`, `hotfix` | `ci-<rama>-java-pipeline` | Copia el tag del ambiente anterior al `-config` de esa rama y verifica |

Los disparadores viven en el repositorio `tekton` (los creó el scaffolder). El
`EventListener` es uno por proyecto Tuleap y se ve en la pestaña CI/CD del componente.

---

## 3. Pipeline CR (revisión)

| # | Tarea | Qué hace | Bloquea |
|---|---|---|---|
| 1 | `git-clone` | Trae el patchset | Sí |
| 2 | `validate-branch-gerrit` | Comprueba que el cambio esté al día con la rama | Sí (`Verified -1`) |
| 3 | `dependency-clean-test` | `mvn` con compilación y pruebas unitarias, cache de dependencias compartida | Sí (`Verified -1`) |
| 4 | `sonarqube-scanner` | Análisis y espera del Quality Gate | Publica el veredicto (`PASSED`/`FAILED`) como comentario. El código debe cumplir las reglas SonarQube del Ministerio |
| 5 | `validate-repo-conventions` | Karate presente, `catalog-info.yaml` y `api/openapi.yaml` completos | No; comentario `OK`/`WARN`/`GAP` |
| 6 | `secret-scan` | gitleaks sobre el fuente | No; comentario con hallazgos |
| 7 | `trivy-scan` | CVE en las dependencias del `pom.xml` | No; comentario por severidad |
| 8 | `report-completitud-cve` | Publica el resumen de 5-7 en la revisión | No |
| 9 | `validate-pipeline` | Emite `Verified` y `Sonar` según 1-4 | Es el que vota |

`Verified +1` y `Sonar +1` son requisito de fusión. Los comentarios no bloquean, pero son
hallazgos reales: un secreto versionado o una CVE crítica se corrigen en el mismo cambio.

Un fallo se lee en la pestaña CI/CD del componente → PipelineRun `cr-…` → step en rojo. El
mismo comando corre en local: `./mvnw verify`.

---

## 4. Pipeline CI (fusión en `dev`)

| # | Tarea | Qué hace | Bloquea |
|---|---|---|---|
| 1 | `git-clone` | Trae el commit fusionado | Sí |
| 2 | `build-artifact` | `mvn package` con el espejo del Nexus; produce `target/app.jar` | Sí |
| 3 | `create-version-metadata` | Calcula el tag: `AA.MM.DD-I<6 primeros caracteres del Change-Id>` | Sí |
| 4 | `maven-upload-artifact` | Publica el `.jar` en Nexus, repositorio `DGICP-release` | Sí |
| 5 | `build-image` | Construye con el `Dockerfile` del repositorio y sube a Quay `mh-dgicp-siip2/siipsafi-srv:<tag>` | Sí |
| 6 | `acs-image-check` | Analiza la imagen con Advanced Cluster Security | **Sí si hay CVE sobre el umbral**: la imagen queda en Quay sin firmar ni desplegar. Si ACS no responde, avisa y sigue |
| 7 | `trusted-artifact-signer` | Firma la imagen (RHTAS) | Sí |
| 8 | `update-image-tag` | Escribe `image.tag` en `siipsafi-srv/values.yaml` del `-config`, rama `dev`, y hace push | Sí |
| 9 | `karate-verify` | Espera el despliegue (readiness, hasta 900 s) y corre las pruebas de integración contra el servicio | No; veredicto `OK`/`FAILED`/`UNVERIFIED` en el log |
| 10 | `report-completitud-cve` | Resumen final | No |

Entre 8 y 9 actúa ArgoCD: detecta el commit del bot y sincroniza. El CI no espera a ArgoCD
más que por el health check de la tarea 9.

**Imagen única.** La imagen se construye **una sola vez**, en dev. Las ramas de ambiente no
reconstruyen: su pipeline `ci-<rama>` copia al `-config` de esa rama el tag que tiene el
ambiente anterior (`merge-dev-to-qa-image`, `merge-qa-to-test-image`, …) y verifica con
Karate. Lo que corre en producción es el mismo binario que se probó en dev.

---

## 5. Despliegue continuo (ArgoCD)

| Qué | Valor |
|---|---|
| Objeto | ApplicationSet `<ambiente>-dgicp-siip2-siipsafi-srv` en el ArgoCD del hub |
| Fuente | Repositorio `-config`, rama = ambiente, carpeta `siipsafi-srv/` |
| Values | `values.yaml` + `envs/values-<clúster>.yaml` del clúster destino |
| Destino | Los clústeres que la plataforma asigna al ambiente (Placement); namespace `mh-<ambiente>-dgicp-siip2` |
| Release de Helm | `siipsafi-srv` (fijado en `.argocd-source.yaml`); de ahí salen los nombres de Deployment, Service, Route y ConfigMap |
| Sincronización | Automática, con `selfHeal`: un cambio manual en el clúster se revierte, salvo los datos del Secret |

Se ve en la pestaña ArgoCD del componente o en el ArgoCD del hub filtrando por
`backstage-argocd-selector=dgicp-siip2-siipsafi-srv`.

---

## 6. Cómo seguir un cambio de punta a punta

1. **Gerrit**: la revisión muestra `Verified +1`, `Sonar +1` y los comentarios del CR.
2. **CI/CD** (ficha del componente): PipelineRun `ci-…` en `Succeeded`; en el log de
   `create-version-metadata` está el tag.
3. **Repositorio `-config`**: commit del bot en `dev` con ese tag en `image.tag`.
4. **ArgoCD**: `Synced` / `Healthy` y la revisión = ese commit.
5. **Kubernetes**: pod `Running` con `image: …:<tag>`.
6. **Servicio**: `/actuator/health` en `UP`.

```bash
# el tag desplegado debe ser el del último Change-Id fusionado
oc -n mh-dev-dgicp-siip2 get deploy siipsafi-srv \
   -o jsonpath='{.spec.template.spec.containers[0].image}'
```

Si se fusionaron varios cambios seguidos, corren varios CI en paralelo y cada uno escribe su
tag; el último push gana y puede no ser el del commit más nuevo. Comprobar el tag desplegado
y, si no coincide, editar `image.tag` en el `-config` con el tag correcto.

---

## 7. Si algo falla

| Dónde | Síntoma | Causa habitual | Qué hacer |
|---|---|---|---|
| CR | `Verified -1` en `validate-branch-gerrit` | La rama avanzó | `git rebase origin/dev` y reenviar |
| CR | `Verified -1` en `dependency-clean-test` | Compilación o prueba rota | `./mvnw verify` en local |
| CR | Gate de Sonar `FAILED` | Reglas incumplidas o cobertura baja | Corregir el código; ver el detalle en SonarQube, proyecto `dgicp-siip2-siipsafi-srv` |
| CI | `build-artifact` no resuelve dependencias | Artefacto no está en el Nexus | Verificar el `pom.xml`; el CI usa el mismo espejo que local |
| CI | `acs-image-check` en rojo | CVE en la imagen (dependencia o imagen base) | Actualizar la dependencia; el reporte está en el log |
| CI | `update-image-tag` en rojo | Conflicto en el `-config` | Revisar el último commit del `-config`; reintentar |
| CI | `karate-verify` `UNVERIFIED` | Sin credencial de pruebas, o la espera de readiness venció: el ciclo consulta `/q/health/ready` por defecto y en Spring la readiness es `/actuator/health/readiness` | Ver [testing.md](testing.md) §6; pedir a plataforma el `HEALTH_PATH` del servicio |
| ArgoCD | `OutOfSync` que no se resuelve | Manifiesto inválido en el chart | `Sync` manual para ver el error; `helm template` en local |
| ArgoCD | `Synced` pero pod viejo | Tag pisado por otro CI | §6 |
| Nada corrió | Sin PipelineRun tras el push | El evento no casó con el filtro (rama o proyecto) | Revisar el nombre del proyecto Gerrit y la rama; el log del `EventListener` conserva los eventos recibidos |
