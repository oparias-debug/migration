---
id: CU-PRE-18
codigo: CU-PRE-18
nombre: Flujo de costos de Operación y Mantenimiento
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-18_Flujo_de_Costos_de_O_M_AGO_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 9

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-14", "CU-PRE-11", "CU-PRE-15", "CU-PRE-16", "CU-PRE-17"]

casos_relacionados: ["CU-PRE-21"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Presupuesto de Operación y Mantenimiento (Anexo A.1)", "Detalle de Actividad (Anexo A.2)", "Mensaje emergente Guardar (Anexo A.3)"]

procesos: []

servicios_externos: []

entidades: ["Presupuesto de Operación y Mantenimiento", "Actividad", "Detalle de Actividad / Insumo", "Período", "Catálogo Insumos"]

catalogos: ["Insumos"]

palabras_clave: ["Operación y Mantenimiento", "Presupuesto", "Costos", "Vida útil", "Tasa de crecimiento", "Precios ajustados", "Precios de mercado", "Factor de corrección"]

ultima_actualizacion: "AGO 2025"

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    SF-01:
      pagina: 4
    FA-01.1:
      pagina: 4
    FA-01.2:
      pagina: 4
    FA-02:
      pagina: 5
    FA-03:
      pagina: 5
  reglas_negocio:
    RN01:
      pagina: 5
    RN02:
      pagina: 5
    RN03:
      pagina: 5
    RN04:
      pagina: 5
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
  anexos:
    A1:
      nombre: Presupuesto de Operación y Mantenimiento
      pagina: 7
    A2:
      nombre: Detalle de Actividad
      pagina: 8
    A3:
      nombre: Guardar
      pagina: 8
    B1:
      nombre: Requerimientos Funcionales - Formatos
      pagina: 8
    Catalogo_Insumos:
      nombre: Catálogo Insumos
      pagina: 9
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."

nota_nomenclatura_editorial: "El identificador 'SF-01' usado en este documento (frontmatter, sección 'Flujos Alternos', 'Entidades Detectadas' y 'Eventos del Sistema') es una convención editorial introducida para estandarizar la nomenclatura de subflujos en la serie de casos de uso. El PDF original denomina esta sección literalmente 'Flujo 1 – F1 Detalle de Actividad', sin usar la etiqueta 'SF-01'."
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
| Nombre | Flujo de costos de Operación y Mantenimiento |
| Código | CU-PRE-18 |
| Módulo | Preinversión |
| Fuente | CU-PRE-18 Flujo de Costos de O&M_AGO 2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Presupuesto de Operación y Mantenimiento
  - N°
  - Actividad
  - Costo por período
  - Total
  - Vida útil (periodos a proyectar)
  - Tasa de crecimiento de los costos
- Detalle de Actividad
  - Insumo Tipo
  - Periodos 1
  - Total

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite al actor "Técnico URP" registrar en el sistema los presupuestos de operación y mantenimiento considerados para la vida útil del proyecto (n).

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico PRE (mencionado en RN02, con permisos de solo visualización sobre todas las Unidades Ejecutoras).

> Nota: el campo "Actores" de la Identificación del caso de uso solo lista a "Técnico URP". La mención de "Técnico PRE" y de "todos los demás actores" proviene de las Reglas de Negocio RN01 y RN02 (ver sección Observaciones).

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP de CU-PRE-01 "Registro de Proyectos".
2. Contar con la Ruta de Preinversión generada en CU-PRE-03.5 "Selección y registro de etapas".
3. CU-PRE-14 Análisis ambiental.
4. CU-PRE-11 Descripción técnica.
5. CU-PRE-15 Análisis de riesgos.
6. CU-PRE-16 Análisis legal.
7. CU-PRE-17 Presupuesto de inversión.

> Nota de ambigüedad: el documento presenta primero un texto con verbo explícito ("Contar con CUP de CU-PRE-01..., con la Ruta de Preinversión generada en CU-PRE-03.5..., CU-PRE-14 Análisis ambiental") y a continuación lista, sin verbo ni encabezado propio, las referencias "CU-PRE-11 Descripción técnica", "CU-PRE-15 Análisis de riesgos", "CU-PRE-16 Análisis legal" y "CU-PRE-17 Presupuesto de inversión". Se incorporan a la lista de Precondiciones por continuidad de formato, pero la redacción exacta de la condición que las vincula (p. ej. "haber completado" o "contar con") no está especificada en el documento.

---

# Flujo Principal

## FB – Flujo Básico

1. Técnico URP ingresa a la pestaña "Formulación" en la sección "Presupuesto de Operación y Mantenimiento".
2. Técnico URP selecciona el tipo de costo a registrar.
3. Sistema, según la selección, muestra las tablas de acuerdo con RN05.
4. Técnico URP ingresa información en los campos "Vida útil (periodos a proyectar)" y "Tasa de crecimiento de costos", da clic en el botón "Aceptar" y genera columnas según RN06.
5. Técnico URP da clic en el botón "Agregar Actividad".

---

# Flujos Alternos

## SF-01 – Flujo 1 (F1) Detalle de Actividad

> Nota de nomenclatura editorial: el PDF titula esta sección literalmente como "Flujo 1 – F1 Detalle de Actividad". El identificador "SF-01" es una convención editorial introducida para estandarizar la nomenclatura de subflujos en la serie de casos de uso; no aparece como tal en el documento fuente.

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP registra la actividad en el campo "Nombre de la Actividad". Registra los costos de cada insumo según aplique en la columna "Período 1 (US$)" a precios de mercado.
2. Sistema calcula el valor de la columna "Período 1 (US$) Precio Ajustados" para cada insumo mediante la fórmula siguiente: Periodo 1 precios ajustados = Periodo 1 precios de mercado * FC
3. Técnico URP da clic en el botón "Guardar".
4. Sistema traslada la actividad registrada en el campo "Nombre de la Actividad" y el valor de la celda "Total (Precios de mercado)" Precios de Mercado a la tabla del Anexo A.1; ubicará este último en el Período 1, y proyectará las cifras para los períodos siguientes según RN06.

**Resultado**

> No especificado en el documento.

## FA-01.1 – Flujo Alternativo 1.1 (del Flujo Detalle de Actividad) Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón "Guardar".
2. Sistema muestra mensaje emergente del Anexo A.3.
3. Técnico URP da clic en "Aceptar" al mensaje emergente.
4. Sistema vacía los campos de la columna "Periodo 1" (para que el Técnico URP continúe con el registro de costos de otras actividades).

**Resultado**

> No especificado en el documento.

## FA-01.2 – Flujo Alternativo 1.2 (del Flujo Detalle de Actividad) Salir

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón "Salir".
2. Sistema muestra el mensaje emergente "Los datos no serán guardados".
3. Técnico URP da clic en "Aceptar" al mensaje emergente.
4. Sistema regresa a la pestaña "Costos de Operación y Mantenimiento" sin guardar cualquier información que se haya registrado.

**Resultado**

> No especificado en el documento.

## FA-02 – Flujo Alternativo 2 Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón "Guardar".
2. Sistema muestra mensaje emergente del Anexo A.3.
3. Técnico URP da clic en "Aceptar" al mensaje emergente.
4. Sistema guarda la información registrada y se mantiene en la pantalla "Presupuesto de Operación y Mantenimiento".

**Resultado**

> No especificado en el documento.

## FA-03 – Flujo Alternativo 3 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón "Siguiente".
2. Sistema avanza a la siguiente sección (Parámetros de Evaluación) para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no desarrolla una tabla de excepciones con código, descripción y consecuencia para este caso de uso.

# Postcondiciones

1. CU-PRE-21 Flujo de Caja y cálculo de Indicadores.

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN03

**Descripción:** El Técnico URP podrá adicionar y/o eliminar más filas a la tabla de la pantalla "Costos de Operación y Mantenimiento".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN04

**Descripción:** En la indicación "Seleccione el tipo de costo a registrar" se contará con botones radiales para seleccionar una opción (Anexo A.1), de acuerdo con la selección que realice el Técnico URP se presentarán los siguientes escenarios:
- Si se selecciona "Operación", el Sistema desplegará los campos "Vida útil (periodos a proyectar)" y "Tasa de crecimiento de los costos", y la tabla "Costos de Operación", dicha tabla mostrará por defecto solo el Periodo 1.
- Si se selecciona "Mantenimiento", el Sistema desplegará los campos "Vida útil (periodos a proyectar)" y "Tasa de crecimiento de los costos" y la tabla "Costos de Mantenimiento" dicha tabla mostrará por defecto solo el Periodo 1.
- Si se selecciona "O&M", el Sistema desplegará los campos "Vida útil (periodos a proyectar)" y "Tasa de crecimiento de los costos" y las tablas "Costos de Operación" y "Costos de Mantenimiento" dichas tablas mostrarán por defecto solo el Periodo 1.
- Si se selecciona "No aplica", el Sistema no desplegará los campos ni las tablas, y para continuar con el registro de la información en otra sección o pestaña, el Técnico URP dará clic en el botón "Guardar" y en el botón "Siguiente", según lo descrito en los Flujos Alternativos 1 o 2.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN05

**Descripción:** Dependiendo de la cantidad de Períodos que el Técnico URP registre en el campo "Vida útil (Periodos a proyectar)" (Ver Anexo A.1), el Sistema agregará la misma cantidad de columnas para los "Periodos" en las tablas "Costos de Operación" y "Costo de Mantenimiento", según aplique. Por ejemplo, si el Técnico URP registra 15 periodos, la cantidad de columnas en el campo "Periodos" será de 15 mostrando como encabezado de cada columna el Periodo correspondiente.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN06

**Descripción:** En las tablas "Costos de Operación" y "Costos de Mantenimiento" los datos (Costo por período) para cada Actividad en el campo "Período 1" serán trasladados desde la celda "Total" (a precios de mercado) de la tabla "Detalle de Actividad" (Anexo A.2); los datos (Costo por período) para los campos "Período 2" hasta el "Período n" serán mostrados de manera automática en las columnas que correspondan mediante el cálculo realizado por el Sistema con la fórmula siguiente:

> [Fórmula tal como aparece en el documento fuente]: "(r + 1) Costoperiodo base = n Costoperiodo n / n" — los caracteres de la fórmula en el documento original no se muestran con claridad (símbolos y subíndices ilegibles en la extracción del PDF). Ver sección "Datos Pendientes de Definir".

Donde:
- Costoperiodo base: Costo del periodo inicial.
- r: Tasa de crecimiento (Valor registrado en el campo "Tasa de crecimiento de costos").
- n: Posición del periodo respecto al periodo de inicio (número de periodos entre el periodo base y el periodo n).

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN07

**Descripción:** La tabla "Detalle de Actividad" mostrará por defecto en la columna "Insumo Tipo" los insumos listados según catálogo "Insumos" y en la columna "FC" su factor de corrección (FC) correspondiente.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN08

**Descripción:** En las tablas "Costos de Operación" y "Costos de Mantenimiento" en la fila "Total precio de mercado (P.M)", el Sistema realizará de forma automática la suma de los valores a precios de mercado de la columna correspondiente.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN09

**Descripción:** El cálculo del presupuesto se deberá realizar tanto a precios de mercado como a precios ajustados. Para ello, las tablas de los Anexos A.1 y A.2, deberán calcularse tanto a precios de mercado como a precios ajustados, este último aplicando el Factor de Corrección (FC) a la tabla a precios de mercado. La tabla de presupuesto a precios ajustados sólo podrá ser visible para los usuarios internos.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN10

**Descripción:** El Sistema deberá redondear las decenas de los montos registrados en las casillas "TOTAL INVERSIÓN (PRECIOS DE MERCADO)" y "TOTAL INVERSIÓN (PRECIOS AJUSTADOS)" del Anexo A.1. Deberá redondear siempre por arriba y a múltiplos de 5 o de 10. Por ejemplo, si el valor es de $1,750,427.58 lo redondeará a $1,750,430.00; y si el valor es de $1,750,423.58 lo redondeará a $1,750,425.00.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

> Nota de ambigüedad: RN10 hace referencia a casillas "TOTAL INVERSIÓN (PRECIOS DE MERCADO)" y "TOTAL INVERSIÓN (PRECIOS AJUSTADOS)" del Anexo A.1, mientras que el Anexo A.1 (mockup) y el resto de las reglas de este caso de uso utilizan las etiquetas "TOTAL (PRECIOS DE MERCADO)" y "TOTAL (PRECIOS AJUSTADOS)" para las tablas "Costos de Operación" y "Costos de Mantenimiento", sin la palabra "INVERSIÓN". No se puede determinar si RN10 se refiere a estas mismas celdas o a celdas adicionales no mostradas en el mockup.

## RN11

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

## RN12

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-18.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Vida útil (periodos a proyectar) | Campo para registrar la vida útil del proyecto o la cantidad de periodos a proyectar para los "Costos de Operación" y/o los "Costos de Mantenimiento". Campo obligatorio. | Numérico | Numérico | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. |
| Tasa de crecimiento de costos | Campo para registrar la tasa de crecimiento de los costos de operación y de mantenimiento. Debe considerar las estadísticas institucionales, con valores de referencia de entre 0 y 3%. Sin embargo, el administrador del sistema puede ir actualizando en el tiempo estos topes. (Este campo es parametrizable). Campo obligatorio. | Porcentaje | Porcentaje | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. Valores de referencia entre 0% y 3%, parametrizables por el administrador del sistema. |
| N° (tabla Costos de Operación) | Numeración que el sistema genera automáticamente para las actividades que sean ingresadas de forma manual. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Actividad (tabla Costos de Operación) | Campo que muestra las actividades registradas en el campo "Nombre de la actividad" del Anexo A.2. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Costo por período (tabla Costos de Operación) | Columnas que muestran el valor del costo por cada actividad. Este valor procede de la pantalla "Detalle de Actividad", según RN07, en US$. El sistema deberá agregar el separador de miles (,). Para el Período 1 el valor se traslada desde la celda "Total" (a precios de mercado) de la tabla "Detalle de Actividad" según RN06; para el Período 2 hasta el Período n el valor se calcula automáticamente mediante la fórmula descrita en RN06: [fórmula no legible con claridad en el documento fuente, ver RN06]. Donde: Costoperiodo base: Costo del periodo inicial; r: Tasa de crecimiento (Valor registrado en el campo "Tasa de crecimiento de costos"); n: Posición del periodo respecto al periodo de inicio (número de periodos entre el periodo base y el periodo n). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la referencia a RN07 en lugar de RN06. |
| Total (Precios de Mercado) (tabla Costos de Operación) | Campo que mostrará la suma de costos de operación por cada periodo a precios de mercado. El Sistema lo calculará de manera automática (RN08). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Total (Precios Ajustados) (tabla Costos de Operación) | Campo que mostrará la suma de los costos de operación por cada periodo a precios ajustados. El Sistema los mostrará según RN09. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| N° (tabla Costos de Mantenimiento) | Numeración que el sistema genera automáticamente para las actividades que sean ingresadas de forma manual. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Actividad (tabla Costos de Mantenimiento) | Campo que muestra las actividades registradas en el campo "Nombre de la actividad" del Anexo A.2. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Costo por período (tabla Costos de Mantenimiento) | Columnas que muestran el valor del costo por cada actividad. Este valor procede de la pantalla "Detalle de Actividad", según RN06, en US$. El sistema deberá agregar el separador de miles (,). Para el Período 1 el valor se traslada desde la celda "Total" (a precios de mercado) de la tabla "Detalle de Actividad" según RN06; para el Período 2 hasta el Período n el valor se calcula automáticamente mediante la fórmula descrita en RN06: [fórmula no legible con claridad en el documento fuente, ver RN06]. Donde: Costoperiodo base: Costo del periodo inicial; r: Tasa de crecimiento (Valor registrado en el campo "Tasa de crecimiento de costos"); n: Posición del periodo respecto al periodo de inicio (número de periodos entre el periodo base y el periodo n). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: "Sí/No" (tal como aparece literalmente en el documento; no se aclara en qué condiciones es editable y en cuáles no). |
| Total (Precios de Mercado) (tabla Costos de Mantenimiento) | Campo que mostrará la suma de costos de mantenimiento por cada periodo a precios de mercado. El Sistema lo calculará de manera automática. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Total (Precios Ajustados) (tabla Costos de Mantenimiento) | Campo que mostrará la suma de los costos de mantenimiento por cada periodo. El Sistema los calculará según RN09. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Actividad (pantalla Detalle de Actividad) | Campo para registrar el nombre de la actividad. Campo obligatorio. Al menos se debe seleccionar una actividad. | Texto | Texto | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. |
| Insumo Tipo | Campo que muestra los tipos de insumo. El Sistema mostrará por defecto los insumos según el catálogo "Insumos". | Texto | Texto | No especificado en el documento. | Insumos listados en el catálogo "Insumos" (RN07). | Editable: No. Ver Observaciones respecto a diferencias entre el catálogo "Insumos" del Anexo B y los valores mostrados en el mockup del Anexo A.2. |
| FC | Campo que muestra el factor de corrección por cada insumo tipo, según el catálogo "Insumos". | Número | Número | No especificado en el documento. | Valores del catálogo "Insumos" (RN07). | Editable: No. |
| Período 1 (Precio de Mercado) | Campo para registrar los costos de cada insumo en el periodo en US$. El sistema deberá agregar el separador de miles (,). Campo obligatorio. Al menos se debe registrar información en un insumo. | Moneda | Moneda | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. |
| Período Año 1 (Precio Ajustado) | Campo que muestra el costo de cada insumo afectado por el Factor de Corrección correspondiente. El Sistema lo calcula así: Período_n = Insumo × FC. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la inconsistencia de nombre entre "Período 1" y "Período Año 1". |
| Total Precio de Mercado (Detalle de Actividad) | Campo que muestra la suma de todos los insumos de la columna "Período 1 (P. Mercado)" en US$. El sistema deberá agregar el separador de miles (,). El Sistema lo calculará de manera automática. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Total Precio Ajustado (Detalle de Actividad) | Campo que muestra la suma de todos los insumos de la columna "Período 1 (P. Ajustado)" en US$. El sistema deberá agregar el separador de miles (,). El Sistema lo calculará de manera automática. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Campos pendientes de completar (general, al dar clic en "Guardar") | El sistema sombreará los bordes de los campos pendientes en color rojo (RN11). | No especificado en el documento (texto exacto de mensaje no indicado; solo se describe el comportamiento visual de sombreado en rojo). |
| Actividad (Detalle de Actividad) | Al menos se debe seleccionar/registrar una actividad (campo obligatorio). | No especificado en el documento. |
| Período 1 (Precio de Mercado) (Detalle de Actividad) | Al menos se debe registrar información en un insumo (campo obligatorio). | No especificado en el documento. |
| Vida útil (periodos a proyectar) | Campo obligatorio. | No especificado en el documento. |
| Tasa de crecimiento de costos | Campo obligatorio; valores de referencia entre 0% y 3% (parametrizable por el administrador). | No especificado en el documento. |

---

# Errores

> No especificado en el documento. El documento no desarrolla una tabla de códigos de error para este caso de uso.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pantalla "Costos de Operación y Mantenimiento" y en "Detalle de Actividad", según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |
| Todos los demás actores (no especificados individualmente en el documento) | Visualizar únicamente la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Usuarios Internos (no especificado a qué rol corresponde exactamente) | Visualizar la tabla de presupuesto a precios ajustados. | RN09 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos" (precondición)
- CU-PRE-03.5 "Selección y registro de etapas" (precondición)
- CU-PRE-14 Análisis ambiental (precondición)
- CU-PRE-11 Descripción técnica (precondición, ver nota de ambigüedad en Precondiciones)
- CU-PRE-15 Análisis de riesgos (precondición, ver nota de ambigüedad en Precondiciones)
- CU-PRE-16 Análisis legal (precondición, ver nota de ambigüedad en Precondiciones)
- CU-PRE-17 Presupuesto de inversión (precondición, ver nota de ambigüedad en Precondiciones)
- CU-PRE-21 "Flujo de Caja y cálculo de Indicadores" (postcondición)

**Procesos relacionados:**
> No especificado en el documento.

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Pantalla: Presupuesto de Operación y Mantenimiento (Anexo A.1)

**Descripción:** Pantalla identificada en el encabezado como "COSTOS DE OPERACIÓN Y MANTENIMIENTO", donde el Técnico URP selecciona el tipo de costo a registrar y gestiona las tablas de "Costos de Operación" y "Costos de Mantenimiento".

**Campos:**
- Seleccione el tipo de costo a registrar (opciones de botón radial: Operación, Mantenimiento, O&M, No aplica)
- Vida útil (periodos a proyectar)
- Tasa de crecimiento de costos
- Tabla "Costos de Operación": N°, Actividad, Costo por Período (columnas 1 a n), Total (Precios de Mercado), Total (Precios Ajustados)
- Tabla "Costos de Mantenimiento": N°, Actividad, Costo por Período (columnas 1 a n), Total (Precios de Mercado), Total (Precios Ajustados)

**Botones:**
- Agregar Actividad (uno para la tabla de Costos de Operación y otro para la tabla de Costos de Mantenimiento)
- Aceptar
- Guardar
- Siguiente

**Acciones:**
- Selección del tipo de costo a registrar (RN04).
- Ingreso de "Vida útil" y "Tasa de crecimiento de costos" y clic en "Aceptar" para generar columnas (RN05, RN06).
- Clic en "Agregar Actividad" para abrir la pantalla "Detalle de Actividad".
- Clic en "Guardar" (ver FA-01.1, FA-02).
- Clic en "Siguiente" (ver FA-03).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

Opción seleccionada en "Seleccione el tipo de costo a registrar": **O&M**

Tabla "COSTOS DE OPERACIÓN":

| N° | ACTIVIDAD | Período 1 | Período 2 | Período 3 | ... | Período n |
|----|-----------|-----------|-----------|-----------|-----|-----------|
| 1 | Actividad operación 1 | $ 6,000.00 | | | | |
| 2 | Actividad operación 2 | | | | | |
| 3 | | | | | | |
| ... | | | | | | |
| n | | | | | | |
| **TOTAL (PRECIOS DE MERCADO)** | | **$ 6,000.00** | | | | |
| **TOTAL (PRECIOS AJUSTADOS)** | | **$ 6,000.00** | | | | |

Tabla "COSTOS DE MANTENIMIENTO":

| N° | ACTIVIDAD | Período 1 | Período 2 | Período 3 | ... | Período n |
|----|-----------|-----------|-----------|-----------|-----|-----------|
| 1 | Actividad mantenimiento 1 | $ 8,000.00 | | | | |
| 2 | Actividad mantenimiento 2 | | | | | |
| 3 | | | | | | |
| ... | | | | | | |
| n | | | | | | |
| **TOTAL (PRECIOS DE MERCADO)** | | **$ 8,000.00** | | | | |
| **TOTAL (PRECIOS AJUSTADOS)** | | **$ 8,000.00** | | | | |

## Pantalla: Detalle de Actividad (Anexo A.2)

**Descripción:** Pantalla donde el Técnico URP registra el nombre de la actividad y los costos de cada insumo por período.

**Campos:**
- Nombre de la actividad
- Tabla con columnas: Insumo Tipo, FC, Período 1 (US$) Precio de Mercado, Período 1 (US$) Precio Ajustado
- Total (Precios de Mercado)
- Total (Precios Ajustados)

**Botones:**
- Guardar
- Salir

**Acciones:**
- Registro del nombre de la actividad y de los costos por insumo (SF-01, paso 1).
- Cálculo automático del precio ajustado por insumo (SF-01, paso 2).
- Clic en "Guardar" (FA-01.1) o "Salir" (FA-01.2).

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**

Nombre de la actividad: **Actividad operación 1**

| INSUMO TIPO | FC | PRECIO DE MERCADO – PERÍODO 1 (US$) | PRECIO AJUSTADO – PERÍODO 1 (US$) |
|-------------|----|--------------------------------------|-------------------------------------|
| M.O CALIFICADA | 1.00 | $ 500.00 | |
| M.O NO CALIFICADA | 1.00 | $ 2,500.00 | |
| MATERIALES | 1.00 | $ 3,000.00 | |
| EQUIPOS | 1.00 | | |
| **TOTAL (PRECIOS DE MERCADO)** | | **$ 6,000.00** | |
| **TOTAL (PRECIOS AJUSTADOS)** | | | **$ 6,000.00** |

## Pantalla: Mensaje emergente Guardar (Anexo A.3)

**Descripción:** Ventana modal de confirmación mostrada al guardar información.

**Campos:** No aplica (mensaje informativo).

**Botones:**
- Aceptar

**Acciones:**
- Confirmación de guardado exitoso.

**Ejemplo de datos mostrados en el mockup (Anexo A.3):**
- Ícono de confirmación (check).
- Texto: "¡Guardado!"
- Texto: "Sus datos han sido guardados exitosamente."
- Botón: "Aceptar"

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Al dar clic en el botón "Guardar" en las pantallas "Detalle de Actividad" (FA-01.1) y "Presupuesto de Operación y Mantenimiento" (FA-02); se muestra el mensaje emergente del Anexo A.3. |
| Advertencia | "Los datos no serán guardados" | Al dar clic en el botón "Salir" en la pantalla "Detalle de Actividad" (FA-01.2). |
| Ayuda contextual | No especificado en el documento (solo se indica que el Sistema "indicará qué información se debe completar en dicho campo"). | Al acercar el cursor a un campo y dar clic en el ícono "?" (RN12). |

---

# Observaciones

- El campo "Actores" de la Identificación del caso de uso solo lista a "Técnico URP", pero las Reglas de Negocio RN01 y RN02 mencionan adicionalmente a "Técnico PRE" y a "todos los demás actores" con permisos de solo visualización, sin identificarlos individualmente.
- Existe una discrepancia en la referencia normativa del campo "Costo por período": en la pantalla "Costos de Operación" el documento indica que el valor "procede de la pantalla 'Detalle de Actividad', según RN07", mientras que en la pantalla "Costos de Mantenimiento" el mismo campo indica que procede "según RN06". La regla RN07 describe el catálogo "Insumos" y el factor de corrección (FC), no el traslado de valores desde "Detalle de Actividad"; la regla que efectivamente describe dicho traslado y cálculo por período es RN06. No se puede determinar si la referencia a RN07 en la tabla "Costos de Operación" es un error del documento o si existe una diferencia funcional no explicada entre ambas tablas.
- Existe una inconsistencia en el nombre de los campos de la pantalla "Detalle de Actividad": la columna de precio de mercado se denomina "Período 1 (Precio de Mercado)" mientras que la columna equivalente de precio ajustado se denomina "Período Año 1 (Precio Ajustado)". El documento no aclara si "Período 1" y "Período Año 1" refieren al mismo concepto o a conceptos distintos.
- Existe una discrepancia entre el catálogo "Insumos" definido en el Anexo B (Mano de obra calificada, Mano de obra semi calificada, Mano de obra no calificada, Bienes importados, Bienes nacionales, Combustibles/Energía, Servicios, Otros — todos con Factor de Corrección 1.00) y los insumos mostrados en el mockup del Anexo A.2 (M.O CALIFICADA, M.O NO CALIFICADA, MATERIALES, EQUIPOS). Los nombres y la cantidad de insumos no coinciden entre el catálogo documentado y el ejemplo de pantalla.
- La columna "Editable" del campo "Costo por período" en la pantalla "Costos de Mantenimiento" indica el valor "Sí/No" de forma literal en el documento, sin aclarar bajo qué condiciones el campo es editable y bajo cuáles no lo es.
- RN10 hace referencia a las casillas "TOTAL INVERSIÓN (PRECIOS DE MERCADO)" y "TOTAL INVERSIÓN (PRECIOS AJUSTADOS)" del Anexo A.1, mientras que el mockup del Anexo A.1 y las demás reglas del caso de uso utilizan las etiquetas "TOTAL (PRECIOS DE MERCADO)" y "TOTAL (PRECIOS AJUSTADOS)" (sin la palabra "INVERSIÓN") para las tablas "Costos de Operación" y "Costos de Mantenimiento". No se puede determinar si RN10 se refiere a estas mismas celdas.
- El Flujo Básico (paso 5) menciona un único botón "Agregar Actividad", pero el mockup del Anexo A.1 muestra dos botones "Agregar Actividad" (uno para la tabla "Costos de Operación" y otro para la tabla "Costos de Mantenimiento"). El documento no aclara explícitamente si, al seleccionarse "O&M", el paso 5 del Flujo Básico aplica de forma independiente a cada botón.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Presupuesto de Operación y Mantenimiento | Conjunto de tablas "Costos de Operación" y "Costos de Mantenimiento" asociadas a un tipo de costo seleccionado. | Registro/consulta según credenciales (RN01); adición y/o eliminación de filas (RN03); generación automática de columnas por período (RN05). |
| Actividad | Fila registrada en las tablas "Costos de Operación" y/o "Costos de Mantenimiento", originada en la pantalla "Detalle de Actividad". | Registro (Flujo SF-01, paso 1); traslado automático a la tabla del Anexo A.1 (Flujo SF-01, paso 4; RN06); cálculo automático de totales por período (RN08). |
| Detalle de Actividad / Insumo | Insumos asociados a una actividad, con su Factor de Corrección (FC) y costo por período. | Registro de costos por insumo (Flujo SF-01, paso 1); cálculo automático del precio ajustado (Flujo SF-01, paso 2); visualización por defecto según catálogo "Insumos" (RN07). |
| Período | Columna de costo correspondiente a cada periodo proyectado según la "Vida útil". | Generación automática de columnas según la "Vida útil" registrada (RN05); cálculo automático de valores proyectados del Período 2 al Período n (RN06). |
| Catálogo Insumos | Listado de tipos de insumo y su Factor de Corrección. | Consulta/visualización por defecto en la tabla "Detalle de Actividad" (RN07); sujeto a actualización por parte de la DGICP (Nota del catálogo). |

---

# Catálogos Detectados

## Catálogo "Insumos"

| Insumo | Factor de corrección |
|--------|------------------------|
| Mano de obra calificada | 1.00 |
| Mano de obra semi calificada | 1.00 |
| Mano de obra no calificada | 1.00 |
| Bienes importados | 1.00 |
| Bienes nacionales | 1.00 |
| Combustibles/Energía | 1.00 |
| Servicios | 1.00 |
| Otros | 1.00 |

> Nota importante (según el documento): Este catálogo estará sujeto a actualización por parte de la DGICP.

> Ver Observaciones respecto a la discrepancia entre este catálogo y los insumos mostrados en el mockup del Anexo A.2 (M.O CALIFICADA, M.O NO CALIFICADA, MATERIALES, EQUIPOS).

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Generación de columnas de período | Sistema (RN05), tras registro de "Vida útil" y clic en "Aceptar" | Tablas "Costos de Operación" y "Costos de Mantenimiento" |
| Cálculo de precio ajustado por insumo | Sistema (Flujo SF-01, paso 2), tras registro de costo en "Período 1" | Columna "Período 1 (US$) Precio Ajustados" en "Detalle de Actividad" |
| Traslado de actividad a Anexo A.1 | Sistema (Flujo SF-01, paso 4; RN06), tras clic en "Guardar" en "Detalle de Actividad" | Tabla correspondiente ("Costos de Operación" o "Costos de Mantenimiento") en Anexo A.1 |
| Cálculo automático de totales | Sistema (RN08), sobre columnas de período | Fila "Total precio de mercado (P.M)" en tablas de Anexo A.1 |

---

# Integraciones

> No especificado en el documento.

---

# Datos Pendientes de Definir

- La fórmula matemática de la Regla de Negocio RN06 (cálculo del "Costo por período" para el Período 2 hasta el Período n) no puede determinarse con precisión a partir del documento: los caracteres de la fórmula aparecen con una notación que no es legible con claridad en el documento fuente. Únicamente se cuenta con la definición de variables: Costoperiodo base (costo del periodo inicial), r (tasa de crecimiento) y n (posición del periodo respecto al periodo base). Esta ambigüedad bloquea la implementación exacta del cálculo descrito en RN06.
- El texto exacto del mensaje que el sistema muestra al dar clic en el ícono "?" de ayuda contextual (RN12) no está especificado; el documento solo describe que el sistema "indicará qué información se debe completar en dicho campo".
- El texto exacto del mensaje mostrado cuando hay campos pendientes de completar al dar clic en "Guardar" (RN11) no está especificado; el documento solo describe el comportamiento visual (bordes sombreados en rojo).
- No se especifica una tabla de códigos de error para este caso de uso.
- No se especifica el disparador (evento que inicia el caso de uso) de forma explícita.
- No se especifica la prioridad del caso de uso.
- No se aclara si la discrepancia entre RN07 y RN06 como referencia del campo "Costo por período" (ver Observaciones) corresponde a un error del documento o a una diferencia funcional real entre las tablas "Costos de Operación" y "Costos de Mantenimiento".
- No se aclara la contradicción entre el catálogo "Insumos" del Anexo B y los insumos mostrados en el mockup del Anexo A.2 (ver Observaciones), lo cual impide determinar cuál es el listado de insumos que debe implementarse por defecto en la pantalla "Detalle de Actividad".