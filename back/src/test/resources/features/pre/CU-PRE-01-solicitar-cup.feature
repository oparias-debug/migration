# language: es
@CU-PRE-01 @rol:TECNICO_URP
Característica: Solicitar el Código Único de Proyecto (CUP)

  Como Técnico URP
  Quiero solicitar a la DGICP la asignación del Código Único de Proyecto (CUP) de un proyecto registrado

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Nuevo registro" con la información del proyecto registrada

  Escenario: Solicitar el CUP sin inconsistencias (camino feliz)
    Cuando el Técnico URP hace clic en el botón "Solicitar CUP"
    Y el sistema realiza las validaciones descritas en el Anexo B.2
    Y no existen inconsistencias
    Entonces el sistema deshabilita la edición de todos los campos de la pantalla "Nuevo registro"
    Y el proyecto pasa al estado "Enviado a DGICP (Registro)"
    Y el sistema envía alerta al Coordinador PRE por correo electrónico según el modelo del Anexo A.3.1
    Y el sistema regresa a la pantalla "Registro de Proyecto"
    Y el proyecto aparece en la Bandeja de Preinversión (CU-PRE-02) con estado "Enviado a DGICP (Registro)"

  Escenario: Solicitar el CUP con campos incompletos
    Cuando el Técnico URP hace clic en el botón "Solicitar CUP"
    Y el sistema realiza las validaciones descritas en el Anexo B.2
    Y existen inconsistencias
    Entonces el sistema sombrea en rojo el contorno de cada campo con inconsistencia
    Y el sistema muestra en cada campo los mensajes descritos en el Anexo B.2
    Y se cancela la acción de solicitar CUP
    Y retorna a la pantalla "Nuevo registro"

  Escenario: Solo consulta mientras el proyecto está "Enviado a DGICP (Registro)"
    Dado un proyecto en estado "Enviado a DGICP (Registro)"
    Cuando el Técnico URP intenta acceder al registro
    Entonces el sistema solo permite consultar el registro
    Y no permite editarlo