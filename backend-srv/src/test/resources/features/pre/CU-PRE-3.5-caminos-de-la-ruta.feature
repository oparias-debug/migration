# language: es
@CU-PRE-3.5 @rol:TECNICO_URP
Característica: Los caminos de la Ruta de Preinversión

  Como Técnico URP
  Quiero que el sistema me lleve por el camino que corresponde a mi iniciativa
  Para no tener que saber de antemano si mi proyecto lleva ruta o no

  Antecedentes:
    Dado que el Técnico URP entra al proceso "Creación ruta de preinversión"

  Escenario: El proceso abre con la lista de proyectos con CUP
    Entonces el sistema muestra el listado de proyectos con CUP
    Cuando el Técnico URP elige un proyecto
    Entonces el sistema abre la pantalla "Ruta de Preinversión" de ese proyecto
    Y lo primero que se ve es la matriz de criterios

  Escenario: Camino de un proyecto — genera su ruta y elige etapas
    Dado un proyecto con iniciativa "Proyecto", que no es de emergencia
    Cuando el Técnico URP califica los tres criterios y genera la Ruta de Preinversión
    Entonces el sistema sugiere las etapas del Anexo B.2
    Y el Técnico URP puede aceptarlas o modificarlas

  Escenario: Camino de un proyecto de emergencia — sin ruta
    Dado un proyecto marcado como de emergencia
    Cuando el Técnico URP abre su "Ruta de Preinversión"
    Entonces el sistema advierte que un proyecto de emergencia no lleva ruta
    Y no pide calificar criterios
    Y ofrece el botón "Ir a la Ficha de emergencia"

  Esquema del escenario: Camino de un programa y de unos estudios generales — sin ruta (RN07/RN08)
    Dado un proyecto con iniciativa "<iniciativa>"
    Cuando el Técnico URP abre su "Ruta de Preinversión"
    Entonces el sistema advierte que sus etapas ya están definidas: Perfil y Ejecución
    Y no pide calificar criterios
    Y sólo la etapa "Perfil" queda habilitada para la formulación

    Ejemplos:
      | iniciativa         |
      | Programa           |
      | Estudios Generales |

  Escenario: La ruta se puede modificar en cualquier momento
    Dado un proyecto cuya ruta ya fue aceptada
    Cuando el Técnico URP abre su "Ruta de Preinversión"
    Entonces el sistema muestra las etapas vigentes
    Y ofrece el botón "Modificar" para cambiarlas, indicando la justificación
