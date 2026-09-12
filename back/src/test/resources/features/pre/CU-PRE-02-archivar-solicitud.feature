# language: es
@CU-PRE-02 @rol:COORDINADOR_PRE
Característica: Archivar una solicitud de la Bandeja Preinversión

  Como Coordinador PRE
  Quiero archivar una solicitud de CUP u Opinión Técnica de la Bandeja Preinversión

  Antecedentes:
    Dado que el Coordinador PRE se encuentra en la pantalla "Bandeja Preinversión / Solicitudes Activas" (Anexo A.1)
    Y existe una solicitud registrada en la tabla "Solicitudes Activas"

  Escenario: Archivar una solicitud (camino feliz)
    Cuando el Coordinador PRE acerca el cursor al extremo izquierdo del nombre del proyecto
    Y el sistema muestra el botón interactivo "Archivar"
    Y el Coordinador PRE hace clic en el botón interactivo "Archivar"
    Entonces el sistema muestra el aviso de confirmación de archivo descrito en el Anexo A.3
    Cuando el Coordinador PRE hace clic en "Aceptar"
    Entonces el sistema asigna el estado "Archivado" a la solicitud
    Y la solicitud desaparece de la tabla "Solicitudes Activas"
    Y la solicitud aparece en la pantalla "Reporte de solicitudes Preinversión archivadas" (Anexo A.4) con estado "Archivado"
    Y el Coordinador PRE permanece en la pantalla "Bandeja Preinversión"

  Escenario: Cancelar el archivo de una solicitud
    Cuando el Coordinador PRE hace clic en el botón interactivo "Archivar"
    Y el sistema muestra el aviso de confirmación de archivo descrito en el Anexo A.3
    Y el Coordinador PRE hace clic en "Cancelar"
    Entonces no se ejecuta ninguna acción
    Y la solicitud permanece en la tabla "Solicitudes Activas"
    Y el Coordinador PRE permanece en la pantalla "Bandeja Preinversión"

  # ⚠️ Escenario pendiente: existe una discrepancia no resuelta entre el texto del mensaje de confirmación de archivo del paso 3.4 ("¿Está seguro de archivar esta solicitud?") y el del mockup del Anexo A.3 ("¿Desea archivar esta solicitud?"); no se transcribe un texto literal definitivo. Tampoco se resuelve si existe un botón "No" independiente del botón "Cancelar" (el paso 3.5 los menciona a ambos, el mockup solo muestra "CANCELAR"). No se implementa ningún detalle adicional hasta que el negocio lo aclare (ver Datos Pendientes de Definir del CU original).