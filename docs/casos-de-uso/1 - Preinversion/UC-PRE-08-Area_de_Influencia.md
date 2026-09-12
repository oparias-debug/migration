---
id: CU-PRE-08
codigo: CU-PRE-08
nombre: Área de Influencia
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-08_Área_de_Influencia_JUL_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 16

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-07"]

casos_relacionados: ["CU-PRE-09", "CU-PRO-19", "CU-EJE-03"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Área de Influencia (Anexo A.1)", "Mensaje de Guardado (Anexo A.2)"]

procesos: ["Registro y autocompletado del área de influencia (RN07, FA-03)", "Adición/eliminación de filas de ubicación específica (RN03, RN04)"]

servicios_externos: []

entidades: ["Área de Influencia", "Ubicación Geográfica (Región/Departamento/Distrito)", "Ubicación Específica", "Proyecto", "Unidad Ejecutora"]

catalogos: ["Catálogo de Ubicaciones Geográficas (Anexo C.1)"]

palabras_clave: ["Área de Influencia", "Ubicación Geográfica", "Población Objetivo", "Técnico URP", "Preinversión"]

ultima_actualizacion: JUL 2025

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    FA01:
      pagina: 4
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
      pagina: 5
    RN06:
      pagina: 5
    RN07:
      pagina: 5
    RN08:
      pagina: 5
  anexos:
    A1:
      nombre: Área de Influencia (Pantalla)
      pagina: 6
    A2:
      nombre: Guardar (Mensaje emergente)
      pagina: 7
    B1:
      nombre: Formatos (Pantalla "Análisis de la Población")
      pagina: 7
    C1:
      nombre: Catálogo de ubicaciones geográficas
      pagina: 8
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Área de Influencia |
| Código | CU-PRE-08 |
| Módulo | Preinversión |
| Fuente | CU-PRE-08_Área_de_Influencia_JUL_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Distrito/s
- Departamento/s
- Región/es
- Ubicación específica

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

Este caso de uso permite al actor "Técnico URP" revisar e ingresar la información correspondiente al área de influencia, es decir, al área geográfica en la cual el proyecto tendrá impacto.

La pantalla permitirá identificar el área de influencia, siendo distrito(s), departamento(s) y región(es) e identificar una ubicación más precisa, según lo registrado en la Población Objetivo (por ejemplo: cantón, caserío, colonia, entre otros).

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

1. Contar con CUP de CU-PRE-01 "Registro de proyectos".
2. Contar con la Ruta de Preinversión generada en CU-PRE-03.5 "Selección y registro de etapas".
3. Haber identificado la "Población objetivo" en CU-PRE-07.

---

# Flujo Principal

1. Técnico URP: Ingresa a la pestaña "Formulación del Proyecto" en la sección "Diagnóstico de la Situación Actual – Área de Influencia".
2. Técnico URP: Revisa, valida y completa la información de los campos del Anexo A.1.

---

# Flujos Alternos

## FA-01 Guardar

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Guardar".
2. Sistema: Muestra mensaje emergente del Anexo A.2.
3. Técnico URP: Da clic en "Aceptar" a mensaje emergente.
4. Sistema: Guarda la información registrada y se mantiene en la pestaña.

**Resultado**

> No especificado en el documento.

## FA-02 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Siguiente".
2. Sistema: Avanza a la siguiente sección CU-PRE-09 "Análisis de mercado" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

## FA-03 Traer ubicación de Población Objetivo

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Traer ubicación de Población Objetivo".
2. Sistema: Completa los campos pertinentes según la información ingresada en CU-PRE-07 "Población Objetivo", y se mantiene en la pantalla actual.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|--------------|--------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento. El documento no incluye una tabla o listado explícito de excepciones para este caso de uso.

# Postcondiciones

1. El proyecto cuenta con el área de influencia identificada y registrada.
2. El proyecto avanza a CU-PRE-09 "Análisis de mercado".
3. CU-PRO-19 "Programación de la Distribución Financiera por Ubicación Geográfica".
4. CU-EJE-03 "Avance Mensual Financiero por Ubicación Geográfica del PAIP".

> Nota de ambigüedad: el documento lista los ítems 3 y 4 (CU-PRO-19 y CU-EJE-03) inmediatamente después de la postcondición principal, sin un verbo explícito (p. ej. "habilita", "permite continuar con") que aclare la naturaleza exacta de su relación con este caso de uso. Se incorporan por continuidad de formato, pero la redacción exacta de dicha relación no está especificada.

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** No especificado en el documento.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** No especificado en el documento.

## RN03

**Descripción:** El Técnico URP podrá adicionar más filas a la tabla de la pantalla "Área de Influencia" mediante un botón emergente al acercar el cursor a un punto definido en la tabla, para colocar las ubicaciones específicas dentro de las ubicaciones registradas en (CU-PRE-07 "Población Objetivo" (ver Anexo A.1).

**Origen:** No especificado en el documento.

## RN04

**Descripción:** El Técnico URP podrá eliminar filas de las ubicaciones específicas que hayan incluido. (ver Anexo A.1).

**Origen:** No especificado en el documento.

## RN05

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y hayan campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** No especificado en el documento.

## RN06

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo, el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** No especificado en el documento.

## RN07

**Descripción:** La información mostrada en los campos de "Región", "Departamento", "Distrito" y "Ubicación Específica" se completará automáticamente si el Técnico URP da clic al botón "Traer ubicación de Población Objetivo" según lo que se haya registrado en CU-PRE-07 "Población Objetivo", permitiéndole editar dicha información o agregar otra ubicación, en caso de ser necesario.

**Origen:** No especificado en el documento.

## RN08

**Descripción:** La información mostrada en los campos de "Región", "Departamento" y "Distrito", permanecerá bloqueada para edición. Si se requiere adicionar, debe hacerse en CU-PRE-07 "Población Objetivo". Sí pueden adicionarse filas para la ubicación específica.

**Origen:** No especificado en el documento.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Distrito | Campo que se podrá autocompletar si el Técnico URP da clic al botón "Traer ubicación de Población Objetivo" del CU-PRE-07 "Población Objetivo". | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Departamento | Este campo se podrá autocompletar si el Técnico URP da clic al botón "Traer ubicación de Población Objetivo" del CU-PRE-07 "Población Objetivo". | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Región | Este campo se podrá autocompletar si el Técnico URP da clic al botón "Traer ubicación de Población Objetivo" del CU-PRE-07 "Población Objetivo". | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ubicación específica | Campo que permitirá al Técnico URP registrar manualmente la ubicación específica. | Alfanumérico | Alfanumérico | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
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
| Técnico URP | Registrar/editar información en la pantalla "Área de Influencia". | RN01 |
| Técnico URP | Adicionar filas a la tabla de ubicación específica. | RN03 |
| Técnico URP | Eliminar filas de ubicaciones específicas incluidas. | RN04 |
| Otros actores (no nombrados individualmente en el documento) | Visualizar la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-07 "Población Objetivo"
- CU-PRE-09 "Análisis de mercado"
- CU-PRO-19 "Programación de la Distribución Financiera por Ubicación Geográfica"
- CU-EJE-03 "Avance Mensual Financiero por Ubicación Geográfica del PAIP"

**Procesos relacionados:** No especificado en el documento.

**Servicios externos:** No especificado en el documento.

---

# Pantallas

## Pantalla: Área de Influencia (Anexo A.1)

**Descripción:** Pantalla que permite al Técnico URP identificar y registrar el área de influencia del proyecto (región, departamento, distrito) y sus ubicaciones específicas.

**Campos:**
- Región
- Departamento
- Distrito
- Ubicación Específica

**Botones:**
- "TRAER UBICACIÓN DE POBLACIÓN OBJETIVO"
- "+" (botón emergente para adicionar fila, según RN03)
- "x" (botón para eliminar fila, según RN04)
- "GUARDAR"
- "SIGUIENTE"

**Acciones:**
- Al dar clic en "TRAER UBICACIÓN DE POBLACIÓN OBJETIVO": completa automáticamente Región, Departamento, Distrito y Ubicación Específica según CU-PRE-07 (RN07, FA-03).
- Al dar clic en "GUARDAR": ejecuta FA-01.
- Al dar clic en "SIGUIENTE": ejecuta FA-02.
- Al acercar el cursor a un punto definido de la tabla: aparece botón emergente para adicionar fila (RN03).
- Al acercar el cursor a un campo: aparece ícono "?" con información de ayuda (RN06).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Región | Departamento | Distrito | Ubicación Específica |
|--------|---------------|----------|------------------------|
| CENTRAL | La Libertad | La Libertad (selector desplegable) | Comunidad Río Mar |
| (vacío) | (vacío) | agregar (selector desplegable) | (vacío) |
| (vacío) | (vacío) | agregar (selector desplegable) | (vacío) |
| (vacío) | (vacío) | agregar (selector desplegable) | (vacío) |

## Pantalla: Mensaje de Guardado (Anexo A.2)

**Descripción:** Mensaje emergente de confirmación mostrado tras el guardado exitoso de la información (paso 1.2 de FA-01).

**Campos:** No aplica (mensaje emergente).

**Botones:**
- "Aceptar"

**Acciones:**
- Al dar clic en "Aceptar": cierra el mensaje emergente y el sistema guarda la información, manteniéndose en la pestaña (paso 1.4 de FA-01).

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

- Contradicción no resuelta entre RN07 y RN08: RN07 indica que los campos "Región", "Departamento", "Distrito" y "Ubicación Específica" se autocompletan al usar el botón "Traer ubicación de Población Objetivo", "permitiéndole editar dicha información o agregar otra ubicación, en caso de ser necesario"; mientras que RN08 indica que los campos "Región", "Departamento" y "Distrito" "permanecerán bloqueados para edición" y que solo pueden adicionarse filas para la "ubicación específica". El documento no aclara si la posibilidad de "editar dicha información" mencionada en RN07 se refiere únicamente al campo "Ubicación Específica" o a los cuatro campos en conjunto.
- La tabla de formatos del Anexo B.1 está titulada "Pantalla 'Análisis de la Población'", nombre que no coincide con el tema del presente caso de uso ("Área de Influencia"). El documento no aclara si se trata de un error de rotulación/copia de otro caso de uso o si "Análisis de la Población" es un nombre alterno de esta misma pantalla.
- En Postcondiciones, los ítems "CU-PRO-19" y "CU-EJE-03" aparecen listados inmediatamente después de la postcondición principal sin verbo explícito que indique su relación exacta con este caso de uso (ver nota de ambigüedad en la sección "Postcondiciones").
- El texto de RN03, tal como aparece en el documento fuente, contiene un paréntesis de apertura antes de "CU-PRE07" ("...registradas en (CU-PRE07 'Población Objetivo' (ver Anexo A.1)") que no se cierra formalmente: dentro de él se abre un segundo paréntesis interno "(ver Anexo A.1)" que sí se cierra, pero el paréntesis externo que antecede a "CU-PRE07" queda sin su correspondiente cierre en todo el fragmento. Se transcribió literalmente tal como figura en el PDF, sin agregar ni quitar signos de puntuación, dado que no es posible determinar con certeza la intención original de puntuación del autor.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|--------------|-----------|
| Área de Influencia | Registro que asocia a un proyecto la ubicación geográfica (región, departamento, distrito) y la(s) ubicación(es) específica(s) donde el proyecto tendrá impacto. | Registro/edición por el Técnico URP (RN01); Guardado de la información (FA-01, RN05). |
| Ubicación Geográfica (Región/Departamento/Distrito) | Catálogo de ubicaciones geográficas (Distrito, Departamento, Región) usado para completar el área de influencia. | Autocompletado desde CU-PRE-07 "Población Objetivo" mediante el botón "Traer ubicación de Población Objetivo" (RN07, FA-03); bloqueado para edición (RN08). |
| Ubicación Específica | Ubicación más precisa dentro del área de influencia (ej. cantón, caserío, colonia), registrada manualmente por el Técnico URP. | Registro manual (Anexo B.1); Adición de filas (RN03); Eliminación de filas (RN04). |
| Proyecto | Proyecto de inversión pública al que se asocia el área de influencia. | Actualización de estado al completar el área de influencia y avanzar a CU-PRE-09 (Postcondiciones). |
| Unidad Ejecutora | Entidad cuya información puede visualizarse por otros actores según credenciales. | Consulta/visualización (RN01, RN02). |

---

# Catálogos Detectados

## Catálogo de Ubicaciones Geográficas (Anexo C.1)

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
| Autocompletado de Región/Departamento/Distrito/Ubicación Específica | Botón "Traer ubicación de Población Objetivo" | Pantalla "Área de Influencia" (RN07, FA-03) |
| Guardado de información | Botón "Guardar" | Mensaje emergente "¡Guardado!" (Anexo A.2, FA-01) |
| Navegación a siguiente sección | Botón "Siguiente" | CU-PRE-09 "Análisis de mercado" (FA-02) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| CU-PRE-07 "Población Objetivo" | Interno (dentro del mismo Sistema de Información de Inversión Pública) | Fuente de datos para autocompletar Región, Departamento, Distrito y Ubicación Específica (RN07, FA-03). |

---

# Datos Pendientes de Definir

- Prioridad del caso de uso: no especificada en el documento.
- Disparador (evento que inicia el caso de uso): no especificado explícitamente en el documento.
- Tabla de excepciones: no desarrollada en el documento.
- Tabla de errores/códigos de error: no desarrollada en el documento.
- Texto exacto del mensaje de validación asociado al resaltado en rojo de campos pendientes (RN05): no especificado.
- Contradicción no resuelta entre RN07 y RN08 respecto a qué campos pueden editarse tras el autocompletado (ver sección "Observaciones").
- Ambigüedad no resuelta en Postcondiciones respecto a la relación exacta de CU-PRO-19 y CU-EJE-03 con este caso de uso (ver sección "Postcondiciones" y "Observaciones").
- Discrepancia no resuelta entre el título de la tabla del Anexo B.1 ("Pantalla 'Análisis de la Población'") y el tema del caso de uso ("Área de Influencia") (ver sección "Observaciones").