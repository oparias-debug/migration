# language: es
@CU-PRE-26 @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:TECNICO_URP @rol:VIABILIZADOR @rol:SISTEMA
Característica: Enviar comentarios DGICP a la Institución

  Como Técnico PRE
  Quiero enviar los comentarios DGICP a la Institución para que ajuste el proyecto

  Esquema del escenario: Visibilidad del botón "Enviar comentarios" según el rol
    Dado que el usuario autenticado tiene el rol "<rol>"
    Cuando ingresa a la pantalla "Opinión Técnica" de un proyecto
    Entonces el botón "Enviar comentarios" <visibilidad>

    Ejemplos:
      | rol             | visibilidad   |
      | Técnico PRE     | es visible    |
      | Coordinador PRE | es visible    |
      | Técnico URP     | no es visible |
      | Viabilizador    | no es visible |

  Esquema del escenario: El botón "Enviar comentarios" se activa según el estado del proyecto
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y el proyecto está en el estado "<estado>"
    Cuando ingresa a la pantalla "Opinión Técnica" del proyecto
    Entonces el botón "Enviar comentarios" está activo

    Ejemplos:
      | estado            |
      | Proyecto viable   |
      | Proyecto Elegible |

  Escenario: Enviar comentarios a los campos del proyecto
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y registró comentarios en "Comentarios DGICP" a los campos del proyecto
    Cuando da clic en el botón "Enviar comentarios"
    Entonces el Sistema guarda la información registrada en "Comentarios DGICP"
    Y habilita al Técnico URP la edición de todos los campos del CU-PRE-04 "Identificación" al CU-PRE-23 "Indicadores del Proyecto"
    Y habilita al Técnico URP el campo "Justificación Institución" de la pantalla del Anexo A.1
    Y habilita al Viabilizador las pantallas CU-PRE-24 "Viabilidad" y CU-PRE-25 "Elegibilidad"
    Y envía al Técnico URP y al Viabilizador el correo electrónico del Anexo A2 c
    Y cambia el estado del proyecto a "Observado"
    Y se activa el botón "Enviar ajustes" para el Técnico URP

  Esquema del escenario: Ruta de retorno del proyecto según el destino de los comentarios
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y registró comentarios a <destino_comentarios>
    Cuando da clic en el botón "Enviar comentarios"
    Entonces el proyecto vuelve al "<actor_destino>"
    Y el proceso de aprobación involucra los filtros "<filtros>"

    Ejemplos:
      | destino_comentarios                                       | actor_destino | filtros                      |
      | los campos del proyecto sin criterios de elegibilidad     | Técnico URP   | Viabilidad, OT               |
      | los campos del proyecto y a los criterios de elegibilidad | Técnico URP   | Viabilidad, Elegibilidad, OT |
      | solamente los criterios de elegibilidad                   | Viabilizador  | Elegibilidad, OT             |

  # Sin escenario: botón "Enviar comentarios Elegibilidad" y correo del Anexo A2 h — no definidos en mockups, flujos ni RN07. Ver historias-CU-PRE-26.md.
  # Sin escenario: RN15 (campo "Observaciones" y contador de devoluciones) — no definidos en pantalla. Ver historias-CU-PRE-26.md.
