# language: es
@CU-PRE-07 @rol:TECNICO_URP
Característica: Registrar y guardar el análisis de la población

  Como Técnico URP
  Quiero registrar y guardar el análisis de la población de referencia, afectada, objetivo y en espera del proyecto

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Formulación del Proyecto", sección "Diagnóstico de la Situación Actual – Análisis de la población"
    Y se encuentra en la tabla "Análisis de la Población" (Anexo A.1)

  Escenario: Registrar y guardar el análisis de la población (camino feliz)
    Cuando el Técnico URP registra la "Ubicación" y el "N° de personas" para las filas de Población de Referencia, Población Afectada y Población Objetivo
    Y registra la "Descripción" para las filas de Población Afectada y Población Objetivo
    Y hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.2)
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema guarda la información registrada
    Y se mantiene en la pestaña "Análisis de la Población"

  Escenario: Cálculo automático de la Población en Espera, sus porcentajes y sus totales
    Dado que se han registrado los valores de "N° de personas" para Población de Referencia, Afectada y Objetivo en una ubicación
    Entonces el sistema calcula automáticamente "N° de personas (Población en Espera)" como Población Afectada menos Población Objetivo, para esa ubicación
    Y calcula el "% (Población Objetivo)" como (Población Objetivo / Población Afectada) * 100
    Y calcula el "% (Población en Espera)" como "% (Población Afectada)" menos "% (Población Objetivo)"
    Y calcula el "Total N° de personas" de cada tipo de población como la suma de todas sus ubicaciones

  Escenario: Agregar una nueva ubicación a la tabla
    Cuando el Técnico URP hace clic en el botón "Adicionar Ubicación (+)"
    Entonces el sistema agrega una nueva columna agrupada de "Ubicación" y "N° de Personas" (RN09)

  Escenario: La celda de porcentaje de Población de Referencia permanece bloqueada y vacía
    Entonces la celda de la columna "%" en la fila "Población de Referencia" permanece bloqueada y vacía (RN04)

  Escenario: La celda de ubicación de Población en Espera permanece bloqueada y vacía
    Entonces la celda de la columna "Ubicación" en la fila "Población en Espera" permanece bloqueada y vacía, mostrada sombreada en gris (RN05)

  Escenario: La columna Descripción está bloqueada para Población de Referencia y Población en Espera
    Entonces la celda de la columna "Descripción" permanece bloqueada y vacía para las filas "Población de Referencia" y "Población en Espera" (RN06)

  Escenario: Ingreso inválido — Población Afectada mayor que Población de Referencia
    Dado que el "N° de personas (Población Afectada)" registrado es mayor al "N° de personas (Población de Referencia)" en la misma ubicación
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el modal de "Ingreso inválido" descrito en el Anexo A.3
    Y no guarda la información registrada
    Y regresa a la pestaña anterior

  Escenario: Ingreso inválido — Población Objetivo mayor que Población Afectada
    Dado que el "N° de personas (Población Objetivo)" registrado es mayor al "N° de personas (Población Afectada)" en la misma ubicación
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el modal de "Ingreso inválido" descrito en el Anexo A.4
    Y no guarda la información registrada
    Y regresa a la pestaña anterior

  Esquema del escenario: Intentar guardar con un campo obligatorio incompleto
    Cuando el Técnico URP hace clic en "Guardar" sin haber completado el campo "<campo>"
    Entonces el sistema sombrea en rojo el borde del campo "<campo>" (RN07)

    Ejemplos:
      | campo                              |
      | Ubicación (Población Afectada)     |
      | Ubicación (Población Objetivo)     |
      | N° de personas (Población de Referencia) |
      | N° de personas (Población Afectada)      |
      | N° de personas (Población Objetivo)      |

  # ⚠️ Escenario pendiente: el texto del mensaje de "Ingreso inválido 1" (Población Afectada > Referencia) difiere entre la tabla de "Validaciones" y el campo "Mensaje mostrado" del Anexo A.3; no se transcribe un texto literal definitivo en el escenario anterior. Lo mismo aplica al mensaje de "Ingreso inválido 2" (Anexo A.4). No se resuelven estas contradicciones aquí (ver Datos Pendientes de Definir del CU original).
  # ⚠️ Escenario pendiente: el ícono de ayuda contextual "?" (RN08) no tiene un texto de mensaje transcrito en el documento. No se genera un escenario que asegure contenido de backend no verificable.
  # ⚠️ Escenario pendiente: no se especifica un límite en la cantidad de columnas de "Ubicación"/"N° de Personas" que pueden agregarse (RN09).