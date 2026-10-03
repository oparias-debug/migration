# language: es
@CU-PRE-26.5 @rol:COORDINADOR_PRE @rol:TECNICO_PRE @rol:TECNICO_SYMP @rol:COORDINADOR_SYMP @rol:SISTEMA
Característica: Revisar la calificación de los criterios 1, 2, 3 y 4

  Como Coordinador PRE
  Quiero revisar la calificación de la prioridad realizada por el Técnico PRE

  Escenario: Registrar la revisión de la calificación del Técnico PRE
    Dado que el Técnico PRE calificó todos los subcriterios de los criterios 1, 2, 3 y 4 del proyecto
    Y el usuario autenticado tiene el rol "Coordinador PRE"
    Cuando revisa la calificación y registra la revisión "Priorización revisada Coordinador PRE"
    Entonces el Sistema muestra el mensaje del Anexo A.3 con el título "¡Calificado!"
    Y envía al Técnico SYMP y al Coordinador SYMP la notificación "Se ha realizado la calificación de los criterios 1, 2, 3 y 4 del proyecto [CUP], denominado [Nombre del proyecto]; para completar el puntaje de Priorización debe calificarse el criterio 5."
    Y la pantalla "Priorización: Matriz Multicriterio" queda deshabilitada para el Técnico PRE

  # ⚠️ Escenario pendiente: existe una contradicción sin resolver (ver CU original) sobre cómo se trata el criterio 5 en la pantalla A.2 "Prioridad del proyecto" que se muestra tras calificar solo los criterios 1–4 (FB1 Técnico PRE paso 8; Observaciones ítem 41). No implementar hasta que se resuelva.
  # Sin escenario: texto literal del mensaje A.3 ("Técnico SYMP" vs. "Técnico ASYMP") y acción del botón "ACEPTAR". Ver historias-CU-PRE-26.5.md.
