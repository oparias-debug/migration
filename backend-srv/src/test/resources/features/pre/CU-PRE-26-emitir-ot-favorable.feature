# language: es
@CU-PRE-26 @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:TECNICO_URP @rol:VIABILIZADOR @rol:SISTEMA
Característica: Emitir Opinión Técnica Favorable

  Como Técnico PRE
  Quiero registrar las conclusiones y emitir la Opinión Técnica Favorable del proyecto

  Escenario: Guardar las conclusiones habilita el visto bueno
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y revisó la información registrada y los documentos anexos del proyecto
    Cuando registra información en el campo "Conclusiones"
    Y da clic en el botón "Guardar"
    Entonces el Sistema guarda la información registrada en "Conclusiones"
    Y habilita el "Visto bueno OT"

  Escenario: Sin conclusiones guardadas no se habilita el visto bueno
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y el campo "Conclusiones" no ha sido registrado y guardado
    Cuando ingresa a la pantalla "Opinión Técnica" del proyecto
    Entonces el "Visto bueno OT" no está habilitado

  Escenario: Emitir la Opinión Técnica Favorable
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y el Coordinador PRE dio el visto bueno de la OT
    Y cargó el archivo "Nota de OT" firmado por el Director DGICP
    Cuando da clic en el botón "OT favorable"
    Entonces el estado del proyecto cambia a "Proyecto con OT"
    Y el Sistema envía a todos los actores el correo electrónico del Anexo A2 e
    Y muestra al Técnico PRE el aviso del Anexo A.5 con el título "¡Opinión Técnica emitida!" y el botón "IR A PRIORIZACIÓN"

  Escenario: Continuar con la Priorización tras emitir la OT
    Dado que se muestra el aviso del Anexo A.5
    Cuando el Técnico PRE da clic en el botón "IR A PRIORIZACIÓN"
    Entonces el Sistema le permite continuar con la Priorización (CU-PRE-26.5)

  Esquema del escenario: Botones inhabilitados tras emitir la OT Favorable
    Dado que el Técnico PRE dio clic en el botón "OT favorable" del proyecto
    Cuando se muestra la pantalla "Opinión Técnica" del proyecto
    Entonces el botón "<boton>" está inhabilitado

    Ejemplos:
      | boton              |
      | Enviar comentarios |
      | Enviar Ajustes     |
      | Guardar            |

  Escenario: Proyecto con OT para la etapa de Ejecución disponible en Captura de Proyectos
    Dado que se emitió la Opinión Técnica del proyecto para la etapa de Ejecución
    Cuando un usuario ingresa a la pantalla "Captura de proyectos" (CU-PRE-03)
    Entonces el proyecto está disponible para visualización y/o actualización

  # Sin escenario: texto literal del aviso A.5 (FA01 paso 1.6 vs. mockup A.5). Ver historias-CU-PRE-26.md.
  # Sin escenario: "Fecha de emisión de OT" y "N° de nota de OT" (automático vs. captura manual). Ver historias-CU-PRE-26.md.
