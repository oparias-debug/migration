# language: es
@CU-PRE-33 @rol:SISTEMA
Característica: Calcular el Estado del estudio y los porcentajes de avance de metas

  Como Sistema
  Quiero calcular automáticamente el Estado de cada estudio y los porcentajes acumulados de avance de metas físicas

  Esquema del escenario: Cálculo del Estado del estudio según su ejecución al cuatrimestre
    Dado un estudio con una ejecución "<condicion>" respecto a lo programado al cuatrimestre
    Entonces el sistema le asigna el estado "<estado>" (RN-B.c)

    Ejemplos:
      | condicion                                                              | estado     |
      | igual al porcentaje programado al cuatrimestre                          | A tiempo   |
      | menor al porcentaje programado al cuatrimestre                          | Atrasado   |
      | mayor al programado al cuatrimestre, sin superar el Programado en el Año | Adelantado |
      | el estudio ha concluido conforme a la ejecución de metas físicas         | Finalizado |
      # Fuera del catálogo de RN-B.c: solo alcanzable por la excepción de RN-B.b o por una reprogramación
      # posterior en CU-PRE-31 (RN-C.b lo bloquea en el registro). El documento no define un estado para
      # este caso, por lo que no se asigna ninguno (no se inventa un quinto estado).
      | mayor al Programado en el Año                                            | Sin estado |

  Escenario: Cálculo de los acumulados anuales y al cuatrimestre
    Entonces el sistema calcula el "Avance Anual Programado/Ejecutado" y el "Avance Programado/Ejecutado al Cuatrimestre" para cada cuatrimestre, según las fórmulas acumulativas de RN-F

  Escenario: Cálculo del Total Meta Ejecutada
    Entonces el sistema calcula "Total Meta Ejecutada" como Ejecutado años anteriores más Avance Anual Ejecutado en el año (RN-F)