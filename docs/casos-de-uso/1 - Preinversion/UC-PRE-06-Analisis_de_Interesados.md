---
id: CU-PRE-06
codigo: CU-PRE-06
nombre: Análisis de Interesados
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-06_Análisis_de_Interesados_JUL_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 7

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
  - CU-PRE-07 Población Objetivo

roles:
  - Técnico URP
  - Técnico PRE

pantallas:
  - Matriz de gestión de interesados (Anexo A.1)
  - Guardar (Anexo A.2)

procesos:
  - Registro de la matriz de gestión de interesados
  - Identificación de niveles de influencia e interés
  - Registro de estrategias de gestión

servicios_externos: []

entidades:
  - Proyecto
  - Interesado
  - Matriz de gestión de interesados

catalogos:
  - Anexo C.1 – Catálogo de interesados

palabras_clave:
  - análisis de interesados
  - matriz de gestión de interesados
  - nivel de influencia
  - nivel de interés
  - estrategia de gestión

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
      nombre: Matriz de gestión de interesados
      pagina: 5
    A2:
      nombre: Guardar
      pagina: 6
    B1:
      nombre: Formatos - Pantalla Matriz de gestión de interesados
      pagina: 6
    C1:
      nombre: Catálogo de interesados
      pagina: 7
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Análisis de Interesados |
| Código | CU-PRE-06 |
| Módulo | Preinversión |
| Fuente | CU-PRE-06_Análisis_de_Interesados_JUL_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Nombre del interesado
- Tipo
- Nivel de influencia
- Nivel de interés
- Estrategia de gestión

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite al actor "Técnico URP" registrar en el sistema la matriz de interesados del proyecto, identificando los niveles de influencia (poder) e interés de cada uno de ellos, así como las estrategias de gestión que se prevé implementar.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico PRE (mencionado en RN02, con permiso de visualización de la información de todas las Unidades Ejecutoras).

> Nota: RN01 menciona de forma genérica "Todos los demás actores" con permiso de solo visualización según credenciales, sin identificarlos individualmente más allá del Técnico PRE (RN02); ver "Datos Pendientes de Definir".

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP de CU-PRE-01 "Registro de Proyectos".
2. Contar con la Ruta de Preinversión generada del CU-PRE-03.5 "Selección y registro de etapas".

---

# Flujo Principal

1. Técnico URP ingresa a la pestaña "Formulación del Proyecto" en la sección "Diagnóstico de la Situación Actual".
2. Técnico URP completa la información de la tabla "Matriz de gestión de interesados" (Ver Anexo A.1).

---

# Flujos Alternos

## FA-01 – Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1.1 Técnico URP da clic en el botón "Guardar".
1.2 Sistema muestra mensaje emergente del Anexo A.2.
1.3 Técnico URP da clic en "Aceptar" al mensaje emergente.
1.4 Sistema guarda la información registrada y se mantiene en Anexo A.1.

**Resultado**

> No especificado en el documento.

## FA-02 – Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

2.1 Técnico URP da clic en el botón "Siguiente".
2.2 Sistema avanza a la siguiente sección CU-PRE-07 "Población objetivo" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|-------------|--------------|
| — | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento.

# Postcondiciones

1. El proyecto cuenta con la matriz de interesados registrada.
2. El proyecto avanza a CU-PRE-07 "Población Objetivo".

---

# Reglas de Negocio

### RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** Documento, sección "Reglas del Negocio".

### RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** Documento, sección "Reglas del Negocio".

### RN03

**Descripción:** El Técnico URP podrá adicionar más filas a la tabla de la pantalla "Matriz de gestión de interesados" conforme a la cantidad de interesados que quiera incluir (ver Anexo A.1).

**Origen:** Documento, sección "Reglas del Negocio".

### RN04

**Descripción:** El Técnico URP podrá eliminar filas de interesados agregados en la tabla (ver Anexo A.1).

**Origen:** Documento, sección "Reglas del Negocio".

### RN05

**Descripción:** El interesado se podrá registrar más de una vez, siempre y cuando cambie la información de al menos una de las columnas (tipo, influencia, interés, estrategia gestión). Esto por cuanto es posible que, en una situación analizada, el actor tenga más de un rol o pueda generar más de una estrategia, entre otras combinaciones.

**Origen:** Documento, sección "Reglas del Negocio".

### RN06

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** Documento, sección "Reglas del Negocio".

### RN07

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Documento, sección "Reglas del Negocio".

---

# Campos

## Pantalla "Matriz de gestión de interesados"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Nombre del interesado | Campo para registrar el nombre del interesado | Texto | Texto | Sí | No especificado en el documento. | Editable |
| Tipo | Campo de selección del tipo de interesado, con las siguientes opciones: Cooperante, Oponente, Beneficiario, Perjudicado | Selección | Selección | Sí | No especificado en el documento. | Editable |
| Nivel de influencia | Campo de selección del nivel de influencia (Alto, Bajo) | Selección | Selección | Sí | No especificado en el documento. | Editable |
| Nivel de interés | Campo de selección del nivel de interés (Alto, Bajo) | Selección | Selección | Sí | No especificado en el documento. | Editable |
| Estrategia de gestión | Campo para registrar la estrategia de gestión | Texto | Texto | Sí | No especificado en el documento. | Editable |

> Observación: la tabla de formatos del Anexo B.1 está encabezada en el documento como perteneciente a la pantalla "Captura de Proyectos"; sin embargo, por su contenido (Nombre del interesado, Tipo, Nivel de influencia, Nivel de interés, Estrategia de gestión) corresponde claramente a la pantalla "Matriz de gestión de interesados" de este caso de uso. Se transcribe bajo el nombre de pantalla correcto según el contexto funcional; ver "Observaciones" y "Datos Pendientes de Definir".

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Nombre del interesado | Campo obligatorio | No especificado en el documento. |
| Tipo | Campo obligatorio | No especificado en el documento. |
| Nivel de influencia | Campo obligatorio | No especificado en el documento. |
| Nivel de interés | Campo obligatorio | No especificado en el documento. |
| Estrategia de gestión | Campo obligatorio | No especificado en el documento. |
| Fila de interesado (registro repetido) | El mismo interesado podrá registrarse más de una vez únicamente si cambia al menos una de las columnas (tipo, influencia, interés, estrategia de gestión) | No especificado en el documento. |
| Campos pendientes de completar (general) | Al dar clic en "Guardar" y haber campos pendientes de completar, se sombrean sus bordes en rojo (RN06) | No especificado en el documento. |

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
| Técnico URP | Adicionar filas a la tabla "Matriz de gestión de interesados" | RN03 |
| Técnico URP | Eliminar filas de interesados agregados | RN04 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras | RN02 |
| Todos los demás actores (no identificados individualmente) | Visualizar únicamente la información de las Unidades Ejecutoras, según credenciales | RN01 |

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01 "Registro de Proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-07 "Población Objetivo"

**Procesos relacionados:**
- Registro de la matriz de gestión de interesados
- Identificación de niveles de influencia e interés
- Registro de estrategias de gestión

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## A.1 – Matriz de gestión de interesados

**Nombre:** Matriz de gestión de interesados

**Descripción:** Tabla donde el Técnico URP registra cada interesado del proyecto, su tipo, nivel de influencia, nivel de interés y estrategia de gestión, con opción de adicionar o eliminar filas.

**Campos:**
- Nombre del interesado
- Tipo (con filtro)
- Nivel de Influencia (con filtro)
- Nivel de Interés (con filtro)
- Estrategia de Gestión

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Nombre del interesado | Tipo | Nivel de Influencia | Nivel de Interés | Estrategia de Gestión |
|---|---|---|---|---|
| Comunidad local afectada | Beneficiario | Bajo | Alto | Involucramiento activo en consultas, reuniones informativas, comités de vigilancia y jornadas de mantenimiento. |
| Alcaldía Municipal | Cooperante | Alto | Alto | Participación activa en la planificación, ejecución y supervisión del proyecto. |
| Propietarios de terrenos cercanos al cauce | Perjudicado | Bajo | Bajo | Comunicación previa, compensación justa y acuerdos voluntarios. |

**Botones:**
- Botón "+" (agregar fila)
- Botón "x" por fila (eliminar interesado)
- GUARDAR
- SIGUIENTE

**Acciones:**
- "+": agrega una nueva fila a la tabla (RN03).
- "x": elimina la fila del interesado correspondiente (RN04).
- Guardar: dispara FA-01.
- Siguiente: dispara FA-02, navega a CU-PRE-07 "Población objetivo".

## A.2 – Guardar

**Nombre:** Modal "¡Guardado!"

**Descripción:** Modal de confirmación que se despliega tras la acción de guardar en FA-01, paso 1.2.

**Campos:**
> No especificado en el documento.

**Botones:**
- Aceptar

**Acciones:**
- Aceptar: cierra el modal de confirmación (FA-01, paso 1.3).

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|-----------------|
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Al hacer clic en el botón Guardar en la pantalla "Matriz de gestión de interesados" (Anexo A.2; FA-01, paso 1.2) |
| Ayuda contextual | Mensaje indicando qué información se debe completar en el campo (texto exacto no especificado) | Al dar clic en el ícono "?" que aparece al acercar el cursor a un campo (RN07) |

---

# Observaciones

- La tabla de formatos del Anexo B.1 se presenta en el documento bajo el encabezado "Pantalla 'Captura de Proyectos'", lo cual no corresponde al nombre de la pantalla de este caso de uso ("Matriz de gestión de interesados" / pestaña "Formulación del Proyecto"). Por el contenido de los campos descritos (Nombre del interesado, Tipo, Nivel de influencia, Nivel de interés, Estrategia de gestión), es evidente que se trata de un error de encabezado (posiblemente copiado de otro caso de uso, como UC-PRE-03 "Captura de Proyectos"), no de una referencia funcional real a dicha pantalla.
- El catálogo de tipos de interesado presenta el mismo listado de cuatro valores en dos lugares del documento (la descripción del campo "Tipo" en el Anexo B.1 y el catálogo del Anexo C.1), con el mismo orden y contenido ("Cooperante", "Oponente", "Beneficiario", "Perjudicado"), por lo que no se detecta discrepancia entre ambos.
- El documento no aclara si existe un límite en la cantidad de filas de interesados que pueden agregarse a la tabla "Matriz de gestión de interesados".

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|----------|-------------|-----------|
| Proyecto | Proyecto sobre el cual se registra la matriz de interesados | No especificado en el documento. |
| Interesado | Cada fila registrada en la tabla "Matriz de gestión de interesados", con su tipo, nivel de influencia, nivel de interés y estrategia de gestión | Registro / Adición / Eliminación de filas (RN03, RN04); Registro repetido bajo condición (RN05) |
| Matriz de gestión de interesados | Conjunto completo de interesados registrados para un proyecto | Registro / Actualización (Flujo Básico, paso 2; FA-01) |

---

# Catálogos Detectados

## C.1 – Catálogo de interesados

| Catálogo | Valores conocidos |
|----------|---------------------|
| Interesados (Tipo) | Cooperante; Oponente; Beneficiario; Perjudicado |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Adición de una nueva fila de interesado | Clic en el botón "+" (Técnico URP) | Tabla "Matriz de gestión de interesados" (RN03) |
| Eliminación de una fila de interesado | Clic en el botón "x" junto al interesado (Técnico URP) | Tabla "Matriz de gestión de interesados" (RN04) |
| Guardado de la información registrada | Clic en botón "Guardar" y "Aceptar" en el mensaje emergente (FA-01) | Pantalla "Matriz de gestión de interesados" (se mantiene en Anexo A.1) |
| Navegación a "Población objetivo" | Clic en botón "Siguiente" (FA-02) | CU-PRE-07 "Población objetivo" |

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
- No se especifica el texto exacto del mensaje de ayuda contextual que muestra el ícono "?" (RN07); solo se indica que "indicará qué información se debe completar en dicho campo", sin el texto literal.
- No se especifican los textos exactos de los mensajes de validación para los campos obligatorios de la tabla (Nombre del interesado, Tipo, Nivel de influencia, Nivel de interés, Estrategia de gestión); solo se indica que son obligatorios y que sus bordes se sombrean en rojo si faltan.
- RN01 menciona genéricamente "Todos los demás actores" con permiso de solo visualización, sin identificarlos de forma individual más allá del Técnico PRE (RN02); no queda claro qué otros roles concretos están incluidos en esa categoría.
- El encabezado de la tabla de formatos del Anexo B.1 identifica erróneamente la pantalla como "Captura de Proyectos" en lugar de "Matriz de gestión de interesados"; no se puede confirmar con certeza si esto refleja una intención distinta del documento o es simplemente un error de transcripción del encabezado.
- No se especifica si existe un límite en la cantidad de filas de interesados que pueden agregarse a la tabla.