# language: es
@CU-PRE-3.5 @rol:TECNICO_URP
Característica: Ver la Ficha de información general de un proyecto

  Como Técnico URP
  Quiero ver la Ficha de información general de un proyecto

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla del Anexo A.1

  Escenario: Ver la Ficha de información general (camino feliz)
    Cuando el Técnico URP hace clic en el botón "Ficha de información general"
    Entonces el sistema muestra la Ficha de información general del proyecto (Anexo A.3), generada a partir de los datos registrados en CU-PRE-01 "Registro de Proyectos"
    Y la ficha no permite edición (RN14)
    Cuando el Técnico URP hace clic en el botón "Regresar"
    Entonces el sistema regresa a la pantalla del Anexo A.1

  Escenario: El campo "Monto de inversión (ajustado en Ejecución)" solo se muestra en etapa de Ejecución
    Dado un proyecto que no se encuentra en etapa de Ejecución
    Entonces el campo "Monto de inversión (ajustado en Ejecución)" no se muestra en la Ficha de información general
    Dado un proyecto que se encuentra en etapa de Ejecución
    Entonces el campo "Monto de inversión (ajustado en Ejecución)" sí se muestra en la Ficha de información general (RN17)

  Escenario: El campo "Monto de inversión (ajustado en Ejecución)" se alimenta automáticamente
    Cuando se realiza un ajuste al monto de la ejecución del proyecto
    Entonces el sistema alimenta automáticamente el campo "Monto de inversión (ajustado en Ejecución)" (RN18)

  Escenario: Actualización de campos conforme a la última Opinión Técnica registrada
    Cuando se registra la última Opinión Técnica del proyecto
    Entonces el campo "Objetivo del Proyecto" se actualiza con el campo "Objetivo General" de CU-PRE-04 "Identificación"
    Y el campo "Monto Estimado de Inversión" se actualiza con el campo "Total inversión" del Anexo A.1 de CU-PRE-17 "Presupuesto de inversión"
    Y el campo "Descripción del Proyecto" se actualiza con la tabla "Descripción Técnica" del Anexo A.1 de CU-PRE-11 "Descripción técnica"