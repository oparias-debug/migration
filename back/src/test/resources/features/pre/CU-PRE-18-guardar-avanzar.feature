# language: es
@CU-PRE-18 @rol:TECNICO_URP @wip
Característica: Guardar el presupuesto de Operación y Mantenimiento y avanzar

  Como Técnico URP
  Quiero gestionar las filas de actividades, guardar el presupuesto y avanzar a la siguiente sección

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Presupuesto de Operación y Mantenimiento" (Anexo A.1)

  Escenario: Agregar una fila de actividad manualmente
    Cuando el Técnico URP adiciona una nueva fila en la tabla "Costos de Operación" o "Costos de Mantenimiento"
    Entonces el sistema agrega la fila correspondiente (RN03)

  Escenario: Eliminar una fila de actividad
    Dado una fila registrada en alguna de las tablas
    Cuando el Técnico URP elimina esa fila
    Entonces el sistema elimina la fila correspondiente (RN03) - presupuesto-om

  Escenario: Cálculo automático del Total a precios de mercado
    Dado que se han registrado costos por período en varias actividades
    Entonces el sistema calcula automáticamente el "Total (Precios de Mercado)" de cada período como la suma de esa columna (RN08)

  Esquema del escenario: Redondeo automático del Total Inversión
    Dado que el total calculado es "<valor_calculado>"
    Entonces el sistema redondea el total hacia arriba, a múltiplos de 5 o de 10, a "<valor_redondeado>" (RN10)

    Ejemplos:
      | valor_calculado | valor_redondeado |
      | $1,750,427.58   | $1,750,430.00     |
      | $1,750,423.58   | $1,750,425.00     |

  Escenario: Guardar el presupuesto de Operación y Mantenimiento (camino feliz)
    Cuando el Técnico URP hace clic en el botón "Guardar"
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.3)
    Cuando el Técnico URP hace clic en "Aceptar"
    Entonces el sistema guarda la información registrada - presupuesto-om
    Y se mantiene en la pantalla "Presupuesto de Operación y Mantenimiento" - presupuesto-om

  Escenario: Avanzar a Parámetros de Evaluación (camino feliz, FA-03)
    Cuando el Técnico URP hace clic en el botón "Siguiente"
    Entonces el sistema avanza a la sección "Parámetros de Evaluación"

  # ⚠️ Escenario pendiente: no se resuelve si las celdas "TOTAL INVERSIÓN (PRECIOS DE MERCADO/AJUSTADOS)" mencionadas en RN10 corresponden a las mismas celdas "TOTAL (PRECIOS DE MERCADO/AJUSTADOS)" mostradas en el mockup, o a celdas adicionales no documentadas.
