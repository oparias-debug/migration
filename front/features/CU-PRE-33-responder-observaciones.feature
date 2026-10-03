# language: es
@CU-PRE-33 @rol:TECNICO_URP
Característica: Responder a las observaciones sobre el avance de metas físicas

  Como Técnico URP
  Quiero responder a las observaciones de la DGICP sobre el avance de metas físicas

  Escenario: Ajustar y enviar respuesta (camino feliz, SF-2 pasos 4-5)
    Dado que el Técnico PRE o el Coordinador PRE registraron observaciones avance-metas
    Cuando el Técnico URP realiza los ajustes correspondientes y/o registra información en el campo "Respuesta Institución"
    Y hace clic en el botón "Enviar Respuesta" avance-metas
    Entonces el sistema guarda la información con la fecha de registro de la respuesta
    Y notifica al Técnico PRE por correo electrónico que el Técnico URP ha realizado ajustes avance-metas

  Escenario: El Técnico URP no puede ver los campos de comentarios de la DGICP
    Entonces los campos "Comentarios al reporte financiero DGICP" y "Comentarios al reporte de metas físicas" no son visibles para el Técnico URP (RN-A.b)

  Escenario: El Técnico URP puede visualizar, pero no editar, el campo Observaciones DGICP
    Entonces el Técnico URP puede visualizar el campo "Observaciones DGICP", sin poder editarlo (RN-A.b)