---
id: CU-PRE-24
codigo: CU-PRE-24
nombre: Viabilidad
modulo: "No especificado en el documento. (El documento no tiene un campo explícito llamado 'Módulo' ni una sección 'Ruta de Acceso')"
submodulo: "Gestión del Proyecto > Viabilidad (derivado del paso 1 del Flujo Básico 1 — 'Ingresa a la pestaña \"Gestión del Proyecto\" en la sección \"Viabilidad\"'; el documento no tiene un campo explícito llamado 'Submódulo')"
version: "1.0 (portada, encabezados de página e Historial de Revisiones coinciden en la versión 1.0; ver Observaciones sobre la discrepancia de fechas: encabezados 'MAYO 2026' vs. Historial 'AGO 2025')"
fuente_pdf: CU-PRE-24_Viabilidad_MAY0_2026_V1_F2.pdf
pagina_inicio: 1
pagina_fin: 10

actor_principal: "Técnico URP / Viabilizador (el documento lista ambos en 'Actores' sin distinguir actor principal de secundario)"

actores_secundarios:
  - "Sistema (no figura en la sección 'Actores' del documento; se incluye por ser quien ejecuta los pasos automáticos de los flujos: validaciones, mensajes, notificaciones, cambios de estado, habilitación/deshabilitación de campos y botones)"
  - "Demás actores (mención genérica 'Todos los demás actores' en RN01; no figuran en la sección 'Actores' del documento ni se nombran)"

prioridad: "No especificado en el documento."

estado: Analizado

depende_de:
  - "Todos los casos de uso de Formulación, Evaluación y Programación (Precondiciones)"
  - CU-PRE-01 Registro de Proyectos
  - CU-PRE-04 Identificación
  - CU-PRE-07 Población Objetivo
  - CU-PRE-11 Descripción técnica
  - CU-PRE-17 Presupuesto de inversión
  - CU-PRE-18 Flujo de costos de Operación y Mantenimiento
  - CU-PRE-21 Flujo de Caja y cálculo de Indicadores
  - CU-PRE-23 Indicadores del Proyecto

casos_relacionados:
  - CU-PRE-25 Elegibilidad
  - CU-PRE-26 Opinión Técnica
  - CU-PRE-26.5 Priorización

roles:
  - Técnico URP
  - Viabilizador
  - "Sistema (no figura en la sección 'Actores' del documento; ejecuta los pasos automáticos)"
  - "Demás actores (mención genérica en RN01; solo visualización, según credenciales)"

pantallas:
  - "Anexo A.1 Pantallas (Ficha del Proyecto — Viabilidad)"
  - "Anexo A.2 Botón de alerta 'Emitir Viabilidad'"
  - "Captura de proyectos (mencionada en FB2 paso 2; sin mockup en el documento)"

procesos:
  - Solicitud de Viabilidad
  - Revisión de información para emisión de Viabilidad
  - Envío de comentarios (devolución del proyecto)
  - Emisión de Viabilidad

servicios_externos: []

entidades:
  - Proyecto
  - Documento de Preinversión
  - Nota de solicitud de OT
  - Otros documentos
  - Comentarios del Viabilizador
  - Observaciones Generales/Justificación de la Viabilidad
  - Devoluciones del proyecto
  - Notificación
  - Comentarios OT / Justificación Institución

catalogos:
  - Estados del proyecto (valores mencionados)
  - Indicadores de evaluación (valores mostrados en el mockup)
  - Formatos de archivo de carga

palabras_clave:
  - viabilidad
  - viabilizador
  - técnico URP
  - solicitar viabilidad
  - emitir viabilidad
  - enviar comentarios
  - elegibilidad
  - opinión técnica
  - ficha del proyecto
  - preinversión
  - proyecto viable
  - observado

ultima_actualizacion: "MAYO 2026 (fecha de los encabezados de página del documento; el Historial de Revisiones solo registra una entrada de AGO 2025)"

trazabilidad:
  informacion_general:
    pagina: 3
  historial_revisiones:
    pagina: 2
  flujo_principal:
    FB1:
      nombre: Solicitud de Viabilidad por el Técnico URP
      pagina: 3
    FB2:
      nombre: Revisión de información para emisión de Viabilidad
      pagina: 4
  flujos_alternos:
    FA01:
      nombre: Enviar comentarios
      pagina: 4
    FA02:
      nombre: Emitir Viabilidad
      pagina: 4
      nota: "El encabezado del FA02 está en la página 4; sus pasos 2.1 a 2.7 están en la página 5."
  reglas_negocio:
    RN01:
      pagina: 5
    RN02:
      pagina: 5
    RN03:
      pagina: 5
      nota: "Continúa en la página 6."
    RN04:
      pagina: 6
    RN05:
      pagina: 6
    RN06:
      pagina: 6
    RN07:
      pagina: 6
    RN08:
      pagina: 6
    RN09:
      pagina: 6
    RN10:
      pagina: 6
    RN11:
      pagina: 6
  anexos:
    A1:
      nombre: Anexo A.1 Pantallas
      pagina: 7
    A2:
      nombre: Anexo A.2 Botón de alerta "Emitir Viabilidad"
      pagina: 8
    B1:
      nombre: "Anexo B – Requerimientos Funcionales / B.1– Formatos / Pantalla Anexo A.1"
      pagina: 8
      nota: "La tabla de formatos abarca las páginas 8 a 10."
  nota_paginas: "Los números de página se determinaron extrayendo el texto de cada página del PDF de forma individual (10 páginas). El PDF no incluye numeración de página explícita en el pie, por lo que corresponden a la posición física de cada página dentro del archivo."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Viabilidad |
| Código | CU-PRE-24 |
| Módulo | No especificado en el documento. |
| Fuente | CU-PRE-24_Viabilidad_MAY0_2026_V1_F2.pdf — Dirección General de Inversión y Crédito Público / Sistema de Información de Inversión Pública — "Especificación de Casos de Uso: 'CU-PRE-24 Viabilidad'" |
| Versión | 1.0 |
| Fecha (encabezado de página) | MAYO 2026 |

**Campos requeridos (según el PDF):**
- CUP
- Nombre del Proyecto
- Objetivo General
- Descripción
- Productos
- Población objetivo
- Inversión estimada
- Resumen del presupuesto
- Costo de operación
- Costo de mantenimiento
- Fuente de financiamiento estimada
- Indicadores de evaluación
- Comentarios del Viabilizador
- Observaciones Generales/Justificación de la Viabilidad

**Ruta de Acceso (según el PDF):**

> No especificado en el documento. Este documento usa el patrón de tabla "Identificación" y no incluye una sección "Ruta de Acceso".

> Nota: el valor `submodulo` del Front Matter ("Gestión del Proyecto > Viabilidad") se derivó del paso 1 del Flujo Básico 1 ("Ingresa a la pestaña 'Gestión del Proyecto' en la sección 'Viabilidad'") y del paso 4 del Flujo Básico 2; el documento no tiene un campo explícito llamado "Módulo" ni "Submódulo".

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|---|---|---|---|---|
| AGO 2025 | 1.0 | Primera Versión | Equipo Preinversión | |

> Nota de ambigüedad: la versión registrada en el Historial (1.0) coincide con la de la portada y los encabezados de página. Sin embargo, los encabezados de página indican "Fecha: MAYO 2026", mientras que la única entrada del Historial de Revisiones tiene fecha "AGO 2025", sin una entrada que corresponda a MAYO 2026.

---

# Objetivo

Este caso de uso permite:

1. Al actor "Técnico URP" solicitar Viabilidad a un proyecto
2. Al actor "Viabilizador" emitir viabilidad a un proyecto, así como agregar comentarios en caso que se requieran ajustes a la información del mismo.

---

# Descripción

Este caso de uso permite:

1. Al actor "Técnico URP" solicitar Viabilidad a un proyecto
2. Al actor "Viabilizador" emitir viabilidad a un proyecto, así como agregar comentarios en caso que se requieran ajustes a la información del mismo.

> El documento no incluye un apartado "Objetivo" separado de la "Descripción caso de uso"; ambas secciones transcriben el mismo texto de la tabla "Identificación".

---

# Actor Principal

Según la sección "Actores" del documento:

- Técnico URP
- Viabilizador

> El documento lista ambos actores sin distinguir cuál es el actor principal y cuál el secundario.

---

# Actores Secundarios

- **Sistema** — No figura en la sección "Actores" del documento. Se incluye por ser quien ejecuta los pasos automáticos descritos en los flujos (habilitar botones, validar, mostrar mensajes, enviar notificaciones, cambiar el estado del proyecto, habilitar/bloquear campos de CU-PRE-04 a CU-PRE-23).
- **Demás actores** — Mención genérica en RN01 ("Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales"). No figuran en la sección "Actores" del documento ni se identifican por nombre.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Todos los casos de uso de Formulación, Evaluación y Programación.

> Nota de ambigüedad: el documento lista la precondición sin un verbo explícito (p. ej. "contar con", "haber completado") que aclare la condición exacta, ni identifica los códigos de los casos de uso que componen "Formulación, Evaluación y Programación". El paso 4 del Flujo Básico 1 menciona una validación del registro "desde el CU-PRE-04 'Identificación' hasta el CU-PRE-23 'Indicadores del Proyecto', según corresponda", pero el documento no afirma que ese rango sea equivalente a la precondición.

---

# Flujo Principal

## Flujo Básico 1 – FB Solicitud de Viabilidad por el Técnico URP

1. **Técnico URP:** Ingresa a la pestaña "Gestión del Proyecto" en la sección "Viabilidad", carga el documento de Preinversión y Anexos del proyecto.
2. **Sistema:** Habilita el botón "Solicitar Viabilidad"-
3. **Técnico URP:** Da clic en el botón "Solicitar Viabilidad".
4. **Sistema:** El Sistema valida que se haya completado el registro de la información en los casos de uso desde el CU-PRE-04 "Identificación" hasta el CU-PRE-23 "Indicadores del Proyecto", según corresponda.
5. **Sistema:** Muestra mensaje emergente que le indica al Técnico URP que "la solicitud del proyecto ha sido enviada al proceso de viabilidad exitosamente".
6. **Sistema:** Envía notificación al "Viabilizador" informando que le ha llegado la solicitud.

## Flujo Básico 2 – FB Revisión de información para emisión de Viabilidad

1. **Sistema:** Notifica al Viabilizador que hay un proyecto en bandeja y en el mensaje le muestra un link para que ingrese directamente al formulario del Anexo A.1.
2. **Viabilizador:** Tiene dos opciones:
   a) Da clic en el link e ingresa directamente al formulario A.1.
   b) Ingresa a la pantalla "Captura de proyectos", con la lista de los proyectos de la entidad en el estado "Proyecto Formulado"
3. **Viabilizador:** Da clic en el proyecto sobre el cual va a emitir viabilidad.
4. **Viabilizador:** O ingresa a la pestaña "Gestión del Proyecto" en la sección "Viabilidad"
5. **Sistema:** Despliega la pantalla del Anexo A.1.

> Nota de ambigüedad: el paso 4 comienza con "O ingresa...", lo que sugiere una alternativa a los pasos 2 y/o 3, pero el documento no indica a qué paso u opción es alternativo.

---

# Flujos Alternos

## FA01 — Enviar comentarios

**Condición**

> No especificado en el documento.

**Flujo**

1.1. **Viabilizador:** Registra comentarios en los campos de la columna "Comentarios del viabilizador" y en el campo de "observaciones generales" y da clic en el botón "Guardar".
1.2. **Sistema:** Guarda cada uno de los comentarios registrados por el actor "Viabilizador".
1.3. **Viabilizador:** Da clic en el botón "Enviar comentarios"
1.4. **Sistema:** Cambia el estado del proyecto a "Observado" y habilita los campos de las pantallas de CU-PRE-04 "Identificación" a CU-PRE-23 "Indicadores del Proyecto".
1.5. **Sistema:** Notifica al actor "Técnico URP" que el "Viabilizador" ha enviado comentarios a la información registrada, para su ajuste.
1.6. **Técnico URP:** Realiza los ajustes en los campos que correspondan, carga el perfil ajustado (si aplica), y vuelve a iniciar el proceso de solicitud de viabilidad, respondiendo los comentarios en CU-PRE-26 "Opinión Técnica"

> Nota de reconciliación: en rondas anteriores esta referencia a CU-PRE-26 se identificó como una posible referencia cruzada incorrecta y no se modela como comportamiento esperado en las Historias de Usuario (ver "Contradicciones no resueltas del CU original", punto 3, y Observación 6).

**Resultado**

> No especificado en el documento.

## FA02 — Emitir Viabilidad

**Condición**

> No especificado en el documento.

**Flujo**

2.1. **Viabilizador:** Registra comentarios en el campo "observaciones generales/Justificación de la viabilidad".
2.2. **Viabilizador:** Da clic en el botón "Emitir Viabilidad"
2.3. **Sistema:** Cambia el estado del proyecto a "Proyecto Viable" y muestra el mensaje "La viabilidad del proyecto ha sido emitida con éxito".
Si es la primera vez que se gestiona la viabilidad, el mensaje anterior incluirá el texto: "Es necesario continuar con la gestión de Elegibilidad". (Ver Anexo A.2).
Si el proyecto ya contaba con Elegibilidad, el mensaje anterior incluirá el texto "Es necesario continuar con la gestión de la Opinión Técnica". (Similar al Anexo A.2 pero mostrará el botón "Ir a OT").
2.4. **Viabilizador:** Si selecciona el botón "Ir a Elegibilidad" del Anexo A.2, el sistema envía al usuario a CU-PRE-25 "Elegibilidad". Si selecciona "Salir", el sistema queda en la pantalla del Anexo A.1.
2.5. **Sistema:** Notifica al actor "Técnico URP" que se ha emitido Viabilidad al proyecto y deshabilita la pantalla del Anexo A.1
2.6. **Sistema:** Habilita el botón "Ir a Elegibilidad" y permite la edición de la pantalla "Elegibilidad" (Solo en los casos en que esta gestión vaya a realizarse por primera vez. Cuando el proyecto ya haya pasado por un proceso de Elegibilidad, el Sistema no habilitará CU-PRE-25 "Elegibilidad").
2.7. **Viabilizador:** La primera vez da clic en el botón "Ir a Elegibilidad".
Las veces posteriores da clic en el botón "Ir a OT".

**Resultado**

> No especificado en el documento.

> Nota de ambigüedad: el paso 2.4 ya permite seleccionar "Ir a Elegibilidad" desde el Anexo A.2, mientras que el paso 2.6 describe que el Sistema habilita el botón "Ir a Elegibilidad" después de la notificación del paso 2.5, y el paso 2.7 vuelve a indicar el clic en ese botón. El documento no aclara si se trata del mismo botón (el del mensaje del Anexo A.2) o del botón de la pantalla del Anexo A.1.

---

# Excepciones

| Código | Descripción | Consecuencia |
|---|---|---|
| Sin código en el documento | Solicitud de viabilidad que responde a una observación de la OT sin que el Técnico URP haya respondido todos los comentarios emitidos por OT en la columna "Justificación Institución" de CU-PRE-26 "Opinión técnica" (RN11). | El sistema deberá mostrar un mensaje que diga "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad" y no permitirá solicitar Viabilidad nuevamente. |
| Sin código en el documento | La validación del paso 4 del FB1 (registro completado desde CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto") no se cumple. | No especificado en el documento. |

> El documento no incluye una sección formal de "Excepciones". Las filas anteriores provienen de las condiciones de validación descritas en el FB1 y en RN11.

---

# Postcondiciones

1. CU-PRE-25 Elegibilidad.
2. CU-PRE-26 Opinión Técnica
3. CU-PRE-26.5 Priorización

> Nota de ambigüedad: el documento lista las postcondiciones únicamente como referencias a otros casos de uso, sin un verbo explícito que aclare la condición resultante (p. ej. "queda habilitado", "continúa con"). Adicionalmente, según FA02 paso 2.6 y RN03, CU-PRE-25 "Elegibilidad" solo aplica la primera vez que se gestiona el proyecto, condición que no se refleja en la lista de postcondiciones.

---

# Reglas de Negocio

## RN01

**Descripción:** El Viabilizador es el único que puede registrar/editar información en la sección, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** Sección "Reglas del Negocio" (página 5).

## RN02

**Descripción:** Para que se habilite el botón "Solicitar Viabilidad", el Técnico URP previamente deberá adjuntar el documento de Preinversión, y si aplica, otros anexos del proyecto, en el campo correspondiente.

Para que se habilite el botón "Ir a Elegibilidad", previamente el Viabilizador debió dar clic en el botón "Emitir Viabilidad". Este proceso de Elegibilidad solo se requerirá una vez.

**Origen:** Sección "Reglas del Negocio" (página 5).

## RN03

**Descripción:** El proyecto debe pasar por los tres filtros (Viabilidad, Elegibilidad, Opinión Técnica) la primera vez que realiza la gestión del proyecto. Sin embargo, después de que el viabilizador haya emitido por primera vez la Elegibilidad, ya no es necesario volver a ella, en caso de algún ajuste del proyecto o actualización. Por tanto, se salta de viabilidad, a OT. Solamente se puede ajustar la elegibilidad si la OT la devuelve para ajustes.

**Origen:** Sección "Reglas del Negocio" (páginas 5–6).

## RN04

**Descripción:** Una vez que el Técnico URP da clic en el botón "Solicitar Viabilidad" el Sistema bloqueará para edición los campos de los casos de uso CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto", y deshabilitará el botón "Solicitar Viabilidad".

**Origen:** Sección "Reglas del Negocio" (página 6).

## RN05

**Descripción:** Una vez que el Viabilizador dé clic en el botón "Enviar comentarios" el Sistema habilitará nuevamente para edición los campos de los casos de uso CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto", y el botón "Solicitar Viabilidad".

**Origen:** Sección "Reglas del Negocio" (página 6).

## RN06

**Descripción:** El botón "Solicitar Viabilidad" solo estará habilitado para el "Técnico URP".

**Origen:** Sección "Reglas del Negocio" (página 6).

## RN07

**Descripción:** "Los botones "Enviar comentarios", "Emitir Viabilidad" e "Ir a Elegibilidad" (si es la primera vez, o "Ir a OT" si ya cuenta con Elegibilidad), sólo estarán habilitados para el Viabilizador.

**Origen:** Sección "Reglas del Negocio" (página 6).

## RN08

**Descripción:** El sistema mostrará información de apoyo que oriente al Viabilizador sobre los criterios a revisar en cada campo. Dicha información se mostrará al acercar el cursor al signo de pregunta en cada campo.

**Origen:** Sección "Reglas del Negocio" (página 6).

## RN09

**Descripción:** Este formato de viabilidad aplica para todas las etapas del proyecto.

**Origen:** Sección "Reglas del Negocio" (página 6).

## RN10

**Descripción:** El proyecto se puede devolver cuantas veces sea necesario. Los comentarios del viabilizador se deben guardar cada vez que se devuelva y deben poderse consultar en pantalla las observaciones de cada devolución. El sistema debe mostrar cuántas veces se ha devuelto.

**Origen:** Sección "Reglas del Negocio" (página 6).

## RN11

**Descripción:** En caso que la solicitud de viabilidad se haga respondiendo a una observación por parte de la OT, el sistema debe validar que previamente se hayan respondido los comentarios en CU-PRE-26 "Opinión Técnica" por parte del Técnico URP.

El Técnico URP debe consultar las observaciones a través del botón "Ver comentarios OT" ubicado en la parte superior del Anexo A.1. Al hacer clic el sistema debe remitirlo a los comentarios de CU-PRE-26 "Opinión técnica" para que pueda diligenciar la columna "Justificación Institución" y oprimir el botón "Guardar Ajustes".

El sistema debe validar que si hay observaciones de OT en CU-PRE-26 "Opinión técnica" el Técnico URP haya respondido todos los comentarios emitidos por OT en la columna "Justificación Institución" para que le permita solicitar Viabilidad nuevamente. De lo contrario, el sistema deberá mostrar un mensaje que diga "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad".

**Origen:** Sección "Reglas del Negocio" (página 6).

---

# Campos

### Pantalla Anexo A.1 (según Anexo B – Requerimientos Funcionales, B.1– Formatos)

> La numeración de la pantalla en el Anexo B.1 ("Pantalla Anexo A.1") coincide con la usada en los flujos y en el título del mockup ("Anexo A.1 Pantallas").

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Nota de solicitud de OT | Campo para que el Técnico URP cargue la nota de solicitud de Opinión Técnica en formato PDF/A. | Botón de carga | Botón de carga | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Contradicción: la columna "Editable" indica "No", pero el Detalle describe un campo que el Técnico URP diligencia (carga de archivo). En el mockup se rotula "Nota de solicitud OT". Ningún flujo ni regla de negocio menciona la carga de este documento. |
| Documento de Preinversión | Campo para que el Técnico URP cargue el documento de Preinversión en formato PDF/ o .doc. Campo obligatorio. | Botón de carga | Botón de carga | Sí ("Campo obligatorio", según Detalle del Anexo B.1) | No especificado en el documento. | Editable: No (según Anexo B.1). Contradicción: la columna "Editable" indica "No", pero el Detalle describe un campo que el Técnico URP diligencia (carga de archivo). Requisito para habilitar "Solicitar Viabilidad" (RN02). El formato se transcribe literalmente como "PDF/ o .doc". |
| Otros documentos | Campo para que el Técnico URP cargue otros documentos relacionados, según corresponda. | Botón de carga | Botón de carga | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Contradicción: la columna "Editable" indica "No", pero el Detalle describe un campo que el Técnico URP diligencia (carga de archivo). En el mockup se rotula "Otros documentos anexos"; en RN02 "otros anexos del proyecto"; en FB1 paso 1 "Anexos del proyecto". |
| Ver comentarios OT | Botón que muestra al técnico URP los comentarios de la OT para que desde allí diligencie los campos de "Justificación Institución". | Botón de carga | Botón de carga | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: el Tipo/Formato "Botón de carga" es el mismo de los campos de carga de archivos, pero el Detalle describe un botón que muestra comentarios, no uno de carga. Ver RN11. |
| CUP | Muestra el Código Único de Proyecto (CUP) emitido en el CU-PRE-01 "Registro de Proyectos" | Numérico | Numérico | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Nombre del proyecto | Muestra el Nombre del proyecto registrado en el CU-PRE-01 "Registro de Proyectos" | Alfanumérico | Alfanumérico | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). Nota: "Alfanumérico" es el único campo de la tabla con este Tipo; los demás campos de texto usan "Texto". Se transcribe literalmente. |
| Objetivo General | Muestra el objetivo del proyecto registrado en el campo "Objetivo General" del CU-PRE-04 "Identificación" | Texto | Texto | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Descripción | Muestra la información del campo "Descripción del proyecto" del Anexo A1 del CU-PRE-11 "Descripción técnica" | Texto | Texto | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). En el mockup se rotula "Descripción del proyecto". |
| Productos | Muestra la información de las columnas "Nombre del producto" del Anexo A1, del CU-PRE-23 "Indicadores del Proyecto". Además, debe contener un link que direccione a la tabla de CU-PRE-23 "Indicadores del Proyecto". Estas ventanas no son editables. | Texto | Texto | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Población objetivo | Muestra la información de la columna "N° de personas" de la "Población objetivo" del Anexo A1 del CU-PRE-07 "Población Objetivo". Además, debe contener un link que direccione a la tabla de CU-PRE-07 "Población Objetivo". Estas ventanas no son editables. | Numérico | Numérico | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Inversión estimada | Muestra el valor de la columna "Monto" -celda "Total de inversión" (A precios de mercado) del Anexo A.1 del CU-PRE-17 "Presupuesto de inversión". El sistema deberá agregar el separador de miles (,). Además, debe contener un link que direccione a la tabla de CU-PRE-17 "Presupuesto de inversión". Estas ventanas no son editables. | Moneda | Moneda | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Resumen del presupuesto | Muestra la Tabla del Anexo A.4 del CU-PRE-17 "Presupuesto de inversión". Además, debe contener un link que direccione a la tabla de CU-PRE-17 "Presupuesto de inversión", estas ventanas no son editables. | Moneda | Moneda | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: el Tipo/Formato es "Moneda", pero el Detalle indica que se muestra una tabla completa (Anexo A.4 de CU-PRE-17), cuya estructura no se describe en este documento. |
| Costo de operación | Muestra el valor de los costos de operación, toma el valor de la celda "Total (P.M.)" del Año 1 de la tabla "Costos de Operación" del Anexo A.1 del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Además, debe contener un link que direccione a la tabla de CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Estas ventanas no son editables. | Moneda | Moneda | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). En el mockup se rotula "Costos de Operación". |
| Costo de mantenimiento | Muestra el valor de los costos de mantenimiento, toma el valor de la celda "Total (P.M.)" del Año 1 de la tabla "Costos de Mantenimiento" del Anexo A.1 del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Además, debe contener un link que direccione a la tabla de CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Estas ventanas no son editables. | Moneda | Moneda | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). En el mockup se rotula "Costos de Mantenimiento". |
| Fuente de financiamiento | Muestra la tabla del Anexo A.5 del CU-PRE-17 "Presupuesto de inversión" | Texto | Texto | Incluido en "Campos requeridos" (como "Fuente de financiamiento estimada") | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: el Tipo/Formato es "Texto", pero el Detalle indica que se muestra una tabla (Anexo A.5 de CU-PRE-17), cuya estructura no se describe en este documento. A diferencia de los demás campos de consulta, el Detalle no menciona un link a CU-PRE-17. |
| Indicadores de evaluación | Muestra los indicadores de evaluación calculados por el Sistema, toma la información del campo "Indicadores" del CU-PRE-21 "Flujo de Caja y cálculo de Indicadores". Además, debe contener un link que direccione a la tabla de CU-PRE-21 Flujo de Caja y cálculo de Indicadores". Estas ventanas no son editables. | Moneda/ Porcentaje | Moneda/ Porcentaje | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). Las fórmulas de cálculo no se incluyen en este documento; se remiten a CU-PRE-21. En el mockup se muestran las etiquetas "VAN:", "TIR:", "R B/C". Tipo compuesto "Moneda/ Porcentaje" transcrito literalmente; el documento no indica qué indicador corresponde a cada tipo. |
| Comentarios del Viabilizador | Campo para que el Viabilizador registre comentarios a la información | Texto | Texto | No especificado en el documento. (Incluido en "Campos requeridos") | No especificado en el documento. | Editable: Sí (según Anexo B.1). En el mockup corresponde a la columna "COMENTARIOS DEL VIABILIZADOR" de la tabla "FICHA DEL PROYECTO", con una celda por cada campo. |
| Observaciones Generales/Justificación de la Viabilidad | Campo para que el Viabilizador registre observaciones generales al proyecto y justifique por qué emitió la viabilidad o va a devolver el proyecto. Campo obligatorio | Texto | Texto | Sí ("Campo obligatorio", según Detalle del Anexo B.1) | No especificado en el documento. | Editable: Sí (según Anexo B.1). En FA01 paso 1.1 se denomina "observaciones generales"; en FA02 paso 2.1, "observaciones generales/Justificación de la viabilidad". Debe estar registrado y guardado para habilitar "Emitir viabilidad" (Anexo B.1). |
| Solicitar viabilidad | Este botón se habilitará una vez que el Técnico URP haya cargado los documentos de soporte en los campos correspondientes. Únicamente podrá diligenciarlo el Técnico URP. | Botón | Botón | No aplica | No especificado en el documento. | Editable: No (según Anexo B.1). Ver RN02, RN04, RN05, RN06. |
| Enviar comentarios | Botón que permite al Viabilizador enviar comentarios al Técnico URP sobre la información registrada, según corresponda. Este botón se activa una vez que el Técnico URP dé clic en el botón "Solicitar Viabilidad". | Botón | Botón | No aplica | No especificado en el documento. | Editable: No (según Anexo B.1). Ver RN05, RN07. |
| Emitir viabilidad | Permitirá al Viabilizador otorgar la viabilidad a cada versión de las diferentes etapas del proyecto. Se habilita una vez que dicho actor haya registrado y guardado la información del campo "Observaciones Generales/Justificación de la Viabilidad". | Botón | Botón | No aplica | No especificado en el documento. | Editable: No (según Anexo B.1). Ver RN07, RN09. |
| Pasar al siguiente filtro | Se habilita una vez emitida la Viabilidad para pasar a Elegibilidad, o a Opinión Técnica, según corresponda. | Botón | Botón | No aplica | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad (doble nomenclatura): el Anexo B.1 llama a este botón "Pasar al siguiente filtro", mientras que el mockup lo rotula "IR A ELEGIBILIDAD" y los flujos (FA02) y reglas (RN02, RN07) lo llaman "Ir a Elegibilidad" / "Ir a OT". |

> El botón "Guardar" (mockup del Anexo A.1 y FA01 paso 1.1) no aparece en la tabla de formatos del Anexo B.1.

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|---|---|---|
| Documento de Preinversión (y otros anexos, si aplica) | Debe adjuntarse previamente para que se habilite el botón "Solicitar Viabilidad" (RN02; Anexo B.1 "Campo obligatorio"). | No especificado en el documento. |
| Registro de información de CU-PRE-04 a CU-PRE-23 | El Sistema valida que se haya completado el registro de la información en los casos de uso desde el CU-PRE-04 "Identificación" hasta el CU-PRE-23 "Indicadores del Proyecto", según corresponda (FB1 paso 4). | No especificado en el documento. |
| Comentarios de OT en CU-PRE-26 (columna "Justificación Institución") | Si hay observaciones de OT en CU-PRE-26 "Opinión técnica", el Técnico URP debe haber respondido todos los comentarios emitidos por OT para que le permita solicitar Viabilidad nuevamente (RN11). | "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad" (sin Anexo/mockup propio asociado). |
| Observaciones Generales/Justificación de la Viabilidad | Campo obligatorio (Anexo B.1). Debe haberse registrado y guardado para habilitar el botón "Emitir viabilidad" (Anexo B.1). | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|---|---|---|
| Sin código en el documento | Intento de solicitar Viabilidad sin haber respondido todos los comentarios de OT en CU-PRE-26 "Opinión técnica" (RN11). | Mostrar el mensaje "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad" (sin Anexo/mockup propio asociado). |
| Sin código en el documento | Registro incompleto de la información de CU-PRE-04 a CU-PRE-23 al validar la solicitud (FB1 paso 4). | No especificado en el documento. |

> El documento no incluye una tabla de errores con códigos.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Técnico URP | Cargar la nota de solicitud de OT, el documento de Preinversión y otros documentos. | Anexo B.1 ("Campo para que el Técnico URP cargue..."); FB1 paso 1; RN02. |
| Técnico URP | Solicitar Viabilidad (único rol con el botón habilitado). | RN06; Anexo B.1 ("Únicamente podrá diligenciarlo el Técnico URP"). |
| Técnico URP | Consultar los comentarios de la OT mediante "Ver comentarios OT" y diligenciar la columna "Justificación Institución" en CU-PRE-26. | RN11; Anexo B.1. |
| Técnico URP | Realizar ajustes en los campos de CU-PRE-04 a CU-PRE-23 tras el envío de comentarios, cargar el perfil ajustado (si aplica) y volver a solicitar viabilidad. | FA01 paso 1.6; RN05. |
| Viabilizador | Único rol que puede registrar/editar información en la sección, según credenciales. | RN01. |
| Viabilizador | Registrar "Comentarios del Viabilizador" y "Observaciones Generales/Justificación de la Viabilidad"; guardar. | FA01 paso 1.1; FA02 paso 2.1; Anexo B.1 (Editable: Sí). |
| Viabilizador | Usar los botones "Enviar comentarios", "Emitir Viabilidad" e "Ir a Elegibilidad" / "Ir a OT" (habilitados solo para este rol). | RN07. |
| Demás actores (no nombrados) | Únicamente visualizar la información de las Unidades Ejecutoras, según credenciales. | RN01. |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos" (origen de CUP y Nombre del proyecto)
- CU-PRE-04 "Identificación" (origen de Objetivo General; inicio del rango de casos de uso validados/bloqueados/habilitados)
- CU-PRE-07 "Población Objetivo" (origen de Población objetivo)
- CU-PRE-11 "Descripción técnica" (origen de Descripción)
- CU-PRE-17 "Presupuesto de inversión" (origen de Inversión estimada, Resumen del presupuesto y Fuente de financiamiento)
- CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" (origen de Costo de operación y Costo de mantenimiento)
- CU-PRE-21 "Flujo de Caja y cálculo de Indicadores" (origen de Indicadores de evaluación)
- CU-PRE-23 "Indicadores del Proyecto" (origen de Productos; fin del rango de casos de uso validados/bloqueados/habilitados)
- CU-PRE-25 "Elegibilidad" (postcondición; destino del botón "Ir a Elegibilidad")
- CU-PRE-26 "Opinión Técnica" (postcondición; respuesta a comentarios OT en la columna "Justificación Institución")
- CU-PRE-26.5 "Priorización" (postcondición)
- "Todos los casos de uso de Formulación, Evaluación y Programación" (precondición, sin códigos específicos)

**Procesos relacionados:**
- Filtros de gestión del proyecto: Viabilidad, Elegibilidad, Opinión Técnica (RN03)
- Devolución del proyecto (RN10)

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Anexo A.1 Pantallas (Ficha del Proyecto)

**Descripción:** Pantalla de viabilidad del proyecto en la pestaña "Gestión del Proyecto", sección "Viabilidad". Muestra en modo consulta la información del proyecto proveniente de otros casos de uso, junto a una columna para los comentarios del Viabilizador, y permite la carga de documentos por parte del Técnico URP.

**Campos (según el mockup):**
- CUP
- NOMBRE DEL PROYECTO
- Nota de solicitud OT (ícono de carga)
- Documento de Preinversión (ícono de carga)
- Otros documentos anexos (ícono de carga)
- Tabla "FICHA DEL PROYECTO" con columnas "CAMPOS" y "COMENTARIOS DEL VIABILIZADOR", filas:
  - Objetivo General (con ícono "?")
  - Descripción del proyecto (con ícono "?")
  - Productos (con ícono "?")
  - Población Objetivo (con ícono "?")
  - Inversión Estimada (con ícono "?")
  - Resumen del Presupuesto (con ícono "?")
  - Costos de Operación (con ícono "?")
  - Costos de Mantenimiento (con ícono "?")
  - Fuente de Financiamiento (con ícono "?")
  - Indicadores de Evaluación (con ícono "?")
- Observaciones Generales / Justificación de la Viabilidad

**Botones (según el mockup):**
- VER COMENTARIOS OT
- SOLICITAR VIABILIDAD
- GUARDAR
- ENVIAR COMENTARIOS
- EMITIR VIABILIDAD
- IR A ELEGIBILIDAD

**Acciones:**
- Carga de documentos por el Técnico URP (FB1 paso 1; RN02).
- Solicitud de viabilidad (FB1 pasos 2–3).
- Consulta de comentarios de OT (RN11).
- Registro y guardado de comentarios del Viabilizador (FA01 pasos 1.1–1.2).
- Envío de comentarios (FA01 paso 1.3).
- Emisión de viabilidad (FA02 pasos 2.1–2.2).
- Paso a Elegibilidad u OT (FA02 pasos 2.4, 2.6, 2.7).
- Información de apoyo al pasar el cursor sobre el signo de pregunta de cada campo (RN08).
- Links a las tablas de origen de los campos de consulta (Anexo B.1).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Elemento | Valor mostrado |
|---|---|
| CUP | XXXX |
| NOMBRE DEL PROYECTO | XXXXXXXXXXXXXXXXXXXXXXX (el último carácter se ve cortado en el borde del texto en el mockup original; se transcribe tal como se ve, sin completarlo) |
| Indicadores de Evaluación | VAN: / TIR: / R B/C (etiquetas sin valores) |
| Resto de campos de la columna "CAMPOS" | Cuadros de texto vacíos |
| Columna "COMENTARIOS DEL VIABILIZADOR" | Celdas vacías |
| Observaciones Generales / Justificación de la Viabilidad | Cuadro de texto vacío |

> Los valores "XXXX" son marcadores de posición, no datos de ejemplo concretos.

## Anexo A.2 Botón de alerta "Emitir Viabilidad"

**Descripción:** Mensajes emergentes mostrados tras dar clic en "Emitir Viabilidad" (FA02 paso 2.3). El mockup contiene dos ventanas.

**Campos:** No aplica.

**Botones:**
- Ventana 1: IR A ELEGIBILIDAD, SALIR
- Ventana 2: ACEPTAR

**Acciones:**
- "Ir a Elegibilidad": el sistema envía al usuario a CU-PRE-25 "Elegibilidad" (FA02 paso 2.4).
- "Salir": el sistema queda en la pantalla del Anexo A.1 (FA02 paso 2.4).
- "Aceptar": No especificado en el documento.

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**

| Ventana | Ícono | Título | Texto | Botones |
|---|---|---|---|---|
| 1 (izquierda) | Marca de verificación verde | ¡Enviado! | La viabilidad del proyecto ha sido emitida con éxito. Es necesario continuar con la gestión de Elegibilidad | IR A ELEGIBILIDAD (verde), SALIR (rojo) |
| 2 (derecha) | Marca de verificación verde | ¡Enviado! | La viabilidad del proyecto ha sido emitida con éxito. | ACEPTAR (azul) |

## Variante del mensaje de emisión con botón "Ir a OT" (sin Anexo/mockup propio asociado)

**Descripción:** Mensaje que se muestra cuando el proyecto ya contaba con Elegibilidad; incluye el texto "Es necesario continuar con la gestión de la Opinión Técnica". El documento indica "(Similar al Anexo A.2 pero mostrará el botón 'Ir a OT')" (FA02 paso 2.3).

**Campos:** No aplica.

**Botones:** Ir a OT (el documento no especifica si incluye otros botones).

**Acciones:** No especificado en el documento.

## Captura de proyectos (sin mockup en el documento)

**Descripción:** Pantalla con la lista de los proyectos de la entidad en el estado "Proyecto Formulado", desde la cual el Viabilizador da clic en el proyecto sobre el cual va a emitir viabilidad (FB2 pasos 2b y 3).

**Campos:** No especificado en el documento.

**Botones:** No especificado en el documento.

**Acciones:** Selección del proyecto (FB2 paso 3).

## Comentarios de CU-PRE-26 "Opinión técnica" (pantalla de otro caso de uso)

**Descripción:** Pantalla a la que el sistema remite al Técnico URP al dar clic en "Ver comentarios OT", para diligenciar la columna "Justificación Institución" y oprimir el botón "Guardar Ajustes" (RN11). Pertenece a CU-PRE-26; no se describe en este documento.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|---|---|---|
| Mensaje emergente (confirmación) | "la solicitud del proyecto ha sido enviada al proceso de viabilidad exitosamente" | Tras la validación del FB1 paso 4, al solicitar viabilidad (FB1 paso 5). Sin Anexo/mockup propio asociado. |
| Notificación | Notificación al "Viabilizador" informando que le ha llegado la solicitud (texto exacto no especificado). | FB1 paso 6. |
| Notificación | Notificación al Viabilizador de que hay un proyecto en bandeja, con un link para ingresar directamente al formulario del Anexo A.1 (texto exacto no especificado). | FB2 paso 1. |
| Notificación | Notificación al "Técnico URP" de que el "Viabilizador" ha enviado comentarios a la información registrada, para su ajuste (texto exacto no especificado). | FA01 paso 1.5. |
| Mensaje emergente (confirmación) | "La viabilidad del proyecto ha sido emitida con éxito". + "Es necesario continuar con la gestión de Elegibilidad" | Al emitir viabilidad por primera vez (FA02 paso 2.3; Anexo A.2, ventana 1). |
| Mensaje emergente (confirmación) | "La viabilidad del proyecto ha sido emitida con éxito". + "Es necesario continuar con la gestión de la Opinión Técnica" | Al emitir viabilidad cuando el proyecto ya contaba con Elegibilidad (FA02 paso 2.3). Sin Anexo/mockup propio asociado ("Similar al Anexo A.2 pero mostrará el botón 'Ir a OT'"). |
| Mensaje emergente (confirmación) | "La viabilidad del proyecto ha sido emitida con éxito." (botón ACEPTAR) | Anexo A.2, ventana 2. El documento no describe en qué caso se muestra. |
| Notificación | Notificación al actor "Técnico URP" de que se ha emitido Viabilidad al proyecto (texto exacto no especificado). | FA02 paso 2.5. |
| Validación / error | "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad" | Al solicitar viabilidad con observaciones de OT sin responder en CU-PRE-26 (RN11). Sin Anexo/mockup propio asociado. |
| Información de apoyo | Criterios a revisar en cada campo (texto no especificado). | Al acercar el cursor al signo de pregunta de cada campo (RN08). |
| Indicador en pantalla | Número de veces que se ha devuelto el proyecto (presentación no especificada). | RN10. |

---

# Observaciones

1. **Fechas del documento:** los encabezados de página indican "Fecha: MAYO 2026", pero el Historial de Revisiones solo contiene una entrada de "AGO 2025" (versión 1.0, "Primera Versión"). No hay una entrada que corresponda a la fecha de los encabezados.

2. **Precondición genérica:** "Todos los casos de uso de Formulación, Evaluación y Programación" no identifica códigos ni un verbo que defina la condición. El FB1 paso 4 valida el rango CU-PRE-04 a CU-PRE-23 "según corresponda", sin que el documento establezca su relación con la precondición.

3. **Postcondiciones sin condición explícita:** se listan CU-PRE-25, CU-PRE-26 y CU-PRE-26.5 solo como referencias. CU-PRE-25 "Elegibilidad" aplica únicamente la primera vez (FA02 paso 2.6; RN03), lo que no se refleja en las postcondiciones. CU-PRE-26.5 "Priorización" no se menciona en ningún flujo ni regla de negocio del documento.

4. **Estado del proyecto al solicitar viabilidad:** el FB1 no indica cambio de estado al enviar la solicitud. El FB2 paso 2b indica que la pantalla "Captura de proyectos" lista los proyectos en estado "Proyecto Formulado", sin que el documento defina cuándo se asigna ese estado.

5. **Relación entre pasos del FB2:** el paso 4 ("O ingresa a la pestaña 'Gestión del Proyecto' en la sección 'Viabilidad'") se presenta como alternativa, sin indicar a qué paso u opción sustituye.

6. **Respuesta a comentarios del Viabilizador en CU-PRE-26:** el FA01 paso 1.6 indica que el Técnico URP responde los comentarios "en CU-PRE-26 'Opinión Técnica'", aunque los comentarios del FA01 provienen del Viabilizador. RN11, en cambio, restringe la respuesta en CU-PRE-26 al caso en que la solicitud responde a observaciones de la OT. El documento no aclara dónde responde el Técnico URP los comentarios del Viabilizador.

7. **Contradicción en RN01:** RN01 establece que el Viabilizador es el único que puede registrar/editar información en la sección, pero el Anexo B.1, el FB1 paso 1 y RN02 asignan al Técnico URP la carga de la nota de solicitud de OT, el documento de Preinversión y otros documentos en la misma pantalla.

8. **Doble nomenclatura del botón de paso al siguiente filtro:** "Pasar al siguiente filtro" (Anexo B.1) vs. "IR A ELEGIBILIDAD" (mockup del Anexo A.1 y del Anexo A.2) vs. "Ir a Elegibilidad" / "Ir a OT" (FA02 pasos 2.3, 2.4, 2.6, 2.7; RN02; RN07). El mockup del Anexo A.1 no muestra un botón "Ir a OT".

9. **Doble nomenclatura de campos:** 
   - "Nota de solicitud de OT" (Anexo B.1) vs. "Nota de solicitud OT" (mockup A.1).
   - "Otros documentos" (Anexo B.1) vs. "Otros documentos anexos" (mockup A.1) vs. "otros anexos del proyecto" (RN02) vs. "Anexos del proyecto" (FB1 paso 1).
   - "Descripción" (Anexo B.1 y Campos requeridos) vs. "Descripción del proyecto" (mockup A.1).
   - "Costo de operación" / "Costo de mantenimiento" (Anexo B.1 y Campos requeridos) vs. "Costos de Operación" / "Costos de Mantenimiento" (mockup A.1).
   - "Fuente de financiamiento" (Anexo B.1) vs. "Fuente de Financiamiento" (mockup A.1) vs. "Fuente de financiamiento estimada" (Campos requeridos).
   - "Observaciones Generales/Justificación de la Viabilidad" (Anexo B.1, mockup, Campos requeridos) vs. "observaciones generales" (FA01 paso 1.1).

10. **Botón "Guardar" no documentado en el Anexo B.1:** aparece en el mockup del Anexo A.1 y en FA01 paso 1.1, pero no en la tabla de formatos.

11. **Guardado previo a "Emitir viabilidad":** el Anexo B.1 indica que "Emitir viabilidad" se habilita una vez registrado **y guardado** el campo "Observaciones Generales/Justificación de la Viabilidad", pero el FA02 no incluye un paso de guardado entre el registro (2.1) y el clic en "Emitir Viabilidad" (2.2).

12. **Contradicción "Editable" en campos de carga:** "Nota de solicitud de OT", "Documento de Preinversión" y "Otros documentos" figuran con "Editable: No", pero su Detalle los describe como campos para que el Técnico URP cargue archivos. El documento no aclara si "Editable" se refiere a la perspectiva del Viabilizador o a otra condición.

13. **Tipos/formatos atípicos o inconsistentes en el Anexo B.1:**
    - "Ver comentarios OT" tiene Tipo/Formato "Botón de carga", igual que los campos de carga de archivos, aunque se describe como un botón que muestra comentarios.
    - "Nombre del proyecto" es el único campo con Tipo "Alfanumérico".
    - "Resumen del presupuesto" (Tipo "Moneda") y "Fuente de financiamiento" (Tipo "Texto") se describen como tablas completas de otros casos de uso.
    - "Indicadores de evaluación" usa el Tipo compuesto "Moneda/ Porcentaje" sin indicar qué indicador corresponde a cada tipo.

14. **Nota de solicitud de OT sin flujo asociado:** aparece en el mockup y en el Anexo B.1, pero ningún flujo ni regla de negocio describe cuándo ni por qué se carga, ni si es requisito para habilitar "Solicitar Viabilidad" (RN02 solo menciona el documento de Preinversión y otros anexos).

15. **Formatos de archivo:** el Anexo B.1 indica "PDF/A" para la nota de solicitud de OT y "PDF/ o .doc" para el documento de Preinversión (transcrito literalmente, incluida la barra). No se especifican formatos para "Otros documentos".

16. **Mensaje con botón "Ir a OT" sin mockup propio:** el FA02 paso 2.3 describe la variante para proyectos que ya contaban con Elegibilidad como "Similar al Anexo A.2 pero mostrará el botón 'Ir a OT'", sin Anexo dedicado, a diferencia de la variante de primera vez (Anexo A.2, ventana 1).

17. **Ventana del Anexo A.2 con botón "ACEPTAR" no descrita en los flujos:** la ventana 2 del Anexo A.2 muestra solo "La viabilidad del proyecto ha sido emitida con éxito." con botón "ACEPTAR", sin texto adicional. Ninguno de los dos casos descritos en el FA02 paso 2.3 corresponde a un mensaje sin texto adicional, y el documento no indica cuándo se muestra.

18. **Secuencia de pasos del FA02:** el paso 2.4 permite "Ir a Elegibilidad" desde el Anexo A.2 antes de que el paso 2.6 describa que el Sistema habilita el botón "Ir a Elegibilidad"; el paso 2.7 vuelve a indicar el clic. Además, el paso 2.4 indica que con "Salir" el sistema queda en la pantalla del Anexo A.1, mientras que el paso 2.5 indica que el Sistema deshabilita la pantalla del Anexo A.1.

19. **RN03 — actor que emite la Elegibilidad:** RN03 indica "después de que el viabilizador haya emitido por primera vez la Elegibilidad". El documento no define en otra sección qué actor emite la Elegibilidad (CU-PRE-25).

20. **RN10 sin soporte en pantalla:** la regla exige consultar en pantalla las observaciones de cada devolución y mostrar cuántas veces se ha devuelto el proyecto, pero ni el mockup del Anexo A.1 ni el Anexo B.1 incluyen elementos para ello.

21. **RN08 — información de apoyo:** el mockup muestra el ícono "?" en cada campo de la "FICHA DEL PROYECTO", pero el documento no especifica el texto de apoyo de cada campo.

22. **Mensajes sin Anexo/mockup propio:** el mensaje del FB1 paso 5 ("la solicitud del proyecto ha sido enviada al proceso de viabilidad exitosamente") y el mensaje de validación de RN11 ("Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad") no tienen Anexo asociado, a diferencia del mensaje de emisión de viabilidad (Anexo A.2).

23. **Link faltante en "Fuente de financiamiento":** a diferencia de los demás campos de consulta tomados de otros casos de uso (Productos, Población objetivo, Inversión estimada, Resumen del presupuesto, Costos, Indicadores), el Detalle de "Fuente de financiamiento" no menciona un link a la tabla de origen.

24. **"Unidades Ejecutoras" en RN01:** RN01 limita la visualización de los demás actores a "la información de las Unidades Ejecutoras", término que no aparece en otra parte del documento.

25. **Catálogos y tablas remitidos a otros documentos:** las tablas del Anexo A.4 y A.5 de CU-PRE-17, el campo "Indicadores" de CU-PRE-21 y las tablas de CU-PRE-07, CU-PRE-18 y CU-PRE-23 no están incluidas en este documento.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|----------|-------------|-----------|
| Proyecto | Proyecto sobre el cual se solicita y emite viabilidad; identificado por CUP y Nombre del proyecto. | Consulta (FB2 pasos 2–3); cambio de estado a "Observado" (FA01 paso 1.4) y a "Proyecto Viable" (FA02 paso 2.3); devolución cuantas veces sea necesario (RN10). |
| Documento de Preinversión | Documento cargado por el Técnico URP en formato PDF/ o .doc; campo obligatorio. | Carga (FB1 paso 1; RN02; Anexo B.1). |
| Nota de solicitud de OT | Nota de solicitud de Opinión Técnica cargada por el Técnico URP en formato PDF/A. | Carga (Anexo B.1). |
| Otros documentos | Otros documentos relacionados / anexos del proyecto cargados por el Técnico URP. | Carga, según corresponda (FB1 paso 1; RN02; Anexo B.1). |
| Comentarios del Viabilizador | Comentarios registrados por el Viabilizador para cada campo de la ficha del proyecto. | Registro y guardado (FA01 pasos 1.1–1.2); envío (FA01 paso 1.3); guardado en cada devolución y consulta (RN10). |
| Observaciones Generales/Justificación de la Viabilidad | Observaciones generales y justificación de la emisión de viabilidad o de la devolución. | Registro y guardado (FA01 paso 1.1; FA02 paso 2.1; Anexo B.1). |
| Devoluciones del proyecto | Cada devolución del proyecto con los comentarios del viabilizador. | Guardado de comentarios por devolución, consulta en pantalla y conteo de devoluciones (RN10). |
| Notificación | Avisos enviados por el Sistema al Viabilizador y al Técnico URP. | Envío (FB1 paso 6; FB2 paso 1; FA01 paso 1.5; FA02 paso 2.5). |
| Comentarios OT / Justificación Institución | Comentarios de la OT en CU-PRE-26 "Opinión técnica" y respuestas del Técnico URP en la columna "Justificación Institución". | Consulta mediante "Ver comentarios OT"; diligenciamiento y "Guardar Ajustes" en CU-PRE-26; validación de respuesta completa antes de solicitar viabilidad (RN11). |
| Información de CU-PRE-04 a CU-PRE-23 | Campos de las pantallas de los casos de uso CU-PRE-04 "Identificación" a CU-PRE-23 "Indicadores del Proyecto". | Validación de registro completo (FB1 paso 4); bloqueo para edición (RN04); habilitación para edición (FA01 paso 1.4; RN05); consulta de datos en la ficha (Anexo B.1). |

---

# Catálogos Detectados

| Catálogo | Valores conocidos |
|---|---|
| Estados del proyecto (valores mencionados en el documento) | Proyecto Formulado (FB2 paso 2b); Observado (FA01 paso 1.4); Proyecto Viable (FA02 paso 2.3). *El documento no define un catálogo completo de estados; solo se listan los valores mencionados.* |
| Filtros de gestión del proyecto | Viabilidad, Elegibilidad, Opinión Técnica (RN03). |
| Indicadores de evaluación (etiquetas mostradas en el mockup del Anexo A.1) | VAN, TIR, R B/C. *El catálogo completo y su cálculo se remiten a CU-PRE-21 "Flujo de Caja y cálculo de Indicadores", no incluido en este documento; estos valores no constituyen necesariamente el catálogo completo.* |
| Formatos de archivo de carga | Nota de solicitud de OT: PDF/A. Documento de Preinversión: PDF/ o .doc. Otros documentos: No especificado en el documento. |
| Fuente de financiamiento | *Se remite a la tabla del Anexo A.5 del CU-PRE-17 "Presupuesto de inversión", no incluido en este documento. El catálogo completo no está disponible en el documento analizado y el mockup no muestra valores de ejemplo.* |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|---|---|---|
| Carga del documento de Preinversión → habilitación del botón "Solicitar Viabilidad" | Técnico URP (FB1 paso 1) | Sistema (FB1 paso 2; RN02) |
| Clic en "Solicitar Viabilidad" → validación del registro CU-PRE-04 a CU-PRE-23 | Técnico URP (FB1 paso 3) | Sistema (FB1 paso 4) |
| Solicitud enviada → bloqueo de campos de CU-PRE-04 a CU-PRE-23 y deshabilitación de "Solicitar Viabilidad" | Sistema | CU-PRE-04 a CU-PRE-23 / Anexo A.1 (RN04) |
| Solicitud enviada → activación del botón "Enviar comentarios" | Sistema | Anexo A.1 (Anexo B.1) |
| Solicitud enviada → mensaje emergente de confirmación | Sistema | Técnico URP (FB1 paso 5) |
| Solicitud enviada → notificación de solicitud recibida (con link al Anexo A.1) | Sistema | Viabilizador (FB1 paso 6; FB2 paso 1) |
| Clic en "Enviar comentarios" → estado "Observado" y habilitación de CU-PRE-04 a CU-PRE-23 y de "Solicitar Viabilidad" | Viabilizador (FA01 paso 1.3) | Sistema / Proyecto (FA01 paso 1.4; RN05) |
| Comentarios enviados → notificación para ajuste | Sistema | Técnico URP (FA01 paso 1.5) |
| Guardado de "Observaciones Generales/Justificación de la Viabilidad" → habilitación de "Emitir viabilidad" | Viabilizador | Sistema (Anexo B.1) |
| Clic en "Emitir Viabilidad" → estado "Proyecto Viable" y mensaje del Anexo A.2 | Viabilizador (FA02 paso 2.2) | Sistema / Proyecto (FA02 paso 2.3) |
| Viabilidad emitida → notificación y deshabilitación del Anexo A.1 | Sistema | Técnico URP / Anexo A.1 (FA02 paso 2.5) |
| Viabilidad emitida (primera vez) → habilitación de "Ir a Elegibilidad" y de la edición de "Elegibilidad" | Sistema | CU-PRE-25 "Elegibilidad" (FA02 paso 2.6; RN02) |
| Clic en "Ir a Elegibilidad" | Viabilizador | CU-PRE-25 "Elegibilidad" (FA02 pasos 2.4, 2.7) |
| Clic en "Ir a OT" | Viabilizador | CU-PRE-26 "Opinión Técnica" (FA02 paso 2.7; RN03) |
| Clic en "Ver comentarios OT" | Técnico URP | Comentarios de CU-PRE-26 "Opinión técnica" (RN11) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---|---|---|
| No especificado en el documento. | — | El documento no menciona integraciones con sistemas externos. Las fuentes de datos de la pantalla son otros casos de uso del mismo sistema (ver "Dependencias"). |

---

# Datos Pendientes de Definir

1. **Prioridad y disparador** del caso de uso: no especificados.
2. **Comportamiento ante validación fallida del FB1 paso 4** (registro incompleto de CU-PRE-04 a CU-PRE-23): no se especifica el mensaje ni la acción del sistema.
3. **Dónde responde el Técnico URP los comentarios del Viabilizador:** FA01 paso 1.6 indica CU-PRE-26 "Opinión Técnica", mientras que RN11 vincula CU-PRE-26 solo a las observaciones de la OT.
4. **Alcance de RN01 frente a la carga de documentos por el Técnico URP** en la misma sección.
5. **Nombre del botón de paso al siguiente filtro:** "Pasar al siguiente filtro" (Anexo B.1) vs. "Ir a Elegibilidad" / "Ir a OT" (flujos, RN, mockup); y cómo se presenta "Ir a OT" en la pantalla del Anexo A.1, que no lo muestra.
6. **Uso de la ventana del Anexo A.2 con botón "ACEPTAR"**, no descrita en el FA02, y contenido completo de la variante con botón "Ir a OT" (sin mockup propio).
7. **Secuencia del FA02:** relación entre el botón "Ir a Elegibilidad" del Anexo A.2 (paso 2.4) y el habilitado en el paso 2.6; y comportamiento de "Salir" (paso 2.4) frente a la deshabilitación del Anexo A.1 (paso 2.5).
8. **Guardado previo a "Emitir Viabilidad":** el Anexo B.1 exige guardar "Observaciones Generales/Justificación de la Viabilidad", pero el FA02 no incluye ese paso.
9. **Significado de "Editable: No"** en los campos de carga ("Nota de solicitud de OT", "Documento de Preinversión", "Otros documentos"), cuyo Detalle indica que el Técnico URP los diligencia.
10. **Nota de solicitud de OT:** cuándo se carga y si es obligatoria para solicitar viabilidad.
11. **Presentación de las devoluciones (RN10):** elementos de pantalla para consultar las observaciones de cada devolución y el número de devoluciones, ausentes del mockup y del Anexo B.1.
12. **Textos de la información de apoyo (RN08)** de cada campo.
13. **Estado del proyecto tras solicitar viabilidad** y momento en que se asigna "Proyecto Formulado".
14. **Estructura de las tablas mostradas** en "Resumen del presupuesto" (Anexo A.4 de CU-PRE-17) y "Fuente de financiamiento" (Anexo A.5 de CU-PRE-17), tipificadas como "Moneda" y "Texto" respectivamente.
15. **Fecha vigente del documento:** encabezados "MAYO 2026" vs. Historial de Revisiones "AGO 2025".

---

# Notas de trazabilidad de actores

- Ambos actores principales usan el nombre exacto de `catalogo_actores`: **Técnico URP** y **Viabilizador**.
- A diferencia del resto de la serie, aquí **no hay un actor secundario nombrado individualmente** para la consulta de solo lectura (RN01 solo dice "todos los demás actores"); no se genera una historia de consulta con rol identificado.

# Contradicciones no resueltas del CU original (no se resuelven aquí)

1. **RN01 dice que el Viabilizador es "el único" que puede registrar/editar información en la sección**, pero el resto del documento describe claramente acciones de registro/edición del Técnico URP (carga de documentos, clic en "Solicitar Viabilidad", ajuste de campos tras comentarios). No se resuelve el alcance exacto de "la sección" a la que se refiere RN01; los escenarios de esta historia siguen las reglas de permisos más específicas (RN06 para Técnico URP, RN07 para Viabilizador), que sí delimitan con precisión qué botones usa cada actor.
2. **FB2 (paso 4)** usa "O ingresa a..." de forma ambigua respecto al orden de los pasos 2-3; se modela solo la vía directa por enlace (paso 2, opción a), sin asumir el orden exacto de la vía alternativa (opción b, "Captura de proyectos").
3. **FA01 (paso 1.6)** menciona que el Técnico URP responde comentarios "en CU-PRE-26 'Opinión Técnica'", lo cual parece una referencia cruzada incorrecta (este flujo trata sobre comentarios del Viabilizador en CU-PRE-24, no sobre comentarios de OT). No se transcribe esa referencia como parte del comportamiento esperado.
4. **Mensaje de "Emitir Viabilidad" cuando el proyecto ya contaba con Elegibilidad (Variante 2)**: el paso 2.3 del flujo dice que debe incluir el texto "Es necesario continuar con la gestión de la Opinión Técnica" y el botón "Ir a OT", pero esta variante no tiene mockup propio (el documento solo indica "Similar al Anexo A.2 pero mostrará el botón 'Ir a OT'"), y la ventana 2 del Anexo A.2 (texto genérico + botón "ACEPTAR") no corresponde a ninguno de los dos casos descritos en el FA02 (ver Observaciones 16 y 17). No se modela el texto/botón adicional en el escenario de esta variante, solo el cambio de estado y el mensaje base, dejando constancia de la discrepancia.
5. **RN10** (contador y consulta del historial de devoluciones) no tiene ningún soporte en el mockup ni en el Anexo B; no se genera un escenario para esta funcionalidad.
6. *(Resuelta en esta ronda — fuente externa)* El botón **"Guardar Ajustes"** (mencionado en RN11) pertenece a la pantalla de comentarios de CU-PRE-26 "Opinión técnica", a la que el sistema remite al Técnico URP desde "Ver comentarios OT" (RN11; ver Pantallas › "Comentarios de CU-PRE-26"). No forma parte de la pantalla del Anexo A.1, por lo que no corresponde generar un escenario para él en este CU.
7. El **tercer mensaje** ("¡Enviado! El proyecto fue enviado a Elegibilidad exitosamente"), identificado solo en el anexo Excel de la versión 1.1 y sin confirmación del paso exacto en que aparece, no se modela en ningún escenario.

---

# Historias de Usuario

## HU-01
**Como** Técnico URP
**Quiero** solicitar Viabilidad de un proyecto adjuntando el documento de Preinversión

- Escenarios cubiertos: camino feliz (cargar documento de Preinversión → habilitar "Solicitar Viabilidad" → clic → validación de completitud CU-PRE-04 a CU-PRE-23 → mensaje de confirmación → notificación al Viabilizador → bloqueo de campos CU-PRE-04 a CU-PRE-23, RN04), validación de campo obligatorio "Documento de Preinversión", validación de que se hayan respondido los comentarios de OT antes de solicitar Viabilidad respondiendo a una observación de OT (RN11, con su mensaje literal).

## HU-02
**Como** Viabilizador
**Quiero** revisar la ficha del proyecto y devolver comentarios al Técnico URP cuando se requieran ajustes

- Escenarios cubiertos: acceso a la ficha de Viabilidad mediante el enlace de la notificación (FB2, opción a), registrar y guardar comentarios por campo y observaciones generales, enviar comentarios (cambio de estado a "Observado", habilitación de campos CU-PRE-04 a CU-PRE-23, RN05, notificación al Técnico URP), el proyecto puede devolverse cuantas veces sea necesario (RN10, sin el contador/historial no soportado).

## HU-03
**Como** Viabilizador
**Quiero** emitir la Viabilidad de un proyecto

- Escenarios cubiertos: camino feliz (registrar "Observaciones Generales/Justificación de la Viabilidad" → habilitar "Emitir Viabilidad" → clic → cambio de estado a "Proyecto Viable" → mensaje de confirmación → notificación al Técnico URP → deshabilitación de la pantalla), variante del mensaje cuando es la primera vez (incluye "Es necesario continuar con la gestión de Elegibilidad" y habilita "Ir a Elegibilidad"), navegar a CU-PRE-25 "Elegibilidad" o quedarse en la pantalla al elegir "Salir", el proceso de Elegibilidad solo se requiere una vez — en gestiones posteriores se salta directo a Opinión Técnica (RN03), validación de campo obligatorio "Observaciones Generales/Justificación de la Viabilidad".
- Pendientes sin escenario: no se modela el texto/botón adicional de la Variante 2 del mensaje (ver contradicción 4), ni el tercer mensaje del anexo Excel (contradicción 7).
