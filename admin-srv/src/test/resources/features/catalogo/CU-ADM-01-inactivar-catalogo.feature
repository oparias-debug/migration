# language: es
@CU-ADM-01 @HU-ADM-01-07 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Inactivar un catálogo

  Como Administrador del Sistema
  Quiero inactivar un catálogo, ya sea marcando su estado como INACTIVE o fijando su TO DATE
  Para darlo de baja lógica sin eliminarlo

  Escenario: Inactivar un catálogo fijando ACTIVE en INACTIVE
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" con estado "ACTIVE"
    Cuando fijo el estado ACTIVE del catálogo "CAT-A" en "INACTIVE"
    Entonces el catálogo "CAT-A" queda con estado "INACTIVE"
    Y la TO DATE del catálogo "CAT-A" queda fijada en la fecha actual

  Esquema del escenario: Inactivar un catálogo fijando su TO DATE
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" con estado "ACTIVE"
    Cuando fijo la TO DATE del catálogo "CAT-A" en <fecha>
    Entonces el catálogo "CAT-A" queda con estado "INACTIVE"

    Ejemplos:
      | fecha             |
      | la fecha actual   |
      | una fecha pasada  |

  Escenario: Las búsquedas sobre un catálogo inactivo retornan INACTIVE para todos sus registros
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" con estado "INACTIVE" y con registros
    Cuando realizo una búsqueda de registros sobre el catálogo "CAT-A"
    Entonces el sistema retorna "INACTIVE" para todos los registros del catálogo "CAT-A"

  Escenario: Rechazar la eliminación de un catálogo y ofrecer su inactivación
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A"
    Cuando intento eliminar el catálogo "CAT-A"
    Entonces el sistema rechaza la operación
    Y el sistema ofrece inactivar el catálogo "CAT-A" en su lugar
    Y el catálogo "CAT-A" sigue existiendo en el catalogMaster

  Escenario: Rechazar la inactivación de un catálogo a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" con estado "ACTIVE"
    Cuando intento inactivar el catálogo "CAT-A"
    Entonces el sistema rechaza la operación
