---
id: CU-PRE-30
codigo: CU-PRE-30
nombre: Programación Cuatrimestral Financiera de la Preinversión
modulo: Preinversión
submodulo: Gestión del Proyecto
version: "1.2 (1.1 según el PDF/anexo original; ver nota_cambio_v1_2 — la versión 1.2 refleja una decisión funcional solicitada por el usuario, no una nueva versión del PDF fuente)"
fuente_pdf: CU-PRE-30_Programación_Cuatrimestral_Financiera_de_la_Preinversión_F.pdf
fuente_anexo_xlsx: "CU-PRE-30-33__ANEXO__Esquemas.xlsx (hojas 'CUPRE-30 A.1' y 'CUPRE-30 A.2'; el archivo cubre además los CU-PRE-31 a 33 en hojas separadas, no analizadas en este documento)"
pagina_inicio: 1
pagina_fin: 16

nota_version: >
  La versión 1.1 incorpora el archivo Excel anexo
  `CU-PRE-30-33__ANEXO__Esquemas.xlsx`, aportado posteriormente y compartido
  entre varios casos de uso (CU-PRE-30 a CU-PRE-33), cada uno en sus propias
  hojas independientes. Para este documento se revisaron únicamente las
  hojas "CUPRE-30 A.1" y "CUPRE-30 A.2", que corresponden a los mockups ya
  transcritos como Anexo A.1 y Anexo A.2 a partir del PDF en la versión 1.0.
  Se verificó, celda por celda, que ambas hojas coinciden exactamente con
  el contenido ya documentado (montos, CUP, nombres de proyecto, fuentes de
  financiamiento/recursos/convenio, programación cuatrimestral, totales, y
  el mensaje de error "Las etapas no coinciden con las registradas en la
  Ruta de Preinversión..." ya transcrito como Anexo A.7). No se detectaron
  discrepancias ni contenido nuevo. No se modificó ningún otro contenido de
  la versión 1.0.

nota_cambio_v1_2: >
  Cambio funcional solicitado por el usuario (31/08/2026), NO proveniente del
  PDF fuente: se aclara que CU-PRE-30 no cuenta con un flujo propio de
  "solicitud de aprobación"/"envío a revisión" independiente. La única
  mención existente en la versión 1.1 era la referencia ambigua al botón
  "Enviar a revisión DGI" en el texto explicativo del Anexo A.8 (Reporte),
  la cual no estaba descrita en ningún Flujo Básico, Subflujo ni Regla de
  Negocio de este documento, y contradecía a la RN-E (que declara el botón
  "GENERAR REPORTE" visible y habilitado para todos los actores sin
  condición previa). A solicitud del usuario, se documenta explícitamente
  que el Técnico URP realiza una única solicitud de aprobación/envío a
  revisión, y el Técnico PRE realiza una única aprobación ("Revisión
  Finalizada"), ambas ejecutadas en CU-PRE-31 "Programación Cuatrimestral
  de Metas Físicas de la Preinversión" (Subflujo SF-2, paso 7 "Enviar a
  revisión DGICP", y Subflujo SF-6 "Finalizar Revisión"), las cuales ya
  cubren, según el propio texto de CU-PRE-31, "tanto financiera como de
  metas físicas". No se elimina ni se reescribe ningún contenido literal
  del PDF; se preserva la transcripción original del Anexo A.8 y se agregan
  notas aclaratorias en las secciones correspondientes (ver "Pantallas",
  "Observaciones", "Datos Pendientes de Definir" y "Dependencias"). Este
  mismo cambio se aplicó de forma consistente en CU-PRE-31 (ver su propio
  `nota_cambio_v1_2`).

actor_principal: [Técnico URP,Técnico PRE]

actores_secundarios: [Coordinador PRE, Coordinador PRO,Técnico PRO,Jefe DGI,Subjefe DGI, Administrador del Sistema]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-26"]

casos_relacionados: ["CU-PRE-31 Programación Cuatrimestral de Metas Físicas de la Preinversión", "CU-PRO-25 Monitoreo Programación PAP (nombre oficial confirmado por resolución de negocio RQ-C-07, ronda 3; el documento original citaba \"Monitoreo de la programación del PAP\")", "CU-PRO-01 Elaboración/Actualización del Programa de Inversión Pública de Mediano Plazo - PRIPME"]

roles: ["Técnico URP", "Técnico PRE", "Coordinador PRE", "Coordinador PRO", "Técnico PRO", "Jefe DGI", "Subjefe DGI", "Administrador del Sistema"]

pantallas: ["Anexo A.1 - Programación Financiera Cuatrimestral del PAP", "Anexo A.2 - Programación Financiera por Etapa de Preinversión", "Anexo A.3 - Monto Programado supera el Costo de la Etapa", "Anexo A.4 - Periodo de ingreso de información ha finalizado", "Anexo A.5 - Eliminar Financiamiento", "Anexo A.6 - No puede Eliminar Etapa/Financiamiento", "Anexo A.7 - No puede saltarse la Ruta de Preinversión", "Anexo A.8 - Reporte de la Programación Financiera de la Preinversión"]

procesos: []

servicios_externos: []

entidades: ["Proyecto/Estudio", "Etapa de Preinversión", "Fuente de Financiamiento", "Fuente de Recursos", "Convenio", "Programación Cuatrimestral", "Reporte de Programación Financiera", "Calendario de Eventos del PAP"]

catalogos: ["Fuentes de Financiamiento (catálogo remitido a CU-PRE-17 'Presupuesto de inversión'; no incluido en este documento)", "Fuentes de Recursos (catálogo remitido a CU-PRE-17; no incluido en este documento)", "Convenios (catálogo remitido a CU-PRE-17; no incluido en este documento)"]

palabras_clave: ["programación cuatrimestral", "PAP", "Programa Anual de Preinversión", "fuente de financiamiento", "cuatrimestre", "etapa de preinversión", "ruta de preinversión", "año n+1"]

ultima_actualizacion: "02/10/2025"

trazabilidad:
  informacion_general:
    pagina: 3
  flujo_principal:
    FB:
      pagina: 4
  flujos_alternos:
    SF1:
      pagina: 4
    SF2:
      pagina: 5
    SF3:
      pagina: 5
    SF4:
      pagina: 5
    SF5:
      pagina: 6
    SF6:
      pagina: 6
  reglas_negocio:
    RNA:
      pagina: 6
    RNB:
      pagina: 7
    RNC:
      pagina: 8
    RND:
      pagina: 8
    RNE:
      pagina: 8
  anexos:
    A1:
      nombre: "Programación Financiera Cuatrimestral del PAP"
      pagina: 10
    A2:
      nombre: "Programación Financiera por Etapa de Preinversión"
      pagina: 11
    A3:
      nombre: "Monto Programado supera el Costo de la Etapa"
      pagina: 11
    A4:
      nombre: "Periodo de ingreso de información ha finalizado"
      pagina: 12
    A5:
      nombre: "Eliminar Financiamiento"
      pagina: 12
    A6:
      nombre: "No puede Eliminar Etapa/Financiamiento"
      pagina: 12
    A7:
      nombre: "No puede saltarse la Ruta de Preinversión"
      pagina: 13
    A8:
      nombre: "Reporte de la Programación Financiera de la Preinversión"
      pagina: 14
    B:
      nombre: "Requerimientos Funcionales - Formatos"
      pagina: 15
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original (aunque el pie de página del documento sí incluye numeración propia, que coincide con la aquí estimada)."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Programación Cuatrimestral Financiera de la Preinversión |
| Código | CU-PRE-30 |
| Módulo | Programación |
| Fuente | CU-PRE-30_Programación_Cuatrimestral_Financiera_de_la_Preinversión_F.pdf; complementado con el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hojas "CUPRE-30 A.1"/"CUPRE-30 A.2" — ver "Pantallas") |
| Versión | 1.2 (1.1 según el PDF/anexo original; la 1.2 refleja una decisión funcional del usuario, no una nueva versión del PDF — ver "Historial de Revisiones" y `nota_cambio_v1_2` en el Front Matter) |

**Campos requeridos (según el PDF):**
> No especificado en el documento. Este documento no incluye una sección explícita titulada "Campos requeridos" en la identificación del caso de uso (a diferencia de otros documentos del mismo sistema). En su lugar, el documento incluye una sección "Ruta de Acceso" (Sistema de Información de Inversión Pública → Programación → PAP Institucional → Financiera), que se transcribe a continuación por su relevancia funcional:

**Ruta de Acceso (según el PDF):**
- Sistema de Información de Inversión Pública
- Programación
- PAP Institucional
- Financiera

> Nota: los valores `modulo` y `submodulo` del Front Matter se derivaron de esta "Ruta de Acceso", ya que el documento no tiene un campo explícito llamado "Módulo" o "Submódulo".

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|---|---|---|---|---|
| 21/7/2025 | 1.0 | Versión Inicial | Ricardo Orellana | No especificado en el documento. |
| 2/10/2025 | 1.0 | Versión Inicial modificada | Equipo Preinversión | No especificado en el documento. |

> Nota (fuera del PDF, no forma parte de la tabla anterior): la versión "1.2" de este Markdown corresponde a una decisión funcional solicitada por el usuario (31/08/2026), no a una nueva versión del PDF fuente ni del Historial de Revisiones oficial del documento. Ver `nota_cambio_v1_2` en el Front Matter para el detalle.

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección o campo explícito titulado "Objetivo".

---

# Descripción

Este caso de uso es para programar en forma cuatrimestral los recursos financieros de cada uno de los estudios de proyectos para el año n+1 del Programa Anual de Preinversión (PAP) Institucional.

Al Técnico de la Unidad Responsable de Proyectos (URP)-Técnico URP, le permite:
- Ingresar la programación financiera cuatrimestral de los estudios para el año n+1.
- Consultar información de estudios programados en ejercicios anteriores.

Para el Técnico de Preinversión-Técnico PRE, este caso de uso permite:
- Revisar la información registrada por el Técnico URP.
- Consultar información de estudios programados en ejercicios anteriores.

Además, para este caso de uso se podrán asignar roles de consulta a actores internos y externos según credenciales. Asimismo, con este caso de uso, los usuarios podrán exportar la información en el formato que seleccione (Excel o PDF).

# Actor Principal

La sección "1. Actores" del documento lista conjuntamente:
- Técnico de Unidad Responsable de Proyectos (Técnico URP)
- Técnicos de Preinversión (Técnico PRE)
- Coordinador Área de Preinversión, Coordinador Área de Programación y Análisis de la Inversión (Coordinador PRE y Coordinador PRO)
- Técnico en Programación y Análisis de la Inversión (Técnico PRO)
- Jefe de la División de Gestión de la Inversión (Jefe DGI)
- Subjefe de la División de Gestión de la Inversión (Subjefe DGI)

> Nota de ambigüedad: el documento no distingue explícitamente un "actor principal"; sin embargo, la "Breve Descripción" y las Reglas de Negocio (RN-A c) atribuyen roles sustanciales y diferenciados únicamente al Técnico URP (responsable del ingreso de la información) y al Técnico PRE (encargado de revisar la información), mientras que el resto de actores tienen únicamente derecho de consulta. Ver Observaciones.

---

# Actores Secundarios

- Coordinador PRE (interviene además en RN-A c —consulta— y en SF-4/SF-5 como solicitante de habilitación de modificaciones al PAP)

> ✅ RESUELTO (RQ-C-01, Ronda 5) — no aplica: el negocio confirmó que este documento (CU-PRE-30) **no tiene campos de comentarios/observaciones** en su formato, a diferencia de CU-PRE-31 (que sí los tiene y con el cual CU-PRE-30 funciona como un solo flujo secuencial, según la misma resolución). Por lo tanto, la pregunta de si el Coordinador PRE debería tener la capacidad de "Revisión Finalizada" no aplica a este documento. El alcance del Coordinador PRE en CU-PRE-30 se mantiene sin cambios: consulta y solicitante de habilitación de modificaciones, según RN-A.c.
- Coordinador PRO
- Técnico PRO
- Jefe DGI
- Subjefe DGI
- Administrador del Sistema (ejecuta el primer paso de los Subflujos SF-4 y SF-5; no está listado en la sección "1. Actores" del documento — ver Observaciones)

---

# Disparador

> No especificado en el documento. El documento no incluye un campo explícito titulado "Disparador".

---

# Precondiciones

1. CU-PRE-01 Registro de Proyectos (CUP)
2. CU-PRE-03.5 Selección y registro de etapas
3. CU-PRE-17 "Presupuesto de inversión"
4. CU-PRE-22.1 Programación financiera de la preinversión del Proyecto
5. Calendario de eventos para programación, seguimiento y ejecución cuatrimestral del PAP.

> Nota de ambigüedad: el documento lista estos cinco elementos bajo el encabezado "4. Precondiciones" sin un verbo explícito (p. ej. "contar con", "haber ejecutado") que aclare la redacción formal de la condición. Adicionalmente, el quinto elemento ("Calendario de eventos...") no incluye un código de caso de uso propio en esta sección, aunque en la RN-A b) se referencia como "Caso de Uso CU-ADM XX 'Gestión de Eventos de Calendario'" — código que contiene un marcador de posición ("XX") en lugar de un número definitivo. Ver Observaciones y Datos Pendientes de Definir.

---

# Flujo Principal

## FB — Flujo Básico

1. Actor. Ingresa en la pantalla del Anexo A.1, de acuerdo a las credenciales: Selecciona la Unidad Ejecutora y el año que desea visualizar/registrar.
2. Sistema. Muestra la pantalla descrita en Anexo A.1. El Sistema mostrará la lista de todos los estudios que vienen de arrastre, es decir, aquellos cuya ejecución financiera y/o física de la Preinversión no haya sido completada en el ejercicio anterior. Para cada uno de estos estudios deberá mostrar la información de los campos "CUP", "Nombre del proyecto", "Etapa", "Fuente de Financiamiento", "Costo de la etapa" y "Ejecutado años anteriores".
3. Actor. Selecciona uno de los siguientes subflujos:
   - SF-1 Registrar Programación PAP (para estudios de arrastre)
   - SF-2 Agregar un nuevo estudio y registrar Programación PAP
   - SF-3 Generar Reporte
   - SF-4 Modificaciones al PAP agregar un nuevo estudio
   - SF-5 Modificaciones al PAP modificar los registros de un estudio existente

Caso de Uso Termina.

> Nota de ambigüedad: el paso 3 del Flujo Básico no incluye el SF-6 "Botón Programación de Metas" entre las opciones de subflujo, aunque dicho subflujo está descrito en la sección "2. Flujo de Eventos" y referenciado en la RN-A c) (botón "Programación de Metas" habilitado para Técnico URP y Técnico PRE). Ver Observaciones.

---

# Flujos Alternos

> El documento no utiliza la nomenclatura "Flujo Alternativo – FA" para este caso de uso, sino "Subflujos" (SF-1 a SF-6), presentados en la sección "2. Flujo de Eventos". Se transcriben a continuación conservando esa nomenclatura original, dentro de la estructura de "Flujos Alternos" solicitada.

## SF-1 — Registrar Programación PAP (para estudios de arrastre)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP. Da clic en el código de proyecto.
2. Sistema. Muestra el Anexo A.2. Programación Financiera por Etapa de Preinversión.
3. Técnico URP. Ingresa información en los campos de la columna "PROGRAMACIÓN CUATRIMESTRAL". Dado que los estudios son de arrastre, los campos "Fuente de Financiamiento", "Fuente de Recursos" y "Convenio" del Anexo A.2 no estarán habilitados y mostrarán la información que se seleccionó en el ejercicio anterior. Para agregar otra fuente de financiamiento da clic en botón (+) (ver Anexo A.2) y registra los montos de cada cuatrimestre para esa fuente.
4. Técnico URP. Selecciona uno de los siguientes subflujos: Guardar o Salir.
5. Sistema. Si la opción seleccionada es "Guardar": Valida los datos registrados en la Programación Cuatrimestral según se establece en la RN B literal c.2, guarda automáticamente todo el registro de la programación cuatrimestral ingresada y traslada los valores registrados de cada cuatrimestre a los campos correspondientes de la pantalla del Anexo A.1 a cada fuente según corresponda. Si la suma de los valores cuatrimestrales es inferior al "Costo de la etapa", el saldo deberá registrarse en la columna "Años posteriores". En todo caso, la suma de los cuatrimestres no puede ser mayor al "Costo de la etapa". Si la opción seleccionada es "Salir": regresa al Anexo A.1, sin guardar.

Subflujo termina.

**Resultado**

> No especificado en el documento.

## SF-2 — Agregar un nuevo estudio y registrar Programación PAP

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP. Da clic en botón "Agregar estudio".
2. Sistema. Mostrará en la pantalla del Anexo A.2 únicamente el campo "Código" y el botón "Aceptar".
3. Técnico URP. Registra el código y da clic en el botón "Aceptar".
4. Sistema. Realiza validaciones de la Ruta de Preinversión y de las etapas finalizadas en períodos anteriores según RN B b) y RN B e), y muestra la pantalla del Anexo A.2 con los campos habilitados para todas las etapas de la Preinversión que se requieran registrar.
5. Sistema. Muestra la ventana A.2 con los campos "Etapa" y "Costo de la Etapa" prediligenciada con la información registrada en (CU-PRE-03.5 "Selección y registro de etapas"). Este último campo puede ser editable para que el usuario lo pueda cambiar si es necesario en el período de elaboración del PAP. El Sistema mostrará la ventana A.2 con las etapas de la Preinversión registradas en la ruta de Preinversión (CU-PRE-03.5 "Selección y registro de etapas"). Será obligatorio registrar la programación de por lo menos una de las etapas; el Sistema validará según RN B b).
6. Técnico URP. Selecciona, según corresponda, la Fuente de Financiamiento, la Fuente de Recursos y el Convenio y registra los montos de cada Cuatrimestre a programar en el periodo n+1.
7. Sistema. Valida que la diferencia de los valores: "Costo de la Etapa" - "Ejecutado años anteriores" - "Total año" sea igual a cero $(0); en caso de ser menor, el saldo deberá registrarse en la columna "Años posteriores". En todo caso, la suma de los cuatrimestres no puede ser mayor al "Costo de la etapa".
8. Sistema. Calcula los porcentajes para cada cuatrimestre según los montos que se han registrado para el periodo n+1.
9. Técnico URP. Selecciona uno de los siguientes subflujos: Guardar o Salir.
10. Sistema. Si la opción seleccionada es "Guardar": Valida los datos registrados en la Programación Cuatrimestral según se establece en la RN B literal c.1. y los traslada a la tabla del Anexo A.1. Si la opción seleccionada es "Salir": regresa al Anexo A.1, sin guardar.

Subflujo Termina.

**Resultado**

> No especificado en el documento.

## SF-3 — Generar Reporte

**Condición**

> No especificado en el documento.

**Flujo**

1. Actores. Da clic en el botón "Generar Reporte", que podrá seleccionarse en Excel o PDF.
2. Sistema. Verifica el formato seleccionado y genera el reporte (Anexo A.8).

Subflujo Termina.

**Resultado**

> No especificado en el documento.

## SF-4 — Modificaciones al PAP Agregar un nuevo estudio (fuera del período de elaboración del PAP)

**Condición**

> No especificado en el documento como un apartado formal de "Condición"; el propio título del subflujo indica que aplica "fuera del período de elaboración del PAP".

**Flujo**

1. Administrador del Sistema. Habilita el Sistema previa solicitud del Coordinador PRE, contando con nota de solicitud de modificación del PAP remitida por la Institución.

El documento indica literalmente: "Los pasos 2, 3, en adelante, son los mismos desarrollados en el Subflujo 2 Agregar un nuevo estudio y registrar programación PAP."

Subflujo termina.

**Resultado**

> No especificado en el documento.

## SF-5 — Modificaciones al PAP Modificar la programación de un estudio existente (fuera del período de elaboración del PAP)

**Condición**

> No especificado en el documento como un apartado formal de "Condición"; el propio título del subflujo indica que aplica "fuera del período de elaboración del PAP".

**Flujo**

1. Administrador del Sistema. Habilita el Sistema previa solicitud del Coordinador PRE, contando con nota de solicitud de modificación del PAP remitida por la Institución.
2. Técnico URP. Ingresa en la pantalla del Anexo A.1. Da clic en el CUP del estudio que requiere modificar.
3. Sistema. Muestra la pantalla del Anexo A.2.
4. Técnico URP. Registra los ajustes en la programación del estudio según corresponda.
5. Sistema. Valida que la diferencia de los valores: "Costo de la Etapa" - "Ejecutado años anteriores" - "Total año" sea igual a cero $(0); en caso de ser menor, el saldo deberá registrarse en la columna "Años posteriores". En todo caso, la suma de los cuatrimestres no puede ser mayor al "Costo de la etapa".
6. Sistema. Calcula los porcentajes para cada cuatrimestre según los montos que se han registrado para el periodo n+1.
7. Técnico URP. Selecciona uno de los siguientes subflujos: Guardar o Salir.
8. Sistema. Si la opción seleccionada es "Guardar": Valida los datos registrados en la Programación Cuatrimestral según se establece en la RN B literal c.1. Si la opción seleccionada es "Salir": regresa al Anexo A.1, sin guardar.

Subflujo termina.

**Resultado**

> No especificado en el documento.

## SF-6 — Botón Programación de Metas

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP. Da clic en el botón "Siguiente".
2. Sistema. Muestra el Anexo A.1 del CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión".

Subflujo termina.

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no incluye una sección explícita titulada "Excepciones" con Código / Descripción / Consecuencia.

# Postcondiciones

1. CU-PRE-31 Programación Cuatrimestral de Metas Físicas de la Preinversión
2. CU-PRO-25 Monitoreo Programación PAP (nombre oficial, RQ-C-07; ver Observaciones)
3. CU-PRO-01 Elaboración / Actualización del Programa de Inversión Pública de Mediano Plazo - PRIPME

---

# Reglas de Negocio

## RN-A.a
**Descripción:** El campo de "Unidad Ejecutora" aparece por defecto de acuerdo a las credenciales del Técnico URP de registro, y para el caso del Técnico PRE tendrá la opción de lista desplegable con todas las Unidades Ejecutoras, para poder realizar el análisis y la revisión individual. En el campo "Año" por defecto aparecerá el año vigente, con una lista desplegable partiendo desde el año 1999 hasta 10 años adelante del año vigente.
**Origen:** No especificado en el documento.

## RN-A.b
**Descripción:** El sistema no debe de permitir ajustes o ingreso de datos por parte del Técnico URP, fuera de la fecha establecida en el Calendario de Eventos del PAP, según Caso de Uso CU-ADM XX "Gestión de Eventos de Calendario", y se debe de mostrar el mensaje del Anexo A.4 "Periodo de ingreso de información ha finalizado"; y todas las acciones de la tabla Programación Financiera Cuatrimestral del PAP, permanecerán deshabilitadas.
**Origen:** No especificado en el documento.

## RN-A.c
**Descripción:** Los privilegios de cada actor: Técnico URP: responsable del ingreso de la información, podrá registrar la programación de los estudios de arrastre, así como agregar códigos nuevos y sus programaciones correspondientes desde el botón "Agregar estudio". Tendrá habilitado los botones "Generar reporte" y "Programación de Metas". Técnico PRE: encargado de revisar la información, tendrá habilitado los botones "Generar reporte" y "Programación de Metas". Los demás actores: sólo tendrán derecho de consultar la información ingresada por el técnico URP y técnico PRE, es decir no podrán ver ningún ícono en el campo de acciones de la tabla, a excepción de los botones "Generar reporte" y "Programación de Metas".
**Origen:** No especificado en el documento.

## RN-B.a
**Descripción:** En la pantalla del Anexo A.2 los campos "I Cuatrimestre", "II Cuatrimestre" y "III Cuatrimestre", al registrar por primera vez la programación deberán estar en valor "$0.00"; en los campos "Fuente de Financiamiento", "Fuente de Recursos" y "Convenio" se mostrará al registrar por primera vez "Seleccione".
**Origen:** No especificado en el documento.

## RN-B.b
**Descripción:** El sistema permitirá al usuario registrar las etapas previstas en la ruta de preinversión, de los proyectos que serán programados en el PAP, en su orden lógico. No necesariamente debe programar todas las etapas. En todo caso la programación de las etapas debe realizarse en el orden del ciclo de la preinversión. Si se llega a saltar una etapa incluida en la Ruta de Preinversión, el sistema debe emitir una alerta que indique que se ha saltado la ruta de preinversión y por tanto tendrá que programar las etapas allí registradas o, ir a ajustar la ruta de preinversión en CU-PRE-03.5 "Selección y registro de etapas" para continuar con el registro.
**Origen:** No especificado en el documento.

## RN-B.c
**Descripción:** El valor del campo "Total programado Año" de la sección Programación Cuatrimestral del Anexo A.2 debe cumplir las siguientes condiciones:
- **c.1 Cuando son estudios nuevos:** No deberá ser mayor al valor del campo "Costo de la etapa" para el estudio y la Fuente de Financiamiento correspondientes; en caso contrario, el Sistema mostrará el siguiente mensaje al dar clic en el botón "Guardar": "Monto Programado supera el costo de la etapa" (Ver Anexo A.3) y se mantendrá en la pantalla del Anexo A.2. Dicho monto podrá ser inferior al monto pendiente de ejecutar, en cuyo caso el Sistema colocará el remanente en la columna "Años posteriores".
- **c.2 Cuando son estudios de arrastre:** No deberá ser superior al monto pendiente de ejecutar del estudio para la Fuente de Financiamiento correspondiente (dicho monto procede de la diferencia entre el Costo de la etapa y lo Ejecutado en años anteriores), en caso contrario, el Sistema mostrará el siguiente mensaje al dar clic en el botón "Guardar": "Monto Programado supera el costo de la etapa" (Ver Anexo A.3) y se mantendrá en la pantalla del Anexo A.2. Dicho monto podrá ser inferior al monto pendiente de ejecutar, en cuyo caso el Sistema colocará el remanente en la columna "Años posteriores".

Fórmula asociada (Años posteriores): `Años posteriores = Costo de la etapa − Ejecutado años anteriores − Total Año`
**Origen:** No especificado en el documento.

## RN-B.d
**Descripción:** En el campo "Porcentaje" de la sección Programación Cuatrimestral del Anexo A.2 el sistema calculará automáticamente los valores para cada Cuatrimestre así: `% Cuatrimestre = (Monto Programado del cuatrimestre / Total Programado Año) * 100`. La suma de los porcentajes, siempre deberá ser igual al 100%.
**Origen:** No especificado en el documento.

## RN-B.e
**Descripción:** Para el registro de nuevos estudios, el Sistema no mostrará en el Anexo A.2 las etapas de la preinversión finalizadas física y financieramente en años anteriores.
**Origen:** No especificado en el documento.

## RN-C
**Descripción:** Dentro de cada proyecto, se podrán eliminar las etapas del estudio y fuentes de financiamiento que no se quieran programar dentro del período de elaboración del PAP. Cuando se requiera eliminar una etapa y/o Fuente de Financiamiento el sistema preguntará "¿Está seguro de eliminar la fuente de financiamiento seleccionada?" o "¿Está seguro de eliminar la etapa seleccionada?". El sistema validará que la etapa y/o fuente de financiamiento seleccionado para eliminar no tengan ejecución de años anteriores relacionada para ese CUP en la etapa que está programando. Si encuentra ejecución de dicho código en esa etapa, el sistema deberá advertir con un mensaje de Anexo A.6 "Etapa y/o Fuente de Financiamiento no puede ser eliminado, existe ejecución en años anteriores".
**Origen:** No especificado en el documento.

## RN-D
**Descripción:** Un estudio no iniciado podrá eliminarse de la tabla de programación del PAP, siempre y cuando se encuentre dentro del periodo de elaboración del PAP. El Sistema le preguntará si "está seguro de desactivar el proyecto con código XXX". Si el estudio es de arrastre y no cumplió el 100% de lo programado física o financieramente en periodos anteriores, este no podrá eliminarse pues debe culminar lo que está pendiente.
**Origen:** No especificado en el documento.

## RN-E
**Descripción:** Los botones del Anexo A.1 contarán con las siguientes características:
- BUSCAR: Visible y habilitado para todos los actores.
- AGREGAR ESTUDIO: Visible solo para el Técnico URP, habilitado solo para el Técnico URP durante el periodo de ingreso de información o cuando se presenten modificaciones al PAP.
- GENERAR REPORTE: Visible y habilitado para todos los actores.
- Los botones del Anexo A.2 solo serán visibles y estarán habilitados para el Técnico URP durante el período de ingreso de información o cuando se presenten modificaciones al PAP.
**Origen:** No especificado en el documento.

---

# Campos

## Pantalla "Programación Financiera Cuatrimestral del PAP" (Anexo A.1)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Unidad Ejecutora | Para el Técnico PRE permite seleccionar de un listado la Unidad Ejecutora. Para el Técnico URP la mostrará por defecto según credenciales. | Selección | Selección | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Año | Permite seleccionar un año para consultar o registrar la información del PAP del periodo n+1 según corresponda. | Selección | Selección | Sí (texto explícito: "Campo obligatorio.") | El año vigente (RN-A.a). | Editable: No (según Anexo B.1). Rango de la lista desplegable: desde 1999 hasta 10 años adelante del año vigente (RN-A.a). |
| CUP | Muestra el Código Único del Proyecto asignado según CU-PRE-01 "Registro de Proyectos". Procede del campo CUP del Anexo A2. | Numérico | Numérico | No especificado en el documento (no incluye la anotación "Campo obligatorio" — ver Observaciones). | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Nombre del Proyecto | Mostrará el nombre del proyecto según se asignó en el CU-PRE-01 "Registro de Proyectos". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Etapa | Procede del campo "Etapa" del Anexo A2. Debe tener al menos una etapa. | Selección | Selección | Sí (implícito: "Debe tener al menos una etapa"). | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: en la tabla de Formatos del Anexo A.2 este mismo campo se define con Tipo/Formato "Texto"/"Texto" — ver Observaciones. |
| Fuente de Financiamiento | Campo que muestra la Fuente de Financiamiento, el Organismo Financiador y Convenio. La información proviene de los campos "Fuente de Financiamiento", "Fuente de Recursos" y "Convenio" del Anexo A2. Debe tener al menos una fuente de financiamiento. | Texto | Texto | Sí (implícito: "Debe tener al menos una fuente de financiamiento"). | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Costo de la etapa | Cuando son estudios de arrastre: Procede del valor registrado el año en que se programó por primera vez. Cuando son estudios nuevos: Proviene del campo "Costo de la etapa" del Anexo A2. El campo será editable durante el período de elaboración del PAP, tanto para estudios nuevos como para estudios de arrastre. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según la columna "Editable" del Anexo B.1); contradicción interna: la propia columna "Detalle" del mismo campo indica "El campo será editable durante el período de elaboración del PAP" — ver Observaciones. |
| Ejecutado años anteriores | Cuando son estudios de arrastre: Procede del monto ejecutado en años anteriores. Cuando son estudios nuevos: El campo muestra un valor de $0.00. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | $0.00 (para estudios nuevos). | Editable: No (según Anexo B.1). |
| Programación I Cuatrimestre, II Cuatrimestre y III Cuatrimestre | Muestra el monto programado para cada cuatrimestre para el ejercicio seleccionado. El monto procede del campo "Monto" de cada Cuatrimestre del Anexo A2 de este Caso de Uso. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Total programado Año | Muestra el total programado por año para cada estudio. El monto procede del campo "Total Programado Año" del Anexo A2 de este Caso de Uso. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Años posteriores | Muestra el monto pendiente de ejecutar de un estudio. El Sistema lo calculará mediante la fórmula: `Años posteriores = Costo de la etapa − Ejecutado años anteriores − Total Año`. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Campo calculado; fórmula transcrita completa en la Descripción. |

## Pantalla "Programación Financiera por Etapa de Preinversión" (Anexo A.2)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| CUP | Campo para que el Técnico URP registre el código del proyecto cuya programación requiere realizar. Cuando sean estudios de arrastre el Sistema mostrará por defecto el CUP y el campo no será editable. Cuando sea un estudio nuevo a programar el campo será editable. | Numérico | Numérico | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: Sí (según Anexo B.1), condicionado a que el estudio sea nuevo (ver Descripción). |
| Nombre del proyecto | Muestra el nombre del proyecto según se asignó en el CU-PRE-01 "Registro de Proyectos". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Etapa | Muestra la etapa de Preinversión a programar. Procede del campo "Etapas" del Anexo A.2 del CU-PRE-03.5 "Selección y registro de etapas". | Texto | Texto | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: en la tabla de Formatos del Anexo A.1 este mismo campo se define con Tipo/Formato "Selección"/"Selección" — ver Observaciones. |
| Costo de la etapa | Procede del campo "Costo de la etapa" registrado en CU-PRE-03.5 "Selección y registro de etapas". Sin embargo, el usuario Técnico URP podrá modificarlo. | Moneda | Moneda | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Fuente de Financiamiento | Permite seleccionar la fuente de financiamiento. Según catálogo "Fuentes de Financiamiento" (CU-PRE-17 "Presupuesto de inversión"). | Selección | Selección | Sí (texto explícito: "Campo obligatorio.") | "Seleccione" (al registrar por primera vez, RN-B.a). | Editable: No (según Anexo B.1). |
| Fuente de Recursos | Permite seleccionar la fuente de recursos. Según catálogo "Fuentes de Recursos" (CU-PRE-17 "Presupuesto de inversión"). | Selección | Selección | Sí (texto explícito: "Campo obligatorio"). | "Seleccione" (al registrar por primera vez, RN-B.a). | Editable: No (según Anexo B.1). |
| Convenio | Permite seleccionar el convenio. Según catálogo "Convenios". Podrá seleccionarse más de un convenio para cada fuente de recursos (CU-PRE-17 "Presupuesto de inversión"). | Selección | Selección | Sí (texto explícito: "Campo obligatorio"). | "Seleccione" (al registrar por primera vez, RN-B.a). | Editable: No (según Anexo B.1). |
| I Cuatrimestre, II Cuatrimestre y III Cuatrimestre (Sección "Programación Cuatrimestral") | Registro manual del monto en US$ a programar para cada Cuatrimestre. El sistema deberá agregar el separador de miles (,). Deberá registrarse en al menos un cuatrimestre. | Moneda | Moneda | Sí (texto explícito: "Campo obligatorio."; adicionalmente: "Deberá registrarse en al menos un cuatrimestre"). | $0.00 (al registrar por primera vez, RN-B.a). | Editable: Sí (según Anexo B.1). |
| Total programado año | Suma de los montos programados para cada cuatrimestre. Debe cumplir lo establecido en RN B literal b) según corresponda. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad/posible error: el propio contenido descrito coincide con las condiciones de la RN-B literal c) (condiciones del "Total programado Año"), no con la RN-B literal b) (orden de las etapas) — ver Observaciones. |
| Porcentaje | Resultado de dividir el campo Monto Programado del Cuatrimestre (Cuatrimestre I hasta el Cuatrimestre III) entre el campo Total Programado Año, multiplicado por 100. Fórmula (RN-B.d): `% Cuatrimestre = (Monto Programado del cuatrimestre / Total Programado Año) * 100`. La suma de los porcentajes siempre deberá ser igual al 100% (RN-B.d). | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Campo calculado; fórmula transcrita completa en la Descripción. |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|---|---|---|
| Total programado Año (Anexo A.2, estudios nuevos) | No deberá ser mayor al valor del campo "Costo de la etapa" para el estudio y la Fuente de Financiamiento correspondientes (RN-B c.1). | "Monto Programado supera el costo de la etapa" (Anexo A.3). |
| Total programado Año (Anexo A.2, estudios de arrastre) | No deberá ser superior al monto pendiente de ejecutar del estudio para la Fuente de Financiamiento correspondiente (Costo de la etapa − Ejecutado años anteriores) (RN-B c.2). | "Monto Programado supera el costo de la etapa" (Anexo A.3). |
| Etapa (orden de registro en el Anexo A.2) | La programación de las etapas debe realizarse en el orden lógico del ciclo de la preinversión, según la Ruta de Preinversión (RN-B b). | "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda" (Anexo A.7). |
| Ingreso/ajuste de datos por el Técnico URP (fuera de fecha del Calendario de Eventos del PAP) | El sistema no debe permitir ajustes o ingreso de datos fuera de la fecha establecida en el Calendario de Eventos del PAP (RN-A b). | "Periodo de ingreso de información ha finalizado" (Anexo A.4). |
| Eliminación de etapa y/o Fuente de Financiamiento | No se debe permitir eliminar una etapa y/o fuente de financiamiento que tenga ejecución de años anteriores relacionada para ese CUP en esa etapa (RN-C). | "Etapa y/o Fuente de Financiamiento no puede ser eliminado, existe ejecución en años anteriores" (Anexo A.6; nota de ambigüedad de redacción — ver Observaciones). |
| Eliminación de código de estudio de arrastre | No podrá eliminarse un estudio de arrastre que no haya cumplido el 100% de lo programado física o financieramente en periodos anteriores (RN-D). | No especificado en el documento (RN-D no transcribe un mensaje literal para este caso específico, solo para la pregunta de confirmación de desactivación). |
| Etapa (Anexo A.2, SF-2 paso 5) | Será obligatorio registrar la programación de por lo menos una de las etapas (RN-B b). | No especificado en el documento (mensaje literal no transcrito para este caso). |
| I/II/III Cuatrimestre (Anexo A.2) | Deberá registrarse en al menos un cuatrimestre (Anexo B.1). | No especificado en el documento (mensaje literal no transcrito para este caso). |

---

# Errores

| Código | Descripción | Acción esperada |
|---|---|---|
| No especificado en el documento (sin código formal asociado). | "Monto Programado supera el costo de la etapa" (Anexo A.3). | El registro se mantiene en la pantalla del Anexo A.2; el usuario debe ajustar el monto programado (RN-B c.1, c.2). |
| No especificado en el documento. | "Periodo de ingreso de información ha finalizado" (Anexo A.4). | Todas las acciones de la tabla Programación Financiera Cuatrimestral del PAP permanecen deshabilitadas (RN-A b). |
| No especificado en el documento. | "Etapa y/o Fuente de Financiamiento no puede ser eliminado, existe ejecución en años anteriores" / mockup: "Financiamiento no puede ser eliminado, existe ejecución en años anteriores para esta Etapa" (Anexo A.6; ver Observaciones sobre la discrepancia de redacción). | No se permite la eliminación de la etapa y/o fuente de financiamiento (RN-C). |
| No especificado en el documento. | "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda" (Anexo A.7). | El usuario debe revisar y ajustar la Ruta de Preinversión en CU-PRE-03.5, o registrar las etapas en el orden correspondiente (RN-B b). |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Técnico URP | Responsable del ingreso de la información; puede registrar la programación de los estudios de arrastre y agregar códigos nuevos con sus programaciones desde el botón "Agregar estudio" | RN-A.c |
| Técnico URP | Tiene habilitado el botón "Generar reporte" | RN-A.c; RN-E |
| Técnico URP | Tiene habilitado el botón "Programación de Metas" (SF-6) | RN-A.c |
| Técnico URP | Único actor con los botones del Anexo A.2 visibles y habilitados, durante el período de ingreso de información o modificaciones al PAP | RN-E |
| Técnico PRE | Encargado de revisar la información; puede seleccionar cualquier Unidad Ejecutora mediante lista desplegable para análisis y revisión individual | RN-A.a |
| Técnico PRE | Tiene habilitado el botón "Generar reporte" | RN-A.c; RN-E |
| Técnico PRE | Tiene habilitado el botón "Programación de Metas" (SF-6); nota de ambigüedad: SF-6 solo describe la acción para el Técnico URP — ver Observaciones | RN-A.c |
| Coordinador PRE, Coordinador PRO, Técnico PRO, Jefe DGI, Subjefe DGI | Solo derecho de consultar la información ingresada por el Técnico URP y el Técnico PRE; no verán ningún ícono en el campo de acciones de la tabla, salvo los botones "Generar reporte" y "Programación de Metas" | RN-A.c |
| Todos los actores | Botón "BUSCAR": visible y habilitado | RN-E |
| Todos los actores | Botón "GENERAR REPORTE": visible y habilitado (la mención del Anexo A.8 a una condición previa "Enviar a revisión DGI" no corresponde a un flujo propio de este documento — ver Observaciones numerales 5 y 13, y Datos Pendientes de Definir) | RN-E |
| Coordinador PRE | Solicita al Administrador del Sistema la habilitación de modificaciones al PAP fuera del período de elaboración, mediante nota de solicitud remitida por la Institución | SF-4 paso 1; SF-5 paso 1 |
| Administrador del Sistema | Habilita el Sistema para permitir modificaciones al PAP fuera del período de elaboración, previa solicitud del Coordinador PRE | SF-4 paso 1; SF-5 paso 1 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-01 Registro de Proyectos (precondición; fuente de los campos "CUP" y "Nombre del proyecto")
- CU-PRE-03.5 Selección y registro de etapas (precondición; fuente de los campos "Etapa" y "Costo de la etapa" para estudios nuevos)
- CU-PRE-17 "Presupuesto de inversión" (precondición; fuente de los catálogos "Fuentes de Financiamiento", "Fuentes de Recursos" y "Convenios")
- CU-PRE-22.1 Programación financiera de la preinversión del Proyecto (precondición)
- CU-ADM XX "Gestión de Eventos de Calendario" (referenciado en RN-A b; código con marcador de posición "XX" — ver Observaciones)
- CU-PRE-31 Programación Cuatrimestral de Metas Físicas de la Preinversión (postcondición; destino del SF-6; **también es el caso de uso donde se ejecuta la única solicitud de aprobación del Técnico URP y la única aprobación del Técnico PRE que cubren ambos casos de uso — decisión funcional v1.2, ver Observaciones numeral 13**)
- CU-PRO-25 Monitoreo Programación PAP (nombre oficial, RQ-C-07; el documento original citaba "Monitoreo de la programación del PAP") (postcondición)
- CU-PRO-01 Elaboración/Actualización del Programa de Inversión Pública de Mediano Plazo - PRIPME (postcondición)

**Procesos relacionados:**
> No especificado en el documento (más allá de los casos de uso listados).

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Anexo A.1 — Programación Financiera Cuatrimestral del PAP

- **Nombre:** Programación Financiera Cuatrimestral del PAP.
- **Descripción:** Pantalla principal que muestra el listado de estudios/proyectos con su programación financiera cuatrimestral, agrupados por Etapa y Fuente de Financiamiento, con un filtro de Unidad Ejecutora y Año.
- **Campos:** Unidad Ejecutora (selector), Año (selector), y en la tabla: CUP, Nombre del proyecto, Etapa, Fuente de Financ., Costo de la etapa, Ejecutado años anteriores, Programación (I Cuatrimestre, II Cuatrimestre, III Cuatrimestre), Total Año, Años posteriores.
- **Botones:** "Buscar", "Agregar estudio" (da acceso al SF-2), "Generar Reporte" (da acceso al SF-3), "Siguiente" (da acceso al SF-6).
- **Acciones:** Seleccionar Unidad Ejecutora y Año; buscar; dar clic en el CUP de un estudio para registrar/modificar su programación (SF-1 o SF-5); agregar un nuevo estudio (SF-2); generar reporte (SF-3); avanzar a la Programación de Metas (SF-6).

### Ejemplo de datos mostrados en el mockup (Anexo A.1)

**PROGRAMACIÓN FINANCIERA DE LA PREINVERSIÓN PÚBLICA, 2025**

UNIDAD EJECUTORA: MINSAL | AÑO: 2025 | Botón: Buscar

| CUP | NOMBRE DEL PROYECTO | ETAPA | FUENTE DE FINANC. | COSTO DE LA ETAPA | EJECUTADO AÑOS ANTERIORES | I CUATRIMESTRE | II CUATRIMESTRE | III CUATRIMESTRE | TOTAL AÑO | AÑOS POSTERIORES |
|---|---|---|---|---|---|---|---|---|---|---|
| 8040 | Construcción de Unidades de Salud | | | 850,000.00 | 525,000.00 | 65,000.00 | 110,000.00 | 100,000.00 | 275,000.00 | 50,000.00 |
| | | Diseño | | 800,000.00 | 500,000.00 | 50,000.00 | 100,000.00 | 100,000.00 | 250,000.00 | 50,000.00 |
| | | | FOSEP | 800,000.00 | 500,000.00 | 50,000.00 | 100,000.00 | 100,000.00 | 250,000.00 | 50,000.00 |
| | | | FGEN | 50,000.00 | 25,000.00 | 15,000.00 | 10,000.00 | 0.00 | 25,000.00 | 0.00 |
| 8140 | Equipamiento de hospitales a nivel nacional | | | 350,000.00 | 0.00 | 0.00 | 75,000.00 | 125,000.00 | 200,000.00 | 150,000.00 |
| | | Factibilidad | | 350,000.00 | 0.00 | 0.00 | 75,000.00 | 125,000.00 | 200,000.00 | 150,000.00 |
| | | | FOSEP | 350,000.00 | 0.00 | 0.00 | 75,000.00 | 125,000.00 | 200,000.00 | 150,000.00 |
| 8142 | Capacitación... | | | 2,500.00 | 0.00 | 2,500.00 | 0.00 | 0.00 | 2,500.00 | 0.00 |
| | | Perfil | | 2,500.00 | 0.00 | 2,500.00 | 0.00 | 0.00 | 2,500.00 | 0.00 |
| | | | FGEN | 2,500.00 | 0.00 | 2,500.00 | 0.00 | 0.00 | 2,500.00 | 0.00 |
| 8150 | Construcción... | | | 450,000.00 | 100,000.00 | 125,000.00 | 100,000.00 | 125,000.00 | 350,000.00 | 0.00 |
| | | Factibilidad | | 450,000.00 | 100,000.00 | 125,000.00 | 100,000.00 | 125,000.00 | 350,000.00 | 0.00 |
| | | | FOSEP | 450,000.00 | 100,000.00 | 125,000.00 | 100,000.00 | 125,000.00 | 350,000.00 | 0.00 |
| 8280 | Construcción... | | | 1,502,500.00 | 0.00 | 0.00 | 2,500.00 | 200,000.00 | 202,500.00 | 1,300,000.00 |
| | | Perfil | | 2,500.00 | 0.00 | 0.00 | 2,500.00 | 0.00 | 2,500.00 | 0.00 |
| | | | FGEN | 2,500.00 | 0.00 | 0.00 | 2,500.00 | 0.00 | 2,500.00 | 0.00 |
| | | Diseño | | 1,500,000.00 | 0.00 | 0.00 | 0.00 | 200,000.00 | 200,000.00 | 1,300,000.00 |
| | | | Préstamo Externo / BCIE / BCIE 2230 | 1,500,000.00 | 0.00 | 0.00 | 0.00 | 200,000.00 | 200,000.00 | 1,300,000.00 |
| **TOTAL** | | | | **3,155,000.00** | **625,000.00** | **192,500.00** | **287,500.00** | **550,000.00** | **1,030,000.00** | **1,500,000.00** |

Botones mostrados: "Agregar estudio" (inferior izquierda), "Generar Reporte" (inferior derecha), "Siguiente" (inferior derecha, debajo de "Generar Reporte").

> **Verificación (v1.1):** el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hoja "CUPRE-30 A.1") contiene este mismo mockup en formato nativo/editable. Se comparó celda por celda contra la tabla anterior y coincide exactamente: mismo título, mismo filtro "UNIDAD EJECUTORA: MINSAL" / "AÑO: 2025", los mismos 5 estudios (CUP 8040, 8140, 8142, 8150, 8280) con sus mismas etapas, fuentes de financiamiento, montos por cuatrimestre y fila "TOTAL". No se detectaron discrepancias.

## Anexo A.2 — Programación Financiera por Etapa de Preinversión

- **Nombre:** Programación de Financiamiento por Etapa de Preinversión.
- **Descripción:** Pantalla de registro/edición de la programación cuatrimestral de un estudio, mostrando una columna por cada Etapa de la ruta de preinversión que se esté programando, con su Fuente de Financiamiento, Fuente de Recursos, Convenio y los montos por cuatrimestre.
- **Campos:** CUP, Nombre del proyecto, Etapa, Costo de la etapa, Fuente de Financiamiento, Fuente de Recursos, Convenio, y en la sección "Programación Cuatrimestral": Periodo (I, II, III Cuatrimestre), Monto (US$), Porcentaje, Total programado Año.
- **Botones:** "Aceptar" (al registrar el código de un estudio nuevo), botón "(+)" junto al campo "Fuente de Financiamiento" para agregar otra fuente, "Guardar" (uno por cada columna/etapa mostrada), "Salir".
- **Acciones:** Registrar o consultar el CUP; seleccionar Fuente de Financiamiento, Fuente de Recursos y Convenio; registrar montos por cuatrimestre; agregar una fuente de financiamiento adicional; guardar o salir sin guardar.

### Ejemplo de datos mostrados en el mockup (Anexo A.2)

**Programación de Financiamiento por Etapa de Preinversión**

CUP: 8280 [Botón: Aceptar]
Nombre del proyecto: Construcción...

| | Columna izquierda (Etapa: Perfil) | Columna derecha (Etapa: Diseño) |
|---|---|---|
| Etapa | Perfil | Diseño |
| Costo de la etapa | $ 2,500.00 | $ 200,000.00 |
| Fuente de Financiamiento | FGEN [botón (+)] | Préstamo Externo [botón (+)] |
| Fuente de Recursos | Fondo General | BCIE |
| Convenio | N/A | BCIE 2230 |

**Programación Cuatrimestral (columna izquierda — Etapa Perfil):**

| Periodo | Monto (US$) | Porcentaje |
|---|---|---|
| I Cuatrimestre | 0.00 | 0.00% |
| II Cuatrimestre | 2,500.00 | 100.00% |
| III Cuatrimestre | 0.00 | 0.00% |
| **Total programado Año** | **2,500.00** | **100.00%** |

[Botón: Guardar]

**Programación Cuatrimestral (columna derecha — Etapa Diseño):**

| Periodo | Monto (US$) | Porcentaje |
|---|---|---|
| I Cuatrimestre | 0.00 | 0.00% |
| II Cuatrimestre | 0.00 | 0.00% |
| III Cuatrimestre | 200,000.00 | 100.00% |
| **Total programado** | **200,000.00** | **100.00%** |

[Botón: Guardar]

[Botón: Salir] (común a ambas columnas)

> Nota: el texto del documento aclara que "Esta pantalla permitirá agregar más de una fuente de financiamiento, una fuente de recursos y un convenio en casos que sea necesario; para poder realizarlo, se contará con un botón (+) al lado del campo 'Fuente de Financiamiento', los campos para el registro de la nueva Fuente de Financiamiento, así como para la Programación Cuatrimestral de la misma, deberán mostrarse debajo de los registros de la primera fuente."

> **Verificación (v1.1):** el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hoja "CUPRE-30 A.2") contiene este mismo mockup en formato nativo/editable, para el mismo estudio de ejemplo (CUP 8280). Se comparó celda por celda y coincide exactamente: mismas dos columnas de etapa (Perfil/Diseño), mismos costos ($2,500.00 / $200,000.00), mismas fuentes de financiamiento/recursos/convenio (FGEN-Fondo General-N/A / Préstamo Externo-BCIE-BCIE 2230), y misma programación cuatrimestral y porcentajes. La hoja Excel también incluye, en una zona separada de la hoja, el mismo texto de error "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda" ya transcrito en este documento como Anexo A.7. No se detectaron discrepancias.

## Anexo A.3 — Monto Programado supera el Costo de la Etapa

- **Nombre:** Monto Programado supera el Costo de la Etapa.
- **Descripción:** Ventana emergente de error mostrada al intentar guardar una programación cuya suma de cuatrimestres supera el costo de la etapa o el monto pendiente de ejecutar.
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "Aceptar".
- **Acciones:** Aceptar el mensaje; el sistema mantiene al usuario en la pantalla del Anexo A.2 (RN-B c.1, c.2).

### Ejemplo de datos mostrados en el mockup (Anexo A.3)

Ícono de error (X roja). Título: "Error". Texto: "Monto Programado supera el Costo de la Etapa". Botón: "Aceptar".

## Anexo A.4 — Periodo de ingreso de información ha finalizado

- **Nombre:** Periodo de ingreso de información ha finalizado.
- **Descripción:** Ventana emergente de error mostrada cuando el Técnico URP intenta ajustar o ingresar datos fuera del período establecido en el Calendario de Eventos del PAP (RN-A b).
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "Aceptar".
- **Acciones:** Aceptar el mensaje.

### Ejemplo de datos mostrados en el mockup (Anexo A.4)

Ícono de error (X roja). Título: "Error". Texto: "Período de ingreso de información ha finalizado". Botón: "Aceptar".

## Anexo A.5 — Eliminar Financiamiento

- **Nombre:** Eliminar Financiamiento.
- **Descripción:** Ventana emergente de confirmación mostrada al intentar eliminar una fuente de financiamiento (RN-C).
- **Campos:** No aplica (mensaje de confirmación).
- **Botones:** "No", "Sí".
- **Acciones:** Confirmar o cancelar la eliminación.

### Ejemplo de datos mostrados en el mockup (Anexo A.5)

Ícono de advertencia (signo de exclamación amarillo). Título: "¿Está seguro?". Texto: "¡Una vez eliminado ya no estará disponible!". Botones: "No" / "Sí".

> Nota de ambigüedad: este mockup está titulado únicamente "Eliminar Financiamiento"; la RN-C describe también una pregunta equivalente para "eliminar la etapa seleccionada", sin un mockup propio — ver Observaciones.

## Anexo A.6 — No puede Eliminar Etapa/Financiamiento

- **Nombre:** No puede Eliminar Etapa/Financiamiento.
- **Descripción:** Ventana emergente de error mostrada cuando se intenta eliminar una etapa y/o fuente de financiamiento que tiene ejecución de años anteriores relacionada (RN-C).
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "Aceptar" (uno por cada recuadro mostrado).
- **Acciones:** Aceptar el mensaje; no se permite la eliminación.

### Ejemplo de datos mostrados en el mockup (Anexo A.6)

El mockup muestra dos recuadros idénticos, lado a lado, cada uno con: ícono de error (X roja), título "Error", texto "Financiamiento no puede ser eliminado, existe ejecución en años anteriores para esta Etapa", y botón "Aceptar".

> Nota de ambigüedad: el texto mostrado en el mockup ("Financiamiento no puede ser eliminado, existe ejecución en años anteriores para esta Etapa") no coincide textualmente con el mensaje descrito en la RN-C ("Etapa y/o Fuente de Financiamiento no puede ser eliminado, existe ejecución en años anteriores"), y ambos recuadros del mockup muestran el mismo texto sin distinguir si corresponden al caso de "etapa" o de "financiamiento" — ver Observaciones.

## Anexo A.7 — No puede saltarse la Ruta de Preinversión

- **Nombre:** No puede saltarse la Ruta de Preinversión.
- **Descripción:** Ventana emergente de error mostrada cuando el usuario intenta registrar una etapa fuera del orden lógico de la Ruta de Preinversión (RN-B b).
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "ACEPTAR".
- **Acciones:** Aceptar el mensaje; el usuario debe revisar y ajustar según corresponda (RN-B b).

### Ejemplo de datos mostrados en el mockup (Anexo A.7)

Ícono de error (X roja). Título: "Error". Texto: "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda". Botón: "ACEPTAR".

## Anexo A.8 — Reporte de la Programación Financiera de la Preinversión

- **Nombre:** Reporte de la Programación Financiera de la Preinversión.
- **Descripción:** Reporte exportable (Excel o PDF) que corresponde a la pantalla del Anexo A.1, sin los botones, generado mediante el SF-3.
- **Campos:** Institución Ejecutora (ver Observaciones sobre esta etiqueta), Año, la misma tabla de datos del Anexo A.1, y un campo adicional "Comentarios al reporte financiero DGICP" (visible solo para reportes generados por actores internos de la DGICP).
- **Botones:** No aplica según el documento ("El reporte de la Programación Financiera corresponde a la pantalla del Anexo A.1, sin los botones").
- **Acciones:** Generar el reporte en Excel o PDF (SF-3); consultar/imprimir el reporte; registrar comentarios (solo actores internos DGICP).

### Ejemplo de datos mostrados en el mockup (Anexo A.8)

**PROGRAMACIÓN FINANCIERA DE LA PREINVERSIÓN PÚBLICA, 2025**

INSTITUCIÓN EJECUTORA: MINSAL | AÑO: 2025

(La tabla de datos es idéntica a la transcrita para el Anexo A.1, incluyendo la fila "TOTAL".)

Campo de texto: "Comentarios al reporte financiero DGICP:"

Texto explicativo del documento: "El reporte de la Programación Financiera corresponde a la pantalla del Anexo A.1, sin los botones. Dicho reporte podrá generarse una vez que el Técnico URP haya dado clic en el botón 'Enviar a revisión DGI'. El campo 'comentarios al reporte financiero DGICP' solo será visible para los reportes generados por actores internos (DGICP)."

> Nota de ambigüedad (texto original del PDF, no modificado): el botón "Enviar a revisión DGI" mencionado en este texto no está descrito en ningún Flujo Básico, Subflujo, Regla de Negocio, ni aparece en el mockup del Anexo A.1, y contradice a la RN-E (que declara "GENERAR REPORTE" visible y habilitado para todos los actores sin condición previa) — ver Observaciones y Datos Pendientes de Definir.
>
> ✅ Resolución funcional (decisión del usuario, no proveniente del PDF — v1.2): se aclara que CU-PRE-30 no tiene, ni tendrá, un flujo propio de "solicitud de aprobación"/"envío a revisión" independiente. La única solicitud de aprobación existe de forma unificada en CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión" (SF-2, paso 7 "Enviar a revisión DGICP"), y la única aprobación ("Revisión Finalizada") también se ejecuta allí (SF-6), cubriendo ambas —según el propio texto de CU-PRE-31— "tanto financiera como de metas físicas". El Técnico URP realiza así una sola solicitud, y el Técnico PRE una sola aprobación, para ambos casos de uso. Esta referencia al botón "Enviar a revisión DGI" en el Anexo A.8 de este documento no debe interpretarse como una segunda solicitud independiente de este caso de uso.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|---|---|---|
| Error | "Monto Programado supera el Costo de la Etapa" (Anexo A.3) | Al dar clic en "Guardar" cuando la suma de los cuatrimestres supera el "Costo de la etapa" (estudios nuevos, RN-B c.1) o el monto pendiente de ejecutar (estudios de arrastre, RN-B c.2). |
| Error | "Período de ingreso de información ha finalizado" (Anexo A.4) | Cuando el Técnico URP intenta ajustar o ingresar datos fuera de la fecha establecida en el Calendario de Eventos del PAP (RN-A b). |
| Confirmación | "¿Está seguro? ¡Una vez eliminado ya no estará disponible!" (Anexo A.5) | Al intentar eliminar una fuente de financiamiento (RN-C). |
| Confirmación (sin mockup propio) | "¿Está seguro de eliminar la etapa seleccionada?" | Al intentar eliminar una etapa (RN-C; ver Observaciones sobre la ausencia de mockup específico). |
| Error | "Financiamiento no puede ser eliminado, existe ejecución en años anteriores para esta Etapa" (Anexo A.6) / "Etapa y/o Fuente de Financiamiento no puede ser eliminado, existe ejecución en años anteriores" (RN-C) | Al intentar eliminar una etapa y/o fuente de financiamiento con ejecución de años anteriores relacionada (RN-C; ver Observaciones sobre la discrepancia de redacción). |
| Confirmación (sin mockup propio) | "¿Está seguro de desactivar el proyecto con código XXX?" | Al intentar eliminar/desactivar un estudio no iniciado dentro del período de elaboración del PAP (RN-D; ver Observaciones sobre la ausencia de un número de Anexo asociado). |
| Error | "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda" (Anexo A.7) | Al intentar registrar una etapa fuera del orden lógico de la Ruta de Preinversión (RN-B b). |

---

# Observaciones

1. El campo "Costo de la etapa" en la tabla de Formatos del Anexo B.1 para la pantalla del Anexo A.1 se marca con "Editable: No", pero su propia columna "Detalle" indica explícitamente: "El campo será editable durante el período de elaboración del PAP, tanto para estudios nuevos como para estudios de arrastre." Esta contradicción interna no está resuelta en el documento.

2. El campo "Etapa" tiene un tipo de dato distinto según la pantalla: en la tabla de Formatos de la pantalla del Anexo A.1 se define como "Selección"/"Selección", mientras que en la tabla de Formatos de la pantalla del Anexo A.2 se define como "Texto"/"Texto". El documento no aclara si se trata de campos distintos o de una inconsistencia de tipificación para el mismo dato.

3. El campo "Total programado año" (Anexo A.2, Anexo B.1) indica en su Detalle: "Debe cumplir lo establecido en RN B literal b) según corresponda", pero el contenido descrito (condiciones sobre el monto total programado frente al costo de la etapa) coincide con lo establecido en la RN-B literal c), no con la RN-B literal b) (que trata sobre el orden de las etapas en la ruta de preinversión). El documento no aclara si se trata de un error de referencia cruzada.

4. El mockup del Anexo A.8 (Reporte) utiliza la etiqueta "INSTITUCIÓN EJECUTORA", mientras que el mockup del Anexo A.1 y la tabla de Formatos del Anexo B.1 utilizan consistentemente la etiqueta "Unidad Ejecutora" para el mismo campo. El documento no aclara si "Institución Ejecutora" y "Unidad Ejecutora" son equivalentes.

5. El texto de la sección A.8 indica que el reporte "podrá generarse una vez que el Técnico URP haya dado clic en el botón 'Enviar a revisión DGI'"; sin embargo, ese botón no aparece descrito en ningún Flujo Básico, Subflujo, ni en el mockup del Anexo A.1, y contradice la RN-E, que establece que "GENERAR REPORTE" estará "Visible y habilitado para todos los actores" sin mencionar dicha condición previa. **✅ Resolución funcional (decisión del usuario, v1.2, no proveniente del PDF):** se aclaró que esta mención no representa un flujo de solicitud/aprobación propio de CU-PRE-30; la única solicitud de aprobación (Técnico URP) y la única aprobación (Técnico PRE) del PAP —que cubren tanto la programación financiera como la de metas físicas— se ejecutan de forma unificada en CU-PRE-31 (SF-2 paso 7 "Enviar a revisión DGICP" y SF-6 "Finalizar Revisión"). La contradicción original entre este texto del Anexo A.8 y la RN-E de este documento se mantiene señalada como tal (no se corrige la redacción literal del PDF), pero se documenta explícitamente que no debe generarse una segunda solicitud/aprobación exclusiva de CU-PRE-30.

6. El mensaje de error mostrado en el mockup del Anexo A.6 ("Financiamiento no puede ser eliminado, existe ejecución en años anteriores para esta Etapa") no coincide textualmente con el mensaje descrito en la RN-C ("Etapa y/o Fuente de Financiamiento no puede ser eliminado, existe ejecución en años anteriores"). El documento no aclara cuál es el texto definitivo.

7. La RN-C describe dos preguntas de confirmación distintas ("¿Está seguro de eliminar la fuente de financiamiento seleccionada?" y "¿Está seguro de eliminar la etapa seleccionada?"), pero el único mockup asociado (Anexo A.5, titulado "Eliminar Financiamiento") solo muestra el texto genérico "¿Está seguro? ¡Una vez eliminado ya no estará disponible!", sin distinguir entre ambos casos ni presentar un mockup específico para la eliminación de una etapa.

8. El actor "Administrador del Sistema", que ejecuta el primer paso de los Subflujos SF-4 y SF-5, no aparece en la lista de "Actores" de la sección 1 del documento.

9. El Flujo Básico (paso 3) no incluye el SF-6 "Botón Programación de Metas" entre los subflujos que el actor puede seleccionar, aunque dicho subflujo está descrito en la sección 2 y referenciado en la RN-A c) (botón "Programación de Metas").

10. El mockup del Anexo A.2 muestra un botón "Guardar" independiente para cada etapa/columna mostrada (por ejemplo, "Perfil" y "Diseño" del proyecto 8280), mientras que los Subflujos SF-1, SF-2 y SF-5 describen la selección de "Guardar" o "Salir" como una única acción (un solo paso numerado), sin aclarar si el guardado es independiente por etapa o global para todas las etapas mostradas en pantalla.

11. **Resuelto (RQ-C-07, ronda 3):** las tres menciones a CU-PRO-25 en este documento (`casos_relacionados`, Postcondición 2, y "Dependencias") usaban la redacción "Monitoreo de la programación del PAP", que no era el nombre oficial. Se actualizaron a "Monitoreo Programación PAP", confirmado por el negocio. **Nota de verificación:** la resolución de negocio que resolvió RQ-C-07 describe que CU-PRE-30 citaba a CU-PRO-25 con **tres** redacciones distintas, incluyendo una que coincidía por error con el título literal de CU-EJE-10 ("Monitoreo del Avance Cuatrimestral del PAP") y otra ("Monitoreo del Programa Anual de Preinversión – PAP", citada en RN-D). En esta versión del documento que se corrigió, sin embargo, solo se encontró **una** redacción, repetida de forma consistente en las tres menciones — no se localizó ninguna de esas otras dos variantes, ni en RN-D ni en ningún otro punto del documento. Es posible que la observación original se refiriera a una versión distinta de este archivo. Se deja esta nota para que el Analista de Consistencia confirme si existe otra versión de CU-PRE-30 con esas variantes adicionales, o si la observación original tenía una imprecisión de referencia.

11. El campo "CUP" en la tabla de Formatos de la pantalla del Anexo A.1 no incluye la anotación explícita "Campo obligatorio" en su Detalle, a diferencia del campo "CUP" de la pantalla del Anexo A.2, que sí la incluye.

12. La precondición "Calendario de eventos para programación, seguimiento y ejecución cuatrimestral del PAP" (sección 4) no incluye un código de caso de uso propio, mientras que en la RN-A b) el mismo concepto se referencia como "Caso de Uso CU-ADM XX 'Gestión de Eventos de Calendario'", cuyo código contiene un marcador de posición ("XX") en lugar de un número definitivo.

13. **Decisión funcional (v1.2, a solicitud del usuario, 31/08/2026 — no proveniente del PDF):** se confirma y documenta que CU-PRE-30 y CU-PRE-31 comparten una única solicitud de aprobación (Técnico URP → Técnico PRE) y una única aprobación ("Revisión Finalizada", Técnico PRE), ambas ejecutadas en CU-PRE-31 (SF-2 paso 7; SF-6), sin que exista una solicitud/aprobación adicional o independiente propia de CU-PRE-30. Esta decisión resuelve la ambigüedad señalada en el numeral 5 de estas Observaciones respecto al botón "Enviar a revisión DGI" del Anexo A.8. Ver también `nota_cambio_v1_2` en el Front Matter y la nota equivalente en CU-PRE-31.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---|---|---|
| Proyecto/Estudio | Entidad principal identificada por su CUP, con Nombre, y una o más Etapas de Preinversión. | Registro de un nuevo estudio mediante el botón "Agregar estudio" (SF-2); consulta y registro de programación para estudios de arrastre (SF-1); modificación de la programación de un estudio existente fuera del período de elaboración (SF-5); desactivación de un estudio no iniciado (RN-D). |
| Etapa de Preinversión | Fase del ciclo de preinversión de un estudio (p. ej. Perfil, Prefactibilidad, Factibilidad, Diseño), con su Costo de la Etapa asociado. | Registro/selección de etapas a programar, en el orden lógico de la ruta de preinversión (SF-2 pasos 5-6; RN-B b); eliminación de una etapa sin ejecución de años anteriores (RN-C). |
| Fuente de Financiamiento / Fuente de Recursos / Convenio | Clasificación del origen de los recursos financieros de una etapa (catálogos remitidos a CU-PRE-17). | Selección (SF-2 paso 6; Anexo A.2); adición de una fuente de financiamiento adicional mediante el botón "(+)" (Anexo A.2); eliminación de una fuente de financiamiento sin ejecución de años anteriores (RN-C). |
| Programación Cuatrimestral | Registro de los montos programados por cuatrimestre (I, II, III) para una etapa y fuente de financiamiento determinadas. | Registro manual (SF-1 paso 3; SF-2 paso 6; Anexo B.1, Anexo A.2); cálculo automático de porcentajes por cuatrimestre (RN-B d); cálculo automático del "Total programado Año" y de "Años posteriores" (RN-B c; Anexo B.1, Anexo A.1); traslado de valores a la tabla del Anexo A.1 al guardar (SF-1 paso 5; SF-2 paso 10). |
| Reporte de Programación Financiera | Documento exportable con el detalle de la programación financiera de todos los estudios de una Unidad Ejecutora/Año. | Generación en formato Excel o PDF (SF-3; RN-E; Anexo A.8); registro de comentarios por actores internos de la DGICP (Anexo A.8). |
| Calendario de Eventos del PAP | Registro de fechas habilitadas para el ingreso/ajuste de información del PAP (referenciado como CU-ADM XX "Gestión de Eventos de Calendario"). | Consulta automática por el Sistema para habilitar o deshabilitar el ingreso de datos por parte del Técnico URP (RN-A b). |

---

# Catálogos Detectados

## Catálogo Fuentes de Financiamiento

> El documento indica que este campo se rige "Según catálogo 'Fuentes de Financiamiento' (CU-PRE-17 'Presupuesto de inversión')"; el catálogo completo no está incluido en este documento. A continuación se listan únicamente los valores observados en los mockups de ejemplo (Anexos A.1, A.2 y A.8), sin que esto constituya el catálogo completo:

| Fuente de Financiamiento (observada en los mockups) |
|---|
| FOSEP |
| FGEN |
| Préstamo Externo |

## Catálogo Fuentes de Recursos

> El documento indica que este campo se rige "Según catálogo 'Fuentes de Recursos' (CU-PRE-17 'Presupuesto de inversión')"; el catálogo completo no está incluido en este documento. A continuación se lista únicamente el valor observado en el mockup de ejemplo (Anexo A.2), sin que esto constituya el catálogo completo:

| Fuente de Recursos (observada en el mockup) |
|---|
| Fondo General |
| BCIE |

## Catálogo Convenios

> El documento indica que este campo se rige "Según catálogo 'Convenios' (CU-PRE-17 'Presupuesto de inversión')"; el catálogo completo no está incluido en este documento. A continuación se listan únicamente los valores observados en el mockup de ejemplo (Anexo A.2 y A.1), sin que esto constituya el catálogo completo:

| Convenio (observado en los mockups) |
|---|
| N/A |
| BCIE 2230 |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|---|---|---|
| Listado de estudios de arrastre con ejecución financiera/física pendiente | Sistema | Pantalla "Programación Financiera Cuatrimestral del PAP" (Anexo A.1) (FB paso 2) |
| Traslado de los valores registrados de cada cuatrimestre a los campos de la pantalla del Anexo A.1 | Sistema | Anexo A.1 (SF-1 paso 5; SF-2 paso 10, al Guardar) |
| Validación de la Ruta de Preinversión y de etapas finalizadas en períodos anteriores | Sistema | Anexo A.2 (SF-2 paso 4; RN-B b, RN-B e) |
| Cálculo automático de porcentajes por cuatrimestre | Sistema | Anexo A.2, campo "Porcentaje" (SF-2 paso 8; SF-5 paso 6; RN-B d) |
| Cálculo automático de "Años posteriores" | Sistema | Anexo A.1 y Anexo A.2 (RN-B c) |
| Generación del reporte de Programación Financiera | Sistema | Anexo A.8 (SF-3) |
| Habilitación del Sistema para modificaciones al PAP fuera del período de elaboración | Administrador del Sistema | Técnico URP (SF-4 paso 1; SF-5 paso 1) |
| Deshabilitación de todas las acciones de la tabla Programación Financiera Cuatrimestral del PAP | Sistema | Técnico URP (RN-A b, fuera de fecha del Calendario de Eventos del PAP) |

---

# Integraciones

> No especificado en el documento. El documento no describe integraciones con sistemas externos para este caso de uso; únicamente hace referencia a otros casos de uso internos del mismo sistema (SIIP) — ver sección "Dependencias".

---

# Datos Pendientes de Definir

1. **✅ Resuelto parcialmente (decisión funcional v1.2, a solicitud del usuario, no proveniente del PDF):** el botón "Enviar a revisión DGI" mencionado en el Anexo A.8 no está descrito en ningún Flujo Básico, Subflujo, ni Regla de Negocio de CU-PRE-30, ni aparece en el mockup del Anexo A.1. Se decidió que este documento no tendrá un flujo propio de solicitud/aprobación: la única solicitud (Técnico URP) y la única aprobación (Técnico PRE) del PAP se gestionan de forma unificada en CU-PRE-31 (SF-2 paso 7; SF-6), cubriendo tanto la programación financiera como la de metas físicas (ver Observaciones, numerales 5 y 13). **Pendiente aún:** el propio texto del PDF de este documento (Anexo A.8) sigue mencionando literalmente el botón "Enviar a revisión DGI" como condición para generar el reporte; no se ha verificado con el negocio si dicho texto del PDF debe corregirse/eliminarse en una futura actualización del documento fuente, ni se especifica la ubicación en pantalla o ícono exacto de ese botón si llegara a implementarse como una referencia visual al proceso unificado de CU-PRE-31.
2. El actor "Administrador del Sistema" (SF-4, SF-5) no forma parte de la lista de "Actores" de la sección 1 de la Identificación (ver Observaciones, numeral 8).
3. No se aclara si el Técnico PRE puede ejecutar el SF-6 "Botón Programación de Metas" en la práctica, dado que RN-A c) le habilita el botón "Programación de Metas", pero el subflujo SF-6 solo describe la acción para el Técnico URP.
4. El Flujo Básico (paso 3) no incluye el SF-6 entre las opciones de subflujo listadas, aunque el subflujo existe y está descrito en la sección 2 (ver Observaciones, numeral 9).
5. El código del Caso de Uso "CU-ADM XX 'Gestión de Eventos de Calendario'" referido en RN-A b) contiene un marcador de posición ("XX") en lugar de un número de caso de uso definitivo (ver Observaciones, numeral 12).
6. No se especifica un número de Anexo/mockup para el mensaje de confirmación de "eliminar la etapa seleccionada" (RN-C), a diferencia del mensaje de confirmación para eliminar una fuente de financiamiento, que sí tiene su mockup propio (Anexo A.5) (ver Observaciones, numeral 7).
7. No se especifica un número de Anexo/mockup para el mensaje de confirmación de desactivación de código descrito en RN-D ("¿Está seguro de desactivar el proyecto con código XXX?").
8. No se aclara si el botón "Guardar" mostrado por separado en cada columna/etapa del mockup del Anexo A.2 corresponde a un guardado independiente por etapa o si el paso "Selecciona uno de los siguientes subflujos: Guardar o Salir" es una única acción global para todas las etapas mostradas (ver Observaciones, numeral 10).
9. No se especifica si "Institución Ejecutora" (Anexo A.8) y "Unidad Ejecutora" (Anexo A.1, Anexo B.1) son el mismo campo (ver Observaciones, numeral 4).
10. No se aclara la referencia cruzada del campo "Total programado año" a "RN B literal b)", que aparentemente debería corresponder a la RN-B literal c) (ver Observaciones, numeral 3).
11. Prioridad (importancia) del propio caso de uso no especificada.
12. El documento no incluye una sección explícita de "Disparador" ni de "Excepciones" con Código/Descripción/Consecuencia.
13. **Verificación pendiente (no es una pregunta nueva al negocio):** confirmar si existe una versión de este documento distinta de la aquí corregida que contenga las otras dos variantes del nombre de CU-PRO-25 señaladas por la resolución de negocio RQ-C-07 ("Monitoreo del Avance Cuatrimestral del PAP" y "Monitoreo del Programa Anual de Preinversión – PAP"), ya que en esta versión solo se localizó una única redacción ("Monitoreo de la programación del PAP"), ya corregida al nombre oficial (ver Observaciones, numeral 11).
14. **Resuelto (RQ-C-01, Ronda 5) — no aplica:** el negocio confirmó que este documento no tiene campos de comentarios/observaciones, por lo que la pregunta sobre si el Coordinador PRE debería tener la capacidad de "Revisión Finalizada" no aplica aquí. El alcance del Coordinador PRE se mantiene sin cambios (consulta/solicitante, RN-A.c). La misma pregunta sí aplicó y se resolvió en CU-PRE-31 y CU-PRE-33 (ambos con campos de comentarios/observaciones). Ver nota en Actores Secundarios.