# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar días restantes hasta el fin de un período LABORAL

  Como cualquier usuario del sistema
  Quiero consultar cuántos días faltan desde una fecha dada hasta el fin de un período LABORAL específico

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026" con un período LABORAL "LAB-01" que finaliza el "2026-06-30"

  Esquema del escenario: Calcular días restantes usando la convención de conteo de la sección 14
    Cuando cualquier usuario consulta los días restantes desde la fecha "<fecha>" hasta el fin del período "LAB-01" del calendario "CAL-2026"
    Entonces el sistema devuelve "<dias>" días restantes

    Ejemplos:
      | fecha      | dias |
      | 2026-06-30 | 0    |
      | 2026-06-29 | 1    |
      | 2026-06-28 | 2    |

  Escenario: Retornar error si el período no existe
    Cuando cualquier usuario consulta los días restantes hasta el fin del período "NOEXISTE" del calendario "CAL-2026"
    Entonces el sistema retorna error indicando que el período no existe

  Escenario: Retornar error si la fecha dada no está dentro del período
    Cuando cualquier usuario consulta los días restantes desde la fecha "2027-01-01" hasta el fin del período "LAB-01" del calendario "CAL-2026"
    Entonces el sistema retorna error indicando que la fecha no está dentro del período

  Escenario: Retornar error si el código de calendario no existe
    Cuando cualquier usuario consulta los días restantes de un período del calendario "CAL-INEXISTENTE"
    Entonces el sistema retorna error indicando que el calendario no existe
