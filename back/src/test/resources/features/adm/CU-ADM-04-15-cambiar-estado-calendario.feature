# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Cambiar el estado de un calendario (activar/inactivar)

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero cambiar el estado de un calendario entre ACTIVO e INACTIVO, sin poder eliminarlo

  Antecedentes:
    Dado que existe un calendario con código "CAL-2026" con períodos LABORAL, NO_LABORAL y una excepción

  Esquema del escenario: Cambiar el estado del calendario y heredar el estado en sus CalendarItems
    Dado que el actor tiene el rol "<rol>"
    Y el calendario "CAL-2026" está en estado "<estado_actual>"
    Cuando el actor cambia el estado del calendario "CAL-2026" a "<estado_nuevo>"
    Entonces el calendario queda en estado "<estado_nuevo>"
    Y todos los CalendarItems del calendario "CAL-2026" heredan el estado "<estado_nuevo>"

    Ejemplos:
      | rol                      | estado_actual | estado_nuevo |
      | ADMINISTRADOR            | ACTIVO        | INACTIVO     |
      | ADMINISTRADOR_CALENDARIO | INACTIVO      | ACTIVO       |

  Escenario: Rechazar la operación cuando el actor no tiene el rol adecuado
    Dado que el actor no tiene el rol "ADMINISTRADOR" ni "ADMINISTRADOR_CALENDARIO"
    Cuando el actor intenta cambiar el estado del calendario "CAL-2026"
    Entonces el sistema rechaza la operación por falta de permisos

  # ⚠️ Escenario pendiente: el CU establece que el calendario "no puede eliminarse"
  # pero no describe el comportamiento del sistema ante un intento de eliminación
  # (mensaje, código de error, etc.). No implementar hasta que se resuelva.
