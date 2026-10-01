# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar la duración de un período

  Como cualquier usuario del sistema
  Quiero consultar la duración en días de un período LABORAL o NO_LABORAL identificado por su código

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026"

  Escenario: Calcular la duración de un período LABORAL excluyendo la intersección con NO_LABORAL
    Dado que el período LABORAL "LAB-01" del calendario "CAL-2026" se intersecta parcialmente con el período NO_LABORAL "NOLAB-01"
    Cuando cualquier usuario consulta la duración del período "LAB-01" del calendario "CAL-2026"
    Entonces el sistema devuelve la duración en días excluyendo los días de intersección con "NOLAB-01"

  Escenario: Calcular la duración de un período NO_LABORAL incluyendo todos sus días
    Dado que el período NO_LABORAL "NOLAB-01" del calendario "CAL-2026" se intersecta parcialmente con el período LABORAL "LAB-01"
    Cuando cualquier usuario consulta la duración del período "NOLAB-01" del calendario "CAL-2026"
    Entonces el sistema devuelve la duración en días incluyendo todos los días del período, sin excluir ninguno

  Esquema del escenario: Calcular la duración según el tipo de recurrencia
    Dado que el período "<codigo>" del calendario "CAL-2026" tiene recurrencia "<recurrencia>"
    Cuando cualquier usuario consulta la duración del período "<codigo>" del calendario "CAL-2026"
    Entonces el sistema calcula la duración "<criterio>"

    Ejemplos:
      | codigo | recurrencia                                        | criterio                                                                                          |
      | LAB-02 | SEMANAL (2026-01-01 a 2026-12-31, lunes a viernes) | considerando la duración tradicional de los meses involucrados (RN09)                            |
      | LAB-03 | MENSUAL (días 1 a 5, meses enero a marzo)          | como el agregado de los días declarados en cada mes, sin extrapolar a meses no declarados (RN11) |

  Escenario: Retornar error si el período no existe
    Cuando cualquier usuario consulta la duración del período "NOEXISTE" del calendario "CAL-2026"
    Entonces el sistema retorna error indicando que el período no existe

  Escenario: Retornar error si el código de calendario no existe
    Cuando cualquier usuario consulta la duración de un período del calendario "CAL-INEXISTENTE"
    Entonces el sistema retorna error indicando que el calendario no existe
