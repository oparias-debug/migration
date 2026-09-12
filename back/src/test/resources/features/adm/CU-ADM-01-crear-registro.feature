# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Crear Registro de Catálogo

  Como Administrador de Catálogos
  Quiero crear un registro dentro de un catálogo existente, proveyendo un valor para cada campo definido

  Escenario: Crear un registro con un valor de clave (KEY) que no existe aún en el catálogo
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona un catálogo existente
    Y provee un valor para cada campo definido en el catálogo
    Y el valor del campo KEY no existe aún en el catálogo
    Cuando solicita crear el registro
    Entonces el sistema almacena el nuevo registro

  Escenario: Rechazar la creación de un registro cuyo valor de KEY ya existe en el catálogo
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona un catálogo existente
    Y provee un valor de campo KEY que ya existe en el catálogo
    Cuando solicita crear el registro
    Entonces el sistema rechaza la creación del registro

  Esquema del escenario: Vigencia por defecto del registro al crearlo
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y crea un registro válido con un valor de KEY no existente en el catálogo
    Cuando "<condicion_vigencia>"
    Entonces el registro queda en estado "<estado_resultante>"

    Ejemplos:
      | condicion_vigencia                          | estado_resultante |
      | no se indican fechas de vigencia            | ACTIVE             |
      | la fecha "hasta" (TO DATE) es anterior a la fecha actual | INACTIVE  |

  Escenario: Rechazar la creación de un registro por un actor no autorizado
    Dado que el actor no está autenticado o no está autorizado para administrar catálogos
    Cuando intenta crear un registro en un catálogo
    Entonces el sistema rechaza la operación por falta de autorización
