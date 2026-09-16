---
id: CU-PRE-11
codigo: CU-PRE-11
nombre: Descripción Técnica
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-11_Descripción_Técnica_JUL_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 14

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-09"]

casos_relacionados: ["CU-PRE-12"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Descripción Técnica (Anexo A.1)", "Mensaje de Guardado (Anexo A.2)"]

procesos: ["Autocompletado de la descripción del proyecto desde CU-PRE-01 u OT (RN03)", "Autocompletado de productos desde CU-PRE-09 (RN04)", "Adición/eliminación de filas de descripción técnica por producto (RN05, RN06)"]

servicios_externos: []

entidades: ["Descripción Técnica", "Producto", "Componente", "Proyecto", "Opinión Técnica (O.T.)", "Unidad de Medida", "Unidad Ejecutora"]

catalogos: ["Catálogo de componentes del proyecto (Anexo C.1)", "Catálogo de unidades de medida (Anexo D.1)"]

palabras_clave: ["Descripción Técnica", "Componente", "Producto", "Estudio Técnico", "Técnico URP", "Preinversión"]

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
    RN08:
      pagina: 4
  anexos:
    A1:
      nombre: Descripción Técnica (Pantalla)
      pagina: 5
    A2:
      nombre: Guardar (Mensaje emergente)
      pagina: 5
    B1:
      nombre: Formatos (tabla titulada "Pantalla 'Análisis de Mercado'" en el documento fuente)
      pagina: 5
    C1:
      nombre: Catálogo de componentes del proyecto
      pagina: 6
    D1:
      nombre: Catálogo de unidades de medida
      pagina: 6
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Descripción Técnica |
| Código | CU-PRE-11 |
| Módulo | Preinversión |
| Fuente | CU-PRE-11_Descripción_Técnica_JUL_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Componente
- Producto
- Descripción
- Cantidad
- Unidad de medida

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

Este caso de uso permite al actor "Técnico URP" ingresar la información correspondiente a la descripción técnica del proyecto.

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
3. Contar con el "Análisis de mercado" de CU-PRE-09.

---

# Flujo Principal

1. Técnico URP: Ingresa a la pestaña "Formulación del Proyecto" en la sección "Estudio Técnico del Proyecto – Descripción Técnica".
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
2. Sistema: Avanza a la siguiente Sección CU-PRE-12 "Localización" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|--------------|--------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento. El documento no incluye una tabla o listado explícito de excepciones para este caso de uso.

# Postcondiciones

1. El proyecto cuenta con la descripción técnica registrada y avanza a CU-PRE-12 "Localización".

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** No especificado en el documento.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** No especificado en el documento.

## RN03

**Descripción:** En la pantalla debe haber un campo en el que el sistema trae la descripción del proyecto registrada en CU-PRE-01 "Registro de Proyectos". Este campo debe quedar editable para ajuste por parte del Técnico URP. Si el proyecto ya tuvo OT, el sistema debe traer a este campo la descripción contenida en la última O.T emitida.

**Origen:** No especificado en el documento.

## RN04

**Descripción:** El sistema deberá diligenciar automáticamente las filas de la tabla del Anexo A.1, con los productos seleccionados en el CU-PRE-09 "Análisis de Mercado".

**Origen:** No especificado en el documento.

## RN05

**Descripción:** El Técnico URP podrá adicionar más filas a la tabla de la pantalla "Descripción Técnica" conforme al número de productos que contenga el proyecto que se va a formular (ver Anexo A.1).

**Origen:** No especificado en el documento.

## RN06

**Descripción:** El Técnico URP podrá eliminar las filas que haya creado; en todo caso, debe haber al menos una fila con toda la información registrada (ver Anexo A.1).

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
| Descripción del proyecto | Campo para registrar la descripción general del proyecto. El sistema traerá automáticamente la descripción del CU-PRE-01 "Registro de Proyectos". Será editable para que el usuario pueda ajustarla o complementarla. (Ver también RN03, que agrega que si el proyecto ya tuvo O.T., el sistema debe traer la descripción de la última O.T. emitida). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Producto | Este campo traerá automáticamente los productos registrados en la pantalla de análisis de mercado del CU-PRE-09 "Análisis de Mercado", permitiendo adicionar filas para seleccionar los demás productos que tendrá el proyecto, en caso de ser necesario. Los Productos previamente registrados en la pantalla de Análisis de mercado que se trasladen a la Descripción Técnica, permanecerán bloqueados para edición en esta pantalla. Además, dentro de este mismo campo, habrá un listado desplegable para seleccionar el componente al cual corresponde el producto registrado. | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1), aunque el propio detalle del campo indica que los productos trasladados desde CU-PRE-09 "permanecerán bloqueados para edición"; el documento no aclara esta aparente contradicción (ver "Observaciones"). |
| Componente | Campo para seleccionar el componente a describir. Se mostrará un listado según catálogo "Componentes del proyecto" Anexo C.1. Solo podrá seleccionarse un componente por cada fila. | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Descripción del producto | Campo para registrar la descripción general de cada producto agregado. Con un máximo de 500 caracteres. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Cantidad | Campo en el cual el usuario indica la cantidad de producto que va a producir. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Unidad de medida | Campo para seleccionar la unidad de medida del producto (por ejemplo: m2, Ton, Km). | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|--------------------|
| Descripción del producto | Máximo 500 caracteres (Anexo B.1). | No especificado en el documento. |
| Campos pendientes de completar (no se especifica cuáles exactamente son obligatorios) | Al dar clic en "Guardar", si hay campos pendientes de completar, el sistema sombrea los bordes de dichos campos en color rojo (RN07). | No especificado en el documento. |
| Fila de registro | Debe haber al menos una fila con toda la información registrada (RN06). | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pantalla "Descripción Técnica". | RN01 |
| Técnico URP | Adicionar filas a la tabla conforme al número de productos del proyecto. | RN05 |
| Técnico URP | Eliminar filas creadas, manteniendo al menos una fila completa. | RN06 |
| Otros actores (no nombrados individualmente en el documento) | Visualizar la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-09 "Análisis de Mercado"
- CU-PRE-12 "Localización"

**Procesos relacionados:** No especificado en el documento.

**Servicios externos:** No especificado en el documento.

---

# Pantallas

## Pantalla: Descripción Técnica (Anexo A.1)

**Descripción:** Pantalla que permite al Técnico URP registrar la descripción y tamaño del proyecto, así como una tabla de descripción técnica por producto, indicando el componente, la descripción del producto, la cantidad y la unidad de medida.

**Campos:**
- Descripción y Tamaño del Proyecto (recuadro de texto libre, ubicado antes de la tabla)
- Producto (con listado desplegable de Componente incluido en la misma celda, según Anexo B.1)
- Descripción
- Cantidad
- Unidad de Medida
- "Capacidad de Producción" (columna visible en el mockup, ver nota en "Observaciones")

**Botones:**
- "+" (botón emergente para agregar más filas, según RN05)
- "x" (botón emergente para eliminar filas, según RN06)
- "GUARDAR"
- "SIGUIENTE"

**Acciones:**
- Autocompletado de la tabla con productos desde CU-PRE-09 "Análisis de Mercado" (RN04).
- Al dar clic en "GUARDAR": ejecuta FA-01.
- Al dar clic en "SIGUIENTE": ejecuta FA-02.
- Al acercar el cursor a un punto definido de la tabla: aparece botón emergente para agregar fila.
- Al acercar el cursor a la fila: aparece botón emergente ("x") para eliminar fila.
- Al acercar el cursor a un campo: aparece ícono "?" con información de ayuda (RN08).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

Recuadro "DESCRIPCIÓN Y TAMAÑO DEL PROYECTO":
> El proyecto consiste en la reconstrucción de bordas en el sector de Los Desmontes, en el lateral derecho del río Grande de San Miguel.

Tabla "DESCRIPCIÓN TÉCNICA":

| Producto | Componente (listado dentro de la celda de Producto) | Descripción | Cantidad | Unidad de Medida | Capacidad de Producción |
|----------|--------------------------------------------------------|--------------|----------|---------------------|----------------------------|
| Bordas construidas (selector desplegable) | Infraestructura (selector desplegable) | construcción de bordas en las zonas afectadas, en margen derecho e izquierdo del río | 1500 | ml | (vacío) |
| Bordas recubiertas (selector desplegable) | Ambiental (selector desplegable) | siembra de zacate vetiver, jaragua, o cobertura vegetal idónea para el material del terraplén, en toda la corona y superficie de la borda. | 5000 | m2 | (vacío) |

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

- Contradicción no resuelta respecto al campo "Producto": la tabla de formatos del Anexo B.1 lo marca como "Editable: Sí", pero el propio texto de "Detalle" del mismo campo indica que "Los Productos previamente registrados en la pantalla de Análisis de mercado que se trasladen a la Descripción Técnica, permanecerán bloqueados para edición en esta pantalla." El documento no aclara si la edición permitida se refiere únicamente a la posibilidad de adicionar nuevas filas/productos, o si contradice directamente el bloqueo mencionado.
- El mockup del Anexo A.1 muestra una columna adicional titulada "CAPACIDAD DE PRODUCTTIÓN" (sic, con doble "T" en el documento fuente) en la tabla "Descripción Técnica", con celdas vacías en el ejemplo. Esta columna no está documentada en la tabla de campos del Anexo B.1 ("Formatos"), por lo que no se cuenta con su tipo, formato, obligatoriedad ni comportamiento (editable o no).
- El mockup del Anexo A.1 muestra, dentro de la celda de "Producto", un valor de componente "Ambiental" (fila "Bordas recubiertas"). Sin embargo, el Catálogo de componentes del proyecto (Anexo C.1) no incluye la opción "Ambiental" entre sus valores (Infraestructura, Equipamiento, Capacitaciones, Administración, Consultorías, Terrenos, Otros). El documento no aclara esta discrepancia entre el catálogo documentado y el valor mostrado en el ejemplo de pantalla.
- La tabla de formatos del Anexo B.1 está titulada "Pantalla 'Análisis de Mercado'", nombre que no coincide con el tema del presente caso de uso ("Descripción Técnica"). El documento no aclara si se trata de un error de rotulación/copia de otro caso de uso.
- El RN03 utiliza la sigla "OT" y, en la misma regla, la forma "O.T" (sin definir su significado en ningún punto del documento). Se transcribió literalmente tal como aparece en el texto fuente, sin expandir ni estandarizar la sigla.
- El Catálogo de unidades de medida (Anexo D.1) es idéntico en contenido al presentado como Anexo C.1 en el documento CU-PRE-09 "Análisis de Mercado"; se transcribe íntegramente en este documento por tratarse de un anexo completo propio de este PDF.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|--------------|-----------|
| Descripción Técnica | Registro que asocia a un proyecto la descripción general del proyecto y el detalle técnico (componente, producto, descripción, cantidad, unidad de medida) de cada producto. | Registro/edición por el Técnico URP (RN01); Guardado de la información (FA-01, RN07). |
| Producto | Bien o servicio trasladado automáticamente desde CU-PRE-09 "Análisis de Mercado" hacia la tabla de Descripción Técnica. | Autocompletado desde CU-PRE-09 (RN04); Adición de filas para productos adicionales (RN05); Eliminación de filas (RN06). |
| Componente | Categoría del catálogo "Componentes del proyecto" a la cual se asocia cada producto descrito. | Selección, un componente por fila (Anexo B.1). |
| Proyecto | Proyecto de inversión pública al que se asocia la descripción técnica. | Fuente de la descripción general autocompletada (RN03); Actualización de estado al completar la descripción técnica (Postcondiciones). |
| Opinión Técnica (O.T.) | Documento cuya última versión emitida, de existir, provee la descripción a autocompletar en el campo "Descripción del proyecto". | Consulta para autocompletado condicional (RN03). |
| Unidad Ejecutora | Entidad cuya información puede visualizarse por otros actores según credenciales. | Consulta/visualización (RN01, RN02). |

---

# Catálogos Detectados

## Catálogo de componentes del proyecto (Anexo C.1)

| Componente |
|------------|
| Infraestructura |
| Equipamiento |
| Capacitaciones |
| Administración |
| Consultorías |
| Terrenos |
| Otros |

## Catálogo de unidades de medida (Anexo D.1)

| Tipo | Categoría | Unidad de medida | Descripción / Uso común en estudio de mercado |
|------|-----------|-------------------|--------------------------------------------------|
| Bien | Conteo | Unidad (u) | Medida básica para bienes individuales |
| Bien | Conteo | Pieza | Bien físico individual |
| Bien | Conteo | Artículo | Bien comercializable |
| Bien | Conteo | Ítem | Elemento individual de un conjunto |
| Bien | Conteo | Lote | Conjunto de unidades |
| Bien | Conteo | Paquete | Agrupación comercial |
| Bien | Conteo | Caja | Contenedor con varias unidades |
| Bien | Conteo | Docena | Conjunto de 12 unidades |
| Bien | Conteo | Juego / Set | Conjunto de piezas relacionadas |
| Bien | Conteo | Kit | Conjunto de insumos o herramientas |
| Bien | Peso | Gramo (g) | Pequeñas cantidades |
| Bien | Peso | Kilogramo (kg) | Medida estándar de peso |
| Bien | Peso | Quintal | Productos agrícolas |
| Bien | Peso | Tonelada (t) | Grandes volúmenes |
| Bien | Peso | Libra (lb) | Uso comercial |
| Bien | Longitud | Milímetro (mm) | Componentes pequeños |
| Bien | Longitud | Centímetro (cm) | Medidas menores |
| Bien | Longitud | Metro (m) | Medida estándar |
| Bien | Longitud | Kilómetro (km) | Grandes extensiones |
| Bien | Superficie | Metro cuadrado (m²) | Construcción y áreas |
| Bien | Superficie | Hectárea (ha) | Terrenos agrícolas |
| Bien | Superficie | Manzana | Uso catastral |
| Bien | Volumen | Mililitro (ml) | Líquidos pequeños |
| Bien | Volumen | Litro (l) | Líquidos comerciales |
| Bien | Volumen | Metro cúbico (m³) | Obras y materiales |
| Bien | Volumen | Galón | Combustibles y líquidos |
| Bien | Capacidad | Watt (W) | Potencia eléctrica |
| Bien | Capacidad | Kilowatt (kW) | Equipos eléctricos |
| Bien | Capacidad | Kilovoltio-amperio (kVA) | Capacidad instalada |
| Servicio | Tiempo | Hora | Prestación por tiempo |
| Servicio | Tiempo | Día | Servicios diarios |
| Servicio | Tiempo | Mes | Servicios periódicos |
| Servicio | Tiempo | Año | Proyección anual |
| Servicio | Proporción | Porcentaje | Proporción, fracción o razón de una cantidad total dividida en 100 partes iguales |
| Mixta | Compuesta | Usuario / mes | Demanda periódica |
| Mixta | Compuesta | Atención / día | Capacidad operativa |
| Mixta | Compuesta | Servicio / año | Proyección anual |
| Mixta | Compuesta | Tonelada / mes | Flujo de bienes |
| Mixta | Compuesta | m² / año | Uso de infraestructura |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Autocompletado de la descripción del proyecto | CU-PRE-01 "Registro de Proyectos" (o última O.T. emitida, si existe) | Campo "Descripción del proyecto" (RN03) |
| Autocompletado de filas de productos | CU-PRE-09 "Análisis de Mercado" | Tabla "Descripción Técnica" (RN04) |
| Guardado de información | Botón "Guardar" | Mensaje emergente "¡Guardado!" (Anexo A.2, FA-01) |
| Navegación a siguiente sección | Botón "Siguiente" | CU-PRE-12 "Localización" (FA-02) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| CU-PRE-01 "Registro de Proyectos" | Interno (dentro del mismo Sistema de Información de Inversión Pública) | Fuente de la descripción general del proyecto autocompletada en el campo "Descripción del proyecto" (RN03). |
| CU-PRE-09 "Análisis de Mercado" | Interno (dentro del mismo Sistema de Información de Inversión Pública) | Fuente de los productos que se autocompletan en la tabla de Descripción Técnica (RN04). |

---

# Datos Pendientes de Definir

- Prioridad del caso de uso: no especificada en el documento.
- Disparador (evento que inicia el caso de uso): no especificado explícitamente en el documento.
- Tabla de excepciones: no desarrollada en el documento.
- Tabla de errores/códigos de error: no desarrollada en el documento.
- Texto exacto del mensaje de validación asociado al resaltado en rojo de campos pendientes (RN07): no especificado.
- Significado de la sigla "OT" / "O.T" mencionada en RN03: no definida en el documento.
- Contradicción no resuelta respecto a la editabilidad del campo "Producto" (Anexo B.1 vs. su propio texto de Detalle; ver sección "Observaciones").
- Columna "Capacidad de Producción" visible en el mockup del Anexo A.1 sin documentación en la tabla de campos del Anexo B.1 (tipo, formato, obligatoriedad, editabilidad no especificados).
- Discrepancia no resuelta entre el valor de componente "Ambiental" mostrado en el mockup y el Catálogo de componentes del proyecto (Anexo C.1), que no incluye dicho valor.