# language: es
@CU-ADM-01 @HU-ADM-01-12 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Actualizar un registro de un catálogo

  Como Administrador del Sistema
  Quiero actualizar el valor de cualquier campo no KEY de un registro
  Para mantener actualizada la información del catálogo

  Escenario: Actualizar campos no KEY de un registro existente
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que el catálogo "CAT-A" contiene el registro:
      | codigo | descripcion   | sigla |
      | 01     | Descripción 1 | D1    |
    Cuando actualizo el registro con KEY "01" del catálogo "CAT-A" con los valores:
      | campo       | valor                |
      | descripcion | Descripción revisada |
      | sigla       | DR                   |
    Entonces el registro con KEY "01" del catálogo "CAT-A" queda con los valores:
      | campo       | valor                |
      | descripcion | Descripción revisada |
      | sigla       | DR                   |

  Escenario: Rechazar la modificación del campo KEY de un registro
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que el catálogo "CAT-A" contiene el registro:
      | codigo | descripcion   | sigla |
      | 01     | Descripción 1 | D1    |
    Cuando intento modificar el campo KEY "codigo" del registro "01" del catálogo "CAT-A" a "02"
    Entonces el sistema rechaza la operación
    Y el registro conserva el valor de KEY "01"

  Escenario: Rechazar la actualización de un registro a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Y que el catálogo "CAT-A" contiene un registro con KEY "01"
    Cuando intento actualizar un campo no KEY del registro con KEY "01" del catálogo "CAT-A"
    Entonces el sistema rechaza la operación
