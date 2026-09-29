# 11. Paso entre ambientes

Cómo llega una versión nueva a un ambiente que ya existe. La imagen se construye una sola
vez, en dev; de ahí en adelante lo que viaja es el **tag**, con el código y el chart de la
rama.

---

## 1. La regla

- El código entra por `dev` y sólo por `dev` ([desarrollo.md](desarrollo.md)).
- Una versión sube por la cadena `dev → qa → test → preprod → master`, un eslabón por vez.
- Cada eslabón lo dispara una persona desde el portal; nada sube solo.
- El hotfix es la única excepción: `master → preprod`, para corregir producción sin pasar
  por toda la cadena.

---

## 2. Promover una versión

Developer Hub → **Create** → **Quarkus CI Template**, con la rama de origen y la de destino.
Es la misma plantilla que creó el ambiente ([ambientes.md](ambientes.md) §2); sobre un
ambiente que ya existe hace lo siguiente:

1. Fusiona la rama de origen en la de destino, en el repositorio de código **y** en el
   `-config`. Con el `-config` viaja el `image.tag` del origen.
2. Regenera los overlays `envs/` del destino.
3. Deja el ApplicationSet y el cliente OIDC como estaban (ya existen).
4. Vuelve a copiar la configuración del componente en el inventario del origen al destino.

ArgoCD detecta el commit en la rama del destino y despliega el tag nuevo.

**Antes de promover**, comprobar en el origen que el tag desplegado es el que quieres
llevar (varios CI seguidos pueden dejar en el `-config` un tag que no es el último; ver
[ci-cd-pipeline.md](ci-cd-pipeline.md) §6).

**Después de promover**, repetir las comprobaciones de [ambientes.md](ambientes.md) §3.2 y
§3.3: la promoción regenera el ConfigMap y vuelve a copiar el inventario.

---

## 3. Qué pasa con los cambios directos en una rama de ambiente

Un cambio enviado a `refs/for/qa` (o `test`, `preprod`, `master`) abre una revisión y corre
la pipeline `cr-branches`, que sólo comprueba que la rama esté al día. Al fusionarlo, la
pipeline `ci-<rama>` **no construye una imagen**: copia al `-config` de esa rama el tag que
tiene el ambiente anterior y corre la verificación Karate.

Por eso un cambio de código enviado directamente a `qa` no llega al pod: el binario sigue
siendo el de dev. Los cambios de código van a `dev` y se promueven. Lo único que tiene
sentido cambiar directamente en una rama de ambiente es el **chart** en el repositorio
`-config` (réplicas, recursos, una variable del ConfigMap de ese ambiente).

---

## 4. Hotfix en producción

1. Corregir en `dev` y promover por la cadena si el tiempo lo permite. Es el camino normal.
2. Si no, la plantilla admite `master → preprod` como origen/destino: lleva a preprod lo
   que hay en producción para reproducir y validar la corrección ahí, y la rama `hotfix`
   tiene su propio disparador `ci-hotfix`. Coordinar con el equipo de plataforma antes de
   usar este camino.

---

## 5. Cómo comprobar

| Qué | Dónde | Lo esperado |
|---|---|---|
| Commit de la promoción | `-config`, rama destino | `image.tag` igual al del origen |
| ArgoCD | `<destino>-dgicp-siip2-siipsafi-srv` | `Synced` con la revisión nueva |
| Pod | Namespace destino | Imagen con el tag nuevo |
| ConfigMap | `siipsafi-srv-cmp` | URLs del marco del destino, `DB_URL` del destino |
| Funcional | Rama `pruebas-funcional-<destino>` | `OK` |

```bash
oc -n mh-<destino>-dgicp-siip2 get deploy siipsafi-srv \
   -o jsonpath='{.spec.template.spec.containers[0].image}'
```

---

## 6. Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| El pod del destino sigue con el tag viejo | El merge no cambió `values.yaml` (el origen tenía el mismo tag) o ArgoCD no sincronizó | Comparar tags; `Refresh` en ArgoCD |
| La plantilla falla en el merge | Conflicto entre las ramas (normalmente en el `-config` editado a mano) | Resolver en el repositorio y relanzar |
| El ambiente funciona pero apunta al marco de dev | Overlay con `mh-dev-` | [ambientes.md](ambientes.md) §3.2 |
| `403` tras promover | Permisos no sembrados en el autorizador del destino | [security.md](security.md) §2 |
| Se promovió por error | — | Promover de nuevo desde el origen correcto; el chart y el código de la rama destino vuelven al estado del origen |
