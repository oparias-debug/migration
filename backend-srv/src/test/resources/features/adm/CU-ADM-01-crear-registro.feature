# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Crear un registro de un catálogo

  Como Administrador del Sistema
  Quiero crear un nuevo registro de un catálogo indicando el valor de todos sus campos
  Para incorporar datos maestros al catálogo

  Escenario: Crear un registro proveyendo el valor de todos los campos, incluido el KEY
    Dado un catálogo existente
    Cuando proveo el valor de cada uno de sus campos, incluido el campo KEY
    Entonces el registro se crea correctamente conforme a las Reglas 1 y 8

  Escenario: Rechazar la creación de un registro al que le falta el valor de algún campo
    Dado un catálogo existente
    Cuando intento crear un registro sin proveer el valor de alguno de los campos definidos
    Entonces el sistema rechaza la operación

  Escenario: Fijar el registro como ACTIVE cuando no se indican fechas de vigencia
    Dado un catálogo existente
    Cuando creo un registro sin indicar fechas de vigencia
    Entonces su estado queda ACTIVE conforme a la Regla 13

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Ver historias-CU-ADM-01.md.
