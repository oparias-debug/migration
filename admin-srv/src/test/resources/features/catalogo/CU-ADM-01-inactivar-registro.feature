# language: es
@CU-ADM-01 @HU-ADM-01-13 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Inactivar un registro de un catálogo

  Como Administrador del Sistema
  Quiero inactivar un registro de un catálogo, ya sea marcando su estado como INACTIVE o fijando su TO DATE
  Para darlo de baja lógica sin eliminarlo

  Escenario: Inactivar un registro fijando ACTIVE en INACTIVE
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que el catálogo "CAT-A" contiene un registro con KEY "01" en estado "ACTIVE"
    Cuando fijo el estado ACTIVE del registro "01" del catálogo "CAT-A" en "INACTIVE"
    Entonces el registro "01" queda con estado "INACTIVE"
    Y la TO DATE del registro "01" queda fijada en la fecha actual

  Esquema del escenario: Inactivar un registro fijando su TO DATE
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que el catálogo "CAT-A" contiene un registro con KEY "01" en estado "ACTIVE"
    Cuando fijo la TO DATE del registro "01" del catálogo "CAT-A" en <fecha>
    Entonces el registro "01" queda con estado "INACTIVE"

    Ejemplos:
      | fecha            |
      | la fecha actual  |
      | una fecha pasada |

  Escenario: Rechazar la eliminación de un registro y ofrecer su inactivación
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que el catálogo "CAT-A" contiene un registro con KEY "01"
    Cuando intento eliminar el registro "01" del catálogo "CAT-A"
    Entonces el sistema rechaza la operación
    Y el sistema ofrece inactivar el registro "01" en su lugar
    Y el registro "01" sigue existiendo en el catálogo "CAT-A"

  Escenario: Rechazar la inactivación de un registro a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Y que el catálogo "CAT-A" contiene un registro con KEY "01" en estado "ACTIVE"
    Cuando intento inactivar el registro "01" del catálogo "CAT-A"
    Entonces el sistema rechaza la operación
