# language: es
@CU-PRE-11 @rol:TECNICO_PRE
Característica: Consultar la descripción técnica

  Como Técnico PRE
  Quiero consultar la descripción técnica de todas las Unidades Ejecutoras, sin poder editarla

  Escenario: Consultar la descripción técnica (camino feliz)
    Cuando el Técnico PRE accede a la pantalla "Descripción Técnica" de cualquier Unidad Ejecutora desc-tecnica
    Entonces el sistema muestra la información registrada por el Técnico URP en modo solo lectura desc-tecnica