# language: es
@CU-ADM-01 @HU-ADM-01-08 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Consultar los catálogos hijos de un catálogo padre

  Como Administrador del Sistema
  Quiero consultar los catálogos hijos de un catálogo dado
  Para conocer la jerarquía de catálogos

  Escenario: Consultar un catálogo padre que tiene catálogos hijos
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-PADRE"
    Y que existen los catálogos hijos de "CAT-PADRE":
      | codigo     | nombre          |
      | CAT-HIJO-1 | Catálogo Hijo 1 |
      | CAT-HIJO-2 | Catálogo Hijo 2 |
    Cuando consulto los catálogos hijos del catálogo "CAT-PADRE"
    Entonces el sistema retorna la lista de código y nombre:
      | codigo     | nombre          |
      | CAT-HIJO-1 | Catálogo Hijo 1 |
      | CAT-HIJO-2 | Catálogo Hijo 2 |

  Escenario: Consultar un catálogo sin catálogos hijos
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin catálogos hijos
    Cuando consulto los catálogos hijos del catálogo "CAT-A"
    Entonces el sistema retorna una lista vacía

  Escenario: Rechazar la consulta de catálogos hijos a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Cuando intento consultar los catálogos hijos del catálogo "CAT-PADRE"
    Entonces el sistema rechaza la operación
