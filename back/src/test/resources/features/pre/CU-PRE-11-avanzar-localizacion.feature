# language: es
@CU-PRE-11 @rol:TECNICO_URP
Característica: Avanzar a Localización

  Como Técnico URP
  Quiero avanzar a "Localización" tras completar la descripción técnica

  Escenario: Avanzar a Localización (camino feliz, FA-02)
    Dado que el Técnico URP se encuentra en la pantalla "Descripción Técnica" desc-tecnica
    Cuando hace clic en el botón "Siguiente"
    Entonces el sistema avanza exitosamente a la sección "Localización" (CU-PRE-12)