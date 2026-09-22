# language: es
@CU-PRE-12 @rol:TECNICO_PRE
Característica: Consultar la localización

  Como Técnico PRE
  Quiero consultar la localización de todas las Unidades Ejecutoras, sin poder editarla

  Escenario: Consultar la localización (camino feliz)
    Cuando el Técnico PRE accede a la pantalla "Localización" de cualquier Unidad Ejecutora localizacion
    Entonces el sistema muestra la información registrada por el Técnico URP en modo solo lectura localizacion