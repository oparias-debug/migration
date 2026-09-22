# language: es
@CU-PRE-07 @rol:TECNICO_URP @rol:TECNICO_PRE
Característica: Acceder al Censo de Población y Vivienda 2024

  Como Técnico URP o Técnico PRE
  Quiero acceder al botón "Censo" para consultar el Censo de Población y Vivienda 2024 del Banco Central de Reserva

  Escenario: Acceder al Censo (camino feliz, RN03)
    Dado que el actor se encuentra en la pantalla "Análisis de la Población"
    Cuando hace clic en el botón "Censo"
    Entonces el sistema muestra la página del Geoportal del Banco Central de Reserva con el "Censo de Población y Vivienda 2024"