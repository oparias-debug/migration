# language: es
@CU-PRE-20 @rol:TECNICO_PRE
Característica: Consultar los beneficios del proyecto

  Como Técnico PRE
  Quiero consultar los beneficios del proyecto de todas las Unidades Ejecutoras, sin poder editarlos

  Escenario: Consultar los beneficios del proyecto (camino feliz)
    Cuando el Técnico PRE accede a la pantalla "Beneficios del Proyecto" de cualquier Unidad Ejecutora - flujo-beneficios
    Entonces el sistema muestra la información registrada por el Técnico URP en modo solo lectura - flujo-beneficios
