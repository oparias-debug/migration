# language: es
@CU-PRE-33 @rol:TECNICO_URP
Característica: Registrar el avance del cuatrimestre por meta física

  Como Técnico URP
  Quiero registrar el avance del cuatrimestre por meta física de cada etapa

  Antecedentes:
    Dado que el Técnico URP hace clic en el código de un proyecto avance-metas
    Y el sistema muestra la pantalla del Anexo A.4

  Escenario: Registrar y guardar el avance de metas físicas (camino feliz, SF-1)
    Cuando el Técnico URP registra, para cada etapa, el "Avance del Cuatrimestre" y las "Observaciones del Cuatrimestre"
    Y hace clic en el botón "Guardar" avance-metas
    Entonces el sistema valida los datos según RN-C.b
    Y calcula el "Total meta acumulada" como Ejecutado años anteriores más Avance acumulado de cuatrimestres anteriores más Avance del cuatrimestre
    Y coloca el porcentaje registrado en "Ejecutado del Cuatrimestre" del Anexo A.1
    Y coloca el "Total meta ejecutada" calculado en el campo del mismo nombre del Anexo A.1
    Y coloca las "Observaciones del Cuatrimestre" en el campo "Observaciones del cuatrimestre" del Anexo A.1

  Escenario: Salir sin guardar
    Cuando el Técnico URP hace clic en el botón "Salir" avance-metas
    Entonces el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios avance-metas

  Escenario: El avance del cuatrimestre no puede superar el porcentaje programado anual
    Dado que el avance registrado supera el porcentaje "programado anual" del estudio
    Cuando el Técnico URP hace clic en el botón "Guardar" avance-metas-limite
    Entonces el sistema muestra el mensaje "El porcentaje total registrado supera el 100%" (RN-C.b)

  Escenario: Los estudios sin programación en el cuatrimestre siempre pueden reportar avance
    Dado un estudio con "programado del cuatrimestre" igual a 0 en el cuatrimestre que se está registrando
    Entonces el Técnico URP puede reportar el avance correspondiente desde el Anexo A.4 (RN-B.b)

  Esquema del escenario: Visualización progresiva de avances de cuatrimestres anteriores
    Dado que el Técnico URP está registrando el avance del "<cuatrimestre_actual>" avance-metas
    Entonces la sección "Avances reportados en cuatrimestres anteriores" muestra "<contenido>" (RN-G)

    Ejemplos:
      | cuatrimestre_actual | contenido                                        |
      | Cuatrimestre I         | no se muestra esta sección                        |
      | Cuatrimestre II        | solo lo ejecutado en Cuatrimestre I               |
      | Cuatrimestre III       | lo ejecutado en Cuatrimestre I y Cuatrimestre II  |

  # ⚠️ Escenario pendiente: el documento no describe, en ningún Flujo Básico o Subflujo, el paso en que el Técnico URP hace clic en el botón "Enviar a revisión DGICP" (solo se menciona como visible/habilitado en RN-A.b), a diferencia del caso análogo de CU-PRE-31 (SF-2, pasos 7-8). No se inventa ese paso ni la notificación correspondiente al Técnico PRE.