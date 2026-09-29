---
id: CU-PRE-25
codigo: CU-PRE-25
nombre: Elegibilidad
modulo: Preinversión
submodulo: Gestión del Proyecto
version: "1.3"
fuente_pdf: 1790382766227_CU-PRE-25_Elegibilidad.pdf
fuente_pdf_anterior: CU-PRE-25_Elegibilidad_MAYO_2026_V1_F.pdf
fuente_anexo_xlsx: CU-PRE-25__ANEXO__Criterios_de_Priorización_de_proy_de_IP.xlsx
version_documento_fuente: "1.0 (portada, encabezados de página e Historial de Revisiones del PDF de AGOSTO 2026 coinciden en la versión 1.0; ver Observaciones sobre la discrepancia de fechas)"
pagina_inicio: 1
pagina_fin: 8

nota_version: >
  La versión 1.3 reconcilia este documento con una nueva entrega del PDF
  fuente (1790382766227_CU-PRE-25_Elegibilidad.pdf, encabezados "AGOSTO
  2026"), que reemplaza como fuente principal al PDF anterior
  (CU-PRE-25_Elegibilidad_MAYO_2026_V1_F.pdf). El nuevo PDF no incluye el
  anexo Excel. Su mockup del Anexo A.1 muestra solo la dimensión
  "1. Alineación estratégica" (4 criterios), su Anexo C solo contiene los
  catálogos C.1 y C.2, y el FB2 menciona "Dimensión 1" (antes "Dimensiones
  1 y 2"). El contenido de la versión anterior que el nuevo PDF no repite
  (dimensión "Aspectos Medioambientales" del mockup, catálogos C.3 a C.5,
  tabla completa del anexo Excel) se conserva y se señala como posible
  eliminación pendiente de confirmar. Ver "Observaciones" y "Datos
  Pendientes de Definir".

  La versión 1.2 añade una verificación cruzada con el documento hermano
  UC-PRE-26_5-Priorizacion.md (CU-PRE-26.5 "Priorización"), cuyo propio
  catálogo de criterios/subcriterios (4 criterios, ponderación en
  porcentaje, confirmado contra su propio PDF y su propio anexo Excel
  independiente) no coincide con la tabla de 6 dimensiones/20 criterios
  ponderados en puntos (total 100) transcrita en este documento a partir
  de su propio anexo Excel. Esto debilita, sin descartar, la hipótesis
  planteada en la versión 1.1 de que dicha tabla perteneciera a CU-PRE-26.5
  en lugar de a este caso de uso. Ver "Observaciones" y "Datos Pendientes
  de Definir".

  La versión 1.1 corrigió una extracción incompleta e inexacta del anexo
  Excel realizada en la versión 1.0. La versión 1.0 afirmaba, sin haberlo
  verificado línea por línea, que la Hoja1 del anexo "confirma la misma
  estructura de 2 dimensiones y 7 criterios" mostrada en el mockup del PDF.
  Al inspeccionar íntegramente el archivo ahora provisto (nombre correcto:
  `CU-PRE-25__ANEXO__Criterios_de_Priorización_de_proy_de_IP.xlsx`, distinto
  del nombre `CU-PRE-25__ANEXO__Criterios_de_Elegibilidad.xlsx` registrado en
  la versión 1.0), se confirma que la Hoja1 contiene en realidad **6
  dimensiones y 20 criterios**, con un sistema de ponderación por dimensión
  y por criterio que totaliza 100 puntos ("Total Calificación (Prioridad)"),
  no descrito en ninguna otra parte de este documento. Se corrigieron todas
  las afirmaciones de la versión 1.0 que asumían solo 2 dimensiones/7
  criterios (en "Campos", "Pantallas", "Entidades Detectadas", "Catálogos
  Detectados", "Observaciones" y "Datos Pendientes de Definir"), y se
  resolvió la incertidumbre previamente señalada sobre a qué dimensión
  correspondían los catálogos "grupos poblacionales en situación de
  vulnerabilidad" y "mejoras en calidad de vida": ambos corresponden a la
  dimensión "2. Aspectos Sociales", ahora confirmada en la Hoja1.

actor_principal: [Viabilizador]

actores_secundarios: [Técnico URP, Técnico PRE, Técnico de SYMP, Sistema, Actores de la DGI]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-24"]

casos_relacionados: ["CU-PRE-26", "CU-PRE-26.5", "CU-PRE-32"]

roles: ["Viabilizador", "Técnico URP", "Técnico PRE", "Técnico de SYMP", "Sistema (no figura en la sección 'Actores' del documento; ejecuta los pasos automáticos)", "Actores de la DGI (solo visualización, RN02)"]

pantallas: ["Selección y calificación de Criterios de Elegibilidad (Anexo A.1)", "Guardar (Anexo A.2)", "Mensaje de Emisión de Elegibilidad (Anexo A.3)", "Captura de proyectos (FB1 paso 1; sin mockup en el documento)", "Formulario de CU-PRE-26 'Opinión Técnica' en ventana emergente (FB2 paso 5; pantalla de otro caso de uso)"]

procesos: ["Calificación de criterios de elegibilidad", "Guardado de la información de elegibilidad", "Emisión de Elegibilidad", "Atención de comentarios a la Elegibilidad desde la gestión de OT"]

servicios_externos: ["Objetivos de Desarrollo Sostenible (ODS) - Naciones Unidas (enlace externo, RN06)"]

entidades: ["Proyecto", "Criterio de Elegibilidad", "Dimensión", "Respuesta ¿Aplica?", "Información 'Especificar'", "Elegibilidad (estado del proyecto)", "Comentario de Opinión Técnica sobre Elegibilidad / Respuesta Institución", "Devoluciones a elegibilidad", "Notificación", "Ponderación / Calificación de Prioridad (identificada en el anexo Excel, Hoja1; no descrita en el PDF)"]

catalogos: ["C.1 ODS", "C.2 Ejes del Plan de Gobierno", "C.3 Componentes del Medio Ambiente (solo en el PDF anterior; posible eliminación, v1.3)", "C.4 Medidas GRD (solo en el PDF anterior; posible eliminación, v1.3)", "C.5 Medidas de Adaptación/Mitigación Cambio Climático (solo en el PDF anterior; posible eliminación, v1.3)", "Catálogo de grupos poblacionales en situación de vulnerabilidad (dimensión '2. Aspectos Sociales', confirmado en v1.1)", "Catálogo de mejoras en calidad de vida (dimensión '2. Aspectos Sociales', confirmado en v1.1)", "Tabla completa de Dimensiones/Criterios/Ponderación (6 dimensiones, 20 criterios, anexo Excel Hoja1, incorporada en v1.1)", "Opciones 'Sí'/'No' de Especificar (Selección radial)", "Estados del proyecto (valores mencionados)"]

palabras_clave: ["Elegibilidad", "Viabilizador", "Criterios", "Dimensión", "Alineación estratégica", "ODS", "Plan de Gobierno", "Medio Ambiente", "GRD", "Cambio Climático", "Emitir Elegibilidad", "Proyecto Elegible", "Opinión Técnica", "Ver comentarios OT", "Ponderación", "Priorización", "Calificación"]

ultima_actualizacion: "JUN 2025 (versión 1.0); corrección de la extracción del anexo Excel (tabla completa de 6 dimensiones/20 criterios/ponderación) aplicada AGO 2026 (v1.1); verificación cruzada con CU-PRE-26.5 aplicada AGO 2026 (v1.2); reconciliación con el PDF de AGOSTO 2026 aplicada SEP 2026 (v1.3)"

trazabilidad:
  informacion_general:
    pagina: 3
  historial_revisiones:
    pagina: 2
  flujo_principal:
    FB1:
      pagina: 3
      nota: "El paso 6 comienza en la página 3 y continúa en la página 4; el paso 7 está en la página 4."
  flujos_alternos:
    FA01:
      pagina: 4
    FB2:
      pagina: 4
  reglas_negocio:
    RN01:
      pagina: 4
    RN02:
      pagina: 4
    RN03:
      pagina: 4
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
    RN15:
      pagina: 5
  anexos:
    A1:
      nombre: Selección y calificación de Criterios de Elegibilidad
      pagina: 6
    A2:
      nombre: Guardar
      pagina: 7
    A3:
      nombre: "Mensaje de Emisión de Elegibilidad"
      pagina: 7
    B1:
      nombre: "Requerimientos Funcionales - Formatos (sección 'Criterios de Elegibilidad Proyectos')"
      pagina: 7
    C_catalogos:
      nombre: "Anexo C - Catálogos (C.1 y C.2 en el PDF de AGOSTO 2026; C.3 a C.5 solo en el PDF anterior de MAYO 2026, página 9 de ese archivo)"
      pagina: 8
    Anexo_Excel:
      nombre: "CU-PRE-25__ANEXO__Criterios_de_Priorización_de_proy_de_IP.xlsx (Hoja1: tabla de 6 dimensiones/20 criterios/ponderación, título interno 'CRITERIOS DE ELEGIBILIDAD DE PROYECTOS'; Hoja2: catálogos completos). Entregado junto con el PDF anterior; no acompaña al PDF de AGOSTO 2026."
      pagina: No aplica (archivo Excel independiente, sin numeración de página).
  nota_paginas: "Los números de página corresponden al PDF de AGOSTO 2026 (8 páginas) y se determinaron extrayendo el texto de cada página de forma individual. El PDF no incluye numeración de página explícita en el pie, por lo que corresponden a la posición física de cada página dentro del archivo. El anexo Excel es un archivo independiente sin paginación."
---

# Caso de Uso

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|-------|---------|--------------|-------|-------|
| AGO 2025 | 1.0 | Versión Inicial | Equipo Preinversión | No especificado en el documento. |

> Nota de ambigüedad: la versión registrada en el Historial (1.0) coincide con la de la portada y los encabezados de página. Sin embargo, los encabezados de página indican "Fecha: AGOSTO 2026", mientras que la única entrada del Historial de Revisiones tiene fecha "AGO 2025", sin una entrada que corresponda a AGOSTO 2026. Ver Observaciones.
>
> Nota de reconciliación (v1.3): el PDF anterior (encabezados "MAYO 2026") registraba para la misma versión 1.0 la fecha "JUN 2025" en el Historial. Ningún historial registra una versión 1.1 o posterior del documento, pese a que el contenido cambió entre ambas entregas.

---

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Elegibilidad |
| Código | CU-PRE-25 |
| Módulo | Preinversión |
| Fuente | 1790382766227_CU-PRE-25_Elegibilidad.pdf (Dirección General de Inversión y Crédito Público / Sistema de Información de Inversión Pública — "Especificación de Casos de Uso: 'CU-PRE-25 Elegibilidad'"); fuentes anteriores conservadas: CU-PRE-25 Elegibilidad_MAYO 2026_V1_F.pdf y CU-PRE-25__ANEXO__Criterios_de_Priorización_de_proy_de_IP.xlsx |
| Versión | 1.3 (versión de este análisis); versión del documento fuente: 1.0 |
| Fecha (encabezado de página) | AGOSTO 2026 |

> Nota de reconciliación (v1.3): el PDF de AGOSTO 2026 no tiene un campo explícito "Módulo" ni "Submódulo". Los valores "Preinversión" / "Gestión del Proyecto" provienen de la versión de trabajo anterior y se conservan.

**Campos requeridos (según el PDF):**
- Dimensión
- Criterio
- ¿Aplica?
- Especificar

**Ruta de Acceso (según el PDF):**

> No especificado en el documento. Este documento usa el patrón de tabla "Identificación" y no incluye una sección "Ruta de Acceso".

## Comentarios de revisión presentes en el documento

El PDF de AGOSTO 2026 contiene dos comentarios de revisión (formato "Commented [n]") en el margen de la página 3, que forman parte del contenido del documento:

| Comentario | Texto anclado en el documento | Texto del comentario |
|---|---|---|
| Commented [1] | Postcondición "CU-PRE-32 Avance Financiero Cuatrimestral del PAP" | "Hacía falta colocar este caso de uso... aunque lo quitaron de la versión anterior, si es necesario. e lo contrario tocaría actualizar ese CU." |
| Commented [2] | "Anexo A.1." en el paso 2 del Flujo Básico 1 | "Esta pantalla no podría unificarse con el paso de viabiliad? no se trata de desaparecer la elegibilidad, sino que aprovechando que quien lo emite es el mismo rol (viabilizador), en un solo paso, aparezcan las dos pantallas "Viabilidad" y "elegibilidad" y que de una vez emita concepto..." |

> Nota: en el comentario [1], la última oración aparece como "e lo contrario" (el texto parece truncado al inicio de la palabra); se transcribe tal como aparece, sin completarlo. El comentario [2] termina con puntos suspensivos en el original.

---

# Objetivo

Este caso de uso permitirá al Viabilizador calificar los criterios establecidos para elegibilidad de los proyectos.

---

# Descripción

Este caso de uso permitirá al Viabilizador calificar los criterios establecidos para elegibilidad de los proyectos.

Este filtro de aprobación se realiza una sola vez en cada proyecto, luego de emitir viabilidad en la fase de perfil, para poder emitir Opinión Técnica por primera vez.

# Actor Principal

Viabilizador (único actor listado en la sección "Actores" del documento).

---

# Actores Secundarios

- Técnico URP (notificado al emitirse la Elegibilidad; según el paso 7 del Flujo Básico 1, da clic en el botón "ir a OT" — ver nota de ambigüedad en el Flujo Principal).
- Técnico PRE (envía comentarios desde CU-PRE-26 "Opinión Técnica" y da clic en "Enviar comentarios" desde CU-PRE-26, RN09; notificado al emitirse la Elegibilidad y al atenderse observaciones).
- "Técnico de SYMP" (mencionado únicamente en el paso 8 del Flujo Básico 2 como destinatario de una notificación; no aparece en el campo "Actores" de la Identificación ni se define en ninguna otra parte del documento). Ver "Datos Pendientes de Definir".
- **Sistema** — No figura en la sección "Actores" del documento. Se incluye por ser quien ejecuta los pasos automáticos de los flujos (mostrar formularios y mensajes, guardar la información, cambiar el estado del proyecto, notificar, habilitar/deshabilitar pantallas y campos).
- **Actores de la DGI** — No figuran en la sección "Actores" del documento. Mención genérica en RN02 ("Los actores de la DGI podrán únicamente visualizar todas las secciones de la pantalla de este caso de uso").

> Nota: el campo "Actores" de la Identificación del caso de uso solo lista a "Viabilizador". La mención de "Técnico URP", "Técnico PRE" y "Técnico de SYMP" proviene de los pasos del Flujo Básico 1, del Flujo Básico 2 y de RN09; la de "Actores de la DGI", de RN02.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. CU-PRE-24 Viabilidad.

> Nota de ambigüedad: el documento lista la precondición únicamente como referencia a otro caso de uso, sin un verbo explícito (p. ej. "haber ejecutado", "contar con") que aclare la condición exacta. La Descripción indica que el filtro se realiza "luego de emitir viabilidad en la fase de perfil", y el FB1 paso 1 lista proyectos en estado "Proyecto viable", pero el documento no formula esa condición en la sección de Precondiciones.

---

# Flujo Principal

## FB1 – Flujo Básico 1 (Actor Viabilizador)

1. Viabilizador:
   - **Opción 1:** Ingresa a la pantalla "Captura de proyectos", con la lista de los proyectos de la entidad en el estado "Proyecto viable".
   - **Opción 2:** Luego de emitir viabilidad el sistema lo lleva al formulario de elegibilidad (continúa con el paso 4).
2. Viabilizador (Opción 1): Da clic en el proyecto sobre el cual va a emitir elegibilidad y entra al formulario del Anexo A.1.
3. Sistema (Opción 2): El sistema muestra el formulario "Criterios de Elegibilidad del proyecto" (Ver Anexo A.1).
4. Viabilizador (para las dos opciones): Selecciona en la columna "Aplica" los criterios a los cuales contribuye el proyecto, y en la columna "Especificar" registra o selecciona la información complementaria sobre cada criterio seleccionado (Ver Anexo A.1).
5. Viabilizador: Da clic en el botón "Guardar" en cualquier momento durante el diligenciamiento y cuando termine da clic en el botón "Emitir Elegibilidad".
6. Sistema: Muestra el mensaje del Anexo A.3. Cambia el estado del Proyecto a "Proyecto Elegible", notifica al Técnico URP y al Técnico PRE y habilita el formulario para emitir OT, y la pantalla "Elegibilidad" queda deshabilitada.
7. Técnico URP: Da clic en el botón "ir a OT" para continuar con la gestión del proyecto.

> Nota de ambigüedad: el paso 7 atribuye al "Técnico URP" el clic en el botón "ir a OT"; sin embargo, dicho botón forma parte del mensaje emergente del Anexo A.3, que aparece como parte de la secuencia de acciones del Viabilizador (pasos 5–6, inmediatamente tras "Emitir Elegibilidad"), y el mockup del Anexo A.1 muestra además un botón "IR A OT" en la pantalla del Viabilizador. No se puede determinar con certeza cuál actor visualiza y acciona efectivamente ese botón. Ver "Datos Pendientes de Definir".
>
> Nota de ambigüedad: la Opción 2 del paso 1 indica "(Continúa con el paso 4)", pero el paso 3 está rotulado como "Opción 2" ("El sistema muestra el formulario 'Criterios de Elegibilidad del proyecto'"). El documento no aclara si la Opción 2 pasa por el paso 3 antes de llegar al paso 4 o si salta directamente al paso 4.

---

# Flujos Alternos

## FA01 – Flujo Alternativo 1: Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1.1. Viabilizador da clic en el botón "Guardar".

1.2. Sistema muestra el mensaje emergente del Anexo A.2.

1.3. Viabilizador da clic en "Aceptar" al mensaje emergente.

1.4. Sistema guarda la información registrada y se mantiene en la pantalla "Elegibilidad".

**Resultado**

> No especificado en el documento.

> Nota de ambigüedad: el FA01 no incluye la validación de la columna "Especificar" que RN03 establece al dar clic en "Guardar". Además, el mensaje "¡Guardado!" (Anexo A.2) se muestra en el paso 1.2, antes de que el Sistema guarde la información en el paso 1.4.

## FB2 – Flujo Básico 2: Comentarios a la Elegibilidad desde la gestión de OT

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico PRE envía comentarios, si aplica, a la información registrada en la dimensión de la elegibilidad, a través del formato CU-PRE-26 "Opinión Técnica".
2. Sistema notifica al Viabilizador que se emitieron comentarios, y habilita los campos de la pantalla de "Criterios de Elegibilidad del Proyecto".
3. Viabilizador ajusta la información en el formulario A.1 (Dimensión 1).
4. Viabilizador da clic en el botón "Ver comentarios OT".
5. Sistema muestra en una ventana emergente el formulario de CU-PRE-26 "Opinión Técnica".
6. Viabilizador registra respuesta en el formulario CU-PRE-26 "Opinión Técnica" y oprime el botón "Guardar Ajustes" y sale de la ventana emergente.
7. Viabilizador da clic en el botón "Emitir Elegibilidad".
8. Sistema notifica al Técnico PRE y al "Técnico de SYMP" que fueron atendidas las observaciones a la Elegibilidad.

**Resultado**

> No especificado en el documento.

> Nota de ambigüedad: el paso 3 menciona únicamente "(Dimensión 1)", y el paso 1 se refiere a "la dimensión de la elegibilidad" en singular, consistente con el mockup del Anexo A.1 del PDF de AGOSTO 2026 (que solo muestra "1. Alineación estratégica"). Esto no coincide con la tabla completa de 6 dimensiones de la Hoja1 del anexo Excel (ver "Catálogos Detectados"), por lo que no se puede determinar si el ajuste tras comentarios de OT se limita a la dimensión 1 o si la referencia es solo ilustrativa. El paso 8 introduce al actor "Técnico de SYMP", no definido en el documento. Ver "Datos Pendientes de Definir".
>
> El documento denomina a este flujo "Flujo Básico 2 – FB2", aunque se ubica después del Flujo Alternativo 1 y describe un escenario de devolución desde CU-PRE-26 "Opinión Técnica". Se preserva la denominación original.

---

# Excepciones

| Código | Descripción | Consecuencia |
|---|---|---|
| Sin código en el documento | Al dar clic en "Guardar", existen criterios seleccionados sin información en la columna "Especificar" (RN03). | El Sistema mostrará el mensaje "Se debe completar la información de la columna 'Especificar' para los criterios seleccionados". |
| Sin código en el documento | Al emitir nuevamente la elegibilidad respondiendo a observaciones de la OT, el viabilizador no ha respondido todos los comentarios en el campo "Respuesta Institución" de CU-PRE-26 "Opinión Técnica" (RN15). | El sistema deberá mostrar un mensaje que diga "Es necesario responder los comentarios de la Opinión Técnica previo a la emisión de elegibilidad" y no permitirá generar la elegibilidad nuevamente. |

> El documento no incluye una sección formal de "Excepciones". Las filas anteriores provienen de las validaciones descritas en RN03 y RN15.

# Postcondiciones

1. CU-PRE-26 "Opinión Técnica".
2. CU-PRE-26.5 "Priorización".
3. CU-PRE-32 "Avance Financiero Cuatrimestral del PAP".

> Nota de ambigüedad: el documento lista las postcondiciones únicamente como referencias a otros casos de uso, sin un verbo explícito que aclare la condición resultante. La postcondición "CU-PRE-32 Avance Financiero Cuatrimestral del PAP" (nueva en el PDF de AGOSTO 2026; no figuraba en el PDF anterior) tiene anclado el comentario de revisión [1] ("Hacía falta colocar este caso de uso... aunque lo quitaron de la versión anterior, si es necesario. e lo contrario tocaría actualizar ese CU."), y no se menciona en ningún flujo ni regla de negocio del documento.

---

# Reglas de Negocio

## RN01

**Descripción:** El Viabilizador es el único que puede registrar información en las columnas "¿Aplica?" y "Especificar".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

## RN02

**Descripción:** Los actores de la DGI podrán únicamente visualizar todas las secciones de la pantalla de este caso de uso.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

## RN03

**Descripción:** Será obligatorio que para los criterios seleccionados por el Viabilizador se complete la información de la columna "Especificar", caso contrario, el Sistema mostrará el mensaje "Se debe completar la información de la columna 'Especificar' para los criterios seleccionados". Dicha validación se realizará cuando el Viabilizador dé clic al botón "Guardar".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

## RN04

**Descripción:** Una vez el Viabilizador dé clic en el botón "Emitir Elegibilidad" el Sistema bloqueará para edición los campos de las columnas "¿Aplica?" y "Especificar" y el Sistema mostrará el mensaje "La Elegibilidad ha sido emitida con éxito. Puede continuar con la gestión de Opinión Técnica" (Anexo A3).

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

> Nota de ambigüedad: el texto de este mensaje difiere levemente del mostrado en el mockup del Anexo A.3 ("La Elegibilidad del proyecto ha sido emitida con éxito. Puede contiuar con la gestión de la Opinión Técnica"). Ver Observaciones.

## RN05

**Descripción:** El botón "Guardar" será visible y estará habilitado únicamente para el Viabilizador.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

## RN06

**Descripción:** Para consulta de los ODS, el Sistema presentará el botón "Consulta ODS" que lo llevará al siguiente link: https://www.un.org/sustainabledevelopment/es/objetivos-de-desarrollo-sostenible/

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

## RN07

**Descripción:** El Sistema bloqueará la pantalla del Anexo A.1 una vez el proyecto se envíe a CU-PRE-26 "Opinión Técnica" por primera vez.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

> Nota de ambigüedad: esta regla comparte su cláusula inicial con RN09, la cual la amplía. Ver Observaciones.

## RN08

**Descripción:** La columna "Especificar" debe permitir múltiples selecciones o respuestas en algunas de las preguntas que así lo requieran.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

## RN09

**Descripción:** El Sistema bloqueará la pantalla del Anexo A.1 una vez el proyecto se envíe a CU-PRE-26 "Opinión Técnica" por primera vez y se habilitará nuevamente solo si el Técnico PRE da clic en el botón "Enviar comentarios" desde CU-PRE-26 "Opinión Técnica", a fin de que el Viabilizador realice ajustes en los criterios seleccionados según corresponda.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

## RN10

**Descripción:** El proyecto se puede devolver a elegibilidad cuantas veces sea necesario. Los comentarios emitidos en OT se deben guardar cada vez que se devuelva y deben poderse consultar en pantalla las observaciones de cada devolución. El sistema debe mostrar cuántas veces se ha devuelto.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

> Nota de ambigüedad: ni el mockup del Anexo A.1 ni la tabla de formatos del Anexo B muestran un contador de devoluciones ni una vista de historial de observaciones por devolución. Ver "Datos Pendientes de Definir".

## RN15

**Descripción:** En caso que la elegibilidad se emita nuevamente respondiendo a una observación por parte de la OT, el sistema debe validar que previamente se hayan respondido los comentarios en CU-PRE-26 "Opinión Técnica" por parte del viabilizador.

El viabilizador debe consultar las observaciones a través del botón "Ver comentarios OT" ubicado en el Anexo A.1. Al hacer clic el sistema debe remitirlo a los comentarios de CU-PRE-26 "Opinión Técnica" para que pueda diligenciar el campo "Respuesta Institución" y oprimir el botón "Guardar Ajustes".

El sistema debe validar que si hay observaciones CU-PRE-26 "Opinión Técnica", el viabilizador haya respondido todos los comentarios en el campo "Respuesta Institución" para que le permita generar la elegibilidad nuevamente. De lo contrario, el sistema deberá mostrar un mensaje que diga "Es necesario responder los comentarios de la Opinión Técnica previo a la emisión de elegibilidad".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-25.

> Nota de ambigüedad: la numeración de las Reglas de Negocio salta de RN10 a RN15, sin que las reglas RN11, RN12, RN13 y RN14 estén presentes en el documento. Además, el campo "Respuesta Institución" mencionado aquí no coincide con el nombre "Justificación Institución" usado para un concepto aparentemente equivalente en CU-PRE-24 "Viabilidad" (RN11 de dicho documento). Ver "Datos Pendientes de Definir".

---

# Campos

### Sección "Criterios de Elegibilidad Proyectos" (según Anexo B – Requerimientos Funcionales, B.1– Formatos)

> La sección del Anexo B.1 se denomina "Criterios de Elegibilidad Proyectos" y no indica un número de Anexo; corresponde a la pantalla que el resto del documento identifica como "Anexo A.1" / "A.1- Selección y calificación de Criterios de Elegibilidad". El mockup se titula "CRITERIOS DE ELEGIBILIDAD DE PROYECTOS" (ver Observaciones sobre la nomenclatura de la pantalla).

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Dimensiones | Campo que muestra las dimensiones de agrupación de criterios. | Texto | Texto | Incluido en "Campos requeridos" (como "Dimensión") | No especificado en el documento. | Editable: No. **Nota v1.3:** el mockup del PDF de AGOSTO 2026 solo muestra la dimensión "1. Alineación estratégica". **Corrección v1.1:** el mockup del PDF anterior (Anexo A.1) solo ejemplificaba 2 dimensiones ("1. Alineación estratégica" y "4. Aspectos Medioambientales" en la numeración completa); la Hoja1 del anexo Excel confirma que el catálogo completo tiene **6 dimensiones**: 1. Alineación estratégica, 2. Aspectos Sociales, 3. Rentabilidad social, 4. Aspectos Medioambientales, 5. Sostenibilidad fiscal, 6. Madurez del Proyecto (ver tabla completa en "Catálogos Detectados"). |
| Criterio | Campo que muestra los criterios definidos por la DGI para la elegibilidad de un proyecto. (Criterios en el Excel) | Texto | Texto | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No. El Detalle remite a un Excel con los criterios; el PDF de AGOSTO 2026 no incluye ese Excel (el anexo Excel transcrito en este documento acompañaba al PDF anterior). **Nota v1.3:** el mockup del PDF de AGOSTO 2026 solo muestra 4 criterios. **Corrección v1.1:** el mockup del PDF anterior solo ejemplificaba 7 de los 20 criterios totales; la Hoja1 del anexo Excel confirma **20 criterios en total**, distribuidos en las 6 dimensiones, cada uno con una "Ponderación por Criterio" que no se describe en ningún Flujo ni Regla de Negocio de este documento (ver tabla completa en "Catálogos Detectados" y nota en Observaciones). |
| ¿Aplica? | Campo para que el viabilizador seleccione a qué criterios contribuye un proyecto. Mostrará un botón radial para selección. | Selección | Selección | Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según el Anexo B). Contradicción: RN01 indica que el Viabilizador registra información en esta columna. En FB1 paso 4 se denomina columna "Aplica". Ver Observaciones respecto a que la Detalle describe un campo seleccionable por el Viabilizador mediante botón radial, y el mockup lo muestra como control interactivo con selecciones de ejemplo. |
| Especificar (sub-formato: selección radial Sí/No) | Campo para que el Viabilizador seleccione entre las opciones "Sí" y "No". | Selección (radial) | Selección (radial) | Obligatorio para los criterios seleccionados (RN03). Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según Anexo B.1). Contradicción: el Detalle describe un campo que el Viabilizador diligencia (selección), y RN01 indica que el Viabilizador registra información en esta columna. Ninguna pregunta del mockup del Anexo A.1 (en ninguna de las dos versiones del PDF) muestra opciones "Sí"/"No", y el PDF no indica a qué criterios aplica este tipo; según la Hoja1 del anexo Excel, 7 de los 20 criterios lo usan (ver Observaciones). |
| Especificar (sub-formato: selección de listado) | Campo para que el viabilizador seleccione de listados predefinidos según los catálogos C1 y C2 a qué características específicas relacionadas a los criterios aporta el proyecto. El Viabilizador podrá seleccionar más de un elemento de los listados en cada catálogo. | Selección (listado) | Selección (listado) | Obligatorio para los criterios seleccionados (RN03). Incluido en "Campos requeridos" | No especificado en el documento. | Editable: No (según el Anexo B, aunque describe una selección activa del Viabilizador; ver Observaciones). Permite selección múltiple (RN08). Los catálogos se identifican como "C1 y C2" en el Anexo B.1 y como "C.1" y "C.2" en el Anexo C. **Nota de reconciliación (v1.3):** el Anexo B.1 del PDF anterior remitía a "C1, C2, C3, C4 y C5"; el PDF de AGOSTO 2026 solo menciona "C1 y C2". Los catálogos C.3 a C.5 se conservan en "Catálogos Detectados" como posible eliminación pendiente de confirmar, dado que la Hoja1 del anexo Excel sigue asociando criterios a ellos. |
| Especificar (sub-formato: texto libre) | Campo para que el Viabilizador registre información específica del criterio seleccionado, identificando en qué aspecto contribuye al criterio. | Texto | Texto | Obligatorio para los criterios seleccionados (RN03). Incluido en "Campos requeridos" | No especificado en el documento. | Editable: Sí (según Anexo B.1). En el mockup corresponde a las preguntas con la indicación "Especifique". |

> Los botones "Guardar", "Emitir Elegibilidad", "Ver comentarios OT", "IR A OT" y "Consulta ODS" (mockup del Anexo A.1, flujos y reglas de negocio) no aparecen en la tabla de formatos del Anexo B.1.

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Especificar (para criterios con "¿Aplica?" seleccionado) | Obligatorio completar la información de "Especificar" para los criterios seleccionados; se valida al dar clic en "Guardar" (RN03). | "Se debe completar la información de la columna 'Especificar' para los criterios seleccionados" |
| Especificar | Debe permitir múltiples selecciones o respuestas en algunas de las preguntas que así lo requieran (RN08); en la Selección (listado), el Viabilizador podrá seleccionar más de un elemento de cada catálogo (Anexo B.1). | No aplica. |
| Emisión de elegibilidad respondiendo a observación de OT | El viabilizador debe haber respondido todos los comentarios de la OT en el campo "Respuesta Institución" antes de poder generar la elegibilidad nuevamente (RN15). | "Es necesario responder los comentarios de la Opinión Técnica previo a la emisión de elegibilidad" |

---

# Errores

| Código | Descripción | Acción esperada |
|---|---|---|
| Sin código en el documento | Criterios seleccionados sin información en la columna "Especificar" al guardar (RN03). | Mostrar el mensaje "Se debe completar la información de la columna 'Especificar' para los criterios seleccionados". |
| Sin código en el documento | Intento de emitir nuevamente la elegibilidad sin haber respondido todos los comentarios de CU-PRE-26 "Opinión Técnica" (RN15). | Mostrar el mensaje "Es necesario responder los comentarios de la Opinión Técnica previo a la emisión de elegibilidad". |

> El documento no incluye una tabla de errores con códigos.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Viabilizador | Único rol que puede registrar información en las columnas "¿Aplica?" y "Especificar". | RN01 |
| Viabilizador | Ver y usar el botón "Guardar" (visible y habilitado únicamente para este rol). | RN05 |
| Viabilizador | Emitir Elegibilidad. | FB1 paso 5; FB2 paso 7; RN04 |
| Viabilizador | Consultar los comentarios de OT mediante "Ver comentarios OT", diligenciar "Respuesta Institución" y oprimir "Guardar Ajustes" en CU-PRE-26. | FB2 pasos 4–6; RN15 |
| Actores de la DGI | Únicamente visualizar todas las secciones de la pantalla de este caso de uso. | RN02 |
| Técnico PRE | Enviar comentarios a la dimensión de la elegibilidad desde CU-PRE-26 "Opinión Técnica" (botón "Enviar comentarios"), lo que habilita nuevamente la pantalla del Anexo A.1; recibir notificaciones de emisión de elegibilidad y de atención de observaciones. | FB2 (pasos 1, 8); FB1 (paso 6); RN09 |
| Técnico URP | Recibir notificación de emisión de elegibilidad; dar clic en "ir a OT" (ver nota de ambigüedad en el Flujo Principal). | FB1 (pasos 6–7) |
| "Técnico de SYMP" (rol no definido en el documento) | Recibir notificación de atención de observaciones a la Elegibilidad. | FB2 (paso 8); ver "Datos Pendientes de Definir" |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-24 "Viabilidad" (precondición; origen de la Opción 2 del FB1 paso 1)
- CU-PRE-26 "Opinión Técnica" (postcondición; origen y destino de comentarios referenciados en FB2 y RN15; formulario para registrar "Respuesta Institución")
- CU-PRE-26.5 "Priorización" (postcondición)
- CU-PRE-32 "Avance Financiero Cuatrimestral del PAP" (postcondición; con comentario de revisión [1])

**Procesos relacionados:**
- Emisión de viabilidad en la fase de perfil (Descripción)
- Emisión de Opinión Técnica (FB1 pasos 6–7)
- Devolución a elegibilidad desde OT (FB2; RN09; RN10)

**Servicios externos:**
- Página web de los Objetivos de Desarrollo Sostenible de Naciones Unidas: https://www.un.org/sustainabledevelopment/es/objetivos-de-desarrollo-sostenible/ (RN06, botón "Consulta ODS").

**Documentos referenciados:**
- Excel con los criterios definidos por la DGI ("Criterios en el Excel", Anexo B.1). No acompaña al PDF de AGOSTO 2026; el anexo Excel transcrito en este documento (`CU-PRE-25__ANEXO__Criterios_de_Priorización_de_proy_de_IP.xlsx`) se entregó junto con el PDF anterior. No se ha confirmado que corresponda a la versión vigente del documento.

---

# Pantallas

## Pantalla: Selección y calificación de Criterios de Elegibilidad (Anexo A.1)

**Descripción:** Pantalla identificada con el título "CRITERIOS DE ELEGIBILIDAD DE PROYECTOS", organizada en dimensiones de criterios, donde el Viabilizador selecciona si cada criterio aplica y especifica la información complementaria correspondiente. **Corrección v1.1:** el mockup del PDF (transcrito abajo, sección "Ejemplo de datos") solo ejemplifica 2 de las 6 dimensiones del catálogo completo; ver la tabla íntegra de 6 dimensiones y 20 criterios en "Catálogos Detectados".

**Campos:**
- Tabla: Dimensiones, Criterio, ¿Aplica?, Especificar

**Botones:**
- Guardar
- Emitir Elegibilidad
- Ver comentarios OT
- Ir a OT
- Consulta ODS (asociado al criterio de ODS, RN06)

**Acciones:**
- Selección de "¿Aplica?" y registro/selección de "Especificar" por criterio (FB1, paso 4).
- Clic en "Guardar" (FA01) y en "Emitir Elegibilidad" (FB1, paso 5).
- Consulta y respuesta a comentarios de OT (FB2, pasos 4–7; RN15).
- Ir a OT (FB1, paso 7).
- Consulta de ODS mediante link externo (RN06).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Dimensiones | Criterio | ¿Aplica? | Especificar |
|-------------|----------|----------|--------------|
| 1. Alineación estratégica | Contribución a metas Objetivos de Desarrollo Sostenible | ○ (No aplica, en el ejemplo) | "¿A cuáles ODS contribuye?" [listado desplegable, sin selección de ejemplo] + botón "Consulta ODS" |
| | Coherencia con Plan de Gobierno | ✓ (Aplica, en el ejemplo) | "¿A cuál eje o pilar del Plan de Gobierno contribuye?" [listado desplegable, sin selección de ejemplo] |
| | Contribución a Planes Regionales | ○ (No aplica, en el ejemplo) | "¿A cuál Plan Regional contribuye? Especifique" [texto libre, sin valor de ejemplo] |
| | Contribución a Planes Sectoriales o Institucionales | ✓ (Aplica, en el ejemplo) | "¿A cuál Plan Institucional o Sectorial contribuye? Especifique" [texto libre, sin valor de ejemplo] |
| 2. Aspectos Medioambientales | Contribución a conservar, restablecer o mejorar el medio ambiente (reforestación, preservación, conservación de especies, energías verdes, mejoramiento de recursos naturales, plantas de tratamiento de agua, entre otros). | ✓ (Aplica, en el ejemplo) | "¿A cuáles componentes del Medio Ambiente contribuye?" [listado desplegable, sin selección de ejemplo] |
| | Apoya el cumplimiento de metas de Gestión de Riesgo de Desastres (GRD). | ○ (No aplica, en el ejemplo) | "¿Qué tipo de medidas para GRD incluye el proyecto?" [listado desplegable, sin selección de ejemplo] |
| | Contribución a la adaptación (prevención) o mitigación (reacción ante daños), en materia de Adaptación al Cambio Climático (ACC). | ✓ (Aplica, en el ejemplo) | "¿El proyecto incluye medidas de adaptación o de mitigación al Cambio Climático?" [listado desplegable, sin selección de ejemplo] |

> **Nota de reconciliación (v1.3):** el mockup del Anexo A.1 del PDF de AGOSTO 2026 muestra únicamente las cuatro filas de "1. Alineación estratégica" (con las mismas selecciones de ejemplo que la tabla anterior). Las tres filas de "2. Aspectos Medioambientales" provienen del mockup del PDF anterior (MAYO 2026) y no aparecen en el nuevo; se conservan como posible eliminación pendiente de confirmar. En el nuevo mockup, la celda "1. Alineación estratégica" abarca las cuatro filas, la palabra "Especifique" aparece en cursiva, los botones "GUARDAR" e "IR A OT" se muestran en azul y "EMITIR ELEGIBILIDAD" y "VER COMENTARIOS OT" en verde claro (el documento no indica si los colores representan estados habilitado/deshabilitado), y no se muestra identificación del proyecto (CUP / nombre) ni el número de devoluciones (RN10).
>
> Nota: el mockup solo ejemplifica las selecciones de la columna "¿Aplica?" (círculos vacíos/marcados); no incluye valores de ejemplo concretos en la columna "Especificar" (los listados desplegables y campos de texto se muestran vacíos).
>
> **Corrección (v1.1):** la nota de la versión 1.0 afirmaba, de forma incorrecta, que "la Hoja1 del anexo Excel confirma la misma estructura de 2 dimensiones y 7 criterios, sin datos de ejemplo adicionales ni filas adicionales". Esta afirmación no reflejaba el contenido real del archivo y se corrige aquí: la Hoja1 del anexo Excel contiene en realidad **6 dimensiones y 20 criterios**, junto con un sistema de ponderación por dimensión y por criterio (total 100 puntos) no presente en el mockup del PDF ni descrito en ningún Flujo o Regla de Negocio de este documento. El mockup del PDF transcrito arriba corresponde únicamente a una vista parcial/de ejemplo de 2 de las 6 dimensiones (numeradas en el catálogo completo como "1. Alineación estratégica" y "4. Aspectos Medioambientales", no como "1." y "2." según se muestran en el mockup). Ver la tabla completa en "Catálogos Detectados" y la discusión sobre su posible correspondencia con CU-PRE-26.5 "Priorización" en "Observaciones".

## Pantalla: Guardar (Anexo A.2)

**Descripción:** Ventana modal de confirmación mostrada al guardar información.

**Campos:** No aplica (mensaje informativo).

**Botones:**
- Aceptar

**Acciones:**
- Al dar clic en "Aceptar", el Sistema guarda la información registrada y se mantiene en la pantalla "Elegibilidad" (FA01 pasos 1.3–1.4).

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**
- Ícono de confirmación (check).
- Texto: "¡Guardado!"
- Texto: "Sus datos han sido guardados exitosamente."
- Botón: "Aceptar" (azul claro)

> En el mockup, la palabra "datos" se muestra con una tipografía distinta al resto del texto.

## Pantalla: Mensaje de "Emisión de Elegibilidad" (Anexo A.3)

**Descripción:** Ventana modal de confirmación mostrada al emitir elegibilidad.

**Campos:** No aplica (mensaje informativo).

**Botones:**
- Aceptar
- Ir a Opinión Técnica

**Acciones:**
- No especificado en el documento (el documento no describe el efecto de cada botón del mensaje). Ver Observaciones.

**Ejemplo de datos mostrados en el mockup (Anexo A.3):**
- Ícono de confirmación (check).
- Texto: "¡Enviado!" (aparece dos veces en la ventana: como encabezado y debajo del ícono).
- Texto: "La Elegibilidad del proyecto ha sido emitida con éxito. Puede contiuar con la gestión de la Opinión Técnica" (transcripción literal, incluyendo la probable errata "contiuar" por "continuar").
- Botones: "ACEPTAR" (verde), "IR A OPINIÓN TÉCNICA" (verde)

> Nota: este texto difiere levemente del mensaje descrito en RN04 ("La Elegibilidad ha sido emitida con éxito. Puede continuar con la gestión de Opinión Técnica"). Ver Observaciones.

## Pantalla: Captura de proyectos (sin mockup en el documento)

**Descripción:** Pantalla con la lista de los proyectos de la entidad en el estado "Proyecto viable" (FB1 paso 1, Opción 1), desde la cual el Viabilizador da clic en el proyecto sobre el cual va a emitir elegibilidad (FB1 paso 2).

**Campos:** No especificado en el documento.

**Botones:** No especificado en el documento.

**Acciones:** Selección del proyecto (FB1 paso 2).

## Pantalla: Formulario de CU-PRE-26 "Opinión Técnica" en ventana emergente (pantalla de otro caso de uso)

**Descripción:** Formulario mostrado en ventana emergente al dar clic en "Ver comentarios OT" (FB2 paso 5), en el que el Viabilizador registra la respuesta en el campo "Respuesta Institución" y oprime "Guardar Ajustes" (FB2 paso 6; RN15). Pertenece a CU-PRE-26; no se describe en este documento.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Error de validación | "Se debe completar la información de la columna 'Especificar' para los criterios seleccionados" | Al dar clic en "Guardar" si hay criterios con "¿Aplica?" seleccionado sin completar "Especificar" (RN03). Sin Anexo/mockup propio asociado. |
| Confirmación (Guardar) | "¡Guardado! Sus datos han sido guardados exitosamente." | Al dar clic en "Guardar" (FA01 paso 1.2; Anexo A.2). |
| Confirmación (Emitir Elegibilidad) | "¡Enviado! La Elegibilidad del proyecto ha sido emitida con éxito. Puede contiuar con la gestión de la Opinión Técnica" (mockup, Anexo A.3) / "La Elegibilidad ha sido emitida con éxito. Puede continuar con la gestión de Opinión Técnica" (RN04) | Al dar clic en "Emitir Elegibilidad" (FB1, paso 6). Los textos de RN04 y del mockup difieren (ver Observaciones). |
| Notificación | Notificación al Técnico URP y al Técnico PRE (texto exacto no especificado). | Al emitir la elegibilidad (FB1 paso 6). |
| Notificación | Notificación al Viabilizador de que se emitieron comentarios (texto exacto no especificado). | Cuando el Técnico PRE envía comentarios desde CU-PRE-26 (FB2 paso 2). |
| Notificación | Notificación al Técnico PRE y al "Técnico de SYMP" de que fueron atendidas las observaciones a la Elegibilidad (texto exacto no especificado). | Tras emitir nuevamente la elegibilidad (FB2 paso 8). |
| Error de validación | "Es necesario responder los comentarios de la Opinión Técnica previo a la emisión de elegibilidad" | Cuando el Viabilizador intenta emitir elegibilidad nuevamente respondiendo a una observación de OT sin haber respondido todos los comentarios en "Respuesta Institución" (RN15). Sin Anexo/mockup propio asociado. |
| Indicador en pantalla | Número de veces que se ha devuelto el proyecto a elegibilidad (presentación no especificada). | RN10. |
| Confirmación (origen incierto) | "¡Enviado! El proyecto fue enviado a Opinión Técnica exitosamente" | No especificado en el documento PDF; este texto se encontró en una celda aislada al final de la Hoja2 del anexo Excel "Criterios de Priorización de proy. de IP", sin un paso de flujo asociado explícito en el PDF. No aparece en el PDF de AGOSTO 2026. Ver Observaciones. |

---

# Observaciones

- Existe una discrepancia entre los encabezados de página del documento PDF, que indican "Fecha: AGOSTO 2026", y la tabla "Historial de Revisiones", que indica "AGO 2025" para la misma versión 1.0. **(v1.3)** En el PDF anterior la discrepancia era "MAYO 2026" vs. "JUN 2025"; ninguno de los dos registra una revisión posterior a la 1.0.
- RN07 y RN09 comparten la misma cláusula inicial ("El Sistema bloqueará la pantalla del Anexo A.1 una vez el proyecto se envíe a CU-PRE-26 'Opinión Técnica' por primera vez"), siendo RN09 una versión ampliada de RN07 que agrega la condición de reactivación. Podría tratarse de una regla parcialmente duplicada.
- El mensaje descrito en RN04 ("La Elegibilidad ha sido emitida con éxito. Puede continuar con la gestión de Opinión Técnica") difiere levemente del texto mostrado en el mockup del Anexo A.3 ("La Elegibilidad del proyecto ha sido emitida con éxito. Puede contiuar con la gestión de la Opinión Técnica", que incluye la probable errata "contiuar"). Se transcriben ambos textos tal como aparecen, sin unificarlos ni corregir la errata.
- El Anexo B describe tres sub-formatos posibles para la columna "Especificar" (selección radial "Sí"/"No", selección de listado por catálogo, y texto libre). **Corrección (v1.1):** la versión 1.0 afirmaba que "ninguno de los 7 criterios... utiliza el sub-formato de selección radial Sí/No", basada únicamente en los 7 criterios visibles en el mockup del PDF. Con la tabla completa de 20 criterios (Hoja1 del anexo Excel) ahora incorporada, se confirma que sí existen criterios que usan el sub-formato "Sí"/"No": "Indicadores de evaluación... indican la rentabilidad social", los 3 criterios de la dimensión "Sostenibilidad fiscal", y los 3 criterios de la dimensión "Madurez del Proyecto" (7 de los 20 criterios en total). Los criterios restantes usan selección de listado (catálogos C.1 a C.5 y los dos catálogos adicionales de Hoja2) o texto libre (Planes Regionales, Planes Sectoriales/Institucionales).
- El campo "¿Aplica?" se documenta en el Anexo B con la columna "Editable" en "No"; sin embargo, su Detalle lo describe como un campo que el Viabilizador "selecciona" mediante botón radial, y el mockup del Anexo A.1 lo muestra como un control interactivo con selecciones de ejemplo distintas por fila (patrón similar al observado en otros casos de uso de la serie, p. ej. CU-PRE-20 y CU-PRE-23). **(v1.3)** La misma contradicción aplica a "Especificar (Selección radial)" y "Especificar (Selección listado)", que figuran con "Editable: No" pese a que su Detalle los describe como campos que el Viabilizador diligencia y RN01 le asigna el registro en esa columna. Solo "Especificar (Texto)" figura con "Editable: Sí".
- El anexo Excel "Criterios de Priorización" (Hoja2) incluye dos catálogos adicionales — "Catálogo de grupos poblacionales en situación de vulnerabilidad" (10 elementos) y "Catálogo de mejoras en calidad de vida" (5 elementos) — que **(corrección v1.1, ya resuelto)** corresponden a los dos criterios de la dimensión "2. Aspectos Sociales" de la tabla completa (Hoja1), no documentada en el mockup del PDF (que solo ejemplifica las dimensiones "1. Alineación estratégica" y "4. Aspectos Medioambientales").
- **Nueva observación (v1.1) — posible correspondencia con otro caso de uso:** el nombre externo del archivo Excel es `CU-PRE-25__ANEXO__Criterios_de_Priorización_de_proy_de_IP.xlsx` ("Priorización"), mientras que el título interno de la Hoja1 es "CRITERIOS DE ELEGIBILIDAD DE PROYECTOS" (coincide con el nombre de este CU). El sistema de ponderación por dimensión/criterio y la "Calificación (Prioridad)" total de 100 puntos no se mencionan en ningún Flujo Básico, Flujo Alternativo o Regla de Negocio de este documento (CU-PRE-25), que solo describe una selección binaria "¿Aplica?"/"Especificar" sin cálculo de puntaje. Este documento lista como caso de uso relacionado (postcondición) a **CU-PRE-26.5 "Priorización"**, cuyo nombre coincide con el del archivo Excel. No se puede determinar, con la información disponible, si la tabla de ponderación transcrita en "Catálogos Detectados" pertenece funcionalmente a CU-PRE-25 "Elegibilidad" (como sugiere el título interno de la hoja y el nombre del archivo, que lo vincula explícitamente a este CU), a CU-PRE-26.5 "Priorización" (como sugiere el nombre externo del archivo y la naturaleza de sistema de puntaje/priorización), o a ambos. Se transcribe íntegra en este documento por haber sido aportada como anexo de CU-PRE-25, sin asumir cuál de las dos asignaciones es la correcta.
- **Verificación cruzada (v1.1):** se revisó el documento `UC-PRE-26_5-Priorizacion.md` (CU-PRE-26.5 "Priorización"), que cuenta con su propio anexo Excel independiente (`CU-PRE-26_5__ANEXO__Criterios_de_Priorización.xlsx`, archivo distinto del de este documento) y su propio catálogo de criterios y subcriterios de priorización, ya confirmado contra el mockup de su propio PDF: **4 Criterios** (Madurez Técnica y Cumplimiento Regulatorio, Rentabilidad Social o Valor Público, Impacto Territorial y Cierre de Brechas, Sostenibilidad Fiscal y Financiera) con 13 a 18 subcriterios ponderados en **porcentajes** (dentro de cada criterio). Esta estructura **no coincide** con la tabla de 6 dimensiones/20 criterios ponderados en **puntos** (total 100) del anexo Excel de este documento (CU-PRE-25) — ni en nombres de categorías, ni en cantidad de elementos, ni en el mecanismo de ponderación (porcentaje relativo dentro de un criterio vs. puntos absolutos sobre 100). Esta comparación **debilita, pero no descarta**, la hipótesis de que la tabla de este documento pertenezca a CU-PRE-26.5: es posible que se trate de un catálogo genuinamente distinto y propio de CU-PRE-25 (a pesar del nombre "Priorización" del archivo), de una versión descartada o preliminar de la matriz de CU-PRE-26.5, o de un catálogo sin relación directa con ninguno de los dos casos de uso. No se resuelve esta pregunta; se deja constancia de la comparación para que el Gestor del Dominio decida.
- El anexo Excel incluye, en una celda aislada al final de la Hoja2, el texto "¡Enviado! El proyecto fue enviado a Opinión Técnica exitosamente", que parece corresponder a un mensaje de confirmación no descrito explícitamente en ningún paso del Flujo Básico o los Flujos Alternativos del documento PDF.
- El campo "Respuesta Institución" mencionado en RN15 no coincide con el nombre "Justificación Institución" usado para un concepto aparentemente equivalente en el caso de uso CU-PRE-24 "Viabilidad" (RN11 de dicho documento).
- El paso 8 del Flujo Básico 2 menciona la notificación a un actor denominado "Técnico de SYMP", el cual no aparece en el campo "Actores" de la Identificación del caso de uso, ni se define en ninguna otra parte del documento.
- **(v1.3) Reducción de alcance entre versiones del PDF:** respecto al PDF anterior (MAYO 2026), el PDF de AGOSTO 2026 muestra en el mockup del Anexo A.1 una sola dimensión ("1. Alineación estratégica", 4 criterios) en lugar de dos (7 criterios), su Anexo C contiene solo los catálogos C.1 y C.2 (antes C.1 a C.5), su Anexo B.1 remite a "C1 y C2" (antes "C1, C2, C3, C4 y C5") y su FB2 se refiere a "la dimensión" / "(Dimensión 1)" (antes "las dos dimensiones" / "(Dimensiones 1 y 2)"). El Anexo B.1 sigue remitiendo a "Criterios en el Excel". No se puede determinar si el recorte es intencional (p. ej. un mockup reducido a modo de ejemplo) o una eliminación de alcance; el contenido anterior se conserva hasta que se confirme.
- **(v1.3) Comentarios de revisión sin resolver en el documento:** el PDF de AGOSTO 2026 conserva dos comentarios de revisión. El comentario [1], anclado a la postcondición "CU-PRE-32 Avance Financiero Cuatrimestral del PAP", indica que el caso de uso fue quitado de una versión anterior y que "si es necesario"; el comentario [2], anclado a "Anexo A.1." en FB1 paso 2, plantea unificar las pantallas de "Viabilidad" y "Elegibilidad" en un solo paso. El documento no indica si estos comentarios fueron atendidos.
- **(v1.3) Postcondición CU-PRE-32:** "CU-PRE-32 Avance Financiero Cuatrimestral del PAP" figura como postcondición, pero no se menciona en ningún flujo ni regla de negocio, y está sujeta al comentario de revisión [1].
- **(v1.3) "Una sola vez" vs. devoluciones:** la Descripción indica que "Este filtro de aprobación se realiza una sola vez en cada proyecto", mientras que RN10 indica que "El proyecto se puede devolver a elegibilidad cuantas veces sea necesario" y el FB2 describe la nueva emisión de elegibilidad tras comentarios de OT. El documento no aclara la relación entre ambas afirmaciones.
- **(v1.3) Secuencia de opciones en el FB1:** la Opción 2 del paso 1 indica "(Continúa con el paso 4)", pero el paso 3 está rotulado como "Opción 2".
- **(v1.3) Momento del bloqueo de la pantalla:** el FB1 paso 6 indica que, al emitir la elegibilidad, "la pantalla 'Elegibilidad' queda deshabilitada", y RN04 bloquea las columnas "¿Aplica?" y "Especificar" al dar clic en "Emitir Elegibilidad". En cambio, RN07 y RN09 indican que el Sistema bloqueará la pantalla del Anexo A.1 "una vez el proyecto se envíe a CU-PRE-26 'Opinión Técnica' por primera vez".
- **(v1.3) Doble nomenclatura del botón hacia Opinión Técnica:** "ir a OT" (FB1 paso 7), "IR A OT" (mockup A.1) e "IR A OPINIÓN TÉCNICA" (mockup A.3). El FB1 paso 6 menciona además que el Sistema "habilita el formulario para emitir OT". El mockup del Anexo A.1 muestra el botón "IR A OT" en la pantalla del Viabilizador, mientras que el FB1 paso 7 asigna el clic al Técnico URP.
- **(v1.3) Doble nomenclatura de la pantalla:** "Anexo A.1" / "A.1- Selección y calificación de Criterios de Elegibilidad" (título del anexo), "CRITERIOS DE ELEGIBILIDAD DE PROYECTOS" (título del mockup), "Criterios de Elegibilidad Proyectos" (Anexo B.1), "Criterios de Elegibilidad del proyecto" (FB1 paso 3), "Criterios de Elegibilidad del Proyecto" (FB2 paso 2), pantalla "Elegibilidad" (FB1 paso 6; FA01 paso 1.4) y "formulario de elegibilidad" (FB1 paso 1).
- **(v1.3) Doble nomenclatura de columnas/campos:** "¿Aplica?" (Campos requeridos, RN01, RN04, Anexo B.1, mockup) vs. "Aplica" (FB1 paso 4); "Dimensión" (Campos requeridos) vs. "Dimensiones" (Anexo B.1 y mockup).
- **(v1.3) Acciones de los botones del mensaje A.3 no descritas:** el documento no describe qué ocurre al dar clic en "ACEPTAR" ni en "IR A OPINIÓN TÉCNICA".
- **(v1.3) Asociación de catálogos con criterios:** el Anexo B.1 del PDF de AGOSTO 2026 remite a los "catálogos C1 y C2" para la Selección (listado). Según el mockup, C.1 corresponde a "¿A cuáles ODS contribuye?" y C.2 a "¿A cuál eje o pilar del Plan de Gobierno contribuye?". Las preguntas de Planes Regionales y Planes Sectoriales o Institucionales se muestran como "Especifique" (texto), sin catálogo. Los criterios de las demás dimensiones de la Hoja1 del anexo Excel que usan listados (C.3 a C.5 y los dos catálogos de Aspectos Sociales) no tienen catálogo referenciado en el Anexo B.1 del nuevo PDF.
- **(v1.3) Validación de RN03 no reflejada en el FA01:** el FA01 muestra directamente el mensaje "¡Guardado!" (Anexo A.2) sin contemplar la validación de RN03. Además, el mensaje se muestra (paso 1.2) antes de que el Sistema guarde la información (paso 1.4).
- **(v1.3) Orden de pasos en el FB2:** el Viabilizador ajusta la información (paso 3) antes de consultar los comentarios de OT (paso 4).
- **(v1.3) "Ventana emergente" vs. remisión a CU-PRE-26:** el FB2 paso 5 indica que el formulario de CU-PRE-26 se muestra "en una ventana emergente", mientras que RN15 indica que el sistema "debe remitirlo a los comentarios de CU-PRE-26".
- **(v1.3) RN10 sin soporte en pantalla:** la regla exige consultar en pantalla las observaciones de cada devolución y mostrar el número de devoluciones, pero el mockup del Anexo A.1 y el Anexo B.1 no incluyen elementos para ello.
- **(v1.3) Botones no documentados en el Anexo B.1:** "Guardar", "Emitir Elegibilidad", "Ver comentarios OT", "IR A OT" y "Consulta ODS" aparecen en el mockup y/o en flujos y reglas, pero no en la tabla de formatos.
- **(v1.3) Otras inconsistencias con CU-PRE-24 "Viabilidad"** (además de "Respuesta Institución" vs. "Justificación Institución", señalada arriba):
    - El estado previo se denomina "Proyecto Viable" en CU-PRE-24 (FA02 paso 2.3) y "Proyecto viable" en CU-PRE-25 (FB1 paso 1).
    - En CU-PRE-24, el botón "Ir a OT" lo usa el Viabilizador (FA02 paso 2.7) y RN07 lo habilita solo para ese rol; en CU-PRE-25, "ir a OT" lo usa el Técnico URP (FB1 paso 7).
    - En CU-PRE-24 (FA02 paso 2.4), el paso a CU-PRE-25 ocurre si el Viabilizador selecciona "Ir a Elegibilidad"; en CU-PRE-25 (FB1 paso 1, Opción 2), "Luego de emitir viabilidad el sistema lo lleva al formulario de elegibilidad", sin mencionar la selección del botón.
    - CU-PRE-24 lista como postcondiciones CU-PRE-25, CU-PRE-26 y CU-PRE-26.5; CU-PRE-25 lista CU-PRE-26, CU-PRE-26.5 y CU-PRE-32.
    - RN15 de CU-PRE-25 reproduce la estructura de RN11 de CU-PRE-24 (validación de respuesta a comentarios de OT y botón "Ver comentarios OT"), con adaptaciones de actor y de nombre de campo. CU-PRE-24 indica que el botón está "ubicado en la parte superior del Anexo A.1"; CU-PRE-25, "ubicado en el Anexo A.1" (el mockup lo ubica en la parte inferior).
    - Ambos documentos presentan el mismo patrón de fecha: encabezados de 2026 (MAYO 2026 en CU-PRE-24; AGOSTO 2026 en CU-PRE-25) frente a una única entrada del Historial de AGO 2025.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Proyecto | Proyecto sobre el cual se emite elegibilidad. | Consulta en "Captura de proyectos" (FB1 pasos 1–2); cambio de estado a "Proyecto Elegible" (FB1 paso 6); devolución a elegibilidad cuantas veces sea necesario (RN10). |
| Criterio de Elegibilidad | Criterio predefinido por la DGI, agrupado por dimensión, sobre el cual el Viabilizador califica si el proyecto contribuye. | Selección/calificación mediante "¿Aplica?" y "Especificar" (RN01, FB1 paso 4); validación de "Especificar" obligatorio si se marca "Aplica" (RN03); selección múltiple permitida en algunos catálogos (RN08). |
| Dimensión | Agrupación de criterios de elegibilidad. **Corrección v1.1:** el catálogo completo (Hoja1 del anexo Excel) tiene 6 dimensiones (Alineación estratégica, Aspectos Sociales, Rentabilidad social, Aspectos Medioambientales, Sostenibilidad fiscal, Madurez del Proyecto); el mockup del PDF solo ejemplifica 2 de ellas. | Visualización (Anexo A.1); ajuste de información por dimensión tras comentarios de OT (FB2, paso 3). |
| Respuesta ¿Aplica? | Selección de los criterios a los que contribuye el proyecto. | Registro (FB1 paso 4; RN01); guardado (FA01); bloqueo al emitir (RN04); ajuste tras comentarios de OT (FB2 paso 3; RN09). |
| Información "Especificar" | Información complementaria de cada criterio seleccionado (Sí/No, listados de catálogos, texto). | Registro o selección, con selección múltiple (FB1 paso 4; RN08); validación de obligatoriedad al guardar (RN03); guardado (FA01); bloqueo al emitir (RN04); ajuste (FB2 paso 3). |
| Elegibilidad (estado del proyecto) | Estado del proyecto relacionado con el proceso de calificación de criterios de elegibilidad ("Proyecto Elegible"). | Emisión (FB1, pasos 5–6); cambio de estado a "Proyecto Elegible" (FB1, paso 6); bloqueo/desbloqueo de pantalla (RN07, RN09); devolución (RN10, ver "Datos Pendientes de Definir"). |
| Comentario de Opinión Técnica sobre Elegibilidad / Respuesta Institución | Observación emitida por el Técnico PRE, a través de CU-PRE-26 "Opinión Técnica", sobre la información de elegibilidad registrada, y respuesta del viabilizador en el campo "Respuesta Institución". | Envío por el Técnico PRE (FB2, paso 1); consulta y respuesta por el Viabilizador mediante "Ver comentarios OT" y "Guardar Ajustes" (FB2, pasos 4–6; RN15); validación de respuesta completa antes de emitir elegibilidad nuevamente (RN15); guardado en cada devolución y consulta (RN10). |
| Devoluciones a elegibilidad | Cada devolución del proyecto a elegibilidad con los comentarios emitidos en OT. | Guardado de comentarios por devolución, consulta en pantalla y conteo de devoluciones (RN10). |
| Notificación | Avisos enviados por el Sistema. | Envío al Técnico URP y al Técnico PRE (FB1 paso 6); al Viabilizador (FB2 paso 2); al Técnico PRE y al "Técnico de SYMP" (FB2 paso 8). |
| Ponderación / Calificación de Prioridad (identificada en el anexo Excel, Hoja1; no descrita en el PDF) | Puntaje asignado a cada criterio ("Ponderación por Criterio") y a cada dimensión ("Ponderación por Dimensión"), que totaliza 100 puntos ("Total Calificación (Prioridad)"). No se describe en ningún Flujo ni Regla de Negocio de este documento cómo, cuándo o por quién se calcula o aplica este puntaje. | No especificado en el documento ni en el anexo Excel más allá de los valores fijos de ponderación (ver "Catálogos Detectados" y "Datos Pendientes de Definir"). |

---

# Catálogos Detectados

## Tabla completa de Dimensiones, Criterios y Ponderación (Hoja1 del anexo Excel)

> **Corrección (v1.1):** esta tabla no estaba presente en la versión 1.0 de este documento, que solo transcribía los 7 criterios de 2 dimensiones visibles en el mockup del PDF (Anexo A.1) y afirmaba, incorrectamente, que la Hoja1 del anexo Excel "confirma la misma estructura". Al inspeccionar íntegramente la hoja "Hoja1" (título interno "CRITERIOS DE ELEGIBILIDAD DE PROYECTOS" / "CALIFICACIÓN DE CRITERIOS"), se confirma que contiene **6 dimensiones y 20 criterios**, cada uno con una "Ponderación por Dimensión" y una "Ponderación por Criterio", cuya suma total es 100 ("Total Calificación (Prioridad)"). Este sistema de ponderación no está descrito en ningún Flujo Básico, Flujo Alternativo o Regla de Negocio (RN01–RN15) de este documento — ni el término "ponderación" ni "calificación de prioridad" aparecen en ninguna otra parte del PDF. Ver "Observaciones" y "Datos Pendientes de Definir" respecto a la posible correspondencia de esta tabla con CU-PRE-26.5 "Priorización" en lugar de (o además de) este caso de uso.

| Dimensiones | Criterio | Especificar (pregunta guía) | Ponderación por Dimensión | Ponderación por Criterio |
|---|---|---|---|---|
| **1. Alineación estratégica** | Contribución a metas Objetivos de Desarrollo Sostenible | ¿A cuáles ODS contribuye? | 10 | 2 |
|  | Coherencia con Plan de Gobierno | ¿A cuál eje o pilar del Plan de Gobierno contribuye? |  | 5 |
|  | Contribución a Planes Regionales | ¿A cuál Plan Regional contribuye? Especifique |  | 1 |
|  | Contribución a Planes Sectoriales o Institucionales | ¿A cuál Plan Institucional o Sectorial contribuye? Especifique |  | 2 |
| **2. Aspectos Sociales** | Beneficia a grupos poblacionales en situación de vulnerabilidad: en riesgo ambiental, discapacitados, tercera edad, mujeres cabeza de hogar, primera infancia, otros. | ¿A cuáles grupos en situación de vulnerabilidad contribuye? | 20 | 10 |
|  | Contribución a mejorar la calidad de vida (mayor disponibilidad y calidad de servicios, generación de empleo productivo, seguridad) | ¿Cómo el proyecto contribuye a mejorar la calidad de vida? |  | 10 |
| **3. Rentabilidad social** | Indicadores de evaluación (VAN, TIR, B/C, CAE, VAC) indican la rentabilidad social | ¿El proyecto demuestra rentabilidad social? (Sí / No) | 20 | 20 |
| **4. Aspectos Medioambientales** | Contribución a conservar, restablecer o mejorar el medio ambiente (reforestación, preservación, conservación de especies, energías verdes, mejoramiento de recursos naturales, plantas de tratamiento de agua, entre otros). | ¿A cuáles componentes del Medio Ambiente contribuye? | 15 | 5 |
|  | Apoya el cumplimiento de metas de Gestión de Riesgo de Desastres (GRD). | ¿Qué tipo de medidas para GRD incluye el proyecto? |  | 5 |
|  | Contribución a la adaptación (prevención) o mitigación (reacción ante daños), en materia de Adaptación al Cambio Climático (ACC). | ¿El proyecto incluye medidas de adaptación o de mitigación al Cambio Climático? |  | 5 |
| **5. Sostenibilidad fiscal** | Cuenta con financiamiento autorizado | ¿Cuenta con financiamiento autorizado? (Sí / No) | 15 | 6 |
|  | Se encuentra en gestión de financiamiento | ¿Se encuentra en gestión el financiamiento? (Sí / No) |  | 3 |
|  | Cuenta con apoyo de las autoridades Institucionales para asignar recursos, dentro del techo de funcionamiento, para su operación | ¿Cuenta con apoyo de las autoridades Institucionales para asignar recursos para su operación y mantenimiento? (Sí / No) |  | 6 |
| **6. Madurez del Proyecto** | Proyecto en ejecución (arrastre) | ¿Es proyecto de arrastre? (Sí / No) | 20 | 10 |
|  | Cuenta con estudios de preinversión | ¿Cuenta con estudios de preinversión para ejecutar? (Sí / No) |  | 5 |
|  | Capacidad institución ejecutora | ¿La institución ejecutora demuestra buena capacidad de ejecución histórica? (Sí / No) |  | 5 |
| | | **Total Calificación (Prioridad)** | | **100** |

> Nota: la columna "Especificar (pregunta guía)" corresponde a la columna "Especificar" de la Hoja1, que contiene el texto de la pregunta mostrada al Viabilizador para cada criterio (equivalente al mockup del PDF). Para los criterios cuya pregunta guía en el archivo original incluye las palabras "Sí" / "No" en líneas separadas (con saltos de línea), se transcribe de forma abreviada como "(Sí / No)"; el texto original de cada celda incluye saltos de línea adicionales entre la pregunta y las opciones "Sí"/"No" que no se reproducen aquí por razones de formato de tabla, sin alterar el contenido textual.

## C.1 – Catálogo ¿A cuáles ODS contribuye?

| N° | ODS |
|----|-----|
| 1 | Fin de la pobreza |
| 2 | Hambre cero |
| 3 | Salud y bienestar |
| 4 | Educación de calidad |
| 5 | Igualdad de género |
| 6 | Agua limpia y saneamiento |
| 7 | Energía asequible y no contaminante |
| 8 | Trabajo decente y crecimiento económico |
| 9 | Industria, innovación e infraestructura |
| 10 | Reducción de las desigualdades |
| 11 | Ciudades y comunidades sostenibles |
| 12 | Producción y consumo responsables |
| 13 | Acción por el clima |
| 14 | Vida submarina |
| 15 | Vida de ecosistemas terrestres |
| 16 | Paz, justicia e instituciones sólidas |
| 17 | Alianzas para lograr los objetivos |

## C.2 – Catálogo ¿A cuál eje o pilar del Plan de Gobierno contribuye?

| Eje |
|-----|
| Eje 1: Carreteras |
| Eje 2: Transporte |
| Eje 3: Infraestructura de salud y educación |
| Eje 4: Puertos, aeropuertos y aduanas |
| Eje 5: Agua potable y saneamiento |
| Eje 6: Vivienda y desarrollo urbano |
| Eje 7: Infraestructura penitenciaria |
| Eje 8: Asocios públicos-privados |
| Eje 9: Energía |

> **Nota de reconciliación (v1.3):** los catálogos C.3, C.4 y C.5 figuran en el Anexo C del PDF anterior (MAYO 2026), pero no en el PDF de AGOSTO 2026, cuyo Anexo C solo contiene C.1 y C.2. Se conservan como posible eliminación pendiente de confirmar: la Hoja1 del anexo Excel sigue asociando criterios de la dimensión "4. Aspectos Medioambientales" a estas preguntas.

## C.3 – Catálogo ¿A cuáles componentes del Medio Ambiente contribuye?

| N° | Componente |
|----|------------|
| 1 | Medio Físico (agua, aire, suelos, otros) |
| 2 | Medio Biológico (flora, fauna, ecosistemas, áreas naturales protegidas) |

## C.4 – Catálogo ¿Qué tipo de medidas para GRD incluye el proyecto?

| N° | Medida |
|----|--------|
| 1 | Reducción del riesgo existente |
| 2 | Prospectivos para evitar nuevos riesgos y generación de conocimiento |
| 3 | Preparación |
| 4 | Respuesta y recuperación |

## C.5 – Catálogo ¿El proyecto incluye medidas de adaptación o de mitigación al Cambio Climático?

| N° | Medida |
|----|--------|
| 1 | Adaptación |
| 2 | Mitigación |

## Catálogos adicionales encontrados únicamente en el anexo Excel (Hoja2)

> **Corrección (v1.1):** la versión 1.0 de este documento indicaba que estos dos catálogos "no corresponden a ninguno de los 7 criterios del Anexo A.1" y dejaba abierta la pregunta de si correspondían a una tercera dimensión no documentada, sugiriendo como ejemplo "Aspectos Sociales". Con la tabla completa de la Hoja1 ahora incorporada (ver arriba), se confirma que ambos catálogos corresponden efectivamente a los dos criterios de la dimensión **"2. Aspectos Sociales"**: "Catálogo de grupos poblacionales en situación de vulnerabilidad" corresponde al criterio "Beneficia a grupos poblacionales en situación de vulnerabilidad...", y "Catálogo de mejoras en calidad de vida" corresponde al criterio "Contribución a mejorar la calidad de vida...". Se transcriben íntegramente a continuación, igual que en la versión 1.0 (sin cambios en su contenido).

### Catálogo de grupos poblacionales en situación de vulnerabilidad

| N° | Grupo poblacional |
|----|----------------------|
| 1 | Personas en riesgo ambiental |
| 2 | Personas con discapacidad |
| 3 | Tercera edad |
| 4 | Mujeres cabeza de hogar |
| 5 | Primera infancia |
| 6 | Personas retornadas |
| 7 | Jóvenes vulnerables |
| 8 | Desempleados |
| 9 | Niñez y adolescencia |
| 10 | Personas reinsertadas |

### Catálogo de mejoras en calidad de vida

| N° | Mejora |
|----|--------|
| 1 | Mayor disponibilidad de servicios (salud, educación, agua, energía, transporte, entre otros) |
| 2 | Mejoras en la calidad de servicios (salud, educación, agua, energía, transporte, entre otros) |
| 3 | Generación de empleo productivo |
| 4 | Seguridad |
| 5 | Acceso a vivienda |

## Otros catálogos

| Catálogo | Valores conocidos |
|---|---|
| Dimensiones y Criterios de elegibilidad según el mockup del Anexo A.1 del PDF de AGOSTO 2026 | Dimensión "1. Alineación estratégica": Contribución a metas Objetivos de Desarrollo Sostenible; Coherencia con Plan de Gobierno; Contribución a Planes Regionales; Contribución a Planes Sectoriales o Institucionales. *Subconjunto de la tabla completa de la Hoja1 del anexo Excel (ver arriba).* |
| Opciones de "Especificar" (Selección radial) | Sí; No (Anexo B.1). |
| Estados del proyecto (valores mencionados en el documento) | Proyecto viable (FB1 paso 1); Proyecto Elegible (FB1 paso 6). *El documento no define un catálogo completo de estados.* |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Emisión de viabilidad → el sistema lleva al Viabilizador al formulario de elegibilidad | CU-PRE-24 "Viabilidad" | Anexo A.1 (FB1 paso 1, Opción 2) |
| Selección de proyecto en "Captura de proyectos" → despliegue del formulario "Criterios de Elegibilidad del Proyecto" | Viabilizador | Sistema / Anexo A.1 (FB1 pasos 2–3) |
| Clic en "Guardar" → validación de "Especificar" | Viabilizador | Sistema (RN03) |
| Clic en "Guardar" → mensaje del Anexo A.2 y guardado | Viabilizador | Sistema (FA01 pasos 1.1–1.4) |
| Clic en "Emitir Elegibilidad" → bloqueo de "¿Aplica?" y "Especificar" y mensaje del Anexo A.3 | Viabilizador | Sistema (FB1 paso 6; RN04) |
| Elegibilidad emitida → estado "Proyecto Elegible", notificación al Técnico URP y al Técnico PRE, habilitación del formulario para emitir OT y deshabilitación de la pantalla "Elegibilidad" | Sistema (FB1 paso 6) | Proyecto / Técnico URP / Técnico PRE / CU-PRE-26 |
| Clic en "ir a OT" | Técnico URP | CU-PRE-26 "Opinión Técnica" (FB1 paso 7) |
| Primer envío del proyecto a CU-PRE-26 → bloqueo de la pantalla del Anexo A.1 | Sistema | Anexo A.1 (RN07; RN09) |
| Clic en "Enviar comentarios" en CU-PRE-26 → notificación y rehabilitación de la pantalla del Anexo A.1 | Técnico PRE (CU-PRE-26) | Sistema / Viabilizador (FB2 pasos 1–2; RN09) |
| Clic en "Ver comentarios OT" → formulario de CU-PRE-26 en ventana emergente | Viabilizador | Sistema (FB2 pasos 4–5; RN15) |
| Nueva emisión de elegibilidad → validación de respuestas en CU-PRE-26 | Viabilizador | Sistema (FB2 paso 7; RN15) |
| Observaciones atendidas → notificación | Sistema (FB2 paso 8) | Técnico PRE y "Técnico de SYMP" |
| Clic en "Consulta ODS" → link externo de ODS | Viabilizador | Sitio de Naciones Unidas (RN06) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|--------------|
| Sitio web de Naciones Unidas (ODS) | Enlace externo | Botón "Consulta ODS" que dirige a https://www.un.org/sustainabledevelopment/es/objetivos-de-desarrollo-sostenible/ (RN06). |

---

# Datos Pendientes de Definir

- RN10 exige que el sistema muestre cuántas veces se ha devuelto el proyecto a elegibilidad y permita consultar en pantalla las observaciones de cada devolución; ni el mockup del Anexo A.1 ni la tabla de formatos del Anexo B muestran o describen un contador de devoluciones ni una vista de historial de observaciones por devolución. No se puede determinar cómo debe implementarse esta funcionalidad.
- La numeración de las Reglas de Negocio salta de RN10 a RN15, sin que existan las reglas RN11, RN12, RN13 y RN14 en el documento. No se puede determinar si estas reglas fueron removidas intencionalmente, corresponden a otro documento, o existe un error de numeración.
- **Resuelto (v1.1):** el anexo Excel "Criterios de Priorización de proy. de IP" (antes referido como "Criterios de Elegibilidad" por error de nombre de archivo) incluye dos catálogos — "Catálogo de grupos poblacionales en situación de vulnerabilidad" y "Catálogo de mejoras en calidad de vida" — que corresponden a los dos criterios de la dimensión "2. Aspectos Sociales" de la tabla completa de 6 dimensiones/20 criterios (Hoja1), no a una dimensión sin documentar como se especulaba en la versión 1.0. Ver "Catálogos Detectados".
- El campo "Respuesta Institución" mencionado en RN15 no coincide con el nombre "Justificación Institución" usado para el concepto aparentemente equivalente en el caso de uso CU-PRE-24 "Viabilidad" (RN11 de dicho documento). No se puede determinar si se trata del mismo campo con nombres distintos entre ambos casos de uso, o de campos diferentes.
- El paso 7 del Flujo Básico 1 atribuye al "Técnico URP" el clic en el botón "ir a OT"; sin embargo, dicho botón forma parte del mensaje emergente del Anexo A.3, que aparece como parte de la secuencia de acciones del Viabilizador (pasos 5–6, tras "Emitir Elegibilidad"). No se puede determinar con certeza cuál actor visualiza y acciona efectivamente ese botón.
- El paso 8 del Flujo Básico 2 menciona la notificación a un actor denominado "Técnico de SYMP", el cual no aparece en el campo "Actores" de la Identificación del caso de uso, ni se define en ninguna otra parte del documento. No se puede determinar su rol ni su relación con los demás actores documentados.
- No se especifica el disparador (evento que inicia el caso de uso) de forma explícita, más allá de lo descrito en el Flujo Básico 1.
- No se especifica la prioridad del caso de uso.
- No se especifica una tabla de códigos de error para este caso de uso (más allá de los mensajes descritos en RN03 y RN15).
- El mensaje "¡Enviado! El proyecto fue enviado a Opinión Técnica exitosamente", encontrado en el anexo Excel, no tiene un paso de flujo explícito asociado en el documento PDF que indique cuándo se muestra exactamente. **(v1.3)** Tampoco aparece en el PDF de AGOSTO 2026.
- **Nuevo (v1.1) — no resuelto:** confirmación con el negocio de si la tabla de ponderación de 6 dimensiones/20 criterios (Hoja1 del anexo Excel) pertenece funcionalmente a este caso de uso (CU-PRE-25 "Elegibilidad") o al caso de uso relacionado CU-PRE-26.5 "Priorización", dado que el nombre externo del archivo Excel referencia "Priorización" mientras que el título interno de la hoja coincide con el nombre de este CU. **Verificación cruzada realizada:** el catálogo propio de CU-PRE-26.5 (4 criterios/13–18 subcriterios ponderados en porcentaje, confirmado contra su propio PDF y su propio anexo Excel independiente) no coincide con la tabla de este documento, lo cual debilita pero no descarta la hipótesis de que pertenezca a CU-PRE-26.5. Ver "Observaciones".
- **Nuevo (v1.1) — no resuelto:** en caso de que la tabla de ponderación sí pertenezca a este caso de uso, no se describe en ningún Flujo ni Regla de Negocio cómo, cuándo o por quién se calcula el puntaje total ("Total Calificación (Prioridad)" = 100), ni qué efecto tiene ese puntaje sobre el estado o la gestión del proyecto.
- **Nuevo (v1.3) — no resuelto:** confirmar si la reducción de alcance del PDF de AGOSTO 2026 frente al PDF anterior es intencional: dimensión "Aspectos Medioambientales" ausente del mockup, catálogos C.3 a C.5 ausentes del Anexo C, referencia "C1 y C2" en el Anexo B.1 y "(Dimensión 1)" en el FB2. Mientras no se confirme, ese contenido se conserva.
- **Nuevo (v1.3) — no resuelto:** confirmar si el anexo Excel `CU-PRE-25__ANEXO__Criterios_de_Priorización_de_proy_de_IP.xlsx`, entregado con el PDF anterior, sigue vigente para el PDF de AGOSTO 2026 (cuyo Anexo B.1 remite a "Criterios en el Excel" sin adjuntarlo).
- **Nuevo (v1.3):** aplicación del tipo "Selección (radial)" Sí/No en la columna "Especificar": el PDF no indica a qué criterios corresponde; solo la Hoja1 del anexo Excel lo permite deducir (sujeto a la confirmación anterior).
- **Nuevo (v1.3):** significado de "Editable: No" en "¿Aplica?", "Especificar (Selección radial)" y "Especificar (Selección listado)", campos que el Viabilizador diligencia según su Detalle y RN01.
- **Nuevo (v1.3):** momento del bloqueo de la pantalla del Anexo A.1: al emitir la elegibilidad (FB1 paso 6; RN04) o al enviar el proyecto a CU-PRE-26 por primera vez (RN07; RN09).
- **Nuevo (v1.3):** relación del botón "ir a OT" (FB1 paso 7) con el botón "IR A OT" de la pantalla del Viabilizador y el botón "IR A OPINIÓN TÉCNICA" del Anexo A.3; efecto de los botones "ACEPTAR" e "IR A OPINIÓN TÉCNICA" del Anexo A.3.
- **Nuevo (v1.3):** texto definitivo del mensaje de emisión: RN04 vs. mockup del Anexo A.3.
- **Nuevo (v1.3):** secuencia de la Opción 2 del FB1 respecto al paso 3.
- **Nuevo (v1.3):** relación entre "una sola vez" (Descripción) y las devoluciones ilimitadas a elegibilidad (RN10; FB2).
- **Nuevo (v1.3):** vigencia de la postcondición CU-PRE-32 y de los comentarios de revisión [1] y [2] presentes en el documento.
- **Nuevo (v1.3):** fecha vigente del documento: encabezados "AGOSTO 2026" vs. Historial de Revisiones "AGO 2025".