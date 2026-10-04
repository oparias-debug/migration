---
id: CU-ADM-01
codigo: CU-ADM-01
nombre: Administración de Catálogos
modulo: Administración
submodulo: Catálogos
version: "2.1"
fuente: CU-ADM-01-catalogs grammar.txt (gramática de catálogos y 24 reglas de negocio)

nota_version: >
  La versión 2.0 reemplaza completamente a la versión 1.0, que fue derivada de
  una versión anterior de la gramática (17 reglas). Incorpora la jerarquía
  padre-hijo entre catálogos y entre registros, el armado del Conjunto de
  Campos (Field Set), del Conjunto de Aciertos (Hit Set) y del Conjunto de
  Resultados (Result Set) en las búsquedas, la propagación de inactivación
  padre → hijo, la consulta del catálogo hijo y de los registros hijos, y la
  consulta de un catálogo por código. La numeración de reglas (RN01–RN24)
  corresponde 1:1 con la numeración de la gramática fuente.

nota_cambio_v2_1: >
  Cambios por corrección de la gramática fuente y confirmación de supuestos
  por negocio (03/10/2026): (1) DP01 y DP02 confirmados e incorporados como
  comportamiento definitivo (código del catálogo padre en la búsqueda de
  registros hijos; clave compuesta cuando hay varios campos KEY). (2) RN14
  ampliada en la gramática: no se pueden crear registros en un catálogo
  INACTIVE (resuelve DP03) y la inactivación de un registro padre inactiva
  sus registros hijos (resuelve DP09). (3) RN24 paso e.v corregido en la
  gramática (resuelve la observación O02). DP07 (reactivación) sigue
  pendiente: la gramática corregida no incluye texto al respecto.

actor_principal: Administrador de Catálogos

actores_secundarios:
  - Proceso Consumidor de Catálogos

prioridad: Alta (módulo transversal; los catálogos son consumidos por los demás módulos del SIIP)

estado: Analizado

depende_de: []

casos_relacionados:
  - "CU-PRE-* (consumidores de catálogos, p. ej. CU-PRE-03.5 Anexos C.1 a C.6)"

roles:
  - Administrador de Catálogos
  - Proceso Consumidor de Catálogos

entidades:
  - Catálogo Maestro (catalogMaster)
  - Catálogo
  - Campo (FIELD / KEY)
  - Registro de catálogo

palabras_clave:
  - catálogo
  - catálogo maestro
  - registro
  - campo KEY
  - jerarquía padre-hijo
  - vigencia
  - inactivación

ultima_actualizacion: 03/10/2026 (versión 2.1)
---

# Caso de Uso

## Información General

| Atributo | Valor |
|---|---|
| Código | CU-ADM-01 |
| Nombre | Administración de Catálogos |
| Módulo | Administración |
| Versión | 2.1 |
| Actor principal | Administrador de Catálogos |
| Actores secundarios | Proceso Consumidor de Catálogos |
| Fuente | `CU-ADM-01-catalogs grammar.txt` |
| Historias de usuario | HU-ADM-01-01 a HU-ADM-01-12 |

## Historial de Revisiones

| Versión | Fecha | Descripción |
|---|---|---|
| 1.0 | — | Versión inicial, derivada de una versión anterior de la gramática (17 reglas). |
| 2.0 | 03/10/2026 | Reescritura completa con base en la gramática vigente y sus 24 reglas de negocio. Se agregan jerarquía de catálogos/registros, Field Set / Hit Set / Result Set, búsqueda de catálogo hijo y de registros hijos, e historias HU-ADM-01-01 a HU-ADM-01-12. |
| 2.1 | 03/10/2026 | Corrección de la gramática fuente (RN14 y RN24) y confirmación de supuestos: se incorporan DP01 y DP02 como comportamiento definitivo; se resuelven DP03 (no se crean registros en catálogos INACTIVE, error E21) y DP09 (la inactivación de un registro padre se propaga a sus registros hijos); se elimina la observación O02. DP07 sigue pendiente. |

# Objetivo

Permitir definir, consultar, actualizar e inactivar los catálogos del SIIP y sus registros, de forma que todos los módulos del sistema dispongan de estructuras maestras de datos consistentes, con claves únicas, campos tipados, vigencia controlada y jerarquías padre-hijo (por ejemplo Departamento → Municipio).

# Descripción

Un **catálogo** es una estructura que almacena un conjunto de **registros**. Cada catálogo está definido por un código único, un nombre, un catálogo padre opcional, un indicador de actividad, un rango de vigencia opcional y una lista de **campos**, de los cuales al menos uno es **KEY**. Cada registro contiene un valor para cada campo definido en el catálogo, respetando las restricciones de tipo del campo, y un indicador de actividad propio.

El conjunto de todos los catálogos se denomina **Catálogo Maestro** (*catalogMaster*).

Los catálogos y los registros **nunca se eliminan**: solo se inactivan. La inactivación de un catálogo padre se propaga a su catálogo hijo y a todos los registros de ambos.

## Gramática de definición de catálogos

La estructura de un catálogo queda formalmente definida por la siguiente gramática, donde `STRING`, `NUMBER` y `DATE` son terminales:

```text
catalogMaster   :   catalog
                :   catalogMaster ';'  catalog
catalog         :   'CATALOG' code name  parent  active valid '(' fields ')'
code            :    STRING
name            :    STRING
parent          :
                :   'PARENT'  code
active          :   'ACTIVE'
                :   'INACTIVE'
valid           :
                :   DATE
                :   ':'   DATE
                :   DATE  ':' DATE
fields          :   field
                :   fields ';' field
field           :   head  name  type valid
head            :   'FIELD'
                :   'KEY'
type            :   'NUMBER' '{' NUMBER ':'  NUMBER '}'
                :   'STRING' '{'  NUMBER  '}'
                :   'DATE'   '{'  DATE  ':'  DATE '}'
                :   'ENUM'   '{'  enumList '}'
enumList        :    STRING
                :    enumList ','  STRING
```

### Descriptores de un catálogo

| Descriptor | Producción | Obligatorio | Descripción | Actualizable |
|---|---|---|---|---|
| Código | `code` | Sí | Identificador del catálogo, único en el Catálogo Maestro (RN02). | **No** (RN19) |
| Nombre | `name` | Sí | Nombre descriptivo del catálogo. | Sí (RN19) |
| Padre | `parent` | No | Código del catálogo padre. Si se omite, el catálogo es **plano** (RN05). | Sí (RN19) |
| Actividad | `active` | Sí | `ACTIVE` o `INACTIVE`. | Sí (RN19) |
| Vigencia | `valid` | No | Rango de fechas `[DESDE] : [HASTA]` (ver tabla de vigencia). | Sí (RN19) |
| Campos | `fields` | Sí (≥ 1) | Lista de campos separados por `;` (RN20). | Solo si el catálogo no tiene registros (RN21) |

### Descriptores de un campo

| Descriptor | Producción | Descripción |
|---|---|---|
| Encabezado | `head` | `KEY` (campo clave) o `FIELD` (campo no clave). |
| Nombre | `name` | Nombre del campo; único dentro del catálogo (RN04). |
| Tipo | `type` | Tipo de dato y su restricción (ver tabla de tipos). |
| Vigencia | `valid` | Rango de vigencia del campo (ver Datos Pendientes de Definir, DP06). |
| Posición | (implícita) | Orden en que el campo aparece en la definición; se usa para armar el Conjunto de Campos por defecto (RN07-a). |

### Tipos de campo y restricciones

| Tipo | Sintaxis | Restricción que debe cumplir el valor de cada registro |
|---|---|---|
| NUMBER | `NUMBER {min : max}` | Valor numérico con `min ≤ valor ≤ max`. |
| STRING | `STRING {longitud}` | Cadena con longitud máxima `longitud`. |
| DATE | `DATE {desde : hasta}` | Fecha con `desde ≤ valor ≤ hasta`. |
| ENUM | `ENUM {"v1", "v2", ...}` | Valor perteneciente a la lista enumerada. |

### Formas de la vigencia (`valid`)

| Forma | Significado | Estado de vigencia resultante |
|---|---|---|
| *(vacía)* | Sin fechas. | `ACTIVE` (RN15) |
| `DATE` | Solo fecha DESDE. | `ACTIVE` mientras no se indique HASTA (ver DP04 sobre DESDE futura). |
| `: DATE` | Solo fecha HASTA. | `INACTIVE` si HASTA es anterior a la fecha actual (RN16); en otro caso `ACTIVE`. |
| `DATE : DATE` | Rango DESDE–HASTA. | `INACTIVE` si HASTA es anterior a la fecha actual (RN16); en otro caso `ACTIVE`. |

La fecha **HASTA** (*TO DATE*) es la fecha que sigue al terminal `':'` en la producción `valid`.

### Ejemplo de definición

```text
CATALOG "DEP" "Departamentos" ACTIVE
  ( KEY   "codigo"  STRING {2};
    FIELD "nombre"  STRING {60} );
CATALOG "MUN" "Municipios" PARENT "DEP" ACTIVE 01/01/2026
  ( KEY   "codigo"    STRING {4};
    FIELD "nombre"    STRING {60};
    FIELD "categoria" ENUM {"URBANO", "RURAL"};
    FIELD "poblacion" NUMBER {0 : 99999999} )
```

En este ejemplo `DEP` es catálogo padre de `MUN`; cada registro de `MUN` debe llevar la identificación del registro de `DEP` al que pertenece.

# Actor Principal

**Administrador de Catálogos**: usuario responsable de definir, mantener e inactivar los catálogos y sus registros. Ejecuta todas las operaciones de creación, actualización e inactivación, y puede ejecutar todas las consultas.

# Actores Secundarios

**Proceso Consumidor de Catálogos**: cualquier módulo o caso de uso del SIIP (p. ej. los casos de uso de Preinversión) que consulta catálogos y registros para poblar listas de selección o validar datos. Solo ejecuta operaciones de consulta (HU-ADM-01-02, 05, 06, 10, 11 y 12).

# Disparador

- El Administrador de Catálogos necesita crear, modificar o inactivar un catálogo o un registro.
- Un Proceso Consumidor necesita consultar un catálogo, sus registros o su jerarquía.

# Precondiciones

1. El actor está autenticado en el SIIP y tiene asignado el rol correspondiente.
2. Para crear o actualizar un catálogo hijo, el catálogo padre existe en el Catálogo Maestro.
3. Para crear un registro, el catálogo está `ACTIVE` (RN14); si el catálogo es hijo, el registro padre existe en el catálogo padre.
4. Para operar sobre un catálogo o registro existente, este se identifica por su código / valor KEY.

# Flujo Principal

## FB — Crear Catálogo

1. El Administrador solicita crear un nuevo catálogo.
2. El sistema solicita los descriptores del catálogo: código, nombre, catálogo padre (opcional), actividad (`ACTIVE`/`INACTIVE`) y vigencia (opcional).
3. El Administrador registra los descriptores.
4. El Administrador define uno o más campos indicando para cada uno: encabezado (`KEY`/`FIELD`), nombre, tipo con su restricción y vigencia (opcional).
5. El Administrador confirma la creación.
6. El sistema valida:
   1. Que el código no exista en el Catálogo Maestro (RN02) → **E01**.
   2. Que exista al menos un campo (RN20) → **E02**.
   3. Que exista al menos un campo `KEY` (RN03) → **E03**.
   4. Que no haya nombres de campo repetidos (RN04) → **E04**.
   5. Que la restricción de cada tipo sea coherente (mínimo ≤ máximo, longitud > 0, lista ENUM no vacía y sin duplicados, fecha desde ≤ fecha hasta) → **E05**.
   6. Si se indicó padre: que el catálogo padre exista (RN05) → **E06**; y que ningún otro catálogo tenga ya ese mismo padre (RN05) → **E07**.
   7. Que la vigencia sea coherente (DESDE ≤ HASTA) → **E08**.
7. El sistema determina el estado de vigencia del catálogo (RN15, RN16); si el catálogo padre está `INACTIVE`, el catálogo nuevo queda `INACTIVE` (RN06).
8. El sistema agrega el catálogo al Catálogo Maestro. Si tiene padre, registra en el catálogo padre la referencia a su catálogo hijo (RN05).
9. El sistema muestra el mensaje **M01** y el catálogo creado.

# Flujos Alternos

## FA01 — Consultar Catálogo por Código (RN22, RN23)

1. El actor proporciona el código de un catálogo.
2. El sistema busca el catálogo en el Catálogo Maestro.
   - Si no existe → **E09**.
3. El sistema retorna todos los descriptores del catálogo (código, nombre, padre, catálogo hijo si lo tiene, actividad, vigencia y campos con su posición) e indica si el catálogo contiene registros.

## FA02 — Actualizar Catálogo (RN19, RN21, RN23)

1. El Administrador recupera el catálogo por su código (FA01).
2. El Administrador modifica uno o más de: nombre, padre, actividad, vigencia y —solo si el catálogo no contiene registros— campos.
3. El sistema valida:
   1. Que el código no haya sido modificado (RN19) → **E10**.
   2. Si se modificaron los campos: que el catálogo no contenga registros (RN21) → **E11**; y las mismas validaciones de campos del FB (pasos 6.2 a 6.5).
   3. Si se modificó el padre: que el nuevo padre exista (E06), que no tenga ya otro hijo (E07), que no sea el propio catálogo ni uno de sus descendientes (E12), y las restricciones de DP08.
   4. Que la vigencia sea coherente (E08).
4. Si la actividad pasa a `INACTIVE` o la fecha HASTA queda en la fecha actual o en el pasado, se ejecuta FA03.
5. El sistema guarda los cambios y muestra **M02**.

## FA03 — Inactivar Catálogo (RN06, RN12, RN13, RN16)

1. El Administrador inactiva un catálogo mediante una de dos formas:
   - a) Cambia la actividad de `ACTIVE` a `INACTIVE`: el sistema asigna automáticamente la fecha actual como fecha HASTA (RN12-a).
   - b) Establece la fecha HASTA en la fecha actual o en una fecha pasada: el sistema marca el catálogo como `INACTIVE` (RN12-b, RN16).
2. El sistema marca como `INACTIVE` todos los registros del catálogo (RN06).
3. Si el catálogo tiene catálogo hijo, el sistema marca el catálogo hijo como `INACTIVE` y todos sus registros como `INACTIVE` (RN06). La propagación continúa hacia los descendientes del hijo (ver Observaciones, O02).
4. El catálogo y sus registros permanecen almacenados; no se eliminan (RN13, RN14).
5. El sistema muestra **M03**.

## FA04 — Consultar el Catálogo Maestro (RN09)

1. El actor proporciona un código o un nombre de catálogo.
2. El sistema indica si existe un catálogo definido con ese código o nombre y, si existe, retorna su código y nombre.

**Variante FA04.1 — Listar catálogos:** el actor solicita la lista de catálogos disponibles; el sistema retorna todos los catálogos del Catálogo Maestro con su código, nombre, padre y estado de actividad.

## FA05 — Consultar Catálogo Hijo (RN17)

1. El actor proporciona el código de un catálogo padre.
   - Si el catálogo no existe → **E09**.
2. El sistema retorna el código y el nombre de su catálogo hijo.
3. Si el catálogo no tiene catálogo hijo, el resultado es nulo (no es un error).

## FA06 — Crear Registro (RN01, RN03, RN05, RN11, RN14, RN15, RN16)

1. El Administrador selecciona un catálogo existente y solicita crear un registro.
2. El Administrador proporciona un valor para cada campo del catálogo, la vigencia del registro (opcional) y, si el catálogo es hijo, la identificación del registro padre (valor KEY del registro en el catálogo padre).
3. El sistema valida:
   1. Que el catálogo no esté `INACTIVE` (RN14) → **E21**.
   2. Que se haya proporcionado un valor para cada campo del catálogo → **E13**.
   3. Que cada valor cumpla la restricción del tipo del campo (RN01) → **E14**.
   4. Que el valor KEY (la combinación de valores de todos los campos KEY, si hay más de uno) no exista ya en el catálogo (RN03) → **E15**.
   5. Si el catálogo es hijo: que se haya indicado el registro padre y que exista en el catálogo padre (RN05) → **E16**.
   6. Que la vigencia sea coherente (E08).
4. El sistema determina el estado de vigencia del registro (RN15, RN16).
5. El sistema almacena el registro y muestra **M04**.

## FA07 — Actualizar Registro (RN18)

1. El Administrador selecciona un registro por su valor KEY.
2. El Administrador modifica el valor de uno o más campos no KEY y/o la vigencia del registro.
3. El sistema valida:
   1. Que no se haya modificado ningún campo KEY (RN18) → **E17**.
   2. Que cada nuevo valor cumpla la restricción de su tipo → **E14**.
4. Si la fecha HASTA queda en la fecha actual o en el pasado, se ejecuta FA08.
5. El sistema guarda los cambios y muestra **M05**.

## FA08 — Inactivar Registro (RN12, RN14, RN16)

1. El Administrador inactiva un registro mediante una de dos formas:
   - a) Cambia la actividad del registro a `INACTIVE`: el sistema asigna la fecha actual como fecha HASTA (RN12-a).
   - b) Establece la fecha HASTA en la fecha actual o en una fecha pasada (RN12-b, RN16).
2. Si el catálogo del registro tiene catálogo hijo, el sistema marca como `INACTIVE` todos los registros hijos del registro, es decir, aquellos cuya identificación de registro padre es el valor KEY del registro inactivado (RN14). La propagación continúa hacia los registros hijos de estos en los niveles inferiores de la jerarquía (ver Observaciones, O02).
3. El registro y sus registros hijos permanecen almacenados con estado `INACTIVE`; no se eliminan (RN14).
4. El sistema muestra **M06**.

## FA09 — Buscar Registro por Clave (RN07)

1. El actor proporciona el código del catálogo, el valor KEY buscado (**Argumento de Búsqueda**) y, opcionalmente, un conjunto de nombres de campo. Si el catálogo tiene más de un campo KEY, el Argumento de Búsqueda es la combinación de los valores de todos sus campos KEY, en su orden de posición (RN03).
   - Si el catálogo no existe → **E09**.
2. **Armar el Conjunto de Campos (Field Set)**:
   - Si no se proporcionó un conjunto de campos o está vacío: el Conjunto de Campos tiene dos elementos: (nombre del campo KEY, nombre del primer campo no KEY de menor posición en la definición del catálogo). Si hay más de un campo KEY, todos ellos encabezan el Conjunto de Campos en orden de posición.
   - Si el conjunto proporcionado no contiene el campo KEY: se incluye el campo KEY (o los campos KEY, en orden de posición) al inicio del Conjunto de Campos, seguido de los campos proporcionados en el orden indicado.
   - Si algún campo proporcionado no está definido en el catálogo → **E18**.
3. **Armar el Conjunto de Aciertos (Hit Set)**: se busca en el catálogo el registro cuyo valor KEY sea igual al Argumento de Búsqueda y se incluye en el Conjunto de Aciertos. Si no se encuentra, el Conjunto de Aciertos queda vacío (no es un error).
4. **Armar el resultado** con:
   - i) El valor KEY proporcionado como Argumento de Búsqueda.
   - ii) El código del catálogo.
   - iv) El Conjunto de Campos armado en el paso 2.
   - v) El **Conjunto de Resultados (Result Set)**: por cada registro del Conjunto de Aciertos, un elemento con la lista de valores (uno por cada campo del Conjunto de Campos, en el mismo orden) y el indicador de actividad del registro. El Conjunto de Resultados puede estar vacío.

## FA10 — Listar Registros de un Catálogo (RN08)

1. El actor proporciona el código del catálogo y, opcionalmente, una lista de nombres de campo.
   - Si el catálogo no existe → **E09**.
2. Se arma el Conjunto de Campos como en FA09 paso 2 (incluido E18).
3. El Conjunto de Aciertos contiene todos los registros del catálogo (activos e inactivos). Puede estar vacío.
4. **Armar el resultado** con:
   - i) El código del catálogo.
   - iv) El Conjunto de Campos.
   - v) El Conjunto de Resultados: por cada registro del Conjunto de Aciertos, un elemento con la lista de valores (uno por cada campo del Conjunto de Campos) y el indicador de actividad del registro.

## FA11 — Buscar Registros Hijos (RN24)

1. El actor proporciona el valor KEY de un registro padre (**Argumento**), el código del catálogo al que pertenece (catálogo padre) y, opcionalmente, un conjunto ordenado de nombres de campo.
2. El sistema identifica el catálogo al que pertenece el registro (catálogo padre) a partir del código proporcionado; el código es necesario porque el valor KEY solo es único dentro de su catálogo.
   - Si el catálogo no existe → **E09**.
3. El sistema identifica el catálogo hijo del catálogo padre. Si no existe catálogo hijo, el Conjunto de Aciertos es vacío.
4. Se arma el Conjunto de Campos como en FA09 paso 2, **sobre los campos del catálogo hijo** (incluido E18).
5. **Armar el Conjunto de Aciertos**: se incluyen los registros del catálogo hijo cuya identificación de registro padre sea igual al Argumento.
6. **Armar el resultado** con:
   - i) El valor KEY del registro proporcionado como Argumento.
   - ii) El código del catálogo padre.
   - iii) El código del catálogo hijo (nulo si no existe).
   - iv) El Conjunto de Campos armado en el paso 4.
   - v) El Conjunto de Resultados: por cada registro del Conjunto de Aciertos, un elemento con la lista de valores (uno por cada campo del Conjunto de Campos) y el indicador de actividad del registro.

# Excepciones

| Código | Condición | Flujo |
|---|---|---|
| E01 | Ya existe un catálogo con el código indicado. | FB |
| E02 | El catálogo no tiene campos definidos. | FB, FA02 |
| E03 | El catálogo no tiene ningún campo KEY. | FB, FA02 |
| E04 | Hay dos o más campos con el mismo nombre. | FB, FA02 |
| E05 | Restricción de tipo incoherente (mín > máx, longitud ≤ 0, ENUM vacío o con duplicados, fecha desde > hasta). | FB, FA02 |
| E06 | El catálogo padre indicado no existe. | FB, FA02 |
| E07 | El catálogo padre indicado ya tiene un catálogo hijo. | FB, FA02 |
| E08 | Vigencia incoherente: fecha DESDE posterior a fecha HASTA. | FB, FA02, FA06, FA07 |
| E09 | El código de catálogo no existe. | FA01, FA05, FA09, FA10, FA11 |
| E10 | Se intentó modificar el código del catálogo. | FA02 |
| E11 | Se intentó modificar los campos de un catálogo que contiene registros. | FA02 |
| E12 | El padre indicado generaría un ciclo en la jerarquía. | FA02 |
| E13 | Falta el valor de uno o más campos del catálogo. | FA06 |
| E14 | Un valor no cumple la restricción de tipo de su campo. | FA06, FA07 |
| E15 | Ya existe un registro con el mismo valor KEY en el catálogo. | FA06 |
| E16 | No se indicó el registro padre o no existe en el catálogo padre. | FA06 |
| E17 | Se intentó modificar el campo KEY de un registro. | FA07 |
| E18 | Uno o más nombres de campo solicitados no están definidos en el catálogo. | FA09, FA10, FA11 |
| E19 | Se intentó eliminar un catálogo o un registro. | Todos |
| E20 | El actor no está autorizado para la operación. | Todos |
| E21 | No se pueden crear registros en un catálogo `INACTIVE`. | FA06 |

# Postcondiciones

1. El catálogo o registro queda creado, actualizado o inactivado según la operación ejecutada.
2. Ningún catálogo ni registro es eliminado; la inactivación solo cambia su estado y su fecha HASTA.
3. Al inactivar un catálogo, todos sus registros, su catálogo hijo (y descendientes) y los registros de estos quedan `INACTIVE`.
4. Al inactivar un registro, todos sus registros hijos (y los descendientes de estos) quedan `INACTIVE`.
5. Las operaciones de consulta no modifican el estado del sistema.

# Reglas de Negocio

## RN01 — Contenido de un registro
Un catálogo almacena un conjunto de registros; cada registro contiene un valor para cada campo definido en el catálogo y un indicador de actividad. Cada registro cumple las reglas (restricciones de tipo) del catálogo.

## RN02 — Código único
El código de un catálogo es único dentro del Catálogo Maestro.

## RN03 — Campo KEY obligatorio
Un catálogo debe tener al menos un campo KEY. Si tiene más de uno, la clave del registro es la combinación de los valores de todos sus campos KEY (clave compuesta); esa combinación es la que se usa como Argumento de Búsqueda (RN07) y como identificación del registro padre (RN05, RN24).

## RN04 — Nombres de campo únicos
Los campos de un catálogo deben tener nombres distintos.

## RN05 — Jerarquía padre-hijo
- Un catálogo puede ser padre de otro catálogo. La jerarquía se establece asignando como padre del catálogo hijo el código del catálogo padre.
- Cada registro del catálogo hijo debe llevar la identificación de su registro padre en el catálogo padre.
- El catálogo padre conoce cuál es su catálogo hijo.
- Dos catálogos no pueden tener el mismo código de padre (un catálogo padre tiene a lo sumo un catálogo hijo).
- Si no se define el código de padre, el catálogo es un catálogo plano.

## RN06 — Propagación de la inactivación
- Si un catálogo queda `INACTIVE`, todos sus registros quedan `INACTIVE`.
- Si un catálogo padre queda `INACTIVE`, su catálogo hijo queda automáticamente `INACTIVE`, y todos los registros del catálogo padre y del catálogo hijo quedan `INACTIVE`.

## RN07 — Búsqueda de un registro por clave
Un catálogo puede consultarse para obtener un registro específico a partir del código del catálogo, el valor KEY buscado (Argumento de Búsqueda) y, opcionalmente, un conjunto de nombres de campo. El resultado se arma con el Conjunto de Campos, el Conjunto de Aciertos y el Conjunto de Resultados según FA09.

## RN08 — Listado de registros de un catálogo
Un catálogo puede consultarse para obtener todos sus registros a partir del código del catálogo y, opcionalmente, una lista de nombres de campo, según FA10.

## RN09 — Consulta del Catálogo Maestro
El Catálogo Maestro puede consultarse para saber si existe un catálogo con un código o nombre determinado. El sistema puede listar todos los catálogos disponibles.

## RN10 — Creación de catálogos
Un catálogo nuevo se crea proporcionando todos sus descriptores obligatorios.

## RN11 — Creación de registros
Un registro nuevo se crea proporcionando el valor de cada campo del catálogo y, si el catálogo es hijo, la identificación del registro padre en el catálogo padre.

## RN12 — Formas de inactivación
- a) Cambiando el indicador `ACTIVE` a `INACTIVE`: el sistema asigna automáticamente la fecha actual como fecha HASTA.
- b) Estableciendo la fecha HASTA en la fecha actual o en una fecha pasada.

## RN13 — Catálogos no eliminables
Un catálogo no puede eliminarse; solo puede inactivarse.

## RN14 — Registros no eliminables e inactivación de registros
- Un registro de catálogo no puede eliminarse; solo puede inactivarse.
- No se pueden crear registros en un catálogo `INACTIVE`.
- La inactivación de un registro padre inactiva sus registros hijos.

## RN15 — Vigencia por defecto
Si no se indican fechas de vigencia para un catálogo o registro, su estado de vigencia es `ACTIVE`.

## RN16 — Fecha HASTA vencida
Si la fecha HASTA de un catálogo o registro es anterior a la fecha actual, su estado de vigencia es `INACTIVE`.

## RN17 — Consulta del catálogo hijo
Un catálogo padre puede consultarse para obtener su catálogo hijo; se retorna el código y el nombre del catálogo hijo. El resultado puede ser nulo si el catálogo no tiene hijo.

## RN18 — Actualización de registros
Cualquier campo no KEY de un registro puede actualizarse. El campo KEY no puede actualizarse.

## RN19 — Actualización de catálogos
El nombre, el padre, la actividad y la vigencia de un catálogo pueden actualizarse; su código no.

## RN20 — Al menos un campo
Un catálogo debe tener al menos un campo definido.

## RN21 — Campos inmutables con registros
Los descriptores de los campos de un catálogo solo pueden actualizarse si el catálogo no contiene registros.

## RN22 — Consulta por código
Un catálogo puede recuperarse proporcionando su código. Si el código no existe, se reporta un error.

## RN23 — Recuperar y actualizar
Un catálogo puede recuperarse y actualizarse.

## RN24 — Búsqueda de registros hijos
Un registro de un catálogo padre puede consultarse para obtener sus registros hijos, proporcionando su valor KEY, el código del catálogo padre y, opcionalmente, un conjunto ordenado de nombres de campo, según FA11. El Conjunto de Campos se arma sobre los campos del catálogo hijo.

# Validaciones

| Elemento | Validación | Regla | Error |
|---|---|---|---|
| Código de catálogo | Obligatorio, único en el Catálogo Maestro, inmutable. | RN02, RN19 | E01, E10 |
| Nombre de catálogo | Obligatorio. | RN10 | — |
| Padre | Debe existir; sin otro hijo; sin ciclos. | RN05 | E06, E07, E12 |
| Vigencia | DESDE ≤ HASTA. | RN15, RN16 | E08 |
| Campos | ≥ 1 campo, ≥ 1 KEY, nombres únicos, restricciones coherentes; inmutables si hay registros. | RN03, RN04, RN20, RN21 | E02–E05, E11 |
| Catálogo del registro | Debe estar `ACTIVE` para crear registros. | RN14 | E21 |
| Valores de registro | Uno por campo; cumplen el tipo; KEY (simple o compuesta) única en el catálogo; KEY inmutable. | RN01, RN03, RN11, RN18 | E13–E15, E17 |
| Registro padre | Obligatorio en catálogos hijos y debe existir en el catálogo padre. | RN05, RN11 | E16 |
| Campos solicitados en búsquedas | Deben existir en el catálogo consultado. | RN07 | E18 |

# Mensajes al Usuario

| Código | Mensaje |
|---|---|
| M01 | "El catálogo {código} fue creado exitosamente." |
| M02 | "El catálogo {código} fue actualizado exitosamente." |
| M03 | "El catálogo {código} fue inactivado. Se inactivaron {n} registros y {m} catálogos dependientes." |
| M04 | "El registro {KEY} fue creado en el catálogo {código}." |
| M05 | "El registro {KEY} del catálogo {código} fue actualizado." |
| M06 | "El registro {KEY} del catálogo {código} fue inactivado. Se inactivaron {n} registros hijos." |

# Permisos

| Operación | Administrador de Catálogos | Proceso Consumidor |
|---|---|---|
| Crear / actualizar / inactivar catálogo | Sí | No |
| Crear / actualizar / inactivar registro | Sí | No |
| Consultar catálogo, Catálogo Maestro, catálogo hijo | Sí | Sí |
| Buscar registro por clave, listar registros, buscar registros hijos | Sí | Sí |
| Eliminar catálogo o registro | No (RN13, RN14) | No |

# Entidades Detectadas

| Entidad | Atributos principales |
|---|---|
| Catálogo Maestro | Conjunto de catálogos. |
| Catálogo | código (PK), nombre, código padre (FK opcional), código hijo (derivado), actividad, fecha desde, fecha hasta. |
| Campo | catálogo (FK), nombre, posición, es KEY, tipo, restricción (mín/máx, longitud, rango de fechas o lista ENUM), fecha desde, fecha hasta. |
| Registro | catálogo (FK), valor KEY, valores por campo, identificación del registro padre (FK opcional), actividad, fecha desde, fecha hasta. |

# Eventos del Sistema

| Evento | Efecto |
|---|---|
| Inactivación de un catálogo | Inactiva todos sus registros, su catálogo hijo y los registros de este (RN06). |
| Inactivación de un registro padre | Inactiva todos sus registros hijos y los descendientes de estos (RN14). |
| Llegada de la fecha HASTA de un catálogo o registro | El estado de vigencia pasa a `INACTIVE` (RN16); aplica la propagación de RN06 (catálogos) o de RN14 (registros) (ver DP05). |

# Observaciones

- **O01 — Numeración de las reglas 7 y 8.** En la fuente, el armado del resultado salta de "ii)" a "iv)" en las reglas 7 y 8; se conservó la numeración original para mantener la trazabilidad.
- **O02 — Propagación en cadena.** La gramática permite jerarquías de más de dos niveles (A → B → C). Se interpreta que la inactivación de RN06 (catálogos) y de RN14 (registros) se propaga de forma transitiva a todos los descendientes.
- **O03 — Indicador de actividad vs. estado de vigencia.** La gramática distingue el indicador `active` (explícito) de la vigencia `valid` (fechas). RN12 los enlaza: inactivar el indicador fija la fecha HASTA, y una fecha HASTA vencida inactiva el catálogo/registro. Se trata como un único estado efectivo `ACTIVE`/`INACTIVE`.
- **O04 — Búsqueda sin coincidencia.** A diferencia de la versión 1.0 de este CU, una búsqueda por clave sin coincidencias **no es un error**: retorna un Conjunto de Resultados vacío (RN07-b). Solo es error un código de catálogo inexistente o un nombre de campo no definido.
- **O05 — Registros inactivos en búsquedas.** RN07, RN08 y RN24 no filtran por actividad: el Conjunto de Resultados incluye registros activos e inactivos, cada uno con su indicador de actividad, para que el consumidor decida.

# Datos Pendientes de Definir

| Código | Pendiente | Supuesto adoptado mientras no se defina |
|---|---|---|
| DP04 | No se define el estado de un catálogo/registro con fecha DESDE futura. | Se considera `ACTIVE` (solo RN16 inactiva por fecha). |
| DP05 | No se define cómo ni cuándo se aplica la inactivación por llegada de la fecha HASTA (en línea al consultar o mediante un proceso programado). | Se evalúa al consultar y mediante un proceso diario. |
| DP06 | No se define el efecto de la vigencia (`valid`) a nivel de campo. | Se almacena; un campo vencido sigue presente en los registros existentes. |
| DP07 | No se indica si se puede reactivar un catálogo o registro inactivo, ni si la reactivación del padre reactiva al hijo. | Se permite reactivar individualmente (cambiando actividad y fecha HASTA); no se propaga. |
| DP08 | No se define si puede cambiarse el padre de un catálogo que ya tiene registros (sus registros no tendrían registro padre en el nuevo catálogo padre), ni si puede cambiarse el registro padre de un registro. | Cambiar el padre solo se permite si el catálogo no tiene registros; el registro padre de un registro no es modificable. |
| DP10 | Formato del terminal `DATE`. | `dd/mm/aaaa`, coherente con los demás CU del SIIP. |
| DP11 | Comportamiento si el catálogo solo tiene campos KEY y no se solicitan campos (no existe "primer campo no KEY"). | El Conjunto de Campos contiene solo el campo KEY. |
| DP12 | Paginación del listado de registros (RN08). | Sin paginación definida por negocio; a definir en el contrato técnico. |

## Pendientes resueltos

Se conservan los códigos originales para mantener la trazabilidad con la versión 2.0.

| Código | Pendiente | Resolución (03/10/2026) | Incorporado en |
|---|---|---|---|
| DP01 | RN24 identificaba el registro padre solo por su valor KEY, que es único solo dentro de su catálogo. | Confirmado por negocio: la búsqueda de registros hijos recibe también el código del catálogo padre. | RN24, FA11, HU-ADM-01-12 |
| DP02 | RN03 admite varios campos KEY, pero RN07 habla de "el campo KEY" en singular. | Confirmado por negocio: con varios campos KEY, la clave es la combinación de sus valores, y todos ellos encabezan el Conjunto de Campos en orden de posición. | RN03, FA06, FA09, HU-ADM-01-07, HU-ADM-01-10 |
| DP03 | No se indicaba si podían crearse registros en un catálogo `INACTIVE`. | Gramática corregida (RN14): no se pueden crear registros en un catálogo `INACTIVE`. | RN14, FA06, E21, HU-ADM-01-07 |
| DP09 | No se indicaba si la inactivación de un registro padre se propaga a sus registros hijos. | Gramática corregida (RN14): la inactivación de un registro padre inactiva sus registros hijos. | RN14, FA08, HU-ADM-01-09 |

# Matriz de Trazabilidad

| Regla | Flujo | Historia de usuario |
|---|---|---|
| RN01 | FA06 | HU-ADM-01-07 |
| RN02 | FB | HU-ADM-01-01 |
| RN03 | FB, FA02, FA06, FA09 | HU-ADM-01-01, HU-ADM-01-03, HU-ADM-01-07, HU-ADM-01-10 |
| RN04 | FB, FA02 | HU-ADM-01-01, HU-ADM-01-03 |
| RN05 | FB, FA02, FA06 | HU-ADM-01-01, HU-ADM-01-03, HU-ADM-01-07 |
| RN06 | FA03 | HU-ADM-01-04 |
| RN07 | FA09 | HU-ADM-01-10 |
| RN08 | FA10 | HU-ADM-01-11 |
| RN09 | FA04 | HU-ADM-01-05 |
| RN10 | FB | HU-ADM-01-01 |
| RN11 | FA06 | HU-ADM-01-07 |
| RN12 | FA03, FA08 | HU-ADM-01-04, HU-ADM-01-09 |
| RN13 | FA03 | HU-ADM-01-04 |
| RN14 | FA06, FA08 | HU-ADM-01-07, HU-ADM-01-09 |
| RN15 | FB, FA06 | HU-ADM-01-01, HU-ADM-01-07 |
| RN16 | FB, FA03, FA06, FA08 | HU-ADM-01-01, HU-ADM-01-04, HU-ADM-01-07, HU-ADM-01-09 |
| RN17 | FA05 | HU-ADM-01-06 |
| RN18 | FA07 | HU-ADM-01-08 |
| RN19 | FA02 | HU-ADM-01-03 |
| RN20 | FB, FA02 | HU-ADM-01-01, HU-ADM-01-03 |
| RN21 | FA02 | HU-ADM-01-03 |
| RN22 | FA01 | HU-ADM-01-02 |
| RN23 | FA01, FA02 | HU-ADM-01-02, HU-ADM-01-03 |
| RN24 | FA11 | HU-ADM-01-12 |

# Historias de Usuario

## HU-ADM-01-01 — Crear catálogo

**Como** Administrador de Catálogos
**Quiero** definir un nuevo catálogo con sus descriptores y campos
**Para** disponer de una estructura maestra de datos que los módulos del SIIP puedan consumir

- **Flujo:** FB · **Reglas:** RN02, RN03, RN04, RN05, RN10, RN15, RN16, RN20

**Criterios de aceptación**

```gherkin
Escenario: Crear un catálogo plano válido
  Dado que no existe el catálogo "DEP"
  Cuando creo el catálogo "DEP" "Departamentos" ACTIVE sin vigencia
    con los campos KEY "codigo" STRING {2} y FIELD "nombre" STRING {60}
  Entonces el catálogo "DEP" queda registrado en el Catálogo Maestro
  Y su estado de vigencia es ACTIVE
  Y se muestra el mensaje M01

Escenario: Crear un catálogo hijo
  Dado que existe el catálogo "DEP" sin catálogo hijo
  Cuando creo el catálogo "MUN" con PARENT "DEP"
  Entonces el catálogo "MUN" queda registrado como hijo de "DEP"
  Y al consultar el catálogo hijo de "DEP" se obtiene "MUN"

Escenario: Rechazar código duplicado
  Dado que existe el catálogo "DEP"
  Cuando intento crear otro catálogo con código "DEP"
  Entonces el sistema rechaza la operación con el error E01

Escenario: Rechazar catálogo sin campos
  Cuando intento crear un catálogo sin campos
  Entonces el sistema rechaza la operación con el error E02

Escenario: Rechazar catálogo sin campo KEY
  Cuando intento crear un catálogo cuyos campos son todos FIELD
  Entonces el sistema rechaza la operación con el error E03

Escenario: Rechazar nombres de campo repetidos
  Cuando intento crear un catálogo con dos campos llamados "nombre"
  Entonces el sistema rechaza la operación con el error E04

Escenario: Rechazar restricción de tipo incoherente
  Cuando intento crear un catálogo con un campo NUMBER {100 : 1}
  Entonces el sistema rechaza la operación con el error E05

Escenario: Rechazar padre inexistente
  Dado que no existe el catálogo "XYZ"
  Cuando intento crear un catálogo con PARENT "XYZ"
  Entonces el sistema rechaza la operación con el error E06

Escenario: Rechazar un segundo hijo para el mismo padre
  Dado que el catálogo "DEP" ya es padre de "MUN"
  Cuando intento crear el catálogo "CAN" con PARENT "DEP"
  Entonces el sistema rechaza la operación con el error E07

Escenario: Catálogo creado con fecha HASTA vencida
  Cuando creo un catálogo con vigencia ": 01/01/2020"
  Entonces el catálogo queda registrado con estado INACTIVE
```

## HU-ADM-01-02 — Consultar catálogo por código

**Como** Administrador de Catálogos o Proceso Consumidor
**Quiero** recuperar un catálogo a partir de su código
**Para** conocer su definición completa antes de usarlo o modificarlo

- **Flujo:** FA01 · **Reglas:** RN22, RN23

**Criterios de aceptación**

```gherkin
Escenario: Consultar un catálogo existente
  Dado que existe el catálogo "MUN" hijo de "DEP"
  Cuando consulto el catálogo "MUN"
  Entonces obtengo su código, nombre, padre "DEP", actividad, vigencia
    y la lista de campos con su encabezado, tipo, restricción y posición
  Y se indica si el catálogo contiene registros

Escenario: Consultar un código inexistente
  Cuando consulto el catálogo "NOEXISTE"
  Entonces el sistema reporta el error E09
```

## HU-ADM-01-03 — Actualizar catálogo

**Como** Administrador de Catálogos
**Quiero** modificar el nombre, padre, actividad, vigencia o campos de un catálogo
**Para** mantener su definición al día sin perder su identidad

- **Flujo:** FA02 · **Reglas:** RN03, RN04, RN05, RN19, RN20, RN21, RN23

**Criterios de aceptación**

```gherkin
Escenario: Actualizar el nombre de un catálogo
  Dado que existe el catálogo "DEP"
  Cuando cambio su nombre a "Departamentos de El Salvador"
  Entonces el catálogo conserva el código "DEP" con el nuevo nombre
  Y se muestra el mensaje M02

Escenario: Rechazar cambio de código
  Cuando intento cambiar el código del catálogo "DEP" a "DPT"
  Entonces el sistema rechaza la operación con el error E10

Escenario: Modificar campos de un catálogo sin registros
  Dado que el catálogo "MUN" no contiene registros
  Cuando agrego el campo FIELD "area" NUMBER {0 : 5000}
  Entonces el catálogo "MUN" queda con el nuevo campo

Escenario: Rechazar modificación de campos con registros
  Dado que el catálogo "DEP" contiene registros
  Cuando intento modificar sus campos
  Entonces el sistema rechaza la operación con el error E11

Escenario: Rechazar ciclo en la jerarquía
  Dado que "DEP" es padre de "MUN"
  Cuando intento asignar "MUN" como padre de "DEP"
  Entonces el sistema rechaza la operación con el error E12

Escenario: Actualizar la actividad a INACTIVE dispara la inactivación
  Cuando cambio la actividad del catálogo "DEP" a INACTIVE
  Entonces se ejecuta la inactivación descrita en HU-ADM-01-04
```

## HU-ADM-01-04 — Inactivar catálogo

**Como** Administrador de Catálogos
**Quiero** inactivar un catálogo
**Para** retirarlo de uso sin perder su información histórica

- **Flujo:** FA03 · **Reglas:** RN06, RN12, RN13, RN16

**Criterios de aceptación**

```gherkin
Escenario: Inactivar cambiando el indicador de actividad
  Dado que el catálogo "MUN" está ACTIVE sin fecha HASTA
  Cuando cambio su actividad a INACTIVE
  Entonces su fecha HASTA es la fecha actual
  Y todos los registros de "MUN" quedan INACTIVE
  Y se muestra el mensaje M03

Escenario: Inactivar fijando la fecha HASTA en el pasado
  Cuando establezco la fecha HASTA del catálogo "MUN" en una fecha pasada
  Entonces el catálogo "MUN" y todos sus registros quedan INACTIVE

Escenario: Propagación al catálogo hijo
  Dado que "DEP" es padre de "MUN" y ambos están ACTIVE con registros
  Cuando inactivo el catálogo "DEP"
  Entonces el catálogo "MUN" queda INACTIVE
  Y todos los registros de "DEP" y de "MUN" quedan INACTIVE

Escenario: Un catálogo no puede eliminarse
  Cuando intento eliminar el catálogo "DEP"
  Entonces el sistema rechaza la operación con el error E19
  Y el catálogo sigue almacenado
```

## HU-ADM-01-05 — Consultar el Catálogo Maestro

**Como** Administrador de Catálogos o Proceso Consumidor
**Quiero** verificar si existe un catálogo por código o nombre y listar todos los catálogos
**Para** saber qué catálogos están disponibles

- **Flujo:** FA04 · **Reglas:** RN09

**Criterios de aceptación**

```gherkin
Escenario: Verificar existencia por código
  Dado que existe el catálogo "DEP"
  Cuando consulto si existe un catálogo con código "DEP"
  Entonces el sistema indica que existe y retorna su código y nombre

Escenario: Verificar existencia por nombre
  Cuando consulto si existe un catálogo con nombre "Municipios"
  Entonces el sistema indica si existe

Escenario: Catálogo no definido
  Cuando consulto si existe un catálogo con código "NOEXISTE"
  Entonces el sistema indica que no existe

Escenario: Listar catálogos
  Cuando solicito la lista de catálogos
  Entonces obtengo todos los catálogos del Catálogo Maestro
    con su código, nombre, padre y actividad
```

## HU-ADM-01-06 — Consultar catálogo hijo

**Como** Administrador de Catálogos o Proceso Consumidor
**Quiero** obtener el catálogo hijo de un catálogo padre
**Para** navegar la jerarquía de catálogos

- **Flujo:** FA05 · **Reglas:** RN05, RN17

**Criterios de aceptación**

```gherkin
Escenario: Catálogo con hijo
  Dado que "DEP" es padre de "MUN"
  Cuando consulto el catálogo hijo de "DEP"
  Entonces obtengo el código "MUN" y el nombre "Municipios"

Escenario: Catálogo sin hijo
  Dado que "MUN" no tiene catálogo hijo
  Cuando consulto el catálogo hijo de "MUN"
  Entonces el resultado es nulo

Escenario: Catálogo inexistente
  Cuando consulto el catálogo hijo de "NOEXISTE"
  Entonces el sistema reporta el error E09
```

## HU-ADM-01-07 — Crear registro

**Como** Administrador de Catálogos
**Quiero** agregar un registro a un catálogo
**Para** poblar el catálogo con valores válidos

- **Flujo:** FA06 · **Reglas:** RN01, RN03, RN05, RN11, RN14, RN15, RN16

**Criterios de aceptación**

```gherkin
Escenario: Crear registro en un catálogo plano
  Dado el catálogo "DEP" con campos KEY "codigo" STRING {2} y FIELD "nombre" STRING {60}
  Cuando creo el registro codigo="01", nombre="Ahuachapán" sin vigencia
  Entonces el registro queda almacenado con estado ACTIVE
  Y se muestra el mensaje M04

Escenario: Crear registro en un catálogo hijo
  Dado que "MUN" es hijo de "DEP" y existe el registro "01" en "DEP"
  Cuando creo el registro codigo="0101", nombre="Ahuachapán Norte" con registro padre "01"
  Entonces el registro queda almacenado vinculado al registro padre "01"

Escenario: Rechazar registro hijo sin registro padre válido
  Dado que "MUN" es hijo de "DEP"
  Cuando creo un registro en "MUN" con registro padre "99" inexistente en "DEP"
  Entonces el sistema rechaza la operación con el error E16

Escenario: Rechazar valores faltantes
  Cuando creo un registro en "DEP" sin el valor del campo "nombre"
  Entonces el sistema rechaza la operación con el error E13

Esquema del escenario: Rechazar valores que no cumplen el tipo
  Cuando creo un registro con el valor <valor> para un campo <tipo>
  Entonces el sistema rechaza la operación con el error E14
  Ejemplos:
    | tipo                       | valor        |
    | NUMBER {0 : 100}           | 150          |
    | STRING {2}                 | "ABC"        |
    | DATE {01/01/2020 : 31/12/2030} | 01/01/2035 |
    | ENUM {"URBANO", "RURAL"}   | "MIXTO"      |

Escenario: Rechazar KEY duplicada
  Dado que existe el registro "01" en "DEP"
  Cuando creo otro registro con codigo="01" en "DEP"
  Entonces el sistema rechaza la operación con el error E15

Escenario: Rechazar KEY compuesta duplicada
  Dado el catálogo "TAR" con campos KEY "anio" NUMBER {2000 : 2100}, KEY "mes" NUMBER {1 : 12} y FIELD "valor" NUMBER {0 : 1000}
  Y existe el registro anio=2026, mes=1
  Cuando creo otro registro con anio=2026, mes=1
  Entonces el sistema rechaza la operación con el error E15

Escenario: Aceptar KEY compuesta distinta
  Dado que en "TAR" existe el registro anio=2026, mes=1
  Cuando creo el registro anio=2026, mes=2, valor=10
  Entonces el registro queda almacenado

Escenario: Rechazar registro en un catálogo inactivo
  Dado que el catálogo "DEP" está INACTIVE
  Cuando intento crear un registro en "DEP"
  Entonces el sistema rechaza la operación con el error E21
```

## HU-ADM-01-08 — Actualizar registro

**Como** Administrador de Catálogos
**Quiero** modificar los campos no KEY de un registro
**Para** corregir o actualizar su información sin alterar su identidad

- **Flujo:** FA07 · **Reglas:** RN18

**Criterios de aceptación**

```gherkin
Escenario: Actualizar un campo no KEY
  Dado el registro "01" del catálogo "DEP"
  Cuando cambio su campo "nombre" a "Ahuachapán (actualizado)"
  Entonces el registro conserva la KEY "01" con el nuevo nombre
  Y se muestra el mensaje M05

Escenario: Rechazar modificación del campo KEY
  Cuando intento cambiar el campo KEY "codigo" del registro "01" a "02"
  Entonces el sistema rechaza la operación con el error E17

Escenario: Rechazar valor fuera de restricción
  Cuando cambio el campo "nombre" del registro "01" por un texto de 80 caracteres
  Entonces el sistema rechaza la operación con el error E14
```

## HU-ADM-01-09 — Inactivar registro

**Como** Administrador de Catálogos
**Quiero** inactivar un registro de un catálogo
**Para** retirarlo de uso conservando su historial

- **Flujo:** FA08 · **Reglas:** RN12, RN14, RN16

**Criterios de aceptación**

```gherkin
Escenario: Inactivar cambiando el indicador de actividad
  Dado el registro "01" ACTIVE del catálogo "DEP"
  Cuando cambio su actividad a INACTIVE
  Entonces su fecha HASTA es la fecha actual
  Y el registro queda INACTIVE
  Y se muestra el mensaje M06

Escenario: Inactivar fijando la fecha HASTA
  Cuando establezco la fecha HASTA del registro "01" en la fecha actual o una fecha pasada
  Entonces el registro queda INACTIVE

Escenario: Propagación a los registros hijos
  Dado que "DEP" es padre de "MUN"
  Y el registro "01" de "DEP" tiene en "MUN" los registros hijos "0101" y "0102" en estado ACTIVE
  Cuando inactivo el registro "01" de "DEP"
  Entonces los registros "0101" y "0102" de "MUN" quedan INACTIVE
  Y el mensaje M06 indica que se inactivaron 2 registros hijos

Escenario: Un registro no puede eliminarse
  Cuando intento eliminar el registro "01" del catálogo "DEP"
  Entonces el sistema rechaza la operación con el error E19
  Y el registro sigue almacenado
```

## HU-ADM-01-10 — Buscar registro por clave

**Como** Proceso Consumidor o Administrador de Catálogos
**Quiero** obtener un registro de un catálogo a partir de su valor KEY y los campos que necesito
**Para** mostrar o validar la información de ese registro

- **Flujo:** FA09 · **Reglas:** RN03, RN07

**Criterios de aceptación**

```gherkin
Antecedentes:
  Dado el catálogo "MUN" con campos en este orden:
    KEY "codigo", FIELD "nombre", FIELD "categoria", FIELD "poblacion"
  Y el registro codigo="0101", nombre="Ahuachapán Norte", categoria="URBANO", poblacion=12000, ACTIVE

Escenario: Búsqueda sin conjunto de campos
  Cuando busco en "MUN" el registro "0101" sin indicar campos
  Entonces el resultado contiene el argumento "0101" y el catálogo "MUN"
  Y el Conjunto de Campos es ("codigo", "nombre")
  Y el Conjunto de Resultados tiene un elemento con valores ("0101", "Ahuachapán Norte") y actividad ACTIVE

Escenario: Búsqueda con campos que no incluyen la KEY
  Cuando busco en "MUN" el registro "0101" con los campos ("poblacion", "categoria")
  Entonces el Conjunto de Campos es ("codigo", "poblacion", "categoria")
  Y el Conjunto de Resultados tiene un elemento con valores ("0101", 12000, "URBANO") y actividad ACTIVE

Escenario: Registro no encontrado
  Cuando busco en "MUN" el registro "9999"
  Entonces el resultado contiene el argumento "9999", el catálogo "MUN" y el Conjunto de Campos
  Y el Conjunto de Resultados está vacío

Escenario: Campo no definido
  Cuando busco en "MUN" el registro "0101" con los campos ("altitud")
  Entonces el sistema reporta el error E18

Escenario: Catálogo inexistente
  Cuando busco en "NOEXISTE" el registro "0101"
  Entonces el sistema reporta el error E09

Escenario: Búsqueda con clave compuesta
  Dado el catálogo "TAR" con campos KEY "anio", KEY "mes" y FIELD "valor"
  Y el registro anio=2026, mes=1, valor=15.5
  Cuando busco en "TAR" el registro (2026, 1) sin indicar campos
  Entonces el Conjunto de Campos es ("anio", "mes", "valor")
  Y el Conjunto de Resultados tiene un elemento con valores (2026, 1, 15.5) y su indicador de actividad
```

## HU-ADM-01-11 — Listar registros de un catálogo

**Como** Proceso Consumidor o Administrador de Catálogos
**Quiero** obtener todos los registros de un catálogo con los campos que necesito
**Para** poblar listas de selección o revisar el contenido del catálogo

- **Flujo:** FA10 · **Reglas:** RN07-a, RN08

**Criterios de aceptación**

```gherkin
Escenario: Listar sin conjunto de campos
  Dado el catálogo "DEP" con 14 registros, 2 de ellos INACTIVE
  Cuando solicito la lista de registros de "DEP" sin indicar campos
  Entonces el resultado contiene el catálogo "DEP" y el Conjunto de Campos ("codigo", "nombre")
  Y el Conjunto de Resultados tiene 14 elementos, cada uno con sus valores y su indicador de actividad

Escenario: Listar con conjunto de campos
  Cuando solicito la lista de registros de "MUN" con los campos ("nombre")
  Entonces el Conjunto de Campos es ("codigo", "nombre")

Escenario: Catálogo sin registros
  Dado que el catálogo "CAN" no tiene registros
  Cuando solicito la lista de registros de "CAN"
  Entonces el Conjunto de Resultados está vacío

Escenario: Campo no definido
  Cuando solicito la lista de registros de "DEP" con los campos ("altitud")
  Entonces el sistema reporta el error E18
```

## HU-ADM-01-12 — Buscar registros hijos

**Como** Proceso Consumidor o Administrador de Catálogos
**Quiero** obtener los registros hijos de un registro de un catálogo padre
**Para** construir selecciones dependientes (por ejemplo, municipios de un departamento)

- **Flujo:** FA11 · **Reglas:** RN03, RN05, RN07-a, RN24

**Criterios de aceptación**

```gherkin
Antecedentes:
  Dado que "DEP" es padre de "MUN"
  Y el registro "01" de "DEP" tiene en "MUN" los registros hijos "0101" y "0102"

Escenario: Registro con hijos
  Cuando busco los registros hijos del registro "01" del catálogo "DEP" sin indicar campos
  Entonces el resultado contiene el argumento "01", el catálogo padre "DEP" y el catálogo hijo "MUN"
  Y el Conjunto de Campos es ("codigo", "nombre") del catálogo "MUN"
  Y el Conjunto de Resultados tiene 2 elementos ("0101" y "0102") con su indicador de actividad

Escenario: Registro sin hijos
  Dado que el registro "02" de "DEP" no tiene registros hijos
  Cuando busco los registros hijos del registro "02" del catálogo "DEP"
  Entonces el Conjunto de Resultados está vacío

Escenario: Catálogo sin catálogo hijo
  Dado que "MUN" no tiene catálogo hijo
  Cuando busco los registros hijos del registro "0101" del catálogo "MUN"
  Entonces el catálogo hijo del resultado es nulo
  Y el Conjunto de Resultados está vacío

Escenario: Campo no definido en el catálogo hijo
  Cuando busco los registros hijos del registro "01" de "DEP" con los campos ("altitud")
  Entonces el sistema reporta el error E18
```
