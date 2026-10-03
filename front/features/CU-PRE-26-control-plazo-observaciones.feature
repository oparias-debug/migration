# language: es
@CU-PRE-26 @rol:SISTEMA @rol:TECNICO_URP @rol:VIABILIZADOR
Característica: Controlar el plazo de atención de comentarios DGICP

  Como Sistema
  Quiero controlar el plazo de 5 días hábiles para atender los comentarios DGICP

  Escenario: Alertar dos días antes del vencimiento del plazo
    Dado que el Técnico PRE envió comentarios al proyecto
    Y han transcurrido 3 días hábiles desde el envío de comentarios
    Y el Técnico URP no ha dado clic en el botón "Enviar Ajustes"
    Cuando el Sistema evalúa el plazo de atención de observaciones
    Entonces envía al Técnico URP y al Viabilizador el correo electrónico del Anexo A2 f

  Escenario: No se alerta si el Técnico URP ya envió los ajustes
    Dado que el Técnico PRE envió comentarios al proyecto
    Y han transcurrido 3 días hábiles desde el envío de comentarios
    Y el Técnico URP dio clic en el botón "Enviar Ajustes"
    Cuando el Sistema evalúa el plazo de atención de observaciones
    Entonces no envía el correo electrónico del Anexo A2 f

  Escenario: Vencimiento del plazo de atención de observaciones
    Dado que el Técnico PRE envió comentarios al proyecto
    Y venció el plazo de 5 días hábiles de atención de observaciones
    Cuando el Sistema evalúa el plazo de atención de observaciones
    Entonces envía al Técnico URP y al Viabilizador el correo electrónico del Anexo A2 g
    Y elimina las solicitudes del módulo de gestión del proyecto (Viabilidad, soportes y OT observado)
    Y archiva la solicitud
    Y la solicitud deja de visualizarse en la Bandeja de Preinversión (CU-PRE-03)

  # Sin escenario: cómputo de días hábiles (remite a lineamientos no incluidos). Ver historias-CU-PRE-26.md.
