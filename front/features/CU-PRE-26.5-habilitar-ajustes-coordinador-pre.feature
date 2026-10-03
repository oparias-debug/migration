# language: es
@CU-PRE-26.5 @rol:COORDINADOR_PRE @rol:TECNICO_PRE @rol:SISTEMA
Característica: Habilitar ajustes en la calificación de los criterios 1, 2, 3 y 4

  Como Coordinador PRE
  Quiero habilitar ajustes en la calificación de los criterios 1, 2, 3 y 4 al Técnico PRE encargado del caso

  Escenario: Habilitar al Técnico PRE la calificación de los criterios 1, 2, 3 y 4
    Dado que la pantalla "Priorización: Matriz Multicriterio" está deshabilitada para el Técnico PRE del proyecto
    Y el usuario autenticado tiene el rol "Coordinador PRE"
    Cuando acciona el campo "Habilitar Calificación de Prioridad"
    Entonces el Sistema habilita al Técnico PRE encargado del caso los campos de calificación de los criterios 1, 2, 3 y 4

  # Sin escenario: naturaleza del control (botón vs. casilla, "Editable: No") y flujo posterior al ajuste. Ver historias-CU-PRE-26.5.md.
