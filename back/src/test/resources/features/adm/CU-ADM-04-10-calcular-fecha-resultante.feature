# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Calcular la fecha LABORAL resultante al sumar días hábiles

  Como cualquier usuario del sistema
  Quiero indicar una fecha inicial y un número de días a adicionar, y obtener la fecha LABORAL resultante contando solo días hábiles
  Para proyectar una fecha ignorando los días no hábiles

  Escenario: Calcular la fecha resultante ignorando los días de períodos NO_LABORAL
    Dado una fecha inicial y un número de días hábiles a adicionar
    Y los días que caen en períodos NO_LABORAL dentro de ese rango no deben contarse
    Cuando cualquier usuario solicita la fecha resultante de sumar esos días hábiles
    Entonces el sistema responde la fecha LABORAL resultante, sin contar los días NO_LABORAL

  Escenario: Retornar error si la fecha resultante no cae en un período LABORAL
    Dado que la fecha resultante del cálculo no cae en ningún período LABORAL del calendario
    Cuando cualquier usuario solicita la fecha resultante de sumar días hábiles
    Entonces el sistema retorna un error

  Escenario: Retornar error si la fecha resultante no cae en ningún período definido del calendario
    Dado que la fecha resultante del cálculo no cae en ningún período definido del calendario
    Cuando cualquier usuario solicita la fecha resultante de sumar días hábiles
    Entonces el sistema retorna un error

  Escenario: Retornar error si el código de calendario no existe
    Dado que no existe ningún calendario con el código indicado
    Cuando cualquier usuario solicita la fecha resultante de sumar días hábiles sobre ese código de calendario
    Entonces el sistema retorna un error
