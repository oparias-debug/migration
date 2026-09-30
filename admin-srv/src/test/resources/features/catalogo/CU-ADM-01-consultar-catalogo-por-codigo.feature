# language: es
@CU-ADM-01 @HU-ADM-01-02 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Consultar un catálogo por código

  Como Administrador del Sistema
  Quiero recuperar un catálogo indicando su código
  Para revisar o utilizar su definición

  Escenario: Consultar un catálogo existente por su código
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A"
    Cuando consulto el catálogo con código "CAT-A"
    Entonces el sistema retorna la definición completa del catálogo "CAT-A":
      | descriptor |
      | nombre     |
      | padre      |
      | estado     |
      | vigencia   |
      | campos     |

  Escenario: Reportar error al consultar un código de catálogo inexistente
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que no existe en el catalogMaster un catálogo con código "CAT-X"
    Cuando consulto el catálogo con código "CAT-X"
    Entonces el sistema reporta un error

  Escenario: Rechazar la consulta de un catálogo a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Cuando intento consultar el catálogo con código "CAT-A"
    Entonces el sistema rechaza la operación
