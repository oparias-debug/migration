---
id: CU-PRE-31
codigo: CU-PRE-31
nombre: Programación Cuatrimestral de Metas Físicas de la Preinversión
modulo: Preinversión
submodulo: Gestión del Proyecto
version: "1.2 (1.1 según el PDF/anexo original; ver nota_cambio_v1_2 — la versión 1.2 refleja una decisión funcional solicitada por el usuario, no una nueva versión del PDF fuente)"
fuente_pdf: CU-PRE-31_Programación_Cuatrimestral_Fisico_de_la_Preinversión_F.pdf
fuente_anexo_xlsx: "CU-PRE-30-33__ANEXO__Esquemas.xlsx (hojas 'CUPRE-31 A.1' y 'CUPRE-31 A.4'; el archivo cubre además los CU-PRE-30, 32 y 33 en hojas separadas, no analizadas en este documento)"
pagina_inicio: 1
pagina_fin: 15

nota_version: >
  La versión 1.1 incorpora el archivo Excel anexo
  `CU-PRE-30-33__ANEXO__Esquemas.xlsx`, aportado posteriormente y
  compartido entre varios casos de uso (CU-PRE-30 a 33). Para este
  documento se revisaron las hojas "CUPRE-31 A.1" y "CUPRE-31 A.4". Se
  detectaron y corrigieron dos vacíos: (1) la tabla del mockup del Anexo
  A.1 no incluía la columna "TOTAL META" presente en la hoja Excel
  (con valor "1" para los 4 registros de ejemplo); (2) el valor
  "Estudio de factibili..." de la columna "Entregable" en el mockup del
  Anexo A.4, truncado en el PDF, se completa ahora a "Estudio de
  factibilidad" a partir del valor íntegro de la hoja Excel, que además
  coincide con el valor ya documentado en el catálogo "Entregable"
  (Anexo D) de este mismo documento. No se modificó ningún otro
  contenido de la versión 1.0.

nota_cambio_v1_2: >
  Cambio funcional solicitado por el usuario (31/08/2026), NO proveniente del
  PDF fuente: se documenta explícitamente que el Subflujo SF-2 (paso 7,
  "Enviar a revisión DGICP") y el Subflujo SF-6 ("Finalizar Revisión")
  constituyen la única solicitud de aprobación (Técnico URP) y la única
  aprobación (Técnico PRE) del PAP Institucional, cubriendo de forma
  unificada tanto la programación financiera (CU-PRE-30 "Programación
  Cuatrimestral Financiera de la Preinversión") como la de metas físicas
  (este documento, CU-PRE-31) — consistente con el propio texto ya
  transcrito de SF-2 paso 8 ("la programación PAP, tanto financiera como de
  metas físicas, fue enviada para su revisión") y de SF-6/RN-D. CU-PRE-30
  no tiene ni tendrá un flujo propio de solicitud/aprobación independiente;
  su única mención relacionada (el botón "Enviar a revisión DGI" del
  Anexo A.8, no descrita en ningún flujo de ese documento) fue señalada y
  resuelta en la versión 1.2 de CU-PRE-30. No se elimina ni se reescribe
  ningún contenido literal del PDF; se agregan notas aclaratorias en las
  secciones correspondientes (ver "Flujos Alternos" SF-2/SF-6, "Reglas de
  Negocio" RN-D, y "Observaciones").

actor_principal: [Técnico URP,Técnico PRE]

actores_secundarios: [Coordinador PRE,Coordinador PRO,Técnico PRO,Jefe DGI,Subjefe DGI, Administrador del Sistema,Técnico SYMP,Coordinador SYMP]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-26"]

casos_relacionados: ["CU-PRO-25 Monitoreo Programación PAP", "CU-PRE-01 Registro de Proyectos"]

roles: ["Técnico URP", "Técnico PRE", "Coordinador PRE", "Coordinador PRO", "Técnico PRO", "Jefe DGI", "Subjefe DGI", "Administrador del Sistema", "Técnico SYMP", "Coordinador SYMP", "Sistema"]

pantallas: ["Anexo A.1 - Programación por Meta Física Cuatrimestral del PAP", "Anexo A.2 - Porcentaje Programado supera el 100% de la Etapa", "Anexo A.3 - Periodo de ingreso de información ha finalizado", "Anexo A.4 - Registro de la programación física por etapa de preinversión", "Anexo A.5 - Reporte de la Programación Cuatrimestral por Meta Física de la Preinversión"]

procesos: []

servicios_externos: ["Notificación por correo electrónico (Anexo C)"]

entidades: ["Proyecto/Estudio", "Etapa de Preinversión", "Meta Física", "Entregable", "Programación Cuatrimestral de Metas", "Observaciones DGICP / Respuesta Institución", "Reporte de Programación de Metas Físicas"]

catalogos: ["Catálogo 'Entregable' (Anexo D)"]

palabras_clave: ["programación cuatrimestral", "metas físicas", "PAP", "entregable", "meta total", "avance físico", "revisión DGICP", "año n+1"]

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
      pagina: 4
    SF3:
      pagina: 6
    SF4:
      pagina: 6
    SF5:
      pagina: 6
    SF6:
      pagina: 6
    SF7:
      pagina: 7
    SF8:
      pagina: 7
    SF9:
      pagina: 7
  reglas_negocio:
    RNA:
      pagina: 7
    RNB:
      pagina: 7
    RNC:
      pagina: 7
    RND:
      pagina: 8
    RNE:
      pagina: 8
  anexos:
    A1:
      nombre: "Programación por Meta Física Cuatrimestral del PAP"
      pagina: 9
    A2:
      nombre: "Porcentaje Programado supera el 100% de la Etapa"
      pagina: 10
    A3:
      nombre: "Periodo de ingreso de información ha finalizado"
      pagina: 10
    A4:
      nombre: "Registro de la programación física por etapa de preinversión"
      pagina: 11
    A5:
      nombre: "Reporte de la Programación Cuatrimestral por Meta Física de la Preinversión"
      pagina: 12
    B:
      nombre: "Requerimientos Funcionales - Formatos"
      pagina: 13
    C:
      nombre: "Contenido Correos Electrónicos"
      pagina: 15
    D:
      nombre: "Catálogo Entregable"
      pagina: 15
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original (aunque el pie de página del documento sí incluye numeración propia, que coincide con la aquí estimada)."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Programación Cuatrimestral de Metas Físicas de la Preinversión |
| Código | CU-PRE-31 |
| Módulo | Programación |
| Fuente | CU-PRE-31_Programación_Cuatrimestral_Fisico_de_la_Preinversión_F.pdf; complementado con el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hojas "CUPRE-31 A.1"/"CUPRE-31 A.4" — ver "Pantallas") |
| Versión | 1.2 (1.1 según el PDF/anexo original; la 1.2 refleja una decisión funcional del usuario, no una nueva versión del PDF — ver "Historial de Revisiones" y `nota_cambio_v1_2` en el Front Matter) |

**Campos requeridos (según el PDF):**
> No especificado en el documento. Este documento no incluye una sección explícita titulada "Campos requeridos" en la identificación del caso de uso. En su lugar, el documento incluye una sección "Ruta de Acceso", que se transcribe a continuación por su relevancia funcional:

**Ruta de Acceso (según el PDF):**
- Sistema de Información de Inversión Pública
- Programación
- PAP Institucional
- Meta Física

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

Este caso de uso es para programar en forma cuatrimestral el avance físico de cada uno de los estudios de proyectos para el año n+1 del Programa Anual de Preinversión (PAP) institucional.

Al Técnico de la Unidad Responsable de Proyectos (URP)-Técnico URP, le permite:
- Ingresar la programación de meta física cuatrimestral de los estudios para el año n+1.
- Consultar información de estudios programados en ejercicios anteriores.
- Responder a los ajustes solicitados por la DGICP a las programaciones financieras y de metas físicas.

Para el Técnico de Preinversión-Técnico PRE, este caso de uso permite:
- Revisar la información registrada por el Técnico URP.
- Digitar observaciones a la información programada en el año n+1 (tanto programación financiera como de metas físicas).
- Consultar información de estudios programados en ejercicios anteriores.
- Una vez finalizada la revisión, la programación (tanto financiera como de metas) será enviada a Monitoreo (CU-PRO-25 "Monitoreo Programación PAP" — nombre oficial, RQ-C-07).

Además, para este caso de uso se podrán asignar roles de consulta a actores internos y externos según credenciales. Asimismo, con este caso de uso, los usuarios podrán generar reportes para ambas programaciones en el formato que seleccione (Excel o PDF).

# Actor Principal

La sección "1. Actores" del documento lista conjuntamente:
- Técnico de Unidad Responsable de Proyectos (Técnico URP)
- Técnicos de Preinversión (Técnico PRE)
- Coordinador Área de Preinversión, Coordinador Área de Programación y Análisis de la Inversión (Coordinador PRE y Coordinador PRO)
- Técnico en Programación y Análisis de la Inversión (Técnico PRO)
- Jefe de la División de Gestión de la Inversión (Jefe DGI)
- Subjefe de la División de Gestión de la Inversión (Subjefe DGI)

> Nota de ambigüedad: el documento no distingue explícitamente un "actor principal"; la "Breve Descripción" y las Reglas de Negocio (RN-A c) atribuyen roles sustanciales y diferenciados únicamente al Técnico URP (ingreso de información) y al Técnico PRE (revisión de la información), mientras que el resto de actores tienen únicamente derecho de consulta. Ver Observaciones.

---

# Actores Secundarios

- Coordinador PRE (además, solicita al Administrador del Sistema la habilitación de modificaciones al PAP fuera de período — SF-8, SF-9; y según RN-A.c/RN-C.a/RN-E tiene habilitados, junto al Técnico PRE, la subsección "Revisión de la Programación Financiera y de Metas Físicas de la Preinversión" y el botón "Revisión Finalizada" — ✅ RESUELTO, RQ-C-01, Ronda 5)
- Coordinador PRO
- Técnico PRO
- Jefe DGI
- Subjefe DGI
- Administrador del Sistema (ejecuta el primer paso de los Subflujos SF-8 y SF-9; no está listado en la sección "1. Actores" del documento)
- Técnico SYMP y Coordinador SYMP (mencionados únicamente en la RN-C, como actores con acceso al campo "Comentarios al reporte DGICP"; no aparecen en la lista de "Actores" de la sección 1 ni coinciden con la nomenclatura del resto del documento — ver Observaciones)

---

# Disparador

> No especificado en el documento como un campo formal titulado "Disparador". El paso 1 del Flujo Básico describe la acción que da inicio al caso de uso: "Da clic en el botón Programación de Metas del CU-PRE-30 'Programación Cuatrimestral Financiera de la Preinversión'".

---

# Precondiciones

1. CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión"
2. Avance Cuatrimestral financiero del PAP (CU-PRE-32)
3. Calendario de eventos para programación, seguimiento y ejecución cuatrimestral del PAP. Caso de Uso CU-ADM-04 "Gestión de Eventos de Calendario"

> Nota de ambigüedad: el documento lista estos tres elementos bajo el encabezado "4. Precondiciones" sin un verbo explícito (p. ej. "contar con", "haber ejecutado") que aclare la redacción formal de la condición.

---

# Flujo Principal

## FB — Flujo Básico

1. Actor. Da clic en el botón Programación de Metas del CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión".
2. Sistema. Mostrará la pantalla del Anexo A.1 en la que ya estarán listados los CUP de los estudios que se programaron en el CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión". También ya se encontrarán cargados los campos "Nombre del proyecto" y "Etapa". Para los estudios de arrastre se mostrará la información completada en ejercicios anteriores de los campos "Meta", "Entregable" y "Ejecutado años anteriores".
3. Actor. Selecciona uno de los siguientes subflujos:
   - SF-1 Registrar Programación de metas físicas (para estudios de arrastre)
   - SF-2 Registrar Programación de metas físicas (para nuevos estudios)
   - SF-3 Revisión Programación de Metas Físicas del PAP
   - SF-4 Desactivar Código
   - SF-5 Eliminar Etapa
   - SF-6 Finalizar Revisión
   - SF-7 Generar Reporte
   - SF-8 Modificaciones al PAP Agregar un nuevo estudio (fuera del período de elaboración del PAP)
   - SF-9 Modificaciones al PAP Modificar la programación de un estudio existente (fuera del período de elaboración del PAP)

Caso de Uso Termina.

---

# Flujos Alternos

> El documento no utiliza la nomenclatura "Flujo Alternativo – FA" para este caso de uso, sino "Subflujos" (SF-1 a SF-9), presentados en la sección "2. Flujo de Eventos". Se transcriben a continuación conservando esa nomenclatura original.

## SF-1 — Registrar Programación de metas físicas (para estudios de arrastre)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP. Da clic en el CUP del proyecto para registrar la programación de metas.
2. Sistema. Despliega la pantalla (Anexo A.4) que contiene precargada la información del ejercicio anterior, de cada una de las etapas del proyecto con los siguientes campos: CUP, Etapa, Meta, Entregable, Ejecutado años Anteriores, los cuales se encuentran deshabilitados, y además mostrará un cuadro para programar las metas cuatrimestrales y su respectivo "Total programado Año" en donde el Técnico URP deberá registrar la programación cuatrimestral.
3. Técnico URP. Ingresa información en los campos de la tabla del Anexo A.4 habilitados para la programación de las metas cuatrimestrales para cada una de las etapas que contiene el proyecto.
4. Técnico URP. Selecciona uno de los siguientes subflujos: Guardar o Salir.
5. Sistema. Si la opción seleccionada es "Guardar": Valida los datos registrados en la Programación Cuatrimestral según se establece en la RN B literal a.1 y guarda automáticamente todo el registro de la programación cuatrimestral ingresada en la tabla del Anexo A.1. Si la opción seleccionada es "Salir": regresa al Anexo A.1, sin guardar.

Subflujo termina.

**Resultado**

> No especificado en el documento.

## SF-2 — Registrar Programación de metas físicas (para nuevos estudios)

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP. Selecciona el proyecto nuevo que cargó en CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión", haciendo clic en su código.
2. Sistema. Despliega la pantalla del Anexo A.4 con los siguientes campos: CUP, Etapa (que viene de CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión"), Meta, Entregable, % Ejecutado años Anteriores (los cuales se encuentran vacíos, a excepción del CUP) y además mostrará un cuadro para programar las metas cuatrimestrales en donde el Técnico URP deberá registrar la programación cuatrimestral.
3. Técnico URP. Ingresa información en los campos habilitados de la tabla del Anexo A.4.
4. Técnico URP. Selecciona uno de los siguientes subflujos: Guardar o Salir.
5. Sistema. Si la opción seleccionada es "Guardar": Valida los datos registrados en la Programación Cuatrimestral según se establece en la RN B literal a.2 y traslada automáticamente todo el registro de la programación cuatrimestral ingresada a la tabla del Anexo A.1. Si la opción seleccionada es "Salir": regresa al Anexo A.1, sin guardar.
7. Técnico URP. Da clic en el botón "Enviar a revisión DGICP".
8. Sistema. Notifica al Técnico PRE que la programación PAP (tanto financiera como de metas físicas) fue enviada para su revisión. (ver Anexo C literal a)

Subflujo Termina.

> Nota de ambigüedad: la numeración de los pasos de este subflujo salta directamente del paso 5 al paso 7, sin que exista un paso "6" en el documento. Ver Observaciones.

> ✅ Decisión funcional (v1.2, a solicitud del usuario, no proveniente del PDF): se confirma que los pasos 7-8 de este subflujo constituyen la **única** acción de solicitud de aprobación del Técnico URP para el PAP Institucional, cubriendo de forma unificada tanto la programación financiera (CU-PRE-30) como la de metas físicas (CU-PRE-31) — consistente con el propio texto del paso 8 ("tanto financiera como de metas físicas"). CU-PRE-30 no tiene ni ejecuta una solicitud de aprobación independiente o adicional; su documentación fue actualizada para reflejar esto (ver CU-PRE-30, Observaciones numeral 13). El Técnico URP realiza, por tanto, una sola solicitud para ambas programaciones.

**Resultado**

> No especificado en el documento.

## SF-3 — Revisión Programación de Metas Físicas del PAP

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico PRE. Registra información en el campo "Observaciones DGICP" y da clic en el botón "Enviar observaciones".
2. Sistema. Guarda la información y le asigna la fecha (DD/MM/AA) en que se registró por el Técnico PRE. Habilita los campos del Anexo A.1 y A.4, así como el campo "Respuesta Institución" para el Técnico URP. Notifica al Técnico URP mediante correo electrónico que se han realizado observaciones a lo registrado (ver Anexo C literal b).
3. Técnico URP. Realiza ajustes según corresponda y registra información en el campo "Respuesta Institución" y da clic en el botón "Enviar Respuesta".
4. Sistema. Guarda la información y le asigna la fecha (DD/MM/AA) en que se registró por el Técnico URP. Deshabilita los campos y notifica al Técnico PRE mediante correo electrónico que el Técnico URP ha realizado ajustes a lo registrado (ver Anexo C literal c).

Subflujo Termina.

**Resultado**

> No especificado en el documento.

## SF-4 — Desactivar Código

**Condición**

> No especificado en el documento como un apartado formal; se infiere del propio contenido del paso 1: cuando el código sea desactivado en la Programación Financiera (CU-PRE-30).

**Flujo**

1. Sistema. Cuando el código sea desactivado en la Programación Financiera CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión", automáticamente se desactivará en la programación por Metas Físicas.

Subflujo Termina.

**Resultado**

> No especificado en el documento.

## SF-5 — Eliminar Etapa

**Condición**

> No especificado en el documento como un apartado formal; se infiere del propio contenido del paso 1: cuando se elimine una etapa de proyecto en la Programación Financiera (CU-PRE-30).

**Flujo**

1. Sistema. Cuando se elimine una etapa de proyecto en la Programación Financiera CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión", automáticamente se desactivará en la programación por Metas Físicas.

Subflujo Termina.

**Resultado**

> No especificado en el documento.

## SF-6 — Finalizar Revisión

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico PRE. Ingresa comentarios en los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas DGICP".
2. Técnico PRE. Da clic al botón "Revisión finalizada".
3. Sistema. Guarda y Actualiza el estado a "PAP Revisado" en el Monitoreo PAP del CU-PRO-25 "Monitoreo Programación PAP" (nombre oficial, RQ-C-07).

Subflujo Termina.

> ✅ Decisión funcional (v1.2, a solicitud del usuario, no proveniente del PDF): se confirma que este subflujo constituye la **única** acción de aprobación del Técnico PRE para el PAP Institucional, cubriendo de forma unificada tanto la programación financiera (CU-PRE-30) como la de metas físicas (CU-PRE-31) — consistente con el registro conjunto de comentarios financieros y de metas físicas en el paso 1. CU-PRE-30 no tiene ni ejecuta una aprobación ("Revisión Finalizada") independiente o adicional. El Técnico PRE realiza, por tanto, una sola aprobación para ambas programaciones.

**Resultado**

> No especificado en el documento.

## SF-7 — Generar Reporte

**Condición**

> No especificado en el documento.

**Flujo**

1. Actor. Da clic en el botón "Generar Reporte", que podrá seleccionarse en Excel o PDF.
2. Sistema. Verifica el formato seleccionado y genera el reporte.

Subflujo Termina.

**Resultado**

> No especificado en el documento.

## SF-8 — Modificaciones al PAP Agregar un nuevo estudio (fuera del período de elaboración del PAP)

**Condición**

> No especificado en el documento como un apartado formal de "Condición"; el propio título del subflujo indica que aplica "fuera del período de elaboración del PAP". El documento aclara además: "Para realizar modificaciones al PAP en la programación de metas físicas, será necesario haber realizado previamente el registro en la Programación Financiera (CU-PRE-30 'Programación Cuatrimestral Financiera de la Preinversión')."

**Flujo**

1. Administrador del Sistema. Habilita el Sistema previa solicitud del Coordinador PRE, contando con nota de solicitud de modificación del PAP remitida por la Institución.

El documento indica literalmente: "Los pasos 2, 3, en adelante, son los mismos desarrollados en el Subflujo 2 Registrar programación de metas físicas para nuevos estudios."

**Resultado**

> No especificado en el documento.

## SF-9 — Modificaciones al PAP Modificar la programación de un estudio existente (fuera del período de elaboración del PAP)

**Condición**

> No especificado en el documento como un apartado formal de "Condición"; el propio título del subflujo indica que aplica "fuera del período de elaboración del PAP".

**Flujo**

1. Administrador del Sistema. Habilita el Sistema previa solicitud del Coordinador PRE, contando con nota de solicitud de modificación del PAP remitida por la Institución.
2. Técnico URP. Ingresa en la pantalla del Anexo A.1. Da clic en el CUP del estudio que requiere modificar.
3. Sistema. Muestra la pantalla del Anexo A.4.
4. Técnico URP. Registra los ajustes en la programación del estudio según corresponda.
5. Técnico URP. Selecciona uno de los siguientes subflujos: Guardar o Salir.
6. Sistema. Si la opción seleccionada es "Guardar": Valida los datos registrados en la Programación Cuatrimestral según se establece en la RN B literal a. Si la opción seleccionada es "Salir": regresa al Anexo A.1, sin guardar.

Subflujo termina.

> Nota de ambigüedad: el paso 6 remite genéricamente a "RN B literal a", sin especificar si corresponde al literal a.1 (estudios de arrastre) o a.2 (estudios nuevos), lo cual es relevante dado que este subflujo aplica a un "estudio existente" que podría corresponder a cualquiera de los dos escenarios.

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no incluye una sección explícita titulada "Excepciones" con Código / Descripción / Consecuencia.

# Postcondiciones

1. CU-PRO-25 "Monitoreo Programación PAP" (nombre oficial, RQ-C-07). El documento original citaba aquí "Monitoreo del Avance Cuatrimestral del PAP", que corresponde textualmente al título real de otro caso de uso distinto, CU-EJE-10 — ver nota de resolución en Observaciones.
2. Se guardaron con éxito los registros de avance de la ejecución Cuatrimestral de los estudios contenidos en el PAP vigente.

> Nota de ambigüedad: en el documento original, esta sección aparece numerada como "1. Post condiciones" en lugar de continuar la secuencia numérica del documento (que debería corresponder a "5."), inmediatamente después de la sección "4. Precondiciones". Ver Observaciones.

---

# Reglas de Negocio

## RN-A.a
**Descripción:** El Sistema mostrará en la misma pantalla los Anexos A.1 del CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión" y A.1 del CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión".
**Origen:** No especificado en el documento.

## RN-A.b
**Descripción:** El sistema no debe de permitir ajustes o ingreso de datos por parte del Técnico URP, fuera de la fecha establecida en el Calendario de Eventos del PAP, según Caso de Uso CU-ADM-04 "Gestión de Eventos de Calendario", y se debe de mostrar el mensaje del Anexo A.3 "Periodo de ingreso de información ha finalizado"; y todas las acciones de la tabla Programación Física Cuatrimestral del PAP, quedarán deshabilitadas.
**Origen:** No especificado en el documento.

## RN-A.c
**Descripción:** Los privilegios de cada actor: Técnico URP: responsable del ingreso de la información, podrá registrar la programación de los estudios de arrastre y de los nuevos estudios (que se registraron en la Programación Cuatrimestral Financiera del PAP CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión"). Podrá visualizar, pero no tendrá habilitado el campo "Observaciones DGICP", podrá registrar información en el campo "Respuesta Institución". No podrá visualizar el campo "Comentarios al reporte DGICP". Tendrá habilitado el botón "Generar reporte". Técnico PRE y Coordinador PRE: encargados de revisar la información, tendrán habilitada la subsección Revisión de la Programación Financiera y de Metas Físicas de la Preinversión para el registro (visualización y modificación) en el campo "Observaciones DGICP", y podrán visualizar el campo "Respuesta Institución" sin derecho a edición en el mismo. Ambos tendrán habilitado el botón "Revisión Finalizada", y el botón "Generar reporte". Los demás actores: solo podrán consultar información ingresada por técnico URP, técnico PRE y Coordinador PRE, es decir no podrán ver ningún botón en el campo de acciones de la tabla, a excepción de "Generar reporte".

> ✅ RESUELTO (RQ-C-01, Ronda 5): el negocio confirmó que la subsección "Revisión de la Programación Financiera y de Metas Físicas de la Preinversión" (incluida su capacidad de visualización y modificación del campo "Observaciones DGICP", y el botón "Revisión Finalizada") debe estar disponible tanto para el Técnico PRE como para el Coordinador PRE — no exclusivamente para el Técnico PRE, como decía la redacción original de este literal. Se actualizó el texto para incluir a ambos actores; "los demás actores" (la restricción de solo consulta) ahora excluye explícitamente también al Coordinador PRE, además del Técnico URP y Técnico PRE. Ver también la nota de resolución en RN-C literal a).
**Origen:** No especificado en el documento.

## RN-B.a
**Descripción:** El valor del campo "Total Programado Año" de la sección Programación Cuatrimestral del Anexo A.4 debe cumplir las siguientes condiciones:
- **a.1 Cuando son estudios de arrastre:** No deberá ser superior al porcentaje pendiente de ejecutar del estudio correspondiente, en caso contrario, el Sistema mostrará el siguiente mensaje al dar clic en el botón "Guardar": "Porcentaje Programado supera el 100% de la etapa" (Ver Anexo A.2) y se mantendrá en la pantalla del Anexo A.4. Fórmula: `% Ejecutado años anteriores + % Total año + % Años posteriores = 100%`.
- **a.2 Cuando son estudios nuevos:** No deberá ser mayor a 100%; en caso contrario, el Sistema mostrará el siguiente mensaje al dar clic en el botón "Guardar": "Monto Programado supera el 100%" (Ver Anexo A.2) y se mantendrá en la pantalla del Anexo A.4. Fórmula: `% Ejecutado años anteriores + % Total año + % Años posteriores = 100%`.

> Nota de ambigüedad: los literales a.1 y a.2 remiten al mismo mockup (Anexo A.2) pero describen mensajes de error con textos distintos ("Porcentaje Programado supera el 100% de la etapa" vs. "Monto Programado supera el 100%"). Ver Observaciones.

**Origen:** No especificado en el documento.

## RN-C
**Descripción:** Subsección Observaciones a la programación financiera y de metas físicas de la Preinversión. El campo "Comentarios al reporte DGICP" del Anexo A.1 solo será visible para los actores internos de la DGICP (Técnico Pre, Técnico PRO, Técnico SYMP, Coordinador PRE, Coordinador PRO, Coordinador SYMP, Subjefe DGI y Jefe DGI).
- **a)** El Técnico PRE y el Coordinador PRE podrán visualizar y registrar/modificar información en el campo "Observaciones DGICP", y ambos podrán utilizar el botón de "Revisión Finalizada".

> ✅ RESUELTO (RQ-C-01, Ronda 5): la redacción original de este literal decía "El Técnico PRE, será el único actor que podrá registrar información en el campo 'Observaciones DGICP' y utilizar el botón de 'Revisión Finalizada'", lo cual contradecía a la RN-E (que ya habilitaba el botón "REVISIÓN FINALIZADA" también para el Coordinador PRE). El negocio confirmó que ambos actores (Técnico PRE y Coordinador PRE) deben tener visualización y modificación del campo "Observaciones DGICP" y acceso al botón "Revisión Finalizada"; la restricción "será el único actor" queda descartada. Esta misma resolución se aplicó de forma idéntica en CU-PRE-33 (RN-A.b); no aplica a CU-PRE-30 ni CU-PRE-32, que no tienen este campo (el alcance del Coordinador PRE en esos dos documentos se mantiene sin cambios, ver Observaciones/Datos Pendientes de Definir de esos documentos).
**Origen:** No especificado en el documento.

## RN-D
**Descripción:** Del Botón Revisión Finalizada. Una vez que el Técnico PRE dé clic en el botón "Revisión Finalizada" se actualizará la tabla de la pantalla B.1 del CU-PRO-25 "Monitoreo Programación PAP" (nombre oficial, RQ-C-07).

> ✅ Decisión funcional (v1.2, a solicitud del usuario, no proveniente del PDF): este botón "Revisión Finalizada" es la única acción de aprobación del PAP Institucional por parte del Técnico PRE, y cubre de forma unificada tanto la programación financiera (CU-PRE-30) como la de metas físicas (CU-PRE-31). Ver nota equivalente en SF-6 y en CU-PRE-30.

**Origen:** No especificado en el documento.

## RN-E
**Descripción:** Botones. Los botones del Anexo A.1 contarán con las siguientes características:
- ENVIAR A REVISIÓN DGICP: Visible solo para el Técnico URP, habilitado solo para el Técnico URP durante el período de ingreso de información o cuando se presenten modificaciones al PAP.
- GENERAR REPORTE: Visible y habilitado para todos los actores.
- REVISIÓN FINALIZADA: Visible y habilitado solo para Técnico PRE y Coordinador PRE durante el período de ingreso de información o cuando se presenten modificaciones al PAP.

Los botones del Anexo A.2 solo serán visibles y estarán habilitados para el Técnico URP durante el período de ingreso de información o cuando se presenten modificaciones al PAP.

> ✅ RESUELTO (RQ-C-01, Ronda 5): esta regla ya no contradice a RN-C literal a), que fue actualizada para habilitar el botón "REVISIÓN FINALIZADA" también para el Coordinador PRE, de forma consistente con lo que ya establecía este literal.

**Origen:** No especificado en el documento.

---

# Campos

## Pantalla "Programación Meta Física Cuatrimestral del PAP" (Anexo A.1)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Código | Muestra el Código Único del Proyecto asignado según CU-PRE-01 "Registro de Proyectos". | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Nombre del Proyecto | Muestra el nombre del proyecto según se asignó en el CU-PRE-01 "Registro de Proyectos". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Etapa | Muestra la o las etapas seleccionadas en la Programación Financiera CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Meta Total | Cuando son estudios de arrastre, mostrará siempre 1.00. Si es un estudio nuevo, el Sistema mostrará la información registrada en el Anexo A.4 en el campo "Meta" (1.00). | Numérico | Numérico | No especificado en el documento. | 1.00 | Editable: No (según Anexo B.1). |
| Entregable | Cuando son estudios de arrastre, trae el entregable programado en los años anteriores. Si es un estudio nuevo, el sistema muestra la información registrada en Anexo A.4 en el campo "Entregable". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ejecutado años anteriores | Cuando son estudios de arrastre: Procede del porcentaje ejecutado acumulado en años anteriores según lo reportado. Cuando son estudios nuevos: El campo muestra un valor de 0.00%. | Porcentaje | Porcentaje | No especificado en el documento. | 0.00% (para estudios nuevos). | Editable: No (según Anexo B.1). |
| Programación I Cuatrimestre, II Cuatrimestre y III Cuatrimestre | Campos que se diligencian con la información programada en el Anexo A.4. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Total Año | Muestra la suma de porcentajes de los tres cuatrimestres. El Sistema traerá la suma realizada en el Anexo A.4 en el campo "Total programado año". | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Años posteriores | Muestra el monto pendiente de ejecutar de un estudio. El Sistema lo calculará mediante la fórmula: `Años posteriores = 100% − Ejecutado años anteriores% − Total Año%`. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Campo calculado; fórmula transcrita completa en la Descripción. |
| Total Meta | Columna visible en el mockup del Anexo A.1 y en la hoja Excel "CUPRE-31 A.1" (valor "1" para los 4 registros de ejemplo), pero **no está descrita en la tabla de Formatos del Anexo B.1**, que no incluye una fila para este campo. | No especificado en el documento. | No especificado en el documento. | No especificado en el documento. | No especificado en el documento. | Editable: No especificado. Campo identificado mediante el mockup y el anexo Excel (v1.1); no documentado en el Anexo B.1 — ver "Datos Pendientes de Definir". |

## Sección "Registro de la programación física por etapa de preinversión" (denominada "Anexo A.2" en el Anexo B.1 — ver Observaciones sobre la discrepancia con la numeración de mockups, donde esta pantalla se identifica como "Anexo A.4")

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| CUP | Viaja desde el campo "CUP" del CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión". | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Etapa | Muestra la o las etapas seleccionadas en la Programación Financiera CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión". | Texto | Texto | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Meta Total | Cuando son estudios de arrastre, muestra un valor de 1.00. Si es un estudio nuevo, muestra un valor de 1.00. | Numérico | Numérico | Sí (texto explícito: "Campo obligatorio.") | 1.00 | Editable: No (según Anexo B.1). |
| Entregable Unidad de Medida | Campo para que el técnico URP seleccione un elemento del listado según catálogo "Entregable" (Anexo D). | Texto | Texto | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ejecutado años anteriores | Solo se habilita para estudios que vienen de arrastre. El sistema trae el porcentaje ejecutado acumulado en años anteriores según lo reportado. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Programación I Cuatrimestre, II Cuatrimestre y III Cuatrimestre | Campo para que el Técnico URP registre la programación cuatrimestral física de cada estudio. Debe registrar en al menos un cuatrimestre. Con valores entre 0.00% y 100%. | Porcentaje | Porcentaje | Sí (texto explícito: "Campo obligatorio."; adicionalmente: "Debe registrar en al menos un cuatrimestre"). | No especificado en el documento. | Editable: Sí (según Anexo B.1). Rango válido: 0.00% a 100%. |
| Total Año | Muestra la suma de los porcentajes programados para cada estudio. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |

## Sección "Revisión de la Programación Financiera y de Metas Físicas de la Preinversión"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Comentarios al reporte DGICP (Financiero y de Metas Físicas) | Campos para el registro manual de comentarios a los reportes financiero y de metas físicas por parte del Técnico PRE. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). Solo visible para actores internos de la DGICP (RN-C). |

## Sección "Observaciones a la Programación Financiera y de Metas Físicas del PAP"

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Observaciones DGICP | Campo para que el Técnico PRE registre observaciones (si aplica). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). Único actor habilitado para registrar: Técnico PRE (RN-C.a). |
| Respuesta Institución | Campo para que Técnico URP registre su respuesta a las observaciones del Técnico PRE (si aplica). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). El Técnico PRE puede visualizarlo, sin derecho a edición (RN-A.c). |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|---|---|---|
| Total Programado Año (Anexo A.4, estudios de arrastre) | No deberá ser superior al porcentaje pendiente de ejecutar del estudio correspondiente (RN-B a.1). | "Porcentaje Programado supera el 100% de la etapa" (Anexo A.2). |
| Total Programado Año (Anexo A.4, estudios nuevos) | No deberá ser mayor a 100% (RN-B a.2). | "Monto Programado supera el 100%" (Anexo A.2; nota de ambigüedad: el mockup del Anexo A.2 solo muestra el texto "Porcentaje programado supera el 100% de la etapa" — ver Observaciones). |
| Ingreso/ajuste de datos por el Técnico URP (fuera de fecha del Calendario de Eventos del PAP) | El sistema no debe permitir ajustes o ingreso de datos fuera de la fecha establecida en el Calendario de Eventos del PAP (RN-A b). | "Periodo de ingreso de información ha finalizado" (Anexo A.3). |
| I/II/III Cuatrimestre (Anexo A.4) | Debe registrar en al menos un cuatrimestre, con valores entre 0.00% y 100% (Anexo B.1). | No especificado en el documento (mensaje literal no transcrito para este caso). |
| Etapa, Meta Total, Entregable Unidad de Medida (Anexo A.4) | Campo obligatorio (Anexo B.1). | No especificado en el documento (mensaje literal no transcrito para este caso). |

---

# Errores

| Código | Descripción | Acción esperada |
|---|---|---|
| No especificado en el documento. | "Porcentaje programado supera el 100% de la etapa" (Anexo A.2; ver Observaciones sobre la discrepancia con el texto "Monto Programado supera el 100%" de la RN-B a.2). | El registro se mantiene en la pantalla del Anexo A.4; el usuario debe ajustar el porcentaje programado (RN-B a.1, a.2). |
| No especificado en el documento. | "Período de ingreso de información ha finalizado" (Anexo A.3). | Todas las acciones de la tabla Programación Física Cuatrimestral del PAP quedan deshabilitadas (RN-A b). |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Técnico URP | Responsable del ingreso de la información; puede registrar la programación de los estudios de arrastre y de los nuevos estudios | RN-A.c |
| Técnico URP | Puede visualizar, pero no tiene habilitado, el campo "Observaciones DGICP" | RN-A.c |
| Técnico URP | Puede registrar información en el campo "Respuesta Institución" | RN-A.c; Anexo B.1 |
| Técnico URP | No puede visualizar el campo "Comentarios al reporte DGICP" | RN-A.c |
| Técnico URP | Tiene habilitado el botón "Generar reporte" | RN-A.c; RN-E |
| Técnico URP | Tiene visible y habilitado el botón "Enviar a revisión DGICP" durante el período de ingreso de información o modificaciones al PAP | RN-E |
| Técnico URP | Único actor con los botones del Anexo A.2 visibles y habilitados durante el período de ingreso de información o modificaciones al PAP | RN-E |
| Técnico PRE | Encargado de revisar la información; tiene habilitada la subsección "Revisión de la Programación Financiera y de Metas Físicas de la Preinversión" para visualizar y modificar el campo "Observaciones DGICP" | RN-A.c; RN-C.a |
| Técnico PRE | Puede visualizar el campo "Respuesta Institución", sin derecho a edición | RN-A.c |
| Técnico PRE | Tiene habilitado el botón "Generar reporte" | RN-A.c; RN-E |
| Técnico PRE | Tiene habilitado el botón "Revisión Finalizada" — ✅ RESUELTO (RQ-C-01, Ronda 5): el Coordinador PRE también lo tiene habilitado, de forma consistente en RN-A.c, RN-C.a y RN-E | RN-A.c; RN-C.a; RN-E |
| Coordinador PRE | Tiene habilitada la subsección "Revisión de la Programación Financiera y de Metas Físicas de la Preinversión" para visualizar y modificar el campo "Observaciones DGICP", y tiene habilitado el botón "Revisión Finalizada" — ✅ RESUELTO (RQ-C-01, Ronda 5) | RN-A.c; RN-C.a; RN-E |
| Técnico PRE, Técnico PRO, Técnico SYMP, Coordinador PRE, Coordinador PRO, Coordinador SYMP, Subjefe DGI, Jefe DGI | Pueden visualizar el campo "Comentarios al reporte DGICP" (actores internos de la DGICP) | RN-C |
| Coordinador PRO, Técnico PRO, Jefe DGI, Subjefe DGI | Solo derecho de consulta de la información ingresada por el Técnico URP y el Técnico PRE; no verán ningún botón en el campo de acciones de la tabla, salvo "Generar reporte" | RN-A.c |
| Todos los actores | Botón "GENERAR REPORTE": visible y habilitado | RN-E |
| Coordinador PRE | Solicita al Administrador del Sistema la habilitación de modificaciones al PAP fuera del período de elaboración, mediante nota de solicitud remitida por la Institución | SF-8 paso 1; SF-9 paso 1 |
| Administrador del Sistema | Habilita el Sistema para permitir modificaciones al PAP fuera del período de elaboración, previa solicitud del Coordinador PRE | SF-8 paso 1; SF-9 paso 1 |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión" (precondición; disparador del Flujo Básico; fuente de los campos CUP, Nombre del proyecto y Etapa; **este documento (CU-PRE-31) es, por decisión funcional v1.2, el que ejecuta la única solicitud de aprobación del Técnico URP (SF-2) y la única aprobación del Técnico PRE (SF-6) para ambos casos de uso — ver Observaciones**)
- Avance Cuatrimestral financiero del PAP (CU-PRE-32) (precondición)
- CU-ADM-04 "Gestión de Eventos de Calendario" (precondición; RN-A.b)
- CU-PRE-01 "Registro de Proyectos" (fuente del campo "Código"/CUP y "Nombre del Proyecto")
- CU-PRO-25 Monitoreo Programación PAP (nombre oficial, RQ-C-07; el documento original lo mencionaba con tres redacciones distintas — ver Observaciones) (postcondición)

**Procesos relacionados:**
> No especificado en el documento (más allá de los casos de uso listados).

**Servicios externos:**
- Servicio de notificación por correo electrónico (Anexo C), utilizado para notificar al Técnico URP y al Técnico PRE sobre observaciones, ajustes y solicitudes de revisión.

---

# Pantallas

## Anexo A.1 — Programación por Meta Física Cuatrimestral del PAP

- **Nombre:** Programación por Meta Física Cuatrimestral del PAP.
- **Descripción:** Pantalla principal que muestra el listado de estudios/proyectos con su programación de metas físicas cuatrimestrales, junto con las subsecciones de comentarios de revisión ("Revisión de la Programación Financiera y de Metas Físicas de la Preinversión") y de observaciones ("Observaciones a la Programación Financiera y de Metas Físicas del PAP").
- **Campos:** CUP, Nombre del proyecto, Etapa, Meta Total, Entregable, Ejecutado años anteriores, Programación (I, II, III Cuatrimestre), Total Año, Programación años posteriores; además "Comentarios al reporte financiero DGICP", "Comentarios al reporte de metas físicas DGICP", "Observaciones DGICP", "Respuesta Institución".
- **Botones:** "Enviar a revisión DGICP", "Generar reporte", "Revisión Finalizada", "Enviar observaciones", "Enviar Respuesta".
- **Acciones:** Consultar el listado de estudios; enviar la programación a revisión; generar reporte; finalizar la revisión; registrar y enviar observaciones (Técnico PRE); registrar y enviar respuesta a las observaciones (Técnico URP).

### Ejemplo de datos mostrados en el mockup (Anexo A.1)

**PROGRAMACIÓN CUATRIMESTRAL DE METAS FÍSICAS DEL PAP (%)**

| CUP | NOMBRE DEL PROYECTO | ETAPA | META TOTAL | ENTREGABLE | EJECUTADO AÑOS ANTERIORES | I CUATRIMESTRE | II CUATRIMESTRE | III CUATRIMESTRE | TOTAL AÑO | PROGRAMACIÓN AÑOS POSTERIORES | TOTAL META |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 8040 | Construcción de Unidades de Salud | Perfil | 1 | Estudio de perfil | 70% | 5% | 10% | 15% | 30% | 0% | 1 |
| 8140 | Equipamiento de hospitales a nivel nacional | Prefactibilidad | 1 | Estudio de prefactibilidad | 60% | 0% | 15% | 15% | 30% | 10% | 1 |
| 8142 | Construcción... | Factibilidad | 1 | Estudio de factibilidad | 50% | 5% | 10% | 15% | 30% | 20% | 1 |
| | | Diseño | 1 | Estudio de diseño | 0% | 10% | 25% | 40% | 75% | 25% | 1 |

Botones: "Enviar a revisión DGICP", "Generar reporte".

Subsección "Revisión de la Programación Financiera y de Metas Físicas de la Preinversión": campo de texto "Comentarios al reporte financiero DGICP:", campo de texto "Comentarios al reporte de metas físicas DGICP:", botón "Revisión Finalizada".

Subsección "Observaciones a la Programación Financiera y de Metas Físicas del PAP": campo de texto "Observaciones DGICP:" con botón "Enviar observaciones"; campo de texto "Respuesta Institución:" con botón "Enviar Respuesta".

> **Corrección (v1.1):** la tabla anterior fue actualizada para incluir la columna "TOTAL META" (con valor "1" para los 4 registros de ejemplo), presente en la hoja Excel "CUPRE-31 A.1" pero no incluida en la transcripción original a partir del PDF. El resto de los valores de la hoja Excel (título, filas, columnas y las dos subsecciones "Revisión..."/"Observaciones...") coincide exactamente con lo ya transcrito; no se detectaron más discrepancias.

## Anexo A.2 — Porcentaje Programado supera el 100% de la Etapa

- **Nombre:** Porcentaje Programado supera el 100% de la Etapa.
- **Descripción:** Ventana emergente de error mostrada al intentar guardar una programación cuyo total supera el 100% de la etapa o el porcentaje pendiente de ejecutar (RN-B a.1, a.2).
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "Aceptar".
- **Acciones:** Aceptar el mensaje; el sistema mantiene al usuario en la pantalla del Anexo A.4.

### Ejemplo de datos mostrados en el mockup (Anexo A.2)

Ícono de error (X roja). Título: "Error". Texto: "Porcentaje programado supera el 100% de la etapa". Botón: "Aceptar".

## Anexo A.3 — Periodo de ingreso de información ha finalizado

- **Nombre:** Periodo de ingreso de información ha finalizado.
- **Descripción:** Ventana emergente de error mostrada cuando el Técnico URP intenta ajustar o ingresar datos fuera del período establecido en el Calendario de Eventos del PAP (RN-A b).
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "Aceptar".
- **Acciones:** Aceptar el mensaje.

### Ejemplo de datos mostrados en el mockup (Anexo A.3)

Ícono de error (X roja). Título: "Error". Texto: "Período de ingreso de información ha finalizado". Botón: "Aceptar".

## Anexo A.4 — Registro de la programación física por etapa de preinversión

- **Nombre:** Programación de Metas por Etapa de Preinversión.
- **Descripción:** Pantalla de registro/edición de la programación cuatrimestral de metas físicas de un estudio, mostrando una columna por cada Etapa que se esté programando, con su Meta Total, Entregable, Ejecutado años anteriores y los porcentajes por cuatrimestre.
- **Campos:** CUP, Etapa, Meta Total, Entregable, Ejecutado años anteriores, y en la sección "Programación Cuatrimestral": Periodo (I, II, III Cuatrimestre), Porcentaje (%), Total programado Año.
- **Botones:** "Guardar" (uno por cada columna/etapa mostrada), "Salir".
- **Acciones:** Consultar o registrar el CUP y la Etapa; seleccionar el Entregable; registrar el porcentaje programado por cuatrimestre; guardar o salir sin guardar.

### Ejemplo de datos mostrados en el mockup (Anexo A.4)

**Programación de Metas por Etapa de Preinversión**

| | Columna izquierda (Etapa: Factibilidad) | Columna derecha (Etapa: Diseño) |
|---|---|---|
| CUP | 8142 | (no repetido en la columna derecha) |
| Etapa | Factibilidad | Diseño |
| Meta Total | 1.00 | 1.00 |
| Entregable | Estudio de factibilidad | Estudio de diseño |
| Ejecutado años anteriores | 50.00% | 0.00% |

**Programación Cuatrimestral (columna izquierda — Etapa Factibilidad):**

| Periodo | Porcentaje (%) |
|---|---|
| I Cuatrimestre | 5.00 |
| II Cuatrimestre | 10.00 |
| III Cuatrimestre | 15.00 |
| **Total programado Año** | **30.00** |

[Botón: Guardar]

**Programación Cuatrimestral (columna derecha — Etapa Diseño):**

| Periodo | Porcentaje (%) |
|---|---|
| I Cuatrimestre | 10.00 |
| II Cuatrimestre | 25.00 |
| III Cuatrimestre | 40.00 |
| **Total programado Año** | **75.00** |

[Botón: Guardar]

[Botón: Salir] (común a ambas columnas)

> Nota de ambigüedad: este mockup está titulado "A.4 Registro de la programación física por etapa de preinversión" y así se referencia consistentemente en los Flujos (SF-1, SF-2, SF-9); sin embargo, la tabla de Formatos del Anexo B.1 denomina a esta misma sección de campos "(Anexo A.2)" — ver Observaciones.

> **Corrección (v1.1):** el valor de "Entregable" para la columna Factibilidad se transcribía originalmente como "Estudio de factibili..." por aparecer truncado en el mockup del PDF. La hoja Excel "CUPRE-31 A.4" contiene el mismo mockup con el valor íntegro "Estudio de factibilidad", coincidente además con el valor ya documentado en el catálogo "Entregable" (Anexo D, ver "Catálogos Detectados"). Se completa el valor con base en esta doble confirmación (Excel + catálogo propio del documento), no por inferencia. El resto de los valores de esta pantalla (CUP 8142, Etapas Factibilidad/Diseño, Meta Total, Ejecutado años anteriores, programación cuatrimestral y totales) coincide exactamente entre el PDF y el anexo Excel.

## Anexo A.5 — Reporte de la Programación Cuatrimestral por Meta Física de la Preinversión

- **Nombre:** Reporte de la Programación Cuatrimestral por Meta Física de la Preinversión.
- **Descripción:** Reporte exportable (Excel o PDF) que corresponde a la pantalla del Anexo A.1, sin los botones, generado mediante el SF-7.
- **Campos:** La misma tabla de datos del Anexo A.1, y un campo adicional "Comentarios al reporte de metas físicas DGICP" (visible solo para reportes generados por actores internos de la DGICP).
- **Botones:** No aplica según el documento ("corresponde a la pantalla del Anexo A1, sin los botones").
- **Acciones:** Generar el reporte en Excel o PDF (SF-7); consultar/imprimir el reporte.

### Ejemplo de datos mostrados en el mockup (Anexo A.5)

**PROGRAMACIÓN CUATRIMESTRAL DE METAS FÍSICAS DEL PAP (%)**

(La tabla de datos es idéntica a la transcrita para el Anexo A.1.)

Campo de texto: "Comentarios al reporte de metas físicas DGICP:"

Texto explicativo del documento: "El reporte de la Programación Cuatrimestral por Meta Física de la Preinversión corresponde a la pantalla del Anexo A1, sin los botones. Dicho reporte podrá generarse una vez que el Técnico URP haya dado clic en el botón 'Enviar a revisión DGI'. El campo 'comentarios al reporte financiero DGICP' solo será visible para los reportes generados por actores internos (DGICP)."

> Nota de ambigüedad: el texto menciona el botón "Enviar a revisión DGI" (en vez de "Enviar a revisión DGICP", como se denomina en el resto del documento) y el campo "comentarios al reporte financiero DGICP" (en vez de "comentarios al reporte de metas físicas DGICP", que es el campo efectivamente mostrado en este mockup) — ver Observaciones.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|---|---|---|
| Error | "Porcentaje programado supera el 100% de la etapa" (Anexo A.2) | Al dar clic en "Guardar" cuando el "Total Programado Año" supera el porcentaje pendiente de ejecutar (estudios de arrastre, RN-B a.1) o el 100% (estudios nuevos, RN-B a.2; ver Observaciones sobre la discrepancia de redacción con "Monto Programado supera el 100%"). |
| Error | "Período de ingreso de información ha finalizado" (Anexo A.3) | Cuando el Técnico URP intenta ajustar o ingresar datos fuera de la fecha establecida en el Calendario de Eventos del PAP (RN-A b). |
| Notificación por correo electrónico (Anexo C, literal a) | "Estimado/a [Técnico PRE]: Atentamente, se informa que el día [Fecha de envío de comentarios] la Institución [Nombre de la Institución] ha enviado para revisión la programación financiera y de metas físicas del PAP Institucional." | Cuando el Técnico URP da clic en el botón "Enviar a revisión DGICP" (SF-2, paso 8). |
| Notificación por correo electrónico (Anexo C, literal b) | "Estimados señores [Nombre de la Institución]: Atentamente, se informa que el día [Fecha de envío de comentarios] se han realizado comentarios a los registros del PAP institucional a fin de que sean revisados y/o ajustados según corresponda." | Cuando el Técnico PRE registra observaciones y da clic en "Enviar observaciones" (SF-3, paso 2). |
| Notificación por correo electrónico (Anexo C, literal c) | "Estimado/a [Técnico PRE]: Atentamente, se informa que el día [Fecha de envío de comentarios] la Institución [Nombre de la Institución] ha realizado ajustes y/o registrado justificaciones a los comentarios sobre el PAP Institucional." | Cuando el Técnico URP registra su respuesta y da clic en "Enviar Respuesta" (SF-3, paso 4). |
| Actualización de estado (sin mockup propio) | Actualización del estado a "PAP Revisado" en el Monitoreo PAP del CU-PRO-25 | Cuando el Técnico PRE da clic en el botón "Revisión finalizada" (SF-6, paso 3; RN-D). |

---

# Observaciones

1. La sección de Postcondiciones aparece numerada en el documento como "1. Post condiciones" en lugar de continuar la secuencia numérica del documento (que debería corresponder a "5."), inmediatamente después de la sección "4. Precondiciones". El documento no aclara si se trata de un error de numeración o de una intención distinta.

2. Los actores "Técnico SYMP" y "Coordinador SYMP", mencionados en la RN-C (visibilidad del campo "Comentarios al reporte DGICP"), no aparecen en la lista de "Actores" de la sección 1, ni coinciden con la nomenclatura usada en el resto del documento ("Técnico PRO"/"Coordinador PRO"). El documento no aclara si se trata de roles adicionales no listados, de un error de transcripción, o de una nomenclatura equivalente a "ASYMP" (vista en otros casos de uso del mismo sistema, p. ej. CU-PRE-26.5 "Técnico SYMP").

3. **Resuelto (RQ-C-01, Ronda 5).** Existía una contradicción entre la RN-E y la RN-C.a: la RN-E establecía que el botón "REVISIÓN FINALIZADA" es "Visible y habilitado solo para Técnico PRE y Coordinador PRE", mientras que la RN-C.a) establecía que "El Técnico PRE, será el único actor que podrá... utilizar el botón de 'Revisión Finalizada'". El negocio confirmó que ambos actores (Técnico PRE y Coordinador PRE) deben tener visualización y modificación del campo "Observaciones DGICP" y acceso al botón "Revisión Finalizada"; se actualizaron RN-A.c, RN-C.a y RN-E para reflejarlo de forma consistente. La misma resolución se aplicó en CU-PRE-33; no aplica a CU-PRE-30/32 (no tienen este campo).

4. **Resuelto (RQ-C-07, ronda 3):** el caso de uso CU-PRO-25 se mencionaba con tres redacciones distintas en este documento: "Monitoreo de la programación del PAP" (Breve Descripción y SF-6), "Monitoreo del Avance Cuatrimestral del PAP" (Postcondiciones — que coincidía por error con el título literal de otro caso de uso, CU-EJE-10), y "Monitoreo del Programa Anual de Preinversión – PAP" (RN-D). El negocio confirmó que el nombre oficial de CU-PRO-25 es **"Monitoreo Programación PAP"**; ninguna de las tres variantes era la correcta. Se actualizaron las cinco menciones de este documento (frontmatter `casos_relacionados`, Breve Descripción, SF-6, Postcondición 1 y RN-D) a esta redacción oficial.

5. La tabla de Formatos del Anexo B.1 denomina "(Anexo A.2)" a la sección de campos "Registro de la programación física por etapa de preinversión"; sin embargo, en todos los Flujos (SF-1, SF-2, SF-9) y en los mockups del Anexo A, esa misma pantalla se identifica consistentemente como "Anexo A.4". En la numeración de mockups de este documento, "Anexo A.2" corresponde en realidad al mensaje de error "Porcentaje Programado supera el 100% de la Etapa". El documento no aclara esta discrepancia de numeración.

6. El mensaje de error descrito en la RN-B a.2 ("Monto Programado supera el 100%") no coincide con el texto mostrado en el mockup del Anexo A.2 ("Porcentaje programado supera el 100% de la etapa"), que es el mismo mockup referenciado tanto por la RN-B a.1 como por la RN-B a.2. El documento no aclara cuál es el texto definitivo del mensaje para el escenario de estudios nuevos.

7. El texto descriptivo del Anexo A.5 (Reporte) indica que el reporte "podrá generarse una vez que el Técnico URP haya dado clic en el botón 'Enviar a revisión DGI'", mientras que en el resto del documento (SF-2 paso 7, mockup del Anexo A.1, RN-E) el botón se denomina consistentemente "Enviar a revisión DGICP". El documento no aclara si son el mismo botón.

8. El texto descriptivo del Anexo A.5 indica: "El campo 'comentarios al reporte financiero DGICP' solo será visible para los reportes generados por actores internos (DGICP)"; sin embargo, el campo mostrado en el propio mockup del Anexo A.5 se denomina "Comentarios al reporte de metas físicas DGICP", no "financiero". Esta discrepancia sugiere que el texto pudo haberse copiado de la especificación del CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión" sin adaptarlo completamente a este caso de uso.

9. El Subflujo SF-2 tiene una discontinuidad en la numeración de sus pasos: después del paso 5 continúa directamente con el paso "7" (Técnico URP da clic en "Enviar a revisión DGICP"), sin que exista un paso "6" en el documento.

10. El último contenido del documento (página 15) consiste únicamente en el texto "Estudio general", sin contexto, título ni estructura reconocible. El documento no aclara si corresponde a un quinto elemento incompleto del catálogo "Entregable" (Anexo D) o a otro contenido truncado.

11. La RN-C establece que el campo "Comentarios al reporte DGICP" es visible para varios actores internos de la DGICP (Técnico PRE, Técnico PRO, Técnico SYMP, Coordinador PRE, Coordinador PRO, Coordinador SYMP, Subjefe DGI y Jefe DGI), lo cual amplía la visibilidad de dicho campo más allá de lo indicado en la RN-A.c) para "los demás actores" (que, según esa regla, "no podrán ver ningún botón en el campo de acciones de la tabla, a excepción de 'Generar reporte'"). El documento no aclara si la visibilidad de un campo de comentarios se considera parte de las excepciones permitidas para "los demás actores".

12. El Subflujo SF-9 (paso 6) remite genéricamente a "RN B literal a", sin especificar si corresponde al literal a.1 (estudios de arrastre) o a.2 (estudios nuevos), siendo que dicho subflujo aplica a la modificación de "un estudio existente" que podría corresponder a cualquiera de los dos escenarios.

13. **Decisión funcional (v1.2, a solicitud del usuario, 31/08/2026 — no proveniente del PDF):** se confirma y documenta que el Subflujo SF-2 (paso 7, "Enviar a revisión DGICP") y el Subflujo SF-6 ("Finalizar Revisión") son la única solicitud de aprobación (Técnico URP) y la única aprobación (Técnico PRE) del PAP Institucional, cubriendo de forma unificada tanto la programación financiera (CU-PRE-30) como la de metas físicas (este documento). CU-PRE-30 no tiene, ni tendrá, un flujo propio de solicitud/aprobación independiente; su única mención relacionada (el botón "Enviar a revisión DGI" del Anexo A.8 de ese documento, no descrita en ningún flujo) fue señalada y resuelta en la versión 1.2 de CU-PRE-30 (ver Observaciones numeral 13 de ese documento). Nótese que esta decisión no resuelve la discrepancia de nomenclatura ya señalada en el numeral 7 de estas Observaciones ("Enviar a revisión DGI" vs. "Enviar a revisión DGICP" en el Anexo A.5 de este mismo documento), la cual se mantiene como una discrepancia de redacción interna del PDF, no relacionada con la existencia de una segunda solicitud.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---|---|---|
| Proyecto/Estudio | Entidad principal identificada por su CUP, ya cargada desde el CU-PRE-30 (Programación Financiera). | Consulta y registro de la programación de metas físicas para estudios de arrastre (SF-1) y para nuevos estudios (SF-2); desactivación automática cuando el código se desactiva en la Programación Financiera (SF-4). |
| Etapa de Preinversión | Fase del ciclo de preinversión de un estudio, con su Meta Total, Entregable y Ejecutado años anteriores. | Registro de la meta física por etapa (SF-1, SF-2; Anexo A.4); eliminación automática cuando la etapa se elimina en la Programación Financiera (SF-5). |
| Meta Física / Entregable | Meta total (siempre 1.00 según el documento) y su entregable asociado, seleccionado del catálogo "Entregable" (Anexo D). | Selección del Entregable (Anexo A.4; catálogo Anexo D); registro del avance físico programado por cuatrimestre. |
| Programación Cuatrimestral de Metas | Registro de los porcentajes de avance físico programados por cuatrimestre (I, II, III) para una etapa determinada. | Registro manual (SF-1 paso 3; SF-2 paso 3; Anexo B.1, Anexo A.4); traslado de valores a la tabla del Anexo A.1 al guardar (SF-1 paso 5; SF-2 paso 5). |
| Observaciones DGICP / Respuesta Institución | Ciclo de observaciones del Técnico PRE y respuestas del Técnico URP sobre la programación financiera y de metas físicas. | Registro de observaciones por el Técnico PRE (SF-3 paso 1; RN-C.a); registro de la respuesta por el Técnico URP (SF-3 paso 3); notificación por correo electrónico en ambos sentidos (Anexo C, literales b y c). |
| Reporte de Programación de Metas Físicas | Documento exportable con el detalle de la programación de metas físicas de todos los estudios. | Generación en formato Excel o PDF (SF-7; RN-E; Anexo A.5); registro de comentarios por actores internos de la DGICP (Anexo A.1, Anexo A.5). |

---

# Catálogos Detectados

## Catálogo "Entregable" (Anexo D)

| Entregable |
|---|
| Estudio de perfil |
| Estudio de prefactibilidad |
| Estudio de factibilidad |
| Estudio de diseño |

> Nota: inmediatamente después de este catálogo, el documento incluye el texto "Estudio general" en su última página, sin estructura ni contexto reconocible; no se incorpora como un quinto elemento del catálogo porque no está presentado en el mismo formato de lista que los cuatro anteriores, y podría corresponder a contenido truncado — ver Observaciones, numeral 10, y Datos Pendientes de Definir.

---

# Eventos del Sistema

| Evento | Origen | Destino |
|---|---|---|
| Carga automática de los CUP, "Nombre del proyecto" y "Etapa" desde el CU-PRE-30 | Sistema | Pantalla "Programación por Meta Física Cuatrimestral del PAP" (Anexo A.1) (FB paso 2) |
| Notificación al Técnico PRE de que la programación PAP fue enviada a revisión | Sistema | Técnico PRE (SF-2, paso 8; Anexo C literal a) |
| Notificación al Técnico URP de que se han realizado observaciones | Sistema | Técnico URP (SF-3, paso 2; Anexo C literal b) |
| Notificación al Técnico PRE de que el Técnico URP ha realizado ajustes | Sistema | Técnico PRE (SF-3, paso 4; Anexo C literal c) |
| Desactivación automática de un código en la programación de Metas Físicas | Sistema (CU-PRE-30) | Programación de Metas Físicas (SF-4) |
| Desactivación automática de una etapa en la programación de Metas Físicas | Sistema (CU-PRE-30) | Programación de Metas Físicas (SF-5) |
| Actualización del estado a "PAP Revisado" | Sistema | Monitoreo PAP del CU-PRO-25 (SF-6, paso 3; RN-D) |
| Generación del reporte de Programación de Metas Físicas | Sistema | Anexo A.5 (SF-7) |
| Deshabilitación de todas las acciones de la tabla Programación Física Cuatrimestral del PAP | Sistema | Técnico URP (RN-A b, fuera de fecha del Calendario de Eventos del PAP) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---|---|---|
| Servicio de correo electrónico | Notificación saliente | Envío de notificaciones automáticas al Técnico PRE y al Técnico URP en los momentos descritos en el Anexo C (solicitud de revisión, observaciones realizadas, ajustes realizados). El documento no especifica el proveedor o mecanismo técnico del servicio de correo. |

---

# Datos Pendientes de Definir

1. **Resuelto (RQ-C-01, Ronda 5):** el Coordinador PRE sí puede utilizar el botón "Revisión Finalizada" y visualizar/modificar el campo "Observaciones DGICP", igual que el Técnico PRE (contradicción entre RN-E y RN-C.a ya resuelta — ver Observaciones, numeral 3). La misma resolución aplica en CU-PRE-33; no aplica a CU-PRE-30/32.
2. **Resuelto (RQ-C-07, ronda 3):** el nombre oficial de CU-PRO-25 es "Monitoreo Programación PAP". Las tres redacciones distintas que este documento usaba quedaron actualizadas a esa redacción oficial (ver Observaciones, numeral 4).
3. No se aclara la discrepancia de numeración entre "Anexo A.2" (usado en el Anexo B.1 para la sección de campos de registro de metas por etapa) y "Anexo A.4" (usado en los Flujos y mockups para la misma pantalla) (ver Observaciones, numeral 5).
4. No se aclara cuál es el texto definitivo del mensaje de error para estudios nuevos (RN-B a.2 dice "Monto Programado supera el 100%"; el mockup del Anexo A.2 solo muestra "Porcentaje programado supera el 100% de la etapa") (ver Observaciones, numeral 6).
5. No se aclara si el botón "Enviar a revisión DGICP" (usado en los flujos y en RN-E) y el botón "Enviar a revisión DGI" (mencionado en el texto del Anexo A.5) son el mismo control (ver Observaciones, numeral 7).
6. No se especifica el número de paso "6" faltante en el Subflujo SF-2 (ver Observaciones, numeral 9).
7. No se aclara el contenido o propósito del texto final "Estudio general" en la última página del documento (ver Observaciones, numeral 10).
8. No se aclara si "Técnico SYMP" y "Coordinador SYMP" (mencionados en RN-C) son roles adicionales no listados en la sección de Actores, o corresponden a un error de transcripción (ver Observaciones, numeral 2).
9. No se especifica a cuál de los literales a.1 o a.2 de la RN-B se refiere el paso 6 del Subflujo SF-9 (ver Observaciones, numeral 12).
10. **Nuevo (v1.1):** la columna "Total Meta" (valor "1" en el mockup y en el anexo Excel para los 4 registros de ejemplo del Anexo A.1) no está descrita en la tabla de Formatos del Anexo B.1; no se puede determinar su tipo, formato ni el origen exacto de su cálculo (ver "Campos").
11. Prioridad (importancia) del propio caso de uso no especificada.
12. El documento no incluye una sección explícita de "Excepciones" con Código/Descripción/Consecuencia.