# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar si una fecha pertenece a un período específico

  Como cualquier usuario del sistema
  Quiero consultar si una fecha dada pertenece a un período LABORAL o NO_LABORAL identificado por su código
  Para validar la pertenencia de la fecha a ese período concreto

  Esquema del escenario: Consultar la pertenencia de una fecha a un período
    Dado un período "<tipo_periodo>" definido en un calendario
    Cuando cualquier usuario consulta si una fecha "<resultado_esperado>" a ese período
    Entonces el sistema responde "<resultado_esperado>"

    Ejemplos:
      | tipo_periodo | resultado_esperado |
      | LABORAL      | pertenece           |
      | LABORAL      | no pertenece        |
      | NO_LABORAL   | pertenece           |
      | NO_LABORAL   | no pertenece        |

  Escenario: Retornar error si el código de calendario no existe
    Dado que no existe ningún calendario con el código indicado
    Cuando cualquier usuario consulta la pertenencia de una fecha a un período sobre ese código de calendario
    Entonces el sistema retorna un error

  Escenario: Retornar error si el código de período no existe dentro del calendario
    Dado un calendario existente sin ningún período registrado con el código indicado
    Cuando cualquier usuario consulta la pertenencia de una fecha a ese código de período
    Entonces el sistema retorna un error
