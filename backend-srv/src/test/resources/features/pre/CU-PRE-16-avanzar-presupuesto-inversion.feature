# language: es
@CU-PRE-16 @rol:TECNICO_URP
Característica: Avanzar a Presupuesto de inversión

  Como Técnico URP
  Quiero avanzar a "Presupuesto de inversión" tras completar el análisis legal

  Escenario: Avanzar a Presupuesto de inversión (camino feliz, FA-02)
    Dado que el Técnico URP se encuentra en la pantalla "Análisis Legal" avanzar-analisis-legal
    Cuando hace clic en el botón "Siguiente" avanzar-analisis-legal
    Entonces el sistema avanza a la sección "Presupuesto de inversión" (CU-PRE-17) avanzar-analisis-legal