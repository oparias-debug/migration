# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Actualizar los campos de un catálogo

  Como Administrador del Sistema
  Quiero modificar, agregar o cambiar los campos (FIELD/KEY) de un catálogo
  Para ajustar su estructura antes de que tenga registros

  Escenario: Permitir la actualización de campos de un catálogo sin registros
    Dado un catálogo que no contiene registros
    Cuando modifico sus campos manteniendo nombres únicos entre ellos
    Entonces el sistema permite la actualización conforme a la Regla 3

  Escenario: Rechazar la actualización de campos de un catálogo que ya contiene registros
    Dado un catálogo que sí contiene registros
    Cuando intento modificar sus campos
    Entonces el sistema rechaza la operación conforme a la Regla 19

  Escenario: Rechazar una actualización que deja al catálogo sin ningún campo KEY
    Dado un catálogo sin registros cuya modificación de campos elimina todos los campos KEY
    Cuando intento guardar esa modificación
    Entonces el sistema rechaza la operación conforme a la Regla 2

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
