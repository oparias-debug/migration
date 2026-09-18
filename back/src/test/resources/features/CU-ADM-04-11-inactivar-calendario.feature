# language: es
@CU-ADM-04 @rol:ADMINISTRADOR @rol:ADMINISTRADOR_CALENDARIO
Característica: Cambiar el estado de un calendario (activar/inactivar)

  Como ADMINISTRADOR o ADMINISTRADOR_CALENDARIO
  Quiero cambiar el estado de un calendario entre ACTIVO e INACTIVO, sin poder eliminarlo del sistema
  Para dar de baja o restituir funcionalmente un calendario conservando su configuración

  Esquema del escenario: Cambiar el estado de un calendario y propagarlo a sus CalendarItems
    Dado un calendario existente en estado "<estado_actual>" con períodos y excepciones asociados
    Cuando el actor cambia el estado del calendario a "<estado_nuevo>"
    Entonces el calendario queda en estado "<estado_nuevo>"
    Y todos los períodos y excepciones del calendario heredan el estado "<estado_nuevo>"

    Ejemplos:
      | estado_actual | estado_nuevo |
      | ACTIVO        | INACTIVO     |
      | INACTIVO      | ACTIVO       |

  Escenario: Rechazar cualquier intento de eliminar un calendario
    Dado un calendario existente en el sistema
    Cuando el actor intenta eliminar el calendario
    Entonces el sistema rechaza la operación
    Y el sistema indica que un calendario no puede eliminarse, solo inactivarse

  Escenario: Rechazar el cambio de estado de un calendario a un actor sin el rol adecuado
    Dado que el actor autenticado no tiene el rol ADMINISTRADOR ni ADMINISTRADOR_CALENDARIO
    Cuando el actor intenta cambiar el estado de un calendario
    Entonces el sistema rechaza la operación por falta de permisos
