# language: es
@CU-PRE-06 @rol:TECNICO_URP
Característica: Avanzar a Población objetivo

  Como Técnico URP
  Quiero avanzar a "Población objetivo" tras completar la matriz de gestión de interesados

  Escenario: Avanzar a Población objetivo (camino feliz, FA-02)
    Dado que el Técnico URP se encuentra en la pantalla "Matriz de gestión de interesados"
    Cuando hace clic en el botón "Siguiente"
    Entonces el sistema avanza a la sección "Población objetivo" (CU-PRE-07)