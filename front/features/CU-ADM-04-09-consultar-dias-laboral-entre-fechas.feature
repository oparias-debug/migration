# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar días LABORAL entre dos fechas

  Como cualquier usuario del sistema
  Quiero consultar cuántos días LABORAL existen entre dos fechas dadas

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026", fecha de inicio "2026-01-01" y fecha de fin "2026-12-31"

  Escenario: Calcular los días LABORAL entre dos fechas dentro del rango del calendario
    Dado que entre las fechas "2026-01-01" y "2026-01-10" existen varios períodos LABORAL y NO_LABORAL, incluyendo intersecciones LABORAL+NO_LABORAL
    Cuando cualquier usuario consulta los días LABORAL entre "2026-01-01" y "2026-01-10" en el calendario "CAL-2026"
    Entonces el sistema devuelve el total de días LABORAL, excluyendo los días en intersección con NO_LABORAL y restando 1 según la convención de conteo

  Escenario: Retornar error si alguna de las fechas está fuera del rango del calendario
    Cuando cualquier usuario consulta los días LABORAL entre "2025-12-01" y "2026-01-10" en el calendario "CAL-2026"
    Entonces el sistema retorna error indicando que una de las fechas está fuera del rango del calendario

  Escenario: Retornar error si las fechas dadas son inconsistentes entre sí
    Cuando cualquier usuario consulta los días LABORAL entre "2026-06-30" y "2026-01-01" en el calendario "CAL-2026"
    Entonces el sistema retorna error indicando que las fechas son inconsistentes

  Escenario: Retornar error si el código de calendario no existe
    Cuando cualquier usuario consulta los días LABORAL entre dos fechas en el calendario "CAL-INEXISTENTE"
    Entonces el sistema retorna error indicando que el calendario no existe
