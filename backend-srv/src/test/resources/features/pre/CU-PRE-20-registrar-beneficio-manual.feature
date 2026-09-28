# language: es
@CU-PRE-20 @rol:TECNICO_URP
Característica: Registrar un beneficio en modo Manual

  Como Técnico URP
  Quiero registrar un beneficio ingresando manualmente el monto de cada período

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Evaluación", sección "Beneficios del Proyecto" - flujo-beneficios
    Y hace clic en el botón "Agregar Beneficio" - flujo-beneficios-agregar
    Y el sistema muestra la pantalla del Anexo A.2 - flujo-beneficios

  Escenario: Registrar y guardar un beneficio en modo Manual (camino feliz)
    Cuando el Técnico URP selecciona el "Tipo de Beneficio"
    Y registra el nombre del "Beneficio"
    Y selecciona el "Parámetro" (Factor de corrección) del catálogo Parámetros
    Y selecciona la opción "Manual"
    Entonces el sistema deshabilita los campos "Monto período 1" y "Tasa de crecimiento proyectado"
    Y muestra el valor numérico del "FC" asociado al Parámetro seleccionado
    Y el sistema muestra en la tabla "Montos por período" una cantidad de filas igual a la "Vida útil (períodos a proyectar)" registrada en CU-PRE-18
    Cuando el Técnico URP registra el monto de cada período en la columna "Monto Precios de Mercado"
    Y el Técnico URP hace clic en el botón "Guardar" - flujo-beneficios
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.3) - flujo-beneficios
    Cuando el Técnico URP hace clic en "Aceptar" - flujo-beneficios
    Entonces el sistema traslada el "Beneficio" y los montos por período a la tabla "Beneficios del proyecto"
    Y los ubica en la sección correspondiente al Tipo de Beneficio seleccionado (RN08)

  Esquema del escenario: Cálculo del Monto Precios Ajustados (según el ejemplo documentado)
    Dado que el Parámetro seleccionado tiene un FC de 1.10
    Y el Técnico URP registró "<monto_mercado>" en "Monto Precios de Mercado" de un período
    Entonces el sistema calcula "<monto_ajustado>" en "Monto Precios Ajustados" para ese período

    Ejemplos:
      | monto_mercado | monto_ajustado |
      | $800.00       | $880.00         |
      | $840.00       | $924.00         |
      | $882.00       | $970.20         |

  Escenario: Salir sin guardar un beneficio en modo Manual
    Dado que el Técnico URP ha registrado información en la pantalla "Detalle del Beneficio" - flujo-beneficios
    Cuando hace clic en el botón "Salir" - flujo-beneficios-salir
    Entonces el sistema regresa a la pestaña "Beneficios del proyecto" sin guardar la información registrada (FA1.2)

  Escenario: Intentar guardar sin seleccionar el Parámetro
    Cuando el Técnico URP hace clic en "Guardar" sin haber seleccionado el "Parámetro"
    Entonces el sistema no permite guardar, ya que el campo es obligatorio - flujo-beneficios

  Escenario: Intentar guardar sin registrar el Monto Precios de Mercado
    Cuando el Técnico URP hace clic en "Guardar" sin haber registrado el "Monto Precios de Mercado" de al menos un período
    Entonces el sistema no permite guardar, ya que el campo es obligatorio - flujo-beneficios

  # ⚠️ Escenario pendiente: la fórmula general del campo "Monto Precios Ajustados" (paso 1.4) es ilegible en el documento fuente; el Scenario Outline anterior solo reproduce el ejemplo numérico concreto documentado (FC=1.10), sin generalizar una fórmula no confirmada por el negocio para otros valores de FC.
