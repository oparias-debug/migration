# language: es
@CU-ADM-01 @rol:ADMINISTRADOR
Característica: Buscar un registro específico por su clave

  Como Administrador del Sistema (o un sistema consumidor)
  Quiero buscar un registro de un catálogo por el valor de su campo KEY, indicando opcionalmente una lista de nombres de campos
  Para obtener los valores requeridos de ese registro

  Escenario: Retornar los campos solicitados al buscar por un KEY existente con lista de campos
    Dado un valor de KEY existente y una lista de nombres de campos
    Cuando realizo la búsqueda
    Entonces el sistema retorna el valor de cada campo solicitado conforme a la Regla 4

  Escenario: Retornar el primer campo no KEY al buscar por un KEY existente sin lista de campos
    Dado un valor de KEY existente sin lista de campos
    Cuando realizo la búsqueda
    Entonces el sistema retorna el valor del primer campo no KEY del registro conforme a la Regla 4

  Escenario: Reportar error cuando el valor de KEY no corresponde a ningún registro
    Dado un valor de KEY que no corresponde a ningún registro
    Cuando realizo la búsqueda
    Entonces el sistema reporta un error conforme a la Regla 4

  Escenario: Reportar error cuando se solicita un campo que no existe en el catálogo
    Dado un nombre de campo que no existe en el catálogo
    Cuando realizo la búsqueda solicitando ese campo
    Entonces el sistema reporta un error conforme a la Regla 4

  Escenario: Retornar INACTIVE cuando el catálogo o el registro están inactivos
    Dado que el catálogo o el registro está INACTIVE
    Cuando realizo la búsqueda
    Entonces el sistema retorna INACTIVE conforme a la Regla 12

  # ⚠️ Pendiente: no se genera escenario de rechazo por falta de permisos de administración
  # de catálogos porque el CU-ADM-01 no especifica el mensaje ni el flujo de error para ese caso.
  # Tampoco se modela un mecanismo de autenticación/rol distinto para el "sistema consumidor"
  # mencionado como actor alternativo, por no estar detallado en el CU. Ver historias-CU-ADM-01.md.
