# language: es
@CU-PRE-14 @rol:TECNICO_PRE
Característica: Consultar el análisis ambiental

  Como Técnico PRE
  Quiero consultar el análisis ambiental de todas las Unidades Ejecutoras, sin poder editarlo

  Escenario: Consultar el análisis ambiental (camino feliz)
    Cuando el Técnico PRE accede a la pantalla "Análisis Ambiental" de cualquier Unidad Ejecutora analisis-ambiental
    Entonces el sistema muestra la información registrada por el Técnico URP en modo solo lectura analisis-ambiental