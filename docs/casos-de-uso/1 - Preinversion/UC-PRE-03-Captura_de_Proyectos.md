---
id: UC-PRE-03
codigo: UC-PRE-03
nombre: Captura de Proyectos
modulo: "Preinversión (derivado del prefijo 'PRE' del código 'UC-PRE-03' y de la referencia 'Equipo Preinversión' en el Historial de Revisiones; el documento no tiene un campo explícito llamado 'Módulo')"
submodulo: No especificado en el documento.
version: "1.2"
nota_version: >
  Versión 1.1: se aplica la resolución de negocio de la Ronda 2
  (RQ-C-02): se corrige, en RN04, la redacción de los nombres de los
  estados "Enviado a DGICP" y "Observado DGICP" a "Enviado a DGICP
  (Registro)" y "Observado DGICP (Registro)" respectivamente, para
  usar, estado por estado, la misma redacción que UC-PRE-01 "Registro
  y Solicitud de CUP" (fijada como oficial por el negocio). No se
  modifica ningún otro contenido de RN04 (duración de los estados,
  actores, casos de uso referenciados), ya que ninguna otra
  resolución de la Ronda 2 lo cubre para este documento.
nota_cambio_v1_2: >
  Cambio solicitado por el usuario (11/09/2026), NO proveniente de una
  nueva versión del PDF fuente: se agrega "Etapa" como sexta columna de
  la pantalla "Captura de Proyectos", ausente del Anexo B.1 del PDF
  original (que documenta solo 5 columnas: Unidad Ejecutora, CUP,
  Nombre del proyecto, Estado, Iniciativa de inversión). Representa la
  etapa actual del proyecto dentro de la Ruta de Preinversión, catálogo
  definido en CU-PRE-03.5 "Selección y Registro de Etapas" (RN07-RN10 de
  ese documento: Perfil, Prefactibilidad, Factibilidad, Diseño,
  Ejecución) — no debe confundirse con el campo "Estado" ya documentado
  en RN04 de este mismo documento, que sigue un catálogo distinto. Se
  añade como fila nueva en "Campos", marcada explícitamente como adición
  posterior al PDF, sin alterar ninguna otra fila ya transcrita del
  Anexo B.1. Ver contrato-CU-PRE-03.md para el detalle de la
  implementación (campo `etapaActual` de `ProyectoCapturaItem`).
fuente_pdf: UC-PRE-03_Captura_de_Proyectos_JUL_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 6
actor_principal: "No especificado en el documento. El documento lista varios actores en la sección 'Actores' sin designar explícitamente uno como 'principal': Técnico URP, Técnico PRE, Coordinador PRE, Viabilizador, Usuarios Internos y externos."
actores_secundarios:
  - "Sistema (no figura en la sección 'Actores' del documento; se incluye porque ejecuta el paso automático 1 del Flujo Básico — 'Registra en Captura de Proyectos los proyectos con CUP' — y el paso 1.1 del Flujo Alternativo FA01)"
prioridad: No especificado en el documento.
estado: Analizado
depende_de:
  - "CU-PRE-01 Registro de proyectos (precondición: contar con CUP)"
casos_relacionados:
  - "CU-PRE-03.5 Selección y registro de etapas"
  - "CU-PRE-04 Identificación"
  - "CU-PRE-23 Indicadores del Proyecto"
  - "CU-PRE-24 Viabilidad"
  - "CU-PRE-25 Elegibilidad"
  - "CU-PRE-26 Opinión Técnica"
  - "CU-PRE-01 Registro de proyectos"
roles:
  - "Técnico URP"
  - "Técnico PRE"
  - "Coordinador PRE"
  - "Viabilizador"
  - "Usuarios Internos y externos"
  - "Sistema (no figura en la sección 'Actores' del documento; se incluye por ejecutar pasos automáticos del Flujo Básico y del Flujo Alternativo FA01)"
pantallas:
  - "Anexo A.1 Captura de proyectos"
procesos: []
servicios_externos: []
entidades:
  - "Proyecto"
  - "CUP (Código Único de Proyectos)"
  - "Unidad Ejecutora"
  - "Estado"
  - "Iniciativa de inversión"
catalogos:
  - "Estados de Proyecto (RN04)"
palabras_clave:
  - "Captura de Proyectos"
  - "CUP"
  - "Preinversión"
  - "Estado del proyecto"
ultima_actualizacion: "JUL 2025"
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
    RN01:
      pagina: 4
    RN02:
      pagina: 4
    RN03:
      pagina: 4
    RN04:
      pagina: 4
    RN05:
      pagina: 5
  anexos:
    A1:
      nombre: "Anexo A.1 Captura de proyectos"
      pagina: 5
    B1:
      nombre: "Anexo B.1 – Formatos (Pantalla 'Captura de Proyectos')"
      pagina: 6
  nota_paginas: "Los números de página se determinaron mediante extracción de texto del PDF página por página (herramienta programática), identificando en qué página comienza cada sección (Identificación, Flujo Básico, Flujo Alternativo, cada Regla de Negocio y cada Anexo). El PDF no incluye numeración de página explícita en el pie de página, por lo que no fue posible verificar contra un marcador impreso; los números aquí indicados corresponden a la página física del documento (1 a 6) según el orden de extracción."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Captura de Proyectos |
| Código | UC-PRE-03 |
| Módulo | Preinversión (derivado del prefijo "PRE" del código "UC-PRE-03" y de la referencia "Equipo Preinversión" en el Historial de Revisiones; el documento no tiene un campo explícito llamado "Módulo") |
| Fuente | UC-PRE-03_Captura_de_Proyectos_JUL_2025_V1_F.pdf |
| Versión | 1.1 (contenido base v1.0 del documento fuente; corrección de negocio Ronda 2 — RQ-C-02 — aplicada en v1.1) |

**Campos requeridos (según el PDF):**
- Buscador
- CUP
- Nombre
- Iniciativa de inversión
- Unidad Ejecutora
- Estado

> ⚠️ Adición posterior al PDF (v1.2, 11/09/2026, decisión funcional del usuario): se agrega **Etapa** como sexto campo/columna de la pantalla, no listado en el PDF original. Ver `nota_cambio_v1_2` y la sección "Campos" más abajo.

> Nota: el documento usa el patrón de identificación en tabla ("Identificación"), no el patrón numerado con "Ruta de Acceso"; por lo tanto no aplica una sección de "Ruta de Acceso" para este caso de uso.

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|---|---|---|---|---|
| JUL 2025 | 1.0 | Primera Versión | Equipo Preinversión | No especificado en el documento. |

No se detectó discrepancia entre la versión indicada en la portada/encabezados ("Versión 1.0") y la versión registrada en el Historial de Revisiones ("1.0"). Esta tabla transcribe fielmente el control de versiones del PDF original; la versión 1.1 de este archivo (corrección de negocio Ronda 2) es posterior a esa fuente y no está reflejada en dicho historial.

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección explícita titulada "Objetivo"; la sección "Descripción caso de uso" (ver más abajo) es la que se aproxima a este propósito.

---

# Descripción

Muestra una tabla con el listado de proyectos a los cuales se ha emitido coordinador CUP. Permitirá dar clic en el CUP de cada proyecto a fin de ir a la pantalla "Registro de Etapas" en CU-PRE-03.5 "Selección y registro de etapas".

# Actor Principal

> No especificado en el documento. La sección "Actores" del PDF enumera varios actores sin designar explícitamente uno como "principal":
- Técnico URP
- Técnico PRE
- Coordinador PRE
- Viabilizador
- Usuarios Internos y externos

---

# Actores Secundarios

- **Sistema**: no figura en la sección "Actores" del documento. Se incluye porque ejecuta el paso 1 del Flujo Básico ("Registra en 'Captura de Proyectos' los proyectos con CUP") y el paso 1.1 del Flujo Alternativo FA01 ("Muestra la pantalla 'Registro de Etapas'").

---

# Disparador

> No especificado en el documento. El documento no describe explícitamente el evento que dispara el caso de uso (por ejemplo, el acceso del actor a la pantalla "Captura de Proyectos"); el Flujo Básico inicia directamente con el paso "Sistema. Registra en 'Captura de Proyectos' los proyectos con CUP".

---

# Precondiciones

1. Contar con CUP (CU-PRE-01 "Registro de proyectos").

---

# Flujo Principal

**Flujo Básico – FB**

1. Sistema. Registra en "Captura de Proyectos" los proyectos con CUP.
2. Todos los actores. Dan clic en el CUP del proyecto que deseen visualizar/registrar/editar.

---

# Flujos Alternos

## FA-01 — Clic en CUP

**Condición**

> No especificado en el documento. El título del flujo ("Clic en CUP") y el paso 2 del Flujo Básico sugieren contexto, pero el documento no incluye un apartado o frase explícita de "Condición" para este flujo alternativo.

**Flujo**

1.1. Sistema. Muestra la pantalla "Registro de Etapas" en CU-PRE-03.5 "Selección y registro de etapas".

**Resultado**

> No especificado en el documento. El documento no incluye un apartado o frase explícita de "Resultado" para este flujo alternativo.

---

# Excepciones

> No especificado en el documento. El documento no incluye una tabla o lista de excepciones (Código / Descripción / Consecuencia).

# Postcondiciones

El documento lista las siguientes postcondiciones, sin verbo introductorio explícito (solo los códigos y nombres de los casos de uso a los que se continúa):

1. CU-PRE-03.5 Selección y registro de etapas
2. CU-PRE-04 Identificación
3. CU-PRE-23 Indicadores del Proyecto
4. CU-PRE-24 Viabilidad
5. CU-PRE-25 Elegibilidad
6. CU-PRE-26 Opinión Técnica

> Nota de ambigüedad: el documento presenta esta lista de Postcondiciones como una simple enumeración de casos de uso relacionados, sin un verbo o frase que indique la naturaleza exacta de la postcondición (p. ej. "el sistema queda en condiciones de..." o "el usuario puede continuar hacia..."). Se incorporan tal como aparecen, pero no está especificado si representan una postcondición literal (estado resultante) o simplemente referencias de continuidad del flujo de trabajo.

---

# Reglas de Negocio

## RN01

**Descripción:** Los actores "Técnico URP", Viabilizador y "Usuarios Internos y externos" podrán visualizar únicamente los proyectos del listado de "Captura de Proyectos" según credenciales.

**Origen:** Página 4 del documento.

## RN02

**Descripción:** Los actores "Técnico PRE" y "Coordinador PRE" podrán visualizar los proyectos de todas las Unidades Ejecutoras.

**Origen:** Página 4 del documento.

## RN03

**Descripción:** En el campo "Búsqueda" podrán buscarse proyectos por CUP, por Nombre y por Unidad Ejecutora (Anexo A.1).

**Origen:** Página 4 del documento.

## RN04

**Descripción:** El campo "Estado" contará con un filtro para mostrar los proyectos según el estado seleccionado.

Los estados que podrían aparecer en la columna "Estado" de la pantalla "Captura de proyectos" son:

- **En Elaboración:** Significa que el proyecto se encuentra en proceso de registro de información parcial o registrado completamente por parte del actor "Técnico URP". Aparecerá al dar clic al botón "Guardar" y permanecerá hasta que el actor "Técnico URP" presiona el botón "Solicitar CUP".
- **Enviado a DGICP (Registro):** desde que la unidad ejecutora ha solicitado CUP u Opinión Técnica (según casos de uso CU-PRE-01 "Registro de proyectos", CU-PRE-26 "Opinión técnica") hasta que el Coordinador PRE asigna el caso a un Técnico PRE.
- **Observado DGICP (Registro):** desde que el Técnico PRE emite observaciones hasta que el Técnico URP ajusta y solicita nuevamente el CUP o la Opinión Técnica (CU-PRE-01 "Registro de proyectos", CU-PRE-26 "Opinión técnica").
- **CUP asignado:** desde que el "Técnico Pre" asigna el CUP (CU-PRE-01 "Registro de proyectos") hasta que el "Técnico URP" inicia con el ingreso de información en la pestaña "Identificación" (CU-PRE-04).
- **En F&E:** desde que el "Técnico URP" procede a registrar información desde el caso de uso CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto".
- **Proyecto formulado:** Desde que el "Técnico URP" termina de formular el proyecto en CU-PRE-23 "Indicadores del Proyecto", hasta cuando el "Viabilizador" emite viabilidad (CU-PRE-24 "Viabilidad"). Si el proyecto es devuelto por observaciones, el estado debe cambiar a "Observado".
- **Observado:** desde que el "Viabilizador" emite comentarios con observaciones al proyecto, hasta que el "Técnico URP" ajusta y remite nuevamente el proyecto a viabilidad (CU-PRE-24 "Viabilidad").
- **En viabilidad:** Desde que el "Técnico URP" solicita la viabilidad del proyecto, hasta que el "Viabilizador" emite observaciones o Viabilidad al mismo (CU-PRE-24 "Viabilidad").
- **Proyecto viable:** desde que el "Viabilizador" emite viabilidad al proyecto (CU-PRE-24 "Viabilidad") hasta que este emite Elegibilidad al proyecto a través de la selección de criterios (CU-PRE-25 "Elegibilidad").
- **En elegibilidad:** Desde que el "Viabilizador" da clic al botón "Ir a elegibilidad" hasta que el mismo da clic al botón "Emitir elegibilidad".
- **Proyecto elegible:** desde que el "Viabilizador" emite Elegibilidad (CU-PRE-25 "Elegibilidad") hasta que se solicita Opinión Técnica (CU-PRE-26 "Opinión técnica").
- **En OT:** Desde que el "Viabilizador" da clic al botón "Emitir elegibilidad" hasta que el Técnico PRE da clic al botón "O.T Favorable".
- **Proyecto con O.T.:** desde que el "Técnico PRE" emite Opinión Técnica (CU-PRE-26), hasta que el Técnico URP solicite OT para otra etapa de la Preinversión o actualización de una OT ya emitida, y se repite el proceso de F&E (desde el caso de uso CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto").

> ✅ **RESUELTO [RQ-C-02]** (Ronda 2 — respuesta del negocio: opción A): se corrigió la redacción de los estados "Enviado a DGICP" y "Observado DGICP" de este catálogo a "Enviado a DGICP (Registro)" y "Observado DGICP (Registro)", para usar la misma redacción fijada como oficial en UC-PRE-01 "Registro y Solicitud de CUP". No se modificó ninguna otra parte de la descripción de estos estados.
>
> Nota aparte (fuera del alcance de esta corrección): la definición de "Enviado a DGICP (Registro)" en este documento indica que el estado permanece vigente "hasta que el Coordinador PRE asigna el caso a un Técnico PRE" — la misma regla que la Ronda 2 (RQ-C-01) descartó para UC-PRE-02, a favor de que el estado permanezca hasta que el Técnico PRE resuelve la solicitud (CU-PRE-01.5). La resolución de negocio de RQ-C-01 no incluyó explícitamente a este documento (UC-PRE-03) en su alcance, por lo que este contenido no se modifica aquí; se recomienda incorporarlo como ítem de una próxima ronda de consistencia para evitar que esta misma contradicción quede sin corregir en este documento.

**Origen:** Páginas 4 y 5 del documento (la regla RN04 continúa de la página 4 a la página 5).

## RN05

**Descripción:** Se podrá filtrar por unidad ejecutora, CUP, nombre del proyecto, iniciativa de inversión y estado en la "Captura de Proyectos", dando clic en cada uno de los mismos, según la información requerida.

**Origen:** Página 5 del documento.

---

# Campos

Tabla construida a partir del Anexo B.1 – Formatos (Pantalla "Captura de Proyectos"). Los nombres de Anexo son consistentes entre el cuerpo del documento (RN03, RN05), el Anexo A.1 (mockup) y el Anexo B.1: no se detectó discrepancia de numeración de Anexos para esta pantalla.

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Unidad Ejecutora | Se muestra según credenciales del Técnico URP que registró la información del proyecto. Contará con un filtro. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| CUP | Muestra el CUP del proyecto. Contará con un filtro. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Nombre del proyecto | Mostrará el nombre del proyecto. Contará con un filtro. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Estado | Mostrará el estado en el que se encuentra cada proyecto. Contará con un filtro para poder visualizar proyectos según el estado en el que se encuentren. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Ver catálogo de Estados en RN04 y discrepancia con los valores mostrados en el mockup del Anexo A.1 (ver Observaciones). |
| Iniciativa de inversión | Mostrará el tipo de iniciativa identificada en el registro del proyecto, desde CU-PRE-01 "Registro de proyectos". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Etapa ⚠️ | Mostrará la etapa actual del proyecto dentro de la Ruta de Preinversión (catálogo Perfil/Prefactibilidad/Factibilidad/Diseño/Ejecución de CU-PRE-03.5 "Selección y Registro de Etapas"). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. **Fila agregada en v1.2 (11/09/2026), no proveniente del Anexo B.1 del PDF** — ver `nota_cambio_v1_2` y Observaciones. |

> Nota: el campo "Buscador", listado en "Campos requeridos" de la Identificación y visible en el mockup del Anexo A.1 (control de búsqueda con placeholder "CUP; NOMBRE; UNIDAD EJECUTORA"), no cuenta con una fila propia en el Anexo B.1 – Formatos. Su comportamiento se describe únicamente de forma indirecta en RN03 ("En el campo 'Búsqueda' podrán buscarse proyectos por CUP, por Nombre y por Unidad Ejecutora"). No se documenta su Tipo/Formato/Editable de forma explícita en el Anexo B.1 (ver Datos Pendientes de Definir).

La columna "Editable" (Sí/No) del Anexo B.1 es distinta de una columna "Obligatorio": el documento no incluye una columna "Obligatorio" explícita, por lo que dicha columna se marca como "No especificado en el documento." en todas las filas. En todas las filas documentadas, el valor "Editable: No" es consistente con la Descripción de cada campo (todas describen campos de solo visualización/consulta, p. ej. "Se muestra según...", "Muestra el...", "Mostrará..."); no se detectó contradicción entre "Editable" y la Descripción para ningún campo de esta tabla (ver criterio de verificación sistemática).

No se detectaron valores de "Tipo"/"Formato" atípicos respecto al resto de la tabla (todos son "Texto/Texto" o "Numérico/Numérico").

---

# Validaciones

> No especificado en el documento. El Anexo B.1 no describe validaciones de entrada de datos para los campos de esta pantalla (todos los campos documentados son de solo visualización/consulta).

| Campo | Validación | Mensaje esperado |
|---|---|---|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Errores

> No especificado en el documento. El documento no incluye una tabla ni descripción de códigos de error para este caso de uso.

| Código | Descripción | Acción esperada |
|---|---|---|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Técnico URP | Visualizar únicamente los proyectos del listado de "Captura de Proyectos" según credenciales. | RN01 |
| Viabilizador | Visualizar únicamente los proyectos del listado de "Captura de Proyectos" según credenciales. | RN01 |
| Usuarios Internos y externos | Visualizar únicamente los proyectos del listado de "Captura de Proyectos" según credenciales. | RN01 |
| Técnico PRE | Visualizar los proyectos de todas las Unidades Ejecutoras. | RN02 |
| Coordinador PRE | Visualizar los proyectos de todas las Unidades Ejecutoras. | RN02 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de proyectos" (precondición; también referenciado dentro de RN04)
- CU-PRE-03.5 "Selección y registro de etapas" (postcondición; destino del FA-01)
- CU-PRE-04 "Identificación" (postcondición; referenciado dentro de RN04)
- CU-PRE-23 "Indicadores del Proyecto" (postcondición; referenciado dentro de RN04)
- CU-PRE-24 "Viabilidad" (postcondición; referenciado dentro de RN04)
- CU-PRE-25 "Elegibilidad" (postcondición; referenciado dentro de RN04)
- CU-PRE-26 "Opinión Técnica" (postcondición; referenciado dentro de RN04)

**Procesos relacionados:** No especificado en el documento.

**Servicios externos:** No especificado en el documento.

---

# Pantallas

## Anexo A.1 Captura de proyectos

**Descripción:** Pantalla que muestra una tabla con el listado de proyectos a los cuales se ha emitido CUP, con un buscador y filtros por columna.

**Campos:**
- Buscador (placeholder: "CUP; NOMBRE; UNIDAD EJECUTORA")
- CUP
- Nombre del proyecto
- Iniciativa de inversión
- Estado
- Unidad Ejecutora

**Botones:**
- Botón "BUSCAR" (con ícono de lupa, junto al campo de búsqueda).

**Acciones:**
- Cada columna (CUP, Nombre del proyecto, Iniciativa de inversión, Estado, Unidad Ejecutora) muestra un ícono de filtro (▼) junto a su encabezado, conforme a RN05.
- Dar clic en el CUP de un proyecto lleva a la pantalla "Registro de Etapas" (CU-PRE-03.5), según el Flujo Alternativo FA-01.

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| CUP | NOMBRE DEL PROYECTO | INICIATIVA DE INVERSIÓN | ESTADO | UNIDAD EJECUTORA |
|---|---|---|---|---|
| XXXX | Mejoramiento... | Programa | En análisis DGICP | MOPT |
| XXXX | Construcción... | Proyecto | CUP asignado | MINSAL |
| XXXX | Equipamiento... | Estudio | Viabilizado | ISSS |
| XXXX | Construcción... | Proyecto | En elaboración | ISBM |
| XXXX | Fortalecimiento... | Proyecto | ... | MOPT |

> Nota aclaratoria (criterio 23): Los valores de la columna "CUP" se muestran como "XXXX" (placeholder genérico) en el propio mockup del PDF, no como valores reales de ejemplo. Los valores de la columna "NOMBRE DEL PROYECTO" ("Mejoramiento...", "Construcción...", "Equipamiento...", "Fortalecimiento...") están truncados con puntos suspensivos en el mockup original; se transcriben tal como aparecen, sin completar el texto faltante. El valor de "ESTADO" en la última fila aparece como "..." (truncado/ilegible en el mockup original); se transcribe tal como se ve, sin inferir su contenido.

> Nota adicional al pie del mockup: "*CUP - CÓDIGO ÚNICO DE PROYECTOS".

---

# Mensajes al Usuario

> No especificado en el documento. El documento no incluye mensajes de confirmación, error o notificación explícitos con su texto exacto para este caso de uso.

| Tipo | Mensaje | Cuándo ocurre |
|---|---|---|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Observaciones

- **Columna "Etapa" agregada en v1.2 (11/09/2026), no proveniente del PDF:** por decisión funcional del usuario, se agregó "Etapa" como sexta columna de la pantalla "Captura de Proyectos", ausente del Anexo B.1 y del mockup del Anexo A.1 del PDF original (que solo documentan 5 columnas). Representa la etapa actual del proyecto dentro de la Ruta de Preinversión (catálogo Perfil/Prefactibilidad/Factibilidad/Diseño/Ejecución definido en CU-PRE-03.5 "Selección y Registro de Etapas"), no debe confundirse con el campo "Estado" ya documentado en RN04 de este mismo documento. Se marca con ⚠️ en las tablas de "Campos", "Entidades Detectadas" y "Catálogos Detectados" para distinguirla del resto del contenido, que sí proviene fielmente del PDF fuente. Ver `nota_cambio_v1_2` en el encabezado y contrato-CU-PRE-03.md (campo `etapaActual`) para el detalle de implementación.

- **Discrepancia entre el catálogo de Estados (RN04) y los valores de ejemplo del mockup (Anexo A.1):** RN04 define explícitamente 13 estados posibles para el campo "Estado" ("En Elaboración", "Enviado a DGICP (Registro)", "Observado DGICP (Registro)", "CUP asignado", "En F&E", "Proyecto formulado", "Observado", "En viabilidad", "Proyecto viable", "En elegibilidad", "Proyecto elegible", "En OT", "Proyecto con O.T."). Sin embargo, el mockup del Anexo A.1 muestra los valores "En análisis DGICP" y "Viabilizado" en la columna "Estado", ninguno de los cuales coincide textualmente con los 13 estados listados en RN04. Los valores "CUP asignado" y "En elaboración" del mockup sí coinciden (este último con diferencia de mayúscula inicial: "En Elaboración" en RN04 vs. "En elaboración" en el mockup). No se resuelve esta discrepancia (no fue parte de las resoluciones de la Ronda 2); se documenta para que sea aclarada en una etapa posterior. *(Nota: los nombres "Enviado a DGICP (Registro)" y "Observado DGICP (Registro)" fueron actualizados en la Ronda 2 — RQ-C-02 — para alinearse con la redacción oficial de UC-PRE-01; ver RN04.)*

- **Campo "Buscador" sin fila propia en el Anexo B.1:** el campo "Buscador" aparece en la sección "Campos requeridos" de la Identificación y en el mockup del Anexo A.1, y su comportamiento funcional se describe en RN03, pero el Anexo B.1 – Formatos no incluye una fila dedicada a este campo con su Tipo/Formato/Editable, a diferencia de los demás campos de la pantalla que sí están documentados individualmente.

- **Postcondiciones sin verbo introductorio:** la sección "Postcondiciones" de la Identificación lista seis casos de uso (CU-PRE-03.5, CU-PRE-04, CU-PRE-23, CU-PRE-24, CU-PRE-25, CU-PRE-26) sin una frase que indique la naturaleza exacta de la postcondición (ver nota de ambigüedad en la sección correspondiente).

- **Flujo Alternativo FA-01 sin apartados explícitos de "Condición" y "Resultado":** el documento no incluye encabezados o frases explícitas para estos apartados; se completaron con "No especificado en el documento." conforme al criterio de no sintetizar contenido no verificable.

- **Codificación de caracteres del PDF original:** el texto extraído del PDF presenta caracteres especiales alterados en algunos encabezados institucionales (p. ej. "DIRECCIÓN GENERAL DE INVERSIÓN Y CRÉDITO PÚBLICO", "Versión", "Información" aparecen en el texto plano subyacente del PDF con codificación distinta a la que se observa visualmente en el documento renderizado). Esto es una característica del propio archivo PDF fuente, no una interpretación añadida en este análisis; los textos se han transcrito en este Markdown usando la grafía correctamente acentuada tal como se observa visualmente en el documento renderizado.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|----------|-------------|-----------|
| Proyecto | Proyecto de inversión pública al cual se le ha emitido CUP. | Registro/listado automático en la pantalla "Captura de Proyectos" (Flujo Básico, paso 1); Consulta/visualización (Flujo Básico, paso 2); Filtrado por Unidad Ejecutora, CUP, nombre del proyecto, iniciativa de inversión y estado (RN05); Visibilidad restringida según credenciales y rol (RN01, RN02). |
| CUP (Código Único de Proyectos) | Identificador único asignado al proyecto. | Consulta/visualización (Anexo A.1); Búsqueda (RN03); Navegación — al dar clic lleva a "Registro de Etapas" (Flujo Alternativo FA-01). |
| Unidad Ejecutora | Entidad institucional responsable del proyecto. | Consulta/visualización (Anexo B.1); Filtro (RN03, RN05); Determina visibilidad de proyectos según rol (RN01, RN02). |
| Estado | Estado del proyecto dentro del ciclo de Preinversión. | Cálculo/transición automática según eventos descritos en RN04; Filtro (RN04, RN05). |
| Iniciativa de inversión | Tipo de iniciativa identificada en el registro del proyecto (CU-PRE-01). | Consulta/visualización (Anexo B.1); Filtro (RN05). |
| Etapa ⚠️ | Etapa actual del proyecto dentro de la Ruta de Preinversión (CU-PRE-03.5). Entidad agregada en v1.2, no proveniente del Anexo B.1 del PDF original (ver nota_cambio_v1_2). | Consulta/visualización. |

---

# Catálogos Detectados

| Catálogo | Valores conocidos |
|---|---|
| Estados de Proyecto (RN04) | En Elaboración; Enviado a DGICP (Registro); Observado DGICP (Registro); CUP asignado; En F&E; Proyecto formulado; Observado; En viabilidad; Proyecto viable; En elegibilidad; Proyecto elegible; En OT; Proyecto con O.T. Este catálogo está definido íntegramente dentro de RN04 (ver Reglas de Negocio; nombres de "Enviado a DGICP (Registro)" y "Observado DGICP (Registro)" actualizados en la Ronda 2 — RQ-C-02 — para alinearse con UC-PRE-01). Ver discrepancia con los valores de ejemplo mostrados en el mockup del Anexo A.1 ("En análisis DGICP", "Viabilizado") en la sección Observaciones. |
| Etapas de la Ruta de Preinversión ⚠️ | Perfil; Prefactibilidad; Factibilidad; Diseño; Ejecución. Catálogo definido en CU-PRE-03.5 "Selección y Registro de Etapas" (RN07-RN10 de ese documento), no en este PDF. Referenciado aquí solo porque, a partir de v1.2 (11/09/2026), respalda la columna "Etapa" agregada por decisión funcional del usuario — ver nota_cambio_v1_2 y Observaciones. Distinto del catálogo "Estados de Proyecto" de arriba. |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|---|---|---|
| Registro/listado de proyectos con CUP en "Captura de Proyectos" | Sistema | Pantalla "Captura de Proyectos" (Anexo A.1) |
| Visualización de la pantalla "Registro de Etapas" | Sistema (tras clic en CUP, FA-01) | CU-PRE-03.5 "Selección y registro de etapas" |

---

# Integraciones

> No especificado en el documento. El documento no describe integraciones con sistemas externos para este caso de uso.

| Sistema | Tipo | Descripción |
|---|---|---|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Datos Pendientes de Definir

1. **Prioridad** del caso de uso: no especificada en el documento.
2. **Disparador** explícito del caso de uso: el documento no describe el evento que inicia el Flujo Básico (p. ej. el acceso del actor a la pantalla).
3. **Discrepancia entre el catálogo de Estados (RN04)** y los valores de ejemplo mostrados en el mockup del Anexo A.1 ("En análisis DGICP" y "Viabilizado" no coinciden con ninguno de los 13 estados listados en RN04): debe aclararse si estos valores del mockup corresponden a estados adicionales no documentados en RN04, a nombres alternativos de estados ya existentes, o a un error del mockup.
4. **Definición formal del campo "Buscador"** (Tipo, Formato, Editable): no cuenta con una fila propia en el Anexo B.1 – Formatos, a diferencia de los demás campos de la pantalla.
5. **Naturaleza exacta de las Postcondiciones**: la lista de seis casos de uso en "Postcondiciones" no incluye una frase o verbo que aclare si se trata de un estado resultante, una condición de continuidad de flujo, o ambas.
6. **Condición y Resultado del Flujo Alternativo FA-01**: no están especificados explícitamente en el documento.
7. **Validaciones, Errores y Mensajes al Usuario**: el documento no describe ninguno de estos elementos para este caso de uso; no es posible determinar si esta ausencia se debe a que la pantalla es de solo consulta (sin necesidad de validaciones) o a que el documento simplemente no los desarrolló.