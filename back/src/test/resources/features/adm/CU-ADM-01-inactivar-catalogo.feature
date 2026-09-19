# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Inactivar un catálogo

  Como Administrador del Sistema
  Quiero inactivar un catálogo, ya sea marcando su estado como INACTIVE o fijando su TO DATE
  Para darlo de baja lógica sin eliminarlo

  Escenario: Fijar automáticamente la TO DATE al marcar el catálogo como INACTIVE
    Dado un catálogo activo
    Cuando fijo su ACTIVE en INACTIVE
    Entonces el sistema fija automáticamente la TO DATE en la fecha actual conforme a la Regla 9a

  Escenario: Inactivar el catálogo al fijar una TO DATE actual o pasada
    Dado un catálogo activo
    Cuando fijo su TO DATE en la fecha actual o en una fecha pasada
    Entonces el catálogo queda INACTIVE conforme a las Reglas 9b y 14

  Escenario: Propagar el estado INACTIVE del catálogo a la consulta de sus registros
    Dado un catálogo inactivo
    Cuando se consultan sus registros
    Entonces toda búsqueda sobre ese catálogo retorna INACTIVE conforme a la Regla 12

  Escenario: Rechazar la eliminación física de un catálogo
    Dado un catálogo existente
    Cuando intento eliminar (borrar) el catálogo
    Entonces el sistema rechaza la operación y solo permite inactivarlo conforme a la Regla 10

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
