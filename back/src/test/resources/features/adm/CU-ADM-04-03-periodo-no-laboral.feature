# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Definir un período NO_LABORAL

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero agregar a un calendario un período NO_LABORAL con código, nombre y una recurrencia
  Para delimitar los rangos de fechas que se consideran no hábiles

  Esquema del escenario: Agregar un período NO_LABORAL con cada tipo de recurrencia
    Dado un calendario existente con rango de fechas <fecha_inicio_calendario> a <fecha_fin_calendario>
    Y no existe dentro de ese calendario ningún período con el código indicado
    Cuando el actor agrega un período NO_LABORAL con recurrencia "<recurrencia>" y los datos correspondientes
    Entonces el período NO_LABORAL queda registrado en el calendario
    Y el período hereda el estado del calendario

    Ejemplos:
      | recurrencia | fecha_inicio_calendario | fecha_fin_calendario |
      | UNA_VEZ     | 2026-01-01              | 2026-12-31            |
      | SEMANAL     | 2026-01-01              | 2026-12-31            |
      | MENSUAL     | 2026-01-01              | 2026-12-31            |

  Escenario: Rechazar un período NO_LABORAL con código ya usado en el mismo calendario
    Dado que ya existe un período con un código determinado dentro de un calendario
    Cuando el actor intenta agregar un nuevo período NO_LABORAL con ese mismo código en el mismo calendario
    Entonces el sistema rechaza la operación

  Escenario: Rechazar un período NO_LABORAL cuya fecha de inicio es posterior a la fecha de fin
    Dado que el actor prepara los datos de un nuevo período NO_LABORAL con recurrencia UNA_VEZ o SEMANAL
    Cuando el actor indica una fecha de inicio posterior a la fecha de fin del período
    Entonces el sistema rechaza la operación

  Escenario: Rechazar un período NO_LABORAL que no está enmarcado en el rango del calendario
    Dado un calendario con un rango de fechas determinado
    Cuando el actor agrega un período NO_LABORAL cuyo rango de fechas excede el rango del calendario
    Entonces el sistema rechaza la operación

  Escenario: Permitir que dos períodos NO_LABORAL se intersecten sin generar conflicto
    Dado un calendario con un período NO_LABORAL ya definido
    Cuando el actor agrega un segundo período NO_LABORAL cuyas fechas se intersectan con el primero
    Entonces el sistema registra el segundo período NO_LABORAL sin reportar ningún conflicto

  Escenario: Rechazar la creación de un período NO_LABORAL a un actor sin el rol adecuado
    Dado que el actor autenticado no tiene el rol ADMINISTRADOR ni ADMINISTRADOR_CALENDARIO
    Cuando el actor intenta agregar un período NO_LABORAL a un calendario
    Entonces el sistema rechaza la operación por falta de permisos
