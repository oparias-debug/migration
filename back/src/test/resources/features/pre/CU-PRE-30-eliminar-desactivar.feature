# language: es
@CU-PRE-30 @rol:TECNICO_URP
Característica: Eliminar etapas/fuentes de financiamiento y desactivar estudios

  Como Técnico URP
  Quiero eliminar una etapa o fuente de financiamiento no deseada, o desactivar un estudio no iniciado

  Escenario: Eliminar una fuente de financiamiento sin ejecución de años anteriores
    Cuando el Técnico URP intenta eliminar una fuente de financiamiento
    Entonces el sistema pregunta "¿Está seguro de eliminar la fuente de financiamiento seleccionada?"
    Cuando el Técnico URP confirma
    Entonces el sistema elimina la fuente de financiamiento (RN-C)

  Escenario: Eliminar una etapa sin ejecución de años anteriores
    Cuando el Técnico URP intenta eliminar una etapa
    Entonces el sistema pregunta "¿Está seguro de eliminar la etapa seleccionada?"
    Cuando el Técnico URP confirma
    Entonces el sistema elimina la etapa (RN-C)

  Escenario: Bloquear la eliminación cuando existe ejecución de años anteriores
    Dado que la etapa o fuente de financiamiento seleccionada tiene ejecución de años anteriores relacionada para ese CUP
    Cuando el Técnico URP intenta eliminarla
    Entonces el sistema muestra el mensaje "Etapa y/o Fuente de Financiamiento no puede ser eliminado, existe ejecución en años anteriores" (Anexo A.6)

  Escenario: Desactivar un estudio no iniciado dentro del período de elaboración
    Dado un estudio no iniciado y el período de elaboración del PAP vigente
    Cuando el Técnico URP intenta eliminarlo de la tabla de programación
    Entonces el sistema pregunta "¿está seguro de desactivar el proyecto con código XXX?" (RN-D)
    Cuando el Técnico URP confirma
    Entonces el sistema desactiva el estudio

  Escenario: No se puede eliminar un estudio de arrastre con programación incompleta
    Dado un estudio de arrastre que no cumplió el 100% de lo programado física o financieramente en periodos anteriores
    Cuando el Técnico URP intenta eliminarlo
    Entonces el sistema no permite la eliminación (RN-D)