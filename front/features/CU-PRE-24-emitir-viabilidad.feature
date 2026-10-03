# language: es
@CU-PRE-24 @HU-PRE-24-03 @rol:VIABILIZADOR @rol:TECNICO_URP
Característica: Emisión de Viabilidad de un proyecto

  Como Viabilizador
  Quiero emitir la Viabilidad de un proyecto

  Escenario: "Emitir Viabilidad" no se habilita sin la justificación registrada
    Dado que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto con solicitud de Viabilidad
    Cuando el campo "Observaciones Generales/Justificación de la Viabilidad" no ha sido registrado
    Entonces el botón "Emitir Viabilidad" no está habilitado

  Escenario: "Emitir Viabilidad" se habilita al registrar y guardar la justificación
    Dado que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto con solicitud de Viabilidad
    Cuando el Viabilizador registra el campo "Observaciones Generales/Justificación de la Viabilidad"
    Y da clic en el botón "Guardar"
    Entonces el Sistema habilita el botón "Emitir Viabilidad"

  Escenario: "Ir a Elegibilidad" no está habilitado antes de emitir la Viabilidad
    Dado que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto con solicitud de Viabilidad
    Cuando el Viabilizador aún no ha dado clic en el botón "Emitir Viabilidad"
    Entonces el botón "Ir a Elegibilidad" no está habilitado

  Escenario: Emisión de Viabilidad por primera vez
    Dado que es la primera vez que se gestiona la Viabilidad del proyecto
    Y el Viabilizador registró y guardó el campo "Observaciones Generales/Justificación de la Viabilidad"
    Cuando el Viabilizador da clic en el botón "Emitir Viabilidad"
    Entonces el Sistema cambia el estado del proyecto a "Proyecto Viable"
    Y el Sistema muestra el mensaje "La viabilidad del proyecto ha sido emitida con éxito" con el texto "Es necesario continuar con la gestión de Elegibilidad"
    Y el mensaje muestra los botones "Ir a Elegibilidad" y "Salir"
    Y el Sistema notifica al Técnico URP que se ha emitido Viabilidad al proyecto
    Y el Sistema deshabilita la pantalla del Anexo A.1
    Y el Sistema habilita el botón "Ir a Elegibilidad"
    Y el Sistema permite la edición de la pantalla "Elegibilidad"

  Esquema del escenario: Opción seleccionada en el mensaje de emisión de Viabilidad
    Dado que el Viabilizador emitió la Viabilidad del proyecto por primera vez
    Y el Sistema muestra el mensaje del Anexo A.2 con los botones "Ir a Elegibilidad" y "Salir"
    Cuando el Viabilizador selecciona el botón "<opcion>"
    Entonces el Sistema <resultado>

    Ejemplos:
      | opcion            | resultado                                   |
      | Ir a Elegibilidad | envía al usuario a CU-PRE-25 "Elegibilidad" |
      | Salir             | queda en la pantalla del Anexo A.1          |

  Escenario: Emisión de Viabilidad cuando el proyecto ya contaba con Elegibilidad
    Dado que el proyecto ya pasó por un proceso de Elegibilidad
    Y el Viabilizador registró y guardó el campo "Observaciones Generales/Justificación de la Viabilidad"
    Cuando el Viabilizador da clic en el botón "Emitir Viabilidad"
    Entonces el Sistema cambia el estado del proyecto a "Proyecto Viable"
    Y el Sistema muestra el mensaje "La viabilidad del proyecto ha sido emitida con éxito"
    Y el Sistema notifica al Técnico URP que se ha emitido Viabilidad al proyecto
    Y el Sistema no habilita CU-PRE-25 "Elegibilidad"

  Esquema del escenario: Botones exclusivos del Viabilizador no habilitados para el Técnico URP
    Dado que la Viabilidad de un proyecto está en gestión en la pantalla del Anexo A.1
    Cuando el Técnico URP accede a la pestaña "Gestión del Proyecto", sección "Viabilidad" del proyecto
    Entonces el botón "<boton>" no está habilitado para el Técnico URP

    Ejemplos:
      | boton             |
      | Emitir Viabilidad |
      | Ir a Elegibilidad |

  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original, contradicción 4: texto "Es necesario continuar con la gestión de la Opinión Técnica" y botón "Ir a OT" de la variante con Elegibilidad previa, sin mockup propio; incluye la navegación a CU-PRE-26 y el permiso de "Ir a OT" de RN07). No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original, contradicción 7: tercer mensaje "¡Enviado! El proyecto fue enviado a Elegibilidad exitosamente" del anexo Excel v1.1). No implementar hasta que se resuelva.
  # ℹ️ Sin escenario: ventana 2 del Anexo A.2 (mensaje base + botón "ACEPTAR"). El CU no indica cuándo se muestra (Observación 17).
  # ℹ️ Sin escenario: caso "justificación registrada pero no guardada". El FA02 no incluye paso de guardado y el Anexo B.1 sí lo exige (Observación 11).
  # ℹ️ Sin escenario: RN09 (formato aplicable a todas las etapas del proyecto). El CU no enumera las etapas.
