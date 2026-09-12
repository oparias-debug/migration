# language: es
@CU-PRE-3.5 @rol:TECNICO_URP
Característica: Modificar la Ruta de Preinversión de un proyecto

  Como Técnico URP
  Quiero modificar la Ruta de Preinversión ya establecida de un proyecto

  Antecedentes:
    Dado que el proyecto ya cuenta con una Ruta de Preinversión generada
    Y el Técnico URP se encuentra en la pantalla del Anexo A.2

  Escenario: Modificar la Ruta de Preinversión (camino feliz)
    Cuando el Técnico URP hace clic en el botón "Modificar"
    Entonces el sistema muestra el campo "Justifique Modificación", el campo "Identifique nueva Ruta de Preinversión" y el botón "Guardar"
    Cuando el Técnico URP diligencia el campo "Justifique Modificación" con la justificación del cambio
    Y selecciona la nueva ruta de preinversión marcando cada una de las etapas que desarrollará
    Y hace clic en el botón "Guardar"
    Entonces el sistema guarda la información
    Y se dirige a la pantalla del Anexo A.1 mostrando las etapas según la nueva selección para completar costos y fechas

  Escenario: Intentar modificar la ruta sin justificar
    Cuando el Técnico URP intenta seleccionar la nueva ruta de preinversión sin haber registrado el campo "Justifique Modificación"
    Entonces el sistema no habilita la selección de la nueva ruta de preinversión, ya que el campo es obligatorio (RN03)

  Escenario: Bloqueo de una etapa que ya cuenta con Opinión Técnica al requerir una nueva revisión
    Dado un proyecto de iniciativa "Proyecto" en formulación en alguna de las etapas PERFIL, PREFACTIBILIDAD, FACTIBILIDAD o DISEÑO
    Y una de esas etapas ya cuenta con Opinión Técnica emitida
    Cuando el Técnico URP modifica la Ruta de Preinversión seleccionando una etapa anterior a la ya emitida
    Entonces el sistema bloquea la etapa que ya contaba con Opinión Técnica
    Y no se pierde la información previamente registrada en esa etapa
    Y el Técnico URP deberá volver a pasar por el proceso de aprobación hasta obtener la Opinión Técnica nuevamente si actualiza dicha etapa