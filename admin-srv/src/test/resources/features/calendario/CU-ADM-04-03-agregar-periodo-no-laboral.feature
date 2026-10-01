# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Agregar un período NO_LABORAL a un calendario

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero agregar a un calendario un período NO_LABORAL con código, nombre y una recurrencia

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026", fecha de inicio "2026-01-01" y fecha de fin "2026-12-31"

  Esquema del escenario: Agregar un período NO_LABORAL con cada forma de recurrencia
    Dado que el actor tiene el rol "ADMINISTRADOR_CALENDARIO"
    Cuando el actor agrega al calendario "CAL-2026" un período NO_LABORAL con código "<codigo>", nombre "<nombre>" y recurrencia "<recurrencia>"
    Entonces el período se agrega correctamente al calendario "CAL-2026"
    Y el período hereda el estado "ACTIVO" del calendario

    Ejemplos:
      | codigo   | nombre                | recurrencia                                          |
      | NOLAB-01 | Vacaciones colectivas | UNA_VEZ (2026-07-01 a 2026-07-15)                    |
      | NOLAB-02 | Fines de semana       | SEMANAL (2026-01-01 a 2026-12-31, sábado y domingo)  |
      | NOLAB-03 | Feriados fijos        | MENSUAL (día 25, meses diciembre)                    |

  Escenario: Rechazar el período si su código ya existe en el mismo calendario
    Dado que el calendario "CAL-2026" ya tiene un período con código "NOLAB-01"
    Cuando el actor intenta agregar otro período con código "NOLAB-01" al calendario "CAL-2026"
    Entonces el sistema rechaza la operación indicando que el código de período ya existe en ese calendario

  Escenario: Rechazar el período si la fecha de inicio es posterior a la fecha de fin
    Cuando el actor intenta agregar un período NO_LABORAL con fecha de inicio "2026-07-15" y fecha de fin "2026-07-01"
    Entonces el sistema rechaza la operación indicando que la fecha de inicio no puede ser posterior a la fecha de fin

  Escenario: Rechazar el período si no queda enmarcado dentro del rango del calendario
    Cuando el actor intenta agregar un período NO_LABORAL con fecha de inicio "2026-12-20" y fecha de fin "2027-01-05" al calendario "CAL-2026"
    Entonces el sistema rechaza la operación indicando que el período no está enmarcado dentro del rango del calendario

  Escenario: Rechazar la operación cuando el actor no tiene el rol adecuado
    Dado que el actor no tiene el rol "ADMINISTRADOR" ni "ADMINISTRADOR_CALENDARIO"
    Cuando el actor intenta agregar un período NO_LABORAL al calendario "CAL-2026"
    Entonces el sistema rechaza la operación por falta de permisos
