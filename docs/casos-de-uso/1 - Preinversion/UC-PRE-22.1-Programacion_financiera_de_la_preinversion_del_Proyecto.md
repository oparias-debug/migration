---
id: CU-PRE-22.1
codigo: CU-PRE-22.1
nombre: Programación financiera de la preinversión del Proyecto
modulo: Preinversión
submodulo: Programación
version: "1.0"
fuente_pdf: CU-PRE-22_1_Registro_Programación_financiera_PRE_de_Proyectos_AGO_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 6

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-07 a CU-PRE-18"]

casos_relacionados: ["CU-PRE-30", "CU-PRE-03.5"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Programación Financiera Preinversión (Anexo A.1)"]

procesos: []

servicios_externos: []

entidades: ["Programación Financiera de la Preinversión", "Etapa", "Período de programación", "Costo de la etapa"]

catalogos: []

palabras_clave: ["Programación financiera", "Preinversión", "Etapa", "Período", "Costo de la etapa", "Programación", "Total"]

ultima_actualizacion: "AGO 2025"

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos: {}
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
      pagina: 4
    RN06:
      pagina: 4
    RN07:
      pagina: 4
    RN08:
      pagina: 4
    RN09:
      pagina: 4
  anexos:
    A1:
      nombre: Programación Financiera Preinversión
      pagina: 5
    B1:
      nombre: Requerimientos Funcionales - Formatos
      pagina: 6
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|-------|---------|--------------|-------|-------|
| AGO 2025 | 1.0 | Versión Inicial | Equipo Preinversión | No especificado en el documento. |

---

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Programación financiera de la preinversión del Proyecto |
| Código | CU-PRE-22.1 |
| Módulo | Preinversión |
| Fuente | CU-PRE-22.1 Registro_Programación financiera PRE de Proyectos_AGO 2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Períodos a programar (preinversión)
- Etapa
- Monto
- Programación
- Total

> Nota de ambigüedad: el elemento "Monto" listado aquí no corresponde de forma clara a ningún campo documentado en la tabla de formatos del Anexo B (que solo describe "Períodos a programar (preinversión)", "Etapa", "Programación" y "Total programación"). Ver sección "Datos Pendientes de Definir".

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permitirá al Técnico URP registrar la programación financiera de la preinversión para un proyecto.

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
3. Contar con el proyecto formulado de "Población Objetivo a Flujo de costos de Operación y Mantenimiento" (CU-PRE-07 a CU-PRE-18).

> Nota: el ítem 3 hace referencia a un rango de casos de uso ("CU-PRE-07 a CU-PRE-18") sin listarlos individualmente. Se transcribe tal como aparece en el documento, sin desagregar el rango.

---

# Flujo Principal

## FB – Flujo Básico

1. Técnico URP ingresa en la pestaña "Programación" en la sección "Programación financiera de la Preinversión". Registra la cantidad de períodos de la preinversión en el campo "Períodos a programar (preinversión)" y da clic en el botón "Aceptar".
2. Sistema muestra la pantalla del Anexo A.1. En la sección "Programación" mostrará una cantidad de columnas igual a la cantidad de períodos registrados en el campo "Períodos a programar (preinversión)".
3. Técnico URP registra la información en los campos del Anexo A.1. Da clic en "Guardar".
4. Sistema guarda los registros realizados por el Técnico URP.

---

# Flujos Alternos

> No especificado en el documento. A diferencia de otros casos de uso de la serie, este documento no desarrolla Flujos Alternativos (no existe un botón "Siguiente" documentado ni un flujo de "Guardar" con mensaje emergente asociado a un Anexo específico). El paso 4 del Flujo Básico solo indica que "el Sistema guarda los registros realizados por el Técnico URP", sin mayor detalle de interacción. Ver Observaciones.

---

# Excepciones

> No especificado en el documento. El documento no desarrolla una tabla de excepciones con código, descripción y consecuencia para este caso de uso.

# Postcondiciones

1. CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión".
2. CU-PRE-03.5 "Selección y registro de etapas".

> Nota de ambigüedad: CU-PRE-03.5 aparece tanto en las Precondiciones (como origen de la Ruta de Preinversión) como en las Postcondiciones de este caso de uso. El documento no aclara explícitamente en qué consiste el retorno o actualización de información hacia CU-PRE-03.5 tras completar este caso de uso, más allá de lo indicado en RN06 (actualización del campo "Costo de la etapa") y en el campo "Total programación" (consolidación de totales por etapa). Ver Observaciones.

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla "Programación Financiera", según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

## RN03

**Descripción:** En la columna "Etapa" del Anexo A.1 se visualizarán las etapas de preinversión registradas en la pantalla Anexo A.1 Sección II Registro de Etapas del CU-PRE-03.5 "Selección y registro de etapas".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

## RN04

**Descripción:** Para el Anexo A.1, el período de programación mostrará como título "periodo", acompañado de un número consecutivo (por ejemplo, Periodo 1), el cual también deberá visualizarse en columnas posteriores (por ejemplo, Periodo 2, Periodo 3…).

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

> Nota de ambigüedad: el mockup del Anexo A.1 muestra los encabezados de columna únicamente como números ("1", "2", "3", "n"), sin el texto "Periodo" descrito en esta regla. Existe una discrepancia entre RN04 y el mockup. Ver Observaciones.

## RN05

**Descripción:** La cantidad de columnas que se mostrarán en la programación serán diligenciadas desde el campo "Períodos a programar (preinversión)" según se muestra en el Flujo Básico.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

## RN06

**Descripción:** El valor del campo "Costo de la etapa" para cada etapa se debe actualizar en el campo "Costo de la etapa" del CU-PRE-03.5 "Selección y registro de etapas", según corresponda.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

> Nota de ambigüedad: el campo "Costo de la etapa" referenciado en esta regla no está documentado en la tabla de formatos del Anexo B de este caso de uso. Ver "Datos Pendientes de Definir".

## RN07

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

## RN08

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

## RN09

**Descripción:** La programación de la preinversión se hará para periodos futuros al actual. Por lo tanto, los periodos anteriores aparecerán deshabilitados. Se podrán programar más de una etapa de la Preinversión en un mismo período.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-22.1.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Períodos a programar (preinversión) | Campo que permite al Técnico URP registrar la cantidad de períodos a programar (Preinversión). Campo obligatorio. | Numérico | Numérico | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. Determina la cantidad de columnas de período mostradas en el Anexo A.1 (RN05). |
| Etapa | Este campo se completará automáticamente, según lo registrado en CU-PRE-03.5 "Selección y registro de etapas". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver RN03. |
| Costo de la etapa | Campo mostrado en el mockup del Anexo A.1 (columna "COSTO DE LA ETAPA") y referenciado en RN06, según la cual su valor debe actualizarse en el campo "Costo de la etapa" del CU-PRE-03.5 "Selección y registro de etapas". No se cuenta con una descripción de este campo en la tabla de formatos del Anexo B. | No especificado en el documento. | No especificado en el documento. | No especificado en el documento. | No especificado en el documento. | Editable: No especificado en el documento. Ver sección "Datos Pendientes de Definir". |
| Programación | Espacio para que el Técnico URP distribuya en períodos los desembolsos estimados en US$. El sistema deberá agregar el separador de miles (,). Campo obligatorio. | Moneda | Moneda | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. |
| Total programación | El sistema suma los valores de cada fila y columna. La consolidación de estos totales por etapa serán los que se lleven a CU-PRE-03.5 "Selección y registro de etapas". | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Campos pendientes de completar (general, al dar clic en "Guardar") | El sistema sombreará los bordes de los campos pendientes en color rojo (RN07). | No especificado en el documento (texto exacto de mensaje no indicado; solo se describe el comportamiento visual de sombreado en rojo). |
| Períodos a programar (preinversión) | Campo obligatorio. | No especificado en el documento. |
| Programación | Campo obligatorio. | No especificado en el documento. |

---

# Errores

> No especificado en el documento. El documento no desarrolla una tabla de códigos de error para este caso de uso.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pantalla "Programación Financiera", según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |
| Todos los demás actores (no especificados individualmente en el documento) | Visualizar únicamente la información de las Unidades Ejecutoras, según credenciales. | RN01 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos" (precondición)
- CU-PRE-03.5 "Selección y registro de etapas" (precondición y también postcondición; ver nota de ambigüedad)
- CU-PRE-07 a CU-PRE-18 (rango de casos de uso citado como precondición, referido como "Población Objetivo a Flujo de costos de Operación y Mantenimiento")
- CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión" (postcondición)

**Procesos relacionados:**
> No especificado en el documento.

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Pantalla: Programación Financiera Preinversión (Anexo A.1)

**Descripción:** Pantalla identificada en el encabezado como "PROGRAMACIÓN FINANCIERA PREINVERSIÓN", donde el Técnico URP distribuye por período el costo de cada etapa de la preinversión.

**Campos:**
- Períodos a programar (Preinversión)
- Tabla: Etapa, Costo de la etapa, Programación (columnas de período 1 a n), Total

**Botones:**
- Aceptar
- Guardar

**Acciones:**
- Registro de la cantidad de períodos a programar y clic en "Aceptar" (FB, paso 1), lo cual genera la cantidad de columnas de período correspondiente (FB, paso 2; RN05).
- Registro de la programación por período y clic en "Guardar" (FB, paso 3), tras lo cual el Sistema guarda los registros (FB, paso 4).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

Períodos a programar (Preinversión): (campo vacío en el mockup)

| ETAPA | COSTO DE LA ETAPA | 1 | 2 | 3 | n | TOTAL |
|-------|---------------------|---|---|---|---|-------|
| Perfil | $ 2,000.00 | $ 1,000.00 | $ 1,000.00 | | | $ 2,000.00 |
| Diseño | $ 15,000.00 | | | $ 10,000.00 | $ 5,000.00 | $ 15,000.00 |
| **TOTAL** | **$ 17,000.00** | **$ 1,000.00** | **$ 1,000.00** | **$ 10,000.00** | **$ 5,000.00** | **$ 17,000.00** |

> Nota: los encabezados de columna de período se muestran en el mockup como números simples ("1", "2", "3", "n"), sin el texto "Periodo" que describe RN04 (ver Observaciones).

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Ayuda contextual | No especificado en el documento (solo se indica que el Sistema "indicará qué información se debe completar en dicho campo"). | Al acercar el cursor a un campo y dar clic en el ícono "?" (RN08). |

> Nota: a diferencia de otros casos de uso de la serie, este documento no incluye un mensaje de confirmación de guardado (no existe un Anexo con un mockup de mensaje emergente tipo "¡Guardado!"). Ver Observaciones.

---

# Observaciones

- RN04 indica que el título de cada columna de periodo debe mostrarse como "periodo" acompañado de un número consecutivo (por ejemplo, "Periodo 1", "Periodo 2"...); sin embargo, el mockup del Anexo A.1 muestra los encabezados de columna únicamente como números ("1", "2", "3", "n"), sin el texto "Periodo". Existe una discrepancia entre RN04 y el mockup.
- CU-PRE-03.5 "Selección y registro de etapas" aparece tanto en las Precondiciones (como origen de la Ruta de Preinversión) como en las Postcondiciones de este caso de uso. El documento no aclara explícitamente en qué consiste el retorno o actualización de información hacia CU-PRE-03.5 tras completar este caso de uso, más allá de lo indicado en RN06 (actualización del campo "Costo de la etapa") y en el campo "Total programación" (consolidación de totales por etapa).
- A diferencia de otros casos de uso de la serie (p. ej. CU-PRE-18, CU-PRE-20, CU-PRE-21.5), este documento no incluye Flujos Alternativos: no existe un botón "Siguiente" documentado ni un flujo de "Guardar" con mensaje emergente asociado a un Anexo específico. El Flujo Básico (paso 4) solo indica que "el Sistema guarda los registros realizados por el Técnico URP", sin mayor detalle de interacción ni mensaje de confirmación.
- La sección "Campos requeridos" de la Identificación del caso de uso incluye el elemento "Monto", el cual no se encuentra documentado como tal en la tabla de formatos del Anexo B (que solo describe "Períodos a programar (preinversión)", "Etapa", "Programación" y "Total programación").
- El campo "Costo de la etapa", visible en el mockup del Anexo A.1 (columna "COSTO DE LA ETAPA") y referenciado en RN06, no está documentado en la tabla de formatos del Anexo B (no se especifica su Tipo, Formato, si es Editable, ni su Detalle funcional completo).
- La precondición "Contar con el proyecto formulado de 'Población Objetivo a Flujo de costos de Operación y Mantenimiento' (CU-PRE-07 a CU-PRE-18)" hace referencia a un rango de casos de uso sin listarlos individualmente; se transcribe tal como aparece en el documento, sin desagregar el rango.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Programación Financiera de la Preinversión | Registro consolidado de la distribución por período del costo de cada etapa de la preinversión de un proyecto. | Registro/edición según credenciales (RN01); generación de columnas de período según "Períodos a programar" (RN05); cálculo automático de totales por fila y columna (Anexo B, campo "Total programación"). |
| Etapa | Etapa de preinversión visualizada en la columna "Etapa", originada en CU-PRE-03.5. | Visualización automática desde CU-PRE-03.5 (RN03); posibilidad de programar más de una etapa en un mismo período (RN09). |
| Período de programación | Columna de la tabla de programación correspondiente a cada período futuro al periodo actual. | Generación automática de columnas según "Períodos a programar" (RN05); deshabilitación de periodos anteriores al actual (RN09). |
| Costo de la etapa | Valor monetario del costo de cada etapa, mostrado en el mockup del Anexo A.1 y sincronizado con CU-PRE-03.5. | Actualización hacia el campo "Costo de la etapa" de CU-PRE-03.5 (RN06); ver Datos Pendientes de Definir respecto a su documentación incompleta en el Anexo B. |

---

# Catálogos Detectados

> No especificado en el documento. Este documento no incluye catálogos.

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Generación de columnas de período | Sistema (FB, paso 2), tras registrar "Períodos a programar (preinversión)" y clic en "Aceptar" | Sección "Programación" del Anexo A.1 |
| Guardado de registros | Sistema (FB, paso 4), tras clic en "Guardar" | Pantalla "Programación Financiera Preinversión" |
| Actualización de "Costo de la etapa" | Sistema (RN06) | CU-PRE-03.5 "Selección y registro de etapas" |
| Consolidación de "Total programación" por etapa | Sistema (Anexo B, campo "Total programación") | CU-PRE-03.5 "Selección y registro de etapas" |

---

# Integraciones

> No especificado en el documento.

---

# Datos Pendientes de Definir

- El campo "Costo de la etapa", mostrado en el mockup del Anexo A.1 y referenciado en RN06, no está documentado en la tabla de formatos del Anexo B (no se especifica su Tipo, Formato, si es Editable, ni su Detalle funcional completo). Esto impide determinar con precisión cómo debe implementarse este campo (por ejemplo, si es editable por el Técnico URP, se completa automáticamente desde CU-PRE-03.5, o se calcula mediante alguna fórmula).
- El elemento "Monto" mencionado en la sección "Campos requeridos" no corresponde de forma clara a ningún campo documentado en la tabla de formatos del Anexo B. No se puede determinar si "Monto" es un nombre alternativo de "Costo de la etapa", de "Programación", o un campo distinto no descrito en el documento.
- No se aclara en qué consiste exactamente la actualización o retorno de información hacia CU-PRE-03.5 "Selección y registro de etapas" indicada como postcondición, más allá de lo mencionado en RN06 (actualización de "Costo de la etapa") y en el campo "Total programación" (consolidación de totales por etapa).
- No se especifica el disparador (evento que inicia el caso de uso) de forma explícita.
- No se especifica la prioridad del caso de uso.
- No se especifica una tabla de códigos de error para este caso de uso.
