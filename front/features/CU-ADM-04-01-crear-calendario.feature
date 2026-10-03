# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Crear un calendario

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero crear un calendario indicando código, nombre, descripción, fecha de inicio, fecha de fin y estado

  Esquema del escenario: Crear un calendario válido con estado inicial indicado
    Dado que el actor tiene el rol "<rol>"
    Y no existe ningún calendario con código "CAL-2026"
    Cuando el actor crea un calendario con código "CAL-2026", nombre "Calendario 2026", fecha de inicio "2026-01-01", fecha de fin "2026-12-31" y estado "<estado>"
    Entonces el calendario se crea correctamente con estado "<estado>"

    Ejemplos:
      | rol                      | estado   |
      | ADMINISTRADOR            | ACTIVO   |
      | ADMINISTRADOR_CALENDARIO | INACTIVO |

  Escenario: Rechazar la creación si el código de calendario ya existe
    Dado que el actor tiene el rol "ADMINISTRADOR"
    Y ya existe un calendario con código "CAL-2026"
    Cuando el actor intenta crear otro calendario con código "CAL-2026"
    Entonces el sistema rechaza la operación indicando que el código ya existe

  Escenario: Rechazar la creación si la fecha de inicio es posterior a la fecha de fin
    Dado que el actor tiene el rol "ADMINISTRADOR"
    Cuando el actor intenta crear un calendario con fecha de inicio "2026-12-31" y fecha de fin "2026-01-01"
    Entonces el sistema rechaza la operación indicando que la fecha de inicio no puede ser posterior a la fecha de fin

  Escenario: Rechazar la creación cuando el actor no tiene el rol adecuado
    Dado que el actor no tiene el rol "ADMINISTRADOR" ni "ADMINISTRADOR_CALENDARIO"
    Cuando el actor intenta crear un calendario
    Entonces el sistema rechaza la operación por falta de permisos
