# language: es
@CU-PRE-02 @rol:COORDINADOR_PRE
Característica: Desarchivar una solicitud de la Bandeja Preinversión

  Como Coordinador PRE
  Quiero devolver a las solicitudes activas una solicitud que archivé
  Para corregir un archivo hecho por error sin perder el estado en que estaba

  Antecedentes:
    Dado que el Coordinador PRE archivó una solicitud desde la "Bandeja Preinversión"
    Y esa solicitud aparece en el "Reporte de solicitudes Preinversión archivadas"

  Escenario: Desarchivar una solicitud archivada a mano (camino feliz, RN11)
    Cuando el Coordinador PRE hace clic en el botón "Desarchivar" de la fila de la solicitud
    Entonces el sistema muestra un aviso que advierte que la solicitud volverá con el estado que tenía antes de archivarse
    Cuando el Coordinador PRE hace clic en "Aceptar"
    Entonces el sistema devuelve la solicitud a la tabla "Solicitudes Activas"
    Y la solicitud conserva el estado y el Técnico PRE que tenía antes de archivarse
    Y la solicitud desaparece del "Reporte de solicitudes Preinversión archivadas"
    Y el sistema informa que la solicitud volvió a las solicitudes activas

  Escenario: Cancelar el desarchivo
    Cuando el Coordinador PRE hace clic en el botón "Desarchivar" de la fila de la solicitud
    Y hace clic en "Cancelar" en el aviso de confirmación
    Entonces la solicitud permanece archivada
    Y el sistema no llama al servicio de desarchivo

  Escenario: Una solicitud archivada automáticamente no puede desarchivarse (RN11)
    Dado que la solicitud fue archivada por el sistema tras tres meses sin respuesta (RN-4 de CU-PRE-01)
    Cuando el Coordinador PRE hace clic en el botón "Desarchivar" de esa fila
    Y confirma la acción
    Entonces el servicio responde que no existe un estado previo al cual volver
    Y el sistema muestra ese mensaje sin devolver la solicitud a las activas

  Escenario: El botón no se ofrece a quien no puede archivar
    Dado que un Técnico PRE consulta el "Reporte de solicitudes Preinversión archivadas"
    Entonces el sistema no muestra el botón "Desarchivar" en ninguna fila
