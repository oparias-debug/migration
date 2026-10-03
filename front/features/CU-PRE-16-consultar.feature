# language: es
@CU-PRE-16 @rol:TECNICO_PRE
Característica: Consultar el análisis legal

  Como Técnico PRE
  Quiero consultar el análisis legal de todas las Unidades Ejecutoras, sin poder editarlo

  Escenario: Consultar el análisis legal (camino feliz)
    Cuando el Técnico PRE accede a la pantalla "Análisis Legal" de cualquier Unidad Ejecutora analisis-legal
    Entonces el sistema muestra la información registrada por el Técnico URP en modo solo lectura analisis-legal