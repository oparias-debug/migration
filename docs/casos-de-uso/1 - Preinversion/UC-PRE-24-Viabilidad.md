---
id: CU-PRE-24
codigo: CU-PRE-24
nombre: Viabilidad
modulo: Preinversión
submodulo: Gestión del Proyecto
version: "1.1"
fuente_pdf: CU-PRE-24_Viabilidad_MAY0_2026_V1_F.pdf
pagina_inicio: 3
pagina_fin: 10

nota_version: >
  La versión 1.1 incorpora el archivo Excel anexo
  `CU-PRE-24__ANEXO__Ficha_Viabilidad.xlsx`, aportado posteriormente como
  mockup nativo/editable de las pantallas Anexo A.1 y Anexo A.2. Su
  contenido confirma, sin discrepancias, los datos de ejemplo y las dos
  variantes del mensaje "¡Enviado!" ya transcritos en la versión 1.0 a
  partir del PDF. Adicionalmente, el archivo revela un tercer mensaje
  emergente ("¡Enviado! El proyecto fue enviado a Elegibilidad
  exitosamente") no documentado en el PDF ni en la versión 1.0 de este
  documento; se incorporó en "Pantallas", "Mensajes al Usuario",
  "Eventos del Sistema" y "Datos Pendientes de Definir". No se modificó
  ningún otro contenido ya corregido en la versión 1.0.

actor_principal: ["Técnico URP","Viabilizador"]

actores_secundarios: []

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-04", "CU-PRE-07", "CU-PRE-11", "CU-PRE-17", "CU-PRE-18", "CU-PRE-21", "CU-PRE-23", "Todos los CU de Formulación, Evaluación y Programación"]

casos_relacionados: ["CU-PRE-25", "CU-PRE-26", "CU-PRE-26.5"]

roles: ["Técnico URP", "Viabilizador"]

pantallas: ["Pantalla Anexo A.1 (Viabilidad)", "Botón de alerta 'Emitir Viabilidad' (Anexo A.2)", "Mensaje emergente 'Enviado a Elegibilidad' (identificado mediante anexo Excel, no documentado en el PDF original)"]

procesos: []

servicios_externos: []

entidades: ["Solicitud de Viabilidad", "Comentario del Viabilizador", "Viabilidad (estado del proyecto)", "Devolución del proyecto"]

catalogos: []

palabras_clave: ["Viabilidad", "Viabilizador", "Comentarios", "Elegibilidad", "Opinión Técnica", "Observado", "Proyecto Viable"]

ultima_actualizacion: "AGO 2025 (versión 1.0); incorporación del anexo Excel 'Ficha_Viabilidad' aplicada AGO 2026"

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB1:
      pagina: 3
  flujos_alternos:
    FB2:
      pagina: 4
    FA01:
      pagina: 5
    FA02:
      pagina: 5
  reglas_negocio:
    RN01:
      pagina: 6
    RN02:
      pagina: 6
    RN03:
      pagina: 6
    RN04:
      pagina: 7
    RN05:
      pagina: 7
    RN06:
      pagina: 7
    RN07:
      pagina: 7
    RN08:
      pagina: 7
    RN09:
      pagina: 7
    RN10:
      pagina: 7
    RN11:
      pagina: 7
  anexos:
    A1:
      nombre: Pantallas (Anexo A.1)
      pagina: 8
    A2:
      nombre: "Botón de alerta 'Emitir Viabilidad'"
      pagina: 9
    B1:
      nombre: Requerimientos Funcionales - Formatos (Pantalla Anexo A.1)
      pagina: 9
    Excel_FichaViabilidad:
      nombre: "Ficha_Viabilidad (mockup nativo de Anexo A.1/A.2, anexo Excel)"
      pagina: "No aplica — anexo Excel externo al PDF (CU-PRE-24__ANEXO__Ficha_Viabilidad.xlsx), no forma parte de la paginación del PDF original."
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|-------|---------|--------------|-------|-------|
| AGO 2025 | 1.0 | Primera Versión | Equipo Preinversión | No especificado en el documento. |

> Nota de ambigüedad: el pie de página repetido en todas las páginas del documento indica "Fecha: MAYO 2026", mientras que la tabla "Historial de Revisiones" indica "AGO 2025" para la misma versión 1.0. Ver Observaciones.

---

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Viabilidad |
| Código | CU-PRE-24 |
| Módulo | Preinversión |
| Fuente | CU-PRE-24 Viabilidad_MAY0 2026_V1_F.pdf; complementado con el anexo Excel `CU-PRE-24__ANEXO__Ficha_Viabilidad.xlsx` (ver "Pantallas") |
| Versión | 1.1 |

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

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite:
1. Al actor "Técnico URP" solicitar Viabilidad a un proyecto.
2. Al actor "Viabilizador" emitir viabilidad a un proyecto, así como agregar comentarios en caso que se requieran ajustes a la información del mismo.

# Actor Principal

Este caso de uso tiene dos actores principales, ambos listados en el campo "Actores" de la Identificación:
- Técnico URP
- Viabilizador

> Nota: a diferencia de otros casos de uso de la serie (donde el segundo actor aparece únicamente en Reglas de Negocio con rol de solo visualización), en este documento el "Viabilizador" es un actor principal con capacidad de registro/edición, listado explícitamente en el campo "Actores" de la Identificación.

---

# Actores Secundarios

> No especificado en el documento como actor secundario propiamente dicho. RN01 menciona genéricamente a "todos los demás actores" con permiso de solo visualización, sin nombrarlos individualmente.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Todos los casos de uso de Formulación, Evaluación y Programación.

> Nota: esta precondición se transcribe tal como aparece en el documento, de forma genérica, sin listar códigos de casos de uso específicos.

---

# Flujo Principal

## FB1 – Flujo Básico 1: Solicitud de Viabilidad por el Técnico URP

1. Técnico URP ingresa a la pestaña "Gestión del Proyecto" en la sección "Viabilidad", carga el documento de Preinversión y Anexos del proyecto.
2. Sistema habilita el botón "Solicitar Viabilidad".
3. Técnico URP da clic en el botón "Solicitar Viabilidad".
4. Sistema valida que se haya completado el registro de la información en los casos de uso desde el CU-PRE-04 "Identificación" hasta el CU-PRE-23 "Indicadores del Proyecto", según corresponda.
5. Sistema muestra mensaje emergente que le indica al Técnico URP que "la solicitud del proyecto ha sido enviada al proceso de viabilidad exitosamente".
6. Sistema envía notificación al "Viabilizador" informando que le ha llegado la solicitud.

> Nota: el documento abrevia tanto este flujo como el siguiente ("Flujo Básico 2") con la misma sigla "FB"; se distinguen aquí como "FB1" y "FB2" para facilitar la referencia, conservando sus títulos completos originales.

---

# Flujos Alternos

## FB2 – Flujo Básico 2: Revisión de información para emisión de Viabilidad

**Condición**

> No especificado en el documento.

**Flujo**

1. Sistema notifica al Viabilizador que hay un proyecto en bandeja y en el mensaje le muestra un link para que ingrese directamente al formulario del Anexo A.1.
2. Viabilizador tiene dos opciones:
   a) Da clic en el link e ingresa directamente al formulario A.1.
   b) Ingresa a la pantalla "Captura de proyectos", con la lista de los proyectos de la entidad en el estado "Proyecto Formulado".
3. Viabilizador da clic en el proyecto sobre el cual va a emitir viabilidad.
4. Viabilizador o ingresa a la pestaña "Gestión del Proyecto" en la sección "Viabilidad".
5. Sistema despliega la pantalla del Anexo A.1.

**Resultado**

> No especificado en el documento.

> Nota de ambigüedad: el paso 4 utiliza la conjunción "O ingresa a..." lo cual sugiere que se trata de una vía alternativa a los pasos 2–3 (opción "b", Captura de proyectos), y no de un paso secuencial adicional después del paso 3. El documento no aclara con precisión el orden o la relación exacta entre los pasos 2, 3 y 4.

## FA01 – Flujo Alternativo 1: Enviar comentarios

**Condición**

> No especificado en el documento.

**Flujo**

1.1. Viabilizador registra comentarios en los campos de la columna "Comentarios del viabilizador" y en el campo de "observaciones generales" y da clic en el botón "Guardar".

1.2. Sistema guarda cada uno de los comentarios registrados por el actor "Viabilizador".

1.3. Viabilizador da clic en el botón "Enviar comentarios".

1.4. Sistema cambia el estado del proyecto a "Observado" y habilita los campos de las pantallas de CU-PRE-04 "Identificación" a CU-PRE-23 "Indicadores del Proyecto".

1.5. Sistema notifica al actor "Técnico URP" que el "Viabilizador" ha enviado comentarios a la información registrada, para su ajuste.

1.6. Técnico URP realiza los ajustes en los campos que correspondan, carga el perfil ajustado (si aplica), y vuelve a iniciar el proceso de solicitud de viabilidad, respondiendo los comentarios en CU-PRE-26 "Opinión Técnica".

**Resultado**

> No especificado en el documento.

> Nota de ambigüedad: el paso 1.6 menciona que el Técnico URP responde "los comentarios en CU-PRE-26 'Opinión Técnica'"; sin embargo, este flujo trata sobre los comentarios del Viabilizador registrados en el marco de este caso de uso (CU-PRE-24), no sobre comentarios de la Opinión Técnica (CU-PRE-26, que se aborda de forma separada en RN11). No se aclara si esta referencia es intencional o un posible error de referencia cruzada. Ver Observaciones.

## FA02 – Flujo Alternativo 2: Emitir Viabilidad

**Condición**

> No especificado en el documento.

**Flujo**

2.1. Viabilizador registra comentarios en el campo "observaciones generales/Justificación de la viabilidad".

2.2. Viabilizador da clic en el botón "Emitir Viabilidad".

2.3. Sistema cambia el estado del proyecto a "Proyecto Viable" y muestra el mensaje "La viabilidad del proyecto ha sido emitida con éxito". Si es la primera vez que se gestiona la viabilidad, el mensaje anterior incluirá el texto: "Es necesario continuar con la gestión de Elegibilidad" (Ver Anexo A.2). Si el proyecto ya contaba con Elegibilidad, el mensaje anterior incluirá el texto "Es necesario continuar con la gestión de la Opinión Técnica" (similar al Anexo A.2 pero mostrará el botón "Ir a OT").

2.4. Viabilizador: si selecciona el botón "Ir a Elegibilidad" del Anexo A.2, el sistema envía al usuario a CU-PRE-25 "Elegibilidad". Si selecciona "Salir", el sistema queda en la pantalla del Anexo A.1.

2.5. Sistema notifica al actor "Técnico URP" que se ha emitido Viabilidad al proyecto y deshabilita la pantalla del Anexo A.1.

2.6. Sistema habilita el botón "Ir a Elegibilidad" y permite la edición de la pantalla "Elegibilidad" (solo en los casos en que esta gestión vaya a realizarse por primera vez; cuando el proyecto ya haya pasado por un proceso de Elegibilidad, el Sistema no habilitará CU-PRE-25 "Elegibilidad").

2.7. Viabilizador: la primera vez da clic en el botón "Ir a Elegibilidad". Las veces posteriores da clic en el botón "Ir a OT".

**Resultado**

> No especificado en el documento.

> Nota de ambigüedad: el paso 2.3 indica que, cuando el proyecto ya contaba con Elegibilidad, el mensaje emergente debe incluir el texto sobre la Opinión Técnica y mostrar el botón "Ir a OT"; sin embargo, el segundo mockup del Anexo A.2 solo muestra el texto "La viabilidad del proyecto ha sido emitida con éxito." junto con un botón "ACEPTAR", sin el texto adicional ni el botón "Ir a OT" descritos en este paso. Ver Observaciones y "Datos Pendientes de Definir".

---

# Excepciones

> No especificado en el documento. El documento no desarrolla una tabla de excepciones con código, descripción y consecuencia para este caso de uso.

# Postcondiciones

1. CU-PRE-25 "Elegibilidad".
2. CU-PRE-26 "Opinión Técnica".
3. CU-PRE-26.5 "Priorización".

---

# Reglas de Negocio

## RN01

**Descripción:** El Viabilizador es el único que puede registrar/editar información en la sección, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

> Nota de ambigüedad: esta regla establece que el Viabilizador es "el único" con capacidad de registro/edición; sin embargo, el resto del documento describe claramente acciones de registro/edición por parte del Técnico URP (carga de documentos en FB1, clic en botones habilitados exclusivamente para él según RN06, y ajustes de campos en FA01 paso 1.6). No se aclara el alcance exacto de "la sección" a la que se refiere esta regla. Ver Observaciones y "Datos Pendientes de Definir".

## RN02

**Descripción:** Para que se habilite el botón "Solicitar Viabilidad", el Técnico URP previamente deberá adjuntar el documento de Preinversión, y si aplica, otros anexos del proyecto, en el campo correspondiente. Para que se habilite el botón "Ir a Elegibilidad", previamente el Viabilizador debió dar clic en el botón "Emitir Viabilidad". Este proceso de Elegibilidad solo se requerirá una vez.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

## RN03

**Descripción:** El proyecto debe pasar por los tres filtros (Viabilidad, Elegibilidad, Opinión Técnica) la primera vez que realiza la gestión del proyecto. Sin embargo, después de que el viabilizador haya emitido por primera vez la Elegibilidad, ya no es necesario volver a ella, en caso de algún ajuste del proyecto o actualización. Por tanto, se salta de viabilidad, a OT.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

## RN04

**Descripción:** Una vez que el Técnico URP da clic en el botón "Solicitar Viabilidad" el Sistema bloqueará para edición los campos de los casos de uso CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto", y deshabilitará el botón "Solicitar Viabilidad".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

## RN05

**Descripción:** Una vez que el Viabilizador dé clic en el botón "Enviar comentarios" el Sistema habilitará nuevamente para edición los campos de los casos de uso CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto", y el botón "Solicitar Viabilidad".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

## RN06

**Descripción:** El botón "Solicitar Viabilidad" solo estará habilitado para el "Técnico URP".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

## RN07

**Descripción:** Los botones "Enviar comentarios", "Emitir Viabilidad" e "Ir a Elegibilidad" (si es la primera vez, o "Ir a OT" si ya cuenta con Elegibilidad), sólo estarán habilitados para el Viabilizador.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

## RN08

**Descripción:** El sistema mostrará información de apoyo que oriente al Viabilizador sobre los criterios a revisar en cada campo. Dicha información se mostrará al acercar el cursor al signo de pregunta en cada campo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

## RN09

**Descripción:** Este formato de viabilidad aplica para todas las etapas del proyecto.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

## RN10

**Descripción:** El proyecto se puede devolver cuantas veces sea necesario. Los comentarios del viabilizador se deben guardar cada vez que se devuelva y deben poderse consultar en pantalla las observaciones de cada devolución. El sistema debe mostrar cuántas veces se ha devuelto.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

> Nota de ambigüedad: ni el mockup del Anexo A.1 ni la tabla de formatos del Anexo B muestran o describen un contador de devoluciones ni una vista de historial de observaciones por devolución. Ver "Datos Pendientes de Definir".

## RN11

**Descripción:** En caso que la solicitud de viabilidad se haga respondiendo a una observación por parte de la OT, el sistema debe validar que previamente se hayan respondido los comentarios en CU-PRE-26 "Opinión Técnica" por parte del Técnico URP.

El Técnico URP debe consultar las observaciones a través del botón "Ver comentarios OT" ubicado en la parte superior del Anexo A.1. Al hacer clic el sistema debe remitirlo a los comentarios de CU-PRE-26 "Opinión técnica" para que pueda diligenciar la columna "Justificación Institución" y oprimir el botón "Guardar Ajustes".

El sistema debe validar que si hay observaciones de OT en CU-PRE-26 "Opinión técnica" el Técnico URP haya respondido todos los comentarios emitidos por OT en la columna "Justificación Institución" para que le permita solicitar Viabilidad nuevamente. De lo contrario, el sistema deberá mostrar un mensaje que diga "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-24.

> Nota de ambigüedad: el botón "Guardar Ajustes" mencionado aquí no aparece en la tabla de Botones del Anexo B de este caso de uso. No se puede determinar si pertenece a la pantalla de este caso de uso o a la de CU-PRE-26 "Opinión Técnica". Ver "Datos Pendientes de Definir".

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Nota de solicitud de OT | Campo para que el Técnico URP cargue la nota de solicitud de Opinión Técnica en formato PDF/A. | Botón de carga | Botón de carga | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Documento de Preinversión | Campo para que el Técnico URP cargue el documento de Preinversión en formato PDF/ o .doc. Campo obligatorio. | Botón de carga | Botón de carga | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: No. |
| Otros documentos | Campo para que el Técnico URP cargue otros documentos relacionados, según corresponda. | Botón de carga | Botón de carga | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Ver comentarios OT | Botón que muestra al técnico URP los comentarios de la OT para que desde allí diligencie los campos de "Justificación Institución". | Botón de carga | Botón de carga | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| CUP | Muestra el Código Único de Proyecto (CUP) emitido en el CU-PRE-01 "Registro de Proyectos". | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Nombre del proyecto | Muestra el Nombre del proyecto registrado en el CU-PRE-01 "Registro de Proyectos". | Alfanumérico | Alfanumérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Objetivo General | Muestra el objetivo del proyecto registrado en el campo "Objetivo General" del CU-PRE-04 "Identificación". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Descripción | Muestra la información del campo "Descripción del proyecto" del Anexo A1 del CU-PRE-11 "Descripción técnica". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Productos | Muestra la información de las columnas "Nombre del producto" del Anexo A1, del CU-PRE-23 "Indicadores del Proyecto". Además, debe contener un link que direccione a la tabla de CU-PRE-23 "Indicadores del Proyecto". Estas ventanas no son editables. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a que CU-PRE-23 documenta el campo equivalente como "Producto", no "Nombre del producto". |
| Población objetivo | Muestra la información de la columna "N° de personas" de la "Población objetivo" del Anexo A1 del CU-PRE-07 "Población Objetivo". Además, debe contener un link que direccione a la tabla de CU-PRE-07 "Población Objetivo". Estas ventanas no son editables. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Inversión estimada | Muestra el valor de la columna "Monto" -celda "Total de inversión" (a precios de mercado) del Anexo A.1 del CU-PRE-17 "Presupuesto de inversión". El sistema deberá agregar el separador de miles (,). Además, debe contener un link que direccione a la tabla de CU-PRE-17 "Presupuesto de inversión". Estas ventanas no son editables. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Resumen del presupuesto | Muestra la Tabla del Anexo A.4 del CU-PRE-17 "Presupuesto de inversión". Además, debe contener un link que direccione a la tabla de CU-PRE-17 "Presupuesto de inversión"; estas ventanas no son editables. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Costo de operación | Muestra el valor de los costos de operación, toma el valor de la celda "Total (P.M.)" del Año 1 de la tabla "Costos de Operación" del Anexo A.1 del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Además, debe contener un link que direccione a la tabla de CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Estas ventanas no son editables. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la denominación "Año 1", que no coincide con la nomenclatura "Período 1" usada en CU-PRE-18. |
| Costo de mantenimiento | Muestra el valor de los costos de mantenimiento, toma el valor de la celda "Total (P.M.)" del Año 1 de la tabla "Costos de Mantenimiento" del Anexo A.1 del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Además, debe contener un link que direccione a la tabla de CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Estas ventanas no son editables. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la denominación "Año 1". |
| Fuente de financiamiento | Muestra la tabla del Anexo A.5 del CU-PRE-17 "Presupuesto de inversión". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la denominación "Fuente de financiamiento estimada" usada en "Campos requeridos". |
| Indicadores de evaluación | Muestra los indicadores de evaluación calculados por el Sistema, toma la información del campo "Indicadores" del CU-PRE-21 "Flujo de Caja y cálculo de Indicadores". Además, debe contener un link que direccione a la tabla de CU-PRE-21 "Flujo de Caja y cálculo de Indicadores". Estas ventanas no son editables. | Moneda / Porcentaje | Moneda / Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la referencia a "CU-PRE-21" (distinto de "CU-PRE-21.5" analizado previamente) y respecto al indicador "R B/C" mostrado en el mockup sin documentación. |
| Comentarios del Viabilizador | Campo para que el Viabilizador registre comentarios a la información. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí. |
| Observaciones Generales/Justificación de la Viabilidad | Campo para que el Viabilizador registre observaciones generales al proyecto y justifique por qué emitió la viabilidad o va a devolver el proyecto. Campo obligatorio. | Texto | Texto | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. |
| Solicitar viabilidad (botón) | Este botón se habilitará una vez que el Técnico URP haya cargado los documentos de soporte en los campos correspondientes. Únicamente podrá diligenciarlo el Técnico URP. | Botón | Botón | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Enviar comentarios (botón) | Botón que permite al Viabilizador enviar comentarios al Técnico URP sobre la información registrada, según corresponda. Este botón se activa una vez que el Técnico URP dé clic en el botón "Solicitar Viabilidad". | Botón | Botón | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Emitir viabilidad (botón) | Permitirá al Viabilizador otorgar la viabilidad a cada versión de las diferentes etapas del proyecto. Se habilita una vez que dicho actor haya registrado y guardado la información del campo "Observaciones Generales/Justificación de la Viabilidad". | Botón | Botón | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Pasar al siguiente filtro (botón) | Se habilita una vez emitida la Viabilidad para pasar a Elegibilidad, o a Opinión Técnica, según corresponda. | Botón | Botón | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a que este nombre genérico no coincide con los nombres específicos de botón usados en el Flujo Alternativo 2 y en el mockup del Anexo A.2 ("Ir a Elegibilidad", "Ir a OT"). |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Documento de Preinversión | Campo obligatorio. | No especificado en el documento. |
| Observaciones Generales/Justificación de la Viabilidad | Campo obligatorio. | No especificado en el documento. |
| Solicitud de Viabilidad (nivel de proceso) | El sistema valida que se haya completado el registro de la información desde CU-PRE-04 hasta CU-PRE-23 (FB1, paso 4). | No especificado en el documento (no se transcribe un mensaje de error específico para este caso; solo se describe el comportamiento de validación). |
| Solicitud de Viabilidad respondiendo a observación de OT | El sistema debe validar que el Técnico URP haya respondido todos los comentarios emitidos por la OT en la columna "Justificación Institución" antes de permitir solicitar Viabilidad nuevamente (RN11). | "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad" |

---

# Errores

> No especificado en el documento. El documento no desarrolla una tabla de códigos de error para este caso de uso (más allá del mensaje de validación descrito en RN11, documentado en la sección "Validaciones").

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Cargar documentos de soporte; dar clic en "Solicitar Viabilidad"; ajustar campos tras recibir comentarios del Viabilizador; consultar comentarios de la OT mediante "Ver comentarios OT". | RN02, RN06, FA01 (paso 1.6), RN11 |
| Viabilizador | Registrar comentarios y observaciones generales; dar clic en "Enviar comentarios", "Emitir Viabilidad", "Ir a Elegibilidad" o "Ir a OT". | RN07 |
| Viabilizador (según RN01) | "El único que puede registrar/editar información en la sección", según credenciales. | RN01 (ver nota de ambigüedad respecto a las acciones de registro/edición atribuidas también al Técnico URP) |
| Todos los demás actores (no especificados individualmente en el documento) | Visualizar únicamente la información de las Unidades Ejecutoras, según credenciales. | RN01 |

---

# Dependencias

**Casos de uso mencionados:**
- Todos los casos de uso de Formulación, Evaluación y Programación (precondición genérica)
- CU-PRE-01 "Registro de Proyectos" (origen de CUP y Nombre del proyecto)
- CU-PRE-04 "Identificación" (origen de "Objetivo General"; parte del rango bloqueado/validado por RN04/RN05/FB1)
- CU-PRE-07 "Población Objetivo" (origen de "Población objetivo")
- CU-PRE-11 "Descripción técnica" (origen de "Descripción")
- CU-PRE-17 "Presupuesto de inversión" (origen de "Inversión estimada", "Resumen del presupuesto", "Fuente de financiamiento")
- CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" (origen de "Costo de operación" y "Costo de mantenimiento")
- CU-PRE-21 "Flujo de Caja y cálculo de Indicadores" (origen de "Indicadores de evaluación"; ver Observaciones respecto a su posible relación con CU-PRE-21.5)
- CU-PRE-23 "Indicadores del Proyecto" (origen de "Productos"; parte del rango bloqueado/validado por RN04/RN05/FB1)
- CU-PRE-25 "Elegibilidad" (postcondición; destino de "Ir a Elegibilidad")
- CU-PRE-26 "Opinión Técnica" (postcondición; origen de comentarios de OT referenciados en RN11 y FA01 paso 1.6)
- CU-PRE-26.5 "Priorización" (postcondición)

**Procesos relacionados:**
> No especificado en el documento.

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Pantalla: Anexo A.1 (Viabilidad)

**Descripción:** Pantalla donde se muestra la ficha consolidada del proyecto (datos de solo lectura provenientes de otros casos de uso), junto con la columna de comentarios del Viabilizador y las acciones de solicitud/emisión de viabilidad.

**Campos:**
- CUP, Nombre del proyecto
- Nota de solicitud de OT, Documento de Preinversión, Otros documentos (campos de carga de archivos)
- Botón "Ver comentarios OT"
- Tabla "Ficha del Proyecto": Objetivo General, Descripción del proyecto, Productos, Población Objetivo, Inversión Estimada, Resumen del Presupuesto, Costos de Operación, Costos de Mantenimiento, Fuente de Financiamiento, Indicadores de Evaluación — cada uno con su columna correspondiente "Comentarios del Viabilizador"
- Observaciones Generales / Justificación de la Viabilidad

**Botones:**
- Ver comentarios OT
- Solicitar Viabilidad
- Guardar
- Enviar comentarios
- Emitir Viabilidad
- Ir a Elegibilidad

**Acciones:**
- Carga de documentos de soporte por el Técnico URP (FB1, paso 1).
- Clic en "Solicitar Viabilidad" (FB1, pasos 2–3).
- Registro de comentarios por el Viabilizador y clic en "Guardar" / "Enviar comentarios" (FA01).
- Registro de observaciones generales y clic en "Emitir Viabilidad" (FA02).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

CUP: XXXX (marcador de posición)
NOMBRE DEL PROYECTO: XXXXXXXXXXXXXXXXXXXXXXXXX (marcador de posición)

| CAMPOS | COMENTARIOS DEL VIABILIZADOR |
|--------|--------------------------------|
| Objetivo General: [ícono "?"] (campo vacío) | (campo vacío) |
| Descripción del proyecto: [ícono "?"] (campo vacío) | (campo vacío) |
| Productos: [ícono "?"] (campo vacío) | (campo vacío) |
| Población Objetivo: [ícono "?"] (campo vacío) | (campo vacío) |
| Inversión Estimada: [ícono "?"] (campo vacío) | (campo vacío) |
| Resumen del Presupuesto: [ícono "?"] (campo vacío) | (campo vacío) |
| Costos de Operación: [ícono "?"] (campo vacío) | (campo vacío) |
| Costos de Mantenimiento: [ícono "?"] (campo vacío) | (campo vacío) |
| Fuente de Financiamiento: [ícono "?"] (campo vacío) | (campo vacío) |
| Indicadores de Evaluación: [ícono "?"] — "VAN:", "TIR:", "R B/C" (marcadores de fila, sin valores de ejemplo) | (campo vacío) |

Observaciones Generales / Justificación de la Viabilidad: (campo vacío)

Botones mostrados: GUARDAR, ENVIAR COMENTARIOS, EMITIR VIABILIDAD, IR A ELEGIBILIDAD.

> Nota: el mockup no presenta valores numéricos o de texto de ejemplo concretos para los campos de la ficha (a diferencia de los mockups de otros casos de uso de la serie); solo se muestran los marcadores de posición "XXXX"/"XXXXXXXXXXXXXXXXXXXXXXXXX" para CUP y Nombre del proyecto, y las etiquetas "VAN:", "TIR:", "R B/C" dentro del campo "Indicadores de Evaluación". El indicador "R B/C" no está documentado en este ni en otros casos de uso de la serie relacionados con indicadores de evaluación. Ver "Datos Pendientes de Definir".

> **Verificación (v1.1):** el anexo Excel `CU-PRE-24__ANEXO__Ficha_Viabilidad.xlsx` (hoja "Hoja1") contiene el mockup nativo/editable de esta misma pantalla y confirma, sin discrepancias, el orden y los rótulos de los 10 campos de la tabla "Ficha del Proyecto" (Objetivo General, Descripción del proyecto, Productos, Población Objetivo, Inversión Estimada, Resumen del Presupuesto, Costos de Operación, Costos de Mantenimiento, Fuente de Financiamiento, Indicadores de Evaluación), los marcadores de posición "CUP: XXXX" y "NOMBRE DEL PROYECTO: XXXXXXXXXXXXXXXXXXXXXXX", y el título de columna "COMENTARIOS DEL VIABILIZADOR". No se detectaron discrepancias entre el PDF y este anexo Excel para la pantalla del Anexo A.1.

## Pantalla: Botón de alerta "Emitir Viabilidad" (Anexo A.2)

**Descripción:** Ventana modal de confirmación mostrada al emitir viabilidad, con dos variantes según si es la primera vez que se gestiona la viabilidad del proyecto.

**Campos:** No aplica (mensaje informativo).

**Botones:**
- Variante 1 (primera vez): "Ir a Elegibilidad", "Salir"
- Variante 2 (proyecto ya contaba con Elegibilidad): "Aceptar"

**Acciones:**
- Confirmación de emisión de viabilidad exitosa; navegación condicional a CU-PRE-25 "Elegibilidad" o permanencia en el Anexo A.1.

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**

Variante 1:
- Ícono de confirmación (check).
- Texto: "¡Enviado!"
- Texto: "La viabilidad del proyecto ha sido emitida con éxito. Es necesario continuar con la gestión de Elegibilidad"
- Botones: "IR A ELEGIBILIDAD", "SALIR"

Variante 2:
- Ícono de confirmación (check).
- Texto: "¡Enviado!"
- Texto: "La viabilidad del proyecto ha sido emitida con éxito."
- Botón: "ACEPTAR"

> Nota: la Variante 2 del mockup no incluye el texto "Es necesario continuar con la gestión de la Opinión Técnica" ni el botón "Ir a OT" descritos en el paso 2.3 del Flujo Alternativo 2 para este mismo escenario (proyecto que ya contaba con Elegibilidad). Ver Observaciones y "Datos Pendientes de Definir".

> **Verificación (v1.1):** el anexo Excel `CU-PRE-24__ANEXO__Ficha_Viabilidad.xlsx` (hoja "Hoja1", filas 21–22, celdas combinadas I21:K22 y N21:P22) contiene ambas variantes de este mensaje, con texto idéntico al ya transcrito de la versión 1.0 (basada en el PDF): la Variante 1 con el texto largo ("...Es necesario continuar con la gestión de Elegibilidad") y la Variante 2 con el texto corto ("La viabilidad del proyecto ha sido emitida con éxito."). No se detectaron discrepancias. El anexo Excel tampoco incluye el botón "Ir a OT" ni el texto sobre la Opinión Técnica para la Variante 2, confirmando la discrepancia ya señalada frente al paso 2.3 del Flujo Alternativo 2 (ver Observaciones y "Datos Pendientes de Definir").

## Pantalla/Mensaje: "Enviado a Elegibilidad" (identificado mediante anexo Excel — no documentado en el PDF original)

> **Origen:** este mensaje emergente no se identificó en el PDF fuente `CU-PRE-24_Viabilidad_MAY0_2026_V1_F.pdf` ni en la versión 1.0 de este documento. Se descubrió en el anexo Excel `CU-PRE-24__ANEXO__Ficha_Viabilidad.xlsx`, presente de forma idéntica en dos ubicaciones del archivo: la hoja "Hoja1" (filas 39–40, celdas combinadas K39:M39 y K40:M40) y como contenido único de la hoja "Hoja 1" (con espacio en el nombre; celdas combinadas E12:G12 y E14:G14). No tiene un número de Anexo propio asignado en el material fuente.

**Descripción:** Mensaje emergente de confirmación, aparentemente mostrado al confirmar el envío del proyecto al proceso de Elegibilidad.

**Campos:** No aplica (mensaje informativo).

**Botones:** No especificado en el anexo Excel (no se incluye una imagen de botón en las celdas transcritas).

**Acciones:** No especificado explícitamente; por su contenido textual, parece corresponder al momento en que el Viabilizador da clic en el botón "Ir a Elegibilidad" del Anexo A.2 (FA02, paso 2.4), inmediatamente antes de que "el sistema envía al usuario a CU-PRE-25 'Elegibilidad'". Esta correspondencia **no está confirmada por el documento fuente** y se señala en "Datos Pendientes de Definir".

**Ejemplo de datos mostrados en el mockup:**
- Texto: "¡Enviado!"
- Texto: "El proyecto fue enviado a Elegibilidad exitosamente"

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Confirmación | "la solicitud del proyecto ha sido enviada al proceso de viabilidad exitosamente" | Al completar la solicitud de Viabilidad por el Técnico URP (FB1, paso 5). |
| Confirmación (Variante 1) | "¡Enviado! La viabilidad del proyecto ha sido emitida con éxito. Es necesario continuar con la gestión de Elegibilidad" | Al emitir Viabilidad por primera vez (FA02, paso 2.3; Anexo A.2, Variante 1). |
| Confirmación (Variante 2) | "¡Enviado! La viabilidad del proyecto ha sido emitida con éxito." (según el paso 2.3 debería incluir además "Es necesario continuar con la gestión de la Opinión Técnica", texto no presente en el mockup) | Al emitir Viabilidad cuando el proyecto ya contaba con Elegibilidad (FA02, paso 2.3; Anexo A.2, Variante 2). |
| Advertencia/Error de validación | "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad" | Cuando el Técnico URP intenta solicitar Viabilidad respondiendo a una observación de OT sin haber respondido todos los comentarios de CU-PRE-26 "Opinión Técnica" (RN11). |
| Ayuda contextual | No especificado en el documento (solo se indica que el Sistema mostrará "información de apoyo que oriente al Viabilizador sobre los criterios a revisar en cada campo"). | Al acercar el cursor al signo de pregunta "?" en cada campo (RN08). |
| Notificación (Técnico URP) | No especificado en el documento (texto exacto no transcrito; solo se describe el evento de notificación). | Cuando el Viabilizador envía comentarios (FA01, paso 1.5) o cuando se emite Viabilidad (FA02, paso 2.5). |
| Notificación (Viabilizador) | No especificado en el documento (texto exacto no transcrito; solo se describe el evento de notificación). | Cuando el Técnico URP solicita Viabilidad (FB1, paso 6; FB2, paso 1). |
| Confirmación (identificado mediante anexo Excel, no documentado en el PDF original) | "¡Enviado! El proyecto fue enviado a Elegibilidad exitosamente" | No confirmado por el documento fuente; por su contenido, parece corresponder al clic en el botón "Ir a Elegibilidad" del Anexo A.2 (FA02, paso 2.4), antes de la redirección a CU-PRE-25 "Elegibilidad". Ver "Datos Pendientes de Definir". |

---

# Observaciones

- Existe una discrepancia entre el pie de página de todas las páginas del documento, que indica "Fecha: MAYO 2026", y la tabla "Historial de Revisiones", que indica "AGO 2025" para la misma versión 1.0.
- RN01 establece que "El Viabilizador es el único que puede registrar/editar información en la sección"; sin embargo, el resto del documento describe claramente acciones de registro/edición por parte del Técnico URP (carga de documentos en FB1 paso 1, clic en botones habilitados exclusivamente para él según RN06, y ajustes de campos en FA01 paso 1.6). No se aclara el alcance exacto de "la sección" a la que se refiere RN01.
- El botón "Guardar Ajustes" mencionado en RN11 no aparece documentado en la tabla de Botones del Anexo B de este caso de uso (que solo lista "Solicitar viabilidad", "Enviar comentarios", "Emitir viabilidad" y "Pasar al siguiente filtro"), ni se identifica claramente en el mockup del Anexo A.1. No se puede determinar si este botón pertenece a la pantalla de este caso de uso o a la de CU-PRE-26 "Opinión Técnica".
- El botón "Pasar al siguiente filtro" documentado en el Anexo B no coincide con los nombres de botón específicos usados en el Flujo Alternativo 2 y en el mockup del Anexo A.2 ("Ir a Elegibilidad", "Ir a OT"); parece ser una denominación genérica para ambos botones específicos.
- El paso 4 del Flujo Básico 2 utiliza la conjunción "O ingresa a..." lo cual sugiere que se trata de una vía alternativa a los pasos 2–3 (opción "b", Captura de proyectos), y no de un paso secuencial adicional después del paso 3. El documento no aclara con precisión el orden o la relación exacta entre los pasos 2, 3 y 4.
- El paso 1.6 del Flujo Alternativo 1 (Enviar comentarios) menciona que el Técnico URP "vuelve a iniciar el proceso de solicitud de viabilidad, respondiendo los comentarios en CU-PRE-26 'Opinión Técnica'"; sin embargo, este flujo trata sobre comentarios del Viabilizador en el marco de este caso de uso (CU-PRE-24), no sobre comentarios de la Opinión Técnica (CU-PRE-26, abordada de forma separada en RN11). No se aclara si esta referencia es intencional o un posible error de referencia cruzada.
- El campo "Productos" del Anexo B hace referencia a "las columnas 'Nombre del producto' del Anexo A1... del CU-PRE-23 'Indicadores del Proyecto'"; sin embargo, en el documento CU-PRE-23 analizado previamente, el campo correspondiente de la tabla "Indicadores de Producto" se denomina "Producto", no "Nombre del producto". Existe una posible discrepancia de nomenclatura entre ambos documentos.
- El campo "Indicadores de evaluación" del Anexo B hace referencia a "CU-PRE-21 'Flujo de Caja y cálculo de Indicadores'"; este nombre y código no coinciden exactamente con "CU-PRE-21.5 Flujo de Caja Financiero" (documento previamente analizado en esta serie). No se puede determinar si "CU-PRE-21" y "CU-PRE-21.5" corresponden al mismo caso de uso con nombres distintos, o si son casos de uso diferentes.
- Los campos "Costo de operación" y "Costo de mantenimiento" (Anexo B) hacen referencia al "Año 1" de las tablas "Costos de Operación" y "Costos de Mantenimiento" del CU-PRE-18; sin embargo, en el documento CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" analizado previamente, dichas tablas usan la nomenclatura "Período 1", no "Año 1". Existe una posible discrepancia de nomenclatura entre ambos documentos.
- El campo "Fuente de financiamiento" (Anexo B) se documenta sin el calificativo "estimada", mientras que la sección "Campos requeridos" de la Identificación lo denomina "Fuente de financiamiento estimada". Discrepancia menor de nomenclatura.
- El primer mockup del Anexo A.2 usa el texto "¡Enviado!" como título del ícono de confirmación, mientras que el cuerpo del mensaje usa el verbo "emitida" ("La viabilidad del proyecto ha sido emitida con éxito"). Se transcribe tal como aparece, sin unificar la terminología.
- La Variante 2 del mockup del Anexo A.2 (proyecto que ya contaba con Elegibilidad) no incluye el texto "Es necesario continuar con la gestión de la Opinión Técnica" ni el botón "Ir a OT" descritos en el paso 2.3 del Flujo Alternativo 2 para este mismo escenario; el mockup solo muestra el texto genérico y un botón "ACEPTAR".
- A diferencia de otros casos de uso de la serie, este documento no incluye una regla de negocio sobre el sombreado de campos pendientes de completar en color rojo; solo se documenta la ayuda contextual mediante el ícono "?" (RN08).
- El indicador "R B/C" mostrado en el mockup del campo "Indicadores de Evaluación" (Anexo A.1) no está documentado ni definido en este caso de uso ni en el CU-PRE-21.5 "Flujo de Caja Financiero" (que solo documenta VAN y TIR).
- **Anexo Excel (v1.1):** el archivo `CU-PRE-24__ANEXO__Ficha_Viabilidad.xlsx`, aportado posteriormente, confirma sin discrepancias el contenido de los mockups de los Anexos A.1 y A.2 ya transcritos a partir del PDF en la versión 1.0. Adicionalmente, revela un tercer mensaje emergente — "¡Enviado! El proyecto fue enviado a Elegibilidad exitosamente" — no mencionado en ninguna parte del PDF original ni de la versión 1.0 de este documento. Este mensaje aparece de forma idéntica en dos ubicaciones del archivo (hoja "Hoja1", filas 39–40; hoja "Hoja 1", como contenido único). No se confirma en qué paso exacto del flujo se muestra; por su contenido textual parece corresponder al clic en "Ir a Elegibilidad" (FA02, paso 2.4), pero esta correspondencia no está confirmada por el documento fuente (ver "Datos Pendientes de Definir").

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Solicitud de Viabilidad | Solicitud enviada por el Técnico URP para que el Viabilizador revise y emita viabilidad sobre el proyecto. | Registro/envío (FB1); validación de completitud de CU-PRE-04 a CU-PRE-23 (FB1, paso 4); bloqueo de campos tras solicitud (RN04); notificación al Viabilizador (FB1, paso 6; FB2, paso 1). |
| Comentario del Viabilizador | Observación registrada por el Viabilizador sobre un campo específico de la ficha del proyecto, o de forma general. | Registro (FA01, paso 1.1); guardado (FA01, paso 1.2); envío y notificación al Técnico URP (FA01, pasos 1.3–1.5); habilitación de campos tras envío (RN05). |
| Viabilidad (estado del proyecto) | Estado del proyecto relacionado con el proceso de viabilidad ("Observado", "Proyecto Viable"). | Cambio de estado a "Observado" (FA01, paso 1.4); cambio de estado a "Proyecto Viable" (FA02, paso 2.3); habilitación del siguiente filtro (FA02, paso 2.6); aplicación a todas las etapas del proyecto (RN09). |
| Devolución del proyecto | Registro de cada ocasión en que el Viabilizador devuelve el proyecto al Técnico URP con comentarios. | Guardado de comentarios en cada devolución y conteo de devoluciones (RN10; ver "Datos Pendientes de Definir" respecto a su implementación no reflejada en el mockup ni en el Anexo B). |

---

# Catálogos Detectados

> No especificado en el documento. Este documento no incluye catálogos.

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Habilitación del botón "Solicitar Viabilidad" | Sistema (FB1, paso 2), tras carga de documentos de soporte | Botón "Solicitar Viabilidad" (Anexo A.1) |
| Validación de completitud CU-PRE-04 a CU-PRE-23 | Sistema (FB1, paso 4), tras clic en "Solicitar Viabilidad" | — |
| Notificación al Viabilizador | Sistema (FB1, paso 6 / FB2, paso 1) | Bandeja del Viabilizador |
| Bloqueo de campos CU-PRE-04 a CU-PRE-23 | Sistema (RN04), tras clic en "Solicitar Viabilidad" | Pantallas CU-PRE-04 a CU-PRE-23 |
| Habilitación de campos CU-PRE-04 a CU-PRE-23 | Sistema (RN05), tras clic en "Enviar comentarios" | Pantallas CU-PRE-04 a CU-PRE-23 |
| Cambio de estado a "Observado" | Sistema (FA01, paso 1.4) | Estado del proyecto |
| Cambio de estado a "Proyecto Viable" | Sistema (FA02, paso 2.3) | Estado del proyecto |
| Habilitación de "Ir a Elegibilidad" | Sistema (FA02, paso 2.6), solo la primera vez | CU-PRE-25 "Elegibilidad" |
| Mensaje "Enviado a Elegibilidad" (identificado mediante anexo Excel, no documentado en el PDF original) | Sistema, posiblemente al clic en "Ir a Elegibilidad" (FA02, paso 2.4) — correspondencia no confirmada por el documento fuente | CU-PRE-25 "Elegibilidad" (redirección descrita en FA02, paso 2.4) |

---

# Integraciones

> No especificado en el documento.

---

# Datos Pendientes de Definir

- RN10 exige que el sistema muestre cuántas veces se ha devuelto el proyecto y permita consultar en pantalla las observaciones de cada devolución; sin embargo, ni el mockup del Anexo A.1 ni la tabla de formatos del Anexo B muestran o describen un contador de devoluciones ni una vista de historial de observaciones por devolución. No se puede determinar cómo debe implementarse esta funcionalidad.
- RN01 establece que "El Viabilizador es el único que puede registrar/editar información en la sección", lo cual parece contradecir las acciones de registro/edición atribuidas al Técnico URP en el resto del documento (carga de documentos, clic en "Solicitar Viabilidad", ajuste de campos en FA01 paso 1.6). No se puede determinar el alcance exacto de "la sección" a la que se refiere RN01.
- El botón "Guardar Ajustes" mencionado en RN11 no aparece documentado en la tabla de Botones del Anexo B de este caso de uso, ni se identifica claramente en el mockup del Anexo A.1. No se puede determinar si este botón pertenece a la pantalla de este caso de uso o a la de CU-PRE-26 "Opinión Técnica".
- El indicador "R B/C" mostrado en el mockup del campo "Indicadores de Evaluación" (Anexo A.1) no está documentado ni definido en este caso de uso ni en los casos de uso previos de la serie relacionados con indicadores de evaluación (CU-PRE-21.5 solo documenta VAN y TIR). No se puede determinar su fórmula ni su origen.
- El paso 2.3 del Flujo Alternativo 2 indica que, cuando el proyecto ya contaba con Elegibilidad, el mensaje emergente "incluirá el texto 'Es necesario continuar con la gestión de la Opinión Técnica'" y "mostrará el botón 'Ir a OT'"; sin embargo, la Variante 2 del mockup del Anexo A.2 solo muestra el texto genérico y un botón "ACEPTAR", sin el texto adicional ni el botón "Ir a OT" descritos en el flujo. No se puede determinar cuál de las dos versiones (texto del flujo o mockup) debe implementarse.
- No se especifica el disparador (evento que inicia el caso de uso) de forma explícita, más allá de lo descrito en el Flujo Básico 1.
- No se especifica la prioridad del caso de uso.
- No se especifica una tabla de códigos de error para este caso de uso (más allá del mensaje de validación descrito en RN11).
- **Anexo Excel (v1.1):** confirmación con el negocio del momento exacto del flujo en que se muestra el mensaje "¡Enviado! El proyecto fue enviado a Elegibilidad exitosamente" (identificado únicamente en el anexo Excel `CU-PRE-24__ANEXO__Ficha_Viabilidad.xlsx`, sin referencia en el PDF original). Se presume, sin confirmación del documento fuente, que ocurre al dar clic en el botón "Ir a Elegibilidad" del Anexo A.2 (FA02, paso 2.4), inmediatamente antes de la redirección a CU-PRE-25 "Elegibilidad"; no se especifican los botones que acompañan a este mensaje.