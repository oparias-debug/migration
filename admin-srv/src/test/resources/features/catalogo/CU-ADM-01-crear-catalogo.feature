# language: es
@CU-ADM-01 @HU-ADM-01-01 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Crear un catálogo

  Como Administrador del Sistema
  Quiero crear un nuevo catálogo indicando su código, nombre, catálogo padre (opcional), estado, vigencia y al menos un campo KEY
  Para disponer de una nueva estructura de datos maestros en el sistema, sea independiente o hija de otro catálogo

  Escenario: Crear un catálogo con código, nombre y al menos un campo KEY
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Cuando creo el catálogo con código "CAT-A", nombre "Catálogo A" y los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Entonces el catálogo "CAT-A" queda creado en el catalogMaster

  Escenario: Rechazar la creación de un catálogo sin ningún campo KEY
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Cuando intento crear el catálogo con código "CAT-A", nombre "Catálogo A" y los campos:
      | nombre      | calificador |
      | codigo      | FIELD       |
      | descripcion | FIELD       |
    Entonces el sistema rechaza la operación
    Y el catálogo "CAT-A" no existe en el catalogMaster

  Escenario: Rechazar la creación de un catálogo con nombres de campo repetidos
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Cuando intento crear el catálogo con código "CAT-A", nombre "Catálogo A" y los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
      | descripcion | FIELD       |
    Entonces el sistema rechaza la operación
    Y el catálogo "CAT-A" no existe en el catalogMaster

  Escenario: Rechazar la creación de un catálogo sin ningún campo
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Cuando intento crear el catálogo con código "CAT-A", nombre "Catálogo A" sin indicar campos
    Entonces el sistema rechaza la operación
    Y el catálogo "CAT-A" no existe en el catalogMaster

  Escenario: El catálogo creado sin fechas de vigencia queda ACTIVE
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Cuando creo el catálogo con código "CAT-A", nombre "Catálogo A", sin fechas de vigencia y los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Entonces el catálogo "CAT-A" queda creado con estado "ACTIVE"

  Escenario: Reportar error al indicar un catálogo padre inexistente
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que no existe en el catalogMaster un catálogo con código "CAT-PADRE"
    Cuando intento crear el catálogo con código "CAT-HIJO", nombre "Catálogo Hijo", catálogo padre "CAT-PADRE" y los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Entonces el sistema reporta un error
    Y el catálogo "CAT-HIJO" no existe en el catalogMaster

  Escenario: Crear un catálogo hijo de un catálogo padre existente
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-PADRE"
    Cuando creo el catálogo con código "CAT-HIJO", nombre "Catálogo Hijo", catálogo padre "CAT-PADRE" y los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Entonces el catálogo "CAT-HIJO" queda registrado como hijo del catálogo "CAT-PADRE"
    Y los registros que se creen en "CAT-HIJO" deberán enlazarse con un registro del catálogo "CAT-PADRE"

  Escenario: Rechazar la creación de un catálogo a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Cuando intento crear un catálogo
    Entonces el sistema rechaza la operación
