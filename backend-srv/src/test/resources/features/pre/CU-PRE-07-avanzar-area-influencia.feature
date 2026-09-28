# language: es
@CU-PRE-07 @rol:TECNICO_URP
Característica: Avanzar a Área de influencia

  Como Técnico URP
  Quiero avanzar a "Área de influencia" tras completar el análisis de la población

  Escenario: Avanzar a Área de influencia (camino feliz, FA-02)
    Dado que el Técnico URP se encuentra en la pantalla "Análisis de la Población"
    Cuando hace clic en el botón "Siguiente"
    Entonces el sistema avanza a la sección "Área de influencia" (CU-PRE-08)