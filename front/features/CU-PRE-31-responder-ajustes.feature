# language: es
@CU-PRE-31 @rol:TECNICO_URP
Característica: Responder a las observaciones de la DGICP

  Como Técnico URP
  Quiero responder a las observaciones de la DGICP y enviar mis ajustes

  Escenario: Ajustar y enviar respuesta (camino feliz, SF-3 pasos 3-4)
    Dado que el Técnico PRE o el Coordinador PRE registraron observaciones
    Cuando el Técnico URP realiza los ajustes correspondientes
    Y registra información en el campo "Respuesta Institución"
    Y hace clic en el botón "Enviar Respuesta"
    Entonces el sistema guarda la información con la fecha de registro respuesta
    Y deshabilita los campos
    Y notifica al Técnico PRE por correo electrónico que el Técnico URP ha realizado ajustes

  Escenario: El Técnico URP no puede ver el campo Comentarios al reporte DGICP
    Entonces el campo "Comentarios al reporte DGICP" no es visible para el Técnico URP (RN-A.c)

  Escenario: El Técnico URP puede visualizar, pero no editar, el campo Observaciones DGICP
    Entonces el Técnico URP puede visualizar el campo "Observaciones DGICP", sin poder editarlo (RN-A.c)