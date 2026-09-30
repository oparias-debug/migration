# language: es
@CU-ADM-01 @HU-ADM-01-05 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Actualizar los descriptores de un catálogo

  Como Administrador del Sistema
  Quiero modificar el nombre, el catálogo padre, el estado activo o la vigencia de un catálogo
  Para mantener actualizada su definición

  Esquema del escenario: Actualizar un descriptor modificable de un catálogo existente
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A"
    Cuando actualizo el descriptor "<descriptor>" del catálogo "CAT-A" con un nuevo valor válido
    Entonces el nuevo valor del descriptor "<descriptor>" del catálogo "CAT-A" queda guardado

    Ejemplos:
      | descriptor |
      | nombre     |
      | padre      |
      | active     |
      | valid      |

  Escenario: Rechazar la modificación del código de un catálogo
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A"
    Cuando intento modificar el código del catálogo "CAT-A" a "CAT-B"
    Entonces el sistema rechaza la operación
    Y el catálogo conserva el código "CAT-A"

  Escenario: Rechazar la actualización de descriptores a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A"
    Cuando intento actualizar el nombre del catálogo "CAT-A"
    Entonces el sistema rechaza la operación

  # ℹ️ Sin escenario (ambigüedad, ver historias-CU-ADM-01.md): el CU no indica qué ocurre al asignar
  # un catálogo padre inexistente, ni al cambiar el padre de un catálogo que ya tiene registros enlazados
  # a registros del padre anterior (Regla 23).
