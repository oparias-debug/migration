# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Verificar existencia de un catálogo por nombre

  Como Administrador del Sistema
  Quiero verificar si existe un catálogo con un nombre específico
  Para evitar duplicidad antes de crear uno nuevo

  Escenario: Confirmar la existencia de un catálogo cuyo nombre ya está registrado
    Dado un nombre de catálogo que ya existe en el catalogMaster
    Cuando realizo la búsqueda por ese nombre
    Entonces el sistema confirma su existencia

  Escenario: Indicar que un catálogo con ese nombre no está definido
    Dado un nombre de catálogo que no existe en el catalogMaster
    Cuando realizo la búsqueda por ese nombre
    Entonces el sistema indica que no está definido

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
