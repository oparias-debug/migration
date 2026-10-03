# language: es
@CU-PRE-15 @rol:TECNICO_URP
Característica: Avanzar a Análisis Legal con validación condicional de riesgos altos

  Como Técnico URP
  Quiero avanzar a "Análisis Legal", con la validación condicional de que los riesgos calificados como "Alto" o "Muy Alto" tengan su acción de mitigación y costo completos

  Antecedentes:
    Dado que el Técnico URP se encuentra en la pantalla "Análisis de Riesgos" Análisis-Riesgos-legal

  Escenario: Avanzar sin restricciones cuando ninguna fila tiene Calificación Alta o Muy Alta
    Dado que ninguna fila tiene "Calificación del Riesgo" igual a "Alto" o "Muy Alto" Análisis-Riesgos-legal
    Cuando el Técnico URP hace clic en el botón "Siguiente" Análisis-Riesgos-legal
    Entonces el sistema avanza a la sección "Análisis Legal" (CU-PRE-16, FA-02) Análisis-Riesgos-legal

  Esquema del escenario: Bloquear el avance si una fila de riesgo alto no tiene acción de mitigación o costo completos
    Dado una fila con "Calificación del Riesgo" igual a "<calificacion>" Análisis-Riesgos-legal
    Y el campo "<campo_faltante>" no está completo en esa fila Análisis-Riesgos-legal
    Cuando el Técnico URP hace clic en el botón "Siguiente" Análisis-Riesgos-legal
    Entonces el sistema muestra el mensaje "Se requiere completar los campos obligatorios" (RN06) Análisis-Riesgos-legal
    Y marca dicho campo Análisis-Riesgos-legal
    Y no permite avanzar a "Análisis Legal" Análisis-Riesgos-legal

    Ejemplos:
      | calificacion | campo_faltante              |
      | Alto         | Acción de mitigación         |
      | Alto         | Costo acción de mitigación   |
      | Muy Alto     | Acción de mitigación         |
      | Muy Alto     | Costo acción de mitigación   |

  Escenario: Avanzar cuando una fila de riesgo alto sí tiene acción de mitigación y costo completos
    Dado una fila con "Calificación del Riesgo" igual a "Alto" o "Muy Alto" Análisis-Riesgos-legal
    Y los campos "Acción de mitigación" y "Costo acción de mitigación" están completos en esa fila Análisis-Riesgos-legal
    Cuando el Técnico URP hace clic en el botón "Siguiente" Análisis-Riesgos-legal
    Entonces el sistema avanza a la sección "Análisis Legal" (CU-PRE-16) Análisis-Riesgos-legal