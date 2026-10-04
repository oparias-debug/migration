# CU-ADM-01 — Administración de Catálogos

| Atributo | Valor |
|---|---|
| **Identificador** | CU-ADM-01 |
| **Nombre** | Administración de Catálogos |
| **Módulo** | ADM — Administración |
| **Fuente** | `CU-ADM-01-catalogs grammar.txt` (gramática + reglas de negocio 1 a 25) |
| **Versión** | 1.3 |
| **Fecha** | 04/10/2026 |
| **Prioridad** | Alta |
| **Estado** | Borrador para revisión |

---

## 1. Descripción

El caso de uso permite definir, mantener y consultar el **Maestro de Catálogos** (`catalogMaster`): un conjunto de catálogos, cada uno con su estructura de campos tipados, su vigencia, su estado (activo/inactivo) y, opcionalmente, una relación jerárquica padre-hijo con otro catálogo. Cubre también la administración de los **registros** de cada catálogo y las búsquedas sobre catálogos, registros y registros hijos.

Ningún catálogo ni registro se elimina físicamente; únicamente se inactiva (RN-13, RN-14).

## 2. Actores

| Actor | Tipo | Descripción |
|---|---|---|
| Administrador de Catálogos | Primario | Crea, actualiza, inactiva y reactiva catálogos y registros. |
| Usuario (cualquier usuario) | Primario | Cualquier usuario del sistema puede consultar el maestro de catálogos, los catálogos, sus registros y registros hijos, sin restricción de catálogo (RN-25). |
| Sistema (Reloj/Planificador) | Secundario | Evalúa diariamente las fechas de vigencia y aplica la inactivación automática y en cascada. |
| Sistemas Consumidores | Secundario | Aplicaciones que invocan las búsquedas de las reglas 7, 8, 17 y 24 vía servicio. |

## 3. Modelo conceptual derivado de la gramática

### 3.1 Gramática de referencia

```
catalogMaster : catalog | catalogMaster ';' catalog
catalog       : 'CATALOG' code name parent active valid '(' fields ')'
code          : STRING
name          : STRING
parent        : <vacío> | 'PARENT' code
active        : 'ACTIVE' | 'INACTIVE'
valid         : <vacío> | DATE | ':' DATE | DATE ':' DATE
fields        : field | fields ';' field
field         : head name type posicion
head          : 'FIELD' | 'KEY'
type          : 'NUMERIC' '{' NUMBER ':' NUMBER '}'
              | 'STRING'  '{' NUMBER '}'
              | 'FECHA'   '{' DATE ':' DATE '}'
              | 'ENUM'    '{' enumList '}'
posicion      : NUMBER
enumList      : STRING | enumList ',' STRING
```

> **Tipo por defecto (RN-04):** si un campo no define su `type`, se le asigna `STRING {80}`.

### 3.2 Entidades

**Catálogo**

| Atributo | Origen gramatical | Obligatorio | Actualizable | Observación |
|---|---|---|---|---|
| Código | `code` | Sí | **No** (RN-19) | Único en el `catalogMaster` (RN-02). |
| Nombre | `name` | Sí | Sí | |
| Padre | `parent` | No | Sí | Código de otro catálogo existente. Vacío = catálogo plano (RN-05). |
| Estado | `active` | Sí | Sí | ACTIVE / INACTIVE. |
| Vigencia | `valid` | No | Sí | Fecha desde (`DATE`) y/o fecha hasta (`TO DATE`, la que sigue a `':'`). |
| Hijo | derivado | — | — | El catálogo padre conoce a su único hijo (RN-05). |
| Campos | `fields` | Sí (≥ 1) | Solo sin registros (RN-21) | Exactamente un campo KEY (RN-03); al menos un campo (RN-20). |

**Campo**

| Atributo | Origen gramatical | Obligatorio | Observación |
|---|---|---|---|
| Tipo de cabecera | `head` | Sí | `KEY` (llave) o `FIELD` (campo común). |
| Nombre | `name` | Sí | Único dentro del catálogo (RN-04). |
| Tipo | `type` | No | NUMERIC {mín:máx}, STRING {longitud máx.}, FECHA {desde:hasta}, ENUM {lista}. Si no se define, `STRING {80}` (RN-04, S-09). |
| Posición | `posicion` | Sí | Determina el orden de los campos. |

**Registro**

| Atributo | Observación |
|---|---|
| Valores | Un valor por campo del catálogo, almacenado como STRING y validado contra tipo y rango (RN-11). |
| Estado | Bandera ACTIVE / INACTIVE (RN-01). |
| Vigencia | Fechas desde/hasta (RN-12, RN-15, RN-16). |
| Id del registro padre | Valor KEY del registro padre en el catálogo padre; obligatorio si el catálogo tiene padre (RN-05, RN-11). |

### 3.3 Reglas de validación de tipos

| Tipo | Definición | Validación del valor de un registro |
|---|---|---|
| NUMERIC `{a : b}` | `a ≤ b` | Valor numérico con `a ≤ valor ≤ b`. |
| STRING `{n}` | `n > 0` | Longitud del texto `≤ n`. |
| FECHA `{d1 : d2}` | `d1 ≤ d2` | Fecha válida con `d1 ≤ valor ≤ d2`. |
| ENUM `{s1, s2, …}` | Lista no vacía, sin duplicados | Valor perteneciente a la lista. |
| *(sin tipo)* | Se asigna `STRING {80}` (RN-04) | Longitud del texto `≤ 80`. |

## 4. Reglas de negocio

| ID | Regla (resumen) | Fuente |
|---|---|---|
| RN-01 | Un catálogo almacena registros; cada registro contiene los valores de los campos del catálogo y una bandera de estado, y cumple las reglas del catálogo. | Regla 1 |
| RN-02 | El código del catálogo es único en el `catalogMaster`. | Regla 2 |
| RN-03 | Todo catálogo tiene, como mínimo, el campo KEY (un único campo llave). | Regla 3 |
| RN-04 | Los nombres de los campos de un catálogo son distintos; el tipo define los valores admitidos (NUMERIC, STRING, FECHA, ENUM). Si un campo no define su tipo, se usa `STRING {80}`. | Regla 4 |
| RN-05 | Jerarquía padre-hijo: el hijo referencia al padre; cada registro hijo lleva el id de su registro padre; el padre conoce a su hijo; dos catálogos no pueden tener el mismo padre (un catálogo padre tiene como máximo un hijo); sin padre = catálogo plano. | Regla 5 |
| RN-06 | Inactivar un catálogo inactiva todos sus registros. Inactivar un catálogo padre inactiva al catálogo hijo y todos los registros de ambos. | Regla 6 |
| RN-07 | Búsqueda de un registro por su valor KEY. | Regla 7 |
| RN-08 | Búsqueda de todos los registros de un catálogo. | Regla 8 |
| RN-09 | Búsqueda de catálogos por código o nombre y listado del maestro. | Regla 9 |
| RN-10 | Creación de catálogo con todos sus datos requeridos. | Regla 10 |
| RN-11 | Creación de registro con valores de todos los campos e id del registro padre; valores guardados como STRING y validados contra tipo y rango. | Regla 11 |
| RN-12 | Inactivación: (a) estado → INACTIVE fija la TO DATE en la fecha actual; (b) fijar TO DATE en hoy o en el pasado. | Regla 12 |
| RN-13 | Un catálogo no se elimina; solo se inactiva. | Regla 13 |
| RN-14 | Un registro no se elimina; solo se inactiva. No se crean registros en catálogos INACTIVE. Inactivar un registro padre inactiva sus registros hijos. Un catálogo INACTIVE puede reactivarse. | Regla 14 |
| RN-15 | Sin fechas de vigencia, el estado del catálogo/registro es ACTIVE. | Regla 15 |
| RN-16 | TO DATE en el pasado ⇒ estado INACTIVE. | Regla 16 |
| RN-17 | Un catálogo padre puede consultarse por su hijo (código y nombre); el resultado puede ser nulo. | Regla 17 |
| RN-18 | Cualquier campo no KEY de un registro es actualizable; el KEY no. | Regla 18 |
| RN-19 | Nombre, padre, estado y vigencia del catálogo son actualizables; el código no. | Regla 19 |
| RN-20 | Todo catálogo tiene al menos un campo definido. | Regla 20 |
| RN-21 | Los descriptores de campos solo se actualizan si el catálogo no tiene registros. | Regla 21 |
| RN-22 | Un catálogo se recupera por su código; si no existe se reporta error. | Regla 22 |
| RN-23 | Un catálogo puede recuperarse y actualizarse. | Regla 23 |
| RN-24 | Búsqueda de los registros hijos de un registro padre a partir del código del catálogo padre y el valor KEY del registro. | Regla 24 |
| RN-25 | Todos los catálogos, sin excepción, pueden ser consultados por cualquier usuario. | Regla 25 |

### 4.1 Algoritmo común: armado del Conjunto de Campos (Field Set)

Usado por RN-07, RN-08 y RN-24.

1. Si no se presenta conjunto de campos, o está vacío: Field Set = (nombre del campo KEY, nombre del primer campo no KEY con **menor posición**).
2. Si se presenta un conjunto que **no contiene** el campo KEY: se agrega el campo KEY como **primer elemento**, seguido de los campos presentados en el orden recibido.
3. Si se presenta un conjunto que contiene el KEY: se usa tal cual.
4. Si algún campo presentado **no está definido** en el catálogo: se reporta error y la búsqueda no se ejecuta.

## 5. Precondiciones

1. Las búsquedas y consultas están disponibles para cualquier usuario sobre cualquier catálogo (RN-25). Las operaciones de creación, actualización, inactivación y reactivación requieren el rol de Administrador de Catálogos (ver supuesto S-08).
2. Para operaciones sobre registros o búsquedas, el catálogo referenciado existe en el `catalogMaster`.
3. El reloj del sistema provee la fecha actual de referencia para las evaluaciones de vigencia.

## 6. Postcondiciones

**Éxito:** el `catalogMaster`, el catálogo o el registro quedan en un estado consistente con todas las reglas de negocio; las búsquedas devuelven la estructura de resultado definida; los cambios de estado se propagan en cascada (RN-06, RN-14).

**Fallo:** no se persiste ningún cambio parcial; el sistema informa el error correspondiente (sección 9).

## 7. Flujo principal

| Paso | Actor | Sistema |
|---|---|---|
| 1 | Ingresa a la opción *Administración de Catálogos*. | |
| 2 | | Muestra el listado de catálogos del `catalogMaster` (código, nombre, padre, hijo, estado, vigencia) y las operaciones disponibles según el rol: cualquier usuario accede a las consultas (SF-02, SF-03, SF-10 a SF-13, RN-25); el Administrador de Catálogos accede además a las operaciones de mantenimiento. |
| 3 | Selecciona una operación. | |
| 4 | | Ejecuta el subflujo correspondiente (SF-01 a SF-15). |
| 5 | | Muestra el resultado de la operación y regresa al paso 2. |

## 8. Subflujos

### SF-01 Crear catálogo (RN-02, RN-03, RN-04, RN-05, RN-10, RN-15, RN-16, RN-20)

1. El actor proporciona código, nombre, padre (opcional), estado, vigencia (opcional) y la lista de campos (cabecera, nombre, tipo (opcional) y posición).
2. El sistema valida que el código no exista en el `catalogMaster` (E-01).
3. El sistema valida que exista al menos un campo (E-02) y exactamente un campo KEY (E-03, E-23).
4. El sistema valida que los nombres de campo sean únicos (E-04). A cada campo sin tipo le asigna `STRING {80}` (RN-04, S-09) y valida que las definiciones de tipo sean consistentes (E-05).
5. Si se indicó padre, el sistema valida que el catálogo padre exista (E-06), que no tenga ya un hijo (E-07), que no se forme un ciclo (E-08) y que el padre esté activo (E-15).
6. El sistema valida las fechas de vigencia (desde ≤ hasta) (E-09) y determina el estado: sin fechas ⇒ ACTIVE (RN-15); TO DATE en el pasado ⇒ INACTIVE (RN-16).
7. El sistema registra el catálogo; si tiene padre, actualiza la referencia al hijo en el catálogo padre.
8. El sistema confirma la creación.

### SF-02 Consultar catálogo por código (RN-22, RN-23)

1. El actor proporciona el código.
2. El sistema recupera el catálogo con todos sus descriptores y campos ordenados por posición, cada uno con su tipo (un campo creado sin tipo se muestra como `STRING {80}`); si no existe, E-10.

### SF-03 Buscar en el maestro y listar catálogos (RN-09)

1. El actor proporciona un código o un nombre, o solicita el listado completo.
2. El sistema indica si existe un catálogo con ese código o nombre y lo muestra; o lista todos los catálogos del `catalogMaster`.

### SF-04 Actualizar descriptores del catálogo (RN-19, RN-21, RN-23)

1. El actor recupera el catálogo (SF-02) y modifica nombre, padre, estado y/o vigencia.
2. El sistema rechaza cualquier intento de modificar el código (E-11).
3. Si cambia el padre, se aplican las validaciones del paso 5 de SF-01 y se actualizan las referencias de hijo en el padre anterior y en el nuevo.
4. Si cambia el estado o la vigencia, se aplica SF-05 o SF-06.
5. Si se modifican los campos, el sistema verifica que el catálogo no tenga registros (E-12) y aplica las validaciones de los pasos 3 y 4 de SF-01, incluido el tipo por defecto de los campos sin tipo.

### SF-05 Inactivar catálogo (RN-06, RN-12, RN-13, RN-16)

1. El actor fija el estado en INACTIVE **o** fija la TO DATE en la fecha actual o una fecha pasada.
2. Si se fijó el estado, el sistema asigna TO DATE = fecha actual (RN-12a). Si se fijó la TO DATE ≤ hoy, el sistema asigna estado INACTIVE (RN-12b, RN-16).
3. El sistema inactiva todos los registros del catálogo (RN-06).
4. Si el catálogo es padre, el sistema inactiva el catálogo hijo y todos sus registros, de forma recursiva en la jerarquía (RN-06).
5. La operación de eliminación no está disponible (RN-13).

### SF-06 Reactivar catálogo (RN-14, RN-19)

1. El actor fija el estado de un catálogo INACTIVE en ACTIVE.
2. Si el catálogo tiene padre, el sistema verifica que el padre esté ACTIVE (E-15).
3. El sistema limpia la TO DATE o exige al actor una TO DATE futura (E-16).
4. Los registros conservan su estado; el actor los reactiva individualmente (ver supuesto S-03).

### SF-07 Crear registro (RN-01, RN-05, RN-11, RN-14, RN-15, RN-16)

1. El actor selecciona el catálogo y proporciona un valor para cada campo, la vigencia (opcional) y, si el catálogo tiene padre, el id del registro padre.
2. El sistema verifica que el catálogo esté ACTIVE (E-13).
3. El sistema valida que se hayan proporcionado todos los campos y que cada valor cumpla tipo y rango (E-14).
4. El sistema valida que el valor KEY no exista en el catálogo (E-17).
5. Si el catálogo tiene padre: el sistema valida que se proporcionó el id del registro padre y que corresponde a un registro existente y activo del catálogo padre (E-18).
6. El sistema determina el estado del registro según su vigencia (RN-15, RN-16), almacena los valores en su versión STRING y confirma.

### SF-08 Actualizar registro (RN-18)

1. El actor localiza el registro por su valor KEY y modifica campos no KEY.
2. El sistema rechaza la modificación del KEY (E-19) y valida tipo y rango de los nuevos valores (E-14).
3. El sistema almacena los valores en versión STRING y confirma.

### SF-09 Inactivar / reactivar registro (RN-12, RN-14, RN-16)

1. El actor fija el estado del registro en INACTIVE o fija su TO DATE en hoy o en el pasado.
2. El sistema completa el dato complementario (TO DATE o estado) según RN-12.
3. El sistema inactiva en cascada los registros hijos (en el catálogo hijo) cuyo id de registro padre sea el KEY del registro inactivado, de forma recursiva.
4. Para reactivar un registro, el sistema exige que su catálogo esté ACTIVE y, si tiene padre, que el registro padre esté ACTIVE (E-20).

### SF-10 Buscar registro por llave (RN-07)

**Entrada:** código del catálogo, Argumento de Búsqueda (valor KEY), conjunto de campos (opcional).

1. El sistema verifica que el catálogo exista (E-10).
2. El sistema arma el Field Set (sección 4.1) (E-21).
3. El sistema construye el Hit Set con el registro cuyo KEY = Argumento; si no hay coincidencia, Hit Set vacío.
4. El sistema arma el resultado:

```
{
  "argumentoBusqueda": "<valor KEY>",
  "codigoCatalogo":    "<code>",
  "fieldSet":          ["<KEY>", "<campo2>", ...],
  "resultSet": [
    { "valores": ["<v KEY>", "<v campo2>", ...], "estado": "ACTIVE|INACTIVE" }
  ]                                   // puede ser vacío
}
```

### SF-11 Listar registros del catálogo (RN-08)

**Entrada:** código del catálogo, lista de campos (opcional).

1. El sistema verifica que el catálogo exista (E-10) y arma el Field Set (sección 4.1) (E-21).
2. El Hit Set contiene todos los registros del catálogo (puede ser vacío).
3. Resultado: `codigoCatalogo`, `fieldSet` y `resultSet` (un elemento por registro con sus valores en el orden del Field Set y su estado).

### SF-12 Consultar catálogo hijo (RN-17)

1. El actor proporciona el código del catálogo padre.
2. El sistema verifica que exista (E-10) y retorna `{codigo, nombre}` del catálogo hijo, o **nulo** si no tiene hijo.

### SF-13 Buscar registros hijos de un registro padre (RN-24)

**Entrada:** código del catálogo padre, Argumento (valor KEY del registro padre), conjunto ordenado de campos (opcional).

1. El sistema verifica que el catálogo padre exista (E-10) y que contenga un registro cuyo KEY = Argumento (E-22).
2. El sistema obtiene el catálogo hijo; si no existe, el Hit Set es vacío.
3. El sistema arma el Field Set sobre el catálogo hijo (sección 4.1) (E-21).
4. El Hit Set contiene los registros del catálogo hijo cuyo id de registro padre = Argumento.
5. El sistema arma el resultado:

```
{
  "argumento":            "<KEY del registro padre>",
  "codigoCatalogoPadre":  "<code padre>",
  "codigoCatalogoHijo":   "<code hijo | null>",
  "fieldSet":             ["<KEY hijo>", ...],
  "resultSet": [
    { "valores": [...], "estado": "ACTIVE|INACTIVE" }
  ]
}
```

### SF-14 Evaluación automática de vigencia (RN-06, RN-14, RN-15, RN-16)

1. Diariamente (y en cada operación de lectura/escritura sobre el elemento), el sistema evalúa la TO DATE de catálogos y registros.
2. Todo catálogo o registro con TO DATE anterior a la fecha actual pasa a INACTIVE.
3. Se aplican las cascadas de SF-05 (catálogos) y SF-09 (registros).

### SF-15 Gestión de jerarquía padre-hijo (RN-05)

1. Al asignar padre a un catálogo, el sistema registra en el padre la referencia a su hijo.
2. Al quitar el padre, el catálogo pasa a ser plano y se elimina la referencia en el antiguo padre. Si el catálogo tiene registros, el sistema exige confirmación, ya que los ids de registro padre dejarán de ser significativos (ver supuesto S-04).

## 9. Flujos de excepción

| Código | Condición | Respuesta del sistema |
|---|---|---|
| E-01 | Código de catálogo duplicado. | "Ya existe un catálogo con el código {code}." |
| E-02 | Catálogo sin campos. | "El catálogo debe tener al menos un campo." |
| E-03 | Catálogo sin campo KEY. | "El catálogo debe tener un campo KEY." |
| E-04 | Nombres de campo repetidos. | "El nombre de campo {name} está repetido." |
| E-05 | Definición de tipo inválida (mín > máx, longitud ≤ 0, fechas invertidas, ENUM vacío o con duplicados, tipo indicado sin sus parámetros). | "Definición de tipo inválida en el campo {name}." |
| E-06 | Catálogo padre inexistente. | "El catálogo padre {code} no existe." |
| E-07 | El catálogo padre ya tiene hijo. | "El catálogo {code} ya tiene un catálogo hijo ({hijo})." |
| E-08 | Ciclo en la jerarquía (incluye ser padre de sí mismo). | "La relación padre-hijo genera un ciclo." |
| E-09 | Fecha desde posterior a fecha hasta. | "Rango de vigencia inválido." |
| E-10 | Código de catálogo inexistente. | "El catálogo {code} no existe." |
| E-11 | Intento de modificar el código del catálogo. | "El código del catálogo no es modificable." |
| E-12 | Modificación de campos en catálogo con registros. | "No se pueden modificar los campos: el catálogo contiene registros." |
| E-13 | Creación de registro en catálogo INACTIVE. | "No se pueden crear registros en un catálogo inactivo." |
| E-14 | Valor faltante o fuera de tipo/rango. | "El valor {v} no es válido para el campo {name} ({tipo y rango})." |
| E-15 | Activar/vincular un catálogo con padre INACTIVE. | "El catálogo padre {code} está inactivo." |
| E-16 | Reactivación con TO DATE vencida. | "Debe actualizar la fecha de vigencia final." |
| E-17 | Valor KEY duplicado en el catálogo. | "Ya existe un registro con la llave {key}." |
| E-18 | Id de registro padre faltante, inexistente o inactivo. | "Registro padre {id} inválido en el catálogo {padre}." |
| E-19 | Intento de modificar el KEY de un registro. | "El campo llave no es modificable." |
| E-20 | Reactivar registro con catálogo o registro padre INACTIVE. | "No se puede reactivar: el catálogo o registro padre está inactivo." |
| E-21 | Campo solicitado no definido en el catálogo. | "El campo {name} no está definido en el catálogo {code}." |
| E-22 | Argumento no corresponde a ningún registro. | "No existe un registro con la llave {key}." |
| E-23 | Catálogo con más de un campo KEY. | "El catálogo solo admite un campo KEY." |
| E-24 | Solicitud de eliminación de catálogo o registro. | "Operación no permitida: solo se admite la inactivación." |
| E-25 | Usuario sin rol de Administrador intenta una operación de mantenimiento. | "No tiene permisos para modificar catálogos o registros." |

## 10. Supuestos e interpretaciones

| ID | Punto de la especificación | Interpretación adoptada |
|---|---|---|
| S-01 | Regla 6 menciona "valid flag … INACTIVE". | Se interpreta como la bandera `active` del catálogo. |
| S-02 | Las reglas no definen precedencia entre `active` explícito y fechas. | La fecha vencida prevalece (INACTIVE). Fijar ACTIVE exige TO DATE vacía o futura. |
| S-03 | Regla 14 permite reactivar catálogos, pero no indica efecto sobre registros. | La reactivación **no** reactiva automáticamente registros ni catálogos hijos. |
| S-04 | Regla 19 permite cambiar el padre; la regla 5 exige ids de registro padre. | Cambiar el padre de un catálogo con registros requiere confirmación y revisión de los ids de registro padre. |
| S-05 | Unicidad de la posición de campos no está definida. | Las posiciones son enteros positivos únicos por catálogo. |
| S-06 | Unicidad del valor KEY no está explícita. | El valor KEY es único dentro de su catálogo (necesario para que la regla 7 retorne un registro). |
| S-07 | La numeración de las reglas 7c y 8c omite literales. | Se conserva la estructura del resultado sin los literales omitidos. |
| S-08 | La regla 25 abre las búsquedas a cualquier usuario, pero no dice quién puede crear, modificar o inactivar. | Las consultas son abiertas a todo usuario autenticado e incluyen catálogos y registros activos e inactivos; las operaciones de mantenimiento se reservan al Administrador de Catálogos. |
| S-09 | La regla 4 define `STRING {80}` como tipo por defecto, pero no dice si se guarda o se calcula al consultar, ni qué ocurre si se indica un tipo sin sus parámetros. | El tipo por defecto se asigna y guarda al definir el campo: queda como `STRING {80}` explícito, se muestra así en las consultas y se valida como cualquier STRING. Solo se aplica cuando el campo no indica tipo; un tipo indicado sin sus parámetros (p. ej. `NUMERIC` sin rango) es una definición inválida (E-05). Aplica también al campo KEY. |

## 11. Requisitos especiales

- **Auditoría:** toda creación, actualización, inactivación y reactivación registra usuario, fecha/hora, valor anterior y nuevo.
- **Atomicidad:** las cascadas de inactivación se ejecutan en una sola transacción.
- **Control de acceso:** las consultas no se restringen por catálogo ni por rol (RN-25); el mantenimiento exige el rol de Administrador de Catálogos (S-08).
- **Formato de fechas:** ISO-8601 (`AAAA-MM-DD`).
- **Almacenamiento:** los valores de los registros se persisten como STRING (RN-11); la conversión a tipo nativo se hace solo para validar.

---

# Historias de Usuario

## HU-ADM-01-01 — Crear catálogo

**Como** Administrador de Catálogos, **quiero** crear un catálogo con su código, nombre, padre, estado, vigencia y campos, **para** disponer de una nueva tabla de referencia en el sistema.

**Reglas:** RN-02, RN-03, RN-04, RN-05, RN-10, RN-15, RN-16, RN-20 · **Subflujo:** SF-01

**Criterios de aceptación**

```gherkin
Escenario: Creación exitosa de catálogo plano
  Dado que no existe el catálogo "PAIS"
  Cuando creo el catálogo "PAIS" con nombre "Países", estado ACTIVE, sin vigencia
    Y con los campos KEY "codPais" STRING{3} pos 1 y FIELD "nombre" STRING{60} pos 2
  Entonces el catálogo queda registrado en el catalogMaster
    Y su estado es ACTIVE

Escenario: Código duplicado
  Dado que existe el catálogo "PAIS"
  Cuando intento crear otro catálogo con código "PAIS"
  Entonces el sistema rechaza la operación con el error E-01

Escenario: Catálogo sin campo KEY
  Cuando intento crear un catálogo cuyos campos son todos FIELD
  Entonces el sistema rechaza la operación con el error E-03

Escenario: Catálogo con más de un campo KEY
  Cuando intento crear un catálogo con dos campos KEY
  Entonces el sistema rechaza la operación con el error E-23

Escenario: Catálogo sin campos
  Cuando intento crear un catálogo sin campos
  Entonces el sistema rechaza la operación con el error E-02

Escenario: TO DATE en el pasado
  Cuando creo un catálogo con vigencia "2020-01-01:2021-01-01"
  Entonces el catálogo se crea con estado INACTIVE
```

## HU-ADM-01-02 — Definir campos y tipos del catálogo

**Como** Administrador de Catálogos, **quiero** definir los campos con su cabecera (KEY/FIELD), nombre, tipo (NUMERIC, STRING, FECHA, ENUM) y posición, **para** que los registros solo acepten valores válidos.

**Reglas:** RN-03, RN-04, RN-20, RN-21 · **Supuestos:** S-05, S-09 · **Subflujos:** SF-01, SF-04

**Criterios de aceptación**

```gherkin
Escenario: Nombres de campo repetidos
  Cuando defino dos campos con el nombre "nombre"
  Entonces el sistema rechaza la definición con el error E-04

Escenario: Rango NUMERIC invertido
  Cuando defino un campo NUMERIC{100:1}
  Entonces el sistema rechaza la definición con el error E-05

Escenario: ENUM vacío o con duplicados
  Cuando defino un campo ENUM{"A","A"}
  Entonces el sistema rechaza la definición con el error E-05

Escenario: Campo sin tipo definido
  Cuando defino el campo FIELD "observacion" en la posición 3 sin indicar su tipo
  Entonces el campo "observacion" queda definido como STRING{80}

Escenario: Campo KEY sin tipo definido
  Cuando creo un catálogo con el campo KEY "codigo" sin indicar su tipo
  Entonces el campo "codigo" queda definido como STRING{80}

Escenario: Tipo indicado sin sus parámetros
  Cuando defino un campo de tipo NUMERIC sin su rango
  Entonces el sistema rechaza la definición con el error E-05

Escenario: Modificar campos de catálogo con registros
  Dado que el catálogo "PAIS" tiene al menos un registro
  Cuando intento modificar, agregar o quitar un campo
  Entonces el sistema rechaza la operación con el error E-12

Escenario: Modificar campos de catálogo sin registros
  Dado que el catálogo "PAIS" no tiene registros
  Cuando cambio el campo "nombre" a STRING{80}
  Entonces la definición del campo queda actualizada
```

## HU-ADM-01-03 — Consultar catálogo por código

**Como** cualquier usuario, **quiero** recuperar un catálogo por su código, **para** ver su definición completa.

**Reglas:** RN-22, RN-23, RN-25 · **Subflujo:** SF-02

```gherkin
Escenario: Catálogo existente
  Dado que existe el catálogo "PAIS"
  Cuando lo consulto por su código
  Entonces obtengo código, nombre, padre, hijo, estado, vigencia y campos ordenados por posición

Escenario: Campo creado sin tipo
  Dado que el catálogo "PAIS" tiene el campo "observacion" definido sin tipo
  Cuando lo consulto por su código
  Entonces el campo "observacion" se muestra con el tipo STRING{80}

Escenario: Catálogo inexistente
  Cuando consulto el código "XYZ" que no existe
  Entonces el sistema reporta el error E-10
```

## HU-ADM-01-04 — Buscar y listar catálogos del maestro

**Como** cualquier usuario, **quiero** buscar un catálogo por código o nombre y listar todos los catálogos, **para** saber qué catálogos están disponibles.

**Reglas:** RN-09, RN-25 · **Subflujo:** SF-03

```gherkin
Escenario: Buscar por nombre
  Dado que existe el catálogo "PAIS" con nombre "Países"
  Cuando busco por el nombre "Países"
  Entonces el sistema indica que el catálogo existe y lo muestra

Escenario: Búsqueda sin coincidencias
  Cuando busco el código "NOEXISTE"
  Entonces el sistema indica que no existe un catálogo con ese código

Escenario: Listado completo
  Cuando solicito el listado de catálogos
  Entonces obtengo todos los catálogos del catalogMaster, activos e inactivos

Escenario: Consulta por usuario sin rol de administrador
  Dado un usuario sin rol de Administrador de Catálogos
  Cuando busca o lista cualquier catálogo, activo o inactivo
  Entonces el sistema le permite la consulta
```

## HU-ADM-01-05 — Actualizar descriptores del catálogo

**Como** Administrador de Catálogos, **quiero** modificar el nombre, padre, estado y vigencia de un catálogo, **para** mantener su información al día.

**Reglas:** RN-19, RN-23 · **Subflujo:** SF-04

```gherkin
Escenario: Cambio de nombre
  Dado el catálogo "PAIS"
  Cuando cambio su nombre a "Países del mundo"
  Entonces el nuevo nombre queda registrado

Escenario: Intento de cambiar el código
  Cuando intento cambiar el código "PAIS" por "PAISES"
  Entonces el sistema rechaza la operación con el error E-11

Escenario: Usuario sin rol de administrador
  Dado un usuario sin rol de Administrador de Catálogos
  Cuando intenta modificar el catálogo "PAIS"
  Entonces el sistema rechaza la operación con el error E-25
```

## HU-ADM-01-06 — Establecer jerarquía padre-hijo

**Como** Administrador de Catálogos, **quiero** asignar un catálogo padre a otro catálogo, **para** representar estructuras jerárquicas (p. ej. País → Departamento).

**Reglas:** RN-05, RN-17 · **Subflujos:** SF-01, SF-04, SF-15

```gherkin
Escenario: Asignación de padre válida
  Dado que existen los catálogos "PAIS" (sin hijo, ACTIVE) y "DEPTO"
  Cuando asigno "PAIS" como padre de "DEPTO"
  Entonces "DEPTO" referencia a "PAIS" como padre
    Y "PAIS" registra a "DEPTO" como su hijo

Escenario: El padre ya tiene hijo
  Dado que "PAIS" ya es padre de "DEPTO"
  Cuando intento asignar "PAIS" como padre de "REGION"
  Entonces el sistema rechaza la operación con el error E-07

Escenario: Ciclo en la jerarquía
  Dado que "PAIS" es padre de "DEPTO"
  Cuando intento asignar "DEPTO" como padre de "PAIS"
  Entonces el sistema rechaza la operación con el error E-08

Escenario: Padre inexistente
  Cuando asigno como padre el código "NOEXISTE"
  Entonces el sistema rechaza la operación con el error E-06
```

## HU-ADM-01-07 — Inactivar catálogo

**Como** Administrador de Catálogos, **quiero** inactivar un catálogo, **para** dejarlo fuera de uso sin perder su historia.

**Reglas:** RN-06, RN-12, RN-13, RN-16 · **Subflujo:** SF-05

```gherkin
Escenario: Inactivación por estado
  Dado el catálogo "PAIS" ACTIVE con registros
  Cuando fijo su estado en INACTIVE
  Entonces su TO DATE se fija en la fecha actual
    Y todos sus registros quedan INACTIVE

Escenario: Inactivación por fecha
  Cuando fijo la TO DATE de "PAIS" en la fecha actual o una fecha pasada
  Entonces su estado pasa a INACTIVE
    Y todos sus registros quedan INACTIVE

Escenario: Cascada a catálogo hijo
  Dado que "PAIS" es padre de "DEPTO"
  Cuando inactivo "PAIS"
  Entonces "DEPTO" queda INACTIVE
    Y todos los registros de "PAIS" y de "DEPTO" quedan INACTIVE

Escenario: Eliminación no permitida
  Cuando intento eliminar el catálogo "PAIS"
  Entonces el sistema rechaza la operación con el error E-24
```

## HU-ADM-01-08 — Reactivar catálogo

**Como** Administrador de Catálogos, **quiero** reactivar un catálogo inactivo, **para** volver a utilizarlo.

**Reglas:** RN-14, RN-19 · **Subflujo:** SF-06

```gherkin
Escenario: Reactivación exitosa
  Dado el catálogo plano "PAIS" INACTIVE
  Cuando fijo su estado en ACTIVE con TO DATE vacía o futura
  Entonces el catálogo queda ACTIVE
    Y se permite crear registros en él

Escenario: Padre inactivo
  Dado que "PAIS" está INACTIVE y es padre de "DEPTO"
  Cuando intento reactivar "DEPTO"
  Entonces el sistema rechaza la operación con el error E-15

Escenario: TO DATE vencida
  Cuando intento reactivar un catálogo manteniendo una TO DATE pasada
  Entonces el sistema rechaza la operación con el error E-16
```

## HU-ADM-01-09 — Crear registro

**Como** Administrador de Catálogos, **quiero** crear registros con valores para todos los campos, **para** poblar el catálogo.

**Reglas:** RN-01, RN-04, RN-05, RN-11, RN-14, RN-15, RN-16 · **Subflujo:** SF-07

```gherkin
Escenario: Registro válido en catálogo plano
  Dado el catálogo "PAIS" ACTIVE
  Cuando creo el registro {codPais:"COL", nombre:"Colombia"} sin vigencia
  Entonces el registro se almacena con sus valores en versión STRING
    Y su estado es ACTIVE

Escenario: Registro hijo con id de registro padre
  Dado que "PAIS" es padre de "DEPTO" y existe el registro "COL" ACTIVE en "PAIS"
  Cuando creo en "DEPTO" el registro {codDepto:"ANT", nombre:"Antioquia"} con registro padre "COL"
  Entonces el registro queda vinculado al registro padre "COL"

Escenario: Falta el id de registro padre
  Dado que "DEPTO" tiene catálogo padre
  Cuando creo un registro sin id de registro padre
  Entonces el sistema rechaza la operación con el error E-18

Escenario: Valor fuera de rango
  Dado el campo "poblacion" NUMERIC{0:2000000000}
  Cuando ingreso el valor "-5"
  Entonces el sistema rechaza la operación con el error E-14

Escenario: Valor fuera de la enumeración
  Dado el campo "continente" ENUM{"AMERICA","EUROPA"}
  Cuando ingreso el valor "ASIA"
  Entonces el sistema rechaza la operación con el error E-14

Escenario: Valor de un campo definido sin tipo
  Dado el campo "observacion" definido sin tipo
  Cuando ingreso un texto de 81 caracteres
  Entonces el sistema rechaza la operación con el error E-14

Escenario: Catálogo inactivo
  Dado el catálogo "PAIS" INACTIVE
  Cuando intento crear un registro
  Entonces el sistema rechaza la operación con el error E-13

Escenario: Llave duplicada
  Dado que existe el registro "COL" en "PAIS"
  Cuando intento crear otro registro con codPais "COL"
  Entonces el sistema rechaza la operación con el error E-17
```

## HU-ADM-01-10 — Actualizar registro

**Como** Administrador de Catálogos, **quiero** modificar los campos no llave de un registro, **para** corregir o actualizar su información.

**Reglas:** RN-11, RN-18 · **Subflujo:** SF-08

```gherkin
Escenario: Actualización de campo no KEY
  Dado el registro "COL" de "PAIS"
  Cuando cambio "nombre" a "República de Colombia"
  Entonces el valor queda actualizado

Escenario: Intento de cambiar el KEY
  Cuando intento cambiar codPais de "COL" a "CO"
  Entonces el sistema rechaza la operación con el error E-19

Escenario: Nuevo valor inválido
  Cuando asigno a "nombre" un texto que supera la longitud máxima
  Entonces el sistema rechaza la operación con el error E-14
```

## HU-ADM-01-11 — Inactivar y reactivar registro

**Como** Administrador de Catálogos, **quiero** inactivar un registro, **para** retirarlo de uso sin eliminarlo.

**Reglas:** RN-12, RN-14, RN-16 · **Subflujo:** SF-09

```gherkin
Escenario: Inactivación por estado
  Dado el registro "COL" ACTIVE
  Cuando fijo su estado en INACTIVE
  Entonces su TO DATE se fija en la fecha actual

Escenario: Cascada a registros hijos
  Dado que "ANT" y "CUN" en "DEPTO" tienen como registro padre "COL"
  Cuando inactivo el registro "COL"
  Entonces "ANT" y "CUN" quedan INACTIVE

Escenario: Reactivación con padre inactivo
  Dado que el registro "COL" está INACTIVE
  Cuando intento reactivar el registro hijo "ANT"
  Entonces el sistema rechaza la operación con el error E-20

Escenario: Eliminación no permitida
  Cuando intento eliminar el registro "COL"
  Entonces el sistema rechaza la operación con el error E-24
```

## HU-ADM-01-12 — Buscar registro por llave

**Como** cualquier usuario o Sistema Consumidor, **quiero** buscar un registro por su valor KEY indicando opcionalmente los campos a retornar, **para** obtener solo la información que necesito.

**Reglas:** RN-07, RN-25 · **Subflujo:** SF-10 · **Algoritmo:** 4.1

```gherkin
Escenario: Sin conjunto de campos
  Dado el catálogo "PAIS" con KEY "codPais" (pos 1), "nombre" (pos 2), "continente" (pos 3)
  Cuando busco "COL" sin indicar campos
  Entonces el Field Set es ["codPais","nombre"]
    Y el Result Set contiene [["COL","Colombia"], ACTIVE]
    Y el resultado incluye el argumento "COL" y el código "PAIS"

Escenario: Conjunto de campos sin KEY
  Cuando busco "COL" con los campos ["continente"]
  Entonces el Field Set es ["codPais","continente"]

Escenario: Campo inexistente
  Cuando busco "COL" con los campos ["moneda"] y "moneda" no está definido
  Entonces el sistema reporta el error E-21

Escenario: Registro no encontrado
  Cuando busco "ZZZ"
  Entonces el Result Set es vacío

Escenario: Búsqueda por cualquier usuario
  Dado un usuario sin rol de Administrador de Catálogos
  Cuando busca "COL" en el catálogo "PAIS"
  Entonces obtiene el mismo resultado que un administrador
```

## HU-ADM-01-13 — Listar registros de un catálogo

**Como** cualquier usuario o Sistema Consumidor, **quiero** obtener todos los registros de un catálogo con los campos que indique, **para** poblar listas y reportes.

**Reglas:** RN-08, RN-25 · **Subflujo:** SF-11 · **Algoritmo:** 4.1

```gherkin
Escenario: Listado con campos por defecto
  Dado el catálogo "PAIS" con 3 registros
  Cuando solicito su listado sin indicar campos
  Entonces el resultado contiene el código "PAIS", el Field Set ["codPais","nombre"]
    Y un elemento por registro con sus valores y su estado

Escenario: Catálogo sin registros
  Dado el catálogo "MONEDA" sin registros
  Cuando solicito su listado
  Entonces el Result Set es vacío
```

## HU-ADM-01-14 — Consultar catálogo hijo

**Como** cualquier usuario, **quiero** saber cuál es el catálogo hijo de un catálogo padre, **para** navegar la jerarquía.

**Reglas:** RN-05, RN-17, RN-25 · **Subflujo:** SF-12

```gherkin
Escenario: Catálogo con hijo
  Dado que "PAIS" es padre de "DEPTO" (nombre "Departamentos")
  Cuando consulto el hijo de "PAIS"
  Entonces obtengo {codigo:"DEPTO", nombre:"Departamentos"}

Escenario: Catálogo sin hijo
  Cuando consulto el hijo de "MONEDA"
  Entonces el resultado es nulo
```

## HU-ADM-01-15 — Buscar registros hijos de un registro padre

**Como** cualquier usuario o Sistema Consumidor, **quiero** obtener los registros hijos de un registro indicando el código de su catálogo y su valor KEY, **para** construir listas dependientes (p. ej. departamentos de un país).

**Reglas:** RN-24, RN-25 · **Subflujo:** SF-13 · **Algoritmo:** 4.1

```gherkin
Escenario: Registro con hijos
  Dado que "ANT" y "CUN" en "DEPTO" tienen como registro padre "COL" de "PAIS"
  Cuando busco en el catálogo "PAIS" los hijos de "COL" sin indicar campos
  Entonces el resultado contiene argumento "COL", catálogo padre "PAIS", catálogo hijo "DEPTO"
    Y el Field Set ["codDepto","nombre"]
    Y el Result Set con los registros "ANT" y "CUN" y su estado

Escenario: Catálogo sin hijo
  Dado que "MONEDA" no tiene catálogo hijo
  Cuando busco en el catálogo "MONEDA" los hijos del registro "USD"
  Entonces el Result Set es vacío

Escenario: Registro padre inexistente
  Cuando busco en el catálogo "PAIS" los hijos de "ZZZ"
  Entonces el sistema reporta el error E-22

Escenario: Catálogo padre inexistente
  Cuando busco en el catálogo "NOEXISTE" los hijos de "COL"
  Entonces el sistema reporta el error E-10
```

## HU-ADM-01-16 — Evaluar vigencia automáticamente

**Como** Sistema (Planificador), **quiero** evaluar diariamente las fechas de vigencia de catálogos y registros, **para** que su estado refleje siempre su vigencia.

**Reglas:** RN-06, RN-14, RN-15, RN-16 · **Subflujo:** SF-14

```gherkin
Escenario: Sin fechas de vigencia
  Dado un catálogo o registro sin fechas de vigencia
  Cuando se evalúa su vigencia
  Entonces su estado es ACTIVE

Escenario: TO DATE vencida
  Dado un registro con TO DATE igual a ayer
  Cuando se ejecuta la evaluación diaria
  Entonces el registro pasa a INACTIVE
    Y sus registros hijos pasan a INACTIVE

Escenario: Catálogo vencido con hijo
  Dado el catálogo "PAIS" con TO DATE vencida, padre de "DEPTO"
  Cuando se ejecuta la evaluación diaria
  Entonces "PAIS", "DEPTO" y todos sus registros pasan a INACTIVE
```

---

## 12. Matriz de trazabilidad

| Regla | Subflujo(s) | Historia(s) |
|---|---|---|
| RN-01 | SF-07 | HU-ADM-01-09 |
| RN-02 | SF-01 | HU-ADM-01-01 |
| RN-03 | SF-01 | HU-ADM-01-01, HU-ADM-01-02 |
| RN-04 | SF-01, SF-02, SF-04, SF-07 | HU-ADM-01-01, HU-ADM-01-02, HU-ADM-01-03, HU-ADM-01-09 |
| RN-05 | SF-01, SF-07, SF-15 | HU-ADM-01-06, HU-ADM-01-09, HU-ADM-01-14 |
| RN-06 | SF-05, SF-14 | HU-ADM-01-07, HU-ADM-01-16 |
| RN-07 | SF-10 | HU-ADM-01-12 |
| RN-08 | SF-11 | HU-ADM-01-13 |
| RN-09 | SF-03 | HU-ADM-01-04 |
| RN-10 | SF-01 | HU-ADM-01-01 |
| RN-11 | SF-07, SF-08 | HU-ADM-01-09, HU-ADM-01-10 |
| RN-12 | SF-05, SF-09 | HU-ADM-01-07, HU-ADM-01-11 |
| RN-13 | SF-05 | HU-ADM-01-07 |
| RN-14 | SF-06, SF-07, SF-09 | HU-ADM-01-08, HU-ADM-01-09, HU-ADM-01-11 |
| RN-15 | SF-01, SF-07, SF-14 | HU-ADM-01-01, HU-ADM-01-09, HU-ADM-01-16 |
| RN-16 | SF-05, SF-09, SF-14 | HU-ADM-01-07, HU-ADM-01-11, HU-ADM-01-16 |
| RN-17 | SF-12 | HU-ADM-01-06, HU-ADM-01-14 |
| RN-18 | SF-08 | HU-ADM-01-10 |
| RN-19 | SF-04, SF-06 | HU-ADM-01-05, HU-ADM-01-08 |
| RN-20 | SF-01 | HU-ADM-01-01, HU-ADM-01-02 |
| RN-21 | SF-04 | HU-ADM-01-02 |
| RN-22 | SF-02 | HU-ADM-01-03 |
| RN-23 | SF-02, SF-04 | HU-ADM-01-03, HU-ADM-01-05 |
| RN-24 | SF-13 | HU-ADM-01-15 |
| RN-25 | SF-02, SF-03, SF-10 a SF-13 | HU-ADM-01-03, HU-ADM-01-04, HU-ADM-01-12 a HU-ADM-01-15 |

## 13. Control de cambios

| Versión | Fecha | Cambio |
|---|---|---|
| 1.0 | 03/10/2026 | Versión inicial. |
| 1.1 | 03/10/2026 | Ajustes por la nueva versión de la gramática: (1) el campo ya no tiene vigencia (`field : head name type posicion`); (2) la regla 3 exige el campo KEY, por lo que se admite un único KEY y se agrega E-23 para más de un KEY; (3) la regla 5 fija explícitamente que un padre tiene como máximo un hijo; (4) la regla 24 recibe el código del catálogo padre además del valor KEY, lo que elimina la ambigüedad de llaves (antiguo E-23); (5) la regla 1 precisa el contenido del registro. Se retiran los supuestos que la nueva gramática resuelve y se renumeran los restantes. |
| 1.2 | 03/10/2026 | Incorporación de la regla 25: las consultas sobre cualquier catálogo quedan abiertas a cualquier usuario (RN-25). Se ajustan el actor consultor, las precondiciones, el flujo principal y las historias de consulta; se agregan el supuesto S-08, la excepción E-25, el requisito de control de acceso y la fila RN-25 en la matriz de trazabilidad. |
| 1.3 | 04/10/2026 | Incorporación del tipo por defecto de la regla 4: un campo sin tipo se define como `STRING {80}`. Se ajustan la tabla de campos (tipo opcional), las reglas de validación de tipos (3.3), RN-04, SF-01, SF-02 y SF-04; se precisa E-05 (tipo indicado sin sus parámetros); se agrega el supuesto S-09; se agregan escenarios en HU-ADM-01-02, HU-ADM-01-03 y HU-ADM-01-09, y se amplía la fila RN-04 de la matriz de trazabilidad. |
