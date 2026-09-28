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
