---
id: CU-PRE-12
codigo: CU-PRE-12
nombre: Localización
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.3"
fuente_pdf: CU-PRE-12_Localización_AGO_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 20

nota_version: >
  La versión 1.1 incorpora un CAMBIO FUNCIONAL solicitado explícitamente por
  el equipo de negocio, que NO proviene del PDF original y por tanto se
  aparta del contenido literal extraído en la versión 1.0:
  (1) Las preguntas "¿El proyecto requiere de la adquisición de un terreno o
  inmueble?", "¿Quién es el propietario del terreno o inmueble?" y
  "Especifique" dejan de ser campos únicos mostrados en una sección aparte
  ("ESTATUS DEL TERRENO (SI APLICA)") y pasan a ser tres columnas de la
  tabla "LOCALIZACIÓN" (Anexo A.1), ubicadas inmediatamente después de la
  columna "Coordenadas"; por lo tanto ahora se registran por cada fila
  (cada ubicación) en lugar de una sola vez por proyecto.
  (2) El botón que autocompleta la fila de ubicación debe traer la
  información desde CU-PRE-08 "Área de Influencia", según instrucción
  del equipo funcional, en lugar de (o adicionalmente a) CU-PRE-07
  "Población Objetivo" como indicaba el PDF original (RN03, FA-03).
  Todo el contenido marcado como "Cambio funcional" en este documento
  refleja esta decisión de negocio y no una corrección de extracción del
  PDF. No se modificó ningún otro contenido ya transcrito en la versión 1.0.

  La versión 1.2 resuelve el pendiente que la versión 1.1 había dejado
  abierto: el equipo funcional confirmó que la restricción de RN04 (los
  distritos adicionados en filas nuevas no pueden estar fuera de los
  departamentos ya registrados) debe validar contra CU-PRE-08 "Área de
  Influencia", en consistencia con el nuevo origen del autocompletado
  definido en RN03. Se actualizó RN04, la tabla de Campos ("Distritos"),
  "Validaciones", "Integraciones" y "Entidades Detectadas" en
  consecuencia, y se retiró el pendiente correspondiente de "Datos
  Pendientes de Definir". No se modificó ningún otro contenido ya
  corregido en versiones anteriores.

  La versión 1.3 resuelve el segundo pendiente que la versión 1.1 había
  dejado abierto: el equipo funcional confirmó que el botón "TRAER
  UBICACIÓN DE POBLACIÓN OBJETIVO" (nombre del PDF original) debe
  renombrarse a "TRAER UBICACIÓN DE ÁREA DE INFLUENCIA", para que el
  nombre sea consistente con su fuente de datos vigente (CU-PRE-08
  "Área de Influencia"). Se actualizó el nombre del botón en FA-03, en la
  tabla de Campos, en "Pantallas", en "Eventos del Sistema" y en
  "Integraciones", y se retiró el pendiente correspondiente de "Datos
  Pendientes de Definir". El texto y nombre originales del PDF se
  conservan, marcados explícitamente como tales, únicamente como
  referencia histórica. No se modificó ningún otro contenido ya corregido
  en versiones anteriores.

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-07", "CU-PRE-08"]

casos_relacionados: ["CU-PRE-07"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Localización (Anexo A.1)", "Mensaje de Guardado (Anexo A.2)"]

procesos: ["Autocompletado de macro/microlocalización desde CU-PRE-07 (RN03, FA-03)", "Adición/eliminación de filas de localización (RN04, RN05)", "Registro de la propiedad del terreno o inmueble (RN06, RN07)"]

servicios_externos: ["Plataforma de georreferenciación (mapa/marcador, ver Anexo A.1)"]

entidades: ["Localización", "Macrolocalización (Departamento/Distrito)", "Microlocalización (Dirección Específica/Coordenadas)", "Propiedad del Terreno o Inmueble", "Proyecto", "Unidad Ejecutora"]

catalogos: ["Catálogo de propietarios (Anexo C.1)", "Catálogo de ubicaciones geográficas (Anexo C.2)"]

palabras_clave: ["Localización", "Macrolocalización", "Microlocalización", "Georreferenciación", "Terreno", "Inmueble", "Técnico URP", "Preinversión"]

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
      pagina: 4
    RN05:
      pagina: 4
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
      nombre: Localización (Pantalla)
      pagina: 6
    A2:
      nombre: Guardar (Mensaje emergente)
      pagina: 7
    B1:
      nombre: Formatos (Pantalla "Localización")
      pagina: 7
    C1:
      nombre: Catálogo de propietarios
      pagina: 9
    C2:
      nombre: Catálogo de ubicaciones geográficas
      pagina: 9
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Localización |
| Código | CU-PRE-12 |
| Módulo | Preinversión |
| Fuente | CU-PRE-12_Localización_AGO_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Departamento
- Distrito
- Dirección Específica
- Coordenadas

---

# Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|-------|---------|-------------|-------|-------|
| JUL 2025 | 1.0 | Primera Versión | Equipo Preinversión | No especificado en el documento. |

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección explícita titulada "Objetivo"; presenta en su lugar una sección "Descripción caso de uso" (ver sección "Descripción" de este documento).

---

# Descripción

Este caso de uso permite al actor "Técnico URP" identificar la macro y micro localización del proyecto, con la posibilidad de incorporar las coordenadas del mismo y que estas se muestren como un marcador en una plataforma de georreferenciación, asimismo contar con la opción de seleccionar directamente en la plataforma la ubicación mediante marcadores.

Además, permitirá identificar la propiedad del terreno o inmueble (si aplica).

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
3. Contar con el registro de información de la "Población" CU-PRE-07.
4. Contar con el registro de "Área de influencia" CU-PRE-08.

---

# Flujo Principal

1. Técnico URP: Ingresa a la pestaña "Formulación del Proyecto" en la sección "Estudio Técnico del Proyecto – Localización".
2. Técnico URP: Completa la información de los campos del Anexo A.1.

---

# Flujos Alternos

## FA-01 Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Guardar".
2. Sistema: Muestra mensaje emergente del Anexo A.2.
3. Técnico URP: Da clic en "Aceptar" a mensaje emergente.
4. Sistema: Guarda la información registrada y se mantiene en la sección.

**Resultado**

> No especificado en el documento.

## FA-02 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Siguiente".
2. Sistema: Avanza a la siguiente sección (Tamaño) para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

## FA-03 Traer ubicación de Área de Influencia

**Condición**

> No especificado en el documento.

**Flujo (texto original del PDF):**

1. Técnico URP: Da clic en el botón "Traer ubicación de Población Objetivo".
2. Sistema: Completa los campos pertinentes según la información ingresada en Población Objetivo CU-PRE-07 "Población Objetivo", y se mantiene en la pantalla actual.

**Resultado**

> No especificado en el documento.

> **Cambio funcional (no proviene del PDF original):** por instrucción del equipo funcional, el botón se renombra a **"Traer ubicación de Área de Influencia"** y su paso 2 debe leerse ahora como "Sistema: Completa los campos pertinentes (Departamento, Distrito, Dirección Específica y Coordenadas de la fila) según la información registrada en CU-PRE-08 'Área de Influencia', y se mantiene en la pantalla actual". El nombre y el texto originales del PDF se conservan arriba tal como fueron extraídos, únicamente como referencia histórica.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|--------------|--------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento. El documento no incluye una tabla o listado explícito de excepciones para este caso de uso.

# Postcondiciones

1. El proyecto cuenta con la macro y microlocalización definida.

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** No especificado en el documento.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** No especificado en el documento.

## RN03

**Descripción (texto original del PDF):** El Sistema traerá automáticamente la información registrada en el campo "Población Objetivo" de la pantalla "Análisis de la población" del CU-PRE-07 "Población Objetivo" y completará los campos de la microlocalización cuando sea necesario (Dirección específica y coordenadas). Estos campos no son obligatorios.

**Origen:** No especificado en el documento.

> **Cambio funcional (no proviene del PDF original):** por instrucción explícita del equipo funcional, el botón que autocompleta la fila de ubicación debe traer la información desde CU-PRE-08 "Área de Influencia" en lugar de CU-PRE-07 "Población Objetivo". Esto es consistente con lo ya descrito en la tabla "Campos" para "Dirección Específica", cuyo detalle (Anexo B.1) siempre indicó que esa información "proviene de CU-PRE-08 'Área de Influencia'" — es decir, el texto original del PDF ya contenía esa inconsistencia entre RN03/FA-03 (que citan CU-PRE-07) y el detalle de "Dirección Específica" (que cita CU-PRE-08); esta versión resuelve la inconsistencia a favor de CU-PRE-08, por decisión de negocio y no por hallazgo de extracción. El equipo funcional confirmó además que el botón debe **renombrarse** de "TRAER UBICACIÓN DE POBLACIÓN OBJETIVO" a **"TRAER UBICACIÓN DE ÁREA DE INFLUENCIA"**, para que su nombre sea consistente con su nueva fuente de datos (ver "Pantallas" y "Observaciones").

## RN04

**Descripción:** El Técnico URP podrá editar dicha información y adicionar más filas a la tabla de la pantalla "Localización" mediante un botón emergente al acercar el cursor a un punto definido en la tabla (ver Anexo A.1). Sin embargo, los distritos que incluya, no podrán estar por fuera de los departamentos seleccionados en la población afectada registrados en CU-PRE-07 "Población Objetivo".

**Origen:** No especificado en el documento.

> **Cambio funcional (no proviene del PDF original) — pendiente resuelto:** por instrucción del equipo funcional, la restricción se actualiza para validar contra **CU-PRE-08 "Área de Influencia"** en lugar de CU-PRE-07 "Población Objetivo", en consistencia con el cambio de fuente ya aplicado en RN03. Debe leerse: "los distritos que incluya no podrán estar por fuera de los departamentos registrados en la tabla 'Área de Influencia' de CU-PRE-08". Se conserva la referencia a CU-PRE-07 arriba porque es el texto literal del PDF original; esta nota documenta la regla vigente.

## RN05

**Descripción:** El Técnico URP podrá eliminar una fila mediante un botón emergente ubicado a un costado de la misma (ver Anexo A.1).

**Origen:** No especificado en el documento.

## RN06

**Descripción (texto original del PDF):** Si el Técnico URP selecciona la opción "Sí" en el campo "¿El proyecto requiere de la adquisición de un terreno o inmueble?" Se desplegará el campo "¿Quién es el propietario del terreno o inmueble?". En caso de seleccionar la opción "No", el campo "¿Quién es el propietario del terreno o inmueble?" no se desplegará.

**Origen:** No especificado en el documento.

> **Cambio funcional (no proviene del PDF original):** al convertirse "¿El proyecto requiere de la adquisición de un terreno o inmueble?" y "¿Quién es el propietario del terreno o inmueble?" en columnas de la tabla "LOCALIZACIÓN" (ver nota en Front Matter y en "Pantallas"), esta regla ahora aplica de forma independiente por cada fila/ubicación de la tabla, en lugar de una sola vez para todo el proyecto.

## RN07

**Descripción (texto original del PDF):** En la sección "Propiedad del Terreno", si el Técnico URP selecciona del listado desplegable cualquiera de las opciones "Otra Institución Pública", "La Municipalidad", "Comodato" y "Otros", el Sistema habilitará un campo para que el Técnico URP especifique o amplíe la condición del terreno o inmueble.

**Origen:** No especificado en el documento.

> **Cambio funcional (no proviene del PDF original):** la antigua "sección 'Propiedad del Terreno'" ya no existe como bloque separado; el campo "Especifique" ahora es la tercera columna de la tabla "LOCALIZACIÓN" y esta regla se habilita por fila, según la opción seleccionada en la columna "¿Quién es el propietario del terreno o inmueble?" de esa misma fila.

## RN08

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** No especificado en el documento.

## RN09

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** No especificado en el documento.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Departamento | Se autocompleta si el Técnico URP da clic al botón "Traer ubicación de Área de Influencia" (renombrado; ver RN03). Si se adicionan filas, los campos serán editables para que seleccione el respectivo departamento. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1), salvo en filas adicionadas. Fuente del autocompletado: **CU-PRE-08 "Área de Influencia"** (ver nota de Cambio funcional en RN03). |
| Distritos | Se autocompleta si el Técnico URP da clic al botón "Traer ubicación de Área de Influencia" (renombrado; ver RN03). Si se adicionan filas, los campos serán editables para que seleccione el respectivo distrito. | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1), salvo en filas adicionadas; restricción de RN04, resuelta a favor de **CU-PRE-08 "Área de Influencia"** (ver RN04). Fuente del autocompletado: **CU-PRE-08 "Área de Influencia"** (ver nota de Cambio funcional en RN03). |
| Dirección Específica | Campo para registrar la dirección específica del proyecto, en caso que aplique. La información proviene de CU-PRE-08 "Área de Influencia" pero debe estar habilitada para que el usuario la pueda incluir o ajustar. No obligatorio. | Texto | Texto | No | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Coordenadas | Campo para registrar las coordenadas geográficas del proyecto para cada localización ingresada en formato DD (Grados Decimales). Al ingresar las coordenadas el Sistema de manera automática las ubicará en el mapa (ver Anexo A.1) por medio de un marcador. El Sistema también permitirá que el campo se autocomplete en formato DD si se agrega un marcador en una ubicación específica dentro del mapa. En el caso en que en el campo "Distritos" se seleccione "Nivel nacional" se bloqueará el ingreso de coordenadas, presentando únicamente la celda sombreada. Opcional. | Numérico | Numérico | No | No especificado en el documento. | Editable: Sí (según Anexo B.1); bloqueado si Distrito = "Nivel nacional". |
| ¿El proyecto requiere de la adquisición de un terreno o inmueble? | Campo para que el técnico URP seleccione a través de un botón radial si el proyecto requiere la adquisición de un terreno o inmueble. Las opciones serán Sí y No. | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); condiciona la visibilidad del campo "¿Quién es el propietario del terreno o inmueble?" (RN06). **Cambio funcional:** ahora es la 5ª columna de la tabla "LOCALIZACIÓN", ubicada inmediatamente después de "Coordenadas"; se registra por cada fila/ubicación (ver Front Matter, `nota_version`). |
| ¿Quién es el propietario del terreno o inmueble? | Campo para que el Técnico URP seleccione una opción de un listado desplegable (las opciones a mostrar son: "La Institución propietaria del proyecto", "Otra Institución pública", "La Municipalidad", "Comodato" y "Otros", según el Catálogo de propietarios, Anexo C.1). | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); se despliega solo si la respuesta anterior es "Sí" (RN06); habilita el campo "Especifique" según ciertas opciones (RN07). **Cambio funcional:** ahora es la 6ª columna de la tabla "LOCALIZACIÓN", por fila. |
| Especifique | Campo abierto de hasta 100 caracteres, en el que el Técnico URP especifica quien es el propietario. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1); se habilita solo si se selecciona "Otra Institución Pública", "La Municipalidad", "Comodato" u "Otros" (RN07). **Cambio funcional:** ahora es la 7ª (última) columna de la tabla "LOCALIZACIÓN", por fila. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|--------------------|
| Coordenadas | Formato DD (Grados Decimales); se bloquea el ingreso si Distrito = "Nivel nacional" (Anexo B.1). | No especificado en el documento. |
| Especifique | Máximo 100 caracteres (Anexo B.1). | No especificado en el documento. |
| Distritos (en filas adicionadas) | Los distritos incluidos no podrán estar por fuera de los departamentos registrados en la tabla "Área de Influencia" de **CU-PRE-08** (RN04, actualizado por cambio funcional). | No especificado en el documento. |
| Campos pendientes de completar (no se especifica cuáles exactamente son obligatorios) | Al dar clic en "Guardar", si hay campos pendientes de completar, el sistema sombrea los bordes de dichos campos en color rojo (RN08). | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pantalla "Localización". | RN01 |
| Técnico URP | Editar información autocompletada y adicionar filas a la tabla de localización. | RN04 |
| Técnico URP | Eliminar una fila de la tabla de localización. | RN05 |
| Otros actores (no nombrados individualmente en el documento) | Visualizar la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-07 "Población Objetivo"
- CU-PRE-08 "Área de Influencia"

**Procesos relacionados:** No especificado en el documento.

**Servicios externos:** Plataforma de georreferenciación (mapa con marcador, mostrada en Anexo A.1; el documento no identifica el proveedor o sistema específico más allá de la marca de agua "Google Earth" visible en la captura de pantalla).

---

# Pantallas

## Pantalla: Localización (Anexo A.1)

**Descripción:** Pantalla que permite al Técnico URP registrar, por cada ubicación, la macrolocalización (Departamento, Distrito), la microlocalización (Dirección Específica, Coordenadas) y el estatus del terreno o inmueble de esa ubicación, visualizando además dicha ubicación en un mapa mediante un marcador.

> **Cambio funcional (no proviene del PDF original):** el mockup original del PDF (Anexo A.1) mostraba "¿El proyecto requiere de la adquisición de un terreno o inmueble?", "¿Quién es el propietario del terreno o inmueble?" y "Especifique" como tres campos únicos, agrupados aparte en una sección "ESTATUS DEL TERRENO (SI APLICA)" debajo de la tabla y el mapa, aplicables una sola vez para todo el proyecto. Por instrucción del equipo funcional, estos tres campos se integran ahora como columnas 5ª, 6ª y 7ª de la propia tabla "LOCALIZACIÓN", inmediatamente después de "Coordenadas", de modo que se registran de forma independiente por cada fila/ubicación. La sección aparte "ESTATUS DEL TERRENO (SI APLICA)" deja de existir como tal.

**Campos:**
- Departamento (Macrolocalización)
- Distrito (Macrolocalización)
- Dirección Específica (Microlocalización)
- Coordenadas (Microlocalización)
- ¿El proyecto requiere de la adquisición de un terreno o inmueble? (Sí/No) — *columna añadida por cambio funcional*
- ¿Quién es el propietario del terreno o inmueble? (catálogo desplegable, Anexo C.1) — *columna añadida por cambio funcional*
- Especifique — *columna añadida por cambio funcional*

**Botones:**
- "TRAER UBICACIÓN DE ÁREA DE INFLUENCIA" *(renombrado por cambio funcional; texto original del PDF: "TRAER UBICACIÓN DE POBLACIÓN OBJETIVO")*
- "GUARDAR"
- "SIGUIENTE"

**Acciones:**
- Al dar clic en "TRAER UBICACIÓN DE ÁREA DE INFLUENCIA": completa automáticamente Departamento, Distrito y, cuando sea necesario, Dirección Específica y Coordenadas, según **CU-PRE-08 "Área de Influencia"** (RN03 modificado por cambio funcional; FA-03). El botón fue renombrado por instrucción del equipo funcional para que su nombre sea consistente con su fuente de datos (ver "Observaciones").
- Al ingresar Coordenadas: el sistema ubica automáticamente un marcador en el mapa (Anexo B.1).
- Al agregar un marcador en el mapa: el sistema autocompleta el campo Coordenadas en formato DD (Anexo B.1).
- Al seleccionar "Sí" en "¿El proyecto requiere de la adquisición de un terreno o inmueble?" de una fila: se despliega, en esa misma fila, el campo "¿Quién es el propietario del terreno o inmueble?" (RN06, alcance por fila desde el cambio funcional).
- Al seleccionar ciertas opciones en "¿Quién es el propietario del terreno o inmueble?" de una fila: se habilita, en esa misma fila, el campo "Especifique" (RN07, alcance por fila desde el cambio funcional).
- Al dar clic en "GUARDAR": ejecuta FA-01.
- Al dar clic en "SIGUIENTE": ejecuta FA-02.
- Al acercar el cursor a un punto definido de la tabla: aparece botón emergente para adicionar fila (RN04).
- Al acercar el cursor a un costado de una fila: aparece botón emergente para eliminar fila (RN05).
- Al acercar el cursor a un campo: aparece ícono "?" con información de ayuda (RN09).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

> El ejemplo de datos original del PDF (para Departamento, Distrito, Dirección Específica y Coordenadas) se conserva sin cambios. Las tres columnas nuevas no tenían equivalente en el mockup del PDF (estaban vacías y en una sección aparte, según se documentó en la versión 1.0); se muestran aquí igualmente vacías, ahora integradas a la fila, para no inventar valores de ejemplo que el PDF no mostró.

Tabla "LOCALIZACIÓN":

| Departamento (Macrolocalización) | Distrito (Macrolocalización) | Dirección Específica (Microlocalización) | Coordenadas (Microlocalización) | ¿El proyecto requiere la adquisición de un terreno o inmueble? | ¿Quién es el propietario del terreno o inmueble? | Especifique |
|-------------------------------------|----------------------------------|------------------------------------------------|--------------------------------------|--------------------------------------------------------------------|-------------------------------------------------------|-------------|
| La Libertad | La Libertad (selector desplegable) | Cantón San Rafael, en el casco urbano del Puerto de La Libertad | 13.494399201865445, -89.37787076933522 | (vacío) | (vacío) | (vacío) |
| (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |
| (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |

Mapa: Imagen satelital (con marca de agua "Google Earth") del territorio de El Salvador, con un marcador rojo ubicado en la zona costera del departamento de La Libertad, correspondiente a las coordenadas registradas en la tabla.

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
| Mensaje | Sus datos han sido guardados exitosamente. |
| Botón | Aceptar |

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Éxito | ¡Guardado! Sus datos han sido guardados exitosamente. | Al dar clic en el botón "Guardar" (paso 1.2 de FA-01, Anexo A.2). |

---

# Observaciones

- Discrepancia de fechas dentro del propio documento fuente: el nombre del archivo indica "AGO 2025"; el encabezado repetido en cada página del documento indica "Fecha: JUN 2025"; y la tabla "Historial de Revisiones" indica "JUL 2025" para la versión 1.0. El documento no aclara cuál de las tres fechas corresponde a la fecha real de emisión o última actualización del caso de uso.
- RN03 hace referencia a la pantalla "Análisis de la población" del CU-PRE-07 "Población Objetivo". En los documentos CU-PRE-08 "Área de Influencia" y CU-PRE-11 "Descripción Técnica" (de la misma serie), las referencias a CU-PRE-07 no mencionan una pantalla con ese nombre; en su lugar remiten genéricamente a "Población Objetivo" o "CU-PRE-07 'Población Objetivo'". El presente documento no aclara si "Análisis de la población" es el nombre formal de una pantalla dentro de CU-PRE-07 o si constituye una inconsistencia de nomenclatura entre casos de uso.
- FA-02 indica que el sistema "Avanza a la siguiente sección (Tamaño)" sin especificar un código de caso de uso (a diferencia de flujos equivalentes en otros documentos de la serie, como CU-PRE-08 y CU-PRE-09, que sí indican el código del siguiente CU). De igual forma, la sección "Postcondiciones" de este documento no menciona explícitamente el avance a un caso de uso posterior. El documento no proporciona el código formal de la sección "Tamaño".
- El campo "Coordenadas" es descrito en el Anexo B.1 como "Editable: Sí" y a la vez "Opcional"; el texto de Detalle no aclara si el campo puede quedar vacío de forma permanente o si su condición de "no obligatorio" aplica únicamente cuando el Distrito seleccionado es "Nivel nacional" (caso en que además queda bloqueado, según el mismo Detalle).
- La sección "ESTATUS DEL TERRENO (SI APLICA)" en el mockup del Anexo A.1 no muestra ninguna opción seleccionada ni valores de ejemplo diligenciados para los campos "¿El proyecto requiere de la adquisición de un terreno o inmueble?", "¿Quién es el propietario del terreno o inmueble?" ni "Especifique"; esto describe el mockup **del PDF original (versión 1.0)**, antes del cambio funcional de la versión 1.1 que integró estos campos como columnas de la tabla "LOCALIZACIÓN".
- **[Versión 1.1 → resuelto en versión 1.3 — cambio funcional, no hallazgo de extracción]** El botón "TRAER UBICACIÓN DE POBLACIÓN OBJETIVO" (nombre original del PDF) se renombró a **"TRAER UBICACIÓN DE ÁREA DE INFLUENCIA"**, por instrucción del equipo funcional, para que su nombre sea consistente con su fuente de datos (CU-PRE-08 "Área de Influencia" en lugar de CU-PRE-07 "Población Objetivo"; ver RN03).
- **[Versión 1.1 → resuelto en versión 1.2 — cambio funcional, no hallazgo de extracción]** RN04 restringía originalmente los distritos adicionados a los departamentos "seleccionados en la población afectada registrados en CU-PRE-07 'Población Objetivo'". El equipo funcional confirmó que esta restricción debe validar contra **CU-PRE-08 "Área de Influencia"**, en consistencia con el cambio de fuente de RN03. La regla quedó actualizada en RN04, en la tabla de Campos ("Distritos") y en "Validaciones".

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|--------------|-----------|
| Localización | Registro que asocia a un proyecto su macrolocalización (Departamento, Distrito) y microlocalización (Dirección Específica, Coordenadas). | Registro/edición por el Técnico URP (RN01); Guardado de la información (FA-01, RN08); Adición de filas (RN04); Eliminación de filas (RN05). |
| Macrolocalización (Departamento/Distrito) | Ubicación geográfica general del proyecto, autocompletada desde CU-PRE-08 y restringida a los departamentos registrados en CU-PRE-08 "Área de Influencia" (ambos por cambio funcional). | Autocompletado desde CU-PRE-08 "Área de Influencia" (RN03 modificado, FA-03 modificado); edición en filas adicionadas sujeta a restricción de RN04 (modificada, valida contra CU-PRE-08). |
| Microlocalización (Dirección Específica/Coordenadas) | Ubicación más precisa del proyecto, con posibilidad de visualización en mapa mediante marcador. | Autocompletado desde CU-PRE-08 "Área de Influencia" para Dirección Específica (Anexo B.1); registro manual o mediante marcador en mapa para Coordenadas (Anexo B.1); bloqueo condicional si Distrito = "Nivel nacional". |
| Propiedad del Terreno o Inmueble | Información sobre si el proyecto requiere adquisición de terreno/inmueble y, de ser así, quién es su propietario. Por cambio funcional, se registra por cada fila/ubicación de la tabla "LOCALIZACIÓN" (columnas 5ª a 7ª), en lugar de una sola vez por proyecto. | Registro condicional mediante despliegue de campos, por fila (RN06); habilitación de campo "Especifique" según opción seleccionada, por fila (RN07). |
| Proyecto | Proyecto de inversión pública al que se asocia la localización. | Actualización de estado al completar la macro y microlocalización (Postcondiciones). |
| Unidad Ejecutora | Entidad cuya información puede visualizarse por otros actores según credenciales. | Consulta/visualización (RN01, RN02). |

---

# Catálogos Detectados

## Catálogo de propietarios (Anexo C.1)

| Propietarios |
|--------------|
| La Institución propietaria del proyecto |
| Otra Institución pública |
| La Municipalidad |
| Comodato |
| Otros |

## Catálogo de ubicaciones geográficas (Anexo C.2)

| Distrito | Departamento | Región |
|----------|---------------|--------|
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
| Santa Tecla (antes: Nueva San Salvador) | La Libertad | Central |
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
| Autocompletado de Departamento/Distrito/Dirección Específica/Coordenadas | Botón "Traer ubicación de Área de Influencia" (renombrado por cambio funcional; fuente de datos: CU-PRE-08 "Área de Influencia" — ver RN03, FA-03) | Pantalla "Localización" (RN03, FA-03) |
| Ubicación automática de marcador en el mapa | Ingreso de Coordenadas | Mapa (Anexo A.1) |
| Autocompletado de Coordenadas | Adición de marcador en el mapa | Campo "Coordenadas" |
| Despliegue del campo "¿Quién es el propietario del terreno o inmueble?" | Selección "Sí" en "¿El proyecto requiere de la adquisición de un terreno o inmueble?" | Pantalla "Localización" (RN06) |
| Habilitación del campo "Especifique" | Selección de "Otra Institución Pública", "La Municipalidad", "Comodato" u "Otros" | Pantalla "Localización" (RN07) |
| Guardado de información | Botón "Guardar" | Mensaje emergente "¡Guardado!" (Anexo A.2, FA-01) |
| Navegación a siguiente sección | Botón "Siguiente" | Sección "Tamaño" (FA-02) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| CU-PRE-07 "Población Objetivo" | Interno (dentro del mismo Sistema de Información de Inversión Pública) | Referenciado en el texto original del PDF (RN03, FA-03, RN04) como fuente del autocompletado y como restricción de distritos permitidos. Por cambio funcional, ambos roles pasaron a CU-PRE-08 (ver siguiente fila); ya no aplica como fuente activa en esta pantalla. |
| CU-PRE-08 "Área de Influencia" | Interno (dentro del mismo Sistema de Información de Inversión Pública) | Fuente de la información del campo "Dirección Específica" (Anexo B.1) y, por cambio funcional, fuente del autocompletado de Departamento, Distrito y Coordenadas mediante el botón "Traer ubicación de Área de Influencia" (renombrado; RN03 modificado, FA-03 modificado), y fuente de la restricción de distritos permitidos en filas adicionadas (RN04 modificado). |
| Plataforma de georreferenciación (mapa) | Externo (según se infiere de la marca de agua "Google Earth" en el mockup; el documento no la nombra explícitamente en el texto) | Visualización de un marcador correspondiente a las coordenadas registradas, y posibilidad de fijar un marcador para autocompletar coordenadas (Anexo B.1). |

---

# Datos Pendientes de Definir

- Prioridad del caso de uso: no especificada en el documento.
- Disparador (evento que inicia el caso de uso): no especificado explícitamente en el documento.
- Tabla de excepciones: no desarrollada en el documento.
- Tabla de errores/códigos de error: no desarrollada en el documento.
- Texto exacto del mensaje de validación asociado al resaltado en rojo de campos pendientes (RN08): no especificado.
- Código formal del caso de uso al que corresponde la sección "Tamaño" mencionada en FA-02: no especificado en el documento (ver "Observaciones").
- Discrepancia no resuelta entre las tres fechas presentes en el documento fuente (nombre de archivo "AGO 2025", encabezado "JUN 2025", historial de revisiones "JUL 2025") (ver "Observaciones").
- Ambigüedad no resuelta respecto al nombre de la pantalla "Análisis de la población" referida en RN03 dentro de CU-PRE-07, que no se corresponde con las referencias a CU-PRE-07 en otros documentos de la serie (ver "Observaciones").
- Ambigüedad no resuelta respecto a si el campo "Coordenadas" puede permanecer vacío de forma permanente al ser "Opcional" pero también "Editable: Sí" (ver "Observaciones").