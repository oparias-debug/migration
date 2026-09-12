# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Crear Catálogo

  Como Administrador de Catálogos
  Quiero crear un nuevo catálogo definiendo código, nombre, catálogo padre opcional, vigencia y campos

  Escenario: Crear un catálogo válido con al menos un campo KEY
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y define código, nombre y una lista de campos que incluye al menos un campo marcado como KEY, sin nombres repetidos
    Cuando solicita crear el catálogo
    Entonces el sistema crea el catálogo
    Y lo agrega al catalogMaster

  Escenario: Rechazar la creación de un catálogo sin campos definidos
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y no define ningún campo para el catálogo
    Cuando solicita crear el catálogo
    Entonces el sistema rechaza la creación por no existir al menos un campo definido

  Escenario: Rechazar la creación de un catálogo sin campo KEY
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y define campos para el catálogo pero ninguno está marcado como KEY
    Cuando solicita crear el catálogo
    Entonces el sistema rechaza la creación por no existir al menos un campo KEY

  Escenario: Rechazar la creación de un catálogo con nombres de campo repetidos
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y define dos campos con el mismo nombre
    Cuando solicita crear el catálogo
    Entonces el sistema rechaza la creación por nombres de campo repetidos

  Escenario: Rechazar la creación de un catálogo hijo cuyo catálogo padre no existe
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y indica como catálogo padre un código que no existe previamente
    Cuando solicita crear el catálogo
    Entonces el sistema rechaza la creación del catálogo hijo

  Esquema del escenario: Vigencia por defecto del catálogo al crearlo
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y crea un catálogo válido con al menos un campo KEY
    Cuando "<condicion_vigencia>"
    Entonces el catálogo queda en estado "<estado_resultante>"

    Ejemplos:
      | condicion_vigencia                          | estado_resultante |
      | no se indican fechas de vigencia            | ACTIVE             |
      | la fecha "hasta" (TO DATE) es anterior a la fecha actual | INACTIVE  |

  Escenario: Rechazar la creación de un catálogo por un actor no autorizado
    Dado que el actor no está autenticado o no está autorizado para administrar catálogos
    Cuando intenta crear un catálogo
    Entonces el sistema rechaza la operación por falta de autorización
