# language: es
@CU-ADM-01 @HU-ADM-01-09 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Crear un registro de un catálogo

  Como Administrador del Sistema
  Quiero crear un nuevo registro de un catálogo indicando el valor de todos sus campos y, si el catálogo tiene padre, el registro padre correspondiente
  Para incorporar datos maestros al catálogo, manteniendo la trazabilidad jerárquica cuando aplique

  Escenario: Crear un registro en un catálogo sin padre
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin catálogo padre, con los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Cuando creo un registro en el catálogo "CAT-A" con los valores:
      | campo       | valor         |
      | codigo      | 01            |
      | descripcion | Descripción 1 |
    Entonces el registro con KEY "01" queda creado en el catálogo "CAT-A"

  Escenario: Crear un registro en un catálogo hijo enlazado a un registro padre existente
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-PADRE" con un registro de KEY "P01"
    Y que existe en el catalogMaster el catálogo "CAT-HIJO" con catálogo padre "CAT-PADRE", con los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Cuando creo un registro en el catálogo "CAT-HIJO" con registro padre "P01" y los valores:
      | campo       | valor         |
      | codigo      | H01           |
      | descripcion | Descripción 1 |
    Entonces el registro con KEY "H01" queda creado en el catálogo "CAT-HIJO"
    Y el registro "H01" queda enlazado al registro padre "P01" del catálogo "CAT-PADRE"

  Esquema del escenario: Rechazar la creación de un registro en un catálogo hijo sin un registro padre válido
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-HIJO" con catálogo padre "CAT-PADRE"
    Cuando intento crear un registro en el catálogo "CAT-HIJO" con todos sus valores y <registro_padre>
    Entonces el sistema rechaza la operación
    Y no se crea el registro en el catálogo "CAT-HIJO"

    Ejemplos:
      | registro_padre                                                 |
      | sin indicar el registro padre                                  |
      | un registro padre que no existe en el catálogo "CAT-PADRE"     |

  Escenario: Rechazar la creación de un registro al que le falta el valor de un campo definido
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin catálogo padre, con los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Cuando intento crear un registro en el catálogo "CAT-A" con los valores:
      | campo  | valor |
      | codigo | 01    |
    Entonces el sistema rechaza la operación
    Y no se crea el registro en el catálogo "CAT-A"

  Escenario: El registro creado sin fechas de vigencia queda ACTIVE
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin catálogo padre
    Cuando creo un registro en el catálogo "CAT-A" con todos sus valores y sin fechas de vigencia
    Entonces el registro queda creado con estado "ACTIVE"

  Escenario: Rechazar la creación de un registro a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin catálogo padre
    Cuando intento crear un registro en el catálogo "CAT-A" con todos sus valores
    Entonces el sistema rechaza la operación

  # ℹ️ Sin escenario (ambigüedad, ver historias-CU-ADM-01.md): el CU no indica qué ocurre al crear un
  # registro con un valor de KEY ya existente, en un catálogo INACTIVE, enlazado a un registro padre
  # INACTIVE, ni al indicar un registro padre en un catálogo que no tiene padre.
