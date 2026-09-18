# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Crear un calendario

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero crear un calendario indicando código, nombre, descripción, fecha de inicio, fecha de fin y estado
  Para establecer el marco temporal en el que se definirán los períodos y excepciones

  Escenario: Crear un calendario con datos válidos
    Dado que el actor autenticado tiene el rol ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
    Y no existe ningún calendario con el código indicado
    Cuando el actor crea un calendario con código, nombre, descripción, fecha de inicio, fecha de fin y estado válidos
    Entonces el calendario queda creado con los datos indicados
    Y el calendario queda disponible para las consultas de solo lectura

  Escenario: Rechazar la creación si el código de calendario ya existe
    Dado que ya existe un calendario registrado con un código determinado
    Cuando el actor intenta crear un nuevo calendario con ese mismo código
    Entonces el sistema rechaza la operación
    Y el sistema informa que el código de calendario ya existe

  Escenario: Rechazar la creación si la fecha de inicio es posterior a la fecha de fin
    Dado que el actor prepara los datos de un nuevo calendario
    Cuando el actor indica una fecha de inicio posterior a la fecha de fin
    Entonces el sistema rechaza la operación

  Escenario: Rechazar la creación de un calendario a un actor sin el rol adecuado
    Dado que el actor autenticado no tiene el rol ADMINISTRADOR ni ADMINISTRADOR_CALENDARIO
    Cuando el actor intenta crear un calendario
    Entonces el sistema rechaza la operación por falta de permisos
