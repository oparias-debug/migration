# language: es
@CU-PRE-26.5 @rol:JEFE_DGI @rol:SUBJEFE_DGI
Característica: Visualizar la priorización del proyecto

  Como Jefe DGI / Subjefe DGI
  Quiero visualizar las pantallas de priorización del proyecto

  Esquema del escenario: Visualizar las pantallas del caso de uso
    Dado que el usuario autenticado tiene el rol "<rol>"
    Cuando accede a la priorización de un proyecto
    Entonces puede visualizar la pantalla "<pantalla>"

    Ejemplos:
      | rol         | pantalla                                   |
      | Jefe DGI    | Priorización: Matriz multicriterio (A.1)   |
      | Jefe DGI    | Prioridad del proyecto (A.2)               |
      | Subjefe DGI | Priorización: Matriz multicriterio (A.1)   |
      | Subjefe DGI | Prioridad del proyecto (A.2)               |

  Esquema del escenario: Consultar la escala de calificación
    Dado que el usuario autenticado tiene el rol "<rol>"
    Y está visualizando el formulario "Priorización: Matriz multicriterio" de un proyecto
    Cuando da clic en el botón "Ver Escala de Calificación" de un subcriterio
    Entonces el Sistema muestra la ventana emergente con la escala de calificación del subcriterio

    Ejemplos:
      | rol         |
      | Jefe DGI    |
      | Subjefe DGI |

  Esquema del escenario: Jefe DGI y Subjefe DGI no pueden modificar la priorización
    Dado que el usuario autenticado tiene el rol "<rol>"
    Cuando accede a la priorización de un proyecto
    Entonces el control "<control>" no está disponible para modificación

    Ejemplos:
      | rol         | control                                   |
      | Jefe DGI    | Calificación                              |
      | Jefe DGI    | Guardar                                   |
      | Jefe DGI    | Calificar la priorización                 |
      | Jefe DGI    | Priorización revisada Coordinador PRE/SYMP |
      | Jefe DGI    | Habilitar Calificación de Prioridad       |
      | Subjefe DGI | Calificación                              |
      | Subjefe DGI | Guardar                                   |
      | Subjefe DGI | Calificar la priorización                 |
      | Subjefe DGI | Priorización revisada Coordinador PRE/SYMP |
      | Subjefe DGI | Habilitar Calificación de Prioridad       |

  # Sin escenario: ruta de acceso de estos roles a la priorización — no especificada en el CU.
