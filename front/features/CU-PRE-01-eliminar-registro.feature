# language: es
@CU-PRE-01 @rol:TECNICO_URP
Característica: Eliminar un registro de proyecto antes de la primera solicitud de CUP

  Como Técnico URP
  Quiero eliminar un registro de proyecto antes de haber solicitado el CUP por primera vez

  Escenario: Eliminar un registro que nunca ha solicitado CUP (camino feliz)
    Dado un proyecto en la pantalla "Registro de Proyecto" (Anexo A.1) que nunca ha solicitado CUP
    Cuando el Técnico URP elimina el registro del proyecto
    Entonces el registro deja de aparecer en la pantalla "Registro de Proyecto"

   Escenario: Eliminar un registro antes de la primera solicitud de CUP
    Dado el Técnico URP tiene un registro en estado "En Elaboración" que nunca ha solicitado el CUP
    Cuando el Técnico URP elimina el registro
    Entonces el Sistema elimina la información de la "Bandeja de Registro de Proyectos"

  Escenario: No es posible eliminar un registro que ya solicitó el CUP
    Dado un proyecto ya tuvo al menos una solicitud de CUP registrada
    Cuando el Técnico URP intenta eliminar el registro
    Entonces el Sistema deniega la eliminación

  Escenario: El botón/acción de eliminar no está disponible para otros actores
    Dado un actor distinto de "Técnico URP" consulta un proyecto
    Entonces no puede eliminar el registro