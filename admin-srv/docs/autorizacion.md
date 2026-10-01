# Autorización

Cómo se protege un endpoint por **permiso**, no sólo por sesión, y cómo cargar el padrón que hace
falta para que ese permiso exista.

La autenticación —quién eres— está en [security.md](security.md). Esto es lo otro: **qué puedes
hacer**.

---

## 1. En una figura

```
@PermissionsAllowed(@Permission(operation = "DELETE", path = "expedientes-registro"))
        │
        ▼  el aspecto AOP de infrastructure/config/authz pregunta por HTTP
GET {authz.service.url}/verify-groups
      ?groupIds=<los grupos del token>      ← sólo grupos; el usuario NO va aquí
      &componentId=<service.name>           ← tu servicio, tal cual
      &resourcePath=expedientes-registro    ← el CÓDIGO del recurso
      &operationName=DELETE
      Authorization: Bearer <el token de la petición>
        │
        ▼
authorization-service  →  ¿alguno de esos grupos tiene DELETE sobre ese recurso?
```

En esta plantilla la autorización **no viene de una librería del marco**: vive dentro del propio
servicio, en `src/main/java/sv/gob/mh/infrastructure/config/authz`. Son cuatro piezas —la
anotación `@PermissionsAllowed`, el `Permission` que lleva la operación y el recurso, el aspecto
`PermissionsAllowedInterceptor` que intercepta la llamada y el `AuthorizationService` que
pregunta—. El contrato con el `authorization-service` es idéntico al de las plantillas Quarkus,
así que todo lo demás de este documento sirve igual.

Tres cosas que conviene saber desde el principio:

1. **El `code` de la anotación no viaja.** Sólo van la operación y el path. El `code` es
   documentación para quien lee el código.
2. **El `path` es el código del recurso, no una ruta.** Es un segmento simple. Un punto no crea
   jerarquía y una barra rompe la construcción del menú de **todo** el componente.
3. **La jerarquía la da el padrón**, con `parentCode`. El permiso se hereda hacia abajo, y una
   denegación explícita en un hijo gana sobre el permiso del padre.

---

## 2. Lo que trae la plantilla

La plantilla traía un controlador de ejemplo (`SecurityController`, bajo
`/api/v1/demo/security`) y su padrón (`authz/ejemplo-authz.json`); en admin-srv ya se quitaron.
Lo que queda del marco es el mecanismo: `@PermissionsAllowed`, el aspecto que lo aplica y el
cliente de `authorization-service`.

Hoy ningún endpoint de admin-srv lo usa: CU-ADM-01 y CU-ADM-04 se autorizan por rol de realm con
`@PreAuthorize`. El resto de esta guía aplica cuando un endpoint pase a exigir un permiso.

---

## 3. Cargar el padrón: un archivo y un comando

Sin padrón, los endpoints con permiso responden `403` a todo el mundo. Es lo correcto —el marco
falla cerrado—, pero no se puede probar nada.

El padrón vive en un archivo `authz/<componente>-authz.json` del repositorio, con el formato del
`import` (§4 muestra un ejemplo). Los grupos que nombre **tienen que existir** y el usuario
pertenecer a ellos: ver §8. Se carga con una sola llamada:

```bash
COMPONENT_ID="<el service.name de tu servicio>"
AUTHZ_URL="https://authorization-service-<...>/api/v1/authz"
TOKEN="<un token de un usuario con sesión>"

curl -sS -X POST "$AUTHZ_URL/import/$COMPONENT_ID?mode=merge" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  --data @authz/<componente>-authz.json
```

La respuesta dice cuántos recursos y permisos se crearon o actualizaron. Es **idempotente**:
volver a ejecutarlo no duplica nada.

Para ver lo que quedó cargado, o para llevártelo a otro ambiente:

```bash
curl -sS "$AUTHZ_URL/export/$COMPONENT_ID" -H "Authorization: Bearer $TOKEN"
```

El formato del `export` es el mismo del `import`: exportas, editas y vuelves a importar.

---

## 4. Un padrón de ejemplo

Cuatro recursos, en jerarquía:

```
expedientes                 (raíz)
├── expedientes-consulta
├── expedientes-registro
└── expedientes-reporte
```

Y dos grupos que se comportan distinto a propósito:

| Grupo | Qué tiene | Qué demuestra |
|---|---|---|
| el administrador | `VIEW` en la raíz + `CREATE`, `UPDATE`, `DELETE` y `EXPORT` | el caso completo |
| el de consulta | `VIEW` en la raíz, y **`DELETE` con `effect = 0`** en `expedientes-registro` | hereda la lectura del padre, pero el borrado se le deniega |

Con eso, el mismo usuario de consulta puede llamar a `GET /expedientes` y recibe `403` en
`DELETE /expedientes/{id}`. Es la demostración de que el permiso se comprueba **por operación**,
no por sesión.

---

## 5. Las ocho operaciones, y ninguna más

`VIEW`, `CREATE`, `UPDATE`, `DELETE`, `EXECUTE`, `EXPORT`, `IMPORT`, `ALLOW`.

⚠️ **Una operación que no esté en esa lista se descarta en silencio al importar**: el permiso no se
crea, no hay error, y el endpoint deniega sin decir por qué. `EDIT`, `READ` y `ADMIN` **no
existen** — son los errores más comunes.

`ALLOW` **no es un comodín**: es una operación más. Un permiso con `ALLOW` no habilita `VIEW`.

---

## 6. Requisitos en `application.yml` y en el `pom.xml`

| Requisito | Si falta |
|---|---|
| `service.name` | El `componentId` no coincide con el padrón y no se encuentra ningún permiso |
| `authz.service.url` | La llamada no se puede hacer y **se deniega** |
| La dependencia `spring-boot-starter-aop` | **La anotación queda inerte**: sin proxy AOP el aspecto nunca corre y el endpoint responde `200` a cualquier usuario autenticado |

Los tres vienen puestos en la plantilla. El tercero es el más traicionero, porque al quitarlo nada
falla: simplemente deja de haber autorización.

Hay además una trampa propia de Spring: **la anotación sólo actúa si la llamada entra por el
proxy**. Un método anotado al que llama otro método de la *misma* clase se ejecuta sin pasar por
el aspecto, y por tanto sin comprobar el permiso. Anota el método que recibe la petición, no un
auxiliar interno.

---

## 7. Cuando algo no cuadra

| Síntoma | Causa probable |
|---|---|
| `200` para todos, aunque no tengan permiso | El aspecto no corrió: falta `spring-boot-starter-aop`, o la llamada al método anotado vino de la propia clase y no pasó por el proxy |
| `403` para todos, incluso para quien sí debería | El padrón no está cargado para este `componentId`, o el `service.name` no coincide |
| `403` sólo para algunos, sin patrón claro | Revisa el `groups` del token: el permiso se concede al grupo, y el nombre debe coincidir exacto |
| El permiso está en la base pero no surte efecto | La operación no está en el catálogo de las ocho, o el cambio no invalidó la caché (el import sí la invalida; un `UPDATE` por SQL, no) |
| `401` en vez de `403` | No es un problema de permisos: la petición no llegó autenticada |

Para verlo desde fuera sin tocar el servicio, pregunta directamente:

```bash
curl -sS -G "$AUTHZ_URL/verify-groups" -H "Authorization: Bearer $TOKEN" \
  --data-urlencode "groupIds=<grupo>" \
  --data-urlencode "componentId=$COMPONENT_ID" \
  --data-urlencode "resourcePath=expedientes-registro" \
  --data-urlencode "operationName=DELETE"
```

Un `200` con `hasPermission: true` concede; un `403` con `hasPermission: false` deniega; un `403`
que dice que *el grupo no pertenece al usuario autenticado* significa que estás preguntando por un
grupo que no está en el token.

---

## 8. Los dos grupos del ejemplo: nada se crea solo

El padrón concede los permisos a **grupos**, y el `authorization-service` sólo mira los grupos que
vienen dentro del token. Así que el ejemplo no funciona hasta que existan de verdad los dos grupos
y tú pertenezcas a ellos. La plantilla **no los crea** —ni al generar el componente ni al
desplegarlo—: es una decisión de directorio, y se hace a mano.

El padrón de ejemplo de §4 usa dos nombres de grupo:

| Grupo | Para qué está |
|---|---|
| `EJEMPLO_GRUPO_ADMIN` | Todo concedido: es el caso completo |
| `EJEMPLO_GRUPO_CONSULTA` | Sólo lectura, con el borrado denegado: es el que demuestra la diferencia |

Tienes dos caminos, y el primero es el más rápido:

**1. Reutilizar grupos que ya existan.** Mira qué grupos trae tu token y pon esos dos nombres en el
JSON antes de cargarlo. Para verlos, decodifica el `access_token` y busca el claim `groups`:

```bash
TOKEN="<tu token>"
echo "$TOKEN" | cut -d. -f2 | base64 -d 2>/dev/null | tr ',' '\n' | grep -i groups
```

**2. Crear los dos grupos en Keycloak.** En el realm del ambiente: *Groups → Create group* con los
dos nombres, y luego *Members → Add member* contigo. Comprueba después que el token los trae: si
el claim `groups` no aparece, al realm le falta el *mapper* que lo publica, y sin él **ningún**
permiso se resuelve, por muy bien que esté el padrón.

Los nombres son libres: lo único que importa es que el `groupId` del padrón coincida **exacto**
con el nombre del grupo en el token. Si cambias los nombres en el JSON, cámbialos también en las
pruebas de la plantilla, que usan los mismos.
