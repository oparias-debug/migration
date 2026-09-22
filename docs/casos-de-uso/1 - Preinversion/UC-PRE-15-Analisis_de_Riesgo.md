---
id: CU-PRE-15
codigo: CU-PRE-15
nombre: Análisis de Riesgo
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-15_Análisis_de_Riesgo_AGO_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 15

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5"]

casos_relacionados: ["CU-PRE-16"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Análisis de Riesgos (Anexo A.1)", "Mensaje de Guardado (Anexo A.2)"]

procesos: ["Despliegue condicional de la tabla de Análisis de Riesgos (RN04)", "Adición/eliminación de filas del plan de mitigación de riesgos (RN03)", "Cálculo automático de la Calificación del Riesgo según Probabilidad e Impacto (Anexo C.1)", "Cálculo automático del total de acciones de mitigación (Anexo B.1)", "Validación de obligatoriedad condicional de Acción de mitigación y Costo cuando la Calificación es Alto o Muy Alto (RN06)"]

servicios_externos: ["Mapa de Riesgo Departamental (enlace externo, SNET)", "VIGEA (enlace externo, MARN)"]

entidades: ["Análisis de Riesgo", "Riesgo", "Acción de Mitigación", "Proyecto", "Unidad Ejecutora"]

catalogos: ["Catálogo de Calificación (Anexo C.1)", "Matriz de Riesgos - imagen (Anexo C.2)", "Catálogo Probabilidad (Anexo C.3)", "Catálogo Impacto (Anexo C.4)"]

palabras_clave: ["Análisis de Riesgo", "Amenaza", "Probabilidad", "Impacto", "Calificación del Riesgo", "Mitigación", "Técnico URP", "Preinversión"]

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
      pagina: 5
    RN07:
      pagina: 5
    RN08:
      pagina: 5
  anexos:
    A1:
      nombre: Análisis de Riesgos (Pantalla)
      pagina: 5
    A2:
      nombre: Guardar (Mensaje emergente)
      pagina: 6
    B1:
      nombre: Formatos (tabla titulada "Pantalla 'Análisis Ambiental'" en el documento fuente)
      pagina: 6
    C1:
      nombre: Calificación (Probabilidad x Impacto)
      pagina: 8
    C2:
      nombre: Matriz de Riesgos (imagen)
      pagina: 8
    C3:
      nombre: Catálogo Probabilidad
      pagina: 9
    C4:
      nombre: Catálogo Impacto
      pagina: 9
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Análisis de Riesgo |
| Código | CU-PRE-15 |
| Módulo | Preinversión |
| Fuente | CU-PRE-15_Análisis_de_Riesgo_AGO_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Descripción del riesgo
- Probabilidad
- Impacto del riesgo
- Calificación del riesgo
- Acción de mitigación (cuando se requiere)
- Costo acción de mitigación
- Total acciones de mitigación

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

Este caso de uso permite al actor "Técnico URP" determinar si existen riesgos a desastres asociados al proyecto, y registrar un plan de mitigación de riesgos en los casos que aplique, describiendo las amenazas identificadas, el nivel de riesgo que representan y las acciones de mitigación propuestas ante la amenaza, detallando los costos de las mismas.

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

1. Contar con CUP de CU-PRE-01 "Registro de Proyectos".
2. Contar con la Ruta de Preinversión generada en CU-PRE-03.5 "Selección y registro de etapas".

---

# Flujo Principal

1. Técnico URP: Ingresa a la pestaña "Formulación del Proyecto" en la sección "Análisis de Riesgos".
2. Técnico URP: Completa la información de los campos del Anexo A.1.

---

# Flujos Alternos

## FA-01 Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Guardar".
2. Sistema: Muestra el mensaje emergente del Anexo A.2 "Sus datos han sido guardados con éxito. Recuerde que los costos derivados del análisis de riesgos deben estar considerados dentro del presupuesto del proyecto."
3. Técnico URP: Da clic en "Aceptar" a mensaje emergente.
4. Sistema: Guarda la información registrada y se mantiene en la sección.

**Resultado**

> No especificado en el documento.

## FA-02 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Siguiente".
2. Sistema: Avanza a la siguiente sección CU-PRE-16 "Análisis Legal" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|--------------|--------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento. El documento no incluye una tabla o listado explícito de excepciones para este caso de uso.

# Postcondiciones

1. El proyecto cuenta con el Análisis de Riesgos registrado y avanza a CU-PRE-16 "Análisis legal".

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pestaña, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** No especificado en el documento.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** No especificado en el documento.

## RN03

**Descripción:** El Técnico URP podrá adicionar o eliminar filas a la tabla de la pestaña "Análisis de Riesgos" (ver Anexo A.1).

**Origen:** No especificado en el documento.

## RN04

**Descripción:** En la pregunta "¿Se han identificado riesgos de desastres?" del Anexo A.1, si se da clic en el botón radial "Sí" el sistema desplegará la tabla "Análisis de Riesgos" para el registro de la información pertinente; si se da clic en "No" la lista no se desplegará.

**Origen:** No especificado en el documento.

## RN05

**Descripción:** El Sistema mostrará para la interpretación de los datos una imagen de la Matriz de Riesgos que indica el significado de los colores (ver imagen en Anexo C.2).

**Origen:** No especificado en el documento.

## RN06

**Descripción:** En el caso en que el resultado de la columna "Calificación del Riesgo" sea "Alto" o "Muy Alto" será obligatorio el registro en las columnas "Acción de Mitigación" y "Costo acción de mitigación". En el caso que el Técnico URP no complete la información de dichas columnas, al dar clic en el botón "Siguiente", el Sistema presentará el mensaje "Se requiere completar los campos obligatorios", marcará dichos campos y no permitirá que se avance a la siguiente pestaña.

**Origen:** No especificado en el documento.

## RN07

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** No especificado en el documento.

## RN08

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** No especificado en el documento.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Descripción del riesgo | Campo para registrar manualmente la descripción breve del riesgo identificado. | Texto | Texto | Sí | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Probabilidad | Campo para seleccionar el tipo de probabilidad, se mostrará un listado con las opciones siguientes: 1. Improbable; 2. Probable; 3. Muy probable; 4. Casi seguro. | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Impacto del riesgo | Campo para seleccionar el tipo de impacto, se mostrará un listado con las opciones siguientes: 1. Insignificante; 2. Bajo; 3. Moderado; 4. Alto; 5. Extremo. | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Calificación del Riesgo | Campo que mostrará de manera automática y con un color específico (ver Anexo C.1) la calificación del riesgo del proyecto. Su resultado dependerá de las selecciones realizadas en los campos "Probabilidad" e "Impacto" según el Anexo C.1 Calificación. | Color | Color | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado, referencia cruzada a la tabla completa en Anexo C.1 (ver sección "Catálogos Detectados" de este documento). |
| Acción de mitigación | Campo para registrar manualmente la acción de mitigación a implementar para cada riesgo. Campo obligatorio solo si la calificación está en "alto" o "muy alto" (ver RN06). | Texto | Texto | Condicional: obligatorio solo si Calificación del Riesgo = "Alto" o "Muy Alto" (RN06) | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Costo acción de mitigación | Campo para registrar el costo de la medida a implementar por cada riesgo en US$. El sistema deberá agregar el separador de miles (,). Campo obligatorio solo si la calificación está en "alto" o "muy alto" (ver RN06). | Moneda | Moneda | Condicional: obligatorio solo si Calificación del Riesgo = "Alto" o "Muy Alto" (RN06) | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Total acciones de mitigación | Campo que muestra la suma de los valores de cada acción de mitigación de la columna "Costo acción de mitigación". El Sistema deberá sumarlo de manera automática en US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado. |
| Mapa de riesgo departamental | Campo que vincula al MAPA DE RIESGO DEPARTAMENTAL: https://portafolio.snet.gob.sv/digitalizacion/pdf/spa/doc00073/doc00073.htm | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo de enlace/vínculo externo. |
| VIGEA | Campo que vincula al VIGEA: https://mapas.marn.gob.sv/VIGEA/entry.aspx | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo de enlace/vínculo externo. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|--------------------|
| Descripción del riesgo | Campo obligatorio (Anexo B.1). | No especificado en el documento. |
| Probabilidad | Campo obligatorio (Anexo B.1). | No especificado en el documento. |
| Impacto del riesgo | Campo obligatorio (Anexo B.1). | No especificado en el documento. |
| Acción de mitigación | Obligatorio solo si Calificación del Riesgo = "Alto" o "Muy Alto"; si no se completa y se da clic en "Siguiente", el sistema bloquea el avance (RN06). | "Se requiere completar los campos obligatorios" (RN06). |
| Costo acción de mitigación | Obligatorio solo si Calificación del Riesgo = "Alto" o "Muy Alto"; si no se completa y se da clic en "Siguiente", el sistema bloquea el avance (RN06). | "Se requiere completar los campos obligatorios" (RN06). |
| Campos pendientes de completar (al Guardar; no se especifica cuáles exactamente son obligatorios en ese contexto) | Al dar clic en "Guardar", si hay campos pendientes de completar, el sistema sombrea los bordes de dichos campos en color rojo (RN07). | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pestaña "Análisis de Riesgos". | RN01 |
| Técnico URP | Adicionar o eliminar filas de la tabla "Análisis de Riesgos". | RN03 |
| Otros actores (no nombrados individualmente en el documento) | Visualizar la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-16 "Análisis Legal"

**Procesos relacionados:** No especificado en el documento.

**Servicios externos:**
- Mapa de Riesgo Departamental (SNET): https://portafolio.snet.gob.sv/digitalizacion/pdf/spa/doc00073/doc00073.htm
- VIGEA (MARN): https://mapas.marn.gob.sv/VIGEA/entry.aspx

---

# Pantallas

## Pantalla: Análisis de Riesgos (Anexo A.1)

**Descripción:** Pantalla que permite al Técnico URP indicar si se han identificado riesgos de desastres asociados al proyecto y, en caso afirmativo, registrar una tabla con la descripción del riesgo, su probabilidad e impacto (que determinan automáticamente una calificación de color), las acciones de mitigación propuestas y sus costos. La pantalla incluye además una leyenda de colores de "Calificación del Riesgo" y accesos a recursos externos ("VIGEA" y "Mapa de Riesgo Departamental").

**Campos:**
- ¿Se han identificado riesgos de desastres (inminentes en el área de influencia del proyecto)? (Sí/No)
- Descripción del Riesgo
- Probabilidad de Ocurrencia
- Impacto del Riesgo
- Calificación del Riesgo (color, calculado)
- Acción de Mitigación
- Costo Acción de Mitigación
- Total Acciones de Mitigación (calculado)

**Botones:**
- "GUARDAR"
- "SIGUIENTE"
- "VIGEA"
- "MAPA DE RIESGO DEPARTAMENTAL"

**Acciones:**
- Al seleccionar "Sí" en la pregunta inicial: se despliega la tabla "Análisis de Riesgos" (RN04).
- Al seleccionar "No": la tabla no se despliega (RN04).
- Al seleccionar Probabilidad e Impacto en una fila: el sistema calcula y colorea automáticamente la "Calificación del Riesgo" según el Anexo C.1.
- El sistema permite adicionar o eliminar filas de la tabla (RN03).
- El sistema muestra una imagen de la Matriz de Riesgos para apoyar la interpretación de los colores (RN05, ver Anexo C.2).
- Si la Calificación del Riesgo es "Alto" o "Muy Alto" y no se completan "Acción de Mitigación" y "Costo Acción de Mitigación", el sistema bloquea el avance con "Siguiente" y muestra el mensaje "Se requiere completar los campos obligatorios" (RN06).
- El sistema calcula automáticamente el "Total Acciones de Mitigación" como la suma de la columna "Costo Acción de Mitigación" (Anexo B.1).
- Al dar clic en "GUARDAR": ejecuta FA-01.
- Al dar clic en "SIGUIENTE": ejecuta FA-02 (sujeto a la validación de RN06).
- Al dar clic en "VIGEA": dirige al enlace externo del VIGEA (Anexo B.1).
- Al dar clic en "MAPA DE RIESGO DEPARTAMENTAL": dirige al enlace externo del Mapa de Riesgo Departamental (Anexo B.1).
- Al acercar el cursor a un campo: aparece ícono "?" con información de ayuda (RN08).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

Pregunta: "¿Se han identificado riesgos de desastres inminentes en el área de influencia del proyecto?" → Opción seleccionada: **Sí** (el botón radial "Sí" aparece marcado; "No" no está marcado).

Tabla "ANÁLISIS DE RIESGOS":

| Descripción del Riesgo | Probabilidad de Ocurrencia | Impacto del Riesgo | Calificación del Riesgo (color) | Acción de Mitigación | Costo Acción de Mitigación |
|---------------------------|--------------------------------|--------------------------|-------------------------------------|---------------------------|--------------------------------|
| Desbordamiento del Río Chilama debido a la insuficiente capacidad del cauce y falta de muros de contención. | Casi seguro | Extremo | Rojo (Muy Alto) | Construcción de muro de gaviones/contención y dragado del cauce del río. | $ 300,000.00 |
| Inundación Pluvial por saturación del sistema de drenaje existente | Probable | Alto | Amarillo (Medio) | Ampliación y reemplazo del sistema de alcantarillado sanitario y pluvial. | $ 150,000.00 |
| Sismos de baja magnitud (Riesgo estructural manejable) | Improbable | Moderado | Verde (Bajo) | Asegurar diseño de infraestructura con normativa sismoresistente | $ - |
| **TOTAL ACCIONES DE MITIGACIÓN** | | | | | **$ 450,000.00** |

Leyenda "Calificación del Riesgo" (mostrada al margen izquierdo de la tabla, colores idénticos a los del Anexo C.1):

| Calificación del Riesgo | Color |
|---------------------------|-------|
| Bajo | Verde claro |
| Medio | Amarillo |
| Alto | Naranja |
| Muy Alto | Rojo |

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
| Texto adicional | Recuerde que los costos derivados del análisis de riesgos deben estar considerados dentro del presupuesto del proyecto |
| Botón | Aceptar |

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Éxito | Sus datos han sido guardados exitosamente. Recuerde que los costos derivados del análisis de riesgos deben estar considerados dentro del presupuesto del proyecto. | Al dar clic en el botón "Guardar" (paso 1.2 de FA-01, Anexo A.2). |
| Advertencia / Validación | Se requiere completar los campos obligatorios | Al dar clic en "Siguiente" cuando la Calificación del Riesgo es "Alto" o "Muy Alto" y no se han completado "Acción de Mitigación" y/o "Costo Acción de Mitigación" (RN06). |

---

# Observaciones

- La pregunta inicial se redacta de dos formas distintas dentro del mismo documento: RN04 la enuncia como "¿Se han identificado riesgos de desastres?", mientras que el mockup del Anexo A.1 la muestra como "¿Se han identificado riesgos de desastres inminentes en el área de influencia del proyecto?". El documento no aclara si se trata de la misma pregunta con una redacción abreviada en la regla, o de un cambio de alcance (por ejemplo, limitar la pregunta a "riesgos inminentes" y "en el área de influencia").
- La tabla de formatos del Anexo B.1 está titulada "Pantalla 'Análisis Ambiental'", nombre que no coincide con el tema del presente caso de uso ("Análisis de Riesgo"). El documento no aclara si se trata de un error de rotulación/copia del documento CU-PRE-14 "Análisis Ambiental".
- El Anexo C.2 "Matriz de Riesgos (imagen)" muestra una matriz de colores (columnas: 1. Improbable, 2. Probable, 3. Muy probable, 4. Casi seguro; filas: 5. Extremo, 4. Alto, 3. Moderado, 2. Bajo, 1. Insignificante) con tres celdas rotuladas como "Amenaza 1", "Amenaza 2" y "Amenaza 3" (ver descripción en "Catálogos Detectados"). El documento no explica qué representan estas tres "amenazas" de ejemplo ni si corresponden a un caso ilustrativo distinto del ejemplo de datos mostrado en la tabla del Anexo A.1.
- El ejemplo de datos del mockup del Anexo A.1 es consistente con la tabla de Calificación del Anexo C.1: "Casi seguro" + "Extremo" = "Muy alto" (rojo); "Probable" + "Alto" = "Medio" (amarillo); "Improbable" + "Moderado" = "Bajo" (verde). No se identifica discrepancia entre el catálogo y el ejemplo de pantalla en este caso.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|--------------|-----------|
| Análisis de Riesgo | Registro que asocia a un proyecto la existencia de riesgos de desastres y, de existir, el plan de mitigación de riesgos correspondiente. | Registro/edición por el Técnico URP (RN01); Guardado de la información (FA-01, RN07); Despliegue condicional de la tabla según respuesta Sí/No (RN04). |
| Riesgo | Cada fila de la tabla que describe un riesgo identificado, con su probabilidad, impacto y calificación resultante. | Adición/Eliminación de filas (RN03); Cálculo automático de la Calificación del Riesgo (Anexo C.1). |
| Acción de Mitigación | Medida propuesta para mitigar un riesgo identificado, junto con su costo asociado. | Registro manual, obligatorio condicionalmente según Calificación del Riesgo (RN06); Cálculo automático del total de costos (Anexo B.1). |
| Proyecto | Proyecto de inversión pública al que se asocia el análisis de riesgo. | Actualización de estado al completar el análisis de riesgo y avanzar a CU-PRE-16 (Postcondiciones). |
| Unidad Ejecutora | Entidad cuya información puede visualizarse por otros actores según credenciales. | Consulta/visualización (RN01, RN02). |

---

# Catálogos Detectados

## Calificación (Probabilidad x Impacto) (Anexo C.1)

| Probabilidad | Impacto | Calificación del riesgo |
|---------------|---------|---------------------------|
| Improbable | Extremo | Medio |
| Improbable | Alto | Bajo |
| Improbable | Moderado | Bajo |
| Improbable | Bajo | Bajo |
| Improbable | Insignificante | Bajo |
| Probable | Extremo | Alto |
| Probable | Alto | Medio |
| Probable | Moderado | Medio |
| Probable | Bajo | Bajo |
| Probable | Insignificante | Bajo |
| Muy probable | Extremo | Muy alto |
| Muy probable | Alto | Alto |
| Muy probable | Moderado | Alto |
| Muy probable | Bajo | Medio |
| Muy probable | Insignificante | Bajo |
| Casi seguro | Extremo | Muy alto |
| Casi seguro | Alto | Muy alto |
| Casi seguro | Moderado | Alto |
| Casi seguro | Bajo | Medio |
| Casi seguro | Insignificante | Bajo |

> Nota: en el documento fuente, cada fila de esta tabla se presenta con un color de fondo correspondiente a la "Calificación del riesgo": Medio = amarillo; Bajo = verde; Alto = naranja; Muy alto = rojo.

## Matriz de Riesgos (imagen) (Anexo C.2)

**Descripción de la imagen:** Matriz de doble entrada (Probabilidad en columnas, Impacto en filas) que representa visualmente, mediante celdas de color, la misma correspondencia definida en la tabla de Calificación del Anexo C.1.

- Columnas (Probabilidad): 1. Improbable, 2. Probable, 3. Muy probable, 4. Casi seguro.
- Filas (Impacto, de mayor a menor): 5. Extremo, 4. Alto, 3. Moderado, 2. Bajo, 1. Insignificante.
- Colores de celda: amarillo, naranja, rojo y verde, distribuidos según la correspondencia de Probabilidad/Impacto de la tabla del Anexo C.1.
- La imagen incluye además una leyenda de "Calificación del Riesgo" con los colores: Bajo (verde), Medio (amarillo), Alto (naranja), Muy Alto (rojo).
- Tres celdas de la matriz están rotuladas con texto de ejemplo: "Amenaza 1" (fila "2. Bajo", columna "1. Improbable", celda verde), "Amenaza 2" (fila "1. Insignificante", columna "1. Improbable", celda verde) y "Amenaza 3" (fila "4. Alto", columna "4. Casi seguro", celda de color naranja/rojo). El documento no explica el propósito de estas tres etiquetas de ejemplo (ver "Observaciones").

## Catálogo Probabilidad (Anexo C.3)

| Probabilidad |
|---------------|
| Improbable |
| Probable |
| Muy probable |
| Casi seguro |

## Catálogo Impacto (Anexo C.4)

| Impacto |
|---------|
| Insignificante |
| Bajo |
| Moderado |
| Alto |
| Extremo |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Despliegue de la tabla "Análisis de Riesgos" | Selección "Sí" en "¿Se han identificado riesgos de desastres?" | Pantalla "Análisis de Riesgos" (RN04) |
| No despliegue de la tabla "Análisis de Riesgos" | Selección "No" en "¿Se han identificado riesgos de desastres?" | Pantalla "Análisis de Riesgos" (RN04) |
| Cálculo automático de la Calificación del Riesgo | Selección de Probabilidad e Impacto en una fila | Campo "Calificación del Riesgo" (Anexo C.1) |
| Cálculo automático del Total Acciones de Mitigación | Registro/edición de "Costo acción de mitigación" en cada fila | Campo "Total acciones de mitigación" (Anexo B.1) |
| Bloqueo de avance por campos obligatorios incompletos | Clic en "Siguiente" con Calificación "Alto"/"Muy Alto" sin Acción de Mitigación o Costo completos | Mensaje "Se requiere completar los campos obligatorios" (RN06) |
| Guardado de información | Botón "Guardar" | Mensaje emergente "¡Guardado!" (Anexo A.2, FA-01) |
| Navegación a siguiente sección | Botón "Siguiente" | CU-PRE-16 "Análisis Legal" (FA-02, sujeto a RN06) |
| Navegación a recurso externo VIGEA | Botón "VIGEA" | https://mapas.marn.gob.sv/VIGEA/entry.aspx |
| Navegación a recurso externo Mapa de Riesgo Departamental | Botón "MAPA DE RIESGO DEPARTAMENTAL" | https://portafolio.snet.gob.sv/digitalizacion/pdf/spa/doc00073/doc00073.htm |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| Mapa de Riesgo Departamental (SNET) | Externo | Enlace de consulta a https://portafolio.snet.gob.sv/digitalizacion/pdf/spa/doc00073/doc00073.htm, accesible desde el campo/botón "Mapa de riesgo departamental" (Anexo B.1). |
| VIGEA (MARN) | Externo | Enlace de consulta a https://mapas.marn.gob.sv/VIGEA/entry.aspx, accesible desde el campo/botón "VIGEA" (Anexo B.1). |

---

# Datos Pendientes de Definir

- Prioridad del caso de uso: no especificada en el documento.
- Disparador (evento que inicia el caso de uso): no especificado explícitamente en el documento.
- Tabla de excepciones: no desarrollada en el documento.
- Tabla de errores/códigos de error: no desarrollada en el documento.
- Texto exacto del mensaje de validación asociado al resaltado en rojo de campos pendientes al hacer clic en "Guardar" (RN07): no especificado (distinto del mensaje sí especificado para el botón "Siguiente" en RN06).
- Discrepancia no resuelta en la redacción de la pregunta inicial entre RN04 ("¿Se han identificado riesgos de desastres?") y el mockup del Anexo A.1 ("¿Se han identificado riesgos de desastres inminentes en el área de influencia del proyecto?") (ver "Observaciones").
- Propósito y significado de las etiquetas de ejemplo "Amenaza 1", "Amenaza 2" y "Amenaza 3" mostradas en la imagen de la Matriz de Riesgos del Anexo C.2: no explicado en el documento (ver "Observaciones").