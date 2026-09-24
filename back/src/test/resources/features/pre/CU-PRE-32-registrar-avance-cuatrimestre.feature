# language: es
@CU-PRE-32 @rol:TECNICO_URP
Característica: Registrar el avance del cuatrimestre

  Como Técnico URP
  Quiero registrar el avance del cuatrimestre por etapa y fuente de financiamiento

  Antecedentes:
    Dado que el Técnico URP hace clic en el código de un proyecto
    Y el sistema muestra la pantalla del Anexo A.5

  Escenario: Registrar y guardar el avance del cuatrimestre (camino feliz, SF-1)
    Cuando el Técnico URP registra, para cada etapa y fuente de financiamiento, el "Avance del Cuatrimestre" y las "Observaciones del Cuatrimestre"
    Y hace clic en el botón "Guardar" avance-financiero
    Entonces el sistema valida los datos según RN-D.a
    Y coloca el monto registrado en la columna "Ejecutado" del Avance del Cuatrimestre del Anexo A.1
    Y coloca la información de "Observaciones del Cuatrimestre" en el campo "Observaciones" del Anexo A.1

  Escenario: Salir sin guardar
    Cuando el Técnico URP hace clic en el botón "Salir" avance-financiero
    Entonces el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios avance-financiero

  Esquema del escenario: El avance del cuatrimestre no puede superar el monto pendiente
    Dado que el Técnico URP está registrando el avance del "<cuatrimestre>"
    Cuando el monto registrado supera "<limite>"
    Entonces el sistema muestra el mensaje "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado" (Anexo A.2, RN-D.a)

    Ejemplos:
      | cuatrimestre    | limite                                                          |
      | Cuatrimestre I   | el Total Año registrado en CU-PRE-30                            |
      | Cuatrimestre II  | Total anual menos el Avance del Cuatrimestre I                  |
      | Cuatrimestre III | Total anual menos los Avances de los Cuatrimestres I y II        |

  Escenario: Alerta cuando el acumulado ejecutado supera lo programado
    Dado que el monto ejecutado acumulado supera el "Costo de la Etapa" programado en CU-PRE-30
    Entonces el sistema genera una alerta para revisión y ajuste del PAP vigente (Anexo A.2, RN-B.b)

  # RN-B.b: el "Costo de la Etapa" es único por etapa; el acumulado se suma sobre todas sus fuentes.
  Escenario: La alerta suma lo ejecutado por todas las fuentes de financiamiento de la etapa
    Dado que otra fuente de financiamiento de la misma etapa ya ejecutó en años anteriores parte del "Costo de la Etapa"
    Cuando el Técnico URP registra un avance que por sí solo no supera el "Costo de la Etapa" pero sumado a la otra fuente sí
    Entonces el sistema genera la alerta para revisión y ajuste del PAP vigente en todas las fuentes de la etapa (Anexo A.2, RN-B.b)

  Esquema del escenario: Visualización progresiva de avances de cuatrimestres anteriores
    Dado que el Técnico URP está registrando el avance del "<cuatrimestre_actual>"
    Entonces la sección "Avances reportados en cuatrimestres anteriores" muestra "<contenido>" (RN-F)

    Ejemplos:
      | cuatrimestre_actual | contenido                                    |
      | Cuatrimestre I        | no se muestra esta sección                    |
      | Cuatrimestre II       | solo lo ejecutado en Cuatrimestre I           |
      | Cuatrimestre III      | lo ejecutado en Cuatrimestre I y Cuatrimestre II |