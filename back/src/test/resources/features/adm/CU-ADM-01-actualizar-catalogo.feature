# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Actualizar Catálogo

  Como Administrador de Catálogos
  Quiero actualizar nombre, catálogo padre, estado y vigencia de un catálogo existente, y sus campos cuando aún no tiene registros

  Escenario: Actualizar nombre, padre, estado y/o vigencia de un catálogo
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona un catálogo existente
    Cuando modifica nombre, catálogo padre, estado activo/inactivo y/o vigencia, sin modificar el código
    Entonces el sistema aplica los cambios permitidos

  Escenario: Permitir modificar los campos (fields) de un catálogo que aún no tiene registros
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona un catálogo existente que no contiene registros
    Cuando modifica los campos (fields) del catálogo
    Entonces el sistema aplica los cambios permitidos

  Escenario: Rechazar la modificación del código de un catálogo
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona un catálogo existente
    Cuando intenta modificar el código del catálogo
    Entonces el sistema rechaza la modificación del código

  Escenario: Rechazar la modificación de los campos (fields) de un catálogo que ya tiene registros
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona un catálogo existente que ya contiene registros
    Cuando intenta modificar los campos (fields) del catálogo
    Entonces el sistema rechaza la modificación de los campos

  Escenario: Rechazar la actualización de un catálogo por un actor no autorizado
    Dado que el actor no está autenticado o no está autorizado para administrar catálogos
    Cuando intenta actualizar un catálogo existente
    Entonces el sistema rechaza la operación por falta de autorización
