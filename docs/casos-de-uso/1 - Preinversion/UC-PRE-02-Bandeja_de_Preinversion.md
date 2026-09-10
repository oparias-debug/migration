---
id: CU-PRE-02
codigo: CU-PRE-02
nombre: Bandeja Preinversión
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.2"
fuente_pdf: CU-PRE-02_Bandeja_Preinversión_JUL_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 9

nota_version: >
  Versión 1.1: se actualizan las referencias a "CU-PRE-01 Registro de
  Proyectos" para reflejar la división de dicho caso de uso en
  CU-PRE-01 "Registro y Solicitud de CUP" (actor Técnico URP) y
  CU-PRE-01.5 "Revisión y Emisión de CUP" (actor Técnico PRE). El punto
  de mayor impacto es el Flujo Alterno FA-01: el enlace que antes
  apuntaba a "CU-PRE-01" ahora apunta a "CU-PRE-01.5", ya que la acción
  que ejecuta el Técnico PRE al dar clic en el caso asignado (revisar,
  comentar, devolver o emitir CUP) corresponde a ese nuevo documento, no
  al registro original del Técnico URP. No se modificó ningún
  requerimiento funcional propio de CU-PRE-02; solo se corrigieron
  referencias cruzadas.
  Versión 1.2: se aplica la resolución de negocio de la Ronda 2 (RQ-C-01)
  sobre la contradicción de RN07: se corrige la definición del estado
  "Enviado a DGICP" para que documente que dicho estado no cambia en el
  momento de la asignación del Coordinador PRE, sino hasta que el
  Técnico PRE resuelve la solicitud (CU-PRE-01.5).

actor_principal: ["Coordinador PRE"]

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de:
  - CU-PRE-01 Registro y Solicitud de CUP

casos_relacionados:
  - CU-PRE-01 Registro y Solicitud de CUP
  - CU-PRE-01.5 Revisión y Emisión de CUP
  - CU-PRE-29 Banco de proyectos

roles:
  - Coordinador PRE
  - Técnico PRE

pantallas:
  - Bandeja Preinversión / Solicitudes Activas (Anexo A.1)
  - Aviso "¿Está seguro de asignar esta solicitud?" (Anexo A.2)
  - Aviso de archivo de solicitud (Anexo A.3)
  - Reporte de solicitudes Preinversión archivadas (Anexo A.4)

procesos:
  - Asignación de solicitud a Técnico PRE
  - Archivo de solicitud
  - Traslado de proyecto a Captura de Proyectos

servicios_externos: []

entidades:
  - Solicitud (CUP / Opinión Técnica)
  - Proyecto
  - Técnico PRE (catálogo)
  - Reporte de solicitudes archivadas

catalogos:
  - Anexo C – Nombres Técnicos PRE

palabras_clave:
  - bandeja de preinversión
  - CUP
  - opinión técnica
  - coordinador PRE
  - técnico PRE
  - asignación de solicitudes
  - archivo de solicitudes

ultima_actualizacion: AGO 2025 (actualización de referencias cruzadas por división de CU-PRE-01; fuente original JUL 2025); corrección Ronda 2 (RQ-C-01) aplicada AGO 2026.

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    FA01:
      pagina: 4
    FA02:
      pagina: 4
    FA03:
      pagina: 4
  reglas_negocio:
    RN01:
      pagina: 4
    RN02:
      pagina: 4
    RN03:
      pagina: 5
    RN04:
      pagina: 5
    RN05:
      pagina: 5
    RN06:
      pagina: 5
    RN07:
      pagina: 5
    RN08:
      pagina: 5
    RN09:
      pagina: 5
    RN10:
      pagina: 5
  anexos:
    A1:
      nombre: Solicitudes Activas
      pagina: 6
    A2:
      nombre: Aviso de asignación de solicitud
      pagina: 6
    A3:
      nombre: Aviso de archivo de solicitud
      pagina: 6
    A4:
      nombre: Reporte de solicitudes Preinversión archivadas
      pagina: 6
    B1:
      nombre: Formatos - Pantallas Solicitudes Activas y Reporte de solicitudes archivadas
      pagina: 8
    C:
      nombre: Nombres Técnicos PRE
      pagina: 9
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Bandeja Preinversión |
| Código | CU-PRE-02 |
| Módulo | Preinversión |
| Fuente | CU-PRE-02_Bandeja_Preinversión_JUL_2025_V1_F.pdf |
| Versión | 1.2 (referencias cruzadas actualizadas en v1.1; corrección de RN07 por resolución de negocio Ronda 2 — RQ-C-01 — en v1.2) |

**Campos requeridos (según el PDF):**
- Unidad Ejecutora
- Tipo de solicitud
- CUP
- Nombre del proyecto
- Fecha de solicitud
- Estado
- Asignado a
- Archivar solicitud

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Permitirá al "Coordinador Pre": 1) asignar las diferentes solicitudes de las Unidades Ejecutoras (Código Único de Proyecto "CUP" y Opiniones Técnicas) al Técnico PRE; 2) archivar solicitudes de la Bandeja Preinversión, previa solicitud de las IE's mediante correo electrónico; y 3) consultar el estado de la solicitud de cada proyecto activo.

# Actor Principal

Coordinador PRE

---

# Actores Secundarios

Técnico PRE (interviene en los Flujos Alternativos FA-01 y FA-02, ingresando a la Bandeja Preinversión para dar clic al caso asignado; la actuación posterior del Técnico PRE — revisar, comentar, devolver u otorgar el CUP — se documenta en **CU-PRE-01.5 "Revisión y Emisión de CUP"**).

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Haber ejecutado el CU-PRE-01 "Registro y Solicitud de CUP".
2. Haber ejecutado el caso de uso CU-PRE-26 "Opinión Técnica".

---

# Flujo Principal

1. Sistema registra en Bandeja Preinversión la solicitud de la Unidad Ejecutora y la muestra en la tabla del Anexo A.1 "Solicitudes Activas".
2. Coordinador PRE asigna a Técnico PRE la solicitud para revisión y oprime el botón "Guardar".
3. Sistema muestra un aviso con la pregunta "¿Está seguro de asignar esta solicitud?".
4. Coordinador PRE:
   - 4.a. Si oprime el botón "Aceptar": Sistema envía alerta a Técnico PRE "Se ha asignado para revisión la solicitud XXX" y mantiene al Coordinador PRE en la pantalla "Bandeja Preinversión".
   - 4.b. Si oprime el botón "Cancelar": no se realiza ninguna acción, desaparece la ventana emergente y se mantiene al Coordinador PRE en la pantalla "Bandeja Preinversión".

---

# Flujos Alternos

## FA-01 – Enlace a Revisión CUP

**Condición**

> No especificado en el documento.

**Flujo**

1.1 Técnico PRE ingresa a Bandeja Preinversión y da clic al caso asignado.
1.2 Sistema muestra la pantalla "Nuevo Registro" en el contexto del caso de uso **CU-PRE-01.5 "Revisión y Emisión de CUP"** (sección "Revisión PRE" habilitada para el Técnico PRE).

**Resultado**

> No especificado en el documento.

> Nota de actualización (v1.1): este flujo apuntaba originalmente a "CU-PRE-01 Registro de Proyectos" (versión no dividida). Tras la división de ese caso de uso, la acción propia del Técnico PRE (revisar, comentar, devolver o emitir CUP) corresponde a **CU-PRE-01.5**, mientras que CU-PRE-01 quedó acotado a la actuación del Técnico URP. Se actualiza la referencia para mantener la consistencia de actor entre los documentos.

## FA-02 – Enlace a Revisión Opinión Técnica

**Condición**

> No especificado en el documento.

**Flujo**

2.1 Técnico PRE ingresa a Bandeja Preinversión y da clic al caso asignado.
2.2 Sistema muestra pantalla Opinión Técnica del CU-PRE-26 "Opinión Técnica".

**Resultado**

> No especificado en el documento.

> Nota: este flujo no se ve afectado por la división de CU-PRE-01, ya que corresponde a un caso de uso distinto (CU-PRE-26).

## FA-03 – Archivo de solicitud de CUP o de Opinión Técnica

**Condición**

> No especificado en el documento.

**Flujo**

3.1 Coordinador PRE acerca el cursor al extremo izquierdo del nombre del proyecto.
3.2 Sistema muestra botón interactivo (RN08) (aparece al acercar el cursor al extremo izquierdo del nombre del proyecto, y desaparece al alejarlo).
3.3 Coordinador PRE da clic en el botón interactivo "Archivar".
3.4 Sistema muestra el mensaje "¿Está seguro de archivar esta solicitud?" (Anexo A.3).
3.5 Coordinador PRE:
   - Si oprime el botón "Aceptar": se archiva la solicitud y se envía la solicitud a la pantalla "Reporte de solicitudes Preinversión archivadas" (Anexo A.4), y se mantiene al Coordinador PRE en la pantalla "Bandeja Preinversión".
   - Si oprime el botón "No" / "Cancelar": no se ejecuta ninguna acción y se mantiene al Coordinador PRE en la pantalla "Bandeja Preinversión".
3.6 Sistema asigna el estado "Archivado" a la solicitud del proyecto.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|-------------|--------------|
| — | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento.

# Postcondiciones

1. UC-PRE-03 "Captura de proyectos".
2. CU-PRE-26 "Opinión Técnica".
3. CU-PRE-29 "Banco de proyectos".

> Nota: el documento lista estas postcondiciones únicamente como referencias a otros casos de uso, sin describir de forma explícita el estado resultante que queda en cada uno tras la ejecución de CU-PRE-02. La postcondición inmediata y más directa —la ejecución de CU-PRE-01.5 tras la asignación (FA-01)— se documenta en ese flujo alterno; no se agregó aquí como postcondición numerada por fidelidad a la estructura original del documento fuente.

---

# Reglas de Negocio

### RN01

**Descripción:** Al emitirse el CUP (según caso de uso **CU-PRE-01.5 "Revisión y Emisión de CUP"**, SF-2 Emitir CUP) la solicitud desaparece de la tabla "Solicitudes Activas" y el proyecto se traslada a "Captura de Proyectos" (UC-PRE-03).

**Origen:** Documento, sección "Reglas del Negocio".

> Nota de actualización (v1.1): la referencia original era "CU-PRE-01 'Registro de Proyectos', SF 1.2, paso 3". Tras la división, la acción de emitir el CUP es propia del Técnico PRE y se documenta en CU-PRE-01.5 (SF-2).

### RN02

**Descripción:** Al emitirse la "Opinión Técnica" para las etapas de la Preinversión (Prefactibilidad, Factibilidad, Diseño, Estudio), el proyecto desaparece de la tabla "Solicitudes Activas" y se sigue mostrando en "Captura de Proyectos" (UC-PRE-03); en caso de emitirse Opinión Técnica para Ejecución el proyecto desaparece de la tabla "Solicitudes Activas" y se sigue mostrando en "Captura de Proyectos" (UC-PRE-03). Tanto los proyectos en preinversión como en ejecución se muestran también en el "Banco de Proyectos" CU-PRE-29.

**Origen:** Documento, sección "Reglas del Negocio".

> Esta regla no se ve afectada por la división de CU-PRE-01, ya que corresponde al flujo de Opinión Técnica (CU-PRE-26).

### RN03

**Descripción:** Cuando el "Coordinador PRE" seleccione a un "Técnico PRE" del listado en el campo "Asignado a", el Sistema mostrará la ventana Anexo A.2 con el mensaje "¿Está seguro de asignar esta solicitud?".

**Origen:** Documento, sección "Reglas del Negocio".

### RN04

**Descripción:** Es necesario que en cada celda de la columna "Asignado a" de la tabla "Solicitudes Activas" se cuente con un campo de selección del Técnico PRE (según catálogo "Nombres Técnicos PRE", Anexo C).

**Origen:** Documento, sección "Reglas del Negocio".

### RN05

**Descripción:** Es necesario que el Sistema contabilice por separado (una fila para solicitudes de CUP y otra para solicitudes de Opinión Técnica) la cantidad de casos asignados a cada Técnico PRE (Ver pie de página de tabla "Solicitudes Activas").

**Origen:** Documento, sección "Reglas del Negocio".

### RN06

**Descripción:** En la columna "Tipo de Solicitud" de la tabla "Solicitudes Activas" es necesario agregar un filtro según tipo de solicitud.

**Origen:** Documento, sección "Reglas del Negocio".

### RN07

**Descripción:** Los estados que podrían aparecer en la columna "Estado" de la pantalla "Solicitudes Activas" son:
- **Enviado a DGICP**: desde que la unidad ejecutora ha solicitado CUP u Opinión Técnica (según casos de uso **CU-PRE-01 "Registro y Solicitud de CUP"** y CU-PRE-26 "Opinión Técnica") hasta que el "Técnico PRE" resuelve la solicitud —dando clic en "Emitir CUP" o "Devolver" en **CU-PRE-01.5 "Revisión y Emisión de CUP"** (o la acción equivalente de CU-PRE-26 para Opinión Técnica). La asignación del caso a un "Técnico PRE" por parte del "Coordinador PRE" **no** constituye una transición de este estado; el estado permanece "Enviado a DGICP" durante toda la revisión hasta ese momento de resolución.
- **Observado DGICP**: desde que el "Técnico PRE" envía observaciones (según caso de uso **CU-PRE-01.5 "Revisión y Emisión de CUP"**) hasta que el "Técnico URP" ajusta y solicita nuevamente el CUP o la Opinión Técnica (**CU-PRE-01** y CU-PRE-26).

> ✅ **RESUELTO [RQ-C-01]** (Ronda 2 — respuesta del negocio: opción A): se corrige la definición de "Enviado a DGICP" de esta RN07, que documentaba la asignación del Coordinador PRE como el momento de salida del estado. Queda confirmado que la definición correcta es la de CU-PRE-01/CU-PRE-01.5: el estado no cambia con la asignación y permanece vigente hasta que el Técnico PRE resuelve la solicitud (Emitir CUP / Devolver). No se detectó, ni el negocio mencionó, un estado intermedio (p. ej. "Asignado" o "En revisión") — el catálogo de estados queda tal como se documenta arriba.

**Origen:** Documento, sección "Reglas del Negocio".

### RN08

**Descripción:** Las solicitudes que hayan sido archivadas por el "Coordinador PRE" desaparecerán de la tabla "Solicitudes Activas".

**Origen:** Documento, sección "Reglas del Negocio".

### RN09

**Descripción:** El Sistema permitirá al "Coordinador PRE" cambiar al "Técnico PRE" previamente asignado a un caso en cualquier estado que presente.

**Origen:** Documento, sección "Reglas del Negocio".

### RN10

**Descripción:** El estado que aparecerá en la columna "Estado de la solicitud" de la pantalla "Reporte de solicitudes Preinversión archivadas" es:
- **Archivado**: desde que Coordinador PRE archiva el caso según Flujo alternativo 3 FA3.

**Origen:** Documento, sección "Reglas del Negocio".

---

# Campos

## Pantalla "Solicitudes Activas"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Unidad Ejecutora | Se muestra según lo registrado en **CU-PRE-01 "Registro y Solicitud de CUP"** por parte del "Técnico URP" | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Tipo de solicitud | Se muestra en la lista dependiendo de si es una solicitud de CUP o de Opinión Técnica | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| CUP | Si el proyecto aún no cuenta con CUP: mostrará el campo vacío. Si el proyecto cuenta con CUP: mostrará el CUP asignado (asignación ejecutada en CU-PRE-01.5) | Numérico o campo vacío | Numérico o campo vacío | No especificado en el documento. | No especificado en el documento. | No editable |
| Nombre del proyecto | Mostrará el nombre del proyecto. Si es una solicitud de CUP: se incorpora a la lista con la información contenida en el campo "Nombre" de la pantalla "Nuevo Registro", **CU-PRE-01 "Registro y Solicitud de CUP"**. Si es una solicitud de OT: se incorpora a la lista con la información contenida en el campo "Nombre" de la pantalla "Definición del proyecto" del CU-PRE-26 "Opinión Técnica" | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Fecha de solicitud | Campo que mostrará la fecha en que la solicitud ingresó, bajo el formato DD/MM/AAAA | Fecha | Fecha | No especificado en el documento. | No especificado en el documento. | No editable |
| Estado | Muestra el estado actual de la solicitud (ver RN07) | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Asignado a | Campo que permite al Coordinador PRE asignar al Técnico PRE según Anexo C | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable |

## Pantalla "Reporte de solicitudes Preinversión archivadas"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Unidad Ejecutora | Se muestra según lo registrado en **CU-PRE-01 "Registro y Solicitud de CUP"** | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Tipo de solicitud | Se muestra en la lista dependiendo de si es una solicitud de CUP o de Opinión Técnica | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| CUP | Si el proyecto aún no cuenta con CUP: mostrará el campo vacío. Si el proyecto cuenta con CUP: mostrará el CUP asignado | Numérico o campo vacío | Numérico o campo vacío | No especificado en el documento. | No especificado en el documento. | No editable |
| Nombre del proyecto | Mostrará el nombre del proyecto. Si es una solicitud de CUP: se incorpora a la lista con la información contenida en el campo "Nombre" de la pantalla "Nuevo Registro", **CU-PRE-01 "Registro y Solicitud de CUP"**. Si es una solicitud de OT: se incorpora a la lista con la información contenida en el campo "Nombre" de la pantalla "Definición del proyecto" del CU-PRE-26 "Opinión Técnica" | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Fecha de solicitud | Campo que mostrará la fecha en que la solicitud ingresó, bajo el formato DD/MM/AAAA | Fecha | Fecha | No especificado en el documento. | No especificado en el documento. | No editable |
| Estado de la solicitud | Mostrará siempre el estado "Archivado" (Ver Flujo Alternativo 3) | Texto | Texto | No especificado en el documento. | Archivado | No editable |
| Fecha de archivo | Mostrará la fecha en que la solicitud de CUP o OT fue archivada a requerimiento de la Unidad Ejecutora. Formato DD/MM/AAAA | Fecha | Fecha | No especificado en el documento. | No especificado en el documento. | No editable |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| — | No especificado en el documento. | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| — | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Coordinador PRE | Asignar solicitudes (CUP y Opinión Técnica) a Técnico PRE | Descripción del caso de uso; Flujo Básico paso 2; RN03 |
| Coordinador PRE | Archivar solicitudes de la Bandeja Preinversión | Descripción del caso de uso; FA-03; RN08 |
| Coordinador PRE | Cambiar al Técnico PRE previamente asignado a un caso, en cualquier estado | RN09 |
| Coordinador PRE | Consultar el estado de la solicitud de cada proyecto activo | Descripción del caso de uso |
| Técnico PRE | Ingresar a la Bandeja Preinversión y dar clic al caso asignado para acceder a la revisión de CUP (CU-PRE-01.5) u Opinión Técnica (CU-PRE-26) | FA-01, FA-02 |

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01 "Registro y Solicitud de CUP" (actor Técnico URP — antes "Registro de Proyectos")
- CU-PRE-01.5 "Revisión y Emisión de CUP" (actor Técnico PRE — nuevo, desprendido de "Registro de Proyectos")
- UC-PRE-03 "Captura de proyectos"
- CU-PRE-26 "Opinión Técnica"
- CU-PRE-29 "Banco de proyectos"

**Procesos relacionados:**
- Asignación de solicitud a Técnico PRE
- Archivo de solicitud
- Traslado de proyecto a Captura de Proyectos

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## A.1 – Solicitudes Activas

**Nombre:** Bandeja Preinversión / Solicitudes Activas

**Descripción:** Tabla que muestra las solicitudes activas (de CUP u Opinión Técnica) de las Unidades Ejecutoras, permitiendo su asignación a un Técnico PRE.

**Campos:**
- Unidad Ejecutora
- Tipo de Solicitud
- CUP
- Nombre del Proyecto
- Fecha de Solicitud
- Estado
- Asignado a (columna con campo de selección de Técnico PRE)

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Unidad Ejecutora | Tipo de Solicitud | CUP | Nombre del Proyecto | Fecha de Solicitud | Estado | Asignado a |
|---|---|---|---|---|---|---|
| MAG | CUP | — | ABC | XX/MM/XXXX | Enviado a DGICP | — |
| MOPT | OT | XXXX | XYE | XX/MM/XXXX | En análisis | Técnico B |
| ISBM | OT | XXXX | JKL | XX/MM/XXXX | Observado | Técnico C |

**Elementos adicionales visibles en el mockup:**
- Indicadores de conteo por Técnico PRE: "Técnico A (#OT, #CUP)", "Técnico B (#OT, #CUP)", "Técnico C (#OT, #CUP)" (relacionado con RN05).
- Leyenda: "CP" = CUP; "OT" = Opinión Técnica.
- Lista desplegable de técnicos disponibles para asignación: Técnico A, Técnico B, Técnico C.
- Ícono/botón interactivo junto al nombre del proyecto para archivar la solicitud (relacionado con RN08 y FA-03).

**Botones:**
- Guardar

**Acciones:**
- Guardar: confirma la asignación del Técnico PRE seleccionado y dispara el aviso del Anexo A.2.

## A.2 – Aviso de asignación de solicitud

**Nombre:** Aviso "¿Está seguro de asignar esta solicitud?"

**Descripción:** Ventana emergente de confirmación que se muestra al seleccionar un Técnico PRE en la columna "Asignado a".

**Campos:**
> No especificado en el documento.

**Botones:**
- ACEPTAR
- CANCELAR

**Acciones:**
- Aceptar: confirma la asignación, envía alerta a Técnico PRE y mantiene al Coordinador PRE en la pantalla "Bandeja Preinversión".
- Cancelar: no realiza ninguna acción, cierra la ventana emergente.

## A.3 – Aviso de archivo de solicitud

**Nombre:** Aviso de archivo de solicitud

**Descripción:** Ventana emergente que aparece al dar clic en el botón interactivo de archivar, junto al nombre del proyecto en la tabla "Solicitudes Activas".

**Campos:**
> No especificado en el documento.

**Botones:**
- ACEPTAR
- CANCELAR

**Acciones:**
- Aceptar: archiva la solicitud y la envía a la pantalla "Reporte de solicitudes Preinversión archivadas".
- Cancelar: no ejecuta ninguna acción, mantiene al Coordinador PRE en la pantalla "Bandeja Preinversión".

## A.4 – Reporte de solicitudes Preinversión archivadas

**Nombre:** Reporte de solicitudes Preinversión archivadas

**Descripción:** Pantalla que muestra el listado de solicitudes (CUP u Opinión Técnica) que han sido archivadas.

**Campos:**
- Unidad Ejecutora
- Tipo de Solicitud
- CUP
- Nombre del Proyecto
- Fecha de Solicitud
- Estado de la Solicitud
- Fecha de Archivo

**Ejemplo de datos mostrados en el mockup (Anexo A.4):**

| Unidad Ejecutora | Tipo de Solicitud | CUP | Nombre del Proyecto | Fecha de Solicitud | Estado de la Solicitud | Fecha de Archivo |
|---|---|---|---|---|---|---|
| MAG | CUP | — | ABC | XX/MM/XXXX | Archivado | XX/MM/XXXX |

**Botones:**
> No especificado en el documento.

**Acciones:**
> No especificado en el documento.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|-----------------|
| Confirmación (aviso) | "¿Está seguro de asignar esta solicitud?" | Al seleccionar un Técnico PRE en el campo "Asignado a" y oprimir "Guardar" (Anexo A.2; RN03) |
| Alerta | "Se ha asignado para revisión la solicitud XXX" | Al oprimir "Aceptar" en el aviso de asignación (Flujo Básico, paso 4) — se envía al Técnico PRE |
| Confirmación (aviso) | "¿Está seguro de archivar esta solicitud?" (texto del paso 3.4) / "¿Desea archivar esta solicitud?" (texto mostrado en el mockup del Anexo A.3) | Al dar clic en el botón interactivo "Archivar" junto al nombre del proyecto (FA-03, paso 3.4) |

---

# Observaciones

- **Actualización v1.1:** el CU-PRE-01 "Registro de Proyectos" referenciado en la versión 1.0 de este documento fue dividido en dos casos de uso: **CU-PRE-01 "Registro y Solicitud de CUP"** (actor Técnico URP) y **CU-PRE-01.5 "Revisión y Emisión de CUP"** (actor Técnico PRE). Se actualizaron las referencias de FA-01, RN01 y RN07, y las de la sección "Campos", para apuntar al documento correcto según la acción descrita. Ninguna regla de negocio propia de CU-PRE-02 fue modificada en su contenido.
- Existe una discrepancia entre el texto del paso 3.4 del Flujo Alternativo FA-03, que indica el mensaje "¿Está seguro de archivar esta solicitud?", y el texto que aparece en el mockup del Anexo A.3, que muestra "¿Desea archivar esta solicitud?". El documento no aclara cuál es el texto definitivo.
- El paso 3.5 del Flujo Alternativo FA-03 menciona un botón "No" ("Si oprime el Botón 'No' 'Cancelar'"), pero el mockup del Anexo A.3 solo muestra un botón etiquetado "CANCELAR", sin un botón "No" independiente. El documento no aclara si se trata de dos botones distintos o de una única opción con doble nomenclatura.
- La columna "Estado" del mockup de la pantalla "Solicitudes Activas" (Anexo A.1) muestra el valor de ejemplo "En análisis" para una de las filas; sin embargo, la Regla de Negocio RN07, que enumera los estados posibles de dicha columna, solo define "Enviado a DGICP" y "Observado DGICP", sin incluir "En análisis" como estado documentado.
- El Anexo A.1 muestra elementos visuales (indicadores de conteo por técnico "Técnico A (#OT, #CUP)", etc., y una leyenda "CP"/"OT") que no están descritos en el cuerpo del texto del caso de uso, más allá de la referencia general de RN05 al "pie de página de tabla 'Solicitudes Activas'".
- El documento no específica quién ejecuta el paso 1 del Flujo Básico más allá de "Sistema"; no se indica el evento o disparador que hace que la Unidad Ejecutora genere la solicitud dentro de este caso de uso (la generación de la solicitud parece originarse en CU-PRE-01 o CU-PRE-26, pero esto no se documenta explícitamente aquí).
- La contradicción [C-01] sobre el momento exacto de salida del estado "Enviado a DGICP" (ver RN07), que persistía tras la división de CU-PRE-01, fue **resuelta en la Ronda 2** (RQ-C-01): el estado no cambia con la asignación del Coordinador PRE, sino hasta que el Técnico PRE resuelve la solicitud en CU-PRE-01.5. Se corrigió la redacción de RN07 en consecuencia. Ver la anotación de resolución en esa misma sección.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|----------|-------------|-----------|
| Solicitud (CUP / Opinión Técnica) | Solicitud de una Unidad Ejecutora, ya sea de Código Único de Proyecto (CUP) o de Opinión Técnica, mostrada en la Bandeja Preinversión | Registro (Sistema, Flujo Básico paso 1); Asignación a Técnico PRE (RN03, Flujo Básico paso 2-4); Archivo (FA-03) |
| Proyecto | Proyecto asociado a una solicitud de CUP u Opinión Técnica | No especificado en el documento. |
| Técnico PRE (catálogo) | Catálogo de técnicos disponibles para asignación (Anexo C) | Selección (RN04) |
| Reporte de solicitudes archivadas | Registro histórico de solicitudes que han sido archivadas | Registro automático (al archivar, FA-03 paso 3.5-3.6) |

---

# Catálogos Detectados

## Anexo C – Nombres Técnicos PRE

| N° | Técnico |
|----|---------|
| 1 | jorge.ayala |
| 2 | rodrigo.escobar |
| 3 | raquel.gonzalez |
| 4 | victoria.guerra |
| 5 | noemy.mancia |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Registro de solicitud en Bandeja Preinversión | Unidad Ejecutora (a través de CU-PRE-01 o CU-PRE-26) | Sistema (tabla "Solicitudes Activas", Anexo A.1) |
| Envío de alerta de asignación | Clic en "Aceptar" del aviso de asignación (Coordinador PRE) | Técnico PRE |
| Traslado del proyecto a "Captura de Proyectos" | Emisión del CUP (**CU-PRE-01.5**, SF-2 Emitir CUP) | UC-PRE-03 "Captura de proyectos" (RN01) |
| Desaparición de la solicitud de "Solicitudes Activas" y visualización en "Captura de Proyectos" / "Banco de Proyectos" | Emisión de la Opinión Técnica (CU-PRE-26) | UC-PRE-03 "Captura de proyectos"; CU-PRE-29 "Banco de proyectos" (RN02) |
| Archivo de solicitud | Clic en "Aceptar" del aviso de archivo (Coordinador PRE, FA-03) | Pantalla "Reporte de solicitudes Preinversión archivadas" (Anexo A.4) |
| Navegación a revisión de CUP | Clic del Técnico PRE en el caso asignado (FA-01) | **CU-PRE-01.5 "Revisión y Emisión de CUP"** |
| Navegación a pantalla "Opinión Técnica" | Clic del Técnico PRE en el caso asignado (FA-02) | CU-PRE-26 "Opinión Técnica" |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Datos Pendientes de Definir

- No se especifica la prioridad del caso de uso.
- No se especifica un disparador (evento inicial) explícito del caso de uso; el paso 1 del Flujo Básico asume que el Sistema ya recibió la solicitud, sin describir el evento que la origina dentro de este caso de uso.
- La sección "Excepciones" no está desarrollada en el documento (no hay códigos, descripciones ni consecuencias documentadas).
- La tabla de "Errores" no está desarrollada en el documento (no hay códigos de error, descripciones ni acciones esperadas).
- No hay validaciones documentadas para los campos de este caso de uso (tabla "Validaciones" vacía).
- Contradicción no resuelta: el texto del mensaje de archivo difiere entre el paso 3.4 ("¿Está seguro de archivar esta solicitud?") y el mockup del Anexo A.3 ("¿Desea archivar esta solicitud?"); no se define cuál es el texto exacto y definitivo.
- Contradicción no resuelta: el paso 3.5 menciona un botón "No" además de "Cancelar", que no aparece como elemento distinto en el mockup del Anexo A.3.
- El estado "En análisis", mostrado en el mockup del Anexo A.1, no está incluido en el catálogo de estados definido por RN07 ("Enviado a DGICP", "Observado DGICP"), por lo que no queda claro si es un estado adicional no documentado o un valor de ejemplo sin relación con el catálogo real.
- Confirmación con el negocio de la codificación definitiva del caso de uso "CU-PRE-01.5" (código de trabajo propuesto al dividir "Registro de Proyectos"), para que las referencias de este documento (FA-01, RN01, RN07, Eventos del Sistema) queden con el código formal final.