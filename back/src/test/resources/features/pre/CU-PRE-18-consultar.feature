# language: es
@CU-PRE-18 @rol:TECNICO_PRE @wip
Característica: Consultar el presupuesto de Operación y Mantenimiento

  Como Técnico PRE
  Quiero consultar el presupuesto de Operación y Mantenimiento de todas las Unidades Ejecutoras, sin poder editarlo

  Escenario: Consultar el presupuesto de Operación y Mantenimiento (camino feliz)
    Cuando el Técnico PRE accede a la pantalla "Presupuesto de Operación y Mantenimiento" de cualquier Unidad Ejecutora
    Entonces el sistema muestra la información registrada por el Técnico URP en modo solo lectura
