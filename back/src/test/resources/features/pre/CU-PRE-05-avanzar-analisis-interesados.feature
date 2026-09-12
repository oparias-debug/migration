# language: es
@CU-PRE-05 @rol:TECNICO_URP
Característica: Avanzar a Análisis de interesados

  Como Técnico URP
  Quiero avanzar a "Análisis de interesados" tras completar el registro de alternativas de solución

  Antecedentes:
    Dado que el Técnico URP se encuentra en la sección "Registro de Alternativas" de la pestaña "Identificación del proyecto"

  Escenario: Avanzar a Análisis de interesados (camino feliz, SF-2)
    Dado que se ha registrado al menos una alternativa
    Y la Justificación está completa según corresponda
    Cuando el Técnico URP hace clic en el botón "Siguiente"
    Entonces el sistema avanza a la sección "Análisis de interesados" (CU-PRE-06)

  Escenario: Intentar avanzar sin haber registrado ninguna alternativa
    Dado que no se ha registrado ninguna alternativa
    Cuando el Técnico URP hace clic en el botón "Siguiente"
    Entonces el sistema muestra la alerta "Debe ingresar al menos una alternativa" (RN2-4)
    Y no avanza a "Análisis de interesados"

  Escenario: Intentar avanzar con una única alternativa sin justificación
    Dado que se ha registrado únicamente una alternativa
    Y el campo "Justificación" no ha sido completado
    Cuando el Técnico URP hace clic en el botón "Siguiente"
    Entonces el sistema muestra la alerta "Debe completarse el campo Justificación" (RN2-3)
    Y no avanza a "Análisis de interesados"