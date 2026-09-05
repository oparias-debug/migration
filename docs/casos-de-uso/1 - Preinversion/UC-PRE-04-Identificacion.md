---
id: CU-PRE-04
codigo: CU-PRE-04
nombre: Identificación
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-04_Identificación_JUL_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 7

actor_principal: Técnico URP

actores_secundarios:
  - Técnico PRE
  - Usuarios Internos y externos

prioridad: No especificado en el documento.

estado: Analizado

depende_de:
  - CU-PRE-01 Registro de Proyectos
  - CU-PRE-3.5 Selección y registro de etapas

casos_relacionados:
  - CU-PRE-01 Registro de Proyectos
  - CU-PRE-3.5 Selección y registro de etapas
  - CU-PRE-05 Alternativas de Solución

roles:
  - Técnico URP
  - Técnico PRE
  - Usuarios Internos y externos

pantallas:
  - Identificación del proyecto (Anexo A.1)
  - Información guardada (Anexo A.2)

procesos:
  - Registro de antecedentes, problema central, objetivo general y objetivos específicos
  - Carga de árbol de problemas y árbol de objetivos

servicios_externos: []

entidades:
  - Proyecto
  - Identificación del proyecto
  - Árbol de problemas (archivo)
  - Árbol de objetivos (archivo)
  - Objetivo específico

catalogos: []

palabras_clave:
  - identificación del proyecto
  - antecedentes
  - problema central
  - objetivo general
  - objetivos específicos
  - árbol de problemas
  - árbol de objetivos

ultima_actualizacion: JUL 2025

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    SF1:
      pagina: 3
    SF2:
      pagina: 3
  reglas_negocio:
    RNA1:
      pagina: 4
    RNA2:
      pagina: 4
    RNA3:
      pagina: 4
    RNB1:
      pagina: 4
    RNB2:
      pagina: 4
    RNC1:
      pagina: 5
    RNC2:
      pagina: 5
  anexos:
    A1:
      nombre: Identificación del proyecto
      pagina: 6
    A2:
      nombre: Información guardada
      pagina: 6
    B1:
      nombre: Formatos - Pantalla Identificación del proyecto
      pagina: 7
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Identificación |
| Código | CU-PRE-04 |
| Módulo | Preinversión |
| Fuente | CU-PRE-04_Identificación_JUL_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**

> No especificado en el documento. A diferencia de otros casos de uso de este sistema, este documento no incluye una sección explícita titulada "Campos requeridos" en la identificación del caso de uso.

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite al actor "Técnico URP" cargar la información del proyecto correspondiente a antecedentes, problema central (incluyendo esquema de árbol de problemas), objetivo general (incluyendo esquema de árbol de objetivos), y objetivos específicos.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico PRE (Técnico Preinversión): solo puede visualizar la información ingresada, sin permiso de editar; puede descargar archivos adjuntos.
- Usuarios Internos y externos: solo pueden visualizar la información ingresada, sin permiso de editar; pueden descargar archivos adjuntos.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Que el proyecto cuente con CUP (CU-PRE-01 "Registro de Proyectos").
2. Que el técnico URP haya calificado el proyecto y generado una Ruta de Preinversión (CU-PRE-3.5 "Selección y registro de etapas").

---

# Flujo Principal

1. Técnico URP ingresa a la pestaña "Identificación del proyecto".
2. Sistema muestra la pestaña Identificación del proyecto (Anexo A.1).
3. Técnico URP registra la información en los campos mostrados en la pestaña Identificación del proyecto y carga los archivos en los íconos "Agregar Árbol de problemas" y "Agregar Árbol de objetivos".
4. Técnico URP selecciona uno de los siguientes subflujos:
   - SF-1 Guardar
   - SF-2 Siguiente
   
   Caso de uso termina.

---

# Flujos Alternos

## SF-1 – Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón "Guardar".
2. Sistema muestra mensaje emergente descrito en A.2 - Información guardada y marca en color rojo los bordes de los campos que aún no cuenten con información registrada.
3. Técnico URP da clic en "Aceptar" al mensaje emergente.
4. Sistema guarda la información registrada en la pantalla Identificación del proyecto y se queda en la sección Identificación del proyecto.

Subflujo termina.

**Resultado**

> No especificado en el documento.

## SF-2 – Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón Siguiente.
2. Sistema avanza a la siguiente sección (Alternativas de solución CU-PRE-05) para continuar con el ingreso de información.

Subflujo termina.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|-------------|--------------|
| — | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento.

# Postcondiciones

1. Que el proyecto avanza a CU-PRE-05 "Alternativas de Solución".

---

# Reglas de Negocio

### RNA-1

**Descripción:** El Técnico URP es el único que puede ingresar y modificar información en la pantalla. Únicamente podrá ingresar y modificar la información de las Unidades Ejecutoras, según sus credenciales.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN A – Actores / Roles".

### RNA-2

**Descripción:** El Técnico preinversión (Técnico PRE) solo podrá acceder a ver la información ingresada, sin permiso de editar. Tendrá acceso a visualizar la pantalla desde que se guarden cambios por primera vez. Tiene permiso para descargar archivos adjuntos. Podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN A – Actores / Roles".

### RNA-3

**Descripción:** Los Usuarios Internos y externos solo podrán acceder a ver la información ingresada, sin permiso de editar. Tendrán acceso a visualizar la pantalla desde que se guarden cambios por primera vez. Tienen permiso para descargar archivos adjuntos. Podrán visualizar la información de las Unidades Ejecutoras en las pantallas, según sus credenciales.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN A – Actores / Roles".

### RNB-1

**Descripción:** Ícono "Agregar árbol de problemas":
- Estará habilitado sólo cuando esté permitida la edición de los campos.
- Permite cargar el archivo que contiene diagrama de árbol de problemas (formato PDF/A).
- Solo se podrá cargar un archivo; en caso de que el Técnico URP quiera volver a cargar un nuevo archivo, el sistema permitirá la carga del mismo y reemplazará el archivo anterior con el actual, antes de enviarlo a viabilidad.
- El archivo cargado aparecerá junto al ícono, con la opción de descargar.
- Debe haber un ícono para eliminar el archivo.
- Para efectos de correcciones o actualizaciones en el árbol de problemas, se podrá volver a cargar el archivo actualizado o corregido, siempre y cuando no haya pasado al estado "En proceso de viabilidad".

**Origen:** Documento, sección "4. Reglas de Negocio", "RN B – Condiciones de Pantalla".

### RNB-2

**Descripción:** Ícono "Agregar árbol de objetivos":
- Estará habilitado sólo cuando esté permitida la edición de los campos.
- Permite cargar archivo que contiene diagrama de árbol de objetivos (formato PDF/A).
- Solo se podrá cargar un archivo; en caso de que el Técnico URP quiera volver a cargar un nuevo archivo, el sistema permitirá la carga del mismo y reemplazará el archivo anterior con el actual, antes de enviarlo a viabilidad.
- Debe haber un ícono para eliminar el archivo.
- El archivo cargado aparecerá junto al ícono, con la opción de descargar.
- Para efectos de correcciones o actualizaciones en el árbol de objetivos, se podrá volver a cargar el archivo actualizado o corregido, siempre y cuando no haya pasado al estado "En proceso de viabilidad". El sistema debe permitir que se pueda reemplazar un archivo ya cargado por otro.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN B – Condiciones de Pantalla".

### RNC-1

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN C – Mensajes colaborativos o de ayuda".

### RNC-2

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN C – Mensajes colaborativos o de ayuda".

---

# Campos

## Pantalla "Identificación del proyecto"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Unidad Ejecutora | Trae la información de CU-PRE-01 "Registro de Proyectos", la Unidad Ejecutora según credenciales del Técnico URP que registró la información. Campo diligenciado automáticamente | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Nombre del proyecto | Trae la información de CU-PRE-01 "Registro de Proyectos". Muestra el nombre del proyecto. Campo diligenciado automáticamente | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| CUP | Muestra el código del proyecto asignado, CUP (CU-PRE-01 "Registro de Proyectos"). Campo diligenciado automáticamente | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable |
| Antecedentes | Campo para digitar los antecedentes que dieron origen al proyecto. Con límite de 3000 caracteres | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable |
| Problema central | Campo para digitar la problemática central a atender con el proyecto. Debe tener un botón para adjuntar el árbol del problema en PDF/A. Con límite de 500 caracteres | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable |
| Objetivo general | Campo editable para que el formulador pueda escribir el objetivo. Debe tener un botón para adjuntar el árbol de objetivos en PDF/A. Con límite de 500 caracteres | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable |
| Objetivos Específicos | Campo con opciones para insertar una fila por objetivo específico. Debe tener botones para adicionar o eliminar filas. Con límite de 500 caracteres por cada objetivo | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Antecedentes | Límite de 3000 caracteres | No especificado en el documento. |
| Problema central | Límite de 500 caracteres | No especificado en el documento. |
| Objetivo general | Límite de 500 caracteres | No especificado en el documento. |
| Objetivos Específicos | Límite de 500 caracteres por cada objetivo | No especificado en el documento. |
| Campos pendientes de completar (general) | Al dar clic en "Guardar" y haber campos pendientes de completar, se sombrean sus bordes en rojo (RNC-2) | No especificado en el documento; solo se describe el comportamiento visual, no un texto de mensaje |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| — | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Ingresar y modificar información en la pantalla "Identificación del proyecto" | RNA-1 |
| Técnico URP | Ingresar y modificar información únicamente de las Unidades Ejecutoras según sus credenciales | RNA-1 |
| Técnico PRE | Visualizar la información ingresada (sin editar), desde que se guarden cambios por primera vez | RNA-2 |
| Técnico PRE | Descargar archivos adjuntos | RNA-2 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras | RNA-2 |
| Usuarios Internos y externos | Visualizar la información ingresada (sin editar), desde que se guarden cambios por primera vez | RNA-3 |
| Usuarios Internos y externos | Descargar archivos adjuntos | RNA-3 |
| Usuarios Internos y externos | Visualizar la información de las Unidades Ejecutoras según sus credenciales | RNA-3 |

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01 "Registro de Proyectos"
- CU-PRE-3.5 "Selección y registro de etapas"
- CU-PRE-05 "Alternativas de Solución"

**Procesos relacionados:**
- Registro de antecedentes, problema central, objetivo general y objetivos específicos
- Carga de árbol de problemas y árbol de objetivos

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## A.1 – Identificación del proyecto

**Nombre:** Identificación del proyecto

**Descripción:** Pantalla donde el Técnico URP registra los antecedentes, problema central, objetivo general y objetivos específicos del proyecto, y carga los archivos de árbol de problemas y árbol de objetivos. Muestra en la parte superior campos no editables (Unidad Ejecutora, Nombre del proyecto, CUP).

**Campos:**
- Campos no editables: Unidad Ejecutora, Nombre del Proyecto, CUP
- Campos editables:
  - Antecedentes
  - Problema Central (con ícono para adjuntar árbol de problemas)
  - Objetivo General (con ícono para adjuntar árbol de objetivos)
  - Objetivos Específicos (lista de filas, con opción de agregar/eliminar; en el mockup se muestran filas marcadas con "X" como indicador de eliminación de fila)

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Campo | Valor de ejemplo |
|-------|-------------------|
| Unidad Ejecutora | MOPT |
| Nombre del Proyecto | Construcción de obras de mitigación en la comunidad Río Mar departamento de La Libertad |
| CUP | 8928 |
| Antecedentes | El Ministerio de Obras Públicas y de Transporte, a través de la Dirección de Planificación de la Obra Pública, en base a estudios previos, se llevó a cabo una visita técnica de inspección a las Comunidades Río Mar y Chilama Sur I y II, asentadas en los márgenes de la desembocadura del Río Chilama hacia aguas abajo del Puente Chilama [...] |
| Problema Central | La cuenca del Río Chilama presenta problemas de escorrentía rápida y concentración de flujos superficiales aguas abajo, generado por precipitaciones ocurridas en la parte alta de la cuenca, específicamente en los municipios de Santa Tecla, Comasagua y Zaragoza, por lo que el sistema hídrico no está regulado adecuadamente para controlar los caudales generados en la parte alta de la cuenca, haciendo vulnerables a las comunidades aguas abajo a riesgos de inundación por desbordamiento del río |
| Objetivo General | Dotar de obras de protección hidráulica a la cuenca baja del río Chilama, con el propósito de mitigar el riesgo de desbordamientos y reducir la vulnerabilidad de las comunidades de la zona de influencia |
| Objetivos Específicos (fila 1) | Definir las zonas de retiro y los alineamientos actuales de las comunidades |
| Objetivos Específicos (fila 2) | Mejorar el impacto ambiental y social de esta zona, reduciendo su vulnerabilidad |

**Botones:**
- REGRESAR (regresa a la pantalla "Ruta de Preinversión", según anotación del mockup)
- GUARDAR
- SIGUIENTE

**Acciones:**
- Regresar: navega a la pantalla "Ruta de Preinversión".
- Guardar: dispara el subflujo SF-1.
- Siguiente: dispara el subflujo SF-2, navega a CU-PRE-05 "Alternativas de Solución".
- Ícono junto a Problema Central: permite adjuntar el árbol de problemas (RNB-1).
- Ícono junto a Objetivo General: permite adjuntar el árbol de objetivos (RNB-2).

## A.2 – Información guardada

**Nombre:** Modal "¡Guardado!"

**Descripción:** Modal de confirmación que se despliega tras la acción de guardar en SF-1, paso 2.

**Campos:**
> No especificado en el documento.

**Botones:**
- Aceptar

**Acciones:**
- Aceptar: cierra el modal de confirmación (SF-1, paso 3).

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|-----------------|
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Al hacer clic en el botón Guardar en la pantalla "Identificación del proyecto" (Anexo A.2; SF-1, paso 2) |
| Ayuda contextual | Mensaje indicando qué información se debe completar en el campo (texto exacto no especificado) | Al dar clic en el ícono "?" que aparece al acercar el cursor a un campo (RNC-1) |

---

# Observaciones

- El mockup del Anexo A.1 rotula el primer botón como "REGRESAR" con la anotación "Regresa a la pantalla 'Ruta de Preinversión'"; sin embargo, este botón y su comportamiento no se describen en el texto del Flujo Básico ni en los Subflujos SF-1 o SF-2, solo son visibles en la imagen del mockup.
- El mockup del Anexo A.1 muestra en la sección "Objetivos Específicos" varias filas marcadas con una "X" en color rojo junto a cada objetivo; el texto no aclara si esta "X" corresponde al ícono para "eliminar fila" mencionado en la descripción del campo "Objetivos Específicos" del Anexo B.1, o si representa otro elemento visual.
- El texto del RN B (Condiciones de Pantalla) indica, tanto para el árbol de problemas como para el árbol de objetivos, que la posibilidad de volver a cargar el archivo aplica "siempre y cuando no haya pasado al estado 'En proceso de viabilidad'"; sin embargo, este caso de uso no describe en ninguna otra sección (Flujo Básico, Subflujos, Reglas de Negocio adicionales) cómo o cuándo el proyecto pasa a dicho estado, por lo que la condición depende de información externa a este documento.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|----------|-------------|-----------|
| Proyecto | Proyecto sobre el cual se registra la información de identificación | Consulta (datos automáticos de Unidad Ejecutora, Nombre del proyecto, CUP) |
| Identificación del proyecto | Conjunto de datos de antecedentes, problema central, objetivo general y objetivos específicos de un proyecto | Registro / Actualización (Flujo Básico, paso 3; SF-1) |
| Árbol de problemas (archivo) | Archivo PDF/A adjunto que contiene el diagrama de árbol de problemas | Carga / Reemplazo / Eliminación (RNB-1) |
| Árbol de objetivos (archivo) | Archivo PDF/A adjunto que contiene el diagrama de árbol de objetivos | Carga / Reemplazo / Eliminación (RNB-2) |
| Objetivo específico | Cada fila registrada en la sección "Objetivos Específicos" | Adición / Eliminación de filas (descripción del campo, Anexo B.1) |

---

# Catálogos Detectados

> No especificado en el documento.

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Visualización de la pestaña "Identificación del proyecto" | Clic del Técnico URP al ingresar a la pestaña (Flujo Básico, paso 1) | Pantalla Anexo A.1 |
| Guardado de la información registrada | Clic en botón "Guardar" y "Aceptar" en el mensaje emergente (SF-1) | Pantalla "Identificación del proyecto" (permanece en la misma sección) |
| Habilitación de visualización para Técnico PRE y Usuarios Internos/externos | Primer guardado de cambios en la pantalla | Técnico PRE, Usuarios Internos y externos (RNA-2, RNA-3) |
| Navegación a "Alternativas de Solución" | Clic en botón "Siguiente" (SF-2) | CU-PRE-05 "Alternativas de Solución" |
| Reemplazo del archivo de árbol de problemas u objetivos | Carga de un nuevo archivo en el ícono correspondiente, antes del estado "En proceso de viabilidad" | Campo "Problema central" / "Objetivo general" en Anexo A.1 (RNB-1, RNB-2) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Datos Pendientes de Definir

- No se especifica la prioridad del caso de uso.
- No se especifica un disparador (evento inicial) explícito del caso de uso.
- La sección "Excepciones" no está desarrollada en el documento (no hay códigos, descripciones ni consecuencias documentadas).
- La tabla de "Errores" no está desarrollada en el documento (no hay códigos de error, descripciones ni acciones esperadas).
- No se especifica el texto exacto del mensaje de ayuda contextual que muestra el ícono "?" (RNC-1); solo se indica que "indicará qué información se debe completar en dicho campo", sin el texto literal.
- El botón "REGRESAR" que aparece en el mockup del Anexo A.1 (con la anotación "Regresa a la pantalla 'Ruta de Preinversión'") no está descrito en el texto del Flujo Básico ni de los Subflujos, por lo que no se cuenta con su comportamiento funcional documentado más allá de la anotación visual del mockup.
- El documento no describe explícitamente cómo o en qué caso de uso el proyecto pasa al estado "En proceso de viabilidad", condición referida en RNB-1 y RNB-2 para limitar el reemplazo de los archivos de árbol de problemas y árbol de objetivos.
- No se detalla si existe un límite en la cantidad de filas de "Objetivos Específicos" que pueden agregarse.
