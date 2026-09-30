# language: es
@CU-ADM-01 @HU-ADM-01-06 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Actualizar los campos de un catálogo

  Como Administrador del Sistema
  Quiero modificar, agregar o cambiar los campos (FIELD/KEY) de un catálogo
  Para ajustar su estructura antes de que tenga registros

  Escenario: Modificar los campos de un catálogo sin registros
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin registros, con los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Cuando actualizo los campos del catálogo "CAT-A" a:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
      | sigla       | FIELD       |
    Entonces los campos del catálogo "CAT-A" quedan actualizados

  Escenario: Rechazar la actualización de campos que deja nombres repetidos
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin registros, con los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Cuando intento actualizar los campos del catálogo "CAT-A" a:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
      | descripcion | FIELD       |
    Entonces el sistema rechaza la operación
    Y los campos del catálogo "CAT-A" no cambian

  Esquema del escenario: Rechazar la actualización de campos de un catálogo que ya contiene registros
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" con al menos un registro
    Cuando intento <operacion> en el catálogo "CAT-A"
    Entonces el sistema rechaza la operación
    Y los campos del catálogo "CAT-A" no cambian

    Ejemplos:
      | operacion                 |
      | agregar un campo          |
      | modificar un campo existente |

  Escenario: Rechazar la actualización de campos que deja al catálogo sin ningún campo KEY
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin registros, con los campos:
      | nombre      | calificador |
      | codigo      | KEY         |
      | descripcion | FIELD       |
    Cuando intento actualizar los campos del catálogo "CAT-A" a:
      | nombre      | calificador |
      | codigo      | FIELD       |
      | descripcion | FIELD       |
    Entonces el sistema rechaza la operación
    Y los campos del catálogo "CAT-A" no cambian

  Escenario: Rechazar la actualización de campos a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Y que existe en el catalogMaster el catálogo "CAT-A" sin registros
    Cuando intento agregar un campo en el catálogo "CAT-A"
    Entonces el sistema rechaza la operación
