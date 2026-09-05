---
id: CU-PRE-01
codigo: CU-PRE-01
nombre: Registro y Solicitud de CUP
modulo: Preinversión
submodulo: Formulación del Proyecto
version: 2.2
fuente_pdf: CU-PRE-01_Registro_de_Proyectos__JUL_2025_V1_F.pdf
pagina_inicio: 1
pagina_fin: 19

nota_version: >
  Esta versión 2.0 es el resultado de dividir el documento original CU-PRE-01
  "Registro de Proyectos" (v1.0) en dos casos de uso independientes, con el
  fin de asignar un actor principal único a cada uno y clarificar el enlace
  con CU-PRE-02 "Bandeja Preinversión". El contenido correspondiente al actor
  "Técnico PRE" se trasladó al nuevo caso de uso CU-PRE-01.5
  "Revisión y Emisión de CUP". Ningún requerimiento del documento original
  fue eliminado; únicamente se redistribuyó entre los dos documentos.
  La versión 2.1 incorpora las resoluciones de negocio de la Ronda 2
  (RQ-C-01, RQ-C-02) sobre las contradicciones de estados señaladas en RN 3.
  La versión 2.2 incorpora, como anexo externo al PDF original, el contenido
  íntegro del archivo `CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx`
  (4 hojas: TIPO EJECUTORAS, INSTITUCIONES, UNIDADES EJECUTORAS y
  CLASIFICADOR INSTITUCIONAL), previamente referenciado pero no transcrito
  en la versión 2.1 (ver resolución de negocio RQ-T-01, ronda 3). No se
  modificó ningún otro contenido de las secciones ya corregidas manualmente
  en versiones anteriores.

actor_principal: Técnico URP

actores_secundarios: [Administrador del Sistema, "Usuarios Internos/Externos (no es un rol/actor formal con credenciales propias; describe el acceso de solo consulta disponible para cualquier usuario del sistema, interno o externo, según sus credenciales de Unidad Ejecutora — ver anotación del especialista de dominio en Actores Secundarios)", Viabilizador, Técnico UAL, Técnico PRE]

prioridad: No especificado en el documento original.

estado: Analizado

depende_de: []

casos_relacionados: [CU-PRE-01.5, CU-PRE-02, UC-PRE-03, CU-PRE-3.5, CU-PRE-24, CU-PRE-25, CU-PRE-26, CU-PRE-29, CU-OYM-01, CU-MPD-01]

roles: [Técnico URP, Administrador del Sistema, "Usuarios Internos/Externos (no es un rol formal; ver nota en Actores Secundarios)"]

pantallas: [Registro de Proyecto, Nuevo Registro]

procesos: []

servicios_externos: [Correo electrónico / otro tipo de mensajería]

entidades: [Proyecto, Unidad Ejecutora, Institución, Respuesta, CUP (solicitud)]

catalogos: [Catálogo GRD (C.1), Catálogo GRC (C.1.5), Catálogo ACC (C.2), Catálogo Ejes del Plan de Gobierno (C.3), Catálogo Planes Sectoriales (C.4), Catálogo Sectores y Macrosectores (C.5), Catálogo Ejes Temáticos (C.6), "Catálogo de Instituciones y Unidades Ejecutoras (anexo Excel externo al PDF original: CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx — no referenciado con número de Anexo propio en el documento fuente)"]

palabras_clave: [Registro de Proyectos, Solicitud de CUP, Técnico URP, Preinversión, DGICP]

ultima_actualizacion: AGO 2025 (redistribución interna); corrección Ronda 2 (RQ-C-01, RQ-C-02) aplicada AGO 2026; incorporación del anexo Excel de Instituciones y Unidades Ejecutoras aplicada AGO 2026.
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | Registro y Solicitud de CUP |
| Código | CU-PRE-01 |
| Módulo | Preinversión |
| Fuente | CU-PRE-01_Registro_de_Proyectos__JUL_2025_V1_F.pdf (documento original, dividido); complementado con el anexo Excel `CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx` (ver sección "Catálogo de Instituciones y Unidades Ejecutoras") |
| Versión | 2.2 |

---

# Objetivo

Permitir al Técnico URP registrar la información básica de un nuevo proyecto, programa o estudio general, y solicitar a la DGICP la asignación de su Código Único de Proyecto (CUP).

> Nota: el documento fuente original no declaraba un "Objetivo" explícito; este texto es una síntesis del contenido de la sección "Descripción" del documento original, acotada al alcance de este caso de uso tras la división.

---

# Descripción

Este caso de uso permite al actor "Técnico URP":

1. Incorporar un nuevo registro (proyecto) en la bandeja "Registro de Proyecto", mediante el ingreso de su información básica.
2. Una vez creado el registro, solicitar a la DGICP el CUP del proyecto.
3. Visualizar los estados del proyecto.
4. Una vez que el proyecto es devuelto con observaciones (resultado de CU-PRE-01.5 "Revisión y Emisión de CUP"), ajustar la información y/o justificar los comentarios recibidos, y reenviar la solicitud.

El caso de uso comprende dos pantallas denominadas "Registro de Proyectos" y "Nuevo Registro". La primera contiene el listado de proyectos que se encuentran en proceso de registro, o bien a espera de la emisión de un CUP. La segunda sirve para el registro de información básica de un nuevo proyecto y su incorporación al listado antes mencionado. Los nuevos registros, que en adelante se denominarán "proyecto", pueden ser de tres tipos: programas, proyectos o estudios generales, según lo determine el usuario "Técnico URP".

La revisión, emisión del CUP y devolución de observaciones —anteriormente descritas en este mismo documento— corresponden ahora al actor "Técnico PRE" y se documentan en el caso de uso **CU-PRE-01.5 "Revisión y Emisión de CUP"**.

---

# Actor Principal

Técnico URP

> Nota: el documento original no especificaba cuál de los dos actores ("Técnico URP" o "Técnico PRE") era el actor principal. Al dividir el caso de uso, "Técnico URP" queda establecido como actor principal de este documento, ya que es quien origina el registro y controla el disparador del caso de uso.

---

# Actores Secundarios

- Administrador del Sistema en la DGICP (único autorizado a cambiar la Unidad Ejecutora de un proyecto — RN 4).
- Usuarios Internos / Externos (solo consulta) (RN 1, punto 3).

> ⚠️ **CORREGIDO (anotación del especialista de dominio):** "Usuarios Internos / Externos" no es un rol o actor formal distinto, con credenciales o permisos propios — se refiere, en realidad, a **cualquier usuario del sistema** (interno o externo a la institución) accediendo en modo de solo consulta, sujeto a las mismas credenciales de Unidad Ejecutora que aplican al resto de actores. Se conserva la mención tal como aparece en el documento original (ya que así describe el nivel de acceso de solo lectura definido en RN 1, punto 3), pero se deja esta aclaración explícita en cada punto donde aparece, para evitar que se interprete o implemente como un rol/actor especial con su propio conjunto de credenciales.
- Técnico PRE — participa únicamente como referencia de flujo saliente/entrante: recibe la solicitud de CUP (vía CU-PRE-02) y retorna el proyecto con observaciones o con CUP asignado. Su actuación propia está descrita en CU-PRE-01.5.

---

# Disparador

> Síntesis derivada (el documento original no contenía una sección "Disparador" explícita): El Técnico URP ingresa a la pantalla "Registro de Proyecto".

---

# Precondiciones

1. N/A (el documento original indica explícitamente "N/A" en su sección de Precondiciones).

---

# Flujo Principal

1. Técnico URP ingresa a la pantalla "Registro de Proyecto".
2. Sistema muestra la pantalla descrita en Anexo A.1 Registro de Proyecto.
3. Técnico URP selecciona uno de los siguientes subflujos: SF-1 Nuevo registro, SF-2 Editar registro, SF-3 Ver registro.

Caso de uso termina.

> Nota: la pantalla "Registro de Proyecto" también es consultada por Técnico PRE, Administrador del Sistema y por cualquier usuario interno o externo del sistema (ver nota sobre "Usuarios Internos/Externos" en Actores Secundarios) únicamente en modo de visualización (ver SF-3 Ver Registro). La actuación operativa del Técnico PRE sobre una solicitud asignada se documenta en CU-PRE-01.5.

---

# Flujos Alternos

> Nota: se conservan los identificadores originales (SF/FA) del documento fuente para no romper la trazabilidad con matrices de pruebas o requerimientos ya elaboradas sobre la versión 1.0.

## SF-1 Nuevo Registro

1. Técnico URP da clic al botón "Nuevo Registro".
2. Sistema muestra la pantalla descrita en A.2 Nuevo registro.
3. Técnico URP selecciona una de las opciones del campo "Iniciativa de inversión" (Programa / Proyecto / Estudios Generales).
4. Técnico URP registra la información de los campos descritos en Anexo A.2 Nuevo registro.
5. Técnico URP selecciona una de las siguientes opciones: SF 1.1 Guardar, SF 1.2 Solicitar CUP, SF 1.3 Regresar.

Subflujo Termina.

## SF-1.1 Guardar

1. Técnico URP da clic al botón "Guardar".
2. Sistema muestra el mensaje del Anexo A.2.2 (Información guardada). Ejecuta la acción de guardado de los datos. Regresa a pantalla Registro de Proyecto.

Subflujo Termina.

## SF-1.2 Solicitar CUP

1. Técnico URP da clic en el botón "Solicitar CUP".
2. Sistema realiza validaciones (FA-1). Si no hay inconsistencias, envía la solicitud a la pantalla Anexo A.1 del caso de uso CU-PRE-02 "Bandeja Preinversión"; el proyecto presentará estado "Enviado a DGICP (Registro)".
3. **[Punto de traspaso a CU-PRE-01.5]** — A partir de este punto, la revisión de la solicitud, la emisión del CUP y/o la devolución con observaciones son ejecutadas por el actor "Técnico PRE" en el caso de uso **CU-PRE-01.5 "Revisión y Emisión de CUP"**, tras ser asignada la solicitud por el Coordinador PRE en CU-PRE-02.
4. Cuando CU-PRE-01.5 devuelve el proyecto con estado "Observado DGICP (Registro)", el Sistema permite al Técnico URP visualizar los comentarios del Técnico PRE en la sección "Revisión PRE" de la pantalla Nuevo registro, y habilita el campo "Respuesta".
5. Técnico URP ajusta los campos correspondientes en la pantalla Nuevo registro y/o digita comentarios justificativos en el campo "Respuesta" de la sección "Revisión PRE". Da clic en el botón "Enviar" (RN-2.9).
6. Sistema notifica al Técnico PRE (correo Anexo A.3.3) y retorna el control a CU-PRE-01.5 para una nueva revisión. Este ciclo (pasos 3 a 6) puede repetirse tantas veces como observaciones se generen, hasta que el Técnico PRE emita el CUP.

Subflujo Termina.

## SF-1.3 Regresar

1. Técnico URP da clic en el botón "Regresar".
2. Sistema muestra el mensaje descrito en A.2.1 Regresar.
3. Técnico URP selecciona uno de los siguientes subflujos: SF 1.3.1 Cancelar, SF 1.3.2 Aceptar.

Subflujo Termina.

## SF-1.3.1 Cancelar

1. Técnico URP da clic en el botón Cancelar.
2. Sistema cancela la acción de Regresar y retorna al punto donde fue llamado descrito en A.2 Nuevo registro.

Subflujo Termina.

## SF-1.3.2 Aceptar

1. Técnico URP da clic en el botón Aceptar.
2. Sistema retorna a la pantalla Registro de Proyecto sin guardar los datos.

Subflujo Termina.

## SF-2 Editar Registro

1. Técnico URP da clic en el nombre del proyecto que cuenta con los estados "En elaboración" y "Observado DGICP (Registro)" en la pantalla Registro de Proyecto.
2. Sistema habilita para edición los campos de la pantalla descrita en Anexo A.2 Nuevo registro.
3. Técnico URP realiza los ajustes y/o actualizaciones en los campos editados y selecciona una de las siguientes opciones: SF 2.1 Guardar, SF 2.2 Solicitar CUP, SF 2.3 Regresar. Estas opciones contienen la misma funcionalidad descrita anteriormente (equivalentes a SF-1.1, SF-1.2 y SF-1.3).

Subflujo Termina.

## SF-3 Ver Registro

1. Todos los actores (Técnico URP, Técnico PRE, Administrador del Sistema) y, en general, cualquier usuario interno o externo del sistema en modo de solo consulta (ver nota sobre "Usuarios Internos/Externos" en Actores Secundarios) dan clic en el nombre del proyecto del listado de la pantalla Registro de Proyecto.
2. Sistema muestra la pantalla descrita en A.2 Nuevo registro, sin autorización de editar información.
3. Todos los actores visualizan la pantalla A.2 Nuevo registro, sin autorización de editar información.

Subflujo termina.

> Nota: este subflujo es de solo consulta y se mantiene en este documento porque la pantalla "Registro de Proyecto" (punto de entrada) es propia de este caso de uso. Cuando el actor es Técnico PRE y el proyecto tiene una solicitud asignada activa, el acceso operativo (con permisos de comentar/devolver/emitir CUP) ocurre a través de CU-PRE-01.5, no de este subflujo.

## FA-1 Solicitar CUP

**Condición**: Se ejecuta cuando, dentro de SF-1.2 (Solicitar CUP), el Sistema verifica las validaciones descritas en el Anexo B.2 Requerimientos funcionales - Validación.

**Flujo:**

Si hay inconsistencias:
1. Sombrea en rojo el contorno de cada campo, y muestra en cada campo los mensajes descritos en el Anexo B.2 Requerimientos funcionales - Validación.
2. Se cancela la acción.
3. Retorna al punto donde fue llamado descrito en el A.2 Nuevo registro.

Si no hay inconsistencias:
1. Continúa según lo descrito en el SF-1.2 Solicitar CUP.

**Resultado**: Según exista o no inconsistencia, la acción se cancela retornando a A.2 Nuevo registro, o continúa el flujo de SF-1.2 Solicitar CUP.

---

# Excepciones

No especificado en el documento original.

---

# Postcondiciones

1. CU-PRE-01.5 Revisión y Emisión de CUP (nueva postcondición inmediata: toda solicitud de CUP pasa primero por este caso de uso antes de continuar hacia los siguientes).
2. CU-PRE-02 Bandeja de Preinversión
3. UC-PRE-03 Captura de proyectos
4. CU-PRE-3.5 Selección y registro de etapas
5. CU-PRE-24 Viabilidad
6. CU-PRE-25 Elegibilidad
7. CU-PRE-26 Opinión Técnica
8. CU-PRE-29 Banco de proyectos
9. CU-OYM-01 Listado de Proyectos Finalizados
10. CU-MPD-01 Registro del Contenido del Convenio

---

# Reglas de Negocio

> Las reglas de negocio originales (RN 1 a RN 6) del documento fuente mencionaban a ambos actores. Se conservan aquí íntegras las que rigen la actuación del Técnico URP y las que son transversales a ambos casos de uso (estados, administración, mensajes de ayuda), y se referencian las que corresponden exclusivamente al Técnico PRE, documentadas en CU-PRE-01.5.

### RN 1 – Actores / Roles (Técnico URP y roles transversales)

**1. Técnico URP:**
a) Es el único que cuenta con permiso para crear nuevos registros en "Registro de Proyecto", y para el registro de información en la pantalla "Nuevo registro".
b) Podrá visualizar la sección "Revisión PRE" y registrar comentarios justificativos en el campo "Respuesta", de la pantalla "Nuevo registro" una vez que el "Técnico PRE" haya dado clic al botón "Devolver" (ver CU-PRE-01.5).
c) Mientras el estado sea "Enviado a DGICP (Registro)" no podrá editar los registros, únicamente consultarlos.
d) Únicamente podrá registrar/editar/visualizar la información de las Unidades Ejecutoras, según credenciales.

**2. Usuarios Internos / Externos (solo consulta):**
a) Únicamente podrán acceder a consultar la información de los registros, sin permisos de editar, y visualizar la información que corresponda a las Unidades Ejecutoras según credenciales.

> ⚠️ Nota (anotación del especialista de dominio): "Usuarios Internos / Externos" no constituye un rol o actor formal adicional con credenciales propias; describe el nivel de acceso de solo consulta disponible para cualquier usuario del sistema (interno o externo), sujeto a las credenciales de Unidad Ejecutora ya descritas en el punto 1.d) para Técnico URP. Ver nota completa en Actores Secundarios.

> Las reglas correspondientes al actor "Técnico PRE" (originalmente RN 1, punto 2) se trasladaron a CU-PRE-01.5.

Origen: Sección 5, RN 1 (documento original CU-PRE-01 v1.0).

---

### RN 2 – Condiciones de Pantalla (acciones del Técnico URP)

**Pantalla "Registro de Proyecto"**

1. Botón "Nuevo Registro":
   a) Será visible únicamente para el actor "Técnico URP".
2. Enlace en cada nombre de proyecto:
   a) Visible para todos los actores.
   b) Al dar clic en cualquier nombre de proyecto se dirige a la pantalla "Nuevo registro" con la última información guardada por el "Técnico URP". Dicha información sólo podrá ser editada por el actor "Técnico URP", si el proyecto posee cualquiera de estos estados "En Elaboración" u "Observado DGICP (Registro)".

**Pantalla "Nuevo Registro"**

3. Botón "Guardar":
   a) Será visible solo para el actor "Técnico URP".
   b) Al dar clic en este botón el sistema almacena/actualiza los datos registrados, y lo incorpora al listado de dicha pantalla. En el instante en que se oprime el botón y luego de que el sistema haya almacenado la información, debe aparecer el mensaje "Guardado" (A.2.2 Información guardada).
4. Botón "Solicitar CUP":
   a) Será visible solo para el actor "Técnico URP".
   b) Al dar clic en este botón el sistema pasará a "Enviado a DGICP" y regresará a la pantalla Registro de Proyecto. El sistema deshabilita la edición de todos los campos de dicha pantalla, y envía alerta al actor "Coordinador PRE" por medio de correo electrónico u otro tipo de mensajería (ver modelo de correo electrónico en el Anexo A.3.1), y muestra la solicitud en la Bandeja de Preinversión (CU-PRE-02).
5. Botón "Regresar":
   a) Será visible para todos los actores.
   b) Al dar clic sobre este botón, si no han sido guardados los registros realizados, el sistema muestra el mensaje de advertencia "Se borrarán todos los datos ingresados" (A.2.1 Regresar) al actor "Técnico URP". Para los demás actores, al dar clic sobre este botón regresará a la pantalla Registro de Proyecto.
   - 5.1 Botón "Cancelar": Al dar clic en el botón, este regresa a la pantalla "Nuevo registro".
   - 5.2 Botón "Aceptar": Al dar clic en el botón, la información ingresada no se guarda y regresa a la pantalla Registro de Proyecto.
6. Botón "Ver descripción de categorías":
   a) Será visible para todos los actores.
   b) Al dar clic en este botón se mostrarán en una ventana emergente las tablas mostradas en los Anexos C.1, C.1.5 y C.2.

**Pantalla "Nuevo Registro", Sección "Revisión PRE" (lado del Técnico URP)**

9. Botón "Enviar":
   a) Visible solo para el actor "Técnico URP".
   b) Al dar clic en el botón, el Sistema informa al "Técnico PRE", por medio de correo electrónico u otro tipo de mensajería, que la Unidad Ejecutora ha respondido a los comentarios de la solicitud de CUP (Anexo A.3.3) y guarda los datos registrados.
   c) Se pasa a la pantalla "Nuevo registro".

> Los puntos 7 (botón "Devolver") y 8 (botón "Emitir CUP") de la RN 2 original corresponden al Técnico PRE y se documentan en CU-PRE-01.5.

Origen: Sección 5, RN 2 (documento original CU-PRE-01 v1.0).

---

### RN 3 – Estados (regla transversal a ambos casos de uso)

Los proyectos en el "Registro de Proyecto" tendrán los siguientes estados:

- **En Elaboración:** Significa que el proyecto se encuentra en proceso de registro de información parcial o registrado completamente por parte del actor "Técnico URP". Aparecerá al dar clic al botón "Guardar" y permanecerá hasta que el actor "Técnico URP" presiona el botón "Solicitar CUP".
- **Enviado a DGICP (Registro):** Significa que el "Técnico URP" ha solicitado la revisión de los registros a la DGICP para la emisión del CUP. Aparecerá al dar clic al botón "Solicitar CUP", y permanecerá hasta que el "Técnico PRE" dé clic al botón "Emitir CUP" o se devuelva al actor "Técnico URP" al dar clic al botón "Devolver" (acciones descritas en CU-PRE-01.5).
- **Observado DGICP (Registro):** Significa que la DGICP ha solicitado ajustes a la información de la solicitud. Aparecerá cuando el "Técnico PRE" dé clic al botón "Devolver" y permanecerá hasta que se atiendan las observaciones y se solicite código único de proyecto nuevamente a la DGICP dando clic al botón "Solicitar CUP", o al registrar comentarios justificativos en el campo "Respuesta" y enviar a la DGICP dando clic al botón "Enviar".
- **CUP asignado:** Significa que la DGICP ha asignado el Código Único de Proyecto (CUP) a través del botón "Emitir CUP" (acción descrita en CU-PRE-01.5).

Estos estados también aparecerán reflejados en la Bandeja Preinversión (CU-PRE-02).

> ✅ **RESUELTO [RQ-C-01]** (Ronda 2 — respuesta del negocio: opción A): queda confirmada como vigente la definición de este documento — el estado "Enviado a DGICP (Registro)" permanece igual durante toda la revisión hasta que el "Técnico PRE" la resuelve (clic en "Emitir CUP" o en "Devolver", en CU-PRE-01.5). Se descarta la definición de CU-PRE-02 (estado vigente solo hasta la asignación del Coordinador PRE); esa corrección corresponde aplicarse en CU-PRE-02, no en este documento.

> ✅ **RESUELTO [RQ-C-02]** (Ronda 2 — respuesta del negocio: opción A): queda confirmada como redacción oficial de nombres de estado la usada en este documento ("Enviado a DGICP (Registro)", "Observado DGICP (Registro)"). Se descartan las redacciones divergentes usadas en UC-PRE-03 para el mismo campo; esa corrección corresponde aplicarse en UC-PRE-03, no en este documento.

Origen: Sección 5, RN 3 (documento original CU-PRE-01 v1.0).

---

### RN 4 – Administración

- El proceso de creación de CUP se hace por una única vez en todo el horizonte del proyecto. Sin embargo, la Unidad Ejecutora podrá cambiarse en cualquier etapa. Este cambio solo podrá realizarlo el administrador del sistema en la DGICP.
- El Técnico URP podrá eliminar la creación del CUP desde la pantalla del Anexo A.1, siempre y cuando no haya dado clic en el botón "Solicitar CUP" por primera vez.
- Contados tres meses a partir de la fecha en que el "Técnico URP" realiza un nuevo registro mediante el botón "Guardar", el Sistema enviará de manera automática un mensaje de alerta al "Técnico URP", por medio de correo electrónico, indicando que la información será eliminada de la "Bandeja de Registro de Proyectos" 5 días hábiles posteriores al envío del mensaje, si no se solicita código (botón "Solicitar CUP").
- El sistema asignará el CUP de manera consecutiva (regla de asignación descrita en CU-PRE-01.5, RN 2.8.c original), y en la asignación de los códigos se saltará un número cada 53 códigos emitidos. Ejemplo: se saltarán los números "10053", "10106", "10159", en adelante. Dichos números quedarán disponibles.

Origen: Sección 5, RN 4 (documento original CU-PRE-01 v1.0).

---

### RN 5 – Mensajes colaborativos o de ayuda

- Al acercar el cursor a un campo se mostrará un ícono "?" y al dar clic en el mismo el Sistema indicará qué información se debe completar en dicho campo.
- Cuando el "Técnico URP" dé clic en el botón "Guardar" y haya campos pendientes de completar, el sistema sombreará los bordes de dichos campos en color rojo.

Origen: Sección 5, RN 5 (documento original CU-PRE-01 v1.0).

---

### RN 6 – Sobre sección "Revisión Área de Preinversión" (regla transversal)

En la sección "Revisión área de preinversión" la parte de la izquierda (comentarios/fecha del comentario/devolver/emitir CUP) solo la diligencia el "Técnico PRE" (ver CU-PRE-01.5). La parte derecha (Respuesta/fecha de la respuesta/enviar) solo se habilita para el "Técnico URP" (este documento); el "Técnico PRE" puede verla pero no se le habilitan estos campos.

Origen: Sección 5, RN 6 (documento original CU-PRE-01 v1.0).

---

# Campos

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|-------|-------------|------|---------|--------------|--------------------|----------------|
| Nombre del proyecto (pantalla Registro de Proyecto) | Muestra el nombre del proyecto. Se incorpora a la lista con la información contenida en el campo "Nombre" de la pantalla "Nuevo Registro", cada vez que se guarda/actualiza un nuevo registro. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable. |
| Unidad Ejecutora (pantalla Registro de Proyecto) | Muestra el nombre de la unidad ejecutora que desarrollará el proyecto. Se incorpora a la lista con la información contenida en el campo "Unidad Ejecutora" de la pantalla "Nuevo registro". | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable. Ver catálogo completo en la sección "Catálogo de Instituciones y Unidades Ejecutoras". |
| Iniciativa de inversión (pantalla Registro de Proyecto) | Muestra si se trata de un programa, un proyecto o un estudio general. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable. |
| Fecha de ingreso (pantalla Registro de Proyecto) | Muestra la fecha de la última actualización de la solicitud de Proyectos bajo el formato DD/MM/AAAA. | Fecha | Fecha | No especificado en el documento. | No especificado en el documento. | No editable. |
| Estado (pantalla Registro de Proyecto) | Muestra el estado actual del proyecto en el Registro de Proyectos. | Texto | Texto | No especificado en el documento. | No especificado en el documento. | No editable. |
| Institución | Se selecciona automáticamente conforme a las credenciales del Técnico URP. | Texto | Texto | No especificado en el documento. | Automático | No seleccionable. Catálogo completo de instituciones disponible en la sección "Catálogo de Instituciones y Unidades Ejecutoras" (hoja "INSTITUCIONES"/"CLASIFICADOR INSTITUCIONAL" del anexo Excel). |
| Unidad Ejecutora (pantalla Nuevo Registro) | Se selecciona automáticamente conforme a las credenciales del Técnico URP. | Texto | Texto | No especificado en el documento. | Automático | No seleccionable. Catálogo completo de unidades ejecutoras disponible en la sección "Catálogo de Instituciones y Unidades Ejecutoras" (hoja "UNIDADES EJECUTORAS"/"CLASIFICADOR INSTITUCIONAL" del anexo Excel). |
| Iniciativa de inversión (pantalla Nuevo Registro) | Muestra para selección en forma radial las opciones "Programa", "Proyecto" y "Estudio General". El Técnico URP solo podrá seleccionar una de las tres opciones, y será el único actor que podrá realizar esta selección. | Selección | Selección | Sí (Campo obligatorio) | Seleccione | Seleccionable. |
| Nombre del proyecto (pantalla Nuevo Registro) | Campo de edición para digitar el nombre del proyecto con un máximo de 250 caracteres. | Texto | Texto | Sí (Campo obligatorio) | No especificado en el documento. | Seleccionable/editable. |
| Monto Estimado de Inversión | Campo de edición para digitar el costo estimado de inversión del proyecto. El sistema deberá agregar el separador de miles (,). | Numérico | Numérico | Sí (Campo obligatorio) | No especificado en el documento. | Seleccionable/editable. |
| Sector | Desplegará para selección un listado con la información registrada en el nivel "Sector" del catálogo "Macrosectores y sectores" (Anexo C.5, referido en el documento original como "Anexo 5"). Solo podrá seleccionarse un elemento del listado. | Texto | Texto | Sí (Campo obligatorio) | Seleccione | Seleccionable. |
| Macrosector | Dependerá del Sector que se ha seleccionado; el Sistema lo asigna automáticamente de acuerdo con el catálogo "Macrosectores y sectores". | Texto | Texto | No especificado en el documento. | Automático | No seleccionable. |
| Eje temático | Campo para seleccionar el eje temático del proyecto. Opciones del catálogo "Eje temático" (Anexo C.6). Solo podrá seleccionarse un elemento del listado. | Selección | Selección | Sí (Campo obligatorio) | No especificado en el documento. | Seleccionable. |
| GRD | Campo para seleccionar las medidas de GRD con que contará el proyecto (si aplica). El listado se activará si el Técnico URP da clic en el botón radial al lado del listado. Opciones de la columna "Categoría" del Anexo C.1. | Selección | Selección | Sí, solo si se da clic en botón radial y no seleccionó una opción de la lista (Campo obligatorio) | No especificado en el documento. | Seleccionable, condicional. |
| GRC | Campo para seleccionar las medidas de GRC con que contará el proyecto (si aplica). Activado por botón radial. Opciones de la columna "Categoría" del Anexo C.1.5. | Selección | Selección | Sí, solo si se da clic en botón radial y no seleccionó una opción (Campo obligatorio) | No especificado en el documento. | Seleccionable, condicional. |
| ACC | Campo para seleccionar las medidas de ACC con que contará el proyecto (si aplica). Activado por botón radial. Opciones de la columna "Categoría" del Anexo C.2. | Selección | Selección | Sí, solo si se da clic en botón radial y no seleccionó una opción (Campo obligatorio) | No especificado en el documento. | Seleccionable, condicional. |
| Proyecto de emergencia | Muestra para selección en forma radial las opciones "Sí" y "No". Si se selecciona "Sí" el Sistema habilita para edición los campos "Tipo de evento" y "N° de DL"; si se selecciona "No", no se habilitan. | Selección | Selección | Sí, condicional (Campo obligatorio si se da clic en botón radial y no se seleccionó opción) | No especificado en el documento. | Seleccionable. |
| Tipo de evento | Campo para registrar el tipo de evento de emergencia. | Texto | Texto | Sí, si "Proyecto de emergencia" = "Sí" (Campo obligatorio) | No especificado en el documento. | Editable condicional. |
| N° de DL | Campo para registrar el número y fecha del Decreto Legislativo de declaratoria de emergencia. | Texto y Numérico | Texto y Numérico | Sí, si "Proyecto de emergencia" = "Sí" (Campo obligatorio) | No especificado en el documento. | Editable condicional. |
| Línea/Eje del Plan de Gobierno | Campo para seleccionar a qué eje del Plan Cuscatlán pertenece el proyecto (si aplica). Activado por botón radial. Opciones de la columna "Ejes" del Anexo C.3. | Selección | Selección | Sí, solo si se da clic en botón radial (Campo obligatorio) | No especificado en el documento. | Seleccionable, condicional. |
| Plan Sectorial/Regional al que contribuye | Campo para seleccionar a qué Plan Sectorial pertenece el proyecto (si aplica). Activado por botón radial. Opciones de la columna "Plan" del Anexo C.4. | Selección | Selección | Sí, solo si se da clic en botón radial (Campo obligatorio) | No especificado en el documento. | Seleccionable, condicional. |
| Descripción del proyecto | Campo para digitar una breve descripción del proyecto con un máximo de 1,000 caracteres. | Texto | Texto | Sí (Campo obligatorio) | No especificado en el documento. | Seleccionable/editable. |

---

# Validaciones

| Campo | Regla | Mensaje |
|-------|-------|---------|
| Iniciativa de inversión | Campo obligatorio, debe seleccionar una opción. | "*Campo obligatorio" |
| Nombre del proyecto | Campo obligatorio, debe ingresar nombre del proyecto. | "*Campo obligatorio" |
| Monto Estimado de Inversión | Campo obligatorio, debe ingresar un monto. | "*Campo obligatorio" |
| Sector | Campo obligatorio, debe seleccionar un sector. | "*Campo obligatorio" |
| Eje temático | Campo obligatorio, debe seleccionar un eje temático. | "*Campo obligatorio" |
| Descripción del proyecto | Campo obligatorio, debe ingresar descripción del proyecto. | "*Campo obligatorio" |

---

# Errores

No especificado en el documento original.

---

# Permisos

| Rol | Acción Permitida | Justificación |
|-----|-------------------|-----------------|
| Técnico URP | Crear nuevo registro en "Registro de Proyecto" y registrar información en "Nuevo registro". | RN 1.1.a: es el único con este permiso. |
| Técnico URP | Visualizar la sección "Revisión PRE" y registrar comentarios en el campo "Respuesta". | RN 1.1.b: habilitado una vez que Técnico PRE da clic en "Devolver" (CU-PRE-01.5). |
| Técnico URP | Consultar (no editar) los registros mientras el estado sea "Enviado a DGICP (Registro)". | RN 1.1.c. |
| Técnico URP | Registrar/editar/visualizar información únicamente de las Unidades Ejecutoras según credenciales. | RN 1.1.d. |
| Técnico URP | Ver y usar los botones "Nuevo Registro", "Guardar", "Solicitar CUP" y "Enviar". | RN 2, puntos 1, 3, 4 y 9. |
| Técnico URP | Editar registros con estados "En Elaboración" u "Observado DGICP (Registro)". | RN 2, punto 2.b. |
| Usuarios Internos / Externos — cualquier usuario del sistema en modo de solo consulta, no es un rol formal (⚠️ ver anotación del especialista en Actores Secundarios) | Consultar la información de los registros, sin permisos de editar, según credenciales de Unidad Ejecutora. | RN 1.3.a. |
| Administrador del Sistema (DGICP) | Cambiar la Unidad Ejecutora de un proyecto en cualquier etapa. | RN 4, primer punto. |

> Los permisos del Técnico PRE (consultar registros, comentar, devolver, emitir CUP) se documentan en CU-PRE-01.5.

---

# Dependencias

**Casos de uso relacionados:**
- CU-PRE-01.5 Revisión y Emisión de CUP (nuevo — desprendido de este documento)
- CU-PRE-02 Bandeja de Preinversión
- UC-PRE-03 Captura de proyectos
- CU-PRE-3.5 Selección y registro de etapas
- CU-PRE-24 Viabilidad
- CU-PRE-25 Elegibilidad
- CU-PRE-26 Opinión Técnica
- CU-PRE-29 Banco de proyectos
- CU-OYM-01 Listado de Proyectos Finalizados
- CU-MPD-01 Registro del Contenido del Convenio

**Procesos relacionados:**
No especificado en el documento original (más allá de los casos de uso listados como postcondiciones).

**Servicios externos:**
- Correo electrónico / otro tipo de mensajería (usado para notificar la respuesta a observaciones y para recibir la alerta de inactividad).

---

# Pantallas

## Pantalla "Registro de Proyecto" (Anexo A.1)

- **Nombre:** Registro de Proyecto
- **Descripción:** Contiene el listado de proyectos que se encuentran en proceso de registro, o bien a espera de la emisión de un CUP.
- **Campos:** Unidad Ejecutora, Nombre del proyecto, Iniciativa de Inversión, Fecha de Ingreso, Estado.
- **Botones:** "Nuevo Registro".
- **Acciones:**
  - Clic en botón "Nuevo Registro" → abre pantalla "Nuevo registro" (solo visible para Técnico URP).
  - Clic en el nombre de un proyecto → dirige a la pantalla "Nuevo registro" con la última información guardada; editable solo por Técnico URP si el estado es "En Elaboración" u "Observado DGICP (Registro)".

> Nota sobre imagen: la captura del Anexo A.1 (documento original) muestra una tabla de ejemplo con las columnas Unidad Ejecutora, Nombre del proyecto, Iniciativa de Inversión, Fecha de Ingreso y Estado, con filas de ejemplo (MOPT/Proyecto/En Elaboración; ISSS/Estudio General/Enviado a DGICP; ISBM/Programa/Observado; ANDA/Proyecto/En Elaboración). Se observa una marca roja "(X)" junto a la fila de ISSS, cuyo significado no se explica en el documento original.

## Pantalla "Nuevo Registro" (Anexo A.2)

- **Nombre:** Nuevo Registro
- **Descripción:** Sirve para el registro de información básica de un nuevo proyecto y su incorporación al listado de "Registro de Proyecto". Esta pantalla es compartida con CU-PRE-01.5: la sección "Revisión Área de Preinversión" se divide en un lado editable por el Técnico URP (este documento) y un lado editable por el Técnico PRE (CU-PRE-01.5).
- **Campos (según Anexo B.1, alcance de este documento):**
  - Institución (automático)
  - Unidad Ejecutora (automático)
  - Iniciativa de Inversión: Proyecto / Programa / Estudio General
  - Sección I. Identificación: Nombre del proyecto, Inversión Estimada, Sector, Macro sector (automático), Eje temático, Es proyecto GRD/GRC/ACC (con listas asociadas), Proyecto de emergencia (Sí/No), Tipo de evento, N° de DL
  - Sección II. Planificación: Línea/Eje del Plan de Gobierno, Plan Sectorial/Regional al que contribuye
  - Sección III. Descripción: Descripción del Proyecto
  - Sección "Revisión Área de Preinversión" (lado Técnico URP): Respuesta
- **Botones (alcance de este documento):** "Regresar", "Ver descripción de categorías", "Guardar", "Solicitar CUP", "Enviar".
- **Acciones:** descritas en los subflujos SF-1, SF-1.1, SF-1.2, SF-1.3, SF-2 y SF-3.

## Pantalla/Modal "A.2.1 Regresar"

- **Descripción:** Mensaje de confirmación "¿Está seguro? ¡Se borrarán todos los datos ingresados!"
- **Botones:** "Cancelar", "Aceptar".

## Pantalla/Modal "A.2.2 Información guardada"

- **Descripción:** Mensaje de confirmación "¡Guardado! Sus datos han sido guardados exitosamente."
- **Botones:** "Aceptar".

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|------|---------|-----------------|
| Confirmación | "¿Está seguro? ¡Se borrarán todos los datos ingresados!" | Al dar clic en el botón "Regresar" sin haber guardado los registros realizados (Anexo A.2.1). |
| Confirmación | "¡Guardado! Sus datos han sido guardados exitosamente." | Al dar clic en el botón "Guardar" y el sistema almacena la información (Anexo A.2.2). |
| Validación | "*Campo obligatorio" | Cuando un campo obligatorio no ha sido completado (ver tabla de Validaciones, Anexo B.2). |
| Correo electrónico | Modelo A.3.1 "Solicitud de CUP de la Unidad ejecutora" (informa al Coordinador PRE la solicitud de CUP). | Al dar clic en el botón "Solicitar CUP". |
| Correo electrónico | Modelo A.3.3 "Respuesta a las observaciones por parte del Técnico URP". | Al dar clic en el botón "Enviar". |

> Los mensajes correspondientes a los botones "Devolver" y "Emitir CUP" (modelos A.3.2 y A.3.4) se documentan en CU-PRE-01.5.

---

# Observaciones

- Este documento es el resultado de dividir el CU-PRE-01 original (v1.0), que no especificaba de forma explícita cuál de los dos actores listados ("Técnico URP" o "Técnico PRE") era el "actor principal". Esa ambigüedad se resuelve aquí asignando "Técnico URP" como actor principal de este documento y trasladando la actuación del "Técnico PRE" a CU-PRE-01.5.
- El actor "Coordinador PRE" es mencionado en el documento original (RN 2, punto 4.b y modelo de correo A.3.1) como destinatario de notificaciones al solicitarse un CUP, pero no aparece en la sección de actores del documento original. Su rol como actor principal está definido en CU-PRE-02 "Bandeja Preinversión".
- El "administrador del sistema en la DGICP" es mencionado únicamente en RN 4 (para el cambio de Unidad Ejecutora); no se documentan sus permisos completos en el material fuente.
- En el Anexo B.1 (campo "Sector"), el documento original hace referencia al "Anexo 5" para el catálogo de Macrosectores y sectores; el catálogo correspondiente en el propio documento está identificado como "Anexo C.5". No se aclara si se trata del mismo anexo o de una referencia a un documento externo.
- Los catálogos de Anexo C.3 (Ejes del Plan de Gobierno), C.4 (Planes Sectoriales) y C.6 (Ejes Temáticos) están marcados en el documento original como "Sujeto a actualización".
- Las contradicciones [C-01] y [C-05] señaladas en la sección "Reglas de Negocio – RN 3", heredadas del documento original, fueron **resueltas en la Ronda 2** (respuestas del negocio RQ-C-01 y RQ-C-02): la definición de estados y la redacción de sus nombres, tal como están documentadas en este CU-PRE-01, quedan confirmadas como vigentes/oficiales. Ver las anotaciones de resolución en RN 3. La corrección correspondiente en CU-PRE-02 (RQ-C-01) y en UC-PRE-03 (RQ-C-02) queda pendiente de aplicarse en esos documentos, no en este.
- **Resuelto (RQ-T-01, ronda 3):** el negocio confirmó que "Institución Ejecutora" y "Unidad Ejecutora" no son sinónimos, sino conceptos con relación 1:N (una Institución Ejecutora agrupa una o más Unidades Ejecutoras), según el catálogo `CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx`. Este documento ya distinguía correctamente ambos conceptos como campos/entidades separados ("Institución" y "Unidad Ejecutora" en el Anexo B, pantalla "Nuevo Registro"); se ajustó únicamente la descripción de la entidad "Unidad Ejecutora" en "Entidades Detectadas", que usaba de forma informal la frase "institución ejecutora asociada" y podía dar a entender sinonimia. **Actualización (v2.2):** el archivo Excel `CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx` fue aportado posteriormente y su contenido íntegro (4 hojas) se transcribió en la sección "Catálogo de Instituciones y Unidades Ejecutoras". Dicho anexo sigue sin formar parte del PDF original (`CU-PRE-01_Registro_de_Proyectos__JUL_2025_V1_F.pdf`); se documenta aquí como fuente complementaria aportada por el negocio, no como contenido del documento fuente original. Se detectaron 2 discrepancias internas entre las hojas del propio Excel (unidades ejecutoras `207` y `3241` presentes solo en "CLASIFICADOR INSTITUCIONAL"; unidad ejecutora `0` "NINGUNA" presente solo en "UNIDADES EJECUTORAS") — ver detalle en "Observaciones sobre este anexo", dentro de esa misma sección. Su integración como tabla maestra de referencia del sistema sigue pendiente de decisión del Gestor del Dominio.

---

# Entidades Detectadas

| Entidad | Descripción | Operación |
|---------|-------------|-----------|
| Proyecto | Registro de información básica de un proyecto, programa o estudio general. | Crear, Leer, Actualizar |
| Unidad Ejecutora | Entidad operativa ejecutora asociada al proyecto (RQ-T-01, ronda 3: distinta de "Institución Ejecutora", que puede agrupar una o más Unidades Ejecutoras); se asigna automáticamente según credenciales del Técnico URP. Catálogo completo en la sección "Catálogo de Instituciones y Unidades Ejecutoras" (hojas "UNIDADES EJECUTORAS"/"CLASIFICADOR INSTITUCIONAL" del anexo Excel). | Leer |
| Institución | Institución a la que pertenece el usuario; asignación automática. Catálogo completo en la sección "Catálogo de Instituciones y Unidades Ejecutoras" (hojas "INSTITUCIONES"/"CLASIFICADOR INSTITUCIONAL" del anexo Excel). | Leer |
| Respuesta | Respuesta justificativa registrada por el Técnico URP a los comentarios del Técnico PRE. | Crear, Leer |
| CUP (Código Único de Proyecto) — como solicitud | Solicitud iniciada por el Técnico URP; su asignación efectiva ocurre en CU-PRE-01.5. | Crear (solicitud), Leer, Eliminar (RN 4: el Técnico URP podrá eliminar la creación del CUP desde la pantalla del Anexo A.1, siempre y cuando no haya dado clic en el botón "Solicitar CUP" por primera vez; el Sistema eliminará la información de la Bandeja de Registro de Proyectos automáticamente tras 3 meses + 5 días hábiles de inactividad sin solicitud de CUP) |

> La entidad "Comentario" (creada por el Técnico PRE) se documenta en CU-PRE-01.5.

---

# Catálogos Detectados

| Catálogo | Valores conocidos |
|----------|---------------------|
| Catálogo de Gestión de Riesgo de Desastres (GRD) – Anexo C.1 | i. Reducción del riesgo existente; ii. Prospectivos para evitar nuevos riesgos y generación de conocimiento; iii. Preparación; iv. Respuesta y Recuperación |
| Catálogo de Gestión de Riesgo Climático (GRC) – Anexo C.1.5 | i. Prevención; ii. Preparación; iii. Gestión de desastres |
| Catálogo de Adaptación al Cambio Climático (ACC) – Anexo C.2 | iv. Mitigación; v. Adaptación |
| Catálogo de Ejes del Plan de Gobierno (Plan Cuscatlán) – Anexo C.3 | Eje 1: Carreteras; Eje 2: Transporte; Eje 3: Infraestructura de salud y educación; Eje 4: Puertos, aeropuertos y aduanas; Eje 5: Agua potable y saneamiento; Eje 6: Vivienda y desarrollo urbano; Eje 7: Infraestructura penitenciaria; Eje 8: Asocios públicos-privados; Eje 9: Energía (*Sujeto a actualización) |
| Catálogo de Planes Sectoriales – Anexo C.4 | Plan Control Territorial (Seguridad); Plan Nacional de Turismo - 2030 (Turismo); Plan Sectorial de Educación 2022-2030 (Educación); Planes Sectoriales para la implementación de las Contribuciones Nacionalmente Determinadas de El Salvador (Medio Ambiente); Plan Nacional de Cambio Climático 2022-2026 (Medio Ambiente); Plan Nacional para la Gestión Integral de Residuos (Medio Ambiente); Política Crecer Juntos 2020-2030 (Salud/Educación) (*Sujeto a actualización) |
| Catálogo de Sectores y Macrosectores – Anexo C.5 | **Desarrollo Social:** Previsión social, Deporte y recreación, Vivienda, Medio ambiente, Asistencia social, Agua potable y alcantarillado, Multisectorial, Salud, Desarrollo urbano y comunal, Educación y cultura. **Desarrollo Económico:** Energía, Industria/comercio y turismo, Silvoagropecuario, Comunicación, Transporte y almacenaje. **Seguridad Pública y Justicia:** Seguridad, Justicia. |
| Catálogo de Ejes Temáticos – Anexo C.6 | Infraestructura Educativa (Construcción y Mejoramiento); Equipamiento, Tecnología y Fortalecimiento Pedagógico en Centros Escolares; Educación Superior e Investigación; Construcción y Mejoramiento de Infraestructura de Salud; Construcción y Mejoramiento de Infraestructura Vial; Transporte Público y Movilidad Urbana; Infraestructura Turística; Espacios Públicos y Desarrollo Urbano; Gestión Ambiental y Restauración; Infraestructura para Gestión de Riesgo y Adaptación Climática; Infraestructura Agrícola y Seguridad Alimentaria; Generación, Transmisión o Distribución de Energía; Infraestructura Aeroportuaria o Portuaria; Conectividad y Comunicaciones; Infraestructura y Servicios para Grupos Vulnerables; Equipamiento y Formación de Capital Humano; Seguridad Ciudadana y Convivencia Comunitaria; Fortalecimiento y Equipamiento Institucional; Vivienda y Mejoramiento Habitacional; Sistemas de Agua y Saneamiento Básico; Infraestructura Cultural y Patrimonio; Infraestructura Deportiva y Recreativa (*Sujeto a actualización, cada valor con su descripción completa en el Anexo C.6 del documento original) |
| Catálogo de Instituciones y Unidades Ejecutoras — Anexo Excel externo (`CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx`) | No forma parte del PDF original; fue aportado posteriormente como archivo anexo independiente para resolver RQ-T-01 (ronda 3). Contiene 4 hojas (TIPO EJECUTORAS, INSTITUCIONES, UNIDADES EJECUTORAS, CLASIFICADOR INSTITUCIONAL). Se transcribe íntegro, sin resumir, en la sección "Catálogo de Instituciones y Unidades Ejecutoras" más abajo. |

---

## Catálogo de Instituciones y Unidades Ejecutoras (Anexo Excel)

> **Origen:** este catálogo no proviene del PDF fuente `CU-PRE-01_Registro_de_Proyectos__JUL_2025_V1_F.pdf`, sino de un archivo Excel anexo aportado posteriormente por el negocio: `CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx`. Se incorpora aquí para dar cumplimiento a la resolución de negocio RQ-T-01 (ronda 3), citada en la sección "Observaciones", que estableció que "Institución Ejecutora" y "Unidad Ejecutora" son conceptos distintos con relación 1:N. El PDF original no asigna un número de Anexo propio (tipo "Anexo A"/"Anexo B"/"Anexo C") a este catálogo; se transcribe bajo esta sección independiente por no encajar en la numeración de Anexos del documento original.
>
> El archivo Excel contiene 4 hojas, inspeccionadas en su totalidad. Las 4 hojas no son versiones alternativas o propuestas competidoras de un mismo catálogo (a diferencia del patrón descrito para otros anexos Excel de esta serie), sino tablas relacionadas entre sí: "TIPO EJECUTORAS" e "INSTITUCIONES" son catálogos base; "UNIDADES EJECUTORAS" relaciona cada unidad ejecutora con su institución y tipo ejecutora; "CLASIFICADOR INSTITUCIONAL" es una vista desnormalizada (unión) de las tres anteriores. Se transcriben las 4 íntegras, sin resumir ni deduplicar, incluyendo las discrepancias detectadas entre ellas (ver "Observaciones sobre este anexo" al final de esta sección).

### Hoja "TIPO EJECUTORAS"

| Código | Nombre |
|---|---|
| 1 | GOBIERNO CENTRAL |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES |
| 3 | EMPRESAS PUBLICAS NO FINANCIERAS |
| 4 | INSTITUCIONES DE SEGURIDAD SOCIAL |
| 5 | OTRAS |

### Hoja "INSTITUCIONES"

> Nota: el campo `CODIG_INSTI_PADRE` establece la jerarquía institucional (una institución cuyo `CODIG_INSTI_PADRE` es `0` es de primer nivel; en caso contrario, es subordinada de la institución cuyo `CODIG_INSTI` coincide con ese valor). El código `0` / nombre "NINGUNA" representa la ausencia de institución (usado, por ejemplo, por unidades ejecutoras que no están asociadas a una institución específica en el catálogo, como se observa en la hoja "CLASIFICADOR INSTITUCIONAL").

| Código Institución (CODIG_INSTI) | Código Institución Padre (CODIG_INSTI_PADRE) | Nombre Institución (NOMBR_INSTI) |
|---|---|---|
| 0 | 0 | NINGUNA |
| 100 | 0 | ORGANO LEGISLATIVO |
| 200 | 0 | CORTE DE CUENTAS DE LA REPUBLICA |
| 300 | 0 | TRIBUNAL SUPREMO ELECTORAL |
| 301 | 300 | REGISTRO NACIONAL DE LAS PERSONAS NATURALES |
| 400 | 0 | TRIBUNAL DE SERVICIO CIVIL |
| 500 | 0 | PRESIDENCIA DE LA REPUBLICA |
| 501 | 500 | INSTITUTO NACIONAL DE LOS DEPORTES DE EL SALVADOR |
| 502 | 500 | INSTITUTO SALVADOREÑO DE PROTECCION AL MENOR |
| 503 | 500 | ADMINISTRACION NACIONAL DE TELECOMUNICACIONES |
| 504 | 500 | INSTITUTO SALVADOREÑO PARA EL DESARROLLO DE LA MUJER |
| 505 | 500 | FONDO DE INVERSION SOCIAL PARA EL DESARROLLO LOCAL |
| 507 | 500 | FONDO DEL MILENIO |
| 537 | 500 | FONDO SOLIDARIO PARA LA FAMILIA MICROEMPRESARIA |
| 551 | 500 | AGENCIA DE PROMOCION DE EXPORTACIONES E INVERSIONES DE EL SALVADOR |
| 556 | 556 | DIRECCIÓN NACIONAL DE OBRAS MUNICIPALES |
| 557 | 500 | DIRECCION NACIONAL DE COMPRAS PUBLICAS |
| 599 | 500 | ENTIDAD DEL MILENIO |
| 600 | 0 | TRIBUNAL DE ETICA GUBERNAMENTAL |
| 700 | 0 | RAMO DE HACIENDA |
| 701 | 700 | LOTERIA NACIONAL DE BENEFICENCIA |
| 702 | 700 | INSTITUTO NACIONAL DE PENSIONES DE LOS EMPLEADOS PUBLICOS |
| 800 | 0 | RAMO DE RELACIONES EXTERIORES |
| 900 | 0 | RAMO DE LA DEFENSA NACIONAL |
| 902 | 900 | CEFAFA |
| 1500 | 0 | CONSEJO NACIONAL DE LA JUDICATURA |
| 1600 | 0 | ORGANO JUDICIAL |
| 1700 | 0 | FISCALIA GENERAL DE LA REPUBLICA |
| 1800 | 0 | PROCURADURIA GENERAL DE LA REPUBLICA |
| 1900 | 0 | PROCURADURIA PARA LA DEFENSA DE LOS DERECHOS HUMANOS |
| 2000 | 0 | MINISTERIO DE GOBERNACION |
| 2002 | 2000 | INSTITUTO SALVADOREÑO DE DESARROLLO MUNICIPAL |
| 2100 | 0 | RAMO DE SEGURIDAD PUBLICA |
| 2101 | 2100 | ACADEMIA NACIONAL DE SEGURIDAD PUBLICA |
| 2200 | 0 | RAMO DE JUSTICIA |
| 2201 | 2200 | CENTRO NACIONAL DE REGISTROS |
| 2202 | 2200 | UNIDAD TECNICA EJECUTIVA |
| 2300 | 0 | RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL |
| 2303 | 2300 | INSTITUTO SALVADOREÑO DE DESARROLLO MUNICIPAL |
| 2306 | 2306 | CUERPO DE BOMBEROS DE EL SALVADOR |
| 2307 | 2300 | DIRECCION DE INTEGRACION |
| 2308 | 2300 | DIRECCIÓN DE ORDENAMIENTO TERRITORIAL Y CONSTRUCCIÓN |
| 2400 | 0 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA |
| 2401 | 2400 | ACADEMIA NACIONAL DE SEGURIDAD PUBLICA |
| 2402 | 2400 | UNIDAD TECNICA EJECUTIVA |
| 3100 | 0 | RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA |
| 3101 | 3100 | UNIVERSIDAD DE EL SALVADOR |
| 3102 | 3100 | FEDERACION SALVADOREÑA DE FUTBOL |
| 3105 | 3100 | CAJA MUTUAL DE LOS EMPLEADOS DEL MINISTERIO DE EDUCACION |
| 3106 | 3100 | INSTITUTO NACIONAL PARA EL DESARROLLO INTEGRAL DE LA NIÑEZ Y LA ADOLESCENCIA |
| 3107 | 3100 | INSTITUTO SALVADOREÑO DE BIENESTAR MAGISTERIAL |
| 3108 | 3100 | CONSEJO NACIONAL DE LA NIÑEZ Y DE LA ADOLESCENCIA |
| 3109 | 3100 | CONSEJO NACIONAL DE LA PRIMERA INFANCIA, NIÑEZ Y ADOLESCENCIA |
| 3110 | 3100 | INSTITUTO CRECER JUNTOS |
| 3200 | 0 | RAMO DE SALUD |
| 3201 | 3200 | HOSPITAL NACIONAL ROSALES |
| 3202 | 3200 | HOSPITAL NACIONAL "BENJAMIN BLOOM" |
| 3203 | 3200 | HOSPITAL NACIONAL DE MATERNIDAD "DR. RAUL ARGUELLO |
| 3204 | 3200 | HOSPITAL NACIONAL PSIQUIATRICO "DR. JOSE MOLINA MA |
| 3205 | 3200 | HOSPITAL NACIONAL NEUMOLOGICO "DR. JOSE ANTONIO ZA |
| 3206 | 3200 | HOSPITAL NACIONAL "SAN JUAN DE DIOS", SANTA ANA |
| 3207 | 3200 | HOSPITAL NACIONAL "FRANCISCO MENENDEZ",AHUACHAPAN |
| 3208 | 3200 | HOSPITAL NACIONAL "SAN JUAN DE DIOS", SONSONATE |
| 3209 | 3200 | HOSPITAL NACIONAL "DR. LUIS EDMUNDO VASQUEZ", CHAL |
| 3210 | 3200 | HOSPITAL NACIONAL "SAN RAFAEL", NUEVA SAN SALVADOR |
| 3211 | 3200 | HOSPITAL NACIONAL "SANTA GERTRUDIS", SAN VICENTE |
| 3212 | 3200 | HOSPITAL NACIONAL "SANTA TERESA", ZACATECOLUCA |
| 3213 | 3200 | HOSPITAL NACIONAL "SAN JUAN DE DIOS", SAN MIGUEL |
| 3214 | 3200 | HOSPITAL NACIONAL "SAN PEDRO", USULUTAN |
| 3215 | 3200 | HOSPITAL NACIONAL "DR. JUAN JOSE FERNANDEZ", ZACAM |
| 3216 | 3200 | HOSPITAL NACIONAL DE SAN BARTOLO |
| 3217 | 3200 | HOSPITAL NACIONAL DE COJUTEPEQUE |
| 3218 | 3200 | HOSPITAL NACIONAL DE LA UNION |
| 3219 | 3200 | HOSPITAL NACIONAL DE ILOBASCO |
| 3220 | 3200 | HOSPITAL NACIONAL DE NUEVA GUADALUPE |
| 3221 | 3200 | HOSPITAL NACIONAL DE CIUDAD BARRIOS |
| 3222 | 3200 | HOSPITAL NACIONAL DE SENSUNTEPEQUE |
| 3223 | 3200 | HOSPITAL NACIONAL DE CHALCHUAPA |
| 3224 | 3200 | HOSPITAL NACIONAL DE METAPAN |
| 3225 | 3200 | HOSPITAL NACIONAL DE SAN FRANCISCO GOTERA |
| 3226 | 3200 | HOSPITAL NACIONAL DE SANTA ROSA DE LIMA |
| 3227 | 3200 | HOSPITAL NACIONAL DE NUEVA CONCEPCION |
| 3228 | 3200 | HOSPITAL NACIONAL DE SANTIAGO DE MARIA |
| 3229 | 3200 | HOSPITAL NACIONAL DE JIQUILISCO |
| 3230 | 3200 | HOSPITAL NACIONAL DE SUCHITOTO |
| 3231 | 3200 | CONSEJO SUPERIOR DE SALUD PUBLICA |
| 3232 | 3200 | INSTITUTO SALVADOREÑO DE REHABILITACION INTEGRAL |
| 3233 | 3200 | HOGAR DE ANCIANOS "NARCISA CASTILLO", SANTA ANA |
| 3234 | 3200 | CRUZ ROJA SALVADOREÑA |
| 3235 | 3200 | FONDO SOLIDARIO PARA LA SALUD |
| 3236 | 3200 | DIRECCION NACIONAL DE MEDICAMENTOS |
| 3237 | 3237 | Hospital Nacional El Salvador |
| 3300 | 0 | RAMO DE TRABAJO Y PREVISION SOCIAL |
| 3301 | 3300 | INSTITUTO SALVADOREÑO DE FOMENTO COOPERATIVO |
| 3302 | 3300 | INSTITUTO SALVADOREÑO DE FORMACION PROFESIONAL |
| 3303 | 3300 | INSTITUTO SALVADOREÑO DEL SEGURO SOCIAL |
| 3304 | 3300 | FONDO DE PROTECCION DE LISIADOS Y DISCAPACITADOS A CONSECUENCIA DEL CONFLICTO ARMADO |
| 3400 | 0 | RAMO DE VIVIENDA Y DESARROLLO URBANO |
| 3401 | 3400 | FONDO NACIONAL DE VIVIENDA POPULAR |
| 3500 | 0 | RAMO DE CULTURA |
| 3600 | 0 | RAMO DE VIVIENDA |
| 3700 | 0 | RAMO DE DESARROLLO LOCAL |
| 4100 | 0 | RAMO DE ECONOMIA |
| 4101 | 4100 | CENTRO INTERNACIONAL DE FERIAS Y CONVENCIONES |
| 4102 | 4100 | CONSEJO NACIONAL DE CIENCIA Y TECNOLOGIA |
| 4103 | 4100 | CONSEJO DE VIGILANCIA DE LA CONTADURIA PUBLICA Y AUDITORIA |
| 4104 | 4100 | INSTITUTO SALVADOREÑO DE TURISMO |
| 4105 | 4100 | CONSEJO SALVADOREÑO DEL CAFE |
| 4106 | 4100 | COMISION EJECUTIVA HIDROELECTRICA DEL RIO LEMPA |
| 4108 | 4100 | CORPORACION SALVADOREÑA DE TURISMO |
| 4109 | 4100 | SUPERINTENDENCIA GENERAL DE ELECTRICIDAD Y TELECOMUNICACIONES |
| 4110 | 4100 | SUPERINTENDENCIA DE VALORES |
| 4111 | 4100 | SUPERINTENDENCIA DE PENSIONES |
| 4112 | 4100 | CORPORACION SALVADOREÑA DE INVERSIONES |
| 4114 | 4100 | CENTRO NACIONAL DE REGISTROS |
| 4115 | 4100 | FINET |
| 4116 | 4100 | CONSEJO SALVADOREÑO DE LA AGROINDUSTRIA AZUCARERA |
| 4117 | 4100 | SUPERINTENDENCIA DE COMPETENCIA |
| 4118 | 4100 | DEFENSORIA DEL CONSUMIDOR |
| 4119 | 4100 | CONSEJO NACIONAL DE ENERGIA |
| 4122 | 4100 | COMISION NACIONAL PARA LA MICRO Y PEQUEÑA EMPRESA |
| 4127 | 4127 | INSTITUTO NACIONAL DE CAPACITACION Y FORMACION |
| 4200 | 0 | RAMO DE AGRICULTURA Y GANADERIA |
| 4201 | 4200 | INSTITUTO SALVADOREÑO DE TRANSFORMACION AGRARIA |
| 4202 | 4200 | CENTRO NACIONAL DE TECNOLOGIA AGROPECUARIA Y FORESTAL |
| 4203 | 4200 | ESCUELA NACIONAL DE AGRICULTURA |
| 4300 | 0 | RAMO DE OBRAS PUBLICAS Y TRANSPORTE |
| 4301 | 4300 | ADMINISTRACION NACIONAL DE ACUEDUCTOS Y ALCANTARILLADOS |
| 4302 | 4300 | FONDO SOCIAL PARA LA VIVIENDA |
| 4303 | 4300 | COMISION EJECUTIVA PORTUARIA AUTONOMA |
| 4304 | 4300 | AUTORIDAD DE AVIACION CIVIL |
| 4305 | 4300 | FONDO NACIONAL DE VIVIENDA POPULAR |
| 4306 | 4300 | FONDO DE CONSERVACION VIAL |
| 4400 | 0 | RAMO DE MEDIO AMBIENTE Y RECURSOS NATURALES |
| 4401 | 4400 | FONDO AMBIENTAL DE EL SALVADOR |
| 4404 | 4400 | AUTORIDAD SALVADOREÑA DEL AGUA |
| 4500 | 0 | RAMO DE TRANSPORTE |
| 4600 | 0 | RAMO DE TURISMO |
| 4601 | 4600 | INSTITUTO SALVADOREÑO DE TURISMO |
| 4602 | 4600 | CORPORACION SALVADOREÑA DE TURISMO |
| 4603 | 4600 | AUTORIDAD DE PLANIFICACION DEL CENTRO HISTORICO DE SAN SALVADOR |
| 4700 | 0 | HABITAT |
| 4800 | 0 | MINISTERIO DE SEGURIDA PUBLICA Y JUSTICIA |
| 4900 | 0 | FUNDASAL |

### Hoja "UNIDADES EJECUTORAS"

> Nota: cada fila indica también, mediante `CODIG_TIPO_EJECU` y `CODIG_INSTI`, a qué Tipo Ejecutora e Institución pertenece la unidad ejecutora (columnas de las hojas "TIPO EJECUTORAS" e "INSTITUCIONES" respectivamente). La columna `ES_ACTIV` indica si la unidad ejecutora está activa ("S") o no ("N") en el catálogo fuente.

| Código Unidad Ejecutora (CODIG_UNIDA_EJECU) | Nombre Unidad Ejecutora (NOMBR_UNIDA_EJECU) | Sigla (SIGLA_UNIDA_EJECU) | Código Tipo Ejecutora (CODIG_TIPO_EJECU) | Código Institución (CODIG_INSTI) | Es Activo (ES_ACTIV) |
|---|---|---|---|---|---|
| 0 | NINGUNA | NINGUNA | 1 | 0 | N |
| 1 | Ministerio de Seguridad Pública | MSP | 1 | 2400 | N |
| 2 | Dirección General de Urbanismo y Arquitectura | DUA | 1 | 4300 | N |
| 3 | Ministerio de Educación, Ciencia y Tecnología | MINEDUCYT | 1 | 3100 | S |
| 4 | Ministerio de Agricultura y Ganadería | MAG | 1 | 4200 | S |
| 5 | Corte Suprema de Justicia | CSJ | 1 | 1600 | S |
| 6 | Fondo de Inversión Social para el Desarrollo Local | FISDL | 2 | 505 | N |
| 7 | Instituto Nacional de los Deportes de El Salvador | INDES | 2 | 501 | S |
| 8 | Secretaría Nacional de la Familia | SNF | 1 | 500 | S |
| 9 | Policia Nacional Civil | PNC - MSPJ | 1 | 2400 | S |
| 10 | Ministerio de Seguridad Pública y Justicia | MSPJ | 1 | 2400 | S |
| 11 | Ministerio de Salud | MINSAL | 1 | 3200 | S |
| 12 | Ministerio de Hacienda | MH | 1 | 700 | S |
| 13 | Ministerio de Gobernación y Desarrollo Territorial | MIGOBDT | 1 | 2300 | S |
| 14 | Ministerio de Defensa | MINDEF | 1 | 900 | S |
| 15 | Academia Nacional de Seguridad Pública | ANSP - MSPJ | 2 | 2401 | S |
| 16 | Ministerio de Relaciones Exteriores | MIREX | 1 | 800 | S |
| 17 | Fiscalía General de la República | FGR | 1 | 1700 | S |
| 18 | Ministerio de Economía | MINEC | 1 | 4100 | S |
| 19 | Ministerio de Trabajo y Previsión Social | MTYPS | 1 | 3300 | S |
| 20 | Asamblea Legislativa | AL | 1 | 100 | S |
| 21 | Viceministerio de Vivienda y Desarrollo Urbano | VMVDU | 1 | 4300 | N |
| 22 | Universidad de El Salvador | UES | 2 | 3100 | S |
| 23 | Instituto Salvadoreño de Turismo | ISTU | 2 | 4601 | S |
| 24 | Instituto Salvadoreño del Seguro Social | ISSS | 2 | 3303 | S |
| 25 | Comisión Ejecutiva Hidroeléctrica del Río Lempa | CEL | 3 | 4106 | S |
| 26 | Administración Nacional de Acueductos y Alcant. | ANDA | 3 | 4301 | S |
| 27 | Comisión Ejecutiva Portuaria Autónoma | CEPA | 3 | 4303 | S |
| 28 | Instituto Salvadoreño de Desarrollo Municipal | ISDEM | 2 | 2303 | S |
| 29 | Comisión Presid. para la Modern.del Sector Público | CPMSP - CAPRES | 1 | 500 | N |
| 30 | Unidad Técnica Ejecutiva del Sector Justicia | UTE | 2 | 2402 | S |
| 31 | Centro Nacional de Registros | CNR | 2 | 4114 | S |
| 32 | Instituto Salvadoreño para el Desa de la Mujer | ISDEMU | 2 | 504 | S |
| 33 | Viceministerio de Obras Públicas - MOP | MOPT | 1 | 4300 | S |
| 34 | Centro Internacional de Ferias y Convenciones | CIFCO | 2 | 4101 | S |
| 35 | Dirección General de Caminos | DGC | 1 | 4300 | N |
| 36 | Instituto Salvadoreño de Rehabilitación Integral | ISRI | 2 | 3232 | S |
| 37 | Dirección General de Correos | CORREO - MIGOBDT | 1 | 2300 | S |
| 38 | Cuerpo de Bomberos de El Salvador | BOMBEROS - MIGOBDT | 2 | 2306 | S |
| 39 | Dirección de Desarrollo de la Comunidad | DIDECO - MIGOBDT | 1 | 2300 | S |
| 40 | Radio Nacional de El Salvador | RADIO | 5 | 2300 | S |
| 41 | Instituto Salvadoreño del Protección al Menor | ISPM | 5 | 0 | N |
| 42 | Viceministerio de Transporte | VMT | 1 | 4300 | S |
| 43 | Administración de Maquinaria y Equipo -AME- | AME | 1 | 0 | N |
| 44 | Procuraduria General de la República | PGR | 1 | 1800 | S |
| 45 | Dirección General de Centros Penales - MSPJ | DGCP - MSPJ | 1 | 2400 | S |
| 46 | Ministerio de Medio Ambiente y Recursos Naturales | MARN | 1 | 4400 | S |
| 47 | Comité de Emergencia Nacional | COEN | 1 | 2300 | S |
| 48 | Caja Mutual de los Empleados del Min. de Educación | CM | 1 | 3105 | S |
| 49 | Fondo Nacional de Vivienda Popular | FONAVIPO | 5 | 4305 | S |
| 50 | Corporación Salvadoreña de Turismo | CORSATUR | 2 | 4602 | S |
| 51 | Procuraduria p/la Defensa de los Derechos Humanos | PDDH | 1 | 1900 | S |
| 52 | Consejo Nacional de Judicatura | CNJ | 1 | 1500 | S |
| 53 | Corte de Cuentas de la República | CCR | 1 | 200 | S |
| 54 | Fundacion Salvador del Mundo | FUSALMO | 5 | 0 | S |
| 55 | Fondo de Conservación Vial | FOVIAL | 5 | 4306 | S |
| 56 | Instituto Salvadoreño de Formación Profesional | INSAFORP | 2 | 3302 | S |
| 57 | Consejo Superior de Salud Pública | CSSP | 2 | 3231 | S |
| 58 | Fondo Social Para la Vivienda | FSV | 5 | 4300 | S |
| 59 | Consejo Nacional para la Cultura y el Arte | CONCULTURA | 1 | 3100 | S |
| 60 | Instituto para el Desarrollo de la Niñez y la Adolescencia | IDNA | 2 | 0 | N |
| 61 | Fundación Salvadoreña de Desarrollo y Vivienda Mínima | FUNDASAL | 3 | 0 | N |
| 62 | Instituto Salvadoreño para el Desarrollo de la Niñez y la Adolescencia | ISNA | 2 | 3106 | N |
| 63 | Fondo de Protección de Lisiados y Discap. a Consecuencia del Conflicto Armado | FOPROLYD | 5 | 3304 | S |
| 64 | Servicio Nacional de Estudios Territoriales | SNET | 1 | 4400 | S |
| 65 | Secretaría Técnica y de Planificación de la Presidencia | STPP - CAPRES | 1 | 500 | N |
| 66 | Fondo Ambiental de El Salvador | FONAES | 5 | 4401 | S |
| 67 | Superintendencia de Pensiones | SP | 1 | 4111 | S |
| 68 | Alcaldia Municipal de San Salvador | AMSS | 1 | 0 | S |
| 69 | Banco Multisectorial de Inversiones | BMI | 1 | 0 | N |
| 70 | Instituto Libertad y Progreso | ILP | 1 | 0 | N |
| 71 | Fondo de Desarrollo Económico y Social | FODES | 1 | 2303 | N |
| 72 | Comisión Nacional de la Micro y Pequeña Empresa | CONAMYPE | 1 | 4122 | S |
| 73 | Superintendencia General de Electricidad y Telecomunicaciones | SIGET | 2 | 4109 | S |
| 74 | Consejo Salvadoreño del Café | CSC | 2 | 4105 | S |
| 75 | Defensoría del Consumidor | DC | 2 | 4118 | S |
| 76 | Loteria Nacional de Beneficencia | LNB | 3 | 701 | S |
| 77 | Secretaría de la Juventud | SJ - CAPRES | 1 | 500 | S |
| 78 | Comisión Nacional de Promoción de Exportaciones e Inversiones | CONADEI - CAPRES | 1 | 500 | S |
| 79 | Superintendencia del Sistema Financiero | SSF | 1 | 0 | S |
| 80 | Superintendencia de Valores | SV | 1 | 4110 | S |
| 81 | Tribunal Supremo Electoral | TSE | 1 | 300 | S |
| 82 | Centros Intermedios | CI - MSPJ | 1 | 2400 | S |
| 83 | Secretaría de Estado - MSPJ | SE - MSPJ | 1 | 2400 | S |
| 84 | CONACYT | CONACYT | 1 | 4102 | S |
| 87 | Fondo del Milenio | FOMILENIO | 5 | 507 | S |
| 88 | Academia Internacional para el Cumplimiento de la Ley | ILEA | 1 | 2400 | S |
| 89 | Superintendencia de Competencia | SC | 1 | 4117 | S |
| 90 | Protección Civil | PC | 1 | 0 | N |
| 91 | MINISTERIO DE TURISMO | MITUR | 1 | 4600 | S |
| 92 | Registro Nacional de las Personas Naturales | RNPN | 1 | 301 | S |
| 93 | Instituto Nacional de Pensiones de los Empleados Públicos | INPEP | 3 | 702 | S |
| 94 | Consejo de Vigilancia de la Contaduría Pública y Auditoría | CVCPA | 1 | 4103 | S |
| 95 | Instituto Salvadoreño de Transformación Agraria | ISTA | 1 | 4201 | S |
| 96 | Ministerio de Cultura | MICULTURA | 1 | 3500 | S |
| 97 | Centro Nacional de Tecnología Agropecuaria y Forestal | CENTA | 2 | 4202 | S |
| 98 | Secretaría de Inclusión Social | SIS - CAPRES | 1 | 500 | S |
| 99 | PENDIENTE | PENDIENTE | 1 | 0 | N |
| 100 | Secretaría de Participación Ciudadana, Transparencia y Anticorrupción | SPCTA - CAPRES | 1 | 500 | S |
| 101 | Escuela Nacional de Agricultura "Roberto Quiñonez" | ENA | 2 | 4203 | S |
| 102 | Consejo Nacional de la Seguridad Pública | CNSP | 1 | 500 | S |
| 103 | Fondo Solidario para la Familia Microempresaria | FOSOFAMILIA | 1 | 537 | S |
| 104 | Instituto Salvadoreño de Fomento Cooperativo | INSAFOCOOP | 1 | 3301 | S |
| 105 | Instituto Salvadoreño de Bienestar Magisterial | ISBM | 2 | 3107 | S |
| 106 | Consejo Nacional de Energía | CNE | 1 | 4119 | S |
| 107 | Instituto Nacional de la Juventud | INJUVE | 2 | 500 | N |
| 108 | Fondo Solidario para la Salud | FOSALUD | 1 | 3235 | S |
| 109 | Consejo Nacional de Calidad | CNC | 1 | 0 | S |
| 110 | Dirección Nacional de Medicamentos | DNM | 2 | 3236 | S |
| 192 | Secretaría de Gobernabilidad y Comunicaciones de la de Presidencia | SGYCP - CAPRES | 1 | 500 | S |
| 193 | Centro Farmacéutico de la Fuerza Armada | CEFAFA | 5 | 902 | S |
| 194 | Cruz Roja Salvadoreña | CRUZ ROJA | 1 | 3234 | S |
| 195 | Ministerio de Vivienda | MIVI | 1 | 3600 | S |
| 196 | Ministerio de Desarrollo Local | MINDEL | 1 | 3700 | S |
| 197 | Dirección General de Migración y Extranjería | DGME - MSPJ | 1 | 2400 | S |
| 198 | Instituto Administrador de los Beneficios de los Veteranos Militares y Excombatientes | INABVE | 2 | 2300 | S |
| 199 | Entidad del Milenio | EDM | 5 | 599 | S |
| 200 | Dirección de Reconstrucción del Tejido Social | DRTS - MSPJ | 1 | 2400 | S |
| 201 | Vicepresidencia de la República | VICEPRESIDENCIA | 1 | 500 | S |
| 202 | Secretaría de Innovación de la Presidencia | SI - CAPRES | 1 | 500 | S |
| 203 | Dirección Nacional de Obras Municipales | DOM | 2 | 556 | S |
| 204 | Autoridad Salvadoreña del Agua- | ASA- | 5 | 4404 | N |
| 205 | Secretaría Privada de la Presidencia | SPP - CAPRES | 1 | 500 | S |
| 206 | Agencia de El Salvador para la Cooperación Internacional | ESCO - CAPRES | 1 | 500 | S |
| 500 | Presidencia de la República | PRESIDENCIA | 1 | 500 | S |
| 551 | Agencia de Promoción de Exportaciones e Inversiones de El Salvador | PROESA | 1 | 551 | S |
| 557 | Dirección Nacional de Compras Públicas | DINAC | 2 | 557 | S |
| 903 | MINED-FOSEDU | MINED-FOSEDU | 1 | 3100 | N |
| 909 | PNC-FOSEDU | PNC-FOSEDU | 1 | 2400 | N |
| 915 | ANSP-FOSEDU | ANSP-FOSEDU | 2 | 2401 | N |
| 917 | FGR-FOSEDU | FGR-FOSEDU | 1 | 1700 | N |
| 944 | PGR-FOSEDU | PGR-FOSEDU | 1 | 1800 | N |
| 945 | MJSP/DGCP-FOSEDU | MJSP/DGCP-FOSEDU | 1 | 2400 | N |
| 2307 | Dirección de Integración | DI | 2 | 2307 | S |
| 2308 | Dirección de Ordenamiento Territorial y Construcción | DOT | 2 | 2308 | S |
| 3108 | Consejo Nacional de la Niñez y de la Adolescencia | CONNA | 2 | 3108 | N |
| 3109 | Consejo Nacional de la Primera Infancia, Niñez y Adolescencia | CONAPINA | 2 | 3109 | S |
| 3110 | Instituto Crecer Juntos | ICJ | 2 | 3100 | S |
| 3237 | Hospital Nacional El Salvador | HES | 2 | 3237 | S |
| 4115 | Fondo de Inversión Nacional en Electricidad y Telefonía | FINET | 2 | 4115 | S |
| 4127 | Instituto Nacional de Capacitación y Formación | INCAF | 2 | 4127 | S |
| 4404 | Autoridad Salvadoreña del Agua | ASA | 2 | 4404 | S |
| 4603 | Autoridad de Planificación del Centro Histórico de San Salvador | Centro Histórico | 1 | 4603 | S |
| 99999 | Banco Central de Reserva | BCR | 1 | 0 | S |

### Hoja "CLASIFICADOR INSTITUCIONAL"

> Nota: esta hoja presenta la unión desnormalizada (Tipo Ejecutora + Institución + Unidad Ejecutora) en una sola tabla, útil como catálogo de referencia directo para el campo "Unidad Ejecutora" de la pantalla "Nuevo Registro". Se transcribe íntegra, sin resumir sus 145 filas, conservando el orden original de la hoja.

| Código Tipo Ejecutora | Nombre Tipo Ejecutora | Código Institución | Nombre Institución | Código Unidad Ejecutora | Nombre Unidad Ejecutora | Sigla Unidad Ejecutora | Es Activo |
|---|---|---|---|---|---|---|---|
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 43 | Administración de Maquinaria y Equipo -AME- | AME | N |
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 68 | Alcaldia Municipal de San Salvador | AMSS | S |
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 69 | Banco Multisectorial de Inversiones | BMI | N |
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 70 | Instituto Libertad y Progreso | ILP | N |
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 79 | Superintendencia del Sistema Financiero | SSF | S |
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 90 | Protección Civil | PC | N |
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 99 | PENDIENTE | PENDIENTE | N |
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 109 | Consejo Nacional de Calidad | CNC | S |
| 1 | GOBIERNO CENTRAL | 0 | NINGUNA | 99999 | Banco Central de Reserva | BCR | S |
| 1 | GOBIERNO CENTRAL | 100 | ORGANO LEGISLATIVO | 20 | Asamblea Legislativa | AL | S |
| 1 | GOBIERNO CENTRAL | 200 | CORTE DE CUENTAS DE LA REPUBLICA | 53 | Corte de Cuentas de la República | CCR | S |
| 1 | GOBIERNO CENTRAL | 300 | TRIBUNAL SUPREMO ELECTORAL | 81 | Tribunal Supremo Electoral | TSE | S |
| 1 | GOBIERNO CENTRAL | 301 | REGISTRO NACIONAL DE LAS PERSONAS NATURALES | 92 | Registro Nacional de las Personas Naturales | RNPN | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 8 | Secretaría Nacional de la Familia | SNF | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 29 | Comisión Presid. para la Modern.del Sector Público | CPMSP - CAPRES | N |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 65 | Secretaría Técnica y de Planificación de la Presidencia | STPP - CAPRES | N |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 77 | Secretaría de la Juventud | SJ - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 78 | Comisión Nacional de Promoción de Exportaciones e Inversiones | CONADEI - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 98 | Secretaría de Inclusión Social | SIS - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 100 | Secretaría de Participación Ciudadana, Transparencia y Anticorrupción | SPCTA - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 102 | Consejo Nacional de la Seguridad Pública | CNSP | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 192 | Secretaría de Gobernabilidad y Comunicaciones de la de Presidencia | SGYCP - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 201 | Vicepresidencia de la República | VICEPRESIDENCIA | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 202 | Secretaría de Innovación de la Presidencia | SI - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 205 | Secretaría Privada de la Presidencia | SPP - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 206 | Agencia de El Salvador para la Cooperación Internacional | ESCO - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 207 | Organismo de Mejora Regulatoria | OMR - CAPRES | S |
| 1 | GOBIERNO CENTRAL | 500 | PRESIDENCIA DE LA REPUBLICA | 500 | Presidencia de la República | PRESIDENCIA | S |
| 1 | GOBIERNO CENTRAL | 537 | FONDO SOLIDARIO PARA LA FAMILIA MICROEMPRESARIA | 103 | Fondo Solidario para la Familia Microempresaria | FOSOFAMILIA | S |
| 1 | GOBIERNO CENTRAL | 551 | AGENCIA DE PROMOCION DE EXPORTACIONES E INVERSIONES DE EL SALVADOR | 551 | Agencia de Promoción de Exportaciones e Inversiones de El Salvador | PROESA | S |
| 1 | GOBIERNO CENTRAL | 700 | RAMO DE HACIENDA | 12 | Ministerio de Hacienda | MH | S |
| 1 | GOBIERNO CENTRAL | 800 | RAMO DE RELACIONES EXTERIORES | 16 | Ministerio de Relaciones Exteriores | MIREX | S |
| 1 | GOBIERNO CENTRAL | 900 | RAMO DE LA DEFENSA NACIONAL | 14 | Ministerio de Defensa | MINDEF | S |
| 1 | GOBIERNO CENTRAL | 1500 | CONSEJO NACIONAL DE LA JUDICATURA | 52 | Consejo Nacional de Judicatura | CNJ | S |
| 1 | GOBIERNO CENTRAL | 1600 | ORGANO JUDICIAL | 5 | Corte Suprema de Justicia | CSJ | S |
| 1 | GOBIERNO CENTRAL | 1700 | FISCALIA GENERAL DE LA REPUBLICA | 17 | Fiscalía General de la República | FGR | S |
| 1 | GOBIERNO CENTRAL | 1700 | FISCALIA GENERAL DE LA REPUBLICA | 917 | FGR-FOSEDU | FGR-FOSEDU | N |
| 1 | GOBIERNO CENTRAL | 1800 | PROCURADURIA GENERAL DE LA REPUBLICA | 44 | Procuraduria General de la República | PGR | S |
| 1 | GOBIERNO CENTRAL | 1800 | PROCURADURIA GENERAL DE LA REPUBLICA | 944 | PGR-FOSEDU | PGR-FOSEDU | N |
| 1 | GOBIERNO CENTRAL | 1900 | PROCURADURIA PARA LA DEFENSA DE LOS DERECHOS HUMANOS | 51 | Procuraduria p/la Defensa de los Derechos Humanos | PDDH | S |
| 1 | GOBIERNO CENTRAL | 2300 | RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL | 13 | Ministerio de Gobernación y Desarrollo Territorial | MIGOBDT | S |
| 1 | GOBIERNO CENTRAL | 2300 | RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL | 37 | Dirección General de Correos | CORREO - MIGOBDT | S |
| 1 | GOBIERNO CENTRAL | 2300 | RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL | 39 | Dirección de Desarrollo de la Comunidad | DIDECO - MIGOBDT | S |
| 1 | GOBIERNO CENTRAL | 2300 | RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL | 47 | Comité de Emergencia Nacional | COEN | S |
| 1 | GOBIERNO CENTRAL | 2303 | INSTITUTO SALVADOREÑO DE DESARROLLO MUNICIPAL | 71 | Fondo de Desarrollo Económico y Social | FODES | N |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 1 | Ministerio de Seguridad Pública | MSP | N |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 9 | Policia Nacional Civil | PNC - MSPJ | S |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 10 | Ministerio de Seguridad Pública y Justicia | MSPJ | S |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 45 | Dirección General de Centros Penales - MSPJ | DGCP - MSPJ | S |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 82 | Centros Intermedios | CI - MSPJ | S |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 83 | Secretaría de Estado - MSPJ | SE - MSPJ | S |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 88 | Academia Internacional para el Cumplimiento de la Ley | ILEA | S |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 197 | Dirección General de Migración y Extranjería | DGME - MSPJ | S |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 200 | Dirección de Reconstrucción del Tejido Social | DRTS - MSPJ | S |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 909 | PNC-FOSEDU | PNC-FOSEDU | N |
| 1 | GOBIERNO CENTRAL | 2400 | RAMO DE SEGURIDAD PUBLICA Y JUSTICIA | 945 | MJSP/DGCP-FOSEDU | MJSP/DGCP-FOSEDU | N |
| 1 | GOBIERNO CENTRAL | 3100 | RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA | 3 | Ministerio de Educación, Ciencia y Tecnología | MINEDUCYT | S |
| 1 | GOBIERNO CENTRAL | 3100 | RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA | 59 | Consejo Nacional para la Cultura y el Arte | CONCULTURA | S |
| 1 | GOBIERNO CENTRAL | 3100 | RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA | 903 | MINED-FOSEDU | MINED-FOSEDU | N |
| 1 | GOBIERNO CENTRAL | 3105 | CAJA MUTUAL DE LOS EMPLEADOS DEL MINISTERIO DE EDUCACION | 48 | Caja Mutual de los Empleados del Min. de Educación | CM | S |
| 1 | GOBIERNO CENTRAL | 3200 | RAMO DE SALUD | 11 | Ministerio de Salud | MINSAL | S |
| 1 | GOBIERNO CENTRAL | 3200 | RAMO DE SALUD | 3241 | Superintendencia de Regulación Sanitaria | SRS | S |
| 1 | GOBIERNO CENTRAL | 3234 | CRUZ ROJA SALVADOREÑA | 194 | Cruz Roja Salvadoreña | CRUZ ROJA | S |
| 1 | GOBIERNO CENTRAL | 3235 | FONDO SOLIDARIO PARA LA SALUD | 108 | Fondo Solidario para la Salud | FOSALUD | S |
| 1 | GOBIERNO CENTRAL | 3300 | RAMO DE TRABAJO Y PREVISION SOCIAL | 19 | Ministerio de Trabajo y Previsión Social | MTYPS | S |
| 1 | GOBIERNO CENTRAL | 3301 | INSTITUTO SALVADOREÑO DE FOMENTO COOPERATIVO | 104 | Instituto Salvadoreño de Fomento Cooperativo | INSAFOCOOP | S |
| 1 | GOBIERNO CENTRAL | 3500 | RAMO DE CULTURA | 96 | Ministerio de Cultura | MICULTURA | S |
| 1 | GOBIERNO CENTRAL | 3600 | RAMO DE VIVIENDA | 195 | Ministerio de Vivienda | MIVI | S |
| 1 | GOBIERNO CENTRAL | 3700 | RAMO DE DESARROLLO LOCAL | 196 | Ministerio de Desarrollo Local | MINDEL | S |
| 1 | GOBIERNO CENTRAL | 4100 | RAMO DE ECONOMIA | 18 | Ministerio de Economía | MINEC | S |
| 1 | GOBIERNO CENTRAL | 4102 | CONSEJO NACIONAL DE CIENCIA Y TECNOLOGIA | 84 | CONACYT | CONACYT | S |
| 1 | GOBIERNO CENTRAL | 4103 | CONSEJO DE VIGILANCIA DE LA CONTADURIA PUBLICA Y AUDITORIA | 94 | Consejo de Vigilancia de la Contaduría Pública y Auditoría | CVCPA | S |
| 1 | GOBIERNO CENTRAL | 4110 | SUPERINTENDENCIA DE VALORES | 80 | Superintendencia de Valores | SV | S |
| 1 | GOBIERNO CENTRAL | 4111 | SUPERINTENDENCIA DE PENSIONES | 67 | Superintendencia de Pensiones | SP | S |
| 1 | GOBIERNO CENTRAL | 4117 | SUPERINTENDENCIA DE COMPETENCIA | 89 | Superintendencia de Competencia | SC | S |
| 1 | GOBIERNO CENTRAL | 4119 | CONSEJO NACIONAL DE ENERGIA | 106 | Consejo Nacional de Energía | CNE | S |
| 1 | GOBIERNO CENTRAL | 4122 | COMISION NACIONAL PARA LA MICRO Y PEQUEÑA EMPRESA | 72 | Comisión Nacional de la Micro y Pequeña Empresa | CONAMYPE | S |
| 1 | GOBIERNO CENTRAL | 4200 | RAMO DE AGRICULTURA Y GANADERIA | 4 | Ministerio de Agricultura y Ganadería | MAG | S |
| 1 | GOBIERNO CENTRAL | 4201 | INSTITUTO SALVADOREÑO DE TRANSFORMACION AGRARIA | 95 | Instituto Salvadoreño de Transformación Agraria | ISTA | S |
| 1 | GOBIERNO CENTRAL | 4300 | RAMO DE OBRAS PUBLICAS Y TRANSPORTE | 2 | Dirección General de Urbanismo y Arquitectura | DUA | N |
| 1 | GOBIERNO CENTRAL | 4300 | RAMO DE OBRAS PUBLICAS Y TRANSPORTE | 21 | Viceministerio de Vivienda y Desarrollo Urbano | VMVDU | N |
| 1 | GOBIERNO CENTRAL | 4300 | RAMO DE OBRAS PUBLICAS Y TRANSPORTE | 33 | Viceministerio de Obras Públicas - MOP | MOPT | S |
| 1 | GOBIERNO CENTRAL | 4300 | RAMO DE OBRAS PUBLICAS Y TRANSPORTE | 35 | Dirección General de Caminos | DGC | N |
| 1 | GOBIERNO CENTRAL | 4300 | RAMO DE OBRAS PUBLICAS Y TRANSPORTE | 42 | Viceministerio de Transporte | VMT | S |
| 1 | GOBIERNO CENTRAL | 4400 | RAMO DE MEDIO AMBIENTE Y RECURSOS NATURALES | 46 | Ministerio de Medio Ambiente y Recursos Naturales | MARN | S |
| 1 | GOBIERNO CENTRAL | 4400 | RAMO DE MEDIO AMBIENTE Y RECURSOS NATURALES | 64 | Servicio Nacional de Estudios Territoriales | SNET | S |
| 1 | GOBIERNO CENTRAL | 4600 | RAMO DE TURISMO | 91 | MINISTERIO DE TURISMO | MITUR | S |
| 1 | GOBIERNO CENTRAL | 4603 | AUTORIDAD DE PLANIFICACION DEL CENTRO HISTORICO DE SAN SALVADOR | 4603 | Autoridad de Planificación del Centro Histórico de San Salvador | Centro Histórico | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 0 | NINGUNA | 60 | Instituto para el Desarrollo de la Niñez y la Adolescencia | IDNA | N |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 500 | PRESIDENCIA DE LA REPUBLICA | 107 | Instituto Nacional de la Juventud | INJUVE | N |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 501 | INSTITUTO NACIONAL DE LOS DEPORTES DE EL SALVADOR | 7 | Instituto Nacional de los Deportes de El Salvador | INDES | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 504 | INSTITUTO SALVADOREÑO PARA EL DESARROLLO DE LA MUJER | 32 | Instituto Salvadoreño para el Desa de la Mujer | ISDEMU | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 505 | FONDO DE INVERSION SOCIAL PARA EL DESARROLLO LOCAL | 6 | Fondo de Inversión Social para el Desarrollo Local | FISDL | N |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 556 | DIRECCIÓN NACIONAL DE OBRAS MUNICIPALES | 203 | Dirección Nacional de Obras Municipales | DOM | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 557 | DIRECCION NACIONAL DE COMPRAS PUBLICAS | 557 | Dirección Nacional de Compras Públicas | DINAC | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 2300 | RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL | 198 | Instituto Administrador de los Beneficios de los Veteranos Militares y Excombatientes | INABVE | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 2303 | INSTITUTO SALVADOREÑO DE DESARROLLO MUNICIPAL | 28 | Instituto Salvadoreño de Desarrollo Municipal | ISDEM | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 2306 | CUERPO DE BOMBEROS DE EL SALVADOR | 38 | Cuerpo de Bomberos de El Salvador | BOMBEROS - MIGOBDT | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 2307 | DIRECCION DE INTEGRACION | 2307 | Dirección de Integración | DI | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 2308 | DIRECCIÓN DE ORDENAMIENTO TERRITORIAL Y CONSTRUCCIÓN | 2308 | Dirección de Ordenamiento Territorial y Construcción | DOT | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 2401 | ACADEMIA NACIONAL DE SEGURIDAD PUBLICA | 15 | Academia Nacional de Seguridad Pública | ANSP - MSPJ | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 2401 | ACADEMIA NACIONAL DE SEGURIDAD PUBLICA | 915 | ANSP-FOSEDU | ANSP-FOSEDU | N |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 2402 | UNIDAD TECNICA EJECUTIVA | 30 | Unidad Técnica Ejecutiva del Sector Justicia | UTE | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3100 | RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA | 22 | Universidad de El Salvador | UES | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3100 | RAMO DE EDUCACION, CIENCIA Y TECNOLOGIA | 3110 | Instituto Crecer Juntos | ICJ | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3106 | INSTITUTO NACIONAL PARA EL DESARROLLO INTEGRAL DE LA NIÑEZ Y LA ADOLESCENCIA | 62 | Instituto Salvadoreño para el Desarrollo de la Niñez y la Adolescencia | ISNA | N |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3107 | INSTITUTO SALVADOREÑO DE BIENESTAR MAGISTERIAL | 105 | Instituto Salvadoreño de Bienestar Magisterial | ISBM | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3108 | CONSEJO NACIONAL DE LA NIÑEZ Y DE LA ADOLESCENCIA | 3108 | Consejo Nacional de la Niñez y de la Adolescencia | CONNA | N |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3109 | CONSEJO NACIONAL DE LA PRIMERA INFANCIA, NIÑEZ Y ADOLESCENCIA | 3109 | Consejo Nacional de la Primera Infancia, Niñez y Adolescencia | CONAPINA | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3231 | CONSEJO SUPERIOR DE SALUD PUBLICA | 57 | Consejo Superior de Salud Pública | CSSP | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3232 | INSTITUTO SALVADOREÑO DE REHABILITACION INTEGRAL | 36 | Instituto Salvadoreño de Rehabilitación Integral | ISRI | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3236 | DIRECCION NACIONAL DE MEDICAMENTOS | 110 | Dirección Nacional de Medicamentos | DNM | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3237 | Hospital Nacional El Salvador | 3237 | Hospital Nacional El Salvador | HES | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3302 | INSTITUTO SALVADOREÑO DE FORMACION PROFESIONAL | 56 | Instituto Salvadoreño de Formación Profesional | INSAFORP | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 3303 | INSTITUTO SALVADOREÑO DEL SEGURO SOCIAL | 24 | Instituto Salvadoreño del Seguro Social | ISSS | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4101 | CENTRO INTERNACIONAL DE FERIAS Y CONVENCIONES | 34 | Centro Internacional de Ferias y Convenciones | CIFCO | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4105 | CONSEJO SALVADOREÑO DEL CAFE | 74 | Consejo Salvadoreño del Café | CSC | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4109 | SUPERINTENDENCIA GENERAL DE ELECTRICIDAD Y TELECOMUNICACIONES | 73 | Superintendencia General de Electricidad y Telecomunicaciones | SIGET | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4114 | CENTRO NACIONAL DE REGISTROS | 31 | Centro Nacional de Registros | CNR | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4115 | FINET | 4115 | Fondo de Inversión Nacional en Electricidad y Telefonía | FINET | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4118 | DEFENSORIA DEL CONSUMIDOR | 75 | Defensoría del Consumidor | DC | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4127 | INSTITUTO NACIONAL DE CAPACITACION Y FORMACION | 4127 | Instituto Nacional de Capacitación y Formación | INCAF | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4202 | CENTRO NACIONAL DE TECNOLOGIA AGROPECUARIA Y FORESTAL | 97 | Centro Nacional de Tecnología Agropecuaria y Forestal | CENTA | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4203 | ESCUELA NACIONAL DE AGRICULTURA | 101 | Escuela Nacional de Agricultura "Roberto Quiñonez" | ENA | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4404 | AUTORIDAD SALVADOREÑA DEL AGUA | 4404 | Autoridad Salvadoreña del Agua | ASA | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4601 | INSTITUTO SALVADOREÑO DE TURISMO | 23 | Instituto Salvadoreño de Turismo | ISTU | S |
| 2 | INSTITUCIONES DESCENTRALIZADAS NO EMPRESARIALES | 4602 | CORPORACION SALVADOREÑA DE TURISMO | 50 | Corporación Salvadoreña de Turismo | CORSATUR | S |
| 3 | EMPRESAS PUBLICAS NO FINANCIERAS | 0 | NINGUNA | 61 | Fundación Salvadoreña de Desarrollo y Vivienda Mínima | FUNDASAL | N |
| 3 | EMPRESAS PUBLICAS NO FINANCIERAS | 701 | LOTERIA NACIONAL DE BENEFICENCIA | 76 | Loteria Nacional de Beneficencia | LNB | S |
| 3 | EMPRESAS PUBLICAS NO FINANCIERAS | 702 | INSTITUTO NACIONAL DE PENSIONES DE LOS EMPLEADOS PUBLICOS | 93 | Instituto Nacional de Pensiones de los Empleados Públicos | INPEP | S |
| 3 | EMPRESAS PUBLICAS NO FINANCIERAS | 4106 | COMISION EJECUTIVA HIDROELECTRICA DEL RIO LEMPA | 25 | Comisión Ejecutiva Hidroeléctrica del Río Lempa | CEL | S |
| 3 | EMPRESAS PUBLICAS NO FINANCIERAS | 4301 | ADMINISTRACION NACIONAL DE ACUEDUCTOS Y ALCANTARILLADOS | 26 | Administración Nacional de Acueductos y Alcant. | ANDA | S |
| 3 | EMPRESAS PUBLICAS NO FINANCIERAS | 4303 | COMISION EJECUTIVA PORTUARIA AUTONOMA | 27 | Comisión Ejecutiva Portuaria Autónoma | CEPA | S |
| 5 | OTRAS | 0 | NINGUNA | 41 | Instituto Salvadoreño del Protección al Menor | ISPM | N |
| 5 | OTRAS | 0 | NINGUNA | 54 | Fundacion Salvador del Mundo | FUSALMO | S |
| 5 | OTRAS | 507 | FONDO DEL MILENIO | 87 | Fondo del Milenio | FOMILENIO | S |
| 5 | OTRAS | 599 | ENTIDAD DEL MILENIO | 199 | Entidad del Milenio | EDM | S |
| 5 | OTRAS | 902 | CEFAFA | 193 | Centro Farmacéutico de la Fuerza Armada | CEFAFA | S |
| 5 | OTRAS | 2300 | RAMO DE GOBERNACION Y DESARROLLO TERRITORIAL | 40 | Radio Nacional de El Salvador | RADIO | S |
| 5 | OTRAS | 3304 | FONDO DE PROTECCION DE LISIADOS Y DISCAPACITADOS A CONSECUENCIA DEL CONFLICTO ARMADO | 63 | Fondo de Protección de Lisiados y Discap. a Consecuencia del Conflicto Armado | FOPROLYD | S |
| 5 | OTRAS | 4300 | RAMO DE OBRAS PUBLICAS Y TRANSPORTE | 58 | Fondo Social Para la Vivienda | FSV | S |
| 5 | OTRAS | 4305 | FONDO NACIONAL DE VIVIENDA POPULAR | 49 | Fondo Nacional de Vivienda Popular | FONAVIPO | S |
| 5 | OTRAS | 4306 | FONDO DE CONSERVACION VIAL | 55 | Fondo de Conservación Vial | FOVIAL | S |
| 5 | OTRAS | 4401 | FONDO AMBIENTAL DE EL SALVADOR | 66 | Fondo Ambiental de El Salvador | FONAES | S |
| 5 | OTRAS | 4404 | AUTORIDAD SALVADOREÑA DEL AGUA | 204 | Autoridad Salvadoreña del Agua- | ASA- | N |

### Observaciones sobre este anexo

- **Discrepancia detectada entre hojas (no se resuelve unilateralmente):** la hoja "CLASIFICADOR INSTITUCIONAL" incluye 2 unidades ejecutoras que **no aparecen** en la hoja "UNIDADES EJECUTORAS": código `207` "Organismo de Mejora Regulatoria" (sigla "OMR - CAPRES", bajo institución `500` "PRESIDENCIA DE LA REPUBLICA") y código `3241` "Superintendencia de Regulación Sanitaria" (sigla "SRS", bajo institución `3200` "RAMO DE SALUD"). No se puede determinar, con la información disponible en el propio archivo, si se trata de una omisión en la hoja "UNIDADES EJECUTORAS" o de datos obsoletos/adicionales exclusivos de la hoja "CLASIFICADOR INSTITUCIONAL".
- **Discrepancia inversa:** la hoja "UNIDADES EJECUTORAS" incluye el código `0` "NINGUNA" (tipo ejecutora `1`, institución `0`, `ES_ACTIV` = "N"), que **no aparece** en la hoja "CLASIFICADOR INSTITUCIONAL". No se puede determinar con la información disponible si este registro "NINGUNA" es un valor de control/placeholder deliberadamente excluido del clasificador, o una omisión.
- El resto de las referencias cruzadas entre hojas (código de institución, código de tipo ejecutora, nombres) son consistentes entre "TIPO EJECUTORAS", "INSTITUCIONES", "UNIDADES EJECUTORAS" y "CLASIFICADOR INSTITUCIONAL"; no se detectaron otras discrepancias de nombre, sigla o código.
- Este anexo no indica explícitamente cuál de las 4 hojas debe usarse como fuente del listado desplegable del campo "Unidad Ejecutora" (pantalla "Nuevo Registro"); por la estructura de los datos, la hoja "CLASIFICADOR INSTITUCIONAL" parece ser la vista de consulta directa (unidad ejecutora + institución + tipo en una sola fila), pero esto no está confirmado por el negocio y se incluye también en "Datos Pendientes de Definir".

---

# Eventos del Sistema

| Evento | Origen | Destino |
|--------|--------|---------|
| Solicitud de CUP | Pantalla "Nuevo registro" (Técnico URP, botón "Solicitar CUP") | Pantalla "Bandeja Preinversión" (CU-PRE-02), estado "Enviado a DGICP (Registro)" |
| Alerta automática de posible eliminación (3 meses sin solicitud de CUP) | Sistema | Técnico URP (por correo electrónico) |
| Notificación de solicitud de CUP | Sistema | Coordinador PRE (por correo electrónico, modelo A.3.1) — recibido y gestionado en CU-PRE-02 |
| Notificación de respuesta a observaciones (botón "Enviar") | Sistema (originado por Técnico URP) | Técnico PRE (por correo electrónico, modelo A.3.3) — recibido en CU-PRE-01.5 |

> Los eventos "Emisión de CUP" y "Notificación de observaciones (Devolver)" se originan en CU-PRE-01.5 y se documentan ahí; en este documento aparecen únicamente como entrada (ver Postcondiciones y SF-1.2, paso 4).

---

# Integraciones

| Sistema | Tipo | Descripción |
|---------|------|-------------|
| Sistema de correo electrónico / mensajería | Notificación | Envío de alertas y notificaciones automáticas relacionadas con la actuación del Técnico URP (solicitud de CUP, respuesta a observaciones), según los modelos descritos en el Anexo A.3. |
| CU-PRE-01.5 Revisión y Emisión de CUP | Interno (caso de uso desprendido) | Recibe la solicitud de CUP enviada desde este caso de uso para su revisión y emisión, y retorna el proyecto con observaciones o con CUP asignado. |
| CU-PRE-02 Bandeja de Preinversión | Interno (módulo del mismo sistema) | Recibe las solicitudes de CUP enviadas desde este caso de uso y refleja los estados del proyecto. |
| UC-PRE-03 Captura de Proyectos | Interno (módulo del mismo sistema) | Recibe los proyectos que ya cuentan con CUP asignado (evento originado en CU-PRE-01.5). |

---

# Datos Pendientes de Definir

- Confirmación con el negocio de que la división propuesta (Técnico URP en este documento / Técnico PRE en CU-PRE-01.5) es la correcta, y de la codificación definitiva a asignar al nuevo caso de uso (se usó "CU-PRE-01.5" siguiendo la convención decimal ya usada en "CU-PRE-3.5" del propio conjunto de documentos).
- ¿Quién es el "administrador del sistema en la DGICP" y cuáles son sus permisos completos? (mencionado solo en RN 4).
- Definición exacta de qué constituyen las "credenciales" que determinan qué Unidades Ejecutoras puede ver/editar cada Técnico URP, y qué determinan para el acceso de solo consulta descrito como "Usuarios Internos/Externos" (ver aclaración del especialista en Actores Secundarios: no es un rol formal distinto, sino el nivel de acceso de lectura disponible para cualquier usuario del sistema).
- Significado de la marca "(X)" en rojo junto a la fila de "ISSS" en la captura del Anexo A.1 (documento original).
- Confirmación de si "Anexo 5" (mencionado en el campo "Sector" del Anexo B.1) corresponde al Anexo C.5 del mismo documento, o a un anexo externo distinto.
- Longitud máxima o formato numérico específico para el campo "Monto Estimado de Inversión" (solo se especifica el uso de separador de miles).
- Formato/validación exacta requerida para el campo "N° de DL" (texto y numérico).
- ¿Existen registros de auditoría (quién y cuándo edita, guarda o elimina un registro)?
- ¿Quién puede eliminar manualmente un registro, más allá de la eliminación automática tras 3 meses + 5 días hábiles sin solicitud de CUP?
- Confirmación con el negocio de cuál de las 4 hojas del anexo `CU-PRE-01_Catálogos_de_instituciones_y_unidades_ejecutoras.xlsx` debe usarse como fuente autoritativa del listado desplegable de "Unidad Ejecutora" (pantalla "Nuevo Registro"): la hoja "CLASIFICADOR INSTITUCIONAL" parece ser la vista de consulta directa, pero esto no está confirmado.
- Resolución de las 2 discrepancias detectadas entre las hojas del anexo Excel de instituciones/unidades ejecutoras: unidades ejecutoras `207` (Organismo de Mejora Regulatoria) y `3241` (Superintendencia de Regulación Sanitaria), presentes solo en "CLASIFICADOR INSTITUCIONAL" y ausentes de "UNIDADES EJECUTORAS"; y unidad ejecutora `0` ("NINGUNA"), presente solo en "UNIDADES EJECUTORAS" y ausente de "CLASIFICADOR INSTITUCIONAL".