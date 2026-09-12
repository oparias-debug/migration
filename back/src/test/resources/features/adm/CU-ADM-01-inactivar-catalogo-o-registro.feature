# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Inactivar Catálogo o Registro

  Como Administrador de Catálogos
  Quiero inactivar un catálogo o un registro, sin que quede eliminado, mediante el cambio de su flag o el establecimiento de su TO DATE

  Esquema del escenario: Inactivar un catálogo o registro cambiando su flag a INACTIVE
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona "<objetivo>" existente en estado ACTIVE
    Cuando cambia el flag ACTIVE a INACTIVE
    Entonces el sistema asigna automáticamente la TO DATE con la fecha actual
    Y el "<objetivo>" queda marcado como INACTIVE
    Y el "<objetivo>" permanece almacenado, sin ser eliminado

    Ejemplos:
      | objetivo  |
      | catálogo  |
      | registro  |

  Esquema del escenario: Inactivar un catálogo o registro estableciendo la TO DATE en una fecha actual o pasada
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona "<objetivo>" existente en estado ACTIVE
    Cuando establece la TO DATE en la fecha actual o en una fecha pasada
    Entonces el "<objetivo>" queda marcado como INACTIVE
    Y el "<objetivo>" permanece almacenado, sin ser eliminado

    Ejemplos:
      | objetivo  |
      | catálogo  |
      | registro  |

  Escenario: Rechazar la inactivación de un catálogo o registro por un actor no autorizado
    Dado que el actor no está autenticado o no está autorizado para administrar catálogos
    Cuando intenta inactivar un catálogo o un registro
    Entonces el sistema rechaza la operación por falta de autorización
