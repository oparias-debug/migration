# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar días LABORAL entre dos fechas

  Como cualquier usuario del sistema
  Quiero consultar cuántos días LABORAL existen entre dos fechas dadas, sin importar que existan múltiples períodos entre ellas
  Para conocer la cantidad de días hábiles en un rango

  Escenario: Consultar los días LABORAL entre dos fechas válidas del rango del calendario
    Dado un calendario con múltiples períodos LABORAL y NO_LABORAL entre dos fechas dadas
    Y ambas fechas están dentro del rango del calendario y son consistentes entre sí
    Cuando cualquier usuario consulta los días LABORAL entre esas dos fechas
    Entonces el sistema responde el número total de días LABORAL considerando todos los períodos del rango

  Escenario: Excluir del conteo los días de intersección entre un período LABORAL y uno NO_LABORAL
    Dado un rango de fechas donde un período LABORAL se intersecta con un período NO_LABORAL
    Cuando cualquier usuario consulta los días LABORAL en ese rango
    Entonces los días de la intersección no se cuentan como LABORAL

  Escenario: Retornar error si alguna de las fechas está fuera del rango del calendario
    Dado que la fecha inicial o la fecha final indicadas no están dentro del rango de fechas del calendario
    Cuando cualquier usuario consulta los días LABORAL entre esas fechas
    Entonces el sistema retorna un error

  Escenario: Retornar error si las fechas dadas son inconsistentes entre sí
    Dado que la fecha inicial indicada es posterior a la fecha final indicada
    Cuando cualquier usuario consulta los días LABORAL entre esas fechas
    Entonces el sistema retorna un error

  Escenario: Retornar error si el código de calendario no existe
    Dado que no existe ningún calendario con el código indicado
    Cuando cualquier usuario consulta los días LABORAL entre dos fechas sobre ese código de calendario
    Entonces el sistema retorna un error
