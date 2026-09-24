---
id: CU-PRE-33
codigo: CU-PRE-33
nombre: Avance Cuatrimestral por Metas Físicas del PAP
modulo: Preinversión
submodulo: Gestión del Proyecto
version: "1.2 (1.1 según el anexo Excel/PDF original; ver nota_cambio_v1_2 — la '1.2' de este Markdown refleja una decisión funcional solicitada por el usuario, no una nueva versión del PDF fuente. Nota adicional preexistente: el Historial de Revisiones del propio documento incluye una entrada de versión '2.0' fechada 05/12/2025; sin embargo, todos los encabezados de página y la portada indican consistentemente 'Versión: 1.0' — ver Observaciones)"
fuente_pdf: CU-PRE-33_Avance_cuatrimestral_por_Metas_Físicas_del_PAP_F.pdf
fuente_anexo_xlsx: "CU-PRE-30-33__ANEXO__Esquemas.xlsx (hojas 'CUPRE-33 A.1' y 'CUPRE-33 A.6'; esta última corresponde al mockup documentado en este Markdown como 'Anexo A.4'; el archivo cubre además los CU-PRE-30 a 32 en hojas separadas, no analizadas en este documento)"
pagina_inicio: 1
pagina_fin: 13

nota_version: >
  La versión 1.1 (de la numeración interna de este análisis) incorpora el
  archivo Excel anexo `CU-PRE-30-33__ANEXO__Esquemas.xlsx`. Se revisaron
  las hojas "CUPRE-33 A.1" y "CUPRE-33 A.6" (esta última, pese a su
  nombre, corresponde al mockup ya documentado en este Markdown como
  "Anexo A.4"). Se verificó, celda por celda, que ambas hojas coinciden
  con el contenido ya transcrito a partir del PDF, incluyendo los textos
  truncados con puntos suspensivos ya señalados como tales en la versión
  1.0. Se detectó una única discrepancia menor de puntuación: el texto
  truncado "El atraso se debe a..." aparece en el archivo Excel como
  "El atraso se debe a ...." (con un espacio y cuatro puntos, no tres); se
  señala en "Observaciones" sin alterar el texto ya transcrito del PDF. No
  se detectó contenido nuevo ni otras discrepancias.

nota_cambio_v1_2: >
  Cambio funcional solicitado por el usuario (31/08/2026), NO proveniente del
  PDF fuente: se documenta que el Subflujo SF-3 ("Revisión Finalizada")
  constituye la única aprobación del Técnico PRE para el avance cuatrimestral
  del PAP, cubriendo de forma unificada tanto el avance financiero
  (CU-PRE-32 "Avance Financiero Cuatrimestral del PAP") como el avance de
  metas físicas (este documento, CU-PRE-33) — consistente con el registro
  conjunto de "Comentarios al reporte financiero DGICP" y "Comentarios al
  reporte de metas físicas DGICP" en el paso 1 de ese mismo subflujo.
  CU-PRE-32 no tiene ni ejecutará una aprobación independiente propia (ver
  su propia `nota_cambio_v1_2`).

  **Hallazgo documental identificado al aplicar este cambio (no resuelto,
  se deja constancia):** a diferencia del caso análogo en CU-PRE-31 (donde
  el Subflujo SF-2, pasos 7-8, describe explícitamente al Técnico URP dando
  clic en "Enviar a revisión DGICP" y al Sistema notificando al Técnico
  PRE), en este documento (CU-PRE-33) el botón "Enviar a revisión DGICP"
  solo se menciona como visible/habilitado para el Técnico URP en la
  RN-A.b y en la lista de Botones del mockup del Anexo A.1, pero NINGÚN
  Flujo Básico ni Subflujo de este documento describe el paso en que el
  Técnico URP efectivamente hace clic en ese botón, ni la notificación
  correspondiente al Técnico PRE. Es decir, la "solicitud de aprobación"
  del Técnico URP no tiene, en el PDF de este documento, un paso de flujo
  explícito equivalente al de CU-PRE-31. No se inventa dicho paso; se
  documenta este vacío en "Observaciones" y "Datos Pendientes de Definir"
  para que el negocio confirme si el paso simplemente no fue transcrito al
  PDF, o si en la práctica el envío a revisión ocurre de forma implícita al
  guardar la información (SF-1), sin un clic explícito adicional.

actor_principal: ["Técnico URP","Técnico PRE"]

actores_secundarios: ["Coordinador PRE", "Coordinador PRO", "Técnico PRO", "Jefe DGI", "Subjefe DGI"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-31 Programación Cuatrimestral de Metas Físicas de la Preinversión", "CU-PRE-32 Avance Cuatrimestral financiero del PAP", "Calendario de eventos para programación, seguimiento y ejecución cuatrimestral del PAP (CU-ADM-04 'Gestión de Eventos de Calendario')"]

casos_relacionados: ["CU-EJE-10 Monitoreo del Avance Cuatrimestral del PAP (nota: en el CU-PRE-31 relacionado, un caso de uso de monitoreo aparentemente equivalente se identifica como 'CU-PRO-25', con nombre además inconsistente entre sí — ver Observaciones)"]

roles: ["Técnico URP", "Técnico PRE", "Coordinador PRE", "Coordinador PRO", "Técnico PRO", "Jefe DGI", "Subjefe DGI"]

pantallas: ["Anexo A.1 - Pantalla de avance de la ejecución Cuatrimestral de Metas del PAP", "Anexo A.2 - Generar reporte (selector de formato)", "Anexo A.3 - Período finalizado", "Anexo A.4 - Seguimiento a la programación de Metas Físicas por Etapa de Preinversión"]

procesos: []

servicios_externos: ["Notificación por correo electrónico (Anexo C)"]

entidades: ["Proyecto/Estudio", "Etapa de Preinversión", "Meta Física / Entregable", "Avance Cuatrimestral de Metas (Anual, al Cuatrimestre, del Cuatrimestre)", "Estado del Estudio", "Observaciones DGICP / Respuesta Institución", "Reporte del Avance Cuatrimestral de Metas Físicas"]

catalogos: ["Catálogo de Estados del estudio (RN-B.c): A tiempo, Atrasado, Adelantado, Finalizado"]

palabras_clave: ["avance cuatrimestral", "metas físicas", "PAP", "ejecución de metas", "avance anual", "avance al cuatrimestre", "avance del cuatrimestre", "revisión DGICP"]

ultima_actualizacion: "15/07/2025 (v1.0); ver nota sobre entrada de v2.0 fechada 05/12/2025 en el Historial de Revisiones — Observaciones"

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
  reglas_negocio:
    RNA:
      pagina: 5
    RNB:
      pagina: 6
    RNC:
      pagina: 6
    RND:
      pagina: 7
    RNE:
      pagina: 7
    RNF:
      pagina: 7
    RNG:
      pagina: 8
  anexos:
    A1:
      nombre: "Pantalla de avance de la ejecución Cuatrimestral de Metas del PAP"
      pagina: 9
    A2:
      nombre: "Generar reporte"
      pagina: 10
    A3:
      nombre: "Período finalizado"
      pagina: 10
    A4:
      nombre: "Seguimiento a la programación de Metas Físicas por Etapa de Preinversión"
      pagina: 11
    B:
      nombre: "Requerimientos Funcionales - Formatos"
      pagina: 11
    C:
      nombre: "Contenido Correos Electrónicos"
      pagina: 13
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original (aunque el pie de página del documento sí incluye numeración propia, que coincide con la aquí estimada)."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Avance Cuatrimestral por Metas Físicas del PAP |
| Código | CU-PRE-33 |
| Módulo | Ejecución y Seguimiento |
| Fuente | CU-PRE-33_Avance_cuatrimestral_por_Metas_Físicas_del_PAP_F.pdf; complementado con el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hojas "CUPRE-33 A.1"/"CUPRE-33 A.6" — ver "Pantallas") |
| Versión | 1.2 (1.1 según el anexo Excel/PDF original; la 1.2 refleja una decisión funcional del usuario, no una nueva versión del PDF — ver "Historial de Revisiones" y `nota_cambio_v1_2` en el Front Matter) |

**Campos requeridos (según el PDF):**
> No especificado en el documento. Este documento no incluye una sección explícita titulada "Campos requeridos" en la identificación del caso de uso. En su lugar, el documento incluye una sección "Ruta de Acceso", que se transcribe a continuación por su relevancia funcional:

**Ruta de Acceso (según el PDF):**
- Sistema de Información de Inversión Pública
- Ejecución y Seguimiento
- Avance Cuatrimestral por Metas Físicas del PAP
- Físico

> Nota: los valores `modulo` y `submodulo` del Front Matter se derivaron de esta "Ruta de Acceso", ya que el documento no tiene un campo explícito llamado "Módulo" o "Submódulo".

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|---|---|---|---|---|
| 18/07/2025 | 1.0 | Versión Inicial | Ana Guadalupe Escobar | No especificado en el documento. |
| 05/12/2025 | 2.0 | Versión 2.0 | Equipo Preinversión | No especificado en el documento. |

> Nota de ambigüedad: esta tabla incluye una entrada de versión "2.0" fechada 05/12/2025, mientras que la portada y todos los encabezados de página del documento indican consistentemente "Versión: 1.0". El documento no aclara si el contenido aquí analizado corresponde efectivamente a la versión 1.0 o si debería reflejar cambios de una versión 2.0 no evidenciada en el resto del texto. Este mismo patrón se observó en el CU-PRE-32 relacionado. Ver Observaciones.

> Nota (fuera del PDF, no forma parte de la tabla anterior): la versión "1.2" de este Markdown corresponde a una decisión funcional solicitada por el usuario (31/08/2026), no a una nueva versión del PDF fuente ni del Historial de Revisiones oficial del documento. Ver `nota_cambio_v1_2` en el Front Matter para el detalle.

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección o campo explícito titulado "Objetivo".

---

# Descripción

Este caso de uso es para dar seguimiento al avance de la ejecución de metas físicas cuatrimestral de cada uno de los estudios de proyectos a desarrollar, incluidos en el Programa Anual de Preinversión (PAP) institucional, y poder consultar sobre la ejecución de los cuatrimestres de ejercicios anteriores.

Al Técnico de la Unidad Responsable de Proyectos (URP), le permite:
- Registrar información de avance de la ejecución física cuatrimestral de los estudios.
- Digitar observaciones sobre la ejecución física cuatrimestral de los estudios.
- Consultar información de estudios sobre períodos de seguimiento cuatrimestral en ejercicios anteriores.

Para el Técnico de Preinversión (PRE), este caso de uso permite:
- Revisar el avance de la ejecución y digitar comentarios/observaciones sobre la ejecución física cuatrimestral de los estudios.
- Consultar información de estudios en períodos de seguimiento cuatrimestral en ejercicios anteriores.
- Dar clic en el botón de "Revisión Finalizada", cuando ha revisado la coherencia de la información cargada de acuerdo al calendario de eventos.

Además, para este caso de uso se podrán asignar roles de consulta a actores internos y externos según perfil, para buscar información de seguimiento del avance en la ejecución física de estudios de proyectos que se ejecutan actualmente en el PAP o sobre períodos específicos que se presentarán según el ejercicio fiscal seleccionado.

Asimismo, con este caso de uso, los usuarios podrán generar un reporte en el formato que seleccione (Excel o PDF).

# Actor Principal

La sección "1. Actores" del documento lista conjuntamente:
- Técnico de Unidad Responsable de Proyectos (Técnico URP)
- Técnicos de Preinversión (Técnico PRE)
- Coordinador Área de Preinversión, Coordinador Área de Programación y Análisis de la Inversión (Coordinador PRE, Coordinador PRO)
- Técnico en Programación y Análisis de la Inversión (Técnico PRO)
- Jefe de la División de Gestión de la Inversión (Jefe DGI)
- Subjefe de la División de Gestión de la Inversión (Subjefe DGI)

> Nota de ambigüedad: el documento no distingue explícitamente un "actor principal"; la "Breve Descripción" y la RN-A.b atribuyen roles sustanciales y diferenciados únicamente al Técnico URP (ingreso de información) y al Técnico PRE (revisión de la información), mientras que el resto de actores tienen únicamente derecho de consulta.

---

# Actores Secundarios

- Coordinador PRE (con permisos adicionales según RN-A.b/RN-D.b — ✅ RESUELTO, RQ-C-01, Ronda 5: tiene, junto al Técnico PRE, visualización y modificación de "Comentarios al reporte financiero DGICP", "Comentarios al reporte de metas físicas" y "Observaciones DGICP", y el botón "Revisión Finalizada")
- Coordinador PRO
- Técnico PRO
- Jefe DGI
- Subjefe DGI

---

# Disparador

> No especificado en el documento como un campo formal titulado "Disparador". El paso 1 del Flujo Básico describe la acción que da inicio al caso de uso: "Da clic en el botón 'Siguiente' del CU-PRE-32 'Avance Financiero Cuatrimestral del PAP'".

---

# Precondiciones

1. (CU-PRE-31) Programación de Metas Físicas Cuatrimestral del PAP
2. (CU-PRE-32) Avance Cuatrimestral financiero del PAP
3. Calendario de eventos para programación, seguimiento y ejecución cuatrimestral del PAP. Caso de Uso CU-ADM-04 "Gestión de Eventos de Calendario"

> Nota de ambigüedad: el documento lista estos tres elementos bajo el encabezado "4. Precondiciones" sin un verbo explícito (p. ej. "contar con", "haber ejecutado") que aclare la redacción formal de la condición.

---

# Flujo Principal

## FB — Flujo Básico

1. Actor. Da clic en el botón "Siguiente" del CU-PRE-32 "Avance Financiero Cuatrimestral del PAP".
2. Sistema. Muestra la pantalla del Anexo A.1. En los campos CUP, Nombre del Proyecto y Etapa se muestra la misma información del Anexo A.1 del CU-PRE-32 "Avance Financiero Cuatrimestral del PAP".
3. Actor. Selecciona uno de los siguientes subflujos:
   - SF-1 Registrar seguimiento cuatrimestral de metas físicas del PAP
   - SF-2 Revisión del seguimiento del PAP
   - SF-3 Revisión Finalizada
   - SF-4 Generar Reporte

Caso de Uso Termina.

> Nota de ambigüedad: el paso 1 de este Flujo Básico usa consistentemente el nombre "botón 'Siguiente'" para el control que da acceso a este caso de uso desde el CU-PRE-32; sin embargo, la RN-A.c de este mismo documento se refiere a ese mismo control como "botón 'Seguimiento de Metas' de ese CU" — nombre que también usa la RN-A.c del propio CU-PRE-32 para el mismo botón, mientras que el flujo de ese documento también lo llama "Siguiente". Ver Observaciones.

---

# Flujos Alternos

> El documento no utiliza la nomenclatura "Flujo Alternativo – FA" para este caso de uso, sino "Subflujos" (SF-1 a SF-4), presentados en la sección "2. Flujo de Eventos". Se transcriben a continuación conservando esa nomenclatura original.

## SF-1 — Registrar seguimiento cuatrimestral de metas físicas del PAP

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP. Da clic en el código del proyecto al que se le requiere registrar avance de las metas.
2. Sistema. Muestra la pantalla del Anexo A.4.
3. Técnico URP. Para cada etapa de cada estudio programado en CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": Registra el avance del cuatrimestre en el campo "Avance del Cuatrimestre". Registra comentarios en el campo "Observaciones del Cuatrimestre".
4. Técnico URP. Selecciona uno de los siguientes subflujos: Guardar o Salir.
5. Sistema. Si la opción seleccionada es "Guardar": Realiza validación según RN-C b). Calcula el valor del campo "Total meta acumulada" así: `Total meta acumulada = Ejecutado años anteriores + Avance acumulado meses anteriores + Avance cuatrimestre` (nota de ambigüedad: ver Observaciones sobre el término "meses anteriores" y sobre las variaciones de nombre de este campo). Coloca el porcentaje registrado en el campo "Avance del Cuatrimestre" en el campo "Ejecutado del Cuatrimestre" del Anexo A.1. Dichos valores se acumularán de acuerdo con lo estipulado en la Regla de Negocio RN-B d) y el Anexo A.1. Coloca el valor del campo "Total meta ejecutada" en el campo del mismo nombre del Anexo A.1. Coloca la información registrada en el campo "Observaciones del Cuatrimestre" en el campo "Observaciones del cuatrimestre" del Anexo A.1. Si la opción seleccionada es "Salir": regresa al Anexo A.1, sin guardar.

Subflujo termina.

**Resultado**

> No especificado en el documento.

## SF-2 — Revisión del Seguimiento del PAP

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico PRE. Registra información en el campo "Observaciones DGICP" y da clic en el botón "Enviar observaciones".
2. Sistema. Guarda la información y le asigna la fecha (DD/MM/AA) en que se registró por el Técnico PRE. Habilita el campo "Respuesta Institución" para el Técnico URP y los respectivos campos del Anexo A.1 y A.4.
3. Sistema. Notifica al Técnico URP mediante correo electrónico que se han realizado observaciones a lo registrado (ver Anexo C literal a).
4. Técnico URP. Realiza ajustes según corresponda y/o registra información en el campo "Respuesta Institución" y da clic en el botón "Enviar Respuesta".
5. Sistema. Guarda la información y le asigna la fecha (DD/MM/AA) en el campo "Respuesta Institución" que registró el Técnico URP. Notifica al Técnico PRE mediante correo electrónico que el Técnico URP ha realizado ajustes a lo registrado (ver Anexo C literal b).

Subflujo Termina.

**Resultado**

> No especificado en el documento.

## SF-3 — Revisión Finalizada

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico PRE. Ingresa comentarios en los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas DGICP".
2. Técnico PRE. Da clic al botón "Revisión finalizada".
3. Sistema. Guarda y Actualiza el estado a "Revisado" en el Monitoreo PAP del CU-EJE-10 "Monitoreo del Avance Cuatrimestral del PAP".

Subflujo Termina.

> Nota de ambigüedad: el mockup del Anexo A.1 muestra el botón correspondiente con el rótulo "Finalizar revisión", mientras que este paso y las RN-A.b, RN-D.b y RN-E lo denominan consistentemente "Revisión Finalizada"/"Revisión finalizada". Ver Observaciones. Adicionalmente, el estado resultante se describe aquí como "Revisado", mientras que en el CU-PRE-31 relacionado (subflujo equivalente "Finalizar Revisión") el estado análogo se describe como "PAP Revisado".

> ✅ Decisión funcional (v1.2, a solicitud del usuario, no proveniente del PDF): se confirma que este subflujo constituye la **única** acción de aprobación del Técnico PRE para el avance cuatrimestral del PAP, cubriendo de forma unificada tanto el avance financiero (CU-PRE-32) como el de metas físicas (CU-PRE-33) — consistente con el registro conjunto de comentarios financieros y de metas físicas en el paso 1. CU-PRE-32 no tiene ni ejecuta una aprobación independiente adicional. El Técnico PRE realiza, por tanto, una sola aprobación para ambos avances.

**Resultado**

> No especificado en el documento.

## SF-4 — Generar Reporte

**Condición**

> No especificado en el documento.

**Flujo**

1. Actor. Da clic en el botón "Generar Reporte", el cual podrá seleccionarse en Excel o PDF.
2. Sistema. Verifica el formato seleccionado y genera el reporte. Anexo A.1.

Subflujo Termina.

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no incluye una sección explícita titulada "Excepciones" con Código / Descripción / Consecuencia.

# Postcondiciones

1. CU-EJE-10 "Monitoreo del Avance Cuatrimestral del PAP".
2. Se guardaron con éxito los registros de avance de la ejecución Cuatrimestral de los estudios contenidos en el PAP vigente.

---

# Reglas de Negocio

## RN-A.a
**Descripción:** El sistema no debe de permitir ajustes o ingreso de información por parte del Técnico URP, fuera de las fechas establecidas en el Calendario de Eventos del PAP, según Caso de Uso CU-ADM-04 "Gestión de Eventos de Calendario", y se debe de mostrar el mensaje del Anexo A.3; y todas las acciones de los anexos A.1 y A.4 están deshabilitadas, a excepción del botón "Generar Reporte".
**Origen:** No especificado en el documento.

## RN-A.b
**Descripción:** Los privilegios de cada actor:
- Técnico URP: responsable del ingreso de la información, podrá visualizar los botones "Enviar a revisión DGICP", "Enviar respuesta" y "Generar reporte". Podrá visualizar, pero no tendrá habilitado el campo "Observaciones DGICP", podrá registrar información en el campo "Respuesta Institución". No podrá visualizar los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas".
- Técnico PRE y Coordinador PRE: encargados de revisar la información, tendrán habilitada la subsección Revisión del avance cuatrimestral del PAP para el registro (visualización y modificación) en los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas". Ambos tendrán habilitado el campo "Observaciones DGICP", y podrán solo visualizar el campo "Respuesta Institución". Ambos tendrán habilitado el botón "Revisión Finalizada", y el botón "Generar reporte".
- Los demás actores: sólo tendrán derecho de consultar la información ingresada por el técnico URP, técnico PRE y Coordinador PRE, es decir no podrán ver ningún botón en el campo de acciones de la tabla, a excepción del botón "Generar reporte".

> ✅ RESUELTO (RQ-C-01, Ronda 5): la redacción original de esta regla no mencionaba al Coordinador PRE en el segundo literal (solo al Técnico PRE), y el tercer literal ("Los demás actores") lo excluía implícitamente de estos permisos por no ser ni Técnico URP ni Técnico PRE — lo cual contradecía a la RN-D.b, que ya otorgaba estas mismas capacidades al Coordinador PRE. El negocio confirmó que ambos actores (Técnico PRE y Coordinador PRE) deben tener visualización y modificación de los campos "Comentarios al reporte financiero DGICP", "Comentarios al reporte de metas físicas" y "Observaciones DGICP", y acceso al botón "Revisión Finalizada". Se actualizó el segundo literal para incluir a ambos actores, y el tercer literal para excluir explícitamente también al Coordinador PRE (además de Técnico URP y Técnico PRE) de la restricción de "los demás actores". Esta misma resolución se aplicó de forma idéntica en CU-PRE-31 (RN-C.a); no aplica a CU-PRE-30 ni CU-PRE-32, que no tienen estos campos (el alcance del Coordinador PRE en esos dos documentos se mantiene sin cambios).

> ⚠️ Hallazgo documental (v1.2, identificado al aplicar la decisión funcional de solicitud/aprobación única — no proveniente del PDF): este literal menciona que el Técnico URP "podrá visualizar" el botón "Enviar a revisión DGICP", pero ningún Flujo Básico ni Subflujo de este documento describe el paso en que el Técnico URP hace clic en ese botón, a diferencia del caso análogo de CU-PRE-31 (SF-2, pasos 7-8), donde sí existe ese paso explícito. Ver Observaciones y Datos Pendientes de Definir.

**Origen:** No especificado en el documento.

## RN-A.c
**Descripción:** La pantalla del Anexo A.1 se verá abajo del Anexo A.1 del CU-PRE-32 "Avance Financiero Cuatrimestral del PAP" una vez que se dé clic en el botón "Seguimiento de Metas" de ese CU.
**Origen:** No especificado en el documento.

## RN-B.a
**Descripción:** Para el seguimiento Cuatrimestral, el sistema solo deberá mostrar en la tabla del Anexo A.1, los estudios que estén activos en el PAP para el ejercicio fiscal vigente al cuatrimestre, y para la consulta del seguimiento de años anteriores los estudios de dichos ejercicios.
**Origen:** No especificado en el documento.

## RN-B.b
**Descripción:** Los estudios con "programado del cuatrimestre" = 0, en el cuatrimestre que se está registrando dicho avance, siempre podrán reportar el avance correspondiente desde la pantalla del Anexo A.4.
**Origen:** No especificado en el documento.

## RN-B.c
**Descripción:** Para la columna "Estados" de cada Estudio se aplican los siguientes criterios calculados al cuatrimestre:
- **A tiempo:** presentan una ejecución igual al porcentaje programado al cuatrimestre.
- **Atrasado:** el porcentaje registrado es menor al programado.
- **Adelantado:** la ejecución es mayor a la programada al cuatrimestre, sin sobrepasar el porcentaje del campo "Programado en el Año".
- **Finalizado:** Estudios concluidos conforme a la ejecución de metas físicas.
**Origen:** No especificado en el documento.

## RN-B.d
**Descripción:** En las columnas Avance Anual y Avance al Cuatrimestre, el campo Ejecutado (%) refleja el acumulado reportado hasta el periodo correspondiente. El acumulado no podrá superar el 100% (suma de ejecutados años anteriores + ejecutado en el año vigente).
**Origen:** No especificado en el documento.

## RN-C.a
**Descripción:** Los campos "Ejecutado del Cuatrimestre" y "Observaciones del Cuatrimestre", de la pantalla del Anexo A.4, son editables y estarán habilitados en el período de tiempo según lo mencionado en Caso de Uso CU-ADM-04 "Gestión de Eventos de Calendario".
**Origen:** No especificado en el documento.

## RN-C.b
**Descripción:** El avance del cuatrimestre no debe superar el porcentaje "programado anual". Caso contrario mostrará el mensaje: "El porcentaje total registrado supera el 100%".

> Nota de ambigüedad: este mensaje no tiene un Anexo/mockup propio asociado en el documento (a diferencia de otros mensajes de error del mismo documento, como el del Anexo A.3), por lo que no puede verificarse su presentación visual. Ver Observaciones.

**Origen:** No especificado en el documento.

## RN-D.a
**Descripción:** Subsección Revisión del avance cuatrimestral del PAP. Esta sección del Anexo A.1, solo será visible para los actores internos de la DGICP.
**Origen:** No especificado en el documento.

## RN-D.b
**Descripción:** El Técnico PRE y el Coordinador PRE, serán los únicos actores que podrán registrar información en los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas DGICP" y utilizar el botón de "Revisión Finalizada".

> ✅ RESUELTO (RQ-C-01, Ronda 5): esta regla ya no contradice a RN-A.b, que fue actualizada para otorgar estas mismas capacidades al Coordinador PRE de forma consistente con lo que ya establecía este literal.

**Origen:** No especificado en el documento.

## RN-E
**Descripción:** Del Botón Revisión Finalizada. Una vez que el Técnico PRE dé clic en el botón "Revisión Finalizada" se actualizará la tabla de la pantalla A.1 del CU-EJE-10 "Monitoreo del Avance Cuatrimestral del PAP".

> ✅ Decisión funcional (v1.2, a solicitud del usuario, no proveniente del PDF): este botón "Revisión Finalizada" es la única acción de aprobación del avance cuatrimestral del PAP por parte del Técnico PRE, y cubre de forma unificada tanto el avance financiero (CU-PRE-32) como el de metas físicas (CU-PRE-33). Ver nota equivalente en SF-3 y en CU-PRE-32.

**Origen:** No especificado en el documento.

## RN-F
**Descripción:** Del Cálculo de Porcentajes. En la tabla Ejecución Cuatrimestral de Metas del PAP, el sistema debe realizar cálculos automáticos (%), para cada proyecto, considerando el significado de las siguientes variables:

| Concepto | Explicación | Fórmula |
|---|---|---|
| Ejecutado años anteriores (EAA) | Suma de los porcentajes ejecutados en años anteriores. | No especificado en el documento (no se transcribe una fórmula simbólica para este concepto, solo la descripción textual). |
| Avance Anual Programado en el año (AAP) | Es la suma de lo programado en todos los cuatrimestres; esta información se captura de la pantalla de la Programación Cuatrimestral de Metas Físicas del PAP (CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión"). | `AAP = PROGRAMADO CUATRIMESTRE I + PROGRAMADO CUATRIMESTRE II + PROGRAMADO CUATRIMESTRE III` |
| Avance Anual Ejecutado en el año (AAE) | Es la suma de lo ejecutado en los cuatrimestres; esta información se captura de los registros realizados por el Técnico URP en cada cuatrimestre según corresponda. | `AAE I = EJECUTADO CUATRIMESTRE I`; `AAE II = EJECUTADO CUATRIMESTRE I + EJECUTADO CUATRIMESTRE II`; `AAE III = EJECUTADO CUATRIMESTRE I + EJECUTADO CUATRIMESTRE II + EJECUTADO CUATRIMESTRE III` |
| Avance Programado al Cuatrimestre (APC) | Es la suma de lo programado al cuatrimestre; esta información se captura de la pantalla de la Programación Cuatrimestral de Metas Físicas del PAP (CU-PRE-31). | `APC I = PROGRAMADO CUATRIMESTRE I`; `APC II = PROGRAMADO CUATRIMESTRE I + PROGRAMADO CUATRIMESTRE II`; `APC III = PROGRAMADO CUATRIMESTRE I + PROGRAMADO CUATRIMESTRE II + PROGRAMADO CUATRIMESTRE III` |
| Avance Ejecutado al Cuatrimestre (AEC) | Es la suma de lo ejecutado en cada uno de los cuatrimestres; esta información se captura de los registros realizados por el Técnico URP al cuatrimestre, según corresponda. | `AEC I = EJECUTADO CUATRIMESTRE I`; `AEC II = EJECUTADO CUATRIMESTRE I + EJECUTADO CUATRIMESTRE II`; `AEC III = EJECUTADO CUATRIMESTRE I + EJECUTADO CUATRIMESTRE II + EJECUTADO CUATRIMESTRE III` |
| Avance Programado del Cuatrimestre | Captura el dato de la programación física del cuatrimestre que corresponda, del CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". | No especificado en el documento (no se transcribe una fórmula simbólica adicional, solo la descripción textual). |
| Avance Ejecutado del Cuatrimestre | Es el registro que la Unidad Ejecutora reporta como el porcentaje ejecutado del estudio del proyecto del cuatrimestre; el registro se realiza en el campo "Avance del cuatrimestre" del Anexo A.4 de este CU. | No especificado en el documento (no se transcribe una fórmula simbólica adicional, solo la descripción textual). |
| Total Meta Ejecutada | Es la suma de porcentajes ejecutados en años anteriores + el porcentaje ejecutado en el año. | `Total Meta Ejecutada = EAA + AAE` |

**Origen:** No especificado en el documento.

## RN-G
**Descripción:** Sobre la sección "Avances reportados en cuatrimestres anteriores" (Anexo A.4). Esta sección deberá cumplir con lo siguiente:
- Cuando se registre el avance del Cuatrimestre I, no se mostrará esta sección.
- Cuando se registre el avance del Cuatrimestre II, solo se visualizará lo ejecutado en Cuatrimestre I.
- Cuando se registre el avance del Cuatrimestre III, se visualizará lo ejecutado en cuatrimestres I y II.
**Origen:** No especificado en el documento.

---

# Campos

## Pantalla "Informe de avance cuatrimestral de metas del PAP" (Anexo A.1)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| CUP | Código Único de Proyecto que se ha activado en la Programación de Metas Físicas (CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión"). | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Nombre del Proyecto | Nombre de proyecto que se ha activado en la Programación de Metas Físicas (CU-PRE-31). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Etapa | Proviene del campo "Etapa" del CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". | Tabla | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Tipo de dato "Tabla" transcrito literalmente tal como aparece en el Anexo B.1, sin poder confirmar su significado exacto — ver Observaciones. |
| Meta | Proviene del campo "Meta" del CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: el mockup del Anexo A.1 muestra este campo con valores "1.00" (formato numérico decimal), no en formato porcentaje — ver Observaciones. |
| Entregable | Proviene del campo "Entregable" del CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ejecutado Años anteriores | Suma del porcentaje de la ejecución reportada de años anteriores. Proviene del campo "Ejecución años anteriores" del CU-PRE-31. (Avance Anual) | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Programado en el Año (Avance Anual) | Suma de las cantidades Programadas desde el Cuatrimestre I hasta el Cuatrimestre III, en la Programación por metas Físicas (CU-PRE-31). | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ejecutado en el Año (Avance Anual) | Suma de los porcentajes Ejecutados desde el Cuatrimestre I hasta el Cuatrimestre III. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Programado al Cuatrimestre (Avance al Cuatrimestre) | Suma de los porcentajes programados del Cuatrimestre I, hasta el Cuatrimestre que está en seguimiento (CU-PRE-31). | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ejecutado al Cuatrimestre (Avance al Cuatrimestre) | Suma de los porcentajes Ejecutados desde el Cuatrimestre I hasta el Cuatrimestre en seguimiento. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Programado del Cuatrimestre (Avance del Cuatrimestre) | Porcentaje correspondiente al Cuatrimestre vigente del seguimiento (CU-PRE-31). | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ejecutado del Cuatrimestre (Avance del Cuatrimestre) | Porcentaje ejecutado para el Cuatrimestre correspondiente al campo "Cuatrimestre"; procede del campo "Avance del Cuatrimestre" del Anexo A.4. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Total meta ejecutada | Procede del campo "Total avance acumulado" del Anexo A.4. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Nota de ambigüedad: se transcribe con distintos nombres a través del documento ("Total meta acumulada" en SF-1 paso 5; "Total avance acumulado" en el Anexo A.4; "Total meta ejecutada" aquí y en el mockup) — ver Observaciones. |
| Observaciones del Cuatrimestre | Muestra las observaciones del estudio al cuatrimestre que informa. Procede del campo "Observaciones del Cuatrimestre" del Anexo A.4. | Texto | Texto | Sí (según Anexo B.1). | No especificado en el documento. | Editable: Sí (según Anexo B.1). Contradicción interna: la propia Descripción indica que este campo "muestra" y "procede de" otro Anexo (es decir, es un campo derivado/de solo consulta dentro de esta pantalla), pero la columna "Editable" indica "Sí" — ver Observaciones. |
| Estado | Muestra el Estado en que se encuentra el estudio físicamente (RN-B.c). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). Catálogo completo en la sección "Catálogos Detectados" (RN-B.c). |

## Pantalla "Seguimiento a la programación de Metas por Etapa de Preinversión" (Anexo A.4)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Etapa | Procede del campo "Etapa" del CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Meta programada | Procede del campo "Meta" del CU-PRE-31. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Entregable | Procede del campo "Entregable" del CU-PRE-31. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ejecutado años anteriores | Suma del porcentaje de la ejecución reportada de años anteriores. Proviene del campo "Ejecución años anteriores" del CU-PRE-31. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance del cuatrimestre | Campo para que el Técnico URP registre el avance del cuatrimestre. | Porcentaje | Porcentaje | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Total avance acumulado | Muestra la suma de los campos "Ejecutado años anteriores", "Total" del Avance reportado en cuatrimestres anteriores y "Avance del cuatrimestre". | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Observaciones del Cuatrimestre | Campo para que el Técnico URP registre las observaciones del cuatrimestre. | Texto | Texto | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Cuatrimestre (I, II) — Sección "Avance reportado en cuatrimestres anteriores" | Muestra el Cuatrimestre (I y II). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Porcentaje — Sección "Avance reportado en cuatrimestres anteriores" | Muestra el porcentaje reportado como ejecutado para cada cuatrimestre. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Observaciones — Sección "Avance reportado en cuatrimestres anteriores" | Muestra las observaciones registradas en cada cuatrimestre. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Total — Sección "Avance reportado en cuatrimestres anteriores" | Muestra la suma de los cuatrimestres I y II. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|---|---|---|
| Avance del cuatrimestre (Anexo A.4) | El avance del cuatrimestre no debe superar el porcentaje "programado anual" (RN-C.b). | "El porcentaje total registrado supera el 100%" (sin Anexo/mockup propio asociado — ver Observaciones). |
| Ejecutado (%) (columnas Avance Anual y Avance al Cuatrimestre) | El acumulado no podrá superar el 100% (suma de ejecutados años anteriores + ejecutado en el año vigente) (RN-B.d). | No especificado en el documento (mensaje literal no transcrito para este caso específico; presumiblemente el mismo de la RN-C.b). |
| Ingreso/ajuste de datos por el Técnico URP (fuera de fecha del Calendario de Eventos del PAP) | El sistema no debe permitir ajustes o ingreso de información fuera de las fechas establecidas en el Calendario de Eventos del PAP (RN-A.a). | Mensaje del Anexo A.3: "Período de ingreso de información ha finalizado". |
| Avance del cuatrimestre, Observaciones del Cuatrimestre (Anexo A.4) | Campo obligatorio (Anexo B.1). | No especificado en el documento (mensaje literal no transcrito para este caso). |

---

# Errores

| Código | Descripción | Acción esperada |
|---|---|---|
| No especificado en el documento. | "El porcentaje total registrado supera el 100%" (RN-C.b; sin mockup propio — ver Observaciones). | El usuario debe ajustar el porcentaje registrado (RN-C.b, RN-B.d). |
| No especificado en el documento. | "Período de ingreso de información ha finalizado" (Anexo A.3). | Todas las acciones de los Anexos A.1 y A.4 quedan deshabilitadas, a excepción del botón "Generar Reporte" (RN-A.a). |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Técnico URP | Responsable del ingreso de la información; puede visualizar los botones "Enviar a revisión DGICP", "Enviar respuesta" y "Generar reporte" | RN-A.b |
| Técnico URP | Puede visualizar, pero no tiene habilitado, el campo "Observaciones DGICP" | RN-A.b |
| Técnico URP | Puede registrar información en el campo "Respuesta Institución" | RN-A.b |
| Técnico URP | No puede visualizar los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas" | RN-A.b |
| Técnico PRE | Encargado de revisar la información; tiene habilitada la subsección "Revisión del avance cuatrimestral del PAP" para visualizar y modificar "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas" | RN-A.b; RN-D.a |
| Técnico PRE | Tiene habilitado el campo "Observaciones DGICP"; solo puede visualizar el campo "Respuesta Institución" | RN-A.b |
| Técnico PRE | Tiene habilitado el botón "Revisión Finalizada" y el botón "Generar reporte" — ✅ RESUELTO (RQ-C-01, Ronda 5): el Coordinador PRE también los tiene habilitados, de forma consistente en RN-A.b y RN-D.b | RN-A.b; RN-D.b |
| Coordinador PRE | Tiene habilitada la subsección "Revisión del avance cuatrimestral del PAP" para visualizar y modificar "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas DGICP", el campo "Observaciones DGICP", y tiene habilitado el botón "Revisión Finalizada" — ✅ RESUELTO (RQ-C-01, Ronda 5) | RN-A.b; RN-D.b |
| Coordinador PRO, Técnico PRO, Jefe DGI, Subjefe DGI | Solo derecho de consultar la información ingresada por el Técnico URP y el Técnico PRE; no verán ningún botón en el campo de acciones de la tabla, a excepción de "Generar reporte" | RN-A.b |
| Todos los actores | Todas las acciones de los Anexos A.1 y A.4 se deshabilitan fuera del período de ingreso, excepto "Generar Reporte" | RN-A.a |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión" (precondición; fuente de la mayoría de los campos de las pantallas del Anexo A.1 y A.4)
- CU-PRE-32 "Avance Financiero Cuatrimestral del PAP" (precondición; disparador del Flujo Básico; fuente de los campos CUP, Nombre del Proyecto y Etapa; **este documento (CU-PRE-33) es, por decisión funcional v1.2, el que gestiona la única aprobación del Técnico PRE (SF-3) para ambos casos de uso — ver Observaciones; la solicitud del Técnico URP presenta, sin embargo, un vacío documental pendiente, ver Datos Pendientes de Definir**)
- CU-ADM-04 "Gestión de Eventos de Calendario" (precondición; RN-A.a, RN-C.a)
- CU-EJE-10 "Monitoreo del Avance Cuatrimestral del PAP" (postcondición; destino del SF-3; ver Observaciones sobre su posible relación con "CU-PRO-25" del CU-PRE-31)

**Procesos relacionados:**
> No especificado en el documento (más allá de los casos de uso listados).

**Servicios externos:**
- Servicio de notificación por correo electrónico (Anexo C), utilizado para notificar al Técnico URP y al Técnico PRE sobre observaciones y ajustes.

---

# Pantallas

## Anexo A.1 — Pantalla de avance de la ejecución Cuatrimestral de Metas del PAP

- **Nombre:** Pantalla de avance de la ejecución Cuatrimestral de Metas del PAP.
- **Descripción:** Pantalla principal que muestra el listado de estudios/proyectos con su avance de metas físicas (Avance Anual, Avance al Cuatrimestre y Avance del Cuatrimestre, cada uno con Programado y Ejecutado), el Total meta ejecutada, el Estado del estudio, y las subsecciones de revisión y observaciones.
- **Campos:** CUP, Nombre del proyecto, Etapa, Meta, Entregable, Ejecutado años anteriores, Avance Anual (Programado en el Año/Ejecutado en el Año), Avance al Cuatrimestre (Programado/Ejecutado), Avance del Cuatrimestre (Programado/Ejecutado), Total meta ejecutada, Estado, Observaciones del Cuatrimestre; además "Comentarios al reporte financiero DGICP", "Comentarios al reporte de metas físicas DGICP", "Observaciones DGICP", "Respuesta Institución".
- **Botones:** "Enviar a revisión DGICP", "Generar reporte", "Finalizar revisión" (nota de ambigüedad: rótulo del mockup, distinto de "Revisión Finalizada" usado en el resto del documento — ver Observaciones), "Enviar observaciones", "Enviar Respuesta".
- **Acciones:** Consultar el listado de estudios y su estado; dar clic en el código de un proyecto para registrar su avance de metas (SF-1); enviar la programación a revisión; generar reporte (SF-4); registrar y enviar observaciones (Técnico PRE); registrar y enviar respuesta a las observaciones (Técnico URP); finalizar la revisión (SF-3).

### Ejemplo de datos mostrados en el mockup (Anexo A.1)

**INFORME DE AVANCE AL CUATRIMESTRE [Cuatrimestre] DE METAS FÍSICAS DEL PROGRAMA ANUAL DE PREINVERSION PUBLICA [Año]**

| CUP | NOMBRE DEL PROYECTO | ETAPA | META | ENTREGABLE | EJECUTADO AÑOS ANTERIORES | PROGRAMADO EN EL AÑO | EJECUTADO EN EL AÑO | PROGRAMADO AL CUATRIMESTRE | EJECUTADO AL CUATRIMESTRE | PROGRAMADO DEL CUATRIMESTRE | EJECUTADO DEL CUATRIMESTRE | TOTAL META EJECUTADA | ESTADO | OBSERVACIONES DEL CUATRIMESTRE |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 8040 | Construcción de Unidades de Salud | Diseño | 1.00 | Estudio | 70% | 30% | 15% | 15% | 15% | 10% | 10% | 0.85 | A tiempo | |
| 8140 | Equipamiento de hospitales a nivel nacional | Prefactibilidad | 1.00 | Estudio | 60% | 30% | 0% | 15% | 0% | 15% | 0% | 0.60 | Atrasado | El atraso se debe a... |
| 8142 | Construcción... | Factibilidad | 1.00 | Estudio | 50% | 30% | 10% | 15% | 10% | 10% | 5% | 0.60 | Atrasado | El atraso se debe a... |
| | | Diseño | 1.00 | Diseño | 0% | 75% | 0% | 35% | 0% | 25% | 0% | 0.00 | Atrasado | El atraso se debe a... |

Botones: "Enviar a revisión DGICP", "Generar reporte".

Subsección "Revisión del avance cuatrimestral del PAP": campo de texto "Comentarios al reporte financiero DGICP:", campo de texto "Comentarios al reporte de metas físicas DGICP:", botón "Finalizar revisión".

Subsección "Observaciones a la Programación Financiera y de Metas Físicas del PAP" (nota de ambigüedad: este título menciona "Programación Financiera", tema que no corresponde a este caso de uso — ver Observaciones): campo de texto "Observaciones DGICP:" con botón "Enviar observaciones"; campo de texto "Respuesta Institución:" con botón "Enviar Respuesta".

> Nota: los textos de "Observaciones del Cuatrimestre" para los proyectos 8140 y 8142 aparecen truncados con puntos suspensivos ("El atraso se debe a...") en el mockup original; se transcriben tal como se muestran, sin completar el texto faltante para no inventar información no visible.

> **Verificación (v1.1):** el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hoja "CUPRE-33 A.1") contiene este mismo mockup en formato nativo/editable. Se comparó celda por celda y coincide exactamente: mismo título, mismos 4 registros (CUP 8040, 8140, 8142×2), mismos porcentajes de avance, "Total meta ejecutada" y "Estado" para cada uno. El texto truncado se confirma también truncado en el archivo original, aunque con una puntuación ligeramente distinta: la hoja Excel usa "El atraso se debe a ...." (con un espacio y cuatro puntos), mientras que este documento lo transcribió del PDF como "El atraso se debe a..." (tres puntos, sin espacio). Se señala la diferencia sin alterar el texto ya transcrito del PDF, dado que ambas fuentes coinciden en que el contenido está truncado y no se puede completar.

## Anexo A.2 — Generar reporte

- **Nombre:** Generar reporte.
- **Descripción:** Ventana emergente para seleccionar el formato de exportación del reporte (SF-4).
- **Campos:** No aplica (selector de formato mediante iconos).
- **Botones:** Ícono "Excel", ícono "PDF".
- **Acciones:** Seleccionar el formato de exportación (Excel o PDF).

### Ejemplo de datos mostrados en el mockup (Anexo A.2)

Ícono de advertencia (signo de exclamación amarillo). Texto: "Seleccione formato". Íconos: "Excel" (verde) y "PDF" (rojo).

## Anexo A.3 — Período finalizado

- **Nombre:** Período finalizado.
- **Descripción:** Ventana emergente de error mostrada cuando el Técnico URP intenta ajustar o ingresar información fuera de las fechas establecidas en el Calendario de Eventos del PAP (RN-A.a).
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "ACEPTAR".
- **Acciones:** Aceptar el mensaje.

### Ejemplo de datos mostrados en el mockup (Anexo A.3)

Ícono de error (X roja). Título: "Error". Texto: "Período de ingreso de información ha finalizado". Botón: "ACEPTAR".

## Anexo A.4 — Seguimiento a la programación de Metas Físicas por Etapa de Preinversión

- **Nombre:** Seguimiento a la programación de Metas por Etapa de Preinversión.
- **Descripción:** Pantalla de registro del avance de metas físicas cuatrimestral de un estudio, mostrando una columna por cada Etapa programada, con el histórico de avances de cuatrimestres anteriores y el campo de registro del avance del cuatrimestre en curso.
- **Campos:** Etapa, Meta total, Entregable, Ejecutado años anteriores; sección "Avance reportado en cuatrimestres anteriores" (Cuatrimestre, Porcentaje, Observaciones, Total); sección "Avance del cuatrimestre" (Avance del Cuatrimestre, Total avance acumulado, Observaciones del Cuatrimestre).
- **Botones:** "Guardar" (uno por cada columna/etapa mostrada), "Salir".
- **Acciones:** Consultar el histórico de avances por cuatrimestre; registrar el avance del cuatrimestre en curso y sus observaciones; guardar o salir sin guardar.

### Ejemplo de datos mostrados en el mockup (Anexo A.4)

**Seguimiento a la programación de Metas por Etapa de Preinversión (CUP - 8142) [Cuatrimestre]**

| | Columna izquierda (Etapa: Factibilidad) | Columna derecha (Etapa: Diseño) |
|---|---|---|
| Etapa | Factibilidad | Diseño |
| Meta total | 1.00 | 1.00 |
| Entregable | Estudio de factibilidad | Estudio de diseño |
| Ejecutado años anteriores | 50.00% | 0.00% |

**Avance reportado en cuatrimestres anteriores (columna izquierda — Etapa Factibilidad):**

| Cuatrimestre | Porcentaje | Observaciones |
|---|---|---|
| I | 5.00% | |
| II | 0.00% | |
| **TOTAL** | **5.00%** | |

**Avance del cuatrimestre (columna izquierda):**

Avance del Cuatrimestre [Cuatrimestre]: 5.00%
Total avance acumulado: 60.00%
Observaciones del Cuatrimestre: "El proyecto no presentó el avance estimado debido a..."

[Botón: Guardar]

**Avance reportado en cuatrimestres anteriores (columna derecha — Etapa Diseño):**

| Cuatrimestre | Porcentaje | Observaciones |
|---|---|---|
| I | (en blanco en el mockup) | |
| II | (en blanco en el mockup) | |
| **TOTAL** | (en blanco en el mockup) | |

**Avance del cuatrimestre (columna derecha):**

Avance del Cuatrimestre [Cuatrimestre]: (en blanco en el mockup)
Total avance acumulado: (en blanco en el mockup)
Observaciones del Cuatrimestre: (en blanco en el mockup)

[Botón: Guardar]

[Botón: Salir] (común a ambas columnas)

> Nota: el texto de "Observaciones del Cuatrimestre" de la columna izquierda aparece truncado con puntos suspensivos ("El proyecto no presentó el avance estimado debido a...") en el mockup original; se transcribe tal como se muestra.

> **Verificación (v1.1):** el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hoja "CUPRE-33 A.6", correspondiente a este mockup pese a llamarse "A.6" en el nombre de la hoja) contiene el mismo mockup para el mismo estudio de ejemplo (CUP 8142). Se comparó celda por celda y coincide exactamente: mismas etapas (Factibilidad/Diseño), Meta total, Entregable (sin truncar en ninguna de las dos fuentes: "Estudio de factibilidad"/"Estudio de diseño"), Ejecutado años anteriores, avance reportado en cuatrimestres anteriores, Avance del Cuatrimestre, Total avance acumulado, y el mismo texto truncado de Observaciones del Cuatrimestre (repetido de forma idéntica en el archivo Excel). No se detectaron discrepancias.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|---|---|---|
| Error | "El porcentaje total registrado supera el 100%" (RN-C.b; sin mockup propio) | Cuando el avance del cuatrimestre registrado supera el porcentaje "programado anual" (RN-C.b, RN-B.d). |
| Error | "Período de ingreso de información ha finalizado" (Anexo A.3) | Cuando el Técnico URP intenta ajustar o ingresar información fuera de las fechas establecidas en el Calendario de Eventos del PAP (RN-A.a). |
| Selección de formato | "Seleccione formato" (Anexo A.2, con íconos Excel/PDF) | Al dar clic en el botón "Generar Reporte" (SF-4). |
| Notificación por correo electrónico (Anexo C, literal a) | "Estimados señores [Nombre de la Institución]: Atentamente, se informa que el día [Fecha de envío de comentarios] se realizaron comentarios a los registros del avance de ejecución del PAP institucional, a fin de llevar a cabo los ajustes, según corresponda." | Cuando el Técnico PRE registra observaciones y da clic en "Enviar observaciones" (SF-2, paso 3). |
| Notificación por correo electrónico (Anexo C, literal b) | "Estimado/a [Técnico PRE]: Atentamente, se informa que el día [Fecha de envío de comentarios] la Institución [Nombre de la Institución] realizó ajustes y registró justificaciones a los comentarios sobre el Seguimiento Cuatrimestral del PAP Institucional." | Cuando el Técnico URP registra su respuesta y da clic en "Enviar Respuesta" (SF-2, paso 5). |
| Actualización de estado (sin mockup propio) | Actualización del estado a "Revisado" en el Monitoreo PAP del CU-EJE-10 | Cuando el Técnico PRE da clic en el botón "Revisión finalizada" (SF-3, paso 3; RN-E). |

---

# Observaciones

1. **Resuelto (RQ-C-07, ronda 3, propagado a este documento en Ronda 4 — D-01):** el caso de uso de monitoreo referenciado en las Postcondiciones y en la RN-E se identifica en este documento como "CU-EJE-10 'Monitoreo del Avance Cuatrimestral del PAP'". El CU-PRE-31 relacionado citaba anteriormente a su caso de uso "hermano" de monitoreo con tres redacciones de nombre distintas entre sí (una de las cuales coincidía por error, letra por letra, con el título real de este propio CU-EJE-10); esa inconsistencia ya fue corregida en la Ronda 4, y CU-PRE-30/CU-PRE-31 ahora citan uniformemente a ese caso de uso por su nombre oficial, "CU-PRO-25 'Monitoreo Programación PAP'". La resolución de negocio RQ-C-07 (ronda 3), aplicada en las correcciones de CU-PRO-25 y CU-EJE-10, confirmó que **CU-PRO-25 y CU-EJE-10 son dos casos de uso distintos, no el mismo con codificación diferente**: CU-PRO-25 "Monitoreo Programación PAP" monitorea la revisión de la *programación/planificación* registrada en CU-PRE-30/CU-PRE-31, mientras que CU-EJE-10 "Monitoreo del Avance Cuatrimestral del PAP" monitorea la revisión del *avance/ejecución* real registrada en CU-PRE-32/CU-PRE-33 (este documento). Ambos casos de uso son necesarios y complementarios, no duplicados.

2. El estado resultante de finalizar la revisión se describe en este documento (SF-3, paso 3) como "Revisado", mientras que en el subflujo equivalente del CU-PRE-31 relacionado ("Finalizar Revisión") el estado análogo se describe como "PAP Revisado". El documento no aclara si son estados distintos o el mismo estado con nombre inconsistente entre documentos.

3. El botón que finaliza la revisión se denomina "Revisión Finalizada" en el Flujo (SF-3, paso 2), en la RN-A.b, en la RN-D.b y en la RN-E; sin embargo, el mockup del Anexo A.1 muestra el botón correspondiente con el rótulo "Finalizar revisión" (orden de palabras distinto). El documento no aclara cuál es el rótulo definitivo.

4. **Resuelto (RQ-C-01, Ronda 5).** Existía una contradicción entre la RN-A.b y la RN-D.b: la RN-A.b establecía que "Los demás actores" (todos excepto Técnico URP y Técnico PRE) "no podrán ver ningún botón en el campo de acciones de la tabla, a excepción del botón 'Generar reporte'"; mientras que la RN-D.b establecía que "El Técnico PRE y el Coordinador PRE, serán los únicos actores que podrán... utilizar el botón de 'Revisión Finalizada'". El negocio confirmó que el Coordinador PRE debe tener las mismas capacidades que el Técnico PRE (visualización y modificación de los campos de comentarios/observaciones DGICP, y el botón "Revisión Finalizada"); se actualizó RN-A.b para reflejarlo de forma consistente con RN-D.b. La misma resolución se aplicó en CU-PRE-31; no aplica a CU-PRE-30/32 (no tienen estos campos, el Coordinador PRE se mantiene en consulta según RN-A.c de esos documentos).

5. El mensaje de error de la RN-C.b ("El porcentaje total registrado supera el 100%") no tiene un número de Anexo o mockup propio asociado en el documento, a diferencia de otros mensajes de error del mismo documento (p. ej. el del Anexo A.3) y de mensajes análogos en los casos de uso relacionados CU-PRE-31 y CU-PRE-32 (que sí cuentan con un Anexo dedicado, como el Anexo A.2 de CU-PRE-32).

6. La subsección del mockup del Anexo A.1 titulada "Observaciones a la Programación Financiera y de Metas Físicas del PAP" menciona "Programación Financiera", un tema que no corresponde a este caso de uso (que trata exclusivamente sobre metas físicas). Esta discrepancia sugiere que el texto pudo haberse copiado de la especificación del CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión" (donde esta misma subsección sí trata tanto lo financiero como lo físico) sin adaptarlo completamente a este caso de uso.

7. El campo que totaliza el avance acumulado de un estudio se denomina de tres formas distintas a través del documento: "Total meta acumulada" (fórmula del SF-1, paso 5), "Total avance acumulado" (campo del Anexo A.4, según el Anexo B.1 y el propio mockup), y "Total meta ejecutada" (campo del Anexo A.1, según el Anexo B.1 y el propio mockup, que además indica que "procede del campo 'Total avance acumulado' del Anexo A.4"). El documento no aclara si son tres nombres para el mismo concepto o si existen diferencias funcionales entre ellos.

8. La fórmula del SF-1, paso 5 ("Total meta acumulada = Ejecutado años anteriores + Avance acumulado meses anteriores + Avance cuatrimestre") utiliza el término "Avance acumulado **meses** anteriores", mientras que el resto del documento trabaja consistentemente en términos de "cuatrimestres", no de "meses". El documento no aclara si "meses" es un error de redacción por "cuatrimestres" o si se refiere a un concepto distinto no desarrollado en el resto del documento.

9. El Anexo B.1 describe el campo "Etapa" de la pantalla del Anexo A.1 con Tipo "Tabla" (en lugar de "Texto", "Numérico", "Selección", "Moneda" o "Porcentaje", que son los tipos usados en el resto de la tabla de Formatos), mientras que su columna "Formato" indica "Texto". El documento no aclara qué significa el tipo de dato "Tabla" en este contexto.

10. El campo "Meta" de la pantalla del Anexo A.1 se describe en el Anexo B.1 con Tipo y Formato "Porcentaje"/"Porcentaje", pero el mockup del Anexo A.1 muestra sus valores como "1.00" (formato numérico decimal, sin símbolo de porcentaje), consistente con la forma en que este mismo dato se describe en el CU-PRE-31 relacionado (donde el campo análogo "Meta Total" es de Tipo "Numérico"). El documento no aclara esta discrepancia de formato.

11. El Historial de Revisiones del documento incluye una entrada de versión "2.0" fechada 05/12/2025 ("Equipo Preinversión"), mientras que la portada y todos los encabezados de página del documento indican consistentemente "Versión: 1.0" — el mismo patrón observado en el CU-PRE-32 relacionado.

12. La RN-A.c y el paso 1 del Flujo Básico usan dos nombres distintos para el mismo botón de acceso desde el CU-PRE-32: el Flujo Básico lo llama "botón 'Siguiente'" (consistente con la denominación usada en el propio flujo del CU-PRE-32), mientras que la RN-A.c lo llama "botón 'Seguimiento de Metas' de ese CU" (consistente con la denominación usada en la RN-A.c del propio CU-PRE-32). Este patrón de doble nominación del mismo control ya se había observado en el CU-PRE-30 (con el botón "Programación de Metas"/"Siguiente") y en el CU-PRE-32 (con "Seguimiento de Metas"/"Siguiente").

13. El campo "Observaciones del Cuatrimestre" de la pantalla del Anexo A.1 se describe en el Anexo B.1 con una Descripción que lo caracteriza como un campo derivado/de solo consulta ("Muestra las observaciones del estudio al cuatrimestre que informa. Procede del campo 'Observaciones del Cuatrimestre' del Anexo A.4"), es decir, un valor que el Sistema traslada automáticamente desde el Anexo A.4 (consistente con el paso 5 del SF-1: "Coloca la información registrada en el campo 'Observaciones del Cuatrimestre' en el campo 'Observaciones del cuatrimestre' del Anexo A.1"). Sin embargo, la columna "Editable" de ese mismo campo en el Anexo B.1 indica "Sí". El documento no aclara si el campo es editable directamente en el Anexo A.1 (lo cual contradiría su propia descripción como campo derivado) o si el valor "Sí" de la columna "Editable" se refiere en realidad a su condición de editable en el Anexo A.4 (su origen), no en el Anexo A.1 donde se transcribe. Este mismo patrón de contradicción "Editable: Sí/No" en campos descritos como derivados o de solo consulta ya se había señalado en el CU-PRE-30 (campo "Costo de la etapa") y no se había señalado aún en el CU-PRE-32 (campo "Observaciones del Cuatrimestre" del Anexo A.1, con idéntica redacción y misma contradicción).

14. **Nuevo (v1.1):** el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hojas "CUPRE-33 A.1" y "CUPRE-33 A.6") confirma, sin discrepancias de contenido, todos los valores ya transcritos a partir del PDF para los Anexos A.1 y A.4 de este documento. Se detectó únicamente una diferencia menor de puntuación en el texto truncado "El atraso se debe a...": el archivo Excel lo presenta como "El atraso se debe a ...." (con un espacio y cuatro puntos), mientras que la transcripción de este documento (basada en el PDF) usa tres puntos sin espacio. No se modifica el texto ya transcrito del PDF; se deja constancia de la variación menor entre ambas fuentes.

15. **Decisión funcional (v1.2, a solicitud del usuario, 31/08/2026 — no proveniente del PDF):** se confirma y documenta que el Subflujo SF-3 ("Revisión Finalizada") es la única aprobación del Técnico PRE para el avance cuatrimestral del PAP, cubriendo de forma unificada tanto el avance financiero (CU-PRE-32) como el de metas físicas (este documento). CU-PRE-32 no tiene, ni tendrá, una aprobación propia independiente (ver Observaciones de ese documento, numeral 14).

16. **⚠️ Hallazgo documental (v1.2, identificado al aplicar la decisión funcional del numeral 15 — no proveniente del PDF, no resuelto):** a diferencia del caso análogo en CU-PRE-31 (SF-2, pasos 7-8, donde el Técnico URP hace clic explícitamente en "Enviar a revisión DGICP" y el Sistema notifica al Técnico PRE), en este documento el botón "Enviar a revisión DGICP" solo se menciona como visible/habilitado para el Técnico URP (RN-A.b) y en la lista de "Botones" del mockup del Anexo A.1 (línea "Botones: 'Enviar a revisión DGICP', 'Generar reporte'."), pero ningún Flujo Básico ni Subflujo de este documento (SF-1 a SF-4) describe el paso en que el Técnico URP hace clic en ese botón, ni una notificación correspondiente al Técnico PRE equivalente a la de CU-PRE-31 (SF-2, paso 8). No se completa este vacío inventando el paso faltante; se deja constancia para que el negocio confirme si el paso simplemente no fue transcrito al PDF de este documento, o si el envío a revisión ocurre de forma implícita al guardar la información en el SF-1.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---|---|---|
| Proyecto/Estudio | Entidad principal identificada por su CUP, cargada desde el CU-PRE-32 (Avance Financiero) y, en última instancia, desde el CU-PRE-31 (Programación de Metas Físicas). | Consulta del avance de metas (FB, Anexo A.1); registro del avance del cuatrimestre (SF-1, Anexo A.4). |
| Etapa de Preinversión / Meta / Entregable | Fase del ciclo de preinversión de un estudio, con su Meta y Entregable asociados (procedentes del CU-PRE-31). | Consulta (Anexo A.1, Anexo A.4); base para el cálculo de avances (RN-F). |
| Avance Cuatrimestral de Metas (Anual, al Cuatrimestre, del Cuatrimestre) | Conjunto de valores Programado/Ejecutado (%) calculados para cada proyecto y etapa, en distintos niveles de acumulación temporal. | Registro manual del "Avance del Cuatrimestre" por el Técnico URP (SF-1, paso 3; Anexo A.4); cálculo automático de los demás valores acumulados (RN-F); validación de que lo ejecutado no supere el 100% (RN-B.d, RN-C.b). |
| Estado del Estudio | Clasificación calculada del estudio según su ejecución de metas físicas al cuatrimestre (RN-B.c). | Cálculo automático por el Sistema (RN-B.c); consulta en la columna "Estado" del Anexo A.1. |
| Observaciones del Cuatrimestre | Comentarios registrados por el Técnico URP sobre el avance de un cuatrimestre específico. | Registro manual (SF-1, paso 3; Anexo A.4); traslado al campo "Observaciones del cuatrimestre" del Anexo A.1 al guardar (SF-1, paso 5). |
| Observaciones DGICP / Respuesta Institución | Ciclo de observaciones del Técnico PRE y respuestas del Técnico URP sobre el avance de metas físicas. | Registro de observaciones por el Técnico PRE (SF-2, paso 1); registro de la respuesta por el Técnico URP (SF-2, paso 4); notificación por correo electrónico en ambos sentidos (Anexo C, literales a y b). |
| Reporte del Avance Cuatrimestral de Metas Físicas | Documento exportable con el detalle del avance de metas físicas de todos los estudios. | Generación en formato Excel o PDF (SF-4; Anexo A.2). |

---

# Catálogos Detectados

## Catálogo de Estados del Estudio (RN-B.c)

| Estado | Criterio |
|---|---|
| A tiempo | Presentan una ejecución igual al porcentaje programado al cuatrimestre. |
| Atrasado | El porcentaje registrado es menor al programado. |
| Adelantado | La ejecución es mayor a la programada al cuatrimestre, sin sobrepasar el porcentaje del campo "Programado en el Año". |
| Finalizado | Estudios concluidos conforme a la ejecución de metas físicas. |

---

# Eventos del Sistema

| Evento | Origen | Destino |
|---|---|---|
| Visualización de la pantalla de avance de metas (con CUP, Nombre del Proyecto y Etapa heredados del CU-PRE-32) | Sistema | Pantalla "Pantalla de avance de la ejecución Cuatrimestral de Metas del PAP" (Anexo A.1) (FB, paso 2) |
| Cálculo del "Total meta acumulada"/"Total meta ejecutada" | Sistema | Anexo A.1 (SF-1, paso 5; RN-F) |
| Traslado del "Avance del Cuatrimestre" al campo "Ejecutado del Cuatrimestre" del Anexo A.1 | Sistema | Anexo A.1 (SF-1, paso 5, al Guardar) |
| Traslado de "Observaciones del Cuatrimestre" al campo "Observaciones del cuatrimestre" del Anexo A.1 | Sistema | Anexo A.1 (SF-1, paso 5, al Guardar) |
| Cálculo automático del Estado del estudio | Sistema | Anexo A.1, columna "Estado" (RN-B.c) |
| Notificación al Técnico URP de que se han realizado observaciones | Sistema | Técnico URP (SF-2, paso 3; Anexo C literal a) |
| Notificación al Técnico PRE de que el Técnico URP ha realizado ajustes | Sistema | Técnico PRE (SF-2, paso 5; Anexo C literal b) |
| Actualización del estado a "Revisado" | Sistema | Monitoreo PAP del CU-EJE-10 (SF-3, paso 3; RN-E) |
| Generación del reporte del Avance Cuatrimestral de Metas Físicas | Sistema | Anexo A.2 (SF-4) |
| Deshabilitación de las acciones de los Anexos A.1 y A.4 (excepto "Generar Reporte") | Sistema | Técnico URP (RN-A.a, fuera de fecha del Calendario de Eventos del PAP) |

---

# Integraciones

| Sistema | Tipo | Descripción |
|---|---|---|
| Servicio de correo electrónico | Notificación saliente | Envío de notificaciones automáticas al Técnico URP y al Técnico PRE en los momentos descritos en el Anexo C (observaciones realizadas, ajustes realizados). El documento no especifica el proveedor o mecanismo técnico del servicio de correo. |

---

# Datos Pendientes de Definir

1. **Resuelto (RQ-C-01, Ronda 5):** el Coordinador PRE sí puede utilizar el botón "Revisión Finalizada" y registrar/modificar comentarios DGICP, igual que el Técnico PRE (contradicción entre RN-A.b y RN-D.b ya resuelta — ver Observaciones, numeral 4). La misma resolución aplica en CU-PRE-31; no aplica a CU-PRE-30/32.
2. No se especifica un número de Anexo/mockup para el mensaje de error "El porcentaje total registrado supera el 100%" (RN-C.b) (ver Observaciones, numeral 5).
3. **Resuelto (RQ-C-07, ronda 3):** "CU-EJE-10 'Monitoreo del Avance Cuatrimestral del PAP'" y "CU-PRO-25 'Monitoreo Programación PAP'" (mencionado en el CU-PRE-31 relacionado) son casos de uso distintos y complementarios, no el mismo con codificación diferente — el primero monitorea el avance/ejecución (registrado en CU-PRE-32/33), el segundo la programación/planificación (registrada en CU-PRE-30/31). Ver Observaciones, numeral 1.
4. No se aclara si el estado "Revisado" (SF-3, este documento) y el estado "PAP Revisado" (CU-PRE-31 relacionado) son el mismo estado con nombre inconsistente (ver Observaciones, numeral 2).
5. No se aclara cuál es el rótulo definitivo del botón que finaliza la revisión: "Revisión Finalizada" (Flujos y Reglas de Negocio) o "Finalizar revisión" (mockup) (ver Observaciones, numeral 3).
6. No se aclara si "Total meta acumulada", "Total avance acumulado" y "Total meta ejecutada" son el mismo concepto con tres nombres distintos, o si existen diferencias funcionales entre ellos (ver Observaciones, numeral 7).
7. No se aclara si "Avance acumulado meses anteriores" (SF-1, paso 5) es un error de redacción por "cuatrimestres anteriores", o si se refiere a un concepto distinto no desarrollado en el documento (ver Observaciones, numeral 8).
8. No se aclara qué significa el tipo de dato "Tabla" asignado al campo "Etapa" en el Anexo B.1 (ver Observaciones, numeral 9).
9. No se aclara la discrepancia de formato del campo "Meta" (descrito como "Porcentaje" en el Anexo B.1, pero mostrado como valor numérico decimal "1.00" en el mockup) (ver Observaciones, numeral 10).
10. No se aclara si el contenido de este documento corresponde a la versión 1.0 (indicada en portada y encabezados) o a la versión 2.0 registrada en el Historial de Revisiones (ver Observaciones, numeral 11).
11. No se aclara si el campo "Observaciones del Cuatrimestre" del Anexo A.1 es directamente editable en esa pantalla o si el valor "Sí" de su columna "Editable" (Anexo B.1) se refiere a su condición en el Anexo A.4, dado que su propia Descripción lo caracteriza como un campo derivado/de consulta que "procede de" ese otro Anexo (ver Observaciones, numeral 13).
12. Prioridad (importancia) del propio caso de uso no especificada.
13. El documento no incluye una sección explícita de "Excepciones" con Código/Descripción/Consecuencia.
14. **⚠️ Nuevo (v1.2, identificado a solicitud del usuario al aplicar la decisión funcional de solicitud/aprobación única — no proveniente del PDF):** ningún Flujo Básico ni Subflujo de este documento describe el paso en que el Técnico URP hace clic en el botón "Enviar a revisión DGICP" (mencionado únicamente como visible/habilitado en la RN-A.b y en la lista de Botones del mockup del Anexo A.1), ni la notificación correspondiente al Técnico PRE — a diferencia del caso análogo en CU-PRE-31 (SF-2, pasos 7-8). No se puede determinar, a partir de este documento, si el paso simplemente no fue transcrito al PDF fuente, o si el envío a revisión ocurre de forma implícita al guardar la información en el SF-1 (ver Observaciones, numeral 16).