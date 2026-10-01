---
id: CU-PRE-23
codigo: CU-PRE-23
nombre: Indicadores del Proyecto
modulo: Preinversión
submodulo: Programación
version: "1.0"
fuente_pdf: CU-PRE-23_Indicadores_del_Proyecto_AGO_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 11

actor_principal: Técnico URP

actores_secundarios: ["Técnico PRE"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-01", "CU-PRE-03.5", "CU-PRE-04", "CU-PRE-11", "CU-PRE-17", "CU-PRE-04 a CU-PRE-21"]

casos_relacionados: ["CU-PRO-18", "CU-EJE-02"]

roles: ["Técnico URP", "Técnico PRE"]

pantallas: ["Indicadores del proyecto (Anexo A.1)", "Registro de indicador de resultado (Anexo A.2)", "Registro de indicador de producto (Anexo A.3)", "Guardar (Anexo A.4)"]

procesos: []

servicios_externos: []

entidades: ["Indicador de Resultado", "Indicador de Producto", "Producto", "Período", "Catálogo C.1 Productos e Indicadores", "Catálogo C.2 Indicadores de Resultado"]

catalogos: ["C.1 Catálogo de Productos e Indicadores", "C.2 Catálogo de Resultados"]

palabras_clave: ["Indicadores", "Resultado", "Producto", "Meta global", "Peso relativo", "Período", "Catálogo de indicadores"]

ultima_actualizacion: "AGO 2025"

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB1:
      pagina: 3
  flujos_alternos:
    FA01:
      pagina: 4
    FA02:
      pagina: 4
    FB2:
      pagina: 5
    FB3:
      pagina: 5
  reglas_negocio:
    RN01:
      pagina: 5
    RN02:
      pagina: 5
    RN03:
      pagina: 5
    RN04:
      pagina: 5
    RN05:
      pagina: 5
    RN06:
      pagina: 5
    RN07:
      pagina: 6
    RN08:
      pagina: 6
    RN09:
      pagina: 6
    RN10:
      pagina: 6
  anexos:
    A1:
      nombre: Indicadores del proyecto
      pagina: 7
    A2:
      nombre: Registro de indicador de resultado
      pagina: 7
    A3:
      nombre: Registro de indicador de producto
      pagina: 7
    A4:
      nombre: Guardar
      pagina: 8
    B_A1:
      nombre: Requerimientos Funcionales - Formatos (Pantalla Anexo A1)
      pagina: 8
    B_A2:
      nombre: Requerimientos Funcionales - Formatos (Pantalla Anexo A2)
      pagina: 9
    B_A3:
      nombre: Requerimientos Funcionales - Formatos (Pantalla Anexo A3)
      pagina: 9
    C1:
      nombre: Catálogo de Productos e Indicadores
      pagina: 10
    C2:
      nombre: Catálogo de Resultados
      pagina: 11
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|-------|---------|--------------|-------|-------|
| AGO 2025 | 1.0 | Versión Inicial | Equipo Preinversión | No especificado en el documento. |

---

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Indicadores del Proyecto |
| Código | CU-PRE-23 |
| Módulo | Preinversión |
| Fuente | CU-PRE-23 Indicadores del Proyecto_AGO 2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Indicadores de resultado
  - Objetivo general
  - Código
  - Nombre del indicador
  - Descripción del indicador
  - Meta
  - Unidad de Medida
- Indicadores de producto
  - Producto
  - Peso relativo del producto
  - Código
  - Nombre del indicador
  - Descripción del indicador
  - ¿La meta es acumulativa?
  - Meta
  - Unidad de Medida
  - Período

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permitirá al Técnico URP registrar los indicadores de Resultado y de Producto. Asimismo, permitirá programar la meta global del indicador de producto.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Técnico PRE (mencionado en RN02, con permisos de solo visualización sobre todas las Unidades Ejecutoras).

> Nota: el campo "Actores" de la Identificación del caso de uso solo lista a "Técnico URP". La mención de "Técnico PRE" y de "todos los demás actores" proviene de las Reglas de Negocio RN01 y RN02.

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP de CU-PRE-01 "Registro de Proyectos".
2. Contar con la Ruta de Preinversión generada en CU-PRE-03.5 "Selección y registro de etapas".
3. Contar con el proyecto formulado (CU-PRE-04 a CU-PRE-21).

> Nota: el ítem 3 hace referencia a un rango de casos de uso ("CU-PRE-04 a CU-PRE-21") sin listarlos individualmente. Se transcribe tal como aparece en el documento, sin desagregar el rango.

---

# Flujo Principal

## FB1 – Flujo Básico 1

1. Técnico URP ingresa a la pantalla "Programación del Proyecto" en la sección "Indicadores del proyecto".
2. Sistema despliega la tabla mostrada en el Anexo A.1. En los "Indicadores de resultado" muestra automáticamente en el campo "Objetivo General" registrado en el CU-PRE-04 "Identificación" en el campo del mismo nombre. En los "Indicadores de producto" muestra automáticamente los productos registrados en el CU-PRE-11 "Descripción técnica", cada producto mostrará por defecto el indicador principal según se establece en el catálogo C.1 Catálogo de Productos e Indicadores, además los campos "Código del indicador", "Nombre del indicador", "Descripción del indicador" y "Unidad de medida" para el indicador principal, ya se mostrarán con la información completa según catálogo C.1. En el campo "Peso relativo del producto" muestra el resultado de la fórmula:

> [Fórmula tal como aparece en el documento fuente, transcripción literal de los caracteres extraídos, con caracteres no legibles con claridad]: "(݋ܿܽ݀ݎ݉݁ ݀݁ ݏ݋݁ܿ݅ݎ݌) ݊ó݅ݏݎ݁ݒ݅݊ ݈ܽݐ݋/ܶ݋ݐܿݑ݀݋ݎ݌ ݈݀݁ ݋ݐݏ݋ܥ = ݋ݒ݅ݐ݈݁ܽݎ ݋ݏ݁��". El propio texto que acompaña la fórmula aclara en prosa que "El costo del producto proviene del CU-PRE-17 'Presupuesto de inversión'" y (según el Anexo B) que el peso relativo se calcula dividiendo el "Costo del producto" entre el "Total inversión (precios de mercado)", ambos procedentes del Anexo A1 del CU-PRE-17.

3. Técnico URP da clic en el botón "Agregar indicador".

---

# Flujos Alternos

> Nota: el documento denomina "Flujo Básico 2" y "Flujo Básico 3" a los flujos de "Guardar" y "Siguiente" de la pantalla del Anexo A.1, en lugar de usar la nomenclatura "Flujo Alternativo" empleada para ellos en otros casos de uso de la serie. Se incorporan en esta sección "Flujos Alternos" por consistencia estructural con la plantilla, preservando sus títulos literales ("FB2", "FB3") tal como aparecen en el documento fuente.

## FA01 – Flujo Alternativo 1 Agregar indicador de resultado

**Condición**

> No especificado en el documento.

**Flujo**

1.1. Sistema muestra la pantalla del Anexo A.2.

1.2. Técnico URP selecciona un indicador del listado mostrado en el campo "Nombre del indicador". Este listado procede del catálogo C.2 "Indicadores de Resultado", y completa la información en el campo "Meta global".

1.3. Sistema completa de manera automática, según el indicador seleccionado, los campos "Código", "Descripción del indicador" y "Unidad de Medida".

1.4. Técnico URP da clic en el botón "Guardar" o "Salir".

1.5. Sistema: si la opción es "Guardar" guarda los datos registrados y los traslada a los campos correspondientes de la tabla "Indicadores de resultado" del Anexo A.1 y regresa a la pantalla del Anexo A.1. Si la opción es "Salir" regresa al Anexo A.1 sin guardar los cambios.

**Resultado**

> No especificado en el documento.

## FA02 – Flujo Alternativo 2 Agregar indicador de producto

**Condición**

> No especificado en el documento.

**Flujo**

2.1. Sistema muestra la pantalla del Anexo A.3.

2.2. Técnico URP selecciona un indicador del listado mostrado en el campo "Nombre del indicador". Este listado procede del catálogo "Indicadores de producto", completa la información en el campo "Meta global", "¿La meta es acumulativa?", y en los campos Período.

2.3. Sistema completa de manera automática, según el indicador seleccionado, los campos "Código", "Descripción del indicador" y "Unidad de Medida", y calcula el valor del campo "Total".

2.4. Técnico URP da clic en el botón "Guardar" o "Salir".

2.5. Sistema: si la opción es "Guardar", valida que la meta total de alguno de los indicadores del respectivo producto sea igual a la meta total del producto registrado en CU-PRE-11 "Descripción técnica", guarda los datos registrados y los traslada a los campos correspondientes de la tabla "Indicadores de producto" del Anexo A.1 y regresa a la pantalla del Anexo A.1. En caso que ningún indicador tenga como meta total la cantidad total del producto registrado en CU-PRE-11 "Descripción técnica", el sistema mostrará una advertencia "Al menos uno de los productos debería programar la meta del producto registrado en la descripción técnica". Este será requisito para que la pantalla quede completa. Si la opción es "Salir" regresa al Anexo A.1 sin guardar los cambios.

**Resultado**

> No especificado en el documento.

> Nota: el paso 2.2 hace referencia al catálogo como "Indicadores de producto", mientras que el paso 2 del Flujo Básico 1 y la pantalla Anexo A3 (Anexo B) lo denominan "catálogo C.1" o "catálogo C1". No se aclara si se trata del mismo catálogo con nombres alternativos o de catálogos distintos.

## FB2 – Flujo Básico 2 Guardar (en pantalla del Anexo A.1)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón "Guardar".
2. Sistema muestra mensaje emergente del Anexo A.4.
3. Técnico URP da clic en "Aceptar" al mensaje emergente.
4. Sistema guarda la información registrada y se mantiene en la pestaña "Presupuesto del proyecto".

**Resultado**

> No especificado en el documento.

> Nota de ambigüedad: el paso 4 indica que el Sistema "se mantiene en la pestaña 'Presupuesto del proyecto'"; sin embargo, este caso de uso trata sobre la pantalla "Programación del Proyecto" / sección "Indicadores del proyecto" (FB1, paso 1), no sobre "Presupuesto del proyecto". Ver Observaciones.

## FB3 – Flujo Básico 3 Siguiente (en pantalla del Anexo A.1)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP da clic en el botón "Siguiente".
2. Sistema avanza al apartado "Gestión del proyecto".

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no desarrolla una tabla de excepciones con código, descripción y consecuencia para este caso de uso.

# Postcondiciones

1. CU-PRO-18 "Programación física mensual del PAIP".
2. CU-EJE-02 "Avance mensual físico del PAIP".

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP es el único que puede registrar/editar información en la pestaña "Indicadores del Proyecto", según credenciales. Todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN03

**Descripción:** El Técnico URP podrá adicionar o eliminar filas de los indicadores la pantalla "Indicadores del Proyecto".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN04

**Descripción:** Para cada proyecto será obligatorio el registro de por lo menos un indicador de "Resultado" y uno para cada "Producto".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN05

**Descripción:** La información de la columna "Producto" procede del campo "Producto" del Anexo A.1 del CU-PRE-11 "Descripción técnica".

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN06

**Descripción:** En los CU de Administración del Catálogo de Indicadores deberán establecerse reglas para el mantenimiento periódico del mismo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN07

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN08

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN09

**Descripción:** Todos los productos deben traer automáticamente su respectivo indicador principal con código, descripción y unidad de medida. Sin embargo, cada producto podrá incluir indicadores adicionales si lo considera necesario. Al seleccionarlos, el sistema deberá traer la información del catálogo para todos los campos anteriormente mencionados.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

## RN10

**Descripción:** El Sistema por defecto cargará para cada producto el indicador principal asociado procedente del catálogo C1; y permitirá el registro y programación de las metas en la pantalla del Anexo A.2. Será obligatorio que cada producto cuente por lo menos con el indicador principal.

El Sistema permitirá agregar más indicadores secundarios a cada producto por medio del botón "Agregar indicador". Esto será opcional.

**Origen:** Sección "Reglas del Negocio" del documento CU-PRE-23.

> Nota de ambigüedad: RN10 indica que el registro y programación de las metas de producto se realiza "en la pantalla del Anexo A.2"; sin embargo, según el resto del documento (FA02, y la tabla de formatos del Anexo B), la pantalla de registro de indicadores de producto es el Anexo A.3 "Registro de Indicador de Producto", mientras que el Anexo A.2 corresponde a "Registro de Indicador de Resultado". No se resuelve esta discrepancia; se transcribe tal como aparece en el documento. Ver Observaciones y "Datos Pendientes de Definir".

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Objetivo general (Anexo A1) | Campo que muestra el objetivo general del proyecto. Procede del campo "Objetivo General" del CU-PRE-04 "Identificación". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Código (Indicadores de Resultado, Anexo A1) | Muestra el código del indicador de resultado. Procede del campo "Código" del Anexo A2. | Alfanumérico | Alfanumérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Nombre del indicador (Indicadores de Resultado, Anexo A1) | Muestra el nombre del indicador de resultado. Procede del campo "Nombre del indicador" del Anexo A2. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Descripción del indicador (Indicadores de Resultado, Anexo A1) | Muestra la descripción del indicador de resultado. Procede del campo "Descripción del indicador" del Anexo A2. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Meta global (Indicadores de Resultado, Anexo A1) | Muestra la Meta global del indicador de resultado. Procede del campo "Meta global" del Anexo A2. | Numérico / Porcentaje | Numérico / Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Unidad de medida (Indicadores de Resultado, Anexo A1) | Muestra la Unidad de medida del indicador de resultado. Procede del campo "Unidad de medida" del Anexo A2. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Producto (Indicadores de Producto, Anexo A1) | Muestra los productos del proyecto. La información procede del campo "Producto" del Anexo A1 del CU-PRE-11 "Descripción técnica" (RN05). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Peso relativo del producto (Anexo A1) | Muestra el peso relativo de cada producto. El Sistema lo calcula según la fórmula siguiente: [fórmula no legible con claridad en el documento fuente, transcripción literal: "(ocádrmé dé sóécírp) nóísrévín latő/Totcudord lédě otsoC = ovitelar oseP" — ver nota en Flujo Básico 1]. El valor del Costo del producto procede del campo "Costo del producto" de cada producto del Anexo A1 del CU-PRE-17 "Presupuesto de inversión". El valor del Total inversión (precios de mercado) procede del campo del mismo nombre del Anexo A1 del CU-PRE-17 "Presupuesto de inversión". | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Código (Indicadores de Producto, Anexo A1) | Muestra el código del indicador. Procede del campo "Código" del Anexo A3. | Alfanumérico | Alfanumérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Nombre del indicador (Indicadores de Producto, Anexo A1) | Muestra el nombre del indicador. Procede del campo "Nombre del Indicador" del Anexo A3. | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Descripción del indicador (Indicadores de Producto, Anexo A1) | Muestra la descripción del indicador de resultado. Procede del campo "Descripción del indicador" del Anexo A3. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a que la Detalle dice "indicador de resultado" estando en la sección "Indicadores de Producto". |
| ¿La meta es acumulativa? (Indicadores de Producto, Anexo A1) | Muestra el valor "Sí" o "No" según se haya seleccionado desde el campo del mismo nombre en el Anexo A3. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Meta global (Indicadores de Producto, Anexo A1) | Muestra la Meta global del indicador de resultado. Procede del campo "Meta global" del Anexo A3. | Numérico / Porcentaje | Numérico / Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a que la Detalle dice "indicador de resultado" estando en la sección "Indicadores de Producto". |
| Unidad de Medida (Indicadores de Producto, Anexo A1) | Muestra la Unidad de medida del indicador de resultado. Procede del campo "Unidad de medida" del Anexo A3. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones respecto a que la Detalle dice "indicador de resultado" estando en la sección "Indicadores de Producto". |
| Nombre del indicador (Anexo A2) | Espacio para que el Técnico URP seleccione el indicador correspondiente de un listado definido en el catálogo "C2". El sistema permitirá filtrar por palabra los indicadores (cuando el Técnico URP digite una palabra u otro atributo clave, el sistema mostrará un listado filtrado con los indicadores que contienen dicha palabra o que pertenecen a dicho atributo). Campo obligatorio. Se debe seleccionar al menos un indicador de Resultado. | Texto | Texto | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: No (según el Anexo B). Ver Observaciones respecto a que la Detalle describe un campo de selección interactiva por el Técnico URP y el mockup lo muestra como un desplegable. |
| Código (Anexo A2) | Asociado al campo "Nombre del indicador". Dependerá del indicador seleccionado, por lo que el Sistema lo asignará automáticamente de acuerdo con lo establecido en el catálogo "C2". Se crea desde el catálogo de indicadores. | Alfanumérico | Alfanumérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Descripción del indicador (Anexo A2) | Asociado al campo "Nombre del Indicador". Dependerá del indicador seleccionado, por lo que el Sistema lo asignará automáticamente de acuerdo con lo establecido en el catálogo "C2". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Meta global (Anexo A2) | Espacio para que el Técnico URP registre la meta global correspondiente al indicador de resultado. Campo obligatorio. | Numérico / Porcentaje | Numérico / Porcentaje | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. |
| Unidad de medida (Anexo A2) | Corresponde a la unidad de medida del indicador, se diligencia automáticamente del catálogo, trayendo la que corresponde al indicador seleccionado. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Nombre del indicador (Anexo A3) | Espacio para que el Técnico URP seleccione el indicador correspondiente de un listado definido en el catálogo "C1". El sistema permitirá filtrar por palabra los indicadores. Campo obligatorio. Se debe seleccionar al menos un indicador de cada producto. | Texto | Texto | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: No (según el Anexo B). Ver Observaciones respecto a que la Detalle describe un campo de selección interactiva por el Técnico URP y el mockup lo muestra como un desplegable. |
| Código (Anexo A3) | Asociado al campo "Nombre del indicador". Dependerá del indicador seleccionado, por lo que el Sistema lo asignará automáticamente de acuerdo con lo establecido en el catálogo "C1". Se crea desde el catálogo de indicadores. | Alfanumérico | Alfanumérico | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Descripción del indicador (Anexo A3) | Asociado al campo "Nombre del Indicador". Dependerá del indicador seleccionado, por lo que el Sistema lo asignará automáticamente de acuerdo con lo establecido en el catálogo "C1". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| Meta global (Anexo A3) | Espacio para que el Técnico URP registre la meta global correspondiente al indicador. Campo obligatorio. | Numérico / Porcentaje | Numérico / Porcentaje | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. |
| Unidad de medida (Anexo A3) | Corresponde a la unidad de medida del indicador, se diligencia automáticamente del catálogo, trayendo la que corresponde al indicador seleccionado. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No. |
| ¿La meta es acumulativa? (Anexo A3) | Campo para que el Técnico URP seleccione si la meta es o no acumulativa. Campo obligatorio. | Radial | Radial | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: No (según el Anexo B). Ver Observaciones respecto a que la Detalle describe un campo seleccionable por el Técnico URP mediante botón radial. |
| Períodos (1 a n) (Anexo A3) | Espacio para que el Técnico URP registre la programación anual de la Meta Global del indicador de producto. Deberá registrarse información en por lo menos un período. Campo obligatorio. El Sistema deberá validar que la suma de las metas programadas en los períodos sea igual al valor del campo "Meta Global", en caso contrario mostrará el mensaje de error: "ERROR. La suma de los períodos debe ser igual a la Meta Global". El Sistema mostrará la cantidad de períodos determinados en el CU-PRE-17 "Presupuesto de inversión"; la nomenclatura de las columnas iniciará a partir del período 0 hasta el período n. | Numérico / Porcentaje | Numérico / Porcentaje | Sí (indicado explícitamente como "Campo obligatorio") | No especificado en el documento. | Editable: Sí. Ver Observaciones y "Datos Pendientes de Definir" respecto a la contradicción entre el inicio de la nomenclatura "a partir del período 0" (Detalle), el nombre del propio campo "(1 a n)", y el mockup del Anexo A.3, que muestra las columnas "PERÍODO 1", "PERÍODO 2", "PERÍODO 3". |
| Total (Anexo A3) | Muestra la suma de la ponderación de los productos. El Sistema realizará el cálculo automáticamente. | Numérico / Porcentaje | Numérico / Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No. Ver Observaciones y "Datos Pendientes de Definir": esta descripción ("suma de la ponderación de los productos") no coincide con el mockup del Anexo A.3, donde el campo "Total" muestra la suma de los períodos del indicador (25+30+15=70.00), no una ponderación de productos. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Campos pendientes de completar (general, al dar clic en "Guardar") | El sistema sombreará los bordes de los campos pendientes en color rojo (RN07). | No especificado en el documento (solo se describe el comportamiento visual de sombreado en rojo). |
| Nombre del indicador (Anexo A2) | Campo obligatorio. Se debe seleccionar al menos un indicador de Resultado. | No especificado en el documento. |
| Meta global (Anexo A2) | Campo obligatorio. | No especificado en el documento. |
| Nombre del indicador (Anexo A3) | Campo obligatorio. Se debe seleccionar al menos un indicador de cada producto. | No especificado en el documento. |
| Meta global (Anexo A3) | Campo obligatorio. | No especificado en el documento. |
| ¿La meta es acumulativa? (Anexo A3) | Campo obligatorio. | No especificado en el documento. |
| Períodos (1 a n) (Anexo A3) | Campo obligatorio. Debe registrarse información en por lo menos un período. La suma de las metas programadas en los períodos debe ser igual al valor del campo "Meta Global". | "ERROR. La suma de los períodos debe ser igual a la Meta Global" |
| Indicadores de producto (validación a nivel de pantalla, FA02 paso 2.5) | Al guardar, la meta total de al menos uno de los indicadores del producto debe ser igual a la meta total del producto registrado en CU-PRE-11 "Descripción técnica". | "Al menos uno de los productos debería programar la meta del producto registrado en la descripción técnica" |

---

# Errores

> No especificado en el documento. El documento no desarrolla una tabla de códigos de error para este caso de uso (los dos mensajes de error/advertencia identificados se documentan en las secciones "Validaciones" y "Mensajes al Usuario").

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Registrar/editar información en la pestaña "Indicadores del Proyecto"; adicionar o eliminar filas de indicadores. | RN01, RN03 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN02 |
| Todos los demás actores (no especificados individualmente en el documento) | Visualizar únicamente la información de las Unidades Ejecutoras, según credenciales. | RN01 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos" (precondición)
- CU-PRE-03.5 "Selección y registro de etapas" (precondición)
- CU-PRE-04 "Identificación" (precondición, dentro del rango citado; origen del campo "Objetivo General")
- CU-PRE-11 "Descripción técnica" (precondición, dentro del rango citado; origen del campo "Producto" y de la meta total del producto)
- CU-PRE-17 "Presupuesto de inversión" (precondición, dentro del rango citado; origen de "Costo del producto", "Total inversión (precios de mercado)" y cantidad de períodos)
- CU-PRE-04 a CU-PRE-21 (rango de casos de uso citado como precondición, "proyecto formulado")
- CU-PRO-18 "Programación física mensual del PAIP" (postcondición)
- CU-EJE-02 "Avance mensual físico del PAIP" (postcondición)

**Procesos relacionados:**
> No especificado en el documento.

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Pantalla: Indicadores del proyecto (Anexo A.1)

**Descripción:** Pantalla identificada en el encabezado como "INDICADORES DE RESULTADO" e "INDICADORES DE PRODUCTO", donde se consolidan los indicadores de resultado y de producto del proyecto.

**Campos:**
- Tabla "Indicadores de Resultado": Objetivo General, Código del indicador, Nombre del indicador, Descripción del indicador, Meta Global, Unidad de Medida
- Tabla "Indicadores de Producto": Producto, Peso relativo del producto, Código del indicador, Nombre del indicador, Descripción del indicador, ¿La meta es acumulativa?, Meta Global, Unidad de Medida, Período (1 a n)

**Botones:**
- Agregar Indicador (en cada fila de ambas tablas)
- Guardar
- Siguiente

**Acciones:**
- Clic en "Agregar Indicador" en la tabla de resultado abre el Anexo A.2 (FA01).
- Clic en "Agregar Indicador" en la tabla de producto abre el Anexo A.3 (FA02).
- Clic en "Guardar" (FB2).
- Clic en "Siguiente" (FB3).

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

Tabla "INDICADORES DE RESULTADO":

| OBJETIVO GENERAL | CÓDIGO DEL INDICADOR | NOMBRE DEL INDICADOR | DESCRIPCIÓN DEL INDICADOR | META GLOBAL | UNIDAD DE MEDIDA |
|---|---|---|---|---|---|
| Contribuir a reducir los tiempos de viaje entre las ciudades A y B | IR001 | Disminución de tiempo de viaje | Mide las reducciones planeadas en tiempo de viaje | 50 | % |

Tabla "INDICADORES DE PRODUCTO":

| PRODUCTO | PESO RELATIVO DEL PRODUCTO | CÓDIGO DEL INDICADOR | NOMBRE DEL INDICADOR | DESCRIPCIÓN DEL INDICADOR | ¿LA META ES ACUMULATIVA? | META GLOBAL | UNIDAD DE MEDIDA | PERÍODO 1 | PERÍODO 2 | PERÍODO 3 |
|---|---|---|---|---|---|---|---|---|---|---|
| Vía construida | 75.00% | IP001 | Vía primaria construida | Compara los km construidos contra lo que se planea construir | SI | 70 | km | 25 | 30 | 15 |
| Equipo de video vigilancia instalado | 20.00% | IP002 | Equipos instalados | Compara... | SI | 150 | unidad | 0 | 50 | 100 |
| Habitantes de la zona capacitados | 5.00% | IP003 | Personas capacitadas | Compara... | SI | 600 | personas | 200 | 200 | 200 |
| **TOTAL** | **100.00%** | | | | | | | | | |

> Nota: la descripción de los indicadores IP002 e IP003 aparece truncada en el mockup como "Compara..." (texto incompleto en la imagen original); se transcribe tal como se muestra.

## Pantalla: Registro de indicador de resultado (Anexo A.2)

**Descripción:** Pantalla modal donde el Técnico URP registra un indicador de resultado.

**Campos:**
- Nombre del indicador
- Código
- Descripción del indicador
- Meta global
- Unidad de medida

**Botones:**
- Guardar
- Salir

**Acciones:**
- Selección del indicador y registro de la meta global (FA01, paso 1.2).
- Clic en "Guardar" o "Salir" (FA01, pasos 1.4–1.5).

**Ejemplo de datos mostrados en el mockup (Anexo A.2):**

- Nombre del indicador: Disminución de tiempo de viaje
- Código: IR001
- Descripción del indicador: Mide las reducciones planeadas en tiempo de viaje
- Meta global: 50.00
- Unidad de medida: %

> Nota: el código "IR001" y la descripción de este mockup no coinciden con los códigos ni nombres del catálogo "C.2 Catálogo de Resultados" (que usa códigos "R2201100", "R2201101", etc.). Ver Observaciones.

## Pantalla: Registro de indicador de producto (Anexo A.3)

**Descripción:** Pantalla modal donde el Técnico URP registra un indicador de producto y programa su meta global por período.

**Campos:**
- Nombre del indicador
- Código del Indicador
- Descripción del indicador
- Meta global
- Unidad de medida
- ¿La meta es Acumulativa?
- Tabla "Programación de la meta global (1 a n)": Período (1 a n), Total

**Botones:**
- Guardar
- Salir

**Acciones:**
- Selección del indicador, registro de meta global, de si la meta es acumulativa y de la programación por período (FA02, paso 2.2).
- Clic en "Guardar" o "Salir" (FA02, pasos 2.4–2.5).

**Ejemplo de datos mostrados en el mockup (Anexo A.3):**

- Nombre del indicador: Vía primaria construida
- Código del Indicador: IP001
- Descripción del indicador: Compara los km construidos contra lo que se planea construir
- Meta global: 70.00
- Unidad de medida: km
- ¿La meta es Acumulativa?: Sí (seleccionado)

| PERÍODO 1 | PERÍODO 2 | PERÍODO 3 | TOTAL |
|-----------|-----------|-----------|-------|
| 25 | 30 | 15 | 70.00 |

> Nota: los encabezados de columna en este mockup son "PERÍODO 1", "PERÍODO 2", "PERÍODO 3" (inician en 1), lo cual contradice la Detalle del campo "Períodos (1 a n)" del Anexo B, que indica que "la nomenclatura de las columnas iniciará a partir del período 0". Ver Observaciones y "Datos Pendientes de Definir".

## Pantalla: Guardar (Anexo A.4)

**Descripción:** Ventana modal de confirmación mostrada al guardar información.

**Campos:** No aplica (mensaje informativo).

**Botones:**
- Aceptar

**Acciones:**
- Confirmación de guardado exitoso.

**Ejemplo de datos mostrados en el mockup (Anexo A.4):**
- Ícono de confirmación (check).
- Texto: "¡Guardado!"
- Texto: "Sus datos han sido guardados exitosamente."
- Botón: "Aceptar"

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Al dar clic en "Guardar" en la pantalla del Anexo A.1 (FB2); se muestra el mensaje emergente del Anexo A.4. |
| Error de validación | "ERROR. La suma de los períodos debe ser igual a la Meta Global" | Cuando, en el Anexo A.3, la suma de las metas programadas en los períodos no es igual al valor del campo "Meta Global" (Anexo B, campo "Períodos (1 a n)"). |
| Advertencia | "Al menos uno de los productos debería programar la meta del producto registrado en la descripción técnica" | Cuando, al guardar un indicador de producto (FA02, paso 2.5), ningún indicador tiene como meta total la cantidad total del producto registrado en CU-PRE-11 "Descripción técnica". |
| Ayuda contextual | No especificado en el documento (solo se indica que el Sistema "indicará qué información se debe completar en dicho campo"). | Al acercar el cursor a un campo y dar clic en el ícono "?" (RN08). |

---

# Observaciones

- El paso 4 del Flujo Básico 2 (Guardar) indica que el Sistema "se mantiene en la pestaña 'Presupuesto del proyecto'"; sin embargo, este caso de uso trata sobre la pantalla "Programación del Proyecto" / sección "Indicadores del proyecto" (FB1, paso 1), no sobre "Presupuesto del proyecto". Parece un posible residuo de la plantilla de otro caso de uso de la serie. Se transcribe tal como aparece en el documento.
- RN10 indica que el registro y programación de las metas de producto se realiza "en la pantalla del Anexo A.2"; sin embargo, según el Flujo Alternativo 2 (FA02) y la tabla de formatos del Anexo B, la pantalla de registro de indicadores de producto es el Anexo A.3 "Registro de Indicador de Producto" (el Anexo A.2 corresponde a "Registro de Indicador de Resultado"). No se resuelve esta discrepancia.
- En la sección "Indicadores de Producto" de la tabla de formatos del Anexo A1 (Anexo B), las descripciones de los campos "Descripción del indicador", "Meta global" y "Unidad de Medida" indican textualmente "indicador de resultado" en lugar de "indicador de producto", pese a estar documentados dentro de la sección de "Indicadores de Producto". Se transcribe tal como aparece en el documento, sin corregir la denominación.
- Los campos "Nombre del indicador" (Anexo A2 y Anexo A3) y "¿La meta es acumulativa?" (Anexo A3) se documentan en el Anexo B con la columna "Editable" en "No"; sin embargo, sus respectivas Detalle los describen como campos que el Técnico URP "selecciona" (de un listado desplegable o mediante botón radial), y los mockups de Anexo A.2 y Anexo A.3 los muestran como campos interactivos (desplegable y botones radiales, respectivamente). No se resuelve esta discrepancia.
- El campo "Períodos (1 a n)" (Anexo A3, Anexo B) indica en su Detalle que "la nomenclatura de las columnas iniciará a partir del período 0 hasta el período n"; sin embargo, el propio nombre del campo es "Períodos (1 a n)" (no "(0 a n)"), y tanto el mockup del Anexo A.1 como el del Anexo A.3 muestran los encabezados de columna como "PERÍODO 1", "PERÍODO 2", "PERÍODO 3" (iniciando en 1, no en 0). Existe una contradicción entre el nombre del campo, su Detalle y los mockups. Ver "Datos Pendientes de Definir".
- El campo "Total" de la pantalla Anexo A3 (Anexo B) se describe como "Muestra la suma de la ponderación de los productos"; sin embargo, en el contexto del Anexo A.3 (registro de un único indicador de producto), y según el mockup correspondiente, el campo "Total" muestra la suma de los períodos de ese indicador (25+30+15=70.00), no una ponderación de productos (concepto que corresponde más bien al campo "Peso relativo del producto" y a la fila "TOTAL" de la tabla "Indicadores de Producto" del Anexo A.1, cuyo total es 100.00%). Existe una posible descripción cruzada o mal ubicada entre ambos campos. Ver "Datos Pendientes de Definir".
- Los ejemplos de indicadores mostrados en los mockups de los Anexos A.1, A.2 y A.3 (códigos IR001, IP001, IP002, IP003; nombres "Disminución de tiempo de viaje", "Vía primaria construida", "Equipos instalados", "Personas capacitadas") no coinciden con los códigos ni nombres registrados en los catálogos "C.1 Catálogo de Productos e Indicadores" (códigos de producto 2201021, códigos de indicador 220105100–220105102, producto "Infraestructura educativa construida") ni "C.2 Catálogo de Resultados" (códigos R2201100, R2201101, R2201102). Existe una discrepancia entre los catálogos documentados y los ejemplos de pantalla.
- La tercera fila del catálogo "C.2 Catálogo de Resultados" (código "R2201102") no incluye nombre de indicador, descripción ni unidad de medida; se transcribe la fila tal como aparece, incompleta, sin completar la información faltante.
- El paso 2.2 del Flujo Alternativo 2 (FA02) hace referencia al catálogo de indicadores de producto como "catálogo 'Indicadores de producto'", mientras que el paso 2 del Flujo Básico 1 y la tabla de formatos del Anexo A3 lo denominan "catálogo C.1" o "catálogo C1". No se aclara si se trata del mismo catálogo con nombres alternativos.
- El Flujo Básico 3 (FB3, "Siguiente") indica que el Sistema "avanza al apartado 'Gestión del proyecto'", mientras que las Postcondiciones del caso de uso mencionan específicamente "CU-PRO-18" y "CU-EJE-02". El documento no aclara la relación exacta entre el apartado "Gestión del proyecto" y estos dos casos de uso postcondición.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Indicador de Resultado | Indicador registrado para medir el objetivo general del proyecto. | Registro (FA01, paso 1.2); traslado a la tabla "Indicadores de resultado" del Anexo A.1 (FA01, paso 1.5); adición/eliminación de filas (RN03); obligatoriedad de al menos uno por proyecto (RN04). |
| Indicador de Producto | Indicador registrado para medir el avance de un producto del proyecto, con programación de meta por período. | Registro (FA02, paso 2.2); traslado a la tabla "Indicadores de producto" del Anexo A.1 (FA02, paso 2.5); validación de meta total contra CU-PRE-11 (FA02, paso 2.5); indicador principal automático por producto (RN09, RN10); adición/eliminación de filas (RN03); obligatoriedad de al menos uno por producto (RN04). |
| Producto | Producto del proyecto, originado en CU-PRE-11 "Descripción técnica". | Visualización automática (RN05); cálculo del "Peso relativo del producto" (fórmula, Anexo B). |
| Período | Columna de programación de la meta global de un indicador de producto. | Registro de la meta programada por período (Anexo A3); validación de que la suma de períodos sea igual a la Meta Global (Anexo B, campo "Períodos (1 a n)"); cálculo automático del campo "Total" (FA02, paso 2.3). |
| Catálogo C.1 (Productos e Indicadores) | Catálogo de productos con su indicador principal y factor de identificación. | Consulta/selección para asignar indicador principal y secundarios de producto (RN09, RN10; FB1, paso 2; FA02, paso 2.2). |
| Catálogo C.2 (Indicadores de Resultado) | Catálogo de indicadores de resultado disponibles para selección. | Consulta/selección del indicador de resultado (FA01, paso 1.2). |

---

# Catálogos Detectados

## C.1 Catálogo de Productos e Indicadores

| Código del Producto | Producto | Descripción | Código del Indicador de Producto | Indicador de Producto | Unidad de medida | Indicador Principal |
|----------------------|----------|--------------|-------------------------------------|--------------------------|--------------------|------------------------|
| 2201021 | Infraestructura educativa construida | Corresponde a la construcción y/o intervención de obra civil, para ampliación de capacidad instalada con el objeto de atender la demanda de matrícula oficial en un territorio, tanto en lote nuevo como en lote de sede existente. | 220105100 | Sedes educativas nuevas construidas | Número | Sí |
| 2201021 | Infraestructura educativa construida | Corresponde a la construcción y/o intervención de obra civil, para ampliación de capacidad instalada con el objeto de atender la demanda de matrícula oficial en un territorio, tanto en lote nuevo como en lote de sede existente. | 220105101 | Sedes educativas nuevas construidas en zona urbana | Número | No |
| 2201021 | Infraestructura educativa construida | Corresponde a la construcción y/o intervención de obra civil, para ampliación de capacidad instalada con el objeto de atender la demanda de matrícula oficial en un territorio, tanto en lote nuevo como en lote de sede existente. | 220105102 | Sedes educativas nuevas construidas en zona rural | Número | No |

> Ver Observaciones respecto a la discrepancia entre este catálogo y los ejemplos mostrados en los mockups de los Anexos A.1 y A.3 (IP001, IP002, IP003).

## C.2 Catálogo de Resultados

| Código del Indicador | Indicadores de Resultado | Descripción | Unidad de medida |
|------------------------|------------------------------|--------------|---------------------|
| R2201100 | Tiempo de viaje disminuído en minutos | Mide la reducción del tiempo de traslado de un vehículo a través de la carretera (con proyecto), comparado con el tiempo promedio anterior (sin proyecto) | minutos |
| R2201101 | Tiempo de viaje disminuído en horas | Mide la reducción del tiempo de traslado de un vehículo a través de la carretera (con proyecto), comparado con el tiempo promedio anterior (sin proyecto) | horas |
| R2201102 | No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

> Ver Observaciones respecto a que la fila "R2201102" aparece incompleta en el documento fuente, y respecto a la discrepancia entre este catálogo y el ejemplo mostrado en el mockup del Anexo A.2 (IR001).

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Despliegue de la tabla "Indicadores del proyecto" | Sistema (FB1, paso 2), tras ingresar a la sección "Indicadores del proyecto" | Anexo A.1 |
| Despliegue de pantalla "Registro de indicador de resultado" | Sistema (FA01, paso 1.1), tras clic en "Agregar indicador" (tabla de resultado) | Anexo A.2 |
| Despliegue de pantalla "Registro de indicador de producto" | Sistema (FA02, paso 2.1), tras clic en "Agregar indicador" (tabla de producto) | Anexo A.3 |
| Traslado de indicador de resultado a la tabla del Anexo A.1 | Sistema (FA01, paso 1.5), tras clic en "Guardar" en Anexo A.2 | Tabla "Indicadores de resultado" (Anexo A.1) |
| Traslado de indicador de producto a la tabla del Anexo A.1 | Sistema (FA02, paso 2.5), tras clic en "Guardar" en Anexo A.3 y validación de meta total | Tabla "Indicadores de producto" (Anexo A.1) |
| Cálculo del "Peso relativo del producto" | Sistema (FB1, paso 2; Anexo B), a partir de datos de CU-PRE-17 | Columna "Peso relativo del producto" (Anexo A.1) |

---

# Integraciones

> No especificado en el documento.

---

# Datos Pendientes de Definir

- Existe una contradicción entre el nombre del campo "Períodos (1 a n)" (Anexo A3), su Detalle en el Anexo B ("la nomenclatura de las columnas iniciará a partir del período 0 hasta el período n") y los mockups de los Anexos A.1 y A.3 (que muestran las columnas como "PERÍODO 1", "PERÍODO 2", "PERÍODO 3", iniciando en 1). No se puede determinar si la programación de metas por período debe iniciar en el "Período 0" o en el "Período 1", lo cual bloquea la implementación exacta de esta funcionalidad.
- El campo "Total" de la pantalla Anexo A3 se describe en el Anexo B como "Muestra la suma de la ponderación de los productos", lo cual no coincide con el comportamiento mostrado en el mockup correspondiente (donde "Total" = suma de los períodos del indicador). No se puede determinar cuál es la definición correcta de este campo.
- RN10 hace referencia a "la pantalla del Anexo A.2" para el registro y programación de metas de producto, mientras que el resto del documento (FA02 y Anexo B) indica que dicha pantalla es el Anexo A.3. No se puede determinar con certeza si se trata de un error tipográfico en RN10 o de una distinción funcional no explicada.
- No se especifica el disparador (evento que inicia el caso de uso) de forma explícita.
- No se especifica la prioridad del caso de uso.
- No se especifica una tabla de códigos de error para este caso de uso (más allá de los dos mensajes identificados en "Validaciones" y "Mensajes al Usuario").
- La fórmula del campo "Peso relativo del producto" presenta caracteres que no se muestran con claridad en el documento fuente; aunque el texto que la acompaña identifica los dos operandos ("Costo del producto" y "Total inversión (precios de mercado)", ambos procedentes de CU-PRE-17), la notación matemática exacta (p. ej., si es una simple división u otra operación) no puede confirmarse con la fórmula tal como aparece en el documento.
- La tercera fila del catálogo "C.2 Catálogo de Resultados" (código "R2201102") no incluye nombre de indicador, descripción ni unidad de medida en el documento fuente.
