# language: es
@CU-PRE-26 @rol:TECNICO_URP @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:VIABILIZADOR @rol:SISTEMA
Característica: Atender comentarios DGICP y enviar ajustes

  Como Técnico URP
  Quiero atender los comentarios DGICP ajustando el proyecto y registrando la justificación de la Institución

  Escenario: Guardar la justificación de la Institución
    Dado que el usuario autenticado tiene el rol "Técnico URP"
    Y el proyecto está en el estado "Observado"
    Cuando registra información en el campo "Justificación Institución" de un apartado comentado
    Y da clic en el botón "Guardar"
    Entonces el Sistema guarda la información registrada en "Justificación Institución"

  Esquema del escenario: Roles de la DGICP no pueden registrar la justificación de la Institución
    Dado que el usuario autenticado tiene el rol "<rol>"
    Y el proyecto está en el estado "Observado"
    Cuando ingresa a la pantalla "Opinión Técnica" del proyecto
    Entonces el campo "Justificación Institución" no está habilitado para registro

    Ejemplos:
      | rol             |
      | Técnico PRE     |
      | Coordinador PRE |

  Esquema del escenario: El botón "Solicitar Viabilidad" solo es visible para el Técnico URP
    Dado que el usuario autenticado tiene el rol "<rol>"
    Cuando ingresa a la pantalla CU-PRE-24 "Viabilidad" de un proyecto en estado "Observado"
    Entonces el botón "Solicitar Viabilidad" <visibilidad>

    Ejemplos:
      | rol             | visibilidad   |
      | Técnico URP     | es visible    |
      | Viabilizador    | no es visible |
      | Técnico PRE     | no es visible |
      | Coordinador PRE | no es visible |

  Escenario: Enviar los ajustes del proyecto
    Dado que el usuario autenticado tiene el rol "Técnico URP"
    Y el proyecto está en el estado "Observado"
    Y ajustó la información de las pantallas del CU-PRE-04 "Identificación" al CU-PRE-23 "Indicadores del Proyecto"
    Y respondió los comentarios en la columna "Justificación Institución" de la pantalla del Anexo A.1
    Y subió la documentación ajustada en el CU-PRE-24 "Viabilidad"
    Cuando da clic en el botón "Solicitar Viabilidad" del CU-PRE-24 "Viabilidad"
    Entonces el Sistema guarda la información de todos los campos ajustados y la nueva documentación
    Y envía al Técnico PRE y al Coordinador PRE el correo electrónico del Anexo A2 d
    Y el proyecto vuelve al proceso de aprobación

  # Sin escenario: registro de "Justificación Institución"/"Respuesta Institución" por el Viabilizador (RN03 vs. FA02, FA03.1 y Anexo B.1). Ver historias-CU-PRE-26.md.
  # Sin escenario: Viabilizador como destinatario del correo A2 d (RN07 d vs. Anexo A2 d). Ver historias-CU-PRE-26.md.
  # Sin escenario: botón "Guardar ajustes" / "Enviar ajustes" y "Fecha de ajustes". Ver historias-CU-PRE-26.md.
