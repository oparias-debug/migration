# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Buscar Lista de Registros

  Como Administrador de Catálogos
  Quiero buscar la lista completa de registros de un catálogo, obteniendo todos los campos o solo los que indique

  Escenario: Buscar la lista de registros sin especificar lista de campos
    Dado que un catálogo tiene registros almacenados
    Cuando se solicita la lista de registros del catálogo sin especificar clave ni lista de campos
    Entonces el sistema retorna el valor del primer campo no-KEY de todos los registros

  Escenario: Buscar la lista de registros especificando una lista de campos
    Dado que un catálogo tiene registros almacenados
    Cuando se solicita la lista de registros del catálogo indicando una lista de nombres de campos
    Entonces el sistema retorna la lista de valores de los campos solicitados para todos los registros encontrados

  # ⚠️ Nota: el CU no especifica qué ocurre si se solicita un campo que no existe en el catálogo
  # en este flujo (a diferencia de la búsqueda por clave, sección 7.2). No se generó escenario
  # de error para ese caso para no inventar comportamiento no descrito.
