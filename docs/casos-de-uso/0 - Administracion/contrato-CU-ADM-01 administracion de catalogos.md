# Contrato CU-ADM-01 — Administración de Catálogos

Fragmento **OpenAPI 3.0.3 consolidado** (un único archivo `CU-ADM-01.openapi.yaml`)
generado a partir del CU corregido y sus 9 historias BDD (`.feature`). Es el
primer CU procesado del pipeline: `<contratos_existentes>` llegó vacío, por lo
que no hay conflictos que resolver contra fragmentos previos.

## 1. Endpoints generados (10 operaciones / 8 paths)

| Método | Path | operationId | HU origen |
|---|---|---|---|
| POST | `/catalogos` | `crearCatalogo` | HU-01 |
| GET | `/catalogos/existencia` | `buscarCatalogoPorNombre` | HU-05 |
| GET | `/catalogos/{codigoCatalogo}/hijos` | `buscarCatalogosHijos` | HU-06 |
| PATCH | `/catalogos/{codigoCatalogo}` | `actualizarCatalogo` | HU-07 |
| POST | `/catalogos/{codigoCatalogo}/inactivacion` | `inactivarCatalogo` | HU-09 |
| POST | `/catalogos/{codigoCatalogo}/registros` | `crearRegistro` | HU-02 |
| GET | `/catalogos/{codigoCatalogo}/registros` | `buscarListaRegistros` | HU-04 |
| GET | `/catalogos/{codigoCatalogo}/registros/{key}` | `buscarRegistroPorClave` | HU-03 |
| PATCH | `/catalogos/{codigoCatalogo}/registros/{key}` | `actualizarRegistro` | HU-08 |
| POST | `/catalogos/{codigoCatalogo}/registros/{key}/inactivacion` | `inactivarRegistro` | HU-09 |

No se generaron los 5 verbos REST estándar por defecto: solo existen los
verbos que el CU y el `.feature` describen explícitamente (no hay, por
ejemplo, `DELETE /catalogos/{codigo}` porque la regla 9 prohíbe eliminar).

## 2. Decisiones de naming y modelado

- **Inactivación como transición de estado propia, no `PUT`/`PATCH` genérico.**
  El CU distingue explícitamente el flujo 7.6 "Actualizar Catálogo" del flujo
  7.8 "Inactivar Catálogo o Registro" (con sus dos formas: cambio de flag o
  fijar `TO DATE`). Se modeló como `POST .../inactivacion` en vez de
  reutilizar el `PATCH` de actualización, siguiendo el criterio de reflejar
  transiciones de estado explícitas como su propio endpoint. El cuerpo
  (`InactivacionRequest`) es opcional: sin `toDate` → forma (a), regla 8a
  (el sistema asigna la fecha actual); con `toDate` ≤ hoy → forma (b), regla 8b.

- **`buscarCatalogoPorNombre` como `GET /catalogos/existencia?nombre=...`**
  en vez de `GET /catalogos/{nombre}`, porque la búsqueda es por nombre (no
  por el identificador de recurso `codigo`) y la respuesta es un booleano de
  existencia (`ExistenciaCatalogoResponse`), no el catálogo completo — evita
  sugerir semántica de "obtener el catálogo" cuando el CU solo pide "indicar
  si existe".

- **Código de catálogo inmutable (regla 16) modelado por *omisión* de campo,
  no por validación adicional.** `ActualizarCatalogoRequest` directamente no
  tiene la propiedad `codigo` y usa `additionalProperties: false`, de modo
  que un intento de enviarlo ya es rechazable en la capa de esquema, además
  del `400` documentado para el caso Gherkin "Rechazar la modificación del
  código de un catálogo".

- **Campo KEY inmutable (regla 15) modelado igual:** `ActualizarRegistroRequest`
  solo expone `valores` (mapa nombre→valor) sin distinguir KEY vs no-KEY en
  el esquema; la regla de negocio de qué claves del mapa son válidas la
  aplica el backend, y el escenario de error "Rechazar la modificación del
  campo KEY" se representa con `400`.

- **`campos` (FIELD/KEY) como array de `CampoDefinicion`**, con `esKey:
  boolean` en vez de un tipo `KEY` separado — el modelo de dominio no se
  entregó como ER para este CU (ver §4), así que se siguió la terminología
  literal del CU (FIELD/KEY, tipo NUMBER/STRING/DATE/ENUM) para no inventar
  nombres. `esKey` en camelCase, `TipoCampo` como enum reutilizable.

- **`valores` de un registro como `object` con `additionalProperties: true`**
  (mapa dinámico nombre de campo → valor) en lugar de una lista fija de
  propiedades, porque el conjunto de campos es definido dinámicamente por
  catálogo (no hay un schema fijo posible sin conocer los campos de cada
  catálogo en tiempo de diseño).

- **`RegistroResponse` (resultado de crear/actualizar) vs
  `RegistroValoresResponse` (resultado de las dos búsquedas) se separaron
  como schemas distintos** aunque se parecen: la búsqueda por catálogo
  INACTIVE fuerza `estado: INACTIVO` para *todo* registro devuelto (regla
  11), comportamiento que no aplica a crear/actualizar; mantenerlos
  separados documenta esa diferencia semántica en vez de sobrecargar un
  único schema con una nota.

- **`tieneRegistros` como campo de solo lectura en `CatalogoResponse`.** No
  está pedido literalmente por el CU, pero se deriva directamente de la
  regla 18 (los `campos` solo son editables si el catálogo no tiene
  registros) y le evita al front tener que inferirlo. Se marca `readOnly:
  true` para dejar claro que no es parte del request de creación/edición.

- **Sin paginación en `buscarListaRegistros`.** El CU (7.3) no menciona
  paginación ni límites; se optó por `ListaRegistrosResponse { total,
  registros[] }` simple en vez de forzar un `PageableResponse` genérico que
  el CU no pidió, para no inventar comportamiento.

## 3. Manejo de errores (Error estándar + mapeo Gherkin → HTTP)

Se define un único schema `Error { codigo, mensaje, timestamp, detalles? }`,
reutilizado en todas las respuestas 4xx. Todos los escenarios de error del
`.feature` quedan representados:

| Escenario Gherkin | Endpoint | Status |
|---|---|---|
| Catálogo sin campos definidos | `crearCatalogo` | 400 |
| Catálogo sin campo KEY | `crearCatalogo` | 400 |
| Nombres de campo repetidos | `crearCatalogo` | 400 |
| Catálogo padre no existe | `crearCatalogo` | 404 |
| KEY de registro ya existe en el catálogo | `crearRegistro` | 409 |
| KEY inexistente en búsqueda | `buscarRegistroPorClave` | 404 |
| Campo solicitado no existe en el catálogo | `buscarRegistroPorClave` | 400 |
| Modificar el código del catálogo | `actualizarCatalogo` | 400 |
| Modificar `campos` de catálogo con registros | `actualizarCatalogo` | 409 |
| Modificar el campo KEY de un registro | `actualizarRegistro` | 400 |
| Actor no autorizado (todas las escrituras) | todos los `POST`/`PATCH` | 401 / 403 |

`401` (no autenticado) y `403` (autenticado pero sin rol) se separaron como
dos respuestas distintas aunque el Gherkin solo dice "rechaza la operación
por falta de autorización", porque son dos causas HTTP-semánticamente
distintas y el agente Backend necesita distinguirlas para el filtro de
seguridad de Spring.

## 4. Seguridad y roles — advertencia de trazabilidad

Todas las operaciones declaran `security: [{ bearerAuth: [] }]` y
`x-roles: [ADMINISTRADOR_DE_CATALOGOS]`.

⚠️ **`catalogo-actores.md` no contiene "Administrador de Catálogos" ni
"Sistema de Catálogos"** (ver nota en `historias-CU-ADM-01.md`): ese catálogo
compartido fue construido solo a partir de los CU de Preinversión,
Programación, Ejecución/Seguimiento y Convenios, y CU-ADM-01 pertenece a un
módulo transversal distinto que aún no está representado allí. Se usó el
literal `ADMINISTRADOR_DE_CATALOGOS`, coherente con el tag `@rol:` que ya
usan las 9 historias BDD de este CU, **como marcador temporal**. Se
recomienda que el Gestor del Dominio incorpore este actor a
`catalogo-actores.md` antes de que el agente de Fase 4 (Keycloak) intente
mapear `x-roles` a roles reales, para evitar un mapeo 1:1 roto.

Las 4 operaciones de solo lectura (`buscarCatalogoPorNombre`,
`buscarCatalogosHijos`, `buscarListaRegistros`, `buscarRegistroPorClave`)
también llevan `bearerAuth` + `x-roles`, aplicando la precondición general
del CU ("el actor debe estar autenticado y autorizado"), pero **no tienen
respuesta `401`/`403` documentada con caso Gherkin propio** — el `.feature`
de `buscar-registro-por-clave` deja explícitamente sin definir si esta
operación tiene la misma restricción que las de escritura, y no se generó
escenario de permisos para evitar inventar comportamiento. Si Fase 4
determina que estas lecturas son públicas o usan otro rol (p. ej. el
"proceso consumidor" que el CU menciona sin definir), este contrato deberá
ajustarse.

## 5. Glosario y modelo de dominio — sin insumos aplicables

- **Glosario compartido:** no se detectaron términos aplicables (cubre
  DGI/DGICP/CUP/SAFI de otros módulos; ninguno aparece en CU-ADM-01), por lo
  que los nombres de propiedades (`codigo`, `nombre`, `estado`, `vigencia`,
  `campos`, `esKey`, `valores`, `key`) se tomaron literalmente del texto del
  CU, en camelCase.
- **Modelo de dominio (ER Fase 3):** no fue entregado como insumo separado
  para este CU. Los nombres de entidad usados en los schemas (`Catalogo`,
  `Registro`, `CampoDefinicion`) siguen la terminología del propio CU
  (catálogo / registro / campo FIELD-KEY). Si el ER de Fase 3 define nombres
  distintos para estas mismas entidades, este fragmento deberá revisarse
  para alinear los `PascalCase` de los schemas antes de mezclarlo al
  contrato consolidado.

## 6. Conflictos con `<contratos_existentes>`

Ninguno — este es el primer CU procesado por el pipeline (`<contratos_existentes>`
llegó vacío). No hay `path` ni `operationId` que dupliquen algo ya definido.

## 7. Tags

Se agruparon las operaciones en `Catálogos` y `Registros` (por recurso), no
por actor, porque el único actor primario del CU es "Administrador de
Catálogos" para todas las operaciones — agrupar por actor habría colapsado
todo en un solo tag sin valor discriminante. No se usó una WBS del Gestor
del Dominio porque no fue entregada para este CU.
