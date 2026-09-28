# language: es
@CU-PRE-01.5 @rol:TECNICO_PRE
Característica: Devolver la solicitud de CUP con observaciones

  Como Técnico PRE
  Quiero registrar observaciones y devolver la solicitud de CUP al Técnico URP
  Para que ajuste la información antes de que la solicitud pueda ser resuelta

  Antecedentes:
    Dado que el Técnico PRE ingresó a la pantalla "Nuevo Registro" desde el caso asignado en la Bandeja Preinversión (CU-PRE-02)
    Y el proyecto se encuentra en estado "Enviado a DGICP (Registro)"

  Escenario: Devolver la solicitud con observaciones (camino feliz)
    Cuando el Técnico PRE digita observaciones en el campo "Comentarios" de la sección "Revisión PRE"
    Y hace clic en el botón "Devolver"
    Entonces el sistema cambia el estado del proyecto a "Observado DGICP (Registro)"
    Y el sistema informa al Técnico URP por correo electrónico según el modelo del Anexo A.3.2
    Y el sistema pasa a la pantalla "Nuevo registro"
    Y habilita, para el Técnico URP, el campo "Respuesta" en CU-PRE-01

  # ⚠️ Escenario pendiente: el CU no especifica si el campo "Comentarios" es obligatorio para devolver la solicitud (la sección "Validaciones" no documenta ninguna regla para este caso de uso). No se genera un escenario de validación de campo obligatorio hasta que el negocio lo confirme.
  # ⚠️ Escenario pendiente: no se especifica un límite de ciclos para el flujo "Devolver → Enviar (CU-PRE-01) → nueva revisión"; el documento asume, sin confirmación del negocio, que puede repetirse indefinidamente hasta la emisión del CUP (ver Datos Pendientes de Definir del CU original). No se genera un escenario que verifique un número máximo de ciclos.