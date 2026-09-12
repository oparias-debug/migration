---
id: CU-PRE-07
codigo: CU-PRE-07
nombre: Población Objetivo
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-07_Población_Objetivo_JUL_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 10

actor_principal: Técnico URP

actores_secundarios:
  - Técnico PRE

prioridad: No especificado en el documento.

estado: Analizado

depende_de:
  - CU-PRE-01 Registro de Proyectos
  - CU-PRE-03.5 Selección y registro de etapas

casos_relacionados:
  - CU-PRE-01 Registro de Proyectos
  - CU-PRE-03.5 Selección y registro de etapas
  - CU-PRE-08 Área de influencia
  - CU-PRO-19 Programación de la Distribución Financiera por Ubicación Geográfica
  - CU-EJE-03 Avance Mensual Financiero por Ubicación Geográfica del PAIP

roles:
  - Técnico URP
  - Técnico PRE

pantallas:
  - Análisis de la Población (Anexo A.1)
  - Guardar (Anexo A.2)
  - Ingreso Inválido 1 (Anexo A.3)
  - Ingreso Inválido 2 (Anexo A.4)

procesos:
  - Registro y cuantificación de población de referencia, afectada, objetivo y en espera
  - Cálculo automático de totales y porcentajes de población
  - Validación cruzada entre población afectada, objetivo y de referencia

servicios_externos:
  - Geoportal BCR - Censo de Población y Vivienda 2024 (https://geoportal.bcr.gob.sv/pages/teg-poblaci%C3%B3n)

entidades:
  - Proyecto
  - Población de Referencia
  - Población Afectada
  - Población Objetivo
  - Población en Espera

catalogos:
  - Anexo C.1 – Catálogo de ubicaciones geográficas

palabras_clave:
  - población objetivo
  - población afectada
  - población de referencia
  - población en espera
  - censo
  - análisis de la población

ultima_actualizacion: JUL 2025

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
    FA03:
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
  anexos:
    A1:
      nombre: Análisis de la Población
      pagina: 6
    A2:
      nombre: Guardar
      pagina: 6
    A3:
      nombre: Ingreso Inválido 1
      pagina: 6
    A4:
      nombre: Ingreso Inválido 2
      pagina: 7
    B1:
      nombre: Formatos - Pantalla Análisis de la Población
      pagina: 7
    C1:
      nombre: Catálogo de ubicaciones geográficas
      pagina: 9
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Población Objetivo |
| Código | CU-PRE-07 |
| Módulo | Preinversión |
| Fuente | CU-PRE-07_Población_Objetivo_JUL_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Población de referencia técnico
- Población afectada
- Población objetivo
- Población en espera
- Enlace a Censo vigente

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite al actor "Técnico URP" registrar en el sistema la identificación y cuantificación de la población objetivo a atender con el proyecto.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico PRE (mencionado en RN02, con permiso de visualización de la información de todas las Unidades Ejecutoras; también mencionado en el Anexo B.1 como validador de la ubicación de la Población Afectada y de la Población Objetivo).

> Nota: RN01 menciona de forma genérica "Todos los demás actores" con permiso de solo visualización según credenciales, sin identificarlos individualmente más allá del Técnico PRE (RN02); ver "Datos Pendientes de Definir".

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP de CU-PRE-01 "Registro de Proyectos".
2. Contar con la Ruta de Preinversión generada de CU-PRE-03.5 "Selección y registro de etapas".

---

# Flujo Principal

1. Técnico URP ingresa a la pestaña "Formulación del Proyecto" en la sección "Diagnóstico de la Situación Actual – Análisis de la población".
2. Técnico URP completa la información de los campos del Anexo A.1.

---

# Flujos Alternos

## FA-01 – Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1.1 Técnico URP da clic en el botón "Guardar".
1.2 Sistema muestra mensaje emergente del Anexo A.2.
1.3 Técnico URP da clic en "Aceptar" al mensaje emergente.
1.4 Sistema guarda la información registrada y se mantiene en la pestaña.

**Resultado**

> No especificado en el documento.

## FA-02 – Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

2.1 Técnico URP da clic en el botón "Siguiente".
2.2 Sistema avanza a la siguiente sección CU-PRE-08 "Área de influencia" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

## FA-03 – Ingreso Inválido

**Condición**

> No especificado en el documento.

**Flujo**

3.1 Técnico URP da clic en el botón "Guardar".
3.2 Sistema muestra mensaje emergente del Anexo A.3 o Anexo A.4.
3.3 Técnico URP da clic en "Aceptar" al mensaje emergente.
3.4 Sistema no guarda la información registrada y regresa a la pestaña anterior.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|-------------|--------------|
| — | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento.

# Postcondiciones

1. El proyecto cuenta con la población objetivo registrada.
2. El proyecto avanza a CU-PRE-08 "Área de influencia".
3. CU-PRO-19 "Programación de la Distribución Financiera por Ubicación Geográfica".
4. CU-EJE-03 "Avance Mensual Financiero por Ubicación Geográfica del PAIP".

> Nota: el documento lista las postcondiciones 3 y 4 (CU-PRO-19 y CU-EJE-03) únicamente como referencias a otros casos de uso, sin describir de forma explícita la relación funcional entre este caso de uso y esos dos, más allá de su mención conjunta en la sección "Postcondiciones".

---

# Reglas de Negocio

### RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** Documento, sección "Reglas del Negocio".

### RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** Documento, sección "Reglas del Negocio".

### RN03

**Descripción:** Al dar clic en el botón "Censo" este mostrará la página del Banco Central de Reserva (BCR) en donde se encuentran los resultados del "Censo de Población y Vivienda 2024". Este botón será visible para todos los actores. Enlace: https://geoportal.bcr.gob.sv/pages/teg-poblaci%C3%B3n

**Origen:** Documento, sección "Reglas del Negocio".

### RN04

**Descripción:** La celda ubicada en la columna de porcentaje (%) de la fila de "Población de Referencia" permanecerá bloqueada y vacía.

**Origen:** Documento, sección "Reglas del Negocio".

### RN05

**Descripción:** La celda ubicada en la columna "Ubicación" de la fila de "Población en espera" permanecerá bloqueada y vacía.

**Origen:** Documento, sección "Reglas del Negocio".

### RN06

**Descripción:** Recomendación: la población afectada y la población objetivo deben tener un campo abierto en el cual el Técnico URP pueda describir las características principales de la población objetivo. La columna de "Descripción" estará habilitada únicamente para las filas de "Población afectada" y "Población objetivo", en donde el Técnico URP podrá agregar las características principales de dichas poblaciones. Para las filas de "Población de referencia" y "Población en espera" las celdas ubicadas en la columna "Descripción" permanecerán bloqueadas y vacías.

**Origen:** Documento, sección "Reglas del Negocio".

### RN07

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** Documento, sección "Reglas del Negocio".

### RN08

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Documento, sección "Reglas del Negocio".

### RN09

**Descripción:** El Técnico URP podrá agregar en la tabla, columnas agrupadas de "Ubicación" y "N° de Personas" dependiendo su necesidad. Dependiendo del número de ubicaciones agregadas, los datos de las celdas en las columnas de "N° de Personas" se totalizan en la columna "Total N° de Personas".

**Origen:** Documento, sección "Reglas del Negocio".

---

# Campos

## Pantalla "Análisis de la Población"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Población | Campo que aparece por defecto, mostrando el tipo de población a identificar y cuantificar (Población de Referencia, Población Afectada, Población Objetivo y Población en Espera) | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Descripción | Campo para registrar la descripción de la población objetivo del proyecto | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable. Según RN06, habilitado únicamente para las filas "Población afectada" y "Población objetivo"; bloqueado y vacío para "Población de referencia" y "Población en espera" |
| Ubicación (Población de Referencia) | Permitirá seleccionar de una lista desplegable la ubicación de la población de referencia. La ubicación a seleccionar podrá ser "a nivel nacional", "por departamento" o "por distrito" de acuerdo con el catálogo "Ubicación geográfica" Anexo C.1. La celda permitirá digitar una palabra clave y el Sistema la autocompletará según catálogo. Se permitirá seleccionar más de una ubicación, para lo cual la matriz permitirá agregar tantas filas como ubicaciones se requieran | Selección | Selección | No especificado en el documento. | No especificado en el documento. | No editable (según tabla de formatos); permite agregar múltiples filas de ubicación (RN09) |
| Ubicación (Población Afectada) | Permitirá al Técnico URP registrar manualmente la ubicación de la población afectada. La ubicación seleccionada deberá estar dentro del área geográfica de la población de referencia (campo anterior), lo cual será validado por el Técnico PRE | Texto | Texto | Sí | No especificado en el documento. | No editable (según tabla de formatos; ver observación sobre esta aparente contradicción) |
| Ubicación (Población Objetivo) | Permitirá al Técnico URP registrar manualmente la ubicación de la población objetivo. La ubicación seleccionada deberá estar dentro del área geográfica de la población de referencia (campo anterior), lo cual será validado por el Técnico PRE | Texto | Texto | Sí | No especificado en el documento. | No editable (según tabla de formatos; ver observación sobre esta aparente contradicción) |
| Ubicación (Población en Espera) | Este campo se mostrará sombreado en gris, y permanecerá bloqueado y vacío | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable (RN05) |
| N° de personas (Población de Referencia) | Permitirá registrar manualmente la cantidad de población de referencia en cada una de las ubicaciones ingresadas | Numérico | Numérico | Sí | No especificado en el documento. | Editable |
| N° de personas (Población Afectada) | Permitirá registrar manualmente la cantidad de población afectada del proyecto en la ubicación respectiva. El sistema debe validar que el dato colocado en este campo no sea mayor al del N° de personas registradas en cada una de las ubicaciones de la población de referencia; de lo contrario, muestra el mensaje "la cantidad de población afectada no puede ser mayor que la registrada en la población de referencia". Debe haber un campo que muestre la cantidad total de población afectada | Numérico | Numérico | Sí | No especificado en el documento. | Editable |
| N° de personas (Población Objetivo) | Permitirá registrar manualmente la cantidad de población objetivo del proyecto. El sistema debe validar que el dato colocado en cada una de las localizaciones de la población objetivo sea menor o igual a lo registrado en cada una de las localizaciones de la población afectada; de lo contrario, muestra el mensaje "la cantidad de población objetivo no puede ser mayor que la registrada en la población afectada". Debe haber un campo que muestre la cantidad total de población objetivo | Numérico | Numérico | Sí | No especificado en el documento. | Editable |
| N° de personas (Población en Espera) | Campo que calcula automáticamente la población en espera. El Sistema deberá calcularlo en cada una de las ubicaciones según la operación: Población en Espera = Población Afectada – Población Objetivo. Debe haber un campo que muestre la cantidad total de población en espera | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable (cálculo automático) |
| % (Población Afectada) | Será igual a 100% tanto para cada ubicación como para la ubicación total de la tabla | Porcentaje | Porcentaje | No especificado en el documento. | 100% | No editable |
| % (Población Objetivo) | Campo que mostrará el porcentaje de población objetivo. El Sistema deberá calcularlo para cada una de las ubicaciones según la operación: % Población objetivo = (Población Objetivo / Población Afectada) * 100. Debe haber un campo que muestre el porcentaje total de población objetivo | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | No editable (cálculo automático) |
| % (Población en Espera) | Campo que mostrará el porcentaje de población en espera. El Sistema deberá calcularlo para cada una de las ubicaciones según la operación: % Población en espera = % Población Afectada - % Población Objetivo. Debe haber un campo que muestre el porcentaje total de población en espera | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | No editable (cálculo automático) |
| Total N° de personas (Población de Referencia) | Campo que calcula automáticamente el total de la población de referencia. El Sistema deberá calcularlo por cada una de las ubicaciones según la operación: Total Población de Referencia = N° Personas población de referencia (Ubicación 1) + N° Personas población de referencia (Ubicación 2) + N° Personas población de referencia (Ubicación n) | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable (cálculo automático) |
| Total N° de personas (Población Afectada) | Campo que calcula automáticamente el total de la población afectada. El Sistema deberá calcularlo por cada una de las ubicaciones según la operación: Total Población afectada = N° Personas población afectada (Ubicación 1) + N° Personas población afectada (Ubicación 2) + N° Personas población afectada (Ubicación n) | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable (cálculo automático) |
| Total N° de personas (Población Objetivo) | Campo que calcula automáticamente el total de la población objetivo. El Sistema deberá calcularlo por cada una de las ubicaciones según la operación: Total Población Objetivo = N° Personas población objetivo (Ubicación 1) + N° Personas población objetivo (Ubicación 2) + N° Personas población objetivo (Ubicación n) | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable (cálculo automático) |
| Total N° de personas (Población en Espera) | Campo que calcula automáticamente el total de la población en espera. El Sistema deberá calcularlo por cada una de las ubicaciones según la operación: Total Población en Espera = N° Personas población en espera (Ubicación 1) + N° Personas población en espera (Ubicación 2) + N° Personas población en espera (Ubicación n) | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable (cálculo automático) |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| N° de personas (Población Afectada) | El dato no puede ser mayor al N° de personas registradas en cada una de las ubicaciones de la población de referencia | "La cantidad de población afectada no puede ser mayor que la registrada en la población de referencia." (Anexo A.3) |
| N° de personas (Población Objetivo) | El dato colocado en cada una de las localizaciones de la población objetivo debe ser menor o igual a lo registrado en cada una de las localizaciones de la población afectada | "El número de personas de la Población Objetivo no puede superar el número de personas de la Población Afectada." (Anexo A.4) |
| Ubicación (Población Afectada) | Campo obligatorio; la ubicación registrada deberá estar dentro del área geográfica de la población de referencia, lo cual será validado por el Técnico PRE | No especificado en el documento. |
| Ubicación (Población Objetivo) | Campo obligatorio; la ubicación registrada deberá estar dentro del área geográfica de la población de referencia, lo cual será validado por el Técnico PRE | No especificado en el documento. |
| N° de personas (Población de Referencia) | Campo obligatorio | No especificado en el documento. |
| N° de personas (Población Afectada) | Campo obligatorio | No especificado en el documento. |
| N° de personas (Población Objetivo) | Campo obligatorio | No especificado en el documento. |
| Campos pendientes de completar (general) | Al dar clic en "Guardar" y haber campos pendientes de completar, se sombrean sus bordes en rojo (RN07) | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| — | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pantalla, según credenciales | RN01 |
| Técnico URP | Agregar columnas agrupadas de "Ubicación" y "N° de Personas" | RN09 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras | RN02 |
| Técnico PRE | Validar que la ubicación registrada de la Población Afectada y de la Población Objetivo esté dentro del área geográfica de la Población de Referencia | Anexo B.1, descripción de los campos "Ubicación (Población Afectada)" y "Ubicación (Población Objetivo)" |
| Todos los actores | Acceder al botón "Censo" y visualizar la página del BCR con el "Censo de Población y Vivienda 2024" | RN03 |
| Todos los demás actores (no identificados individualmente) | Visualizar únicamente la información de las Unidades Ejecutoras, según credenciales | RN01 |

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01 "Registro de Proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-08 "Área de influencia"
- CU-PRO-19 "Programación de la Distribución Financiera por Ubicación Geográfica"
- CU-EJE-03 "Avance Mensual Financiero por Ubicación Geográfica del PAIP"

**Procesos relacionados:**
- Registro y cuantificación de población de referencia, afectada, objetivo y en espera
- Cálculo automático de totales y porcentajes de población
- Validación cruzada entre población afectada, objetivo y de referencia

**Servicios externos:**
- Geoportal del Banco Central de Reserva (BCR) - Censo de Población y Vivienda 2024 (RN03): https://geoportal.bcr.gob.sv/pages/teg-poblaci%C3%B3n

---

# Pantallas

## A.1 – Análisis de la Población

**Nombre:** Análisis de la Población

**Descripción:** Tabla donde el Técnico URP identifica y cuantifica la Población de Referencia, Población Afectada, Población Objetivo y Población en Espera, con posibilidad de agregar múltiples ubicaciones y con cálculos automáticos de totales y porcentajes.

**Campos:**
- Enlace "Adicionar Ubicación" (botón "+")
- Tabla: Población, Descripción de la Población, Ubicación 1 (Seleccione departamento / Seleccione distrito), N° Personas (Ubicación 1), Ubicación 2 (Seleccione departamento / Seleccione distrito), N° Personas (Ubicación 2), Total N° de Personas, %

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Población | Descripción de la Población | Ubicación 1 | N° Personas (Ubic. 1) | Ubicación 2 | N° Personas (Ubic. 2) | Total N° de Personas | % |
|---|---|---|---|---|---|---|---|
| Población de Referencia | (vacío) | Seleccione departamento / Seleccione distrito | 312,000 | Seleccione departamento / Seleccione distrito | 433,464 | 745,464 | — |
| Población Afectada | XYZ... | Distrito La Libertad | 25,300 | Distrito Puerto de La Libertad | 17,824 | 43,124 | 100% |
| Población Objetivo | XYZ... | Comunidad Río Mar | 8,450 | Comunidad El Pimental | 6,643 | 15,093 | 35% |
| Población en Espera | (vacío, bloqueado) | (vacío, bloqueado) | 14,200 | (vacío, bloqueado) | 13,831 | 28,031 | 65% |

**Botones:**
- Adicionar Ubicación (+)
- GUARDAR
- SIGUIENTE

**Acciones:**
- Adicionar Ubicación: agrega una nueva columna agrupada de "Ubicación" y "N° de Personas" (RN09).
- Guardar: dispara FA-01 (si la información es válida) o FA-03 (si hay ingreso inválido).
- Siguiente: dispara FA-02, navega a CU-PRE-08 "Área de influencia".
- Botón "Censo" (mencionado en RN03, no visible explícitamente en el mockup transcrito): muestra la página del Geoportal BCR con el Censo de Población y Vivienda 2024.

## A.2 – Guardar

**Nombre:** Modal "¡Guardado!"

**Descripción:** Modal de confirmación que se despliega tras la acción de guardar exitosamente en FA-01, paso 1.2.

**Campos:**
> No especificado en el documento.

**Botones:**
- Aceptar

**Acciones:**
- Aceptar: cierra el modal de confirmación (FA-01, paso 1.3).

## A.3 – Ingreso Inválido 1

**Nombre:** Modal "Ingreso inválido" (Población Afectada vs. Población de Referencia)

**Descripción:** Modal de advertencia que se despliega cuando el número de personas de la Población Afectada supera el número de personas de la Población de Referencia.

**Campos:**
> No especificado en el documento.

**Mensaje mostrado:** "El número de personas de la Población Afectada no puede superar el número de personas de la Población de Referencia."

**Botones:**
- Aceptar

**Acciones:**
- Aceptar: cierra el modal; dispara el paso 3.4 de FA-03 (no se guarda la información y regresa a la pestaña anterior).

## A.4 – Ingreso Inválido 2

**Nombre:** Modal "Ingreso inválido" (Población Objetivo vs. Población Afectada)

**Descripción:** Modal de advertencia que se despliega cuando el número de personas de la Población Objetivo supera el número de personas de la Población Afectada.

**Campos:**
> No especificado en el documento.

**Mensaje mostrado:** "El número de personas de la Población Objetivo no puede superar el número de personas de la Población Afectada."

**Botones:**
- Aceptar

**Acciones:**
- Aceptar: cierra el modal; dispara el paso 3.4 de FA-03 (no se guarda la información y regresa a la pestaña anterior).

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|-----------------|
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Al hacer clic en el botón Guardar y la información es válida (Anexo A.2; FA-01, paso 1.2) |
| Error / Validación | "El número de personas de la Población Afectada no puede superar el número de personas de la Población de Referencia." | Al hacer clic en el botón Guardar cuando el N° de personas de Población Afectada supera al de Población de Referencia (Anexo A.3; FA-03) |
| Error / Validación | "El número de personas de la Población Objetivo no puede superar el número de personas de la Población de Afectada." (texto tal como aparece en el mockup del Anexo A.4) | Al hacer clic en el botón Guardar cuando el N° de personas de Población Objetivo supera al de Población Afectada (Anexo A.4; FA-03) |
| Error / Validación (texto alternativo, Anexo B.1) | "La cantidad de población afectada no puede ser mayor que la registrada en la población de referencia." | Descrito en la fila "N° de personas (Población Afectada)" del Anexo B.1, como texto alternativo al del Anexo A.3 (ver Observaciones) |
| Error / Validación (texto alternativo, Anexo B.1) | "La cantidad de población objetivo no puede ser mayor que la registrada en la población afectada." | Descrito en la fila "N° de personas (Población Objetivo)" del Anexo B.1, como texto alternativo al del Anexo A.4 (ver Observaciones) |
| Ayuda contextual | Mensaje indicando qué información se debe completar en el campo (texto exacto no especificado) | Al dar clic en el ícono "?" que aparece al acercar el cursor a un campo (RN08) |

---

# Observaciones

- Existe una discrepancia entre el texto del mensaje de error mostrado en el mockup del Anexo A.3 ("El número de personas de la Población Afectada no puede superar el número de personas de la Población de Referencia") y el texto descrito en la tabla de formatos del Anexo B.1 para el mismo caso ("la cantidad de población afectada no puede ser mayor que la registrada en la población de referencia"). Ambos expresan la misma regla de validación, pero con redacción distinta; el documento no aclara cuál es el texto definitivo.
- Existe la misma discrepancia entre el texto del mensaje de error mostrado en el mockup del Anexo A.4 ("El número de personas de la Población Objetivo no puede superar el número de personas de la Población de Afectada") y el texto descrito en la tabla de formatos del Anexo B.1 ("la cantidad de población objetivo no puede ser mayor que la registrada en la población afectada"). El documento no aclara cuál es el texto definitivo.
- El mockup del Anexo A.4 usa la expresión "Población de Afectada" (con el artículo "de" antes de "Afectada"), lo cual difiere de la nomenclatura usada en el resto del documento ("Población Afectada", sin "de"). No se aclara si es un error tipográfico del mockup o una variante intencional del nombre.
- Los campos "Ubicación (Población Afectada)" y "Ubicación (Población Objetivo)" se describen en su detalle funcional como campos que "el Técnico URP" debe "registrar manualmente" y se marcan como "Obligatorio", pero la columna "Editable" de la tabla de formatos del Anexo B.1 los marca como "No". El documento no aclara esta aparente contradicción entre la condición de edición y la descripción funcional del campo.
- El campo "Ubicación (Población de Referencia)" se describe como un campo de selección desde una lista desplegable con autocompletado, gestionado por el Técnico URP, pero también se marca como "No" editable en la tabla de formatos del Anexo B.1. El documento no aclara esta aparente contradicción.
- RN06 comienza con la palabra "Recomendación", lo que sugiere un carácter sugerido más que obligatorio; sin embargo, el resto de la misma regla describe el comportamiento de la columna "Descripción" en términos prescriptivos ("estará habilitada únicamente...", "permanecerán bloqueadas y vacías"). El documento no aclara si el carácter de "recomendación" aplica a toda la regla o solo a su primera oración.
- El botón "Censo" mencionado en RN03 no aparece representado explícitamente en el mockup transcrito del Anexo A.1 dentro del documento; solo se describe textualmente en la regla de negocio.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|----------|-------------|-----------|
| Proyecto | Proyecto sobre el cual se registra la población objetivo | Consulta (visualización según RN01, RN02) |
| Población de Referencia | Población total de referencia del área geográfica del proyecto, con su ubicación y cantidad de personas | Registro (N° de personas, Ubicación); Cálculo automático de totales (RN09); columna % bloqueada (RN04) |
| Población Afectada | Población afectada por la problemática que el proyecto busca resolver, con ubicación, descripción y cantidad de personas | Registro (Ubicación, N° de personas, Descripción); Validación cruzada contra Población de Referencia (Anexo B.1); Cálculo automático de porcentaje (100%) y totales |
| Población Objetivo | Población que el proyecto busca atender directamente, con ubicación, descripción y cantidad de personas | Registro (Ubicación, N° de personas, Descripción); Validación cruzada contra Población Afectada (Anexo B.1); Cálculo automático de porcentaje y totales |
| Población en Espera | Población calculada como la diferencia entre Población Afectada y Población Objetivo | Cálculo automático (N° de personas, porcentaje, totales); columnas Ubicación y Descripción bloqueadas (RN05, RN06) |

---

# Catálogos Detectados

## C.1 – Catálogo de ubicaciones geográficas

| Distrito | Departamento | Región |
|----------|--------------|--------|
| Ahuachapán | Ahuachapán | Occidental |
| Apaneca | Ahuachapán | Occidental |
| Concepción de Ataco | Ahuachapán | Occidental |
| Tacuba | Ahuachapán | Occidental |
| Atiquizaya | Ahuachapán | Occidental |
| El Refugio | Ahuachapán | Occidental |
| San Lorenzo | Ahuachapán | Occidental |
| Turín | Ahuachapán | Occidental |
| Guaymango | Ahuachapán | Occidental |
| Jujutla | Ahuachapán | Occidental |
| San Francisco Menéndez | Ahuachapán | Occidental |
| San Pedro Puxtla | Ahuachapán | Occidental |
| Ahuachapán - Nivel departamental | Ahuachapán | Occidental |
| Santa Ana | Santa Ana | Occidental |
| Coatepeque | Santa Ana | Occidental |
| El Congo | Santa Ana | Occidental |
| Masahuat | Santa Ana | Occidental |
| Metapán | Santa Ana | Occidental |
| Santa Rosa Guachipilín | Santa Ana | Occidental |
| Texistepeque | Santa Ana | Occidental |
| Candelaria de la Frontera | Santa Ana | Occidental |
| Chalchuapa | Santa Ana | Occidental |
| El Porvenir | Santa Ana | Occidental |
| San Antonio Pajonal | Santa Ana | Occidental |
| San Sebastián Salitrillo | Santa Ana | Occidental |
| Santiago de la Frontera | Santa Ana | Occidental |
| Nahulingo | Sonsonate | Occidental |
| San Antonio del Monte | Sonsonate | Occidental |
| Santo Domingo de Guzmán | Sonsonate | Occidental |
| Sonsonate | Sonsonate | Occidental |
| Sonzacate | Sonsonate | Occidental |
| Armenia | Sonsonate | Occidental |
| Caluco | Sonsonate | Occidental |
| Cuisnahuat | Sonsonate | Occidental |
| Santa Isabel Ishuatán | Sonsonate | Occidental |
| Izalco | Sonsonate | Occidental |
| San Julián | Sonsonate | Occidental |
| Juayúa | Sonsonate | Occidental |
| Nahuizalco | Sonsonate | Occidental |
| Salcoatitán | Sonsonate | Occidental |
| Santa Catarina Masahuat | Sonsonate | Occidental |
| Acajutla | Sonsonate | Occidental |
| Sonsonate - Nivel departamental | Sonsonate | Occidental |
| Agua Caliente | Chalatenango | Central |
| Dulce Nombre de María | Chalatenango | Central |
| El Paraíso | Chalatenango | Central |
| La Reina | Chalatenango | Central |
| Nueva Concepción | Chalatenango | Central |
| San Fernando | Chalatenango | Central |
| San Francisco Morazán | Chalatenango | Central |
| San Rafael | Chalatenango | Central |
| Santa Rita | Chalatenango | Central |
| Tejutla | Chalatenango | Central |
| Citalá | Chalatenango | Central |
| San Ignacio | Chalatenango | Central |
| La Palma | Chalatenango | Central |
| Arcatao | Chalatenango | Central |
| Azacualpa | Chalatenango | Central |
| Comalapa | Chalatenango | Central |
| Concepción Quezaltepeque | Chalatenango | Central |
| Chalatenango | Chalatenango | Central |
| El Carrizal | Chalatenango | Central |
| La Laguna | Chalatenango | Central |
| Las Vueltas | Chalatenango | Central |
| Nombre de Jesús | Chalatenango | Central |
| Nueva Trinidad | Chalatenango | Central |
| Ojos de Agua | Chalatenango | Central |
| Potonico | Chalatenango | Central |
| San Antonio de la Cruz | Chalatenango | Central |
| San Antonio Los Ranchos | Chalatenango | Central |
| San Isidro Labrador | Chalatenango | Central |
| San Francisco Lempa | Chalatenango | Central |
| San José Cancasque / Cancasque | Chalatenango | Central |
| San José Las Flores / Las Flores | Chalatenango | Central |
| San Luis del Carmen | Chalatenango | Central |
| San Miguel de Mercedes | Chalatenango | Central |
| Chalatenango - Nivel departamental | Chalatenango | Central |
| Ciudad Arce | La Libertad | Central |
| San Juan Opico | La Libertad | Central |
| Chiltiupán | La Libertad | Central |
| Jicalapa | La Libertad | Central |
| La Libertad | La Libertad | Central |
| Tamanique | La Libertad | Central |
| Teotepeque | La Libertad | Central |
| Antiguo Cuscatlán | La Libertad | Central |
| Huizúcar | La Libertad | Central |
| Nuevo Cuscatlán | La Libertad | Central |
| San José Villanueva | La Libertad | Central |
| Zaragoza | La Libertad | Central |
| Quezaltepeque | La Libertad | Central |
| San Matías | La Libertad | Central |
| San Pablo Tacachico | La Libertad | Central |
| Colón | La Libertad | Central |
| Jayaque | La Libertad | Central |
| Sacacoyo | La Libertad | Central |
| Talnique | La Libertad | Central |
| Tepecoyo | La Libertad | Central |
| Comasagua | La Libertad | Central |
| Santa Tecla antes: Nueva San Salvador | La Libertad | Central |
| La Libertad - Nivel departamental | La Libertad | Central |
| Ayutuxtepeque | San Salvador | Central |
| Cuscatancingo | San Salvador | Central |
| Mejicanos | San Salvador | Central |
| San Salvador | San Salvador | Central |
| Delgado | San Salvador | Central |
| Ilopango | San Salvador | Central |
| San Martín | San Salvador | Central |
| Soyapango | San Salvador | Central |
| Tonacatepeque | San Salvador | Central |
| Aguilares | San Salvador | Central |
| El Paisnal | San Salvador | Central |
| Guazapa | San Salvador | Central |
| Apopa | San Salvador | Central |
| Nejapa | San Salvador | Central |
| Panchimalco | San Salvador | Central |
| Rosario de Mora | San Salvador | Central |
| San Marcos | San Salvador | Central |
| Santiago Texacuangos | San Salvador | Central |
| Santo Tomás | San Salvador | Central |
| San Salvador - Nivel departamental | San Salvador | Central |
| Oratorio de Concepción | Cuscatlán | Central |
| San Bartolomé Perulapía | Cuscatlán | Central |
| San José Guayabal | Cuscatlán | Central |
| San Pedro Perulapán | Cuscatlán | Central |
| Suchitoto | Cuscatlán | Central |
| Candelaria | Cuscatlán | Central |
| Cojutepeque | Cuscatlán | Central |
| El Carmen | Cuscatlán | Central |
| El Rosario | Cuscatlán | Central |
| Monte San Juan | Cuscatlán | Central |
| San Cristóbal | Cuscatlán | Central |
| San Rafael Cedros | Cuscatlán | Central |
| San Ramón | Cuscatlán | Central |
| Santa Cruz Analquito | Cuscatlán | Central |
| Santa Cruz Michapa | Cuscatlán | Central |
| Tenancingo | Cuscatlán | Central |
| Cuscatlán - Nivel departamental | Cuscatlán | Central |
| El Rosario / Rosario de La Paz | La Paz | Central |
| Jerusalén | La Paz | Central |
| Mercedes La Ceiba | La Paz | Central |
| Paraíso de Osorio | La Paz | Central |
| San Antonio Masahuat | La Paz | Central |
| San Emigdio | La Paz | Central |
| San Juan Tepezontes | La Paz | Central |
| San Miguel Tepezontes | La Paz | Central |
| San Pedro Nonualco | La Paz | Central |
| Santa María Ostuma | La Paz | Central |
| Santiago Nonualco | La Paz | Central |
| San Luis La Herradura | La Paz | Central |
| San Juan Nonualco | La Paz | Central |
| San Rafael Obrajuelo | La Paz | Central |
| Zacatecoluca | La Paz | Central |
| Cuyultitán | La Paz | Central |
| Olocuilta | La Paz | Central |
| San Francisco Chinameca | La Paz | Central |
| San Juan Talpa | La Paz | Central |
| San Luis Talpa | La Paz | Central |
| San Pedro Masahuat | La Paz | Central |
| Tapalhuaca | La Paz | Central |
| La Paz - Nivel departamental | La Paz | Central |
| Dolores / Villa Dolores | Cabañas | Central |
| Guacotecti | Cabañas | Central |
| San Isidro | Cabañas | Central |
| Sensuntepeque | Cabañas | Central |
| Victoria | Cabañas | Central |
| Cinquera | Cabañas | Central |
| Ilobasco | Cabañas | Central |
| Jutiapa | Cabañas | Central |
| Tejutepeque | Cabañas | Central |
| Cabañas - Nivel departamental | Cabañas | Central |
| Apastepeque | San Vicente | Central |
| San Esteban Catarina | San Vicente | Central |
| San Ildefonso | San Vicente | Central |
| San Lorenzo | San Vicente | Central |
| San Sebastián | San Vicente | Central |
| Santa Clara | San Vicente | Central |
| Santo Domingo | San Vicente | Central |
| Guadalupe | San Vicente | Central |
| San Cayetano Istepeque | San Vicente | Central |
| San Vicente | San Vicente | Central |
| Tecoluca | San Vicente | Central |
| Tepetitán | San Vicente | Central |
| Verapaz | San Vicente | Central |
| San Vicente - Nivel departamental | San Vicente | Central |
| California | Usulután | Oriental |
| Concepción Batres | Usulután | Oriental |
| Ereguayquín | Usulután | Oriental |
| Jucuarán | Usulután | Oriental |
| Ozatlán | Usulután | Oriental |
| Usulután | Usulután | Oriental |
| San Dionisio | Usulután | Oriental |
| Santa Elena | Usulután | Oriental |
| Santa María | Usulután | Oriental |
| Tecapán | Usulután | Oriental |
| Alegría | Usulután | Oriental |
| Berlín | Usulután | Oriental |
| El Triunfo | Usulután | Oriental |
| Estanzuelas | Usulután | Oriental |
| Jucuapa | Usulután | Oriental |
| Mercedes Umaña | Usulután | Oriental |
| Nueva Granada | Usulután | Oriental |
| San Buenaventura | Usulután | Oriental |
| Santiago de María | Usulután | Oriental |
| Jiquilisco | Usulután | Oriental |
| Puerto El Triunfo | Usulután | Oriental |
| San Agustín | Usulután | Oriental |
| San Francisco Javier | Usulután | Oriental |
| Usulután - Nivel departamental | Usulután | Oriental |
| Comacarán | San Miguel | Oriental |
| Moncagua | San Miguel | Oriental |
| Chirilagua | San Miguel | Oriental |
| Quelepa | San Miguel | Oriental |
| San Miguel | San Miguel | Oriental |
| Uluazapa | San Miguel | Oriental |
| Carolina | San Miguel | Oriental |
| Ciudad Barrios | San Miguel | Oriental |
| Chapeltique | San Miguel | Oriental |
| Nuevo Edén de San Juan | San Miguel | Oriental |
| San Antonio del Mosco | San Miguel | Oriental |
| San Gerardo | San Miguel | Oriental |
| San Luis de La Reina | San Miguel | Oriental |
| Sesori | San Miguel | Oriental |
| Chinameca | San Miguel | Oriental |
| El Tránsito | San Miguel | Oriental |
| Lolotique | San Miguel | Oriental |
| Nueva Guadalupe | San Miguel | Oriental |
| San Jorge | San Miguel | Oriental |
| San Rafael Oriente | San Miguel | Oriental |
| San Miguel - Nivel departamental | San Miguel | Oriental |
| Arambala | Morazán | Oriental |
| Cacaopera | Morazán | Oriental |
| Corinto | Morazán | Oriental |
| El Rosario | Morazán | Oriental |
| Joateca | Morazán | Oriental |
| Jocoaitique | Morazán | Oriental |
| Meanguera | Morazán | Oriental |
| Perquín | Morazán | Oriental |
| San Fernando | Morazán | Oriental |
| San Isidro | Morazán | Oriental |
| Torola | Morazán | Oriental |
| Chilanga | Morazán | Oriental |
| Delicias de Concepción | Morazán | Oriental |
| El Divisadero | Morazán | Oriental |
| Gualococti | Morazán | Oriental |
| Guatajiagua | Morazán | Oriental |
| Jocoro | Morazán | Oriental |
| Lolotiquillo | Morazán | Oriental |
| Osicala | Morazán | Oriental |
| San Carlos | Morazán | Oriental |
| San Francisco Gotera | Morazán | Oriental |
| San Simón | Morazán | Oriental |
| Sensembra | Morazán | Oriental |
| Sociedad | Morazán | Oriental |
| Yamabal | Morazán | Oriental |
| Yoloaiquín | Morazán | Oriental |
| Morazán - Nivel departamental | Morazán | Oriental |
| Anamorós | La Unión | Oriental |
| Bolívar | La Unión | Oriental |
| Concepción de Oriente | La Unión | Oriental |
| El Sauce | La Unión | Oriental |
| Lislique | La Unión | Oriental |
| Nueva Esparta | La Unión | Oriental |
| Pasaquina | La Unión | Oriental |
| Polorós | La Unión | Oriental |
| San José La Fuente | La Unión | Oriental |
| Santa Rosa de Lima | La Unión | Oriental |
| Conchagua | La Unión | Oriental |
| El Carmen | La Unión | Oriental |
| Intipucá | La Unión | Oriental |
| La Unión | La Unión | Oriental |
| Meanguera del Golfo | La Unión | Oriental |
| San Alejo | La Unión | Oriental |
| Yayantique | La Unión | Oriental |
| Yucuaiquín | La Unión | Oriental |
| La Unión - Nivel departamental | La Unión | Oriental |
| Nivel nacional | Nivel nacional | Nivel nacional |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Guardado de la información registrada | Clic en botón "Guardar" y "Aceptar" en el mensaje emergente, sin errores de validación (FA-01) | Pantalla "Análisis de la Población" (se mantiene en la misma pestaña) |
| Bloqueo de guardado por ingreso inválido (Población Afectada > Población de Referencia) | Clic en botón "Guardar" con población afectada mayor a la de referencia (FA-03) | Modal "Ingreso inválido" (Anexo A.3); no se guarda la información |
| Bloqueo de guardado por ingreso inválido (Población Objetivo > Población Afectada) | Clic en botón "Guardar" con población objetivo mayor a la afectada (FA-03) | Modal "Ingreso inválido" (Anexo A.4); no se guarda la información |
| Cálculo automático de Población en Espera, porcentajes y totales | Registro de N° de personas en las filas de Población de Referencia, Afectada y Objetivo | Campos "N° de personas (Población en Espera)", "%", "Total N° de personas" en Anexo A.1 |
| Navegación a página del Censo (BCR) | Clic en el botón "Censo" (cualquier actor) | Geoportal BCR - Censo de Población y Vivienda 2024 (RN03) |
| Navegación a "Área de influencia" | Clic en botón "Siguiente" (FA-02) | CU-PRE-08 "Área de influencia" |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| Geoportal del Banco Central de Reserva (BCR) | Enlace web externo | Muestra los resultados del "Censo de Población y Vivienda 2024" al dar clic en el botón "Censo" (RN03). URL: https://geoportal.bcr.gob.sv/pages/teg-poblaci%C3%B3n |

---

# Datos Pendientes de Definir

- No se especifica la prioridad del caso de uso.
- No se especifica un disparador (evento inicial) explícito del caso de uso.
- La sección "Excepciones" no está desarrollada en el documento (no hay códigos, descripciones ni consecuencias documentadas más allá de los dos casos de ingreso inválido descritos en FA-03).
- La tabla de "Errores" no está desarrollada en el documento como catálogo de códigos formales (no hay códigos de error asignados, solo los textos de los mensajes de validación).
- No se especifica el texto exacto del mensaje de ayuda contextual que muestra el ícono "?" (RN08); solo se indica que "indicará qué información se debe completar en dicho campo", sin el texto literal.
- Contradicción no resuelta: el texto del mensaje de error del mockup del Anexo A.3 difiere del descrito en la tabla de formatos del Anexo B.1 para la misma validación (Población Afectada vs. Población de Referencia); no se define cuál es el texto definitivo.
- Contradicción no resuelta: el texto del mensaje de error del mockup del Anexo A.4 difiere del descrito en la tabla de formatos del Anexo B.1 para la misma validación (Población Objetivo vs. Población Afectada); no se define cuál es el texto definitivo.
- Contradicción no resuelta: los campos "Ubicación (Población Afectada)", "Ubicación (Población Objetivo)" y "Ubicación (Población de Referencia)" se describen funcionalmente como campos que el Técnico URP debe registrar o seleccionar activamente, pero la tabla de formatos del Anexo B.1 los marca como "No" editables.
- RN01 menciona genéricamente "Todos los demás actores" con permiso de solo visualización, sin identificarlos de forma individual más allá del Técnico PRE (RN02); no queda claro qué otros roles concretos están incluidos en esa categoría.
- No se aclara si el carácter de "Recomendación" con que inicia RN06 aplica a toda la regla o solo a su primera oración, dado que el resto de la regla se redacta en términos prescriptivos.
- No se especifica si existe un límite en la cantidad de columnas de "Ubicación" y "N° de Personas" que pueden agregarse (RN09).