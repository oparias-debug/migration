---
id: CU-PRE-32
codigo: CU-PRE-32
nombre: Avance Financiero Cuatrimestral del PAP
modulo: Preinversión
submodulo: Gestión del Proyecto
version: "1.2 (1.1 según el anexo Excel/PDF original; ver nota_cambio_v1_2 — la '1.2' de este Markdown refleja una decisión funcional solicitada por el usuario, no una nueva versión del PDF fuente. Nota adicional preexistente: el Historial de Revisiones del propio documento incluye una entrada de versión '2.0' fechada 5/12/2025; sin embargo, todos los encabezados de página y la portada indican consistentemente 'Versión: 1.0' — ver Observaciones)"
fuente_pdf: CU-PRE-32_Avance_cuatrimestral_Financiero_del_PAP_F.pdf
fuente_anexo_xlsx: "CU-PRE-30-33__ANEXO__Esquemas.xlsx (hojas 'CUPRE-32 A.1' y 'CUPRE-32 A.6'; el archivo cubre además los CU-PRE-30, 31 y 33 en hojas separadas, no analizadas en este documento)"
pagina_inicio: 1
pagina_fin: 16

nota_version: >
  La versión 1.1 (de la numeración interna de este análisis, no de la
  versión "1.0"/"2.0" en disputa del PDF original) incorpora el archivo
  Excel anexo `CU-PRE-30-33__ANEXO__Esquemas.xlsx`. Para este documento se
  revisaron las hojas "CUPRE-32 A.1" y "CUPRE-32 A.6" (esta última
  corresponde, pese a su nombre, al mockup ya documentado como "Anexo A.5"
  en este Markdown — ver la discrepancia de numeración ya señalada frente
  al Anexo B.1). Se aplicaron dos correcciones: (1) se resolvió, mediante
  el valor exacto de la hoja Excel, la ambigüedad marcada con "(*)" en la
  celda "Avance al Cuatrimestre" del proyecto CUP 8040 en el mockup del
  Anexo A.1 (texto superpuesto en el PDF); el valor correcto es Ejecutado
  $75,000.00 / 50.00%. (2) Se identificó, únicamente en la hoja Excel
  "CUPRE-32 A.6", el mensaje de error "Las etapas no coinciden con las
  registradas en la Ruta de Preinversión. Revisar y ajustar según
  corresponda", no mencionado en ninguna parte del PDF ni de la versión
  anterior de este documento; se incorporó como nuevo mensaje/pantalla,
  señalando expresamente que no tiene correspondencia en el PDF fuente.
  No se modificó ningún otro contenido ya presente.

nota_cambio_v1_2: >
  Cambio funcional solicitado por el usuario (31/08/2026), NO proveniente del
  PDF fuente: se confirma y documenta explícitamente que CU-PRE-32 no cuenta
  ni contará con un flujo propio de "solicitud de aprobación"/"envío a
  revisión" independiente (consistente con lo ya señalado en Actores
  Secundarios: este documento no tiene campos de comentarios/observaciones
  DGICP y "funciona como un solo flujo secuencial" junto con CU-PRE-33). El
  Subflujo SF-2 de este documento ("Botón Seguimiento de Metas") es
  únicamente de navegación hacia CU-PRE-33 "Avance Cuatrimestral por Metas
  Físicas del PAP", no una solicitud de aprobación. Por decisión del
  usuario, se documenta que el Técnico URP realiza una única solicitud de
  aprobación/envío a revisión, y el Técnico PRE realiza una única
  aprobación ("Revisión Finalizada"), ambas gestionadas en CU-PRE-33,
  cubriendo de forma unificada tanto el avance financiero (CU-PRE-32) como
  el avance de metas físicas (CU-PRE-33). Nota importante: al revisar
  CU-PRE-33 para aplicar este mismo cambio, se identificó que el botón
  "Enviar a revisión DGICP" (mencionado como visible para el Técnico URP en
  la RN-A.b de ese documento) no está descrito en ningún Flujo Básico ni
  Subflujo de CU-PRE-33 — a diferencia del caso análogo en CU-PRE-31, donde
  sí existe un paso explícito (SF-2, pasos 7-8). Este vacío se documenta
  como un nuevo hallazgo en CU-PRE-33 (ver su propio `nota_cambio_v1_2`,
  Observaciones y Datos Pendientes de Definir), y no se resuelve inventando
  el paso faltante. No se elimina ni se reescribe ningún contenido literal
  del PDF de este documento (CU-PRE-32); se agregan notas aclaratorias en
  las secciones correspondientes.

actor_principal: ["Técnico URP","Técnico PRE"]
actores_secundarios: ["Coordinador PRE", "Coordinador PRO", "Técnico PRO", "Jefe DGI", "Subjefe DGI"]

prioridad: No especificado en el documento.

estado: Analizado

depende_de: ["CU-PRE-30 Programación Cuatrimestral Financiera de la Preinversión", "Calendario de eventos para programación, seguimiento y ejecución cuatrimestral del PAP (CU-ADM-04 'Gestión de Eventos de Calendario')"]

casos_relacionados: ["CU-PRE-33 Avance Cuatrimestral por Metas Físicas del PAP", "CU-PRE-25 Elegibilidad (mencionado como postcondición — ver Observaciones)", "CU-PRE-03.5 (mencionado en el Anexo B.1 como fuente del campo 'Costo de la etapa')"]

roles: ["Técnico URP", "Técnico PRE", "Coordinador PRE", "Coordinador PRO", "Técnico PRO", "Jefe DGI", "Subjefe DGI"]

pantallas: ["Anexo A.1 - Pantalla de avance de la ejecución cuatrimestral del PAP-financiero", "Anexo A.2 - Monto Ejecutado supera al Monto Programado Anual", "Anexo A.3 - Periodo de ingreso de información ha finalizado", "Anexo A.4 - Generar reporte (selector de formato)", "Anexo A.5 - Seguimiento a la Programación Financiera por etapa de Preinversión", "Anexo A.6 - Reporte del Avance Cuatrimestral Financiero del PAP"]

procesos: []

servicios_externos: []

entidades: ["Proyecto/Estudio", "Etapa de Preinversión", "Fuente de Financiamiento/Recursos/Convenio", "Avance Cuatrimestral (Anual, al Cuatrimestre, del Cuatrimestre)", "Observaciones del Cuatrimestre", "Reporte del Avance Cuatrimestral Financiero"]

catalogos: ["Fuentes de Financiamiento, Fuentes de Recursos y Convenios (catálogos remitidos a CU-PRE-30/CU-PRE-17; no incluidos en este documento)"]

palabras_clave: ["avance financiero", "ejecución cuatrimestral", "PAP", "seguimiento", "acumulado", "avance anual", "avance al cuatrimestre", "avance del cuatrimestre"]

ultima_actualizacion: "21/07/2025 (v1.0); ver nota sobre entrada de v2.0 fechada 5/12/2025 en el Historial de Revisiones — Observaciones"

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
      pagina: 5
  reglas_negocio:
    RNA:
      pagina: 5
    RNB:
      pagina: 5
    RNC:
      pagina: 6
    RND:
      pagina: 6
    RNE:
      pagina: 6
    RNF:
      pagina: 9
  anexos:
    A1:
      nombre: "Pantalla de avance de la ejecución cuatrimestral del PAP-financiero"
      pagina: 10
    A2:
      nombre: "Monto Ejecutado supera al Monto Programado Anual"
      pagina: 11
    A3:
      nombre: "Periodo de ingreso de información ha finalizado"
      pagina: 11
    A4:
      nombre: "Generar reporte"
      pagina: 11
    A5:
      nombre: "Seguimiento a la Programación Financiera por etapa de Preinversión"
      pagina: 12
    A6:
      nombre: "Reporte del Avance Cuatrimestral Financiero del PAP"
      pagina: 13
    B:
      nombre: "Requerimientos Funcionales - Formatos"
      pagina: 14
  nota_paginas: "Los números de página son estimaciones basadas en el orden secuencial de las secciones dentro del documento extraído, no en marcadores de paginación explícitos y verificados del PDF original (aunque el pie de página del documento sí incluye numeración propia, que coincide con la aquí estimada)."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Avance Financiero Cuatrimestral del PAP |
| Código | CU-PRE-32 |
| Módulo | Ejecución y Seguimiento |
| Fuente | CU-PRE-32_Avance_cuatrimestral_Financiero_del_PAP_F.pdf; complementado con el anexo Excel `CU-PRE-30-33__ANEXO__Esquemas.xlsx` (hojas "CUPRE-32 A.1"/"CUPRE-32 A.6" — ver "Pantallas") |
| Versión | 1.2 (1.1 según el anexo Excel/PDF original; la 1.2 refleja una decisión funcional del usuario, no una nueva versión del PDF — ver "Historial de Revisiones" y `nota_cambio_v1_2` en el Front Matter) |

**Campos requeridos (según el PDF):**
> No especificado en el documento. Este documento no incluye una sección explícita titulada "Campos requeridos" en la identificación del caso de uso. En su lugar, el documento incluye una sección "Ruta de Acceso", que se transcribe a continuación por su relevancia funcional:

**Ruta de Acceso (según el PDF):**
- Sistema de Información de Inversión Pública
- Ejecución y Seguimiento
- Avance Financiero Cuatrimestral del PAP

> Nota: los valores `modulo` y `submodulo` del Front Matter se derivaron de esta "Ruta de Acceso", ya que el documento no tiene un campo explícito llamado "Módulo" o "Submódulo".

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|---|---|---|---|---|
| 21/7/2025 | 1.0 | Versión Inicial | Ricardo Orellana | No especificado en el documento. |
| 5/12/2025 | 2.0 | Versión 2.0 | Área de Preinversión | No especificado en el documento. |

> Nota de ambigüedad: esta tabla incluye una entrada de versión "2.0" fechada 5/12/2025, mientras que la portada y todos los encabezados de página del documento indican consistentemente "Versión: 1.0". El documento no aclara si el contenido aquí analizado corresponde efectivamente a la versión 1.0 o si debería reflejar cambios de una versión 2.0 no evidenciada en el resto del texto. Ver Observaciones.

> Nota (fuera del PDF, no forma parte de la tabla anterior): la versión "1.2" de este Markdown corresponde a una decisión funcional solicitada por el usuario (31/08/2026), no a una nueva versión del PDF fuente ni del Historial de Revisiones oficial del documento. Ver `nota_cambio_v1_2` en el Front Matter para el detalle.

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección o campo explícito titulado "Objetivo".

---

# Descripción

Este caso de uso es para dar seguimiento al avance de la ejecución financiera cuatrimestral de cada uno de los estudios de proyectos a desarrollar, incluidos en el Programa Anual de Preinversión (PAP) institucional y poder consultar sobre la ejecución de los cuatrimestres de ejercicios anteriores.

Para el seguimiento de avance financiero cuatrimestral, este caso de uso cuenta con una lista de selección de Unidades Ejecutoras de proyectos, según el perfil de usuario.

Al Técnico de la Unidad Responsable de Proyectos (URP)-Técnico URP, le permite:
- Registrar información de avance de la ejecución financiera cuatrimestral de los estudios.
- Consultar información de estudios sobre períodos de seguimiento cuatrimestral en ejercicios anteriores.

Para el Técnico de Preinversión (PRE)-Técnico PRE, este caso de uso permite:
- Revisar el avance de la ejecución.
- Consultar información de estudios sobre períodos de seguimiento cuatrimestral en ejercicios anteriores.

Además, para este caso de uso se podrán asignar roles de consulta a actores internos y externos según perfil, para buscar información de seguimiento del avance en la ejecución financiera de estudios de proyectos que se ejecutan actualmente en el PAP o sobre períodos de seguimiento específicos que se presentarán según el ejercicio fiscal seleccionado.

Asimismo, con este caso de uso, los usuarios podrán exportar la información en el formato que seleccione (Excel o PDF).

# Actor Principal

La sección "1. Actores" del documento lista conjuntamente:
- Técnico URP
- Técnico PRE
- Coordinador PRE
- Coordinador PRO
- Técnico PRO
- Jefe DGI
- Subjefe DGI

> Nota de ambigüedad: el documento no distingue explícitamente un "actor principal"; la "Breve Descripción" y la RN-A.c atribuyen roles sustanciales y diferenciados únicamente al Técnico URP (ingreso de información) y al Técnico PRE (revisión de la información), mientras que el resto de actores tienen únicamente derecho de consulta.

---

# Actores Secundarios

- Coordinador PRE
- Coordinador PRO
- Técnico PRO
- Jefe DGI
- Subjefe DGI

> ✅ RESUELTO (RQ-C-01, Ronda 5) — no aplica: el negocio confirmó que este documento (CU-PRE-32) **no tiene campos de comentarios/observaciones** en su formato, a diferencia de CU-PRE-33 (que sí los tiene y con el cual CU-PRE-32 funciona como un solo flujo secuencial, según la misma resolución — que además corrigió la clasificación de este documento de "Programación" a "Avance", clasificación que este documento ya reflejaba correctamente en su propio nombre y contenido). Por lo tanto, la pregunta de si el Coordinador PRE debería tener una capacidad adicional equivalente a "Revisión Finalizada" no aplica a este documento. El alcance del Coordinador PRE en CU-PRE-32 se mantiene sin cambios: consulta, según RN-A.c.

---

# Disparador

> No especificado en el documento como un campo formal titulado "Disparador". El paso 1 del Flujo Básico describe la acción que da inicio al caso de uso: el Actor ingresa a la pantalla del Anexo A.1 y selecciona la Unidad Ejecutora, el año y el cuatrimestre que desea visualizar/registrar.

---

# Precondiciones

1. Programación Cuatrimestral del PAP (CU-PRE-30)
2. Calendario de eventos para programación, seguimiento y ejecución cuatrimestral del PAP. Caso de Uso CU-ADM-04 "Gestión de Eventos de Calendario"

> Nota de ambigüedad: el documento lista estos dos elementos bajo el encabezado "4. Precondiciones" sin un verbo explícito (p. ej. "contar con", "haber ejecutado") que aclare la redacción formal de la condición.

---

# Flujo Principal

## FB — Flujo Básico

1. Actor. Ingresa en la pantalla del Anexo A.1, de acuerdo a las credenciales: Selecciona la Unidad Ejecutora, el año y cuatrimestre que desea visualizar/registrar.
2. Sistema. Muestra la pantalla del Anexo A.1 con la información correspondiente a la Unidad Ejecutora, año y cuatrimestre seleccionado.
3. Actor. Selecciona uno de los siguientes subflujos:
   - SF-1 Registrar seguimiento cuatrimestral financiero del PAP
   - SF-2 Botón Seguimiento de Metas
   - SF-3 Generar Reporte

Caso de Uso Termina.

> Nota de ambigüedad: en el documento original, el segundo paso del Flujo Básico está numerado como "1." (repitiendo el número del primer paso, en lugar de "2."), y el tercer paso está numerado como "4." (en lugar de "3."), es decir, la secuencia original es 1, 1, 4. Se han renumerado aquí de forma secuencial (1, 2, 3) únicamente para facilitar la lectura, preservando el contenido literal de cada paso; ver Observaciones para el detalle de esta irregularidad tal como aparece en el PDF.

---

# Flujos Alternos

> El documento no utiliza la nomenclatura "Flujo Alternativo – FA" para este caso de uso, sino "Subflujos" (SF-1 a SF-3), presentados en la sección "2. Flujo de Eventos". Se transcriben a continuación conservando esa nomenclatura original.

## SF-1 — Registrar seguimiento cuatrimestral financiero del PAP

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP. Da clic en el código del proyecto al que se le requiere registrar avance financiero.
2. Sistema. Muestra la pantalla del Anexo A.5.
3. Técnico URP. Para cada etapa y fuente de financiamiento programada en CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": Registra el avance del cuatrimestre en el campo "Avance del Cuatrimestre". Registra comentarios en el campo "Observaciones del Cuatrimestre".
4. Técnico URP. Selecciona uno de los siguientes subflujos: Guardar o Salir.
5. Sistema. Si la opción seleccionada es "Guardar": Realiza validación según RN-D a). Coloca el monto registrado en el campo "Avance del Cuatrimestre" en la columna "Ejecutado" en el Avance del Cuatrimestre de la pantalla del Anexo A.1. Dichos valores se acumularán de acuerdo con lo estipulado en la Regla de Negocio RN-B b) y el Anexo A.1. Coloca la información registrada en el campo "Observaciones del Cuatrimestre" del Anexo A.5 en el campo "Observaciones" del Anexo A.1. Si la opción seleccionada es "Salir": regresa al Anexo A.1, sin guardar.

Subflujo termina.

**Resultado**

> No especificado en el documento.

## SF-2 — Botón Seguimiento de Metas

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP. Da clic en el botón "Siguiente".
2. Sistema. Muestra el Anexo A.1 del CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP".

Subflujo termina.

> Nota de ambigüedad: el título de este subflujo y la RN-A.c denominan a este control "botón 'Seguimiento de Metas'", pero el paso 1 del propio subflujo indica que la acción es dar clic en el botón "Siguiente". El documento no aclara si son el mismo botón con dos nombres, o si "Seguimiento de Metas" es solo el nombre descriptivo del subflujo. Ver Observaciones.

> ✅ Decisión funcional (v1.2, a solicitud del usuario, no proveniente del PDF): este subflujo es únicamente de navegación hacia CU-PRE-33; no constituye una solicitud de aprobación. CU-PRE-32 no tiene ni ejecuta una solicitud/aprobación propia — la única solicitud del Técnico URP y la única aprobación del Técnico PRE para el avance del PAP (financiero y de metas físicas) se gestionan de forma unificada en CU-PRE-33. Ver Observaciones y Dependencias.

**Resultado**

> No especificado en el documento.

## SF-3 — Generar Reporte

**Condición**

> No especificado en el documento.

**Flujo**

1. Técnico URP / Técnico PRE. Da clic en el botón "Generar Reporte", el cual podrá seleccionarse en Excel o PDF.
2. Sistema. Verifica el formato seleccionado y genera el reporte. Anexo A.1 (nota de ambigüedad: la referencia "Anexo A.1" en este paso parece corresponder en realidad al reporte generado, mostrado en el mockup del Anexo A.6 "Reporte del Avance Cuatrimestral Financiero del PAP" — ver Observaciones).

Subflujo Termina.

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no incluye una sección explícita titulada "Excepciones" con Código / Descripción / Consecuencia.

# Postcondiciones

1. CU-PRE-25 Elegibilidad
2. Se guardaron con éxito los registros de avance de la ejecución cuatrimestral de cada proyecto contenido en el PAP vigente. CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP"

> Nota de ambigüedad: se incluye "CU-PRE-25 Elegibilidad" como postcondición, lo cual resulta inusual dado que en otros casos de uso del mismo sistema (p. ej. CU-PRE-26.5, CU-PRE-29, CU-PRE-30) "CU-PRE-25 Elegibilidad" se describe como una precondición (un requisito previo del ciclo de preinversión), no como una postcondición de un caso de uso de seguimiento de ejecución. El documento no explica esta relación. Adicionalmente, la referencia a "CU-PRE-33 'Avance Cuatrimestral por Metas Físicas del PAP'" aparece incrustada dentro del texto del segundo punto, sin un viñeta o ítem propio. Ver Observaciones.

---

# Puntos de Inclusión

> Sección adicional presente en el documento (numerada "6. Puntos de Inclusión"), sin equivalente directo en la plantilla estándar. Se transcribe aquí por completitud:

N/A

---

# Reglas de Negocio

## RN-A.a
**Descripción:** Campo de "Unidad Ejecutora" aparece por defecto de acuerdo al perfil del Técnico URP de registro, y para el caso del Técnico PRE tendrá la opción de lista desplegable y de una sola selección, con todas las Unidades Ejecutoras. Campo Año: por defecto aparecerá el año vigente, con una lista desplegable de selección, desde el año 1999 y hasta 10 años posteriores al año vigente. Campo "Período" por defecto mostrará el cuatrimestre vigente, con una lista desplegable de selección, para los demás cuatrimestres. Botón "Buscar" filtra y genera la tabla conforme a las opciones elegidas anteriormente.
**Origen:** No especificado en el documento.

## RN-A.b
**Descripción:** El sistema no debe de permitir ajustes o ingreso de información por parte del Técnico URP, fuera de las fechas establecidas en el Calendario de Eventos del PAP, según Caso de Uso CU-ADM-04 "Gestión de Eventos de Calendario", y se debe de mostrar el mensaje del Anexo A.3; y todas las acciones de las tablas de los Anexos A.1 y A.5, estarán deshabilitadas, a excepción del botón "Generar reporte".
**Origen:** No especificado en el documento.

## RN-A.c
**Descripción:** Los privilegios de cada actor: Técnico URP: responsable del ingreso de la información. Tiene habilitado el botón "Generar reporte" y el botón "Seguimiento de Metas". Técnico PRE: encargado de revisar la información. Tiene habilitado el botón "Generar reporte" y el botón "Seguimiento de Metas". Los demás actores: tendrán derecho de consultar la información ingresada por el técnico URP y técnico PRE, es decir no podrán ver ningún botón en el campo de acciones de la tabla, a excepción del botón "Generar reporte" y el botón "Seguimiento de Metas".
**Origen:** No especificado en el documento.

## RN-B.a
**Descripción:** Para el seguimiento cuatrimestral, el sistema solo deberá mostrar en la tabla de Ejecución Cuatrimestral del PAP, los estudios que estén activos para el ejercicio fiscal vigente al cuatrimestre del período de seguimiento según CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión" y para la consulta del seguimiento de años anteriores, los estudios vigentes de dichos ejercicios.
**Origen:** No especificado en el documento.

## RN-B.b
**Descripción:** En las columnas Avance Anual y Avance al Cuatrimestre, el campo Ejecutado (monto $) refleja el acumulado reportado hasta el periodo correspondiente. El acumulado no podrá superar el monto programado en la columna Costo de la Etapa, según la Programación Financiera registrada en el PAP (CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión"). Esta regla aplica tanto a estudios nuevos como a estudios de proyectos de arrastre. En caso de que el monto ejecutado exceda lo programado, el sistema deberá generar una alerta para revisión y ajuste del PAP vigente (ver Anexo A.2).
**Origen:** No especificado en el documento.

## RN-C
**Descripción:** Botón Buscar. El botón "Buscar" permanecerá siempre activo independientemente del usuario; dependiendo de las credenciales podrá seleccionar "Unidad ejecutora"; al dar clic a este botón se generará el reporte de "Seguimiento Financiero" y "Seguimiento Físico".
**Origen:** No especificado en el documento.

## RN-D.a
**Descripción:** Los campos "Ejecutado del Cuatrimestre" y "Observaciones del Cuatrimestre", de la pantalla del Anexo A.5, son editables y estarán habilitados en el período de tiempo según lo mencionado en Caso de Uso CU-ADM-04 "Gestión de Eventos de Calendario". El valor del campo "Ejecutado del Cuatrimestre" por cada fuente de financiamiento deberá cumplir con las siguientes condiciones:
- **En el Cuatrimestre I:** El avance del Cuatrimestre debe ser menor o igual al valor del campo "Total Año" del CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión".
- **En el Cuatrimestre II:** El avance del cuatrimestre debe ser menor o igual al resultado de la fórmula: `Total anual − Avance del Cuatrimestre I`.
- **En el Cuatrimestre III:** El avance del cuatrimestre debe ser menor o igual al resultado de la fórmula: `Total anual − Avance del Cuatrimestre I − Avance del Cuatrimestre II`.

En caso de no cumplirse esta condición en el Cuatrimestre que se está reportando, el Sistema mostrará el mensaje del Anexo A.2: "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado".

> Nota de ambigüedad: el texto del mensaje citado aquí ("Error. El monto del avance del cuatrimestre no debe superar el monto anual programado") no coincide con el texto mostrado en el mockup del Anexo A.2 ("Monto Ejecutado supera Monto Programado Anual"). Ver Observaciones.

**Origen:** No especificado en el documento.

## RN-D.b
**Descripción:** La regla de procesamiento para el monto ejecutado anual y ejecutado al cuatrimestre es "acumulada" para el mismo año y la suma de los montos "ejecutado al cuatrimestre" no debe superar el monto "programado anual" por fuente de financiamiento.
**Origen:** No especificado en el documento.

## RN-E
**Descripción:** Del Cálculo de Acumulados y Porcentajes. En la tabla Ejecución Cuatrimestral del PAP, el sistema debe realizar cálculos automáticos, para cada proyecto, etapa y fuente, según corresponda, considerando las siguientes variables:
- EAA = Ejecutado Años Anteriores
- AAP = Avance Anual Programado
- AAE$ = Avance Anual Ejecutado (Monto $)
- AA% = Avance Anual Porcentaje (%)
- AaCP$ = Avance al Cuatrimestre Programado (Monto)
- AaCE$ = Avance al Cuatrimestre Ejecutado (Monto)
- AaC% = Avance al Cuatrimestre Porcentaje Ejecutado (%)
- AdCP$ = Avance del Cuatrimestre Programado (Monto)
- AdCE$ = Avance del Cuatrimestre Ejecutado (Monto)
- AdCE% = Avance del Cuatrimestre Porcentaje Ejecutado (%)

| Concepto | Explicación | Fórmula |
|---|---|---|
| Ejecutado Años Anteriores | Suma de los montos de ejecución de estudios de proyectos en años anteriores. | `EAA = ∑ Monto de ejecución de años anteriores` |
| Avance Anual Programado ($) | Es la suma de lo programado en cada uno de los cuatrimestres; esta información se captura de la pantalla de la Programación Anual de Preinversión de la Unidad Ejecutora (CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión"). | `AAP = PROGRAMADO CUATRIMESTRE I + PROGRAMADO CUATRIMESTRE II + PROGRAMADO CUATRIMESTRE III` |
| Avance Anual Ejecutado ($) | Es la suma del avance registrado en los cuatrimestres. | `AAE I = MONTO EJECUTADO CUATRIMESTRE I`; `AAE II = MONTO EJECUTADO CUATRIMESTRE I + MONTO EJECUTADO CUATRIMESTRE II`; `AAE III = MONTO EJECUTADO CUATRIMESTRE I + MONTO EJECUTADO CUATRIMESTRE II + MONTO EJECUTADO CUATRIMESTRE III` |
| Avance Anual Ejecutado (%) | Porcentaje acumulado de ejecución anual. | `AAE% I = (MONTO EJECUTADO CUATRIMESTRE I) / (MONTO PROGRAMADO CUATRIMESTRE I + MONTO PROGRAMADO CUATRIMESTRE II + MONTO PROGRAMADO CUATRIMESTRE III) * 100`; `AAE% II = (MONTO EJECUTADO CUATRIMESTRE I + MONTO EJECUTADO CUATRIMESTRE II) / (MONTO PROGRAMADO CUATRIMESTRE I + MONTO PROGRAMADO CUATRIMESTRE II + MONTO PROGRAMADO CUATRIMESTRE III) * 100`; `AAE% III = (MONTO EJECUTADO CUATRIMESTRE I + MONTO EJECUTADO CUATRIMESTRE II + MONTO EJECUTADO CUATRIMESTRE III) / (MONTO PROGRAMADO CUATRIMESTRE I + MONTO PROGRAMADO CUATRIMESTRE II + MONTO PROGRAMADO CUATRIMESTRE III) * 100` |
| Avance al Cuatrimestre ($) | Monto acumulado de ejecución del estudio del proyecto al cuatrimestre en vigencia. | `AaCE$ I = MONTO EJECUTADO CUATRIMESTRE I`; `AaCE$ II = MONTO EJECUTADO CUATRIMESTRE I + MONTO EJECUTADO CUATRIMESTRE II`; `AaCE$ III = MONTO EJECUTADO CUATRIMESTRE I + MONTO EJECUTADO CUATRIMESTRE II + MONTO EJECUTADO CUATRIMESTRE III` |
| Avance al Cuatrimestre Porcentaje Ejecutado (%) | Porcentaje acumulado de ejecución del estudio del proyecto al cuatrimestre en vigencia. | `AaCE% I = (MONTO EJECUTADO CUATRIMESTRE I) / (MONTO PROGRAMADO CUATRIMESTRE I) * 100`; `AaCE% II = (MONTO EJECUTADO CUATRIMESTRE I + MONTO EJECUTADO CUATRIMESTRE II) / (MONTO PROGRAMADO CUATRIMESTRE I + MONTO PROGRAMADO CUATRIMESTRE II) * 100`; `AaCE% III = (MONTO EJECUTADO CUATRIMESTRE I + MONTO EJECUTADO CUATRIMESTRE II + MONTO EJECUTADO CUATRIMESTRE III) / (MONTO PROGRAMADO CUATRIMESTRE I + MONTO PROGRAMADO CUATRIMESTRE II + MONTO PROGRAMADO CUATRIMESTRE III) * 100` |
| Avance del Cuatrimestre Programado (Monto) | Captura el dato de la programación financiera del CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión". | `AdCP$ = Captura de la programación Financiera del PAP, según el cuatrimestre` |
| Avance del Cuatrimestre Ejecutado (Monto) | El registro que la Unidad Ejecutora reporta como el monto ejecutado del estudio del proyecto del cuatrimestre. | `AdCE$ = Registro del Monto ejecutado del Estudio ($), por parte del Técnico URP en la pantalla del Anexo A.5` |
| Avance del Cuatrimestre Porcentaje Ejecutado (%) | El sistema calcula automáticamente el porcentaje de lo ejecutado según el registro del Técnico URP. | `AdCE% = (Monto del estudio ejecutado del cuatrimestre / Monto programado del estudio del cuatrimestre) * 100` |
| Total General | El sistema deberá realizar, en cada columna que contiene montos monetarios, la suma de todos los totales de cada estudio realizado para cada proyecto. | No especificado en el documento (no se transcribe una fórmula simbólica para este concepto, solo la descripción textual). |

> Nota de ambigüedad: en el texto original del PDF, los subíndices de las fórmulas de "Avance al Cuatrimestre" se muestran como "1", "1I" y "1II" en lugar de "I", "II" y "III"; se han transcrito en esta tabla como "I", "II" y "III" por consistencia con el resto de la sección, ya que el contexto (tres cuatrimestres) no deja duda sobre su significado, pero se advierte la diferencia de notación respecto al texto literal del documento.

**Origen:** No especificado en el documento.

## RN-F
**Descripción:** Sobre la sección "Avances reportados en cuatrimestres anteriores" (Anexo A.5). Esta sección deberá cumplir con lo siguiente:
- Cuando se registre el avance del Cuatrimestre I, no se mostrará esta sección.
- Cuando se registre el avance del Cuatrimestre II, solo se visualizará lo ejecutado en Cuatrimestre I.
- Cuando se registre el avance del Cuatrimestre III, se visualizará lo ejecutado en cuatrimestres I y II.
**Origen:** No especificado en el documento.

---

# Campos

## Pantalla "Avance de la ejecución financiera cuatrimestral del PAP" (Anexo A.1)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Unidad Ejecutora | El Técnico PRE podrá seleccionar según credenciales. Técnico URP según corresponda. | Selección | Selección | No especificado en el documento. | Según credenciales del Técnico URP (RN-A.a). | Editable: No (según Anexo B.1). |
| Año | Año para el cual se está ingresando la información que viene de la pantalla de acceso. | Selección | Selección | No especificado en el documento. | El año vigente (RN-A.a). | Editable: No (según Anexo B.1). Rango: desde 1999 hasta 10 años posteriores al año vigente (RN-A.a). |
| Período | Períodos de seguimiento específicos que se presentarán según el ejercicio fiscal seleccionado (cuatrimestral). | Texto | Texto | No especificado en el documento. | El cuatrimestre vigente (RN-A.a). | Editable: No (según Anexo B.1). |
| CUP | Código Único de Proyecto que se ha activado en la Programación Financiera (CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión"). | Numérico | Número | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Nombre del Proyecto | Nombre de proyecto que se ha activado en la Programación Financiera (CU-PRE-30). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Etapa | Proviene de la Programación Financiera del PAP (CU-PRE-30). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Costo de la etapa | Procede del campo Costo de la etapa. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Fuente de financiamiento | Proviene de la Programación Financiera del PAP (CU-PRE-30). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Ejecutado años anteriores | Procede del monto ejecutado en ejercicios anteriores. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance Anual Programado | Suma de los Montos Programados desde el Cuatrimestre I hasta el Cuatrimestre III. Proviene de la Programación Financiera del PAP (CU-PRE-30). El sistema deberá agregar el separador de miles (,). | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance Anual Ejecutado ($) | Suma de los Montos Ejecutados desde el Cuatrimestre I hasta el Cuatrimestre III (Avance del Cuatrimestre-$). El sistema deberá agregar el separador de miles (,). | Numérico | Numérico | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance Anual - Porcentaje Ejecutado (%) | Suma de los Montos porcentajes Ejecutados desde el Cuatrimestre I hasta el Cuatrimestre III (Avance del Cuatrimestre-%). | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance al Cuatrimestre Programado | Muestra la suma de los montos programados al Cuatrimestre según se programó en CU-PRE-30. Cuando se seleccione el Cuatrimestre I, el monto de este campo será equivalente a lo programado en el Cuatrimestre I. Cuando se seleccione el Cuatrimestre II se mostrará la suma de lo programado en los Cuatrimestres I y II. Cuando se seleccione el Cuatrimestre III se mostrará la suma de lo programado en los Cuatrimestre I, II y III. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance al cuatrimestre Ejecutado (monto) | Muestra la suma de los avances reportados en cada cuatrimestre desde el Anexo A6 (nota de ambigüedad: esta pantalla se identifica en los Flujos y mockups como "Anexo A.5" — ver Observaciones). Cuando se seleccione el Cuatrimestre I, el monto de este campo será equivalente a lo reportado como ejecutado en el Cuatrimestre I. Cuando se seleccione el Cuatrimestre II se mostrará la suma de lo ejecutado en los Cuatrimestres I y II. Cuando se seleccione el Cuatrimestre III se mostrará la suma de lo ejecutado en los Cuatrimestre I, II y III. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Avance al cuatrimestre porcentaje ejecutado (%) | Porcentaje resultante de calcular el Monto Ejecutado al Cuatrimestre entre el Monto Programado al Cuatrimestre. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance del Cuatrimestre Programado | Se captura monto programado del CU-PRE-30 del cuatrimestre según corresponda. | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance del cuatrimestre Ejecutado ($) | Técnico URP registra el monto ejecutado del cuatrimestre según corresponda en el Anexo A6 (nota de ambigüedad: ver Observaciones sobre "Anexo A6" vs "Anexo A.5"). El sistema deberá agregar el separador de miles (,). | Numérico | Numérico | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance del Cuatrimestre Porcentaje Ejecutado | Porcentaje resultante de calcular: Ejecutado/programado*100. | Porcentaje | Porcentaje | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Observaciones del Cuatrimestre | Procede de lo registrado en el campo "Observaciones" del Anexo A6 (nota de ambigüedad: ver Observaciones sobre "Anexo A6" vs "Anexo A.5"). | Texto | Texto | Sí (según Anexo B.1). | No especificado en el documento. | Editable: Sí (según Anexo B.1). Contradicción interna: la propia Descripción indica que este campo "procede de" otro Anexo (es decir, es un campo derivado/de solo consulta dentro de esta pantalla), pero la columna "Editable" indica "Sí" — ver Observaciones. |

## Pantalla "Seguimiento a la Programación Financiera por etapa de Preinversión" (denominada "Anexo A6" en el Anexo B.1 — ver Observaciones sobre la discrepancia con la numeración de mockups, donde esta pantalla se identifica como "Anexo A.5")

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Etapa | Procede del Anexo A1 del CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión". Campo Etapa. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Costo de la etapa | Procede del campo "Costo de la etapa" del CU-PRE-03.5 para la etapa correspondiente. Este campo será editable. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Programado Año | Procede del Anexo A1 del CU-PRE-30. Campo Total año. | Moneda | Moneda | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Fuente de financiamiento | Procede del Anexo A1 del CU-PRE-30. Campo Fuente de financiamiento. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Fuente de recursos | Procede del Anexo A1 del CU-PRE-30. Campo Fuente de recursos. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Convenio | Procede del Anexo A1 del CU-PRE-30. Campo Convenio. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Cuatrimestre (I, II) — Sección "Avance reportado en cuatrimestres anteriores" | Muestra el Cuatrimestre (I y II). | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Monto — Sección "Avance reportado en cuatrimestres anteriores" | Muestra el monto reportado como ejecutado para cada cuatrimestre. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Observaciones — Sección "Avance reportado en cuatrimestres anteriores" | Muestra las observaciones registradas en cada cuatrimestre. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Total — Sección "Avance reportado en cuatrimestres anteriores" | Muestra la suma de los cuatrimestres I y II. El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: No (según Anexo B.1). |
| Avance del cuatrimestre — Sección "Avance del cuatrimestre" | Campo para que el Técnico URP registre el avance del cuatrimestre. Este monto debe cumplir con las validaciones de RN-D a). El sistema deberá agregar el separador de miles (,). | Moneda | Moneda | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: Sí (según Anexo B.1). |
| Observaciones del cuatrimestre — Sección "Avance del cuatrimestre" | Campo para que el Técnico URP registre observaciones sobre el desarrollo del estudio en el cuatrimestre. | Texto | Texto | Sí (texto explícito: "Campo obligatorio.") | No especificado en el documento. | Editable: Sí (según Anexo B.1). |

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|---|---|---|
| Avance del cuatrimestre (Anexo A.5, Cuatrimestre I) | El avance del Cuatrimestre debe ser menor o igual al valor del campo "Total Año" del CU-PRE-30 (RN-D.a). | "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado" (texto de RN-D.a) / "Monto Ejecutado supera Monto Programado Anual" (mockup Anexo A.2 — ver Observaciones sobre la discrepancia). |
| Avance del cuatrimestre (Anexo A.5, Cuatrimestre II) | El avance del cuatrimestre debe ser menor o igual a: `Total anual − Avance del Cuatrimestre I` (RN-D.a). | Igual al anterior. |
| Avance del cuatrimestre (Anexo A.5, Cuatrimestre III) | El avance del cuatrimestre debe ser menor o igual a: `Total anual − Avance del Cuatrimestre I − Avance del Cuatrimestre II` (RN-D.a). | Igual al anterior. |
| Ejecutado (monto $, columnas Avance Anual y Avance al Cuatrimestre) | El acumulado no podrá superar el monto programado en la columna Costo de la Etapa, según la Programación Financiera registrada en el PAP (RN-B.b). | Alerta para revisión y ajuste del PAP vigente (Anexo A.2; sin texto literal adicional transcrito para este caso específico). |
| Ejecutado al cuatrimestre (por fuente de financiamiento) | La suma de los montos "ejecutado al cuatrimestre" no debe superar el monto "programado anual" por fuente de financiamiento (RN-D.b). | No especificado en el documento (mensaje literal no transcrito para este caso; presumiblemente el mismo del Anexo A.2). |
| Ingreso/ajuste de datos por el Técnico URP (fuera de fecha del Calendario de Eventos del PAP) | El sistema no debe permitir ajustes o ingreso de información fuera de las fechas establecidas en el Calendario de Eventos del PAP (RN-A.b). | Mensaje del Anexo A.3 (texto no citado literalmente en la RN-A.b; el mockup del Anexo A.3 muestra "Período de ingreso de información ha finalizado"). |

---

# Errores

| Código | Descripción | Acción esperada |
|---|---|---|
| No especificado en el documento. | "Monto Ejecutado supera Monto Programado Anual" (mockup Anexo A.2) / "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado" (texto de RN-D.a) — ver Observaciones sobre la discrepancia de redacción. | El registro se mantiene en la pantalla del Anexo A.5; el usuario debe ajustar el monto (RN-D.a, RN-B.b). |
| No especificado en el documento. | "Período de ingreso de información ha finalizado" (Anexo A.3). | Todas las acciones de las tablas de los Anexos A.1 y A.5 quedan deshabilitadas, a excepción del botón "Generar reporte" (RN-A.b). |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Técnico URP | Responsable del ingreso de la información (avance financiero cuatrimestral) | RN-A.c |
| Técnico URP | Tiene habilitado el botón "Generar reporte" | RN-A.c |
| Técnico URP | Tiene habilitado el botón "Seguimiento de Metas" (SF-2; ver Observaciones sobre su relación con el botón "Siguiente") | RN-A.c |
| Técnico PRE | Encargado de revisar la información | RN-A.c |
| Técnico PRE | Tiene habilitado el botón "Generar reporte" | RN-A.c |
| Técnico PRE | Tiene habilitado el botón "Seguimiento de Metas" | RN-A.c |
| Coordinador PRE, Coordinador PRO, Técnico PRO, Jefe DGI, Subjefe DGI | Solo derecho de consultar la información ingresada por el Técnico URP y el Técnico PRE; no verán ningún botón en el campo de acciones de la tabla, a excepción de "Generar reporte" y "Seguimiento de Metas" | RN-A.c |
| Todos los actores | Botón "Buscar": permanece siempre activo, independientemente del usuario (con la Unidad Ejecutora filtrada según credenciales) | RN-C |
| Todos los actores | Todas las acciones de las tablas de los Anexos A.1 y A.5 se deshabilitan fuera del período de ingreso, excepto "Generar reporte" | RN-A.b |

---

# Dependencias

**Casos de uso mencionados:**
- CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión" (precondición; fuente de la mayoría de los campos de las pantallas del Anexo A.1 y A.5)
- CU-ADM-04 "Gestión de Eventos de Calendario" (precondición; RN-A.b, RN-D.a)
- CU-PRE-03.5 (fuente del campo "Costo de la etapa" en el Anexo A.5/A6)
- CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP" (destino del SF-2; postcondición; **también es el caso de uso donde se gestiona la única solicitud de aprobación del Técnico URP y la única aprobación del Técnico PRE que cubren ambos casos de uso — decisión funcional v1.2, ver Observaciones**)
- CU-PRE-25 Elegibilidad (postcondición — ver Observaciones sobre esta relación inusual)

**Procesos relacionados:**
> No especificado en el documento (más allá de los casos de uso listados).

**Servicios externos:**
> No especificado en el documento.

---

# Pantallas

## Anexo A.1 — Pantalla de avance de la ejecución cuatrimestral del PAP-financiero

- **Nombre:** Pantalla de avance de la ejecución cuatrimestral del PAP-financiero.
- **Descripción:** Pantalla principal que muestra el listado de estudios/proyectos con su avance financiero (Avance Anual, Avance al Cuatrimestre y Avance del Cuatrimestre, cada uno con Programado, Ejecutado y Porcentaje), filtrable por Unidad Ejecutora, Año y Período.
- **Campos:** Unidad ejecutora (selector), Año (selector), Período (selector), y en la tabla: CUP, Nombre del proyecto, Etapa, Fuente de Financ., Ejecutado años anteriores, Avance Anual (Programado/Ejecutado/%), Avance al Cuatrimestre (Programado/Ejecutado/%), Avance del Cuatrimestre (Programado/Ejecutado/%), Observaciones del Cuatrimestre.
- **Botones:** "Buscar", "Generar Reporte" (da acceso al SF-3), "Siguiente" (da acceso al SF-2).
- **Acciones:** Seleccionar Unidad Ejecutora, Año y Período; buscar/filtrar; dar clic en el código de un proyecto para registrar su avance financiero (SF-1); generar reporte (SF-3); avanzar al Seguimiento de Metas Físicas (SF-2).

### Ejemplo de datos mostrados en el mockup (Anexo A.1)

**INFORME FINANCIERO DE AVANCE AL [Cuatrimestre] CUATRIMESTRE DEL PROGRAMA ANUAL DE PREINVERSION PUBLICA [Año]**

Unidad ejecutora: MINSAL | Año: 2025 | Periodo: II Cuatrimestre | Botón: Buscar

| CUP | NOMBRE DEL PROYECTO | ETAPA | FUENTE DE FINANC. | EJECUTADO AÑOS ANTERIORES | AVANCE ANUAL Programado | AVANCE ANUAL Ejecutado | AVANCE ANUAL % | AVANCE AL CUATRIMESTRE Programado | AVANCE AL CUATRIMESTRE Ejecutado | AVANCE AL CUATRIMESTRE % | AVANCE DEL CUATRIMESTRE Programado | AVANCE DEL CUATRIMESTRE Ejecutado | AVANCE DEL CUATRIMESTRE % | OBSERVACIONES DEL CUATRIMESTRE |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 8040 | Construcción de Unidades de Salud | | | 525,000.00 | 275,000.00 | 75,000.00 | 30.00% | 175,000.00 | 75,000.00 | 50.00% | 110,000.00 | 85,000.00 | 77.27% | |
| | | Diseño | | 500,000.00 | 250,000.00 | 75,000.00 | 30.00% | 150,000.00 | 75,000.00 | 50.00% | 100,000.00 | 75,000.00 | 75.00% | |
| | | | FOSEP | 500,000.00 | 250,000.00 | 75,000.00 | 30.00% | 150,000.00 | 75,000.00 | 50.00% | 100,000.00 | 75,000.00 | 75.00% | |
| | | | FGEN | 25,000.00 | 25,000.00 | | | 25,000.00 | | | 10,000.00 | 10,000.00 | 100.00% | |
| 8140 | Equipamiento de hospitales a nivel nacional | | | 0.00 | 200,000.00 | | | 75,000.00 | | | 75,000.00 | | | |
| | | Factibilidad | | 0.00 | 200,000.00 | | | 75,000.00 | | | 75,000.00 | | | |
| | | | FOSEP | 0.00 | 200,000.00 | | | 75,000.00 | | | 75,000.00 | | | |
| 8142 | Capacitación... | | | 0.00 | 2,500.00 | | | 2,500.00 | 2,500.00 | 100.00% | 0.00 | 0.00 | 0.00% | |
| | | Perfil | | 0.00 | 2,500.00 | | | 2,500.00 | 2,500.00 | 100.00% | 0.00 | 0.00 | 0.00% | |
| | | | FGEN | 0.00 | 2,500.00 | | | 2,500.00 | 2,500.00 | 100.00% | 0.00 | 0.00 | 0.00% | |
| 8150 | Construcción... | | | 100,000.00 | 350,000.00 | | | 225,000.00 | 150,000.00 | 66.67% | 100,000.00 | 25,000.00 | 25.00% | |
| | | Factibilidad | | 100,000.00 | 350,000.00 | | | 225,000.00 | 150,000.00 | 66.67% | 100,000.00 | 25,000.00 | 25.00% | |
| | | | FOSEP | 100,000.00 | 350,000.00 | | | 225,000.00 | 150,000.00 | 66.67% | 100,000.00 | 25,000.00 | 25.00% | |
| 8280 | Construcción... | | | 0.00 | 202,500.00 | 2,500.00 | 100.00% | 2,500.00 | 2,500.00 | 100.00% | 2,500.00 | 2,500.00 | 100.00% | |
| | | Perfil | | 0.00 | 2,500.00 | 2,500.00 | 100.00% | 2,500.00 | 2,500.00 | 100.00% | 2,500.00 | 2,500.00 | 100.00% | Perfil del proyecto finalizado; por presentarse para solicitud de Opinión Técnica |
| | | | FGEN | 0.00 | 2,500.00 | 2,500.00 | 100.00% | 2,500.00 | 2,500.00 | 100.00% | 2,500.00 | 2,500.00 | 100.00% | Perfil del proyecto finalizado; por presentarse para solicitud de Opinión Técnica |
| | | Diseño | | 0.00 | 200,000.00 | 0.00 | 0.00% | 0.00 | 0.00 | 0.00% | 0.00 | 0.00 | 0.00% | |
| | | | Préstamo Externo / BCIE / BCIE 2230 | 0.00 | 200,000.00 | 0.00 | 0.00% | 0.00 | 0.00 | 0.00% | 0.00 | 0.00 | 0.00% | |
| **TOTAL** | | | | **625,000.00** | **1,030,000.00** | **77,500.00** | | **480,000.00** | **230,000.00** | | **287,500.00** | **112,500.00** | | |

Botones: "Generar Reporte" (inferior izquierda), "Siguiente" (inferior derecha).

> **Corrección (v1.1):** la celda "Avance al Cuatrimestre — Ejecutado/%" de la fila total del proyecto 8040 se marcaba con "(*)" porque el mockup del PDF mostraba un texto superpuesto que impedía leer el valor con certeza. La hoja Excel "CUPRE-32 A.1" resuelve esta ambigüedad con el valor exacto: Ejecutado $75,000.00 y 50.00%, consistente con la suma de sus filas de detalle (Diseño: Ejecutado $75,000.00, 50.00%). Se corrige el valor en la tabla anterior; ya no se transcribe como ambiguo.

## Anexo A.2 — Monto Ejecutado supera al Monto Programado Anual

- **Nombre:** Monto Ejecutado supera al Monto Programado Anual.
- **Descripción:** Ventana emergente de error mostrada cuando el monto ejecutado del avance del cuatrimestre supera el monto anual programado (RN-B.b, RN-D.a).
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "Aceptar".
- **Acciones:** Aceptar el mensaje; el sistema mantiene al usuario en la pantalla del Anexo A.5.

### Ejemplo de datos mostrados en el mockup (Anexo A.2)

Ícono de error (X roja). Título: "Error". Texto: "Monto Ejecutado supera Monto Programado Anual". Botón: "Aceptar".

## Anexo A.3 — Periodo de ingreso de información ha finalizado

- **Nombre:** Periodo de ingreso de información ha finalizado.
- **Descripción:** Ventana emergente de error mostrada cuando el Técnico URP intenta ajustar o ingresar información fuera de las fechas establecidas en el Calendario de Eventos del PAP (RN-A.b).
- **Campos:** No aplica (mensaje informativo).
- **Botones:** "ACEPTAR".
- **Acciones:** Aceptar el mensaje.

### Ejemplo de datos mostrados en el mockup (Anexo A.3)

Ícono de error (X roja). Título: "Error". Texto: "Período de ingreso de información ha finalizado". Botón: "ACEPTAR".

## Anexo A.4 — Generar reporte

- **Nombre:** Generar reporte.
- **Descripción:** Ventana emergente para seleccionar el formato de exportación del reporte (SF-3).
- **Campos:** No aplica (selector de formato mediante iconos).
- **Botones:** Ícono "Excel", ícono "PDF".
- **Acciones:** Seleccionar el formato de exportación (Excel o PDF).

### Ejemplo de datos mostrados en el mockup (Anexo A.4)

Ícono de advertencia (signo de exclamación amarillo). Texto: "Seleccione formato". Íconos: "Excel" (verde) y "PDF" (rojo).

## Anexo A.5 — Seguimiento a la Programación Financiera por etapa de Preinversión

- **Nombre:** Seguimiento a la Programación Financiera por etapa de Preinversión.
- **Descripción:** Pantalla de registro del avance financiero cuatrimestral de un estudio, mostrando una columna por cada Etapa/Fuente de Financiamiento programada, con el histórico de avances de cuatrimestres anteriores y el campo de registro del avance del cuatrimestre en curso.
- **Campos:** Etapa, Costo de la etapa, Programado año, Fuente de Financiamiento, Fuente de Recursos, Convenio, Ejecutado años anteriores; sección "Avance reportado en cuatrimestres anteriores" (Cuatrimestre, Monto, Observaciones, Total); sección "Avance del cuatrimestre" (Avance del Cuatrimestre, Observaciones del Cuatrimestre).
- **Botones:** "Guardar" (uno por cada columna/etapa mostrada), "Salir".
- **Acciones:** Consultar el histórico de avances por cuatrimestre; registrar el avance del cuatrimestre en curso y sus observaciones; guardar o salir sin guardar.

> Nota de ambigüedad: el Anexo B.1 (Requerimientos Funcionales) denomina a esta misma pantalla "Anexo A6", mientras que el título del propio mockup y todas las referencias en los Flujos (FB, SF-1, RN-A.b, RN-D.a) la identifican consistentemente como "Anexo A.5". Ver Observaciones.

### Ejemplo de datos mostrados en el mockup (Anexo A.5)

**Seguimiento a la Programación Financiera por etapa de Preinversión (CUP - 8280)**

| | Columna izquierda (Etapa: Perfil) | Columna derecha (Etapa: Diseño) |
|---|---|---|
| Etapa | Perfil | Diseño |
| Costo de la etapa | $ 2,500.00 | $ 1,500,000.00 |
| Programado año | $ 2,500.00 | $ 200,000.00 |
| Fuente de Financiamiento | FGEN | Préstamo Externo |
| Fuente de Recursos | Fondo General | BCIE |
| Convenio | N/A | BCIE 2230 |
| Ejecutado años anteriores | $ - | $ - |

**Avance reportado en cuatrimestres anteriores (columna izquierda — Etapa Perfil):**

| Cuatrimestre | Monto | Observaciones |
|---|---|---|
| I | $500.00 | |
| II | $500.00 | |
| **TOTAL** | **$1,000.00** | |

**Avance del cuatrimestre (columna izquierda):**

Avance del Cuatrimestre: $ 1,500.00
Observaciones del Cuatrimestre: "Perfil del proyecto finalizado; por presentarse para solicitud de Opinión Técnica"

[Botón: Guardar]

**Avance reportado en cuatrimestres anteriores (columna derecha — Etapa Diseño):**

| Cuatrimestre | Monto | Observaciones |
|---|---|---|
| I | $0.00 | |
| II | $0.00 | |
| **TOTAL** | **$0.00** | |

**Avance del cuatrimestre (columna derecha):**

Avance del Cuatrimestre: (en blanco en el mockup)
Observaciones del Cuatrimestre: (en blanco en el mockup)

[Botón: Guardar]

[Botón: Salir] (común a ambas columnas)

## Anexo A.6 — Reporte del Avance Cuatrimestral Financiero del PAP

- **Nombre:** Reporte del Avance Cuatrimestral Financiero del PAP.
- **Descripción:** Reporte exportable (Excel o PDF) que corresponde a la pantalla del Anexo A.1, generado mediante el SF-3.
- **Campos:** Institución Ejecutora, Año, Cuatrimestre, la misma tabla de datos del Anexo A.1, y un campo adicional "Comentarios al reporte financiero DGICP".
- **Botones:** No especificado en el documento (el texto no aclara si conserva o no botones, a diferencia de lo indicado explícitamente para el reporte homólogo en CU-PRE-30/CU-PRE-31).
- **Acciones:** Generar el reporte en Excel o PDF (SF-3); consultar/imprimir el reporte; registrar comentarios.

### Ejemplo de datos mostrados en el mockup (Anexo A.6)

**INFORME DE AVANCE AL CUATRIMESTRE [Cuatrimestre] FINANCIERO DEL PROGRAMA ANUAL DE PREINVERSION PUBLICA [Año]**

Institución Ejecutora: [Nombre de la Institución Ejecutora]

(La tabla de datos es idéntica a la transcrita para el Anexo A.1, incluyendo la fila "TOTAL".)

Campo de texto: "Comentarios al reporte financiero DGICP:"

## Pantalla/Mensaje: "Las etapas no coinciden con la Ruta de Preinversión" (identificado únicamente en el anexo Excel — no documentado en el PDF)

> **Origen:** este mensaje de error no se identificó en el PDF fuente `CU-PRE-32_Avance_cuatrimestral_Financiero_del_PAP_F.pdf` ni en la versión anterior de este documento. Se encontró en la hoja Excel "CUPRE-32 A.6" (filas 48–51, repetido 4 veces en distintas celdas de la misma zona de la hoja), la cual corresponde al mockup ya documentado como "Anexo A.5" en este Markdown (ver la nota de discrepancia de numeración "Anexo A6" vs. "Anexo A.5"). El mismo texto exacto aparece también en las hojas Excel de los casos de uso relacionados CU-PRE-30 (documentado allí como Anexo A.7 "No puede saltarse la Ruta de Preinversión") y CU-PRE-31 (mencionado en RN-B de ese documento). No se confirma si este mensaje aplica efectivamente a CU-PRE-32, ya que ninguna Regla de Negocio ni paso de Flujo de este documento lo menciona.

**Descripción:** Mensaje de error, aparentemente de validación de que las etapas registradas coincidan con la Ruta de Preinversión del proyecto (por analogía con CU-PRE-30, Anexo A.7).

**Campos:** No aplica (mensaje informativo).

**Botones:** No especificado en el anexo Excel para este mensaje específico.

**Texto:** "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda"

**Acciones:** No especificado explícitamente en el documento fuente de este caso de uso.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|---|---|---|
| Error | "Monto Ejecutado supera Monto Programado Anual" (Anexo A.2) / "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado" (texto de RN-D.a) | Al dar clic en "Guardar" cuando el avance del cuatrimestre no cumple las condiciones de la RN-D.a, o cuando el acumulado ejecutado excede lo programado (RN-B.b). |
| Error | "Período de ingreso de información ha finalizado" (Anexo A.3) | Cuando el Técnico URP intenta ajustar o ingresar información fuera de las fechas establecidas en el Calendario de Eventos del PAP (RN-A.b). |
| Selección de formato | "Seleccione formato" (Anexo A.4, con íconos Excel/PDF) | Al dar clic en el botón "Generar Reporte" (SF-3). |
| Error (identificado únicamente en el anexo Excel, sin correspondencia en el PDF) | "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda" | No especificado en el documento fuente de este caso de uso; por analogía con CU-PRE-30 (Anexo A.7), podría ocurrir al intentar registrar una etapa fuera del orden de la Ruta de Preinversión en el Anexo A.5. Ver Observaciones y "Datos Pendientes de Definir". |

---

# Observaciones

1. El Flujo Básico del documento presenta una numeración irregular en sus pasos: el primer paso está numerado "1." (Actor), el segundo paso también está numerado "1." (Sistema, en lugar de "2."), y el tercer paso está numerado "4." (Actor, en lugar de "3."). Se han renumerado secuencialmente en este Markdown (1, 2, 3) únicamente para facilitar la lectura, preservando el contenido literal.

2. El SF-2 se titula "Botón Seguimiento de Metas" y la RN-A.c denomina a este control "botón 'Seguimiento de Metas'"; sin embargo, el paso 1 del propio SF-2 indica que la acción es dar clic en el botón "Siguiente". El documento no aclara si son el mismo botón con dos nombres, o si "Seguimiento de Metas" es solo el nombre descriptivo del subflujo (patrón similar al observado en el CU-PRE-30 relacionado, donde el botón "Programación de Metas" también se activa dando clic en "Siguiente").

3. El mensaje de error citado en la RN-D.a ("Error. El monto del avance del cuatrimestre no debe superar el monto anual programado") no coincide textualmente con el mensaje mostrado en el mockup del Anexo A.2 ("Monto Ejecutado supera Monto Programado Anual"). El documento no aclara cuál es el texto definitivo.

4. El Anexo B.1 (Requerimientos Funcionales) denomina "Anexo A6" a la pantalla "Seguimiento a la Programación Financiera por etapa de Preinversión" en múltiples ocasiones (incluyendo el propio encabezado de esa sección: "Del anexo A6 Seguimiento a la Programación Financiera por etapa de Preinversión"), mientras que el título del mockup y todas las referencias de los Flujos (FB paso 2, SF-1 paso 2, RN-A.b, RN-D.a) identifican esta misma pantalla consistentemente como "Anexo A.5". Este patrón de discrepancia de numeración entre el Anexo B y los mockups/flujos ya se había observado en el CU-PRE-31 relacionado (donde ocurría con "Anexo A.2" vs "Anexo A.4").

5. La postcondición "CU-PRE-25 Elegibilidad" resulta inusual: en otros casos de uso del mismo sistema (CU-PRE-26.5, CU-PRE-29, CU-PRE-30), "CU-PRE-25 Elegibilidad" se describe como una precondición del ciclo de preinversión, no como una postcondición de un caso de uso de seguimiento de ejecución financiera. El documento no explica esta relación.

6. La referencia a "CU-PRE-33 'Avance Cuatrimestral por Metas Físicas del PAP'" en la sección de Postcondiciones aparece incrustada dentro del texto del segundo punto ("Se guardaron con éxito los registros... CU-PRE-33..."), sin un ítem o viñeta propia, a diferencia del resto de referencias a casos de uso en el documento, que suelen tener su propio marcador.

7. El paso 2 del SF-3 ("Verifica el formato seleccionado y genera el reporte. Anexo A.1.") remite al "Anexo A.1", pero el reporte generado corresponde, según su propio encabezado y estructura, al mockup identificado como "Anexo A.6 Reporte del Avance Cuatrimestral Financiero del PAP", una pantalla distinta del Anexo A.1 (pantalla operativa con filtros y botones). El documento no aclara esta referencia.

8. El Historial de Revisiones del documento incluye una entrada de versión "2.0" fechada 5/12/2025 ("Área de Preinversión"), mientras que la portada y todos los encabezados de página del documento indican consistentemente "Versión: 1.0". El documento no aclara si el contenido analizado corresponde a la versión 1.0 (como indican los encabezados) o si debería reflejar cambios de la versión 2.0 registrada en el historial.

9. La RN-C establece que al dar clic en el botón "Buscar" "se generará el reporte de 'Seguimiento Financiero' y 'Seguimiento Físico'"; esto es inconsistente con el resto del documento, donde "Buscar" se describe como un filtro que muestra la tabla del Anexo A.1 (RN-A.a), mientras que "Generar Reporte" es el botón que efectivamente genera un reporte exportable (SF-3). Adicionalmente, "Seguimiento Físico" no se describe en ningún otro punto de este documento (CU-PRE-32), que trata exclusivamente el seguimiento financiero; el concepto de seguimiento físico corresponde al caso de uso relacionado CU-PRE-33.

10. **Resuelto (v1.1):** en el mockup del Anexo A.1, la celda de "Avance al Cuatrimestre — Ejecutado/%" de la fila total del proyecto 8040 mostraba un texto superpuesto ("Programado al cuatrimestre 50.00%") sobre el valor numérico, lo que antes impedía confirmar el porcentaje exacto. La hoja Excel "CUPRE-32 A.1" resuelve el valor: Ejecutado $75,000.00, 50.00%. Ver la nota correspondiente en la sección "Pantallas".

11. En el texto original del PDF, los subíndices de las fórmulas de "Avance al Cuatrimestre" en la RN-E se muestran como "1", "1I" y "1II" en lugar de "I", "II" y "III"; se transcribieron en la tabla de la RN-E con la notación "I", "II", "III" por consistencia, dado que el contexto no deja duda sobre su significado, pero se registra la diferencia de notación respecto al texto literal.

12. El campo "Observaciones del Cuatrimestre" de la pantalla del Anexo A.1 se describe en el Anexo B.1 con una Descripción que lo caracteriza como un campo derivado/de solo consulta ("Procede de lo registrado en el campo 'Observaciones' del Anexo A6"), es decir, un valor que el Sistema traslada automáticamente desde el Anexo A.5/A6 (consistente con el paso 5 del SF-1: "Coloca la información registrada en el campo 'Observaciones del Cuatrimestre' del Anexo A.5 en el campo 'Observaciones' del Anexo A.1"). Sin embargo, la columna "Editable" de ese mismo campo en el Anexo B.1 indica "Sí". El documento no aclara si el campo es editable directamente en el Anexo A.1 (lo cual contradiría su propia descripción como campo derivado) o si el valor "Sí" de la columna "Editable" se refiere en realidad a su condición de editable en el Anexo A.5/A6 (su origen), no en el Anexo A.1 donde se transcribe.

13. **Nuevo (v1.1):** la hoja Excel "CUPRE-32 A.6" (correspondiente al mockup documentado como Anexo A.5) contiene el mensaje "Las etapas no coinciden con las registradas en la Ruta de Preinversión. Revisar y ajustar según corresponda", que no aparece en ninguna parte del PDF fuente de este caso de uso ni se menciona en ninguna Regla de Negocio de este documento. El mismo mensaje sí está documentado en el caso de uso relacionado CU-PRE-30 (como Anexo A.7) y en el anexo Excel de CU-PRE-31. No se puede determinar si este mensaje aplica efectivamente a CU-PRE-32 (por ejemplo, si la pantalla del Anexo A.5 valida también el orden de las etapas contra la Ruta de Preinversión) o si es contenido heredado de una plantilla compartida entre los tres casos de uso sin aplicación real en este documento.

14. **Decisión funcional (v1.2, a solicitud del usuario, 31/08/2026 — no proveniente del PDF):** se confirma y documenta que CU-PRE-32 y CU-PRE-33 comparten una única solicitud de aprobación (Técnico URP → Técnico PRE) y una única aprobación ("Revisión Finalizada", Técnico PRE), ambas gestionadas en CU-PRE-33, sin que exista una solicitud/aprobación adicional o independiente propia de CU-PRE-32. El SF-2 de este documento ("Botón Seguimiento de Metas") es de navegación únicamente. Al revisar CU-PRE-33 para aplicar esta misma decisión, se identificó que el botón "Enviar a revisión DGICP" (RN-A.b de ese documento) no tiene, a diferencia del caso análogo de CU-PRE-31, un paso de Flujo/Subflujo propio que describa su activación; este hallazgo se documenta en CU-PRE-33 (ver su Observaciones y Datos Pendientes de Definir) y no afecta el contenido de CU-PRE-32. Ver también `nota_cambio_v1_2` en el Front Matter.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---|---|---|
| Proyecto/Estudio | Entidad principal identificada por su CUP, cargada desde el CU-PRE-30 (Programación Financiera). | Consulta del avance financiero (FB, Anexo A.1); registro del avance del cuatrimestre (SF-1, Anexo A.5). |
| Etapa de Preinversión / Fuente de Financiamiento | Fase del ciclo de preinversión de un estudio y su fuente de financiamiento, recursos y convenio asociados (procedentes del CU-PRE-30). | Consulta (Anexo A.1, Anexo A.5); base para el cálculo de avances (RN-E). |
| Avance Cuatrimestral (Anual, al Cuatrimestre, del Cuatrimestre) | Conjunto de valores Programado/Ejecutado/Porcentaje calculados para cada proyecto, etapa y fuente, en distintos niveles de acumulación temporal. | Registro manual del "Avance del Cuatrimestre" por el Técnico URP (SF-1 paso 3; Anexo A.5); cálculo automático de los demás valores acumulados y porcentajes (RN-E); validación de que lo ejecutado no supere lo programado (RN-B.b, RN-D.a). |
| Observaciones del Cuatrimestre | Comentarios registrados por el Técnico URP sobre el avance de un cuatrimestre específico. | Registro manual (SF-1 paso 3; Anexo A.5); traslado al campo "Observaciones" del Anexo A.1 al guardar (SF-1 paso 5). |
| Reporte del Avance Cuatrimestral Financiero | Documento exportable con el detalle del avance financiero de todos los estudios de una Unidad Ejecutora/Año/Período. | Generación en formato Excel o PDF (SF-3; Anexo A.6); registro de comentarios ("Comentarios al reporte financiero DGICP", Anexo A.6). |

---

# Catálogos Detectados

> El documento no incluye catálogos propios de este caso de uso (p. ej. Fuentes de Financiamiento, Fuentes de Recursos o Convenios); todos los campos relacionados con dichos catálogos remiten directamente a la información ya registrada en CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión" (y, transitivamente, a CU-PRE-17 "Presupuesto de inversión"). No se transcribe un catálogo adicional en este documento para evitar duplicar información no verificable en esta fuente.

---

# Eventos del Sistema

| Evento | Origen | Destino |
|---|---|---|
| Visualización de la pantalla de avance según Unidad Ejecutora, Año y Período seleccionados | Sistema | Pantalla "Pantalla de avance de la ejecución cuatrimestral del PAP-financiero" (Anexo A.1) (FB, paso 2) |
| Traslado del monto registrado en "Avance del Cuatrimestre" a la columna "Ejecutado" del Anexo A.1 | Sistema | Anexo A.1 (SF-1, paso 5, al Guardar) |
| Traslado de "Observaciones del Cuatrimestre" al campo "Observaciones" del Anexo A.1 | Sistema | Anexo A.1 (SF-1, paso 5, al Guardar) |
| Cálculo automático de acumulados y porcentajes (Avance Anual, al Cuatrimestre) | Sistema | Anexo A.1 (RN-E) |
| Generación de alerta cuando el monto ejecutado excede lo programado | Sistema | Anexo A.2 (RN-B.b) |
| Generación del reporte del Avance Cuatrimestral Financiero | Sistema | Anexo A.6 (SF-3) |
| Deshabilitación de las acciones de las tablas de los Anexos A.1 y A.5 (excepto "Generar reporte") | Sistema | Técnico URP (RN-A.b, fuera de fecha del Calendario de Eventos del PAP) |
| Navegación al Anexo A.1 del CU-PRE-33 | Sistema | Técnico URP (SF-2) |

---

# Integraciones

> No especificado en el documento. El documento no describe integraciones con sistemas externos para este caso de uso; únicamente hace referencia a otros casos de uso internos del mismo sistema (SIIP) — ver sección "Dependencias".

---

# Datos Pendientes de Definir

1. No se aclara si el botón "Seguimiento de Metas" (SF-2, RN-A.c) y el botón "Siguiente" (paso 1 del propio SF-2) son el mismo control (ver Observaciones, numeral 2).
2. No se aclara cuál es el texto definitivo del mensaje de error para el avance del cuatrimestre que supera el monto anual programado (RN-D.a dice "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado"; el mockup del Anexo A.2 muestra "Monto Ejecutado supera Monto Programado Anual") (ver Observaciones, numeral 3).
3. No se aclara la discrepancia de numeración entre "Anexo A6" (usado en el Anexo B.1 para la sección de campos de seguimiento por etapa) y "Anexo A.5" (usado en los Flujos y mockups para la misma pantalla) (ver Observaciones, numeral 4).
4. No se explica la relación entre "CU-PRE-25 Elegibilidad" y este caso de uso, dado que se lista como postcondición en lugar de precondición, a diferencia de su tratamiento en otros casos de uso del mismo sistema (ver Observaciones, numeral 5).
5. No se aclara a qué corresponde exactamente la referencia "Anexo A.1" en el paso 2 del SF-3 ("Generar Reporte"), dado que el reporte generado parece corresponder al mockup del Anexo A.6 (ver Observaciones, numeral 7).
6. No se aclara si el contenido de este documento corresponde a la versión 1.0 (indicada en portada y encabezados) o a la versión 2.0 registrada en el Historial de Revisiones (ver Observaciones, numeral 8).
7. No se aclara la relación entre el botón "Buscar" (descrito en RN-C como generador de los reportes de "Seguimiento Financiero" y "Seguimiento Físico") y el botón "Generar Reporte" (SF-3), ni qué es exactamente el "Seguimiento Físico" dentro de este caso de uso (ver Observaciones, numeral 9).
8. **Resuelto (v1.1):** el valor exacto de la celda "Avance al Cuatrimestre — Ejecutado/%" de la fila total del proyecto 8040 en el mockup del Anexo A.1 (antes ambiguo por un texto superpuesto) se confirmó mediante la hoja Excel "CUPRE-32 A.1": Ejecutado $75,000.00, 50.00% (ver Observaciones, numeral 10).
9. No se especifica si el reporte del Anexo A.6 conserva o no botones al generarse (a diferencia de los reportes homólogos de CU-PRE-30/CU-PRE-31, donde se aclara explícitamente que el reporte "corresponde a la pantalla... sin los botones").
10. No se aclara si el campo "Observaciones del Cuatrimestre" del Anexo A.1 es directamente editable en esa pantalla o si el valor "Sí" de su columna "Editable" (Anexo B.1) se refiere a su condición en el Anexo A.5/A6, dado que su propia Descripción lo caracteriza como un campo derivado/de consulta que "procede de" ese otro Anexo (ver Observaciones, numeral 12).
11. Prioridad (importancia) del propio caso de uso no especificada.
12. El documento no incluye una sección explícita de "Excepciones" con Código/Descripción/Consecuencia.
13. **✅ Resuelto (decisión funcional v1.2, a solicitud del usuario, no proveniente del PDF):** se confirmó que CU-PRE-32 no tiene ni tendrá un flujo propio de solicitud/aprobación; este se gestiona de forma unificada en CU-PRE-33 (ver Observaciones, numeral 14). Como parte de esta verificación se identificó un vacío distinto, no atribuible a este documento: en CU-PRE-33, el botón "Enviar a revisión DGICP" (RN-A.b) no tiene un paso de Flujo/Subflujo propio — ver Datos Pendientes de Definir de CU-PRE-33.
13. **Nuevo (v1.1):** no se puede determinar si el mensaje "Las etapas no coinciden con las registradas en la Ruta de Preinversión..." (identificado únicamente en la hoja Excel "CUPRE-32 A.6") aplica efectivamente a este caso de uso, dado que ninguna Regla de Negocio ni paso de Flujo del PDF lo menciona, ni se especifica en qué momento del Anexo A.5 se mostraría (ver Observaciones, numeral 13).
14. **Resuelto (RQ-C-01, Ronda 5) — no aplica:** el negocio confirmó que este documento no tiene campos de comentarios/observaciones, por lo que la pregunta sobre una capacidad adicional del Coordinador PRE equivalente a "Revisión Finalizada" no aplica aquí. El alcance del Coordinador PRE se mantiene sin cambios (consulta, RN-A.c). La misma pregunta sí aplicó y se resolvió en CU-PRE-31 y CU-PRE-33. Ver nota en Actores Secundarios.