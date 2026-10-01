# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar si una fecha pertenece a un período específico

  Como cualquier usuario del sistema
  Quiero consultar si una fecha dada pertenece a un período LABORAL o NO_LABORAL identificado por su código

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026" con un período "LAB-01"

  Esquema del escenario: Responder si la fecha pertenece o no al período consultado
    Cuando cualquier usuario consulta si la fecha "<fecha>" pertenece al período "LAB-01" del calendario "CAL-2026"
    Entonces el sistema responde "<pertenece>"

    Ejemplos:
      | fecha      | pertenece |
      | 2026-03-10 | true      |
      | 2026-08-15 | false     |

  Escenario: Retornar error si el código de período no existe
    Cuando cualquier usuario consulta si una fecha pertenece al período "NOEXISTE" del calendario "CAL-2026"
    Entonces el sistema retorna error indicando que el período no existe

  Escenario: Retornar error si el código de calendario no existe
    Cuando cualquier usuario consulta la pertenencia de una fecha a un período del calendario "CAL-INEXISTENTE"
    Entonces el sistema retorna error indicando que el calendario no existe
