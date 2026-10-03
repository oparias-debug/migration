# language: es
@CU-PRE-24 @HU-PRE-24-01 @rol:TECNICO_URP @rol:VIABILIZADOR
Característica: Solicitud de Viabilidad de un proyecto

  Como Técnico URP
  Quiero solicitar Viabilidad de un proyecto adjuntando el documento de Preinversión

  Escenario: Se habilita "Solicitar Viabilidad" al cargar el documento de Preinversión
    Dado que el Técnico URP está en la pestaña "Gestión del Proyecto", sección "Viabilidad" de un proyecto
    Y el botón "Solicitar Viabilidad" no está habilitado
    Cuando el Técnico URP carga el "Documento de Preinversión"
    Entonces el Sistema habilita el botón "Solicitar Viabilidad"

  Escenario: "Solicitar Viabilidad" no se habilita sin el documento de Preinversión
    Dado que el Técnico URP está en la pestaña "Gestión del Proyecto", sección "Viabilidad" de un proyecto
    Cuando el "Documento de Preinversión" no ha sido cargado
    Entonces el botón "Solicitar Viabilidad" no está habilitado

  Esquema del escenario: Solicitud de Viabilidad exitosa
    Dado que la solicitud corresponde a <situacion>
    Y el registro de la información desde CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto" está completo, según corresponda
    Y el Técnico URP ha cargado el "Documento de Preinversión"
    Cuando el Técnico URP da clic en el botón "Solicitar Viabilidad"
    Entonces el Sistema muestra el mensaje emergente "la solicitud del proyecto ha sido enviada al proceso de viabilidad exitosamente"
    Y el Sistema envía una notificación al Viabilizador informando que le ha llegado la solicitud
    Y el Sistema bloquea para edición los campos de CU-PRE-04 "Identificación" hasta CU-PRE-23 "Indicadores del Proyecto"
    Y el Sistema deshabilita el botón "Solicitar Viabilidad"
    Y el Sistema activa el botón "Enviar comentarios" para el Viabilizador

    Ejemplos:
      | situacion                                                                                                                                                     |
      | la primera solicitud de Viabilidad del proyecto                                                                                                               |
      | una nueva solicitud tras comentarios del Viabilizador, con el proyecto en estado "Observado"                                                                  |
      | una respuesta a una observación de la OT, con todos los comentarios de OT respondidos en la columna "Justificación Institución" de CU-PRE-26 "Opinión técnica" |

  Escenario: Consultar los comentarios de la Opinión Técnica
    Dado que el Técnico URP está en la pantalla del Anexo A.1 de un proyecto con observaciones de OT
    Cuando el Técnico URP da clic en el botón "Ver comentarios OT"
    Entonces el Sistema lo remite a los comentarios de CU-PRE-26 "Opinión técnica" para diligenciar la columna "Justificación Institución"

  Escenario: Rechazo de la solicitud con comentarios de OT sin responder
    Dado que la solicitud de Viabilidad responde a una observación de la OT
    Y el Técnico URP no ha respondido todos los comentarios emitidos por OT en la columna "Justificación Institución" de CU-PRE-26 "Opinión técnica"
    Y el Técnico URP ha cargado el "Documento de Preinversión"
    Cuando el Técnico URP da clic en el botón "Solicitar Viabilidad"
    Entonces el Sistema muestra el mensaje "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad"
    Y el Sistema no permite solicitar Viabilidad

  Escenario: El botón "Solicitar Viabilidad" no está habilitado para el Viabilizador
    Dado que el "Documento de Preinversión" del proyecto ha sido cargado
    Cuando el Viabilizador accede a la pestaña "Gestión del Proyecto", sección "Viabilidad" del proyecto
    Entonces el botón "Solicitar Viabilidad" no está habilitado para el Viabilizador

  # ℹ️ Sin escenario: comportamiento cuando falla la validación del FB1 paso 4 (registro incompleto de CU-PRE-04 a CU-PRE-23). El CU no especifica mensaje ni acción.
  # ℹ️ Sin escenario: estado del proyecto tras solicitar Viabilidad (no especificado en el CU).
  # ℹ️ Sin escenario: carga de la "Nota de solicitud de OT". Ningún flujo ni regla indica cuándo se carga ni si es obligatoria.
  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original, contradicción 1: alcance de RN01 frente a la carga de documentos del Técnico URP y la visualización de "demás actores"). No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original, contradicción 3: FA01 paso 1.6 indica responder los comentarios del Viabilizador "en CU-PRE-26"). No implementar hasta que se resuelva.
