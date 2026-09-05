---
id: CU-PRE-3.5
codigo: CU-PRE-3.5
nombre: Selección y Registro de Etapas
modulo: Preinversión
submodulo: Formulación del Proyecto
version: "1.3"
fuente_pdf: UC-PRE-03_5_Selección_y_Registro_de_Etapas_SEP_2025_V1_F.pdf
pagina_inicio: 2
pagina_fin: 20

nota_version: >
  La versión 1.1 incorpora, como anexo Excel externo al PDF original, el
  contenido íntegro del archivo `CU-PRE-3_5_ANEXO_F.xlsx` ("Anexo F –
  Contenido de Iniciativas de Proyecto"), no transcrito en la versión 1.0.
  Se añadió como nueva subsección dentro de "Catálogos Detectados" y se
  incorporaron a "Dependencias"/"casos_relacionados" los casos de uso
  referenciados en su columna "Ubicación en caso de uso" que no estaban
  previamente listados (CU-PRE-05, 06, 07, 08, 09, 10, 12, 13, 14, 15, 16,
  18, 20, 21, 22.2, 22.3, 22.4 y 23). No se modificó ningún otro contenido
  ya corregido en la versión 1.0.

  La versión 1.2 reorganiza la tabla "B.2 – Definición de Ruta de
  Preinversión" (sección "Catálogos Detectados"): las filas agrupadas del
  PDF original (p. ej. "Todas las categorías", "Todas Complejidades",
  "Complejidad Media y Alta") se expandieron en sus combinaciones
  individuales de Tipo de capital × Tamaño según monto × Complejidad
  técnica, sin alterar ningún resultado de Ruta de Preinversión ya
  documentado. Se confirmó con el negocio que "Todas las complejidades"
  (cuarto valor del catálogo C.4) es notación abreviada aplicable por
  igual a Complejidad Baja, Media y Alta, y no una opción seleccionable
  independiente con resultado propio. No se modificó ningún otro
  contenido ya corregido en versiones anteriores.

nota_cambio_v1_3: >
  Cambios solicitados por el usuario (04/09/2026), NO provenientes de una
  nueva versión del PDF fuente, que resuelven cuatro pendientes señalados
  en versiones anteriores de este documento:
  (1) RN16: se confirma que el botón de selección radial "Co-ejecutor"
  está oculto (no solo deshabilitado) para cualquier actor distinto del
  Coordinador SYMP.
  (2) Anexo B.2: sin cambios adicionales de contenido respecto a la
  expansión ya incorporada en la versión 1.2 (36 combinaciones completas
  de Tipo de capital × Tamaño × Complejidad); se confirma que dicha
  expansión es la matriz definitiva a utilizar.
  (3) RN04/Anexo B.1/mockup A.1: se confirma que el formato definitivo de
  "Fecha estimada de inicio" y "Fecha estimada de finalización" es
  dd/mm/aaaa (el indicado por RN04).
  (4) RN20/Anexo F: se confirma que el Anexo F sí corresponde al anexo
  referenciado por RN20, y que el símbolo "-" en la columna "Campos a
  habilitar para Actualización de O.T." significa que el campo no aplica
  al Caso de Uso (distinto de una celda vacía, que indica ausencia de
  dato). No se modificó ningún otro contenido ya corregido en versiones
  anteriores.

actor_principal: Técnico URP

actores_secundarios:
  - Coordinador SYMP

prioridad: No especificado en el documento.

estado: Analizado

depende_de:
  - UC-PRE-03 Captura de proyectos
  
casos_relacionados:
  - CU-PRE-01 Registro de Proyectos
  - UC-PRE-03 Captura de proyectos
  - CU-PRE-04 Identificación
  - CU-PRE-11 Descripción técnica
  - CU-PRE-17 Presupuesto de inversión
  - CU-PRE-22.1 Programación financiera de la preinversión del Proyecto
  - CU-PRE-24 Viabilidad
  - CU-PRE-25 Elegibilidad
  - CU-PRE-26 Opinión Técnica
  - CU-PRE-30 Programación Cuatrimestral Financiera de la Preinversión
  - "CU-PRE-05 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-06 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-07 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-08 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-09 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-10 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-12 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-13 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-14 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-15 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-16 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-18 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-20 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-21 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-22.2 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-22.3 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-22.4 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"
  - "CU-PRE-23 (identificado mediante Anexo F, no mencionado en el cuerpo del PDF original)"

roles:
  - Técnico URP
  - Coordinador SYMP

pantallas:
  - Registro de Etapas (Anexo A.1)
  - Ruta de Preinversión (indicativa) (Anexo A.2)
  - Ficha de información general (Anexo A.3)
  - Ficha de proyectos de emergencia (Anexo A.4)

procesos:
  - Generación de Ruta de Preinversión
  - Registro de etapas (costos y fechas)
  - Modificación de Ruta de Preinversión
  - Visualización de ficha de información general
  - Registro de ficha de proyectos de emergencia

servicios_externos: []

entidades:
  - Proyecto
  - Ruta de Preinversión
  - Etapa
  - Ficha de información general
  - Ficha de proyectos de emergencia

catalogos:
  - Anexo C.1 – Catálogo de tipo de capital
  - Anexo C.2 – Catálogo de Tipo de Costos
  - Anexo C.3 – Catálogo tamaño del proyecto según monto
  - Anexo C.4 – Complejidad del proyecto
  - Anexo C.5 – Catálogo de ubicaciones geográficas
  - Anexo C.6 – Catálogo de Productos e Indicadores
  - "Anexo F – Contenido de Iniciativas de Proyecto (anexo Excel externo al PDF original: CU-PRE-3_5_ANEXO_F.xlsx; correspondencia con la referencia de RN20 confirmada en v1.3 — ver Observaciones)"

palabras_clave:
  - ruta de preinversión
  - registro de etapas
  - ficha de información general
  - proyectos de emergencia
  - criterios de calificación
  - CUP

ultima_actualizacion: JUN 2025 (versión 1.0); incorporación del Anexo F (anexo Excel) aplicada AGO 2026

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    FA01:
      pagina: 4
    FA01_1:
      pagina: 4
    FA02:
      pagina: 4
    FA03:
      pagina: 5
    FA04:
      pagina: 5
    FA05:
      pagina: 5
  reglas_negocio:
    RN01:
      pagina: 6
    RN02:
      pagina: 6
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
      pagina: 7
    RN11:
      pagina: 7
    RN12:
      pagina: 7
    RN13:
      pagina: 7
    RN14:
      pagina: 8
    RN15:
      pagina: 8
    RN16:
      pagina: 8
    RN17:
      pagina: 8
    RN18:
      pagina: 9
    RN19:
      pagina: 9
    RN20:
      pagina: 9
    RN21:
      pagina: 9
    RN22:
      pagina: 9
  anexos:
    A1:
      nombre: Registro de Etapas
      pagina: 10
    A2:
      nombre: Ruta de Preinversión (indicativa)
      pagina: 11
    A3:
      nombre: Ficha de información general
      pagina: 12
    A4:
      nombre: Ficha de proyectos de emergencia
      pagina: 13
    B1:
      nombre: Formatos - Pantallas Registro de Etapas, Ruta de Preinversión y Ficha de proyectos de emergencia
      pagina: 14
    B2:
      nombre: Definición de Ruta de Preinversión
      pagina: 17
    C1:
      nombre: Catálogo de tipo de capital
      pagina: 18
    C2:
      nombre: Catálogo de Tipo de Costos
      pagina: 18
    C3:
      nombre: Catálogo tamaño del proyecto según monto
      pagina: 18
    C4:
      nombre: Complejidad del proyecto
      pagina: 18
    C5:
      nombre: Catálogo de ubicaciones geográficas
      pagina: 19
    C6:
      nombre: Catálogo de Productos e Indicadores
      pagina: 20
    F:
      nombre: Contenido de Iniciativas de Proyecto
      pagina: "No aplica — anexo aportado como archivo Excel independiente (CU-PRE-3_5_ANEXO_F.xlsx), no forma parte de la paginación del PDF original."
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Selección y Registro de Etapas |
| Código | CU-PRE-3.5 |
| Módulo | Preinversión |
| Fuente | UC-PRE-03_5_Selección_y_Registro_de_Etapas_SEP_2025_V1_F.pdf |
| Versión | 1.0 |

**Campos requeridos (según el PDF):**
- Nombre
- CUP
- Ruta de Preinversión
  - Tipo de capital que genera el proyecto
  - Tamaño del proyecto según monto
  - Complejidad del proyecto
  - Justifique
- Registro de etapas
  - Etapa
  - Costo de la etapa
  - Fecha estimada de inicio
  - Fecha estimada de finalización
- Ficha de información General
- Ficha de proyectos de emergencia

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permitirá al Técnico URP identificar las etapas de Preinversión a desarrollar para un proyecto para generar la Ruta de Preinversión. Además, mostrará la ficha de información general del proyecto, y una ficha de información de proyectos de emergencia.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Coordinador SYMP (mencionado en RN16, único autorizado a usar el botón de selección radial "Co-ejecutor").

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. UC-PRE-03 "Captura de proyectos".
2. CU-PRE-04 "Identificación".
3. CU-PRE-11 "Descripción técnica".
4. CU-PRE-17 "Presupuesto de inversión".
5. CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto".
6. CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión".

> Nota de ambigüedad: el documento lista estos seis casos de uso bajo el encabezado "Precondiciones" sin un verbo explícito (p. ej. "contar con", "haber ejecutado") para cada uno; se transcriben tal como aparecen en el documento.

---

# Flujo Principal

1. Técnico URP ingresa a la pantalla donde se encuentra la tabla de "Captura de proyectos" (UC-PRE-03) y da clic sobre el CUP del proyecto. Al ingresar, aparecerá en la pantalla del Anexo A.1 un botón llamado "Ruta de Preinversión" al cual debe hacer clic.
2. Sistema muestra la pantalla del Anexo A.2 (en los casos en que ingrese por primera vez al proyecto o cuando aún no se haya generado una Ruta de Preinversión, solo muestra los campos "Criterios", "Calificación" y el botón "Generar Ruta de Preinversión").
3. Técnico URP selecciona una calificación para cada criterio, y da clic en el botón "Generar Ruta de Preinversión".
4. Sistema muestra las etapas de Preinversión sugeridas a desarrollar (Ruta de Preinversión – Anexo A.2) según combinaciones mostradas en tabla del Anexo B.2.
5. Técnico URP da clic en el botón "Aceptar" o en el botón "Modificar" la ruta de preinversión.

---

# Flujos Alternos

## FA-01 – botón "Aceptar"

**Condición**

> No especificado en el documento.

**Flujo**

1.1 Sistema traslada las etapas de Preinversión a la pantalla del Anexo A.1.

**Resultado**

> No especificado en el documento.

## FA-01.1 – Tabla "Registro de etapas"

**Condición**

> No especificado en el documento.

**Flujo**

1.1.1 Técnico URP, en la tabla del Anexo A.1, registra las fechas de inicio y fin, y da clic en el botón "Guardar".
1.1.2 Sistema guarda la información registrada y se mantiene en la pantalla del Anexo A.1, y habilita los botones de las etapas para visualización o registro de la información (Anexo A.1).
1.1.3 Sistema habilita para la etapa que se debe diligenciar los campos de las pantallas de identificación, formulación, evaluación y programación conforme al Anexo CU-PRE-3.5 "Selección y registro de etapas".

**Resultado**

> No especificado en el documento.

## FA-02 – botón "Modificar" Ruta de Preinversión

**Condición**

> No especificado en el documento.

**Flujo**

2.1 Sistema muestra el campo "Justifique Modificación" y el campo "Identifique nueva Ruta de Preinversión", así como el botón "Guardar".
2.2 Técnico URP registra información en el campo "Justifique Modificación" y selecciona la nueva ruta de preinversión marcando cada una de las etapas que va a desarrollar y da clic en el botón "Guardar".
2.3 Sistema guarda la información registrada y se dirige a la pantalla del Anexo A.1 en la que mostrará las etapas según selección para completar su información (costos y fechas de inicio y fin).

**Resultado**

> No especificado en el documento.

## FA-03 – "Registro de etapas"

**Condición**

> No especificado en el documento.

**Flujo**

3.1 Técnico URP registra para cada etapa las fechas de inicio y fin, y da clic en el botón "Guardar".
3.2 Sistema guarda la información registrada y se mantiene en la pantalla del Anexo A.1, y habilita los botones de las etapas para visualización o registro de la información (Anexo A.1).
3.3 Sistema habilita para la etapa que se debe diligenciar los campos de las pantallas de identificación, formulación, evaluación y programación conforme al Anexo CU-PRE-3.5 "Selección y registro de etapas" en Excel.

**Resultado**

> No especificado en el documento.

## FA-04 – "Ficha de Información General"

**Condición**

> No especificado en el documento.

**Flujo**

4.1 Técnico URP da clic en el botón "Ficha de información general" (Anexo A.1).
4.2 Sistema muestra la ficha de información general del proyecto (Anexo A.3) que contiene la información registrada en la solicitud de CUP (CU-PRE-01 "Registro de Proyectos").
4.3 Técnico URP visualiza la ficha de información general del proyecto y luego da clic en el botón "Regresar" (Anexo A.3).
4.4 Sistema regresa a la pantalla del Anexo A.1.

**Resultado**

> No especificado en el documento.

## FA-05 – "Registro de proyectos de emergencia"

**Condición**

> No especificado en el documento.

**Flujo**

5.1 Sistema: luego de que el Técnico URP ingresa a la pantalla donde se encuentra la tabla de "Captura de proyectos" (UC-PRE-03) y da clic sobre el CUP del proyecto de emergencia, el sistema muestra la pantalla A.1 Registro de etapas donde solamente aparecen las etapas "Perfil".
5.2 Técnico URP hace clic en el botón "Perfil".
5.3 Sistema muestra el formulario A.4 con todos los campos habilitados.
5.4 Técnico URP ingresa al formulario del Anexo A.4, diligencia los campos y hace clic en el botón "Guardar".
5.5 Sistema valida que todos los campos estén diligenciados. Si no está completo, aparecerá un mensaje que diga "Existen campos sin diligenciar" y marca con rojo el contorno de los campos faltantes. Si está completo, guarda la información y remite el proyecto a "Viabilidad" (CU-PRE-24).

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|-------------|--------------|
| — | No especificado en el documento. | No especificado en el documento. |

> No especificado en el documento.

# Postcondiciones

1. CU-PRE-24 "Viabilidad".

---

# Reglas de Negocio

### RN01

**Descripción:** Será obligatorio que el Técnico URP seleccione una calificación para cada uno de los criterios a fin de generar la Ruta de Preinversión en la pantalla del Anexo A.2.

**Origen:** Documento, sección "Reglas del Negocio".

### RN02

**Descripción:** Las etapas de PERFIL y EJECUCIÓN deben aparecer seleccionadas por defecto en la Ruta de Preinversión y en la nueva Ruta de Preinversión.

**Origen:** Documento, sección "Reglas del Negocio".

### RN03

**Descripción:** El registro de información en el campo "Justifique Modificación" será de carácter obligatorio y será requisito para habilitar la selección de la nueva ruta de preinversión.

**Origen:** Documento, sección "Reglas del Negocio".

### RN04

**Descripción:** El registro de información en los campos "Costo de la etapa", "Fecha estimada de inicio" y "Fecha estimada de finalización" de cada una de las etapas será de carácter obligatorio. Para el campo del "Costo de la etapa" el separador de unidades debe ser la coma (,). Para las fechas de inicio y fin, el formato debe ser dd/mm/aaaa.

**Origen:** Documento, sección "Reglas del Negocio".

### RN05

**Descripción:** En la pantalla A.1, para la etapa de ejecución, este campo se actualiza con lo reportado en el presupuesto de inversión (CU-PRE-17 "Presupuesto de inversión"). Si en las Opiniones Técnicas que se emitan se modifican estos montos, el sistema actualizará este campo automáticamente.

**Origen:** Documento, sección "Reglas del Negocio".

### RN06

**Descripción:** La selección de cada etapa debe diferenciarse con algún color o forma. Las etapas de "Perfil" y "Ejecución" deben estar marcadas por defecto, pues estas son obligatorias.

**Origen:** Documento, sección "Reglas del Negocio".

### RN07

**Descripción:** Cuando se haya seleccionado la opción "Estudios Generales" en el campo "Iniciativa de Inversión" en el CU-PRE-01 "Registro de Proyectos", el sistema desactivará el botón "Ruta de Preinversión", y mostrará por defecto en la sección Registro de Etapas del Anexo A.1 las etapas PERFIL y EJECUCIÓN.

**Origen:** Documento, sección "Reglas del Negocio".

### RN08

**Descripción:** Cuando se haya seleccionado la opción "Programa" en el campo "Iniciativa de Inversión" en el CU-PRE-01 "Registro de Proyectos", el sistema desactivará el botón "Ruta de Preinversión", y mostrará por defecto en la sección Registro de Etapas del Anexo A.1 las etapas PERFIL y EJECUCIÓN del estudio.

**Origen:** Documento, sección "Reglas del Negocio".

### RN09

**Descripción:** Para los botones de la sección "II. REGISTRO DE ETAPAS", su habilitación dependerá de lo siguiente:

- **Para iniciativas de inversión tipo PROYECTO** (del CU-PRE-01 "Registro de Proyectos"): el Sistema habilitará siempre por defecto los botones correspondientes a las etapas de PERFIL y EJECUCIÓN; para las etapas posteriores (PREFACTIBILIDAD, FACTIBILIDAD y DISEÑO, según corresponda) su habilitación se realizará una vez se haya emitido Opinión Técnica para cada una de dichas etapas.
- **Para iniciativas de inversión tipo PROGRAMA** (del CU-PRE-01 "Registro de Proyectos"): el Sistema habilitará siempre por defecto los botones correspondientes a las etapas de PERFIL y EJECUCIÓN.
- **Para iniciativas de inversión tipo ESTUDIO GENERAL** (del CU-PRE-01 "Registro de Proyectos"): el Sistema habilitará siempre por defecto los botones correspondientes a las etapas de PERFIL y EJECUCIÓN del estudio.
- **Para los proyectos categorizados como "Proyecto de emergencia"**: el Sistema habilitará siempre por defecto los botones correspondientes a las etapas de PERFIL y EJECUCIÓN.

Cada uno de estos botones será el enlace al registro de la información en la IDENTIFICACIÓN, FORMULACIÓN, EVALUACIÓN y PROGRAMACIÓN; así como para las diferentes gestiones a realizar con el proyecto (Viabilidad, Elegibilidad, Opinión Técnica). (Del CU-PRE-04 "Identificación" al CU-PRE-26 "Opinión Técnica").

**Origen:** Documento, sección "Reglas del Negocio".

### RN10

**Descripción:** La Tabla del Anexo B.2 muestra la cantidad de etapas por las que un proyecto deberá pasar de acuerdo con la calificación de los criterios. Por ejemplo, si en un proyecto se selecciona el Tipo de capital "Capital Físico", el Tamaño según monto "Mediano" y la Complejidad Técnica "Alta", las Etapas sugeridas a desarrollar (Ruta de Preinversión) serían: PERFIL – PREFACTIBILIDAD – FACTIBILIDAD – DISEÑO – EJECUCIÓN (Ver Anexo B.2).

**Origen:** Documento, sección "Reglas del Negocio".

### RN11

**Descripción:** El costo de la etapa de EJECUCIÓN se actualizará de manera automática cada vez que se emita una Opinión Técnica o una Actualización de Opinión Técnica al Proyecto, y se tomará del campo "Total inversión" del Anexo A.1 del CU-PRE-17 "Presupuesto de inversión".

**Origen:** Documento, sección "Reglas del Negocio".

### RN12

**Descripción:** El monto de las etapas de Preinversión será actualizado según el monto de la suma de los totales de cada etapa registrados en la columna "Total" del CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto".

**Origen:** Documento, sección "Reglas del Negocio".

### RN13

**Descripción:** Para los casos en que ya se haya definido una Ruta de Preinversión para un proyecto (tipo de iniciativa "Proyecto"), y este se encuentre en formulación en las etapas de PERFIL, PREFACTIBILIDAD, FACTIBILIDAD o DISEÑO o ya cuente con opinión técnica para alguna de dichas etapas, será posible seleccionar una Ruta de Preinversión diferente a la establecida la primera vez. En los casos en que se requiera Opinión Técnica para una etapa anterior a una ya emitida, el Técnico URP deberá modificar la Ruta de Preinversión y el Sistema bloqueará la etapa que ya contaba con Opinión Técnica, sin perder la información registrada, pero tendrá que volver a actualizar la información si es necesario y pasar por el proceso de aprobación hasta obtener la opinión técnica nuevamente.

**Origen:** Documento, sección "Reglas del Negocio".

### RN14

**Descripción:** La ficha de información general será no editable. El Sistema la generará a partir de los datos ingresados en el CU-PRE-01 "Registro de Proyectos", y mostrará la siguiente información:
- Nombre de la Institución
- Unidad Ejecutora
- Iniciativa de Inversión
- Nombre
- Monto Estimado de Inversión
- Monto de inversión (ajustado en Ejecución)
- Sector
- Macrosector
- Eje temático
- Es Proyecto GRD/GRC/ACC
- Proyecto de emergencia
- Tipo de evento (emergencia)
- N° de DL emergencia
- Línea/Eje del Plan de Gobierno
- Plan Sectorial/Regional al que Contribuye
- Co-ejecutor
- Objetivo del proyecto
- Descripción del Proyecto

**Origen:** Documento, sección "Reglas del Negocio".

### RN15

**Descripción:** Los campos "Objetivo del Proyecto", "Monto Estimado de Inversión", y "Descripción del Proyecto" se actualizarán conforme los registrados para la última Opinión Técnica, así:
- **Objetivo del Proyecto**: procederá del campo "Objetivo General" CU-PRE-04 "Identificación".
- **Monto Estimado de inversión**: procederá del campo "Total inversión" del Anexo A.1 del CU-PRE-17 "Presupuesto de inversión".
- **Descripción del proyecto**: procederá de la tabla "Descripción Técnica" Anexo A.1 del CU-PRE-11 "Descripción técnica".

**Origen:** Documento, sección "Reglas del Negocio".

### RN16

**Descripción:** El botón de selección radial "Co-ejecutor" estará habilitado únicamente para el Coordinador SYMP. Una vez que se haya dado clic en este botón, el Sistema habilitará el listado de Unidades Ejecutoras para selección.

> ✅ Aclaración (a solicitud del usuario, 04/09/2026, no proveniente del PDF): se confirma que el botón "Co-ejecutor" es **visible únicamente** para el Coordinador SYMP; para cualquier otro actor que acceda a la Ficha de información general, el botón permanece **oculto** (no solo deshabilitado/visible-sin-efecto). Esto resuelve la ambigüedad de implementación señalada anteriormente en "Observaciones" y "Permisos" sobre el comportamiento del botón para otros roles.

**Origen:** Documento, sección "Reglas del Negocio".

### RN17

**Descripción:** El campo "Monto de inversión (ajustado en Ejecución)" de la Ficha de información general Anexo A.3 será mostrado únicamente cuando el proyecto se encuentre en etapa de ejecución.

**Origen:** Documento, sección "Reglas del Negocio".

### RN18

**Descripción:** El campo "Monto de inversión (ajustado en Ejecución)" de la Ficha de información general Anexo A.3 se alimentará automáticamente cuando se realice un ajuste al monto de la ejecución.

**Origen:** Documento, sección "Reglas del Negocio".

### RN19

**Descripción:** Cuando el Técnico URP dé clic en el botón "Guardar" en las pantallas de los Anexos A.2, A.3 y A.4, el sistema guardará la información registrada y adicionalmente marcará los bordes de los campos pendientes de completar, en color rojo.

**Origen:** Documento, sección "Reglas del Negocio".

### RN20

**Descripción:** Luego de haber guardado la información, el sistema internamente debe habilitar los campos de las pantallas en identificación, formulación, evaluación y programación, conforme a la etapa que se esté diligenciando (perfil, prefactibilidad, factibilidad y diseño) y al tipo de iniciativa (proyecto, programa o estudio general). Ver Anexo CU-PRE-3.5 "Selección y registro de etapas".

**Origen:** Documento, sección "Reglas del Negocio".

### RN21

**Descripción:** Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.

**Origen:** Documento, sección "Reglas del Negocio".

### RN22

**Descripción:** El valor del campo "Costo de la etapa" procederá de los siguientes campos:
- Para las etapas de Preinversión (Perfil, Prefactibilidad, Factibilidad, Diseño y Estudio General): del campo "Monto (US$)" del CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto".
- Para la etapa de Ejecución: procederá del campo "Inversión estimada (precios de mercado)" del CU-PRE-17 "Presupuesto de inversión".

**Origen:** Documento, sección "Reglas del Negocio".

---

# Campos

## Pantalla "Registro de Etapas"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Nombre del proyecto | Muestra el nombre del proyecto. Según se asignó en el CU-PRE-01 "Registro de Proyectos" | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| CUP | Muestra el CUP del proyecto. Según se asignó en el CU-PRE-01 "Registro de Proyectos" | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable |
| Etapa | Muestra las etapas de la Ruta de Preinversión del proyecto definida en la pantalla del Anexo A.2 | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Costo de la etapa | Campo para registrar el costo estimado de la etapa. En US$. El formato para el separador de unidades es la coma (,) | Moneda | Moneda | Sí (RN04) | No especificado en el documento. | Editable |
| Fecha estimada de inicio | Campo para registrar la fecha de inicio estimada de la etapa. En formato MM/AA | Fecha | Fecha | Sí (RN04) | No especificado en el documento. | Editable. Ver observación sobre discrepancia de formato de fecha |
| Fecha estimada de finalización | Campo para registrar la fecha de fin estimada de la etapa. En formato MM/AA | Fecha | Fecha | Sí (RN04) | No especificado en el documento. | Editable. Ver observación sobre discrepancia de formato de fecha |

## Pantalla "Ruta de Preinversión"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Tipo de capital que genera | Campo que permite seleccionar de un listado predeterminado el tipo de capital que genera el proyecto. Podrá seleccionarse sólo un elemento. Listado: Capital Físico, Capital Humano, Capital Institucional, Otros Capitales | Selección | Selección | Sí (RN01) | No especificado en el documento. | No editable (según tabla de formatos) |
| Tamaño del proyecto según monto | Campo que permite seleccionar de un listado predeterminado el tamaño del proyecto según monto. Podrá seleccionarse sólo un elemento. Listado: Pequeño (hasta $1,000,000.00), Mediano (entre $1,000,001.00 hasta $5,000,000.00), Grande (mayor a $5,000,001.00) | Selección | Selección | Sí (RN01) | No especificado en el documento. | No editable (según tabla de formatos) |
| Complejidad del proyecto | Campo que permite seleccionar de un listado predeterminado la complejidad del proyecto. Podrá seleccionarse sólo un elemento. Listado: Complejidad Baja, Complejidad Media, Complejidad Alta, Todas las Complejidades | Selección | Selección | Sí (RN01) | No especificado en el documento. | No editable (según tabla de formatos) |
| Justifique modificación | Campo para registrar la justificación de la modificación de la ruta de Preinversión | Texto | Texto | Sí (RN03) | No especificado en el documento. | Editable |

> Observación sobre esta tabla: los tres campos de criterios (Tipo de capital, Tamaño según monto, Complejidad del proyecto) están marcados como "No" en la columna "Editable" del Anexo B.1, lo cual es contradictorio con el Flujo Básico (paso 3: "Técnico URP selecciona una calificación para cada criterio") y con RN01, que exigen que el Técnico URP los seleccione activamente. Se transcribe el valor tal como aparece en el documento; ver "Datos Pendientes de Definir".

## Pantalla "Ficha de proyectos de emergencia"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| CUP | Muestra el CUP del proyecto. Según se asignó en el CU-PRE-01 "Registro de Proyectos" | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable |
| Nombre del proyecto | Muestra el nombre del proyecto. Según se asignó en el CU-PRE-01 "Registro de Proyectos" | Texto y Numérico | Texto y Numérico | No especificado en el documento. | No especificado en el documento. | No editable |
| Etapa Actual | Muestra la etapa de PERFIL de manera automática | Texto | Texto | No especificado en el documento. | Perfil | No editable |
| Etapa Futura | Muestra la etapa futura del proyecto que corresponde a "Ejecución" | Texto | Texto | No especificado en el documento. | Ejecución | No editable |
| N° de DL | Campo que muestra el número de Decreto Legislativo de declaratoria de emergencia según se registró en el campo "N° de D.L." de CU-PRE-01 "Registro de Proyectos" | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | No editable |
| Tipo de evento | Campo que muestra el tipo de evento de emergencia, procede del campo "Tipo de evento" del CU-PRE-01 "Registro de Proyectos" | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable |
| Planteamiento del problema | Campo para que el Técnico URP registre el planteamiento del problema | Texto | Texto | Sí | No especificado en el documento. | Editable |
| Objetivo General | Campo que procede del campo "Objetivo del proyecto" del CU-PRE-01 "Registro de Proyectos". La información viene de este CU, pero debe ser editable para que el Técnico URP pueda modificarlo o completarlo | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable |
| Descripción del proyecto | Campo que procede del campo "Descripción del proyecto" del CU-PRE-01 "Registro de Proyectos". La información viene de este CU, pero debe ser editable para que el Técnico URP pueda modificarlo o completarlo | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable |
| Producto | Campo para que el Técnico URP seleccione los productos del proyecto, según columna "Catálogo" del catálogo C.6. El Sistema permitirá que pueda agregarse más de un producto | Texto | Texto | Sí | No especificado en el documento. | No editable (según tabla de formatos); permite agregar más de un elemento |
| Departamento | Campo que el Sistema completará según el distrito seleccionado, de acuerdo con catálogo "Ubicación Geográfica" Anexo C.5. Cada Departamento tendrá como distrito "Todo el departamento" | Texto | Texto | Sí | No especificado en el documento. | No editable |
| Distrito | Campo que permitirá seleccionar el distrito en el que el proyecto se ubica. El listado será establecido de acuerdo con el catálogo "Ubicación Geográfica" Anexo C.5. Solamente se mostrarán los distritos del Departamento seleccionado en el campo anterior. La celda permitirá digitar una palabra clave y el Sistema la autocompletará según catálogo | Selección | Selección | Sí | No especificado en el documento. | Editable |
| Coordenadas | Campo para registrar las coordenadas geográficas del proyecto en formato DD (Grados Decimales). En el caso en que en el campo "Distritos" se seleccione "Nivel nacional" se bloqueará el ingreso de coordenadas, presentando únicamente la celda sombreada | Numérico | Numérico | No | No especificado en el documento. | Editable |
| Dirección Específica | Campo para que el Técnico URP registre la dirección específica del proyecto | Texto | Texto | No | No especificado en el documento. | Editable |
| Población objetivo | Campo para que el Técnico URP registre la población objetivo del proyecto | Texto | Texto | Sí | No especificado en el documento. | Editable |
| Inversión estimada | Campo que procede del campo "Costo estimado de inversión" del CU-PRE-01 "Registro de Proyectos". La información viene de este CU, pero debe ser editable para que el Técnico URP pueda modificarlo o completarlo. Este campo contará con un botón de carga de archivos para que el Técnico URP cargue el presupuesto del proyecto en formato Excel | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable |
| Componente | Campo para que el Técnico URP seleccione, de un listado según catálogo C.2, los tipos de costos con que cuenta el proyecto | Selección | Selección | No especificado en el documento. | No especificado en el documento. | No editable (según tabla de formatos) |
| Costo (US$) | Campo para que el Técnico URP registre el monto de cada tipo de costo del proyecto, con formato para el separador de unidades la coma (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable |
| Total | Campo que muestra el total de la suma de todos los montos registrados para cada tipo de costo. Este valor debe ser igual al campo "Inversión Estimada" | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| Costos de operación | Campo para que el Técnico URP registre los costos de operación del proyecto | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable |
| Costos de mantenimiento | Campo para que el Técnico URP registre los costos de mantenimiento del proyecto | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable |
| Fuente de Financiamiento | Permite seleccionar la fuente de financiamiento, según catálogo "Fuentes de Financiamiento" del CU-PRE-17 "Presupuesto de inversión". Podrá seleccionar más de una fuente de financiamiento al dar clic en el botón emergente (+) | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable |
| Fuente de recursos | Permite seleccionar la fuente de recursos, según catálogo "Fuentes de Recursos" del CU-PRE-17 "Presupuesto de inversión" | Selección | Selección | No especificado en el documento. | No especificado en el documento. | Editable |
| Programación física y financiera | Este campo contará con un botón para que el Técnico URP cargue las programaciones física y financiera del proyecto en formato Excel y PDF | Botón de carga | Botón de carga | No especificado en el documento. | No especificado en el documento. | No editable |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| Criterios (Tipo de capital, Tamaño según monto, Complejidad del proyecto) | Selección de calificación obligatoria para cada criterio, para generar la Ruta de Preinversión | No especificado en el documento. |
| Justifique Modificación | Registro obligatorio; requisito para habilitar la selección de la nueva ruta de preinversión | No especificado en el documento. |
| Costo de la etapa | Registro obligatorio; separador de unidades debe ser la coma (,) | No especificado en el documento. |
| Fecha estimada de inicio | Registro obligatorio; formato dd/mm/aaaa (ver observación sobre discrepancia con formato MM/AA del Anexo B.1) | No especificado en el documento. |
| Fecha estimada de finalización | Registro obligatorio; formato dd/mm/aaaa (ver observación sobre discrepancia con formato MM/AA del Anexo B.1) | No especificado en el documento. |
| Planteamiento del problema | Campo obligatorio | "Existen campos sin diligenciar" (mensaje general de FA-05, paso 5.5) |
| Objetivo General | No especificado en el documento si es obligatorio de forma independiente | No especificado en el documento. |
| Producto | Campo obligatorio | "Existen campos sin diligenciar" (mensaje general de FA-05, paso 5.5) |
| Departamento | Campo obligatorio | "Existen campos sin diligenciar" (mensaje general de FA-05, paso 5.5) |
| Distrito | Campo obligatorio | "Existen campos sin diligenciar" (mensaje general de FA-05, paso 5.5) |
| Población objetivo | Campo obligatorio | "Existen campos sin diligenciar" (mensaje general de FA-05, paso 5.5) |
| Todos los campos del formulario de Ficha de proyectos de emergencia (Anexo A.4) | Validación general de que todos los campos estén diligenciados al hacer clic en "Guardar" | "Existen campos sin diligenciar" (marca en rojo el contorno de los campos faltantes) |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|------------------|
| — | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Generar, aceptar y modificar la Ruta de Preinversión; registrar etapas (costos y fechas); visualizar la ficha de información general; registrar la ficha de proyectos de emergencia | Descripción del caso de uso; Flujo Básico; FA-01 a FA-05 |
| Coordinador SYMP | Usar el botón de selección radial "Co-ejecutor" en la Ficha de información general y, tras su uso, habilitar el listado de Unidades Ejecutoras para selección | RN16 |

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01 "Registro de Proyectos"
- UC-PRE-03 "Captura de proyectos"
- CU-PRE-04 "Identificación"
- CU-PRE-11 "Descripción técnica"
- CU-PRE-17 "Presupuesto de inversión"
- CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto"
- CU-PRE-24 "Viabilidad"
- CU-PRE-25 "Elegibilidad"
- CU-PRE-26 "Opinión Técnica"
- CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión"

**Casos de uso adicionales identificados mediante el Anexo F** (columna "Ubicación en caso de uso"; no mencionados en el cuerpo del PDF original de CU-PRE-3.5, ver "Anexo F – Contenido de Iniciativas de Proyecto" en Catálogos Detectados):
- CU-PRE-05 (Análisis de Alternativas de Solución)
- CU-PRE-06 (Análisis de Interesados)
- CU-PRE-07 (Análisis de la Población)
- CU-PRE-08 (Área de Influencia)
- CU-PRE-09 (Análisis de Mercado)
- CU-PRE-10 (Situación Base Optimizada)
- CU-PRE-12 (Localización)
- CU-PRE-13 (Tamaño)
- CU-PRE-14 (Análisis Ambiental)
- CU-PRE-15 (Análisis de Riesgos)
- CU-PRE-16 (Análisis Legal)
- CU-PRE-18 (Presupuesto de O&M)
- CU-PRE-20 (Flujo de Beneficios)
- CU-PRE-21 (Flujo de Caja e Indicadores)
- CU-PRE-22.2 (Programación Financiera Inversión)
- CU-PRE-22.3 (Programación Física Preinversión)
- CU-PRE-22.4 (Programación Física Inversión)
- CU-PRE-23 (Productos del Proyecto)

**Procesos relacionados:**
- Generación de Ruta de Preinversión
- Registro de etapas (costos y fechas)
- Modificación de Ruta de Preinversión
- Visualización de ficha de información general
- Registro de ficha de proyectos de emergencia

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## A.1 – Registro de Etapas

**Nombre:** Registro de Etapas

**Descripción:** Pantalla principal donde se muestra el nombre y CUP del proyecto, el acceso a la "Ruta de Preinversión" y la tabla "II. Registro de Etapas" con los campos de etapa, costo, fecha estimada de inicio y fecha estimada de finalización.

**Campos:**
- Nombre del Proyecto
- CUP
- I. RUTA DE PREINVERSIÓN (botón)
- II. REGISTRO DE ETAPAS (tabla): Etapa, Costo de la etapa, Fecha estimada de inicio, Fecha estimada de finalización

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Etapa | Costo de la etapa | Fecha estimada de inicio | Fecha estimada de finalización |
|-------|--------------------|----------------------------|-----------------------------------|
| PERFIL | $2,000.00 | mm/aaaa | mm/aaaa |
| DISEÑO | $250,000.00 | mm/aaaa | mm/aaaa |
| EJECUCIÓN | $3,500,000.00 | mm/aaaa | mm/aaaa |

**Botones:**
- I. RUTA DE PREINVERSIÓN
- Ficha de Información General
- GUARDAR

**Acciones:**
- I. Ruta de Preinversión: navega a la pantalla del Anexo A.2.
- Ficha de Información General: dispara FA-04, navega al Anexo A.3.
- Guardar: guarda la información de la tabla de etapas (FA-01.1, FA-03).

## A.2 – Ruta de Preinversión (indicativa)

**Nombre:** Ruta de Preinversión

**Descripción:** Pantalla donde el Técnico URP selecciona la calificación de los criterios (Tipo de capital, Tamaño del proyecto, Complejidad del proyecto), genera la Ruta de Preinversión sugerida, y puede aceptarla o modificarla mediante justificación y nueva selección de etapas.

**Campos:**
- CRITERIOS: 1. Tipo de capital que genera (con nota: "Los proyectos que generen más de un tipo de capital deberán seleccionar el que tenga más peso"); 2. Tamaño del proyecto según monto; 3. Complejidad del proyecto
- CALIFICACIÓN (desplegables correspondientes a cada criterio)
- RUTA DE PREINVERSIÓN (etapas): Perfil, Prefactibilidad, Factibilidad, Diseño, Ejecución
- Justificación de cambios (campo de texto)
- Identifique nueva ruta (etapas): Perfil, Prefactibilidad, Factibilidad, Diseño, Ejecución

**Botones:**
- Generar Ruta de Preinversión
- Aceptar
- Modificar
- Guardar

**Acciones:**
- Generar Ruta de Preinversión: calcula y muestra la ruta sugerida según la tabla del Anexo B.2 (Flujo Básico, paso 4).
- Aceptar: dispara FA-01 (traslada las etapas a la pantalla del Anexo A.1).
- Modificar: dispara FA-02 (muestra los campos "Justifique Modificación" e "Identifique nueva Ruta de Preinversión").
- Guardar (en el bloque de modificación): guarda la justificación y la nueva ruta seleccionada, y dirige a la pantalla del Anexo A.1 (FA-02, paso 2.3).

## A.3 – Ficha de información general

**Nombre:** Ficha de información general

**Descripción:** Pantalla no editable que muestra la información general del proyecto, generada a partir de los datos ingresados en CU-PRE-01 "Registro de Proyectos" (RN14), organizada en secciones "IDENTIFICACIÓN", "PLANIFICACIÓN" y "DESCRIPCIÓN".

**Campos:**
- Unidad Ejecutora
- Co-ejecutor (selección radial, habilitado solo para Coordinador SYMP, RN16)
- Iniciativa de Inversión
- Sección IDENTIFICACIÓN: Nombre del proyecto, Inversión Estimada, Monto de inversión (ajustado en Ejecución) (solo visible en etapa de ejecución, RN17), Sector, Macrosector, Eje temático, Es Proyecto GRD/ACC, Proyecto de emergencia, Tipo de evento, N° de DL
- Sección PLANIFICACIÓN: Línea/Eje del Plan de Gobierno, Plan Sectorial/Regional al que Contribuye
- Sección DESCRIPCIÓN: Descripción del Proyecto

**Botones:**
- Regresar
- Guardar

**Acciones:**
- Regresar: dispara el paso 4.4 de FA-04, retorna a la pantalla del Anexo A.1.
- Guardar: según RN19, guarda la información y marca en rojo los bordes de los campos pendientes de completar.

> Observación: la lista de campos de esta pantalla (mockup) usa la etiqueta "Es Proyecto GRD/ACC", mientras que RN14 enumera el campo como "Es Proyecto GRD/GRC/ACC". Ver "Observaciones".

## A.4 – Ficha de proyectos de emergencia

**Nombre:** Ficha de proyectos de emergencia

**Descripción:** Formulario con todos los campos habilitados para el registro de información de un proyecto de emergencia, incluyendo planteamiento del problema, objetivo, descripción, productos, localización, población objetivo, inversión estimada, resumen de costos, costos de operación y mantenimiento, fuentes de financiamiento/recursos y programación física y financiera.

**Campos:**
- CUP
- Nombre del proyecto
- Etapa Actual (Perfil, automático)
- Etapa Futura (Ejecución)
- Declaratoria de Emergencia
- N° de DL
- Tipo de evento
- Planteamiento del Problema
- Objetivo General
- Descripción del Proyecto
- Productos (con botón de agregar "+")
- Localización: Macrolocalización (Distrito, Departamento), Microlocalización (Coordenadas, Dirección Específica)
- Población Objetivo
- Inversión Estimada (con botón de carga de presupuesto en Excel)
- Resumen de costos: Componente, Costo (US$), Total
- Costos de Operación
- Costos de Mantenimiento
- Fuente de Financiamiento/Recursos: Fuente de Financiamiento (con botón "+"), Fuente de Recursos
- Programación Financiera y Física (carga de archivos, con ícono informativo "Carga de las programaciones")

**Botones:**
- GUARDAR
- Botón "+" para agregar Producto
- Botón "+" para agregar Fuente de Financiamiento/Recursos
- Botón de carga de archivos (Inversión Estimada; Programación Financiera y Física)

**Acciones:**
- Guardar: valida que todos los campos estén diligenciados (FA-05, paso 5.5); si falta información muestra el mensaje "Existen campos sin diligenciar" y marca en rojo los campos faltantes; si está completo, guarda la información y remite el proyecto a "Viabilidad" (CU-PRE-24).

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|-----------------|
| Error / Validación | "Existen campos sin diligenciar" | Al hacer clic en "Guardar" en el formulario del Anexo A.4 (Ficha de proyectos de emergencia) si no todos los campos están diligenciados (FA-05, paso 5.5) |
| Ayuda contextual | Mensaje indicando qué información se debe completar en el campo (texto exacto no especificado) | Al dar clic en el ícono "?" que aparece al acercar el cursor a un campo (RN21) |

---

# Observaciones

- La tabla de formatos del Anexo B.1 para la pantalla "Ruta de Preinversión" marca los campos "Tipo de capital que genera", "Tamaño del proyecto según monto" y "Complejidad del proyecto" como "No" editables; sin embargo, el Flujo Básico (paso 3) y RN01 indican explícitamente que el Técnico URP debe seleccionar una calificación para cada uno de estos criterios. El documento no aclara esta contradicción entre la tabla de formatos y la descripción funcional del flujo.
- ✅ **Resuelto (a solicitud del usuario, 04/09/2026, no proveniente del PDF):** existía una discrepancia en el formato de las fechas del campo "Fecha estimada de inicio" y "Fecha estimada de finalización" entre RN04 ("el formato debe ser dd/mm/aaaa"), la tabla de formatos del Anexo B.1 ("En formato MM/AA") y el mockup del Anexo A.1 (placeholder "mm/aaaa"). Se confirma que el formato definitivo es **dd/mm/aaaa**, el indicado por RN04; el Anexo B.1 y el mockup quedan como referencias desactualizadas o imprecisas frente a esta confirmación.
- RN14 enumera el campo "Es Proyecto GRD/GRC/ACC" como parte de la Ficha de información general, mientras que el mockup del Anexo A.3 muestra la etiqueta "Es Proyecto GRD/ACC" (sin "GRC"). El documento no aclara si se trata de la misma etiqueta abreviada o de una omisión.
- El campo "Componente" de la Ficha de proyectos de emergencia se describe en la tabla de formatos como no editable ("No" en la columna Editable) a pesar de tratarse de un campo de selección donde "el Técnico URP seleccione, de un listado según catálogo C.2, los tipos de costos con que cuenta el proyecto"; el documento no aclara esta aparente contradicción entre el tipo "Selección" y la condición de no editable.
- El campo "Producto" también se describe como "No" editable en la tabla de formatos, a pesar de indicar que "el Técnico URP seleccione los productos del proyecto... El Sistema permitirá que pueda agregarse más de un producto"; no se aclara si "no editable" se refiere a que el texto del producto en sí no puede modificarse una vez seleccionado del catálogo, o si es una inconsistencia de la tabla.
- El texto de RN14 llama al Anexo A.3 "Ficha de información general" y describe que incluye "Nombre de la Institución" como primer campo; sin embargo, en el mockup del Anexo A.3 no aparece un campo rotulado explícitamente "Nombre de la Institución" como encabezado separado (solo "Unidad Ejecutora" e "Iniciativa de Inversión" antes de la sección IDENTIFICACIÓN). El documento no aclara si "Nombre de la Institución" corresponde a otro campo visible bajo un nombre distinto.
- El texto del mensaje mostrado en FA-05 paso 5.5 ("Existen campos sin diligenciar") es el único mensaje de error explícito documentado; no se especifican mensajes de validación individuales para cada campo obligatorio del formulario A.4.
- ✅ **Anexo F — correspondencia con RN20 resuelta (a solicitud del usuario, 04/09/2026, no proveniente del PDF):** el archivo `CU-PRE-3_5_ANEXO_F.xlsx`, aportado posteriormente como anexo Excel independiente, contiene una matriz de habilitación de campos por etapa de preinversión (Perfil/Prefactibilidad/Factibilidad/Diseño) y por tipo de iniciativa (Programa/Estudio General), con referencia cruzada a otros casos de uso ("Ubicación en caso de uso"). Se confirma que este Anexo F **sí corresponde** al anexo referenciado en **RN20** ("Ver Anexo CU-PRE-3.5 'Selección y registro de etapas'"), pese a la discrepancia de nomenclatura entre ambos nombres ya señalada en versiones anteriores de este documento.
- ✅ **Anexo F — significado del símbolo "-" resuelto (a solicitud del usuario, 04/09/2026, no proveniente del PDF):** en la columna "Campos a habilitar para Actualización de O.T." del Anexo F, el guion "-" (filas 2.5, 2.8, 4.1, 4.2 y 4.4) significa que **el campo no aplica al Caso de Uso** (es decir, no aplica al proceso de Actualización de Opinión Técnica), a diferencia de una celda vacía, que indica ausencia de dato/no completado. "X" sigue indicando que el campo sí se habilita.
- **Anexo F — celdas combinadas en "Ubicación en caso de uso":** las filas 1.1 a 1.4 (Antecedentes, Problema Central, Objetivo General, Objetivos Específicos) comparten una sola celda combinada con el valor "CUPRE-04"; las filas 2.12 (Presupuesto de Inversión) y 2.13 (Fuentes de Financiamiento) comparten una sola celda combinada con el valor "CUPRE-17". Esto significa que "Fuentes de Financiamiento" no tiene una ubicación propia distinta en el caso de uso CU-PRE-17, sino que comparte la misma referencia que "Presupuesto de Inversión"; el documento no aclara si esto es intencional o si "Fuentes de Financiamiento" debería tener su propia referencia de caso de uso.
- Los casos de uso CU-PRE-05, 06, 07, 08, 09, 10, 12, 13, 14, 15, 16, 18, 20, 21, 22.2, 22.3, 22.4 y 23, identificados a través de la columna "Ubicación en caso de uso" del Anexo F, no estaban listados en "Dependencias"/"casos_relacionados" en la versión 1.0 de este documento (basada únicamente en el PDF); se incorporaron en la versión 1.1 a partir de esta única fuente (el propio Anexo F), sin más contexto sobre la naturaleza exacta de cada relación.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|----------|-------------|-----------|
| Proyecto | Proyecto sobre el cual se define y registra la Ruta de Preinversión y sus etapas | Consulta (acceso vía CUP desde UC-PRE-03) |
| Ruta de Preinversión | Conjunto de etapas de preinversión sugeridas o seleccionadas para un proyecto, según criterios de calificación | Generación (Flujo Básico, paso 4); Aceptación (FA-01); Modificación (FA-02) |
| Etapa | Cada una de las etapas de preinversión (Perfil, Prefactibilidad, Factibilidad, Diseño, Ejecución) con su costo y fechas estimadas | Registro (FA-01.1, FA-03); Actualización automática de costo (RN05, RN11, RN12, RN22) |
| Ficha de información general | Ficha no editable con datos generales del proyecto derivados de CU-PRE-01 y otros casos de uso | Consulta (FA-04); actualización automática de ciertos campos (RN15, RN18) |
| Ficha de proyectos de emergencia | Formulario para el registro de información de un proyecto categorizado como de emergencia | Registro (FA-05); Validación (FA-05, paso 5.5) |

---

# Catálogos Detectados

## B.2 – Definición de Ruta de Preinversión

> **Nota metodológica:** esta tabla reorganiza el contenido del Anexo B.2 del PDF original, que agrupaba varias combinaciones en una sola fila (p. ej. "Todas las categorías", "Todas Complejidades", "Complejidad Media y Alta"). Aquí se despliega cada combinación individual de Tipo de capital × Tamaño según monto × Complejidad técnica, sin agregar ni modificar ningún resultado de Ruta de Preinversión: cada fila expandida conserva exactamente el resultado que el documento fuente asignaba a su fila agrupada de origen. "Todas las complejidades" (cuarto valor del catálogo C.4) se confirmó como notación abreviada que aplica por igual a Complejidad Baja, Media y Alta, no como una opción seleccionable independiente con resultado propio.

| Según tipo de capital | Tamaño según monto | Según complejidad técnica | Ruta de Preinversión (indicativa) |
|---|---|---|---|
| Capital Humano | Pequeño | Complejidad Baja | Perfil |
| Capital Humano | Pequeño | Complejidad Media | Perfil |
| Capital Humano | Pequeño | Complejidad Alta | Perfil |
| Capital Humano | Mediano | Complejidad Baja | Perfil |
| Capital Humano | Mediano | Complejidad Media | Perfil |
| Capital Humano | Mediano | Complejidad Alta | Perfil |
| Capital Humano | Grande | Complejidad Baja | Perfil |
| Capital Humano | Grande | Complejidad Media | Perfil |
| Capital Humano | Grande | Complejidad Alta | Perfil |
| Capital Institucional | Pequeño | Complejidad Baja | Perfil |
| Capital Institucional | Pequeño | Complejidad Media | Perfil |
| Capital Institucional | Pequeño | Complejidad Alta | Perfil |
| Capital Institucional | Mediano | Complejidad Baja | Perfil |
| Capital Institucional | Mediano | Complejidad Media | Perfil |
| Capital Institucional | Mediano | Complejidad Alta | Perfil |
| Capital Institucional | Grande | Complejidad Baja | Perfil |
| Capital Institucional | Grande | Complejidad Media | Perfil |
| Capital Institucional | Grande | Complejidad Alta | Perfil |
| Otros capitales | Pequeño | Complejidad Baja | Perfil |
| Otros capitales | Pequeño | Complejidad Media | Perfil |
| Otros capitales | Pequeño | Complejidad Alta | Perfil |
| Otros capitales | Mediano | Complejidad Baja | Perfil |
| Otros capitales | Mediano | Complejidad Media | Perfil |
| Otros capitales | Mediano | Complejidad Alta | Perfil |
| Otros capitales | Grande | Complejidad Baja | Perfil |
| Otros capitales | Grande | Complejidad Media | Perfil |
| Otros capitales | Grande | Complejidad Alta | Perfil |
| Capital Físico | Pequeño | Complejidad Baja | Perfil con Diseño Básico |
| Capital Físico | Pequeño | Complejidad Media | Perfil con Diseño Básico |
| Capital Físico | Pequeño | Complejidad Alta | Perfil con Diseño Básico |
| Capital Físico | Mediano | Complejidad Baja | Perfil + Diseño |
| Capital Físico | Mediano | Complejidad Media | Perfil + Prefactibilidad + Factibilidad + Diseño |
| Capital Físico | Mediano | Complejidad Alta | Perfil + Prefactibilidad + Factibilidad + Diseño |
| Capital Físico | Grande | Complejidad Baja | Perfil + Prefactibilidad + Factibilidad + Diseño |
| Capital Físico | Grande | Complejidad Media | Perfil + Prefactibilidad + Factibilidad + Diseño |
| Capital Físico | Grande | Complejidad Alta | Perfil + Prefactibilidad + Factibilidad + Diseño |

## C.1 – Catálogo de tipo de capital

| Catálogo | Valores conocidos |
|----------|---------------------|
| Tipo de capital | Capital físico; Capital humano; Capital institucional; Otros capitales |

## C.2 – Catálogo de Tipo de Costos

| Catálogo | Valores conocidos |
|----------|---------------------|
| Tipo de costo | Infraestructura; Infraestructura Informática; Equipamiento; Ambiental; Capacitaciones; Administración; Consultorías; Supervisión; Imprevistos; Derechos de Vía; Terrenos; Otros |

> Nota importante (según el documento): Este catálogo estará sujeto a actualización por parte de la DGICP.

## C.3 – Catálogo tamaño del proyecto según monto

| Catálogo | Valores conocidos |
|----------|---------------------|
| Tamaño del proyecto | Pequeño (hasta $1,000,000.00); Mediano (entre $1,000,001.00 hasta $5,000,000.00); Grande (mayor a $5,000,001.00) |

## C.4 – Complejidad del proyecto

| Catálogo | Valores conocidos |
|----------|---------------------|
| Complejidad del proyecto | Complejidad baja; Complejidad media; Complejidad alta; Todas las complejidades |

## C.5 – Catálogo de ubicaciones geográficas

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

## C.6 – Catálogo de Productos e Indicadores

| Código del Producto | Producto | Descripción | Código del Indicador de Producto | Indicador de Producto | Unidad de medida | Indicador Principal |
|---|---|---|---|---|---|---|
| 2201021 | Infraestructura educativa construida | Corresponde a la construcción y/o intervención de obra civil, para ampliación de capacidad instalada con el objeto de atender la demanda de matrícula oficial en un territorio, tanto en lote nuevo como en lote de sede existente. | 220105100 | Sedes educativas nuevas construidas | Número | Sí |
| 2201021 | Infraestructura educativa construida | Corresponde a la construcción y/o intervención de obra civil, para ampliación de capacidad instalada con el objeto de atender la demanda de matrícula oficial en un territorio, tanto en lote nuevo como en lote de sede existente | 220105101 | Sedes educativas nuevas construidas en zona urbana | Número | No |
| 2201021 | Infraestructura educativa construida | Corresponde a la construcción y/o intervención de obra civil, para ampliación de capacidad instalada con el objeto de atender la demanda de matrícula oficial en un territorio, tanto en lote nuevo como en lote de sede existente. | 220105102 | Sedes educativas nuevas construidas en zona rural | Número | No |

## Anexo F – Contenido de Iniciativas de Proyecto (habilitación de campos por etapa e iniciativa)

> **Origen:** este catálogo no formaba parte del PDF fuente `UC-PRE-03_5_Selección_y_Registro_de_Etapas_SEP_2025_V1_F.pdf` tal como fue procesado inicialmente; fue aportado posteriormente como archivo Excel anexo independiente: `CU-PRE-3_5_ANEXO_F.xlsx` (hoja única "Hoja1"). Se transcribe íntegro a continuación, preservando el orden, la agrupación y las celdas combinadas de la hoja original.
>
> ✅ **Referencia cruzada confirmada (a solicitud del usuario, 04/09/2026, no proveniente del PDF):** RN20 (ver sección "Reglas de Negocio") establece que, luego de guardar la información, "el sistema internamente debe habilitar los campos de las pantallas en identificación, formulación, evaluación y programación, conforme a la etapa que se esté diligenciando (perfil, prefactibilidad, factibilidad y diseño) y al tipo de iniciativa (proyecto, programa o estudio general)", y remite a "Ver Anexo CU-PRE-3.5 'Selección y registro de etapas'". Se confirma que este Anexo F es efectivamente el anexo referenciado por RN20, a pesar de la discrepancia de nomenclatura entre "Anexo CU-PRE-3.5 'Selección y registro de etapas'" (nombre usado en RN20) y "Anexo F" (nombre del archivo aportado), que no aparece con ese nombre en ninguna otra parte del documento fuente.
>
> Título de la hoja (fila 1, celda combinada B1:I1): **"CONTENIDO DE INICIATIVAS DE PROYECTO"**.
>
> Estructura de encabezados (filas 3–4 de la hoja original): la columna "Ubicación en caso de uso" identifica el caso de uso donde se documenta cada contenido; las columnas "1.1 Perfil", "1.2 Prefactibilidad", "1.3 Factibilidad" y "1.4 Diseño" están agrupadas bajo el encabezado combinado "1. PROYECTO" (celdas D3:G3 de la hoja original); las columnas "2. Programa" y "3. Estudio General" son encabezados combinados individuales (H3:H4 e I3:I4 de la hoja original) sin subdivisión por etapa; la columna "Campos a habilitar para Actualización de O.T." es un encabezado combinado independiente (K3:K4 de la hoja original). La columna J de la hoja original está vacía en todas las filas y no se transcribe.
>
> Las filas en **negrita** corresponden a encabezados de sección de la hoja original (sin marca en ninguna columna) y se preservan como agrupadores, tal como aparecen en el archivo, para conservar la jerarquía "1. Identificación del proyecto" / "2. Formulación del proyecto" / "3. Evaluación" / "4. Programación" / "5. Documentos anexos".
>
> El símbolo "—" indica una celda vacía en la hoja original (sin marca). El símbolo "-" (guion), presente en la columna "Campos a habilitar para Actualización de O.T." para varias filas (2.5, 2.8, 4.1, 4.2, 4.4), significa que **el campo no aplica al Caso de Uso** (no aplica al proceso de Actualización de Opinión Técnica) — ✅ aclaración confirmada a solicitud del usuario (04/09/2026, no proveniente del PDF), distinta de una celda vacía (ausencia de dato).
>
> **Celdas combinadas relevantes en la columna "Ubicación en caso de uso":** las filas 1.1 a 1.4 ("Antecedentes", "Problema Central", "Objetivo General", "Objetivos Específicos") comparten una única celda combinada con el valor "CUPRE-04" (celdas C6:C9 de la hoja original); las filas 2.12 ("Presupuesto de Inversión") y 2.13 ("Fuentes de Financiamiento") comparten una única celda combinada con el valor "CUPRE-17" (celdas C23:C24 de la hoja original). Se transcribe el valor heredado de la celda combinada en cada fila correspondiente, sin que esto implique una repetición manual en el archivo original.

| Contenidos | Ubicación en caso de uso | 1.1 Perfil | 1.2 Prefactibilidad | 1.3 Factibilidad | 1.4 Diseño | 2. Programa | 3. Estudio General | Campos a habilitar para Actualización de O.T. |
|---|---|---|---|---|---|---|---|---|
| **1. Identificación del proyecto** |  |  |  |  |  |  |  |  |
| 1.1. Antecedentes | CUPRE-04 | X | X | X | X | X | X | — |
| 1.2. Problema Central | CUPRE-04 | X | X | X | X | X | X | — |
| 1.3. Objetivo General | CUPRE-04 | X | X | X | X | X | X | — |
| 1.4. Objetivos Específicos | CUPRE-04 | X | X | X | X | X | X | — |
| 1.5 Análisis de Alternativas de Solución | CUPRE-05 | X | X | — | — | — | — | — |
| **2. Formulación del proyecto** |  |  |  |  |  |  |  |  |
| 2.1. Análisis de Interesados | CUPRE-06 | X | X | X | X | — | — | X |
| 2.2. Análisis de la Población | CUPRE-07 | X | X | X | X | X | X | X |
| 2.3. Área de Influencia | CUPRE-08 | X | X | X | X | — | — | X |
| 2.4. Análisis de Mercado | CUPRE-09 | X | X | X | X | — | — | X |
| 2.5. Situación Base Optimizada | CUPRE-10 | X | — | — | — | — | — | - |
| 2.6. Descripción Técnica | CUPRE-11 | X | X | X | X | X | X | X |
| 2.7. Localización | CUPRE-12 | X | X | X | X | X | X | X |
| 2.8. Tamaño | CUPRE-13 | X | X | X | X | X | X | - |
| 2.9. Análisis Ambiental | CUPRE-14 | X | X | X | X | — | — | X |
| 2.10. Análisis de Riesgos | CUPRE-15 | X | X | X | X | X | — | X |
| 2.11. Análisis Legal | CUPRE-16 | X | X | X | X | — | — | X |
| 2.12. Presupuesto de Inversión | CUPRE-17 | X | X | X | X | X | X | X |
| 2.13. Fuentes de Financiamiento | CUPRE-17 | X | X | X | X | X | X | X |
| 2.14. Presupuesto de O&M | CUPRE-18 | X | X | X | X | X | — | X |
| 2.15. Productos del Proyecto | CUPRE-23 | X | X | X | X | X | — | X |
| **3. Evaluación** |  |  |  |  |  |  |  |  |
| 3.1. Flujo de Beneficios | CUPRE-20 | X | X | X | X | — | — | X |
| 3.2. Flujo de Caja e Indicadores | CUPRE-21 | X | X | X | X | — | — | X |
| **4. Programación** |  |  |  |  |  |  |  |  |
| 4.1. Programación Financiera Preinversión | CUPRE-22.1 | X | X | X | — | — | X | - |
| 4.2. Programación Física Preinversión | CUPRE-22.3 | X | X | X | — | — | X | - |
| 4.3. Programación Financiera Inversión | CUPRE-22.2 | X | X | X | X | X | — | X |
| 4.4. Programación Física Inversión | CUPRE-22.4 | X | X | X | X | X | — | - |
| **5. Documentos anexos** |  |  |  |  |  |  |  |  |
| 5.1. Nota de solicitud de OT | — | X | X | X | X | X | X | X |
| 5.2. Documento de Preinversión | — | X | X | X | X | X | X | X |
| 5.3. Otros documentos | — | X | X | X | X | X | X | X |
| 5.4. Marco Lógico | — | — | — | — | — | X | — | — |

> **Nota del documento original (fila 41 de la hoja, celda combinada B41:I41):** "Nota: los contenidos marcados con 'X' será presentados por default en el SIIP, y la IE's podrán justificar su no inclusión, según la naturaleza del proyecto."

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Ingreso a pantalla "Ruta de Preinversión" | Clic en botón "Ruta de Preinversión" (Técnico URP, Flujo Básico paso 1) | Pantalla Anexo A.2 |
| Generación de Ruta de Preinversión sugerida | Clic en "Generar Ruta de Preinversión" (Técnico URP, Flujo Básico paso 3) | Sistema (combinaciones del Anexo B.2) |
| Traslado de etapas a Registro de Etapas | Clic en "Aceptar" (Técnico URP, FA-01) | Pantalla Anexo A.1 |
| Habilitación de campos de identificación, formulación, evaluación y programación | Guardado de fechas de inicio/fin en Registro de Etapas (FA-01.1, FA-03) | Pantallas de CU-PRE-04 a CU-PRE-26 (según etapa) |
| Actualización automática del costo de la etapa de Ejecución | Emisión de Opinión Técnica o Actualización de Opinión Técnica | Campo "Costo de la etapa" (Ejecución) en Anexo A.1 (RN05, RN11) |
| Navegación a Ficha de información general | Clic en botón "Ficha de información general" (Técnico URP, FA-04) | Pantalla Anexo A.3 |
| Regreso a Registro de Etapas | Clic en botón "Regresar" (Técnico URP, FA-04) | Pantalla Anexo A.1 |
| Ingreso a Ficha de proyectos de emergencia | Clic en botón "Perfil" (Técnico URP, FA-05) | Formulario Anexo A.4 |
| Remisión del proyecto a Viabilidad | Guardado exitoso (todos los campos diligenciados) del formulario Anexo A.4 (FA-05, paso 5.5) | CU-PRE-24 "Viabilidad" |
| Actualización automática de campos de la Ficha de información general | Registro de última Opinión Técnica (RN15) / ajuste al monto de ejecución (RN18) | Campos "Objetivo del Proyecto", "Monto Estimado de Inversión", "Descripción del Proyecto", "Monto de inversión (ajustado en Ejecución)" en Anexo A.3 |

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
- No se especifica el texto exacto del mensaje de ayuda contextual que muestra el ícono "?" (RN21); solo se indica que "indicará qué información se debe completar en dicho campo", sin el texto literal.
- Contradicción no resuelta: la tabla de formatos (Anexo B.1) marca los campos de criterios de la pantalla "Ruta de Preinversión" (Tipo de capital, Tamaño según monto, Complejidad del proyecto) como no editables, mientras que el Flujo Básico y RN01 requieren que el Técnico URP los seleccione activamente.
- ✅ **Resuelto (04/09/2026):** el formato de fecha para "Fecha estimada de inicio" y "Fecha estimada de finalización" queda confirmado como **dd/mm/aaaa** (RN04), pese a que el Anexo B.1 indica "MM/AA" y el mockup del Anexo A.1 muestra "mm/aaaa".
- Discrepancia no resuelta en la nomenclatura del campo "Es Proyecto GRD/GRC/ACC" (RN14) frente a "Es Proyecto GRD/ACC" (mockup Anexo A.3).
- No se aclara si "Nombre de la Institución", mencionado en RN14 como parte de la Ficha de información general, corresponde a un campo visible bajo otro nombre en el mockup del Anexo A.3, dado que no aparece con ese rótulo exacto.
- Contradicción no resuelta: los campos "Componente" y "Producto" se describen en su detalle funcional como campos de selección activa por parte del Técnico URP, pero la tabla de formatos los marca como "No" editables.
- ✅ **Resuelto (04/09/2026):** el "Anexo F – Contenido de Iniciativas de Proyecto" (archivo `CU-PRE-3_5_ANEXO_F.xlsx`) corresponde efectivamente al anexo referenciado en RN20 como "Anexo CU-PRE-3.5 'Selección y registro de etapas'", pese a la discrepancia de nomenclatura entre ambos nombres (ver Observaciones).
- ✅ **Resuelto (04/09/2026):** el símbolo "-" (guion) en la columna "Campos a habilitar para Actualización de O.T." de las filas 2.5, 2.8, 4.1, 4.2 y 4.4 significa que el campo no aplica al Caso de Uso (no aplica al proceso de Actualización de Opinión Técnica), a diferencia de una celda vacía (ausencia de dato).
- ✅ **Resuelto (04/09/2026):** el botón de selección radial "Co-ejecutor" (RN16) está **oculto** (no solo deshabilitado) para cualquier actor distinto del Coordinador SYMP.
- Sigue pendiente: si "Fuentes de Financiamiento" debería tener su propia referencia de "Ubicación en caso de uso" en el Anexo F, distinta de "Presupuesto de Inversión" (con quien comparte actualmente una celda combinada con valor "CUPRE-17").
- **Anexo F:** aclaración de si "Fuentes de Financiamiento" (fila 2.13) debería tener su propia referencia de "Ubicación en caso de uso" distinta de "Presupuesto de Inversión" (fila 2.12), dado que ambas comparten la misma celda combinada con el valor "CUPRE-17" en el archivo original.
- **Anexo F:** naturaleza exacta de la relación de este caso de uso (CU-PRE-3.5) con los casos de uso CU-PRE-05, 06, 07, 08, 09, 10, 12, 13, 14, 15, 16, 18, 20, 21, 22.2, 22.3, 22.4 y 23, identificados únicamente a través de la columna "Ubicación en caso de uso" del Anexo F y no descritos en ninguna otra sección del documento fuente.