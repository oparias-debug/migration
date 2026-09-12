# language: es
@CU-PRE-01 @rol:TECNICO_URP
Característica: Responder a las observaciones de la DGICP y reenviar la solicitud

  Como Técnico URP
  Quiero responder a las observaciones de la DGICP y reenviar la solicitud
  Para que la revisión del Técnico PRE continúe hasta la emisión del CUP

  Escenario: Responder a las observaciones y reenviar (camino feliz)
    Dado un proyecto en estado "Observado DGICP (Registro)"
    Cuando el sistema permite al Técnico URP visualizar los comentarios del Técnico PRE en la sección "Revisión PRE"
    Y habilita el campo "Respuesta"
    Y el Técnico URP ajusta los campos correspondientes en la pantalla "Nuevo registro" y/o digita comentarios justificativos en el campo "Respuesta"
    Y hace clic en el botón "Enviar"
    Entonces el sistema notifica al Técnico PRE por correo electrónico según el modelo del Anexo A.3.3
    Y guarda los datos registrados
    Y se pasa a la pantalla "Nuevo registro"

  # Nota: el CU indica que este ciclo (visualizar observaciones → ajustar/responder → enviar) puede repetirse tantas veces como observaciones se generen, hasta que el Técnico PRE emita el CUP. No se especifica un límite de repeticiones ni un comportamiento distinto entre ciclos; no se genera un escenario adicional para ello.