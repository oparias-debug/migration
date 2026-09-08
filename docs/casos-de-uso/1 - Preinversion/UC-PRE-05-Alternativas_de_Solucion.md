---
id: CU-PRE-05
codigo: CU-PRE-05
nombre: Alternativas de Solución
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-05_Alternativas_de_Solución_JUL_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 7

actor_principal: Técnico URP

actores_secundarios:
  - Técnico preinversión
  - Usuario Interno / Externo

prioridad: No especificado en el documento.

estado: Analizado

depende_de:
  - CU-PRE-01 Registro de Proyectos
  - CU-PRE-3.5 Selección y registro de etapas

casos_relacionados:
  - CU-PRE-01 Registro de Proyectos
  - CU-PRE-3.5 Selección y registro de etapas
  - CU-PRE-06 Análisis de interesados

roles:
  - Técnico URP
  - Técnico preinversión
  - Usuario Interno / Externo

pantallas:
  - Registro de alternativas (Anexo A.1)
  - Información guardada exitosamente (Anexo A.2)

procesos:
  - Registro de alternativas de solución
  - Selección de la alternativa más conveniente
  - Registro de justificación

servicios_externos: []

entidades:
  - Proyecto
  - Alternativa de solución
  - Justificación

catalogos: []

palabras_clave:
  - alternativas de solución
  - registro de alternativas
  - justificación
  - selección de alternativa

ultima_actualizacion: JUN 2025

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
    RN1_1:
      pagina: 4
    RN1_2:
      pagina: 4
    RN1_3:
      pagina: 4
    RN2_1:
      pagina: 4
    RN2_2:
      pagina: 4
    RN2_3:
      pagina: 4
    RN2_4:
      pagina: 4
    RN2_5:
      pagina: 4
    RN2_6:
      pagina: 4
    RN2_7:
      pagina: 4
    RN3_1:
      pagina: 4
    RN3_2:
      pagina: 4
  anexos:
    A1:
      nombre: Registro de alternativas
      pagina: 5
    A2:
      nombre: Información guardada exitosamente
      pagina: 6
    B1:
      nombre: Formatos - Pantalla Identificación del proyecto, sección Registro de alternativas
      pagina: 7
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Alternativas de Solución |
| Código | CU-PRE-05 |
| Módulo | Preinversión |
| Fuente | CU-PRE-05_Alternativas_de_Solución_JUL_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**

> No especificado en el documento. Este documento, al igual que CU-PRE-04, no incluye una sección explícita titulada "Campos requeridos" en la identificación del caso de uso.

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite al actor "Técnico URP" cargar la información de la(s) alternativa(s) desarrollada(s), a fin de determinar cuál de ellas es la más conveniente (proyecto), así como justificar en caso de haber desarrollado sólo una alternativa.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico preinversión: solo puede acceder a ver la información ingresada, sin permiso de editar; podrá visualizar la información de todas las Unidades Ejecutoras.
- Usuario Interno / Externo: solo puede acceder a ver la información ingresada, sin permiso de editar; podrá visualizar la información de las Unidades Ejecutoras según sus credenciales.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Que el proyecto cuente con CUP (CU-PRE-01 "Registro de Proyectos").
2. Que el técnico URP haya calificado el proyecto y generado una Ruta de Preinversión (CU-PRE-3.5 "Selección y registro de etapas").

---

# Flujo Principal

1. Técnico URP ingresa a la pantalla "Identificación del proyecto".
2. Sistema muestra la pantalla Identificación del proyecto.
3. Técnico URP registra la información en los campos mostrados en la sección "Registro de Alternativas" de la pestaña Identificación del proyecto.
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
2. Sistema muestra mensaje emergente descrito en A.2 - Información guardada exitosamente y marca en color rojo los bordes de los campos que aún no cuenten con información registrada.
3. Técnico URP da clic en "Aceptar" al mensaje emergente.
4. Sistema guarda la información registrada en la pestaña de Registro de alternativas y se mantiene en la sección actual.

Subflujo termina.

**Resultado**

> No especificado en el documento.

## SF-2 – Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón Siguiente.
2. Sistema avanza a la siguiente sección (Análisis de interesados CU-PRE-06) para continuar con el ingreso de información.

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

1. El proyecto avanza a CU-PRE-06 "Análisis de interesados".

---

# Reglas de Negocio

### RN1-1

**Descripción:** El Técnico URP es el único que puede ingresar y modificar información en la pantalla. Únicamente podrá ingresar y modificar la información de las Unidades Ejecutoras, según sus credenciales.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 1 – Actores / Roles".

### RN1-2

**Descripción:** El Técnico preinversión solo podrá acceder a ver la información ingresada, sin permiso de editar. Tendrá acceso a visualizar la pantalla desde que se guarden cambios por primera vez. Podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 1 – Actores / Roles".

### RN1-3

**Descripción:** El Usuario Interno / Externo solo podrá acceder a ver la información ingresada, sin permiso de editar. Tendrá acceso a visualizar la pantalla desde que se guarden cambios por primera vez. Podrá visualizar la información de las Unidades Ejecutoras en las pantallas, según sus credenciales.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 1 – Actores / Roles".

### RN2-1

**Descripción:** El sistema mostrará por defecto una fila y podrán adicionarse más filas con un botón emergente al acercar el cursor a la tabla (ver Anexo A.1).

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 2 – Condiciones de Pantalla".

### RN2-2

**Descripción:** Podrán eliminarse las alternativas adicionadas mediante un botón emergente ubicado a un costado de dicha alternativa (ver Anexo A.1).

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 2 – Condiciones de Pantalla".

### RN2-3

**Descripción:** Si solo se registra una alternativa será obligatorio completar el campo "Justificación". De no registrarse la justificación correspondiente, el sistema emitirá la alerta "Debe completarse el campo Justificación" al momento de dar clic al botón "Siguiente".

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 2 – Condiciones de Pantalla".

### RN2-4

**Descripción:** Será obligatorio el ingreso de al menos una alternativa; de no ingresar alguna, el sistema no permitirá avanzar a la siguiente sección y mostrará la siguiente alerta: "Debe ingresar al menos una alternativa".

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 2 – Condiciones de Pantalla".

### RN2-5

**Descripción:** El Técnico URP podrá registrar para las alternativas desarrolladas el monto y la descripción; la alternativa que se va a desarrollar debe quedar seleccionada.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 2 – Condiciones de Pantalla".

### RN2-6

**Descripción:** Solamente se debe seleccionar una alternativa.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 2 – Condiciones de Pantalla".

### RN2-7

**Descripción:** Será obligatorio registrar información en el campo "Justificación".

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 2 – Condiciones de Pantalla".

### RN3-1

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 3 – Mensajes colaborativos o de ayuda".

### RN3-2

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** Documento, sección "4. Reglas de Negocio", "RN 3 – Mensajes colaborativos o de ayuda".

---

# Campos

## Pantalla "Identificación del proyecto", sección "Registro de alternativas"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Nombre de la alternativa | Campo para digitar el nombre de las alternativas evaluadas por la Unidad Ejecutora, incluyendo la seleccionada como la más conveniente con base en el análisis previo realizado. Cuenta con un botón radial para dar clic sobre la alternativa seleccionada (RN2-6). El sistema deberá resaltar el texto de la alternativa seleccionada | Texto | Texto | Sí (RN2-4: al menos una alternativa) | No especificado en el documento. | Editable. Solo podrá seleccionarse una de las alternativas mediante el botón radial |
| Monto de la alternativa | Campo para digitar el monto de la alternativa. En US$. La alternativa seleccionada debe tener el mismo valor reportado en CU-PRE-01 "Registro de Proyectos" | Moneda | Moneda | No especificado en el documento como obligatorio de forma independiente; ver RN2-5 | No especificado en el documento. | Editable |
| Justificación | Campo para registrar la justificación en caso de que solo se registre una alternativa, o para registrar comentarios respecto a la alternativa seleccionada. Será obligatorio. Se considerará un rango hasta de 1,000 caracteres | Texto | Texto | Sí (RN2-3, RN2-7) | No especificado en el documento. | Editable. Límite de 1,000 caracteres |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Nombre de la alternativa (tabla completa) | Debe ingresarse al menos una alternativa | "Debe ingresar al menos una alternativa" |
| Justificación | Obligatorio cuando solo se registra una alternativa | "Debe completarse el campo Justificación" |
| Justificación | Límite de hasta 1,000 caracteres | No especificado en el documento. |
| Nombre de la alternativa | Solo puede seleccionarse una alternativa (botón radial) | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| — | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Ingresar y modificar información en la pantalla "Identificación del proyecto", sección "Registro de alternativas" | RN1-1 |
| Técnico URP | Ingresar y modificar información únicamente de las Unidades Ejecutoras según sus credenciales | RN1-1 |
| Técnico preinversión | Visualizar la información ingresada (sin editar), desde que se guarden cambios por primera vez | RN1-2 |
| Técnico preinversión | Visualizar la información de todas las Unidades Ejecutoras | RN1-2 |
| Usuario Interno / Externo | Visualizar la información ingresada (sin editar), desde que se guarden cambios por primera vez | RN1-3 |
| Usuario Interno / Externo | Visualizar la información de las Unidades Ejecutoras según sus credenciales | RN1-3 |

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01 "Registro de Proyectos"
- CU-PRE-3.5 "Selección y registro de etapas"
- CU-PRE-06 "Análisis de interesados"

**Procesos relacionados:**
- Registro de alternativas de solución
- Selección de la alternativa más conveniente
- Registro de justificación

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## A.1 – Registro de alternativas

**Nombre:** Registro de Alternativas de Solución

**Descripción:** Sección de la pantalla "Identificación del proyecto" donde el Técnico URP registra una o varias alternativas de solución con su nombre, monto y descripción, selecciona la más conveniente mediante un botón radial, y registra la justificación de dicha selección.

**Campos:**
- Tabla "Registro de Alternativas de Solución": Nombre de la alternativa (con botón radial de selección), Monto de la alternativa, Descripción de la alternativa, botón para eliminar fila (ícono "x")
- Botón emergente "+" para adicionar filas
- Justificación (de la alternativa de solución seleccionada)

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Nombre de la alternativa | Monto de la alternativa | Descripción de la alternativa | Seleccionada |
|---|---|---|---|
| Construcción de obras de mitigación | $3,000,000.0 | El proyecto consiste en... | Sí (botón radial marcado) |
| Reforestación de la cuenca y manejo de escorrentías | $1,250,000.0 | Incluye actividades como... | No |
| Mejoramiento de... | $1,750,000.0 | El proyecto incluye las actividades... | No |

**Justificación (ejemplo del mockup):** "La alternativa se ha seleccionado considerando que…"

**Botones:**
- Botón "+" (agregar fila)
- Botón "x" por fila (eliminar alternativa)
- GUARDAR
- SIGUIENTE

**Acciones:**
- "+": agrega una nueva fila a la tabla de alternativas (RN2-1).
- "x": elimina la alternativa correspondiente (RN2-2).
- Botón radial: selecciona la alternativa más conveniente; solo puede seleccionarse una (RN2-6).
- Guardar: dispara el subflujo SF-1.
- Siguiente: dispara el subflujo SF-2, navega a CU-PRE-06 "Análisis de interesados"; valida las reglas RN2-3 y RN2-4 antes de avanzar.

## A.2 – Información guardada exitosamente

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
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Al hacer clic en el botón Guardar en la pantalla "Identificación del proyecto", sección "Registro de alternativas" (Anexo A.2; SF-1, paso 2) |
| Alerta / Validación | "Debe completarse el campo Justificación" | Al dar clic en el botón "Siguiente" si solo se registró una alternativa y no se completó el campo "Justificación" (RN2-3) |
| Alerta / Validación | "Debe ingresar al menos una alternativa" | Al dar clic en el botón "Siguiente" si no se ha ingresado ninguna alternativa (RN2-4) |
| Ayuda contextual | Mensaje indicando qué información se debe completar en el campo (texto exacto no especificado) | Al dar clic en el ícono "?" que aparece al acercar el cursor a un campo (RN3-1) |

---

# Observaciones

- RN2-7 ("Será obligatorio registrar información en el campo 'Justificación'") establece la obligatoriedad de este campo de forma general, mientras que RN2-3 la condiciona específicamente al caso en que "solo se registra una alternativa". El documento no aclara si RN2-7 es una regla independiente que aplica siempre (incluso con más de una alternativa registrada) o si es una reiteración de la condición ya expresada en RN2-3.
- La tabla de formatos del Anexo B.1 no incluye una fila para el campo "Descripción de la alternativa", a pesar de que dicha columna aparece explícitamente en el mockup del Anexo A.1 ("REGISTRO DE ALTERNATIVAS DE SOLUCIÓN": Nombre de la alternativa, Monto de la alternativa, Descripción de la alternativa). No se cuenta con su Tipo, Formato, condición de edición ni límite de caracteres.
- El botón para eliminar una fila (ícono "x") y el botón emergente "+" para adicionar filas, visibles en el mockup del Anexo A.1, se mencionan en RN2-1 y RN2-2, pero no están descritos como filas propias en la tabla de formatos del Anexo B.1.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|----------|-------------|-----------|
| Proyecto | Proyecto sobre el cual se registran las alternativas de solución | Consulta (verificación de monto contra CU-PRE-01, RN2-5 descripción del campo "Monto de la alternativa") |
| Alternativa de solución | Cada alternativa registrada por el Técnico URP, con nombre, monto y descripción | Registro / Adición / Eliminación de filas (RN2-1, RN2-2); Selección única (RN2-6) |
| Justificación | Texto que justifica la selección de la alternativa, obligatorio en los casos indicados | Registro (RN2-3, RN2-7) |

---

# Catálogos Detectados

> No especificado en el documento.

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Visualización de la pantalla "Identificación del proyecto" | Ingreso del Técnico URP a la pantalla (Flujo Básico, paso 1) | Pantalla Anexo A.1 (sección "Registro de Alternativas") |
| Adición de una nueva fila de alternativa | Clic en el botón emergente "+" (Técnico URP) | Tabla "Registro de Alternativas de Solución" (RN2-1) |
| Eliminación de una alternativa | Clic en el botón emergente "x" junto a la alternativa (Técnico URP) | Tabla "Registro de Alternativas de Solución" (RN2-2) |
| Guardado de la información registrada | Clic en botón "Guardar" y "Aceptar" en el mensaje emergente (SF-1) | Pestaña "Registro de alternativas" (se mantiene en la misma sección) |
| Habilitación de visualización para Técnico preinversión y Usuario Interno/Externo | Primer guardado de cambios en la pantalla | Técnico preinversión, Usuario Interno / Externo (RN1-2, RN1-3) |
| Navegación a "Análisis de interesados" | Clic en botón "Siguiente", con validaciones RN2-3 y RN2-4 superadas (SF-2) | CU-PRE-06 "Análisis de interesados" |

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
- No se especifica el texto exacto del mensaje de ayuda contextual que muestra el ícono "?" (RN3-1); solo se indica que "indicará qué información se debe completar en dicho campo", sin el texto literal.
- El campo "Descripción de la alternativa", visible en el mockup del Anexo A.1, no cuenta con especificación de Tipo, Formato, condición de edición ni límite de caracteres en el Anexo B.1.
- No se aclara si RN2-7 (obligatoriedad general del campo "Justificación") es una regla independiente que aplica siempre, o una reiteración de la condición ya expresada en RN2-3 (obligatoriedad solo cuando se registra una única alternativa).
- No se especifica si existe un límite en la cantidad de alternativas que pueden agregarse a la tabla.