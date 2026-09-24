# language: es
@CU-PRE-15 @rol:TECNICO_URP
Característica: Registrar el análisis de riesgos de un proyecto

  Como Técnico URP
  Quiero registrar los riesgos del proyecto con su probabilidad e impacto
  Para conocer su calificación y las medidas que los atienden

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Análisis de Riesgos" de un proyecto con CUP

  Escenario: Registrar un riesgo y ver su calificación (camino feliz)
    Cuando el Técnico URP agrega una fila con la descripción del riesgo
    Y elige su probabilidad y su impacto
    Y hace clic en "Guardar"
    Entonces el sistema guarda el riesgo
    Y muestra la calificación que calcula el servidor a partir de la probabilidad y el impacto

  Escenario: La calificación se distingue a simple vista
    Dado que el análisis tiene un riesgo calificado como "MUY_ALTO"
    Entonces la calificación se muestra destacada respecto de un riesgo "BAJO"

  Escenario: Avanzar al análisis legal
    Dado que el análisis de riesgos está registrado
    Cuando el Técnico URP hace clic en "Siguiente"
    Entonces el sistema avanza a la pantalla "Análisis Legal"
