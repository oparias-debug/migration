---
id: CU-PRE-17
codigo: CU-PRE-17
nombre: Presupuesto de Inversión
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.1"
fuente_pdf: CU-PRE-17_Presupuesto_de_inversión_AGO_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 20

nota_version: >
  La versión 1.1 incorpora el contenido íntegro del archivo Excel anexo
  `CU-PRE-17 (ANEXO) Catálogos_Financ_Recur_Conv.xlsx`, aportado
  posteriormente y externo al PDF original. La hoja "F Finan" ya había sido
  incorporada en una ronda anterior (RQ-T-02) como catálogo "Fuentes de
  Financiamiento". Esta versión añade las hojas "F Recursos" (191 filas,
  catálogo "Fuentes de Recursos") y "Convenios" (392 filas, catálogo de
  Convenios de Financiamiento), no transcritas anteriormente. No se
  modificó ningún otro contenido ya corregido en la versión 1.0.

actor_principal: Técnico URP

actores_secundarios: [Técnico PRE, Coordinador SYMP, Coordinador PRO, Técnico SYMP, Técnico PRO, Usuarios Internos]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-09", "CU-PRE-11", "CU-PRE-14", "CU-PRE-15", "CU-PRE-16"]

casos_relacionados: ["CU-PRE-21", "CU-PRO-01", "CU-PRE-03.5"]

roles: [Técnico URP, Técnico PRE, Coordinador SYMP, Coordinador PRO, Técnico SYMP, Técnico PRO]

pantallas: ["Presupuesto del Proyecto (Anexo A.1)", "Detalle de Macroactividad (Anexo A.2)", "Guardar (Anexo A.3)", "Resumen de los componentes del proyecto (Anexo A.4)", "Fuente de financiamiento y de recursos (Anexo A.5)"]

procesos: ["Generación dinámica de columnas de período según cantidad registrada (RN06, paso 4 del FB)", "Cálculo automático de costos de macroactividad, producto e inversión estimada a precios de mercado y ajustados (RN03, RN07, RN10, RN12)", "Cálculo automático del resumen de presupuesto por componente (RN09)", "Actualización del campo 'Costo etapa' en CU-PRE-03.5 al guardar (RN15)"]

servicios_externos: []

entidades: ["Presupuesto del Proyecto", "Producto", "Macroactividad", "Insumo", "Componente", "Fuente de Financiamiento", "Fuente de Recursos", "Proyecto", "Unidad Ejecutora"]

catalogos: ["Catálogo 'Insumo Tipo' (con Factor de Corrección)", "Catálogo 'Componentes del proyecto'", "Catálogo 'Fuentes de Financiamiento' (catálogo cerrado de 7 valores, confirmado por resolución de negocio RQ-T-02)", "Catálogo 'Fuentes de Recursos' (191 valores, anexo Excel externo al PDF original: CU-PRE-17 (ANEXO) Catálogos_Financ_Recur_Conv.xlsx, hoja 'F Recursos')", "Catálogo 'Convenios' (392 valores, mismo anexo Excel, hoja 'Convenios')"]

palabras_clave: ["Presupuesto de Inversión", "Macroactividad", "Insumo", "Factor de Corrección", "Precios Ajustados", "Precios de Mercado", "Fuente de Financiamiento", "Fuente de Recursos", "Convenios", "Técnico URP", "Preinversión"]

ultima_actualizacion: AGO 2025 (versión 1.0); incorporación de "Fuentes de Financiamiento" (RQ-T-02) e incorporación íntegra de "Fuentes de Recursos" y "Convenios" (anexo Excel) aplicadas AGO 2026

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 4
  flujos_alternos:
    F1:
      pagina: 4
    FA01.1:
      pagina: 5
    FA01.2:
      pagina: 5
    FA02:
      pagina: 5
    FA03:
      pagina: 5
  reglas_negocio:
    RN01:
      pagina: 5
    RN02:
      pagina: 5
    RN03:
      pagina: 6
    RN04:
      pagina: 6
    RN05:
      pagina: 6
    RN06:
      pagina: 6
    RN07:
      pagina: 6
    RN08:
      pagina: 6
    RN09:
      pagina: 6
    RN10:
      pagina: 6
    RN11:
      pagina: 6
    RN12:
      pagina: 6
    RN13:
      pagina: 7
    RN14:
      pagina: 7
    RN15:
      pagina: 7
    RN16:
      pagina: 7
  anexos:
    A1:
      nombre: Presupuesto del Proyecto (Pantalla)
      pagina: 8
    A2:
      nombre: Detalle de Macroactividad (Pantalla emergente)
      pagina: 9
    A3:
      nombre: Guardar (Mensaje emergente)
      pagina: 9
    A4:
      nombre: Resumen de los componentes del proyecto
      pagina: 10
    A5:
      nombre: Fuente de financiamiento y de recursos
      pagina: 10
    B1:
      nombre: Formatos (Pantallas "Presupuesto del Proyecto", "Detalle de Macroactividad", "Resumen Presupuesto por componente" y "Fuente de financiamiento y de recursos")
      pagina: 10
    C_Insumos:
      nombre: Catálogo "Insumo Tipo"
      pagina: 13
    C_Componentes:
      nombre: Catálogo "Componentes del proyecto"
      pagina: 13
    C_FuentesFinanciamiento:
      nombre: Catálogo "Fuentes de Financiamiento"
      pagina: "No aplica — anexo Excel externo al PDF (hoja 'F Finan'), ya resuelto en ronda anterior (RQ-T-02)."
    C_FuentesRecursos:
      nombre: Catálogo "Fuentes de Recursos"
      pagina: "No aplica — anexo Excel externo al PDF (hoja 'F Recursos'), no forma parte de la paginación del PDF original."
    C_Convenios:
      nombre: Catálogo "Convenios"
      pagina: "No aplica — anexo Excel externo al PDF (hoja 'Convenios'), no forma parte de la paginación del PDF original."
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Presupuesto de Inversión |
| Código | CU-PRE-17 |
| Módulo | Preinversión |
| Fuente | CU-PRE-17_Presupuesto_de_inversión_AGO_2025_V1_F.pdf; complementado con el anexo Excel `CU-PRE-17 (ANEXO) Catálogos_Financ_Recur_Conv.xlsx` (ver sección "Catálogos Detectados") |
| Versión | 1.1 |

**Campos requeridos (según el PDF):**

Pantalla "Presupuesto del Proyecto":
- N°
- Producto
- Macroactividad
- Costo del producto
- Costo de macroactividad
- Inversión estimada (Precios de Mercado)
- Inversión estimada (Precios Ajustados)
- Período (desde 0 hasta n)

Pantalla "Detalle Macroactividad":
- Nombre de la Macroactividad
- Insumo tipo
- FC
- Período (desde 0 hasta n)
- Total Período (Precio de Mercado)
- Total Período (Precio Ajustado)

Sección Resumen presupuesto por componente:
- Componente
- Costo (US$)
- Total

Sección Fuente de financiamiento y recursos:
- Fuente de financiamiento
- Fuente de recursos

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

Este caso de uso permite al actor "Técnico URP" registrar en el sistema el presupuesto del Proyecto. Contará con dos pantallas principales, la primera muestra el total del presupuesto a través de los productos y macroactividades que lo conforman y la segunda (una pantalla emergente), que permite registrar el costo de cada macroactividad por medio de los insumos, mostrándolos en un flujo según los períodos de ejecución.

Asimismo, en este caso de uso se visualizará una tabla que resumirá el total de inversión desagregado en los componentes del proyecto; y también permitirá la selección de las fuentes estimadas de financiamiento y de recursos.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico PRE (según RN02, puede visualizar la información de todas las Unidades Ejecutoras).
- "Todos los demás actores" (según RN01, únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales). El documento no nombra individualmente a estos otros actores.
- **Coordinador SYMP, Coordinador PRO, Técnico SYMP, Técnico PRO** (según RN10 y RN11, son los únicos con visibilidad de la tabla y de la fila de totales a precios ajustados).
>
> ⚠️ **CORREGIDO con conflicto sin resolver (anotación del especialista de dominio):** el especialista indicó que "Actores Internos" se refiere en realidad a estos cuatro roles: Coordinador SYMP, Coordinador PRO, Técnico SYMP y Técnico PRO. Esta corrección **contradice directamente** la resolución de negocio previa **RQ-C-03 (ronda 3)**, que había definido "usuarios internos" / "actores internos" / "usuarios centrales" como sinónimos de un grupo transversal: *cualquier usuario que pertenezca al Ministerio de Hacienda, independientemente de su rol* — explícitamente **no** un rol o conjunto de roles fijo. Se aplicó la anotación del especialista (fuente más reciente y específica) reemplazando las menciones literales de "actores internos" en RN10, RN11, la tabla de Campos y la tabla de Permisos por estos cuatro roles. **No se tocó** la mención de "usuarios internos" (RN10) ni "usuarios centrales", ya que la anotación del especialista solo se refería explícitamente a "Actores Internos"; queda sin resolver si esos términos deben actualizarse de la misma forma o si en realidad designan un concepto distinto. **Se requiere que alguien confirme con el negocio si la resolución RQ-C-03 y la entrada correspondiente del glosario compartido deben actualizarse**, ya que tal como queda este documento, "actores internos" (estos 4 roles) y "usuarios internos"/"usuarios centrales" (el grupo transversal del Ministerio de Hacienda) parecerían ser conceptos distintos, contradiciendo la premisa de sinonimia establecida en RQ-C-03. Ver también Observaciones y Datos Pendientes de Definir.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP de CU-PRE-01 "Registro de Proyectos".
2. Contar con la Ruta de Preinversión generada en CU-PRE-03.5 "Selección y registro de etapas".
3. CU-PRE-14 "Análisis ambiental".
4. CU-PRE-15 "Análisis de riesgos".
5. CU-PRE-16 "Análisis legal".
6. CU-PRE-11 "Descripción Técnica".
7. CU-PRE-09 "Análisis de mercado".

> Nota de ambigüedad: el documento redacta esta precondición como una única oración: "Contar con CUP de CU-PRE-01 'Registro de Proyectos' con la Ruta de Preinversión generada en (CU-PRE-03.5 'Selección y registro de etapas', CU-PRE-14 'Análisis ambiental', CU-PRE-15 'Análisis de riesgos', CU-PRE-16 'Análisis legal', CU-PRE-11 'Descripción Técnica' y CU-PRE-09 'Análisis de mercado'." El verbo "generada en" corresponde naturalmente solo a CU-PRE-03.5 (que efectivamente genera la Ruta de Preinversión); no queda claro si los demás casos de uso listados (CU-PRE-14, CU-PRE-15, CU-PRE-16, CU-PRE-11 y CU-PRE-09) son precondiciones independientes de "haber completado" dichos casos de uso, o si el documento los agrupa erróneamente bajo el mismo verbo "generada en". Se numeran como precondiciones separadas por continuidad de formato, pero la naturaleza exacta de la condición para cada uno no está especificada con un verbo propio.

---

# Flujo Principal

1. Técnico URP: Ingresa a la pantalla "Formulación del Proyecto" en la sección "Presupuesto del Proyecto".
2. Sistema: Muestra la pantalla Presupuesto del proyecto. En la columna "Producto" ya se encontrarán cargados los productos registrados en el CU-PRE-11 "Descripción técnica".
3. Técnico URP: Agrega la cantidad de períodos estimados para la ejecución en el campo "Períodos estimados para la ejecución" y da clic en el botón "Aceptar".
4. Sistema: Agrega una cantidad de columnas según el dato registrado en el paso anterior; la primera de estas columnas deberá tener como título "PERÍODO 0", y las siguientes serán numeradas consecutivamente.
5. Técnico URP: Da clic en el botón "Agregar Macroactividad" que se muestra junto a cada producto.
6. Sistema: Muestra la pantalla del Anexo A.2 "Detalle de Macroactividad" como una ventana emergente.

---

# Flujos Alternos

## F1 Detalle de Macroactividad

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Registra el nombre de la macroactividad y los costos de cada insumo utilizado en la macroactividad, por período según corresponda.
2. Sistema: Calcula el valor de la columna "Período 1 (US$) Precio Ajustados" para cada insumo y lo guarda en la tabla de presupuesto a precios ajustados, mediante la fórmula siguiente: Periodo 1 precios ajustados = Periodo 1 precios de mercado * FC.
3. Técnico URP: Da clic en el botón "Guardar" o en el botón "Salir".

**Resultado**

> No especificado en el documento.

## FA-01.1 Guardar (en Anexo A.2, del Flujo Detalle de Macroactividad)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Guardar".
2. Sistema: Muestra mensaje emergente del Anexo A.3.
3. Técnico URP: Da clic en "Aceptar" a mensaje emergente.
4. Sistema: Guarda la información registrada y regresa a la pantalla "Presupuesto del proyecto". Traslada la información de cada campo según corresponda a la pantalla del Anexo A.1.

**Resultado**

> No especificado en el documento.

## FA-01.2 Salir (en Anexo A.2, del Flujo Detalle de Macroactividad)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Salir".
2. Sistema: Muestra el mensaje emergente "Los datos no serán guardados".
3. Técnico URP: Da clic en "Aceptar" a mensaje emergente.
4. Sistema: Regresa a la pantalla "Presupuesto del proyecto" sin guardar cualquier información que se haya registrado.

**Resultado**

> No especificado en el documento.

## FA-02 Guardar (en pantalla del Anexo A.1)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Guardar".
2. Sistema: Muestra mensaje emergente del Anexo A.3.
3. Técnico URP: Da clic en "Aceptar" a mensaje emergente.
4. Sistema: Guarda la información registrada y se mantiene en la pestaña "Presupuesto del proyecto".

**Resultado**

> No especificado en el documento.

## FA-03 Siguiente (en pantalla del Anexo A.1)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP: Da clic en el botón "Siguiente".
2. Sistema: Avanza a la siguiente pestaña para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|--------------|--------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento. El documento no incluye una tabla o listado explícito de excepciones para este caso de uso.

# Postcondiciones

1. CU-PRE-21 "Flujo de Caja y cálculo de Indicadores".
2. CU-PRO-01 "Elaboración / Actualización del Programa de Inversión Pública de Mediano Plazo - PRIPME".

> Nota de ambigüedad: a diferencia de otros documentos de la serie, este caso de uso no presenta un enunciado de postcondición principal (por ejemplo, "El proyecto cuenta con... y avanza a..."); en su lugar, la sección "Postcondiciones" del documento fuente lista directamente los dos códigos de caso de uso anteriores sin un verbo explícito que aclare su relación exacta con este caso de uso (p. ej. "habilita", "permite continuar con"). Se transcriben tal como aparecen, sin agregar una redacción de postcondición no verificable.

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pantalla, según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** No especificado en el documento.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** No especificado en el documento.

## RN03

**Descripción:** En la tabla "Presupuesto del Proyecto" el Sistema sumará de manera automática en el campo "INVERSIÓN ESTIMADA" los valores de cada macroactividad listada, total para el horizonte y para cada periodo.

**Origen:** No especificado en el documento.

## RN04

**Descripción:** En la tabla "Detalle de Macroactividad" el Sistema mostrará en el campo "Total" de cada período, la suma de los costos de cada insumo por período (ver Anexo A.2).

**Origen:** No especificado en el documento.

## RN05

**Descripción:** La tabla "Detalle de Macroactividad" mostrará por defecto en el campo "Insumo Tipo" todos los insumos listados según catálogo "Insumos".

**Origen:** No especificado en el documento.

## RN06

**Descripción:** La cantidad de columnas para los períodos que se registró en el campo "Períodos estimados para la ejecución" del Anexo A.1, también generará las columnas en la pantalla del Anexo A.2 "Detalle de Macroactividad"; los títulos de estas columnas seguirán el mismo proceso que el descrito en el paso 4 del Flujo Básico.

**Origen:** No especificado en el documento.

## RN07

**Descripción:** El Sistema trasladará de la pantalla "Detalle de Macroactividad" el total calculado de cada macroactividad por cada período (tanto "Total período precio de mercado" como "Total periodo precios ajustados") a los períodos correspondientes de la tabla "Presupuesto del proyecto"; y también debe sumar los valores de las columnas de cada año para calcular el total de cada macroactividad y de todas las filas para calcular el total de la inversión.

**Origen:** No especificado en el documento.

## RN08

**Descripción:** El Sistema asignará un número consecutivo a los productos (1 hasta n) y macroactividades (1.1 hasta n.n). La numeración de cada macroactividad debe estar correlacionada con el consecutivo del producto al cual pertenece.

**Origen:** No especificado en el documento.

## RN09

**Descripción:** El total de la tabla "Resumen presupuesto por componente" se genera automáticamente una vez diligenciada la tabla "Presupuesto del proyecto" (Anexo A.1).

**Origen:** No especificado en el documento.

## RN10

**Descripción:** El cálculo del presupuesto se deberá realizar tanto a precios de mercado como a precios ajustados. Para ello, las tablas de los Anexos A.1, A.2 y A.4, deberán calcularse tanto a precios de mercado como a precios ajustados, este último aplicando el Factor de Corrección (FC) a la tabla a precios de mercado. La tabla de presupuesto a precios ajustados sólo podrá ser visible para los usuarios internos.

> Nota (RQ-C-03, ronda 3): "usuarios internos" es sinónimo de "actores internos" (RN11) y de "usuarios centrales" — grupo definido como cualquier usuario del Ministerio de Hacienda, independientemente de su rol. Ver nota completa de resolución en RN11.
>
> ⚠️ **Conflicto sin resolver (anotación del especialista de dominio, ver RN11):** el especialista redefinió "actores internos" como los roles Coordinador SYMP, Coordinador PRO, Técnico SYMP y Técnico PRO, lo cual contradice la premisa de sinonimia de esta nota. Esta regla (RN10) usa literalmente "usuarios internos", término que el especialista no mencionó explícitamente; no se modificó el texto de esta regla ni el término "usuarios internos" en ningún otro punto del documento. Ver la nota completa del conflicto en RN11 y en Actores Secundarios.

**Origen:** No especificado en el documento.

## RN11

**Descripción:** El Sistema calculará la suma de la fila "Precios Ajustados" de todas las macroactividades registradas para cada periodo en la pantalla "Detalle de Macroactividad". Dicha suma se mostrará en la fila "Total período Precios Ajustados". Sin embargo, esta fila sólo podrá ser visible para los actores internos (Coordinador SYMP, Coordinador PRO, Técnico SYMP, Técnico PRO).

> ✅ RESUELTO (RQ-C-03, ronda 3) — histórico, ver conflicto abajo: la aparente contradicción terminológica entre "usuarios internos" (RN10) y "actores internos" (RN11) queda resuelta — son sinónimos. El negocio confirmó que "usuarios internos" / "actores internos" / "usuarios centrales" corresponden a un solo grupo: cualquier usuario que se disponga dentro del Ministerio de Hacienda, independientemente de su rol. Adicionalmente, el negocio confirmó que CEPA, CEL, ANDA, INDES e ISTU tienen visibilidad equivalente en CU-PRE-21 porque son instituciones que proyectan ingresos (no beneficios) y deben reportarlos para construir el flujo financiero — no porque pertenezcan al Ministerio de Hacienda. Ver entrada de glosario compartido correspondiente. El control de visibilidad de precios ajustados definido en RN10 y RN11 ya puede implementarse conforme a esta definición.
>
> ⚠️ **CORREGIDO con conflicto sin resolver (anotación del especialista de dominio):** el especialista indicó que "actores internos" se refiere en realidad a los roles Coordinador SYMP, Coordinador PRO, Técnico SYMP y Técnico PRO — no a "cualquier usuario del Ministerio de Hacienda" como estableció la resolución RQ-C-03 citada arriba. Se aplicó la corrección del especialista por ser la fuente más reciente y específica, pero esto **contradice directamente** RQ-C-03 y no se eliminó la nota de resolución original (se conserva arriba con fines de trazabilidad). **No se modificó** el término "usuarios internos" (RN10) ni "usuarios centrales", ya que la anotación del especialista se limitó explícitamente a "Actores Internos"; queda pendiente que el negocio confirme si RQ-C-03, el glosario compartido y estos otros dos términos sinónimos deben actualizarse también, o si en realidad se trata de conceptos distintos que fueron fusionados por error en la resolución original.

**Origen:** No especificado en el documento.

## RN12

**Descripción:** El Sistema deberá redondear las decenas de los montos registrados en las casillas "INVERSIÓN ESTIMADA (PRECIOS DE MERCADO)" y "INVERSIÓN ESTIMADA (PRECIOS AJUSTADOS)" del Anexo A.1. Deberá redondear siempre por arriba y a múltiplos de 5 o de 10. Por ejemplo, si el valor es de $1,750,427.58 lo redondeará a $1,750,430.00; y si el valor es de $1,750,423.58 lo redondeará a $1,750,425.00.

**Origen:** No especificado en el documento.

## RN13

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** No especificado en el documento.

## RN14

**Descripción:** El CU contará con la sección Fuente de financiamiento y de Recursos (Anexo A.5) en las que se podrá seleccionar la Fuente de Financiamiento y la Fuente de Recursos estimada del proyecto.

**Origen:** No especificado en el documento.

## RN15

**Descripción:** Una vez se hace clic en "Guardar" del Anexo A.1, el valor total de inversión a precios de mercado se actualizará en el campo "Costo etapa" de la fila "Ejecución" del Anexo A.1 del CU-PRE-03.5 "Selección y registro de etapas".

**Origen:** No especificado en el documento.

## RN16

**Descripción:** Los productos de la pantalla del Anexo A.1 vienen de CU-PRE-11 "Descripción técnica" y no podrán adicionarse o eliminarse. Si se requiere algún cambio, deberá hacerse en dicho caso de uso.

**Origen:** No especificado en el documento.

---

# Campos

**Pantalla "Presupuesto del Proyecto" (Anexo A.1):**

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Períodos estimados para la ejecución | Campo que permite al Técnico URP el registro de la cantidad de períodos estimados para la ejecución del proyecto. Permitirá la generación de las columnas necesarias según corresponda para cada período. | Numérico | Numérico | Sí | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Producto | Campo que muestra los productos con que cuenta el proyecto. Procede del campo "Producto" del Anexo A.1 del CU-PRE-11 "Descripción técnica". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); no podrá adicionarse ni eliminarse (RN16). |
| Macroactividad | Campo que muestra la macroactividad registrada desde el Anexo A.2. Será obligatorio que cada producto cuente con al menos una macroactividad. | Texto | Texto | Sí (al menos una macroactividad por producto) | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Costo de macroactividad | Campo que muestra el valor del costo por cada actividad. Este valor resulta de la suma del costo total de los insumos de cada actividad registrados en pantalla "Detalle de Actividad", según RN07. En US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado. El Anexo B.1 se refiere a esta pantalla como "Detalle de Actividad", mientras que en el resto del documento se le denomina "Detalle de Macroactividad" (ver "Observaciones"). |
| Costo del producto | Campo que muestra el costo total de cada producto. El Sistema deberá obtener este valor a partir de la suma de los Costos de cada Macroactividad comprendida dentro de cada producto; este valor debe ser total y por periodo. En US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado. |
| Período (0 hasta n) | Campo que muestra los totales por período (desde 0 hasta n) de cada Macroactividad. Este valor procede del campo "Total período (precios de mercado)" de cada macroactividad registrada en la pantalla del Anexo A.2. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado. |
| Inversión estimada (Precios de mercado) | El Sistema realizará de forma automática la suma de los valores de todas las columnas en US$. El sistema deberá agregar el separador de miles (,). Sujeto a redondeo según RN12: Donde: se redondea siempre por arriba y a múltiplos de 5 o de 10 (por ejemplo, $1,750,427.58 se redondea a $1,750,430.00; $1,750,423.58 se redondea a $1,750,425.00). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado; actualiza el campo "Costo etapa" de CU-PRE-03.5 al guardar (RN15). |
| Inversión estimada (Precios ajustados) | Campo que muestra el total y por período a precios ajustados que proviene de los totales de la pantalla "Detalle de Actividad". Según RN12, Donde: se redondea siempre por arriba y a múltiplos de 5 o de 10 (mismo criterio que "Inversión estimada (Precios de mercado)"). El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado; visible solo para usuarios internos (RN10). |

**Pantalla "Detalle de Macroactividad" (Anexo A.2):**

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Nombre de la macroactividad | Campo que permite al técnico URP el registro del nombre de la macroactividad. | Texto | Texto | Sí | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Insumo Tipo | Campo que muestra los tipos de insumo. El Sistema mostrará por defecto todos los insumos según el catálogo "Insumos" (RN05). Cada insumo tiene asociado un factor de corrección, el cual se encuentra en el catálogo. | Texto | Texto | No especificado en el documento. | Todos los insumos del catálogo "Insumos" (RN05). | Editable: No (según Anexo B.1). |
| FC | Campo que muestra el factor de corrección de cada insumo tipo según el catálogo "Insumos". | Número | Número | No especificado en el documento. | Según catálogo "Insumo Tipo" (ver "Catálogos Detectados"). | Editable: No (según Anexo B.1). |
| Período "n" | Campo para registrar los costos de cada insumo por período. En US$. El sistema deberá agregar el separador de miles (,). Campo obligatorio. Al menos se debe registrar información en un campo de una fila. | Moneda | Moneda | Sí (al menos un campo de una fila diligenciado) | No especificado en el documento. | Editable: Sí (según Anexo B.1). Fórmula relacionada (F1, paso 1.2): Periodo 1 precios ajustados = Periodo 1 precios de mercado * FC. |
| Total Período (Precio de Mercado) | Campo que muestra el total por período. El Sistema realizará de forma automática la suma de los valores de la columna correspondiente. En US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado (ver también RN04). |
| Total Período (Precio Ajustado) | Campo que muestra el total por período de los costos de los diferentes Insumos, afectados por el FC. Para la obtención de este Total el sistema realizará la suma de cada insumo afectado en cada uno de los periodos por el FC correspondiente así: Total= (Insumo 1*FC)+(Insumo 2*FC)+…+Insumos*FC. En US$. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado; visible solo para actores internos — Coordinador SYMP, Coordinador PRO, Técnico SYMP, Técnico PRO (RN11; ver conflicto con RQ-C-03 en RN11). |

**Pantalla "Resumen Presupuesto por componente" (Anexo A.4):**

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Componente | Muestra el listado de componentes con que se clasificaron cada uno de los productos en el CU-PRE-11 "Descripción técnica". Por ejemplo, si en el CU-PRE-11 "Descripción técnica" se registraron 4 productos y se clasificaron así: 2 productos con "INFRAESTRUCTURA", 1 producto con "EQUIPAMIENTO" y 1 producto con "CAPACITACIONES", la tabla Resumen presupuesto por componentes mostrará listados Infraestructura, Equipamiento y Capacitaciones. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Costo | Mostrará los totales de cada componente. El valor de este campo será totalizado por el Sistema a partir de los totales por producto y el clasificador de componente de cada producto; por ejemplo, si existen dos productos cuyo clasificador sea INFRAESTRUCTURA, el Sistema sumará los totales de estos productos y lo mostrará en la fila correspondiente a INFRAESTRUCTURA de esta tabla. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado. |
| Total | Suma de los montos de los costos de todos los componentes. El Sistema realizará el cálculo de manera automática. En US$. El sistema deberá agregar el separador de miles (,). El total de la tabla "Resumen presupuesto por componente" debe ser igual al de "Inversión estimada" de la tabla "Presupuesto del Proyecto" (Anexo A.1). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1); campo calculado; regla de consistencia cruzada con Anexo A.1 (ver campo "Total" y RN09). |

**Pantalla "Fuente de financiamiento y de recursos" (Anexo A.5):**

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Fuente de Financiamiento | Permite seleccionar la fuente de financiamiento. Según catálogo "Fuentes de Financiamiento" (catálogo cerrado de 7 valores, RQ-T-02; ver "Catálogos Detectados"). Se podrá seleccionar más de una fuente de financiamiento al dar clic en el botón emergente (+). | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1); catálogo ya resuelto (RQ-T-02). |
| Fuente de Recursos | Permite seleccionar la fuente de recursos. Según catálogo "Fuentes de Recursos" (CU-PRE-17 "Presupuesto de inversión" en Excel). | Selección | Selección | Sí | No especificado en el documento. | Editable: No (según Anexo B.1); catálogo referenciado aún no incluido, pendiente de decisión del Gestor del Dominio (ver "Datos Pendientes de Definir" y "Catálogos Detectados"). |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|--------------------|
| Períodos estimados para la ejecución | Campo de carácter obligatorio (Anexo B.1). | No especificado en el documento. |
| Macroactividad | Será obligatorio que cada producto cuente con al menos una macroactividad (Anexo B.1). | No especificado en el documento. |
| Nombre de la macroactividad | Campo obligatorio (Anexo B.1). | No especificado en el documento. |
| Período "n" (Detalle de Macroactividad) | Campo obligatorio; al menos se debe registrar información en un campo de una fila (Anexo B.1). | No especificado en el documento. |
| Fuente de Financiamiento | Campo obligatorio (Anexo B.1). | No especificado en el documento. |
| Fuente de Recursos | Campo obligatorio (Anexo B.1). | No especificado en el documento. |
| Inversión estimada (Precios de mercado / Precios ajustados) | Redondeo obligatorio por arriba, a múltiplos de 5 o de 10 (RN12). | No especificado en el documento. |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pantalla "Presupuesto del Proyecto" y en "Detalle de Macroactividad". | RN01 |
| Otros actores (no nombrados individualmente en el documento) | Visualizar la información de las Unidades Ejecutoras, según credenciales. | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |
| Usuarios internos (definido por RQ-C-03: cualquier usuario del Ministerio de Hacienda, independientemente de su rol) | Visualizar la tabla de presupuesto a precios ajustados. | RN10 |
| Actores internos — Coordinador SYMP, Coordinador PRO, Técnico SYMP, Técnico PRO (⚠️ redefinido por anotación del especialista; contradice la definición de RQ-C-03 usada para "usuarios internos" arriba — ver RN11 y Observaciones) | Visualizar la fila "Total período Precios Ajustados". | RN11 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos"
- CU-PRE-03.5 "Selección y registro de etapas"
- CU-PRE-09 "Análisis de mercado"
- CU-PRE-11 "Descripción técnica"
- CU-PRE-14 "Análisis ambiental"
- CU-PRE-15 "Análisis de riesgos"
- CU-PRE-16 "Análisis legal"
- CU-PRE-21 "Flujo de Caja y cálculo de Indicadores"
- CU-PRO-01 "Elaboración / Actualización del Programa de Inversión Pública de Mediano Plazo - PRIPME"

**Procesos relacionados:** No especificado en el documento.

**Servicios externos:** No especificado en el documento.

---

# Pantallas

## Pantalla: Presupuesto del Proyecto (Anexo A.1)

**Descripción:** Pantalla principal que muestra el listado de productos del proyecto (procedentes de CU-PRE-11), sus macroactividades, los costos por macroactividad y por producto, los totales por período y la inversión estimada total a precios de mercado y ajustados.

**Campos:**
- Períodos estimados para la ejecución
- N° (Producto)
- Producto
- N° (Macroactividad)
- Macroactividad
- Costo del producto
- Costo de macroactividad
- Período 0, Período 1, ..., Período n
- Inversión estimada (Precios de Mercado)
- Inversión estimada (Precios Ajustados)

**Botones:**
- "ACEPTAR" (junto al campo "Períodos estimados para la ejecución")
- "Agregar Macroactividad" (uno por cada producto)
- "GUARDAR"
- "SIGUIENTE"

**Acciones:**
- Al ingresar la cantidad de períodos y dar clic en "ACEPTAR": el sistema genera las columnas de período correspondientes (paso 4 del FB, RN06).
- Al dar clic en "Agregar Macroactividad": se abre la pantalla emergente del Anexo A.2 "Detalle de Macroactividad" (paso 6 del FB).
- Al dar clic en "GUARDAR": ejecuta FA-02 (incluye actualización del campo "Costo etapa" en CU-PRE-03.5, RN15).
- Al dar clic en "SIGUIENTE": ejecuta FA-03.
- El sistema calcula automáticamente los totales por macroactividad, producto e inversión estimada (RN03, RN07, RN08, RN12).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

Campo lateral: "Períodos estimados para la ejecución: 3.00" con botón "ACEPTAR".

Tabla "Presupuesto del proyecto":

| N° (Producto) | Producto | N° (Macroactividad) | Macroactividad | Costo del Producto | Costo de Macroactividad | Período 0 | Período 1 | Período 2 |
|-----------------|----------|-------------------------|------------------|-------------------------|------------------------------|--------------|--------------|--------------|
| — | — | — | **Total del producto por período** | | | $ 10,700.00 | $ 18,600.00 | $ 19,700.00 |
| 1 | Vía construida (con botón "Agregar Macroactividad") | 1.1 | Obras preliminares | $ 49,000.00 | $ 3,300.00 | $ 2,000.00 | $ 600.00 | $ 700.00 |
| | | 1.2 | Terracería | | $ 10,100.00 | $ 5,000.00 | $ 5,000.00 | $ 100.00 |
| | | 1.3 | Fundaciones | | $ 15,000.00 | $ 2,500.00 | $ 5,000.00 | $ 7,500.00 |
| | | 1.4 | Estructuras | | $ 15,000.00 | $ - | $ 5,000.00 | $ 10,000.00 |
| | | 1.5 | Supervisión de obra | | $ 5,600.00 | $ 1,200.00 | $ 3,000.00 | $ 1,400.00 |
| — | — | — | **Total del producto por período** | | | (vacío) | (vacío) | (vacío) |
| 2 | Equipo de video vigilancia instalado (con botón "Agregar Macroactividad") | 2.1 | Adquisición e instalación de equipos | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |
| | | 2.2 | Capacitación a personal sobre el uso de equipos | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |
| — | — | — | **Total del producto por período** | | | (vacío) | (vacío) | (vacío) |
| 3 | Habitantes de la zona capacitados (con botón "Agregar Macroactividad") | 3.1 | Capacitación sobre cuido de equipos | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |
| | | 3.2 | Capacitación seguridad vial | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |
| | | 3.3 | Capacitación primeros auxilios | (vacío) | (vacío) | (vacío) | (vacío) | (vacío) |
| **INVERSIÓN ESTIMADA (Precios de Mercado)** | | | | $ 49,000.00 | $ 49,000.00 | $ 10,700.00 | $ 18,600.00 | $ 19,700.00 |
| **INVERSIÓN ESTIMADA (Precios Ajustados)** | | | | $ 49,000.00 | $ 49,000.00 | $ 10,700.00 | $ 18,600.00 | $ 19,700.00 |

## Pantalla: Detalle de Macroactividad (Anexo A.2)

**Descripción:** Pantalla emergente que permite al Técnico URP registrar el nombre de una macroactividad y los costos de cada insumo utilizado en ella, por período, calculando automáticamente los totales a precios de mercado y a precios ajustados.

**Campos:**
- Nombre de la Macroactividad
- Insumo Tipo
- F.C. (Factor de Corrección)
- Período 0, Período 1, ..., Período n (Costos)
- Total Período (Precios Mercado)
- Total Período (Precios Ajustados)

**Botones:**
- "GUARDAR"
- "SALIR"

**Acciones:**
- Al registrar costos por insumo y período: el sistema calcula automáticamente "Total Período (Precios Mercado)" y "Total Período (Precios Ajustados)" (RN04, F1 paso 1.2).
- Al dar clic en "GUARDAR": ejecuta FA-01.1.
- Al dar clic en "SALIR": ejecuta FA-01.2.

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**

Nombre de la Macroactividad: **Obras preliminares**

Tabla "COSTOS":

| Insumo Tipo | F.C. | Período 0 | Período 1 | Período 2 |
|-------------|------|--------------|--------------|--------------|
| Mano de obra calificada | 1.00 | $ 500.00 | $ 100.00 | (vacío) |
| Mano de obra semi calificada | 1.00 | $ 100.00 | (vacío) | (vacío) |
| Mano de obra no calificada | 1.00 | $ 800.00 | $ 500.00 | (vacío) |
| Bienes importados | 1.00 | $ 100.00 | (vacío) | $ 700.00 |
| Bienes nacionales | 1.00 | $ 500.00 | (vacío) | (vacío) |
| Combustibles/Energías | (vacío) | (vacío) | (vacío) | (vacío) |
| Servicios | (vacío) | (vacío) | (vacío) | (vacío) |
| Otros | (vacío) | (vacío) | (vacío) | (vacío) |
| **TOTAL PERIODO (PRECIOS MERCADO)** | | **$ 2,000.00** | **$ 600.00** | **$ 700.00** ($ 3,300.00 total) |
| **TOTAL PERIODO (PRECIOS AJUSTADOS)** | | **$ 2,000.00** | **$ 600.00** | **$ 700.00** ($ 3,300.00 total) |

> Nota: el valor "$ 3,300.00" corresponde a la columna de total general mostrada al final de la fila en el mockup, junto a los totales por período.

## Pantalla: Guardar (Anexo A.3)

**Descripción:** Mensaje emergente de confirmación mostrado tras el guardado exitoso de la información (paso 1.1.2 de FA-01.1 y paso 2.2 de FA-02).

**Campos:** No aplica (mensaje emergente).

**Botones:**
- "Aceptar"

**Acciones:**
- Al dar clic en "Aceptar": cierra el mensaje emergente (paso 1.1.4 de FA-01.1 o paso 2.4 de FA-02, según corresponda).

**Ejemplo de datos mostrados en el mockup (Anexo A.3):**

| Elemento | Contenido mostrado |
|----------|----------------------|
| Ícono | Ícono de verificación (check) en color verde |
| Título | ¡Guardado! |
| Mensaje | Sus datos han sido guardados exitosamente. |
| Botón | Aceptar |

## Pantalla: Resumen de los componentes del proyecto (Anexo A.4)

**Descripción:** Tabla que resume el total de inversión del proyecto desagregado por componente, calculada automáticamente a partir de la clasificación de componente de cada producto (definida en CU-PRE-11).

**Campos:**
- Componente
- Costo (US$)
- Total (calculado)

**Botones:** No especificado en el documento.

**Acciones:**
- El sistema genera el listado de componentes y sus totales automáticamente al diligenciarse la tabla "Presupuesto del proyecto" (RN09).

**Ejemplo de datos mostrados en el mockup (Anexo A.4):**

| Componente | Costo (US$) |
|------------|----------------|
| Infraestructura | $ - |
| Equipamiento | $ - |
| Capacitaciones | $ - |
| **TOTAL** | **$ -** |

## Pantalla: Fuente de financiamiento y de recursos (Anexo A.5)

**Descripción:** Sección que permite al Técnico URP seleccionar la Fuente de Financiamiento y la Fuente de Recursos estimada del proyecto, pudiendo seleccionar más de una fuente de financiamiento.

**Campos:**
- Fuente de Financiamiento
- Fuente de Recursos

**Botones:**
- "+" (botón emergente para adicionar más de una fuente de financiamiento)

**Acciones:**
- Al dar clic en "+": permite seleccionar una fuente de financiamiento adicional (Anexo B.1).

**Ejemplo de datos mostrados en el mockup (Anexo A.5):**

| Campo | Valor mostrado en el mockup |
|-------|--------------------------------|
| FUENTE DE FINANCIAMIENTO | Listado desplegable (vacío en el mockup) |
| FUENTE DE RECURSOS | Listado desplegable (vacío en el mockup) |

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Éxito | ¡Guardado! Sus datos han sido guardados exitosamente. | Al dar clic en "Guardar" en el Anexo A.1 (FA-02) o en el Anexo A.2 (FA-01.1), mostrando el mensaje emergente del Anexo A.3. |
| Advertencia | Los datos no serán guardados. | Al dar clic en el botón "Salir" en la pantalla "Detalle de Macroactividad" (FA-01.2, paso 1.2.2). |

---

# Observaciones

- La tabla de formatos del Anexo B.1, en los campos "Costo de macroactividad" e "Inversión estimada (Precios ajustados)", hace referencia a una pantalla llamada "Detalle de Actividad", mientras que en el resto del documento (Flujo Básico, Reglas de Negocio, títulos de Anexos) la pantalla se denomina consistentemente "Detalle de Macroactividad". El documento no aclara si "Detalle de Actividad" es un nombre alterno abreviado o una inconsistencia de redacción.
- El catálogo "Componentes del proyecto" transcrito en este documento (Infraestructura, Equipamiento, Capacitaciones, Administración, Consultorías, Terrenos, Otros) es idéntico al catálogo de componentes presentado en el documento CU-PRE-11 "Descripción Técnica" (Anexo C.1 de dicho documento). No se identifica discrepancia entre ambos catálogos.
- Los catálogos "Fuentes de Financiamiento" y "Fuentes de Recursos", referidos en el Anexo B.1 para los campos "Fuente de Financiamiento" y "Fuente de Recursos", se describen originalmente como contenidos en un archivo "CU-PRE-17 'Presupuesto de inversión' en Excel", externo al presente documento PDF. **Actualización (RQ-T-02, ronda 3):** el catálogo "Fuentes de Financiamiento" ya se transcribió y quedó resuelto como catálogo cerrado de 7 valores (ver "Catálogos Detectados"). **Actualización (v1.1):** el catálogo "Fuentes de Recursos" (191 valores) y el catálogo "Convenios" (392 valores) fueron aportados íntegramente en el anexo Excel y se transcribieron completos en "Catálogos Detectados". Este anexo Excel sigue sin formar parte del PDF original; se documenta como fuente complementaria externa. La decisión de negocio sobre cuál(es) de estos catálogos debe enlazar efectivamente el campo "Fuente de Recursos" del Anexo A.5 (191 valores de "F Recursos", 392 valores de "Convenios", o ambos como jerarquía) sigue pendiente del Gestor del Dominio.
- **Anexo Excel — catálogos externos no incluidos:** las columnas `CODIG_PAIS` y `CODIG_CLASE_RECUR` de la hoja "F Recursos", y `CODIG_TIPO_CONVE` de la hoja "Convenios", remiten a catálogos externos (países, clase de recurso, tipo de convenio) que no forman parte de este archivo ni del PDF original. Se transcribieron los códigos numéricos literales sin reconstruir dichos catálogos.
- **Anexo Excel — valores atípicos en "Convenios":** la columna `MONED_CONVE` incluye, además de códigos de moneda reconocibles, los valores atípicos "-" (fila "GOJA/2KR"), "MON" (filas "Modificar" y "N/A") e "IDB" (fila "BID-765/OC-ES"). Además, 3 filas usan como `CODIG_CONVE` un valor de control ("Modificar", "N/A", "S/N") en lugar de un identificador de convenio real. Se transcriben tal como aparecen en el archivo original, sin normalizar ni excluir ninguna fila.
- El ejemplo de datos del mockup del Anexo A.4 "Resumen de los componentes del proyecto" muestra los tres componentes con costo "$ -" (cero o vacío) y un "TOTAL" también en "$ -", pese a que el ejemplo del Anexo A.1 muestra una "INVERSIÓN ESTIMADA" de $49,000.00 total. El documento no aclara si ambos mockups corresponden al mismo escenario de ejemplo o a estados distintos de la pantalla (por ejemplo, antes y después de clasificar los productos por componente en CU-PRE-11).
- RN08 establece la numeración de macroactividades en formato "n.n" (por ejemplo, "1.1"); sin embargo, la fórmula del campo "Total Período (Precio Ajustado)" en el Anexo B.1 usa la notación "Total= (Insumo 1*FC)+(Insumo 2*FC)+…+Insumos*FC", que en su último término ("Insumos*FC") omite el índice numérico explícito presente en los términos anteriores ("Insumo 1", "Insumo 2"). Se transcribe literalmente tal como aparece en el documento fuente, sin corregir la notación.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|--------------|-----------|
| Presupuesto del Proyecto | Registro que asocia a un proyecto sus productos, macroactividades, costos por período e inversión estimada total, a precios de mercado y ajustados. | Registro/edición por el Técnico URP (RN01); Cálculo automático de totales (RN03, RN07, RN12); Guardado (FA-02); Actualización del "Costo etapa" en CU-PRE-03.5 (RN15). |
| Producto | Bien o servicio del proyecto, procedente de CU-PRE-11 "Descripción técnica", sobre el cual se agrupan las macroactividades. | Consulta/visualización (no editable en este CU, RN16); numeración consecutiva (RN08). |
| Macroactividad | Actividad de mayor nivel asociada a un producto, compuesta por insumos con costos por período. | Registro/edición mediante la pantalla "Detalle de Macroactividad" (F1); numeración consecutiva correlacionada al producto (RN08); Cálculo automático de totales (RN04, RN07). |
| Insumo | Tipo de recurso (mano de obra, bienes, servicios, etc.) utilizado en una macroactividad, con un Factor de Corrección asociado. | Consulta desde el catálogo "Insumos" (RN05); Registro de costos por período (Anexo B.1); Cálculo de precios ajustados mediante el FC (F1 paso 1.2, RN10, RN11). |
| Componente | Categoría de clasificación de los productos del proyecto (definida en CU-PRE-11), utilizada para agrupar los costos en el resumen de presupuesto. | Consulta/visualización; Cálculo automático de totales por componente (RN09). |
| Fuente de Financiamiento | Origen de los fondos estimados para el proyecto. | Selección, con posibilidad de seleccionar más de una (Anexo B.1, RN14). |
| Fuente de Recursos | Clasificación de recursos estimados del proyecto. | Selección (Anexo B.1, RN14). |
| Proyecto | Proyecto de inversión pública al que se asocia el presupuesto de inversión. | Actualización de estado y avance hacia CU-PRE-21 y CU-PRO-01 (Postcondiciones). |
| Unidad Ejecutora | Entidad cuya información puede visualizarse por otros actores según credenciales. | Consulta/visualización (RN01, RN02). |

---

# Catálogos Detectados

## Catálogo "Insumo Tipo"

| Insumos | Factor de corrección (FC) |
|---------|------------------------------|
| Mano de obra calificada | 1.00 |
| Mano de obra semi calificada | 1.00 |
| Mano de obra no calificada | 1.00 |
| Bienes importados | 1.00 |
| Bienes nacionales | 1.00 |
| Combustibles/Energía | 1.00 |
| Servicios | 1.00 |
| Otros | 1.00 |

> Nota importante (del documento fuente): Este catálogo estará sujeto a actualización por parte de la DGICP.

## Catálogo "Componentes del proyecto"

| Componente |
|------------|
| Infraestructura |
| Equipamiento |
| Capacitaciones |
| Administración |
| Consultorías |
| Terrenos |
| Otros |

> Nota importante (del documento fuente): Este catálogo estará sujeto a actualización por parte de la DGICP.

## Catálogo "Fuentes de Financiamiento"

> ✅ RESUELTO (RQ-T-02, ronda 3): catálogo cerrado confirmado a partir del anexo `CU-PRE-17 (ANEXO) Catálogos_Financ_Recur_Conv.xlsx` (hoja "F Finan").

| Código | Fuente de Financiamiento | Sigla SIIP |
|---|---|---|
| 0 | Sin Financiamiento | S/F |
| 1 | Fondo General | FGEN |
| 2 | Recursos Propios | RPRO |
| 3 | Préstamos Externos | P.Ext |
| 4 | Préstamos Internos | P.Int |
| 5 | Donaciones | Don. |
| 9 | Otros | Otros |

## Catálogo "Fuentes de Recursos"

> **Actualización (v1.1):** el archivo Excel `CU-PRE-17 (ANEXO) Catálogos_Financ_Recur_Conv.xlsx` fue aportado íntegramente (además de la hoja "F Finan", ya incorporada en la ronda anterior). Se transcriben a continuación las hojas "F Recursos" (191 filas) y "Convenios" (392 filas), completas y sin resumir. Este anexo sigue sin formar parte del PDF original (`CU-PRE-17_Presupuesto_de_inversión_AGO_2025_V1_F.pdf`); se documenta como fuente complementaria externa. La decisión sobre si el campo "Fuente de Recursos" de la pantalla (Anexo A.5) debe enlazar con este catálogo de 191 valores, con el catálogo de "Convenios", o con ambos como jerarquía de tres niveles (Fuente de Financiamiento → Fuente de Recursos → Convenio) **sigue sin estar confirmada por el negocio** y se documenta en "Datos Pendientes de Definir".

### Hoja "F Recursos"

> Nota: las columnas `CODIG_PAIS` y `CODIG_CLASE_RECUR` remiten a catálogos externos (país y clase de recurso, respectivamente) que **no están incluidos** en este archivo Excel ni en el PDF original; se transcriben los códigos numéricos tal como aparecen, sin reconstruir esos catálogos externos (ver "Datos Pendientes de Definir"). Se observan 4 códigos negativos o cero con significado especial: `-5` "Donación", `-4` "Préstamo Interno", `-3` "Préstamo Externo" y `0` "S/D" (sin dato), que agrupan conceptualmente por tipo de fuente antes de los códigos positivos correspondientes a instituciones/organismos específicos. `CODIG_CLASE_RECUR` solo toma los valores `1` y `2` en todo el catálogo (sin que el archivo defina qué representa cada uno).

| Código Fuente de Recursos (CODIG_FUENT_RECUR) | Nombre Fuente de Recursos (NOMBR_FUENT_RECUR) | Sigla (SIGLA_FUENT_RECUR) | Código País (CODIG_PAIS) | Código Clase de Recurso (CODIG_CLASE_RECUR) |
|---|---|---|---|---|
| -5 | Donación | Donación | 1 | 2 |
| -4 | Préstamo Interno | Préstamo Interno | 1 | 1 |
| -3 | Préstamo Externo | Préstamo Externo | 1 | 2 |
| 0 | S/D | S/D | 0 | 1 |
| 1 | AGENCIA PARA EL DESARROLLO INTERNACIONAL | AID | 21 | 2 |
| 2 | BANCO CENTROAMERICANO DE INTEGRACION ECONOMICA | BCIE | 17 | 2 |
| 3 | BANCO INTERAMERICANO DE DESARROLLO | BID | 17 | 2 |
| 4 | BANCO INTERNACIONAL DE RECONSTRUCCION Y FOMENTO | BIRF | 17 | 2 |
| 5 | BANCO VILBAO VIZCAYA S.A. (ESPAÑA) | BILVIZ | 8 | 2 |
| 6 | DUCADO DE LUXEMBURGO | DULUX | 16 | 2 |
| 7 | FONDO GENERAL | FGEN | 7 | 1 |
| 8 | RECURSOS PROPIOS SETEFE | SETEFE | 7 | 1 |
| 9 | RECURSOS PROPIOS | RPRO | 7 | 1 |
| 10 | JAPAN BANK FOR INTERNATIONAL CORPORATION | JBIC | 14 | 2 |
| 11 | FONDO SALVADOREÑO PARA ESTUDIOS DE PREINVERSION | FOSEP | 7 | 1 |
| 12 | GOBIERNO DE HOLANDA | GOHO | 10 | 2 |
| 13 | GOBIERNO DE JAPON | GOJA | 14 | 2 |
| 14 | GOBIERNO DE SUIZA | GOSUI | 19 | 2 |
| 15 | PL-480/93 | PL-480/93 | 17 | 1 |
| 16 | FONDO INTERNACIONAL DE DESARROLLO AGRICOLA | FIDA | 17 | 2 |
| 17 | UNION EUROPEA | UNE | 17 | 2 |
| 18 | KfW - BANCO ALEMAN DE FOMENTO Y RECONSTRUCCION | KFW | 1 | 2 |
| 19 | GOBIERNO DE FRANCIA/CREDIT NATIONAL | GOFRA | 9 | 2 |
| 20 | RECURSOS PROVENIENTES DE LA VENTA DE CEL | FGCEL | 7 | 1 |
| 21 | FONDO DE LAS NACIONES UNIDAS P/LA INFANCIA | UNICEF | 17 | 2 |
| 22 | COMUNIDAD | COMU | 7 | 1 |
| 23 | OTRAS FUENTES INTERNAS | OTRA-I | 7 | 1 |
| 24 | GOBIERNO DE ESPAÑA | GOESP | 8 | 2 |
| 25 | GOBIERNO DE CHINA | GOCHI | 24 | 2 |
| 26 | FONDO DE INVERSIONES DE VENEZUELA | FIV | 22 | 2 |
| 27 | GOBIERNO DE COREA | COREA | 15 | 2 |
| 28 | PROCHALATE | PROCH | 7 | 1 |
| 29 | JUNTA DE ANDALUCIA | JUAND | 8 | 2 |
| 30 | PROGRAMA DE LAS NACIONES UNIDAS PARA EL DESARROLLO | PNUD | 17 | 2 |
| 31 | GOBIERNO DE COLOMBIA | COLOM | 6 | 2 |
| 32 | GOBIERNO DE BRASIL | BRASIL | 3 | 2 |
| 33 | GOBIERNO DE NORUEGA | GONOR | 18 | 2 |
| 34 | ORGANIZACION PANAMERICANA DE SALUD | OPS | 17 | 2 |
| 35 | UNFPA | UNFPA | 17 | 2 |
| 36 | CONGRESO -USA- | CONUSA | 21 | 2 |
| 37 | FONDOS PROV.DE LA RECONST.NACIONAL | AID/ESF | 7 | 2 |
| 38 | FONDO DE MICROEMPRESAS BCR-BMI | FOMMI | 7 | 1 |
| 39 | ORGANISMO DE ESTADOS AMERICANOS | OEA | 17 | 2 |
| 40 | AGENCIA DE EE.UU. DEL COMERCIO Y DESARROLLO. | USTDA | 21 | 2 |
| 41 | COOPERATIVA AMERICANA DE AYUDA AL EXTERIOR | CARE | 21 | 2 |
| 42 | FUNDACION PARA LA SALUD | FUSAL | 7 | 2 |
| 43 | OTRAS FUENTES EXTERNAS | OTRA-E | 17 | 2 |
| 44 | FONDOS TAIWAN | TAIWAN | 20 | 2 |
| 45 | DEPARTAMENTO DE AGRICULTURA DE LOS ESTADOS UNIDOS | USDA | 21 | 2 |
| 46 | CAPITAL FANTEL | KFANTEL | 7 | 1 |
| 47 | INTERESES FANTEL | IFANTEL | 7 | 1 |
| 48 | Fondo de Inversión Nacional en Electricidad y Telefonía | FINET | 7 | 1 |
| 49 | AGENCIA DE LOS EEUU PARA EL DESARROLLO INTERNAC. | USAID | 21 | 2 |
| 61 | BONOS DE EMERGENCIA - RECONSTRUCCION | BONOS-RE | 7 | 1 |
| 64 | GOBIERNO DE ALEMANIA | GOALE | 1 | 2 |
| 65 | GOBIERNO DE ITALIA | GOITA | 13 | 2 |
| 66 | AGENCIA DE COOPERACION INTERNACIONAL DEL JAPAN | JICA | 14 | 2 |
| 70 | PL-480/2001 | PL-480/2001 | 17 | 2 |
| 71 | GOBIERNO DE PUERTO RICO | GOBPR | 23 | 2 |
| 75 | ORGANIZACION DE LAS NAC. UNIDAS PARA LA AGRIC. Y LA ALIME | FAO | 17 | 2 |
| 76 | FONDO DE DESARROLLO ECONOMICO Y SOCIAL | FODES | 7 | 1 |
| 78 | FONDO DE CONVERSION DE DEUDA FRANCO SALVADOREÑO | FCDFS | 7 | 2 |
| 79 | BONOS | BONOS | 7 | 1 |
| 80 | INSTITUTO SALVADOREÑO DE FORMACION PROFESIONAL | INSAFORP | 7 | 1 |
| 81 | COMISION NACIONAL DE LA MICRO Y PEQUEÑA EMPRESA | CONAMYPE | 7 | 1 |
| 82 | BENEFICIARIOS | BENEFICIARIO | 7 | 1 |
| 83 | FIDEICOMISO MAG/BFA/PRODAP | FIDEICOMISO BFA | 7 | 1 |
| 84 | DEUTSCHE BANK | DEUTSCHE | 1 | 2 |
| 85 | EXPORT-IMPORT BANK OF CHINA | EXPORT | 20 | 2 |
| 86 | FONDO INTERNACIONAL DE COOPERACION Y DESARROLLO DE LA REPUBLICA DE CHINA | ICDF | 20 | 2 |
| 87 | INSTITUTO DE CREDITO OFICIAL | ICO | 8 | 2 |
| 88 | FONDO MUNDIAL PARA EL MEDIO AMBIENTE | GEF | 14 | 2 |
| 89 | RECURSOS PROPIOS EN ESPECIE | RPRO-ESPECIE | 7 | 1 |
| 90 | ESPAÑA CANJE DE DEUDA | ESPAÑA CANJE DEUDA | 8 | 2 |
| 91 | INTERVIDA EL SALVADOR (ONG) | INTERVIDA ES (ONG) | 99 | 2 |
| 92 | PLAN EL SALVADOR (ONG) | PLAN ES (ONG) | 99 | 2 |
| 93 | MILENIUM CHALLENGE CORPORATION | MCC | 21 | 2 |
| 94 | BELL HELICOPTER TEXTRON | TEXTRON-BELL | 21 | 2 |
| 95 | EXPORT DEVELOPMENT CORPORATION | EDC | 21 | 2 |
| 96 | TEXTRON FINANCIAL CORPORATION | TEXTRON-FIN | 21 | 2 |
| 97 | Agencia Española de Cooperación Internacional para el Desarrollo | AECID | 8 | 2 |
| 99 | SIN FINANCIAMIENTO | S/F | 7 | 1 |
| 101 | Fdo. Especial de los Rec. de la Priv. de ANTEL | FANTEL | 7 | 1 |
| 109 | THE J. PAUL GETTY TRUST | J. PAUL GETTY TRUST | 0 | 1 |
| 110 | FODES 20% Gasto Corriente | FODES 20% | 7 | 1 |
| 112 | Fondos para Inversión FISDL | Fondos FISDL | 7 | 1 |
| 113 | Viceministerio de Vivienda y Desarrollo Urbano | VMVDU | 7 | 1 |
| 117 | Fideicomiso para Inversión en Educación, Paz Soci | FOSEDU | 7 | 1 |
| 119 | Fundación privada CETEMMSA-España | CETEMMSA | 8 | 2 |
| 120 | Fondo Salvadoreño para la Cooperación Mixta España | MIXTO-ES-SV | 0 | 1 |
| 121 | FONDO DE DESARROLLO CANADIENSE | ACDI | 0 | 2 |
| 122 | COMMODITY CREDIT CORPORATION | CCC | 0 | 2 |
| 124 | Comision Centroaméricana de Ambiente y Desarrollo | CCAD | 0 | 2 |
| 125 | Radda Barnen de Suecia | Radda Barnen | 0 | 2 |
| 126 | Caja de Ahorro y Pensiones de Barcelona "LA CAIXA" | LA CAIXA | 0 | 2 |
| 127 | Comision para el Desarrollo Cientifico y Tecnológi | CTCAP | 0 | 1 |
| 128 | Programa de las Naciones Unidas para el Medio Ambi | UNEP | 0 | 2 |
| 129 | Agencia Sueca de Cooperacion para el Desarrollo In | ASDI | 0 | 2 |
| 130 | Gobierno de Inglaterra | GOENG | 0 | 2 |
| 131 | Gobierno de Irlanda | GOIRL | 0 | 2 |
| 133 | Asociación de Fomento Internacional | AIF | 0 | 2 |
| 134 | Asociación Multilateral de Garantía a las Inversio | MIGA | 0 | 2 |
| 135 | Banco de Comercio de Mexico | BANCOMER | 0 | 2 |
| 136 | Bancos Franceses | BANCOFRANCIA | 0 | 2 |
| 137 | Corporación Financiera Internacional | CFI | 0 | 2 |
| 138 | Corporación Interamericana de Inversiones | CII | 0 | 2 |
| 139 | Comunidad Económica Europea | CEE | 0 | 2 |
| 140 | Eximbank del Japón | EXIMBANK JP | 0 | 2 |
| 141 | Fondo de Finaciamiento para la Exportación | FINEXPO | 0 | 1 |
| 142 | Gobierno de Argentina | GOAR | 0 | 2 |
| 143 | Gobierno de Mexico | GOMX | 0 | 2 |
| 145 | Organización de Paises Exportadores de Petróleo OPEP | OPEP | 0 | 2 |
| 146 | Societe Generale de Banque | SGBANQUE | 0 | 2 |
| 147 | United States Trust Co. of New York | US TRUST | 0 | 2 |
| 149 | Agencia Alemana de Cooperación Técnica (GTZ) | GTZ | 0 | 2 |
| 150 | Organización Internacional del Trabajo | OIT | 0 | 2 |
| 151 | Programa Mundial de Alimentos | PMA | 0 | 2 |
| 152 | City Bank | CITYBANK | 0 | 2 |
| 153 | Chemical Bank New York, U.S.A. | CHEMICAL | 0 | 2 |
| 154 | Gobierno de Bélgica | GOBEL | 0 | 2 |
| 156 | Banco Paribas | PARIBAS | 0 | 2 |
| 157 | Nations Bank | NATIONSBANK | 0 | 2 |
| 158 | General Bank Belgica | GENERALBANK | 0 | 2 |
| 159 | Eximbank de Estados Unidos | EXIMBANK US | 0 | 2 |
| 160 | Coordinadora Educativa Cultural Centroamericana | CECC | 0 | 2 |
| 161 | Organización de las Naciones Unidas para la Educación, la Ciencia y la Cultura | UNESCO | 0 | 2 |
| 162 | C. Itom Japon | CITOH | 0 | 2 |
| 163 | Francesa de Seguros para Comercio Exterior | COFACE | 0 | 2 |
| 164 | Otras Fuentes | OTRAS | 0 | 2 |
| 165 | The Riggs National Bank (Weeber Inc.) | RIGGS BANK | 0 | 2 |
| 166 | Phlcorp inc. | PHLCORP | 0 | 2 |
| 168 | Alto Comisionado de las Naciones Unidas para los Refugiados | ACNUR | 0 | 2 |
| 169 | Fundación Paz y Solidaridad de las Comisiones Obre | CCOO | 0 | 1 |
| 170 | Organizacion Holandesa para la Coop Internc. al D | NOVIB | 0 | 2 |
| 171 | Gobierno de los Estados Unidos de América | GOUSA | 0 | 2 |
| 172 | Fondo Español de Cooperacion para Agua y Saneamiento en América Latian y el Caribe | FECASAL | 0 | 2 |
| 173 | OXFAM AMERICA | OXFAM AMERICA | 0 | 2 |
| 174 | Embajada de Canadá | EMBAJADA DE CANADA | 4 | 2 |
| 175 | Alianza en Energía y Ambiente con Centroamérica | AEA | 0 | 2 |
| 176 | Fondo Común de Apoyo Programático - FOCAP | FOCAP | 0 | 2 |
| 177 | FIDEICOMISO PRODERNOR | FIDEICOMISO PRODERNOR | 0 | 1 |
| 178 | Facilidad de Inversión en América Latina (Latin American Investment Facility – LAIF) | LAIF | 0 | 2 |
| 179 | Fundacion para la Hemofilia Novo Nordisk FHNN | FHNN | 0 | 2 |
| 180 | Philip Morris International | Philip Morris Internat. | 21 | 2 |
| 181 | Foreing Affairs and International Trade Canada | DFAIT-CANADA | 4 | 2 |
| 182 | Centro Internacional de Agricultura Tropical | CIAT | 0 | 2 |
| 183 | Health Focus GmbH | Health Focus GmbH | 1 | 2 |
| 184 | Banco de Desarrollo de El Salvador | BANDESAL | 7 | 1 |
| 185 | Agencia Australiana de Cooperación Internacional | AusAID | 25 | 2 |
| 186 | Estado de Qatar | Estado de Qatar | 0 | 2 |
| 187 | FIDEICOMISO DE APOYO A LA PRODUCCION DE CAFE | FIDEICOMISO PRO CAFE | 7 | 1 |
| 188 | Institución Financiera Italiana (IFI) Artigiancassa S.p.A. | IFI-Artigiancassa | 13 | 2 |
| 189 | Agencia Alemana de Cooperación International (GIZ) | GIZ | 0 | 2 |
| 190 | Fondo de la OPEP para el Desarrollo Internacional (OFID) | OFID | 0 | 2 |
| 191 | Fundación FORD | FORDFoundation | 0 | 2 |
| 192 | Organización Internacional de las Migraciones | OIM | 0 | 2 |
| 194 | Asociación Enfants Du Monde | EDM | 0 | 2 |
| 195 | TEFEX S.A de C.V. | TEFEX | 0 | 1 |
| 196 | Fondo Mundial de lucha contra el SIDA, la tuberculosis y la malaria | FM (SIDA,TB, MALARIA) | 0 | 2 |
| 197 | Entidad de la ONU para la Igualdad de Género y el Empoderamiento de la Mujer | ONU Mujeres | 0 | 2 |
| 198 | Comisión Ejecutiva Portuaria Autónoma | CEPA | 7 | 1 |
| 199 | Consejo Internacional para la Ciencia | ICSU | 0 | 2 |
| 200 | Fondo de Compensación Ambiental | FCA | 0 | 1 |
| 201 | FIDEICOMISO FIDA | FIDEICOMISO FIDA | 0 | 1 |
| 202 | Cassa Depositi e Prestiti S.p.A. | CDP | 13 | 2 |
| 203 | Fundación Howard G. Buffett | Fund. H. G. Buffett | 0 | 2 |
| 204 | Fondos de Sentencias Judiciales por Daños Ambientales | FSJDA | 7 | 1 |
| 206 | Agencia Catalana de Cooperación al Desarrollo | ACCD | 8 | 2 |
| 207 | Departamento de Justicia de los Estados Unidos de América | DJUSA | 21 | 2 |
| 208 | Contribución Especial para la Seguridad Ciudadana | CESC | 7 | 1 |
| 209 | Contribución Especial a los Grandes Contribuyentes | CEGC | 7 | 1 |
| 210 | Fondo de Protección Civil, Prevención y Mitigación de Desastres | FOPROMID | 7 | 1 |
| 211 | Reino de Marruecos | Reino de Marruecos | 0 | 2 |
| 212 | Organización Internacional de Entidades Fiscalizadoras Superiores | INTOSAI | 0 | 2 |
| 213 | Agencia Internacional de Energías Renovables | IRENA | 0 | 2 |
| 214 | Principado de Andorra | Principado de Andorra | 0 | 2 |
| 215 | Banco Hipotecario de El Salvador | BH | 7 | 1 |
| 216 | Corporación Andina de Fomento - CAF | CAF | 0 | 2 |
| 217 | Alianza Mundial para la Educación | AME | 0 | 2 |
| 218 | Reino de Arabia Saudita | Reino de Arabia Saudita | 0 | 2 |
| 219 | Banco de America Central | BAC | 7 | 1 |
| 220 | Consejo Nacional para la Protección y Desarrollo de la Persona Migrante y su Familia | CONMIGRANTES | 7 | 1 |
| 221 | Embajada de Japón | Embajada de Japón | 14 | 2 |
| 222 | Agencia Italiana de Cooperación para el Desarrollo | AICS | 13 | 2 |
| 223 | Fondo Saudita para el Desarrollo | SFD | 26 | 2 |
| 224 | Yutong Bus Co. LTD. | YBCLTD | 27 | 2 |
| 225 | Programa Adopting El Salvador | Adopting El Salvador | 7 | 1 |
| 227 | Gobierno de la India | Gobierno de la India | 0 | 2 |
| 229 | Banco de Fomento Agropecuario | BFA | 7 | 1 |
| 230 | Agencia Luxemburguesa para la Cooperación al Desarrollo | Lux Development | 16 | 2 |
| 901 | RECURSOS PROPIOS - CEPA | RPRO-CEPA | 7 | 1 |

### Hoja "Convenios"

> Nota: la columna `CODIG_TIPO_CONVE` remite a un catálogo externo de "Tipo de Convenio" que **no está incluido** en este archivo Excel ni en el PDF original; toma los valores `1`, `2`, `6` y `9` en todo el catálogo, sin que su significado se explique en el archivo (ver "Datos Pendientes de Definir"). La columna `MONED_CONVE` (moneda del convenio) presenta, además de códigos de moneda reconocibles (USD, EUR, JPY, DEM, FRF, SDR, DEG, CAD, CNY, BEF, YJP), tres valores atípicos: `-` (guion, en la fila "GOJA/2KR"), `MON` (en las filas "Modificar" y "N/A") y `IDB` (en la fila "BID-765/OC-ES", donde el resto de las columnas parece indicar que se trata de un convenio con el BID en dólares). No se corrige ni normaliza ninguno de estos valores; se transcriben tal como aparecen. Asimismo, se observan 3 filas cuyo propio código de convenio (`CODIG_CONVE`) es un valor de control o placeholder en lugar de un identificador de convenio real: "Modificar" ("Modificar Convenio"), "N/A" (nombre "&nbsp;", literal del archivo original) y "S/N" ("S/N"). Se transcriben íntegras junto con el resto del catálogo, sin excluirlas, dado que forman parte literal de la hoja original.

| Código de Convenio (CODIG_CONVE) | Nombre del Convenio (NOMBR_CONVE) | Moneda (MONED_CONVE) | Monto del Convenio (MONTO_CONVE) | Número SIGADE (NUMER_SIGAD) | Código Tipo de Convenio (CODIG_TIPO_CONVE) |
|---|---|---|---|---|---|
| 132-0A1 | EXPLOTAC. DE LOS REC.PES. | FRF | 32664400 | BFR0001 | 1 |
| 2003-65-718 | PROG. FOMENTO DEL DESARROLLO LOCAL Y GOBERNANZA - FISDL IV | EU | 13994257.02 | BAL0018 | 9 |
| 519-HG-006-AQ1 | FINAN.DEUDA CONTR/PAINE WEBBER | USD | 8415312 | BEU0015 | 1 |
| 519-HG-007 | FINAN.DEUDA CON PAINE WEBBER | USD | 5500000 | BEU0016 | 1 |
| 519-I-049 30% NO CON | 519-I-049 30% NO CONDONADA | USD | 83764047 | BEU0012 | 1 |
| 880-SAL-8555 | COMPRA DE 6 HELIC. FUERZA ARMA | USD | 33501559 | PC00001 | 1 |
| ADMINISTRACIÓN DEL PROGRAMA DE BECAS DE EDUCACIÓN SUPERIOR | ADMINISTRACIÓN DEL PROGRAMA DE BECAS DE EDUCACIÓN SUPERIOR ENTRE FANTEL Y PRESIDENCIA DE LA REPÚBLICA DE EL SALVADOR | USD | 21000000 | - | 9 |
| AID 9962 | Programa de prevención y rehabilitación para jóvenes en situación de riesgo y conflicto con la ley en El Salvador | EUR | 5550000 | N/A | 1 |
| AID-519-0462 | CRECIMIENTO ECONOMICO PARA EL SIGLO XXI | USD | 10000000 | N/A | 9 |
| AID-519-HR-001 AQ1 | AID-519-HR-001 AQ1 | USD | 3478404.9 | BEU0013 | 1 |
| AIF-0031-0 ES | Tercer Proyecto de Carreteras | USD | 9645203 | MAI0001 | 1 |
| AIF-0517-0 ES | Proyecto de Lotes con Servicio | USD | 6000000 | MAI0003 | 1 |
| AIF-0726-0 ES | SEG. PROY. DE DES. URB. | USD | 6000000 | MAI0004 | 1 |
| AIF-227-0 ES | CREDITO PARA DESARROLLO | USD | 6343811 | MAI0002 | 1 |
| ALA/2005/17-587 | ALA/2005/17-587 | USD | 0 | N/A | 9 |
| BANCO-COMERCIAL | BCOM-HUELLA | USD | 787194 | PBC0007 | 1 |
| BBV 2130-SV-3773 | SEMAFOR.ELECTRON.MULT. | USD | 4182092 | PBC00001 | 1 |
| BBV 2130-SV-4045 | EQUIP.HOSP. MED.QUIRUR1,2,3NIV | USD | 3554876 | PBC00003 | 1 |
| BBV2130-SV-3935 | EQUIP. MED.IND.RED HOSPIT. | USD | 3554731 | PBC00002 | 1 |
| BBV2130-SV-4003 | sum.bienese transporte Eq.PNc | USD | 6803472 | PBC00004 | 1 |
| BCIE 1132-0-1 ANDA | BCIE 1132-0-1 ANDA | USD | 14698690 | MBCT001 | 2 |
| BCIE 1769 | PROGRAMA CONECTATE | USD | 0 | MBC0039 | 1 |
| BCIE 1773 | CENTRAL TERMICA DE TALNIQUE (50 MW) | USD | 60000000 | DMBC0001 | 6 |
| BCIE 1865 | BCIE-1865 | USD | 0 | MBCCEL1865 | 1 |
| BCIE 1886 | MEJOR. Y CONSERVACION DE VIAS NO PAVIMENTADAS DEL PROGR. DE VIAS SUBURBANAS Y CAMINOS RURALES | USD | 60000000 | DMBC0002 | 6 |
| BCIE 1888 | MODERNIZACION DEL REGISTRO INMOBILIARIO Y DEL CATASTRO - FASE II | USD | 55000000 | MBCCNR | 6 |
| BCIE 2-0242-0 ISDEM | BCIE 2-0242-0 ISDEM ALUMBRADO | USD | 2729925 | FMBC001 | 2 |
| BCIE 2015 | Programa de Infraestructura Social y Prevención de Vulnerabilidades | USD | 0 | MBC0038 | 1 |
| BCIE 2015, 2139 | BCIE 2015, 2139 | USD | 0 | - | 1 |
| BCIE 2031 | Proyecto Apertura del Boulevard Diego de Holgin, Santa Tecla | USD | 0 | MBC0036 | 1 |
| BCIE 2059 | -- | USD | 57500000 | MBC2059 | 1 |
| BCIE 2067 | PROGRAMA DE CONECTIVIDAD DE LA INFRAESTRUCTURA VIAL PARA EL DESARROLLO | USD | 48200000 | MBC0040 | 1 |
| BCIE 2077 | Plan de Agricultura Familiar y Emprededurismo Rural para la Seguridad Alimentaria y Nutricional | USD | 2000000 | MBC0042 | 1 |
| BCIE 2077, 2139 | BCIE 2077, 2139 | USD | 0 | - | 1 |
| BCIE 2102 | Programa de Fortalecimiento del Sistema Penitenciario en El Salvador | USD | 71000000 | MBC0043 | 1 |
| BCIE 2102, 2139 | BCIE 2102, 2139 | USD | 0 | - | 1 |
| BCIE 2114 | Proyecto Construcción de Planta Fotovoltaica 15 de Septiembre | USD | 15000000 | -- | 6 |
| BCIE 2120 | Ampliación de la Carretera al Puerto de La Libertad Tramos II y III, Construcción del Puente General Manuel José Arce sobre el Río Paz en la Frontera de La Hachadura, y Construcción del Puente sobre el Río Anguiatú en la Frontera de Anguiatú | USD | 144708600 | MBC0046 | 1 |
| BCIE 2127 | Construcción y Equipamiento de Edificio para Oficinas de Diputados y Grupos Parlamentarios de la Asamblea Legislativa de la República de El Salvador | USD | 32000000 | -- | 1 |
| BCIE 2139 | Apoyo a Proyectos de Inversión Productiva y Social | USD | 100000000 | MBC0045 | 1 |
| BCIE 2143 | Construcción de la Central Hidroeléctrica el Chaparral | USD | 125000000 | -- | 1 |
| BCIE 2146 | Construcción, Equipamiento y Modernización de las Oficinas Centrales de la FGR de El Salvador | USD | 44887500 | MBC0044 | 1 |
| BCIE 2152 | Rehabilitación de la Planta Potabilizadora de Las Pavas | USD | 16982500 | N/A | 1 |
| BCIE 2234 | Construcción, Equipamiento y Modernización de las Oficinas Centrales de la Fiscalía General de la República | USD | 25338586.48 | N/A | 1 |
| BCIE 2237 | Prog. de Desarrollo Soc en el Marco del Prog. de Finan. del Plan Control Territorial en su Fase II | USD | 91000000 | -- | 1 |
| BCIE 2240 | Programa de Modernización de las Instituciones de Seguridad Ciudadana en el Marco del Financiamiento del Plan Control Territorial en su Fase III | USD | 109000000 | -- | 1 |
| BCIE 2243 | Proyecto Construcción de Viaducto y Ampliación de Carretera CA01W (Tramo Los Chorros), entre Autopista Monseñor Romero y CA01W; Municipios de Santa Tecla, Colón y San Juan Opico, Departamento de La Libertad | USD | 245824129 | -- | 1 |
| BCIE 2254 | Programa de Construcción de Infraestructura y Rescate de Escenarios Deportivos a Nivel Nacional (PRODEPORTE) | USD | 115200000 | MBC0054 | 1 |
| BCIE 2256 | Programa Mi Nueva Escuela | USD | 200000000 | MBC0058 | 1 |
| BCIE 2306 | Proyecto Construcción de Viaducto y Ampliación de Carretera CA01W (Tramo Los Chorros) | USD | 166000000 | MBC0061 | 1 |
| BCIE 2337 | Programa Surf City Fase I | USD | 113000000 | -- | 1 |
| BCIE-2-1099-0 ISDEM | BCIE-2-1099-0 ISDEM CONS.MER. | USD | 9283726 | FMBC004 | 2 |
| BCIE/2-0003-0 | CONST.CARRET LA UNION-FRONT.HO | USD | 2400000 | MBC0001 | 1 |
| BCIE/2-0003-1 | PROY.CONST.CARR.LA CUCHILLA | USD | 2295699 | MBC0002 | 1 |
| BCIE/2-0007-0 | 1era.ETAPA DESAR-COMUNAL-CABAÑ | USD | 3998812.1 | MBC0006 | 1 |
| BCIE/2-0015-0 | DESARR.AGR.P´PEQ.PRODUCTORES | USD | 3270000 | MBC0014 | 1 |
| BCIE/2-0017-0 | PROG.PROD.COLEG.VOCACIONALES | USD | 2500000 | MBC0016 | 1 |
| BCIE/2-0024-0 | PROY.RESTAURAC. DE LA UES | USD | 475000 | MBC0020 | 1 |
| BCIE/2-0029-0 | PROG.INFR.SOC. EDUC.PARV.BÁSIC | USD | 1500000 | MBC0021 | 1 |
| BCIE/2-0030-0 | PROG.INFR.SOCIAL AGUA POTABLE | USD | 1499895 | MBC0022 | 1 |
| BCIE/2-0112-0 | Const.Carrt.SSalv.-San Miguel | USD | 11000000 | BMC0003 | 1 |
| BCIE/2-0136-1 | Carr.CA-1CA-12, Santa Ana | USD | 3840000 | MBC0011 | 1 |
| BCIE/2-0148-1 | Construc.Carret. Km.52 La Herr | USD | 3225627 | MBC0012 | 1 |
| BCIE/2-0213-0 | REHAB.CARRET.LA HACHADURA-CA12 | USD | 10000000 | MBC0009 | 1 |
| BCIE/2-0219-0 | REHAB.CARR.S.ANA-METAPÁN CA-12 | USD | 10199184 | MBC0008 | 1 |
| BCIE/2-0225-0 | Reconst. Carr. SS-Sn Miguel II | USD | 7745859 | MBC0010 | 1 |
| BCIE/2-0278-0 | REHABILITACION VIAS PAVIMENT. | USD | 20000000 | MBC0017 | 1 |
| BCIE/2-0279-0 | PLAN D´RECONST.NAC.REP.DE E.S. | USD | 30000000 | MBC0018 | 1 |
| BCIE/2-0282-0 | PLAN NACNAL. SEÑALAMIENTO VIAL | USD | 7935410 | MBC0024 | 1 |
| BCIE/2-0300-0 | CONST.PUENTE BAILEY S.RIO LEMP | USD | 4900000 | MBC0026 | 1 |
| BCIE/2-0623-0 | PROG.NAC.DE RIEGO Y DRENAJE | USD | 3159616.4 | MBC0007 | 1 |
| BCIE/2-1005-0 | PROY.DESARROLLO RURAL INTEGRAD | USD | 11100000 | MBC0023 | 1 |
| BCIE/2-1144-0 | PROY.RECONST.HOSPIT.GRAL. ISSS | USD | 33300000 | MBC0025 | 1 |
| BCIE/2-1152-0 | PROG.NAC.D´REHAB.VIAS PAVIMENT | USD | 32900000 | MBC0028 | 1 |
| BCIE/2-1180-0 | PROG.ESCUELA SALUDABL.II ETAPA | USD | 20000000 | MBC0027 | 1 |
| BCIE/2-1250-0 | PROY.INFRAEST.ECON.Y SOCIAL B. | USD | 40000000 | MBC0029 | 1 |
| BCIE/2-1306 -MAG | Desarr.Rural S.F.Ecol.Trifinio | USD | 6971000 | MBC0030 | 1 |
| BCIE/2-1417-0 | Primera Etapa Anillo Periféric | USD | 62700000 | MBC0034 | 1 |
| BCIE/2-1417-0,BCIE 2031,BCIE 2015 | Proyecto Apertura del Boulevard Diego de Holgin, Santa Tecla | USD | 0 | - | 1 |
| BCIE/2-1496-0 | Progr. Desarrollo Local FISDL | USD | 30000000 | MBC0032 | 1 |
| BCIE/2-1510-0 INDES | Apoyo Desarr.Educ.Integ.El Sal | USD | 50000000 | MBC0031 | 1 |
| BCIE/2-1517-0 | Multisectorial de Emergencia | USD | 75000000 | MBC0033 | 1 |
| BCIE/2-1531 | CONTRATO DE PREST. PARA MERC C | USD | 7400000 | MBCGES | 2 |
| BCIE/2-1556-0 | PROG. NACIONAL DE CARRETERAS | USD | 135000000 | MBC0035 | 1 |
| BCIE/2-1663-0 | -- | USD | 0 | S/N | 1 |
| BCO.BBV-2130-SV-5047 | ANDA -LEMPA | USD | 15438600 | GBES002 | 2 |
| BFCE/AC # 1 | Construcción del Nuevo H.Ros. | FRF | 21362712 | BFR0005 | 1 |
| BFCE/AC # 2 | Construcción del Comp.G.Chi. | FRF | 17389398 | BFR0006 | 1 |
| BIC-015/CD-ES-CEPA | BIC-015/CD-ES-CEPA AMPLIACION | CAD | 2000000 | GMBI002 | 2 |
| BID - ATN-5977 | BID - ATN-5977 | DON | 0 | BID - ATN-59 | 1 |
| BID - ATN-5981 | BID - ATN-5981 | DON | 0 | BID - ATN-59 | 1 |
| BID 1041/OC-ES | PROG.MODERNIZ.SECTOR PUBLIC. | USD | 70000000 | MBI0057 | 1 |
| BID 1067/OC-ES-1 | PROGRAMA DE DESARROLLO LOCAL | USD | 19769937 | MBI0049 | 1 |
| BID 1067/OC-ES-2 | PROGRAMA DESARROLLO LOCAL | USD | 13965000 | MBI0050 | 1 |
| BID 1084/OC-ES-1 | PROG.APOY.A TECNOLOG.EDUCATIV. | USD | 43051125 | MBI0058 | 1 |
| BID 1084/OC-ES-2 | PROG.APOY.A TEC.EDUCATIV. | USD | 29892810 | MBI0059 | 1 |
| BID 1092/OC-ES-1 | APOY.A LA MODERNIZ.MSPAS | USD | 13934495 | MBI0072 | 1 |
| BID 1092/OC-ES-2 | APOYO A LA MODERNIZ. DEL MSPAS | USD | 6670225 | MBI0073 | 1 |
| BID 1100/OC-ES-1 | Programa de Infraestructura Educativa | USD | 34919000 | MBI0052 | 1 |
| BID 1100/OC-ES-2 | PROGRAMA INFRAESTRUCTURA EDUCA | USD | 34759000 | MBI0053 | 1 |
| BID 1102/OC-ES | PROG.REFORMA SECTOR HIDRICO | USD | 43625705 | MBI0066 | 1 |
| BID 119/TF-ES | PROG. CRED. AGRP. PEQ PRODUCTO | USD | 12313292 | MBI0006 | 1 |
| BID 1203/OC-ES | PROG.MODERNIZ.Y FORT.ASAMB.LEG | USD | 3528774 | MBI0060 | 1 |
| BID 1209/OC-ES | PROG. DESCONTAM DE AREAS CRI | USD | 29765084 | MBI0063 | 1 |
| BID 124/TF-ES | PROG CRED GLOB SEC AGROP REFOR | USD | 3422874 | MBI0017 | 1 |
| BID 1310/OC-ES | APOYO RECONST.EMERG.TERR 13.01 | USD | 19682834 | MBI0062 | 1 |
| BID 1314/OC-ES | PROG MULTIF CAM RURAL SOST | USD | 57712030 | MBI0067 | 1 |
| BID 1315/OC-ES | APOY RECONST EMERG.TERR 13/OC | USD | 18435371 | MBI0068 | 1 |
| BID 1327/OC-ES | PROY.RECONVERSION AGROEMPRESAR | USD | 24921505 | MBI0070 | 1 |
| BID 1352/OC-ES | DESARROLLO LOCAL II FIS | USD | 69793465 | MBI0071 | 1 |
| BID 1379/OC-ES-1 | PROG.VIVIENDA-FASE I- MOP | USD | 30300000 | MBI0074 | 1 |
| BID 1379/OC-ES-2 | PROG.VIVIENDA FASE I-MOP | USD | 39700000 | MBI0075 | 1 |
| BID 1492-OC (IFF) | PROG. DE APOYO A LA COMPETITIVIDAD | USD | 77900000 | MBI0078 | 1 |
| BID 1782/OC-ES | PROGRAMA DE APOYO A LA POLITICA SOCIAL | USD | 0 | MBI0081 | 1 |
| BID 1782/OC-ES,BID 2068/BL-ES,BID 2069/OC-ES,BID 2070/OC-ES | Programa de Apoyo a la Politica Social | USD | 500000000 | - | 1 |
| BID 2296/OC-ES | PROGRAMA DE FORTALECIMIENTO FISCAL | USD | 200000000 | MBI000084 | 1 |
| BID 234/IC-ES | PROG. REHAB EMERG SECTOR TELEF | USD | 5544425 | MBI0051 | 1 |
| BID 2347/OC-ES | Programa Integrado de Salud | USD | 60000000 | MBI00085 | 1 |
| BID 2358/OC-ES | Programa de Agua y Saneamiento Rural | USD | 20000000 | MBI0084 | 1 |
| BID 2369/OC-ES | Programa de Caminos Rurales para el Desarrollo | USD | 35000000 | MBI00086 | 1 |
| BID 2373 OC-ES | Programa de Vivienda y Mejoramiento Integral de Asentamientos Urbanos Precarios Fase II | USD | 70000000 | MBI00087 | 1 |
| BID 2375/OC-ES | Apoyo a Comunidades Solidarias Urbanas | USD | 35000000 | MBI2375/OC-ES | 1 |
| BID 2492/OC-ES | Modernización del Órgano Legislativo II | USD | 5000000 | MBI0096 | 1 |
| BID 2525/OC-ES | PROYECTO CIUDAD MUJER | USD | 20000000 | MBI0097 | 1 |
| BID 2570/OC-ES | PROGRAMA DE APOYO PROGRAMATICO A LA AGENDA DE REFORMAS ESTRUCTURALES DEL SECTOR DE ENERGIA ELECTRICA | USD | 100000000 | MBI0099 | 1 |
| BID 2572/OC-ES | Programa de Transporte del AMSS | USD | 45000000 | MBI0100 | 1 |
| BID 2581/OC-ES | Programa de Conectividad Rural en Zona Norte y Oriente | USD | 15000000 | MBI0101 | 1 |
| BID 2583/OC-ES | Programa de Apoyo al Desarrollo Productivo para Inserción Internacional | USD | 30000000 | MBI0103 | 1 |
| BID 2630/OC-ES | Reducción de Vulnerabilidad en Asentamenientos Urbanos Precarios en el AMSS | USD | 50000000 | MBI0104 | 1 |
| BID 2710/OC-ES | Programa Integral de Sostenibilidad Fiscal y Adaptación al Cambio Climatico para El Salvador | USD | 20000000 | MBI0102 | 1 |
| BID 2881/OC-ES | Programa de Apoyo Integral a la Estrategia de Prevención de la Violencia | USD | 45000000 | MBI0106 | 1 |
| BID 2966/OC-ES | Programa de Desarrollo Turístico de la Franja Costero-Marina | USD | 25000000 | MBI0105 | 1 |
| BID 3170/OC-ES | Programa de Corredores Productivos | USD | 40000000 | MBI0108 | 1 |
| BID 3271/OC-ES | Préstamo Global de Crédito para el Financiamiento del Desarrollo Productivo de El Salvador | USD | 100000000 | MBI0107 | 1 |
| BID 352/SF-ES | PROY. HIDROELEC CERRON GRANDE | USD | 38100000 | MBI0001 | 1 |
| BID 3608/OC-ES | Programa Integrado de Salud | USD | 170000000 | MBI0109 | 1 |
| BID 369/SF-ES | PROY DE RIEGO Y DES. AGROPECUA | USD | 7548267 | MBI0002 | 1 |
| BID 3852/OC-ES | Programa de Fortalecimiento de la Administración Tributaria | USD | 30000000 | -- | 1 |
| BID 393/SF-ES | PROG. MEJOR. SERV. DE SALUD | USD | 15000000 | MBI0003 | 1 |
| BID 426/SF-ES | MJ. AMP. SIS. ABAS AGUA P. SS | USD | 30000000 | MBI0004 | 1 |
| BID 427/SF-ES | APERT. AMP. VIAS URB. S.S. | USD | 12983818 | MBI0005 | 1 |
| BID 4542/OC-ES | Programa de Fortalecimiento Fiscal para el Crecimiento Inclusivo | USD | 350000000 | N/A | 1 |
| BID 472/SF-ES | PROG.DE CONST. DE CAM.RURALES | USD | 10000000 | MBI0008 | 1 |
| BID 480/OC-ES | PROG.CREDITO AGROPECUARIO | USD | 8274264 | MBI0024 | 1 |
| BID 4807/OC-ES | Programa de Fortalecimiento Fiscal para el Crecimiento Inclusivo II | USD | 200000000 | N/A | 1 |
| BID 481/OC-ES | III-ET PROG GLOB.AGROP. PTEII | USD | 29100000 | MBI0025 | 1 |
| BID 4870/OC-ES | Fortalecimiento de la Resiliencia Climática de los Bosques Cafetaleros en El Salvador | USD | 45000000 | -- | 1 |
| BID 502/SF-ES | PROGRAMA DE DESARROLLO PESQUER | USD | 4773149 | MBI0009 | 1 |
| BID 504/SF-ES | SEGUNDA ET PROG. ACUED. RURALE | USD | 4661714 | MBI0007 | 1 |
| BID 5043/OC-ES | Respuesta Inmediata de Salud Pública para Contener y Controlar el Coronavirus y Mitigar su Efecto en la Prestación del Servicio en El Salvador | USD | 50000000 | N/A | 1 |
| BID 5046/OC-ES | Programa de Fortalecimiento de la Politica Pública y Gestión Fiscal para la Atención de la Crisis Sanitaria y Económica causada por el COVID-19 en El Salvador. | USD | 250000000 | MBI0113 | 1 |
| BID 5080/OC-ES | Programa de Mejora de la Calidad y cobertura Educativa: Nacer, Crecer, Aprender | USD | 100000000 | -- | 1 |
| BID 525/SF-ES | PROY.HIDROEL.SN LORENZO EN LEM | USD | 45400000 | MBI0061 | 1 |
| BID 5340/OC-ES | Programa de Conectividad Digital Social | USD | 50000000 | N/A | 1 |
| BID 5341/KI-ES | Programa de Conectividad Digital Social | USD | 35000000 | N/A | 1 |
| BID 537/SF-ES | Proy.Seg. Etapa del Pro. de Ex | USD | 8749282 | MBI0010 | 1 |
| BID 5454/OC-ES | Programa de Modernización del Sistema Estadistico de El Salvador | USD | 44000000 | N/A | 1 |
| BID 5577/OC-ES | Programa de Fortalecimiento del Sector Agua y Saneamiento en El Salvador | USD | 100000000 | MBI0121 | 1 |
| BID 5590/OC-ES | Programa de Apoyo a la Recuperación y Expansión del Sector Turismo en El Salvador | USD | 106000000 | MBI0122 | 1 |
| BID 5620/OC-ES | Programa de Caminos Rurales | USD | 100000000 | MBI0123 | 1 |
| BID 5785/OC-ES | Programa para la Protección Social Responsiva a Choques en El Salvador | USD | 100000000 | MBI0124 | 1 |
| BID 5874/OC-ES | PROGRAMA DE SALUD INTELIGENTE E INTEGRAL | USD | 235000000 | -- | 1 |
| BID 596/SF-ES | PROGRAMA DE PREINVERSION | USD | 4500000 | MBI0013 | 1 |
| BID 5977/OC-ES | Programa de Financiamiento para Vivienda Social, Inclusiva y Sostenible | USD | 50000000 | - | 2 |
| BID 605/SF-ES | PROG. CRED. AGROP. PEQ. PROD. | USD | 16500000 | MBI0015 | 1 |
| BID 653/OC-ES | PROGRAMA CARRETERAS TRONCALES | USD | 94791630 | MBI0034 | 1 |
| BID 665/SF-ES | CONSTRUCCION CAMINOS RURALES | USD | 21476369 | MBI0018 | 1 |
| BID 676/SF-ES | PROG COMERC INSUM PROD AGRIC | USD | 4374317 | MBI0019 | 1 |
| BID 714/OC-ES | PROG REFORMA SECTOR INVERSIONE | USD | 90000000 | MBI0035 | 1 |
| BID 731/OC-ES | PLAN DE RECONSTRUC. NACIONAL | USD | 39331184 | MBI0055 | 1 |
| BID 772/SF-ES | TERC. ET. PROG. ACUEDUC RURALE | USD | 15392413 | MBI0026 | 1 |
| BID 813/SF-ES | PROG.AGUA POTAB.Y ALCANTAR.SAN | USD | 164300582 | MBI0064 | 1 |
| BID 829/OC-ES | PROG FONDO INV SOC SALV III ET | USD | 60000000 | MBI0039 | 1 |
| BID 837/SF-ES | EDUC.TEC.SUPER.NO UNIVERSIT. | USD | 14396645 | MBI0029 | 1 |
| BID 838/OC-ES | PROGRAMA DE DESAR. ELECT. II | USD | 206755061 | MBI0056 | 1 |
| BID 840/OC-ES | PROG.REHAB.MEJORAM.VIAL E-II | USD | 29597132 | MBI0042 | 1 |
| BID 861/SF-ES | PROG. FONDO INV. SOC. EL SALV | USD | 33000000 | MBI0032 | 1 |
| BID 870/SF-ES | PROGRAMA DE CARRET.TRONC. | USD | 24074662 | MBI0033 | 1 |
| BID 879/0C-ES | PROY. MOD. EDUC. BASICA | USD | 37300000 | MBI0040 | 1 |
| BID 886/OC-ES | PROGRAMA AMBIENTAL DE EL SALVA | USD | 30000000 | MBI0045 | 1 |
| BID 905/SF-ES | PROGRAMA F.I.S.D.L., II ETAPA | USD | 35000000 | MBI0038 | 1 |
| BID 919/OC-ES | PROG. APOYO A REFORMA JUSTICIA | USD | 19200000 | MBI0043 | 1 |
| BID 920/OC-ES | PROG. APOYO REFOR SIST JUSTICI | USD | 3000000 | MBI0044 | 1 |
| BID 941/OC-ES-2 | PROY MODERN ADMON FISCAL 2 | USD | 6636000 | MBI0048 | 1 |
| BID 980/SF-ES | PROY MODERN ADMON FISCAL (UTEC | USD | 3774000 | MBI0046 | 1 |
| BID ES-G1001 | Programa Salud Mesoamérica 2015 - El Salvador | USD | 15666000 | N/A | 9 |
| BID ES-O0011 | Préstamo Contingente para Emergencias por Desastres Naturales y de Salud Pública | USD | 400000000 | - | 1 |
| BID ES-O0011; 5631/OC-ES; ES-L1161 | Préstamo Contingente para Emergencias por Desastres Naturales - Tormenta Tropical Julia | USD | 26880000 | - | 1 |
| BID GRT/ER-19647-ES | Programa de Conectividad Digital Social | USD | 6431162 | N/A | 9 |
| BID GRT/HE-12982-ES, GRT/HE-12983-ES | Convenio Individual de Financiamiento No Reembolsable de Inversión del Fondo Mesoamericano de Salud | USD | 6500000 | N/A | 9 |
| BID GRT/HE-14650- ES, GRT/HE-14651-ES | Convenio Individual de Financiamiento No Reembolsable de Inversión del Fondo Mesoamericano de Salud | USD | 3944645 | N/A | 9 |
| BID GRT/HE-16714-ES | Iniciativa Salud Mesoamérica 2015 – El Salvador Tercera Operacion individual | USD | 850000 | N/A | 9 |
| BID GRT/HE-16714-ES, GRT/HE-16715-ES | Iniciativa Salud Mesoamérica 2015 – El Salvador Tercera Operacion individual | USD | 1530000 | N/A | 9 |
| BID-019/VF-ES-CEL | BID-019/VF-ES-CEL-SAN LORENZO | USD | 30000000 | MBIT001 | 1 |
| BID-1173/OC-ES | PROG.APOYO ALSEC FINANC EN E.S | USD | 3782240 | MBI0069 | 1 |
| BID-1492 (TASA AJUST | PROG. DE APOYO A LA COMPETITIVIDAD | USD | 22100000 | MBI0079 | 1 |
| BID-349/OC-ES | PROG. DESARROLLO GANADERO | USD | 14802288 | MBI0012 | 1 |
| BID-561/SF-ES | PROG. DESAR. COMUNAL ZONA NORT | USD | 8648673 | MBI0011 | 1 |
| BID-5851/OC-ES | Programa de Facilitación Comercial y Modernización de Operación Portuaria en El Salvador | USD | 84000000 | MBI0125 | 1 |
| BID-5937/OC-ES | Programa para el Desarrollo de Infraestructura de Datos de El Salvador | USD | 60000000 | N/A | 1 |
| BID-604/SF-ES | P.DE MEJ.DE LOS SERV.DE SALU. | USD | 21516703 | MBI0014 | 1 |
| BID-642/SF-ES | PROG CRED. GLO. SEC. AGRO. REF | USD | 40400000 | MBI0016 | 1 |
| BID-683/SF-ES | PROY.HIDRO.SAN LORENZO.R.LEMP. | USD | 16500000 | MBI0020 | 1 |
| BID-705/SF-ES | Proy. Des. de Inv. y Ext. Agr. | USD | 7657946 | MBI0021 | 1 |
| BID-732/SF-ES | PROGR.DE PREINVERSION II ETAPA | USD | 6421458 | MBI0022 | 1 |
| BID-765/OC-ES | PROGRAMA DE INVERS. SOCIAL | IDB | 15042972 | MBI0037 | 1 |
| BID-801/SF-ES | REHAB. SECT. DE SALUD Y AGUA P | USD | 3136008 | MBI0027 | 1 |
| BID-802/SF-ES | DESARR.AGRICOLA LEMPA-ACAHUAPA | USD | 10875000 | MBI0028 | 1 |
| BID-844/SF-ES | MEJORAM. CAMINOS RURALES | USD | 43696792 | MBI0030 | 1 |
| BID-860/SF-ES | PROGRA.PREINVERSION III ETAPA | USD | 7000000 | MBI0031 | 1 |
| BID-898/SF-ES | PROG.REHABILIT.AGUA POTABLE | USD | 19000000 | MBI0036 | 1 |
| BID0004/SQ-ES | SIST.INT.ELEC.P.C.A.(SIEPAC) | USD | 10000000 | GMBI0004 | 2 |
| BID004/SQ-ES | SIST.INT.ELEC.P.C.A(SIEPAC) | USD | 10000000 | GMBI0003 | 2 |
| BID1004/SF-ES | PROGRAMA INFRAESTRUCTURA EDUC | USD | 1100000 | MBI0054 | 1 |
| BID1369 OC-ES | SIST.INT.ELEC.P.C.A.(SIEPAC) | USD | 30000000 | GMBI0004 | 2 |
| BID839/OC-ES | PROG. REH. Y MEJ. VIAL. E. II | USD | 195000000 | MBI0041 | 1 |
| BID941/OC-ES-1 | PROY.MODER.ADMON.FISCAL | USD | 9300000 | MBI0047 | 1 |
| BIRF 3293-0 ES | PROGRAMA DE AJUSTE ESTRUCTURAL | USD | 75000000 | MBM0006 | 1 |
| BIRF 7135 - ES | PROYECTO DE MODERNIZACION DEL ORGANO JUDICIAL | USD | 18200000 | MBM0021 | 1 |
| BIRF 7635-FV | PRESTAMO PARA POLITICAS DE DESARROLLO DE LAS FINANZAS PUBLICAS Y DEL SECTOR SOCIAL | USD | 450000000 | MBM0022 | 1 |
| BIRF 7635-SV | PRESTAMO PARA POLITICAS DE DESARROLLO DE LAS FINANZAS PUBLICAS Y DEL SECTOR SOCIAL | USD | 450000000 | MBM0022 | 1 |
| BIRF 7806-SV | Sostenibilidad de los Logros Sociales para la Recuperación Económica | USD | 100000000 | MBM0023 | 1 |
| BIRF 7811-SV | Protección de Ingresos y Empleabilidad | USD | 50000000 | MBM0035 | 1 |
| BIRF 7812-SV | PROYECTO DE ASISTENCIA TECNICA PARA ADMINISTRACION FISCAL Y DESEMPEÑO DEL SECTOR PUBLICO | USD | 20000000 | MBM0030 | 1 |
| BIRF 8110-SV | Proyecto de Mejoramiento de la Calidad de la Educación | USD | 60000000 | MBM0036 | 1 |
| BIRF 8948-SV | Proyecto de Desarrollo Económico Social Resiliente | USD | 200000000 | -- | 1 |
| BIRF 9065-SV | Proyecto Creciendo Saludables Juntos: Desarrollo Integral de la Primera Infancia en El Salvador | USD | 250000000 | -- | 1 |
| BIRF 9067-SV | Proyecto y Aprender Juntos: Desarrollo Integral de la Primera Infancia en El Salvador | USD | 250000000 | -- | 1 |
| BIRF 9100-SV | Proyecto de Respuesta de El Salvador ante el COVID-19 | USD | 20000000 | N/A | 1 |
| BIRF 9602-SV | Proyecto de Transporte Infraestructura Resiliente en El Salvador | USD | 150000000 | -- | 1 |
| BIRF 9612-SV | Proyecto Promoviendo Oportunidades de Empleo y Desarrollo de Habilidades en El Salvador | USD | 150000000 | -- | 1 |
| BIRF NO-7084-ES | RECONST.HOSP. DEST.POR EL TERR | USD | 142600000 | MBM0040 | 1 |
| BIRF No. 9790-SV | Proyecto de Mejora de la Atención de Salud en El Salvador | USD | 120000000 | -- | 1 |
| BIRF TF-099529 | Acuerdo para la Preparación de Propuesta de Readiness El Salvador | USD | 3000000 | N/A | 9 |
| BIRF-1007-0 ES | SEGUNDO PROYECTO DE EDUCACION | USD | 15456104 | MBM0002 | 1 |
| BIRF-1050-0 BES | PROYECTO DE LOT. CON SERVICIO | USD | 2500000 | MBM0003 | 1 |
| BIRF-2873-S | PROG. DE REC.DE S.S. Y POB. AL | USD | 63767434 | MBM0004 | 1 |
| BIRF-3348-S ES | REHABILITACION DE LOS SECT.SOC | USD | 25727480 | MBM0007 | 1 |
| BIRF-3389-S ES | PROY ASIST.TEC.P´EL S.E.EL. | USD | 9660412 | MBM0018 | 1 |
| BIRF-3576-S/3576-A | PROG. DE REF. E INV. SECT. AGR | USD | 39550859 | MBM0008 | 1 |
| BIRF-3646-0 ES | SEGUNDO PRESTAMO DE AJ.EST. | USD | 50000000 | MBM0010 | 1 |
| BIRF-3648-0 ES | PROYECTO DE ASISTENCIA TECNICA | USD | 2407944 | MBM0009 | 1 |
| BIRF-3920-0 ES | PROY.MODERN. DEL SECT. ENERGIA | USD | 36038761 | MBM0013 | 1 |
| BIRF-3945-0 ES | PROY. PARA LA MOD. DE LA ED.BA | USD | 34000000 | MBM0011 | 1 |
| BIRF-3946-0 ES | PROY. DE AS.TEC. PARA EL MEJ. | USD | 16000000 | MBM0012 | 1 |
| BIRF-3982-0 ES | PROY. ADMON. DE TIERRAS | USD | 50000000 | MBM0014 | 1 |
| BIRF-4082-0 ES | PROY. ASIST. TEC. P. LA M.S.P. | USD | 24000000 | MBM0016 | 1 |
| BIRF-4224-0 ES | PROYECTO DE EDUC. MEDIA | USD | 58000000 | MBM0017 | 1 |
| BIRF-4320-0 ES | PROGRAMA REFORMA ED.FASE I | USD | 88000000 | MBM0019 | 1 |
| BIRF-7084- ES | RECONST. HOSP. DEST.POR ELTERR | USD | 142060000 | MBM0040 | 1 |
| BIRF-7275-ES | Préstamo para Politica de Desarrollo en Base a un Crecimiento Amplio | USD | 100000000 | MBM0020 | 1 |
| BIRF-7916-SV | PROYECTO DE FORTALECIMIENTO DE GOBIERNOS LOCALES | USD | 80000000 | MBM0025 | 1 |
| BIRF-7997-SV | Préstamo para Politicas de Desarrollo para Manejos de Riesgos de Desastres | USD | 50000000 | MBM0029 | 1 |
| BIRF-8048-SV | PRESTAMO DE POLITICAS DE DESARROLLO PARA FINANZAS PUBLICAS Y PROGRESO SOCIAL | USD | 100000000 | MBM0031 | 1 |
| BIRF-8076-SV | Proyecto de Fortalacimiento del Sistema de Salud Pública | USD | 80000000 | MBM0034 | 1 |
| BIRF-9229-SV | Financiamiento Adicional para Proyecto de Respuesta de El Salvador ante el COVID-19 | USD | 50000000 | S/N | 1 |
| BIRF-9429-SV | Segundo Financiamiento Adicional para Proyecto de Respuesta de El Salvador ante el COVID-19 | USD | 100000000 | S/N | 1 |
| BIRF-9513-SV | Proyecto de Resiliencia del Sector Agua en El Salvador | USD | 100000000 | S/N | 1 |
| BOCAFE0001-TENEDORES | BOCAFE0001 | USD | 80000000 | BOCAFE0001 | 2 |
| BONOSBE2001 | GASTOS DE CAPITAL DEL GOB. | USD | 353500000 | BONOSBE2001 | 1 |
| C.ITOH C.P. | C.ITOH C.P. | USD | 692650 | BJA0002 | 1 |
| CAIXA-PNC2000 | CAIXA-PNC2000 | USD | 5741489 | PIF00001 | 1 |
| CESCE C.P | CESCE C.P. | USD | 999400 | BES0001 | 1 |
| CFA-12061 | Programa para la Transformación del Clima de Negocios de El Salvador, a través de la Facilitación del Comercio e Inversiones | USD | 75000000 | N/A | 1 |
| CFA-12251 | Programa de mejora ambiental, agua potable y saneamiento en la cuenca alta del río Lempa (Trifinio) y Puerto de la Libertad, en El Salvador | USD | 75000000 | - | 1 |
| CFA-12253 | Programa para el Fortalecimiento de Espacios Públicos para la sostenibilidad de la Seguridad y la Recuperación del Tejido Social | USD | 68000000 | - | 1 |
| CFA-12279 | Programa de Apoyo a la Movilidad Urbana Baja en Emisiones | USD | 75000000 | N/A | 1 |
| CFA012205 | Programa para la Implementación de un Sistema de Telemedicina en El Salvador” | USD | 77000000 | N/A | 1 |
| CFA012400 | Programa de Desarrollo del sector Aeronáutico de El Salvador: El Salvador Vuela | USD | 320000000 | MCA0010 | 1 |
| CFA012402 | Operación de Fortalecimiento de la Soberanía de Conectividad de El Salvador: Cable Submarino | USD | 145000000 | MCA0011 | 1 |
| CHINA AREAS CRITICAS | CHINA-AREAS CRITICAS | USD | 7682000 | BCH0003 | 1 |
| COFACE CP | COFACE CP | FRF | 99361076 | BFR0004 | 1 |
| CREDIT NATIONAL CP | CREDIT NATIONAL CP | FRF | 9416451.7 | BFR0003 | 1 |
| DCI-ALA/2009/020-152 | PROEDUCA | EUR | 23000000 | N/A | 9 |
| DCI-ALA/2011/282-216 | Contribución LAIF al Programa Caminos Rurales de El Salvador | EUR | 5340000 | N/A | 9 |
| DCI-ALA/2014/341-133 | Promoción Derechos de Mujeres a través de | EUR | 2250000 | N/A | 9 |
| DEUTSCHE BANK | CONTRATO COMERCIAL DE SUM | USD | 2846993 | PBC0005 | 1 |
| DEUTSCHEBANK-RED | SUMINS Y EQUI RED HOSPITALARIA | USD | 2978554 | PBC0008 | 1 |
| DEUTSCHEBANK-UES | EQUIP.REAC.UNID INV.DES.(U.NAC | USD | 3562138 | PBC0006 | 1 |
| EXIMBANK C.P. | EXIMBANK-JAPON CP | JPY | 1630000000 | BJA0001 | 1 |
| EXIMBANK CEL.MODER. | EXIMBANK CEL.MODER.ENERGIA | JPY | 1230000000 | GBJA001 | 2 |
| Exp.I Bank6020676001 | Rehabilit. agric.en El Salv. | USD | 100000000 | PEI0001 | 2 |
| EXPORT IMPORT BANK O | BALANZA DE PAGOS | USD | 10000000 | BCH0001 | 1 |
| F.ROT/AID 11/005/00 | Recalificación Socio- Económico y Cultural del Centro Histórico de San Salvador | EUR | 12000000 | FROT/AID01 | 1 |
| F.ROT/AID 12/008/00 | Ampliación de Oferta Educativa de Educación Media para Mejorar la Producción en 12 Dptos. del País | EUR | 15000000 | FROT/AID02 | 1 |
| F.ROT/AID 13/003/00 | Programa de Prevención y de Rehabilitación de Jovenes en Riesgo y en Conflicto con la Ley | EUR | 5550000 | N/A | 1 |
| FAES | COMPRA DE 6 HELIC. FUERZA ARMA | USD | 5025434 | PC00001 | 1 |
| FDO.INT.COOP.Y DESAR | CONST.VIVIENDAS MITCH | USD | 4000000 | BCH0002 | 1 |
| FIDA 2000001431 | Programa Nacional de Transformación Económica Rural para el Buen Vivir-Rural Adelante | DEG | 3560000 | N/A | 9 |
| FIDA 2000001432 | Programa Nacional de Transformación Económica Rural para el Buen Vivir-Rural Adelante | EUR | 10850000 | N/A | 1 |
| FIDA 579-SV | PROG DE RECONST. Y MODER. RURA | SDR | 15650000 | MFI0006 | 1 |
| FIDA 728-SV | PROYECTO DE DESARROLLO Y MODERNIZACION RURAL PARA REGION CENTRAL Y PARACENTRAL | DEG | 9500000 | MFI008 | 1 |
| FIDA 784-SV | Proyecto de Desarrollo y Modernización Rural para la Región Oriental (PRODEMORO) | DEG | 963460.13 | N/A | 9 |
| FIDA E-6-SV | Proyecto de Des. y Moder. Rural para las reg. Central y Paracentral (PRODEMOR-Central)-Ampliación | EUR | 11150000 | MFI0009 | 1 |
| FIDA I-828-SV | Programa de Competitividad Territorial Rural (Amanecer Rural) | DEG | 11150000 | MFI00011 | 1 |
| FIDA-163 | CREDITO AGROPECUARIO III ETAPA | SDR | 5050000 | MFI0001 | 1 |
| FIDA-267 | DESARROLLO AGRICOLA PARA PEQUE | SDR | 6314293 | MFI0002 | 1 |
| FIDA-322 | REHABILITACION Y DESARROLLO PO | SDR | 9250000 | MFI0003 | 1 |
| FIDA-465 SV | PRODENOR-MAG | SDR | 13050000 | MFI0004 | 1 |
| FIDA-666 | Proyecto de Desarrollo y Modernización Rural de la Region Oriental (PRODEMORO) | DEG | 9950000 | MFI0007 | 1 |
| FIDA508-SV | DES. RURAL REG.CTRAL PRODAP II | SDR | 9550000 | MFI0005 | 1 |
| FIVEN-REPROG.ARR II | REPROG.DE ADEUDOS ARREGLO II | USD | 6120182 | BVE0001 | 1 |
| FMS-ES-917D | FMS-ES-917D C.P | USD | 41821389 | BEU0014 | 1 |
| FOCAP - AECID | Fondo Común de Apoyo Programático al Programa Comunidades Solidarias | EUR | 16000000 | N/A | 9 |
| FOCAP - LUXEMBURGO | Fondo Común de Apoyo Programático al Programa Comunidades Solidarias | EUR | 10000000 | N/A | 9 |
| GENERAL BANK CEL 90M | GENERAL BANK CEL 90M | BEF | 90000000 | BBET001 | 2 |
| GOBIERNO DE JAPON - BIENES IMPORTADOS | Gobierno de Japón – Bienes Importados | USD | 0 | N/A | 9 |
| GOJA/2KR | GOJA/2KR (DONACION) | - | 0 | N/A | 9 |
| GRT/SW-12281-ES | -- | USD | 1950000 | N/A | 9 |
| ICO (16907.0)HUELLA | HUELLA GENETICA | USD | 787195 | BES0021 | 1 |
| ICO (016901.0) | EQUI.MEDIC-INDUST RED HOSPITAL | USD | 3388863 | BES0002 | 1 |
| ICO (016902.0) | EQUIPAM.MED-QUIR.1-2-3 NIVELES | USD | 3554877 | BES0003 | 1 |
| ICO (016903.0) | INFORMATIZACION PNC-2000 | USD | 5741489 | BES0004 | 1 |
| ICO (16906.0) | SUM.EQ. REAC. UNID. INV.DES.C | USD | 3562138 | BES0007 | 1 |
| ICO (16908.0)POLIDEP | CONSTRUC. EQUI. C. POLIDEPORTI | EUR | 26000000 | BES0008 | 1 |
| ICO 01069015.0 | Programa de Caminos Rurales Progresivos y Mejoramiento de Caminos a Nivel Nacional | USD | 30000000 | BES0022 | 1 |
| ICO 016909.0 | APOYO AL EQUIPAMIENTO | USD | 2978554 | BES0009 | 1 |
| ICO BOMBERO | EQUIPAMIENTO DE RESCATE Y SALVAMENTO DEL CUERPO DE BOMBEROS | USD | 2778072 | BAL0006 | 1 |
| INST DE CREDITO OFIC | CONTRATO COMERCIAL DE SUMINIST | USD | 2846994 | BES0006 | 1 |
| INST.DE CREDITO OFIC | MEJORAS RIO LEMPA | USD | 15438600 | GBES003 | 2 |
| JBIC P5 (CEPA) | PROY.DES PTO.CUTUCO- CEPA | JPY | 11200000000 | BJAT005 | 1 |
| JBIC-ES-P3 | RECONSTR.PUENTE CUSCATLAN | JPY | 10300000000 | BJA0003 | 1 |
| JICA ES SB1 | Préstamo Contingencial para la recuperacion ante desastres naturales | YJP | 5000000000 | BJA0004 | 1 |
| JICA ES-F-P1 | Proyecto de Construcción del Bypass en la Ciudad de San Miguel | USD | 51370075 | S/N | 1 |
| JICA ES-P6 | Proyecto de Construcción de ByPass en la Ciudad de San Salvador | YJP | 12595000000 | BJAT06 | 1 |
| JPN 52119-ES | Don. Japonesa para la Preparacion de la Ref. Educativa Fase II | USD | 400000 | N/A | 9 |
| KfW 25488 | Expansión de Central Hidroeléctrica 5 Noviembre | USD | 57500000 | BALCEL001 | 1 |
| KFW 25815 | Apoyo al Plan Nacional para el Mejoramiento del Manejo de los Desechos Solidos de El Salvador | EUR | 15000000 | BAL0019 | 1 |
| KFW 26022 | Promoción Energía Solar El Salvador | USD | 22236000 | BALCEL002 | 1 |
| KFW 84-65-650 | PROG.HABIT.VIV.MIN.POPOTLAN II | DEM | 17500000 | BAL0003 | 1 |
| KFW 85-67-687 | AYUDA EN MERCANCIAS II | DEM | 20000000 | BAL0002 | 1 |
| KFW 86-66-034 | AYUDA EN MERCANCIAS III | DEM | 20000000 | BAL0005 | 1 |
| KFW 86-66-216 | TELECOMUNICAC.RURAL EN EL AREA | DEM | 13300000 | BAL0004 | 1 |
| KFW 87-66-370 | AGUA POTAB.SANEAM.BASIC RURAL | DEM | 14800000 | BAL0008 | 1 |
| KFW 91-65-473 | REHABILIT.PUERTO DE ACAJUTLA | DEM | 21362904 | BAL0011 | 1 |
| KFW 92-65-927 | AYUDA EN MERCANCIAS V | DEM | 9000000 | BAL0009 | 1 |
| KFW 92-65-943CITT | La Const.yEq.Taller.y Lab.CITT | DEM | 6400000 | BAL0010 | 1 |
| KFW 93-65-966 | PROGRAM. AJUSTE ESTRUC. FASE I | DEM | 20000000 | BAL0013 | 1 |
| KFW 94-65-665 | NUEVO CAMPUS UNIVERSITARIO | DEM | 7000000 | BAL0007 | 1 |
| KFW 94-65-907 | CONSTRUC.Y AMPLIAC. ICAS-UCA | DEM | 7834526 | BAL0015 | 1 |
| KFW 95-66-563 | AYUDA EN MERCANCIAS VI | DEM | 11500000 | BAL0014 | 1 |
| KFW CONVIVIR | Espacios Seguros de Convivencia para Jóvenes en El Salvador (CONVIVIR) | EUR | 17000000 | BAL0020 | 1 |
| KFW ES 2003 65 718 | Programa Fomento del Desarrollo Local y Gobernanza FISDL IV | EUR | 13994257.02 | BAL0018 | 1 |
| KFW No. 193004900 | Donacion de KFW por préstamo KFW -25815 | EUR | 1000000 | N/A | 9 |
| KFW-002-BMI9 | KFW-002 | DEM | 8000000 | FBAL002 | 2 |
| KFW-2001-65-811 | RECT. Y DES. LOCAL (FISDL III) | EUR | 2045167.5 | BAL0017 | 1 |
| KFW-2017.6872.0 | Aporte Financiero | EUR | 12551536.65 | -- | 9 |
| KFW-84-65-767-BFA | KFW-84-65-767-BFA FONDO | DEM | 8200000 | FBAL001 | 2 |
| KFW-84-67-672 | AYUDA EN MERCANCIAS I | DEM | 30000000 | BAL0001 | 1 |
| KFW-87-65-356 | AYUDA EN MERCANCIAS IV | DEM | 20000000 | BAL0006 | 1 |
| KFW-94-65-915 | PROGRAMA FIS-III ETAPA | DEM | 22500000 | BAL0012 | 1 |
| KFW-97-65-819 | AMPLIAC.FORTALEC.CITT-II | DEM | 5000000 | BAL0016 | 1 |
| KFW-BMZ-No. 2017.6524.7 | Adaptación Urbana al Cambio Climático en Centroamérica - Componente El Salvado | EUR | 23551536.65 | -- | 2 |
| MCC-CRM | Convenio del Reto del Milenio entre el Gobierno de la República y los Estados Unidos de América, MCC | USD | 460940000 | N/A | 9 |
| Modificar | Modificar Convenio | MON | 0 | N/A | 9 |
| N/A | &nbsp; | MON | 0 | N/A | 9 |
| NATEXIS BANQ 799 | NATEXIS BANQ 799-OA1 | FRF | 50000000 | BFRT001 | 2 |
| NATIXIS C34 0A1 | Proyecto de Rehabilitación de la Planta de Tratamiento de Agua Potable de Las Pavas y de su Red de Aducción | EUR | 53000000 | PR0001 | 1 |
| OECF-ES-P1-CEL PROG. | OECF-ES-P1-CEL PROG.EMERG. | JPY | 8150000000 | BJAT001 | 2 |
| OECF-ES-P2 ANDA | OECF-ES-P2 ANDA AGUA POTABLE | JPY | 1190000000 | BJAT002 | 2 |
| OECF-P4-CEL.DES-ELEC | OECF-ES-P4-CEL DES.ELECTRICO | JPY | 5500000000 | BJAT003 | 2 |
| OFID 1433P | Programa de Competitividad Territorial Rural (Amanecer Rural) | USD | 15000000 | MOFID001 | 1 |
| OFID 14611P | Proyecto de Respuesta COVID-19 El Salvador | USD | 15000000 | -- | 1 |
| PACSES - UE | Programa de Apoyo a Comunidades Solidarias (DCI-ALA/2011/022-647) | EUR | 47400000 | N/A | 9 |
| PAPSES - UE | Plan Nacional de Desarrollo, Protección e Inclusión Social | USD | 12000000 | N/A | 9 |
| PL-480 20% D.NO COND | PL-480 20% D.NO CONDONADA | USD | 67101271 | BEU0006 | 1 |
| PL-480 CLUB DE PARIS | PL-480 CLUB DE PARIS | USD | 1023669 | BEU0002 | 1 |
| PL-480/1993 CCC | PL-480/1993 CCC | USD | 33226729 | BEU0007 | 1 |
| PL-480/1995 CCC | PL-480/1995 CCC | USD | 9640803 | BEU0008 | 1 |
| PL-480/1996 CCC | PL-480/1996 CCC | USD | 12152231 | BEU0009 | 1 |
| PL-480/1997 CCC | PL-480/1997 CCC | USD | 9172006 | BEU0010 | 1 |
| PL-480/1998 CCC | PL-480/1998 CCC | USD | 4712050 | BEU0011 | 1 |
| PL-480/1999 | PL-480/1999 CCC | USD | 3783883 | BEU0017 | 1 |
| PL-480/90 CCC | PL-480/90 CCC | USD | 39642650 | BEU0001 | 1 |
| PL-480/91 CCC | PL-480/91 CCC | USD | 34819338 | BEU0003 | 1 |
| PL-480/92 CCC | PL-480/92 CCC | USD | 26416686 | BEU0004 | 1 |
| PL-480/92-A CCC | PL-480/92-A CCC | USD | 2804654 | BEU0005 | 1 |
| PL480-CCC | PL-480/2001 CCC | USD | 2486367 | PL480-CCC | 1 |
| S/N | S/N | USD | 0 | N/A | 9 |
| SFD 1/835 | Proyecto de Tratamiento de Aguas y Generación de Energía con Biogás a partir de Agua del Río Acelhuate | USD | 83000000 | N/A | 1 |
| SLV- 060-B | --- | USD | 0 | N/A | 9 |
| SLV-001-B | -- | USD | 23280644.52 | N/A | 9 |
| SLV-041-B | -- | USD | 4205472.45 | N/A | 9 |
| SLV-056-B | -- | USD | 53000000 | N/A | 9 |
| SLV-059-B | --- | USD | 0 | N/A | 9 |
| SVD/025 | Empleo Juvenil y Digitalización | EUR | 4522000 | N/A | 9 |
| TF024109 | -- | USD | 350000 | N/A | 9 |
| TF026534 | -- | USD | 250000 | N/A | 9 |
| TF0B9765-ES | Programa Crecer y Aprender Juntos: Desarrollo Integral de la Primera Infancia en El Salvador | USD | 3915630 | N/A | 1 |
| THE OPEC FUND | AYUDA A LA BALANZA DE PAGOS | USD | 1750000 | MOP0001 | 1 |
| TRANSTOOLS,S.A.DEC.V | ELAB.Y SUMINISTRO LIBRETAS PAS | USD | 8507330 | PRP0001 | 1 |
| YUTONG-BUS-1 | Contratación de Suministro de Vehículos a través de los cuales se brindará el Servicio Esencial de Transporte Público de Pasajeros tipo Colectivo con Financiamiento Incluido | CNY | 1301656680 | N/A | 1 |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Autocompletado de la columna "Producto" | CU-PRE-11 "Descripción técnica" | Pantalla "Presupuesto del Proyecto" (paso 2 del FB, RN16) |
| Generación de columnas de período | Registro de "Períodos estimados para la ejecución" + clic en "ACEPTAR" | Pantalla "Presupuesto del Proyecto" (paso 4 del FB, RN06) |
| Apertura de pantalla emergente "Detalle de Macroactividad" | Clic en "Agregar Macroactividad" | Anexo A.2 (paso 6 del FB) |
| Cálculo de precios ajustados por insumo | Registro de costos de insumo por período | Campo "Período 1 Precios Ajustados" (F1 paso 1.2) |
| Traslado de totales de macroactividad | Guardado en Anexo A.2 (FA-01.1) | Tabla "Presupuesto del proyecto" (RN07) |
| Cálculo automático del Resumen presupuesto por componente | Diligenciamiento de la tabla "Presupuesto del proyecto" | Anexo A.4 (RN09) |
| Actualización del "Costo etapa" | Clic en "Guardar" en Anexo A.1 (FA-02) | Campo "Costo etapa", fila "Ejecución", Anexo A.1 de CU-PRE-03.5 (RN15) |
| Guardado de información (Detalle de Macroactividad) | Botón "Guardar" en Anexo A.2 | Mensaje emergente "¡Guardado!" (Anexo A.3, FA-01.1) |
| Descarte de información (Detalle de Macroactividad) | Botón "Salir" en Anexo A.2 | Mensaje "Los datos no serán guardados" (FA-01.2) |
| Guardado de información (Presupuesto del proyecto) | Botón "Guardar" en Anexo A.1 | Mensaje emergente "¡Guardado!" (Anexo A.3, FA-02) |
| Navegación a siguiente pestaña | Botón "Siguiente" en Anexo A.1 | Siguiente pestaña (FA-03) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| CU-PRE-11 "Descripción técnica" | Interno (dentro del mismo Sistema de Información de Inversión Pública) | Fuente de los productos y de su clasificación por componente, utilizados en las pantallas "Presupuesto del Proyecto" y "Resumen presupuesto por componente" (RN16, Anexo B.1). |
| CU-PRE-03.5 "Selección y registro de etapas" | Interno (dentro del mismo Sistema de Información de Inversión Pública) | Recibe la actualización automática del campo "Costo etapa" de la fila "Ejecución" al guardar el Anexo A.1 (RN15). |
| Archivo Excel "CU-PRE-17 (ANEXO) Catálogos_Financ_Recur_Conv" | Externo al documento PDF (fuente de catálogos) | Fuente de los catálogos "Fuentes de Financiamiento" (7 valores, hoja "F Finan", ya resuelto — RQ-T-02), "Fuentes de Recursos" (191 valores, hoja "F Recursos") y "Convenios" (392 valores, hoja "Convenios"), estos dos últimos incorporados en la versión 1.1 (ver "Catálogos Detectados"). La decisión sobre cuál(es) de estos catálogos debe enlazar el campo "Fuente de Recursos" del Anexo A.5 sigue pendiente del Gestor del Dominio. |

---

# Datos Pendientes de Definir

- Prioridad del caso de uso: no especificada en el documento.
- Disparador (evento que inicia el caso de uso): no especificado explícitamente en el documento.
- Tabla de excepciones: no desarrollada en el documento.
- Tabla de errores/códigos de error: no desarrollada en el documento.
- Contenido del catálogo "Fuentes de Financiamiento": **resuelto (RQ-T-02)** — catálogo cerrado de 7 valores, ya incorporado en "Catálogos Detectados".
- **Actualización (v1.1):** los catálogos "Fuentes de Recursos" (191 valores) y "Convenios" (392 valores) ya se transcribieron íntegros en "Catálogos Detectados" a partir del anexo Excel. Sigue pendiente de decisión del Gestor del Dominio cuál(es) de estos catálogos debe enlazar efectivamente el campo "Fuente de Recursos" de la pantalla del Anexo A.5 (RQ-T-02 no resuelto en su totalidad).
- Significado de los códigos `CODIG_PAIS` (25 valores distintos) y `CODIG_CLASE_RECUR` (valores `1`/`2`) de la hoja "F Recursos", y del código `CODIG_TIPO_CONVE` (valores `1`, `2`, `6`, `9`) de la hoja "Convenios": estos catálogos no están incluidos en el anexo Excel ni en el PDF original.
- Significado de los valores atípicos de `MONED_CONVE` ("-", "MON", "IDB") y de los 3 registros de "Convenios" cuyo `CODIG_CONVE` es un valor de control ("Modificar", "N/A", "S/N") en lugar de un identificador real de convenio.
- Ambigüedad no resuelta en la redacción de las Precondiciones respecto a la relación exacta entre "la Ruta de Preinversión generada en" y los casos de uso CU-PRE-14, CU-PRE-15, CU-PRE-16, CU-PRE-11 y CU-PRE-09 (ver "Precondiciones").
- Ambigüedad no resuelta en la sección "Postcondiciones" respecto a la relación exacta de CU-PRE-21 y CU-PRO-01 con este caso de uso, dado que no se presenta un enunciado de postcondición principal ni un verbo explícito (ver "Postcondiciones").
- Inconsistencia no resuelta entre el nombre de pantalla "Detalle de Actividad" (usado en dos filas del Anexo B.1) y "Detalle de Macroactividad" (usado en el resto del documento) (ver "Observaciones").
- Discrepancia no resuelta entre los valores de ejemplo del mockup del Anexo A.1 (Inversión Estimada de $49,000.00) y del mockup del Anexo A.4 (todos los componentes y el total en $ -) (ver "Observaciones").
- Definición precisa del rol "actores internos" / "usuarios internos" mencionado en RN10 y RN11, y su relación con los roles "Técnico URP" y "Técnico PRE" ya definidos en RN01 y RN02: **resuelto (RQ-C-03)** para "usuarios internos" — es un grupo transversal (cualquier usuario del Ministerio de Hacienda), no un cuarto rol excluyente. Sin embargo, "actores internos" (RN11) fue posteriormente **redefinido por una anotación del especialista de dominio** como los roles Coordinador SYMP, Coordinador PRO, Técnico SYMP y Técnico PRO, lo cual **contradice** la premisa de sinonimia de RQ-C-03 entre "actores internos" y "usuarios internos". ⚠️ **Nuevo pendiente:** queda sin resolver si RQ-C-03 y el glosario compartido deben actualizarse para separar ambos conceptos, o si la anotación del especialista requiere revisión. Ver nota de conflicto en RN11 y en "Actores Secundarios".