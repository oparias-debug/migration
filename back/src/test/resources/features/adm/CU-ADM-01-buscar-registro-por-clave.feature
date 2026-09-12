# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Buscar un Registro por Clave

  Como Administrador de Catálogos
  Quiero buscar un registro de un catálogo por su clave (KEY), obteniendo todos los campos o solo los que indique

  Escenario: Buscar un registro por clave sin especificar lista de campos
    Dado que existe un registro con una clave (KEY) determinada en un catálogo
    Cuando se provee el código del catálogo y el valor de la clave, sin especificar lista de campos
    Entonces el sistema retorna el valor del primer campo no-KEY del registro

  Escenario: Buscar un registro por clave especificando una lista de campos válida
    Dado que existe un registro con una clave (KEY) determinada en un catálogo
    Y se especifica una lista de nombres de campos que existen en el catálogo
    Cuando se provee el código del catálogo, el valor de la clave y la lista de campos
    Entonces el sistema retorna el conjunto de valores solicitados

  Escenario: Rechazar la búsqueda por una clave (KEY) inexistente
    Dado que no existe ningún registro con la clave (KEY) provista en el catálogo
    Cuando se realiza la búsqueda por esa clave
    Entonces el sistema reporta un error

  Escenario: Rechazar la búsqueda cuando se solicita un campo que no existe en el catálogo
    Dado que existe un registro con una clave (KEY) determinada en un catálogo
    Y se especifica una lista de campos donde al menos uno no existe en el catálogo
    Cuando se provee el código del catálogo, el valor de la clave y la lista de campos
    Entonces el sistema reporta un error

  Escenario: Retornar INACTIVO al consultar registros de un catálogo marcado como INACTIVE
    Dado que el catálogo está marcado como INACTIVE
    Cuando se busca cualquier registro de ese catálogo por su clave
    Entonces el sistema retorna INACTIVO para el registro consultado

  # ⚠️ Nota: el CU menciona que esta búsqueda también puede realizarla "un proceso consumidor"
  # además del Administrador de Catálogos, sin definir ese actor ni sus permisos por separado,
  # y no aparece en catalogo-actores.md. No se generó un escenario de permisos (rechazo por
  # falta de autorización) para esta operación de solo lectura por no estar especificado en el CU.
