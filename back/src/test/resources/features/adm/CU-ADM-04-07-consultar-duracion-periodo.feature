# language: es
@CU-ADM-04 @rol:CUALQUIER_USUARIO
Característica: Consultar la duración de un período

  Como cualquier usuario del sistema
  Quiero consultar la duración en días de un período LABORAL o NO_LABORAL identificado por su código
  Para conocer cuántos días abarca dicho período

  Escenario: Consultar la duración de un período con recurrencia UNA_VEZ o SEMANAL
    Dado un período con recurrencia UNA_VEZ o SEMANAL definido sobre un rango de fechas
    Cuando cualquier usuario consulta la duración de ese período
    Entonces el sistema responde la duración en días, considerando la duración tradicional de los meses involucrados

  Escenario: Consultar la duración de un período con recurrencia MENSUAL
    Dado un período con recurrencia MENSUAL que declara ciertos días del mes sobre un conjunto de meses
    Cuando cualquier usuario consulta la duración de ese período
    Entonces el sistema responde el agregado de los días declarados en cada uno de esos meses
    Y el sistema no suma días que no pertenezcan a la declaración de esos meses

  Escenario: Retornar error si el código de calendario o de período no existe
    Dado que el código de calendario o el código de período indicado no existe
    Cuando cualquier usuario consulta la duración de ese período
    Entonces el sistema retorna un error
