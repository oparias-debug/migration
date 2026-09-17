---
id: CU-PRE-21
codigo: CU-PRE-21
nombre: Flujo de Caja y cálculo de Indicadores
modulo: Preinversión
submodulo: Evaluación
version: "1.0"
fuente_pdf: CU-PRE-21_Flujo_de_Caja_y_cálculo_de_Indicadores_AGO_2025_V1_F.pdf
pagina_inicio: 3
pagina_fin: 8

actor_principal: Técnico URP

actores_secundarios:
  - Sistema
  - Técnico PRE
  - Usuarios centrales (RQ-C-03: sinónimo de "usuarios internos"/"actores internos", cualquier usuario del Ministerio de Hacienda) e instituciones CEPA, CEL, ANDA, INDES, ISTU (acceso por un motivo distinto — ver RN06)

prioridad: No especificado en el documento.

estado: Analizado

depende_de:
  - CU-PRE-01 Registro de proyectos
  - CU-PRE-03.5 Selección y registro de etapas
  - CU-PRE-17 Presupuesto de inversión
  - CU-PRE-18 Flujo de costos de Operación y Mantenimiento
  - CU-PRE-20 Flujo de Beneficios

casos_relacionados:
  - CU-PRE-21.5 Flujo de Caja Financiero
  - CU-PRE-23 Indicadores del Proyecto

roles:
  - Técnico URP
  - Técnico PRE
  - Usuarios centrales

pantallas:
  - Flujo de Caja e Indicadores (Anexo A.1)

procesos:
  - Cálculo de flujo de caja
  - Cálculo de indicadores de evaluación socioeconómica
  - Evaluación financiera

servicios_externos: []

entidades:
  - Proyecto
  - Flujo de Caja
  - Costos de Inversión
  - Costos de Operación
  - Costos de Mantenimiento
  - Beneficios
  - Valor de rescate
  - Indicadores de Evaluación (VANS, TIRS, R B/C, VACS, CAE)

catalogos:
  - Instituciones con acceso a "Evaluación Financiera" (nombres completos y clasificación confirmados por RQ-C-03; ver "Catálogos Detectados")

palabras_clave:
  - flujo de caja
  - indicadores de evaluación
  - VANS
  - TIRS
  - R B/C
  - VACS
  - CAE
  - evaluación socioeconómica
  - evaluación financiera

ultima_actualizacion: AGO 2025

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 3
  flujos_alternos:
    FA01:
      pagina: 3
  reglas_negocio:
    RN01:
      pagina: 3
    RN02:
      pagina: 3
    RN03:
      pagina: 3
    RN04:
      pagina: 5
    RN05:
      pagina: 5
    RN06:
      pagina: 5
  anexos:
    A1:
      nombre: Flujo de Caja e Indicadores
      pagina: 5
    B1:
      nombre: Formatos - Pantalla "Flujo de Caja e Indicadores"
      pagina: 6
    B1_2:
      nombre: Formatos - Pantalla "Indicadores"
      pagina: 7
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Flujo de Caja y cálculo de Indicadores |
| Código | CU-PRE-21 |
| Módulo | No especificado en el documento. |
| Fuente | Sistema de Información de Inversión Pública |
| Versión | 1.0 |

**Campos requeridos (según tabla de identificación del PDF):**
- Costos y Beneficios
- Indicadores de evaluación

---

# Objetivo

> No especificado en el documento.

---

# Descripción

Este caso de uso permite al actor "Técnico URP" visualizar el flujo de caja del proyecto, para el período de evaluación y el resultado de los indicadores de evaluación socioeconómica que el sistema calcula automáticamente a partir de los datos de beneficios y costos registrados.

# Actor Principal

Técnico URP

---

# Actores Secundarios

- Sistema (ejecuta el avance de sección y los cálculos automáticos)
- Técnico PRE (según RN02)
- Usuarios centrales y las instituciones CEPA, CEL, ANDA, INDES, ISTU (según RN06). **Resuelto (RQ-C-03, ronda 3):** "usuarios centrales" es sinónimo de "usuarios internos" (CU-PRE-17, CU-PRE-20) y "actores internos" (CU-PRE-17) — cualquier usuario del Ministerio de Hacienda, independientemente de su rol. CEPA, CEL, ANDA, INDES e ISTU **no** forman parte de ese grupo por pertenecer al Ministerio de Hacienda; tienen acceso por un motivo distinto y propio de este caso de uso: son empresas/instituciones que proyectan **ingresos** (no beneficios) y deben reportarlos en el sistema para construir el flujo financiero. Ver nota de resolución completa en RN06 y nombres completos en "Catálogos Detectados".

---

# Disparador

> No especificado en el documento.

---

# Precondiciones

1. Contar con CUP de CU-PRE-01 "Registro de proyectos".
2. Contar con la Ruta de Preinversión generada en CU-PRE-03.5 "Selección y registro de etapas".
3. CU-PRE-17 Presupuesto de inversión.
4. CU-PRE-18 Flujo de costos de Operación y Mantenimiento.
5. CU-PRE-20 Flujo de Beneficios.

> Nota: los ítems 3, 4 y 5 aparecen en el documento inmediatamente después del texto de precondiciones, sin un encabezado propio que indique si forman parte de las precondiciones o si constituyen una lista separada de casos de uso relacionados/dependencias (ver sección Observaciones).

---

# Flujo Principal

1. Técnico URP ingresa a la pestaña "Evaluación" en la sección "Flujo de Caja e Indicadores" (Anexo A.1).
2. Técnico URP visualiza la tabla "Flujo de Caja" y los "Indicadores de evaluación" que indican la conveniencia o no de ejecutarlo.

---

# Flujos Alternos

## FA-01 Siguiente

**Condición**

> No especificado en el documento.

**Flujo**

1.1. Técnico URP da clic en el botón "Siguiente".
1.2. Sistema avanza a la siguiente sección CU-PRE-23 "Indicadores del Proyecto" para continuar con el ingreso de información.

**Resultado**

> No especificado en el documento.

---

# Excepciones

| Código | Descripción | Consecuencia |
|--------|-------------|---------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Postcondiciones

1. El proyecto avanza a CU-PRE-21.5 "Flujo de Caja Financiero".
2. El proyecto avanza a CU-PRE-23 "Indicadores del Proyecto".

---

# Reglas de Negocio

## RN01

**Descripción:** El Técnico URP y todos los demás actores únicamente podrán visualizar la información de las Unidades Ejecutoras, según credenciales.

**Origen:** No especificado en el documento.

## RN02

**Descripción:** El Técnico PRE podrá visualizar la información de todas las Unidades Ejecutoras.

**Origen:** No especificado en el documento.

## RN03

**Descripción:** Los valores de los flujos de COSTOS Y BENEFICIOS de la tabla serán trasladados y ubicados en los años correspondientes, de acuerdo con lo siguiente:

a) **"Costos de Inversión"**: procederán de la fila "Inversión Estimada (Precios Ajustados)" del CU-PRE-17 "Presupuesto de inversión". En la tabla "Flujo de caja e Indicadores" los costos de inversión se ubicarán a partir del "año 0" hasta el año que corresponda a la cantidad de períodos registrados en la tabla "Inversión del proyecto" del CU-PRE-17 "Presupuesto de inversión" (por ejemplo, si en el CU-PRE-17 "Presupuesto de inversión" se registró la inversión en Período 0, Período 1 y Período 2, la tabla "Flujo de Caja e Indicadores" mostrará los "Costos de Inversión" en los Períodos 0, 1 y 2).

b) **"Costos de Operación"**: Procederán de la fila "Total (Precios Ajustados)" de la tabla "Costos de Operación" del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". En la tabla "Flujo de caja e Indicadores" los Costos de Operación se posicionarán en el periodo en que se registró la información en CU-PRE-18 "Flujo de costos de Operación y Mantenimiento"; asimismo, la cantidad de periodos en que se coloquen los costos de operación debe ser igual al registrado en el campo "Vida útil (periodos a proyectar)" del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento".

c) **"Costos de Mantenimiento"**: Procederán de la fila "Total (Precios Ajustados)" de la tabla "Costos de Mantenimiento" del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento". En la tabla "Flujo de caja e Indicadores" los Costos de Mantenimiento se posicionarán en el periodo en que se registró la información en CU-PRE-18 "Flujo de costos de Operación y Mantenimiento"; asimismo, la cantidad de periodos en que se coloquen los costos de mantenimiento debe ser igual al registrado en el campo "Vida útil (periodos a proyectar)" del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento".

d) **"TOTAL COSTOS"**: El Sistema lo calculará para cada periodo así:

`TOTAL COSTOSₙ = Costos de inversiónₙ + Costos de operaciónₙ + Costos de mantenimientoₙ`

e) **"Beneficios"**: Procederán de la fila "Flujo Beneficios (Precios ajustados)" de la tabla "Beneficios del Proyecto" del CU-PRE-20 "Flujo de Beneficios". En la tabla "Flujo de caja e Indicadores" los Beneficios se posicionarán en el periodo en que se registró la información en CU-PRE-20 "Flujo de Beneficios"; asimismo, la cantidad de períodos en que se coloquen los Beneficios debe ser igual al registrado en el campo "Vida útil (periodos a proyectar)" del CU-PRE-20 "Flujo de Beneficios".

f) **"Valor de rescate"**: Procederá del campo "Valor de rescate ajustado" del CU-PRE-20 "Flujo de Beneficios". Se ubicará en el último año del Flujo de Caja.

g) **"TOTAL BENEFICIOS"**: El sistema lo calculará para cada año así:

`TOTAL BENEFICIOSₙ = Beneficiosₙ + Valor de rescateₙ`

h) **"FLUJO NETO DE CAJA"**: El Sistema lo calculará para cada año mediante la fórmula siguiente:

`FLUJO NETO DE CAJA(periodo n) = TOTAL BENEFICIOS(periodo n) − TOTAL COSTOS(periodo n)`

**Origen:** No especificado en el documento.

## RN04

**Descripción:** El cálculo del flujo de caja se deberá realizar tanto a precios de mercado como a precios ajustados. Para ello, la tabla del Anexo A.1, deberá calcularse tanto a precios de mercado como a precios ajustados, este último aplicando tomando los flujos de costos y de beneficios calculados en CU-PRE-17 "Presupuesto de inversión", CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" y CU-PRE-20 "Flujo de Beneficios".

**Origen:** No especificado en el documento.

## RN05

**Descripción:** Para calcular la evaluación financiera, se utiliza el flujo de caja a precios de mercado, y la tasa de oportunidad que el formulador digite en el instante que le da clic al botón "Evaluación Financiera". (Ver CU-PRE-21.5 "Flujo de caja financiero").

**Origen:** No especificado en el documento.

## RN06

**Descripción:** El botón "Evaluación Financiera" será visible y estará habilitado únicamente para los usuarios centrales y para las instituciones siguientes: CEPA, CEL, ANDA, INDES, ISTU.

**Origen:** No especificado en el documento.

> ✅ RESUELTO (RQ-C-03, ronda 3): la contradicción/inconsistencia terminológica queda resuelta. El negocio confirmó que "usuarios internos" (CU-PRE-17 RN10, CU-PRE-20 RN09), "actores internos" (CU-PRE-17 RN11) y "usuarios centrales" (esta RN06, y CU-PRE-20 FA1.1.4/FA2.1.4/Anexo B) son sinónimos de un mismo grupo: cualquier usuario que se disponga dentro del Ministerio de Hacienda, independientemente de su rol.
>
> La adición explícita de CEPA, CEL, ANDA, INDES e ISTU en esta RN06 **no** amplía ese grupo ni contradice la definición anterior: el negocio aclaró que estas instituciones tienen acceso al botón "Evaluación Financiera" por un motivo distinto y propio de este caso de uso — son empresas/instituciones que proyectan **ingresos** (no beneficios) y deben reportarlos en el sistema para construir el flujo financiero. Es decir, esta RN06 concede acceso a dos grupos con fundamentos distintos: (1) "usuarios centrales" = usuarios del Ministerio de Hacienda, y (2) CEPA/CEL/ANDA/INDES/ISTU = instituciones que proyectan ingresos, ambos necesarios para completar el flujo financiero. Ver nombres completos y clasificación de estas instituciones en "Catálogos Detectados". El control de acceso al botón "Evaluación Financiera" ya puede implementarse conforme a esta definición.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|-------------|--------------------|---------------|
| Costos de inversión | Campo que muestra costos de inversión por año según RN03 a) en US$. El sistema deberá agregar el separador de miles (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| Costos de operación | Campo que muestra costos de operación por año según RN03 b) en US$. El sistema deberá agregar el separador de miles (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| Costos de mantenimiento | Campo que muestra los costos de mantenimiento por año según RN03 c) en US$. El sistema deberá agregar el separador de miles (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| Total costos | Campo que muestra el total de costos por año según RN03 d) en US$. El sistema deberá agregar el separador de miles (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| Beneficios | Campo que muestra los beneficios por año según RN03 e) en US$. El sistema deberá agregar el separador de miles (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| Valor de rescate | Campo que muestra el valor de rescate por año según RN03 f) en US$. El sistema deberá agregar el separador de miles (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| Total Beneficios | Campo que muestra el total de beneficios por año según RN03 g) en US$. El sistema deberá agregar el separador de miles (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| Flujo neto de caja | Campo que muestra el flujo neto de caja según RN03 h) en US$. El sistema deberá agregar el separador de miles (,) | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| VANS | Campo que muestra el Valor Actual Neto social del proyecto. Fórmula: VANS = −I0 + Σ(t=1 a n) [Ft / (1+i)^t]. Donde Ft = Flujos de dinero en cada periodo (tomados de la fila "Flujo neto de caja"); I0 = Inversión realizada en el momento inicial (tomada de la fila "Flujo neto de caja" del año 0); n = número de periodos de tiempo; i = tasa social de descuento, con un valor de 12%. Si el cálculo genera #indeterminado#, el campo debe aparecer con N/A | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| TIRS | Campo que muestra la tasa de rentabilidad en donde el VANS es cero. Fórmula: Σ(t=0 a n) [Fn / (1+i)^n] = 0. Donde Fn = Flujos de dinero en cada periodo (tomados de la fila "Flujo neto de caja"); n = número de periodos de tiempo, desde el periodo 0 hasta el periodo n; i = tasa social de descuento, con un valor de 12%. Si el cálculo genera #indeterminado#, el campo debe aparecer con N/A | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | No editable |
| R B/C | Campo que muestra la relación entre los beneficios y costos del proyecto a precios sociales, actualizados a la tasa social de descuento. Fórmula: R B/C = Σ(t=0 a n)[Bt/(1+i)^t] / Σ(t=0 a n)[Ct/(1+i)^t]. Donde Bt = Beneficios del periodo t del proyecto (fila "TOTAL BENEFICIOS" a precios ajustados para cada periodo); Ct = Costos del periodo t del proyecto (fila "TOTAL COSTOS" a precios ajustados para cada periodo, incluyendo el periodo 0); t = periodo correspondiente a la vida del proyecto, varía entre 0 y n; i = tasa social de descuento, con un valor de 12% | Razón | decimal | No especificado en el documento. | No especificado en el documento. | No editable. No se especifica en el documento la regla de "N/A" por indeterminado para este campo (ver Observaciones) |
| VACS | Campo que muestra los costos del proyecto a precios sociales, actualizados a la tasa social de descuento. Fórmula: VACS = I0 + Σ(t=1 a n)[Ct/(1+i)^t]. Donde I0 = Inversión realizada en el momento inicial (fila "TOTAL COSTOS" del periodo 0); Ct = Costos en cada periodo (fila "TOTAL COSTOS"); n = número de periodos de tiempo, desde el periodo 1 hasta el periodo n; i = tasa social de descuento, con un valor de 12%. Si el cálculo genera #indeterminado#, el campo debe aparecer con N/A | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |
| CAE | Campo que muestra un costo uniforme por año, utilizando una tasa social de descuento. Fórmula: CAE = VAC × [i×(1+i)^n / ((1+i)^n − 1)]. Donde VAC = Valor Actual de los Costos del Proyecto (campo "VACS"); n = periodos de vida estimada del proyecto (el Sistema tomará el periodo n); i = tasa social de descuento, con un valor de 12%. Si el cálculo genera #indeterminado#, el campo debe aparecer con N/A | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | No editable |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|-------|------------|-------------------|
| VANS | Si el cálculo genera #indeterminado# | El campo debe aparecer con N/A |
| TIRS | Si el cálculo genera #indeterminado# | El campo debe aparecer con N/A |
| VACS | Si el cálculo genera #indeterminado# | El campo debe aparecer con N/A |
| CAE | Si el cálculo genera #indeterminado# | El campo debe aparecer con N/A |

---

# Errores

| Código | Descripción | Acción esperada |
|--------|-------------|-------------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|----------------|
| Técnico URP | Visualizar información de las Unidades Ejecutoras según credenciales | RN01 |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras | RN02 |
| Usuarios centrales (RQ-C-03: sinónimo de "usuarios internos"/"actores internos", cualquier usuario del Ministerio de Hacienda) e instituciones CEPA, CEL, ANDA, INDES, ISTU (acceso por proyectar ingresos, no por pertenecer al grupo anterior — ver RN06) | Visualizar y utilizar el botón "Evaluación Financiera" | RN06 |

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01 Registro de proyectos
- CU-PRE-03.5 Selección y registro de etapas
- CU-PRE-17 Presupuesto de inversión
- CU-PRE-18 Flujo de costos de Operación y Mantenimiento
- CU-PRE-20 Flujo de Beneficios
- CU-PRE-21.5 Flujo de Caja Financiero
- CU-PRE-23 Indicadores del Proyecto

**Procesos relacionados:**
- Cálculo del flujo de caja (precios de mercado y precios ajustados)
- Cálculo de indicadores de evaluación socioeconómica (VANS, TIRS, R B/C, VACS, CAE)
- Evaluación financiera

**Servicios externos:**

> No especificado en el documento.

---

# Pantallas

## Flujo de Caja e Indicadores (Anexo A.1)

**Descripción:** Pantalla ubicada en la pestaña "Evaluación", sección "Flujo de Caja e Indicadores". Muestra la tabla "FLUJO DE CAJA" con las filas "Costos y Beneficios" organizadas por columnas de "Períodos" (0, 1, 2, 3, 4, 5, 6, 7, ..., n), y el bloque "INDICADORES DE EVALUACIÓN".

**Campos:**
- Inversión Estimada
- Costos de Operación
- Costos de Mantenimiento
- TOTAL COSTOS
- Beneficios
- Valor de rescate
- TOTAL BENEFICIOS
- FLUJO NETO DE CAJA
- VANS
- TIRS
- R B/C
- VACS
- CAE

**Botones:**
- "Evaluación Financiera"
- "SIGUIENTE"

**Acciones:**
- Visualizar la tabla "Flujo de Caja" y los "Indicadores de evaluación".
- Dar clic en "SIGUIENTE" para avanzar a CU-PRE-23 "Indicadores del Proyecto".
- Dar clic en "Evaluación Financiera" (visible y habilitado únicamente para usuarios centrales y las instituciones CEPA, CEL, ANDA, INDES, ISTU) para acceder a CU-PRE-21.5 "Flujo de caja financiero".

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|----------------|
| Informativo | N/A | Cuando el cálculo de VANS, TIRS, VACS o CAE genera un resultado indeterminado (#indeterminado#) |

---

# Observaciones

- El campo "R B/C" no incluye explícitamente en el documento la regla "Si el cálculo genera #indeterminado#, el campo debe aparecer con N/A" que sí se documenta de forma explícita para VANS, TIRS, VACS y CAE, sin que el documento aclare si se trata de una omisión o de una decisión intencional de diseño.
- La lista de CU-PRE-17 "Presupuesto de inversión", CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" y CU-PRE-20 "Flujo de Beneficios" aparece en la tabla de identificación inmediatamente después del texto de "Precondiciones", sin un encabezado propio que indique si forma parte de las precondiciones o si constituye una lista independiente de casos de uso relacionados/dependencias.
- El documento no incluye un campo "Origen" explícito para ninguna de las reglas de negocio RN01 a RN06.
- El documento presenta uso inconsistente de terminología entre "año" y "periodo" dentro de las fórmulas de los indicadores (VANS, TIRS, R B/C, VACS, CAE), incluyendo marcas de texto tachado en el original que sugieren una corrección de "año" a "periodo" no aplicada de manera uniforme en todo el documento.
- El documento distingue en el Anexo B.1 dos columnas independientes, "Tipo" y "Formato". Para la mayoría de los campos ambos valores coinciden (por ejemplo, Moneda/Moneda, Porcentaje/Porcentaje), pero para el campo "R B/C" difieren explícitamente: Tipo = "Razón", Formato = "decimal".

---

# Entidades Detectadas

> Información derivada: reorganización analítica de las entidades mencionadas en el documento; no es texto literal del PDF.

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Proyecto | Entidad principal sobre la cual se calcula el flujo de caja y los indicadores de evaluación | Visualización |
| Flujo de Caja | Conjunto de datos de costos y beneficios organizados por período (0 a n) | Cálculo automático / Visualización |
| Costos de Inversión | Dato trasladado desde CU-PRE-17 "Presupuesto de inversión" | Visualización |
| Costos de Operación | Dato trasladado desde CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" | Visualización |
| Costos de Mantenimiento | Dato trasladado desde CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" | Visualización |
| Beneficios | Dato trasladado desde CU-PRE-20 "Flujo de Beneficios" | Visualización |
| Valor de rescate | Dato trasladado desde CU-PRE-20 "Flujo de Beneficios" | Visualización |
| Indicadores de Evaluación (VANS, TIRS, R B/C, VACS, CAE) | Resultados calculados automáticamente por el sistema a partir del flujo de caja | Cálculo automático / Visualización |

---

# Catálogos Detectados

> Información derivada: reorganización analítica a partir de RN06; no es texto literal del PDF.

| Catálogo | Valores conocidos |
|----------|--------------------|
| Instituciones con acceso al botón "Evaluación Financiera" | CEPA, CEL, ANDA, INDES, ISTU |

> ✅ RESUELTO (RQ-C-03, ronda 3): nombres completos y clasificación institucional confirmados en `CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx` (hoja "UNIDADES EJECUTORAS" / "CLASIFICADOR INSTITUCIONAL"):

| Sigla | Nombre completo | Clasificación |
|---|---|---|
| CEPA | Comisión Ejecutiva Portuaria Autónoma | Empresa Pública No Financiera |
| CEL | Comisión Ejecutiva Hidroeléctrica del Río Lempa | Empresa Pública No Financiera |
| ANDA | Administración Nacional de Acueductos y Alcantarillados | Empresa Pública No Financiera |
| INDES | Instituto Nacional de los Deportes de El Salvador | Institución Descentralizada No Empresarial |
| ISTU | Instituto Salvadoreño de Turismo | Institución Descentralizada No Empresarial |

> El negocio confirmó el motivo de esta lista cerrada: estas instituciones proyectan **ingresos** (no beneficios) y deben reportarlos en el sistema para construir el flujo financiero; por eso tienen acceso al botón "Evaluación Financiera" (RN06), de forma independiente al grupo "usuarios centrales". Ver nota de resolución completa en RN06.

---

# Eventos del Sistema

> Información derivada: reorganización analítica a partir del Flujo Principal, FA-01 y RN06; no es texto literal del PDF.

| Evento | Origen | Destino |
|--------|--------|---------|
| Clic en botón "SIGUIENTE" | Técnico URP | Avance a CU-PRE-23 "Indicadores del Proyecto" |
| Clic en botón "Evaluación Financiera" | Usuarios centrales / CEPA, CEL, ANDA, INDES, ISTU | CU-PRE-21.5 "Flujo de caja financiero" |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| No especificado en el documento. | No especificado en el documento. | No especificado en el documento. |

---

# Datos Pendientes de Definir

1. No se especifica el disparador (evento) explícito que inicia este caso de uso.
2. No se especifica una tabla de excepciones para este caso de uso (sección "Excepciones" no desarrollada en el documento).
3. No se especifica una tabla de errores (códigos, descripciones, acciones esperadas) para este caso de uso.
4. No se especifica la prioridad del caso de uso.
5. No se especifica el campo "Origen" para las reglas de negocio RN01 a RN06.
6. No se especifica si el campo "R B/C" debe mostrar "N/A" cuando el cálculo resulte indeterminado, dado que esta regla sí se documenta explícitamente para VANS, TIRS, VACS y CAE, pero no para R B/C.
7. No se especifica claramente si CU-PRE-17, CU-PRE-18 y CU-PRE-20 (listados justo después de las precondiciones en la tabla de identificación) constituyen precondiciones adicionales o una lista separada de dependencias/casos relacionados.
8. No se especifica una "Condición" ni un "Resultado" explícitos para el flujo alterno FA-01 más allá del título "Siguiente" y los pasos 1.1 y 1.2 descritos.
9. **Resuelto (RQ-C-03, ronda 3):** "usuarios centrales" (RN06), "usuarios internos" (CU-PRE-17, CU-PRE-20) y "actores internos" (CU-PRE-17) se refieren al mismo grupo de usuarios (cualquier usuario del Ministerio de Hacienda). RN06 añade explícitamente a CEPA, CEL, ANDA, INDES e ISTU por un motivo distinto: son instituciones que proyectan ingresos (no beneficios) y deben reportarlos para construir el flujo financiero — no forman parte del grupo "usuarios centrales". Ver nota de resolución en RN06 y nombres completos en "Catálogos Detectados".