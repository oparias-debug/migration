# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Editar la definición de un calendario

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero recuperar un calendario presentando su código y editar su definición agregando, editando o eliminando CalendarItems

  Antecedentes:
    Dado que existe un calendario ACTIVO con código "CAL-2026" con un período LABORAL "LAB-01"

  Esquema del escenario: Editar la definición de un calendario recuperado
    Dado que el actor tiene el rol "<rol>" y recupera el calendario "CAL-2026" para editarlo
    Cuando el actor "<accion>" el CalendarItem "<item>" del calendario "CAL-2026"
    Entonces el cambio queda reflejado en la definición del calendario "CAL-2026"

    Ejemplos:
      | rol                      | accion   | item                                 |
      | ADMINISTRADOR            | elimina  | LAB-01                               |
      | ADMINISTRADOR_CALENDARIO | edita    | LAB-01                               |
      | ADMINISTRADOR            | adiciona | NOLAB-01 (nuevo período NO_LABORAL)  |

  Escenario: Rechazar la edición si el nuevo código de período ya existe en el calendario
    Cuando el actor intenta adicionar un CalendarItem con código "LAB-01" al calendario "CAL-2026"
    Entonces el sistema rechaza la operación indicando que el código de período ya existe en ese calendario

  Escenario: Rechazar la edición si la fecha de inicio del período editado es posterior a la fecha de fin
    Cuando el actor edita el período "LAB-01" del calendario "CAL-2026" con fecha de inicio posterior a la fecha de fin
    Entonces el sistema rechaza la operación indicando que la fecha de inicio no puede ser posterior a la fecha de fin

  Escenario: Rechazar la edición si el período editado no queda enmarcado dentro del rango del calendario
    Cuando el actor edita el período "LAB-01" del calendario "CAL-2026" con fechas fuera del rango del calendario
    Entonces el sistema rechaza la operación indicando que el período no está enmarcado dentro del rango del calendario

  Escenario: Retornar error si el calendario a editar no existe
    Cuando el actor intenta recuperar para edición el calendario "CAL-INEXISTENTE"
    Entonces el sistema retorna error indicando que el calendario no existe

  Escenario: Rechazar la operación cuando el actor no tiene el rol adecuado
    Dado que el actor no tiene el rol "ADMINISTRADOR" ni "ADMINISTRADOR_CALENDARIO"
    Cuando el actor intenta editar la definición del calendario "CAL-2026"
    Entonces el sistema rechaza la operación por falta de permisos
