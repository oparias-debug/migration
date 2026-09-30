---
id: CU-PRE-26.5
codigo: CU-PRE-26.5
nombre: "Priorización"
modulo: "Preinversión (derivado del prefijo del código 'CU-PRE' y de la mención 'Equipo Preinversión' del Historial de Revisiones; el documento no tiene un campo explícito llamado 'Módulo')"
submodulo: "Gestión (derivado de FB1 Actor Técnico SYMP paso 2 'selecciona en el menú Gestiones la opción Priorización'; el documento no tiene un campo explícito llamado 'Submódulo')"
version: "1.0 (portada, encabezados de página e Historial de Revisiones coinciden en 1.0; fecha AGOSTO 2026 / AGO 2026)"
fuente_pdf: "CU-PRE-26_5_Priorizacio_n.pdf (documento principal, 19 páginas); CU-PRE-26_5_Priorizacio_n__Anexos_.pdf (22 páginas; impresión a PDF del libro Excel 'CU-PRE-26.5 (ANEXO) Criterios de Priorización_Agosto-2.xlsx', según los metadatos del archivo)"
pagina_inicio: 1
pagina_fin: 19

actor_principal: "No especificado en el documento (el PDF lista dos actores —Técnico PRE y Técnico SYMP— sin designar uno como principal)"

actores_documento:
  # Los dos actores que el PDF lista en su sección "Actores", sin clasificarlos como principal ni secundarios.
  - "Técnico PRE"
  - "Técnico SYMP"

actores_secundarios:
  # Clasificación derivada: ninguno de estos figura en la sección "Actores" del PDF; se incluyen por su participación en flujos, reglas o anexos.
  - "Coordinador PRE (no figura en la sección 'Actores'; aparece en FB1 Técnico PRE pasos 6–8, RN03 y RN10)"
  - "Coordinador SYMP (no figura en la sección 'Actores'; aparece en FB1 Técnico SYMP pasos 6–8, RN03 y RN11)"
  - "Sistema (no figura en la sección 'Actores'; se incluye por ser quien ejecuta los pasos automáticos: mostrar formularios, notificar, calcular puntajes, bloquear pantallas)"
  - "Sub Jefe y Jefe de la DGI (no figuran en la sección 'Actores'; solo en RN02, con permiso de visualización)"
  - "Técnico ASYMP (no figura en la sección 'Actores'; variante de nomenclatura que aparece en los mockups A.3 y A.4 del archivo anexo, posiblemente referida al 'Técnico SYMP')"
  - "Técnico URP (no figura en la sección 'Actores'; solo aparece en el Anexo B.1, campo 'Calificación')"

prioridad: "No especificado en el documento."

estado: Analizado  # Derivado: valor de control asignado durante el análisis; no proviene del documento.

depende_de:
  - "CU-PRE-24 Viabilidad"
  - "CU-PRE-25 Elegibilidad"
  - "CU-PRE-26 Opinión Técnica al perfil"

casos_relacionados:
  - "Pantalla 'Captura de proyectos' (el documento no cita el código del caso de uso; en CU-PRE-26 esa pantalla corresponde a CU-PRE-03, dato externo a este documento y no verificado aquí)"
  - "CU-PRE-24 Viabilidad"
  - "CU-PRE-25 Elegibilidad"
  - "CU-PRE-26 Opinión Técnica"
  - "CU-PRO-01 Elaboración/Actualización del PRIPME"
  - "CU-PRO-08 Generación de escenarios para corto plazo"

roles:
  - "Técnico PRE"
  - "Técnico SYMP"
  - "Coordinador PRE (no figura en la sección 'Actores')"
  - "Coordinador SYMP (no figura en la sección 'Actores')"
  - "Sistema (no figura en la sección 'Actores'; ejecuta los pasos automáticos)"
  - "Sub Jefe DGI / Jefe DGI (no figuran en la sección 'Actores'; solo visualización, RN02)"

pantallas:
  - "A.1 – Selección y calificación de Criterios de Priorización ('PRIORIZACIÓN: MATRIZ MULTICRITERIO')"
  - "A.1.5 – Columna 'Selección' y botón 'Ver Escala de Calificación'"
  - "A.2 – Prioridad del proyecto (incluye ventana 'Rangos de interpretación')"
  - "A.3 – Mensaje de 'Calificado' Técnico PRE"
  - "A.4 – Mensaje de 'Calificado' Técnico SYMP"
  - "A.5 – Mensaje de 'Guardado'"

procesos:
  # Derivado: nombres y agrupación construidos por el análisis a partir de los flujos y reglas de negocio; no es una lista explícita del documento.
  - "Calificación de criterios 1, 2, 3 y 4 (Técnico PRE)"
  - "Calificación del criterio 5 (Técnico SYMP)"
  - "Revisión de la calificación (Coordinador PRE / Coordinador SYMP)"
  - "Cálculo de puntajes y Prioridad del proyecto (RN12, RN13, RN14)"
  - "Habilitación de ajustes de la calificación (RN10, RN11)"
  - "Actualización de la calificación a requerimiento de la DGI (RN06)"

servicios_externos:
  - "No especificado en el documento (el documento indica que el Sistema 'notifica'/'envía la notificación' sin especificar el medio)"

entidades:
  # Derivado: entidades inferidas por el análisis (ver sección "Entidades Detectadas"); el documento no incluye un modelo de entidades.
  - "Proyecto"
  - "Criterio de priorización"
  - "Subcriterio"
  - "Calificación de subcriterio"
  - "Puntaje por criterio"
  - "Prioridad del proyecto"
  - "Rango de interpretación"
  - "Escala de calificación"
  - "Notificación"

catalogos:
  # Derivado: agrupación del análisis (ver sección "Catálogos Detectados"); el contenido del "Anexo C – Catálogos Escala de Calificación" sí es literal del documento.
  - "Criterios y subcriterios de priorización con ponderaciones"
  - "Valores de calificación (N/A, 0, 1, 2, 3, 4, 5)"
  - "Rangos de interpretación (Puntaje / Categoría / Implicación)"
  - "Anexo C – Catálogos Escala de Calificación (17 subcriterios)"

palabras_clave:
  - "Priorización"
  - "Matriz multicriterio"
  - "Criterio"
  - "Subcriterio"
  - "Ponderación"
  - "Calificación"
  - "Prioridad del proyecto"
  - "Rangos de interpretación"
  - "Escala de calificación"
  - "PRIPME"
  - "Técnico SYMP"

ultima_actualizacion: "AGOSTO 2026 (encabezado de página); AGO 2026 (Historial de Revisiones)"

trazabilidad:
  informacion_general:
    pagina: 3
  historial_revisiones:
    pagina: 2
  flujo_principal:
    "FB1 Actor Técnico PRE":
      pagina: 3
    "FB1 Actor Técnico SYMP":
      pagina: 4
  flujos_alternos:
    "FA01 Guardar (Técnico PRE)":
      pagina: 4
    "FA01 Guardar (Técnico SYMP)":
      pagina: 5
  reglas_negocio:
    RN01:
      pagina: 5
    RN02:
      pagina: 5
    RN03:
      pagina: 5
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
    RN12:
      pagina: 6
    RN13:
      pagina: 6
    RN14:
      pagina: 6
      nota: "continúa en la página 7"
  anexos:
    A.1:
      nombre: "Selección y calificación de Criterios de Priorización"
      pagina: 8
    A.1.5:
      nombre: "Columna 'Selección' y botón 'Ver Escala de Calificación'"
      pagina: 9
    A.2:
      nombre: "Prioridad del proyecto"
      pagina: 10
    A.3:
      nombre: "Mensaje de 'Calificado' Técnico PRE"
      pagina: 11
    A.4:
      nombre: "Mensaje de 'Calificado' Técnico SYMP"
      pagina: 11
    A.5:
      nombre: "Mensaje de 'Guardado'"
      pagina: 11
    B.1:
      nombre: "Anexo B – Requerimientos Funcionales / B.1 – Formatos"
      pagina: 12
    C:
      nombre: "Anexo C – Catálogos Escala de Calificación (Subcriterios 1.1–1.3 p. 13; 1.4–2.2 p. 14; 2.3–2.4 p. 15; 3.1–3.3 p. 16; 4.1–4.3 p. 17; 5.1–5.3 p. 18–19)"
      pagina: 13
    "Archivo anexo (impresión del Excel)":
      nombre: "p.1: Matriz multicriterio; p.2–3: lista de calificación y ventana de escala (recortadas); p.4: Prioridad del proyecto; p.5–6: ventana Rangos de interpretación; p.7: A.3 y A.4; p.8: A.5; p.9–15: escalas por subcriterio (hoja con descripciones recortadas); p.16–22: 'CATÁLOGOS ESCALA DE EVALUACIÓN'"
      pagina: 1
  nota_derivados: "Los valores de `estado`, `procesos`, `entidades` y `catalogos` no provienen de campos explícitos del PDF: `estado` es un valor de control del análisis y los otros tres son agrupaciones construidas a partir de los flujos, reglas de negocio y anexos. `modulo`, `submodulo`, `actor_principal` y `actores_secundarios` llevan su propia anotación."
  nota_paginas: "Los números de página corresponden a las páginas físicas de cada PDF, identificadas mediante extracción de texto página por página. Ninguno de los dos PDF incluye numeración de página explícita en el pie, por lo que no fue posible contrastarlos con una numeración impresa."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | CU-PRE-26.5 Priorización |
| Código | CU-PRE-26.5 |
| Módulo | Preinversión *(valor derivado; el documento no tiene un campo explícito "Módulo" — ver nota sobre valores derivados más abajo)* |
| Fuente | CU-PRE-26_5_Priorizacio_n.pdf — "DIRECCIÓN GENERAL DE INVERSIÓN Y CRÉDITO PÚBLICO — SISTEMA DE INFORMACIÓN DE INVERSIÓN PÚBLICA — ESPECIFICACIÓN DE CASOS DE USO: 'CU-PRE-26.5 Priorización'"; archivo anexo CU-PRE-26_5_Priorizacio_n__Anexos_.pdf |
| Versión | 1.0 (Fecha en encabezado de página: AGOSTO 2026) |
| Identificación (texto literal del PDF) | CU-PRE-26.5 Priorización |

**Campos requeridos (según el PDF):**
- Criterio
- N°
- Subcriterio
- Ponderación criterio
- Ponderación subcriterio
- calificación
- Prioridad del proyecto
  - Criterio
  - Puntaje
  - Prioridad del proyecto
  - Categoría de priorización
  - Implicación para la programación

**Ruta de Acceso (según el PDF):**

> No especificado en el documento. El documento usa el patrón de tabla "Identificación" y no incluye una sección titulada "Ruta de Acceso". Los flujos indican dos vías de acceso: (a) "Luego de emitir Opinión Técnica el sistema lo lleva al formulario de priorización" (FB1 Técnico PRE, paso 1, Opción 1); (b) pantalla "Captura de proyectos" > clic en el proyecto > (para el Técnico SYMP) menú "Gestiones" > opción "Priorización".

> Nota sobre valores derivados: los valores `modulo` ("Preinversión") y `submodulo` ("Gestión") del Front Matter no provienen de un campo explícito del PDF. Se derivaron del prefijo del código "CU-PRE", del autor "Equipo Preinversión" del Historial de Revisiones y de la mención "menú 'Gestiones'" de FB1 Técnico SYMP paso 2.

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|---|---|---|---|---|
| AGO 2026 | 1.0 | Versión Inicial | Equipo Preinversión | |

> La versión (1.0) y la fecha (AGO 2026 / AGOSTO 2026) del Historial coinciden con la portada y los encabezados de página.

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección titulada "Objetivo". (Ver "Descripción".)

---

# Descripción

Este caso de uso permitirá al Técnico PRE calificar los criterios establecidos para la priorización de los proyectos (para los criterios 1, 2, 3 y 4), y al Técnico SYMP oportunamente calificar el criterio 5.

Este proceso se realiza en cada proyecto, que ha pasado por el filtro habilitante, consistente en la emisión de viabilidad (CU-PRE-24), elegibilidad (CU-PRE-25) y Opinión Técnica (CU-PRE-26), y será realizado por el Técnico PRE cada vez que se emita Opinión Técnica al proyecto, y por el Técnico SYMP una vez realizada la calificación de los primeros cuatro criterios por el Técnico PRE, cuando sea necesario.

> ⚠️ PENDIENTE DE RESOLUCIÓN: existe una contradicción interna sobre la obligatoriedad de la calificación del criterio 5. Esta Descripción indica que el Técnico SYMP califica "cuando sea necesario", mientras que RN07 ("El cálculo total de la priorización se completa cuando el Técnico SYMP realiza la calificación del criterio a su cargo") y el mensaje A.3 ("El Técnico SYMP debe calificar el Criterio 5") la tratan como obligatoria. Ver Observaciones, ítem 40. No implementar hasta resolver.

**Comentario de revisión incluido en el PDF** (anotación "Commented [1]" en el margen de la página 3, junto al Flujo Básico):

> 1. ¿Esto no sería como crear otro filtro a la aprobacion del proyecto?
> 2. ¿Cada vez que se emita OT a un proyecto, se debe hacer priorización?

> Nota: se transcribe como parte del contenido del documento porque plantea preguntas funcionales abiertas; el documento no incluye respuesta a ellas (ver Datos Pendientes de Definir).

# Actor Principal

> No especificado en el documento. La sección "Actores" del PDF lista dos actores sin designar uno como principal:

- Técnico PRE
- Técnico SYMP

---

# Actores Secundarios

> Nota sobre información derivada: el documento no clasifica actores como secundarios. Los dos actores de la sección "Actores" del PDF (Técnico PRE, que califica los criterios 1–4, y Técnico SYMP, que califica el criterio 5, según RN01) no se listan aquí porque el documento no los designa como principal ni como secundarios; figuran en "Actor Principal" y en `actores_documento` del Front Matter. Los actores de esta tabla se incluyen por decisión del análisis, según su participación en flujos, reglas o anexos.

| Actor | Fuente | Nota |
|---|---|---|
| Coordinador PRE | FB1 Técnico PRE pasos 6–7; RN03; RN10 | No figura en la sección "Actores". |
| Coordinador SYMP | FB1 Técnico SYMP pasos 6–7; FB1 Técnico PRE paso 8; RN03; RN11 | No figura en la sección "Actores". |
| Sistema | FB1 (ambos), FA01 (ambos), RN04, RN05, RN08, RN12–RN14 | No figura en la sección "Actores" del documento; se incluye por ser quien ejecuta los pasos automáticos. |
| Sub Jefe y Jefe (actores de la DGI) | RN02 | No figuran en la sección "Actores"; solo visualización. |
| Técnico ASYMP | Mockup A.3 (documento principal y archivo anexo) y título del mockup A.4 del archivo anexo | No figura en la sección "Actores"; el texto de los flujos usa "Técnico SYMP". No se puede confirmar si es el mismo rol. |
| Coordinador PRE/ASYMP | Rótulo de la casilla "Priorización Revisada Coordinador PRE/ASYMP" del mockup A.1 | Variante de nomenclatura; el Anexo B.1 usa "Coordinador PRE/SYMP". |
| Técnico URP | Anexo B.1, campo "Calificación" | No figura en la sección "Actores"; ni flujos ni reglas le asignan la calificación (ver Observaciones). |

---

# Disparador

> No especificado en el documento como sección independiente. FB1 Técnico PRE, paso 1, Opción 1 indica: "Luego de emitir Opinión Técnica el sistema lo lleva al formulario de priorización." La Descripción indica que el proceso "será realizado por el Técnico PRE cada vez que se emita Opinión Técnica al proyecto".

---

# Precondiciones

Filtro habilitante *(encabezado literal del PDF)*:

1. CU-PRE-24 Viabilidad
2. CU-PRE-25 Elegibilidad
3. CU-PRE-26 Opinión Técnica al perfil

> Nota de ambigüedad: el documento lista estas referencias bajo el encabezado "Filtro habilitante:" sin un verbo explícito (p. ej. "contar con", "haber ejecutado"). Se incorporan como precondiciones, pero la redacción exacta de la condición no está especificada. Además, el tercer ítem dice "Opinión Técnica al perfil", mientras que la Descripción indica que la priorización se realiza "cada vez que se emita Opinión Técnica al proyecto", sin restringirla a la etapa de perfil.

---

# Flujo Principal

> Nota: el documento contiene dos flujos titulados ambos "Flujo Básico 1 – FB1", uno para el actor Técnico PRE y otro para el actor Técnico SYMP. Se transcriben ambos en esta sección con su título literal.

## Flujo Básico 1 – FB1 Actor Técnico PRE

1. **Técnico PRE:** Opción 1: Luego de emitir Opinión Técnica el sistema lo lleva al formulario de priorización. (Continúa con el paso 4). Opción 2: Ingresa a la pantalla "Captura de proyectos".
2. **Técnico PRE:** Opción 2: Da clic en el proyecto a priorizar y entra al formulario del Anexo A.1.
3. **Sistema:** Opción 1: El sistema muestra el formulario "Priorización: Matriz multicriterio" (Ver Anexo A.1).
4. **Técnico PRE:** Para las dos opciones: Para los Criterios 1, 2, 3 y 4 evalúa cada subcriterio, seleccionando en la columna "Calificación" del Anexo A1 el puntaje según corresponda (N/A, 0, 1, 2, 3, 4, 5)
5. **Técnico PRE:** Da clic en el botón "Guardar" en cualquier momento durante el diligenciamiento y cuando termine da clic en el botón "Calificar Prioridad".
6. **Sistema:** Notifica al Coordinador PRE que es necesario revisar la calificación de la prioridad realizada por el Técnico PRE.
7. **Coordinador PRE:** Revisa la calificación y da clic en el botón "Priorización revisada Coordinador PRE".
8. **Sistema:** Muestra el mensaje del Anexo A.3 "Se han calificado los criterios 1, 2, 3 y 4. El Técnico SYMP debe calificar el Criterio 5". Muestra, bajo la pantalla del Anexo A1, la pantalla del Anexo A.2 "Prioridad del proyecto" con los resultados de la priorización realizada. El cálculo de dichos resultados se detalla en la RN12, RN13 y RN14. Envía al Técnico SYMP y al Coordinador SYMP la notificación siguiente: "Se ha realizado la calificación de los criterios 1, 2, 3 y 4 del proyecto [CUP], denominado [Nombre del proyecto]; para completar el puntaje de Priorización debe calificarse el criterio 5." La pantalla "Priorización: Matriz Multicriterio" queda deshabilitada para el Técnico PRE.

> ⚠️ PENDIENTE DE RESOLUCIÓN: en este paso el Sistema muestra la pantalla A.2 con "los resultados de la priorización realizada" cuando solo están calificados los criterios 1–4, pero el documento no indica cómo se trata el criterio 5 en ese momento (p. ej. si se muestra vacío, con puntaje 0 o excluido del total "Prioridad del proyecto" y de la categoría de interpretación). Ver Observaciones, ítem 41. No implementar hasta resolver.

> Nota de ambigüedad: el paso 1 indica que la Opción 1 "Continúa con el paso 4", pero el paso 3 (Sistema) está rotulado "Opción 1" y el paso 2 "Opción 2"; no queda claro si el paso 3 aplica a la Opción 1, a la Opción 2, o a ambas. El botón del paso 5 se denomina "Calificar Prioridad" (igual que en el mockup A.1), mientras que RN04, RN05 y el FB1 del Técnico SYMP lo denominan "Calificar Priorización". El paso 7 se refiere a un "botón 'Priorización revisada Coordinador PRE'", que en el mockup A.1 es una casilla rotulada "Priorización Revisada Coordinador PRE/ASYMP". El texto del Anexo A.3 citado en el paso 8 ("El Técnico SYMP…") difiere del mockup ("El Técnico ASYMP…").

## Flujo Básico 1 – FB1 Actor Técnico SYMP

1. **Técnico SYMP:** Ingresa a la pantalla "Captura de proyectos".
2. **Técnico SYMP:** Da clic en el proyecto a priorizar y selecciona en el menú "Gestiones" la opción "Priorización".
3. **Sistema:** El sistema muestra el formulario "Priorización: Matriz multicriterio" (Ver Anexo A.1).
4. **Técnico SYMP:** Para el Criterio 5 evalúa cada subcriterio, seleccionando en la columna "Calificación" del Anexo A1 el puntaje según corresponda (N/A, 0, 1, 2, 3, 4, 5).
5. **Técnico SYMP:** Da clic en el botón "Guardar" en cualquier momento durante el diligenciamiento y cuando termine da clic en el botón "Calificar Priorización".
6. **Sistema:** Notifica al Coordinador SYMP que es necesario revisar la calificación de la prioridad realizada por el Técnico SYMP.
7. **Coordinador SYMP:** Revisa la calificación y da clic en el botón "Priorización revisada Coordinador SYMP".
8. **Sistema:** Muestra el mensaje del Anexo A.4 "Se ha completado la calificación de la prioridad del proyecto". Muestra, bajo la pantalla del Anexo A1, la pantalla del Anexo A.2 "Prioridad del proyecto" con los resultados de la priorización realizada. El cálculo de dichos resultados se detalla en la RN12 y RN13, y RN14. Envía al Técnico PRE y al Coordinador PRE la notificación siguiente: "Se ha completado la calificación de la prioridad del proyecto [CUP] denominado [Nombre del proyecto]." La pantalla "Priorización: Matriz Multicriterio" queda deshabilitada para el Técnico SYMP.

---

# Flujos Alternos

> Nota: el documento contiene dos flujos titulados ambos "Flujo Alternativo 1 – FA01 Guardar", uno a continuación de cada Flujo Básico. Se transcriben ambos con su título literal, indicando entre paréntesis el actor para distinguirlos.

## FA01 — Guardar (a continuación del FB1 Actor Técnico PRE)

*(Título literal del PDF: "Flujo Alternativo 1 – FA01 Guardar")*

**Condición**

> No especificado en el documento.

**Flujo**

1.1. **Técnico PRE:** Da clic en el botón "Guardar".
1.2. **Sistema:** Muestra el mensaje emergente del Anexo A.5.
1.3. **Técnico PRE:** Da clic en "Aceptar" a mensaje emergente.
1.4. **Sistema:** Guarda la información registrada y se mantiene en la pantalla "Priorización".

**Resultado**

> No especificado en el documento.

> Nota de ambigüedad: el mensaje "¡Guardado! Sus datos han sido guardados correctamente" (Anexo A.5) se muestra en el paso 1.2, antes de que el Sistema guarde la información en el paso 1.4.

## FA01 — Guardar (a continuación del FB1 Actor Técnico SYMP)

*(Título literal del PDF: "Flujo Alternativo 1 – FA01 Guardar")*

**Condición**

> No especificado en el documento.

**Flujo**

1.1. **Técnico SYMP:** Da clic en el botón "Guardar".
1.2. **Sistema:** Muestra el mensaje emergente del Anexo A.5.
1.3. **Técnico SYMP:** Da clic en "Aceptar" a mensaje emergente.
1.4. **Sistema:** Guarda la información registrada y se mantiene en la pantalla "Priorización".

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no incluye una sección de Excepciones. A continuación se lista únicamente la situación excepcional que el propio documento describe en sus Reglas de Negocio:

| Código | Descripción | Consecuencia |
|---|---|---|
| RN04 (sin código de excepción propio) | El Técnico PRE (criterios 1–4) o el Técnico SYMP (criterio 5) no selecciona un puntaje para cada subcriterio, u omite alguno, y da clic en "Calificar Priorización". | El Sistema muestra el mensaje: "Error. Debe seleccionar un puntaje para cada subcriterio". |

---

# Postcondiciones

1. CU-PRO-01 Elaboración/Actualización del PRIPME
2. CU-PRO-08 Generación de escenarios para corto plazo

> Nota de ambigüedad: el documento lista referencias a otros casos de uso sin un verbo que describa la condición resultante.

---

# Reglas de Negocio

**RN01**
- Descripción: El Técnico PRE únicamente podrá calificar los criterios 1, 2, 3 y 4. El Técnico SYMP solo podrá calificar el criterio 5.
- Origen: Reglas del Negocio, p. 5.

**RN02**
- Descripción: Los actores de la DGI Sub Jefe y Jefe podrán únicamente visualizar todas las pantallas de este caso de uso.
- Origen: Reglas del Negocio, p. 5.

**RN03**
- Descripción: El botón "Guardar" será visible y estará habilitado para los actores Técnico PRE, Coordinador PRE, Técnico SYMP y Coordinador SYMP según corresponda.
- Origen: Reglas del Negocio, p. 5.

**RN04**
- Descripción: El Técnico PRE deberá seleccionar un puntaje para cada Subcriterio (de los criterios 1, 2, 3 y 4) en la columna "Calificación", en caso de no hacerlo u omitir alguno, al dar clic en el botón "Calificar Priorización", el Sistema mostrará el mensaje: "Error. Debe seleccionar un puntaje para cada subcriterio". Esta RN también aplicará para el Técnico SYMP con los subcriterios del criterio 5.
- Origen: Reglas del Negocio, p. 6.

**RN05**
- Descripción: El Sistema bloqueará la pantalla del Anexo A.1 para el Técnico PRE una vez que este dé clic en el botón "Calificar Priorización". Esta RN también aplica para el Técnico SYMP.
- Origen: Reglas del Negocio, p. 6.

**RN06**
- Descripción: El sistema debe permitir que el Técnico PRE y el Técnico SYMP puedan realizar una actualización de la calificación de los criterios a su cargo, a requerimiento y en los tiempos en que la DGI establezca.
- Origen: Reglas del Negocio, p. 6.

**RN07**
- Descripción: El cálculo total de la priorización se completa cuando el Técnico SYMP realiza la calificación del criterio a su cargo.
- Origen: Reglas del Negocio, p. 6.

> ⚠️ PENDIENTE DE RESOLUCIÓN: existe una contradicción interna con la Descripción, que indica que el Técnico SYMP califica el criterio 5 "cuando sea necesario". Ver Observaciones, ítem 40. No implementar hasta resolver.

**RN08**
- Descripción: Cuando se realice la selección del puntaje de cada Subcriterio en la columna "Calificación", el Sistema mostrará los valores N/A, 0, 1, 2, 3, 4 y 5; asimismo incluirá el botón "Ver Escala de Calificación" (Anexo A.1.5), en el cual, al dar clic se mostrará una ventana emergente en la que se detallan las escalas de Calificación para cada subcriterio. Dichas escalas se muestran en el Anexo C – Catálogos Escala de Calificación. Esta ventana será de consulta para todos los actores.
- Origen: Reglas del Negocio, p. 6.

**RN09**
- Descripción: El botón "Rangos de interpretación" mostrará una ventana emergente en la que se detallan los rangos de interpretación según el puntaje obtenido de priorización del proyecto (Ver en Anexo A2).
- Origen: Reglas del Negocio, p. 6.

**RN10**
- Descripción: El Coordinador PRE contará con un campo para habilitar ajustes en la Calificación de la Prioridad cuando sea requerido. Al darle clic habilitará los campos para calificación de criterios 1, 2, 3 y 4 al Técnico PRE encargado del caso.
- Origen: Reglas del Negocio, p. 6.

**RN11**
- Descripción: El Coordinador SYMP contará con un campo para habilitar ajustes en la Calificación de la Prioridad cuando sea requerido. Al darle clic habilitará los campos para calificación del criterio 5 al Técnico SYMP encargado del caso.
- Origen: Reglas del Negocio, p. 6.

**RN12**
- Descripción: El Sistema calculará los puntajes de cada criterio así: Multiplicará el valor de la "Calificación" otorgada al Subcriterio por la "Ponderación del Subcriterio" y por la "Ponderación del criterio", el resultado obtenido será multiplicado por 20. Esta fórmula será aplicada a todos los subcriterios. Para obtener el puntaje total o "Prioridad del proyecto" se sumarán los totales obtenidos de todos los subcriterios.
- Tabla de cálculo (transcripción en forma tabular del texto de RN12; el documento no presenta una tabla propia):

| Concepto | Explicación (texto de RN12) | Fórmula (expresión textual de RN12) |
|---|---|---|
| Puntaje del subcriterio | "Multiplicará el valor de la 'Calificación' otorgada al Subcriterio por la 'Ponderación del Subcriterio' y por la 'Ponderación del criterio', el resultado obtenido será multiplicado por 20." | Calificación × Ponderación del Subcriterio × Ponderación del criterio × 20 |
| Puntaje de cada criterio | "El Sistema calculará los puntajes de cada criterio así: …" | > No especificado en el documento de forma explícita (el texto describe el cálculo por subcriterio; el Anexo B.1 "Puntaje" remite a RN12 y RN13). |
| Prioridad del proyecto | "Para obtener el puntaje total o 'Prioridad del proyecto' se sumarán los totales obtenidos de todos los subcriterios." | Suma de los puntajes de todos los subcriterios |

- Origen: Reglas del Negocio, p. 6.

**RN13**
- Descripción: Si se selecciona el valor N/A para alguno de los subcriterios, a fin de totalizar el 100% en cada criterio, el Sistema deberá redistribuir automáticamente las ponderaciones entre los Subcriterios que tengan un valor numérico seleccionado.
- Origen: Reglas del Negocio, p. 6.

**RN14**
- Descripción: Si dentro de un criterio el puntaje seleccionado para todos sus subcriterios es N/A, el Sistema deberá redistribuir equitativamente el porcentaje del criterio entre los criterios restantes. Por ejemplo, si los tres subcriterios del criterio 3 (porcentaje de 15%) tienen como valor de calificación N/A, ese 15% se distribuiría uniformemente entre los 4 criterios restantes (3.75% adicional para cada uno). Si hay más de un criterio cuyos subcriterios en su totalidad reciban N/A como puntaje, el Sistema seguiría el mismo proceso descrito en esta RN.
- Origen: Reglas del Negocio, p. 6–7.

---

# Campos

> El Anexo B.1 distingue las columnas "Tipo", "Formato", "Editable" y "Detalle". No incluye columnas "Obligatorio" ni "Valor por defecto": la obligatoriedad se toma únicamente de lo que el propio "Detalle" indica; en ausencia de indicación se escribe "No especificado". La columna "Editable" se conserva en Observaciones.

## Sección "Priorización: Matriz Multicriterio" (Anexo B.1 — corresponde a la pantalla A.1)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Criterio | Campo que muestra los criterios definidos por la DGI para priorizar un proyecto. (Criterios en el Excel anexo). | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). Valores: ver catálogo "Criterios y subcriterios de priorización" (no son valores por defecto). |
| N° | Campo que muestra el número de subcriterio. | Número | Número | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Subcriterio | Campo que muestra los subcriterios definidos por la DGI para priorizar un proyecto. (Subcriterios en el Excel anexo). | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). Valores: ver catálogo "Criterios y subcriterios de priorización" (no son valores por defecto). |
| Ponderación criterio | Campo que muestra las ponderaciones asignadas por la DGI a cada criterio. Valor sujeto a actualización por parte de la DGI. | Porcentaje | Porcentaje | No especificado | No especificado | Editable: No (según Anexo B.1). Valores mostrados en el catálogo: 20%, 15%, 15%, 25%, 25% ("Valor sujeto a actualización por parte de la DGI"; no son valores por defecto). En el mockup A.1.5 la ponderación del Criterio 1 aparece como "20" (sin "%"). |
| Ponderación subcriterio | Campo que muestra las ponderaciones asignadas por la DGI a cada subcriterio. Valor sujeto a actualización por parte de la DGI. | Porcentaje | Porcentaje | No especificado | No especificado | Editable: No (según Anexo B.1). Valores: ver catálogo "Criterios y subcriterios de priorización" ("Valor sujeto a actualización por parte de la DGI"; no son valores por defecto). |
| Calificación | Campo que permite al Técnico PRE y al Técnico URP seleccionar un puntaje para cada subcriterio. Campo obligatorio. | Selección | Selección | Sí ("Campo obligatorio", según Detalle; ver también RN04) | No especificado | Editable: No (según Anexo B.1). Contradicción: el Detalle dice que el campo "permite … seleccionar un puntaje" (es decir, lo diligencia el usuario), pero "Editable" indica "No". Además menciona al "Técnico URP" en lugar del "Técnico SYMP" que usan la Descripción, los flujos y RN01. Valores: N/A, 0, 1, 2, 3, 4, 5 (RN08); en el mockup se muestran N/A, 0.00, 1.00, 2.00, 3.00, 4.00, 5.00. |
| Priorización revisada Coordinador PRE/SYMP | Campo que permitirá al Coordinador PRE o Coordinador SYMP, según corresponda, revisar la Calificación realizada por el Técnico PRE (Criterios 1, 2, 3 y 4) o por el Técnico SYMP (Criterio 5). | Selección | Selección | No especificado | No especificado | Editable: No (según Anexo B.1). Contradicción: el Detalle indica que el campo "permitirá al Coordinador … revisar", lo que implica interacción del usuario, mientras "Editable" indica "No". En el mockup es una casilla rotulada "Priorización Revisada Coordinador PRE/ASYMP"; en FB1 (pasos 7) se denomina "botón 'Priorización revisada Coordinador PRE'" / "…Coordinador SYMP". |
| Habilitar Calificación de Prioridad | Campo que permitirá al Coordinador PRE o Coordinador SYMP, según corresponda, habilitar la pantalla de Priorización: Matriz Multicriterio para el Técnico PRE o el Técnico SYMP, según corresponda. | Selección | Selección | No especificado | No especificado | Editable: No (según Anexo B.1). Contradicción: el Detalle describe un campo que el Coordinador acciona ("permitirá … habilitar"; RN10/RN11 "Al darle clic habilitará…"), mientras "Editable" indica "No". En el mockup es una casilla. |

> Botones visibles en el mockup A.1 que no están en la tabla del Anexo B.1: "CALIFICAR PRIORIDAD", "GUARDAR". Botón del mockup A.1.5 no incluido en el Anexo B.1: "Ver Escala de evaluación".

## Sección "Prioridad del proyecto" (Anexo B.1 — corresponde a la pantalla A.2)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Criterio | Campo que muestra los criterios definidos por la DGI para priorizar un proyecto. (Criterios en el Excel anexo). | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). En el mockup A.2 los nombres difieren de los de A.1 (p. ej. "Criterio 5 - Sostenibilidad Fiscal" vs. "CRITERIO 5 SOSTENIBILIDAD FINANCIERA DEL PROYECTO"). |
| Puntaje | Campo que muestra el puntaje de priorización obtenido por el proyecto según la Calificación realizada. El cálculo de cada puntaje para cada criterio es según la RN12 y RN13 | Número | Número | No especificado | No especificado | Editable: No (según Anexo B.1). Fórmula (RN12): Calificación del Subcriterio × Ponderación del Subcriterio × Ponderación del criterio × 20, aplicada a todos los subcriterios; con redistribución de ponderaciones por N/A (RN13). El Detalle no menciona RN14, que los flujos (paso 8) sí citan. |
| Prioridad del proyecto | Campo que muestra el puntaje total de la priorización. El Sistema lo calculará como la suma de los puntajes de cada criterio. | Número | Número | No especificado | No especificado | Editable: No (según Anexo B.1). RN12 lo define como la suma "de todos los subcriterios"; el Anexo B.1 como la suma "de los puntajes de cada criterio". |

## Sección "Rangos de interpretación" (Anexo B.1)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Categoría de priorización | Campo que mostrará la categoría de priorización del proyecto según el puntaje obtenido (Ver en Anexo A2 los rangos. Versión editable en Excel). | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). Valores según catálogo "Rangos de interpretación". |
| Implicación para la programación | Campo que mostrará la implicación del proyecto según el puntaje obtenido (Ver en Anexo A2 los rangos. Versión editable en Excel). | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). El texto mostrado en el mockup A.2 no coincide literalmente con el de la ventana "Rangos de interpretación" (ver Observaciones). |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|---|---|---|
| Calificación (subcriterios de los criterios 1, 2, 3 y 4 — Técnico PRE) | Debe seleccionarse un puntaje para cada subcriterio antes de dar clic en "Calificar Priorización" (RN04; "Campo obligatorio", Anexo B.1). | "Error. Debe seleccionar un puntaje para cada subcriterio" (RN04). Sin Anexo/mockup propio asociado. |
| Calificación (subcriterios del criterio 5 — Técnico SYMP) | Misma validación (RN04: "Esta RN también aplicará para el Técnico SYMP con los subcriterios del criterio 5"). | "Error. Debe seleccionar un puntaje para cada subcriterio" (RN04). Sin Anexo/mockup propio asociado. |
| Calificación | Solo admite los valores N/A, 0, 1, 2, 3, 4 y 5 (RN08). | > No especificado en el documento. |
| Calificación por rol | El Técnico PRE solo califica criterios 1–4; el Técnico SYMP solo el criterio 5 (RN01). | > No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|---|---|---|
| > No especificado en el documento (sin código). | Falta de puntaje en uno o más subcriterios al dar clic en "Calificar Priorización" (RN04). | El Sistema muestra "Error. Debe seleccionar un puntaje para cada subcriterio". La acción posterior no está especificada. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Técnico PRE | Calificar únicamente los criterios 1, 2, 3 y 4. | RN01; FB1 Técnico PRE paso 4 |
| Técnico SYMP | Calificar únicamente el criterio 5. | RN01; FB1 Técnico SYMP paso 4 |
| Técnico PRE, Coordinador PRE, Técnico SYMP, Coordinador SYMP | Ver y usar el botón "Guardar", "según corresponda". | RN03 |
| Técnico PRE / Técnico SYMP | Actualizar la calificación de los criterios a su cargo, a requerimiento y en los tiempos que la DGI establezca. | RN06 |
| Coordinador PRE | Revisar la calificación del Técnico PRE ("Priorización revisada Coordinador PRE"). | FB1 Técnico PRE paso 7; Anexo B.1 |
| Coordinador SYMP | Revisar la calificación del Técnico SYMP ("Priorización revisada Coordinador SYMP"). | FB1 Técnico SYMP paso 7; Anexo B.1 |
| Coordinador PRE | Habilitar ajustes de calificación de los criterios 1–4 al Técnico PRE encargado del caso. | RN10; Anexo B.1 |
| Coordinador SYMP | Habilitar ajustes de calificación del criterio 5 al Técnico SYMP encargado del caso. | RN11; Anexo B.1 |
| Sub Jefe y Jefe (DGI) | Únicamente visualizar todas las pantallas de este caso de uso. | RN02 |
| Todos los actores | Consultar la ventana de escalas de calificación ("Ver Escala de Calificación"). | RN08 |
| Técnico URP | Seleccionar un puntaje para cada subcriterio (según el Anexo B.1). | Anexo B.1, campo "Calificación" — en contradicción con RN01 y los flujos; ver Observaciones |

---

# Dependencias

**Otros casos de uso mencionados:**
- CU-PRE-24 Viabilidad (filtro habilitante)
- CU-PRE-25 Elegibilidad (filtro habilitante)
- CU-PRE-26 Opinión Técnica (filtro habilitante; "Opinión Técnica al perfil" en Precondiciones)
- Pantalla "Captura de proyectos" (FB1 ambos, paso 1; el documento no cita el código del caso de uso)
- CU-PRO-01 Elaboración/Actualización del PRIPME (postcondición)
- CU-PRO-08 Generación de escenarios para corto plazo (postcondición)

**Procesos relacionados:**
- "Filtro habilitante" (viabilidad, elegibilidad y Opinión Técnica)
- PRIPME, PAIP y PAP (mencionados en el catálogo "Rangos de interpretación")
- "Ruta de preinversión" (Subcriterio 4.1)

**Servicios externos:**
> No especificado en el documento. Los flujos indican que el Sistema "notifica" / "envía la notificación" sin especificar el medio (correo electrónico u otro).

---

# Pantallas

## A.1 – Selección y calificación de Criterios de Priorización

- **Nombre:** "PRIORIZACIÓN: MATRIZ MULTICRITERIO"
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Matriz en la que el Técnico PRE (criterios 1–4) y el Técnico SYMP (criterio 5) seleccionan la calificación de cada subcriterio.
- **Campos:** columnas CRITERIO, N°, SUBCRITERIO, PONDERACIÓN CRITERIO, PONDERACIÓN SUBCRITERIO, CALIFICACIÓN (lista desplegable); casillas "Priorización Revisada Coordinador PRE/ASYMP" y "Habilitar Calificación de Prioridad".
- **Botones:** CALIFICAR PRIORIDAD, GUARDAR.
- **Acciones:** FB1 (ambos), FA01 (ambos); RN01, RN03–RN05, RN08, RN10, RN11.

**Ejemplo de datos mostrados en el mockup (Anexo A.1 — documento principal p. 8 y archivo anexo p. 1):**

| CRITERIO | N° | SUBCRITERIO | PONDERACIÓN CRITERIO | PONDERACIÓN SUBCRITERIO | CALIFICACIÓN |
|---|---|---|---|---|---|
| CRITERIO 1 RENTABILIDAD SOCIAL | 1.1 | Tipo y solidez del indicador socioeconómico | 20% | 25% | 5.00 |
| | 1.2 | Resultado del indicador de evaluación | | 40% | 2.00 |
| | 1.3 | Sustento del cálculo de los beneficios | | 15% | 2.00 |
| | 1.4 | Vinculación entre problema-solución-beneficio | | 20% | 5.00 |
| CRITERIO 2 IMPACTO TERRITORIAL Y CIERRE DE BRECHAS | 2.1 | Rezago del territorio beneficiado | 15% | 30% | (vacío) |
| | 2.2 | Brecha que el proyecto contribuye a cerrar | | 30% | (vacío) |
| | 2.3 | Población en condición de vulnerabilidad que el proyecto beneficia | | 20% | (vacío) |
| | 2.4 | Cobertura territorial de la intervención | | 20% | (vacío) |
| CRITERIO 3 SOSTENIBILIDAD AMBIENTAL Y RESILIENCIA | 3.1 | Gestión del riesgo climático y de desastres naturales | 15% | 35% | (vacío) |
| | 3.2 | Impacto sobre ecosistemas y recursos naturales | | 35% | (vacío) |
| | 3.3 | Emisiones de Gases de Efecto Invernadero | | 30% | (vacío) |
| CRITERIO 4 MADUREZ TÉCNICA | 4.1 | Avance en estudios de preinversión | 25% | 45% | (vacío) |
| | 4.2 | Análisis de alternativas y justificación de la solución seleccionada | | 25% | (vacío) |
| | 4.3 | Definición de metas físicas e indicadores y programación financiera | | 30% | (vacío) |
| CRITERIO 5 SOSTENIBILIDAD FINANCIERA DEL PROYECTO | 5.1 | Compatibilidad del costo total y programación financiera de la ejecución | 25% | 40% | (vacío) |
| | 5.2 | Certidumbre y estructura de las fuentes de financiamiento | | 40% | (vacío) |
| | 5.3 | Costos de operación y mantenimiento | | 20% | (vacío) |

| Casilla | Estado en el mockup del documento principal | Valor en el archivo anexo (p. 1) |
|---|---|---|
| Priorización Revisada Coordinador PRE/ASYMP | Marcada | TRUE |
| Habilitar Calificación de Prioridad | Sin marcar | FALSE |

> En el archivo anexo (p. 1) el botón "GUARDAR" aparece dos veces en el texto de la página (uno en la parte superior y otro junto a "CALIFICAR PRIORIDAD").

## A.1.5 – Columna "Selección" y botón "Ver Escala de Calificación"

- **Nombre:** Columna "Selección" y botón "Ver Escala de Calificación"
- **Descripción:** *(Primera oración derivada, síntesis del análisis; la cita entre comillas es literal del documento.)* Lista desplegable de la columna CALIFICACIÓN y ventana emergente con la escala de calificación del subcriterio. "En el Anexo C se muestran los catálogos de escalas de calificación para cada Subcriterio."
- **Campos:** Lista con los valores N/A, 0.00, 1.00, 2.00, 3.00, 4.00, 5.00; ventana emergente con título del subcriterio, ponderación dentro del criterio, "Descripción y requerimiento de información" y la escala sugerida (N/A, 0–5).
- **Botones:** "Ver Escala de evaluación" (en la lista desplegable); "X" (cierre de la ventana emergente).
- **Acciones:** RN08 — ventana de consulta para todos los actores.

**Ejemplo de datos mostrados en el mockup (A.1.5 — documento principal p. 9):**

*Columna Selección (vista parcial de la matriz):*

| SUBCRITERIO | PONDERACIÓN CRITERIO | PONDERACIÓN SUBCRITERIO | CALIFICACIÓN |
|---|---|---|---|
| Tipo y solidez del indicador socioeconómico | 20 | 25% | 5.00 (lista abierta: N/A, 0.00, 1.00, 2.00, 3.00, 4.00, 5.00, botón "Ver Escala de evaluación") |
| Resultado del indicador de evaluación | | 40% | |
| Sustento del cálculo de los beneficios | | 15% | |
| Vinculación entre problema-solución-beneficio | | 20% | |
| Rezago del territorio beneficiado | 15% | 30% | |
| Brecha que el proyecto contribuye a cerrar | | 30% | |
| Población en condición de vulnerabilidad que el proyecto beneficia | | 20% | |
| Cobertura territorial de la intervención | | 20% | |
| Gestión del riesgo climático y de desastres naturales | 15% | 35% | |
| Impacto sobre ecosistemas y recursos naturales | | 35% | |

*Ventana "Ver Escala de Calificación" (ejemplo del Subcriterio 1.1):*

- Título: "Subcriterio 1.1 — Tipo y solidez del indicador de evaluación socioeconómica"
- "Ponderación dentro del criterio: 25% dentro del Criterio 1"
- Descripción y escala: idénticas al Subcriterio 1.1 del Anexo C (ver Catálogos Detectados).

> En el archivo anexo (p. 2–3) la ventana aparece recortada por los límites de impresión (p. ej. "Su…", "Descripción y reque…", "pción y requerimiento de información…", "leza del proyecto…"); se transcribe como recortada, sin completar.

## A.2 – Prioridad del proyecto

- **Nombre:** "PRIORIDAD DEL PROYECTO"
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Se muestra bajo la pantalla del Anexo A1 con los resultados de la priorización (FB1 ambos, paso 8).
- **Campos:** CRITERIO, PUNTAJE, Prioridad del proyecto; bloque "RANGOS DE INTERPRETACIÓN" con Categoría de priorización e Implicación para la programación.
- **Botones:** "Rangos de interpretación" (abre ventana emergente, RN09); "X" (cierre de la ventana).
- **Acciones:** Consulta; RN09, RN12–RN14.

> ⚠️ PENDIENTE DE RESOLUCIÓN: esta pantalla se muestra tras calificar solo los criterios 1–4 (FB1 Técnico PRE paso 8), pero el documento no indica cómo se trata el criterio 5 en ese momento. Ver Observaciones, ítem 41. No implementar hasta resolver.

**Ejemplo de datos mostrados en el mockup (A.2 — documento principal p. 10 y archivo anexo p. 4):**

| CRITERIO | PUNTAJE |
|---|---|
| Criterio 1 - Rentabilidad social | 20.00 |
| Criterio 2 - Impacto territorial y cierre de brechas | 12.00 |
| Criterio 3 - Sostenibilidad ambiental y resiliencia | 12.00 |
| Criterio 4: Madurez técnica | 20.00 |
| Criterio 5 - Sostenibilidad Fiscal | 10.00 |
| Prioridad del proyecto | 74.00 |

| RANGOS DE INTERPRETACIÓN | |
|---|---|
| Categoría de priorización | Priorizado condicional |
| Implicación para la programación | Puede incorporarse en n+1 al PRIPME sujeto a disponibilidad fiscal. |

*Ventana "Rangos de interpretación" (botón):* ver catálogo "Rangos de interpretación" en Catálogos Detectados.

> En el archivo anexo, la ventana "Rangos de interpretación" aparece en la p. 5 y la p. 6 contiene únicamente el símbolo "X" (botón de cierre) y el texto "XX" al final de la p. 5; se transcriben como aparecen.

## A.3 – Mensaje de "Calificado" Técnico PRE

- **Nombre:** Mensaje de "Calificado" Técnico PRE (archivo anexo: "Anexo A.3 - Calificado Técnico PRE")
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Mensaje mostrado en FB1 Técnico PRE paso 8.
- **Campos:** "¡Calificado!", ícono de verificación, texto "Se han calificado los criterios 1, 2, 3 y 4. El Técnico ASYMP debe calificar el criterio 5."
- **Botones:** ACEPTAR
- **Acciones:** > No especificado en el documento (acción del botón "ACEPTAR").

## A.4 – Mensaje de "Calificado" Técnico SYMP

- **Nombre:** Mensaje de "Calificado" Técnico SYMP (archivo anexo: "Anexo A.4 - Calificado Técnico ASYMP")
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Mensaje mostrado en FB1 Técnico SYMP paso 8.
- **Campos:** "¡Calificado!", ícono de verificación, texto "Se ha completado la calificación de la prioridad del proyecto".
- **Botones:** ACEPTAR
- **Acciones:** > No especificado en el documento (acción del botón "ACEPTAR").

## A.5 – Mensaje de "Guardado"

- **Nombre:** Mensaje de "Guardado" (archivo anexo: "Anexo A.5 - Guardar")
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Mensaje emergente mostrado en FA01 (ambos) paso 1.2.
- **Campos:** "¡Guardado!", ícono de verificación, texto "Sus datos han sido guardados correctamente".
- **Botones:** ACEPTAR
- **Acciones:** FA01 paso 1.3 → paso 1.4.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|---|---|---|
| Éxito (Anexo A.3 — texto del mockup) | "¡Calificado!" / "Se han calificado los criterios 1, 2, 3 y 4. El Técnico ASYMP debe calificar el criterio 5." | FB1 Técnico PRE paso 8. |
| Éxito (Anexo A.3 — texto citado en el flujo) | "Se han calificado los criterios 1, 2, 3 y 4. El Técnico SYMP debe calificar el Criterio 5" | FB1 Técnico PRE paso 8 (difiere del mockup). |
| Éxito (Anexo A.4) | "¡Calificado!" / "Se ha completado la calificación de la prioridad del proyecto" | FB1 Técnico SYMP paso 8. |
| Éxito (Anexo A.5) | "¡Guardado!" / "Sus datos han sido guardados correctamente" | FA01 (ambos) paso 1.2. |
| Error (RN04) | "Error. Debe seleccionar un puntaje para cada subcriterio" — sin Anexo/mockup propio asociado. | Al dar clic en "Calificar Priorización" con subcriterios sin puntaje. |
| Notificación al Coordinador PRE | Texto: > No especificado en el documento. (Solo se describe el propósito: "es necesario revisar la calificación de la prioridad realizada por el Técnico PRE"; sin Anexo propio asociado.) | FB1 Técnico PRE paso 6. |
| Notificación al Coordinador SYMP | Texto: > No especificado en el documento. (Propósito: "es necesario revisar la calificación de la prioridad realizada por el Técnico SYMP"; sin Anexo propio asociado.) | FB1 Técnico SYMP paso 6. |
| Notificación al Técnico SYMP y Coordinador SYMP | "Se ha realizado la calificación de los criterios 1, 2, 3 y 4 del proyecto [CUP], denominado [Nombre del proyecto]; para completar el puntaje de Priorización debe calificarse el criterio 5." — sin Anexo propio asociado. | FB1 Técnico PRE paso 8. |
| Notificación al Técnico PRE y Coordinador PRE | "Se ha completado la calificación de la prioridad del proyecto [CUP] denominado [Nombre del proyecto]." — sin Anexo propio asociado. | FB1 Técnico SYMP paso 8. |

---

# Observaciones

**Información ambigua / contradicciones internas**

1. **Preguntas abiertas del propio documento:** el PDF conserva un comentario de revisión ("Commented [1]") con dos preguntas sin respuesta: "¿Esto no sería como crear otro filtro a la aprobacion del proyecto?" y "¿Cada vez que se emita OT a un proyecto, se debe hacer priorización?".
2. **Flujos duplicados:** hay dos flujos titulados "Flujo Básico 1 – FB1" (Actor Técnico PRE y Actor Técnico SYMP) y dos titulados "Flujo Alternativo 1 – FA01 Guardar".
3. **Opciones del FB1 Técnico PRE:** el paso 1 indica que la Opción 1 "Continúa con el paso 4", pero el paso 3 (Sistema: "El sistema muestra el formulario…") está rotulado como "Opción 1", y el paso 2 como "Opción 2".
4. **Momento del bloqueo de la pantalla:** RN05 indica que la pantalla A.1 se bloquea "una vez que este dé clic en el botón 'Calificar Priorización'"; el paso 8 de ambos FB1 indica que queda deshabilitada después de que el Coordinador revisa la calificación.
5. **Orden de mensaje y guardado en FA01:** el mensaje "Sus datos han sido guardados correctamente" se muestra (paso 1.2) antes de que el Sistema guarde la información (paso 1.4).
6. **"Calificación" y el Técnico URP:** el Anexo B.1 indica que el campo "permite al Técnico PRE y al Técnico URP seleccionar un puntaje"; la Descripción, los flujos y RN01 asignan la calificación al Técnico PRE y al Técnico SYMP.
7. **Contradicción "Editable" (criterio de revisión campo por campo):** "Calificación", "Priorización revisada Coordinador PRE/SYMP" y "Habilitar Calificación de Prioridad" tienen "Editable: No", pero sus Detalles los describen como campos que el usuario acciona ("permite … seleccionar", "permitirá … revisar", "permitirá … habilitar"). No se puede determinar si "Editable" se refiere a otro concepto (p. ej. que los valores del catálogo no son modificables).
8. **Controles de revisión:** FB1 (paso 7) habla de "botón 'Priorización revisada Coordinador PRE'" / "…SYMP"; el mockup muestra una casilla "Priorización Revisada Coordinador PRE/ASYMP"; el Anexo B.1 la define como campo "Priorización revisada Coordinador PRE/SYMP" de tipo "Selección". RN10/RN11 hablan de "un campo para habilitar ajustes", que en el mockup es la casilla "Habilitar Calificación de Prioridad".
9. **Datos de ejemplo no coherentes entre A.1 y A.2:** el mockup A.1 solo muestra calificaciones para el Criterio 1 (5.00, 2.00, 2.00, 5.00) y deja vacíos los criterios 2–5, mientras el mockup A.2 muestra puntajes para los cinco criterios. Aplicando literalmente la fórmula de RN12 a las calificaciones y ponderaciones del Criterio 1 mostradas en A.1, el puntaje no coincide con el 20.00 mostrado en A.2. Los mockups parecen ser ejemplos independientes; el documento no incluye un ejemplo de cálculo trabajado.
   - *Cálculo (RN12, interpretando las ponderaciones como fracciones: 25% = 0.25):* (5 × 0.25 + 2 × 0.40 + 2 × 0.15 + 5 × 0.20) × 0.20 × 20 = 3.35 × 4 = **13.40**, frente a **20.00** en A.2.
   - *Máximo posible del Criterio 1:* 5 × (0.25 + 0.40 + 0.15 + 0.20) × 0.20 × 20 = **20.00**. Es decir, 20.00 solo se alcanzaría calificando con 5 todos los subcriterios del criterio (o, con redistribución por N/A según RN13, todos los subcriterios con valor numérico); con las calificaciones de A.1 (5, 2, 2, 5) no es alcanzable.
10. **Definición de "Prioridad del proyecto":** RN12 la define como la suma de los totales "de todos los subcriterios"; el Anexo B.1 como "la suma de los puntajes de cada criterio". El Anexo B.1 ("Puntaje") remite solo a RN12 y RN13, mientras los flujos remiten a RN12, RN13 y RN14.
11. **RN13 sin regla de redistribución:** indica que las ponderaciones se redistribuyen "automáticamente" entre los subcriterios con valor numérico, pero no especifica el método (proporcional o equitativo), a diferencia de RN14, que especifica "equitativamente" con un ejemplo.
12. **Valores de calificación:** RN08 y los flujos indican N/A, 0, 1, 2, 3, 4, 5; la lista del mockup muestra N/A, 0.00, 1.00, 2.00, 3.00, 4.00, 5.00.
13. **Precondición "Opinión Técnica al perfil":** restringe la precondición a la etapa de perfil, mientras la Descripción indica que la priorización se realiza "cada vez que se emita Opinión Técnica al proyecto".
14. **Medio de las notificaciones:** los flujos indican que el Sistema "Notifica" y "Envía … la notificación", sin especificar si es por correo electrónico u otro medio; las notificaciones no tienen Anexo propio (a diferencia de los mensajes A.3–A.5).

**Doble nomenclatura (botones, campos, roles y conceptos)**

15. Botón de calificación: "Calificar Prioridad" (FB1 Técnico PRE paso 5; mockup A.1 "CALIFICAR PRIORIDAD") vs. "Calificar Priorización" (FB1 Técnico SYMP paso 5; RN04; RN05).
16. Botón de escala: "Ver Escala de Calificación" (RN08; título del Anexo A.1.5) vs. "Ver Escala de evaluación" (mockup A.1.5). Catálogo: "Anexo C – Catálogos Escala de Calificación" (documento principal) vs. "CATÁLOGOS ESCALA DE EVALUACIÓN" (archivo anexo).
17. Columna de calificación: "CALIFICACIÓN" (mockups, flujos, Anexo B.1) vs. "Columna 'Selección'" (título del Anexo A.1.5).
18. Rol del criterio 5: "Técnico SYMP" (Actores, Descripción, flujos, RN) vs. "Técnico ASYMP" (texto del mockup A.3; título del mockup A.4 en el archivo anexo); "Coordinador SYMP" (flujos, RN, Anexo B.1) vs. "Coordinador PRE/ASYMP" (casilla del mockup A.1). El título del Anexo A.3/A.4 en el documento principal está resaltado ("Técnico PRE", "Técnico SYMP").
19. Nombres de criterios: A.1 "CRITERIO 5 SOSTENIBILIDAD FINANCIERA DEL PROYECTO" vs. A.2 "Criterio 5 - Sostenibilidad Fiscal"; A.2 usa "Criterio 4: Madurez técnica" (dos puntos) y guion en los demás.
20. Nombre del Subcriterio 1.1: "Tipo y solidez del indicador socioeconómico" (matriz A.1/A.1.5) vs. "Tipo y solidez del indicador de evaluación socioeconómica" (ventana A.1.5 y Anexo C).
21. Implicación para la programación: el mockup A.2 muestra "Puede incorporarse en n+1 al PRIPME sujeto a disponibilidad fiscal."; la ventana "Rangos de interpretación" muestra para la misma categoría "Puede incorporarse al año n+1 del PRIPME sujeto a disponibilidad fiscal. Orientar ajustes con PAP."
22. Mensaje A.3: flujo "El Técnico SYMP debe calificar el Criterio 5" vs. mockup "El Técnico ASYMP debe calificar el criterio 5."
23. Título A.5: "Mensaje de 'Guardado'" (documento principal) vs. "Anexo A.5 - Guardar" (archivo anexo).
24. Ponderación del Criterio 1: "20%" (mockup A.1) vs. "20" (mockup A.1.5).

**Diferencias entre el Anexo C del documento principal y las hojas del archivo anexo (impresión del Excel)**

25. El archivo anexo contiene dos versiones impresas de las escalas: una hoja con descripciones por subcriterio (p. 9–15) y una hoja titulada "CATÁLOGOS ESCALA DE EVALUACIÓN" (p. 16–22). En ambas, parte del texto de "Descripción y requerimiento de información" y de algunos títulos aparece recortado por los límites de impresión (p. ej. "pción y requerimiento…", "bcriterio 2.3…", "leza del proyecto…"); se toma como texto completo el del Anexo C del documento principal.
26. Diferencias de texto observadas en el archivo anexo respecto al Anexo C del documento principal: Subcriterio 1.3 "qué tan sutentado" (anexo) vs. "qué tan sustentado"; Subcriterio 3.2, escala 0, "negativos alto" (anexo) vs. "negativos altos"; Subcriterio 4.2, escala 5, "cuantitavos" (anexo) vs. "cuantitativos"; Subcriterio 4.3, escala 5, "distribucion" (anexo) vs. "distribución"; Subcriterio 5.1, escala 5, "cosistencia" (anexo) vs. "consistencia"; Subcriterio 5.3, descripción, "Evaluaía" (anexo) vs. "Evaluaría"; Subcriterio 5.3, escala 4, "identica" (anexo) vs. "identifica". No se corrige ninguna versión; se documentan ambas.
27. En los mockups A.3 y A.4 los títulos del archivo anexo ("Calificado Técnico PRE", "Calificado Técnico ASYMP") difieren de los del documento principal ("Mensaje de 'Calificado' Técnico PRE", "Mensaje de 'Calificado' Técnico SYMP").

**Información incompleta / referencias a otros documentos**

28. El Anexo B.1 remite a los "Criterios en el Excel anexo", "Subcriterios en el Excel anexo" y "Versión editable en Excel". El archivo recibido es una impresión a PDF de dicho libro Excel ("CU-PRE-26.5 (ANEXO) Criterios de Priorización_Agosto-2.xlsx"), no el archivo .xlsx; por tanto no pudo verificarse si el libro contiene hojas adicionales no impresas.
29. Subcriterio 4.1, escala 5: "Cuenta con Opinión Técnica para el último estudio de Preinversión establecido en la Ruta (Diseño o Perfil)", mientras las escalas 2–4 citan la ruta "Perfil-Prefactibilidad-Factibilidad-Diseño"; no se aclara en qué caso el último estudio es "Perfil".
30. El documento no indica el código del caso de uso de la pantalla "Captura de proyectos".
31. Siglas usadas sin definición en el documento: SYMP/ASYMP, PRIPME, PAIP, PAP, IE, GEI, VAN, TIR, TRI, VAC, CAE, O&M.

**Consistencia con documentos relacionados de la misma serie analizados en esta conversación (CU-PRE-26 Opinión Técnica)**

32. **Mecanismo de acceso desde la OT:** CU-PRE-26.5 (FB1 Técnico PRE, Opción 1) indica que "Luego de emitir Opinión Técnica el sistema lo lleva al formulario de priorización"; CU-PRE-26 (Anexo A.5 y RN17) describe un mensaje con botón "IR A PRIORIZACIÓN" que "permite que pueda continuar con la emisión de la priorización", es decir, una acción del usuario.
33. **Menú:** CU-PRE-26.5 menciona el menú "Gestiones" con la opción "Priorización"; el menú del Anexo A de CU-PRE-26 se rotula "GESTIÓN" (con el botón "PRIORIZACIÓN") y su FB habla de la pestaña "Gestión de Proyectos".
34. **Postcondiciones:** CU-PRE-26 lista como postcondiciones CU-PRE-26.5, CU-PRO-01, CU-PRO-08 y CU-PRO-17; CU-PRE-26.5 lista CU-PRO-01 y CU-PRO-08, pero no CU-PRO-17. Además CU-PRE-26 escribe "Generación de Escenarios para Corto Plazo" y CU-PRE-26.5 "Generación de escenarios para corto plazo".
35. **Roles:** los roles "Técnico SYMP", "Coordinador SYMP" y "Técnico ASYMP" no aparecen en CU-PRE-26. CU-PRE-26 usa "Técnico URP" como rol de la Institución; en CU-PRE-26.5 el "Técnico URP" solo aparece en el Anexo B.1 ("Calificación"), posiblemente en lugar de "Técnico SYMP".
36. **Precondición de OT:** CU-PRE-26.5 exige "CU-PRE-26 Opinión Técnica al perfil"; CU-PRE-26 no restringe la OT a la etapa de perfil (sus ejemplos incluyen etapas futuras Diseño, Factibilidad y Ejecución).
37. **Ruta de preinversión:** el Subcriterio 4.1 cita la ruta "Perfil-Prefactibilidad-Factibilidad-Diseño"; CU-PRE-26 (Anexo B.1, "Etapa futura") cita la "Ruta de Preinversión" con ejemplos Perfil → Prefactibilidad y Diseño → Ejecución. No se detecta contradicción directa, pero ninguno de los dos documentos incluye la ruta completa.
38. **Patrón de mensaje citado vs. mockup:** en ambos documentos el texto del mensaje citado en el flujo difiere del texto del mockup (CU-PRE-26: A.5 "…Opinión Técnica Favorable…" vs. "…Opinión Técnica…"; CU-PRE-26.5: A.3 "Técnico SYMP" vs. "Técnico ASYMP").
39. **Patrón de control "botón" vs. casilla:** en ambos documentos un control de aprobación del Coordinador se describe como "botón" en el flujo y aparece como casilla en el mockup (CU-PRE-26: "Visto bueno OT"; CU-PRE-26.5: "Priorización revisada Coordinador PRE/SYMP").

**Contradicciones y vacíos sobre el criterio 5**

40. **Obligatoriedad de la calificación del criterio 5:** la Descripción indica que el Técnico SYMP califica el criterio 5 "cuando sea necesario"; RN07 establece que "El cálculo total de la priorización se completa cuando el Técnico SYMP realiza la calificación del criterio a su cargo", y el mensaje A.3 indica "El Técnico SYMP debe calificar el Criterio 5" (mockup: "El Técnico ASYMP debe calificar el criterio 5."), lo que la trata como obligatoria. No se puede determinar si existen casos en que el criterio 5 no se califica ni, en ese caso, cómo se completa la priorización.
41. **Resultados de A.2 con el criterio 5 sin calificar:** FB1 Técnico PRE paso 8 muestra la pantalla A.2 "con los resultados de la priorización realizada" tras calificar solo los criterios 1–4. El documento no indica cómo se trata el criterio 5 en ese momento: si se muestra vacío, con puntaje 0, o si se excluye (y cómo afecta entonces a "Prioridad del proyecto" y a la categoría de los Rangos de interpretación). RN14 solo prevé redistribución cuando todos los subcriterios de un criterio son N/A, no cuando están pendientes de calificar.

---

# Entidades Detectadas

> Nota sobre información derivada: el documento no incluye un modelo ni una lista de entidades. Esta sección fue construida por el análisis; los nombres de entidad, sus descripciones y las operaciones son síntesis a partir de los flujos, reglas de negocio y anexos citados en cada fila, no texto literal del documento.

| Entidad | Descripción | Operación |
|---|---|---|
| Proyecto | Proyecto que ha pasado el filtro habilitante (viabilidad, elegibilidad y OT); identificado por [CUP] y [Nombre del proyecto]. | Selección desde "Captura de proyectos" (FB1 ambos, pasos 1–2). |
| Criterio de priorización | Cinco criterios definidos por la DGI con su ponderación. | Consulta (Anexo B.1); actualización de ponderación "por parte de la DGI" (Anexo B.1); redistribución de su porcentaje si todos sus subcriterios son N/A (RN14). |
| Subcriterio | Subcriterios de cada criterio con su ponderación. | Consulta (Anexo B.1); redistribución automática de ponderaciones por N/A (RN13). |
| Calificación de subcriterio | Puntaje seleccionado (N/A, 0–5) por el Técnico PRE o SYMP. | Registro (FB1 ambos, paso 4); guardado (FA01); validación de completitud (RN04); bloqueo (RN05); habilitación de ajustes (RN10, RN11); actualización (RN06). |
| Puntaje por criterio | Resultado del cálculo por criterio. | Cálculo automático (RN12, RN13, RN14); consulta (Anexo A.2). |
| Prioridad del proyecto | Puntaje total de la priorización. | Cálculo automático (RN12; Anexo B.1); se completa con la calificación del Técnico SYMP (RN07). |
| Rango de interpretación | Categoría e implicación según el puntaje. | Consulta (RN09; Anexo A.2). |
| Escala de calificación | Descripción y escala sugerida por subcriterio (Anexo C). | Consulta para todos los actores (RN08). |
| Revisión del Coordinador | Marca "Priorización revisada Coordinador PRE/SYMP". | Registro por el Coordinador (FB1 ambos, paso 7; Anexo B.1). |
| Notificación | Avisos del Sistema a Coordinadores y Técnicos. | Envío (FB1 ambos, pasos 6 y 8). |

---

# Catálogos Detectados

> Nota sobre información derivada: el documento no incluye una sección de catálogos con esta organización. Los encabezados y la agrupación son del análisis. El contenido de las tablas de criterios/subcriterios, rangos de interpretación y del subapartado "Anexo C – Catálogos Escala de Calificación" sí es transcripción literal de las fuentes indicadas; las notas bajo cada tabla son del análisis.

## Criterios y subcriterios de priorización (Anexo A.1 / Anexo C)

| Criterio | Ponderación criterio | N° | Subcriterio (nombre en la matriz A.1) | Ponderación subcriterio |
|---|---|---|---|---|
| CRITERIO 1 RENTABILIDAD SOCIAL | 20% | 1.1 | Tipo y solidez del indicador socioeconómico | 25% |
| | | 1.2 | Resultado del indicador de evaluación | 40% |
| | | 1.3 | Sustento del cálculo de los beneficios | 15% |
| | | 1.4 | Vinculación entre problema-solución-beneficio | 20% |
| CRITERIO 2 IMPACTO TERRITORIAL Y CIERRE DE BRECHAS | 15% | 2.1 | Rezago del territorio beneficiado | 30% |
| | | 2.2 | Brecha que el proyecto contribuye a cerrar | 30% |
| | | 2.3 | Población en condición de vulnerabilidad que el proyecto beneficia | 20% |
| | | 2.4 | Cobertura territorial de la intervención | 20% |
| CRITERIO 3 SOSTENIBILIDAD AMBIENTAL Y RESILIENCIA | 15% | 3.1 | Gestión del riesgo climático y de desastres naturales | 35% |
| | | 3.2 | Impacto sobre ecosistemas y recursos naturales | 35% |
| | | 3.3 | Emisiones de Gases de Efecto Invernadero | 30% |
| CRITERIO 4 MADUREZ TÉCNICA | 25% | 4.1 | Avance en estudios de preinversión | 45% |
| | | 4.2 | Análisis de alternativas y justificación de la solución seleccionada | 25% |
| | | 4.3 | Definición de metas físicas e indicadores y programación financiera | 30% |
| CRITERIO 5 SOSTENIBILIDAD FINANCIERA DEL PROYECTO | 25% | 5.1 | Compatibilidad del costo total y programación financiera de la ejecución | 40% |
| | | 5.2 | Certidumbre y estructura de las fuentes de financiamiento | 40% |
| | | 5.3 | Costos de operación y mantenimiento | 20% |

> Las ponderaciones de subcriterio del Anexo C ("Ponderación dentro del criterio") coinciden con las de la matriz A.1 para los 17 subcriterios. Las ponderaciones son "Valor sujeto a actualización por parte de la DGI" (Anexo B.1).

## Valores de calificación

| Catálogo | Valores conocidos |
|---|---|
| Calificación (RN08; flujos) | N/A, 0, 1, 2, 3, 4, 5 |
| Calificación (lista del mockup A.1.5) | N/A, 0.00, 1.00, 2.00, 3.00, 4.00, 5.00 |

## Rangos de interpretación (ventana del botón "Rangos de interpretación", Anexo A.2)

| PUNTAJE | CATEGORÍA | IMPLICACIÓN |
|---|---|---|
| 85-100 | Priorizado para programación | Incorporación prioritaria al año n+1 del PRIPME. Elegible para PAIP con financiamiento. |
| 70-84 | Priorizado condicional | Puede incorporarse al año n+1 del PRIPME sujeto a disponibilidad fiscal. Orientar ajustes con PAP. |
| 50-69 | Elegible para fortalecimiento | No debe avanzar al PAIP en estado actual. Maduración mediante PAP. |
| 0-49 | No priorizable en estado actual | Se recomienda reformulación o postergación. |

> El documento indica "Versión editable en Excel" (Anexo B.1). Los rangos son enteros; el documento no especifica cómo se clasifican puntajes decimales entre rangos (p. ej. 84.50).

## Anexo C – Catálogos Escala de Calificación (texto completo del documento principal)

### Subcriterio 1.1 — Tipo y solidez del indicador de evaluación socioeconómica
Ponderación dentro del criterio: 25% dentro del Criterio 1

Descripción y requerimiento de información: Evaluaría qué indicador de rentabilidad social o costo-eficiencia se utilizó y qué tan rigurosa es su construcción. Según la naturaleza del proyecto, la IE registraría los indicadores calculados (VAN social, TIR, TRI para proyectos con beneficios cuantificables; VAC, CAE o relación costo-eficiencia para proyectos donde los beneficios no son cuantificables monetariamente), la metodología utilizada y los supuestos principales. Cuando el proyecto permita cuantificar beneficios se esperaría un indicador de costo-beneficio; cuando no, se aceptaría costo-eficiencia como alternativa válida. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica, el proyecto es de fortalecimiento institucional puro sin posibilidad de evaluación económica formal. |
| 0 | No presenta ningún indicador de evaluación económica, ni justificación de por qué no aplica. |
| 1 | Presenta valoración cualitativa de beneficios sin respaldo cuantitativo ni indicador formal. |
| 2 | Presenta indicador de costo-beneficio o costo-eficiencia, pero con metodología no documentada o supuestos no justificados. |
| 3 | Presenta indicador adecuado al tipo de proyecto con metodología documentada y supuestos explícitos. |
| 4 | Presenta indicador adecuado con metodología documentada, supuestos justificados y fuentes identificadas. |
| 5 | Presenta indicador principal con metodología documentada, supuestos justificados, fuentes identificadas y análisis de sensibilidad. |

### Subcriterio 1.2 — Resultado del indicador de evaluación
Ponderación dentro del criterio: 40% dentro del Criterio 1

Descripción y requerimiento de información: Evaluaría el resultado obtenido del indicador en relación con los parámetros de rentabilidad o eficiencia aplicables. Para indicadores de costo-beneficio, la IE contrastaría el resultado con la tasa social de descuento. Para indicadores de costo-eficiencia, contrastaría el costo por unidad de beneficio con referencias sectoriales o proyectos comparables. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto. |
| 0 | El indicador muestra que el proyecto no es rentable ni eficiente, o el resultado no es concluyente. |
| 1 | El resultado es positivo pero marginal: VAN cercano a cero, TIR cercana a la tasa de descuento, o costo-eficiencia por encima de referencias sectoriales. |
| 2 | El resultado supera el umbral mínimo: VAN positivo moderado, TIR sobre la tasa de descuento, o costo-eficiencia dentro del rango sectorial. |
| 3 | El resultado muestra rentabilidad adecuada o eficiencia, claramente favorable respecto al umbral o referencia sectorial. |
| 4 | El resultado muestra rentabilidad o eficiencia sólida con margen significativo sobre el umbral o referencia. |
| 5 | El resultado muestra rentabilidad o eficiencia alta y robusta, confirmada mediante análisis de sensibilidad |

### Subcriterio 1.3 — Sustento del cálculo de los beneficios
Ponderación dentro del criterio: 15% dentro del Criterio 1

Descripción y requerimiento de información: Evaluaría qué tan sustentado está la identificación, cuantificación y valoración de los beneficios que el proyecto genera (no quiénes son los beneficiarios sino cuánto aporta el proyecto al bienestar, la productividad o la calidad del servicio). La IE registraría la estimación del valor de los beneficios generados y su justificación metodológica. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto. |
| 0 | No estima ni describe los beneficios esperados. |
| 1 | Describe beneficios de forma cualitativa sin estimación del valor generado. |
| 2 | Presenta estimación del valor de beneficios, pero sin metodología documentada ni justificación. |
| 3 | Presenta estimación del valor de beneficios con metodología documentada y justificación básica. |
| 4 | Presenta estimación del valor de beneficios con metodología documentada, justificación sólida y comparación con la situación sin proyecto. |
| 5 | Presenta estimación del valor de beneficios con metodología documentada, comparación con situación sin proyecto y cuantificación de beneficios directos e indirectos. |

### Subcriterio 1.4 — Vinculación entre problema-solución-beneficio
Ponderación dentro del criterio: 20% dentro del Criterio 1

Descripción y requerimiento de información: Evaluaría qué tan explícita y coherente es la cadena lógica entre el problema identificado, la solución propuesta y los beneficios económicos o sociales esperados. La IE registraría esta cadena en el perfil del proyecto (árbol de problemas y de objetivos). Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto. |
| 0 | No existe vínculo explícito entre el problema, la solución y los beneficios. |
| 1 | El vínculo existe, pero es implícito o muy general. |
| 2 | El vínculo es explícito, pero con inconsistencias entre el problema identificado y la solución propuesta. |
| 3 | El vínculo es explícito y coherente entre problema, solución y beneficios esperados. |
| 4 | El vínculo es explícito, coherente y respaldado por datos del diagnóstico. |
| 5 | El vínculo es explícito, coherente, respaldado por datos y verificable mediante indicadores con línea base. |

### Subcriterio 2.1 — Rezago del territorio beneficiado
Ponderación dentro del criterio: 30% dentro del Criterio 2

Descripción y requerimiento de información: Evaluaría si el proyecto se ubica en territorios con mayores niveles de rezago en términos de desarrollo humano, pobreza o acceso a servicios. La IE registraría la localización específica y los indicadores de contexto territorial disponibles — Encuesta de Hogares de Propósitos Múltiples u otros indicadores sectoriales aplicables—. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica — el proyecto tiene cobertura nacional sin focalización territorial específica. |
| 0 | El proyecto no se ubica en territorio con rezago identificado o no presenta información territorial. |
| 1 | El proyecto menciona el territorio, pero sin evidencia de rezago relativo. |
| 2 | El proyecto se ubica en territorio con algún indicador de rezago, pero sin documentación de respaldo. |
| 3 | El proyecto se ubica en territorio con rezago documentado en al menos un indicador de referencia nacional. |
| 4 | El proyecto se ubica en territorio con rezago documentado en múltiples indicadores de referencia. |
| 5 | El proyecto se ubica en territorio con rezago crítico documentado en múltiples indicadores, identificado en diagnósticos sectoriales o nacionales como área prioritaria de intervención. |

### Subcriterio 2.2 — Brecha que el proyecto contribuye a cerrar
Ponderación dentro del criterio: 30% dentro del Criterio 2

Descripción y requerimiento de información: Evaluaría la brecha (déficit) de infraestructura o servicios que el proyecto atiende, sus fuentes de respaldo y la contribución del proyecto al cierre de la brecha. La IE registraría la brecha identificada, su magnitud y la fuente de información que la sustenta (diagnósticos sectoriales, censos, encuestas de hogares u otras fuentes oficiales). Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto. |
| 0 | No identifica ni documenta ninguna brecha de infraestructura o servicios. |
| 1 | Menciona una brecha de forma general sin cuantificación ni fuente de respaldo. |
| 2 | Identifica una brecha con cuantificación básica, pero sin fuente de respaldo documentada. |
| 3 | Identifica y cuantifica la brecha con fuente de respaldo documentada. |
| 4 | Identifica y cuantifica la brecha con fuente documentada y la contrasta con promedios nacionales o sectoriales. |
| 5 | Identifica y cuantifica la brecha con fuente documentada, la contrasta con promedios nacionales o sectoriales y estima el porcentaje de cierre que el proyecto representaría. |

### Subcriterio 2.3 — Población en condición de vulnerabilidad que el proyecto beneficia
Ponderación dentro del criterio: 20% dentro del Criterio 2

Descripción y requerimiento de información: Evaluaría si el proyecto beneficia de manera directa a población en condición de vulnerabilidad, pobreza extrema, discapacidad, primera infancia, mujeres jefas de hogar, adultos mayores u otros grupos identificados en el perfil. La IE registraría los grupos beneficiados y la proporción que representan dentro de la población total beneficiaria. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica — el proyecto no tiene una población objetivo específica identificable. |
| 0 | No identifica grupos en condición de vulnerabilidad entre los beneficiarios. |
| 1 | Menciona grupos vulnerables de forma general sin identificación ni cuantificación. |
| 2 | Identifica grupos vulnerables específicos, pero sin estimar su proporción dentro de los beneficiarios. |
| 3 | Identifica y cuantifica grupos vulnerables como parte de la población beneficiaria. |
| 4 | Identifica, cuantifica y describe cómo el proyecto atiende las necesidades específicas de los grupos vulnerables identificados. |
| 5 | Identifica, cuantifica y describe la atención a grupos vulnerables, con evidencia del diagnóstico que sustenta su priorización y estimación del impacto esperado en sus condiciones de vida. |

### Subcriterio 2.4 — Cobertura territorial de la intervención
Ponderación dentro del criterio: 20% dentro del Criterio 2

Descripción y requerimiento de información: Evaluaría el alcance geográfico del proyecto en términos del número de distritos o departamentos beneficiados, considerando la realidad territorial del país. La IE registraría la cobertura geográfica con el detalle de distritos y departamentos según sea el caso. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica — el proyecto es puntual por naturaleza y no tiene cobertura territorial ampliable. |
| 0 | No especifica la cobertura territorial del proyecto. |
| 1 | El proyecto beneficia a una comunidad, cantón o caserío dentro de un distrito. |
| 2 | El proyecto tiene cobertura de un distrito completo. |
| 3 | El proyecto tiene cobertura de dos a cinco distritos. |
| 4 | El proyecto tiene cobertura departamental o de más de cinco distritos. |
| 5 | El proyecto tiene cobertura nacional o beneficia a una región estratégica del país. |

### Subcriterio 3.1 — Gestión del riesgo climático y de desastres naturales
Ponderación dentro del criterio: 35% dentro del Criterio 3

Descripción y requerimiento de información: Evaluaría cómo el proyecto gestiona el riesgo climático y de desastres naturales. La IE registraría las actividades relacionadas con ambos elementos, si el proyecto se ubica en una zona con altos niveles de exposición o amenaza. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto. |
| 0 | Proyecto que incrementa la exposición al riesgo o se ubica en zona de amenaza alta sin medidas de mitigación |
| 1 | Proyecto en zona de amenaza baja |
| 2 | Proyecto en zona de amenaza sin medidas de resiliencia específicas, pero con riesgo identificado |
| 3 | Proyecto en zona de amenaza identificada (inundación, deslizamiento, sismo) con medidas generales (según normativas) de mitigación incorporadas en el diseño |
| 4 | Proyecto en zona de amenaza identificada (inundación, deslizamiento, sismo) con medidas específicas o adicionales de mitigación incorporadas en el diseño |
| 5 | Proyecto incorpora estándares de diseño de resiliencia climática verificables y documentados (normas sísmicas actualizadas, drenaje para periodo de retorno ≥100 años, materiales adaptados al calor, etc.) |

### Subcriterio 3.2 — Impacto sobre ecosistemas y recursos naturales
Ponderación dentro del criterio: 35% dentro del Criterio 3

Descripción y requerimiento de información: Evaluaría qué tan significativo es el impacto sobre ecosistemas y recursos naturales, y si estos son positivos o negativos. La IE registraría los tipos de impacto y su nivel. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto (sin impacto ambiental). |
| 0 | Proyecto con impactos ambientales negativos altos (por ejemplo: alta impermeabilización de suelos, tala, desplazamiento de fauna local, entre otros). |
| 1 | Proyecto con impactos ambientales negativos medio (por ejemplo: talas menores, entre otros) |
| 2 | Proyecto con impactos ambientales negativos bajo (por ejemplo: talas de arbustos, entre otros) |
| 3 | Proyecto con impacto ambiental positivo secundario (por ejemplo mejora de la calidad del agua como externalidad de un proyecto de alcantarillado) |
| 4 | Proyecto restaura, protege o mejora ecosistemas: reforestación, manejo de cuencas, saneamiento de cuerpos de agua, creación de áreas protegidas, sin detallarlo en el presupuesto del documento presentado. |
| 5 | Proyecto restaura, protege o mejora ecosistemas: reforestación, manejo de cuencas, saneamiento de cuerpos de agua, creación de áreas protegidas, y lo detalla, cuantifica e incorpora en el presupuesto del documento presentado |

### Subcriterio 3.3 — Emisiones de Gases de Efecto Invernadero
Ponderación dentro del criterio: 30% dentro del Criterio 3

Descripción y requerimiento de información: Evaluaría qué tan significativa es la cantidad de GEI que el proyecto incrementa o disminuye. La IE registraría la cantidad estimada de GEI que el proyecto disminuye o aumenta. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto. |
| 0 | Proyecto intensivo en consumo de combustibles fósiles sin medidas de compensación |
| 1 | Proyecto intensivo en consumo de combustibles fósiles con medidas de compensación |
| 2 | Proyecto con aumento marginal de emisiones |
| 3 | Proyecto neutro en emisiones: no agrava ni mejora el balance de GEI |
| 4 | Proyecto con reducción indirecta de emisiones identificada y demostrable (por ejemplo: mejora vial que reduce tiempos de motor, eficiencia energética en edificio público) |
| 5 | Proyecto directamente reduce o evita emisiones GEI cuantificadas (por ejemplo: energía renovable, transporte público eléctrico, reforestación con captura de carbono estimada) |

### Subcriterio 4.1 — Avance en estudios de preinversión
Ponderación dentro del criterio: 45% dentro del Criterio 4

Descripción y requerimiento de información: Evaluaría la etapa de preinversión con OT en correspondencia con la ruta de preinversión establecida para cada proyecto. La IE registraría la etapa actual y adjuntaría el documento técnico correspondiente al SIIP para la emisión de OT. Escala sugerida

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto (por ejemplo, proyectos de fortalecimiento institucional que no requieren estudios de preinversión). |
| 0 | Cuenta con OT vencida para cualquier etapa de Preinversión. |
| 1 | Cuenta con OT vencida para cualquier etapa de Preinversión, pero se encuentra en gestión de actualización. |
| 2 | Cuenta con OT para Perfil (Ruta: Perfil-Prefactibilidad-Factibilidad-Diseño). |
| 3 | Cuenta con OT para Prefactibilidad (Ruta: Perfil-Prefactibilidad-Factibilidad-Diseño). |
| 4 | Cuenta con OT para Factibilidad (Ruta: Perfil-Prefactibilidad-Factibilidad-Diseño). |
| 5 | Cuenta con Opinión Técnica para el último estudio de Preinversión establecido en la Ruta (Diseño o Perfil). |

### Subcriterio 4.2 — Análisis de alternativas y justificación de la solución seleccionada
Ponderación dentro del criterio: 25% dentro del Criterio 4

Descripción y requerimiento de información: Evaluaría si el proyecto identificó y comparó alternativas de solución antes de seleccionar la propuesta. La IE registraría las alternativas consideradas y la justificación técnica de la seleccionada en el perfil del proyecto. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica — el proyecto responde a una intervención de emergencia o a una solución única sin alternativas técnicas viables. |
| 0 | Presenta una solución única y no justifica la solución seleccionada. |
| 1 | Menciona alternativas, pero sin comparación técnica ni justificación de la selección. |
| 2 | Presenta comparación básica de alternativas con criterios cualitativos. |
| 3 | Presenta análisis de alternativas con criterios cuantitativos sin justificación de la selección. |
| 4 | Presenta análisis de alternativas con criterios cuantitativos y justificación de la selección. |
| 5 | Presenta análisis de alternativas con criterios cualitativos y cuantitativos y justificación de la selección. |

### Subcriterio 4.3 — Definición de metas físicas e indicadores y programación financiera
Ponderación dentro del criterio: 30% dentro del Criterio 4

Descripción y requerimiento de información: Evaluaría si el proyecto tiene definidas metas físicas cuantificables y los indicadores para medirlas. La IE registraría las metas e indicadores del proyecto, y la programación financiera en el SIIP. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica por la naturaleza del proyecto (por ejemplo, proyectos de fortalecimiento institucional). |
| 0 | No define metas físicas ni indicadores de ningún tipo. |
| 1 | Define metas de forma general sin cuantificación. |
| 2 | Define metas cuantificadas, pero sin indicadores de seguimiento asociados. |
| 3 | Define metas cuantificadas con indicadores de producto. |
| 4 | Define metas con indicadores de producto y resultado con descripción de los indicadores y con programación financiera |
| 5 | Define metas con indicadores de producto y resultado, con descripción de los indicadores y distribución de los mismos por período, y programación financiera. |

### Subcriterio 5.1 — Compatibilidad del costo total y programación financiera de la ejecución
Ponderación dentro del criterio: 40% dentro del Criterio 5

Descripción y requerimiento de información: Evaluaría la consistencia entre el costo total del proyecto (monto de Inversión) y su programación financiera durante el período previsto de ejecución. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica |
| 0 | No presenta información sobre costo total ni programación financiera de la ejecución. |
| 1 | Presenta costo total estimado, pero no presenta programación financiera de la ejecución. |
| 2 | Presenta el costo total y programación financiera, pero esta es incompleta o no permite identificar claramente los requerimientos de recursos por año. |
| 3 | Presenta el costo total y una programación financiera completa, identificando los requerimientos de recursos por año de ejecución. |
| 4 | Presenta el costo total, una programación financiera consistente con el período y un cronograma previsto de ejecución. |
| 5 | Se encuentra incorporado en el PRIPME, manteniendo consistencia entre su costo y programación financiera registrada. |

### Subcriterio 5.2 — Certidumbre y estructura de las fuentes de financiamiento
Ponderación dentro del criterio: 40% dentro del Criterio 5

Descripción y requerimiento de información: Evaluaría el grado de certidumbre sobre la disponibilidad de los recursos para financiar la inversión. La IE registraría la fuente de financiamiento identificada y su estado de gestión. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica |
| 0 | No identifica ninguna fuente de financiamiento para la inversión. |
| 1 | Identifica posibles fuentes de financiamiento, pero sin gestiones iniciadas. |
| 2 | Cuenta con fuentes identificadas y gestiones iniciadas para obtener el financiamiento. |
| 3 | Cuenta con fuentes definidas y evidencia de avances en su gestión o incorporación en la programación financiera/presupuestaria. |
| 4 | Cuenta con financiamiento formalmente contratado, convenido o asignado. |
| 5 | Cuenta con fuentes de financiamiento aseguradas. |

### Subcriterio 5.3 — Costos de operación y mantenimiento
Ponderación dentro del criterio: 20% dentro del Criterio 5

Descripción y requerimiento de información: Evaluaría si el proyecto identifica los costos de operación y mantenimiento que se generarán durante su funcionamiento y las fuentes previstas para su atención. Escala sugerida:

| Valor | Escala |
|---|---|
| N/A | No aplica — el proyecto no genera costos de O&M por su naturaleza. |
| 0 | No identifica ni estima costos de O&M. |
| 1 | Identifica de manera general que existirán costos de O&M, sin cuantificarlos. |
| 2 | Identifica y cuantifica los costos de O&M. |
| 3 | Identifica y cuantifica los costos de O&M y señala la entidad que será responsable de asumirlos. |
| 4 | Identifica y cuantifica los costos de O&M, señala la entidad que será responsable de asumirlos e identifica la fuente prevista para financiar los costos de O&M. |
| 5 | Cuenta con información actualizada sobre los costos de O&M, de acuerdo con el estado de avance del proyecto. |

> Versión del archivo anexo (impresión del Excel): las hojas de escalas (p. 9–15 y p. 16–22, esta última titulada "CATÁLOGOS ESCALA DE EVALUACIÓN") contienen los mismos 17 subcriterios y los mismos valores N/A–5, con los textos de descripción recortados por la impresión y con las diferencias de redacción señaladas en Observaciones (puntos 25 y 26). No se detectaron subcriterios, ponderaciones ni valores de escala adicionales o distintos.

---

# Eventos del Sistema

> Nota sobre información derivada: el documento no incluye una sección de eventos. Esta tabla fue construida por el análisis a partir de los pasos de flujo y reglas de negocio citados en cada fila; las denominaciones de los eventos no son texto literal del documento.

| Evento | Origen | Destino |
|---|---|---|
| Redirección al formulario de priorización tras emitir OT | Sistema (FB1 Técnico PRE paso 1, Opción 1) | Técnico PRE — pantalla A.1 |
| Visualización del formulario "Priorización: Matriz multicriterio" | Sistema (FB1 ambos, paso 3) | Técnico PRE / Técnico SYMP |
| Guardado de la calificación con mensaje A.5 | Técnico PRE / Técnico SYMP (FA01) | Sistema |
| Validación de puntajes completos | Sistema (RN04), al dar clic en "Calificar Priorización" | Técnico PRE / Técnico SYMP |
| Bloqueo de la pantalla A.1 | Sistema (RN05; FB1 ambos, paso 8) | Técnico PRE / Técnico SYMP |
| Notificación de revisión pendiente | Sistema (FB1 ambos, paso 6) | Coordinador PRE / Coordinador SYMP |
| Revisión de la calificación | Coordinador PRE / Coordinador SYMP (FB1 ambos, paso 7) | Sistema |
| Mensaje A.3, pantalla A.2 y notificación de criterio 5 pendiente | Sistema (FB1 Técnico PRE paso 8) | Técnico PRE (mensaje); Técnico SYMP y Coordinador SYMP (notificación) |
| Mensaje A.4, pantalla A.2 y notificación de calificación completada | Sistema (FB1 Técnico SYMP paso 8) | Técnico SYMP (mensaje); Técnico PRE y Coordinador PRE (notificación) |
| Cálculo de puntajes y Prioridad del proyecto | Sistema (RN12–RN14) | Pantalla A.2 |
| Cálculo total completado | Sistema (RN07), al calificar el Técnico SYMP el criterio 5 | Pantalla A.2 |
| Habilitación de ajustes de calificación | Coordinador PRE (RN10) / Coordinador SYMP (RN11) | Técnico PRE / Técnico SYMP encargado del caso |
| Actualización de la calificación a requerimiento de la DGI | DGI (RN06) | Técnico PRE / Técnico SYMP |

---

# Integraciones

> Nota sobre información derivada: el documento no incluye una sección de integraciones. La tabla fue construida por el análisis; la clasificación de la columna "Tipo" y las descripciones son síntesis a partir de las referencias citadas, no texto literal del documento.

| Sistema | Tipo | Descripción |
|---|---|---|
| Servicio de notificaciones | Notificación | > No especificado en el documento (medio no indicado). |
| Pantalla "Captura de proyectos" | Interna (acceso) | Selección del proyecto a priorizar (FB1 ambos). |
| CU-PRE-24 / CU-PRE-25 / CU-PRE-26 | Interna (precondición) | Filtro habilitante. |
| CU-PRE-26 Opinión Técnica | Interna (disparo) | Redirección al formulario de priorización tras emitir OT (FB1 Técnico PRE paso 1). |
| CU-PRO-01 / CU-PRO-08 | Interna (postcondición) | Uso posterior de la priorización (PRIPME, escenarios de corto plazo). |
| Libro Excel anexo ("CU-PRE-26.5 (ANEXO) Criterios de Priorización_Agosto-2.xlsx") | Fuente de catálogos | Criterios, subcriterios, ponderaciones y rangos ("Versión editable en Excel", Anexo B.1). El documento no especifica si el Sistema lo lee o si es solo documento de referencia. |

---

# Datos Pendientes de Definir

1. **Prioridad** del caso de uso: no especificada.
2. **Actor principal:** el documento no designa uno.
3. **Preguntas abiertas del comentario de revisión:** si la priorización constituye un filtro adicional de aprobación y si debe realizarse cada vez que se emite OT.
4. **Secuencia del FB1 Técnico PRE:** a qué opción corresponde el paso 3 y cuál es el paso siguiente de cada opción.
5. **Momento del bloqueo de la pantalla A.1:** al dar clic en "Calificar Priorización" (RN05) o tras la revisión del Coordinador (paso 8).
6. **Rol que califica el criterio 5:** "Técnico SYMP" (flujos, RN), "Técnico ASYMP" (mockups) o "Técnico URP" (Anexo B.1); y rol del Coordinador ("Coordinador SYMP" vs. "Coordinador PRE/ASYMP").
7. **Nombre del botón de calificación:** "Calificar Prioridad" vs. "Calificar Priorización".
8. **Naturaleza de los controles de revisión y habilitación** (botón vs. casilla) y significado de "Editable: No" en campos que el usuario acciona.
9. **Método de redistribución de ponderaciones en RN13** (proporcional o equitativo).
10. **Definición del total "Prioridad del proyecto"** (suma de subcriterios según RN12 vs. suma de criterios según Anexo B.1) y un ejemplo de cálculo coherente, dado que los mockups A.1 y A.2 no son consistentes entre sí.
11. **Tratamiento de puntajes decimales entre rangos de interpretación** (p. ej. entre 84 y 85).
12. **Texto y medio de las notificaciones** al Coordinador PRE y al Coordinador SYMP (pasos 6), y medio de envío de las notificaciones de los pasos 8.
13. **Texto definitivo de los mensajes** A.3 ("Técnico SYMP" vs. "Técnico ASYMP") y de la "Implicación para la programación" (texto del mockup A.2 vs. ventana de rangos).
14. **Alcance de la precondición "Opinión Técnica al perfil"** frente a la priorización "cada vez que se emita Opinión Técnica".
15. **Acción del botón "ACEPTAR"** de los mensajes A.3 y A.4.
16. **Procedimiento de la actualización de la calificación a requerimiento de la DGI (RN06):** no se describe el flujo ni quién lo inicia en el sistema.
17. **Obligatoriedad de la calificación del criterio 5:** "cuando sea necesario" (Descripción) vs. RN07 y mensaje A.3.
18. **Tratamiento del criterio 5 en la pantalla A.2** cuando se muestran resultados con solo los criterios 1–4 calificados (valor mostrado, efecto en "Prioridad del proyecto" y en la categoría de interpretación).
19. **Clasificación de actores:** el documento no distingue actor principal ni secundarios.
