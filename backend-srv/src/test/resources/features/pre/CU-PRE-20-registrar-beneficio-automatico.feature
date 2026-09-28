# language: es
@CU-PRE-20 @rol:TECNICO_URP
Característica: Registrar un beneficio en modo Automático

  Como Técnico URP
  Quiero registrar un beneficio calculando automáticamente su proyección a partir de un monto inicial y una tasa de crecimiento

  Antecedentes:
    Dado que el Técnico URP ingresa a la pestaña "Evaluación", sección "Beneficios del Proyecto" - flujo-beneficios
    Y hace clic en el botón "Agregar Beneficio" - flujo-beneficios-agregar
    Y el sistema muestra la pantalla del Anexo A.2 - flujo-beneficios

  Escenario: Registrar y guardar un beneficio en modo Automático (camino feliz)
    Cuando el Técnico URP selecciona el "Tipo de Beneficio"
    Y registra el nombre del "Beneficio"
    Y selecciona el "Parámetro" (Factor de corrección) del catálogo Parámetros
    Y selecciona la opción "Automático"
    Y registra el "Monto período 1" y la "Tasa de crecimiento proyectado"
    Entonces el sistema muestra en la tabla "Montos por período" una cantidad de filas igual a la "Vida útil (períodos a proyectar)" registrada en CU-PRE-18
    Y calcula automáticamente el "Monto Precios de Mercado" proyectado de cada período
    Y calcula automáticamente el "Monto Precios Ajustados" de cada período
    Cuando el Técnico URP hace clic en el botón "Guardar" - flujo-beneficios
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.3) - flujo-beneficios
    Cuando el Técnico URP hace clic en "Aceptar" - flujo-beneficios
    Entonces el sistema traslada el "Beneficio" y los montos por período a la tabla "Beneficios del proyecto"
    Y los ubica en la sección correspondiente al Tipo de Beneficio seleccionado (RN08)

  Esquema del escenario: Proyección del Monto Precios de Mercado (según el ejemplo documentado)
    Dado que "Monto período 1" es $800.00 y la "Tasa de crecimiento proyectado" es 5%
    Entonces el sistema calcula "<monto_proyectado>" para el "<periodo>"

    Ejemplos:
      | periodo   | monto_proyectado |
      | Período 1 | $800.00            |
      | Período 2 | $840.00            |
      | Período 3 | $882.00            |

  Esquema del escenario: Cálculo del Monto Precios Ajustados (según el ejemplo documentado)
    Dado que el Parámetro seleccionado tiene un FC de 1.10
    Y el "Monto Precios de Mercado" proyectado de un período es "<monto_mercado>"
    Entonces el sistema calcula "<monto_ajustado>" en "Monto Precios Ajustados" para ese período

    Ejemplos:
      | monto_mercado | monto_ajustado |
      | $800.00       | $880.00         |
      | $840.00       | $924.00         |
      | $882.00       | $970.20         |

  Escenario: Salir sin guardar un beneficio en modo Automático
    Dado que el Técnico URP ha registrado información en la pantalla "Detalle del Beneficio" - flujo-beneficios
    Cuando hace clic en el botón "Salir" - flujo-beneficios-salir
    Entonces el sistema regresa a la pestaña "Beneficios del proyecto" sin guardar la información registrada (FA2.2)

  Escenario: Intentar guardar sin seleccionar el Tipo de ingreso
    Cuando el Técnico URP hace clic en "Guardar" sin haber seleccionado el "Tipo de ingreso"
    Entonces el sistema no permite guardar, ya que el campo es obligatorio - flujo-beneficios

  # ⚠️ Escenario pendiente: las fórmulas generales de los pasos 2.2 (proyección) y 2.3 (ajuste) son ilegibles en el documento fuente; los Scenario Outline anteriores solo reproducen el ejemplo numérico concreto documentado (crecimiento del 5%, FC=1.10), sin generalizar una fórmula no confirmada por el negocio para otros valores.
