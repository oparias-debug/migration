# language: es
@CU-PRE-01 @rol:SISTEMA
Característica: Alertar y eliminar automáticamente un registro inactivo

  Como Sistema
  Quiero alertar al Técnico URP sobre la posible eliminación de un registro inactivo y eliminarlo automáticamente si no se solicita el CUP a tiempo

  Escenario: Enviar alerta automática tras tres meses sin solicitud de CUP
    Dado que han transcurrido tres meses desde que el Técnico URP realizó un nuevo registro mediante el botón "Guardar"
    Y el Técnico URP no ha solicitado el CUP de ese registro
    Cuando se cumple el plazo de tres meses
    Entonces el sistema envía al Técnico URP un mensaje de alerta por correo electrónico
    Y el mensaje indica que la información será eliminada de la Bandeja de Registro de Proyectos 5 días hábiles después del envío, si no se solicita el CUP

  Escenario: Eliminación automática tras el plazo de 5 días hábiles sin acción
    Dado que el sistema envió la alerta de posible eliminación al Técnico URP
    Y transcurrieron 5 días hábiles posteriores al envío del mensaje sin que se solicitara el CUP
    Cuando se cumple dicho plazo
    Entonces el sistema elimina la información del registro de la Bandeja de Registro de Proyectos