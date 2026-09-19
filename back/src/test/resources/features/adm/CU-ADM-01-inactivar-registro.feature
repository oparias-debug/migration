# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Inactivar un registro de un catálogo

  Como Administrador del Sistema
  Quiero inactivar un registro de un catálogo, ya sea marcando su estado como INACTIVE o fijando su TO DATE
  Para darlo de baja lógica sin eliminarlo

  Escenario: Fijar automáticamente la TO DATE al marcar el registro como INACTIVE
    Dado un registro activo
    Cuando fijo su ACTIVE en INACTIVE
    Entonces el sistema fija automáticamente su TO DATE en la fecha actual conforme a la Regla 9a

  Escenario: Inactivar el registro al fijar una TO DATE actual o pasada
    Dado un registro activo
    Cuando fijo su TO DATE en la fecha actual o en una fecha pasada
    Entonces el registro queda INACTIVE conforme a las Reglas 9b y 14

  Escenario: Rechazar la eliminación física de un registro
    Dado un registro existente
    Cuando intento eliminar (borrar) el registro
    Entonces el sistema rechaza la operación y solo permite inactivarlo conforme a la Regla 11

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
