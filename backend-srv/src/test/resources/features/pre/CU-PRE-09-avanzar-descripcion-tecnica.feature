# language: es
@CU-PRE-09 @rol:TECNICO_URP
Característica: Avanzar a Descripción Técnica

  Como Técnico URP
  Quiero avanzar a "Descripción Técnica" tras completar el análisis de mercado

  Escenario: Avanzar a Descripción Técnica (camino feliz, FA-02)
    Dado que el Técnico URP se encuentra en la pantalla "Análisis de Mercado"
    Cuando hace clic en el botón "Siguiente"
    Entonces el sistema avanza a la sección "Descripción Técnica" (CU-PRE-11)