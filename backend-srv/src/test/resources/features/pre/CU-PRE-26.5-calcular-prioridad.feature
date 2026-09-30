# language: es
@CU-PRE-26.5 @rol:SISTEMA
Característica: Calcular los puntajes y la Prioridad del proyecto

  Como Sistema
  Quiero calcular los puntajes de priorización y la Prioridad del proyecto a partir de las calificaciones

  Esquema del escenario: Puntaje de un subcriterio sin calificaciones N/A
    Dado que el subcriterio "<subcriterio>" tiene "Ponderación subcriterio" <pond_sub> y su criterio tiene "Ponderación criterio" <pond_crit>
    Y ningún subcriterio del proyecto tiene la calificación "N/A"
    Cuando se asigna la calificación <calificacion> al subcriterio "<subcriterio>"
    Entonces el puntaje del subcriterio es <puntaje>

    # Fórmula RN12: Calificación × Ponderación del subcriterio × Ponderación del criterio × 20
    Ejemplos:
      | subcriterio | pond_sub | pond_crit | calificacion | puntaje |
      | 1.1         | 25%      | 20%       | 5            | 5.00    |
      | 1.2         | 40%      | 20%       | 2            | 3.20    |
      | 4.1         | 45%      | 25%       | 3            | 6.75    |
      | 5.3         | 20%      | 25%       | 0            | 0.00    |

  Esquema del escenario: Prioridad del proyecto como suma de los puntajes de todos los subcriterios
    Dado que todos los subcriterios de los criterios 1, 2, 3, 4 y 5 tienen la calificación <calificacion>
    Cuando el Sistema calcula la priorización
    Entonces la "Prioridad del proyecto" es la suma de los puntajes de todos los subcriterios
    Y la "Prioridad del proyecto" es <prioridad>

    Ejemplos:
      | calificacion | prioridad |
      | 5            | 100.00    |
      | 0            | 0.00      |

  Escenario: Redistribución de ponderaciones cuando un subcriterio tiene N/A
    Dado que el subcriterio "1.3" tiene la calificación "N/A"
    Y los subcriterios "1.1", "1.2" y "1.4" tienen una calificación numérica
    Cuando el Sistema calcula la priorización
    Entonces la ponderación del subcriterio "1.3" no se considera en el cálculo
    Y las ponderaciones de los subcriterios "1.1", "1.2" y "1.4" se redistribuyen automáticamente hasta totalizar 100% en el criterio 1

  Escenario: Redistribución equitativa cuando todos los subcriterios de un criterio tienen N/A
    Dado que los subcriterios "3.1", "3.2" y "3.3" del criterio 3, con "Ponderación criterio" 15%, tienen la calificación "N/A"
    Y los criterios 1, 2, 4 y 5 tienen al menos un subcriterio con calificación numérica
    Cuando el Sistema calcula la priorización
    Entonces el 15% del criterio 3 se distribuye uniformemente entre los criterios 1, 2, 4 y 5
    Y cada uno de esos criterios recibe 3.75% adicional

  Esquema del escenario: Categoría de priorización según el puntaje obtenido
    Dado que se completó la calificación de los criterios 1, 2, 3, 4 y 5 del proyecto
    Y la "Prioridad del proyecto" es <puntaje>
    Cuando se muestra la pantalla del Anexo A.2 "Prioridad del proyecto"
    Entonces el campo "Categoría de priorización" muestra "<categoria>"

    Ejemplos:
      | puntaje | categoria                      |
      | 100     | Priorizado para programación   |
      | 85      | Priorizado para programación   |
      | 84      | Priorizado condicional         |
      | 70      | Priorizado condicional         |
      | 69      | Elegible para fortalecimiento  |
      | 50      | Elegible para fortalecimiento  |
      | 49      | No priorizable en estado actual |
      | 0       | No priorizable en estado actual |

  Escenario: Consultar los rangos de interpretación
    Dado que se muestra la pantalla del Anexo A.2 "Prioridad del proyecto"
    Cuando se da clic en el botón "Rangos de interpretación"
    Entonces el Sistema muestra una ventana emergente con los rangos de interpretación:
      | PUNTAJE | CATEGORÍA                       | IMPLICACIÓN                                                                                        |
      | 85-100  | Priorizado para programación    | Incorporación prioritaria al año n+1 del PRIPME. Elegible para PAIP con financiamiento.            |
      | 70-84   | Priorizado condicional          | Puede incorporarse al año n+1 del PRIPME sujeto a disponibilidad fiscal. Orientar ajustes con PAP. |
      | 50-69   | Elegible para fortalecimiento   | No debe avanzar al PAIP en estado actual. Maduración mediante PAP.                                 |
      | 0-49    | No priorizable en estado actual | Se recomienda reformulación o postergación.                                                        |

  # Sin escenario: método de redistribución de RN13 (proporcional vs. equitativo) y puntaje por criterio en A.2 (RN12 vs. Anexo B.1). Ver historias-CU-PRE-26.5.md.
  # Sin escenario: varios criterios con N/A total (RN14 "mismo proceso"), puntajes decimales entre rangos e "Implicación para la programación" en A.2 (mockup vs. ventana). Ver historias-CU-PRE-26.5.md.
