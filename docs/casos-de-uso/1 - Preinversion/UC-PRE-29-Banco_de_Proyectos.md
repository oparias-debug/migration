---
id: CU-PRE-29
codigo: CU-PRE-29
nombre: Banco de Proyectos
modulo: Preinversión
submodulo: Gestión del Proyecto
version: "1.1"
fuente_pdf: CU-PRE-29_Banco_de_Proyectos_AGO_2025_V1_F.pdf
fuente_anexo_xlsx: "CU-PRE-29___ANEXO__Banco_de_proyectos.xlsx (hoja 'Hoja 1'; mockup nativo/editable de la pantalla Anexo A.1)"
pagina_inicio: 1
pagina_fin: 5

nota_version: >
  La versión 1.1 incorpora el archivo Excel anexo
  `CU-PRE-29___ANEXO__Banco_de_proyectos.xlsx`, aportado posteriormente.
  Se verificó, celda por celda, que su contenido (título "BANCO DE
  PROYECTOS", filtro "UNIDAD EJECUTORA: MOPT", botón "BUSCAR", y las 5
  filas de ejemplo con CUP 9010, 9009, 9008, 9007 y 9006) coincide
  exactamente con el mockup ya transcrito a partir del PDF en la versión
  1.0, incluyendo el truncamiento con puntos suspensivos ("...") de los
  nombres de proyecto, que también está presente en el archivo Excel
  original (no es un artefacto de la herramienta de extracción del PDF).
  No se modificó ningún otro contenido de la versión 1.0; solo se
  referencia la fuente adicional y se deja constancia de la verificación.

actor_principal: "No especificado en el documento como actor principal distinguido; el campo 'Actores' de la Identificación lista conjuntamente a Técnico URP, Técnico PRE, Coordinador PRE y Usuarios Internos, sin jerarquía explícita (ver Observaciones)."

actores_secundarios: []

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01 Registro de Proyectos"]

casos_relacionados: []

roles: ["Técnico URP", "Técnico PRE", "Coordinador PRE",  "Usuarios Internos"]

pantallas: ["Banco de Proyectos (Anexo A.1)", "Ficha del proyecto (CU-PRE-3.6, referenciada en FA01)"]

procesos: []

servicios_externos: []

entidades: ["Proyecto", "Unidad Ejecutora", "Ficha del Proyecto", "Opinión Técnica (OT)", "Prioridad del Proyecto"]

catalogos: ["Estados del proyecto (catálogo completo remitido a UC-PRE-03 'Captura de proyectos'; solo se documentan los valores observados en el mockup)", "Etapas del proyecto (solo se documentan los valores observados en el mockup)"]

palabras_clave: ["banco de proyectos", "CUP", "unidad ejecutora", "estado del proyecto", "prioridad", "ficha del proyecto", "opinión técnica", "viabilidad", "elegibilidad"]

ultima_actualizacion: "AGO 2025"

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    FA01:
      pagina: 3
  reglas_negocio:
    RN01_a_RN05:
      pagina: 4
  anexos:
    A1:
      nombre: "Banco de Proyectos"
      pagina: 4
    B:
      nombre: "Requerimientos Funcionales - Formatos"
      pagina: 4
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Banco de Proyectos |
| Código | CU-PRE-29 |
| Módulo | Preinversión |
| Fuente | CU-PRE-29_Banco_de_Proyectos_AGO_2025_V1_F.pdf; complementado con el anexo Excel `CU-PRE-29___ANEXO__Banco_de_proyectos.xlsx` (ver "Pantallas") |
| Versión | 1.1 |

**Campos requeridos (según el PDF):**
- Buscador
- Unidad Ejecutora
- Código
- Nombre
- Inversión Estimada (según OT)
- Prioridad (base 100 puntos)

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección o campo explícito titulado "Objetivo" dentro de la Identificación del caso de uso.

---

# Descripción

Muestra una tabla con el listado de proyectos que cuentan con Viabilidad, Elegibilidad y que cuentan con Opinión Técnica para cualquier etapa.

# Actor Principal

El campo "Actores" de la Identificación del caso de uso lista conjuntamente:
- Técnico URP
- Técnico PRE
- Coordinador PRE
- Usuarios Internos

> Nota de ambigüedad: el documento no distingue explícitamente cuál de estos actores es "principal" y cuáles son "secundarios"; todos figuran en un mismo listado bajo el campo "Actores" de la Identificación, y el Flujo Básico (paso 2) los agrupa como "Todos los actores".

---

# Actores Secundarios

> No especificado en el documento. Todos los actores mencionados (Técnico URP, Técnico PRE, Coordinador PRE, Usuarios Internos) están listados conjuntamente en el campo "Actores" de la Identificación, sin distinción entre principales y secundarios (ver "Actor Principal" arriba).

---

# Disparador

> No especificado en el documento. La Identificación del caso de uso no contiene un campo explícito titulado "Disparador".

---

# Precondiciones

1. CU-PRE-01 Registro de Proyectos
2. UC-PRE-03 Captura de proyectos
3. CU-PRE-17 Presupuesto de inversión
4. CU-PRE-24 Viabilidad
5. CU-PRE-25 Elegibilidad
6. CU-PRE-26 Opinión Técnica

> Nota de ambigüedad: el documento lista estos seis casos de uso bajo el campo "Precondiciones" sin un verbo explícito (p. ej. "contar con", "haber ejecutado") que aclare la redacción formal de la condición. Se incorporan como precondiciones por continuidad de formato del campo "Precondiciones" del documento, pero la redacción exacta de la condición no está especificada.

---

# Flujo Principal

## FB — Flujo Básico

1. Sistema. Muestra en "Banco de Proyectos" los proyectos viabilizados, priorizados, con Opinión Técnica para Ejecución, en ejecución y finalizados.
2. Todos los actores. Dan clic en el CUP del proyecto que deseen visualizar.

---

# Flujos Alternos

## FA-01 — Clic en CUP

**Condición**

> No especificado en el documento.

**Flujo**

2.1. Sistema. Muestra la Ficha del proyecto (CU-PRE-3.6) que podrá descargarse en PDF o en Excel.

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no incluye una sección explícita titulada "Excepciones" con Código / Descripción / Consecuencia.

# Postcondiciones

1. Casos de uso Programación y/o Seguimiento

> Nota de ambigüedad: el documento describe la Postcondición de forma genérica como "Casos de uso Programación y/o Seguimiento", sin especificar el código ni el nombre completo de un caso de uso concreto, a diferencia de otras referencias del documento que sí incluyen código (p. ej. "CU-PRE-01 Registro de Proyectos"). Ver Observaciones.

---

# Reglas de Negocio

## RN01
**Descripción:** Los actores podrán visualizar los proyectos del listado del "Banco de Proyectos" según credenciales.
**Origen:** No especificado en el documento.

## RN02
**Descripción:** En el campo "Búsqueda" podrán buscarse proyectos por Código, por Nombre y por Institución (Anexo A.1).
**Origen:** No especificado en el documento.

## RN03
**Descripción:** El Banco de proyectos deberá contener todos los proyectos registrados en el sistema y deberá mostrar su respectivo estado.
**Origen:** No especificado en el documento.

## RN04
**Descripción:** Los proyectos que se listen en el banco no deben tener botones de ajuste, sólo botones de ingreso a consulta (visualización de las pantallas, no editables), descarga de ficha en PDF y de bitácora transaccional.
**Origen:** No especificado en el documento.

## RN05
**Descripción:** Los Estados que podrá mostrar un proyecto se retoman del UC-PRE-03 "Captura de proyectos".
**Origen:** No especificado en el documento.

---

# Campos

## Sección "Banco de Proyectos" (Anexo B.1)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Unidad Ejecutora | Para el Técnico URP se mostrará según sus credenciales y tendrá la opción de selección bloqueada en su respectiva Unidad Ejecutora. Para el Técnico PRE se mostrará habilitada la herramienta de selección y podrá consultar el Banco de proyectos de todas las Unidades Ejecutoras disponibles en el SIIP. | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| CUP | Muestra el CUP del proyecto, que proviene de CU-PRE-01 "Registro de Proyectos". | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: la sección "Campos requeridos" de la Identificación denomina este campo "Código", no "CUP" — ver Observaciones. |
| Nombre del proyecto | Mostrará el nombre del proyecto, que proviene de CU-PRE-01 "Registro de Proyectos". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Etapa | Muestra la etapa en que se encuentra el proyecto actualmente. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Inversión estimada | Campo que muestra el monto de inversión del proyecto, según la última OT emitida. Lo toma del campo "Total Inversión" del Anexo A1 del CU-PRE-17 "Presupuesto de inversión". El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Estado | Muestra el estado del proyecto (estados según UC-PRE-03 "Captura de proyectos"). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Catálogo completo de estados no incluido en este documento (ver RN05 y Catálogos Detectados). |
| Prioridad | Campo que muestra la calificación de la prioridad del proyecto realizada por el viabilizador. Toma el dato del campo "Valoración de la prioridad del proyecto" del Anexo A1 del CU-PRE-25 "Elegibilidad". | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Total | El Sistema totalizará los montos de inversión del proyecto. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: este campo no aparece representado en el mockup del Anexo A.1 — ver Observaciones. |

> Nota de ambigüedad: la sección "Campos requeridos" de la Identificación menciona un campo "Buscador" que no tiene una fila propia en la tabla de Formatos del Anexo B.1; su tipo, formato y comportamiento no están especificados en el documento (ver Observaciones y Datos Pendientes de Definir).

---

# Validaciones

> No especificado en el documento. El documento no describe validaciones de campos ni mensajes de validación asociados a este caso de uso.

---

# Errores

> No especificado en el documento. El documento no presenta una tabla de códigos de error con Código / Descripción / Acción esperada.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Técnico URP | Visualizar los proyectos del listado del Banco de Proyectos según sus credenciales, con la opción de selección de Unidad Ejecutora bloqueada a su propia Unidad Ejecutora | RN01; Anexo B.1, campo "Unidad Ejecutora" |
| Técnico PRE | Visualizar los proyectos del listado del Banco de Proyectos según sus credenciales, con la herramienta de selección de Unidad Ejecutora habilitada para consultar todas las Unidades Ejecutoras disponibles en el SIIP | RN01; Anexo B.1, campo "Unidad Ejecutora" |
| Coordinador PRE | Visualizar los proyectos del listado del Banco de Proyectos según credenciales | RN01 |
| Viabilizador | Visualizar los proyectos del listado del Banco de Proyectos según credenciales | RN01 |
| Usuarios Internos | Visualizar los proyectos del listado del Banco de Proyectos según credenciales | RN01 |
| Todos los actores | Dar clic en el CUP de un proyecto para visualizar su Ficha (consulta); no cuentan con botones de ajuste, solo de ingreso a consulta, descarga de ficha en PDF y de bitácora transaccional | FB paso 2; RN04 |

> Nota de ambigüedad: para Coordinador PRE, Viabilizador y Usuarios Internos, el documento no detalla diferencias de permisos más allá de la regla general RN01 ("según credenciales"); no se especifica en qué consisten esas credenciales ni si existen restricciones adicionales equivalentes a las descritas para Técnico URP y Técnico PRE respecto del campo "Unidad Ejecutora".

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 Registro de Proyectos (precondición; fuente de los campos "CUP" y "Nombre del proyecto")
- UC-PRE-03 Captura de proyectos (precondición; fuente del catálogo de Estados, RN05)
- CU-PRE-17 Presupuesto de inversión (precondición; fuente del campo "Inversión estimada")
- CU-PRE-24 Viabilidad (precondición)
- CU-PRE-25 Elegibilidad (precondición; fuente del campo "Prioridad")
- CU-PRE-26 Opinión Técnica (precondición)
- Casos de uso Programación y/o Seguimiento (postcondición, sin código específico — ver Observaciones)
- CU-PRE-3.6 Ficha del proyecto (referenciado en el Flujo Alternativo FA-01)

**Procesos relacionados:**
> No especificado en el documento (más allá de los casos de uso listados).

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Anexo A.1 — Banco de Proyectos

- **Nombre:** Banco de Proyectos.
- **Descripción:** Pantalla que muestra el listado de todos los proyectos registrados en el sistema, con un filtro de Unidad Ejecutora y un buscador, mostrando el estado y la prioridad de cada proyecto.
- **Campos:** Unidad Ejecutora (selector), Buscador (según Campos requeridos y RN02), y en la tabla de resultados: CUP, Nombre del proyecto, Etapa, Inversión Estimada, Estado, Prioridad, Ficha del Proyecto (columna con ícono).
- **Botones:** "BUSCAR"; ícono de "Ficha del Proyecto" (ojo) en cada fila, que da acceso al Flujo Alternativo FA-01.
- **Acciones:** Seleccionar Unidad Ejecutora; buscar proyectos; dar clic en el CUP o en el ícono de "Ficha del Proyecto" para visualizar el detalle del proyecto (FA-01); descargar la ficha en PDF o Excel; descargar bitácora transaccional (RN04, sin control específico visible en el mockup).

### Ejemplo de datos mostrados en el mockup (Anexo A.1)

**UNIDAD EJECUTORA:** MOPT (selector desplegable)

**Botón:** BUSCAR

| CUP | Nombre del proyecto | Etapa | Inversión Estimada | Estado | Prioridad | Ficha del Proyecto |
|---|---|---|---|---|---|---|
| 9010 | Ampliación carretera CA02E, Tramo: km 65+350 ... | Ejecución | $50,000,000.00 | Proyecto con OT | 85 | (ícono de ojo) |
| 9009 | Adquisición de flota para movilidad, ... | Ejecución | $1,500,000.00 | Proyecto con OT | 70 | (ícono de ojo) |
| 9008 | Prolongación de calle Las Truchas hacia carretera LIB12... | Diseño | $456,000.00 | En proceso de OT | 96 | (ícono de ojo) |
| 9007 | Rehabilitación de taludes en tramos de la Carretera Panamericana ... | Factibilidad | $38,000,000.00 | Observado | 89 | (ícono de ojo) |
| 9006 | Construcción y equipamiento del Centro de Monitoreo ... | Prefactibilidad | $956,000,210.00 | Proyecto viable | 78 | (ícono de ojo) |

> Nota: los nombres de proyecto en la tabla aparecen truncados con puntos suspensivos ("...") en el mockup original; se transcriben tal como se muestran, sin completar el texto faltante para no inventar información no visible.

> **Verificación (v1.1):** el anexo Excel `CU-PRE-29___ANEXO__Banco_de_proyectos.xlsx` (hoja "Hoja 1") contiene el mismo mockup en formato nativo/editable. Se comparó celda por celda contra la tabla anterior (transcrita del PDF) y coincide exactamente: mismo filtro "UNIDAD EJECUTORA: MOPT", mismas 5 filas (CUP 9010, 9009, 9008, 9007, 9006), mismos valores de Etapa/Inversión Estimada/Estado/Prioridad, y el mismo truncamiento con puntos suspensivos en los nombres de proyecto (confirmando que la truncación existe en el archivo original y no es un artefacto de la extracción del PDF). No se detectaron discrepancias entre ambas fuentes.

---

# Mensajes al Usuario

> No especificado en el documento. El documento no describe mensajes emergentes, de error o de confirmación asociados a este caso de uso.

---

# Observaciones

1. RN02 indica que en el campo "Búsqueda" podrán buscarse proyectos "por Código, por Nombre y por Institución", pero el mockup del Anexo A.1 solo muestra un selector "UNIDAD EJECUTORA" y un botón "BUSCAR", sin un campo de texto visible para búsqueda por Código o Nombre, y sin un campo o selector explícito etiquetado "Institución". **Resuelto (RQ-T-01, ronda 3):** el negocio confirmó que "Institución Ejecutora" y "Unidad Ejecutora" no son sinónimos, sino conceptos con relación 1:N (una Institución Ejecutora agrupa una o más Unidades Ejecutoras). Esto no aclara, sin embargo, cuál de los dos conceptos corresponde realmente al filtro/selector "UNIDAD EJECUTORA" visible en el mockup frente a la palabra "Institución" usada en el texto de RN02 — esa ambigüedad puntual de este documento (¿la RN02 debió decir "Unidad Ejecutora" en lugar de "Institución", o falta en el mockup un campo/selector adicional para "Institución"?) no queda resuelta por RQ-T-01 y sigue sin aclarar. Tampoco se aclara dónde se ubicaría el campo de búsqueda por Código/Nombre.

2. La sección "Campos requeridos" de la Identificación lista "Código" como campo, mientras que tanto el mockup del Anexo A.1 como la tabla de Formatos del Anexo B.1 usan la etiqueta "CUP" para el campo equivalente. El documento no aclara si "Código" y "CUP" son el mismo campo o corresponden a campos distintos.

3. La sección "Campos requeridos" de la Identificación lista un campo "Buscador", pero la tabla de Formatos del Anexo B.1 no incluye una fila para dicho campo (solo describe "Unidad Ejecutora", "CUP", "Nombre del proyecto", "Etapa", "Inversión estimada", "Estado", "Prioridad" y "Total"). El documento no describe el tipo, formato ni comportamiento del campo "Buscador".

4. RN04 menciona que los proyectos listados deben tener botones de "descarga de ficha en PDF y de bitácora transaccional", pero el Flujo Alternativo FA-01 solo describe la descarga de la Ficha del proyecto "en PDF o en Excel", sin mencionar la descarga de "bitácora transaccional" ni el formato Excel en RN04. El mockup del Anexo A.1 tampoco muestra un botón o ícono diferenciado para la descarga de bitácora transaccional. El documento no aclara esta discrepancia.

5. El campo "Total" descrito en la tabla de Formatos del Anexo B.1 ("El Sistema totalizará los montos de inversión del proyecto") no aparece representado en el mockup del Anexo A.1 (la tabla de ejemplo no muestra una fila o campo de total). El documento no aclara en qué parte de la pantalla se ubicaría este campo.

6. Las Postcondiciones del caso de uso se describen de forma genérica como "Casos de uso Programación y/o Seguimiento", sin especificar el código ni el nombre completo de un caso de uso concreto, a diferencia de otras secciones del documento que sí referencian casos de uso con su código (p. ej. "CU-PRE-01 Registro de Proyectos"). El documento no aclara a qué caso(s) de uso específico(s) se refiere.

7. El Flujo Alternativo FA-01 refiere a "la Ficha del proyecto (CU-PRE-3.6)"; este código de caso de uso ("CU-PRE-3.6") no forma parte de la lista de Precondiciones ni se menciona en otra parte del documento, por lo que no puede verificarse su relación con los demás CU-PRE listados (p. ej. UC-PRE-03).

8. Los nombres de proyecto mostrados en la tabla de ejemplo del mockup del Anexo A.1 aparecen truncados con puntos suspensivos ("..."); el documento no provee el texto completo de dichos nombres.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---|---|---|
| Proyecto | Entidad principal listada en el "Banco de Proyectos", identificada por su CUP, con Nombre, Etapa, Inversión Estimada, Estado y Prioridad. | Consulta/listado de todos los proyectos registrados en el sistema (FB paso 1; RN03); visualización de detalle mediante clic en el CUP (FB paso 2). |
| Unidad Ejecutora | Entidad organizacional utilizada como filtro de selección en la pantalla "Banco de Proyectos" (RQ-T-01, ronda 3: distinta de "Institución Ejecutora", que puede agrupar una o más Unidades Ejecutoras). | Consulta/selección como filtro (Anexo B.1, campo "Unidad Ejecutora"); uso como criterio de búsqueda por "Institución" (RN02) — ver Observaciones, numeral 1, sobre esta ambigüedad puntual aún sin resolver. |
| Ficha del Proyecto | Documento/consulta detallada de un proyecto, correspondiente al CU-PRE-3.6. | Consulta y descarga en PDF o Excel (FA-01, paso 2.1). |
| Bitácora transaccional | Registro de transacciones del proyecto, mencionado como elemento descargable. | Descarga (RN04); sin flujo ni pantalla que detalle su generación o contenido. |
| Opinión Técnica (OT) | Registro que determina el monto de inversión estimada mostrado en el Banco de Proyectos. | Consulta — fuente del campo "Inversión estimada" (Anexo B.1, remite al Anexo A1 de CU-PRE-17 "Presupuesto de inversión"). |
| Prioridad del Proyecto | Calificación numérica (base 100 puntos) asignada por el Viabilizador. | Consulta — fuente del campo "Prioridad" (Anexo B.1, remite al Anexo A1 de CU-PRE-25 "Elegibilidad"). |

---

# Catálogos Detectados

## Catálogo Estados del proyecto

> El documento indica en RN05 que "Los Estados que podrá mostrar un proyecto se retoman del UC-PRE-03 'Captura de proyectos'"; el catálogo completo de estados no está incluido en este documento y no puede transcribirse íntegramente sin invadir el contenido de UC-PRE-03. A continuación se listan únicamente los valores de Estado observados en el mockup de ejemplo del Anexo A.1, sin que esto constituya el catálogo completo:

| Estado (observado en el mockup) |
|---|
| Proyecto con OT |
| En proceso de OT |
| Observado |
| Proyecto viable |

## Catálogo Etapas del proyecto

> El documento no incluye una tabla formal de catálogo de "Etapa". A continuación se listan únicamente los valores de Etapa observados en el mockup de ejemplo del Anexo A.1, sin que esto constituya el catálogo completo:

| Etapa (observada en el mockup) |
|---|
| Ejecución |
| Diseño |
| Factibilidad |
| Prefactibilidad |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|---|---|---|
| Listado de proyectos viabilizados, priorizados, con Opinión Técnica para Ejecución, en ejecución y finalizados | Sistema | Pantalla "Banco de Proyectos" (FB paso 1) |
| Visualización de la Ficha del proyecto | Sistema | Todos los actores (FA-01, paso 2.1, al hacer clic en el CUP) |

---

# Integraciones

> No especificado en el documento. El documento no describe integraciones con sistemas externos para este caso de uso; únicamente hace referencia a otros casos de uso internos del mismo sistema (SIIP) — ver sección "Dependencias".

---

# Datos Pendientes de Definir

1. No se especifica el tipo, formato, ni comportamiento del campo "Buscador"/"Búsqueda" (RN02, Campos requeridos); no está representado con una fila propia en la tabla de Formatos del Anexo B.1 (ver Observaciones, numeral 3).
2. No se aclara si "Código" (Campos requeridos) y "CUP" (Anexo A.1 / Anexo B.1) corresponden al mismo campo (ver Observaciones, numeral 2).
3. No se especifica la ubicación en pantalla del campo "Total" descrito en el Anexo B.1, ya que no aparece en el mockup del Anexo A.1 (ver Observaciones, numeral 5).
4. No se especifica el código o nombre completo de los casos de uso referidos genéricamente como "Casos de uso Programación y/o Seguimiento" en las Postcondiciones (ver Observaciones, numeral 6).
5. El documento no incluye una sección explícita de "Disparador", "Excepciones", "Validaciones" ni "Mensajes al Usuario"; no hay contenido documental para desarrollar estas secciones más allá de lo indicado.
6. No se especifica el mecanismo de descarga de "bitácora transaccional" mencionado en RN04 (formato, contenido, botón o ícono asociado en la pantalla), ni su relación con la descarga "en PDF o en Excel" descrita en el Flujo Alternativo FA-01 (ver Observaciones, numeral 4).
7. Prioridad (importancia) del propio caso de uso no especificada.
8. Módulo/submódulo del caso de uso dentro de la estructura del sistema no especificado.
9. **Parcialmente resuelto (RQ-T-01, ronda 3):** se confirmó que "Institución Ejecutora" y "Unidad Ejecutora" no son sinónimos (relación 1:N). Sin embargo, sigue sin aclararse si el uso de la palabra "Institución" en RN02 ("por Código, por Nombre y por Institución") fue un error de redacción por "Unidad Ejecutora" (el único selector visible en el mockup del Anexo A.1), o si falta en el mockup un campo/selector adicional para "Institución" propiamente dicha (ver Observaciones, numeral 1).