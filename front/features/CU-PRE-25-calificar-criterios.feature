# language: es
@CU-PRE-25 @HU-PRE-25-01 @rol:VIABILIZADOR @rol:TECNICO_URP @rol:TECNICO_PRE @rol:TECNICO_SYMP
Característica: Calificación de los criterios de elegibilidad de un proyecto

  Como Viabilizador
  Quiero calificar los criterios establecidos para la elegibilidad del proyecto y guardar la información registrada

  Escenario: Acceso al formulario de elegibilidad desde "Captura de proyectos"
    Dado que el Viabilizador está en la pantalla "Captura de proyectos"
    Y la pantalla lista los proyectos de la entidad en el estado "Proyecto viable"
    Cuando el Viabilizador da clic en el proyecto sobre el cual va a emitir elegibilidad
    Entonces el Sistema muestra el formulario del Anexo A.1

  Esquema del escenario: Registro de la información complementaria de un criterio de la dimensión "1. Alineación estratégica"
    Dado que el Viabilizador está en el formulario del Anexo A.1 de un proyecto
    Cuando el Viabilizador selecciona en la columna "¿Aplica?" el criterio "<criterio>"
    Entonces el Viabilizador puede responder en la columna "Especificar" la pregunta "<pregunta>" mediante <tipo>

    Ejemplos:
      | criterio                                                | pregunta                                                        | tipo                                   |
      | Contribución a metas Objetivos de Desarrollo Sostenible | ¿A cuáles ODS contribuye?                                       | selección del listado del catálogo C.1 |
      | Coherencia con Plan de Gobierno                         | ¿A cuál eje o pilar del Plan de Gobierno contribuye?            | selección del listado del catálogo C.2 |
      | Contribución a Planes Regionales                        | ¿A cuál Plan Regional contribuye? Especifique                   | texto libre                            |
      | Contribución a Planes Sectoriales o Institucionales     | ¿A cuál Plan Institucional o Sectorial contribuye? Especifique  | texto libre                            |

  Esquema del escenario: Selección múltiple en los listados de "Especificar"
    Dado que el Viabilizador está en el formulario del Anexo A.1 de un proyecto
    Y seleccionó en la columna "¿Aplica?" el criterio "<criterio>"
    Cuando el Viabilizador selecciona "<elemento_1>" y "<elemento_2>" en el listado de la columna "Especificar"
    Entonces el Sistema mantiene ambos elementos seleccionados para el criterio

    Ejemplos:
      | criterio                                                | elemento_1        | elemento_2                        |
      | Contribución a metas Objetivos de Desarrollo Sostenible | Fin de la pobreza | Hambre cero                       |
      | Coherencia con Plan de Gobierno                         | Eje 1: Carreteras | Eje 5: Agua potable y saneamiento |

  Escenario: Consulta de los Objetivos de Desarrollo Sostenible
    Dado que el Viabilizador está en el formulario del Anexo A.1 de un proyecto
    Cuando el Viabilizador da clic en el botón "Consulta ODS"
    Entonces el Sistema lo lleva al link "https://www.un.org/sustainabledevelopment/es/objetivos-de-desarrollo-sostenible/"

  Escenario: Guardar la información registrada
    Dado que el Viabilizador está en el formulario del Anexo A.1 de un proyecto
    Y completó la columna "Especificar" de todos los criterios seleccionados en "¿Aplica?"
    Cuando el Viabilizador da clic en el botón "Guardar"
    Entonces el Sistema muestra el mensaje emergente "¡Guardado!" con el texto "Sus datos han sido guardados exitosamente."

  Escenario: Aceptar el mensaje de guardado
    Dado que el Sistema muestra el mensaje emergente "¡Guardado!" del Anexo A.2
    Cuando el Viabilizador da clic en "Aceptar"
    Entonces el Sistema guarda la información registrada
    Y el Sistema se mantiene en la pantalla "Elegibilidad"

  Esquema del escenario: Validación de la columna "Especificar" al guardar
    Dado que el Viabilizador está en el formulario del Anexo A.1 de un proyecto
    Y seleccionó en la columna "¿Aplica?" el criterio "<criterio>"
    Y no completó la columna "Especificar" de ese criterio
    Cuando el Viabilizador da clic en el botón "Guardar"
    Entonces el Sistema muestra el mensaje "Se debe completar la información de la columna 'Especificar' para los criterios seleccionados"

    Ejemplos:
      | criterio                                                |
      | Contribución a metas Objetivos de Desarrollo Sostenible |
      | Coherencia con Plan de Gobierno                         |
      | Contribución a Planes Regionales                        |
      | Contribución a Planes Sectoriales o Institucionales     |

  Esquema del escenario: Solo el Viabilizador registra y guarda la calificación de criterios
    Dado un proyecto con el formulario del Anexo A.1 disponible
    Cuando el <rol> accede al formulario del Anexo A.1 del proyecto
    Entonces el botón "Guardar" no es visible para el <rol>
    Y el <rol> no puede registrar información en las columnas "¿Aplica?" y "Especificar"

    Ejemplos:
      | rol          |
      | Técnico URP  |
      | Técnico PRE  |
      | Técnico SYMP |

  # ⚠️ Escenario pendiente: acceso por la Opción 2 del FB1 ("luego de emitir viabilidad el sistema lo lleva al formulario de elegibilidad"). La secuencia paso 3 / paso 4 es ambigua y difiere de CU-PRE-24 FA02 2.4 (acceso mediante "Ir a Elegibilidad"). No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: criterios de las dimensiones 2 a 6, catálogos C.3 a C.5 y catálogos de "Aspectos Sociales". El PDF de AGOSTO 2026 solo muestra "1. Alineación estratégica" y no se ha confirmado la vigencia del anexo Excel. No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: sub-formato "Especificar (Selección radial)" Sí/No. El PDF no indica a qué criterios aplica (solo el anexo Excel, pendiente de confirmar). No implementar hasta que se resuelva.
  # ⚠️ Escenario pendiente: ponderación por dimensión/criterio y "Total Calificación (Prioridad)" del anexo Excel. No se sabe si pertenece a este CU o a CU-PRE-26.5, ni cómo se calcula. No implementar hasta que se resuelva.
  # ℹ️ Sin escenario: si el Sistema impide guardar cuando falla la validación de RN03. La regla solo indica el mensaje.
  # ℹ️ Sin escenario: permiso de solo visualización de los "Actores de la DGI" (RN02). No figuran en el catálogo de actores ni hay rol de Keycloak al que mapearlos.
