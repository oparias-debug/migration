# language: es
@CU-PRE-25 @HU-PRE-25-03 @rol:VIABILIZADOR @rol:TECNICO_PRE @rol:TECNICO_SYMP
Característica: Atención de comentarios a la Elegibilidad desde la gestión de Opinión Técnica

  Como Viabilizador
  Quiero ajustar la calificación de criterios y responder los comentarios de la Opinión Técnica para emitir nuevamente la Elegibilidad

  Escenario: La pantalla de elegibilidad permanece bloqueada tras el primer envío a Opinión Técnica
    Dado que el proyecto se envió a CU-PRE-26 "Opinión Técnica" por primera vez
    Y el Técnico PRE no ha dado clic en el botón "Enviar comentarios" desde CU-PRE-26 "Opinión Técnica"
    Cuando el Viabilizador accede a la pantalla del Anexo A.1 del proyecto
    Entonces la pantalla del Anexo A.1 está bloqueada

  Escenario: Los comentarios del Técnico PRE habilitan nuevamente la pantalla de elegibilidad
    Dado que el proyecto se envió a CU-PRE-26 "Opinión Técnica" por primera vez
    Cuando el Técnico PRE envía comentarios a la información registrada en la dimensión de la elegibilidad
    Y da clic en el botón "Enviar comentarios" desde CU-PRE-26 "Opinión Técnica"
    Entonces el Sistema notifica al Viabilizador que se emitieron comentarios
    Y el Sistema habilita los campos de la pantalla "Criterios de Elegibilidad del Proyecto"

  Escenario: Consultar los comentarios de la Opinión Técnica
    Dado que el Técnico PRE envió comentarios a la Elegibilidad desde CU-PRE-26 "Opinión Técnica"
    Cuando el Viabilizador da clic en el botón "Ver comentarios OT"
    Entonces el Sistema presenta el formulario de CU-PRE-26 "Opinión Técnica" para diligenciar el campo "Respuesta Institución" y oprimir el botón "Guardar Ajustes"

  Escenario: Nueva emisión de Elegibilidad con todos los comentarios respondidos
    Dado que el Técnico PRE envió comentarios a la Elegibilidad desde CU-PRE-26 "Opinión Técnica"
    Y el Viabilizador ajustó la información en el formulario del Anexo A.1
    Y el Viabilizador respondió todos los comentarios en el campo "Respuesta Institución" de CU-PRE-26 "Opinión Técnica" y oprimió "Guardar Ajustes"
    Cuando el Viabilizador da clic en el botón "Emitir Elegibilidad"
    Entonces el Sistema permite emitir la Elegibilidad nuevamente
    Y el Sistema notifica al Técnico PRE y al Técnico SYMP que fueron atendidas las observaciones a la Elegibilidad

  Escenario: Rechazo de la nueva emisión con comentarios de OT sin responder
    Dado que el Técnico PRE envió comentarios a la Elegibilidad desde CU-PRE-26 "Opinión Técnica"
    Y el Viabilizador no ha respondido todos los comentarios en el campo "Respuesta Institución" de CU-PRE-26 "Opinión Técnica"
    Cuando el Viabilizador da clic en el botón "Emitir Elegibilidad"
    Entonces el Sistema muestra el mensaje "Es necesario responder los comentarios de la Opinión Técnica previo a la emisión de elegibilidad"
    Y el Sistema no permite generar la Elegibilidad nuevamente

  Escenario: Cada devolución a elegibilidad conserva los comentarios emitidos en OT
    Dado que el proyecto ya fue devuelto a elegibilidad previamente con comentarios de OT
    Cuando el Técnico PRE envía nuevamente comentarios a la Elegibilidad desde CU-PRE-26 "Opinión Técnica"
    Entonces el Sistema guarda los comentarios emitidos en OT de esta devolución
    Y el Sistema conserva los comentarios de las devoluciones anteriores

  # ⚠️ Escenario pendiente: consulta en pantalla de las observaciones de cada devolución y contador de devoluciones (RN10), sin soporte en el mockup del Anexo A.1 ni en el Anexo B.1. No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: forma de presentar los comentarios de OT. FB2 paso 5 indica "ventana emergente" y RN15 indica "remitirlo" a CU-PRE-26. No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: alcance del ajuste tras comentarios de OT. FB2 paso 3 dice "(Dimensión 1)" y el anexo Excel describe 6 dimensiones. No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: nombre del campo de respuesta. "Respuesta Institución" (este CU) frente a "Justificación Institución" (CU-PRE-24 RN11). No implementar hasta que se resuelva.
  # ℹ️ Sin escenario: estado del proyecto tras la nueva emisión de Elegibilidad. El FB2 no lo indica.
