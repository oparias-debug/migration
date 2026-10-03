# language: es
@CU-PRE-26 @rol:TECNICO_PRE @rol:COORDINADOR_PRE @rol:TECNICO_URP @rol:VIABILIZADOR @rol:SISTEMA
Característica: Revisar el proyecto y registrar comentarios DGICP

  Como Técnico PRE
  Quiero revisar la información registrada del proyecto y registrar comentarios DGICP

  Esquema del escenario: Listar en Captura de proyectos los proyectos pendientes de Opinión Técnica
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y existe un proyecto en el estado "<estado>" enviado a Opinión Técnica
    Cuando ingresa a la pantalla "Captura de proyectos" (CU-PRE-03)
    Entonces el proyecto aparece en la lista

    Ejemplos:
      | estado            |
      | Proyecto viable   |
      | Proyecto elegible |

  Escenario: Acceder a la pantalla de Opinión Técnica de un proyecto
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y está en la pantalla "Captura de proyectos" (CU-PRE-03)
    Cuando da clic en un proyecto
    E ingresa a la pestaña "Gestión de Proyectos" en la sección "Opinión Técnica"
    Entonces se muestra la pantalla del Anexo A.1
    Y los campos "CUP", "Nombre del proyecto", "Unidad Ejecutora", "Etapa actual" y "Etapa futura" no son editables
    Y el campo "Comentarios DGICP" está habilitado

  Escenario: Los apartados funcionan como visor con enlace a su pantalla de origen
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y está en la pantalla "Opinión Técnica" de un proyecto
    Entonces la columna "Apartados" no permite editar ningún campo
    Cuando da clic en el ícono de lápiz de un apartado
    Entonces el Sistema lo direcciona a la pantalla donde se registró la información de ese apartado

  Escenario: Guardar los comentarios DGICP
    Dado que el usuario autenticado tiene el rol "Técnico PRE"
    Y está en la pantalla "Opinión Técnica" de un proyecto
    Cuando registra información en el campo "Comentarios DGICP"
    Y da clic en el botón "Guardar"
    Entonces el Sistema guarda la información registrada en "Comentarios DGICP"

  Esquema del escenario: Técnico PRE y Coordinador PRE pueden registrar comentarios y conclusiones
    Dado que el usuario autenticado tiene el rol "<rol>"
    Cuando ingresa a la pantalla "Opinión Técnica" de un proyecto
    Entonces el campo "<campo>" está habilitado para registro

    Ejemplos:
      | rol             | campo                                 |
      | Técnico PRE     | Comentarios DGICP                     |
      | Técnico PRE     | Conclusiones                          |
      | Técnico PRE     | Comentarios DGICP a Elegibilidad      |
      | Técnico PRE     | Comentarios DGICP a documentos anexos |
      | Coordinador PRE | Comentarios DGICP                     |
      | Coordinador PRE | Conclusiones                          |
      | Coordinador PRE | Comentarios DGICP a Elegibilidad      |
      | Coordinador PRE | Comentarios DGICP a documentos anexos |

  Esquema del escenario: Otros roles no pueden registrar comentarios DGICP ni conclusiones
    Dado que el usuario autenticado tiene el rol "<rol>"
    Cuando ingresa a la pantalla "Opinión Técnica" de un proyecto
    Entonces el campo "<campo>" no está habilitado para registro

    Ejemplos:
      | rol          | campo                                 |
      | Técnico URP  | Comentarios DGICP                     |
      | Técnico URP  | Conclusiones                          |
      | Técnico URP  | Comentarios DGICP a Elegibilidad      |
      | Técnico URP  | Comentarios DGICP a documentos anexos |
      | Viabilizador | Comentarios DGICP                     |
      | Viabilizador | Conclusiones                          |
      | Viabilizador | Comentarios DGICP a Elegibilidad      |
      | Viabilizador | Comentarios DGICP a documentos anexos |

  Esquema del escenario: La sección "Comentarios Elegibilidad" solo se muestra en la primera gestión de OT
    Dado que la gestión de OT del proyecto es "<gestion>"
    Cuando se muestra la pantalla "Opinión Técnica" del proyecto
    Entonces la sección "Comentarios Elegibilidad" <visibilidad>

    Ejemplos:
      | gestion                | visibilidad   |
      | la primera             | se muestra    |
      | posterior a la primera | no se muestra |

  Escenario: Formulario de revisión para un proyecto de emergencia
    Dado un proyecto categorizado como "Proyecto de emergencia" en el CU-PRE-01
    Cuando se muestra la pantalla "Opinión Técnica" del proyecto
    Entonces el formulario muestra los campos del Anexo A.4 del CU-PRE-3.5 en lugar de los del Anexo A.1

  # Sin escenario: obligatoriedad de "Comentarios DGICP" (Anexo B.1 vs. FB paso 6 / FA01). Ver historias-CU-PRE-26.md.
