# language: es
@CU-PRE-25 @HU-PRE-25-02 @rol:VIABILIZADOR @rol:TECNICO_URP @rol:TECNICO_PRE
Característica: Emisión de Elegibilidad de un proyecto

  Como Viabilizador
  Quiero emitir la Elegibilidad del proyecto una vez calificados sus criterios

  Escenario: Emisión de Elegibilidad
    Dado que el Viabilizador está en el formulario del Anexo A.1 de un proyecto en estado "Proyecto viable"
    Y registró la información de los criterios a los que contribuye el proyecto
    Cuando el Viabilizador da clic en el botón "Emitir Elegibilidad"
    Entonces el Sistema muestra el mensaje de emisión de Elegibilidad del Anexo A.3
    Y el mensaje muestra los botones "ACEPTAR" e "IR A OPINIÓN TÉCNICA"
    Y el Sistema cambia el estado del proyecto a "Proyecto Elegible"
    Y el Sistema notifica al Técnico URP y al Técnico PRE
    Y el Sistema habilita el formulario para emitir OT

  # ⚠️ Escenario pendiente: texto definitivo del mensaje de emisión. RN04 ("La Elegibilidad ha sido emitida con éxito. Puede continuar con la gestión de Opinión Técnica") difiere del mockup del Anexo A.3. No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: momento del bloqueo de la pantalla del Anexo A.1 y de las columnas "¿Aplica?" y "Especificar": al emitir (FB1 paso 6, RN04) o al enviar el proyecto a CU-PRE-26 por primera vez (RN07, RN09). No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: efecto de los botones "ACEPTAR" e "IR A OPINIÓN TÉCNICA" del Anexo A.3, relación con "IR A OT" del Anexo A.1 y actor que acciona "ir a OT" (FB1 paso 7: Técnico URP). No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: relación entre "se realiza una sola vez en cada proyecto" (Descripción) y las devoluciones ilimitadas a elegibilidad (RN10, FB2). No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: vigencia de la postcondición CU-PRE-32 y del comentario de revisión [2] (unificar las pantallas de Viabilidad y Elegibilidad). No implementar hasta que se resuelva.
  # ℹ️ Sin escenario: validaciones previas a "Emitir Elegibilidad" en la primera emisión (p. ej. si exige guardado previo o "Especificar" completo). El CU no las define.
  # ℹ️ Sin escenario: restricción de rol sobre "Emitir Elegibilidad". Ninguna regla la declara exclusiva del Viabilizador.
  # ℹ️ Sin escenario: mensaje "¡Enviado! El proyecto fue enviado a Opinión Técnica exitosamente" del anexo Excel, sin paso de flujo asociado.
