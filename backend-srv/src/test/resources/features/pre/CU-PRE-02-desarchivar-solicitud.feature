# language: es
@CU-PRE-02 @rol:COORDINADOR_PRE
Característica: Desarchivar una solicitud de la Bandeja Preinversión (RN11, nueva — no proviene del documento original)

  Como Coordinador PRE
  Quiero poder deshacer el archivo manual de una solicitud

  Antecedentes:
    Dado que el Coordinador PRE se encuentra en la pantalla "Bandeja Preinversión / Solicitudes Activas" (Anexo A.1)
    Y existe una solicitud registrada en la tabla "Solicitudes Activas"

  Escenario: Desarchivar una solicitud archivada manualmente (camino feliz)
    Dado que la solicitud fue archivada manualmente por el Coordinador PRE
    Cuando el Coordinador PRE desarchiva la solicitud
    Entonces la solicitud vuelve al estado que tenía antes de archivarse
    Y la solicitud reaparece en la tabla "Solicitudes Activas"
    Y la solicitud desaparece del "Reporte de solicitudes Preinversión archivadas"

  Escenario: Rechazar el desarchivo de una solicitud archivada automáticamente por el sistema
    Dado que la solicitud fue archivada automáticamente por el sistema tras 3 meses sin respuesta
    Cuando el Coordinador PRE intenta desarchivar la solicitud
    Entonces el sistema rechaza la operación porque no puede restaurar el estado previo

  Escenario: Rechazar el desarchivo de una solicitud que no está archivada
    Cuando el Coordinador PRE intenta desarchivar la solicitud
    Entonces el sistema rechaza la operación porque no puede restaurar el estado previo

  # Escenarios de la pantalla (añadidos por el frontend).
  Escenario: El desarchivo se confirma antes de ejecutarse
    Dado que la solicitud fue archivada manualmente por el Coordinador PRE
    Cuando el Coordinador PRE hace clic en el botón "Desarchivar" de la fila de la solicitud
    Entonces el sistema muestra un aviso que advierte que la solicitud volverá con el estado que tenía antes de archivarse
    Cuando el Coordinador PRE hace clic en "Aceptar"
    Entonces el sistema desarchiva la solicitud
    Y el sistema informa que la solicitud volvió a las solicitudes activas

  Escenario: Cancelar el desarchivo
    Cuando el Coordinador PRE hace clic en el botón "Desarchivar" de la fila de la solicitud
    Y hace clic en "Cancelar" en el aviso de confirmación
    Entonces la solicitud permanece archivada
    Y el sistema no llama al servicio de desarchivo

  Escenario: El botón no se ofrece a quien no puede archivar
    Dado que un Técnico PRE consulta el "Reporte de solicitudes Preinversión archivadas"
    Entonces el sistema no muestra el botón "Desarchivar" en ninguna fila

