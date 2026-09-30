# language: es
@CU-ADM-01 @HU-ADM-01-04 @rol:ADMINISTRADOR_DEL_SISTEMA @rol:SISTEMA
Característica: Verificar existencia de un catálogo por nombre

  Como Administrador del Sistema
  Quiero verificar si existe un catálogo con un nombre específico
  Para evitar duplicidad antes de crear uno nuevo

  Escenario: Confirmar la existencia de un catálogo con un nombre ya definido
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que existe en el catalogMaster un catálogo con nombre "Catálogo A"
    Cuando verifico si existe un catálogo con nombre "Catálogo A"
    Entonces el sistema confirma que el catálogo existe

  Escenario: Indicar que no está definido un catálogo con un nombre inexistente
    Dado que he iniciado sesión con el rol "Administrador del Sistema"
    Y que no existe en el catalogMaster un catálogo con nombre "Catálogo X"
    Cuando verifico si existe un catálogo con nombre "Catálogo X"
    Entonces el sistema indica que el catálogo no está definido

  Escenario: Rechazar la verificación de existencia a un usuario sin permisos de administración de catálogos
    Dado que he iniciado sesión con un usuario que no tiene el rol "Administrador del Sistema"
    Cuando intento verificar si existe un catálogo con nombre "Catálogo A"
    Entonces el sistema rechaza la operación
