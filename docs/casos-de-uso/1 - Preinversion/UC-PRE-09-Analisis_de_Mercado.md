---
id: CU-PRE-09
codigo: CU-PRE-09
nombre: Análisis de Mercado
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.0"
fuente_pdf: CU-PRE-09_Análisis_de_Mercado_JUL_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 13

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5"]

casos_relacionados: ["CU-PRE-11", "CU-PRE-17", "CU-PRE-22.1"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Análisis de Mercado (Anexo A.1)", "Mensaje de Guardado (Anexo A.2)"]

procesos: ["Registro de datos de oferta, demanda y déficit por producto (FB, RN03, RN04)", "Cálculo automático de déficit y promedios proyectados (ver fórmulas en la sección Campos)"]

servicios_externos: []

entidades: ["Análisis de Mercado", "Producto", "Catálogo de Productos e Indicadores (externo, referido en RN07)", "Unidad de Medida", "Proyecto", "Unidad Ejecutora"]

catalogos: ["Catálogo de unidades de medida (Anexo C.1)", "Catálogo de Productos e Indicadores (referenciado en RN07, no incluido en este documento)"]

palabras_clave: ["Análisis de Mercado", "Oferta", "Demanda", "Déficit", "Técnico URP", "Preinversión", "Proyección"]

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
      nombre: Análisis de Mercado (Pantalla)
      pagina: 5
    A2:
      nombre: Guardar (Mensaje emergente)
      pagina: 6
    B1:
      nombre: Formatos (Pantalla "Análisis de Mercado")
      pagina: 6
    C1:
      nombre: Catálogo de unidades de medida
      pagina: 8
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Análisis de Mercado |
| Código | CU-PRE-09 |
| Módulo | Preinversión |
| Fuente | CU-PRE-09_Análisis_de_Mercado_JUL_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Producto
- Unidad de Medida
- Déficit año base (Demanda, Oferta y Déficit)
- Promedio Déficit de años proyectados (Años, Tasas, Promedio Demanda, Promedio Oferta, Promedio Déficit)

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

Este caso de uso permite al actor "Técnico URP" registrar en el sistema los datos provenientes del análisis de mercado realizado por la institución. En la pestaña se visualiza una tabla que contiene la información de oferta, demanda y déficit de cada bien o servicio adicionado para el año base y años proyectados, considerando la tendencia del consumo de los años anteriores.

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

---

# Flujo Principal

1. Técnico URP: Ingresa a la pestaña "Formulación del Proyecto" en la sección "Diagnóstico de la Situación Actual – Análisis de Mercado".
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
4. Sistema: Guarda la información registrada y se mantiene en la sección actual.

**Resultado**

> No especificado en el documento.

## FA-02 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Siguiente".
2. Sistema: Avanza a la siguiente sección CU-PRE-11 "Descripción Técnica" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|--------------|--------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento. El documento no incluye una tabla o listado explícito de excepciones para este caso de uso.

# Postcondiciones

1. El proyecto cuenta con los datos provenientes del análisis de mercado registrado.
2. CU-PRE-11 "Descripción técnica".
3. CU-PRE-17 "Presupuesto de inversión".
4. CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto".

> Nota de ambigüedad: el documento lista los ítems 2, 3 y 4 (CU-PRE-11, CU-PRE-17 y CU-PRE-22.1) inmediatamente después de la postcondición principal, sin un verbo explícito (p. ej. "habilita", "permite continuar con") que aclare la naturaleza exacta de su relación con este caso de uso. Se incorporan por continuidad de formato, pero la redacción exacta de dicha relación no está especificada.

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pestaña, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** No especificado en el documento.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** No especificado en el documento.

## RN03

**Descripción:** El Técnico URP podrá adicionar y/o eliminar más filas a la tabla de la sección "Análisis de Mercado" (ver Anexo A.1).

**Origen:** No especificado en el documento.

## RN04

**Descripción:** Mínimo debe haber una fila de registro de la información diligenciada completamente.

**Origen:** No especificado en el documento.

## RN05

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** No especificado en el documento.

## RN06

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** No especificado en el documento.

## RN07

**Descripción:** Los productos provendrán del catálogo de productos e indicadores ubicado en el CU-PRE-03.5 "Selección y registro de etapas".

**Origen:** No especificado en el documento.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Producto | Campo para seleccionar el tipo de bien o servicio que proporcionará el proyecto. Según columna "Producto" del "Catálogo de Productos e Indicadores". | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). El "Catálogo de Productos e Indicadores" referido no está incluido en este documento (ver RN07 y "Datos Pendientes de Definir"). |
| Unidad de medida | Aparece automáticamente una vez se selecciona el producto del catálogo de productos. | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Demanda | Campo para registrar el valor de la demanda (Rango: valores positivos sin límite de caracteres). | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Oferta | Campo para registrar el valor de la oferta (Rango: valores positivos sin límite de caracteres). | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Déficit | El sistema lo calcula con la siguiente fórmula: demanda – oferta = déficit. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado. |
| Años | Campo para registrar los años en que se desean proyectar la demanda y oferta del bien o servicio. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Tasa oferta | Campo para registrar manualmente la tasa de crecimiento de la oferta. Esta tasa está sujeta al bien o servicio a proporcionar. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Tasa demanda | Campo para registrar manualmente la tasa de crecimiento de la demanda. Esta tasa está sujeta al bien o servicio a proporcionar. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Promedio Demanda | El sistema lo calcula con la siguiente fórmula: Dp = (D₀ + Σ((D₀(1+r)¹) + ⋯ + (D₀(1+r)ⁿ))) / (t + 1). Donde: Dp = Promedio demanda; D₀ = Demanda año base (Campo "Cantidad" secc. demanda); r = Tasa de crecimiento (Campo "Tasa de crec." secc. demanda); n = Número del año (Conteo de 1 en 1 de los años a proyectar); t = Total de años (Campo "años a proyectar"). | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1), aunque el campo es descrito como calculado por el sistema; el documento no aclara esta aparente contradicción (ver "Observaciones"). |
| Promedio Oferta | El sistema lo calcula con la siguiente fórmula: OFp = (OF₀ + Σ((OF₀(1+r)¹) + ⋯ + (OF₀(1+r)ⁿ))) / (t + 1). Donde: OFp = Promedio oferta; OF₀ = Oferta año base (Campo "Cantidad" secc. oferta); r = Tasa de crecimiento (Campo "Tasa de crec." secc. oferta); n = Número del año (Conteo de 1 en 1 de los años a proyectar); t = Total de años (Campo "años a proyectar"). | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1), aunque el campo es descrito como calculado por el sistema; el documento no aclara esta aparente contradicción (ver "Observaciones"). |
| Promedio Déficit | El sistema lo calcula con la siguiente fórmula: Promedio demanda – Promedio oferta = Promedio déficit. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Demanda | Rango: valores positivos sin límite de caracteres (Anexo B.1). | No especificado en el documento. |
| Oferta | Rango: valores positivos sin límite de caracteres (Anexo B.1). | No especificado en el documento. |
| Campos pendientes de completar (no se especifica cuáles exactamente son obligatorios) | Al dar clic en "Guardar", si hay campos pendientes de completar, el sistema sombrea los bordes de dichos campos en color rojo (RN05). | No especificado en el documento. |
| Fila de registro | Mínimo debe haber una fila de registro de la información diligenciada completamente (RN04). | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pestaña "Análisis de Mercado". | RN01 |
| Técnico URP | Adicionar y/o eliminar filas de la tabla de la sección "Análisis de Mercado". | RN03 |
| Otros actores (no nombrados individualmente en el documento) | Visualizar la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-11 "Descripción técnica"
- CU-PRE-17 "Presupuesto de inversión"
- CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto"

**Procesos relacionados:** No especificado en el documento.

**Servicios externos:** No especificado en el documento.

---

# Pantallas

## Pantalla: Análisis de Mercado (Anexo A.1)

**Descripción:** Pantalla que permite al Técnico URP registrar, por cada producto (bien o servicio), los valores de demanda, oferta y déficit del año base, así como los años a proyectar, las tasas de crecimiento de demanda y oferta, y los promedios proyectados de demanda, oferta y déficit. La imagen incluye anotaciones que señalan un "Botón emergente para agregar más filas" (ubicado a la izquierda de la tabla) y un "Botón emergente para eliminar filas" (ubicado a la derecha de la tabla, representado con un ícono "x").

**Campos:**
- Producto
- Unidad de Medida
- Demanda (Déficit año base)
- Oferta (Déficit año base)
- Déficit (Déficit año base)
- Años a Proyectar (Promedio Déficit de años proyectado)
- Tasa Demanda (Promedio Déficit de años proyectado)
- Tasa Oferta (Promedio Déficit de años proyectado)
- Promedio Demanda (Promedio Déficit de años proyectado)
- Promedio Oferta (Promedio Déficit de años proyectado)
- Promedio Déficit (Promedio Déficit de años proyectado)

**Botones:**
- "+" (botón emergente para agregar más filas, según RN03)
- "x" (botón emergente para eliminar filas, según RN03)
- "GUARDAR"
- "SIGUIENTE"

**Acciones:**
- Al seleccionar un "Producto": el campo "Unidad de Medida" se autocompleta (Anexo B.1).
- Al dar clic en "GUARDAR": ejecuta FA-01.
- Al dar clic en "SIGUIENTE": ejecuta FA-02.
- Al acercar el cursor a un punto definido de la tabla: aparece botón emergente para agregar fila.
- Al acercar el cursor a la fila: aparece botón emergente ("x") para eliminar fila.
- Al acercar el cursor a un campo: aparece ícono "?" con información de ayuda (RN06).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Producto | Unidad de Medida | Demanda (Déficit año base) | Oferta (Déficit año base) | Déficit (Déficit año base) | Años a Proyectar | Tasa Demanda | Tasa Oferta | Promedio Demanda | Promedio Oferta | Promedio Déficit |
|----------|-------------------|------------------------------|------------------------------|------------------------------|--------------------|----------------|---------------|---------------------|--------------------|---------------------|
| Muro de contención construido (selector desplegable) | ml | 500.00 | 200 | 300 | 5 | 0.15% | 0.15% | 603.25 | 241.90 | 361.35 |
| Red de alcantarillados sanitarios ampliada (selector desplegable) | ml | 1500 | 500 | 1000 | 5 | 0.15% | 0.15% | 1807.76 | 603.25 | 1204.51 |
| (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |
| (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |

## Pantalla: Mensaje de Guardado (Anexo A.2)

**Descripción:** Mensaje emergente de confirmación mostrado tras el guardado exitoso de la información (paso 1.2 de FA-01).

**Campos:** No aplica (mensaje emergente).

**Botones:**
- "Aceptar"

**Acciones:**
- Al dar clic en "Aceptar": cierra el mensaje emergente y el sistema guarda la información, manteniéndose en la sección actual (paso 1.4 de FA-01).

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

- El ejemplo de datos del mockup del Anexo A.1 muestra la unidad de medida "ml" para los productos "Muro de contención construido" y "Red de alcantarillados sanitarios ampliada". El Catálogo de unidades de medida (Anexo C.1) solo define "ml" como abreviatura de "Mililitro" (categoría Volumen, "Líquidos pequeños"), lo cual no coincide funcionalmente con el tipo de bien mostrado en el ejemplo (obras de infraestructura, para las cuales "ml" normalmente representaría "metro lineal"). El documento no aclara ni resuelve esta discrepancia entre el catálogo y el ejemplo de pantalla.
- La tabla de campos del Anexo B.1 marca "Promedio Demanda" y "Promedio Oferta" como "Editable: Sí", pero al mismo tiempo describe ambos campos como calculados por el sistema mediante fórmula ("El sistema lo calcula con la siguiente fórmula..."). El documento no aclara si estos campos permiten edición manual posterior al cálculo automático, o si la columna "Editable" fue diligenciada de forma inconsistente respecto a "Déficit" y "Promedio Déficit", que sí están marcados como "Editable: No".
- El RN07 hace referencia a un "catálogo de productos e indicadores ubicado en el CU-PRE-03.5", el cual no está incluido como anexo dentro de este documento (a diferencia del Catálogo de unidades de medida, que sí se presenta en el Anexo C.1).
- El Catálogo de unidades de medida (Anexo C.1) incluye la columna "Descripción / Uso común en estudio de mercado", que no forma parte de los campos de la pantalla "Análisis de Mercado" descritos en el Anexo B.1; se conserva íntegramente en la sección "Catálogos Detectados" de este documento por tratarse de un catálogo completo del anexo.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|--------------|-----------|
| Análisis de Mercado | Registro que asocia a un proyecto los valores de oferta, demanda y déficit (año base y proyectado) para cada producto (bien o servicio). | Registro/edición por el Técnico URP (RN01); Guardado de la información (FA-01, RN05); Mínimo una fila completa requerida (RN04). |
| Producto | Bien o servicio que proporcionará el proyecto, seleccionado del "Catálogo de Productos e Indicadores" (externo, referido en RN07). | Selección (Anexo B.1); Adición/Eliminación de filas asociadas (RN03). |
| Unidad de Medida | Unidad correspondiente al producto seleccionado, tomada del Catálogo de unidades de medida (Anexo C.1). | Autocompletado al seleccionar el Producto (Anexo B.1). |
| Proyecto | Proyecto de inversión pública al que se asocia el análisis de mercado. | Actualización de estado al completar el análisis de mercado (Postcondiciones). |
| Unidad Ejecutora | Entidad cuya información puede visualizarse por otros actores según credenciales. | Consulta/visualización (RN01, RN02). |

---

# Catálogos Detectados

## Catálogo de unidades de medida (Anexo C.1)

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
| Autocompletado de Unidad de Medida | Selección de Producto | Pantalla "Análisis de Mercado" (Anexo B.1) |
| Cálculo automático de Déficit | Ingreso/edición de Demanda y Oferta | Campo "Déficit" (Anexo B.1) |
| Cálculo automático de Promedio Demanda, Promedio Oferta y Promedio Déficit | Ingreso de Años, Tasa demanda, Tasa oferta | Campos "Promedio Demanda", "Promedio Oferta", "Promedio Déficit" (Anexo B.1) |
| Guardado de información | Botón "Guardar" | Mensaje emergente "¡Guardado!" (Anexo A.2, FA-01) |
| Navegación a siguiente sección | Botón "Siguiente" | CU-PRE-11 "Descripción Técnica" (FA-02) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| CU-PRE-03.5 "Selección y registro de etapas" (Catálogo de Productos e Indicadores) | Interno (dentro del mismo Sistema de Información de Inversión Pública) | Fuente del catálogo de productos desde el cual se selecciona el campo "Producto" (RN07). |

---

# Datos Pendientes de Definir

- Prioridad del caso de uso: no especificada en el documento.
- Disparador (evento que inicia el caso de uso): no especificado explícitamente en el documento.
- Tabla de excepciones: no desarrollada en el documento.
- Tabla de errores/códigos de error: no desarrollada en el documento.
- Texto exacto del mensaje de validación asociado al resaltado en rojo de campos pendientes (RN05): no especificado.
- Contenido del "Catálogo de Productos e Indicadores" referido en RN07 y en el campo "Producto": no está incluido en este documento, lo que impide conocer los valores concretos disponibles para el campo "Producto".
- Contradicción no resuelta en el Anexo B.1 respecto a si "Promedio Demanda" y "Promedio Oferta" son editables manualmente o exclusivamente calculados por el sistema (ver sección "Observaciones").
- Ambigüedad no resuelta en Postcondiciones respecto a la relación exacta de CU-PRE-11, CU-PRE-17 y CU-PRE-22.1 con este caso de uso (ver sección "Postcondiciones").