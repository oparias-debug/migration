# language: es
@CU-PRE-06 @rol:TECNICO_PRE
Característica: Consultar la matriz de gestión de interesados

  Como Técnico PRE
  Quiero consultar la matriz de gestión de interesados de todas las Unidades Ejecutoras, sin poder editarla

  Escenario: Consultar la matriz de gestión de interesados (camino feliz)
    Cuando el Técnico PRE accede a la pantalla "Matriz de gestión de interesados" de cualquier Unidad Ejecutora
    Entonces el sistema muestra la información registrada por el Técnico URP en modo solo lectura
    Y no permite su edición