# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Actualizar un registro de un catálogo

  Como Administrador del Sistema
  Quiero actualizar el valor de cualquier campo no KEY de un registro
  Para mantener actualizada la información del catálogo

  Escenario: Guardar la actualización del valor de campos no KEY de un registro
    Dado un registro existente
    Cuando actualizo el valor de uno o más de sus campos no KEY
    Entonces los cambios se guardan correctamente conforme a la Regla 16

  Escenario: Rechazar la actualización del valor del campo KEY de un registro
    Dado un registro existente
    Cuando intento modificar el valor de su campo KEY
    Entonces el sistema rechaza la operación conforme a la Regla 16

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
