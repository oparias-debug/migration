# language: es
@CU-PRE-26.5 @rol:COORDINADOR_SYMP @rol:TECNICO_SYMP @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:SISTEMA
Característica: Revisar la calificación del criterio 5

  Como Coordinador SYMP
  Quiero revisar la calificación de la prioridad realizada por el Técnico SYMP

  Escenario: Registrar la revisión de la calificación del Técnico SYMP
    Dado que el Técnico SYMP calificó todos los subcriterios del criterio 5 del proyecto
    Y el usuario autenticado tiene el rol "Coordinador SYMP"
    Cuando revisa la calificación y registra la revisión "Priorización revisada Coordinador SYMP"
    Entonces el Sistema muestra el mensaje del Anexo A.4 "¡Calificado!" con el texto "Se ha completado la calificación de la prioridad del proyecto"
    Y muestra, bajo la pantalla del Anexo A.1, la pantalla del Anexo A.2 "Prioridad del proyecto" con los resultados de la priorización
    Y envía al Técnico PRE y al Coordinador PRE la notificación "Se ha completado la calificación de la prioridad del proyecto [CUP] denominado [Nombre del proyecto]."
    Y la pantalla "Priorización: Matriz Multicriterio" queda deshabilitada para el Técnico SYMP

  # Sin escenario: acción del botón "ACEPTAR" de A.4, texto y medio de las notificaciones. Ver historias-CU-PRE-26.5.md.
