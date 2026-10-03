# language: es
@CU-PRE-20 @rol:TECNICO_URP
Característica: Gestionar filas, guardar y avanzar desde Beneficios del Proyecto

  Como Técnico URP
  Quiero gestionar las filas de beneficios, guardar la pantalla y avanzar a Flujo de Caja e Indicadores

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Beneficios del Proyecto" (Anexo A.1) - flujo-beneficios

  Escenario: Agregar una fila de beneficio manualmente
    Cuando el Técnico URP adiciona una nueva fila
    Entonces el sistema agrega la fila correspondiente (RN03) - flujo-beneficios

  Escenario: Eliminar una fila de beneficio
    Dado una fila registrada en la tabla - flujo-beneficios
    Cuando el Técnico URP elimina esa fila - flujo-beneficios
    Entonces el sistema elimina la fila correspondiente (RN03) - flujo-beneficios

  Esquema del escenario: Redondeo automático de los montos por período
    Dado que el monto calculado es "<valor_calculado>"
    Entonces el sistema redondea hacia arriba, a múltiplos de 5 o de 10, a "<valor_redondeado>" (RN04)

    Ejemplos:
      | valor_calculado | valor_redondeado |
      | $1,750,427.58   | $1,750,430.00     |
      | $1,750,423.58   | $1,750,425.00     |

  Escenario: Cada beneficio se ubica en la sección de su Tipo de Beneficio
    Entonces cada beneficio registrado aparece en la sección "Directos", "Indirectos" o "Externalidades" según su "Tipo de Beneficio" (RN08)

  Escenario: Guardar la pantalla de Beneficios del Proyecto (camino feliz)
    Cuando el Técnico URP hace clic en el botón "Guardar" - flujo-beneficios
    Entonces el sistema muestra el mensaje "¡Guardado! Sus datos han sido guardados exitosamente." (Anexo A.3)
    Cuando el Técnico URP hace clic en "Aceptar" - flujo-beneficios
    Entonces el sistema guarda la información registrada - flujo-beneficios
    Y se mantiene en la pantalla "Beneficios del proyecto" - flujo-beneficios

  Escenario: Avanzar a Flujo de Caja e Indicadores (camino feliz, FA04)
    Cuando el Técnico URP hace clic en el botón "Siguiente" - flujo-beneficios
    Entonces el sistema avanza a la pestaña "Flujo de Caja e Indicadores" (CU-PRE-21)
