# language: es
@CU-ADM-01 @HU-ADM-01-03 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Listar los catálogos disponibles

  Como Administrador del Sistema
  Quiero obtener la lista de todos los catálogos existentes
  Para tener visibilidad de los catálogos definidos en el sistema

  Escenario: Listar todos los catálogos del catalogMaster, incluyendo los inactivos
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que el catalogMaster contiene los catálogos:
      | codigo | estado   |
      | CAT-A  | ACTIVE   |
      | CAT-B  | INACTIVE |
    Cuando solicito el listado de catálogos
    Entonces el sistema retorna los catálogos:
      | codigo |
      | CAT-A  |
      | CAT-B  |

  Escenario: Rechazar el listado de catálogos a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Cuando intento solicitar el listado de catálogos
    Entonces el sistema rechaza la operación
