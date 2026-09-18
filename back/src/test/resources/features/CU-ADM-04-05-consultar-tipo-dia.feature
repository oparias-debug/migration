# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar si una fecha es LABORAL o NO_LABORAL

  Como cualquier usuario del sistema
  Quiero consultar si una fecha dada es LABORAL o NO_LABORAL
  Para conocer el tipo de día

  Esquema del escenario: Consultar el tipo de una fecha que cae en un único tipo de período
    Dado un calendario con un período "<tipo_periodo>" que cubre la fecha consultada
    Cuando cualquier usuario consulta el tipo de esa fecha
    Entonces el sistema responde "<tipo_periodo>"

    Ejemplos:
      | tipo_periodo |
      | LABORAL      |
      | NO_LABORAL   |

  Escenario: Una fecha en la intersección de un período LABORAL y uno NO_LABORAL se considera NO_LABORAL
    Dado un calendario donde un período LABORAL y un período NO_LABORAL se intersectan en una fecha determinada
    Cuando cualquier usuario consulta el tipo de esa fecha
    Entonces el sistema responde NO_LABORAL

  Escenario: La intersección entre dos períodos LABORAL, o entre dos NO_LABORAL, no altera la clasificación
    Dado un calendario donde dos períodos LABORAL se intersectan en una fecha determinada
    Cuando cualquier usuario consulta el tipo de esa fecha
    Entonces el sistema responde LABORAL sin reportar ningún conflicto

  Escenario: Una excepción registrada sobre la fecha tiene prioridad sobre los períodos
    Dado un calendario con una excepción registrada sobre una fecha determinada
    Cuando cualquier usuario consulta el tipo de esa fecha
    Entonces el sistema responde el tipo indicado en la excepción

  Escenario: Retornar error si la fecha no cae en ningún período definido del calendario
    Dado un calendario en el que la fecha consultada no está cubierta por ningún período ni excepción
    Cuando cualquier usuario consulta el tipo de esa fecha
    Entonces el sistema retorna un error

  Escenario: Retornar error si el código de calendario no existe
    Dado que no existe ningún calendario con el código indicado
    Cuando cualquier usuario consulta el tipo de una fecha sobre ese código de calendario
    Entonces el sistema retorna un error
