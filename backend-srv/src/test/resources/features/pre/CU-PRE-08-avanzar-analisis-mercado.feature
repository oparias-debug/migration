# language: es
@CU-PRE-08 @rol:TECNICO_URP
Característica: Avanzar a Análisis de mercado

  Como Técnico URP
  Quiero avanzar a "Análisis de mercado" tras completar el área de influencia

  Escenario: Avanzar a Análisis de mercado (camino feliz, FA-02)
    Dado que el Técnico URP se encuentra en la pantalla "Área de Influencia"
    Cuando hace clic en el botón "Siguiente"
    Entonces el sistema avanza a la sección "Análisis de mercado" (CU-PRE-09)