# language: es
@CU-ADM-01 @HU-ADM-01-14 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Consultar los registros hijos de un registro padre

  Como Administrador del Sistema
  Quiero consultar, a partir de un registro de un catálogo padre, los registros del catálogo hijo que están enlazados a él
  Para navegar la jerarquía de datos maestros a nivel de registro

  Escenario: Consultar un registro padre que tiene registros hijos
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe el catálogo "CAT-HIJO" con catálogo padre "CAT-PADRE"
    Y que el catálogo "CAT-PADRE" contiene un registro con KEY "P01"
    Y que el catálogo "CAT-HIJO" contiene los registros enlazados al registro padre "P01":
      | codigo |
      | H01    |
      | H02    |
    Cuando consulto los registros hijos del registro "P01" del catálogo "CAT-PADRE"
    Entonces el sistema retorna los registros hijos:
      | codigo |
      | H01    |
      | H02    |

  Escenario: Consultar un registro padre sin registros hijos asociados
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe el catálogo "CAT-HIJO" con catálogo padre "CAT-PADRE"
    Y que el catálogo "CAT-PADRE" contiene un registro con KEY "P02" sin registros hijos enlazados
    Cuando consulto los registros hijos del registro "P02" del catálogo "CAT-PADRE"
    Entonces el sistema retorna una lista vacía

  Escenario: Reportar error al consultar registros hijos en un catálogo sin catálogo hijo definido
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin catálogos hijos
    Y que el catálogo "CAT-A" contiene un registro con KEY "01"
    Cuando consulto los registros hijos del registro "01" del catálogo "CAT-A"
    Entonces el sistema reporta un error

  # ℹ️ Sin escenario (ambigüedad, ver historias-CU-ADM-01.md): el criterio "si el registro padre o el
  # catálogo hijo está INACTIVE, el sistema retorna INACTIVE según corresponda" no precisa qué elemento
  # se retorna como INACTIVE (el registro padre, cada registro hijo o toda la consulta).
  # Tampoco se genera escenario de permisos: la historia menciona "(o un sistema consumidor)", actor que
  # no figura en el catálogo de actores.
