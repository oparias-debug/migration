# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Calcular la fecha LABORAL resultante al sumar días hábiles

  Como cualquier usuario del sistema
  Quiero indicar una fecha inicial y un número de días a adicionar, y obtener la fecha LABORAL resultante

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026", fecha de inicio "2026-01-01" y fecha de fin "2026-12-31"

  Escenario: Calcular la fecha resultante ignorando los días NO_LABORAL
    Dado que entre la fecha "2026-01-01" y la fecha resultante existen días definidos como NO_LABORAL
    Cuando cualquier usuario suma "5" días hábiles a la fecha "2026-01-01" en el calendario "CAL-2026"
    Entonces el sistema devuelve la fecha LABORAL resultante, sin contar los días NO_LABORAL

  Escenario: Retornar error si la fecha resultante no cae en un período LABORAL
    Dado que la fecha resultante de la suma cae en un período NO_LABORAL
    Cuando cualquier usuario suma días hábiles a una fecha en el calendario "CAL-2026"
    Entonces el sistema retorna error indicando que la fecha resultante no cae en un período LABORAL

  Escenario: Retornar error si la fecha resultante no cae en ningún período definido
    Dado que la fecha resultante de la suma no cae en ningún período definido del calendario
    Cuando cualquier usuario suma días hábiles a una fecha en el calendario "CAL-2026"
    Entonces el sistema retorna error indicando que la fecha resultante no está en ningún período definido

  Escenario: Retornar error si el código de calendario no existe
    Cuando cualquier usuario suma días hábiles a una fecha en el calendario "CAL-INEXISTENTE"
    Entonces el sistema retorna error indicando que el calendario no existe
