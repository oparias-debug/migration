# Adopción: mi pantalla y mi endpoint

**Para:** quien ya tiene el componente creado y va a protegerlo por permiso, no sólo por sesión.

La pantalla y el endpoint casi nunca viven en el mismo componente: son **dos repositorios, dos
`componentId` y dos padrones**. Esta página es la vista cruzada —qué tiene que coincidir entre los
dos lados, y en qué orden se adopta—. El detalle de este stack está en
[autorizacion.md](autorizacion.md).

---

## 1. El cuadro completo

```
  NAVEGADOR                                    ESTE SERVICIO
  la pantalla de una *-ui                      el componente que recibiste
  guard de ruta y botones                      @PermissionsAllowed(@Permission(...))
        |                                            |
        |  ¿ofrezco esto?                            |  ¿lo permito?
        +---------------> authorization-service <----+
                                   |
                        un padrón por componentId
                  (el de la *-ui y el de este servicio son
                   DISTINTOS: hay que cargar los dos)
```

**La pantalla no protege: sólo evita ofrecer lo que va a fallar.** Quien deniega es este servicio.
Hecha sólo la mitad de arriba, el sistema no está protegido; hecha sólo la de abajo, está
protegido, pero el usuario descubre a base de errores lo que no puede hacer.

---

## 2. Lo que tiene que coincidir entre los dos lados

| Qué | Quién lo fija | Si no coincide |
|---|---|---|
| El **código del recurso** (`expedientes-registro`) | el padrón de cada componente | la pantalla ofrece un botón que el servicio deniega, o esconde uno que sí se podía usar |
| La **operación** (`VIEW`, `DELETE`, …) | las ocho del catálogo, ni una más | al importar se descarta **en silencio** y el permiso nunca existe |
| El **`componentId`** | el `service.name` del componente: `<dirección>-<proyecto>-<componente>` | el permiso queda en el padrón de otro componente y aquí no aparece |
| Los **grupos** | Keycloak; el token los trae en el claim `groups` | el padrón concede a un grupo que no tiene nadie |

Los códigos **no** tienen que ser idénticos en los dos componentes, pero sí tienen que describir lo
mismo. Lo más simple es repetir el mismo árbol en los dos padrones, que es lo que hace el ejemplo.

---

## 3. El orden de adopción

| # | Paso | Quién | Cómo se comprueba |
|---|---|---|---|
| 1 | Decidir los recursos y su jerarquía: códigos simples y `parentCode` | tú | quedan escritos en el `authz/*.json` de cada componente |
| 2 | Decidir los dos grupos y pedir que existan en Keycloak | tú + Administración de identidad | el token del usuario trae el claim `groups` |
| 3 | Cargar el padrón de **cada** componente | tú | la respuesta del import trae `createdResources` y `skipped: 0` |
| 4 | Proteger el endpoint | el equipo del servicio | `403` sin permiso, `200` con permiso |
| 5 | Proteger la pantalla: guard de ruta y acción por operación | el equipo de la `*-ui` | la ruta no abre y el botón no se ofrece |
| 6 | Probar con **dos** usuarios | tú | la tabla de la sección 4 |

El orden importa en un punto: **el padrón antes que el código**. Sin padrón, un endpoint protegido
responde `403` a todo el mundo —el marco falla cerrado— y no se puede distinguir «está mal escrito»
de «no hay permisos cargados».

---

## 4. La única prueba que vale: dos usuarios

Hacen falta **dos** usuarios, no uno metido en los dos grupos: con el ALLOW y el DENY en el mismo
token compiten entre sí y no se ve cuál ganó.

| Llamada | Usuario del grupo admin | Usuario del grupo de consulta |
|---|---|---|
| Endpoint público | `200` | `200` |
| Endpoint sólo con sesión | `200` (`401` sin token) | `200` (`401` sin token) |
| Endpoint con `VIEW` | `200` | `200` — permiso **heredado** del recurso padre |
| Endpoint con `DELETE` | `200` | **`403`** — la denegación del hijo gana |

Esa última fila es el ejemplo entero en una línea: **los dos están autenticados, los dos leen, y
sólo uno borra.** Y se comprueba sin navegador:

```bash
TOKEN="<el token del usuario>"
SRV="https://<host-del-servicio>"

curl -s -o /dev/null -w "%{http_code}" -X DELETE "$SRV/api/v1/demo/security/expedientes/1" -H "Authorization: Bearer $TOKEN"
```

---

## 5. Cuando falla, por dónde empezar

| Síntoma | Causa típica | Dónde mirar |
|---|---|---|
| La pantalla esconde el botón, pero la petición a mano borra igual | sólo se hizo la mitad de la UI | el endpoint no lleva la anotación, o su `path` no es el código del padrón |
| El navegador ni siquiera recibe el `401`/`403`: falla antes | **CORS**: el origen de la pantalla no está permitido en el servicio | el preflight `OPTIONS` tiene que volver `200`; si vuelve `403 CORS Rejected`, es la variable de orígenes del ConfigMap del servicio |
| `403` para todo el mundo, incluso para el administrador | el padrón de ese `componentId` no está cargado | `GET /api/v1/authz/export/{componentId}` |
| Un permiso que sí se importó no concede nada | el grupo del padrón no es un grupo del usuario | los `groups` del token, con la barra tal cual (`/MI_GRUPO`) |
| Una operación desapareció sin dar error al importar | no es una de las ocho del catálogo | `skippedPermissions` en la respuesta del import |
| El nodo no aparece en el menú | el recurso no está declarado como menú en el padrón | la marca de menú de ese recurso |

---

## 6. Lo que no hay que hacer

- **Proteger sólo la pantalla.** Es UX; se saltea con las herramientas de desarrollo.
- **Códigos con `/` o con puntos.** El punto no crea jerarquía —eso lo hace `parentCode`— y una
  barra rompe la construcción del menú de **todo** el componente.
- **Inventar operaciones.** Sólo las ocho del catálogo; cualquier otra se descarta en silencio.
- **Quemar los nombres de los grupos en el código.** Viven en el padrón, y el padrón se importa
  por ambiente: los grupos de desarrollo no son los de producción.

---

## 7. A dónde seguir

- [autorizacion.md](autorizacion.md) — el detalle de este stack: la anotación, el padrón del
  ejemplo, las ocho operaciones y el diagnóstico endpoint por endpoint.
- [security.md](security.md) — la autenticación: de dónde sale el token y qué lleva dentro.
- La otra mitad la documenta la plantilla de la pantalla (`react` o `angular-simple`), en su
  propia página de autorización.
