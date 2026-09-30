---
id: CU-PRE-26
codigo: CU-PRE-26
nombre: "Opinión Técnica"
modulo: "Preinversión (derivado del prefijo del código 'CU-PRE' y de RN17 'El proceso de preinversión culmina con la emisión de la Opinión Técnica'; el documento no tiene un campo explícito llamado 'Módulo')"
submodulo: "Gestión (derivado de FB paso 5 'pestaña Gestión de Proyectos', FA04 paso 4.1 'Módulo de Gestión' y del menú del Anexo A 'GESTIÓN > OT'; el documento no tiene un campo explícito llamado 'Submódulo')"
version: "1.0 (portada, encabezados de página e Historial de Revisiones coinciden en 1.0; ver Observaciones sobre la discrepancia de fechas: encabezado 'MAYO 2026' vs. Historial 'SEP 2025')"
fuente_pdf: "CU-PRE-26_Opinio_n_Te_cnica.pdf (documento principal, 18 páginas); CU-PRE-26_Anexo.pdf (anexo de mockups, 4 páginas)"
pagina_inicio: 1
pagina_fin: 18

actor_principal: "No especificado en el documento (el PDF lista cuatro actores —Técnico URP, Viabilizador, Técnico PRE, Coordinador PRE— sin designar uno como principal)"

actores_documento:
  # Los cuatro actores que el PDF lista en su sección "Actores", sin clasificarlos como principal ni secundarios.
  - "Técnico URP"
  - "Viabilizador"
  - "Técnico PRE"
  - "Coordinador PRE"

actores_secundarios:
  # Clasificación derivada: ninguno de estos figura en la sección "Actores" del PDF; se incluyen por su participación en flujos, reglas o anexos.
  - "Sistema (no figura en la sección 'Actores' del documento; se incluye por ser quien ejecuta los pasos automáticos de los flujos: notificaciones, habilitaciones, cambios de estado)"
  - "Director DGICP (no figura en la sección 'Actores'; solo se menciona en FA01 paso 1.5 y en el Anexo B como firmante de la 'Nota de Opinión Técnica')"
  - "Jefe URP Viabilizador (no figura en la sección 'Actores'; solo aparece en el título del correo 'c' del Anexo A2)"
  - "DGI (no figura en la sección 'Actores'; solo aparece en RN09 como administradora de la 'Bandeja de Preinversión')"
  - "Institución Ejecutora / 'Institución' (no figura en la sección 'Actores'; el Anexo B.1 y el Anexo A2 la usan como destinataria de correos y origen de justificaciones)"

prioridad: "No especificado en el documento."

estado: Analizado  # Derivado: valor de control asignado durante el análisis; no proviene del documento.

depende_de:
  - "CU-PRE-03 Captura de proyectos a CU-PRE-23 Indicadores del Proyecto (rango, tal como aparece en el PDF)"
  - "CU-PRE-24 Viabilidad"
  - "CU-PRE-25 Elegibilidad"

casos_relacionados:
  - "CU-PRE-01 Registro de Proyectos"
  - "CU-PRE-03 Captura de proyectos / Bandeja de Preinversión"
  - "CU-PRE-3.5 Selección y registro de etapas"
  - "CU-PRE-04 Identificación"
  - "CU-PRE-07 Población Objetivo"
  - "CU-PRE-11 Descripción técnica"
  - "CU-PRE-12 Localización"
  - "CU-PRE-17 Presupuesto de inversión"
  - "CU-PRE-18 Flujo de costos de Operación y Mantenimiento"
  - "CU-PRE-21 Flujo de Caja y cálculo de Indicadores"
  - "CU-PRE-22.1 Programación financiera de la preinversión del Proyecto"
  - "CU-PRE-23 Indicadores del Proyecto"
  - "CU-PRE-24 Viabilidad"
  - "CU-PRE-25 Elegibilidad"
  - "CU-PRE-26.5 Priorización"
  - "CU-PRO-01 Elaboración/Actualización del PRIPME"
  - "CU-PRO-08 Generación de Escenarios para Corto Plazo"
  - "CU-PRO-17 Programación Mensual Financiera del PAIP"

roles:
  - "Técnico URP"
  - "Viabilizador"
  - "Técnico PRE"
  - "Coordinador PRE"
  - "Sistema (no figura en la sección 'Actores' del documento; ejecuta los pasos automáticos)"
  - "Director DGICP (no figura en la sección 'Actores'; firmante de la Nota de OT)"
  - "Jefe URP Viabilizador (no figura en la sección 'Actores'; solo en Anexo A2 c)"
  - "DGI (no figura en la sección 'Actores'; solo en RN09 como administradora de la 'Bandeja de Preinversión')"
  - "Institución Ejecutora / 'Institución' (no figura en la sección 'Actores'; el Anexo B.1 la describe como la organización cuyos usuarios tienen el rol Técnico URP o Viabilizador — no se puede determinar si es un rol del sistema)"

pantallas:
  - "Anexo A – Menú de registro y gestión de proyectos (Imagen de referencia)"
  - "Anexo A.1 – Opinión Técnica"
  - "Anexo A1.5 – Histórico de Opinión Técnica"
  - "Anexo A.3 – Alerta de Gestión de Actualización de O.T."
  - "Anexo A.4 – Aviso de habilitación de campos para gestión de Actualización de O.T."
  - "Anexo A.5 – Aviso de Emisión de O.T."
  - "Anexo A.6 – Informe OT (Ejemplo)"

procesos:
  # Derivado: nombres y agrupación construidos por el análisis a partir de los flujos y reglas de negocio; no es una lista explícita del documento.
  - "Solicitud de Opinión Técnica"
  - "Revisión y emisión de comentarios DGICP"
  - "Atención de ajustes / justificaciones por la Institución"
  - "Visto bueno y emisión de OT Favorable"
  - "Actualización de Opinión Técnica"
  - "Notificación por correo electrónico"
  - "Control de plazo de atención de observaciones (5 días hábiles)"

servicios_externos:
  - "Correo electrónico (el documento no especifica el servicio o sistema de correo)"

entidades:
  # Derivado: entidades inferidas por el análisis (ver sección "Entidades Detectadas"); el documento no incluye un modelo de entidades.
  - "Proyecto"
  - "Solicitud de Opinión Técnica"
  - "Comentario DGICP"
  - "Justificación / Respuesta Institución"
  - "Documento anexo"
  - "Nota de OT"
  - "Conclusiones"
  - "Histórico de Opinión Técnica"
  - "Informe OT"
  - "Notificación por correo electrónico"

catalogos:
  # Derivado: agrupación del análisis (ver sección "Catálogos Detectados"); algunos nombres coinciden con rótulos del documento ("Tipo de solicitud", "Tipo de gestión") y otros son denominaciones del análisis.
  - "Tipo de solicitud"
  - "Tipo de gestión"
  - "Estados del proyecto"
  - "Etapas"
  - "Apartados"
  - "Opinión Técnica (valor del informe)"
  - "Indicadores de evaluación"

palabras_clave:
  - "Opinión Técnica"
  - "OT"
  - "Actualización de OT"
  - "OT Favorable"
  - "Visto bueno OT"
  - "Comentarios DGICP"
  - "Justificación Institución"
  - "Preinversión"
  - "Viabilidad"
  - "Elegibilidad"
  - "Histórico de OT"
  - "Informe OT"

ultima_actualizacion: "No especificado en el documento. (Encabezado de página: 'Fecha: MAYO 2026'; Historial de Revisiones: 'SEP 2025')"

trazabilidad:
  informacion_general:
    pagina: 3
  historial_revisiones:
    pagina: 2
  flujo_principal:
    FB:
      pagina: 4
  flujos_alternos:
    FA01:
      pagina: 4
    FA02:
      pagina: 5
    FA03:
      pagina: 5
    FA03.1:
      pagina: 5
    "FA04 (rotulado 'Flujo Alternativo 4 – FA01' en el PDF)":
      pagina: 6
  reglas_negocio:
    RN01:
      pagina: 7
    RN02:
      pagina: 7
    RN03:
      pagina: 7
    RN04:
      pagina: 7
    RN05:
      pagina: 7
    RN06:
      pagina: 7
    RN07:
      pagina: 7
    RN08:
      pagina: 7
    RN09:
      pagina: 7
    RN10:
      pagina: 7
    RN11:
      pagina: 8
    "RN 12":
      pagina: 8
    RN13:
      pagina: 8
    RN14:
      pagina: 8
    RN15:
      pagina: 8
    RN16:
      pagina: 8
    RN17:
      pagina: 8
    "Anexo A – RN1":
      pagina: 9
    "Anexo A – RN2":
      pagina: 9
    "Anexo A – RN3":
      pagina: 9
  anexos:
    A:
      nombre: "Menú de registro y gestión de proyectos (Imagen de referencia)"
      pagina: 8
    A.1:
      nombre: "Opinión Técnica"
      pagina: 10
    A1.5:
      nombre: "Histórico de Opinión Técnica"
      pagina: 11
    A2:
      nombre: "Contenido de Correos Electrónicos (literales a–f en p. 11; texto de f, g y h en p. 12)"
      pagina: 11
    A.3:
      nombre: "Alerta de Gestión de Actualización de O.T."
      pagina: 12
    A.4:
      nombre: "Aviso de habilitación de campos para gestión de Actualización de O.T."
      pagina: 12
    A.5:
      nombre: "Aviso de Emisión de O.T."
      pagina: 13
    A.6:
      nombre: "Informe OT (Ejemplo)"
      pagina: 14
    B.1:
      nombre: "Anexo B – Requerimientos Funcionales / B.1 – Formatos (Pantalla Opinión Técnica p. 15–16; Pantalla Histórico de Opinión Técnica p. 16; Pantalla Ver informe OT p. 16–18)"
      pagina: 15
    "Archivo anexo CU-PRE-26_Anexo.pdf":
      nombre: "p.1: Menú + Opinión Técnica (versión A.1) + mockups A.3, A.4, A.5; p.2: Opinión Técnica (versión extendida con todos los apartados); p.3: Histórico de OT; p.4: Informe OT (ejemplo)"
      pagina: 1
  nota_derivados: "Los valores de `estado`, `procesos`, `entidades` y `catalogos` no provienen de campos explícitos del PDF: `estado` es un valor de control del análisis y los otros tres son agrupaciones construidas a partir de los flujos, reglas de negocio y el Anexo B.1. `modulo`, `submodulo`, `actor_principal` y `actores_secundarios` llevan su propia anotación."
  nota_paginas: "Los números de página corresponden a las páginas físicas de cada PDF, identificadas mediante extracción de texto página por página. Ninguno de los dos PDF incluye numeración de página explícita en el pie, por lo que no fue posible contrastarlos con una numeración impresa."
---

# Caso de Uso

## Información General

| Campo | Valor |
|--------|-------|
| Nombre | CU-PRE-26 Opinión Técnica |
| Código | CU-PRE-26 |
| Módulo | Preinversión *(valor derivado; el documento no tiene un campo explícito "Módulo" — ver nota sobre valores derivados más abajo)* |
| Fuente | CU-PRE-26_Opinio_n_Te_cnica.pdf — "DIRECCIÓN GENERAL DE INVERSIÓN Y CRÉDITO PÚBLICO — SISTEMA DE INFORMACIÓN DE INVERSIÓN PÚBLICA — ESPECIFICACIÓN DE CASOS DE USO: 'CU-PRE-26 Opinión Técnica'"; anexo de mockups CU-PRE-26_Anexo.pdf |
| Versión | 1.0 (Fecha en encabezado de página: MAYO 2026) |
| Identificación (texto literal del PDF) | CU-PRE-26 Opinión Técnica viabilizado |

> Nota de ambigüedad: el campo "Identificación" del PDF dice literalmente "CU-PRE-26 Opinión Técnica viabilizado"; la palabra "viabilizado" no aparece en el título de la portada ni en los encabezados de página ("CU-PRE-26 Opinión Técnica").

**Campos requeridos (según el PDF):**
- 1. Identificación del proyecto
  - 1.1. Antecedentes
  - 1.2. Problema Central
  - 1.3. Objetivo General
  - 1.4. Objetivos Específicos
- 2. Formulación del proyecto
  - 2.1. Análisis de Interesados
  - 2.2. Análisis de la Población
  - 2.3. Área de Influencia
  - 2.4. Análisis de Mercado
  - 2.5. Descripción Técnica
  - 2.6. Localización
  - 2.7. Análisis Ambiental
  - 2.8. Análisis de Riesgos
  - 2.9. Análisis Legal
  - 2.10. Presupuesto de Inversión
  - 2.11. Presupuesto de O&M
- 3. Evaluación
  - 3.1. Flujo de Beneficios
  - 3.2. Flujo de Caja e Indicadores
- 4. Programación
  - 4.1. Indicadores del proyecto
  - 4.2. Programación Financiera Preinversión
- 5. Documentos anexos
  - 5.1. Observaciones DGICP
  - 5.2. Respuesta Institución
- 6. Comentarios a Elegibilidad
  - 6.1. Comentarios DGICP a Elegibilidad
  - 6.2. Respuesta Institución
- Fecha de solicitud
- Fecha de ajustes
- Fecha de emisión OT
- N° de nota de OT
- Conclusiones

> Nota de ambigüedad (obligatoriedad): esta lista se titula "Campos requeridos", pero incluye "6.1. Comentarios DGICP a Elegibilidad", que el Anexo B.1 declara expresamente opcional ("Es opcional, en caso de existir observaciones"). Además, "5.2" y "6.2 Respuesta Institución" son obligatorios solo de forma condicional según el Anexo B.1 ("si se emitieron comentarios de DGICP"). El documento no aclara si "requeridos" significa "obligatorios" o "que deben existir en la pantalla". Ver Observaciones, ítem 49.

**Ruta de Acceso (según el PDF):**

> No especificado en el documento. El documento usa el patrón de tabla "Identificación" y no incluye una sección titulada "Ruta de Acceso". El Anexo A ("Menú de registro y gestión de proyectos") indica que "La imagen de referencia muestra la ruta prevista para seleccionar el tipo de Opinión Técnica (OT) a solicitar", con el menú: IDENTIFICACIÓN / FORMULACIÓN / EVALUACIÓN / PROGRAMACIÓN / GESTIÓN > VIABILIDAD / ELEGIBILIDAD / OT (Tipo de solicitud) / PRIORIZACIÓN.

> Nota sobre valores derivados: los valores `modulo` ("Preinversión") y `submodulo` ("Gestión") del Front Matter no provienen de un campo explícito del PDF. Se derivaron del prefijo del código "CU-PRE" y de RN17 ("El proceso de preinversión…"), y de las menciones "pestaña 'Gestión de Proyectos'" (FB paso 5), "Módulo de Gestión" (FA04 paso 4.1) y del menú "GESTIÓN" del Anexo A.

## Historial de Revisiones

| Fecha | Versión | Descripción | Autor | Firma |
|---|---|---|---|---|
| SEP 2025 | 1.0 | Versión Inicial | Equipo Preinversión | |

> Nota de ambigüedad: la versión del Historial (1.0) coincide con la de la portada y los encabezados de página (1.0). Sin embargo, la fecha del Historial ("SEP 2025") difiere de la fecha del encabezado de página ("Fecha: MAYO 2026"), sin que exista una entrada adicional en el Historial que registre un cambio posterior.

---

# Objetivo

> No especificado en el documento. El documento no incluye una sección titulada "Objetivo". (Ver "Descripción".)

---

# Descripción

Este caso de uso permitirá:

1. al "Viabilizador" o al "Técnico URP", solicitar Opinión Técnica a la DGICP y emitir respuestas a las observaciones hechas por el "Técnico PRE", y
2. al Técnico PRE, revisar, emitir observaciones y validar el contenido ingresado en las pantallas de "Identificación", "Formulación", "Evaluación" y "Programación", verificar los archivos adjuntos, así como los criterios de elegibilidad y posterior a eso le permitirá emitir la Opinión Técnica requerida.

# Actor Principal

> No especificado en el documento. La sección "Actores" del PDF lista cuatro actores sin designar uno como principal:

- Técnico URP
- Viabilizador
- Técnico PRE
- Coordinador PRE

---

# Actores Secundarios

> Nota sobre información derivada: el documento no clasifica actores como secundarios. Los cuatro actores de la sección "Actores" del PDF (Técnico URP, Viabilizador, Técnico PRE, Coordinador PRE) no se listan aquí porque el documento no los designa como principal ni como secundarios; figuran en "Actor Principal" y en `actores_documento` del Front Matter. Los actores de esta tabla se incluyen por decisión del análisis, según su participación en flujos, reglas o anexos.

| Actor | Fuente | Nota |
|---|---|---|
| Sistema | Flujos FB, FA01–FA04, RN07–RN10 | No figura en la sección "Actores" del documento; se incluye por ser quien ejecuta los pasos automáticos (notificaciones, habilitación de botones/campos, cambios de estado, validaciones). |
| Director DGICP | FA01 paso 1.5; Anexo B.1 ("Fecha de emisión de OT") | No figura en la sección "Actores"; se menciona como firmante de la "Nota de Opinión Técnica". |
| Jefe URP Viabilizador | Anexo A2, título del literal c | No figura en la sección "Actores"; el resto del documento usa "Viabilizador". No se puede determinar si es el mismo rol. |
| DGI | RN09 | No figura en la sección "Actores"; se menciona como administradora de la "bandeja de preinversión". |
| Institución Ejecutora / "Institución" | Anexo B.1, Anexo A2 | No figura como actor; se usa como destinatario de correos ("Estimados señores [Nombre de la Institución]") y como origen de las justificaciones. |

---

# Disparador

> No especificado en el documento como sección independiente. El Flujo Básico, paso 1, indica literalmente: "Al emitir Viabilidad/Elegibilidad, el sistema envía automáticamente el proyecto a opinión técnica." Por otra parte, RN04 y el Anexo B.1 ("Fecha de solicitud") hacen referencia a que el Viabilizador o Técnico URP da clic en el botón "Solicitar OT" (ver Observaciones sobre esta posible contradicción).

---

# Precondiciones

1. CU-PRE-03 Captura de proyectos a CU-PRE-23 Indicadores del Proyecto
2. CU-PRE-24 Viabilidad
3. CU-PRE-25 Elegibilidad

> Nota de ambigüedad: el documento lista estas referencias a otros casos de uso sin un verbo explícito (p. ej. "contar con", "haber ejecutado") que aclare si son precondiciones formales o referencias de origen de datos. Se incorporan por continuidad de formato, pero la redacción exacta de la condición no está especificada. El primer ítem se expresa como un rango ("CU-PRE-03 … a CU-PRE-23 …") sin enumerar los casos de uso intermedios; se transcribe tal cual sin completarlo.

---

# Flujo Principal

**Flujo Básico – FB Opinión Técnica y Actualización de Opinión Técnica**

1. **Viabilizador:** Al emitir Viabilidad/Elegibilidad, el sistema envía automáticamente el proyecto a opinión técnica.
2. **Sistema:** Notifica al "Coordinador PRE" por medio de correo electrónico sobre la solicitud y habilita al Técnico PRE el campo "Comentarios DGICP" para proceder a la revisión de la información registrada. Cambia el estado del proyecto a "Proyecto viable" o "Proyecto elegible" según sea el caso.
3. **Técnico PRE:** Ingresa a la pantalla "Captura de proyectos" (CU-PRE-03), con la lista de los proyectos en el estado "Proyecto viable" o "Proyecto elegible" según sea el caso.
4. **Técnico PRE:** Da clic en el proyecto sobre el cual va a emitir Opinión Técnica.
5. **Técnico PRE:** Ingresa a pestaña "Gestión de Proyectos" en la sección "Opinión Técnica" (Ver Anexo A.1)
6. **Técnico PRE:** Revisa y valida la información ingresada del proyecto, y envía comentarios al Técnico URP/Viabilizador (para Elegibilidad) o emite Opinión Técnica. (Anexo A1).

> Nota de ambigüedad: en el paso 1 el actor declarado es "Viabilizador", pero la descripción indica que es "el sistema" quien envía automáticamente el proyecto a opinión técnica.

---

# Flujos Alternos

## FA01 — Botón OT Favorable

*(Título literal del PDF: "Flujo Alternativo 1 – FA01 Botón OT Favorable")*

**Condición**

> No especificado en el documento.

**Flujo**

1.1. **Técnico PRE:** El Técnico PRE revisa la información registrada y documentos anexos, y registra información en el campo "Conclusiones" y hace clic en el botón "Guardar".
1.2. **Sistema:** Habilita el botón "Visto bueno OT".
1.3. **Coordinador PRE:** Revisa la información y hace clic en el botón "Visto bueno OT".
1.4. **Sistema:** Notifica al Técnico PRE el visto bueno de la OT y habilita el botón "OT favorable"
1.5. **Técnico PRE:** Carga archivo "Nota de Opinión Técnica" firmada por el Director DGICP y hace clic en el botón "OT favorable".
1.6. **Sistema:** Cambia el estado del proyecto a "Proyecto con OT". Notifica a todos los actores por medio de correo electrónico la emisión de la Opinión Técnica. Muestra al Técnico PRE el mensaje del Anexo A.5. "El proyecto cuenta con Opinión Técnica Favorable, continúe con la Priorización".

> Nota de ambigüedad: el flujo se refiere a un "botón 'Visto bueno OT'", mientras que los mockups del Anexo A.1 muestran "Visto Bueno OT:" como una casilla de verificación. El archivo cargado se denomina "Nota de Opinión Técnica" en el paso 1.5 y "Nota de OT" en el mockup. El texto citado del Anexo A.5 en el paso 1.6 ("…con Opinión Técnica Favorable…") no coincide con el texto del mockup A.5 ("El proyecto cuenta con Opinión Técnica, continúe con la Priorización").

**Resultado**

> No especificado en el documento.

## FA02 — Botón Guardar para "Técnico PRE" o "Técnico URP/Viabilizador"

*(Título literal del PDF: "Flujo Alternativo 2 – FA02 Botón Guardar para 'Técnico PRE' o 'Técnico URP/Viabilizador'")*

**Condición**

> No especificado en el documento.

**Flujo**

2.1. **Técnico PRE o Técnico URP/Viabilizador:** Da clic en el botón "Guardar".
2.2. **Sistema:** Guarda la información registrada por el Técnico PRE en el campo "Comentarios DGICP" o por el Técnico URP/Viabilizador en el campo "Justificación"/"Respuesta institución".

**Resultado**

> No especificado en el documento.

## FA03 — Botón Enviar Comentarios (Técnico PRE)

*(Título literal del PDF: "Flujo Alternativo 3 – FA03 Botón Enviar Comentarios (Técnico PRE)")*

**Condición**

> No especificado en el documento.

**Flujo**

3.1. **Técnico PRE:** Da clic en el botón "Enviar Comentarios". Este botón solo es visible para el "Técnico PRE" y "Coordinador PRE".
3.2. **Sistema:** Guarda la información registrada por el Técnico PRE en el campo "Comentarios DGICP", y habilita al Técnico URP: la edición de todos los campos del CU-PRE-04 "Identificación" al CU-PRE-23 "Indicadores del Proyecto", así como el campo "Justificación Institución" / "Respuesta institución" de la pantalla del Anexo A.1, y al Viabilizador se habilita la pantalla CU-PRE-24 "Viabilidad" y CU-PRE-25 "Elegibilidad" para realizar los ajustes respectivos. Notifica por medio de correo electrónico al Técnico URP y al Viabilizador el envío de comentarios al proyecto y a la Elegibilidad, si aplica.
3.3. **Sistema:** Cambia el estado del proyecto a "Observado"

**Resultado**

> No especificado en el documento.

## FA03.1 — Enviar Ajustes (Técnico URP/Viabilizador)

*(Título literal del PDF: "Flujo Alternativo 3.1 – FA03.1 Enviar Ajustes (Técnico URP/Viabilizador)")*

> Nota de numeración: en el PDF los pasos de este flujo están numerados "3.1.1" y "3.1.3"; no existe un paso "3.1.2". Se conserva la numeración literal del PDF sin renumerar.

**Condición**

> No especificado en el documento.

**Flujo**

3.1.1. **Técnico URP y/o Viabilizador:**
- Técnico URP: Ajusta la información de las pantallas del proyecto (CU-PRE-04 "Identificación" a CU-PRE-23 "Indicadores del Proyecto") y responde los comentarios de los apartados de la pantalla del Anexo A.1 de este caso de uso en la columna "Justificación Institución", hace clic en "Guardar ajustes", sube documentación ajustada en el CU-PRE-24 "Viabilidad" y da clic en el botón "Solicitar Viabilidad" del CU-PRE-24 "Viabilidad". Este botón solo es visible para el Técnico URP. (Ver RN10-11 de CU-PRE-24 "Viabilidad")
- Viabilizador: Revisa y emite Viabilidad CU-PRE-24 "Viabilidad" a la información ajustada; y si hay observaciones a la selección de criterios de Elegibilidad (CU-PRE-25 "Elegibilidad") realiza ajustes, responde los comentarios de los apartados de la pantalla del Anexo A.1 de este caso de uso, hace clic en "Guardar ajustes" y emite nuevamente Elegibilidad. (Ver RN10 y 15 de CU-PRE-25 "Elegibilidad")

3.1.3. **Sistema:** Guarda la información registrada por el Técnico URP en todos los campos ajustados, así como la nueva documentación, y notifica por medio de correo electrónico al viabilizador, al Técnico PRE y al Coordinador PRE la atención de comentarios. El viabilizador podrá ver, más no editar los comentarios emitidos por el Técnico URP en la pantalla de OT.
El proyecto debe volver al proceso de aprobación (viabilidad y OT). No va a elegibilidad si el Técnico PRE no emitió comentarios para este filtro. Los estados cambiarán conforme al proceso de gestión en el que se encuentre ("Proyecto viable" y "Proyecto Elegible")

> Nota de ambigüedad: el flujo menciona un botón "Guardar ajustes" que no aparece en ningún mockup del Anexo A.1 (los mockups muestran "GUARDAR" y "ENVIAR AJUSTES"). El título del flujo es "Enviar Ajustes", pero ningún paso menciona hacer clic en el botón "Enviar ajustes".

**Resultado**

> No especificado en el documento.

## FA04 — Actualización de Opinión Técnica

*(Título literal del PDF: "Flujo Alternativo 4 – FA01 Actualización de Opinión Técnica")*

> Nota de ambigüedad: el PDF rotula este flujo como "Flujo Alternativo 4" pero con el identificador "FA01", que ya está asignado al flujo "Botón OT Favorable". En el Anexo A (RN3) este mismo flujo se denomina "Sub Flujo 4". Se usa "FA04" en el encabezado únicamente para distinguirlo; los pasos conservan su numeración literal (4.1–4.5).

**Condición**

> No especificado en el documento.

> ⚠️ PENDIENTE DE RESOLUCIÓN: existe una contradicción interna con Anexo A – RN3 sobre quién puede gestionar la Actualización de OT. RN3 indica "Si el viabilizador o Técnico URP selecciona la opción 2: Actualización de OT…", mientras que en este flujo (pasos 4.1, 4.3 y 4.5) solo actúa el Técnico URP. Ver Observaciones, ítem 48. No implementar hasta resolver.

**Flujo**

4.1. **Técnico URP:** Ingresa al Módulo de Gestión y selecciona en el menú de OT la opción Actualización de OT.
4.2. **Sistema:** Muestra el mensaje del Anexo A.3
4.3. **Técnico URP:** Da clic en el botón "Sí"
4.4. **Sistema:** Valida que exista una OT previa para la etapa que se está gestionando. Muestra el mensaje del Anexo A.4. Habilita los campos indicados en la hoja de cálculo CU-PRE-3.5 "Selección y registro de etapas" ANEXO, conforme al "Tipo de Iniciativa" (y en el caso de Iniciativa tipo Proyecto, la etapa) y "Campos a habilitar para Actualización de O.T."
4.5. **Técnico URP:** Realiza ajustes en los campos habilitados, según aplique. Continúa con el proceso de Viabilidad (CU-PRE-24 "Viabilidad").

**Resultado**

> No especificado en el documento.

---

# Excepciones

> No especificado en el documento. El documento no incluye una sección de Excepciones. A continuación se listan únicamente las situaciones excepcionales que el propio documento describe dentro de sus Reglas de Negocio y flujos:

| Código | Descripción | Consecuencia |
|---|---|---|
| RN08 (sin código de excepción propio) | Faltan dos días para el vencimiento del período para atender observaciones (5 días hábiles a partir del envío de comentarios por parte del Técnico PRE, según lineamientos) y el Técnico URP no ha dado clic en el botón "Enviar Ajustes". | El Sistema remite correo de advertencia al Técnico URP y al Viabilizador (Anexo A.2 f). |
| RN09 (sin código de excepción propio) | Vencido el plazo de atención de observaciones, según lineamientos. | El Sistema notifica al Técnico URP y al Viabilizador que se deberá tramitar nuevamente la solicitud (Anexo A.2 g); elimina las solicitudes del módulo de gestión del proyecto (Viabilidad, soportes y OT observado), la archiva y deja de visualizarse en la bandeja de preinversión (CU-PRE-03 "Bandeja de Preinversión"). |
| FA04 paso 4.4 (sin código de excepción propio) | El Sistema valida que exista una OT previa para la etapa que se está gestionando. | > No especificado en el documento (no se describe qué ocurre si no existe OT previa). |

---

# Postcondiciones

1. CU-PRE-26.5 Priorización
2. CU-PRO-01 Elaboración/Actualización del PRIPME
3. CU-PRO-08 Generación de Escenarios para Corto Plazo
4. CU-PRO-17 Programación Mensual Financiera del PAIP

> Nota de ambigüedad: igual que en Precondiciones, el documento lista referencias a otros casos de uso sin un verbo que describa la condición resultante.

---

# Reglas de Negocio

## Reglas del Negocio (sección principal)

**RN01**
- Descripción: Los actores podrán visualizar la información de las Instituciones Ejecutoras según credenciales.
- Origen: Reglas del Negocio, p. 7.

**RN02**
- Descripción: Únicamente los actores Técnico PRE y Coordinador PRE podrán registrar información en los campos "Comentarios DGICP" y "Conclusiones".
- Origen: Reglas del Negocio, p. 7.

**RN03**
- Descripción: Únicamente el actor Técnico URP podrá registrar información en el campo "Justificación Institución".
- Origen: Reglas del Negocio, p. 7.

**RN04**
- Descripción: El botón "Solicitar OT" se activará una vez que el Técnico URP haya anexado la Nota de Solicitud OT en el campo "Nota de solicitud de OT". Este botón será visible para el Técnico URP y Viabilizador.
- Origen: Reglas del Negocio, p. 7.

**RN05**
- Descripción: El botón "Enviar comentarios" será visible únicamente para el Técnico PRE y Coordinador PRE y se activará una vez que el proyecto esté en el estado "Proyecto viable" o "Proyecto Elegible", según corresponda.
- Origen: Reglas del Negocio, p. 7.

**RN06**
- Descripción: Una vez el Técnico URP reciba comentarios del Técnico PRE, se activará el botón "Enviar ajustes".
- Origen: Reglas del Negocio, p. 7.

**RN07**
- Descripción: El Sistema notificará mediante correo electrónico (Anexo A.2) en los siguientes eventos:
  - a. Cuando el Técnico URP solicita OT, se envía a Técnico PRE y al Coordinador PRE (Anexo A.2 a)
  - b. Cuando el Coordinador PRE asigna el caso a Técnico PRE, se envía a Técnico PRE (Anexo A.2 b)
  - c. Cuando el Técnico PRE envía comentarios, se envía a Técnico URP y Viabilizador (Anexo A.2 c)
  - d. Cuando el Técnico URP envía ajustes, se envía a Técnico PRE, al viabilizador y a Coordinador PRE (Anexo A.2 d)
  - e. Cuando el Técnico PRE da clic en OT Favorable, se envía a todos los actores (Anexo A.2 e)
- Origen: Reglas del Negocio, p. 7.

**RN08**
- Descripción: El Sistema remitirá correo de advertencia al Técnico URP y al Viabilizador dos días antes del vencimiento del período para atender observaciones (5 días hábiles a partir del envío de comentarios por parte del Técnico PRE, según lineamientos), siempre que el Técnico URP no haya dado clic en el botón "Enviar Ajustes". (Anexo A.2 f)
- Origen: Reglas del Negocio, p. 7.

**RN09**
- Descripción: Una vez vencido el plazo de atención de observaciones, según lineamientos, el Sistema notificará al Técnico URP y al Viabilizador, vía correo electrónico, que se deberá tramitar nuevamente la solicitud. (Anexo A.2 g). El sistema eliminará las solicitudes del módulo de gestión del proyecto (Viabilidad, soportes y OT observado), la archivará y dejará de visualizarse la solicitud en la bandeja de preinversión, administrada por la DGI (CU-PRE03" Bandeja de Preinversión").
- Origen: Reglas del Negocio, p. 7.

**RN10**
- Descripción: Una vez el Técnico PRE dé clic en el botón "OT Favorable", el botón "Enviar comentarios", el botón "Enviar Ajustes" y el botón "Guardar" se inhabilitan.
- Origen: Reglas del Negocio, p. 7.

**RN11**
- Descripción: En caso de emitirse OT para la etapa de Ejecución, el proyecto estará disponible para visualización y/o actualización en la Captura de Proyectos (CU-PRE-03).
- Origen: Reglas del Negocio, p. 8.

**RN 12** *(identificador literal del PDF, con espacio)*
- Descripción: La sección "Comentarios Elegibilidad" se mostrará en el Anexo A.1 únicamente cuando se realice la gestión de OT por primera vez, para el proyecto.
- Origen: Reglas del Negocio, p. 8.

**RN13**
- Descripción: Para la revisión de los proyectos categorizados como "Proyecto de emergencia" en el CU-PRE-01 "Registro de Proyectos", el Sistema cambiará los campos del formulario del Anexo 1 de este caso de uso por los campos del Anexo A.4 del CU-PRE-3.5 "Selección y registro de etapas"
- Origen: Reglas del Negocio, p. 8.

**RN14**
- Descripción: Si el Técnico PRE realiza comentarios a los campos del proyecto, pero no tiene comentarios respecto de los criterios de elegibilidad, el proyecto vuelve al Técnico URP y el proceso de aprobación solo involucra Viabilidad - OT.
  Por el contrario, si el Técnico PRE realiza comentarios a los campos del proyecto y a los criterios de elegibilidad, el proyecto vuelve al Técnico URP y el proceso de aprobación debe surtir todos los filtros (Viabilidad, elegibilidad y OT).
  Si el Técnico PRE realiza comentarios solamente a los criterios de elegibilidad, el proyecto vuelve a dicho filtro para que el viabilizador realice los ajustes y vuelva enviar a OT.
- Origen: Reglas del Negocio, p. 8.

**RN15**
- Descripción: El proyecto se puede devolver cuantas veces los viabilizadores consideren necesario. La información registrada en el campo "Observaciones" se debe guardar cada vez que se devuelva. El sistema debe mostrar cuántas veces se ha devuelto.
- Origen: Reglas del Negocio, p. 8.

**RN16**
- Descripción: El ícono de lápiz, corresponde al enlace para consultar las pantallas respectivas y desde allí hacer los ajustes sugeridos.
- Origen: Reglas del Negocio, p. 8.

**RN17**
- Descripción: El proceso de preinversión culmina con la emisión de la Opinión Técnica- OT, realizada por el Técnico PRE. Sin embargo el sistema, a través de mensaje del Anexo A.5, permite que pueda continuar con la emisión de la priorización.
- Origen: Reglas del Negocio, p. 8.

## Reglas de Negocio para este caso (Anexo A – Menú de registro y gestión de proyectos)

> Nota: el Anexo A contiene su propio bloque de reglas identificadas como "RN1", "RN2" y "RN3", cuyos identificadores coinciden numéricamente con RN01–RN03 de la sección principal pero tienen contenido distinto. Se transcriben con el prefijo "Anexo A –" para distinguirlas; no se renumeran.

**Anexo A – RN1**
- Descripción: Si la Opinión Técnica se está solicitando por primera vez, la opción 2: Actualización de OT, estará desactivada.
- Origen: Anexo A, p. 9.

**Anexo A – RN2**
- Descripción: Si se está solicitando una Actualización de Opinión Técnica, la opción 1: Opinión Técnica, estará desactivada.
- Origen: Anexo A, p. 9.

**Anexo A – RN3**
- Descripción: Si el viabilizador o Técnico URP selecciona la opción 2: Actualización de OT, el Sistema habilitará los campos que podrán ser editables, según Sub Flujo 4 paso 4.4.
- Origen: Anexo A, p. 9.

> ⚠️ PENDIENTE DE RESOLUCIÓN: existe una contradicción interna con FA04 "Actualización de Opinión Técnica" sobre el actor: esta regla atribuye la selección de la Actualización de OT al "viabilizador o Técnico URP", pero en FA04 solo actúa el Técnico URP. Ver Observaciones, ítem 48. No implementar hasta resolver.

---

# Campos

> El Anexo B.1 distingue las columnas "Tipo", "Formato", "Editable" y "Detalle". No incluye columnas "Obligatorio" ni "Valor por defecto": la obligatoriedad se toma únicamente de lo que el propio "Detalle" indica; en ausencia de indicación se escribe "No especificado". La columna "Editable" se conserva en Observaciones.

## Pantalla Opinión Técnica (Anexo B.1 — corresponde a la pantalla del Anexo A.1)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| CUP | Muestra el Código Único de Proyecto. Según se asignó en el CU-PRE-01 "Registro de Proyectos" | Numérico | Numérico | No especificado | No especificado | Editable: No (según Anexo B.1). En el mockup extendido del archivo anexo (p. 2) el rótulo es "Código:" en lugar de "CUP:". |
| Nombre del proyecto | Muestra el nombre del proyecto. Según se asignó en el CU-PRE-01 "Registro de Proyectos" | Alfanumérico | Alfanumérico | No especificado | No especificado | Editable: No (según Anexo B.1). En el mockup extendido (archivo anexo p. 2) el rótulo es "Nombre:". |
| Unidad Ejecutora | Procede del campo Unidad Ejecutora del CU-PRE-01 "Registro de Proyectos" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Etapa actual | Muestra la etapa actual del proyecto. Toma el dato según la etapa en la que se haya ingresado desde el CU-PRE-3.5 "Selección y registro de etapas" (es decir, si se ha ingresado en la etapa de PERFIL la "Etapa Actual" será PERFIL; si donde se ingresó es en DISEÑO, la "Etapa Actual" será DISEÑO. | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). El paréntesis del Detalle no se cierra en el original. |
| Etapa futura | Muestra la etapa futura del proyecto. Toma el dato según la etapa posterior a la que se haya ingresado desde el CU-PRE-3.5 "Selección y registro de etapas" (es decir, si se ha ingresado en la etapa de PERFIL, y de acuerdo con la Ruta de Preinversión la siguiente es PREFACTIBILIDAD, la "Etapa Futura" será PREFACTIBILIDAD; si se ha ingresado en la etapa de DISEÑO la "Etapa Futura" será EJECUCIÓN). | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). Los mockups muestran "Etapa Actual: Perfil / Etapa Futura: Diseño", lo que no coincide con el ejemplo del propio Detalle (Perfil → Prefactibilidad). Ver Observaciones. |
| Apartados | Campo que muestra la información registrada en cada uno de los apartados de las diversas pestañas de "Identificación", "Formulación", "Evaluación" y "Programación". Funcionará como un visor, por lo que no permitirá la edición de ningún campo. Además, en cada campo de dicha columna, debe contener un link que direccione a la información de la pantalla donde se encuentra cada uno de ellos, para poder realizar los ajustes allí. | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). El link corresponde al "ícono de lápiz" (RN16). |
| Comentarios DGICP | Permite el registro de comentarios al rol Técnico PRE y Coordinador PRE. Este campo solo se habilita para este rol. Campos obligatorios. | Texto | Texto | Sí ("Campos obligatorios", según Detalle) | No especificado | Editable: Sí (según Anexo B.1). El Detalle nombra dos roles pero dice "solo se habilita para este rol" (singular). Nota de ambigüedad (obligatoriedad): el Detalle lo declara obligatorio ("Campos obligatorios"), pero el flujo permite emitir la OT sin enviar comentarios (FB paso 6: "envía comentarios… o emite Opinión Técnica"; FA01 no pasa por "Enviar Comentarios"). No se puede determinar en qué condición es obligatorio. Ver Observaciones, ítem 51. |
| Justificación Institución | Permite el registro de justificaciones por parte de la Institución Ejecutora que tenga el rol Técnico URP. Este campo solo se habilita para este rol. Si la observación no puede ser ajustada, el Técnico URP utilizará este campo para justificar. | Texto | Texto | No especificado | No especificado | Editable: Sí (según Anexo B.1). En FA02/FA03 también se denomina "Justificación" / "Respuesta institución". |
| Comentarios DGICP a Elegibilidad | Permite el registro de comentarios al rol Técnico PRE y Coordinador PRE. Este campo solo se habilita para estos roles. Es opcional, en caso de existir observaciones. | Texto | Texto | No ("Es opcional", según Detalle) | No especificado | Editable: Sí (según Anexo B.1). Rótulo en mockup: "COMENTARIOS DGICP A ELEGIBILIDAD". Sección visible solo en la primera gestión de OT (RN 12). Nota de ambigüedad: figura en la lista "Campos requeridos" (6.1) pese a ser opcional según este Detalle. Ver Observaciones, ítem 49. |
| Respuesta Institución | Permite el registro de respuesta por parte de la Institución Ejecutora que tenga los roles de Técnico URP o Viabilizador. Este campo solo se habilita para estos roles. Campo obligatorio, si se emitieron comentarios de DGICP. | Texto | Texto | Sí, condicional ("si se emitieron comentarios de DGICP") | No especificado | Editable: Sí (según Anexo B.1). En el mockup, la columna correspondiente de "Comentarios Elegibilidad" se rotula "JUSTIFICACIÓN INSTITUCIÓN". Contradice RN03 (solo Técnico URP registra "Justificación Institución"). Ver Observaciones. |
| Comentarios DGICP a "documentos anexos" | Permite el registro de comentarios a la documentación anexa al rol Técnico PRE. y Coordinador PRE. Este campo solo se habilita para estos roles. | Texto | Texto | No especificado | No especificado | Editable: Sí (según Anexo B.1). Rótulo en mockup: "COMENTARIOS DGICP A DOCUMENTACIÓN ANEXA"; en "Campos requeridos": "5.1. Observaciones DGICP". |
| Conclusiones | Permite el registro de conclusiones generales al proyecto. las razones por las cuales se decide emitir OT favorable. Campo obligatorio. | Texto | Texto | Sí ("Campo obligatorio", según Detalle) | No especificado | Editable: Sí (según Anexo B.1). En el Anexo B de "Ver informe OT" se hace referencia a este campo como "Conclusiones del Técnico PRE" del Anexo A.1. |
| Fecha de solicitud | Muestra la fecha de solicitud de la gestión de OT. El Sistema la mostrará según la fecha en que el Viabilizador o Técnico URP dio clic en el botón "Solicitar OT". Formato DD/MM/AAAA. Campo obligatorio. | Fecha | Fecha | Sí ("Campo obligatorio", según Detalle) | No especificado | Editable: No (según Anexo B.1). Formato textual: DD/MM/AAAA. Nota de ambigüedad (obligatoriedad): el campo no es editable y su valor lo determina el Sistema (fecha del clic en "Solicitar OT"), pero el Detalle lo declara "Campo obligatorio"; no se puede determinar qué exige esa obligatoriedad a un usuario que no puede capturarlo. Ver Observaciones, ítem 50. |
| Fecha de ajustes | Muestra la última fecha de envío de los ajustes registrados por el Técnico URP. Formato DD/MM/AAAA | Fecha | Fecha | No especificado | No especificado | Editable: No (según Anexo B.1). Formato textual: DD/MM/AAAA. |
| Fecha de emisión de OT | Muestra la fecha de emisión de la OT Favorable, que corresponde al momento en que el Técnico PRE cargó la nota firmada por el Director de la DGICP y se cambia el estado a "Proyecto con OT". Será registrada por el Técnico PRE o Coordinador PRE. Formato DD/MM/AAAA | Fecha | Fecha | No especificado | No especificado | Editable: Sí (según Anexo B.1). Contradicción interna: el Detalle comienza con "Muestra…" y la asocia a un evento del sistema (carga de la nota / cambio de estado), pero también indica "Será registrada por el Técnico PRE o Coordinador PRE". Ver Observaciones. |
| N° de nota de OT | Muestra el número de nota de OT. | Alfanumérico | Alfanumérico | No especificado | No especificado | Editable: Sí (según Anexo B.1). Posible contradicción: el Detalle dice "Muestra…" (lenguaje de campo de solo consulta) mientras "Editable" indica "Sí"; no se especifica quién registra el valor. Ver Observaciones. |

> Campos visibles en los mockups del Anexo A.1 que no están documentados en el Anexo B.1: "Nota de solicitud de OT" (adjunto), "Documento de Preinversión (a revisar)" (adjunto), "Otros documentos anexos" (adjunto), "Justificación Institución" de la sección "Documentos anexos", "Nota de OT:" (adjunto), "Visto Bueno OT:" (casilla). Se listan en "Pantallas" tal como aparecen, sin atribuirles tipo ni formato.

## Pantalla Histórico de Opinión Técnica (Anexo B.1 — corresponde a la pantalla del Anexo A1.5)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| N° de nota de OT | Muestra el número de OT. El Sistema lo toma del campo "N° de nota de OT" del Anexo A1. | Alfanumérico | Alfanumérico | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Tipo de gestión | El Sistema lo mostrará de manera automática según el Tipo de Opinión Técnica seleccionada, como "Opinión Técnica" o "Actualización de OT". | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Etapa Actual | Lo muestra del campo "Etapa Actual" del Anexo A1. | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Etapa Futura | Lo muestra del campo "Etapa Futura" del Anexo A1. | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Fecha de emisión OT | Muestra la fecha de nota de OT. El Sistema lo toma del campo "Fecha de emisión OT" del Anexo A1. | Fecha | Fecha | No especificado | No especificado | Editable: No (según Anexo B.1). Rótulo de columna en el mockup: "FECHA DE NOTA". |
| Monto de Inversión | Muestra el monto de inversión del proyecto. El Sistema lo toma del campo "Total" del Anexo A4 del CU-PRE-17 "Presupuesto de inversión" | Moneda | Moneda | No especificado | No especificado | Editable: No (según Anexo B.1). Rótulo de columna en el mockup: "INVERSIÓN ESTIMADA" (documento principal) / "ESTIMADA" (archivo anexo, p. 3). |
| Ver Informe | Muestra un ícono para ver el Informe seleccionado en el anexo A.1.5. El informe se muestra en el Anexo A6. | Ícono | Ícono | No especificado | No especificado | Editable: No (según Anexo B.1). Nota de ambigüedad: "Ícono" es un valor de Tipo/Formato atípico respecto al resto de la tabla (Texto, Numérico, Alfanumérico, Fecha, Moneda); se transcribe literalmente. El Anexo B lo denomina "anexo A.1.5" mientras el título del Anexo es "Anexo A1.5", y "Anexo A6" mientras el título es "Anexo A.6". |

## Pantalla Ver informe OT (Ejemplo) (Anexo B.1 — corresponde al Anexo A.6)

| Campo | Descripción | Tipo | Formato | Obligatorio | Valor por defecto | Observaciones |
|---|---|---|---|---|---|---|
| Nombre del proyecto | Muestra el Código Único de Proyecto. Según se asignó en el CU-PRE-01 "Registro de Proyectos" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). El Detalle describe el Código Único de Proyecto, no el nombre; parece intercambiado con el Detalle del campo "CUP". Ver Observaciones. |
| CUP | Muestra el nombre del proyecto. Según se asignó en el CU-PRE-01 "Registro de Proyectos" | Numérico | Numérico | No especificado | No especificado | Editable: No (según Anexo B.1). El Detalle describe el nombre del proyecto, no el CUP. Ver Observaciones. |
| Unidad Ejecutora | > No especificado en el documento. (El Detalle está vacío en el Anexo B.1.) | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Etapa Actual | Muestra la etapa actual del proyecto. Toma el dato según la etapa en la que se haya ingresado desde el CU-PRE-3.5 "Selección y registro de etapas"(es decir, si se ha ingresado en la etapa de PERFIL la "Etapa Actual" será PERFIL; si donde se ingresó es en DISEÑO, la "Etapa Actual" será DISEÑO. | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Etapa Futura | Muestra la etapa futura del proyecto. Toma el dato según la etapa posterior a la que se haya ingresado desde el CU-PRE-3.5 "Selección y registro de etapas" (es decir, si se ha ingresado en la etapa de PERFIL, y de acuerdo con la Ruta de Preinversión la siguiente es PREFACTIBILIDAD, la "Etapa Futura" será PREFACTIBILIDAD; si se ha ingresado en la etapa de DISEÑO la "Etapa Futura" será EJECUCIÓN). | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). El ejemplo del Anexo A.6 muestra "Etapa actual: Perfil / Etapa futura: Ejecución". |
| Problema central | Procede del campo Unidad Ejecutora del CU-PRE-01 "Registro de Proyectos" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). El origen indicado ("campo Unidad Ejecutora") no corresponde al contenido mostrado en el ejemplo A.6 (texto del problema). Ver Observaciones. |
| Objetivo General | Viene del campo "Objetivo General" del CU-PRE-04 "Identificación" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Descripción del proyecto | Viene del campo "Descripción del proyecto" del CU-PRE-04 "Identificación" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Productos | Muestra la información de las columnas "Nombre del producto" del Anexo A.1, del CU-PRE-23 "Indicadores del Proyecto" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Tamaño | Viene del campo "Capacidad de producción" del CU-PRE-11 "Descripción técnica" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Población Objetivo | Viene del campo "Población objetivo" incluyendo la Descripción, Ubicación y N° de personas del CU-PRE-07 "Población Objetivo" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Localización | Proviene del campo "Macrolocalización" y "Microlocalización" del CU-PRE-12 "Localización" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Inversión Estimada | Proviene del campo "Inversión Estimada (Precios de Mercado)" del CU-PRE-17 "Presupuesto de inversión" | Moneda | Moneda | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Resumen del presupuesto | Muestra la tabla del Anexo A.4 del CU-PRE-17 "Presupuesto de inversión" | Moneda | Moneda | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Costos de operación | Viene del campo "Total (precios de Mercado)" de los Costos de Operación del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" | Moneda | Moneda | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Costos de mantenimiento | Viene del campo "Total (precios de Mercado)" de los Costos de Mantenimiento del CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" | Moneda | Moneda | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Fuente estimada de financiamiento | Proviene del campo "Fuente de financiamiento" del CU-PRE-17 "Presupuesto de inversión" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Fuente estimada de recursos | Proviene del campo "Fuente de recursos" del CU-PRE-17 "Presupuesto de inversión" | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Indicadores de evaluación | Procede de los campos de la sección "Indicadores de evaluación" del CU-PRE-21 "Flujo de Caja y cálculo de Indicadores", siguientes: VANS, TIRS, RB/C | Moneda / Porcentaje / Decimal | Moneda / Porcentaje / Decimal | No especificado | No especificado | Editable: No (según Anexo B.1). Nota de ambigüedad: el Tipo y el Formato contienen tres valores ("Moneda Porcentaje Decimal") en una sola celda; "Decimal" no se usa en el resto de la tabla. Se transcribe literalmente; el documento no especifica explícitamente qué tipo corresponde a cada indicador. |
| Fecha de solicitud | Fecha en que el Viabilizador/Técnico URP dio clic en el botón "Solicitar OT" por primera vez. | Fecha | Fecha | No especificado | No especificado | Editable: No (según Anexo B.1). Este Detalle añade "por primera vez", ausente en el campo homónimo de la Pantalla Opinión Técnica. |
| Fecha de ajustes | Muestra la última fecha de envío de los ajustes registrados por el Técnico URP. Formato DD/MM/AAAA | Fecha | Fecha | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Fecha de emisión de OT | Muestra la fecha de emisión de la OT Favorable, que corresponde al momento en que el Técnico PRE cargó la nota firmada por el Director de la DGICP y se cambia el estado a "Proyecto con OT". Será registrada por el Técnico PRE o Coordinador PRE. Formato DD/MM/AAAA | Fecha | Fecha | No especificado | No especificado | Editable: No (según Anexo B.1). El mismo campo es "Editable: Sí" en la Pantalla Opinión Técnica. |
| Costo del Estudio | Muestra el valor del campo "Costo de la etapa" del CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto" para la etapa actual. Es decir, si el proyecto tiene como etapa actual PERFIL se mostrará el costo de la etapa de PERFIL del CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto" | Moneda | Moneda | No especificado | No especificado | Editable: No (según Anexo B.1). |
| Opinión Técnica | Mostrará el texto "FAVORABLE" | Texto | Texto | No especificado | "FAVORABLE" (según Detalle) | Editable: No (según Anexo B.1). En el ejemplo A.6 se muestra "Favorable" (no en mayúsculas). |
| Conclusiones del Técnico PRE | Procede del campo "Conclusiones del Técnico PRE" del Anexo A.1 de este caso de uso. | Texto | Texto | No especificado | No especificado | Editable: No (según Anexo B.1). En el Anexo A.1 y su Anexo B el campo se denomina "Conclusiones". |

> Campo visible en el mockup del Anexo A.6 no documentado en el Anexo B.1: "Convenio" (valor de ejemplo "BCIE 2215").

---

# Validaciones

| Campo | Validación | Mensaje esperado |
|---|---|---|
| Comentarios DGICP | "Campos obligatorios" (Anexo B.1). Ambigüedad: el flujo permite emitir OT sin enviar comentarios (FB paso 6; FA01); no se especifica la condición de obligatoriedad (ver Observaciones, ítem 51). | > No especificado en el documento. (Sin Anexo/mockup propio asociado.) |
| Respuesta Institución | "Campo obligatorio, si se emitieron comentarios de DGICP" (Anexo B.1). | > No especificado en el documento. (Sin Anexo/mockup propio asociado.) |
| Conclusiones | "Campo obligatorio" (Anexo B.1). Se registra antes de "Guardar" en FA01 paso 1.1. | > No especificado en el documento. (Sin Anexo/mockup propio asociado.) |
| Fecha de solicitud | "Campo obligatorio"; Formato DD/MM/AAAA (Anexo B.1). Ambigüedad: campo no editable y calculado por el Sistema; no se especifica qué valida la obligatoriedad (ver Observaciones, ítem 50). | > No especificado en el documento. |
| Fecha de ajustes / Fecha de emisión de OT | Formato DD/MM/AAAA (Anexo B.1). | > No especificado en el documento. |
| Botón "Solicitar OT" | Se activa una vez que el Técnico URP haya anexado la Nota de Solicitud OT en el campo "Nota de solicitud de OT" (RN04). | > No especificado en el documento. |
| Botón "Enviar comentarios" | Se activa una vez que el proyecto esté en el estado "Proyecto viable" o "Proyecto Elegible", según corresponda (RN05). | > No especificado en el documento. |
| Botón "Enviar ajustes" | Se activa una vez el Técnico URP reciba comentarios del Técnico PRE (RN06). | > No especificado en el documento. |
| Botón "Visto bueno OT" | Se habilita después de que el Técnico PRE registre "Conclusiones" y haga clic en "Guardar" (FA01 pasos 1.1–1.2). | > No especificado en el documento. |
| Botón "OT favorable" | Se habilita tras el visto bueno del Coordinador PRE (FA01 paso 1.4). | > No especificado en el documento. |
| Tipo de solicitud (menú OT) | Opción 2 "Actualización de OT" desactivada si la OT se solicita por primera vez; opción 1 "Opinión Técnica" desactivada si se solicita actualización (Anexo A – RN1, RN2). | > No especificado en el documento. |
| Actualización de OT | El Sistema valida que exista una OT previa para la etapa que se está gestionando (FA04 paso 4.4). | > No especificado en el documento. (Sin Anexo/mockup propio asociado para el caso de validación fallida.) |
| Plazo de atención de observaciones | 5 días hábiles a partir del envío de comentarios por parte del Técnico PRE (RN08, RN09). | Correos Anexo A.2 f y A.2 g (ver "Mensajes al Usuario"). |

---

# Errores

> No especificado en el documento. El documento no incluye una tabla de errores ni códigos de error.

| Código | Descripción | Acción esperada |
|---|---|---|
| > No especificado en el documento. | > No especificado en el documento. | > No especificado en el documento. |

---

# Permisos

| Rol | Acción Permitida | Justificación |
|---|---|---|
| Todos los actores | Visualizar la información de las Instituciones Ejecutoras según credenciales. | RN01 |
| Técnico PRE | Registrar información en "Comentarios DGICP", "Comentarios DGICP a Elegibilidad", "Comentarios DGICP a documentos anexos" y "Conclusiones". | RN02; Anexo B.1 |
| Coordinador PRE | Registrar información en "Comentarios DGICP", "Comentarios DGICP a Elegibilidad", "Comentarios DGICP a documentos anexos" y "Conclusiones". | RN02; Anexo B.1 |
| Técnico PRE | Dar clic en "Guardar" (guarda "Comentarios DGICP" y, en FA01, "Conclusiones"). | FA01 paso 1.1; FA02 pasos 2.1–2.2 |
| Técnico PRE / Coordinador PRE | Ver y usar el botón "Enviar comentarios". | RN05; FA03 paso 3.1 |
| Técnico PRE / Coordinador PRE | Registrar "Fecha de emisión de OT". | Anexo B.1 ("Será registrada por el Técnico PRE o Coordinador PRE") |
| Coordinador PRE | Dar clic en "Visto bueno OT". | FA01 paso 1.3 |
| Coordinador PRE | Asignar el caso a Técnico PRE. | RN07 b; Anexo A2 a y b |
| Técnico PRE | Cargar la "Nota de Opinión Técnica" y dar clic en "OT favorable". | FA01 paso 1.5; RN10 |
| Técnico URP | Registrar información en "Justificación Institución" (único actor). | RN03; Anexo B.1 |
| Técnico URP / Viabilizador | Ver el botón "Solicitar OT" (se activa cuando el Técnico URP anexa la Nota de Solicitud OT). | RN04 |
| Técnico URP / Viabilizador | Registrar "Respuesta Institución". | Anexo B.1 (en contradicción con RN03 respecto a "Justificación Institución"; ver Observaciones) |
| Técnico URP / Viabilizador | Dar clic en "Guardar". | FA02 paso 2.1 |
| Técnico URP | Usar el botón "Enviar ajustes" (activo tras recibir comentarios). | RN06 |
| Técnico URP | Ver y usar el botón "Solicitar Viabilidad" del CU-PRE-24. | FA03.1 paso 3.1.1 ("Este botón solo es visible para el Técnico URP") |
| Viabilizador | Ver, más no editar, los comentarios emitidos por el Técnico URP en la pantalla de OT. | FA03.1 paso 3.1.3 |
| Técnico URP | Gestionar la Actualización de OT desde el menú de OT. | FA04 pasos 4.1–4.5 (⚠️ en contradicción con Anexo A – RN3; ver nota bajo la tabla) |
| Viabilizador / Técnico URP | Seleccionar el tipo de Opinión Técnica a solicitar. | Anexo A (texto de la imagen de referencia; RN3) (⚠️ para la opción "Actualización de OT", en contradicción con FA04; ver nota bajo la tabla) |
| Viabilizadores | Devolver el proyecto cuantas veces consideren necesario. | RN15 |

> ⚠️ PENDIENTE DE RESOLUCIÓN: existe una contradicción interna entre Anexo A – RN3 y FA04 sobre quién puede gestionar la Actualización de OT (RN3: "viabilizador o Técnico URP"; FA04: solo Técnico URP). Ver Observaciones, ítem 48. No implementar hasta resolver.

---

# Dependencias

**Otros casos de uso mencionados:**
- CU-PRE-01 "Registro de Proyectos" (origen de CUP, Nombre del proyecto, Unidad Ejecutora; categoría "Proyecto de emergencia" — RN13)
- CU-PRE-03 "Captura de proyectos" / "Bandeja de Preinversión" (FB paso 3; RN09; RN11)
- CU-PRE-3.5 "Selección y registro de etapas" (Etapa actual/futura; Anexo A.4 del CU-PRE-3.5 — RN13; hoja de cálculo "ANEXO" con "Campos a habilitar para Actualización de O.T." — FA04 paso 4.4)
- CU-PRE-04 "Identificación" a CU-PRE-23 "Indicadores del Proyecto" (FA03, FA03.1)
- CU-PRE-07 "Población Objetivo"
- CU-PRE-11 "Descripción técnica"
- CU-PRE-12 "Localización"
- CU-PRE-17 "Presupuesto de inversión" (Anexo A4 del CU-PRE-17)
- CU-PRE-18 "Flujo de costos de Operación y Mantenimiento"
- CU-PRE-21 "Flujo de Caja y cálculo de Indicadores"
- CU-PRE-22.1 "Programación financiera de la preinversión del Proyecto"
- CU-PRE-23 "Indicadores del Proyecto" (Anexo A.1 del CU-PRE-23)
- CU-PRE-24 "Viabilidad" (RN10-11 de CU-PRE-24)
- CU-PRE-25 "Elegibilidad" (RN10 y 15 de CU-PRE-25)
- CU-PRE-26.5 "Priorización"
- CU-PRO-01 "Elaboración/Actualización del PRIPME"
- CU-PRO-08 "Generación de Escenarios para Corto Plazo"
- CU-PRO-17 "Programación Mensual Financiera del PAIP"

**Procesos relacionados:**
- Proceso de aprobación (Viabilidad, Elegibilidad y OT) — RN14, FA03.1
- Proceso de preinversión — RN17
- Priorización — RN17, Anexo A.5
- "Lineamientos del Proceso de Inversión Pública" (plazo de 5 días hábiles) — RN08, Anexo A2 c
- "Ruta de Preinversión" — Anexo B.1 (Etapa futura)

**Servicios externos:**
- Correo electrónico (el documento no especifica el sistema o servicio).

---

# Pantallas

## Anexo A – Menú de registro y gestión de proyectos (Imagen de referencia)

- **Nombre:** Menú de registro y gestión de proyectos (Imagen de referencia)
- **Descripción:** "La imagen de referencia muestra la ruta prevista para seleccionar el tipo de Opinión Técnica (OT) a solicitar por parte del Viabilizador o Técnico URP, siendo las opciones:
  1. Opinión Técnica: cuando se solicita OT para una etapa presentada por primera vez
  2. Actualización de Opinión Técnica: cuando se solicita actualización de una Opinión Técnica previamente emitida de un proyecto cuya ejecución no ha iniciado."
- **Campos:** Lista desplegable "Tipo de solicitud" con opciones "1. Opinión Técnica" y "2. Actualización de OT".
- **Botones:** IDENTIFICACIÓN, FORMULACIÓN, EVALUACIÓN, PROGRAMACIÓN, GESTIÓN; bajo GESTIÓN: VIABILIDAD, ELEGIBILIDAD, OT, PRIORIZACIÓN.
- **Acciones:** Al posicionarse sobre "OT" (cursor mostrado en la imagen) se despliega "Tipo de solicitud". Reglas Anexo A – RN1, RN2, RN3.
- **Ejemplo de datos mostrados en el mockup:** No presenta datos de ejemplo más allá de los rótulos.

> En el mockup extendido del archivo anexo (p. 2) el menú bajo GESTIÓN muestra únicamente VIABILIDAD, "ELEGIBILIDA D" (texto partido en dos líneas en el original) y OT; no muestra el botón PRIORIZACIÓN.

## Anexo A.1 – Opinión Técnica

- **Nombre:** Opinión Técnica
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Pantalla donde el Técnico PRE revisa los apartados del proyecto, emite comentarios y la Opinión Técnica; y donde la Institución registra justificaciones/respuestas.
- **Campos (mockup del documento principal, p. 10, y archivo anexo p. 1):**
  - Encabezado: CUP, Nombre del proyecto, Unidad Ejecutora, Etapa Actual, Etapa Futura.
  - Sección "Documentos anexos": Nota de solicitud de OT (ícono de carpeta), Documento de Preinversión (a revisar) (ícono de carpeta), Otros documentos anexos (ícono de carpeta); columnas "COMENTARIOS DGICP A DOCUMENTACIÓN ANEXA" y "JUSTIFICACIÓN INSTITUCIÓN".
  - Tabla con columnas "APARTADOS", "COMENTARIOS DGICP", "JUSTIFICACIÓN INSTITUCIÓN":
    - I. Identificación (desplegable): 1. Antecedentes, 2. Problema Central, 3. Objetivo General, 4. Objetivos Específicos — cada uno con campo de apartado, ícono de lápiz, campo de comentario y campo de justificación.
    - II. Formulación del proyecto (desplegable, contraído)
    - III. Evaluación del proyecto (desplegable, contraído)
    - III. Programación del proyecto (desplegable, contraído)
  - Sección "Comentarios Elegibilidad" (desplegable): columnas "COMENTARIOS DGICP A ELEGIBILIDAD" y "JUSTIFICACIÓN INSTITUCIÓN".
  - Bloque inferior: Nota de OT (ícono de carpeta), Fecha de solicitud, Fecha de ajustes, Fecha de emisión OT, N° de nota de OT, CONCLUSIONES, Visto Bueno OT (casilla).
- **Botones:** SOLICITAR OT, GUARDAR, ENVIAR COMENTARIOS, ENVIAR AJUSTES, OT FAVORABLE, HISTÓRICO DE OT; ícono de lápiz por apartado (RN16).
- **Acciones:** FB, FA01, FA02, FA03, FA03.1; RN02–RN06, RN10, RN 12, RN13, RN16.

**Versión extendida del mockup (archivo anexo CU-PRE-26_Anexo.pdf, p. 2):**

- Encabezado: "Código:", "Nombre:", "Unidad Ejecutora:", "Etapa Actual:", "Etapa Futura:" (esta última en la misma línea que "Etapa Actual").
- Apartados desplegados:
  - I. Identificación: 1. Antecedentes, 2. Problema Central, 3. Objetivo General, 4. Objetivos Específicos, 5. Alternativas de solución
  - II. Formulación del proyecto: 1. Análisis de Interesados, 2. Población Objetivo, 3. Área de Influencia, 4. Análisis de Mercado, 5. Descripción Técnica, 6. Localización, 7. Tamaño, 8. Análisis Ambiental, 9. Análisis Legal, 10. Presupuesto de Inversión
  - III. Evaluación del proyecto: 1. Flujo de Costos de O&M, 2. Parámetros de Evaluación, 3. Flujo de beneficios, 4. Flujo de caja y cálculo de indicadores, 5. Flujo de caja financiero
  - III. Programación del proyecto: 1. Programación Financiera Preinversión, 2. Programación Financiera Inversión, 3. Programación Física Preinversión, 4. Programación Física Inversión, 5. Indicadores del Proyecto
- Sección "Comentarios Elegibilidad" y luego "Documentos anexos" (en esta versión, "Documentos anexos" aparece al final, después de "Comentarios Elegibilidad").
- Bloque inferior: Nota de OT, Fecha de solicitud, Fecha de ajustes, Fecha de emisión OT, N° de nota de OT, Conclusiones. No se muestra la casilla "Visto Bueno OT".
- Botones: OT FAVORABLE, SOLICITAR OT, GUARDAR, ENVIAR COMENTARIOS, ENVIAR AJUSTES, HISTÓRICO DE OT, HABILITAR.

**Ejemplo de datos mostrados en el mockup (Anexo A.1):**

| Rótulo | Valor mostrado (documento principal / archivo anexo p. 1) | Valor mostrado (archivo anexo p. 2) |
|---|---|---|
| CUP / Código | XXXXX | XXXXX |
| Nombre del proyecto / Nombre | XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX | XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX |
| Unidad Ejecutora | XXXXXXXX | XXXXXXXX |
| Etapa Actual | Perfil | Perfil |
| Etapa Futura | Diseño | Diseño |

## Anexo A1.5 – Histórico de Opinión Técnica

- **Nombre:** Histórico de Opinión Técnica
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Tabla de las Opiniones Técnicas emitidas para el proyecto; se accede con el botón "HISTÓRICO DE OT".
- **Campos (columnas):** N° DE NOTA DE OT, TIPO DE GESTIÓN, ETAPA ACTUAL, ETAPA FUTURA, FECHA DE NOTA, INVERSIÓN ESTIMADA, VER INFORME OT.
- **Botones:** Ícono de ojo ("VER INFORME OT") por fila.
- **Acciones:** Ver el informe de OT (Anexo A.6).

**Ejemplo de datos mostrados en el mockup (Anexo A1.5 — documento principal, p. 11):**

| N° DE NOTA DE OT | TIPO DE GESTIÓN | ETAPA ACTUAL | ETAPA FUTURA | FECHA DE NOTA | INVERSIÓN ESTIMADA | VER INFORME OT |
|---|---|---|---|---|---|---|
| MH.DGICP.DGI/001.070/2025 | Opinión Técnica | Perfil | Factibilidad | 4/19/2025 | $1,000,000.00 | (ícono de ojo) |
| MH.DGICP.DGI/001.050/2024 | Opinión Técnica | Perfil | Factibilidad | 5/12/2024 | $950,000.00 | (ícono de ojo) |

**Ejemplo de datos mostrados en el mockup (archivo anexo CU-PRE-26_Anexo.pdf, p. 3):**

| N° DE NOTA DE OT | TIPO DE GESTIÓN | ETAPA ACTUAL | ETAPA FUTURA | FECHA DE NOTA | ESTIMADA | VER INFORME OT |
|---|---|---|---|---|---|---|
| MH.DGICP.DGI/001.070/2025 | Opinión Técnica | Perfil | Factibilidad | 2025-04-19 | $1,000,000.00 | (ícono de ojo) |
| MH.DGICP.DGI/001.050/2024 | Opinión Técnica | Perfil | Factibilidad | 2024-05-12 | $950,000.00 | (ícono de ojo) |

> Nota: en el archivo anexo (p. 3) el encabezado de la columna de monto se lee únicamente "ESTIMADA" y el encabezado "VER INFORME OT" aparece recortado/superpuesto con el borde de la celda; se transcriben tal como se ven, sin completarlos.

## Anexo A.3 – Alerta de Gestión de Actualización de O.T.

- **Nombre:** Alerta de Gestión de Actualización de O.T.
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Mensaje de confirmación mostrado en FA04 paso 4.2.
- **Campos:** Texto "¿Está seguro de gestionar una actualización de Opinión Técnica?"
- **Botones:** SÍ, NO
- **Acciones:** "Sí" → FA04 paso 4.3/4.4. La acción del botón "NO" no está especificada en el documento.

## Anexo A.4 – Aviso de habilitación de campos para gestión de Actualización de O.T.

- **Nombre:** Aviso de habilitación de campos para gestión de Actualización de O.T.
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Mensaje mostrado en FA04 paso 4.4.
- **Campos:** Texto "Se han habilitado los campos correspondientes de F&E, para el ingreso de información actualizada."
- **Botones:** ACEPTAR
- **Acciones:** > No especificado en el documento (acción del botón "ACEPTAR").

## Anexo A.5 – Aviso de Emisión de O.T.

- **Nombre:** Aviso de Emisión de O.T.
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Mensaje mostrado al Técnico PRE al emitirse la OT favorable (FA01 paso 1.6; RN17).
- **Campos:** Título "¡Opinión Técnica emitida!", ícono de verificación, texto "El proyecto cuenta con Opinión Técnica, continúe con la Priorización".
- **Botones:** IR A PRIORIZACIÓN
- **Acciones:** Continuar con la emisión de la priorización (RN17).

## Anexo A.6 – Informe OT (Ejemplo)

- **Nombre:** Informe OT (Ejemplo) — título en el mockup: "REPORTE DE OPINIÓN TÉCNICA"
- **Descripción (derivada — no es texto literal del documento; síntesis del análisis a partir de las referencias indicadas):** Informe que se muestra al dar clic en "Ver Informe" del Histórico (Anexo B.1).
- **Campos:** Nombre del proyecto, CUP, Unidad Ejecutora, Etapa actual, Etapa futura; tabla CAMPOS/CONTENIDO: Problema central, Objetivo General, Descripción del proyecto, Productos, Tamaño, Población Objetivo, Localización, Inversión Estimada, Resumen del Presupuesto, Costos de Operación, Costos de Mantenimiento, Fuente Estimada de Financiamiento, Fuente Estimada de Recursos, Convenio, Indicadores de Evaluación; Fecha de solicitud, Fecha de ajustes, Fecha de emisión de OT, Costo del estudio, Opinión Técnica, Conclusiones del Técnico PRE.
- **Botones:** > No especificado en el documento.
- **Acciones:** Consulta (Anexo B.1: todos los campos "Editable: No").

**Ejemplo de datos mostrados en el mockup (Anexo A.6, idéntico en el documento principal p. 14 y en el archivo anexo p. 4):**

| Campo | Contenido mostrado |
|---|---|
| Nombre del proyecto | Construcción de Unidad de Salud de Rancho Quemado, Morazán |
| CUP | 8803 |
| Unidad Ejecutora | Ministerio de Salud |
| Etapa actual | Perfil |
| Etapa futura | Ejecución |
| Problema central | El inmueble donde opera la Unidad de Salud de Rancho Quemado, del distrito de Perquín, fue diseñado para una vivienda rural, y presenta las siguientes limitantes o inconvenientes que afectan la prestación de los servicios médicos que allí se brindan: a) daños en la infraestructura como columnas y paredes; y b) no cuenta con espacios adecuados para el funcionamiento de una Unidad de Salud. |
| Objetivo General | Contar con infraestructura adecuada para instalar a la Unidad de Salud de Rancho Quemado, a fin de contribuir a mejorar la prestación de los servicios médicos que se brindan a los usuarios. |
| Descripción del proyecto | El proyecto consiste en la construcción de una nueva infraestructura de un nivel, para el funcionamiento de la Unidad de Salud, mediante las siguientes intervenciones: a) demolición de la infraestructura actual, y b) construcción de una nueva edificación, la cual contará, entre otros, con los siguientes espacios: sala de espera, estación de enfermería, área de archivo, preparación de pacientes, consultorio médico, consultorio materno infantil, área de inhaloterapia y rehidratación oral, vacunación y curaciones, farmacia con bodega, sala de reuniones, zona de estar de personal, caseta de desechos bioinfecciosos y comunes. |
| Productos | "Unidad de salud construida"; "Unidad de salud equipada" |
| Tamaño | 1 Unidad de salud con capacidad de atención a 3,000 pacientes por mes |
| Población Objetivo | "10,500" / "personas, entre usuarios y empleados de la Unidad de Salud" / "del distrito de Perquín" |
| Localización | Dirección Específica: "Caserío Rancho Quemado, cantón Casa Blanca"; Distrito: "distrito de Perquín"; Departamento: "Morazán" |
| Inversión Estimada | $ 750,000.00 |
| Resumen del Presupuesto | Ver tabla siguiente |
| Costos de Operación | $ 35,000.00 |
| Costos de Mantenimiento | $ 15,000.00 |
| Fuente Estimada de Financiamiento | Préstamo Externo |
| Fuente Estimada de Recursos | BCIE |
| Convenio | BCIE 2215 |
| Indicadores de Evaluación | Ver tabla siguiente |
| Fecha de solicitud | 10/06/2025 |
| Fecha de ajustes | 23/06/2025 |
| Fecha de emisión de OT | 26/06/2025 |
| Costo del estudio | $2,500.00 |
| Opinión Técnica | Favorable |
| Conclusiones del Técnico PRE | Con base en lo anterior, se considera pertinente emitir la Opinión Técnica para la etapa de Ejecución, tomando en cuenta los siguientes aspectos: a) el perfil remitido cumple con los requisitos mínimos para la formulación de proyectos de Inversión Pública establecidos en la normativa vigente; b) consistente con el mencionado perfil, la información correspondiente fue completada... |

> Nota: el texto de "Conclusiones del Técnico PRE" termina con puntos suspensivos ("completada...") en el original; se transcribe tal como aparece, sin completar el contenido faltante.

*Resumen del Presupuesto (ejemplo):*

| COMPONENTE | COSTO (US$) |
|---|---|
| Infraestructura | 600,000.00 |
| Equipamiento | 150,000.00 |
| TOTAL | 750,000.00 |

*Indicadores de Evaluación (ejemplo):*

| INDICADOR | VALOR |
|---|---|
| VANS | $ 1,500,000.00 |
| TIRS | 15.00 % |
| R B/C | 1.50 |

---

# Mensajes al Usuario

| Tipo | Mensaje | Cuándo ocurre |
|---|---|---|
| Confirmación (Anexo A.3) | "¿Está seguro de gestionar una actualización de Opinión Técnica?" (botones SÍ / NO) | FA04 paso 4.2, al seleccionar "Actualización de OT". |
| Informativo (Anexo A.4) | "Se han habilitado los campos correspondientes de F&E, para el ingreso de información actualizada." (botón ACEPTAR) | FA04 paso 4.4, tras validar la existencia de OT previa. |
| Éxito (Anexo A.5 — texto del mockup) | "¡Opinión Técnica emitida!" / "El proyecto cuenta con Opinión Técnica, continúe con la Priorización" (botón IR A PRIORIZACIÓN) | FA01 paso 1.6; RN17. |
| Éxito (Anexo A.5 — texto citado en FA01 paso 1.6) | "El proyecto cuenta con Opinión Técnica Favorable, continúe con la Priorización". | FA01 paso 1.6 (difiere del texto del mockup). |
| Notificación al Técnico PRE | Visto bueno de la OT. Texto: > No especificado en el documento. (Sin Anexo/mockup propio asociado; no corresponde a ninguno de los literales del Anexo A2.) | FA01 paso 1.4. |
| Validación de OT previa inexistente | > No especificado en el documento. (Sin Anexo/mockup propio asociado.) | FA04 paso 4.4. |
| Correo electrónico (Anexo A2 a–h) | Ver transcripción siguiente. | RN07, RN08, RN09; FB paso 2; FA01 paso 1.6; FA03 paso 3.2; FA03.1 paso 3.1.3. |

## Contenido de Correos Electrónicos (Anexo A2)

**a. Cuando el Técnico URP solicita OT, se envía a Coordinador PRE**

> Estimado/a [Coordinador PRE]:
>
> Se informa que el día [Fecha de solicitud], la Institución [Nombre de la Institución] ha solicitado la emisión de Opinión Técnica para la etapa de [Etapa Futura] del proyecto con código [CUP] denominado [Nombre del proyecto]. Revisar la Bandeja de Preinversión para la asignación del caso.

**b. Cuando el Coordinador PRE asigna el caso a Técnico PRE, se envía a Técnico PRE:**

> Estimado/a [Técnico PRE]:
>
> Se informa que este día [Fecha de asignación] se le ha asignado la revisión de la solicitud de Opinión Técnica para la etapa de [Etapa Futura] del proyecto con código [CUP] denominado [Nombre del proyecto] de la Institución [Nombre de la Institución].

**c. Cuando el Técnico PRE envía comentarios a Técnico URP y Jefe URP Viabilizador:**

> Estimados señores [Nombre de la Institución]:
>
> Atentamente, se informa que este día [Fecha de envío de comentarios]se han realizado comentarios a los registros y a la documentación del proyecto con código [CUP] denominado [Nombre del proyecto] en el SIIP, para lo cual, según los Lineamientos del Proceso de Inversión Pública, dispone de 5 días hábiles para realizar los ajustes correspondientes, período que finaliza en fecha [Fecha de fin de plazo], caso contrario la solicitud no podrá ser tramitada y deberán gestionar nuevamente la Opinión Técnica.

**d. Cuando el Técnico URP envía ajustes a Técnico PRE y a Coordinador PRE:**

> Estimado/a [Técnico PRE]:
>
> Atentamente, se informa que el día [Fecha de envío de ajustes] la Institución [Nombre de la Institución] ha reportado ajustes en los registros del proyecto con código [CUP] denominado [Nombre del proyecto] en el SIIP y en la documentación del proyecto.

**e. Cuando el Técnico PRE carga la nota de Opinión Técnica y da clic en OT Favorable, se envía a todos los actores:**

> Estimados señores [Nombre de la Institución]:
>
> Atentamente, se informa que el día [Fecha de emisión de OT] se ha emitido Opinión Técnica Favorable para la etapa de [Etapa Futura] al proyecto con código [CUP] denominado [Nombre del proyecto]; la cual puede ser descargada desde el SIIP y será entregada físicamente en sus oficinas a la brevedad posible.

**f. Alerta: Dos días antes del vencimiento del período para atender observaciones:**

> Estimados señores [Nombre de la Institución]:
>
> Atentamente, se informa que han transcurrido 3 días hábiles para atención de comentarios remitidos al proyecto con código [CUP] denominado [Nombre del proyecto en fecha [Fecha de envío de comentarios], por lo que dispone de 2 días hábiles para completar la atención de dichos comentarios]; caso contrario la solicitud no podrá ser tramitada y deberá gestionar nuevamente la Opinión Técnica correspondiente.

> Nota: los corchetes de "[Nombre del proyecto en fecha [Fecha de envío de comentarios], … comentarios]" aparecen así en el original (corchete de "[Nombre del proyecto" sin cerrar y corchete de cierre tras "comentarios"); se transcriben sin corregir.

**g. Alerta: Una vez vencido el plazo de atención de observaciones, según lineamientos**

> Estimados señores [Nombre de la Institución]:
>
> Atentamente, se informa que el plazo de 5 días hábiles para la atención de comentarios sobre el proyecto con código [CUP] denominado [Nombre del proyecto] ha caducado este día [Fecha de caducidad], por lo que la solicitud no podrá ser tramitada y deberán gestionar nuevamente la Opinión Técnica correspondiente una vez atendidos dichos comentarios.

**h. Cuando el Técnico PRE da clic en el botón "Enviar comentarios Elegibilidad", se envía al Viabilizador**

> Estimados señores [Nombre de la Institución]:
>
> Atentamente, se informa que este día [Fecha de envío de observaciones a la Elegibilidad] se han emitido observaciones a la Elegibilidad del proyecto con código [CUP] denominado [Nombre del proyecto], siendo necesario la revisión y ajustes de la misma según corresponda.

---

# Observaciones

**Información ambigua / contradicciones internas**

1. **Disparador del flujo:** FB paso 1 indica que "el sistema envía automáticamente el proyecto a opinión técnica" al emitir Viabilidad/Elegibilidad (con actor "Viabilizador"), mientras que RN04, RN07 a, el Anexo A2 a y el Anexo B.1 ("Fecha de solicitud") describen una solicitud manual mediante el botón "Solicitar OT" tras anexar la "Nota de solicitud de OT".
2. **Destinatarios de la notificación de solicitud:** FB paso 2 y el título del Anexo A2 a indican que se notifica al Coordinador PRE; RN07 a indica que se envía "a Técnico PRE y al Coordinador PRE".
3. **Cambio de estado en FB paso 2:** el Sistema "Cambia el estado del proyecto a 'Proyecto viable' o 'Proyecto elegible'" al enviar a OT; el documento no aclara si estos son estados previos (resultado de CU-PRE-24/25) o estados asignados en este paso.
4. **Destinatarios de la notificación de ajustes:** RN07 d y FA03.1 paso 3.1.3 incluyen al Viabilizador; el título del Anexo A2 d solo menciona "Técnico PRE y … Coordinador PRE", y el cuerpo del correo se dirige únicamente a "[Técnico PRE]".
5. **Correo "h" sin regla ni botón asociados:** el Anexo A2 h se dispara con el botón "Enviar comentarios Elegibilidad", que no aparece en ningún mockup, flujo ni en la enumeración de RN07 (a–e).
6. **Correo "e" dirigido a la Institución:** el título indica "se envía a todos los actores", pero el saludo es "Estimados señores [Nombre de la Institución]".
7. **Rol "Jefe URP Viabilizador":** el título del Anexo A2 c usa "Técnico URP y Jefe URP Viabilizador", mientras RN07 c y FA03 usan "Técnico URP y Viabilizador".
8. **Registro de "Justificación Institución"/"Respuesta Institución":** RN03 establece que "Únicamente el actor Técnico URP podrá registrar información en el campo 'Justificación Institución'"; sin embargo, FA02 permite al "Técnico URP/Viabilizador" guardar "Justificación"/"Respuesta institución", FA03.1 indica que el Viabilizador "responde los comentarios de los apartados de la pantalla del Anexo A.1", y el Anexo B.1 ("Respuesta Institución") habilita a "Técnico URP o Viabilizador".
9. **Viabilizador ve comentarios del Técnico URP:** FA03.1 paso 3.1.3 dice "El viabilizador podrá ver, más no editar los comentarios emitidos por el Técnico URP en la pantalla de OT", mientras el mismo flujo indica que el Viabilizador también responde comentarios en esa pantalla.
10. **"Visto bueno OT" botón vs. casilla:** FA01 lo describe como "botón"; el mockup del Anexo A.1 muestra "Visto Bueno OT:" con una casilla de verificación; el mockup extendido (archivo anexo p. 2) no lo muestra.
11. **Texto del mensaje A.5:** FA01 paso 1.6 cita "El proyecto cuenta con Opinión Técnica Favorable, continúe con la Priorización"; el mockup A.5 dice "El proyecto cuenta con Opinión Técnica, continúe con la Priorización".
12. **Botón "Guardar ajustes":** mencionado en FA03.1 paso 3.1.1 para Técnico URP y Viabilizador, no aparece en los mockups (que muestran "GUARDAR" y "ENVIAR AJUSTES"). Ningún paso de FA03.1 menciona hacer clic en "Enviar ajustes", pese al título del flujo.
13. **Botón "HABILITAR":** aparece en el mockup extendido del archivo anexo (p. 2) pero no se menciona en ningún flujo, regla ni en el Anexo B.1.
14. **"Fecha de emisión de OT" (Pantalla Opinión Técnica):** "Editable: Sí", pero el Detalle la describe como una fecha que "Muestra…" y que "corresponde al momento en que el Técnico PRE cargó la nota … y se cambia el estado", y a la vez "Será registrada por el Técnico PRE o Coordinador PRE". No se puede determinar si es automática o de captura manual. En la Pantalla "Ver informe OT" el mismo campo es "Editable: No" (lo cual puede referirse a su condición en el informe y no en su pantalla de origen).
15. **"N° de nota de OT" (Pantalla Opinión Técnica):** "Editable: Sí" con Detalle "Muestra el número de nota de OT"; no se indica quién lo registra ni su origen.
16. **Detalles intercambiados en "Ver informe OT":** el Detalle de "Nombre del proyecto" dice "Muestra el Código Único de Proyecto" y el de "CUP" dice "Muestra el nombre del proyecto".
17. **Origen de "Problema central" en "Ver informe OT":** el Detalle indica "Procede del campo Unidad Ejecutora del CU-PRE-01 'Registro de Proyectos'", lo que no corresponde al contenido mostrado en el ejemplo A.6. El Detalle de "Unidad Ejecutora" en esa misma tabla está vacío.
18. **"Descripción del proyecto" desde CU-PRE-04:** el Anexo B indica que proviene de CU-PRE-04 "Identificación"; los "Campos requeridos" ubican "Descripción Técnica" en "2.5 Formulación del proyecto". Se señala sin resolver.
19. **Nombre del campo "Conclusiones":** Anexo A.1 y Anexo B.1 (Pantalla Opinión Técnica) usan "Conclusiones"; el Anexo B.1 (Pantalla Ver informe OT) remite a un campo "Conclusiones del Técnico PRE" del Anexo A.1.
20. **Campo "Convenio":** aparece en el ejemplo A.6 ("BCIE 2215") pero no está documentado en el Anexo B.1.
21. **Tipo/Formato atípicos:** "Ícono" (Ver Informe) y "Moneda Porcentaje Decimal" (Indicadores de evaluación) no pertenecen al conjunto habitual de la tabla; no se puede confirmar su significado exacto.
22. **Etapa futura en los ejemplos:** el Anexo B.1 ejemplifica "PERFIL → PREFACTIBILIDAD" según la Ruta de Preinversión; los mockups muestran "Perfil → Diseño" (A.1), "Perfil → Factibilidad" (A1.5) y "Perfil → Ejecución" (A.6). La Ruta de Preinversión no se incluye en este documento.
23. **Origen del monto de inversión:** el Histórico ("Monto de Inversión") toma el "Total" del Anexo A4 del CU-PRE-17; el Informe ("Inversión Estimada") toma "Inversión Estimada (Precios de Mercado)" del CU-PRE-17. No se aclara si son el mismo dato.
24. **Formato de fechas en el Histórico:** el Anexo B.1 establece DD/MM/AAAA para las fechas de la Pantalla Opinión Técnica; el mockup A1.5 del documento principal muestra "4/19/2025" y "5/12/2024" (mes/día/año) y el archivo anexo muestra "2025-04-19" y "2024-05-12" (año-mes-día). El Anexo B.1 no indica formato para "Fecha de emisión OT" del Histórico.
25. **Valor "FAVORABLE":** el Anexo B.1 indica que el campo "Opinión Técnica" mostrará "FAVORABLE"; el ejemplo A.6 muestra "Favorable".
26. **"Fecha de solicitud":** en la Pantalla Opinión Técnica es la fecha en que se "dio clic en el botón 'Solicitar OT'"; en "Ver informe OT" es la fecha en que se dio clic "por primera vez". No se aclara qué fecha se muestra en solicitudes posteriores.
27. **RN15 – campo "Observaciones" y conteo de devoluciones:** RN15 exige guardar la información del campo "Observaciones" en cada devolución y mostrar cuántas veces se ha devuelto; ni el campo "Observaciones" ni el contador aparecen en los mockups ni en el Anexo B.1. RN15 atribuye la devolución a "los viabilizadores", mientras FA03 la atribuye al Técnico PRE mediante "Enviar Comentarios".
28. **RN13:** se refiere al "Anexo 1 de este caso de uso" (el documento usa "Anexo A.1") y al "Anexo A.4 del CU-PRE-3.5", documento no incluido.

**Doble nomenclatura (botones, campos y secciones)**

29. Columna de respuesta de la Institución: "JUSTIFICACIÓN INSTITUCIÓN" (mockups), "Justificación Institución" (RN03, Anexo B.1), "Justificación" / "Respuesta institución" (FA02, FA03), "Respuesta Institución" (Anexo B.1 y Campos requeridos 5.2, 6.2).
30. Comentarios a documentos: "COMENTARIOS DGICP A DOCUMENTACIÓN ANEXA" (mockup), "Comentarios DGICP a 'documentos anexos'" (Anexo B.1), "Observaciones DGICP" (Campos requeridos 5.1).
31. Nota adjunta de emisión: "Nota de Opinión Técnica" (FA01 paso 1.5), "nota de Opinión Técnica" (Anexo A2 e), "Nota de OT" (mockup), "nota firmada por el Director de la DGICP" (Anexo B.1).
32. Histórico: columnas "FECHA DE NOTA" e "INVERSIÓN ESTIMADA" (mockup) vs. campos "Fecha de emisión OT" y "Monto de Inversión" (Anexo B.1).
33. Encabezado de pantalla: "CUP:" / "Nombre del proyecto:" (mockup A.1) vs. "Código:" / "Nombre:" (mockup extendido del archivo anexo p. 2).
34. CU-PRE-03 se denomina "Captura de proyectos" (Precondiciones, FB paso 3, RN11) y "Bandeja de Preinversión" (RN09, escrito "CU-PRE03").
35. Estados escritos con distinta capitalización: "Proyecto elegible" (FB paso 2 y 3) y "Proyecto Elegible" (RN05, FA03.1).

**Numeración irregular**

36. FA03.1: pasos "3.1.1" y "3.1.3" sin paso "3.1.2".
37. El flujo "Actualización de Opinión Técnica" se rotula "Flujo Alternativo 4 – FA01" (identificador FA01 duplicado) y en el Anexo A (RN3) se denomina "Sub Flujo 4".
38. Las reglas del Anexo A se numeran "RN1", "RN2", "RN3", coincidiendo con RN01–RN03 de la sección principal pero con contenido distinto.
39. "RN 12" se escribe con espacio, a diferencia del resto.
40. En los mockups del Anexo A.1 las secciones de Evaluación y de Programación se numeran ambas "III." ("III. Evaluación del proyecto" y "III. Programación del proyecto").
41. Numeración de anexos: "Anexo A1.5" (título) vs. "anexo A.1.5" (Anexo B.1); "Anexo A.6" (título) vs. "Anexo A6" (Anexo B.1); "Anexo A2" (título) vs. "Anexo A.2" (RN07); "Anexo A1" vs. "Anexo A.1" (FB paso 5 y 6). Se trata de variaciones de escritura; no hay evidencia de que designen pantallas distintas.

**Información incompleta / referencias a otros documentos**

42. **Campos requeridos vs. apartados del mockup:** la lista de "Campos requeridos" no coincide con los apartados de los mockups. Ejemplos: "2.2 Análisis de la Población" (requeridos) vs. "2. Población Objetivo" (mockup); "2.8 Análisis de Riesgos" y "2.11 Presupuesto de O&M" solo en requeridos; "5. Alternativas de solución", "7. Tamaño", "Parámetros de Evaluación", "Flujo de Costos de O&M", "Flujo de caja financiero", "Programación Financiera Inversión", "Programación Física Preinversión" y "Programación Física Inversión" solo en el mockup extendido.
43. FA04 paso 4.4 remite a una "hoja de cálculo CU-PRE-3.5 'Selección y registro de etapas' ANEXO" con "Campos a habilitar para Actualización de O.T." y "Tipo de Iniciativa", que no forma parte de los documentos analizados. El mensaje A.4 menciona "campos correspondientes de F&E" sin definir la sigla.
44. Precondiciones y Postcondiciones se listan como referencias a otros casos de uso sin redacción de la condición; el primer ítem de Precondiciones es un rango ("CU-PRE-03 … a CU-PRE-23 …").
45. Las referencias "RN10-11 de CU-PRE-24" y "RN10 y 15 de CU-PRE-25" remiten a documentos no incluidos.
46. Fecha del documento: el encabezado de página indica "MAYO 2026" y el Historial de Revisiones "SEP 2025" para la misma versión 1.0.
47. El campo "Identificación" del documento incluye la palabra "viabilizado" ("CU-PRE-26 Opinión Técnica viabilizado"), ausente del título en portada y encabezados.

**Permisos**

48. **Actor de la Actualización de OT:** el Anexo A (RN3) atribuye la selección de la opción "Actualización de OT" al "viabilizador o Técnico URP", mientras que en FA04 (pasos 4.1, 4.3 y 4.5) solo actúa el Técnico URP. No se puede determinar si el Viabilizador está autorizado a gestionar la Actualización de OT.

**Ambigüedades de obligatoriedad**

49. **"Campos requeridos" vs. Anexo B.1:** la lista "Campos requeridos" incluye "6.1. Comentarios DGICP a Elegibilidad", que el Anexo B.1 declara opcional ("Es opcional, en caso de existir observaciones"); "5.2" y "6.2 Respuesta Institución" son obligatorios solo de forma condicional ("si se emitieron comentarios de DGICP").
50. **"Fecha de solicitud":** el Anexo B.1 la declara "Campo obligatorio", pero no es editable ("Editable: No") y su valor lo determina el Sistema (fecha del clic en "Solicitar OT"). No se aclara qué significa la obligatoriedad en un campo que el usuario no captura.
51. **"Comentarios DGICP":** el Anexo B.1 lo declara obligatorio ("Campos obligatorios"), pero el flujo permite emitir la OT sin enviar comentarios (FB paso 6: "envía comentarios… o emite Opinión Técnica"; FA01 no incluye "Enviar Comentarios"). No se especifica en qué condición es obligatorio.

---

# Entidades Detectadas

> Nota sobre información derivada: el documento no incluye un modelo ni una lista de entidades. Esta sección fue construida por el análisis; los nombres de entidad, sus descripciones y las operaciones son síntesis a partir de los flujos, reglas de negocio y anexos citados en cada fila, no texto literal del documento.

| Entidad | Descripción | Operación |
|---|---|---|
| Proyecto | Proyecto de inversión pública identificado por CUP, nombre, Unidad Ejecutora, etapa actual y etapa futura. | Consulta de datos (Anexo B.1); cambio de estado a "Proyecto viable"/"Proyecto elegible" (FB paso 2), "Observado" (FA03 paso 3.3), "Proyecto con OT" (FA01 paso 1.6); devolución (RN14, RN15); disponibilidad para visualización/actualización en CU-PRE-03 (RN11). |
| Solicitud de Opinión Técnica (gestión de OT) | Gestión de OT de tipo "Opinión Técnica" o "Actualización de OT". | Solicitud (RN04, Anexo A); envío automático (FB paso 1); asignación a Técnico PRE (RN07 b); eliminación del módulo de gestión y archivo al vencer el plazo (RN09); validación de OT previa (FA04 paso 4.4). |
| Comentario DGICP | Comentarios del Técnico PRE / Coordinador PRE a apartados, documentos anexos y elegibilidad. | Registro y guardado (RN02, FA02, FA03); envío (FA03). |
| Justificación / Respuesta Institución | Respuesta de la Institución (Técnico URP / Viabilizador) a los comentarios DGICP. | Registro y guardado (RN03, FA02, FA03.1); consulta sin edición por el Viabilizador (FA03.1 paso 3.1.3). |
| Documento anexo | Nota de solicitud de OT, Documento de Preinversión (a revisar), Otros documentos anexos, documentación ajustada. | Carga (RN04; FA03.1 paso 3.1.1); revisión (FA01 paso 1.1). |
| Nota de OT | Nota de Opinión Técnica firmada por el Director DGICP; tiene "N° de nota de OT". | Carga (FA01 paso 1.5). |
| Conclusiones | Conclusiones generales del Técnico PRE sobre el proyecto. | Registro y guardado (FA01 paso 1.1; RN02). |
| Visto bueno OT | Aprobación del Coordinador PRE previa a la emisión de la OT. | Habilitación (FA01 paso 1.2); registro (FA01 paso 1.3). |
| Histórico de Opinión Técnica | Registro de OT emitidas (N° de nota, tipo de gestión, etapas, fecha, monto). | Consulta (Anexo A1.5, Anexo B.1). |
| Informe OT | Reporte de Opinión Técnica con datos consolidados del proyecto. | Consulta (Anexo A.6, Anexo B.1). |
| Notificación por correo electrónico | Correos a los actores según eventos del proceso. | Envío (RN07, RN08, RN09; Anexo A2 a–h). |
| Observaciones (RN15) | Información registrada en cada devolución del proyecto. | Guardado en cada devolución y conteo de devoluciones (RN15). |

---

# Catálogos Detectados

> Nota sobre información derivada: el documento no incluye una sección de catálogos. La agrupación y los nombres de catálogo son del análisis (algunos coinciden con rótulos del documento, como "Tipo de solicitud" o "Tipo de gestión"); los valores listados sí provienen de las fuentes citadas en cada fila.

| Catálogo | Valores conocidos |
|---|---|
| Tipo de solicitud (menú OT, Anexo A) | 1. Opinión Técnica; 2. Actualización de OT |
| Tipo de gestión (Histórico, Anexo B.1) | "Opinión Técnica"; "Actualización de OT" |
| Estados del proyecto (mencionados en el documento) | "Proyecto viable"; "Proyecto elegible" / "Proyecto Elegible"; "Observado"; "Proyecto con OT". > Catálogo completo de estados no especificado en el documento. |
| Etapas (valores observados) | PERFIL, PREFACTIBILIDAD, DISEÑO, EJECUCIÓN (Anexo B.1); Perfil, Diseño, Factibilidad, Ejecución (mockups). > El catálogo completo y la "Ruta de Preinversión" están definidos en CU-PRE-3.5 "Selección y registro de etapas", no incluido; los valores listados son solo los observados. |
| Categoría de proyecto (mencionada) | "Proyecto de emergencia" (RN13, definida en CU-PRE-01). > Catálogo completo no disponible en este documento. |
| Opinión Técnica (valor del informe) | "FAVORABLE" (Anexo B.1); "Favorable" (ejemplo A.6) |
| Indicadores de evaluación | VANS, TIRS, RB/C (Anexo B.1); "R B/C" en el ejemplo A.6 |
| Apartados — según "Campos requeridos" | 1. Identificación del proyecto (1.1 Antecedentes, 1.2 Problema Central, 1.3 Objetivo General, 1.4 Objetivos Específicos); 2. Formulación del proyecto (2.1 Análisis de Interesados, 2.2 Análisis de la Población, 2.3 Área de Influencia, 2.4 Análisis de Mercado, 2.5 Descripción Técnica, 2.6 Localización, 2.7 Análisis Ambiental, 2.8 Análisis de Riesgos, 2.9 Análisis Legal, 2.10 Presupuesto de Inversión, 2.11 Presupuesto de O&M); 3. Evaluación (3.1 Flujo de Beneficios, 3.2 Flujo de Caja e Indicadores); 4. Programación (4.1 Indicadores del proyecto, 4.2 Programación Financiera Preinversión); 5. Documentos anexos (5.1 Observaciones DGICP, 5.2 Respuesta Institución); 6. Comentarios a Elegibilidad (6.1 Comentarios DGICP a Elegibilidad, 6.2 Respuesta Institución) |
| Apartados — según mockup extendido (archivo anexo p. 2) | I. Identificación (1. Antecedentes, 2. Problema Central, 3. Objetivo General, 4. Objetivos Específicos, 5. Alternativas de solución); II. Formulación del proyecto (1. Análisis de Interesados, 2. Población Objetivo, 3. Área de Influencia, 4. Análisis de Mercado, 5. Descripción Técnica, 6. Localización, 7. Tamaño, 8. Análisis Ambiental, 9. Análisis Legal, 10. Presupuesto de Inversión); III. Evaluación del proyecto (1. Flujo de Costos de O&M, 2. Parámetros de Evaluación, 3. Flujo de beneficios, 4. Flujo de caja y cálculo de indicadores, 5. Flujo de caja financiero); III. Programación del proyecto (1. Programación Financiera Preinversión, 2. Programación Financiera Inversión, 3. Programación Física Preinversión, 4. Programación Física Inversión, 5. Indicadores del Proyecto) |
| Apartados — según mockup Anexo A.1 (documento principal) | I. Identificación (1. Antecedentes, 2. Problema Central, 3. Objetivo General, 4. Objetivos Específicos); II. Formulación del proyecto; III. Evaluación del proyecto; III. Programación del proyecto (estas tres últimas contraídas, sin sub-apartados visibles) |
| Documentos anexos | Nota de solicitud de OT; Documento de Preinversión (a revisar); Otros documentos anexos |
| Fuente de financiamiento (valores observados) | "Préstamo Externo" (ejemplo A.6). > Catálogo completo definido en CU-PRE-17 "Presupuesto de inversión", no incluido; el valor listado no constituye el catálogo completo. |
| Fuente de recursos (valores observados) | "BCIE" (ejemplo A.6). > Catálogo completo definido en CU-PRE-17, no incluido. |
| Componentes del presupuesto (valores observados) | Infraestructura; Equipamiento (ejemplo A.6). > Catálogo completo definido en CU-PRE-17, no incluido. |

---

# Eventos del Sistema

> Nota sobre información derivada: el documento no incluye una sección de eventos. Esta tabla fue construida por el análisis a partir de los pasos de flujo y reglas de negocio citados en cada fila; las denominaciones de los eventos no son texto literal del documento.

| Evento | Origen | Destino |
|---|---|---|
| Envío automático del proyecto a opinión técnica al emitir Viabilidad/Elegibilidad | Viabilizador / Sistema (FB paso 1) | Proceso de OT |
| Notificación de solicitud de OT (Anexo A2 a) | Sistema (FB paso 2; RN07 a) | Coordinador PRE (según FB y A2 a); Técnico PRE y Coordinador PRE (según RN07 a) |
| Habilitación del campo "Comentarios DGICP" | Sistema (FB paso 2) | Técnico PRE |
| Asignación del caso (Anexo A2 b) | Coordinador PRE (RN07 b) | Técnico PRE |
| Envío de comentarios; habilitación de edición CU-PRE-04 a CU-PRE-23 y de CU-PRE-24/25; estado "Observado" (Anexo A2 c) | Técnico PRE / Sistema (FA03; RN07 c) | Técnico URP y Viabilizador |
| Envío de comentarios de Elegibilidad (Anexo A2 h) | Técnico PRE (botón "Enviar comentarios Elegibilidad") | Viabilizador |
| Activación del botón "Enviar ajustes" | Sistema (RN06) | Técnico URP |
| Atención de ajustes (Anexo A2 d) | Técnico URP / Sistema (FA03.1 paso 3.1.3; RN07 d) | Técnico PRE, Viabilizador y Coordinador PRE |
| Alerta dos días antes del vencimiento (Anexo A2 f) | Sistema (RN08) | Técnico URP y Viabilizador |
| Vencimiento del plazo; eliminación, archivo y retiro de la bandeja (Anexo A2 g) | Sistema (RN09) | Técnico URP y Viabilizador; Bandeja de Preinversión (CU-PRE-03) |
| Habilitación del botón "Visto bueno OT" | Sistema (FA01 paso 1.2) | Coordinador PRE |
| Notificación de visto bueno y habilitación de "OT favorable" | Sistema (FA01 paso 1.4) | Técnico PRE |
| Emisión de OT favorable: estado "Proyecto con OT", notificación (Anexo A2 e), mensaje A.5 | Sistema (FA01 paso 1.6; RN07 e) | Todos los actores; Técnico PRE (mensaje) |
| Inhabilitación de botones "Enviar comentarios", "Enviar Ajustes", "Guardar" | Sistema (RN10) | Pantalla Anexo A.1 |
| Disponibilidad en Captura de Proyectos tras OT para etapa de Ejecución | Sistema (RN11) | CU-PRE-03 |
| Continuar con la priorización | Mensaje Anexo A.5 (RN17) | CU-PRE-26.5 Priorización |
| Actualización de OT: confirmación, validación de OT previa, habilitación de campos | Técnico URP / Sistema (FA04) | Pantallas del proyecto; CU-PRE-24 Viabilidad |

---

# Integraciones

> Nota sobre información derivada: el documento no incluye una sección de integraciones. La tabla fue construida por el análisis; la clasificación de la columna "Tipo" y las descripciones son síntesis a partir de las referencias citadas, no texto literal del documento.

| Sistema | Tipo | Descripción |
|---|---|---|
| Correo electrónico | Notificación | Envío de correos según RN07, RN08, RN09 y Anexo A2. > El documento no especifica el sistema o servicio de correo. |
| CU-PRE-01 Registro de Proyectos | Interna (origen de datos) | CUP, Nombre del proyecto, Unidad Ejecutora; categoría "Proyecto de emergencia". |
| CU-PRE-3.5 Selección y registro de etapas | Interna (origen de datos / configuración) | Etapa actual y futura; campos a habilitar para Actualización de O.T.; Anexo A.4 del CU-PRE-3.5 (RN13). |
| CU-PRE-04, 07, 11, 12, 17, 18, 21, 22.1, 23 | Interna (origen de datos) | Datos mostrados en "Apartados", "Histórico" e "Informe OT" (Anexo B.1). |
| CU-PRE-24 Viabilidad / CU-PRE-25 Elegibilidad | Interna (proceso) | Habilitación para ajustes y reenvío al proceso de aprobación (FA03, FA03.1, FA04, RN14). |
| CU-PRE-03 Captura de proyectos / Bandeja de Preinversión | Interna (proceso) | Listado de proyectos a revisar (FB paso 3); retiro de solicitudes vencidas (RN09); disponibilidad tras OT de Ejecución (RN11). |
| CU-PRE-26.5 Priorización | Interna (proceso) | Continuación tras la emisión de OT (RN17, Anexo A.5). |

---

# Datos Pendientes de Definir

1. **Prioridad** del caso de uso: no especificada.
2. **Actor principal:** el documento no designa uno.
3. **Disparador:** contradicción entre el envío automático al emitir Viabilidad/Elegibilidad (FB paso 1) y la solicitud manual con el botón "Solicitar OT" (RN04, Anexo B.1).
4. **Resultado de la validación fallida en FA04 paso 4.4** (inexistencia de OT previa para la etapa) y **acción del botón "NO"** del Anexo A.3: no especificados.
5. **Rol autorizado para registrar "Justificación Institución"/"Respuesta Institución":** RN03 (solo Técnico URP) vs. FA02, FA03.1 y Anexo B.1 (Técnico URP o Viabilizador).
6. **Destinatarios exactos de las notificaciones "a" y "d"** (RN07 vs. títulos del Anexo A2 y FB/FA03.1).
7. **Botón "Enviar comentarios Elegibilidad"** (Anexo A2 h), **botón "Guardar ajustes"** (FA03.1) y **botón "HABILITAR"** (mockup extendido): no aparecen en los mockups principales ni en el Anexo B.1, o no tienen comportamiento descrito.
8. **Naturaleza de "Visto bueno OT"** (botón según FA01, casilla según mockup) y su ausencia en el mockup extendido.
9. **Texto definitivo del mensaje A.5** ("…con Opinión Técnica, …" en el mockup vs. "…con Opinión Técnica Favorable, …" en FA01 paso 1.6).
10. **"Fecha de emisión de OT" y "N° de nota de OT":** si son valores automáticos o de captura manual (contradicción "Editable: Sí" vs. "Muestra…").
11. **Detalles de "Nombre del proyecto", "CUP", "Problema central" y "Unidad Ejecutora" en la Pantalla "Ver informe OT"** (intercambiados, no coincidentes o vacíos).
12. **Campo "Convenio"** del Informe OT: sin definición en el Anexo B.1.
13. **Campo "Observaciones" y contador de devoluciones (RN15):** no definidos en pantalla ni en el Anexo B.1.
14. **Lista definitiva de apartados** a revisar en la pantalla de Opinión Técnica ("Campos requeridos" vs. mockup extendido).
15. **Formato de fechas del Histórico** (DD/MM/AAAA del Anexo B.1 vs. M/D/AAAA y AAAA-MM-DD en los mockups).
16. **Identificador del flujo "Actualización de Opinión Técnica"** (FA01 duplicado / "Sub Flujo 4") y paso faltante 3.1.2 en FA03.1.
17. **Fecha vigente del documento** ("MAYO 2026" en encabezado vs. "SEP 2025" en Historial de Revisiones).
18. **Campos a habilitar en la Actualización de OT:** dependen de la hoja de cálculo "CU-PRE-3.5 … ANEXO", no incluida.
19. **Actor autorizado para la Actualización de OT:** Anexo A – RN3 ("viabilizador o Técnico URP") vs. FA04 (solo Técnico URP).
20. **Obligatoriedad de campos:** alcance de "Campos requeridos" frente a campos opcionales o condicionales del Anexo B.1; significado de "Campo obligatorio" en "Fecha de solicitud" (no editable, calculada por el Sistema); condición en que "Comentarios DGICP" es obligatorio si la OT puede emitirse sin enviar comentarios.
21. **Clasificación de actores:** el documento no distingue actor principal ni secundarios; tampoco se puede determinar si "DGI" e "Institución Ejecutora" son roles del sistema u organizaciones.
