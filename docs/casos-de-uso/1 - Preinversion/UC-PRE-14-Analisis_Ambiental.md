---
id: CU-PRE-14
codigo: CU-PRE-14
nombre: Análisis Ambiental
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-14_Análisis_Ambiental_AGO_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 11

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5"]

casos_relacionados: ["CU-PRE-15"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Análisis Ambiental (Anexo A.1)", "Mensaje de Guardado (Anexo A.2)"]

procesos: ["Despliegue condicional de la tabla de Análisis Ambiental (RN04)", "Adición/eliminación de filas de la matriz de gestión ambiental (RN03)", "Cálculo automático del total de costos de medidas de gestión (Anexo B.1)"]

servicios_externos: []

entidades: ["Análisis Ambiental", "Impacto Ambiental", "Medida de Gestión", "Proyecto", "Unidad Ejecutora"]

catalogos: ["Catálogo Medio (Anexo C.1)", "Catálogo Impacto (Anexo C.2)", "Catálogo Magnitud (Anexo C.3)", "Catálogo Duración (Anexo C.4)", "Catálogo Reversibilidad (Anexo C.5)"]

palabras_clave: ["Análisis Ambiental", "Impacto Ambiental", "Medidas de Gestión", "Reversibilidad", "Técnico URP", "Preinversión"]

ultima_actualizacion: AGO 2025

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    FA01:
      pagina: 3
    FA02:
      pagina: 4
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
  anexos:
    A1:
      nombre: Análisis Ambiental (Pantalla)
      pagina: 5
    A2:
      nombre: Guardar (Mensaje emergente)
      pagina: 5
    B1:
      nombre: Formatos (Pantalla "Análisis Ambiental")
      pagina: 5
    C1:
      nombre: Catálogo Medio
      pagina: 7
    C2:
      nombre: Catálogo Impacto
      pagina: 7
    C3:
      nombre: Catálogo Magnitud
      pagina: 7
    C4:
      nombre: Catálogo Duración
      pagina: 7
    C5:
      nombre: Catálogo Reversibilidad
      pagina: 7
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Análisis Ambiental |
| Código | CU-PRE-14 |
| Módulo | Preinversión |
| Fuente | CU-PRE-14_Análisis_Ambiental_AGO_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Medio
- Impacto
- Tipo de impacto
- Magnitud
- Duración
- Reversibilidad
- Medidas de gestión
- Costo de medida de gestión
- Total costo medidas de gestión

---

# Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|-------|---------|-------------|-------|-------|
| AGO 2025 | 1.0 | Primera Versión | Equipo Preinversión | No especificado en el documento. |

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección explícita titulada "Objetivo"; presenta en su lugar una sección "Descripción caso de uso" (ver sección "Descripción" de este documento).

---

# Descripción

Este caso de uso permite al actor "Técnico URP" identificar si el proyecto tiene impactos ambientales asociados, y registrar una matriz de gestión ambiental en los casos que aplique, describiendo el impacto identificado, sus principales características, las medidas de gestión a implementar y el costo de las mismas.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico PRE (según RN02, puede visualizar la información de todas las Unidades Ejecutoras).
- "Todos los demás actores" (según RN01, únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales). El documento no nombra individualmente a estos otros actores.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP generado en CU-PRE-01 "Registro de Proyectos".
2. Contar con la Ruta de Preinversión desde CU-PRE-03.5 "Selección y registro de etapas".

---

# Flujo Principal

1. Técnico URP: Ingresa a la pestaña "Formulación del Proyecto" en la sección "Análisis Ambiental".
2. Técnico URP: Completa la información de los campos del Anexo A.1.

---

# Flujos Alternos

## FA-01 Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Guardar".
2. Sistema: Muestra el mensaje emergente del Anexo A.2 "Sus datos han sido guardados con éxito. Recuerde que los costos derivados del análisis ambiental deben estar considerados dentro del presupuesto del proyecto."
3. Técnico URP: Da clic en "Aceptar" a mensaje emergente.
4. Sistema: Guarda la información registrada y se mantiene en la sección.

**Resultado**

> No especificado en el documento.

## FA-02 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Siguiente".
2. Sistema: Avanza a la siguiente sección CU-PRE-15 "Análisis de Riesgo" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|--------------|--------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento. El documento no incluye una tabla o listado explícito de excepciones para este caso de uso.

# Postcondiciones

1. El proyecto cuenta con el Análisis Ambiental registrado y avanza a CU-PRE-15 "Análisis de riesgo".

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** No especificado en el documento.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** No especificado en el documento.

## RN03

**Descripción:** El Técnico URP podrá adicionar o eliminar filas a la tabla de la pantalla "Análisis Ambiental" (ver Anexo A.1).

**Origen:** No especificado en el documento.

## RN04

**Descripción:** En la pregunta "¿Existen impactos ambientales asociados al proyecto?" del Anexo A.1, si se da clic en el botón radial "Sí" el sistema desplegará la tabla "Análisis Ambiental" para el registro de la información pertinente; si se da clic en "No" la lista no se desplegará.

**Origen:** No especificado en el documento.

## RN05

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** No especificado en el documento.

## RN06

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** No especificado en el documento.

## RN07

**Descripción:** El Sistema permitirá añadir filas anidadas por cada tipo de medio que se seleccione.

**Origen:** No especificado en el documento.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Medio | Campo para seleccionar el tipo de medio o componente del ambiente en que el proyecto tendrá impacto. El sistema mostrará en la lista desplegable el catálogo C.1: Físico – Agua; Físico – Tierra; Físico – Aire; Biológico – Flora; Biológico – Fauna; Social – Estructura Social; Social – Patrimonio Cultural. Solo podrá seleccionarse un elemento por cada fila. | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Impacto | Campo para registrar la descripción breve del impacto. El campo debe tener una amplitud de 200 caracteres. | Texto | Texto | Sí | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Tipo de impacto | Campo para seleccionar el tipo de impacto, se mostrará un listado con las opciones del catálogo C.2: Positivo; Negativo. Solo podrá seleccionarse un elemento por cada fila. | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Magnitud | Campo para seleccionar la magnitud del impacto, se mostrará un listado con las opciones del catálogo C.3: Leve; Moderado; Fuerte. Solo podrá seleccionarse un elemento por cada fila. | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Duración | Campo para seleccionar el tiempo de acción del impacto, se mostrará un listado con las opciones según el catálogo C.4: Corto Plazo; Mediano Plazo; Largo Plazo. Solo podrá seleccionarse un elemento por cada fila. | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Reversibilidad | Campo para seleccionar si un proyecto es o no reversible, se mostrará un listado con las opciones según el catálogo C.5: Reversible; Irreversible. Solo podrá seleccionarse un elemento por cada fila. | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Medida de gestión | Campo para describir el tipo de medida de gestión a implementar por cada impacto. El campo debe tener una amplitud de 200 caracteres. | Texto | Texto | Sí | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Costo de medida de gestión | Campo para registrar el costo de la medida a implementar por cada impacto. En US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Total costo medidas de gestión | Campo que muestra la sumatoria de valores de cada acción de la columna "Costo". El Sistema deberá sumarlo de manera automática. En US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|--------------------|
| Medio | Campo obligatorio; solo podrá seleccionarse un elemento por cada fila (Anexo B.1). | No especificado en el documento. |
| Impacto | Campo obligatorio; amplitud máxima de 200 caracteres (Anexo B.1). | No especificado en el documento. |
| Tipo de impacto | Campo obligatorio; solo podrá seleccionarse un elemento por cada fila (Anexo B.1). | No especificado en el documento. |
| Magnitud | Campo obligatorio; solo podrá seleccionarse un elemento por cada fila (Anexo B.1). | No especificado en el documento. |
| Duración | Campo obligatorio; solo podrá seleccionarse un elemento por cada fila (Anexo B.1). | No especificado en el documento. |
| Reversibilidad | Campo obligatorio; solo podrá seleccionarse un elemento por cada fila (Anexo B.1). | No especificado en el documento. |
| Medida de gestión | Campo obligatorio; amplitud máxima de 200 caracteres (Anexo B.1). | No especificado en el documento. |
| Costo de medida de gestión | Campo NO obligatorio; formato Moneda en US$ con separador de miles (Anexo B.1). | No especificado en el documento. |
| Campos pendientes de completar (no se especifica cuáles exactamente son obligatorios) | Al dar clic en "Guardar", si hay campos pendientes de completar, el sistema sombrea los bordes de dichos campos en color rojo (RN05). | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pantalla "Análisis Ambiental". | RN01 |
| Técnico URP | Adicionar o eliminar filas de la tabla "Análisis Ambiental". | RN03 |
| Otros actores (no nombrados individualmente en el documento) | Visualizar la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-15 "Análisis de Riesgo"

**Procesos relacionados:** No especificado en el documento.

**Servicios externos:** No especificado en el documento.

---

# Pantallas

## Pantalla: Análisis Ambiental (Anexo A.1)

**Descripción:** Pantalla que permite al Técnico URP indicar si existen impactos ambientales asociados al proyecto y, en caso afirmativo, registrar una matriz de gestión ambiental con el medio afectado, la descripción del impacto, su tipo, magnitud, duración, reversibilidad, las medidas de gestión y sus costos asociados.

**Campos:**
- ¿Existen impactos ambientales asociados al proyecto? (Sí/No)
- Medio
- Descripción del Impacto
- Tipo de Impacto
- Magnitud
- Duración
- Reversibilidad
- Medidas de Gestión
- Costo de Medida de Gestión
- Total Costo Medidas de Gestión (calculado)

**Botones:**
- "GUARDAR"
- "SIGUIENTE"

**Acciones:**
- Al seleccionar "Sí" en "¿Existen impactos ambientales asociados al proyecto?": se despliega la tabla "Análisis Ambiental" (RN04).
- Al seleccionar "No": la tabla no se despliega (RN04).
- Al dar clic en "GUARDAR": ejecuta FA-01.
- Al dar clic en "SIGUIENTE": ejecuta FA-02.
- El sistema permite adicionar o eliminar filas de la tabla (RN03).
- El sistema permite añadir filas anidadas por cada tipo de medio seleccionado (RN07).
- El sistema calcula automáticamente el "Total Costo Medidas de Gestión" como la sumatoria de la columna "Costo de Medida de Gestión" (Anexo B.1).
- Al acercar el cursor a un campo: aparece ícono "?" con información de ayuda (RN06).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

Pregunta: "¿Existen impactos ambientales asociadas al proyecto?" → Opción seleccionada: **Sí** (el botón radial "Sí" aparece marcado; "No" no está marcado).

Tabla "ANÁLISIS AMBIENTAL":

| Medio | Descripción del Impacto | Tipo de Impacto | Magnitud | Duración | Reversibilidad | Medidas de Gestión | Costo de Medida de Gestión |
|-------|---------------------------|-------------------|-----------|-----------|-------------------|------------------------|-------------------------------|
| Físico-Agua (selector desplegable) | Erosión y Sedimentación (por movimiento de tierras) | Positivo | Moderada | Corto Plazo | Reversible | Control de escorrentías y revegetación de taludes. | $ 5,000.00 |
| Físico-Tierra (selector desplegable) | Generación de Residuos Sólidos | Positivo | Moderada | Corto Plazo | Reversible | Plan de Manejo de Residuos y disposición final autorizada. | $ 1,000.00 |
| Biológico-Flora y Fauna (selector desplegable) | Pérdida de Hábitat/Vegetación (en la franja del río) | Positivo | Leve | Largo Plazo | Reversible | Compensación con reforestación en zonas aledañas (especies nativas) | $ 8,000.00 |
| **TOTAL COSTO MEDIDAS DE GESTIÓN** | | | | | | | **$ 14,000.00** |

## Pantalla: Mensaje de Guardado (Anexo A.2)

**Descripción:** Mensaje emergente de confirmación mostrado tras el guardado exitoso de la información (paso 1.2 de FA-01).

**Campos:** No aplica (mensaje emergente).

**Botones:**
- "Aceptar"

**Acciones:**
- Al dar clic en "Aceptar": cierra el mensaje emergente y el sistema guarda la información, manteniéndose en la sección (paso 1.4 de FA-01).

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**

| Elemento | Contenido mostrado |
|----------|----------------------|
| Ícono | Ícono de verificación (check) en color verde |
| Título | ¡Guardado! |
| Mensaje | Sus datos han sido guardados exitosamente |
| Texto adicional | Recuerde que los costos derivados del análisis ambiental deben estar considerados dentro del presupuesto del proyecto |
| Botón | Aceptar |

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Éxito | Sus datos han sido guardados exitosamente. Recuerde que los costos derivados del análisis ambiental deben estar considerados dentro del presupuesto del proyecto. | Al dar clic en el botón "Guardar" (paso 1.2 de FA-01, Anexo A.2). |

---

# Observaciones

- El ejemplo de datos del mockup del Anexo A.1 muestra valores de "Medio" como "Físico-Agua", "Físico-Tierra" y "Biológico-Flora y Fauna". Este último valor combinado ("Biológico-Flora y Fauna") no corresponde exactamente a ninguna de las opciones individuales del Catálogo C.1 ("Biológico - Flora" y "Biológico - Fauna" están definidas como dos valores separados). El documento no aclara si el mockup refleja una selección combinada válida, un valor de catálogo adicional no documentado, o una simplificación ilustrativa del ejemplo.
- El ejemplo de datos del mockup del Anexo A.1 muestra "Tipo de Impacto: Positivo" para las tres filas, incluyendo casos que describen efectos que suelen entenderse como adversos en un análisis ambiental (por ejemplo, "Erosión y Sedimentación", "Generación de Residuos Sólidos", "Pérdida de Hábitat/Vegetación"). El documento no explica ni resuelve esta aparente inconsistencia entre la descripción del impacto y su clasificación como "Positivo"; se transcribe tal como aparece en el mockup, sin interpretación.
- RN07 indica que "El Sistema permitirá añadir filas anidadas por cada tipo de medio que se seleccione", pero ni el Flujo Básico, ni los Flujos Alternos, ni el Anexo A.1 (mockup) desarrollan explícitamente qué es una "fila anidada" ni cómo se visualiza esta funcionalidad en la pantalla. El mockup muestra únicamente filas simples (no anidadas).

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|--------------|-----------|
| Análisis Ambiental | Registro que asocia a un proyecto la existencia de impactos ambientales y, de existir, la matriz de gestión ambiental correspondiente. | Registro/edición por el Técnico URP (RN01); Guardado de la información (FA-01, RN05); Despliegue condicional de la tabla según respuesta Sí/No (RN04). |
| Impacto Ambiental | Cada fila de la matriz que describe un impacto sobre un medio específico, con su tipo, magnitud, duración y reversibilidad. | Adición/Eliminación de filas (RN03); Adición de filas anidadas por tipo de medio (RN07). |
| Medida de Gestión | Acción de gestión ambiental asociada a un impacto identificado, junto con su costo. | Registro manual (Anexo B.1); Cálculo automático del total de costos (Anexo B.1). |
| Proyecto | Proyecto de inversión pública al que se asocia el análisis ambiental. | Actualización de estado al completar el análisis ambiental y avanzar a CU-PRE-15 (Postcondiciones). |
| Unidad Ejecutora | Entidad cuya información puede visualizarse por otros actores según credenciales. | Consulta/visualización (RN01, RN02). |

---

# Catálogos Detectados

## Catálogo Medio (Anexo C.1)

| Medio |
|-------|
| Físico - Agua |
| Físico - Tierra |
| Físico - Aire |
| Biológico - Flora |
| Biológico - Fauna |
| Social - Estructura Social |
| Social - Patrimonio Cultural |

## Catálogo Impacto (Anexo C.2)

| Impacto |
|---------|
| Positivo |
| Negativo |

## Catálogo Magnitud (Anexo C.3)

| Magnitud |
|----------|
| Leve |
| Moderado |
| Fuerte |

## Catálogo Duración (Anexo C.4)

| Duración |
|----------|
| Corto Plazo |
| Mediano Plazo |
| Largo Plazo |

## Catálogo Reversibilidad (Anexo C.5)

| Reversibilidad |
|-----------------|
| Reversible |
| Irreversible |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Despliegue de la tabla "Análisis Ambiental" | Selección "Sí" en "¿Existen impactos ambientales asociados al proyecto?" | Pantalla "Análisis Ambiental" (RN04) |
| No despliegue de la tabla "Análisis Ambiental" | Selección "No" en "¿Existen impactos ambientales asociados al proyecto?" | Pantalla "Análisis Ambiental" (RN04) |
| Cálculo automático del Total Costo Medidas de Gestión | Registro/edición de "Costo de medida de gestión" en cada fila | Campo "Total costo medidas de gestión" (Anexo B.1) |
| Guardado de información | Botón "Guardar" | Mensaje emergente "¡Guardado!" (Anexo A.2, FA-01) |
| Navegación a siguiente sección | Botón "Siguiente" | CU-PRE-15 "Análisis de Riesgo" (FA-02) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. Este caso de uso no referencia explícitamente una fuente de datos externa a los propios catálogos internos (Anexos C.1 a C.5). |

---

# Datos Pendientes de Definir

- Prioridad del caso de uso: no especificada en el documento.
- Disparador (evento que inicia el caso de uso): no especificado explícitamente en el documento.
- Tabla de excepciones: no desarrollada en el documento.
- Tabla de errores/códigos de error: no desarrollada en el documento.
- Texto exacto del mensaje de validación asociado al resaltado en rojo de campos pendientes (RN05): no especificado.
- Funcionamiento y visualización concretos de las "filas anidadas por cada tipo de medio" mencionadas en RN07: no desarrollado en el Flujo Básico, Flujos Alternos ni en el mockup del Anexo A.1 (ver "Observaciones").
- Discrepancia no resuelta entre el valor de "Medio" mostrado en el mockup ("Biológico-Flora y Fauna", como valor combinado) y las opciones individuales del Catálogo C.1 ("Biológico - Flora" y "Biológico - Fauna" por separado) (ver "Observaciones").
- Inconsistencia no resuelta entre la clasificación "Positivo" del "Tipo de Impacto" mostrada en el mockup y la naturaleza de los impactos descritos (erosión, generación de residuos, pérdida de hábitat), que suelen asociarse a efectos adversos (ver "Observaciones").
