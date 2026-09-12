# language: es
@CU-PRE-02 @rol:COORDINADOR_PRE
Característica: Asignar una solicitud a un Técnico PRE

  Como Coordinador PRE
  Quiero asignar una solicitud de CUP u Opinión Técnica a un Técnico PRE

  Antecedentes:
    Dado que el Coordinador PRE se encuentra en la pantalla "Bandeja Preinversión / Solicitudes Activas" (Anexo A.1)
    Y existe una solicitud registrada en la tabla "Solicitudes Activas"

  Escenario: Asignar una solicitud a un Técnico PRE (camino feliz)
    Cuando el Coordinador PRE selecciona un Técnico PRE del listado en el campo "Asignado a" (según el catálogo del Anexo C)
    Y hace clic en el botón "Guardar"
    Entonces el sistema muestra el aviso "¿Está seguro de asignar esta solicitud?" (Anexo A.2)
    Cuando el Coordinador PRE hace clic en "Aceptar"
    Entonces el sistema envía la alerta "Se ha asignado para revisión la solicitud XXX" al Técnico PRE
    Y el Coordinador PRE permanece en la pantalla "Bandeja Preinversión"

  Escenario: Cancelar la asignación de una solicitud
    Cuando el Coordinador PRE selecciona un Técnico PRE del listado en el campo "Asignado a"
    Y hace clic en el botón "Guardar"
    Y el sistema muestra el aviso "¿Está seguro de asignar esta solicitud?" (Anexo A.2)
    Y el Coordinador PRE hace clic en "Cancelar"
    Entonces no se realiza ninguna acción
    Y la ventana emergente desaparece
    Y el Coordinador PRE permanece en la pantalla "Bandeja Preinversión"

  Escenario: Reasignar el caso a otro Técnico PRE en cualquier estado
    Dado una solicitud previamente asignada a un Técnico PRE
    Cuando el Coordinador PRE selecciona un Técnico PRE distinto en el campo "Asignado a"
    Y confirma la reasignación
    Entonces el sistema permite el cambio del Técnico PRE asignado, sin importar el estado que presente la solicitud