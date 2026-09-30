# language: es
@CU-PRE-26.5 @rol:COORDINADOR_SYMP @rol:TECNICO_SYMP @rol:SISTEMA
Característica: Habilitar ajustes en la calificación del criterio 5

  Como Coordinador SYMP
  Quiero habilitar ajustes en la calificación del criterio 5 al Técnico SYMP encargado del caso

  Escenario: Habilitar al Técnico SYMP la calificación del criterio 5
    Dado que la pantalla "Priorización: Matriz Multicriterio" está deshabilitada para el Técnico SYMP del proyecto
    Y el usuario autenticado tiene el rol "Coordinador SYMP"
    Cuando acciona el campo "Habilitar Calificación de Prioridad"
    Entonces el Sistema habilita al Técnico SYMP encargado del caso los campos de calificación del criterio 5

  # Sin escenario: naturaleza del control (botón vs. casilla, "Editable: No") y flujo posterior al ajuste. Ver historias-CU-PRE-26.5.md.
