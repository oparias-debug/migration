# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar días restantes hasta el fin de un período LABORAL

  Como cualquier usuario del sistema
  Quiero consultar cuántos días faltan desde una fecha dada hasta el fin de un período LABORAL específico
  Para conocer los días restantes de dicho período

  Escenario: Consultar los días restantes cuando la fecha está dentro del período
    Dado una fecha que está dentro de un período LABORAL identificado por su código
    Cuando cualquier usuario consulta los días restantes hasta el fin de ese período desde esa fecha
    Entonces el sistema responde el número de días que faltan hasta el fin del período

  Escenario: Retornar error si la fecha dada no está dentro del período LABORAL
    Dado una fecha que no está dentro del período LABORAL identificado por su código
    Cuando cualquier usuario consulta los días restantes hasta el fin de ese período desde esa fecha
    Entonces el sistema retorna un error

  Escenario: Retornar error si el código de calendario o de período no existe
    Dado que el código de calendario o el código de período LABORAL indicado no existe
    Cuando cualquier usuario consulta los días restantes hasta el fin de ese período
    Entonces el sistema retorna un error
