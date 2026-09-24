# language: es
@CU-PRE-14 @rol:TECNICO_URP
Característica: Registrar el análisis ambiental de un proyecto

  Como Técnico URP
  Quiero registrar los impactos ambientales de un proyecto y sus medidas
  Para completar el capítulo 1.3.2 de la formulación

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Análisis Ambiental" de un proyecto con CUP

  Escenario: Declarar que el proyecto sí tiene impacto ambiental (camino feliz)
    Cuando el Técnico URP responde que sí a la pregunta de impacto ambiental
    Y agrega una fila con su medio, tipo de impacto, magnitud, duración y reversibilidad
    Y escribe la medida de mitigación y su costo
    Y hace clic en "Guardar"
    Entonces el sistema guarda el análisis completo
    Y muestra el total de los costos de mitigación que calcula el servidor

  Escenario: Declarar que el proyecto no tiene impacto ambiental
    Cuando el Técnico URP responde que no a la pregunta de impacto ambiental
    Y hace clic en "Guardar"
    Entonces el sistema guarda el análisis sin exigir filas de impacto

  Escenario: Quitar una fila registrada
    Dado que el análisis tiene dos filas de impacto
    Cuando el Técnico URP hace clic en "Quitar" en la primera fila
    Y hace clic en "Guardar"
    Entonces el análisis queda con una sola fila

  Escenario: El proyecto todavía no tiene análisis ambiental
    Dado que el proyecto nunca ha guardado su análisis ambiental
    Cuando el Técnico URP entra a la pantalla
    Entonces el sistema muestra la pantalla vacía y lista para registrar

  # ⚠️ Pendiente con el equipo de backend: la consulta responde 500 —una excepción, no un
  # análisis vacío— cuando el proyecto todavía no tiene análisis ambiental, que es el caso de
  # toda primera visita. El frontend lo trata como pantalla vacía para que se pueda registrar;
  # se pidió que devuelva un análisis vacío, como ya hace CU-PRE-16.
