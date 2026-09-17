---
id: CU-PRE-21.5
codigo: CU-PRE-21.5
nombre: Flujo de Caja Financiero
modulo: Preinversión
submodulo: Evaluación
version: "1.0"
fuente_pdf: CU-PRE-21_5_Flujo_de_Caja_Financiero_AGO__2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 8

actor_principal: Técnico URP

actores_secundarios: []

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-17", "CU-PRE-18"]

casos_relacionados: ["CU-PRE-23"]

roles: ["Técnico URP"]

pantallas: ["Flujo de Caja e Indicadores (Anexo A.1)", "Botón Guardar (Anexo A.2)"]

procesos: []

servicios_externos: []

entidades: ["Flujo de Caja e Indicadores", "Ingreso", "Egreso", "Indicadores de evaluación (VAN, TIR)"]

catalogos: []

palabras_clave: ["Flujo de caja", "Ingresos", "Egresos", "VAN", "TIR", "Tasa de descuento", "Indicadores de evaluación", "Inversión estimada"]

ultima_actualizacion: "AGO 2025"

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    FA01:
      pagina: 4
  reglas_negocio:
    RN01:
      pagina: 4
    RN02:
      pagina: 5
    RN03:
      pagina: 5
  anexos:
    A1:
      nombre: Flujo de Caja e Indicadores
      pagina: 6
    A2:
      nombre: Botón Guardar
      pagina: 6
    B1:
      nombre: Requerimientos Funcionales - Formatos (Flujo de Caja e Indicadores)
      pagina: 7
    Seccion_Indicadores:
      nombre: Sección Indicadores (VAN, TIR)
      pagina: 8
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|-------|---------|--------------|-------|-------|
| AGO 2025 | 1.0 | Primera Versión | Equipo Preinversión | No especificado en el documento. |

---

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Flujo de Caja Financiero |
| Código | CU-PRE-21.5 |
| Módulo | Preinversión |
| Fuente | CU-PRE-21.5 Flujo de Caja Financiero_AGO_ 2025_V1_F |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Años a proyectar
- Tasa de descuento
- Ingresos
- Egresos

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite al actor "Técnico URP" el registro de los ingresos y egresos de un proyecto a fin de que el Sistema genere el flujo de caja financiero y calcule los indicadores de evaluación.

# Actor Principal

Técnico URP

---

# Actores Secundarios

> No especificado en el documento. RN01 menciona genéricamente a "todos los demás actores" con permiso de solo visualización, sin nombrar actores específicos (a diferencia de otros casos de uso de la serie, este documento no menciona explícitamente al "Técnico PRE").

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP de CU-PRE-01 "Registro de proyectos".
2. Contar con la Ruta de Preinversión generada en CU-PRE-03.5 "Selección y registro de etapas".
3. CU-PRE-17 "Presupuesto de inversión".
4. CU-PRE-18 "Flujo de costos de Operación y Mantenimiento".

> Nota de ambigüedad: el documento presenta primero un texto con verbo explícito ("Contar con CUP de CU-PRE-01..., y, con la Ruta de Preinversión generada en CU-PRE-03.5...") y a continuación lista, sin verbo ni encabezado propio, las referencias "CU-PRE-17 'Presupuesto de inversión'" y "CU-PRE-18 'Flujo de costos de Operación y Mantenimiento'". Se incorporan a la lista de Precondiciones por continuidad de formato, pero la redacción exacta de la condición que las vincula (p. ej. "haber completado" o "contar con") no está especificada en el documento.

---

# Flujo Principal

## FB – Flujo Básico

1. Técnico URP ingresa a la pestaña "Evaluación" en la sección "Flujo de Caja Financiero" (Anexo A.1).
2. Técnico URP registra la información de los campos mostrados en el Anexo A.1, da clic en el botón Guardar.
3. Sistema realiza el cálculo del flujo neto de caja y de los indicadores de evaluación del proyecto.

> Nota de ambigüedad: a diferencia de otros casos de uso de la serie, el Flujo Básico no describe de forma explícita, paso a paso, la aparición del mensaje emergente de confirmación (Anexo A.2 "¡Guardado!") ni el clic en "Aceptar" tras dar clic en "Guardar". El Anexo A.2 documenta dicho mensaje, pero el Flujo Básico no narra la secuencia de interacción asociada. Ver Observaciones.

---

# Flujos Alternos

## FA01 – Flujo Alternativo 1 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1.1. Técnico URP da clic en el botón "Siguiente".

1.2. Sistema avanza a la siguiente sección CU-PRE-23 "Indicadores del proyecto" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no desarrolla una tabla de excepciones con código, descripción y consecuencia para este caso de uso.

# Postcondiciones

1. El proyecto cuenta con el flujo de caja financiero y los indicadores de evaluación financiera y avanza a CU-PRE-23 "Indicadores del Proyecto".

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único actor que puede registrar información en los campos del Anexo A.1. Todos los demás actores únicamente podrán visualizar la información según sus credenciales.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-21.5.

## RN02

**Descripción:** El Técnico URP podrá adicionar o eliminar filas para los ingresos en la tabla de la pantalla "Flujo de caja e indicadores".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-21.5.

## RN03

**Descripción:** Los valores de los flujos de EGRESOS de la tabla serán trasladados y ubicados en los años correspondientes, de acuerdo con lo siguiente:

a) "Inversión estimada": procederán de la fila "Inversión estimada (Precios de Mercado" del CU-PRE-17 "Presupuesto de inversión". En la tabla "Flujo de caja e Indicadores" la inversión estimada se ubicará a partir del "año 0" hasta el año que corresponda a la cantidad de períodos registrados en la tabla "Inversión del proyecto" del CU-PRE-17 "Presupuesto de inversión" (por ejemplo, si en el CU-PRE-17 "Presupuesto de inversión" se registró la inversión en Período 0, Período 1 y Período 2, la tabla "Flujo de Caja e Indicadores" mostrará la "Inversión estimada" en los Períodos 0, 1 y 2).

b) "Costos de Operación": Procederán de la fila "Total (Precios de Mercado)" de la tabla "Costos de Operación" del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". En la tabla "Flujo de caja e Indicadores" los Costos de Operación se posicionarán en el periodo en que se registró la información en CU-PRE-18 "Flujo de costos de Operación y Mantenimiento"; asimismo, la cantidad de periodos en que se coloquen los costos de operación debe ser igual al registrado en el campo "Vida útil (periodos a proyectar)" del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento".

c) "Costos de Mantenimiento": Procederán de la fila "Total (Precios de Mercado)" de la tabla "Costos de Mantenimiento" del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". En la tabla "Flujo de caja e Indicadores" los Costos de Mantenimiento se posicionarán en el periodo en que se registró la información en CU-PRE-18 "Flujo de costos de Operación y Mantenimiento"; asimismo, la cantidad de periodos en que se coloquen los costos de mantenimiento debe ser igual al registrado en el campo "Vida útil (periodos a proyectar)" del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-21.5.

> Nota de ambigüedad: el inciso (a) especifica explícitamente que la "Inversión estimada" se ubica "a partir del año 0"; sin embargo, los incisos (b) y (c) solo indican que "Costos de Operación" y "Costos de Mantenimiento" se posicionan "en el periodo en que se registró la información en CU-PRE-18", sin aclarar si ese posicionamiento inicia en el "Período 0" o en el "Período 1" de la tabla "Flujo de Caja e Indicadores" (cuyas columnas de período, según el mockup del Anexo A.1, comienzan en "0"). Esta ambigüedad puede afectar la implementación del posicionamiento exacto de dichos costos. Ver sección "Datos Pendientes de Definir".
>
> Nota adicional: RN03 solo describe el traslado automático de los flujos de EGRESOS (Inversión estimada, Costos de Operación, Costos de Mantenimiento); no existe una regla equivalente para el traslado o cálculo automático de los INGRESOS, los cuales, según el Flujo Básico y RN02, se registran y gestionan manualmente por el Técnico URP.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Vida útil (períodos a Proyectar) | Campo que se diligencia automáticamente con la información del campo "Vida útil (períodos a proyectar)" del Anexo A.1 del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Campo obligatorio. | Numérico | Numérico | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: No. Ver Observaciones respecto a que un campo no editable/auto-diligenciado se marque como "obligatorio". |
| Tasa de descuento | Campo para que el Técnico URP registre la tasa de descuento a la que se evaluará el proyecto. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: Sí. Este campo es utilizado como variable "i" en las fórmulas de VAN y TIR (ver más abajo). |
| Periodos (0 hasta n) | Campos para que el Técnico URP registre para cada año los ingresos y egresos del proyecto según corresponda en US$. El sistema deberá agregar el separador de miles (,). Campo obligatorio. | Moneda | Moneda | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. |
| Ingresos | Campo para que el Técnico URP registre los tipos de ingresos con que contará el proyecto. Campo obligatorio. Al menos se debe registrar un ingreso. | Texto | Texto | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. Adición/eliminación de filas según RN02. |
| Total ingresos | Campo que muestra el total de ingresos por periodo en US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Inversión estimada | Campo que se diligencia automáticamente con la información del total de la inversión estimada por período a precios de mercado según se registraron en el CU-PRE-17 "Presupuesto de inversión". Campo obligatorio. Procede de la fila "Inversión estimada (Precios de Mercado)" del CU-PRE-17 y se ubica a partir del "año 0" según la cantidad de períodos registrados en CU-PRE-17 (RN03, inciso a). | Moneda | Moneda | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: No. |
| Costos de Operación | Campo que se diligencia automáticamente con la información del total de los costos de operación por período a precios de mercado según se registraron en el CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Procede de la fila "Total (Precios de Mercado)" de la tabla "Costos de Operación" del CU-PRE-18 y se posiciona en el periodo en que se registró la información en dicho CU (RN03, inciso b). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver nota de ambigüedad en RN03 respecto al periodo inicial de posicionamiento. |
| Costos de Mantenimiento | Campo que se diligencia automáticamente con la información del total de los costos de mantenimiento por período a precios de mercado según se registraron en el CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Procede de la fila "Total (Precios de Mercado)" de la tabla "Costos de Mantenimiento" del CU-PRE-18 y se posiciona en el periodo en que se registró la información en dicho CU (RN03, inciso c). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver nota de ambigüedad en RN03 respecto al periodo inicial de posicionamiento. |
| Total egresos | Campo que muestra el total de egresos por periodo año en US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Flujo neto de caja | Campo que muestra el flujo neto de caja. El Sistema lo calculará mediante la resta de los ingresos menos egresos, así: [fórmula tal como aparece en el documento fuente, transcripción literal de los caracteres extraídos, con caracteres no legibles con claridad]: "௡ݏ݋ݏ݁ݎ݃ܧ − ௡ݏ݋ݏ݁ݎ݊݃ܫ = ௡݆ܿܽܽ ݀݁ ݋ݐ݊݁ ݋݆�𝑙𝐹�". El propio texto que acompaña la fórmula aclara en prosa que el cálculo corresponde a "la resta de los ingresos menos egresos" para cada período (Flujo neto de caja del período n = Ingresos del período n menos Egresos del período n). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| VAN | Campo que muestra el Valor Actual Neto del proyecto. Se calculará a través de la siguiente fórmula: [fórmula tal como aparece en el documento fuente, transcripción literal de los caracteres extraídos, con caracteres no legibles con claridad — ver sección "Datos Pendientes de Definir"]: "∑ + 0ܫ− = ܰܣܸ ௡ ௧=1 𝐹௧ + 0ܫ− = ௧)݅ + 1( 𝐹1 (1 + ݅) + 𝐹2 (1 + ݅) 2 + ⋯ + 𝐹௡ (1 + ݅)௡". Donde: Ft = Flujos de dinero en cada periodo año, el Sistema los tomará de la fila "Flujo neto de caja". I0 = Inversión realizada en el momento inicial, el Sistema lo tomará de la fila "Flujo neto de caja" del periodo 0. n = número de períodos de tiempo (años). i = tasa de descuento, el Sistema lo tomará del campo "Tasa de descuento". Si el cálculo genera "#indeterminado#", el campo debe aparecer "von N/A" (transcripción literal; ver Observaciones respecto a un posible error tipográfico de "con N/A"). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| TIR | Campo que muestra la tasa de rentabilidad en donde el VAN es cero. La TIR (i) se calculará con la siguiente fórmula: [fórmula tal como aparece en el documento fuente, transcripción literal de los caracteres extraídos, con caracteres no legibles con claridad — ver sección "Datos Pendientes de Definir"]: "∑ = ܴܫܶ ௡ ௧=0 𝐹௡ (1 + ݅)௡ = 0". Donde: Fn = Flujos de dinero en cada periodo, el Sistema los tomará de la fila "Flujo neto de caja". n = número de períodos de tiempo (años), el Sistema lo tomará desde el periodo 0 hasta el periodo n. i = tasa de descuento, el Sistema lo tomará del campo "Tasa de descuento". Si el cálculo genera "#indeterminado#", el campo debe aparecer "von N/A" (transcripción literal; ver Observaciones respecto a un posible error tipográfico de "con N/A"). | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Vida útil (períodos a Proyectar) | Campo obligatorio. | No especificado en el documento. |
| Periodos (0 hasta n) | Campo obligatorio. | No especificado en el documento. |
| Ingresos | Campo obligatorio. Al menos se debe registrar un ingreso. | No especificado en el documento. |
| Inversión estimada | Campo obligatorio. | No especificado en el documento. |
| VAN / TIR | Si el cálculo genera "#indeterminado#", el campo debe mostrar "N/A" (transcrito en el documento como "von N/A"). | No especificado en el documento (solo se indica el valor a mostrar, "N/A", no un mensaje de validación adicional). |

---

# Errores

> No especificado en el documento. El documento no desarrolla una tabla de códigos de error para este caso de uso.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar información en los campos del Anexo A.1; adicionar o eliminar filas de ingresos. | RN01, RN02 |
| Todos los demás actores (no especificados individualmente en el documento) | Visualizar la información según sus credenciales. | RN01 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de proyectos" (precondición)
- CU-PRE-03.5 "Selección y registro de etapas" (precondición)
- CU-PRE-17 "Presupuesto de inversión" (precondición; origen de "Inversión estimada", RN03 inciso a)
- CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" (precondición; origen de "Vida útil", "Costos de Operación" y "Costos de Mantenimiento", RN03 incisos b y c)
- CU-PRE-23 "Indicadores del Proyecto" / "Indicadores del proyecto" (postcondición; FA01)

**Procesos relacionados:**
> No especificado en el documento.

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Pantalla: Flujo de Caja e Indicadores (Anexo A.1)

**Descripción:** Pantalla identificada en el encabezado como "Flujo de caja e Indicadores", donde el Técnico URP registra la tasa de descuento y los ingresos del proyecto, y visualiza los egresos trasladados automáticamente desde otros casos de uso, el flujo neto de caja y los indicadores de evaluación (VAN, TIR).

**Campos:**
- Vida útil (períodos a proyectar)
- Tasa de descuento
- Tabla "Ingresos y Egresos" por período (columnas 0 a n): Ingresos (encabezado de sección), Ingreso 1, Ingreso 2, Ingreso 3, Ingreso 4, Total ingresos, Egresos (encabezado de sección), Inversión Estimada, Costos de operación, Costos de mantenimiento, Total Egresos, Flujo neto de caja
- Indicadores de evaluación: VAN, TIR

**Botones:**
- Guardar
- Siguiente

**Acciones:**
- Registro de la Tasa de descuento y de los Ingresos (FB, paso 2).
- Clic en "Guardar" (FB, paso 2), tras lo cual el Sistema calcula el flujo neto de caja y los indicadores (FB, paso 3).
- Clic en "Siguiente" (FA01).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

A diferencia de otros casos de uso de la serie, este mockup no presenta valores numéricos de ejemplo concretos; únicamente muestra los siguientes textos de marcador de posición (placeholder) y etiquetas de fila/columna:

- Vida útil (períodos a proyectar): "n" (marcador de posición, sin valor numérico de ejemplo).
- Tasa de descuento: "%" (marcador de posición, sin valor numérico de ejemplo).

| Ingresos y Egresos | 0 | 1 | 2 | 3 | ... | n |
|---------------------|---|---|---|---|-----|---|
| **Ingresos** | | | | | | |
| Ingreso 1 | | | | | | |
| Ingreso 2 | | | | | | |
| Ingreso 3 | | | | | | |
| Ingreso 4 | | | | | | |
| **Total ingresos** | | | | | | |
| **Egresos** | | | | | | |
| Inversión Estimada | | | | | | |
| Costos de operación | | | | | | |
| Costos de mantenimiento | | | | | | |
| **Total Egresos** | | | | | | |
| **Flujo neto de caja** | | | | | | |

Indicadores de evaluación: VAN: (campo vacío) — TIR: (campo vacío)

## Pantalla: Botón Guardar (Anexo A.2)

**Descripción:** Ventana modal de confirmación mostrada al guardar información.

**Campos:** No aplica (mensaje informativo).

**Botones:**
- Aceptar

**Acciones:**
- Confirmación de guardado exitoso.

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**
- Ícono de confirmación (check).
- Texto: "¡Guardado!"
- Texto: "Sus datos han sido guardados exitosamente."
- Botón: "Aceptar"

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Documentado en el Anexo A.2; el Flujo Básico no narra explícitamente el paso de visualización de este mensaje ni el clic en "Aceptar" (ver Observaciones). |
| Resultado de cálculo (VAN / TIR) | "N/A" (transcrito literalmente en el documento como "von N/A") | Cuando el cálculo de VAN o de TIR genera "#indeterminado#". |

---

# Observaciones

- El Flujo Básico no narra de forma explícita, paso a paso, la aparición del mensaje emergente de confirmación (Anexo A.2 "¡Guardado!") ni el clic en "Aceptar" tras dar clic en "Guardar", a diferencia de otros casos de uso de la serie que sí detallan esta secuencia como un Flujo Alternativo independiente. El Anexo A.2 documenta el mensaje, pero no está referenciado explícitamente desde un paso del Flujo Básico o de un Flujo Alternativo.
- El campo "Vida útil (períodos a Proyectar)" se documenta en el Anexo B con la columna "Editable" en "No" (es decir, se diligencia automáticamente desde CU-PRE-18), pero al mismo tiempo se indica como "Campo obligatorio". No se aclara si esta obligatoriedad se refiere a que el campo de origen en CU-PRE-18 deba estar diligenciado, o a una validación propia de este caso de uso.
- El texto "el campo debe aparecer von N/A", presente de forma idéntica en las descripciones de los campos VAN y TIR, parece corresponder a un posible error tipográfico del documento original (probablemente "con N/A"); se transcribe literalmente tal como aparece, sin corregirlo.
- Las fórmulas de VAN y TIR presentan caracteres que no se muestran con claridad en el documento fuente. Se transcriben literalmente tal como fueron extraídas, junto con la definición completa de variables ("Donde: ...") que sí es legible en el documento. Ver sección "Datos Pendientes de Definir" respecto a la notación exacta de ambas fórmulas.
- RN03 especifica explícitamente que la "Inversión estimada" se ubica "a partir del año 0" en la tabla "Flujo de caja e Indicadores"; sin embargo, para "Costos de Operación" y "Costos de Mantenimiento" (incisos b y c) solo se indica que se posicionan "en el periodo en que se registró la información en CU-PRE-18", sin aclarar si dicho posicionamiento inicia en el "Período 0" o en el "Período 1" de la tabla de este caso de uso. Ver sección "Datos Pendientes de Definir".
- RN03 regula únicamente el traslado automático de los flujos de EGRESOS (Inversión estimada, Costos de Operación, Costos de Mantenimiento); no existe una regla de negocio equivalente para los INGRESOS, los cuales, de acuerdo con el Flujo Básico y RN02, se registran y gestionan manualmente por el Técnico URP (adición/eliminación de filas), sin cálculo ni traslado automático desde otro caso de uso.
- A diferencia de otros casos de uso de la serie (p. ej. CU-PRE-18, CU-PRE-20), este documento no incluye reglas de negocio sobre el sombreado de campos pendientes de completar en color rojo, ni sobre el ícono de ayuda contextual "?"; el documento tampoco menciona la existencia de catálogos.
- El mockup del Anexo A.1 no incluye valores numéricos de ejemplo concretos (a diferencia de los mockups de otros casos de uso de la serie), por lo que la sección "Pantallas" solo pudo transcribir los marcadores de posición ("n" y "%") y las etiquetas de fila/columna que sí se muestran en la imagen.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Flujo de Caja e Indicadores | Registro consolidado de ingresos, egresos, flujo neto de caja e indicadores de evaluación (VAN, TIR) de un proyecto, por período. | Registro/consulta según credenciales (RN01); cálculo automático del flujo neto de caja y de los indicadores tras "Guardar" (FB, paso 3). |
| Ingreso | Fila registrada manualmente por el Técnico URP para cada tipo de ingreso del proyecto. | Registro manual (FB, paso 2); adición/eliminación de filas (RN02). |
| Egreso | Conjunto de filas "Inversión Estimada", "Costos de Operación" y "Costos de Mantenimiento", trasladadas automáticamente desde CU-PRE-17 y CU-PRE-18. | Traslado/cálculo automático según origen y posicionamiento por período (RN03, incisos a, b y c). |
| Indicadores de evaluación (VAN, TIR) | Indicadores financieros calculados a partir del flujo neto de caja y la tasa de descuento. | Cálculo automático mediante fórmula (Anexo B, campos VAN y TIR); manejo del caso "#indeterminado#" mostrando "N/A" (transcrito "von N/A" en el documento). |

---

# Catálogos Detectados

> No especificado en el documento. Este documento no incluye catálogos (a diferencia de otros casos de uso de la serie, como CU-PRE-18 y CU-PRE-20).

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Cálculo del flujo neto de caja | Sistema (FB, paso 3), tras clic en "Guardar" | Fila "Flujo neto de caja" (Anexo A.1) |
| Cálculo de indicadores de evaluación | Sistema (FB, paso 3), tras clic en "Guardar" | Campos "VAN" y "TIR" (Anexo A.1) |
| Traslado automático de Inversión estimada | Sistema (RN03, inciso a), desde CU-PRE-17 "Presupuesto de inversión" | Fila "Inversión Estimada" (Anexo A.1), desde el "año 0" |
| Traslado automático de Costos de Operación y Mantenimiento | Sistema (RN03, incisos b y c), desde CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" | Filas "Costos de operación" y "Costos de mantenimiento" (Anexo A.1) |

---

# Integraciones

> No especificado en el documento.

---

# Datos Pendientes de Definir

- Las fórmulas matemáticas de "VAN" y "TIR" (sección "Indicadores" del Anexo B) no pueden determinarse con precisión a partir del documento: los caracteres de dichas fórmulas presentan una notación que no es legible con claridad en el documento fuente. Se cuenta con la definición completa de las variables (Ft, I0, n, i para VAN; Fn, n, i para TIR), pero no con la notación exacta de las fórmulas, lo que impide implementar el cálculo tal como está descrito sin recurrir a un criterio externo al documento.
- No se aclara si el posicionamiento de "Costos de Operación" y "Costos de Mantenimiento" en la tabla "Flujo de Caja e Indicadores" (RN03, incisos b y c) inicia en el "Período 0" o en el "Período 1", a diferencia de "Inversión estimada" (inciso a), para la cual sí se especifica explícitamente que inicia "a partir del año 0".
- No se especifica el disparador (evento que inicia el caso de uso) de forma explícita.
- No se especifica la prioridad del caso de uso.
- No se especifica una tabla de códigos de error para este caso de uso.
- No se aclara si el texto "el campo debe aparecer von N/A" corresponde a un error tipográfico de "con N/A" o a un texto/estado distinto no descrito en el documento.
