# language: es
@CU-ADM-01 @rol:ADMINISTRADOR_DE_CATALOGOS
Característica: Actualizar Registro

  Como Administrador de Catálogos
  Quiero actualizar el valor de uno o más campos no-KEY de un registro existente

  Escenario: Actualizar uno o más campos no-KEY de un registro
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona un registro existente
    Cuando modifica el valor de uno o más campos no-KEY
    Entonces el sistema aplica los cambios

  Escenario: Rechazar la modificación del campo KEY de un registro
    Dado que el Administrador de Catálogos está autenticado y autorizado para administrar catálogos
    Y selecciona un registro existente
    Cuando intenta modificar el valor del campo KEY
    Entonces el sistema reporta un error
    Y no aplica la modificación del campo KEY

  Escenario: Rechazar la actualización de un registro por un actor no autorizado
    Dado que el actor no está autenticado o no está autorizado para administrar catálogos
    Cuando intenta actualizar un registro existente
    Entonces el sistema rechaza la operación por falta de autorización
