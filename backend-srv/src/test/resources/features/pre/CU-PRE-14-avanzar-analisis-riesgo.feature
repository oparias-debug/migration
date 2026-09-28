# language: es
@CU-PRE-14 @rol:TECNICO_URP
Característica: Avanzar a Análisis de Riesgo

  Como Técnico URP
  Quiero avanzar a "Análisis de Riesgo" tras completar el análisis ambiental

  Escenario: Avanzar a Análisis de Riesgo (camino feliz, FA-02)
    Dado que el Técnico URP se encuentra en la pantalla "Análisis Ambiental" avanzar-análisis-ambiental
    Cuando hace clic en el botón "Siguiente" avanzar-análisis-ambiental
    Entonces el sistema avanza a la sección "Análisis de Riesgo" (CU-PRE-15) avanzar-análisis-ambiental