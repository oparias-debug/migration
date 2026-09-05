---
id: CU-PRE-01.5
codigo: CU-PRE-01.5
nombre: Revisión y Emisión de CUP
modulo: Preinversión
submodulo: Formulación del Proyecto
version: 1.2
fuente_pdf: CU-PRE-01_Registro_de_Proyectos__JUL_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 19

nota_version: >
  Este documento es nuevo y se desprende de la división del caso de uso
  original CU-PRE-01 "Registro de Proyectos" (v1.0), con el fin de asignar
  un actor principal único al bloque de actividades que en el documento
  original correspondían al "Técnico PRE". Todo el contenido aquí descrito
  proviene del documento fuente original; no se introducen requerimientos
  nuevos, solo se reorganiza y se le asigna una identidad propia como caso
  de uso. Ver también CU-PRE-01 "Registro y Solicitud de CUP" (contraparte,
  actor Técnico URP) y CU-PRE-02 "Bandeja Preinversión" (actor Coordinador
  PRE, quien asigna la solicitud al Técnico PRE antes de que este caso de
  uso inicie).
  Versión 1.1: se incorporan las resoluciones de negocio de la Ronda 2
  (RQ-C-01, RQ-C-02) sobre las contradicciones de estados heredadas de
  CU-PRE-01 y señaladas en RN 3: la definición de estados de este
  documento queda confirmada como vigente.
  Versión 1.2: se incorpora anotación directa de especialista de dominio
  sobre el destino del proyecto tras la emisión del CUP (SF-2): el
  proyecto ingresa inmediatamente al banco de proyectos, y se aclara el
  rol de CU-PRE-29 como caso de uso de búsqueda de proyectos con CUP.

actor_principal: ["Técnico PRE"]

actores_secundarios: ["Técnico URP", "Coordinador PRE"]

prioridad: No especificado en el documento original.

estado: Analizado

depende_de: [CU-PRE-02 Bandeja Preinversión]

casos_relacionados: [CU-PRE-01, CU-PRE-29]

roles: [Técnico PRE]

pantallas: [Nuevo Registro (sección Revisión PRE)]

procesos: []

servicios_externos: [Correo electrónico / otro tipo de mensajería]

entidades: [Proyecto, Comentario, CUP]

catalogos: []

palabras_clave: [Revisión CUP, Emisión de CUP, Técnico PRE, Preinversión, DGICP, Observaciones, Banco de proyectos]

ultima_actualizacion: AGO 2025 (documento nuevo, derivado de CU-PRE-01 v1.0); corrección Ronda 2 (RQ-C-01, RQ-C-02) aplicada AGO 2026; anotación de especialista sobre banco de proyectos (CU-PRE-29) aplicada AGO 2026.
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Revisión y Emisión de CUP |
| Código | CU-PRE-01.5 |
| Módulo | Preinversión |
| Fuente | CU-PRE-01_Registro_de_Proyectos__JUL_2025_V1_F.pdf (documento original CU-PRE-01, dividido) |
| Versión | 1.2 (resolución de negocio Ronda 2 — RQ-C-01, RQ-C-02 — aplicada sobre RN 3; anotación de especialista sobre banco de proyectos) |

---

# Objetivo

Permitir al Técnico PRE revisar una solicitud de CUP asignada, registrar observaciones cuando la información no sea satisfactoria, y emitir el Código Único de Proyecto (CUP) cuando la solicitud sea procedente.

> Nota: el documento fuente original no declaraba un "Objetivo" explícito; este texto es una síntesis del contenido de la sección "Descripción" del documento original, acotada al alcance de este caso de uso tras la división.

---

# Descripción

Este caso de uso permite al actor "Técnico PRE":

1. Revisar la información registrada por el Técnico URP en una solicitud de CUP que le ha sido asignada por el Coordinador PRE (CU-PRE-02).
2. Digitar comentarios en la sección "Revisión PRE" de la pantalla "Nuevo Registro" y devolver la solicitud al actor "Técnico URP" para ajustes.
3. Emitir el Código Único de Proyecto (CUP) solicitado.

Este caso de uso se ejecuta sobre la misma pantalla "Nuevo Registro" (Anexo A.2) descrita en CU-PRE-01 "Registro y Solicitud de CUP", pero habilitando únicamente la porción de la sección "Revisión Área de Preinversión" correspondiente al Técnico PRE, más los botones "Devolver" y "Emitir CUP". El registro de la información básica del proyecto (nombre, monto, sector, etc.) no es editable por este actor y se documenta en CU-PRE-01.

Una vez emitido el CUP, el proyecto ingresa inmediatamente al banco de proyectos, donde queda disponible para su búsqueda mediante CU-PRE-29 "Banco de proyectos" (ver SF-2 y RN 2, punto 8).

---

# Actor Principal

Técnico PRE

> Nota: el documento original no especificaba cuál de los dos actores ("Técnico URP" o "Técnico PRE") era el actor principal. Al dividir el caso de uso, "Técnico PRE" queda establecido como actor principal de este documento, coherente con el enlace ya existente en CU-PRE-02 (Flujo Alterno FA-01), que asume que el Técnico PRE ejecuta una acción propia al dar clic en el caso asignado.

---

# Actores Secundarios

- Técnico URP — actor de la solicitud original; recibe las observaciones y responde a ellas en CU-PRE-01. Su respuesta (botón "Enviar") reactiva este caso de uso para una nueva revisión.
- Coordinador PRE — actor principal de CU-PRE-02, responsable de asignar la solicitud al Técnico PRE antes de que este caso de uso pueda ejecutarse, y receptor de la notificación de solicitud de CUP (Anexo A.3.1).

---

# Disparador

Asignación de una solicitud de CUP al Técnico PRE por parte del Coordinador PRE en CU-PRE-02 "Bandeja Preinversión" (Flujo Básico, paso 4.a de CU-PRE-02), seguida del ingreso del Técnico PRE a la Bandeja Preinversión y clic sobre el caso asignado (CU-PRE-02, FA-01).

---

# Precondiciones

1. Debe existir una solicitud de CUP registrada mediante CU-PRE-01 "Registro y Solicitud de CUP" (proyecto en estado "Enviado a DGICP (Registro)").
2. La solicitud debe haber sido asignada al Técnico PRE por el Coordinador PRE en CU-PRE-02 "Bandeja Preinversión".

> Nota: la precondición 2 no aparece de forma explícita en el documento original de CU-PRE-01 v1.0 (que indicaba "N/A"); se incorpora aquí porque es un requisito lógico ya documentado en CU-PRE-02 (FA-01) para que el Técnico PRE pueda acceder al caso.

---

# Flujo Principal

1. Técnico PRE ingresa a la Bandeja Preinversión (CU-PRE-02) y da clic en el caso que le ha sido asignado.
2. Sistema muestra la pantalla "Nuevo Registro" (Anexo A.2 de CU-PRE-01), con la sección "Revisión PRE" habilitada para el Técnico PRE.
3. Técnico PRE revisa la información registrada por el Técnico URP.
4. Técnico PRE selecciona uno de los siguientes subflujos: SF-1 Devolver, SF-2 Emitir CUP.

Caso de uso termina.

---

# Flujos Alternos

## SF-1 Devolver

1. Técnico PRE digita observaciones en el campo "Comentarios" de la sección "Revisión PRE" de la pantalla "Nuevo Registro".
2. Técnico PRE da clic en el botón "Devolver" (RN-2.7).
3. Sistema cambia el estado del proyecto a "Observado DGICP (Registro)".
4. Sistema informa al Técnico URP, por medio de correo electrónico u otro tipo de mensajería, el envío de observaciones (modelo de correo Anexo A.3.2).
5. Sistema pasa a la pantalla "Nuevo registro" y habilita, para el Técnico URP, el campo "Respuesta" en CU-PRE-01.
6. **[Punto de traspaso a CU-PRE-01]** — El Técnico URP ajusta la información y/o responde en el campo "Respuesta" y da clic en "Enviar" (CU-PRE-01, SF-1.2). Esta acción reactiva el presente caso de uso, retornando al paso 3 del Flujo Principal para una nueva revisión.

Subflujo Termina.

## SF-2 Emitir CUP

1. Técnico PRE da clic en el botón "Emitir CUP" (RN-2.8).
2. Sistema asigna el CUP: un código consecutivo numérico de 5 dígitos, partiendo desde el número 10,000 en adelante (nota del documento original: se consideró partir desde 10,000 dado que el sistema SIIP actual ya tiene registros hasta aproximadamente 9,000). En la asignación se salta un número cada 53 códigos emitidos (ejemplo del documento original: se saltan los números "10053", "10106", "10159", en adelante; dichos números quedan disponibles).
3. Sistema informa al Técnico URP, por medio de correo electrónico u otro tipo de mensajería, la asignación del código (modelo de correo Anexo A.3.4).
4. Sistema cambia el estado del proyecto a "CUP asignado" y lo envía a la pantalla "Captura de Proyectos" (UC-PRE-03), donde el proyecto con el código asignado podrá ser visualizado en el listado correspondiente.
5. **El proyecto ingresa inmediatamente al banco de proyectos**, quedando disponible para su búsqueda a través de CU-PRE-29 "Banco de proyectos".
6. Sistema pasa a la pantalla "Nuevo registro" y en la tabla de origen (Registro de Proyecto, CU-PRE-01) aparecerá una fila con el proyecto en estado "CUP asignado".

Subflujo Termina.

---

# Excepciones

No especificado en el documento original.

---

# Postcondiciones

1. CU-PRE-01 Registro y Solicitud de CUP (retorno del proyecto con observaciones, si se ejecutó SF-1 Devolver).
2. UC-PRE-03 Captura de proyectos (si se ejecutó SF-2 Emitir CUP).
3. CU-PRE-3.5 Selección y registro de etapas
4. CU-PRE-24 Viabilidad
5. CU-PRE-25 Elegibilidad
6. CU-PRE-26 Opinión Técnica
7. CU-PRE-29 Banco de proyectos — el proyecto ingresa **inmediatamente** al banco de proyectos al momento de emitirse el CUP (SF-2), quedando disponible para su búsqueda a través de este caso de uso.
8. CU-OYM-01 Listado de Proyectos Finalizados
9. CU-MPD-01 Registro del Contenido del Convenio

> Las postcondiciones 3 a 9 se heredan del documento original CU-PRE-01 v1.0 y se mantienen aquí porque dependen de la emisión del CUP (SF-2), que ahora es una acción propia de este caso de uso.

---

# Reglas de Negocio

> Se reproducen aquí las reglas del documento original CU-PRE-01 v1.0 que rigen específicamente la actuación del Técnico PRE. Las reglas transversales (estados, administración, mensajes de ayuda) se documentan también en CU-PRE-01 "Registro y Solicitud de CUP" para evitar que su lectura quede incompleta en ninguno de los dos documentos.

### RN 1 – Actores / Roles (Técnico PRE)

**Técnico PRE:**
a) Únicamente podrá acceder a consultar la información de los registros, sin permisos de editar (a excepción del campo "Comentarios" de la sección "Revisión PRE" de la pantalla "Nuevo registro").
b) Podrá visualizar la información de todas las Unidades Ejecutoras en las pantallas "Registro de Proyecto" y "Nuevo registro".

Origen: Sección 5, RN 1, punto 2 (documento original CU-PRE-01 v1.0).

---

### RN 2 – Condiciones de Pantalla (acciones del Técnico PRE)

**Pantalla "Nuevo Registro", Sección "Revisión PRE"**

7. Botón "Devolver":
   a) Visible solo para el actor "Técnico PRE".
   b) Al dar clic en el botón el Sistema informa al "Técnico URP", por medio de correo electrónico u otro tipo de mensajería, el envío de observaciones a los registros (ver modelo de correo en Anexo A.3.2).
   c) Se pasa a la pantalla "Nuevo registro".
8. Botón "Emitir CUP":
   a) Visible solo para el actor "Técnico PRE".
   b) Al dar clic en el botón el Sistema informa al "Técnico URP", por medio de correo electrónico u otro tipo de mensajería, la asignación del código (ver modelo del correo en Anexo A.3.4), el proyecto con el código asignado podrá ser visualizado en el listado de la pantalla "Captura de Proyectos" (UC-PRE-03), e ingresa inmediatamente al banco de proyectos, quedando disponible para su búsqueda mediante CU-PRE-29 "Banco de proyectos".
   c) La estructura del CUP será un consecutivo numérico de 5 dígitos partiendo desde el número 10,000 en adelante (NOTA del documento original: se ha considerado partir desde el número 10,000 con el nuevo sistema, dado que ya hay registros de proyectos en el SIIP actual hasta aproximadamente 9,000).
   d) Se pasa a la pantalla "Nuevo registro" y en la tabla aparecerá una fila con el proyecto en estado "CUP asignado".

Origen: Sección 5, RN 2, puntos 7 y 8 (documento original CU-PRE-01 v1.0). El punto 8.b se complementa con anotación de especialista de dominio sobre el ingreso inmediato al banco de proyectos.

---

### RN 3 – Estados (relevante para este caso de uso)

- **Enviado a DGICP (Registro):** permanecerá hasta que el "Técnico PRE" dé clic al botón "Emitir CUP" o se devuelva al actor "Técnico URP" al dar clic al botón "Devolver".
- **Observado DGICP (Registro):** aparecerá cuando el "Técnico PRE" dé clic al botón "Devolver" y permanecerá hasta que se atiendan las observaciones en CU-PRE-01.
- **CUP asignado:** aparecerá cuando la DGICP (Técnico PRE) asigne el Código Único de Proyecto a través del botón "Emitir CUP".

> ✅ **RESUELTO [RQ-C-01]** (Ronda 2 — respuesta del negocio: opción A): se confirma como vigente la definición de este documento — el estado "Enviado a DGICP (Registro)" permanece igual durante toda la revisión hasta que el "Técnico PRE" la resuelve (clic en "Emitir CUP" o en "Devolver"). Se descarta la definición de CU-PRE-02 (estado vigente solo hasta la asignación del Coordinador PRE); esa corrección se aplica en CU-PRE-02, no en este documento.

> ✅ **RESUELTO [RQ-C-02]** (Ronda 2 — respuesta del negocio: opción A): se confirma como oficial la redacción de nombres de estado usada en este documento ("Enviado a DGICP (Registro)", "Observado DGICP (Registro)"), heredada de CU-PRE-01. Se descartan las redacciones divergentes usadas en UC-PRE-03; esa corrección se aplica en UC-PRE-03, no en este documento.

Ver texto completo de RN 3 (todos los estados) en CU-PRE-01 "Registro y Solicitud de CUP".

Origen: Sección 5, RN 3 (documento original CU-PRE-01 v1.0).

---

### RN 4 – Administración (relevante para este caso de uso)

- El proceso de creación de CUP se hace por una única vez en todo el horizonte del proyecto.
- El sistema asignará el CUP de manera consecutiva según RN 2.8.c (ver arriba), y en la asignación de los códigos se saltará un número cada 53 códigos emitidos. Ejemplo: se saltarán los números "10053", "10106", "10159", en adelante. Dichos números quedarán disponibles.

Ver texto completo de RN 4 (incluye reglas de administración del Técnico URP y del administrador DGICP) en CU-PRE-01 "Registro y Solicitud de CUP".

Origen: Sección 5, RN 4 (documento original CU-PRE-01 v1.0).

---

### RN 6 – Sobre sección "Revisión Área de Preinversión" (lado Técnico PRE)

En la sección "Revisión área de preinversión" la parte de la izquierda (comentarios/fecha del comentario/devolver/emitir CUP) solo la diligencia el "Técnico PRE". El Técnico PRE puede ver la parte derecha (Respuesta/fecha de la respuesta/enviar, editable en CU-PRE-01) pero no se le habilitan esos campos.

Origen: Sección 5, RN 6 (documento original CU-PRE-01 v1.0).

---

### RN 7 – Ingreso al Banco de Proyectos (anotación de especialista de dominio)

Al emitirse el CUP (SF-2, botón "Emitir CUP"), el proyecto ingresa **inmediatamente** al banco de proyectos. Esto habilita su búsqueda a través de CU-PRE-29 "Banco de proyectos", el cual actúa como caso de uso de consulta/búsqueda de proyectos que ya cuentan con CUP asignado.

Origen: Anotación directa de especialista de dominio sobre este caso de uso.

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Comentarios | Será visible para todos los usuarios. Editable solo para el "Técnico PRE", habilitado cuando el "Técnico URP" dé clic al botón "Solicitar CUP" (en CU-PRE-01). El Sistema agregará la fecha en que se registre cada comentario, bajo el formato DD/MM/AAAA. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | Editable condicional (solo Técnico PRE). |

> El resto de los campos de la pantalla "Nuevo Registro" (información del proyecto) no son editables por el Técnico PRE y se documentan en CU-PRE-01. El campo "Respuesta" (editable por el Técnico URP) también se documenta en CU-PRE-01.

---

# Validaciones

No especificado en el documento original para las acciones propias de este caso de uso (los botones "Devolver" y "Emitir CUP" no tienen validaciones de campo documentadas más allá de que el campo "Comentarios" no es obligatorio según el documento fuente).

---

# Errores

No especificado en el documento original.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|-----------------|
| Técnico PRE | Consultar la información de los registros, sin permisos de edición (excepto campo "Comentarios"). | RN 1.2.a. |
| Técnico PRE | Visualizar la información de todas las Unidades Ejecutoras. | RN 1.2.b. |
| Técnico PRE | Ver y usar los botones "Devolver" y "Emitir CUP". | RN 2, puntos 7 y 8. |

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01 Registro y Solicitud de CUP (origen de la solicitud; destino del retorno con observaciones)
- CU-PRE-02 Bandeja Preinversión (asignación previa de la solicitud por el Coordinador PRE; FA-01)
- UC-PRE-03 Captura de proyectos (destino tras la emisión del CUP)
- CU-PRE-29 Banco de proyectos (destino inmediato tras la emisión del CUP; caso de uso mediante el cual se buscan los proyectos con CUP asignado)

**Procesos relacionados:**
No especificado en el documento original.

**Servicios externos:**
- Correo electrónico / otro tipo de mensajería (notificación de observaciones al Técnico URP, y notificación de asignación de CUP al Técnico URP).

---

# Pantallas

## Pantalla "Nuevo Registro" (Anexo A.2 de CU-PRE-01) — sección "Revisión PRE"

- **Nombre:** Nuevo Registro (vista/acceso del Técnico PRE)
- **Descripción:** Misma pantalla física documentada en CU-PRE-01, pero en este caso de uso solo se habilitan para el Técnico PRE el campo "Comentarios" y los botones "Devolver" y "Emitir CUP" de la sección "Revisión Área de Preinversión". El resto de los campos de la pantalla se muestran en modo de solo lectura para este actor.
- **Botones (alcance de este documento):** "Devolver", "Emitir CUP".
- **Acciones:** descritas en los subflujos SF-1 y SF-2 de este documento.

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|-----------------|
| Correo electrónico | Modelo A.3.2 "Envío de observaciones del Técnico PRE al Técnico URP". | Al dar clic en el botón "Devolver". |
| Correo electrónico | Modelo A.3.4 "Asignación de Código Único de Proyecto". | Al dar clic en el botón "Emitir CUP". |

---

# Observaciones

- Este documento es el resultado de dividir el CU-PRE-01 original (v1.0). Toda la información aquí presentada proviene íntegramente de ese documento fuente; no se agregó ningún requerimiento nuevo, solo se reorganizó y se le dio identidad propia como caso de uso con actor principal "Técnico PRE".
- La codificación "CU-PRE-01.5" es una propuesta de trabajo (sigue la convención decimal ya usada en "CU-PRE-3.5" dentro del mismo conjunto de documentos) y debe confirmarse/formalizarse con el equipo de negocio o de gestión documental antes de su uso definitivo en matrices de trazabilidad.
- Este caso de uso resuelve la inconsistencia observada originalmente en CU-PRE-02 (FA-01), donde el enlace "Técnico PRE ingresa... Sistema muestra pantalla Nuevo Registro del CU-PRE-01" apuntaba a un caso de uso cuyo actor principal formal (tras esta división) es el Técnico URP. Se recomienda actualizar la referencia de FA-01 en CU-PRE-02 para que apunte a este documento (CU-PRE-01.5) en lugar de CU-PRE-01.
- Las contradicciones [C-01] y [C-05] señaladas en la sección "Reglas de Negocio – RN 3", heredadas de CU-PRE-01, fueron **resueltas en la Ronda 2** (RQ-C-01, RQ-C-02): la definición de estados y la redacción de sus nombres, tal como están documentadas en este CU-PRE-01.5 (heredadas de CU-PRE-01), quedan confirmadas como vigentes/oficiales. Ver las anotaciones de resolución en RN 3. Las correcciones correspondientes en CU-PRE-02 (RQ-C-01) y en UC-PRE-03 (RQ-C-02) se aplican en esos documentos, no en este.
- El documento original no aclaraba si, tras un ciclo de "Devolver" → "Enviar" (respuesta del Técnico URP), el Técnico PRE puede volver a devolver la solicitud múltiples veces o si existe un límite de ciclos. Este documento asume, por lectura del flujo, que el ciclo puede repetirse indefinidamente hasta la emisión del CUP; se recomienda confirmar con el negocio.
- **Anotación de especialista (AGO 2026):** al emitirse el CUP, el proyecto ingresa inmediatamente al banco de proyectos, lo que confirma que CU-PRE-29 "Banco de proyectos" es el caso de uso mediante el cual se buscan los proyectos que ya cuentan con CUP. Esta anotación se aplicó en SF-2 (paso 5), RN 2.8.b, la nueva RN 7, Postcondiciones (ítem 7), Dependencias e Integraciones. Dado que CU-PRE-29 es un documento propio, se recomienda que el Gestor de Requisitos verifique si esta misma precisión (ingreso inmediato al banco de proyectos al emitirse el CUP) debe reflejarse también dentro de CU-PRE-29.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Comentario | Observaciones registradas por el Técnico PRE en la sección "Revisión PRE". | Crear, Leer |
| Proyecto | Recibido en estado "Enviado a DGICP (Registro)"; su estado es modificado por este caso de uso (a "Observado DGICP (Registro)" o "CUP asignado"). | Leer, Actualizar (estado) |
| CUP (Código Único de Proyecto) | Código consecutivo de 5 dígitos asignado por el Técnico PRE a un proyecto. | Crear (asignación) |

---

# Catálogos Detectados

No aplica directamente a este caso de uso (los catálogos de clasificación del proyecto —GRD, GRC, ACC, Ejes, Planes, Sectores— se consultan en modo de solo lectura y se documentan en CU-PRE-01).

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Emisión de CUP | Técnico PRE (botón "Emitir CUP") | Pantalla "Captura de Proyectos" (UC-PRE-03), estado "CUP asignado" |
| Ingreso al banco de proyectos | Sistema (originado inmediatamente tras la emisión del CUP) | Banco de proyectos, disponible para búsqueda mediante CU-PRE-29 |
| Notificación de observaciones (botón "Devolver") | Sistema (originado por Técnico PRE) | Técnico URP (por correo electrónico, modelo A.3.2) — recibido en CU-PRE-01 |
| Notificación de asignación de CUP (botón "Emitir CUP") | Sistema | Técnico URP (por correo electrónico, modelo A.3.4) — recibido en CU-PRE-01 |

> El evento "Solicitud de CUP" (entrada a este caso de uso) se origina en CU-PRE-01 y se documenta ahí.

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| Sistema de correo electrónico / mensajería | Notificación | Envío de notificaciones al Técnico URP (observaciones y asignación de CUP), según los modelos descritos en el Anexo A.3 de CU-PRE-01. |
| CU-PRE-01 Registro y Solicitud de CUP | Interno (caso de uso complementario) | Origen de las solicitudes de CUP recibidas por este caso de uso, y destino del retorno con observaciones. |
| CU-PRE-02 Bandeja de Preinversión | Interno (módulo del mismo sistema) | Origen de la asignación de la solicitud al Técnico PRE (disparador de este caso de uso, FA-01). |
| UC-PRE-03 Captura de Proyectos | Interno (módulo del mismo sistema) | Recibe los proyectos con CUP asignado por este caso de uso. |
| CU-PRE-29 Banco de Proyectos | Interno (módulo del mismo sistema) | Recibe inmediatamente el proyecto al emitirse el CUP (SF-2); es el caso de uso mediante el cual se buscan los proyectos con CUP asignado. |

---

# Datos Pendientes de Definir

- Confirmación con el negocio de la codificación definitiva de este caso de uso (se propuso "CU-PRE-01.5"; alternativas posibles: "CU-PRE-01B", o una renumeración completa del catálogo de casos de uso de Preinversión).
- Actualización recomendada de la referencia FA-01 en CU-PRE-02 "Bandeja Preinversión", para que apunte a este documento en lugar de a CU-PRE-01.
- ¿Existe un límite de ciclos para el flujo "Devolver" → "Enviar" → nueva revisión, o puede repetirse indefinidamente hasta la emisión del CUP?
- ¿Cuál es el mecanismo/reglas exactas de asignación del CUP respecto al salto de números cada 53 códigos (más allá del ejemplo dado en el documento original)?
- ¿Existen registros de auditoría de las acciones del Técnico PRE (quién y cuándo devuelve o emite un CUP)?