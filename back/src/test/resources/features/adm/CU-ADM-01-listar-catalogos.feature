# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Listar los catálogos disponibles

  Como Administrador del Sistema
  Quiero obtener la lista de todos los catálogos existentes
  Para tener visibilidad de los catálogos definidos en el sistema

  Escenario: Listar todos los catálogos, incluyendo los inactivos
    Dado que existen catálogos activos e inactivos en el catalogMaster
    Cuando solicito el listado de catálogos
    Entonces el sistema retorna todos los catálogos del catalogMaster, incluyendo los inactivos

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
