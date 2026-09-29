# 6. Desarrollo — flujo de trabajo

Cómo entra un cambio al servicio y qué revisiones automáticas va a encontrar. Las guías de
código (capas, contrato de errores, versionamiento, migraciones, integraciones) están en las
demás páginas de esta sección.

---

## 1. La regla

**El código entra siempre por `dev`, mediante una revisión en Gerrit.** Las ramas `qa`,
`test`, `preprod` y `master` sólo reciben promociones desde el portal (ver
[Paso entre ambientes](promocion.md)). No se hace `push` directo a ninguna rama de
ambiente.

---

## 2. Preparar el repositorio (una vez)

```bash
git clone "ssh://<usuario>@almsrv2.mh.gob.sv:29419/dgicp-siip/admin-srv"
cd admin-srv
scp -p -O -P 29419 <usuario>@almsrv2.mh.gob.sv:hooks/commit-msg .git/hooks/
chmod +x .git/hooks/commit-msg
```

El hook `commit-msg` añade el `Change-Id` que Gerrit exige en cada commit. Sin él el `push`
a `refs/for/dev` se rechaza.

---

## 3. El ciclo de un cambio

```bash
git checkout dev && git pull
# ... editar, probar en local (local-development.md) ...
./mvnw verify
git add . && git commit -m "Descripción concisa del cambio"
git push origin HEAD:refs/for/dev
```

1. El `push` abre una revisión en Gerrit y dispara la pipeline **CR** (revisión de código).
2. La pipeline vota `Verified` y `Sonar` en la revisión y deja comentarios con lo que encontró.
3. Una persona revisa y aprueba (`Code-Review +2`); el CI **nunca** fusiona por sí solo.
4. Al fusionar, se dispara la pipeline **CI**, que construye la imagen, la analiza, la firma y la despliega en dev.

Si la revisión pide cambios, se corrige el mismo commit (`git commit --amend`, conservando el
`Change-Id`) y se vuelve a enviar a `refs/for/dev`. Un `Change-Id` nuevo abre otra revisión.

---

## 4. Lo que revisa la pipeline CR antes de que alguien lea el código

| Tarea | Qué comprueba | Si falla |
|---|---|---|
| `validate-branch-gerrit` | Que el cambio esté al día con `dev` | `Verified -1`. Hacer `git rebase origin/dev` y reenviar |
| `dependency-clean-test` | Compila y corre las pruebas unitarias con Maven | `Verified -1`. Reproducir con `./mvnw verify` |
| `sonarqube-scanner` | Análisis estático y Quality Gate del proyecto | Se publica el veredicto del gate en la revisión. El proyecto **debe** cumplir las reglas de SonarQube del Ministerio; un gate en rojo se corrige en el código, no se marca como aceptado |
| `validate-repo-conventions` | Que existan las pruebas Karate, que `catalog-info.yaml` y `api/openapi.yaml` estén completos | Comentario `OK`, `WARN` o `GAP` en la revisión |
| `secret-scan` | Credenciales versionadas (gitleaks) | Comentario con el hallazgo. Retirar el secreto **y** reescribir el commit |
| `trivy-scan` | Vulnerabilidades conocidas en las dependencias del `pom.xml` | Comentario con las CVE por severidad |
| `report-completitud-cve` | Publica el resumen de completitud y CVE como comentario | Siempre informa; nunca bloquea |

Regla práctica: `Verified +1` y `Sonar +1` son requisito para fusionar; los comentarios son
deuda que conviene cerrar en el mismo cambio.

---

## 5. Dónde va cada cosa en el código

| Quiero… | Dónde | Guía |
|---|---|---|
| Exponer un endpoint | `sv.gob.mh.api.controller` + DTO en `api.dto.request` / `api.dto.response` | [layer-api.md](layer-api.md) |
| Un caso de uso | `application.command` / `handler` / `query` | [layer-application.md](layer-application.md) |
| Un modelo o una regla de negocio | `domain.model` (POJO, sin JPA) / `domain.service`; el repositorio como interfaz en `domain.repository` | [layer-domain.md](layer-domain.md) |
| Una entidad JPA (tabla) | `infrastructure.persistence.entity` — **no** en `domain.model` | [layer-infrastructure.md](layer-infrastructure.md) |
| Persistencia, REST clients, Kafka | `infrastructure.persistence` / `external` / `messaging` | [layer-infrastructure.md](layer-infrastructure.md) |
| Excepciones, mappers, enums | `shared` | [layer-shared.md](layer-shared.md) · [error-contract.md](error-contract.md) |
| Proteger un endpoint con permisos | `@PermissionsAllowed` (todo endpoint exige JWT salvo los `permitAll` de `SecurityConfig`) | [security.md](security.md) |
| Auditar una entidad | `@Auditable` sobre la entidad JPA de `infrastructure.persistence.entity` | [audit-logging.md](audit-logging.md) |
| Cambiar el esquema de base de datos | Script SQL en `SQL/`, lo aplica el DBA | [database-migration.md](database-migration.md) |
| Versionar la API o paginar | `/api/v1/…`, `page`/`size` | [api-versioning-pagination.md](api-versioning-pagination.md) |

El contrato público del servicio vive en `api/openapi.yaml`, en la raíz del repositorio; es
el que muestra el Developer Hub. Al cambiar un endpoint hay que actualizar ese archivo, el
`version` de `OpenApiConfig` y, si aplica, el `<version>` del `pom.xml`.
