# language: es
@CU-PRE-24 @HU-PRE-24-02 @rol:VIABILIZADOR @rol:TECNICO_URP
Característica: Revisión de la ficha del proyecto y envío de comentarios por el Viabilizador

  Como Viabilizador
  Quiero revisar la ficha del proyecto y devolver comentarios al Técnico URP cuando se requieran ajustes

  Escenario: Acceso a la ficha de Viabilidad desde la notificación
    Dado que el Técnico URP solicitó Viabilidad para un proyecto
    Y el Sistema notificó al Viabilizador que hay un proyecto en bandeja con un link al formulario del Anexo A.1
    Cuando el Viabilizador da clic en el link de la notificación
    Entonces el Sistema despliega la pantalla del Anexo A.1

  Esquema del escenario: Campos de la ficha del proyecto en modo consulta
    Dado que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto
    Cuando el Viabilizador consulta la ficha del proyecto
    Entonces el campo "<campo>" muestra la información registrada en <origen>
    Y el campo "<campo>" no es editable

    Ejemplos:
      | campo                     | origen                                                   |
      | CUP                       | CU-PRE-01 "Registro de Proyectos"                        |
      | Nombre del proyecto       | CU-PRE-01 "Registro de Proyectos"                        |
      | Objetivo General          | CU-PRE-04 "Identificación"                               |
      | Descripción               | CU-PRE-11 "Descripción técnica"                          |
      | Productos                 | CU-PRE-23 "Indicadores del Proyecto"                     |
      | Población objetivo        | CU-PRE-07 "Población Objetivo"                           |
      | Inversión estimada        | CU-PRE-17 "Presupuesto de inversión"                     |
      | Resumen del presupuesto   | CU-PRE-17 "Presupuesto de inversión"                     |
      | Costo de operación        | CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" |
      | Costo de mantenimiento    | CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" |
      | Fuente de financiamiento  | CU-PRE-17 "Presupuesto de inversión"                     |
      | Indicadores de evaluación | CU-PRE-21 "Flujo de Caja y cálculo de Indicadores"       |

  Esquema del escenario: Link a la tabla de origen de los campos de consulta
    Dado que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto
    Cuando el Viabilizador da clic en el link del campo "<campo>"
    Entonces el Sistema lo direcciona a la tabla de <origen>

    Ejemplos:
      | campo                     | origen                                                   |
      | Productos                 | CU-PRE-23 "Indicadores del Proyecto"                     |
      | Población objetivo        | CU-PRE-07 "Población Objetivo"                           |
      | Inversión estimada        | CU-PRE-17 "Presupuesto de inversión"                     |
      | Resumen del presupuesto   | CU-PRE-17 "Presupuesto de inversión"                     |
      | Costo de operación        | CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" |
      | Costo de mantenimiento    | CU-PRE-18 "Flujo de costos de Operación y Mantenimiento" |
      | Indicadores de evaluación | CU-PRE-21 "Flujo de Caja y cálculo de Indicadores"       |

  Escenario: La inversión estimada se muestra con separador de miles
    Dado que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto
    Cuando el Viabilizador consulta el campo "Inversión estimada"
    Entonces el Sistema muestra el valor con el separador de miles (,)

  Escenario: Información de apoyo sobre los criterios a revisar en cada campo
    Dado que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto
    Cuando el Viabilizador acerca el cursor al signo de pregunta de un campo de la "FICHA DEL PROYECTO"
    Entonces el Sistema muestra la información de apoyo sobre los criterios a revisar en ese campo

  Escenario: Guardar los comentarios del Viabilizador
    Dado que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto con solicitud de Viabilidad
    Cuando el Viabilizador registra comentarios en la columna "Comentarios del Viabilizador" y en el campo "Observaciones Generales/Justificación de la Viabilidad"
    Y da clic en el botón "Guardar"
    Entonces el Sistema guarda cada uno de los comentarios registrados por el Viabilizador

  Escenario: Enviar comentarios al Técnico URP
    Dado que el Viabilizador guardó sus comentarios en la pantalla del Anexo A.1 de un proyecto con solicitud de Viabilidad
    Cuando el Viabilizador da clic en el botón "Enviar comentarios"
    Entonces el Sistema cambia el estado del proyecto a "Observado"
    Y el Sistema habilita para edición los campos de CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto"
    Y el Sistema habilita el botón "Solicitar Viabilidad"
    Y el Sistema notifica al Técnico URP que el Viabilizador ha enviado comentarios a la información registrada, para su ajuste

  Escenario: Devolver el proyecto nuevamente conserva los comentarios de devoluciones anteriores
    Dado que el proyecto ya fue devuelto previamente con comentarios del Viabilizador
    Y el Técnico URP volvió a solicitar Viabilidad
    Cuando el Viabilizador registra y guarda nuevos comentarios
    Y da clic en el botón "Enviar comentarios"
    Entonces el Sistema cambia el estado del proyecto a "Observado"
    Y el Sistema conserva guardados los comentarios del Viabilizador de cada devolución

  Escenario: "Enviar comentarios" no está activo antes de la solicitud de Viabilidad
    Dado que el Técnico URP aún no ha dado clic en el botón "Solicitar Viabilidad"
    Cuando el Viabilizador accede a la pantalla del Anexo A.1 del proyecto
    Entonces el botón "Enviar comentarios" no está activo

  Escenario: El botón "Enviar comentarios" no está habilitado para el Técnico URP
    Dado que el Técnico URP solicitó Viabilidad para un proyecto
    Cuando el Técnico URP accede a la pestaña "Gestión del Proyecto", sección "Viabilidad" del proyecto
    Entonces el botón "Enviar comentarios" no está habilitado para el Técnico URP

  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original, contradicción 2: acceso por "Captura de proyectos", FB2 paso 2b y paso 4 "O ingresa…"). No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original, contradicción 5: consulta en pantalla de las observaciones de cada devolución y contador de devoluciones de RN10, sin soporte en mockup ni Anexo B.1). No implementar hasta que se resuelva.
