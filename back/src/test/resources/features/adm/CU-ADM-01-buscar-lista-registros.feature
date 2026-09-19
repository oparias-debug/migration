# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Buscar una lista de registros de un catálogo

  Como Administrador del Sistema (o un sistema consumidor)
  Quiero buscar la lista de registros de un catálogo, indicando opcionalmente una lista de nombres de campos
  Para obtener los valores requeridos de todos los registros

  Escenario: Retornar los campos solicitados para todos los registros de un catálogo
    Dado un catálogo con registros y una lista de nombres de campos
    Cuando realizo la búsqueda sin indicar KEY
    Entonces el sistema retorna los valores de cada campo solicitado para cada registro conforme a la Regla 5

  Escenario: Retornar el primer campo no KEY de todos los registros cuando no se indica lista de campos
    Dado un catálogo con registros sin lista de campos
    Cuando realizo la búsqueda
    Entonces el sistema retorna el valor del primer campo no KEY de todos los registros conforme a la Regla 5

  Escenario: Retornar INACTIVE para todos los registros cuando el catálogo está inactivo
    Dado que el catálogo está INACTIVE
    Cuando realizo la búsqueda
    Entonces el sistema retorna INACTIVE para todos los registros conforme a la Regla 12

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Tampoco se modela un mecanismo de autenticación/rol distinto para el "sistema consumidor"
  # mencionado como actor alternativo, por no estar detallado en el CU. Ver historias-CU-ADM-01.md.
