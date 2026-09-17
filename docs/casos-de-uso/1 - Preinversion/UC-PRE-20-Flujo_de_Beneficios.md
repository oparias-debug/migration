---
id: CU-PRE-20
codigo: CU-PRE-20
nombre: Flujo de Beneficios
modulo: Preinversión
submodulo: Evaluación
version: "1.0"
fuente_pdf: CU-PRE-20_Flujo_de_Beneficios_AGO_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 11

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-17", "CU-PRE-18"]

casos_relacionados: ["CU-PRE-21"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Beneficios del Proyecto (Anexo A.1)", "Detalle del Beneficio (Anexo A.2)", "Mensaje emergente Guardar (Anexo A.3)"]

procesos: []

servicios_externos: []

entidades: ["Beneficio", "Período", "Valor de rescate", "Catálogo Parámetros", "Catálogo Tipo de Beneficio", "Catálogo Tipo de bienes (Valor de rescate)"]

catalogos: ["Parámetros", "Tipo de Beneficio", "Tipo de bienes (Valor de rescate)"]

palabras_clave: ["Beneficios", "Valoración monetaria", "Flujo de beneficios", "Valor de rescate", "Factor de corrección", "Precios ajustados", "Precios de mercado", "Tasa de crecimiento"]

ultima_actualizacion: "JUL 2025"

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 4
  flujos_alternos:
    FA01:
      pagina: 4
    FA1.1:
      pagina: 4
    FA1.2:
      pagina: 5
    FA02:
      pagina: 5
    FA2.1:
      pagina: 6
    FA2.2:
      pagina: 6
    FA03:
      pagina: 6
    FA04:
      pagina: 6
  reglas_negocio:
    RN01:
      pagina: 7
    RN02:
      pagina: 7
    RN03:
      pagina: 7
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
  anexos:
    A1:
      nombre: Beneficios del Proyecto
      pagina: 8
    A2:
      nombre: Detalle del Beneficio
      pagina: 9
    A3:
      nombre: Guardar
      pagina: 9
    B1:
      nombre: Requerimientos Funcionales - Formatos
      pagina: 10
    Catalogo_Parametros:
      nombre: Catálogo Parámetros
      pagina: 11
    Catalogo_TipoBeneficio:
      nombre: Catálogo Tipo de Beneficio
      pagina: 11
    Catalogo_TipoBienes:
      nombre: Catálogo Tipo de bienes (Valor de rescate)
      pagina: 11
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|-------|---------|--------------|-------|-------|
| JUL 2025 | 1.0 | Primera Versión | Equipo Preinversión | No especificado en el documento. |

> Nota de ambigüedad: el pie de página repetido en todas las páginas del documento indica "Fecha: JUN 2025", mientras que la tabla "Historial de Revisiones" indica "JUL 2025" para la misma versión 1.0. Ver sección Observaciones.

---

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Flujo de Beneficios |
| Código | CU-PRE-20 |
| Módulo | Preinversión |
| Fuente | CU-PRE-20 Flujo de Beneficios_AGO 2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Pantalla "Beneficios del Proyecto"
  - Beneficios
  - Periodos (desde 1 hasta n)
  - Flujo de Beneficios (Precios de mercado)
  - Flujo de Beneficios (Precios ajustados)
  - Valor de rescate
  - Tipo de bien
  - Valor de rescate ajustado
  - Vida útil (períodos a proyectar)
- Pantalla "Detalle del Beneficio"
  - Beneficio
  - Factor de corrección
  - Valor de Factor de corrección (FC)
  - Tipo de ingreso
  - Monto período 1
  - Tasa de crecimiento proyectado
  - Periodo
  - Monto ($) Precios de mercado
  - Monto ($) Precios ajustados

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite al actor "Técnico URP" registrar en el Sistema los beneficios (con su valoración monetaria) que se espera generar con el proyecto, los cuales servirán para realizar el cálculo de los indicadores de evaluación en el CU-PRE-21. Cabe mencionar que la identificación, cuantificación y valoración de dichos beneficios será responsabilidad de la institución que formula el proyecto y no será parametrizado en el Sistema.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico PRE (mencionado en RN02, con permisos de solo visualización sobre todas las Unidades Ejecutoras).

> Nota: el campo "Actores" de la Identificación del caso de uso solo lista a "Técnico URP". La mención de "Técnico PRE" y de "todos los demás actores" proviene de las Reglas de Negocio RN01 y RN02.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP de CU-PRE-01 "Registro de Proyectos".
2. Contar con la Ruta de Preinversión generada en CU-PRE-03.5 "Selección y registro de etapas".
3. Tener el "Presupuesto de inversión" (CU-PRE-17).
4. Tener el "Flujo de costos de Operación y Mantenimiento" (CU-PRE-18) (para el campo "n").

---

# Flujo Principal

## FB – Flujo Básico

1. Técnico URP ingresa a la pestaña "Evaluación" en la sección "Beneficios del Proyecto".
2. Técnico URP da clic en el botón "Agregar Beneficio".
3. Sistema despliega la pantalla del Anexo A.2.
4. Técnico URP selecciona una de las opciones dando clic en botón radial: "Manual" o "Proyectada".

> Nota de ambigüedad: el paso 4 denomina a la segunda opción "Proyectada"; sin embargo, en el resto del documento (título del Flujo Alternativo 2, paso 2.1, y el mockup del Anexo A.2 — "Tipo de ingreso: Manual / Automático") dicha opción se denomina "Automático". Ver Observaciones.

---

# Flujos Alternos

## FA01 – Flujo Alternativo 1 Pantalla Beneficios (Anexo A.2) – Manual

**Condición**

> No especificado en el documento.

**Flujo**

1.1. Técnico URP selecciona una de las opciones del campo "Tipo de Beneficio" (según catálogo Tipo de Beneficio). Registra el nombre del beneficio en el campo "Beneficio". Selecciona en el campo "Factor de corrección" una de las opciones listadas (según catálogo Parámetros). Selecciona la opción "Manual".

1.2. Sistema deshabilita los campos "Monto Período 1" y "Tasa de crecimiento proyecto". Muestra en el campo "Factor de corrección" el valor numérico asociado al Factor de corrección seleccionado (Parámetros). Muestra en la tabla "Montos por período" del Anexo A.2 una cantidad de filas equivalente a la cantidad de período registrados en el campo "Vida Útil (periodos a proyectar)" del Anexo A.1 del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento".

1.3. Técnico URP registra el monto por cada período en la columna "Monto Precios de Mercado".

1.4. Sistema calcula el valor de la columna "Monto Precios ajustados" para cada período mediante la fórmula siguiente:

> [Fórmula tal como aparece en el documento fuente, transcripción literal de los caracteres extraídos]: "ܥܨ ∗ ௢ௗ௘ ௠௘௥௖௔ௗ ௦௣௥௘௖௜௢݋ݐ݊݋�� = ݋ܽ݀ݐݏݑ݆ܽ ݋ݐ݊݋��" — los caracteres de la fórmula no se muestran con claridad en el documento original (ver sección "Datos Pendientes de Definir"). No se identifica una definición explícita de variables ("Donde:") junto a esta fórmula en el documento.

**Resultado**

> No especificado en el documento.

## FA1.1 – Flujo Alternativo 1.1 (del Flujo Detalle de Actividad) Guardar

> Nota: el documento titula este flujo "(del Flujo Detalle de Actividad)", denominación que corresponde al caso de uso CU-PRE-18 y no a la pantalla "Detalle del Beneficio" propia de este caso de uso (CU-PRE-20). Se transcribe tal como aparece en el documento fuente. Ver Observaciones.

**Condición**

> No especificado en el documento.

**Flujo**

1.1.1. Técnico URP da clic en el botón "Guardar".

1.1.2. Sistema muestra mensaje emergente del Anexo A.3.

1.1.3. Técnico URP da clic en "Aceptar" al mensaje emergente.

1.1.4. Sistema traslada los valores registrados en el campo "Beneficio" y en la columna "Monto Precios de Mercado" de cada período a los períodos correspondientes de la pantalla "Beneficios del proyecto" y los ubica en la sección del tipo de Beneficio (Directo, Indirecto, Externalidades) que se haya seleccionado en el campo "Tipo de Beneficio". Mostrará los totales por período en las filas "Flujo de Beneficios (precios de mercado)" y "Flujo de beneficios (precios ajustados)". La fila "Flujo de beneficios (precios ajustados)" sólo será visible para usuarios centrales.

**Resultado**

> No especificado en el documento.

## FA1.2 – Flujo Alternativo 1.2 (del Flujo Detalle de Actividad) Salir

**Condición**

> No especificado en el documento.

**Flujo**

1.2.1. Técnico URP da clic en el botón "Salir".

1.2.2. Sistema regresa a la pestaña "Beneficios del proyecto" sin guardar cualquier información que se haya registrado.

**Resultado**

> No especificado en el documento.

## FA02 – Flujo Alternativo 2 Pantalla Beneficios (Anexo A.2) – Automático

**Condición**

> No especificado en el documento.

**Flujo**

2.1. Técnico URP selecciona una de las opciones del campo "Tipo de Beneficio" (según catálogo Tipo de Beneficio). Registra el nombre del beneficio en el campo "Beneficio". Selecciona en el campo "Factor de corrección" una de las opciones listadas (según catálogo Parámetros). Selecciona la opción "Automático" e ingresa la información en los campos "Monto período 1" y "Tasa de crecimiento proyectado".

2.2. Sistema muestra en el campo "Factor de corrección" el valor numérico asociado al Factor de corrección seleccionado (Parámetros). Muestra en la tabla "Montos por período" del Anexo A.2 una cantidad de filas equivalente a la cantidad de períodos registrados en el campo "Vida útil (períodos a proyectar)" del Anexo A.1 del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". Calcula y muestra los valores en la columna "Monto precios de mercado" para cada período según la fórmula siguiente:

> [Fórmula tal como aparece en el documento fuente, transcripción literal de los caracteres extraídos]: "(ݎ + 1)1 ௢ௗ௣௘௥௜௢݋݂݁݊݁݅ܿ݅ܤ = ௡ ௢ௗ௣௘௥௜௢݋ݐ݊݋�� ௡" — los caracteres de la fórmula no se muestran con claridad en el documento original (ver sección "Datos Pendientes de Definir").
>
> Donde:
> - Montoperiodo 1: Valor registrado en el campo "Monto periodo 1".
> - r: Tasa de crecimiento registrada en campo "Tasa de crecimiento proyectada".
> - n: Posición del periodo respecto al periodo base (número de periodos entre el periodo base y el periodo n).

2.3. Sistema calcula el valor de la columna "Monto Precios ajustados" para cada período mediante la fórmula siguiente:

> [Fórmula tal como aparece en el documento fuente, transcripción literal de los caracteres extraídos]: "ܥܨ ∗ ௢ௗ௘ ௠௘௥௖௔ௗ ௦௣௥௘௖௜௢݋ݐ݊݋�� = ݋ܽ݀ݐݏݑ݆ܽ ݋ݐ݊݋��" — los caracteres de la fórmula no se muestran con claridad en el documento original (ver sección "Datos Pendientes de Definir").

**Resultado**

> No especificado en el documento.

## FA2.1 – Flujo Alternativo 2.1 (del Flujo Detalle de Actividad) Guardar

> Nota: el documento titula este flujo "(del Flujo Detalle de Actividad)", igual que FA1.1. Ver Observaciones.

**Condición**

> No especificado en el documento.

**Flujo**

2.1.1. Técnico URP da clic en el botón "Guardar".

2.1.2. Sistema muestra mensaje emergente del Anexo A.3.

2.1.3. Técnico URP da clic en "Aceptar" al mensaje emergente.

2.1.4. Sistema traslada los valores registrados en el campo "Beneficio" y en la columna "Monto Precios de Mercado" de cada período a los períodos correspondientes de la pantalla "Beneficios del proyecto" y los ubica en la sección del tipo de Beneficio (Directo, Indirecto, Externalidades) que se haya seleccionado en el campo "Tipo de Beneficio". Mostrará los totales por período en las filas "Flujo de Beneficios (precios de mercado)" y "Flujo de beneficios (precios ajustados)". La fila "Flujo de beneficios (precios ajustados)" sólo será visible para usuarios centrales.

**Resultado**

> No especificado en el documento.

## FA2.2 – Flujo Alternativo 2.2 (del Flujo Detalle de Actividad) Salir

**Condición**

> No especificado en el documento.

**Flujo**

2.2.1. Técnico URP da clic en el botón "Salir".

2.2.2. Sistema regresa a la pestaña "Beneficios del proyecto" sin guardar cualquier información que se haya registrado.

**Resultado**

> No especificado en el documento.

## FA03 – Flujo Alternativo 3 Guardar

**Condición**

> No especificado en el documento.

**Flujo**

3.1. Técnico URP da clic en el botón "Guardar".

3.2. Sistema muestra un mensaje emergente del Anexo A.3.

3.3. Técnico URP da clic en "Aceptar" al mensaje emergente.

3.4. Sistema guarda la información registrada y se mantiene en la pantalla "Beneficios del proyecto".

**Resultado**

> No especificado en el documento.

## FA04 – Flujo Alternativo 4 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

4.1. Técnico URP da clic en el botón "Siguiente".

4.2. Sistema avanza a la siguiente pestaña (CU-PRE-21 "Flujo de Caja e Indicadores") para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no desarrolla una tabla de excepciones con código, descripción y consecuencia para este caso de uso.

# Postcondiciones

1. CU-PRE-21 Flujo de Caja y cálculo de Indicadores.

> Nota de ambigüedad: el paso 4.2 del Flujo Alternativo 4 se refiere al mismo caso de uso como "CU-PRE-21 'Flujo de Caja e Indicadores'", con una ligera variación de nombre respecto a la postcondición declarada. Ver Observaciones.

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

## RN03

**Descripción:** El Técnico URP podrá adicionar y eliminar más filas a la tabla de la pantalla "Beneficios del Proyecto".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

## RN04

**Descripción:** El Sistema deberá redondear las decenas del monto de las casillas de la tabla de "Montos por período" del Anexo A.2. El Sistema deberá redondear siempre por arriba y a múltiplos de 5 o de 10. Por ejemplo, si el valor es de $1,750,427.58 lo redondeará a $1,750,430.00; y si el valor es de $1,750,423.58 lo redondeará a $1,750,425.00.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

## RN05

**Descripción:** La cantidad de períodos que se muestran en la tabla "Beneficios del proyecto" será igual a la cantidad de períodos que se muestran en la pantalla "Detalle del beneficio".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

## RN06

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

## RN07

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

## RN08

**Descripción:** El Sistema ubicará en la pantalla del Anexo A.1 "Beneficios del proyecto" cada beneficio que se haya registrado en el Anexo A.2 "Detalle del Beneficio" en la sección que corresponda según el tipo de beneficio seleccionado en el campo "Tipo de Beneficio" (Directo, Indirecto y Externalidades).

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

## RN09

**Descripción:** El cálculo del presupuesto se deberá realizar tanto a precios de mercado como a precios ajustados. Para ello, las tablas de los Anexos A.1 y A.2, deberán calcularse tanto a precios de mercado como a precios ajustados, este último aplicando el Factor de Corrección (FC) a la tabla a precios de mercado. La tabla de presupuesto a precios ajustados sólo podrá ser visible para los usuarios internos.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-20.

> ✅ RESUELTO (RQ-C-03, ronda 3): "usuarios internos" (RN09) y "usuarios centrales" (FA1.1.4, FA2.1.4, Anexo B) son sinónimos. El negocio confirmó que "usuarios internos" / "actores internos" / "usuarios centrales" corresponden a un solo grupo: cualquier usuario que se disponga dentro del Ministerio de Hacienda, independientemente de su rol. Esta misma equivalencia aplica en CU-PRE-17 ("actores internos") y en CU-PRE-21. Adicionalmente, el negocio confirmó que CEPA, CEL, ANDA, INDES e ISTU tienen visibilidad equivalente en CU-PRE-21 porque son instituciones que proyectan ingresos (no beneficios) y deben reportarlos para construir el flujo financiero — no porque pertenezcan al Ministerio de Hacienda; esa adición es propia de CU-PRE-21 y no extiende el grupo definido aquí. Ver entrada de glosario compartido correspondiente (pendiente de incorporación formal por el Analista de Consistencia). El control de visibilidad de precios ajustados definido en RN09 ya puede implementarse conforme a esta definición.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Vida útil (años a proyectar) | Campo que muestra la cantidad de períodos registrados desde el campo del mismo nombre en el CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la denominación "años a proyectar" frente a "períodos a proyectar" usada en otras partes del documento. |
| Beneficios (tabla "Beneficios del proyecto") | Campo que muestra cada beneficio que se ha registrado en la pantalla del Anexo A.2. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí. |
| Periodos (tabla "Beneficios del proyecto") | Campos que muestran el valor de cada Beneficio por cada periodo. Este valor procede de la pantalla del Anexo A.2 "Detalle del Beneficio" del campo Monto ($) Precios Ajustados en US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a que el valor de origen indicado es "Precios Ajustados" y no "Precios de Mercado". |
| Flujo Beneficios (Precios de mercado) | Campo que muestra el total de costos de todos los beneficios por cada período en US$ a precios de mercado. El Sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Flujo Beneficios (Precios ajustados) | Campo que muestra el total de costos de todos los beneficios por cada período en US$ a precios ajustados. El Sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Valor de rescate | Campo para registrar el Valor de rescate en US$ a precios de mercado. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: Sí. |
| Valor de rescate ajustado (primera aparición en el Anexo B) | Se calcula tomando del "Valor de rescate" y multiplicándolo por el factor de corrección seleccionado para el beneficio. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la duplicidad de este campo en el Anexo B. |
| Tipo de bien | Campo que permite al Técnico URP seleccionar de un listado el tipo de bien que contará con valor de rescate. Cada elemento del listado contará con un factor de corrección (FC) que afectará al Valor de rescate. Según catálogo Tipo de bienes (Valor de rescate). | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| FC (tabla "Beneficios del proyecto") | Campo que muestra el valor del factor de corrección asociado a cada tipo de bien. Según catálogo Tipo de bienes (Valor de rescate). Este campo solo será visible para los usuarios centrales. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No. Visible solo para "usuarios centrales" — resuelto (RQ-C-03): sinónimo de "usuarios internos" (RN09); ver nota de resolución en RN09. |
| Valor de rescate ajustado (segunda aparición en el Anexo B) | Campo que muestra el valor de rescate ajustado. El sistema lo calculará de acuerdo con la siguiente fórmula: Valor de rescate ajustado = Valor de rescate x FC. Este campo solo será visible para los usuarios centrales. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. Visible solo para "usuarios centrales" — resuelto (RQ-C-03): sinónimo de "usuarios internos" (RN09). Ver Observaciones respecto a la duplicidad de este campo en el Anexo B (asunto no relacionado con la terminología de visibilidad, sigue sin resolver). |
| Tipo de Beneficio | Campo para que el Técnico URP seleccione el tipo de beneficio, según catálogo Tipo de Beneficio. | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según el Anexo B). Ver Observaciones respecto a que los pasos 1.1 y 2.1 describen al Técnico URP "seleccionando" este campo. |
| Beneficios (pantalla "Detalle del beneficio") | Campo para que el Técnico URP registre el nombre del Beneficio. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí. Ver Observaciones respecto a la denominación en plural frente al campo "Beneficio" (singular) usado en los pasos de flujo. |
| Parámetro | Campo para seleccionar el Factor de Corrección, según catálogo Parámetros. Campo obligatorio. | Selección | Selección | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. Ver Observaciones respecto a la denominación "Parámetro" frente a "Factor de corrección" usado en otras partes del documento. |
| FC (pantalla "Detalle del beneficio") | Campo que muestra el valor del Factor de corrección seleccionado, según el catálogo Parámetros. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Tipo de ingreso | Campo para seleccionar de qué manera en que se va registrar la información de los beneficios. Campo obligatorio. | Selección | Selección | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: No (según el Anexo B). Ver Observaciones respecto a que los pasos 1.1 y 2.1 describen al Técnico URP "seleccionando" este campo. |
| Monto periodo 1 | Campo para registrar el valor del beneficio calculado para el periodo 1. Sólo se activa si se selecciona la opción "Automático". | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: Sí. |
| Tasa de crecimiento proyectado | Campo para registrar el valor de la tasa de crecimiento proyectado. Sólo se activa si se selecciona la opción "Automático". | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: Sí. |
| Periodo | Muestra en cada fila los períodos en que se registran los beneficios según Flujo Alternativo 2 – FA02 Pantalla Beneficios (Anexo A.2) – Automático, 6.2. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a la referencia "6.2", que no corresponde a la numeración de pasos utilizada en el documento (FA02 usa pasos "2.1", "2.2", "2.3"). |
| Monto Precios de Mercado | Para el tipo de ingreso Manual: Campo que permite el registro manual de los valores monetarios por cada período del beneficio en US$. El sistema deberá agregar el separador de miles (,). Para el tipo de ingreso Automático: Campo que muestra los valores monetarios calculados por el sistema, en cada período del beneficio en US$, mediante la fórmula descrita en el Flujo Alternativo 2 (paso 2.2): [fórmula no legible con claridad en el documento fuente, transcripción literal: "(r + 1)1 Beneficioperiodo n = n Montoperiodo n / n" — ver sección "Datos Pendientes de Definir"]. Donde: Montoperiodo 1: Valor registrado en el campo "Monto periodo 1"; r: Tasa de crecimiento registrada en el campo "Tasa de crecimiento proyectada"; n: Posición del periodo respecto al periodo base (número de periodos entre el periodo base y el periodo n). El sistema deberá agregar el separador de miles (,). Campo obligatorio. | Moneda | Moneda | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: "Sí/No" (tal como aparece literalmente en el documento; el campo es editable en modo "Manual" y no editable —calculado— en modo "Automático"). |
| Monto Precios Ajustados | Campo que muestra el valor de los precios ajustados. El Sistema lo calculará de acuerdo con la fórmula: [fórmula no legible con claridad en el documento fuente, transcripción literal: "ܥܨ ∗ ௢ௗ௘ ௠௘௥௖௔ௗ ௦௣௥௘௖௜௢݋ݐ݊݋�� = ݋ܽ݀ݐݏݑ݆ܽ ݋ݐ݊݋��" — ver sección "Datos Pendientes de Definir"]. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Campos pendientes de completar (general, al dar clic en "Guardar") | El sistema sombreará los bordes de los campos pendientes en color rojo (RN06). | No especificado en el documento (texto exacto de mensaje no indicado; solo se describe el comportamiento visual de sombreado en rojo). |
| Parámetro (Detalle del beneficio) | Campo obligatorio. | No especificado en el documento. |
| Tipo de ingreso | Campo obligatorio. | No especificado en el documento. |
| Monto Precios de Mercado | Campo obligatorio. | No especificado en el documento. |

---

# Errores

> No especificado en el documento. El documento no desarrolla una tabla de códigos de error para este caso de uso.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en las pantallas "Beneficios del Proyecto" y "Detalle del Beneficio", según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |
| Todos los demás actores (no especificados individualmente en el documento) | Visualizar únicamente la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Usuarios centrales / usuarios internos (RQ-C-03: sinónimos, cualquier usuario del Ministerio de Hacienda) | Visualizar la fila "Flujo de beneficios (precios ajustados)" y los campos "FC" y "Valor de rescate ajustado". | FA1.1.4, FA2.1.4, Anexo B (campos "FC" y "Valor de rescate ajustado") |
| Usuarios Internos / usuarios centrales (RQ-C-03: sinónimos, cualquier usuario del Ministerio de Hacienda) | Visualizar la tabla de presupuesto a precios ajustados. | RN09 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos" (precondición)
- CU-PRE-03.5 "Selección y registro de etapas" (precondición)
- CU-PRE-17 "Presupuesto de inversión" (precondición)
- CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" (precondición; también referenciado como origen del campo "Vida útil (períodos a proyectar)")
- CU-PRE-21 "Flujo de Caja y cálculo de Indicadores" / "Flujo de Caja e Indicadores" (postcondición; ver nota de ambigüedad sobre el nombre)

**Procesos relacionados:**
> No especificado en el documento.

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Pantalla: Beneficios del Proyecto (Anexo A.1)

**Descripción:** Pantalla identificada en el encabezado como "BENEFICIOS DEL PROYECTO", donde se muestran los beneficios registrados agrupados por tipo (Beneficios Directos, Beneficios Indirectos, Externalidades), junto con los totales de flujo de beneficios y el valor de rescate.

**Campos:**
- Vida útil (períodos a proyectar)
- Tabla de beneficios agrupada por tipo: Beneficio, Períodos (columnas 1 a n)
- Fila "FLUJO DE BENEFICIOS (Precios de mercado)"
- Fila "FLUJO DE BENEFICIOS (Precios ajustados)"
- Valor de rescate (US$)
- Valor de rescate ajustado
- Tipo de bien
- FC (asociado al Tipo de bien)

**Botones:**
- Agregar Beneficio
- Guardar
- Siguiente

**Acciones:**
- Clic en "Agregar Beneficio" para abrir la pantalla "Detalle del Beneficio" (Anexo A.2).
- Clic en "Guardar" (ver FA03).
- Clic en "Siguiente" (ver FA04).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

Vida útil (períodos a proyectar): (campo vacío en el mockup)

| BENEFICIO | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | ... | n |
|-----------|---|---|---|---|---|---|---|---|---|----|-----|---|
| **Beneficios Directos** | | | | | | | | | | | | |
| Reducción de tiempos de traslado | | | | | | | | | | | | |
| Beneficio 2 | | | | | | | | | | | | |
| **Beneficios Indirectos** | | | | | | | | | | | | |
| Beneficio 3 | | | | | | | | | | | | |
| Beneficio 4 | | | | | | | | | | | | |
| **Externalidades** | | | | | | | | | | | | |
| Beneficio 5 | | | | | | | | | | | | |
| **FLUJO DE BENEFICIOS (Precios de mercado)** | | | | | | | | | | | | |
| **FLUJO DE BENEFICIOS (Precios ajustados)** | | | | | | | | | | | | |

Valor de rescate (US$): (campo vacío)
Valor de rescate ajustado: (campo vacío)
Tipo de bien: Equipos (seleccionado por defecto en el desplegable) — FC: 1.00

## Pantalla: Detalle del Beneficio (Anexo A.2)

**Descripción:** Pantalla identificada en el encabezado como "DETALLE DEL BENEFICIO", donde el Técnico URP registra el tipo de beneficio, el nombre del beneficio, el parámetro de corrección y los montos por período, ya sea de forma manual o automática.

**Campos:**
- Tipo de Beneficio
- Beneficio
- Parámetro
- FC
- Tipo de ingreso (Manual / Automático)
- Monto período 1
- Tasa de crecimiento proyectado
- Tabla "Montos por período": Período, Monto (US$) Precios de mercado, Monto (US$) Precios ajustados

**Botones:**
- Guardar
- Salir

**Acciones:**
- Selección del tipo de beneficio, registro del nombre del beneficio y selección del parámetro (FA01 paso 1.1 / FA02 paso 2.1).
- Selección de tipo de ingreso Manual (FA01) o Automático (FA02).
- Registro manual de montos por período (FA01) o cálculo automático mediante fórmula (FA02).
- Clic en "Guardar" (FA1.1 / FA2.1) o "Salir" (FA1.2 / FA2.2).

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**

- Tipo de Beneficio: Beneficios Directos
- Beneficio: Reducción de tiempos de traslado
- Parámetro: Valor social del tiempo — FC: 1.10
- Tipo de ingreso: Automático (seleccionado)
- Monto período 1: $ 800.00
- Tasa de crecimiento proyectado: 5.00%

| Período | Monto (US$) Precios de mercado | Monto (US$) Precios ajustados |
|---------|----------------------------------|----------------------------------|
| 1 | $ 800.00 | $ 880.00 |
| 2 | $ 840.00 | $ 924.00 |
| 3 | $ 882.00 | $ 970.20 |
| ... | | |
| n | | |

> Nota: el valor de FC mostrado en este mockup (1.10) no coincide con el valor de FC del catálogo "Parámetros" para "Valor social del tiempo" (1.00). Ver Observaciones.

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
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Al dar clic en el botón "Guardar" en la pantalla "Detalle del Beneficio" (FA1.1, FA2.1) y en la pantalla "Beneficios del proyecto" (FA03); se muestra el mensaje emergente del Anexo A.3. |
| Ayuda contextual | No especificado en el documento (solo se indica que el Sistema "indicará qué información se debe completar en dicho campo"). | Al acercar el cursor a un campo y dar clic en el ícono "?" (RN07). |

---

# Observaciones

- Existe una discrepancia entre el pie de página de todas las páginas del documento, que indica "Fecha: JUN 2025", y la tabla "Historial de Revisiones", que indica "JUL 2025" para la misma versión 1.0.
- El paso 4 del Flujo Básico denomina a la segunda opción del botón radial como "Proyectada"; sin embargo, en el resto del documento (título del Flujo Alternativo 2, paso 2.1, y el mockup del Anexo A.2 "Tipo de ingreso: Manual / Automático") dicha opción se denomina "Automático". No se resuelve cuál es el nombre correcto de la opción.
- Los Flujos Alternativos "FA1.1", "FA1.2", "FA2.1" y "FA2.2" se titulan en el documento como "(del Flujo Detalle de Actividad)", denominación que corresponde al caso de uso CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" y no a la pantalla "Detalle del Beneficio" propia de este caso de uso (CU-PRE-20). Se transcribe tal como aparece en el documento fuente, sin corregirlo.
- El campo "Vida útil" se nombra "Vida útil (años a proyectar)" en la tabla de formatos del Anexo B (pantalla "Beneficios del proyecto"), mientras que en la sección "Campos requeridos" y en el mockup del Anexo A.1 se nombra "Vida útil (períodos a proyectar)". No se aclara si son el mismo campo con dos nombres o campos distintos.
- El campo "Periodos" de la tabla "Beneficios del proyecto" indica en el Anexo B que su valor "procede... del campo Monto ($) Precios Ajustados" de la pantalla "Detalle del Beneficio", lo cual no se corresponde de forma evidente con la existencia de una fila separada "FLUJO DE BENEFICIOS (Precios de mercado)" en la misma tabla. No se resuelve esta discrepancia; se transcribe literalmente lo indicado en el documento.
- El catálogo "Parámetros" (Anexo B) lista un Factor de Corrección de 1.00 para todos sus elementos, incluyendo "Valor social del tiempo"; sin embargo, el mockup del Anexo A.2 muestra el campo "FC" con el valor 1.10 para el parámetro "Valor social del tiempo" seleccionado. Existe una discrepancia entre el catálogo documentado y el valor de ejemplo mostrado en la pantalla.
- El campo "Valor de rescate ajustado" aparece dos veces en la tabla de formatos del Anexo B para la pantalla "Beneficios del proyecto", con dos descripciones distintas: la primera indica genéricamente que "se calcula tomando del 'Valor de rescate' y multiplicándolo por el factor de corrección seleccionado para el beneficio"; la segunda proporciona la fórmula explícita "Valor de rescate ajustado = Valor de rescate x FC" y aclara que el campo "solo será visible para los usuarios centrales". Ambas entradas se transcriben tal como aparecen, sin fusionarlas ni resolver la duplicidad.
- Los campos "Tipo de Beneficio" y "Tipo de ingreso" (pantalla "Detalle del beneficio") se documentan en el Anexo B con la columna "Editable" en "No", pero los pasos 1.1 y 2.1 de los Flujos Alternativos 1 y 2 describen que el Técnico URP "selecciona una de las opciones" de dichos campos, lo cual sugiere que sí son interactuables por el usuario. No se resuelve esta discrepancia.
- El campo "Parámetro" (Anexo B, pantalla "Detalle del beneficio") corresponde conceptualmente al campo denominado "Factor de corrección" en la sección "Campos requeridos" y en los pasos de los Flujos Alternativos 1 y 2. El documento no aclara si "Parámetro" y "Factor de corrección" son nombres alternativos del mismo campo o campos distintos.
- El campo "Beneficios" (Anexo B, pantalla "Detalle del beneficio") se documenta con ese nombre en plural, mientras que en los pasos de los Flujos Alternativos 1 y 2 el campo equivalente se denomina "Beneficio" (singular): "Registra el nombre del beneficio en el campo 'Beneficio'". No se resuelve esta discrepancia de nomenclatura.
- El campo "Periodo" (Anexo B, pantalla "Detalle del beneficio") hace referencia a "Flujo Alternativo 2 – FA02 Pantalla Beneficios (Anexo A.2) – Automático, 6.2"; sin embargo, la numeración de pasos utilizada en el propio documento para el Flujo Alternativo 2 es "2.1", "2.2", "2.3" (no existe un paso "6.2" en el documento). No se puede determinar a qué paso hace referencia esta cita.
- RN09 establece que la tabla de presupuesto a precios ajustados "sólo podrá ser visible para los usuarios internos", mientras que los pasos FA1.1.4 y FA2.1.4 indican que la fila "Flujo de beneficios (precios ajustados)" "sólo será visible para usuarios centrales", y los campos "FC" y "Valor de rescate ajustado" (Anexo B) indican que "solo será visible para los usuarios centrales". **Resuelto (RQ-C-03, ronda 3):** "usuarios internos" y "usuarios centrales" son sinónimos — cualquier usuario del Ministerio de Hacienda, independientemente de su rol. Ver nota de resolución completa en RN09.
- La postcondición del caso de uso se denomina "CU-PRE-21 Flujo de Caja y cálculo de Indicadores", mientras que el paso 4.2 del Flujo Alternativo 4 (Siguiente) se refiere al mismo caso de uso como "CU-PRE-21 'Flujo de Caja e Indicadores'". Existe una ligera variación en el nombre citado.
- Las fórmulas descritas en el Flujo Alternativo 1 (paso 1.4), en el Flujo Alternativo 2 (pasos 2.2 y 2.3) y en el campo "Monto Precios Ajustados" del Anexo B presentan caracteres que no se muestran con claridad en el documento fuente (ver sección "Datos Pendientes de Definir").

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Beneficio | Registro individual de un beneficio esperado del proyecto, clasificado por tipo (Directo, Indirecto, Externalidad). | Registro (FA01 paso 1.1 / FA02 paso 2.1); traslado a la pantalla "Beneficios del proyecto" según tipo de beneficio (FA1.1.4, FA2.1.4; RN08); adición/eliminación de filas (RN03). |
| Período | Columna de monto correspondiente a cada periodo proyectado según la "Vida útil" tomada de CU-PRE-18. | Generación automática de filas/columnas según la Vida útil de CU-PRE-18 (FA01 paso 1.2, FA02 paso 2.2; RN05); cálculo automático de montos proyectados en modo Automático (FA02 paso 2.2); cálculo automático de montos ajustados (FA01 paso 1.4, FA02 paso 2.3). |
| Valor de rescate | Monto de rescate del bien asociado al proyecto, con su versión ajustada por Factor de Corrección. | Registro manual del "Valor de rescate" (Anexo B); cálculo automático del "Valor de rescate ajustado" (Anexo B, fórmula "Valor de rescate ajustado = Valor de rescate x FC"). |
| Catálogo Parámetros | Listado de parámetros de valoración social/económica con su Factor de Corrección. | Consulta/selección para asignar el Factor de Corrección de un beneficio (FA01 paso 1.1, FA02 paso 2.1); sujeto a actualización por parte de la DGICP (Nota del catálogo). |
| Catálogo Tipo de Beneficio | Listado de clasificaciones de beneficio (Directos, Indirectos, Externalidades). | Consulta/selección para clasificar un beneficio (FA01 paso 1.1, FA02 paso 2.1; RN08). |
| Catálogo Tipo de bienes (Valor de rescate) | Listado de tipos de bien con su Factor de Corrección, usado para el cálculo del valor de rescate ajustado. | Consulta/selección del "Tipo de bien" (Anexo B, campo "Tipo de bien"); provee el FC usado en el cálculo del "Valor de rescate ajustado" (Anexo B). |

---

# Catálogos Detectados

## Catálogo "Parámetros"

| Parámetro | Factor de corrección |
|-----------|------------------------|
| Valor social del tiempo | 1.00 |
| Valor social de la vida | 1.00 |
| Precio social del combustible | 1.00 |
| Precio social del carbono | 1.00 |
| PS agua potable | 1.00 |
| PS transporte | 1.00 |
| PS energía | 1.00 |
| PS agropecuario | 1.00 |
| PS forestal | 1.00 |
| PS servicios turísticos | 1.00 |

> Nota importante (según el documento): Este catálogo estará sujeto a actualización por parte de la DGICP.

> Ver Observaciones respecto a la discrepancia entre el FC de este catálogo (1.00 para "Valor social del tiempo") y el valor de FC mostrado en el mockup del Anexo A.2 (1.10).

## Catálogo "Tipo de Beneficio"

| Tipo de Beneficio |
|---------------------|
| Beneficios Directos |
| Beneficios Indirectos |
| Externalidades |

## Catálogo "Tipo de bienes (Valor de rescate)"

| Tipo de bien | FC |
|--------------|----|
| Equipos | 1.00 |
| Edificios | 1.00 |
| Terrenos | 1.00 |
| Vehículos | 1.00 |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Despliegue de pantalla "Detalle del Beneficio" | Sistema (FB, paso 3), tras clic en "Agregar Beneficio" | Pantalla Anexo A.2 |
| Generación de filas de período | Sistema (FA01 paso 1.2 / FA02 paso 2.2), según la "Vida útil" registrada en CU-PRE-18 | Tabla "Montos por período" (Anexo A.2) |
| Cálculo de monto ajustado por período | Sistema (FA01 paso 1.4 / FA02 paso 2.3), tras registro o cálculo del monto en precios de mercado | Columna "Monto Precios ajustados" (Anexo A.2) |
| Traslado de beneficio a la pantalla "Beneficios del proyecto" | Sistema (FA1.1.4 / FA2.1.4), tras clic en "Guardar" en "Detalle del Beneficio" | Tabla "Beneficios del proyecto" (Anexo A.1), sección según Tipo de Beneficio (RN08) |

---

# Integraciones

> No especificado en el documento.

---

# Datos Pendientes de Definir

- Las fórmulas matemáticas descritas en el Flujo Alternativo 1 (paso 1.4), el Flujo Alternativo 2 (pasos 2.2 y 2.3) y el campo "Monto Precios Ajustados" del Anexo B no pueden determinarse con precisión a partir del documento: los caracteres de dichas fórmulas presentan una notación que no es legible con claridad en el documento fuente. Para la fórmula del paso 2.2 se cuenta con la definición de variables (Montoperiodo 1, r, n), pero no con la notación exacta de la fórmula. Esta ambigüedad bloquea la implementación exacta de los cálculos descritos.
- No se especifica el disparador (evento que inicia el caso de uso) de forma explícita.
- No se especifica la prioridad del caso de uso.
- No se especifica una tabla de códigos de error para este caso de uso.
- **Resuelto (RQ-C-03, ronda 3):** "usuarios internos" (RN09) y "usuarios centrales" (FA1.1.4, FA2.1.4, Anexo B) se refieren al mismo grupo de usuarios (cualquier usuario del Ministerio de Hacienda). Esta misma equivalencia se confirmó para CU-PRE-17 ("actores internos"); el caso de CU-PRE-21 mantiene además a CEPA, CEL, ANDA, INDES e ISTU por un motivo distinto (proyectan ingresos, no beneficios) y no forma parte de esta definición base. Ver nota de resolución en RN09.
- No se puede determinar a qué paso del documento corresponde la referencia "6.2" citada en la descripción del campo "Periodo" del Anexo B, ya que no existe un paso con esa numeración en el Flujo Alternativo 2 descrito en el documento.
- No se resuelve la duplicidad de descripciones del campo "Valor de rescate ajustado" en el Anexo B; no se puede determinar cuál de las dos descripciones (genérica o con fórmula explícita) debe prevalecer para la implementación.